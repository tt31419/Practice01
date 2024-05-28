package com.tt.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class JdbcUtils {
	
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(JdbcUtils.class.getSimpleName());
	
	private static JdbcUtils instance = null;
	
	private List<Jdbc> jdbcList = new ArrayList<Jdbc>();
	
	private JdbcUtils() {
	}
	
	public static JdbcUtils getInstance() {
		try {
			if(instance==null) {
				instance = new JdbcUtils();
			}
		} catch(Throwable e) {
			e.printStackTrace();
		}
		return ((instance==null)?new JdbcUtils():instance);
	}
	
	public Jdbc findByJdbcName(String jdbcName) {
		if(StringUtils.isBlank(jdbcName)) {
			return null;
		}
		for(Jdbc jdbc : this.jdbcList) {
			if(jdbc==null || !jdbcName.equals(jdbc.getName())) {
				continue;
			}
			return jdbc;
		}
		return null;
	}
	
	public boolean isContainsName(String name) {
		if(StringUtils.isBlank(name) || this.jdbcList==null || this.jdbcList.size()==0) {
			return false;
		}
		for(Jdbc jdbc : this.jdbcList) {
			if(jdbc==null || StringUtils.isBlank(jdbc.getName())) {
				continue;
			}
			if(name.equals(jdbc.getName())) {
				return true;
			}
		}
		return false;
	}
	
	public Connection getConnectionByJdbcName(String jdbcName) {
		Jdbc jdbc = this.findByJdbcName(jdbcName);
		return ((jdbc!=null)?jdbc.createConnection():null);
	}
	
	public Connection getConnectionByJdbcInfo(String url, String userName, String userPd) {
		return this.getConnectionByJdbcInfo(null, null, url, userPd, userPd);
	}
	
	public Connection getConnectionByJdbcInfo(String name, String driver, String url, String userName, String userPd) {
		Jdbc jdbc = this.getJdbcByJdbcInfo(name, driver, url, userName, userPd);
		return ((jdbc!=null)?jdbc.createConnection():null);
	}
	
	public Jdbc getJdbcByJdbcInfo(String url, String userName, String userPd) {
		return this.getJdbcByJdbcInfo(null, null, url, userName, userPd);
	}
	
	public Jdbc getJdbcByJdbcInfo(String name, String driver, String url, String userName, String userPd) {
		if(StringUtils.isBlank(url) 
				|| StringUtils.isBlank(userName) 
				|| StringUtils.isBlank(userPd)) {
			return null;
		}
		Jdbc jdbc = null;
		if(!StringUtils.isBlank(name)) {
			jdbc = this.findByJdbcName(name);
		}
		if(jdbc==null) {
			jdbc = new Jdbc(name, driver, url, userName, userPd);
			this.addJdbcList(jdbc);
		}
		return jdbc;
	}
	
	public void closeConnection(Connection conn) {
		try {
			if(conn!=null && !conn.isClosed()) {
				conn.close();
			}
		} catch(SQLException e) {
		} catch(Throwable e) {
		}
	}
	
	public void addJdbcList(Jdbc jdbc) {
		if(this.jdbcList==null) {
			this.jdbcList = new ArrayList<Jdbc>();
		}
		if(jdbc==null || StringUtils.isBlank(jdbc.getName()) || this.isContainsName(jdbc.getName())) {
			return;
		}
		this.jdbcList.add(jdbc);
	}
	
	public void addJdbcList(String name, String url, String loginName, String loginPd) {
		this.addJdbcList(name, null, url, loginName, loginPd);
	}
	
	public void addJdbcList(String name, String driver, String url, String loginName, String loginPd) {
		if(this.jdbcList==null) {
			this.jdbcList = new ArrayList<Jdbc>();
		}
		if(StringUtils.isBlank(name) 
				|| StringUtils.isBlank(url) 
				|| StringUtils.isBlank(loginName) 
				|| StringUtils.isBlank(loginPd)
				|| this.isContainsName(name)) {
			return;
		}
		this.jdbcList.add(new Jdbc(name, driver, url, loginName, loginPd));
	}
	
}
