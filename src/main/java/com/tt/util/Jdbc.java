package com.tt.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.tt.Constrain;

public class Jdbc {
	
	private Logger logger = Logger.getLogger(this.getClass().getSimpleName());
	
	private String name = null;
	
	private String driver = null;
	
	private String url = null;
	
	private String loginName = null;
	
	private String loginPd = null;
	
	private Jdbc() {
		this.driver = "oracle.jdbc.driver.OracleDriver";
	}
	
	public Jdbc(String name) {
		this();
		this.name = name;
	}
	
	public Jdbc(String name, String url, String userName, String userPd) {
		this(name, Constrain.ORACLE_DRIVER, url, userName, userPd);
	}
	
	public Jdbc(String name, String driver, String url, String userName, String userPd) {
		this(name);
		this.driver = ((!StringUtils.isBlank(driver))?driver:this.driver);
		this.url = url;
		this.loginName = userName;
		this.loginPd = userPd;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.name==null)?0:this.name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this==obj) {
			return true;
		}
		if(obj==null || this.getClass()!=obj.getClass()) {
			return false;
		}
		Jdbc other = (Jdbc) obj;
		if((this.name==null && other.name!=null) || !this.name.equals(other.name)) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		return this.toString(0);
	}
	
	public String toString(int index) {
		String str = "\n"
				   + "Jdbc["+index+"].name == " + this.name + "\r\n"
				   + "Jdbc["+index+"].driver == " + this.driver + "\r\n"
				   + "Jdbc["+index+"].url == " + this.url + "\n"
				   + "Jdbc["+index+"].loginName == " + this.loginName + "\r\n"
				   + "Jdbc["+index+"].loginPassword == " + this.loginPd + "\r\n"
				   ;
		return str;
	}
	
	public Connection createConnection() {
		try {
			this.logger.debug(this.toString());
			if(StringUtils.isBlank(this.driver) 
					|| StringUtils.isBlank(this.url) 
					|| StringUtils.isBlank(this.loginName) 
					|| StringUtils.isBlank(this.loginPd)) {
				return null;
			}
			Class.forName(this.driver);
			if(StringUtils.equals(this.driver, Constrain.ORACLE_DRIVER)) {
				if(!this.url.startsWith("jdbc:oracle:thin:@")) {
					this.url = "jdbc:oracle:thin:@" + this.url;
				}
			}
			return DriverManager.getConnection(this.url, this.loginName, this.loginPd);
		} catch(SQLException e) {
			e.printStackTrace();
		} catch(Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getLoginPd() {
		return loginPd;
	}

	public void setLoginPd(String loginPd) {
		this.loginPd = loginPd;
	}

}

