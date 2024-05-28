package com.tt.facade;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.tt.util.CommonUtils;
import com.tt.util.DbUtils;

public class MainFacade {
	
	protected String className = this.getClass().getSimpleName();
	
	public static String BATCH_MAP_KEY = "executeSQL";
	
	public static String BATCH_MAP_VALUE = "valueList";
	
	public static String EXCEPTION_OBJECT = "exceptionObject";
	
	public static String EXCEPTION = "exception";
	
	public static String EXCEPTION_TIME = "exceptionTime";
	
	public static String ERRMSG = "errMsg";
	
	protected DbUtils dbUtils = null;
	
	protected Logger log = null;
	
	protected MainFacade() {
		this.initLog4j();
	}
	
	protected MainFacade(DbUtils dbutils) {
		this();
		this.dbUtils = dbutils;
	}
	
	protected MainFacade(Logger log) {
		this();
		try {
			if(log!=null) {
				this.log = log;
			} else {
				this.initLog4j();
			}
		} catch(Throwable e) {
		}
	}
	
	public void initLog4j() {
		try {
			CommonUtils.loadLog4j();
			this.log = Logger.getLogger(this.className);
		} catch(Throwable e) {
			e.printStackTrace();
		}
	}
	
	public void resetLogs(Logger log) {
		this.log = log;
	}
	
	private void closeStatement(Statement statement) {
		try {
			if(statement!=null && !statement.isClosed()) {
				statement.close();
			}
		} catch(SQLException e) {
		} catch(Throwable e) {
		}
	}
	
	private void closeResultSet(ResultSet rs) {
		try {
			if(rs!=null && !rs.isClosed()) {
				rs.close();
			}
		} catch(SQLException e) {
		} catch(Throwable e) {
		}
	}
	
	public void closeAll(ResultSet rs, Statement statement, Connection conn) {
		this.closeAll(rs, statement, conn, true);
	}
	
	public void closeAll(ResultSet rs, Statement statement, Connection conn, boolean isCloseConn) {
		if(rs!=null) {
			this.closeResultSet(rs);
		}
		if(statement!=null) {
			this.closeStatement(statement);
		}
		if(isCloseConn) {
			this.closeConn(conn);
		}
	}
	
	public void closeConn(Connection conn) {
		try {
			if(conn!=null && !conn.isClosed()) {
				conn.close();
			}
		} catch(SQLException e) {
		} catch(Throwable e) {
		}
	}
	
	public void commitConn(Connection conn, boolean isCloseConn) {
		if(conn!=null) {
			try {
				conn.commit();
			} catch(SQLException e) {
			} catch(Throwable e) {
			} finally {
				try {
					if(isCloseConn && !conn.isClosed()) {
						conn.close();
					}
				} catch(SQLException e) {
				} catch(Throwable e) {
				}
			}
		}
	}
	
	public void rollbackConn(Connection conn, boolean isCloseConn) {
		if(conn!=null) {
			try {
				conn.rollback();
			} catch(SQLException e) {
			} catch(Throwable e) {
			} finally {
				try {
					if(isCloseConn && !conn.isClosed()) {
						conn.close();
					}
				} catch(SQLException e) {
				} catch(Throwable e) {
				}
			}
		}
	}
	
	private PreparedStatement setInuptParam(List<Object> valueList, PreparedStatement statement) {
		try {
			if(statement==null || valueList==null || valueList.size()==0) {
				return statement;
			}
			int seq = 1;
			for(Object obj : valueList) {
				if(obj==null) {
					statement.setObject(seq, null);
				} else if(obj instanceof String) {
					statement.setString(seq, (String)obj);
				} else if(obj instanceof Long) {
					statement.setBigDecimal(seq, new BigDecimal(((Long)obj).longValue()));
				} else if(obj instanceof Float) {
					statement.setBigDecimal(seq, new BigDecimal(((Float)obj).floatValue()));
				} else if(obj instanceof Double) {
					statement.setBigDecimal(seq, new BigDecimal(((Double)obj).doubleValue()));
				} else if(obj instanceof Integer) {
					statement.setBigDecimal(seq, new BigDecimal(((Integer)obj).intValue()));
				} else if(obj instanceof BigDecimal) {
					statement.setBigDecimal(seq, (BigDecimal)obj);
				} else if(obj instanceof Timestamp) {
					statement.setTimestamp(seq, (Timestamp)obj);
				} else if(obj instanceof Date) {
					statement.setDate(seq, new java.sql.Date(((Date)obj).getTime()));
				} else {
					throw new Throwable("Unknown Type !!");
				}
				seq++;
			}
		} catch(SQLException e) {
			e.printStackTrace();
			this.logWarningPrint("MainFacade.setInuptParam() SQLException == " + e.getMessage());
		} catch(Throwable e) {
			e.printStackTrace();
			this.logWarningPrint("MainFacade.setInuptParam() Throwable == " + e.getMessage());
		}
		return statement;
	}
	
	private List<Map<String,Object>> resultSetToMap(ResultSet rs) {
		List<Map<String,Object>> rsList = new ArrayList<Map<String,Object>>();
		try {
			if(rs==null) {
				return rsList;
			}
			while(rs.next()) {
				ResultSetMetaData ra = rs.getMetaData();
				int count = ra.getColumnCount();
				if(count==0) {
					continue;
				}
				Map<String,Object> rsMap = new HashMap<String,Object>();
				for(int i=1; i<=count; i++) {
					String key = ra.getColumnName(i);
					Object value = rs.getObject(key);
					if(value==null) {
						rsMap.put(key, value);
					} else {
						if(value instanceof Timestamp) {
							rsMap.put(key, ((Timestamp)value).getTime());
						} else if(value instanceof java.sql.Date) {
							rsMap.put(key, new Date(((java.sql.Date)value).getTime()));
						} else {
							rsMap.put(key, value);
						}
					}
				}
				rsList.add(rsMap);
			}
		} catch(SQLException e) {
			e.printStackTrace();
			this.logWarningPrint("MainFacade.resultSetToMap() SQLException == " + e.getMessage());
		} catch(Throwable e) {
			e.printStackTrace();
			this.logWarningPrint("MainFacade.resultSetToMap() Throwable == " + e.getMessage());
		}
		return rsList;
	}
	
	private Map<String,Object> statementToMap(CallableStatement statement, Map<String,Integer> outputMap) {
		Map<String,Object> rsList = new HashMap<String,Object>();
		try {
			if(statement==null || outputMap==null || outputMap.size()==0) {
				return null;
			}
			Map<String,Object> rtnMap = new HashMap<String,Object>();
			String[] keys = (String[]) outputMap.keySet().toArray(new String[0]);
			Arrays.sort(keys);
			for(String key : keys) {
				Integer value = ((!StringUtils.isBlank(key))?(Integer)outputMap.get(key):null);
				if(value==null) {
					continue;
				}
				int keyInt = Integer.parseInt(key);
				if(value.intValue()==Types.VARCHAR) {
					rtnMap.put(key, statement.getString(keyInt));
				} else if(value.intValue()==Types.INTEGER) {
					rtnMap.put(key, statement.getInt(keyInt));
				} else if(value.intValue()==Types.DOUBLE) {
					rtnMap.put(key, statement.getDouble(keyInt));
				} else if(value.intValue()==Types.FLOAT) {
					rtnMap.put(key, statement.getFloat(keyInt));
				} else if(value.intValue()==Types.NUMERIC) {
					rtnMap.put(key, statement.getBigDecimal(keyInt));
				} else if(value.intValue()==Types.TIME) {
					rtnMap.put(key, statement.getTime(keyInt));
				} else if(value.intValue()==Types.TIMESTAMP) {
					rtnMap.put(key, statement.getTimestamp(keyInt));
				} else if(value.intValue()==Types.DATE) {
					rtnMap.put(key, statement.getDate(keyInt));
				} else {
					rtnMap.put(key, statement.getObject(keyInt));
				}
			}
			return rtnMap;
		} catch(SQLException e) {
			e.printStackTrace();
			this.logWarningPrint("MainFacade.statementToMap() SQLException == " + e.getMessage());
		} catch(Throwable e) {
			e.printStackTrace();
			this.logWarningPrint("MainFacade.statementToMap() Throwable == " + e.getMessage());
		}
		return rsList;
	}
	
	public List<Map<String,Object>> queryBySQL(String sql, List<Object> valueList) {
		return this.queryBySQL(null, sql, valueList);
	}
	
	public List<Map<String,Object>> queryBySQL(String sql) {
		return this.queryBySQL(null, sql);
	}
	
	public List<Map<String,Object>> queryBySQL(Connection conn, String sql) {
		Statement statement = null;
		ResultSet rs = null;
		try {
			if(StringUtils.isBlank(sql)) {
				return null;
			}
			conn = this.reConnection(conn);
			statement = conn.createStatement();
			rs = statement.executeQuery(sql);
			List<Map<String,Object>> rsList = this.resultSetToMap(rs);
			return ((rsList==null || rsList.size()==0)?null:rsList);
		} catch(SQLException e) {
			e.printStackTrace();
		} catch(Throwable e) {
			e.printStackTrace();
		} finally {
			this.closeAll(rs, statement, conn);
		}
		return null;
	}
	
	public List<Map<String,Object>> queryBySQL(Connection conn, String sql, List<Object> valueList) {
		return this.queryBySQL(conn, sql, valueList, true);
	}
	
	public List<Map<String,Object>> queryBySQL(Connection conn, String sql, boolean isCloseConn) {
		Statement statement = null;
		ResultSet rs = null;
		try {
			if(StringUtils.isBlank(sql)) {
				return null;
			}
			conn = this.reConnection(conn);
			statement = conn.createStatement();
			rs = statement.executeQuery(sql);
			List<Map<String,Object>> rsList = this.resultSetToMap(rs);
			this.logPrint("debug", "MainFacade.queryBySQL().rsList.szie() == " + ((rsList==null)?0:rsList.size()));
			return ((rsList==null || rsList.size()==0)?null:rsList);
		} catch(SQLException e) {
			e.printStackTrace();
			this.logWarningPrint("MainFacade.queryBySQL() SQLException == " + e.getMessage());
		} catch(Throwable e) {
			e.printStackTrace();
			this.logWarningPrint("MainFacade.queryBySQL() Throwable == " + e.getMessage());
		} finally {
			this.closeAll(rs, statement, conn, isCloseConn);
		}
		return null;
	}
	
	public List<Map<String,Object>> queryBySQL(Connection conn, String sql, List<Object> valueList, boolean isCloseConn) {
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			if(valueList==null || valueList.size()==0) {
				return this.queryBySQL(conn, sql, isCloseConn);
			}
			if(StringUtils.isBlank(sql)) {
				return null;
			}
			conn = this.reConnection(conn);
			statement = this.setInuptParam(valueList, conn.prepareStatement(sql));
			rs = statement.executeQuery();
			List<Map<String,Object>> rsList = this.resultSetToMap(rs);
			return ((rsList==null || rsList.size()==0)?null:rsList);
		} catch(SQLException e) {
			e.printStackTrace();
			this.logWarningPrint("MainFacade.queryBySQL() SQLException == " + e.getMessage());
		} catch(Throwable e) {
			e.printStackTrace();
			this.logWarningPrint("MainFacade.queryBySQL() Throwable == " + e.getMessage());
		} finally {
			this.closeAll(rs, statement, conn, isCloseConn);
		}
		return null;
	}
	
	public Map<String,Object> queryProcedureBySQL(Connection conn, String spName, List<Object> valueList, Map<String,Integer> outputMap, boolean isCloseConn) {
		CallableStatement statement = null;
		ResultSet rs = null;
		try {
			this.logPrint("debug", "MainFacade.queryProcedureBySQL().spName == " + spName);
			if(StringUtils.isBlank(spName)) {
				return null;
			}
			conn = this.reConnection(conn);
			statement = (CallableStatement) this.setInuptParam(valueList, conn.prepareCall("{call " + spName + "}"));
			if(outputMap!=null && outputMap.size()>0) {
				String[] keys = (String[]) outputMap.keySet().toArray(new String[0]);
				Arrays.sort(keys);
				for(String key : keys) {
					int type = (((Integer)outputMap.get(key)==null)?Types.VARCHAR:((Integer)outputMap.get(key)).intValue());
					statement.registerOutParameter(Integer.parseInt(key), type);
				}
			}
			statement.executeQuery();
			return this.statementToMap(statement, outputMap);
		} catch(SQLException e) {
			e.printStackTrace();
			this.logWarningPrint("MainFacade.queryProcedureBySQL() SQLException == " + e.getMessage());
		} catch(Throwable e) {
			e.printStackTrace();
			this.logWarningPrint("MainFacade.queryProcedureBySQL() Throwable == " + e.getMessage());
		} finally {
			this.closeAll(rs, statement, conn, isCloseConn);
		}
		return null;
	}
	
	public List<Map<String,Object>> executeBatchSQL(Connection conn, String sql, List<List<Object>> valueList) {
		return this.executeBatchSQL(conn, sql, valueList, true);
	}
	
	public List<Map<String,Object>> executeBatchSQL(Connection conn, String sql, List<List<Object>> valueList, boolean isSuccessAll) {
		List<Map<String,Object>> dataList = new ArrayList<Map<String,Object>>();
		if(valueList!=null) {
			for(List<Object> values : valueList) {
				Map<String,Object> dataMap = new HashMap<String,Object>();
				dataMap.put(BATCH_MAP_KEY, sql);
				dataMap.put(BATCH_MAP_VALUE, values);
				dataList.add(dataMap);
			}
		}
		return this.executeBatchSQL(conn, dataList, isSuccessAll, true);
	}
	
	public List<Map<String,Object>> executeBatchSQL(Connection conn, List<Map<String,Object>> dataList) {
		return this.executeBatchSQL(conn, dataList, true);
	}
	
	public List<Map<String,Object>> executeBatchSQL(Connection conn, List<Map<String,Object>> dataList, boolean isSuccessAll) {
		return this.executeBatchSQL(conn, dataList, isSuccessAll, true);
	}
	
	public boolean executeSQL(Connection conn, String sql) {
		return this.executeSQL(conn, sql, true);
	}
	
	public boolean executeSQL(Connection conn, String sql, boolean isCloseConn) {
		Statement statement = null;
		try {
			this.logPrint("info", "MainFacade.executeSQL().sql == " + sql);
			if(StringUtils.isBlank(sql)) {
				return false;
			}
			conn = this.reConnection(conn);
			conn.setAutoCommit(true);
			statement = conn.createStatement();
			statement.execute(sql);
			this.logPrint("debug", "MainFacade.executeSQL() success !!");
			return true;
		} catch(SQLException e) {
			e.printStackTrace();
			this.logWarningPrint("MainFacade.executeSQL() SQLException == " + e.getMessage());
		} catch(Throwable e) {
			e.printStackTrace();
			this.logWarningPrint("MainFacade.executeSQL() Throwable == " + e.getMessage());
		} finally {
			this.closeAll(null, statement, conn, isCloseConn);
		}
		return false;
	}
	
	public boolean executeSQL(Connection conn, String sql, List<Object> valueList) {
		return this.executeSQL(conn, sql, valueList, true);
	}
	
	public boolean executeSQL(Connection conn, String sql, List<Object> valueList, boolean isCloseConn) {
		PreparedStatement statement = null;
		try {
			if(valueList==null || valueList.size()==0) {
				return this.executeSQL(conn,sql);
			}
			if(StringUtils.isBlank(sql)) {
				return false;
			}
			conn = this.reConnection(conn);
			conn.setAutoCommit(true);
			statement = this.setInuptParam(valueList, conn.prepareStatement(sql));
			statement.executeUpdate();
			return true;
		} catch(SQLException e) {
			e.printStackTrace();
			this.logWarningPrint("MainFacade.executeSQL() SQLException == " + e.getMessage());
		} catch(Throwable e) {
			e.printStackTrace();
			this.logWarningPrint("MainFacade.executeSQL() Throwable == " + e.getMessage());
		} finally {
			this.closeAll(null, statement, conn, isCloseConn);
		}
		return false;
	}
	
	public Map<String,Object> executeProcedure(Connection conn, String spName, List<Object> valueList, Map<String,Integer> outputMap) {
		return this.executeProcedure(conn, spName, valueList, outputMap, true);
	}
	
	public Map<String,Object> executeProcedure(Connection conn, String spName, List<Object> valueList, Map<String,Integer> outputMap, boolean isCloseConn) {
		CallableStatement statement = null;
		ResultSet rs = null;
		try {
			this.logPrint("debug", "MainFacade.executeProcedure().spName == " + spName);
			if(StringUtils.isBlank(spName)) {
				return null;
			}
			conn = this.reConnection(conn);
			conn.setAutoCommit(true);
			statement = (CallableStatement) this.setInuptParam(valueList, conn.prepareCall("{call " + spName + "}"));
			if(outputMap!=null && outputMap.size()>0) {
				String[] keys = (String[]) outputMap.keySet().toArray(new String[0]);
				Arrays.sort(keys);
				for(String key : keys) {
					int type = (((Integer)outputMap.get(key)==null)?Types.VARCHAR:((Integer)outputMap.get(key)).intValue());
					statement.registerOutParameter(Integer.parseInt(key), type);
				}
			}
			statement.execute();
			rs = (ResultSet) statement.getObject(1);
			List<Map<String,Object>> rsList = this.resultSetToMap(rs);
			this.logPrint("debug", "MainFacade.executeProcedure().rsList.szie() == " + ((rsList!=null)?rsList.size():0));
			return ((rsList==null || rsList.size()==0)?null:(Map<String,Object>) rsList.get(0));
		} catch(SQLException e) {
			e.printStackTrace();
			this.logWarningPrint("MainFacade.executeProcedure() SQLException == " + e.getMessage());
		} catch(Throwable e) {
			e.printStackTrace();
			this.logWarningPrint("MainFacade.executeProcedure() Throwable == " + e.getMessage());
		} finally {
			this.closeAll(rs, statement, conn, isCloseConn);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> executeBatchSQL(Connection conn, List<Map<String,Object>> dataList, boolean isSuccessAll, boolean isCloseConn) {
		PreparedStatement statement = null;
		List<Map<String,Object>> errList = new ArrayList<Map<String,Object>>();
		try {
			if(dataList==null || dataList.size()==0) {
				Map<String,Object> errMap = new HashMap<String,Object>();
				errMap.put(ERRMSG, "Empty dataList !!");
				errList.add(errMap);
				return errList;
			}
			for(Map<String,Object> dataMap : dataList) {
				Map<String,Object> errMap = new HashMap<String,Object>();
				if(dataMap==null) {
					errMap.put(ERRMSG, "Empty dataMap !!");
				}
				if(!(dataMap.get(BATCH_MAP_KEY) instanceof String) 
							|| StringUtils.isBlank((String)dataMap.get(BATCH_MAP_KEY))) {
					errMap.put(ERRMSG, "Empty SQL !!");
				}
				if(errMap.size()>0) {
					errList.add(errMap);
					return errList;
				}
			}
			conn = this.reConnection(conn);
			conn.setAutoCommit(!isSuccessAll);
			String sql = null;
			for(Map<String,Object> dataMap : dataList) {
				String execSql = ((dataMap==null || dataMap.size()==0)?null:(String)dataMap.get(BATCH_MAP_KEY));
				if(StringUtils.isBlank(execSql)) {
					continue;
				}
				if(StringUtils.isBlank(sql)) {
					this.closeStatement(statement);
					statement = conn.prepareStatement(execSql);
				} else if(!StringUtils.equals(sql, execSql)) {
					statement.executeBatch();
					this.closeStatement(statement);
					statement = conn.prepareStatement(execSql);
				}
				sql = execSql;
				statement = this.setInuptParam((List<Object>)dataMap.get(BATCH_MAP_VALUE), statement);
				statement.addBatch();
			}
			if(sql!=null) {
				statement.executeBatch();
			}
			return ((errList==null || errList.size()==0)?null:errList);
		} catch(SQLException e) {
			e.printStackTrace();
			this.logWarningPrint("MainFacade.executeBatchSQL() SQLException == " + e.getMessage());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
			for(Map<String,Object> dataMap : dataList) {
				Map<String,Object> errMap = new HashMap<String,Object>();
				errMap.put(BATCH_MAP_KEY, (String) dataMap.get(BATCH_MAP_KEY));
				errMap.put(BATCH_MAP_VALUE, (List<Object>)dataMap.get(BATCH_MAP_VALUE));
				errMap.put(EXCEPTION_OBJECT, e);
				errMap.put(EXCEPTION, e.getMessage());
				errMap.put(EXCEPTION_TIME, sdf.format(new Date()));
				errList.add(errMap);
			}
		} catch(Throwable e) {
			e.printStackTrace();
			this.logWarningPrint("MainFacade.executeBatchSQL() Throwable == " + e.getMessage());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
			for(Map<String,Object> dataMap : dataList) {
				Map<String,Object> errMap = new HashMap<String,Object>();
				errMap.put(BATCH_MAP_KEY, (String) dataMap.get(BATCH_MAP_KEY));
				errMap.put(BATCH_MAP_VALUE, (List<Object>)dataMap.get(BATCH_MAP_VALUE));
				errMap.put(EXCEPTION_OBJECT, e);
				errMap.put(EXCEPTION, e.getMessage());
				errMap.put(EXCEPTION_TIME, sdf.format(new Date()));
				errList.add(errMap);
			}
		} finally {
			try {
				if(conn!=null) {
					if(isSuccessAll && errList.size()>0) {
						conn.rollback();
					} else {
						conn.commit();
					}
				}
			} catch(Throwable e) {
			}
			try {
				if(conn!=null) {
					conn.setAutoCommit(true);
				}
			} catch(Throwable e) {
			}
			this.closeAll(null, statement, conn, isCloseConn);
		}
		return ((errList==null || errList.size()==0)?null:errList);
	}
	
	public Map<String,Object> genRsMap(List<Map<String,Object>> dataList, String keyColumn, String valueColumn) {
		if(dataList==null || dataList.size()==0
				|| StringUtils.isBlank(keyColumn) || StringUtils.isBlank(valueColumn)) {
			return null;
		}
		Map<String,Object> rsMap = new HashMap<String,Object>();
		for(Map<String,Object> dataMap : dataList) {
			if(!(dataMap.get(keyColumn) instanceof String)) {
				continue;
			}
			String dataKey = (String) dataMap.get(keyColumn);
			if(!rsMap.containsKey(dataKey)) {
				rsMap.put(dataKey, dataMap.get(valueColumn));
			}
		}
		return ((rsMap!=null && rsMap.size()>0)?rsMap:null);
	}
	
	public Map<String,String> genRsStringMap(List<Map<String,Object>> dataList, String keyColumn, String valueColumn) {
		if(dataList==null || dataList.size()==0
				|| StringUtils.isBlank(keyColumn) || StringUtils.isBlank(valueColumn)) {
			return null;
		}
		Map<String,String> rsMap = new HashMap<String,String>();
		for(Map<String,Object> dataMap : dataList) {
			if(!(dataMap.get(keyColumn) instanceof String) || !(dataMap.get(valueColumn) instanceof String)) {
				continue;
			}
			String dataKey = (String) dataMap.get(keyColumn);
			if(!rsMap.containsKey(dataKey)) {
				rsMap.put(dataKey, (String) dataMap.get(valueColumn));
			}
		}
		return ((rsMap!=null && rsMap.size()>0)?rsMap:null);
	}
	
	public List<Object> genRsList(List<Map<String,Object>> dataList, String keyColumn) {
		if(dataList==null || dataList.size()==0 || StringUtils.isBlank(keyColumn)) {
			return null;
		}
		List<Object> rsList = new ArrayList<Object>();
		for(Map<String,Object> dataMap : dataList) {
			Object data = dataMap.get(keyColumn);
			if(!rsList.contains(data)) {
				rsList.add(data);
			}
		}
		return ((rsList!=null && rsList.size()>0)?rsList:null);
	}

	public List<String> genRsStringList(List<Map<String,Object>> dataList, String keyColumn) {
		if(dataList==null || dataList.size()==0 || StringUtils.isBlank(keyColumn)) {
			return null;
		}
		List<String> rsList = new ArrayList<String>();
		for(Map<String,Object> dataMap : dataList) {
			Object data = dataMap.get(keyColumn);
			if(!(data instanceof String)) {
				continue;
			}
			if(!rsList.contains((String) data)) {
				rsList.add((String) data);
			}
		}
		return ((rsList!=null && rsList.size()>0)?rsList:null);
	}
	
	public List<BigDecimal> genRsBigDecimalList(List<Map<String,Object>> dataList, String keyColumn) {
		if(dataList==null || dataList.size()==0 || StringUtils.isBlank(keyColumn)) {
			return null;
		}
		List<BigDecimal> rsList = new ArrayList<BigDecimal>();
		for(Map<String,Object> dataMap : dataList) {
			Object data = dataMap.get(keyColumn);
			if(!(data instanceof BigDecimal)) {
				continue;
			}
			if(!rsList.contains((BigDecimal) data)) {
				rsList.add((BigDecimal) data);
			}
		}
		return ((rsList!=null && rsList.size()>0)?rsList:null);
	}
	
	public List<Number> genRsNumberList(List<Map<String,Object>> dataList, String keyColumn) {
		if(dataList==null || dataList.size()==0 || StringUtils.isBlank(keyColumn)) {
			return null;
		}
		List<Number> rsList = new ArrayList<Number>();
		for(Map<String,Object> dataMap : dataList) {
			Object data = dataMap.get(keyColumn);
			if(!(data instanceof Number)) {
				continue;
			}
			if(!rsList.contains((Number) data)) {
				rsList.add((Number) data);
			}
		}
		return ((rsList!=null && rsList.size()>0)?rsList:null);
	}
	
	public Object findValueByKey(List<Map<String,Object>> dataList, String keyColum) {
		List<Object> values = this.genRsList(dataList,keyColum);
		return ((values!=null && values.size()>0)?values.get(0):null);
	}
	
	public String findValueStringByKey(List<Map<String,Object>> dataList, String keyColum) {
		List<String> values = this.genRsStringList(dataList,keyColum);
		return ((values!=null && values.size()>0)?values.get(0):null);
	}
	
	public BigDecimal findValueBigDecimalByKey(List<Map<String,Object>> dataList, String keyColum) {
		List<BigDecimal> values = this.genRsBigDecimalList(dataList,keyColum);
		return ((values!=null && values.size()>0)?values.get(0):null);
	}
	
	public Number findValueNumberByKey(List<Map<String,Object>> dataList, String keyColum) {
		List<Number> values = this.genRsNumberList(dataList,keyColum);
		return ((values!=null && values.size()>0)?values.get(0):null);
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> genPOJList(List<Map<String,Object>> dataList, Class<T> classObj) {
		try {
			Class<T> pojClass = ((classObj!=null)?(Class<T>) Class.forName(classObj.getName()):null);
			if(dataList==null || dataList.size()==0 || pojClass==null) {
				return null;
			}
			List<T> pojList = new ArrayList<T>();
			for(Map<String,Object> dataMap : dataList) {
				if(dataMap==null || dataMap.size()==0) {
					continue;
				}
				pojList.add(pojClass.getConstructor(Map.class).newInstance(dataMap));
			}
			return ((pojList==null || pojList.size()==0)?null:pojList);
		} catch(Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public <T> T genPOJ(List<Map<String,Object>> dataList, Class<T> classObj) {
		List<T> pojList = this.genPOJList(dataList, classObj);
		return ((pojList==null || pojList.size()==0)?null:(T)pojList.get(0));
	}
	
	protected void logPrint(String type, String msg) {
		CommonUtils.getInstance().logPrint(this.log, type, msg);
	}
	
	protected void logInfoPrint(String msg) {
		CommonUtils.getInstance().logInfoPrint(this.log, msg);
	}
	
	protected void logDebugPrint(String msg) {
		CommonUtils.getInstance().logDebugPrint(this.log, msg);
	}
	
	protected void logFinePrint(String msg) {
		CommonUtils.getInstance().logFinePrint(this.log, msg);
	}
	
	protected void logWarningPrint(String msg) {
		CommonUtils.getInstance().logWarningPrint(this.log, msg);
	}
	
	protected void logPrintStackTrace(Throwable e) {
		CommonUtils.getInstance().logPrintStackTrace(this.log, e);
	}
	
	/**
	 * Call for get each DB Connection
	 */
	public Connection reConnection() throws Throwable {
		return ((this.dbUtils!=null)?this.dbUtils.getConnection():null);
	}
	
	public Connection reConnection(Connection conn) throws Throwable {
		try {
			if(conn==null || conn.isClosed()) {
				conn = this.reConnection();
			}
			if(conn==null) {
				throw new Throwable("Connection is null");
			} else if(conn.isClosed()) {
				throw new Throwable("Connection is closed");
			}
		} catch(SQLException e) {
			e.printStackTrace();
			throw (Throwable) e;
		} catch(Throwable e) {
			e.printStackTrace();
			throw e;
		}
		return conn;
	}
	
	protected Timestamp strToTimestamp(String str, String pattern) throws Throwable {
		try {
			if(StringUtils.isBlank(str)) {
				return null;
			}
			pattern = ((StringUtils.isBlank(pattern))?"yyyy/MM/dd HH:mm:ss":pattern);
			return new Timestamp(new SimpleDateFormat(pattern).parse(str).getTime());
		} catch(Throwable e) {
			e.printStackTrace();
			throw e;
		}
	}
	
}

