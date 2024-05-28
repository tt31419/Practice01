package com.tt.batch;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.tt.facade.SQLFacade;
import com.tt.util.CommonUtils;
import com.tt.util.DbUtils;
import com.tt.Constrain;

public abstract class BatchRunTask extends TimerTask {
	
	protected String className = this.getClass().getSimpleName();

	protected Logger log = Logger.getLogger(this.className);
	
	protected String dateTimePattern = "yyyy/MM/dd HH:mm:ss";
	
	protected SimpleDateFormat sdfTime = new SimpleDateFormat(this.dateTimePattern);
	
	protected Date lastPollingTime = null;
	
	private boolean stopTask = false;
	
	private boolean running = false;
	
	protected DbUtils defaultDB = null;
	
	protected String hostName = null;
	
	protected SQLFacade uppFacade = null;
	
	protected boolean isSaveLog = true;
	
	protected int runCount = 0;
	
	public BatchRunTask() {
		this.defaultDB = Constrain.DEFAULT_DB;
		this.hostName = this.findHostName();
		CommonUtils.loadLog4j();
	}
	
	protected String calCostTime(Date sDate, Date eDate) {
		return CommonUtils.getInstance().calCostTime(sDate, eDate);
	}
	
	public final void run() {
		try {
			if(this.running) {
				return;
			}
			this.running = true;
			this.lastPollingTime = new Date();
			this.runTask();
			this.stopTask = false;
		} finally {
			this.running = false;
		}
	}
	
	public long getDiffPollingTime() {
		if(this.lastPollingTime==null) {
			return 0;
		}
		return (new Date().getTime()-this.lastPollingTime.getTime())/(1000 * 60);
	}
	
	public abstract void runTask();
	
	public boolean isRunning() {
		return running;
	}
	
	public Date getLastPollingTime() {
		return lastPollingTime;
	}

	public void setLastPollingTime(Date lastPollingTime) {
		this.lastPollingTime = lastPollingTime;
	}
	
	public boolean isStopTask() {
		return stopTask;
	}

	public void setStopTask(boolean stopTask) {
		this.stopTask = stopTask;
	}
	
	protected String nowTimeString() {
		return this.dateToString(new Date(), this.dateTimePattern);
	}
	
	protected String nowDateString() {
		return this.dateToString(new Date(), "yyyyMMdd");
	}
	
	protected String deltaTimeToString(Date date, int year, int month, int day, int hour, int minute, int second, int millSecond, String format) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(((date==null)?new Date():date));
		cal.add(Calendar.YEAR, year);
		cal.add(Calendar.MONTH, month);
		cal.add(Calendar.DATE, day);
		cal.add(Calendar.HOUR_OF_DAY, hour);
		cal.add(Calendar.MINUTE, minute);
		cal.add(Calendar.SECOND, second);
		cal.add(Calendar.MILLISECOND, millSecond);
		return new SimpleDateFormat(((StringUtils.isBlank(format))?this.dateTimePattern:format)).format(cal.getTime());
	}
	
	protected String deltaMinuteToString(Date date, int minute) {
		return this.deltaTimeToString(date, 0, 0, 0, 0, minute, 0, 0, this.dateTimePattern);
	}
	
	protected String deltaMinuteToString(Date date, int minute, String format) {
		return this.deltaTimeToString(date, 0, 0, 0, 0, minute, 0, 0, format);
	}
	
	protected String dateToString(Date date, String format) {
		if(date==null) {
			return null;
		}
		return new SimpleDateFormat(((StringUtils.isBlank(format))?this.dateTimePattern:format)).format(date);
	}
	
	protected String findHostName() {
		try {
			if(!StringUtils.isBlank(this.hostName)) {
				return this.hostName;
			}
			String host = InetAddress.getLocalHost().getHostName();
			if(host!=null && !host.equals("")) {
				host = host.toUpperCase();
				int dotPos = host.indexOf(".");
				this.hostName = ((dotPos!=-1)?host.substring(0, dotPos):host);
			}
		} catch(Throwable e) {
			e.printStackTrace();
			this.logWarningPrint("BatchRunTask.findHostName() Throwable == " + e.getMessage());
		}
		return this.hostName;
	}
	
	protected CommonUtils findCommonUtils() {
		return CommonUtils.getInstance();
	}
	
	protected void logPrint(String type, String msg) {
		this.findCommonUtils().logPrint(this.log, type, msg);
	}
	
	protected void logInfoPrint(String msg) {
		this.findCommonUtils().logInfoPrint(this.log, msg);
	}
	
	protected void logDebugPrint(String msg) {
		this.findCommonUtils().logDebugPrint(this.log, msg);
	}
	
	protected void logFinePrint(String msg) {
		this.findCommonUtils().logFinePrint(this.log, msg);
	}
	
	protected void logWarningPrint(String msg) {
		this.findCommonUtils().logWarningPrint(this.log, msg);
	}
	
	protected void startPrint(String function, Date startTime) {
		if(this.sdfTime==null) {
			this.sdfTime = new SimpleDateFormat(this.dateTimePattern);
		}
		this.logInfoPrint(function + " Start at " + this.sdfTime.format(((startTime==null)?new Date():startTime)));
	}
	
	protected void finalPrint(String function, Date startTime) {
		if(this.sdfTime==null) {
			this.sdfTime = new SimpleDateFormat(this.dateTimePattern);
		}
		Date endTime = new Date();
		this.logInfoPrint(function + " End of   " + this.sdfTime.format(endTime) + ", Total Cost: " + this.calCostTime(startTime, endTime) + "\r\n");
	}
	
	protected String encodeBase64(String param, String charset) throws Throwable {
		try {
			if(StringUtils.isBlank(param)) {
				return param;
			}
			charset = ((StringUtils.isBlank(charset))?"UTF-8":charset);
			return new String(Base64.encodeBase64(param.getBytes()), "UTF-8");
		} catch(Throwable e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	protected void resetSaveLog() {
		if(this.runCount%30==0) {
			this.runCount = 0;
			this.isSaveLog = true;
		}
	}
	
}
