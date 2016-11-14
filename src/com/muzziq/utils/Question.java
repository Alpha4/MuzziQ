package com.muzziq.utils;

import java.util.Collections;
import java.util.List;

public class Question {
	
	//In the question template %%var%% represents a variable
	// Ex: " En quelle année l'album %%var%% est-il sorti? " 
	
	private int id;
	private String content;
	private List<String> variables;
	private String template;
	
	private List<String> answers;
	
	public Question(int id, String template, List<String> vars, List<String> answers) throws RuntimeException{
		this.id = id;
		int nb = vars.size();
		int vnb = this.getVarNb(template);
		if(nb != vnb){
			throw new RuntimeException("the number of variables in the template does not correspond with the nb of var in the list");
		}
		this.variables = vars;
		this.template = template;
		this.buildQuestion();
		this.answers = answers;
		this.content = this.template;
	}
	
	private int getVarNb(String template){
		boolean end = false;
		int counter = 0;
		String str = template;
		while(!end){
			int index = str.indexOf("%%var%%");
			if(index == -1){
				end = true;
			}
			else{
				counter++;
				str = str.substring(index + 7);
			}
		}
		return counter;
	}
	
	private void buildQuestion(){
		//System.out.println("entering the building function");
		String res = null;
		for(int i=0;i<this.variables.size();i++){
			this.variables.get(i);
			res = this.template.replaceFirst("%%var%%", variables.get(i));
			//System.out.println(i+" "+ res);
			this.template = res;
		}
		//System.out.println(res);
		
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
	
	public String toString(){
		return template;
	}
	
		
}
