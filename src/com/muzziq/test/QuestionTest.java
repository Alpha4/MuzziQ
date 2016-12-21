package src.com.muzziq.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import src.com.muzziq.utils.QTemplate;
import src.com.muzziq.utils.Question;

public class QuestionTest {

	public String template = "Quel chanteur a composé %%var%%?";
	public String template2 = "Quel chanteur a composé %%var%% et %%var%%?";
	public List<String> answers = new ArrayList<String>();
	public List<String> lst = new ArrayList<String>();
	
	public QTemplate qtempl = new QTemplate("Chanson","Artist",template);
	public QTemplate qtempl1 = new QTemplate("Chanson","Artist",template2);
	
	@Test
	public void test() {
		answers.add("Linkin Park");
		answers.add("Coldplay");
		answers.add("Skillet");
		answers.add("Michael Jackson");
		lst.add("Numb");
		Question myQuestion = new Question(1,qtempl,lst, answers);
		assert(myQuestion.getContent().equals("Quel chanteur a composé Numb?"));
		//System.out.println(template);
		//System.out.println(template.indexOf("%%var%%"));
		//System.out.println(myQuestion.getContent());
		
		lst.add("Castle of glass");
		Question myQuestion2 = new Question(2,qtempl1,lst, answers);
		assert(myQuestion2.getContent().equals("Quel chanteur a composé Numb et Castle of glass?"));
		//System.out.println("success");
		//System.out.println(myQuestion2.toString());
		List<String> res = myQuestion2.getAnswers();
		for(int i=0;i<res.size();i++){
			System.out.println(res.get(i));
		}
		// verify failing tests
		try{
			@SuppressWarnings("unused")
			Question myQuestion3 = new Question(3,qtempl,lst,answers);
			fail("expected RuntimeError");
		}catch(RuntimeException e){
			System.out.println("function succesfully throws RuntimeException");
		}
		
		lst.remove(1);
		try{
			@SuppressWarnings("unused")
			Question myQuestion4 = new Question(4,qtempl1,lst,answers);
			fail("expected RuntimeError");
		}catch(RuntimeException e){
			System.out.println("function succesfully throws RuntimeException");
		}
		
		
	}

}
