package com.muzziq.quizz;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.appengine.api.datastore.*;

@SuppressWarnings({ "serial", "unused" })
public class MuzziqServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		resp.setContentType("text/plain");
		PrintWriter out = resp.getWriter();
		
	}
}
