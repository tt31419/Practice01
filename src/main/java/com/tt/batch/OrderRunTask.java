package com.tt.batch;

import java.sql.Connection;
import java.util.Date;
//import java.util.List;
//import java.util.Map;

//import tt.enums.CustErrorCode;
//import tt.enums.OrderStatus;
// import tt.facade.SQLFacade;
//import tt.util.CommonUtils;
import com.tt.util.DbUtils;
// import tt.CustException;

public class OrderRunTask extends BatchRunTask {

	public OrderRunTask() {
		super();
	}
	
	@Override
	public void runTask() {
		Connection conn = null;
//		boolean isCloseConn = false;
		Date startTime = new Date();
		try {
			
			if(super.isSaveLog) {
				super.startPrint("runTask()", startTime);
				super.runCount = 0;
			}
			
//			conn = SQLFacade.getInstance(super.log).reConnection(conn);
			
//			Map<String,String> configMap = UPPFacade.getInstance(super.log).findGWConfigs(conn, null, null, isCloseConn);
//			String hostNameList = ((configMap!=null)?(String)configMap.get("HOSTNAME_LIST"):null);  //Decide which AP can run Queue
//			if((","+hostNameList+",").indexOf(","+CommonUtils.getInstance().findHostName()+",")==-1) {
//				super.runCount++;
//				return;
//			}
//			
//			List<PvsRequest> pvsList = UPPFacade.getInstance(super.log).findGWOrdersByStatus(conn, OrderStatus.PROCESS.getValue(), configMap, isCloseConn);
//			if(pvsList!=null && pvsList.size()>0) {
//				//EAI/SPS/PCRF/SAAM 
//				//Master.UPP_STATUS=S(C/X), code=00000 msg=SUCESSS/(Bypass)
//				//Master.UPP_STATUS=X(F), code=ERROR_CODE msg=ERROR_MSG
//				super.logInfoPrint("PROCESS count : "+(pvsList.size()));
//				String HostName = CommonUtils.getInstance(super.log).findHostName();
//				for(PvsRequest pvs : pvsList) {
//					try {
//						super.logInfoPrint("pvs.getOrderId() === " + pvs.getOrderId());
//						String sendSystem = pvs.findUPPWorkSystem();
//						List<String> requestSeqs = pvs.findUppRequestSeqs();
//						if(!"HSS".equals(sendSystem) && (requestSeqs==null || requestSeqs.size()==0)) {
//							throw new CustException(CustErrorCode.ERROR_90003, "UppRequestSeq");
//						}
//						boolean isHss = false;
//						List<Map<String,Object>> dataList = null;
//						if("HSS".equals(sendSystem)) {  //EAI
//							dataList = UPPFacade.getInstance(super.log).findHssDone(conn, pvs.findMsisdnWith1stMaster(), pvs.getUppWorkSeq(), isCloseConn);
//							isHss = true;
//						} else if("SPS".equals(sendSystem)) {
//							dataList = UPPFacade.getInstance(super.log).findSpsOrderDone(conn, requestSeqs, isCloseConn);
//						} else if("PCRF".equals(sendSystem)) {
//							dataList = UPPFacade.getInstance(super.log).findLteOrderDone(conn, requestSeqs, isCloseConn);
//						} else if("SAAM".equals(sendSystem)) {
//							dataList = UPPFacade.getInstance(super.log).findSaamOrderDone(conn, requestSeqs, isCloseConn);
//						} else {
//							throw new CustException(CustErrorCode.ERROR_90008, "sendSystem", sendSystem);
//						}
//						//==null:flow not done yet 
//						if(dataList!=null && dataList.size()>0) {
//							if(pvs.resetUppResult(dataList, isHss)) {
//								pvs.setStatus(OrderStatus.DONE.getValue());
//							} else {  
//								pvs.setStatus(OrderStatus.FAIL.getValue());
//								pvs.setNote("result Fail");  
//								pvs.resetErrorExcep(new CustException(CustErrorCode.ERROR_90017));
//							}
//							UPPFacade.getInstance(super.log).updateGWOrder(conn, pvs, HostName, isCloseConn);
//						}
//					} catch(CustException e) {
//						pvs.setStatus(OrderStatus.FAIL.getValue());
//						pvs.resetErrorExcep(e);
//						super.logInfoPrint("pvs CPCException... : " + pvs);
//						super.logInfoPrint("pvs CPCException... : " + e);
//						UPPFacade.getInstance(super.log).updateGWOrder(conn, pvs, HostName, isCloseConn);
//					} catch(Throwable e) {
//						pvs.setStatus(OrderStatus.FAIL.getValue());
//						pvs.resetErrorExcep(e);
//						super.logInfoPrint("pvs Throwable... : " + e);
//						UPPFacade.getInstance(super.log).updateGWOrder(conn, pvs, HostName, isCloseConn);
//					}
//				}
//				super.runCount = 0;
//			} else {
//				super.runCount++;
//			}
			
//		} catch(CustException e) { 
//			e.printStackTrace();
//			super.logWarningPrint(super.className + ".runTask().CPCException == " + e.getMessage());
		} catch(Throwable e) { 
			e.printStackTrace();
			super.logWarningPrint(super.className + ".runTask().Throwable == " + e.getMessage());
		} finally {
			DbUtils.closeConn(conn);
			if(super.isSaveLog) {
				super.finalPrint("runTask()", startTime);
				this.isSaveLog = false;
			}
			super.resetSaveLog();
		}
	}
	
}

