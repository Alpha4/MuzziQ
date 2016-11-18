package com.muzziq.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.muzziq.utils.Quizz;
import com.muzziq.utils.QTemplate;
import com.muzziq.utils.Question;

@Api(name="muzziqapi",version="v1", description="An API to manage music quizzes")
public class MuzziQAPI {
	
	private List<QTemplate> templates = new ArrayList<QTemplate>();
	private List<Question> questions = new ArrayList<Question>();
	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	/***
	 * Dans le constructeur de cette classe on peut rajouter les templates de questions
	 * QTemplate(String infoProvided ,String infoDemanded ,String template)
	 */
	
	//TODO completer avec les autres templates
	
	public MuzziQAPI(){
		this.templates.add(new QTemplate("Chanson","Artist","Quel chanteur a composé %%var%%?"));
		this.templates.add(new QTemplate("Chansons","Artist","Quel chanteur a composé %%var%% et %%var%%?"));
	}
	
	/**
	 * en fonction de l'information que on donne dans le template de la question, cette fonction
	 * demande au datastore les veariables necessaires à la creation d'une question puis 
	 * rajoute la question cree à la liste des questions
	 * De plus comme les réponses sont dans la classe question ils vont aussi être construit
	 * @param template
	 */
	
	//TODO finir les autres cas du switch apres avoir rajouté les templates
	
	@SuppressWarnings("unchecked")
	private void createQuestion(QTemplate template){
		
		switch(template.getInfoProvided()){
		case "Chanson":{
			Random r = new Random();
			List<Integer> ids = new ArrayList<Integer>();
			int id = r.nextInt(6);
			ids.add(id);
			
			Filter filter = new FilterPredicate("id", FilterOperator.EQUAL, id);
			Query q = new Query("QuestionVars").setFilter(filter);
			PreparedQuery pq = datastore.prepare(q);
			Entity entity = pq.asSingleEntity();
			
			ArrayList<String> songs = (ArrayList<String>) entity.getProperty("Songs");
			String song = songs.get(r.nextInt(songs.size()));
			List<String> vars = new ArrayList<String>();
			vars.add(song);
			
			List<String> answers = new ArrayList<String>();
			String correctAnswer = (String) entity.getProperty("Artist");
			answers.add(correctAnswer);
			
			int i=0;
			while(i<3){
				int id1 = r.nextInt(6);
				if(!ids.contains(id1)){
					Filter filter1 = new FilterPredicate("id", FilterOperator.EQUAL, id1);
					Query q1 = new Query("QuestionVars").setFilter(filter1);
					PreparedQuery pq1 = datastore.prepare(q1);
					Entity entity1 = pq1.asSingleEntity();
					String badAnswer = (String) entity1.getProperty("Artist");
					answers.add(badAnswer);
					ids.add(id1);
					i++;
				}
			}
			
			Question question = new Question(id,template,vars,answers);
			this.questions.add(question);
		} break;
		case "Chansons":{
			List<String> vars = new ArrayList<String>();
			List<String> answers = new ArrayList<String>();
			vars.add("Hero");
			vars.add("Salvation");
			answers.add("Skillet");
			answers.add("Linkin Park");
			answers.add("Halestorm");
			answers.add("Nickelback");
			Question question = new Question(4,template,vars,answers);
			this.questions.add(question);
		} break;
		}
	}
	
	/**
	 * Methode de l'api qui va être executé à chaque requete GET sur l'URL de l'API /quizz  
	 * Elle retourne par le reseau un fichier json contenant le quizz
	 * @return Quizz myQuizz
	 */
	
	@ApiMethod(name="getQuizz")
	public Quizz getQuizz(){
		this.questions.clear();
		Quizz myQuizz = new Quizz(1);
		for(int i=0;i<this.templates.size();i++){
			this.createQuestion(this.templates.get(i));
			myQuizz.addQuestion(this.questions.get(i));
		}
		return myQuizz;
		
	}

}
