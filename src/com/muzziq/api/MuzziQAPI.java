package com.muzziq.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.Named;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.memcache.Stats;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;
import com.muzziq.utils.ResDatastore;
import com.muzziq.utils.Quizz;
import com.muzziq.utils.CorrectAnswer;
import com.muzziq.utils.QTemplate;
import com.muzziq.utils.Question;
import com.muzziq.utils.HighScore;
import com.muzziq.utils.Hss;



@SuppressWarnings("unused")
@Api(name="muzziqapi",version="v1", description="An API to manage music quizzes",clientIds={"230619663769-99mc5h263pjsejb4ka8lb9v7ssvtd41r.apps.googleusercontent.com"})
public class MuzziQAPI {
	
	private List<QTemplate> templates = new ArrayList<QTemplate>();
	private List<Question> questions = new ArrayList<Question>();
	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private Logger logger = Logger.getLogger("myLogger");
	private MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
	private final int nrElemInDS=115; 
	
	/***
	 * Dans le constructeur de cette classe on peut rajouter les templates de questions
	 * QTemplate(String infoProvided ,String infoDemanded ,String template)
	 * 
	 * Pour rajouter des questions rajouter une QTemplate supplementaire!!
	 * infoProvided et infoDemanded doivent etre des noms de propriétés dans la datastore
	 * comme Artist, Year, Single, Album, Nationalité
	 *  
	 */
	
	public MuzziQAPI(){
		this.templates.add(new QTemplate("Title","Artist","Quel artist a composé le single %%var%%?"));
		this.templates.add(new QTemplate("Album","Artist","Quel artist a composé l'album %%var%%?"));
		this.templates.add(new QTemplate("Year","Title", "Quel single est apparu en %%var%%?"));
		this.templates.add(new QTemplate("Year", "Album", "Quel album est apparu en %%var%%?"));
		this.templates.add(new QTemplate("Artist","Title", "Lequel de ces singles est publié par %%var%%"));
		this.templates.add(new QTemplate("Artist","Album", "Lequel de ces albums est publié par %%var%%?"));
		this.templates.add(new QTemplate("Genre","Title","Lequel de ces singles fait partie du genre %%var%%?"));
		this.templates.add(new QTemplate("Title","Genre","De quel genre est le single %%var%%?"));
	}
	
	@ApiMethod(name="fillDataStore",httpMethod = HttpMethod.GET)
	public ResDatastore fillDataStore(@Named("in")String in) throws IOException, JSONException
	{
		File fi = new File("WEB-INF/"+in);

		try 
		{
			FileInputStream fis = new FileInputStream(fi);
			
			int index = 1;
			
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line;
			String previousTitle = "";
			String previousArtist = "";
			String previousAlbum = "";
			
			// On saute les 2 premières lignes
			br.readLine();
			br.readLine();
			
			logger.log(Level.INFO,"inside try, before while() of fillDatastore");
			
			while ((line = br.readLine()) != null)
			{
				JSONObject obj = new JSONObject(line);
				
				
				String n = obj.getJSONObject("name").getString("value");
				
				int ind = n.indexOf(',');
				if (ind > 0)
				{
					n = n.substring(0,ind)+n.substring(ind + 1, n.length());
				}
				
				logger.log(Level.INFO, index + "th time in the while loop");
				// Cas spécial
				if (n.equals("Cotentin Aurélien"))
				{
					n = "Orelsan";
				}
					
				String t = obj.getJSONObject("title").getString("value");
				
				ind = t.indexOf('(');
				if (ind > 0)
				{
					int ind1 = t.indexOf(')');
					t = t.substring(0,ind)+t.substring(ind1 + 1, t.length());
				}
				
				String a = obj.getJSONObject("albumName").getString("value");
				
				ind = a.indexOf('(');
				if (ind > 0)
				{
					int ind2 = a.indexOf(')');
					a = a.substring(0,ind)+a.substring(ind2 + 1, a.length());
				}
				
				String g = obj.getJSONObject("genre").getString("value");
				
				// Cas spécial
				if (g.equals("Horrorcore"))
				{
					g = "Contemporary R&B";
				}
				String an = obj.getJSONObject("annee").getString("value").substring(0,4);
				
				
				/*COMPARAISON PAS DE DOUBLONS
				 * Artiste ≠ on insère
				 * Titre ≠ on insère
				 * Album ≠ on insère (même single repris par l'artiste lui-même ?)
				 */
				if (!previousTitle.equals(t) 
					|| !previousArtist.equals(n) || !previousAlbum.equals(a)) {
				
					Entity ent = new Entity("Qvars",index);
					ent.setProperty("Artist", n);
					ent.setProperty("Title", t);
					ent.setProperty("Album", a);
					ent.setProperty("Genre", g);
					ent.setProperty("Year", an);
					
					datastore.put(ent);
									
					index++;
					
					logger.log(Level.INFO, "put entity in datastore");
				}
				previousArtist=n;
				previousTitle=t;
				previousAlbum=a;
			}
			logger.log(Level.INFO, "exited while()");
			
			br.close();
			
			//nrElemInDS += index;
			logger.log(Level.INFO, "there are "+ nrElemInDS + " in Datastore");
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			logger.log(Level.INFO, "inside FileNotFoundException");
		}
		
		return new ResDatastore(true);
	}
	
	
	
	/**
	 * memcache necessaire pour stocquer les clés du datastore
	 * si il est vide il y a une selectkeys() retourne que les cles du datastore
	 * @return true si le memcache est valide; false sinon
	 */
	private boolean isMemcacheValid(){
		int  size = (int) this.syncCache.getStatistics().getItemCount();
		if(size != nrElemInDS){
			return false;
		}else{
			return true;
		}
	}
	
	
	/**
	 * function qui copie les cles dans le memcache si elles ne le sont pas dedans
	 * @param keys
	 */
	private void putKeysInCache(List<Key> keys){
		for(int i=0;i<keys.size();i++){
			Key e = (Key) syncCache.get(i);
			if(e == null){
				syncCache.put(i, keys.get(i));
			}
		}
	}
	
	/**
	 * requete le datastore pour en récuperer les clés
	 * @return une liste des clés présentes dans le datastore
	 */
	private List<Key> selectKeys(){
		Query q = new Query("Qvars");
		PreparedQuery pq = this.datastore.prepare(q.setKeysOnly());
		Iterable<Entity> itEntity = pq.asIterable();
		Iterator<Entity> it = itEntity.iterator();
		List<Key> keylist = new ArrayList<Key>();
		while(it.hasNext()){
			Entity e = it.next();
			Key key = e.getKey();
			logger.log(Level.INFO, "adding key");
			keylist.add(key);
		}
		return keylist;
	}
	
	
	/**
	 * methode qui construit une question à partir d'un template et la rajoute à la liste des
	 * questions
	 * @param template
	 * @throws EntityNotFoundException
	 */
	private void createQuestion(QTemplate template) throws EntityNotFoundException{
		List<Integer> ids = new ArrayList<Integer>(); //id dans datastore
		List<Integer> memids = new ArrayList<Integer>();//id dans memcache
		List<String> answers = new ArrayList<String>();
		List<String> vars = new ArrayList<String>();
		
		String varContext = template.getInfoProvided();
		String answerContext = template.getInfoDemanded();
		Random r = new Random();
		
		int size = (int) syncCache.getStatistics().getItemCount();
		logger.log(Level.INFO,"memcache.size() = " + size);
		int id = r.nextInt(size-1)+1;
		
		Key key = (Key) this.syncCache.get(id);
		
		Entity goodEntity = this.datastore.get(key);
		
		String var = (String) goodEntity.getProperty(varContext);
		logger.log(Level.INFO, "var =  "+ var);
		
		String answer = (String) goodEntity.getProperty(answerContext);
		logger.log(Level.INFO,"answer = "+ answer);
		int goodId = (int) key.getId();
		ids.add(goodId);
		memids.add(id);
		vars.add(var);
		answers.add(answer);
		
		int i = 0;
		while(i<3){
			int bid = r.nextInt(size-1)+1;
			if(memids.contains(bid)){
				continue;
			}else{
				Key badKey = (Key) this.syncCache.get(bid);
				Entity badEntity = this.datastore.get(badKey);
				String badAnswer = (String) badEntity.getProperty(answerContext);
				if(answers.contains(badAnswer)){
					continue;
				}else{
					int badId = (int) badKey.getId();
					
					ids.add(badId);
					memids.add(bid);
					answers.add(badAnswer);
					
					i++;
				}
			}
		}
		
		Question question = new Question(ids.get(0),template,vars,answers);
		logger.log(Level.INFO, "Question = " + question.getContent());
		this.questions.add(question);
	}
	
	
	
	
	/**
	 * Methode de l'api qui va être executé à chaque requete GET sur l'URL de l'API /quizz  
	 * Elle retourne par le reseau un fichier json contenant le quizz
	 * @return Quizz myQuizz
	 * @throws OAuthRequestException 
	 */
	
	@ApiMethod(name="getQuizz",httpMethod = HttpMethod.GET)
	public Quizz getQuizz(User user) throws OAuthRequestException{ 
		if(user != null){
			Quizz myQuizz = new Quizz(1);
			logger.log(Level.INFO, "checking the memcache ...");
			if(!this.isMemcacheValid()){
				logger.log(Level.INFO, "memcache not valid; querying datastore");
				this.putKeysInCache(this.selectKeys());
			}else{
				logger.log(Level.INFO, "memcache valid; nothing to do ...");
			}
			
			this.questions.clear();
			
			for(int i=0;i<this.templates.size();i++){
				try {
					this.createQuestion(this.templates.get(i));
				} catch (EntityNotFoundException e) {
					// TODO Auto-generated catch block
					logger.log(Level.INFO, "entity could not be found in ds");
				}
				logger.log(Level.INFO, "before adding question "+i+ " to quizz");
				myQuizz.addQuestion(this.questions.get(i));
			}
			return myQuizz;
		}else{
			throw new OAuthRequestException("user should be not null");
		}
	}
	
	@ApiMethod(name="verifyAnswer")
	public CorrectAnswer verifyAnswer(@Named("id") int id, @Named("answer") String answer, User user) throws OAuthRequestException, EntityNotFoundException{
		if(user != null){
			logger.log(Level.INFO, "id= "+id+ " ; answer = "+answer);
			logger.log(Level.INFO,"id = "+id);
			Key k = (Key) syncCache.get(id-1);
			logger.log(Level.INFO,"id = "+k.getId());
			Entity e = datastore.get(k);
			Map<String,Object> map = e.getProperties();
			for(Iterator<Object> it = map.values().iterator();it.hasNext();){
				logger.log(Level.INFO,it.next().toString());
			}
			if(map.values().contains(answer)){
				return new CorrectAnswer(true);
			}else{
				return new CorrectAnswer(false);
			}
		}else{
			throw new OAuthRequestException("user should be not null");
		}
	}
	
	
	@ApiMethod(name="addHighScore",httpMethod = HttpMethod.GET)
	public void addHighScore(@Named("id") String id, @Named("name") String name, @Named("fname") String fname, @Named("score")int score,User user) throws OAuthRequestException{
		if(user != null){
			logger.log(Level.INFO,"id="+id+ " ; name= "+name+ " ; score= "+score);
			Entity ent = new Entity("HighScore");
			ent.setProperty("GoogleID", id);
			ent.setProperty("Nom", name);
			ent.setProperty("Prenom", fname);
			ent.setProperty("Score", score);
			datastore.put(ent);
		}else{
			throw new OAuthRequestException("user should be not null");
		}
		
	}

	
	@ApiMethod(name="getHighScore",httpMethod = HttpMethod.GET)
	private Hss getHighScore(User user){
		
		if(user != null){
			Query qHS = new Query("HighScore").addSort("Score",SortDirection.DESCENDING);
			PreparedQuery pq = this.datastore.prepare(qHS);
			Iterable<Entity> iterable = pq.asIterable(FetchOptions.Builder.withLimit(20));
			Iterator<Entity> it = iterable.iterator();
			Hss highScores = new Hss();
			while(it.hasNext()){
				Entity e = it.next();
				String gid = (String) e.getProperty("GoogleID");
				String name = (String)e.getProperty("Nom");
				String fname = (String)e.getProperty("Prenom");
				int score =(int) e.getProperty("Score");
				HighScore hs = new HighScore(gid,name,fname,score);
				highScores.add(hs);
			}
			Filter f = new FilterPredicate("GoogleID",FilterOperator.EQUAL, user.getUserId());
			Query playerq = new Query("HighScore").setFilter(f);
			PreparedQuery pp = this.datastore.prepare(playerq);
			Entity player = pp.asSingleEntity();
			String gidd = (String) player.getProperty("GoogleID");
			String namee = (String)player.getProperty("Nom");
			String fnamee = (String)player.getProperty("Prenom");
			int scoree =(int) player.getProperty("Score");
			HighScore phs = new HighScore(gidd,namee,fnamee,scoree);
			return highScores;
		}else{
			Query qHS = new Query("HighScore").addSort("Score",SortDirection.DESCENDING);
			PreparedQuery pq = this.datastore.prepare(qHS);
			Iterable<Entity> iterable = pq.asIterable(FetchOptions.Builder.withLimit(20));
			Iterator<Entity> it = iterable.iterator();
			Hss highScores = new Hss();
			while(it.hasNext()){
				Entity e = it.next();
				String gid = (String) e.getProperty("GoogleID");
				String name = (String)e.getProperty("Nom");
				String fname = (String)e.getProperty("Prenom");
				int score =(int) e.getProperty("Score");
				HighScore hs = new HighScore(gid,name,fname,score);
				highScores.add(hs);
			}
			return highScores;
		}
		
		
	}
	

}
