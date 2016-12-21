package com.muzziq.utils;

import java.util.ArrayList;
import java.util.List;

public class Hss {
	private List<HighScore> listHs = new ArrayList<HighScore>();
	
	public void add(HighScore hs){
		listHs.add(hs);
	}
	
	public List<HighScore> getListHs(){
		return this.listHs;
	}
	
	public void setListHs(List<HighScore> lhs){
		this.listHs = lhs;
	}
}
