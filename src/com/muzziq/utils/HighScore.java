package com.muzziq.utils;

import java.lang.String;

public class HighScore{

	private String _nom;
	private long _score;

	public HighScore(String name, long score){
		_nom = name;
		_score = score;
	
	}

	//getters
	
	
	public String getNom(){
		return _nom;
	}
	
	
	public long getScore() {
		return _score;
	}
	
	//setters
	
	
	public void setNom( String name){
		_nom = name;
	}
	
	
	public void setScore( long score) {
		_score = score;
	}
	

}
