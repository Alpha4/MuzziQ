package com.muzziq.utils;

import java.util.ArrayList;
import java.util.List;

public class Quizz {
	private int id;
	private List<Question> questions;
	//private String genre;
	
	public Quizz(int id, List<Question> questions){
		this.id = id;
		this.questions = questions;
		//this.setGenre(genre);
	}
	
	public Quizz(int id){
		this.id = id;
		this.questions = new ArrayList<Question>();
		//this.genre = genre;
	}
	
	public void addQuestion(Question question){
		this.questions.add(question);
	}
	
	public List<Question> getQuestions(){
		return this.questions;
	}
	
	public int getId(){
		return this.id;
	}
	public void setId(int id){
		this.id = id;
	}

	/*
	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}*/
	
}
