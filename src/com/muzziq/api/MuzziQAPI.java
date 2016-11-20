package com.muzziq.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.muzziq.utils.Quizz;
import com.muzziq.utils.QTemplate;
import com.muzziq.utils.Question;

@Api(name="muzziqapi",version="v1", description="An API to manage music quizzes")
public class MuzziQAPI {
	
	private List<QTemplate> templates = new ArrayList<QTemplate>();
	private List<Question> questions = new ArrayList<Question>();
	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private Logger logger = Logger.getLogger("myLogger");
	private MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
	
	/***
	 * Dans le constructeur de cette classe on peut rajouter les templates de questions
	 * QTemplate(String infoProvided ,String infoDemanded ,String template)
	 * 
	 * Pour rajouter des questions rajouter une QTemplate supplementaire!!
	 * infoProvided et infoDemanded doivent etre des noms de propriétés dans la datastore
	 * comme Artist, Année, Single, Album, Nationalité
	 * 
	 * Les questions avec plusieurs variables ne marchent pas encore
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
	}
	
	
	
	private List<Integer> putEntitiesInCache(List<Entity> genreEntities){
		List<Integer> list = new ArrayList<Integer>();
		logger.log(Level.INFO, "genreEntities.size() = "+ genreEntities.size());
		int key = 0;
		for(int i=0; i< genreEntities.size();i++){
			Entity e = (Entity) syncCache.get(key);
			if(e == null){
				logger.log(Level.INFO,"writing in memcache");
				e = genreEntities.get(i);
				syncCache.put(key, e);
				list.add(key);
				logger.log(Level.INFO, "syncCache("+i+") = " + syncCache.get(key));
			}else{
				if(!e.equals(genreEntities.get(i))){
					logger.log(Level.INFO,"writing in memcache");
					e=genreEntities.get(i);
					syncCache.put(key, e);
					list.add(key);
					logger.log(Level.INFO, "syncCache("+i+") = " + syncCache.get(key));
				}else{
					list.add(key);
				}
			}
			key++;
		}
		return list;
	}
	
	private List<Entity> selectEntitiesByGenre(String genre){
		Filter filterGenre = new FilterPredicate("Genre", FilterOperator.EQUAL, genre);
		Query q = new Query("Qvars").setFilter(filterGenre);
		PreparedQuery pq = this.datastore.prepare(q);
		Iterable<Entity> itEntity = pq.asIterable();
		Iterator<Entity> it = itEntity.iterator();
		List<Entity> elist = new ArrayList<Entity>();
		while(it.hasNext()){
			Entity e = it.next();
			logger.log(Level.INFO, "add()");
			elist.add(e);
		}
		
		return elist;
	}
	
	private void createQuestion2(QTemplate template,String genre,List<Integer> keys){
		List<Integer> ids = new ArrayList<Integer>(); //id dans datastore
		List<Integer> localids = new ArrayList<Integer>();//id dans memcache
		List<String> answers = new ArrayList<String>();
		List<String> vars = new ArrayList<String>();
		
		String varContext = template.getInfoProvided();
		String answerContext = template.getInfoDemanded();
		Random r = new Random();
		
		int size = syncCache.getAll(keys).size();
		logger.log(Level.INFO,"memcache.size() = " + size);
		int id = r.nextInt(size);
		Entity correctEntity = (Entity) this.syncCache.get(id);
		String var = (String) correctEntity.getProperty(varContext);
		logger.log(Level.INFO, "var =  "+ var);
		
		String answer = (String) correctEntity.getProperty(answerContext);
		logger.log(Level.INFO,"answer = "+ answer);
		
		int correctId = (int) correctEntity.getKey().getId();
		logger.log(Level.INFO,"id = "+ correctId);
		
		ids.add(correctId);
		localids.add(id);
		vars.add(var);
		answers.add(answer);
		
		int i = 0;
		while(i<3){
			int bid = r.nextInt(size);
			if(localids.contains(bid)){
				continue;
			}else{
				Entity bentity = (Entity) this.syncCache.get(bid);
				String banswer = (String) bentity.getProperty(answerContext);
				if(answers.contains(banswer)){
					continue;
				}else{
					int badid = (int) bentity.getKey().getId();
					
					ids.add(badid);
					localids.add(bid);
					answers.add(banswer);
					
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
	public Quizz getQuizz(@Named("Genre") String genre){
		Quizz myQuizz = new Quizz(1,genre);
		
		List<Integer> listKeys = this.putEntitiesInCache(this.selectEntitiesByGenre(myQuizz.getGenre()));
		this.questions.clear();
		
		for(int i=0;i<this.templates.size();i++){
			this.createQuestion2(this.templates.get(i),myQuizz.getGenre(), listKeys);
			myQuizz.addQuestion(this.questions.get(i));
		}
		return myQuizz;
		
	}

}
