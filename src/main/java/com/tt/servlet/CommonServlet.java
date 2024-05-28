package com.tt.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.tt.util.CommonUtils;

public class CommonServlet extends HttpServlet {

	private static final long serialVersionUID = 6002449971166287313L;
	
	private String className = this.getClass().getSimpleName();
	
	public static Logger log = null;
	
	protected static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	protected CommonServlet() {
		super();
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}
	
	protected void initLog4j() throws Throwable {
		try {
			CommonUtils.loadLog4j();
			log = Logger.getLogger(this.className);
		} catch(Throwable e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	protected String countTimeElapsed(Date sDate) {
		if(sDate==null) {
			return null;
		}
		double cost = (new Date().getTime() - sDate.getTime()) / 1000.0;
		return new DecimalFormat("0.000").format(cost);
	}
	
	protected void printLogs(String msg) {
		if(msg==null || "".equals(msg)) {
			return;
		}
		if(log!=null) {
			log.info(msg);
		}
	}
	
	protected String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if(ip!=null && ip.indexOf(",")!=-1) {
			String[] ips = ip.split(",");
			if(ips!=null && ips.length>0) {
				for(String realIp : ips) {
					if(realIp==null || "unknown".equalsIgnoreCase(realIp.trim())) {
						continue;
					}
					ip = realIp;
					break;
				}
			}
		} else {
			if(ip==null || ip.length()==0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("Proxy-Client-IP");
			}
			if(ip==null || ip.length()==0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
			}
			if(ip==null || ip.length()==0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
			}
		}
		return ip;
	}
	
	protected void feedBackGWState(HttpServletResponse response, Date callTime, String soruceIP) throws Throwable {
		PrintWriter out = null;
		try {
			if(callTime==null) {
				callTime = new Date();
			}
			if(sdf==null) {
				sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			}
			String ackXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						  + "<GWState>"
						  + "  <Description>Server Publisher Available</Description>"
						  + "  <HostName>"+CommonUtils.getInstance().findHostName()+"</HostName>"
						  + "  <AvailableTime>"+sdf.format(callTime)+"</AvailableTime>"
						  + "  <SourceIP>"+soruceIP+"</SourceIP>"
						  + "</GWState>"
						  ;
			out = response.getWriter();
			out.print(ackXML);
			out.flush();
		} catch(Throwable e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				if(out!=null) {
					out.close();
				}
			} catch(Throwable e) {
			}
		}
	}

}

