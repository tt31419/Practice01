package com.tt.servlet.restful;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
// import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.tt.enums.CustErrorCode;
import com.tt.util.CommonUtils;

@Path("/restservices")
public class RESTService {
	
	private String className = this.getClass().getSimpleName();
	
	private Logger log = Logger.getLogger(this.className);
	
	@Context
	private HttpServletRequest httpRequest = null;
	
	
	@GET
	@Path("/test")
	@Produces(MediaType.TEXT_HTML)
	public String testConnection() {
		Date currentTime = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd a hh:mm:ss");
		return "from IP == " +  this.getIpAddr(this.httpRequest)  + "\n" + dateFormat.format(currentTime) + "\nfine.";
	}
	
	
	@GET
	@Path("/testcpcconnection")
	@Consumes(MediaType.TEXT_HTML)
	public String testCPCConnection(@Context UriInfo info) {
		try {
			this.log.info("start testCPCConnection from IP == " +  this.getIpAddr(this.httpRequest));
			String urlType = null;
			urlType = info.getQueryParameters().getFirst("urlType");
			return "done.\n " + urlType;
		} catch(Throwable e) {
			e.printStackTrace();
		}
		return "fail done.";
	}
	
	@POST
	@Path("/cpcprovisionresult")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JsonObject cpcResult(JsonObject jsonObject) throws Exception {
		JsonObject resp = null;
		String respJson = "{\"status\":\"FAIL.\"}";
		try {
			this.log.info("start cpcResult==cpcprovisionresult, from IP == " +  this.getIpAddr(this.httpRequest));
			this.log.info("\r\ncpcprovisionresult Request == \r\n" + jsonObject);
			String cpcWorkId = jsonObject.getString("WORK_ORDER_NBR");
			String uppWorkId = jsonObject.getString("TST_PROCESS_NBR");
			respJson = "{" 
					 + " \"WORK_ORDER_NBR\":\"" + cpcWorkId + "\","
					 + " \"TST_PROCESS_NBR\":\"" + uppWorkId + "\"," 
					 + " \"PROCESS_DATE\":\"" + CommonUtils.getInstance().timeToString(new Date(), "") + "\"," 
					 + " \"NOTE\":\"" + CommonUtils.getInstance().findHostName() + ", for testing\"," 
					 + " \"RESULT_CODE\":\"0\"," 
					 + " \"RESULT_MSG\":\"success\"" 
					 + "}"
					 ;
			this.log.info("\r\ncpcprovisionresult respJson == \r\n" + respJson);
		} catch(Throwable e) {
			e.printStackTrace();
			respJson = "{\"status\":\"FAIL.\"}";
		} finally {
			try {
				resp = this.reponseREST(respJson);
			} catch(Throwable ex) {
				//do nothing
			}
		}
		return resp;
	}

//	@SuppressWarnings("unchecked")
//	@POST
//	@Path("/provisiontoupp")
//	@Produces(MediaType.APPLICATION_JSON)
//	@Consumes(MediaType.APPLICATION_JSON)
//	public JsonObject pvsToUPP(JsonObject jsonObject) throws Exception {
//		Connection conn = null;
//		boolean isCloseConn = false;
//		PvsRequest pvs = null;
//		JsonObject rtnJson = null;
//		String sourceIP = null;
//		Date sDate = new Date();
//		try {
//			
//			CommonUtils.loadLog4j();
//			this.log = Logger.getLogger("pvsToUPP");
//			
//			sourceIP = this.getIpAddr(this.httpRequest);
//			this.log.info("Call provisiontoupp start at " + CommonUtils.getInstance().timeToString(sDate) + ", from IP == " +  sourceIP);
//			
//			conn = UPPFacade.getInstance().reConnection(conn);
//			Map<String,String> configMap = UPPFacade.getInstance(this.log).findGWConfigs(conn, null, null, isCloseConn);
//			
//			if(!"Y".equals((String)configMap.get("IS_PVSTOUPP_ON"))) {
//				this.log.info("\r\n rejecting JSON == \r\n" + jsonObject.toString());
//				String respJson = "{\"RESULT_MSG\":\"SYSTEM MAITAINANCE\"}";
//				return this.reponseREST(respJson);
//			}
//			
//			pvs = PvsRequest.jsonToObj(jsonObject.toString());
//			//UPP_GW_ORDER.STATUS -->W
//			pvs.setOrderId(UPPFacade.getInstance(this.log).insertGWOrder(conn, pvs, isCloseConn));
//			pvs.setNote("IP=" + sourceIP);
//			UPPFacade.getInstance().insertGWJson(conn, pvs.getOrderId(), pvs.getType(), pvs.getOriJSON(), pvs.getNote(), isCloseConn);
//
//			CPCException excep = ValidUtils.getInstance(this.log).findPvsValidResult(pvs);
//			if(excep!=null) {
//				return this.reponseCPC(conn, pvs, excep, isCloseConn);
//			}
//			
//			if(!"Y".equals((String)configMap.get("IS_LOCAL_TO_CPC"))) {
//				if("0:0:0:0:0:0:0:1".equals(sourceIP) || sourceIP.startsWith("172.20.40.") ) {
//					//in order to decide where to send, set CpcWorkId in ReqPwd temporary
//					pvs.setReqPwd(pvs.getCpcWorkId());
//					pvs.setCpcWorkId("gw_" + StringUtils.right(pvs.getOrderId(), 7));
//					UPPFacade.getInstance().updateGWOrderTest(conn, pvs, isCloseConn);
//				}
//			}
//			
//			//UPP_GW_ORDER.STATUS W-->O
//			pvs = this.updatePvsStatus(conn, pvs, OrderStatus.OPEN.getValue(), isCloseConn);
//			
//			String sendSystem = pvs.findUPPWorkSystem();
//			if("HSS".equals(sendSystem)) {
//				pvs.setUppWorkSeq(EAIUtils.getInstance(this.log).gwToEAI(conn, pvs, pvs.getOriJSON(), isCloseConn));
//			} else if("SPS".equals(sendSystem)) {
//				Map<String,Object> rsMap = UPPFacade.getInstance().insertToSpsOrder(conn, pvs, isCloseConn);
//				List<CPCException> errList = (List<CPCException>) rsMap.get("errList");
//				pvs = (PvsRequest) rsMap.get("PvsRequest");
//				if(errList!=null && errList.size()>0) {
//					return this.reponseCPC(conn, pvs, (CPCException)errList.get(0), isCloseConn);
//				}
//			} else if("PCRF".equals(sendSystem)) {
//				Map<String,Object> rsMap = UPPFacade.getInstance().insertToLteOrder(conn, pvs, isCloseConn);
//				List<CPCException> errList = (List<CPCException>) rsMap.get("errList");
//				pvs = (PvsRequest) rsMap.get("PvsRequest");
//				if(errList!=null && errList.size()>0) {
//					return this.reponseCPC(conn, pvs, (CPCException)errList.get(0), isCloseConn);
//				}
//			} else if("SAAM".equals(sendSystem)) {
//				List<Map<String,String>> infos = ((pvs!=null)?pvs.findSaamInfos():null);
//				excep = ValidUtils.getInstance(this.log).findSaamValidResult(infos);
//				if(excep!=null) {
//					return this.reponseCPC(conn, pvs, excep, isCloseConn);
//				}
//				Map<String,Object> rsMap = UPPFacade.getInstance().insertToSaamOrder(conn, pvs, infos, isCloseConn);
//				List<CPCException> errList = (List<CPCException>) rsMap.get("errList");
//				pvs = (PvsRequest) rsMap.get("PvsRequest");
//				if(errList!=null && errList.size()>0) {
//					return this.reponseCPC(conn, pvs, (CPCException)errList.get(0), isCloseConn);
//				}
//			} else {
//				throw new CPCException(CPCErrorCode.ERROR_90013, "sendSystem");
//			}
//
//			//UPP_GW_ORDER.STATUS O-->P
//			rtnJson = this.reponseCPC(conn, pvs, isCloseConn);
//
//		} catch(CPCException e) {
//			e.printStackTrace();
//			rtnJson = this.reponseCPC(conn, pvs, e, isCloseConn);
//		} catch(Throwable e) {
//			e.printStackTrace();
//			rtnJson = this.reponseCPC(conn, pvs, e, isCloseConn);
//		} finally {
//			UPPFacade.getInstance().closeAll(null, null, conn, !isCloseConn);
//			Date eDate = new Date();
//			this.log.info(
//					"Call provisiontoupp end at " + CommonUtils.getInstance().timeToString(eDate) + ", "
//												  + "from IP == " +  sourceIP + ", "
//												  + "CostTime == " + CommonUtils.getInstance().calCostTime(sDate, eDate));
//		} 
//		return rtnJson;
//	}
//	
//	private JsonObject reponseCPC(Connection conn, PvsRequest pvs, boolean isCloseConn) {
//		return this.reponseCPC(conn, pvs, null, isCloseConn);
//	}
//	
//	private JsonObject reponseCPC(Connection conn, PvsRequest pvs, Throwable excep, boolean isCloseConn) {
//		PvsResponse pvsResponse = null;
//		boolean isExcep = false;
//		try {
//			if(pvs==null) {
//				pvsResponse = new PvsResponse(excep);
//				isExcep = true;
//			}else {
//				pvs.resetErrorExcep(excep);
//				if(excep!=null 
//						&& excep instanceof CPCException 
//						&& ((CPCException)excep).isByPass()) {
//					pvs = this.updatePvsStatus(conn, pvs, OrderStatus.COMPLETE.getValue(), isCloseConn);
//				} else {
//					pvs = this.updatePvsStatus(conn, pvs, ((excep!=null)?OrderStatus.FAIL:OrderStatus.PROCESS).getValue(), isCloseConn);
//				}
//				pvsResponse = new PvsResponse(pvs.getOrderId(), pvs, excep);
//				pvsResponse.resetOriJSON();
//				UPPFacade.getInstance().insertCPCResponse(conn, pvsResponse, isCloseConn);
//			}
//		} catch(CPCException e) {
//			e.printStackTrace();
//			pvsResponse = new PvsResponse(e);
//			isExcep = true;
//		} catch(Throwable e) {
//			e.printStackTrace();
//			pvsResponse = new PvsResponse(e);
//			isExcep = true;
//		} finally {
//			try {
//				if(isExcep) {
//					pvsResponse.resetOriJSON();
//					UPPFacade.getInstance().insertCPCResponse(conn, pvsResponse, isCloseConn);
//				}
//				return CommonUtils.getInstance().jsonStrToJavaxJson(pvsResponse.getOriJSON());
//			} catch(Throwable ex) {
//				//do nothing
//			}
//		}
//		return null;
//	}
//	
//	private PvsRequest updatePvsStatus(Connection conn, PvsRequest pvs, String status, boolean isCloseConn) throws CPCException {
//		try {
//			if(pvs!=null) {
//				pvs.setStatus(status);
//				UPPFacade.getInstance().updateGWOrder(conn, pvs, null, isCloseConn);
//			}
//			return pvs;
//		} catch(CPCException e) {
//			e.printStackTrace();
//			throw e;
//		} catch(Throwable e) {
//			e.printStackTrace();
//			throw new CPCException(e);
//		} finally {
//			UPPFacade.getInstance().closeAll(null, null, conn, isCloseConn);
//		}
//	}
	
	private String getIpAddr(HttpServletRequest request) {
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
	
	private JsonObject reponseREST(String respJson) throws Throwable {
		if(StringUtils.isBlank(respJson)) {
			throw new Throwable(CustErrorCode.ERROR_90000.replaceParameter("respJson", null));
		}
		return Json.createReader(new StringReader((respJson))).readObject();
	}
	
}

