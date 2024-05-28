package com.tt.servlet;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StateServlet extends CommonServlet {

	private static final long serialVersionUID = -1387813365920614002L;
	
	public StateServlet() {
		super();
	}
	
	@Override
	public void init() throws ServletException {
		try {
			super.initLog4j();
		} catch(Throwable e) {
			e.printStackTrace();
		}
		super.init();
	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			Date sDate = new Date();
			String sourceIP = super.getIpAddr(request);
			super.printLogs("Call GWStateServlet at " + sdf.format(sDate) + ", from IP == " +  sourceIP);
			super.feedBackGWState(response, sDate, sourceIP);
			super.printLogs("Response cost " + super.countTimeElapsed(sDate) + "s");
		} catch(Throwable e) {
			e.printStackTrace();
		}
	}
	
}

