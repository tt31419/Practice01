package com.tt.threat;

import org.apache.log4j.Logger;

public class MainThreat extends Thread{
	
	private String parentThreat = "";	//from
	
	private Logger log = null;
	
	private String threatContextTest = "";	//from
	
	private MainThreat() {
	}

	public MainThreat(String parentThreat, String threatContextTest) {
		this();
		this.parentThreat = parentThreat;
		this.log = Logger.getLogger(parentThreat);
		this.setThreatContextTest(threatContextTest);
	}
	
	@SuppressWarnings("unused")
	private void printMsg(String msg) {
		msg = ((msg!=null)?msg.trim():null);
		if(msg==null || "".equals(msg)) {
			return;
		}
		if(log!=null) {
			log.info(msg);
		} else {
			System.out.println(msg);
		}
	}
	
	@SuppressWarnings("unused")
	private static void initLog4j(String ModuleName) {
//		PropertyConfigurator.configure(logdir);
		System.out.println("Log Property Config Finish");
	}
	
	@Override
	public void run() {
		super.run();
	}

	public String getParentThreat() {
		return parentThreat;
	}

	public void setParentThreat(String parentThreat) {
		this.parentThreat = parentThreat;
	}

	public String getThreatContextTest() {
		return threatContextTest;
	}

	public void setThreatContextTest(String threatContextTest) {
		this.threatContextTest = threatContextTest;
	}
	
	
	
}

