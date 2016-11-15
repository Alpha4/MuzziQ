package com.muzziq.utils;

public class QTemplate {
	private String infoProvided;
	private String infoDemanded;
	
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
