package com.muzziq.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
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
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.memcache.Stats;
import com.muzziq.utils.Quizz;
import com.muzziq.utils.CorrectAnswer;
import com.muzziq.utils.QTemplate;
import com.muzziq.utils.Question;

@Api(name="muzziqapi",version="v1", description="An API to manage music quizzes")
public class MuzziQAPI {
	
	private List<QTemplate> templates = new ArrayList<QTemplate>();
	private List<Question> questions = new ArrayList<Question>();
	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private Logger logger = Logger.getLogger("myLogger");
	private MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
	
	//TODO a modifier avec le nombre exact
	private final int nrElemInDS=15; 
	
	/***
	 * Dans le constructeur de cette classe on peut rajouter les templates de questions
	 * QTemplate(String infoProvided ,String infoDemanded ,String template)
	 * 
	 * Pour rajouter des questions rajouter une QTemplate supplementaire!!
	 * infoProvided et infoDemanded doivent etre des noms de propriétés dans la datastore
	 * comme Artist, Année, Single, Album, Nationalité
	 *  
	 */
	
	public MuzziQAPI(){
		this.templates.add(new QTemplate("Single","Artist","Quel artist a composé le single %%var%%?"));
		this.templates.add(new QTemplate("Album","Artist","Quel artist a composé l'album %%var%%?"));
		this.templates.add(new QTemplate("Année","Single", "Quel single est apparu en %%var%%?"));
		this.templates.add(new QTemplate("Année", "Album", "Quel album est apparu en %%var%%?"));
		this.templates.add(new QTemplate("Artist","Single", "Lequel de ces singles est publié par %%var%%"));
		this.templates.add(new QTemplate("Artist","Album", "Lequel de ces albums est publié par %%var%%?"));
		this.templates.add(new QTemplate("Artist","Nationalité","De quelle nationalité est l'artist %%var%%?"));
		this.templates.add(new QTemplate("Nationalité","Artist","Lequel de ces artists est de nationalité %%var%%?"));
		this.templates.add(new QTemplate("Genre","Single","Lequel de ces singles fait partie du genre %%var%%?"));
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
	 */
	
	@ApiMethod(name="getQuizz")
	public Quizz getQuizz(){ //@Named("Genre") String genre
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
		
	}
	
	@ApiMethod(name="verifyAnswer")
	public CorrectAnswer verifyAnswer(@Named("id") int id, @Named("answer") String answer){
		return new CorrectAnswer(true);
	}
	
}
