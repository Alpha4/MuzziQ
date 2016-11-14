package com.muzziq.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.muzziq.utils.Question;

public class QuestionTest {

	public String template = "Quel chanteur a composé %%var%%?";
	public String template2 = "Quel chanteur a composé %%var%% et %%var%%?";
	public List<String> answers = new ArrayList<String>();
	public List<String> lst = new ArrayList<String>();
	
	@Test
	public void test() {
		answers.add("Linkin Park");
		answers.add("Coldplay");
		answers.add("Skillet");
		answers.add("Michael Jackson");
		lst.add("Numb");
		Question myQuestion = new Question(1,template,lst, answers);
		assert(myQuestion.toString().equals("Quel chanteur a composé Numb?"));
		//System.out.println(template);
		//System.out.println(template.indexOf("%%var%%"));
		//System.out.println(myQuestion.toString());
		
		lst.add("Castle of glass");
		Question myQuestion2 = new Question(2,template2,lst, answers);
		assert(myQuestion2.toString().equals("Quel chanteur a composé Numb et Castle of glass?"));
		//System.out.println("success");
		//System.out.println(myQuestion2.toString());
		List<String> res = myQuestion2.getAnswers();
		for(int i=0;i<res.size();i++){
			System.out.println(res.get(i));
		}
	}

}
