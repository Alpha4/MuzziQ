package com.muzziq.utils;

import java.util.Collections;
import java.util.List;

public class Question {
	
	//In the question template %%var%% represents a variable
	// Ex: " En quelle année l'album %%var%% est-il sorti? " 
	
	private int id;
	private String content;
	private List<String> variables;
	private QTemplate template;
	
	private List<String> answers;
	
	public Question(int id, QTemplate template, List<String> vars, List<String> answers) throws RuntimeException{
		this.id = id;
		int nb = vars.size();
		int vnb = template.getVarNb();
		if(nb != vnb){
			throw new RuntimeException("the number of variables in the template does not correspond with the nb of var in the list");
		}
		this.variables = vars;
		this.template = template;
		this.content = this.buildQuestion();
		this.answers = answers;
	}
	
	
	private String buildQuestion(){
		//System.out.println("entering the building function");
		String template = this.template.getTemplate();
		String res = null;
		for(int i=0;i<this.variables.size();i++){
			this.variables.get(i);
			res = template.replaceFirst("%%var%%", variables.get(i));
			//System.out.println(i+" "+ res);
			template = res;
		}
		return res;
		
	}
	
	private void shuffleAnswers(){
		Collections.shuffle(this.answers);
	}
	
	public List<String> getAnswers(){
		this.shuffleAnswers();
		return answers;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getContent(){
		return this.content;
	}
	
		
}
