package com.tt.util;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.tt.Constrain;

public class DbUtils {
	
	private String className = this.getClass().getSimpleName();
	
	private Logger log = null;
	
	private static DbUtils instance = null;

	private String datasourceName = null;
	
	private String jdbcName = null;
	
	private Jdbc jdbc = null;
	
	private ThreadLocal<Connection> localConn = new ThreadLocal<Connection>();
	
	private DbUtils() {
		try {
			CommonUtils.loadLog4j();
			this.log = Logger.getLogger(this.className);
		} catch(Throwable e) {
			e.printStackTrace();
		}
	}
	
	public DbUtils(String datasourceName) {
		this();
		this.datasourceName = datasourceName;
	}
	
	public DbUtils(String datasourceName, String jdbcName) {
		this();
		this.datasourceName = datasourceName;
		this.jdbcName = jdbcName;
	}
	
	public DbUtils(String url, String userName, String userPd) {
		this(null, null, url, userName, userPd);
	}
	
	public DbUtils(String name, String driver, String url, String userName, String userPd) {
		this();
		if(StringUtils.isBlank(url) 
				|| StringUtils.isBlank(userName) 
				|| StringUtils.isBlank(userPd)) {
			return;
		}
		this.jdbc = JdbcUtils.getInstance().getJdbcByJdbcInfo(name, driver, url, userName, userPd);
	}
	
	public static DbUtils getInstance() {
		if(instance==null) {
			instance = new DbUtils();
		}
		instance.datasourceName = "LTE_Datasource";
		instance.jdbcName = null;
		instance.jdbc = null;
		return instance;
	}
	
	public static DbUtils getInstance(String datasourceName, String jdbcName) {
		if(instance==null) {
			instance = new DbUtils();
		}
		instance.datasourceName = datasourceName;
		instance.jdbcName = jdbcName;
		instance.jdbc = null;
		return instance;
	}
	
	public static DbUtils getInstance(String url, String userName, String userPd) {
		return getInstance(null, null, url, userName, userPd);
	}
	
	public static DbUtils getInstance(String name, String driver, String url, String userName, String userPd) {
		if(instance==null) {
			instance = new DbUtils();
		}
		instance.datasourceName = null;
		instance.jdbcName = null;
		instance.jdbc = JdbcUtils.getInstance().getJdbcByJdbcInfo(name, driver, url, userName, userPd);
		return instance;
	}
	
	public void closeConnection() {
		try {
			Connection conn = this.localConn.get();
			if(conn!=null && !conn.isClosed()) {
				conn.close();
				this.localConn.remove();
			}
		} catch(SQLException e) {
		} catch(Throwable e) {
		}
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
	
	public static void closeConn(Connection conn) {
		try {
			if(conn!=null && !conn.isClosed()) {
				conn.close();
			}
		} catch(SQLException e) {
		} catch(Throwable e) {
		}
	}
	
	public Connection getConnection() throws Throwable {
		this.log.debug(
					"\r\n" + 
					"DbUtils.datasourceName == " + this.datasourceName + "\r\n" + 
					"DbUtils.jdbcName == " + this.jdbcName + "\r\n" +
					"DbUtils.jdbc == " + ((this.jdbc!=null)?this.jdbc.toString():null));
		return this.getConnection(this.datasourceName, this.jdbcName);
	}
	
	@SuppressWarnings("resource")
	public Connection getConnection(String datasourceName, String jdbcName) throws Throwable {
		Connection conn = null;
		try {
			conn = this.localConn.get();
			if(conn==null || conn.isClosed()) {
				if(!StringUtils.isBlank(datasourceName)) {
					conn = this.getConnectByDatasource(datasourceName);
				} else if(!StringUtils.isBlank(jdbcName)) {
					conn = this.getConnectByJdbc(jdbcName);
				} else if(this.jdbc!=null) {
					conn = this.jdbc.createConnection();
				} else {
					try {
						conn = this.getConnectByDatasource();
					} catch(Throwable e) {
						conn = this.getConnectByJdbc();
					}
				}
				if(conn==null) {
					throw new Throwable("Connection is null");
				}
				this.localConn.set(conn);
			}
		} catch(SQLException e) {
			throw (Throwable) e;
		} catch(Throwable e) {
			throw e;
		}
		return conn;
	}
	
	public Connection getConnectByDatasource() throws Throwable {
		return this.getConnectByDatasource(this.datasourceName);
	}
	
	public Connection getConnectByDatasource(String datasourceName) throws Throwable {
		Connection conn = null;
		try {
			String name = ((StringUtils.isBlank(datasourceName))?Constrain.DEFAULT_DATASOURCE_NAME:datasourceName);
			DataSource ds = (DataSource) new InitialContext().lookup("java:/comp/env/jdbc/" + name);
			if(ds==null) {
			   throw new Throwable("Data source not found !!");
			}
			conn = ds.getConnection();
		} catch(SQLException e) {
			throw (Throwable) e;
		} catch(Throwable e) {
			throw e;
		}
		return conn;
	}
	
	public Connection getConnectByJdbc() {
		return this.getConnectByJdbc(this.jdbcName);
	}
	
	public Connection getConnectByJdbc(String jdbcName) {
		return ((StringUtils.isBlank(jdbcName))?null:JdbcUtils.getInstance().getConnectionByJdbcName(jdbcName));
	}
	
	public static Connection getConnectionByJdbcInfo(String url, String userName, String userPd) {
		return getConnectionByJdbcInfo(null, null, url, userName, userPd);
	}
	
	public static Connection getConnectionByJdbcInfo(String name, String driver, String url, String userName, String userPd) {
		return JdbcUtils.getInstance().getConnectionByJdbcInfo(name, driver, url, userName, userPd);
	}
	
}
