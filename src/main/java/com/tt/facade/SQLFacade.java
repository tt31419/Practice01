package com.tt.facade;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.tt.enums.CustErrorCode;
import com.tt.util.CommonUtils;
import com.tt.util.DbUtils;
import com.tt.CustException;

public class SQLFacade extends MainFacade {

	private static SQLFacade instance = null;
	
	private SQLFacade() {
	}
	
	private SQLFacade(DbUtils dbUtils) {
		super(dbUtils);
	}
	
	private SQLFacade(Logger log) {
		super(log);
	}
	
	public static SQLFacade getInstance() {
		try {
			if(instance==null) {
				instance = new SQLFacade();
			}
			instance.dbUtils = DbUtils.getInstance();
		} catch(Throwable e) {
			e.printStackTrace();
		}
		return instance;
	}
	
	public static SQLFacade getInstance(DbUtils dbUtils) {
		if(dbUtils==null) {
			return getInstance();
		}
		if(instance==null) {
			instance = new SQLFacade();
		}
		instance.dbUtils = dbUtils;
		return instance;
	}
	
	public static SQLFacade getInstance(Logger log) {
		if(instance==null) {
			instance = new SQLFacade(log);
		} else {
			if(log==null) {
				instance.initLog4j();
			} else {
				instance.log = log;
			}
		}
		return instance;
	}
	
	public synchronized String genSys1SeqBySeqName(Connection conn, String seqName, boolean isCloseConn) throws CustException {
		return this.genSeqBySeqName(conn, seqName, "10", isCloseConn);
	}
	
	public synchronized String genSys2SeqBySeqName(Connection conn, String seqName, boolean isCloseConn) throws CustException {
		return this.genSeqBySeqName(conn, seqName, "9", isCloseConn);
	}
	
	public synchronized String genSeqBySeqName(Connection conn, String seqName, String length, boolean isCloseConn) throws CustException {
		try {
			super.logDebugPrint(super.className + ".genSeqBySeqName() !! ");
			super.logDebugPrint(super.className + ".genSeqBySeqName().seqName ==  " + seqName);
			//SEQ_Sys1_ID、SEQ_Sys2_ID、SEQ_Sys3_ID
			if(StringUtils.isBlank(seqName)) {
				throw new CustException(CustErrorCode.ERROR_90000, "seqName");
			}
			conn = super.reConnection(conn);
			String sql = "select LPAD(" + seqName + ".nextval, " + length + ", 0) as SEQ_ID from dual";
			return super.findValueStringByKey(super.queryBySQL(conn, sql, isCloseConn), "SEQ_ID");
		} catch(CustException e) {
			super.logPrintStackTrace(e);
			throw e;
		} catch(Throwable e) {
			super.logPrintStackTrace(e);
			throw new CustException(e);
		} finally {
			super.closeAll(null, null, conn, isCloseConn);
		}
	}
	
	public Map<String,String> findSystemConfigs(Connection conn, String name, String groupId, boolean isCloseConn) throws CustException {
		return this.findSystemConfigs(conn, name, groupId, false, isCloseConn);
	}
	
	public Map<String,String> findSystemConfigs(Connection conn, String name, String groupId, boolean isAll, boolean isCloseConn) throws CustException {
		try {
			super.logDebugPrint(super.className + ".findSystemConfigs() !! ");
			super.logDebugPrint(super.className + ".findSystemConfigs().name == " + name);
			super.logDebugPrint(super.className + ".findSystemConfigs().groupId == " + groupId);
			boolean isEmptyName = StringUtils.isBlank(name);
			boolean isEmptyGroupId = StringUtils.isBlank(groupId);
			String sql = "select NAME, VALUE "
					   + "from SYS_CONFIG "
					   + "where 1 = 1 "
					   + ((isEmptyName)?"":"and NAME = ? ")
					   + ((isEmptyGroupId)?"":"and GROUP_ID = ? ")
					   + ((isAll)?"":"and IS_ACTIVE = ? ")
					   + "order by GROUP_ID, NAME "
					   ;
			List<Object> valueList = new ArrayList<Object>();
			if(!isEmptyName) {
				valueList.add(name);
			}
			if(!isEmptyGroupId) {
				valueList.add(groupId);
			}
			if(!isAll) {
				valueList.add("Y");
			}
			conn = super.reConnection(conn);
			return super.genRsStringMap(super.queryBySQL(conn, sql, valueList, isCloseConn), "NAME", "VALUE");
		} catch(Throwable e) {
			super.logPrintStackTrace(e);
			throw new CustException(e);
		} finally {
			super.closeAll(null, null, conn, isCloseConn);
		}
	}
	
	public String findSYSConfigValueByName(Connection conn, String name, boolean isAll, boolean isCloseConn) throws CustException {
		try {
			super.logDebugPrint(super.className + ".findSYSConfigValueByName() !! ");
			super.logDebugPrint(super.className + ".findSYSConfigValueByName().name == " + name);
			if(StringUtils.isBlank(name)) {
				throw new CustException(CustErrorCode.ERROR_90000, "name");
			}
			String sql = "select VALUE "
					   + "from SYS_CONFIG "
					   + "where NAME = ? "
					   + ((isAll)?"":"and IS_ACTIVE = ? ")
					   ;
			List<Object> valueList = new ArrayList<Object>();
			valueList.add(name);
			if(!isAll) {
				valueList.add("Y");
			}
			conn = super.reConnection(conn);
			return super.findValueStringByKey(super.queryBySQL(conn, sql, valueList, isCloseConn), "VALUE");
		} catch(CustException e) {
			super.logPrintStackTrace(e);
			throw e;
		} catch(Throwable e) {
			super.logPrintStackTrace(e);
			throw new CustException(e);
		} finally {
			super.closeAll(null, null, conn, isCloseConn);
		}
	}
	
	
	/**
	 * only update Order's status for Fail cases 
	 * @author davidchen
	 */
	public void updateFailSYSOrders(Connection conn, List<String> orderIdList, String status, boolean isCloseConn) throws CustException {
		try {
			super.logDebugPrint(super.className + ".updateFailSYSOrders() !! ");
			if(orderIdList==null || orderIdList.size()==0) {
				throw new CustException(CustErrorCode.ERROR_90003, "orderList");
			}
			List<Object> values = new ArrayList<Object>();
			String forSql = "";
			values.add(status);
			for(String orderId : orderIdList) {
				if(StringUtils.isBlank(orderId)) {
					throw new CustException(CustErrorCode.ERROR_90000, "orderId");
				}
				values.add(orderId);
				forSql += ",?";
			}
			String sql = "update SYS_ORDER set "
					   + "STATUS=?, UPDATE_DATE=SYSDATE, "
					   + "ERROR_CODE='', ERROR_MSG='', NOTE='' "
					   + "where ORDER_ID in ( " + forSql.substring(1) + " ) "
				       ;
			conn = super.reConnection(conn);
			super.executeSQL(conn, sql, values, isCloseConn);
		} catch(CustException e) {
			super.logPrintStackTrace(e);
			throw e;
		} catch(Throwable e) {
			super.logPrintStackTrace(e);
			throw new CustException(e);
		} finally {
			super.closeAll(null, null, conn, isCloseConn);
		}
	}
	
	@SuppressWarnings("unused")
	private String findStartTime(int deltaDay) {
		return CommonUtils.getInstance().findStartTime(deltaDay);
	}
	
	public List<Map<String,Object>> findEntitlementByTarrifId(Connection conn, String tarrifId, boolean isCloseConn) throws CustException {
		try {
			super.logDebugPrint(super.className + ".findEntitlementByTarrifId() !! ");
			super.logDebugPrint(super.className + ".findEntitlementByTarrifId().tarrifId == " + tarrifId);
			if(StringUtils.isBlank(tarrifId)) {
				throw new CustException(CustErrorCode.ERROR_90001, "tarrifId");
			}
			String sql = "select " 
					   + "SPR_ID, SPR_NAME, SPR_ID_ROLLOVER, SPR_ID_ROLLOVER, SPR_NAME_ROLLOVER, PLAN_NAME, PLAN_TYPE, TOTAL_VOLUME " 
					   + "from LTE_ENTITLEMENT " 
					   + "where TARRIF_ID = ? " 
					   + "and IS_ACTIVE = ? " 
					   ;
			List<Object> valueList = new ArrayList<Object>();
			valueList.add(tarrifId);
			valueList.add("Y");
			conn = super.reConnection(conn);
			return super.queryBySQL(conn, sql, valueList, isCloseConn);
		} catch(Throwable e) {
			super.logPrintStackTrace(e);
			throw new CustException(e);
		} finally {
			super.closeAll(null, null, conn, isCloseConn);
		}
	}
	
}

