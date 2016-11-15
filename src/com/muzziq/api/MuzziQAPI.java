package com.muzziq.api;

import java.util.ArrayList;
import java.util.List;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.muzziq.utils.Quizz;
import com.muzziq.utils.QTemplate;
import com.muzziq.utils.Question;

@Api(name="muzziqapi",version="v1", description="An API to manage music quizzes")
public class MuzziQAPI {
	
	private List<QTemplate> templates = new ArrayList<QTemplate>();
	private List<Question> questions = new ArrayList<Question>();
	
	public MuzziQAPI(){
		this.templates.add(new QTemplate("Chanson","Artist","Quel chanteur a composé %%var%%?"));
		this.templates.add(new QTemplate("Chanson","Artist","Quel chanteur a composé %%var%% et %%var%%?"));
	}
	
	//Logic to retrieve from the datastore variables to replace in the templates along with a list of answers
	//.....................................................................
	//Just randomly testing with prebuild questions and answers  no datastore
	
	@ApiMethod(name="getQuizz")
	public Quizz getQuizz(){
		List<String> var1 = new ArrayList<String>();
		var1.add("Numb");
		List<String> answers1 = new ArrayList<String>();
		answers1.add("Linkin Park");
		answers1.add("MJ");
		answers1.add("Skillet");
		answers1.add("Coldplay");
		Question q1 = new Question(11,this.templates.get(0),var1,answers1);
		
		List<String> var2 = new ArrayList<String>();
		var2.add("Hero");
		var2.add("Rise");
		List<String> answers2 = new ArrayList<String>();
		answers2.add("Linkin Park");
		answers2.add("MJ");
		answers2.add("Skillet");
		answers2.add("Coldplay");
		Question q2 = new Question(13,this.templates.get(1),var2,answers1);
		
		Quizz myQuizz = new Quizz(1);
		myQuizz.addQuestion(q1);
		myQuizz.addQuestion(q2);
		
		return myQuizz;
		
	}

}
