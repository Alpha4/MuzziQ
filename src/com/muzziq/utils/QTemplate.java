package com.muzziq.utils;

public class QTemplate {
	//atribut permetant de connaitre quelle types de variables il faut chercher dans le datastore pour les remlpacer dans le template
	private String infoProvided;
	//atribut permetant de conaitre le type de variables qui sont dans les reponses
	private String infoDemanded;
	
	//template contenant la sequence de caracteres %%var%% designant une variable
	private String template;
	
	public QTemplate(String infoProvided, String infoDemanded, String template){
		this.setInfoDemanded(infoDemanded);
		this.setInfoProvided(infoProvided);
		this.setTemplate(template);
	}

	public String getInfoProvided() {
		return infoProvided;
	}

	public void setInfoProvided(String infoProvided) {
		this.infoProvided = infoProvided;
	}

	public String getInfoDemanded() {
		return infoDemanded;
	}

	public void setInfoDemanded(String infoDemanded) {
		this.infoDemanded = infoDemanded;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}
	
	/**
	 * 
	 * @return le nombre des variables qu'il faut remplacer dans le template
	 */
	public int getVarNb(){
		boolean end = false;
		int counter = 0;
		String str = this.template;
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
	
}
