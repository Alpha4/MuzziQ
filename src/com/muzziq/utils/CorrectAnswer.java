package com.muzziq.utils;

public class CorrectAnswer {
	private boolean isCorrect;
	
	public CorrectAnswer(boolean b){
		setCorrect(b);
	}

	public boolean isCorrect() {
		return isCorrect;
	}

	public void setCorrect(boolean isCorrect) {
		this.isCorrect = isCorrect;
	}
	
}
