package com.muzziq.utils;

import java.lang.String;

public class HighScore{

	private String _googleID;
	private String _nom;
	private String _prenom;
	private int _score;

	public HighScore(String gid, String name, String fname, int score){
		_googleID = gid;
		_nom = name;
		_prenom = fname;
		_score = score;
	
	}

	//getters
	public String getGoogleID(){
		return _googleID;
	}
	
	public String getNom(){
		return _nom;
	}
	
	public String getPrenom(){
		return _prenom;
	}
	
	public int getScore() {
		return _score;
	}
	
	//setters
	public void setGoogleID(String googleID){
		_googleID = googleID;
	}
	
	public void setNom( String name){
		_nom = name;
	}
	
	public void setPrenom( String fname){
		_prenom = fname;
	}
	
	public void setScore( int score) {
		_score = score;
	}
	

}
