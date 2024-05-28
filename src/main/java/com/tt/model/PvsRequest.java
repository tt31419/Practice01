package com.tt.model;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import com.tt.CustException;
import com.tt.enums.CustErrorCode;
import com.tt.model.request.PvsReqOrderMaster;
import com.tt.util.CommonUtils;
/**
 * For POST / provisiontoupp
 */
@XmlType(name="CPC_REQ", propOrder={
	"reqUser",
	"reqPwd",
	"cpcWorkType",
	"cpcWorkId",
	"uppWorkType",
	"callType",
	"companyCode",
	"priority",
	"actionDate",
	"orderMasters"
})
public class PvsRequest {
	
	@XmlTransient
    private String oriJSON = null;

	@XmlTransient
    private String orderId = null;
	
	@XmlTransient
    private String type = "PVS_REQ";  //insert UPP_GW_ORDER_JSON
	
	@XmlElement(name="REQ_USER", required=true)
	private String reqUser = null;  //REQ_USER 登入帳號(以 Base64 加密)
	
	@XmlElement(name="USER_PWD", required=true)
	private String reqPwd = null;  //USER_PWD 登入密碼(以 Base64 加密)
	
	@XmlElement(name="WORK_TYPE", required=true)
	private String cpcWorkType = null;  //WORK_TYPE TWM申裝異動類別
	
	@XmlElement(name="WORK_ORDER_NBR", required=true)
	private String cpcWorkId = null;  //WORK_ORDER_NBR 異動聯單號碼
	
	@XmlElement(name="TST_WORK_TYPE", required=true)
	private String uppWorkType = null;  //TST_WORK_TYPE TST申裝異動類別
	
	@XmlTransient
    private String uppWorkSeq = null;
	
	@XmlTransient
	private List<String> uppWorkSeqs = null;
	
	@XmlTransient
    private String uppWorkSystem = null;
	
	@XmlElement(name="TST_CALL_TYPE", required=true)
	private String callType = null;  //TST_CALL_TYPE TST申裝異動識別碼
	
	@XmlElement(name="TST_COMPANY_CODE", required=true)
	private String companyCode = null;  //TST_COMPANY_CODE TST用戶別
	
	@XmlElement(name="PRIORITY", required=false)
	private String priority = null;  //PRIORITY 優先等級
	
	@XmlElement(name="ACTION_DATE", required=true)
	private String actionDate = null;  //ACTION_DATE 開通日期(格式YYYY/MM/DD)
	
	@XmlTransient
    private String status = null;

	@XmlTransient
    private String note = null;
	
	@XmlTransient
    private String errorCode = null;

	@XmlTransient
    private String errorMsg = null;
	
	@XmlTransient
    private String hostName = null;
	
	@XmlElement(name="PROV_ORDER_MASTER", required=true)
	private List<PvsReqOrderMaster> orderMasters = null;  //PROV_ORDER_MASTER
	
	public PvsRequest() {
	}
	
	public PvsRequest(Map<String,Object> dataMap, boolean isAddMaster) {
		this();
		if(dataMap!=null && dataMap.size()>0) {
			this.orderId = (String) dataMap.get("ORDER_ID");
			this.reqUser = (String) dataMap.get("LOGIN_ID");
			this.reqPwd = (String) dataMap.get("LOGIN_PWD");
			this.cpcWorkId = (String) dataMap.get("CPC_WORK_ID");
			this.cpcWorkType = (String) dataMap.get("CPC_WORK_TYPE");
			this.uppWorkType = (String) dataMap.get("UPP_WORK_TYPE");
			this.uppWorkSeq = (String) dataMap.get("UPP_WORK_SEQ");
			this.uppWorkSystem = (String) dataMap.get("UPP_WORK_SYSTEM");
			this.callType = (String) dataMap.get("UPP_CALL_TYPE");
			this.companyCode = (String) dataMap.get("UPP_COMPANY_CODE");
			this.priority = (String) dataMap.get("CPC_PRIORITY");
			this.actionDate = (String) dataMap.get("CPC_ACTION_DATE");
			this.status = (String) dataMap.get("STATUS");
			this.note = (String) dataMap.get("NOTE");
			this.errorCode = (String) dataMap.get("ERROR_CODE");
			this.errorMsg = (String) dataMap.get("ERROR_MSG");
			this.hostName = (String) dataMap.get("HOST_NAME");
			if(isAddMaster) {
				this.addOrderMasters(new PvsReqOrderMaster(dataMap));
			}
		}
	}
	
	public static Map<String,String> popFieldMap() throws CustException {
		try {
			Map<String,String> popMap = CommonUtils.getInstance().popFieldMap(PvsRequest.class);
			if(popMap==null || popMap.size()==0) {
				throw new CustException(CustErrorCode.ERROR_90003, "popMap");
			}
			popMap.putAll(PvsReqOrderMaster.popFieldMap());
			popMap.put("PROV_ORDER_MASTER", "orderMasters");
			return popMap;
		} catch(CustException e) {
			e.printStackTrace();
			throw e;
		} catch(Throwable e) {
			e.printStackTrace();
			throw new CustException(e);
		}
	}
	
	public static PvsRequest jsonToObj(String json) throws CustException {
		try {
			if(StringUtils.isBlank(json)) {
				throw new CustException(CustErrorCode.ERROR_90000, "json");
			}
			String oriJson = json;
			Map<String,String> popMap = popFieldMap();
			if(popMap==null || popMap.size()==0) {
				throw new CustException(CustErrorCode.ERROR_90003, "popMap");
			}
			String[] keys = (String[]) popMap.keySet().toArray(new String[0]);
			if(keys!=null && keys.length>0) {
				for(String key : keys) {
					String value = ((!StringUtils.isBlank(key))?(String)popMap.get(key):null);
					if(StringUtils.isBlank(value)) {
						continue;
					}
					json = json.replaceAll("\""+key+"\"", "\""+value+"\"");
				}
			}
			json = json.replace("\\", "").replace("\"null\"", "");
			PvsRequest pvs = (PvsRequest) CommonUtils.getInstance().jsonToObj(json, PvsRequest.class);
			if(pvs!=null) {
				pvs.setOriJSON(oriJson);
				pvs.sortMastersByCPCSequence();
				pvs.setHostName(CommonUtils.getInstance().findHostName());
			}
			return pvs;
		} catch(CustException e) {
			e.printStackTrace();
			throw e;
		} catch(Throwable e) {
			e.printStackTrace();
			throw new CustException(e);
		} 
	}
	
	public List<CustException> validMasterParameters(String names) {
		List<CustException> errList = new ArrayList<CustException>();
		try {
			//names >> order:REQ_USER,USER_PWD;master:IMSI,SUBSCR_ID;detail:ORDER_TYPE,CRM_ID
			if(this.isEmptyOrderMasters()) {
				errList.add(new CustException(CustErrorCode.ERROR_90003, "orderMasters"));
			} else {
				for(PvsReqOrderMaster master : this.orderMasters) {
					try {
						if(master==null) {
							errList.add(new CustException(CustErrorCode.ERROR_90003, "master"));
							continue;
						}
						errList.addAll(master.validParameters(names));
					} catch(Throwable ex) {
						ex.printStackTrace();
						errList.add(new CustException(ex));
					}
				}
			}
		} catch(Throwable e) {
			e.printStackTrace();
			errList.add(new CustException(e));
		}
		return errList;
	}
	
	public List<CustException> validParameters() {
		List<CustException> errList = new ArrayList<>();//Facade.getConfig
//		if(errList!=null && errList.size()>0) {
//			return errList;
//		}
		try {
//			Map<String,String> configMap = UPPFacade.getInstance().findGWConfigs(null, null, null, true);
//			//names >> order:REQ_USER,USER_PWD;master:IMSI,SUBSCR_ID;detail:ORDER_TYPE,CRM_ID
//			String names = (String) configMap.get("BYPASS_NAME_"+this.cpcWorkType);
//			String validNames = ValidUtils.getInstance().findByPassNames("order", names);
			String validNames = "order";
			String[] fieldNames = this.getClass().getAnnotation(XmlType.class).propOrder();
			if(fieldNames!=null && fieldNames.length>0) {
				for(String fieldName : fieldNames) {
					if("orderMasters".equals(fieldName)) {
						continue;
					}
					Field field = ((!StringUtils.isBlank(fieldName))?this.getClass().getDeclaredField(fieldName):null);
					String annName = ((field!=null)?field.getAnnotation(XmlElement.class).name():null);
					boolean required = ((field!=null)?field.getAnnotation(XmlElement.class).required():false);
					if((","+validNames+",").indexOf(","+annName+",")!=-1 
							|| !required) {
						continue;
					}
					Method method = this.getClass().getMethod("get"+StringUtils.capitalize(fieldName));
					Object fieldValue = ((method!=null)?method.invoke(this):null);
					if(fieldValue==null) {
						errList.add(new CustException(CustErrorCode.ERROR_90016, annName));
					} else {
						if(StringUtils.isBlank((String)fieldValue)) {
							errList.add(new CustException(CustErrorCode.ERROR_90000, annName));
						}
					}
					
					if(!StringUtils.equals(new String(Base64.decodeBase64(this.reqUser), "UTF-8"), "_dbReqUser")) {
						errList.add(new CustException(CustErrorCode.ERROR_90008, annName, this.reqUser));
					}else if("WORK_TYPE".equals(annName)) {  //TWM申裝異動類別
						//do nothing, valid in validWorkTypeCallType
					} else if("WORK_ORDER_NBR".equals(annName)) {  //異動聯單號碼
						//do nothing
					} else if("TST_WORK_TYPE".equals(annName)) {  //TST申裝異動類別
						//do nothing, valid in validWorkTypeCallType
					} else if("TST_CALL_TYPE".equals(annName)) {  //TST申裝異動識別碼
						//do nothing, valid in validWorkTypeCallType
					} 
				}
			}
			errList.addAll(this.validMasterParameters(validNames));
		} catch(Throwable e) {
			e.printStackTrace();
			errList.add(new CustException(e));
		}
		return errList;
	}
	
	public Date findActionDate() {
		try {
			if(!StringUtils.isBlank(this.actionDate)) {
				return new SimpleDateFormat("yyyy/MM/dd").parse(this.actionDate);
			}
		} catch(Throwable e) {
		}
		return null;
	}
	
	public boolean isEmptyOrderMasters() {
		return this.orderMasters==null || this.orderMasters.size()==0;
	}
	
	/**
	 * true 	uppStatus="S"<br>
	 * null		error<br>
	 * false	uppStatus="X"
	 */
	public Boolean isDoneOrderMaster() {
		if(this.isEmptyOrderMasters()) {
			return null;
		}
		boolean result = true;
		for(PvsReqOrderMaster orderMaster : this.orderMasters) {
			if(orderMaster==null || !orderMaster.isFbDone()) {
				if(orderMaster.isFbFail()) {
					result = false;
					continue;
				}
				return null;
			} 
		}
		return result;
	}
	
	public PvsReqOrderMaster find1stMaster() {
		if(this.isEmptyOrderMasters()) {
			return null;
		}
		return (PvsReqOrderMaster) this.orderMasters.get(0);
	}

	public List<PvsReqOrderMaster> sortMastersByCPCSequence() throws CustException {
		try {
			if(this.isEmptyOrderMasters() 
					|| this.orderMasters.size()==1) {
				return this.orderMasters;
			}
			Map<Long,PvsReqOrderMaster> sortMap = new HashMap<Long,PvsReqOrderMaster>();
			for(PvsReqOrderMaster orderMaster : this.orderMasters) {
				String seq = ((orderMaster!=null)?orderMaster.getCpcSequence():null);
				if(!StringUtils.isBlank(seq)) {
					Long key = new Long(seq);
					if(sortMap.containsKey(key)) {
//						throw new CustException(CustErrorCode.ERROR_90014, "CPC_SEQ_NBR", seq);
						continue;
					}
					sortMap.put(key, orderMaster);
				}
			}
			if(sortMap!=null && sortMap.size()>0) {
				this.orderMasters.clear();
				Long[] keys = (Long[]) sortMap.keySet().toArray(new Long[0]);
				Arrays.sort(keys);
				for(Long key : keys) {
					this.orderMasters.add((PvsReqOrderMaster)sortMap.get(key));
				}
			}
			return this.orderMasters;
//		} catch(CustException e) {
//			e.printStackTrace();
//			throw e;
		} catch(Throwable e) {
			e.printStackTrace();
			throw new CustException(e);
		}
	}

	public List<Map<String,String>> findProductInfos() throws CustException {
		return this.findProductInfos(false);
	}
	
	public List<Map<String,String>> findProductInfos(boolean isValid) throws CustException {
		try {
			if(this.isEmptyOrderMasters()) {
				return null;
			}
			List<PvsReqOrderMaster> sortedMasters = this.sortMastersByCPCSequence();
			if(sortedMasters==null || sortedMasters.size()==0) {
				return null;
			}
			List<Map<String,String>> productInfos = new ArrayList<Map<String,String>>();
			for(PvsReqOrderMaster orderMaster : sortedMasters) {
				String svcItem = ((orderMaster!=null)?orderMaster.getServiceItem():null);
				String actionCode = ((orderMaster!=null)?orderMaster.getActionCode():null);
				String productId = ((orderMaster!=null)?orderMaster.getProductId():null);
				if(StringUtils.isBlank(svcItem) 
						|| StringUtils.isBlank(actionCode) 
						|| StringUtils.isBlank(productId))  {
					continue;
				}
//				Map<String,String> productMap = this.genTstProductInfo(svcItem, actionCode, itemProdId, itemReqType);
				Map<String,String> productMap = this.genTstProductInfo(svcItem, actionCode, "", "");
				if(productMap==null || productMap.size()==0) {
					continue;
				}
				productInfos.add(productMap);
			}
			return ((productInfos!=null && productInfos.size()>0)?productInfos:null);
		} catch(Throwable e) {
			e.printStackTrace();
			throw new CustException(e);
		}
	}
	
	private Map<String,String> genTstProductInfo(String svcItem, String cpcActionCode, String productId, String requestType) {
		if(StringUtils.isBlank(productId) 
				|| ",C,P,D,R,".indexOf(","+requestType+",")==-1) {
			return null;
		}
		Map<String,String> productMap = new HashMap<String,String>();
		productMap.put("svcItem", svcItem);
		productMap.put("cpcActionCode", cpcActionCode);
		productMap.put("productId", productId);
		productMap.put("requestType", requestType);
		if("C".equals(requestType)) {
			productMap.put("uppActionCode", "New");
		} else if("P".equals(requestType)) {
			productMap.put("uppActionCode", "Suspend");
		} else if("D".equals(requestType)) {
			productMap.put("uppActionCode", "Terminate");
		} else if("R".equals(requestType)) {
			productMap.put("uppActionCode", "Reactivate");
		}
		return productMap;
	}
	
	public Map<String,String> findFbInfos() throws CustException {
		try {
			if(this.isEmptyOrderMasters()) {
				return null;
			}
			Map<String,String> rtnMap = new HashMap<String,String>();
			rtnMap.put("ORDER_ID", this.orderId);
			rtnMap.put("CPC_WORK_ID", this.cpcWorkId);
			rtnMap.put("UPP_WORK_SYSTEM", this.uppWorkSystem);
			rtnMap.put("UPP_WORK_SEQ", this.uppWorkSeq);
			return ((rtnMap!=null && rtnMap.size()>0)?rtnMap:null);
		} catch(Throwable e) {
			e.printStackTrace();
			throw new CustException(e);
		}
	}

	public List<String> findUppRequestSeqs() throws CustException {
		try {
			if(this.isEmptyOrderMasters()) {
				return null;
			}
			List<String> seqs = new ArrayList<String>();
			for(PvsReqOrderMaster orderMaster : this.orderMasters) {
				String reqSeq = ((orderMaster!=null)?orderMaster.getUppRequestSeq():null);
				if(StringUtils.isBlank(reqSeq) 
						|| seqs.contains(reqSeq)) {
					continue;
				}
				seqs.add(reqSeq);
			}
			return ((seqs!=null && seqs.size()>0)?seqs:null);
		} catch(Throwable e) {
			e.printStackTrace();
			throw new CustException(e);
		}
	}
	
	public List<String> findProductIds() throws CustException {
		try {
			if(this.isEmptyOrderMasters()) {
				return null;
			}
			List<String> productIds = new ArrayList<String>();
			for(PvsReqOrderMaster orderMaster : this.orderMasters) {
				String productId = ((orderMaster!=null)?orderMaster.getProductId():null);
				if(StringUtils.isBlank(productId) 
						|| productIds.contains(productId)) {
					continue;
				}
				productIds.add(productId);
			}
			return ((productIds!=null && productIds.size()>0)?productIds:null);
		} catch(Throwable e) {
			e.printStackTrace();
			throw new CustException(e);
		}
	}
	
	public PvsReqOrderMaster findOrderMasterByProductId(String productId) throws CustException {
		try {
			if(StringUtils.isBlank(productId) 
						|| this.isEmptyOrderMasters()) {
				return null;
			}
			for(PvsReqOrderMaster orderMaster : this.orderMasters) {
				if(StringUtils.equals(((orderMaster!=null)?orderMaster.getProductId():null), productId)) {
					return orderMaster;
				}
			}
		} catch(Throwable e) {
			e.printStackTrace();
			throw new CustException(e);
		}
		return null;
	}
	
	public boolean resetUppResult(List<Map<String,Object>> dataList, boolean isHss) throws CustException {
		try {
			if(!this.isEmptyOrderMasters()) {
				if(isHss) {
					if(this.isEmptyOrderMasters()) {
						throw new CustException(CustErrorCode.ERROR_90003, "masters");
					}
					Map<String,Object> byPassMap = null;
					Map<String,String> checkMap = new HashMap<String,String>();
					String hssStatus = null;
					for(PvsReqOrderMaster orderMaster : this.orderMasters) {
						String masterProduct = ((orderMaster!=null)?orderMaster.getProductId():null);
						String masterReqType = ((orderMaster!=null)?orderMaster.getActionCode():null);
						if(StringUtils.isBlank(masterProduct) 
								|| StringUtils.isBlank(masterReqType)) {
							continue;
						}
						masterReqType = (("A".equals(masterReqType))?"C":masterReqType);
						for(Map<String,Object> dataMap : dataList) {
							hssStatus = (String) dataMap.get("UWA_STATUS");
							String dataProduct = ((dataMap==null || dataMap.size()==0)?null:(String)dataMap.get("PRODUCT_ID"));
							String dataReqType = ((dataMap==null || dataMap.size()==0)?null:(String)dataMap.get("REQUEST_TYPE"));
							dataReqType = ((",P,R,U,".indexOf(","+dataReqType+",")!=-1)?"C":dataReqType);
							String rsStatus = (String) checkMap.get(dataProduct);
							if(StringUtils.isBlank(dataProduct) 
									|| !StringUtils.equals(masterProduct, dataProduct) 
									|| StringUtils.isBlank(dataReqType) 
									|| !StringUtils.equals(masterReqType, dataReqType) 
									|| "X".equals(rsStatus)) {
								continue;
							}
							dataMap.put("ORDER_ID", (String)dataMap.get("REQUEST_SEQ"));
							dataMap.put("UPDATE_DATE", (String)dataMap.get("REQUEST_END_DATE"));
							dataMap.put("ERROR_CODE", (String)dataMap.get("NE_RESULT_CODE"));
							dataMap.put("ERROR_MSG", (String)dataMap.get("NE_RESULT_DESC"));
							orderMaster.resetUppResult(dataMap);
							checkMap.put(dataProduct, orderMaster.getUppStatus());
						}
						//byPass is the lasted record in UPP_BATCH_NE_REQUEST_ALL(status is C or F)
						if(StringUtils.isBlank(orderMaster.getUppStatus())) {
							if(byPassMap==null || byPassMap.size()==0) {
								Map<String,Object> dataMap = dataList.get(dataList.size()-1);
								byPassMap = new HashMap<String,Object>();
								byPassMap.put("ORDER_ID", (String)dataMap.get("REQUEST_SEQ"));
								byPassMap.put("UPDATE_DATE", (String)dataMap.get("REQUEST_END_DATE"));
								byPassMap.put("STATUS", "C");
								byPassMap.put("IS_BYPASS", "Y");
							}
							orderMaster.resetUppResult(byPassMap);
						}
					}
					//master's productID = C、S, but flow fail
					if(!"C".equals(hssStatus)) {
						return false;
					}
					return true;
				} else {
					int count = 0;
					for(PvsReqOrderMaster orderMaster : this.orderMasters) {
						if(orderMaster==null) {
							continue;
						}
						for(Map<String,Object> dataMap : dataList) {
							if(dataMap==null || dataMap.size()==0) {
								continue;
							}
							if(StringUtils.equals((String)dataMap.get("ORDER_ID"), orderMaster.getUppRequestSeq())) {
								orderMaster.resetUppResult(dataMap);
								count++;
							}
						}
					}
					return count>0;
				}
			}
			return false;
		} catch(CustException e) {
			e.printStackTrace();
			throw e;
		} catch(Throwable e) {
			e.printStackTrace();
			throw new CustException(e);
		}
	}
	
	@Override
	public String toString() {
		return this.toString(0);
	}
	
	public String toString(int index) {
		String className = this.getClass().getSimpleName();
		String str = className + "["+index+"].orderId == " + this.orderId + "\r\n"
				   + className + "["+index+"].reqUser == " + this.reqUser + "\r\n"
				   + className + "["+index+"].reqPwd == " + this.reqPwd + "\r\n"
				   + className + "["+index+"].cpcWorkType == " + this.cpcWorkType + "\r\n"
				   + className + "["+index+"].cpcWorkId == " + this.cpcWorkId + "\r\n"
				   + className + "["+index+"].uppWorkType == " + this.uppWorkType + "\r\n"
				   + className + "["+index+"].uppWorkSeq == " + this.uppWorkSeq + "\r\n"
				   + className + "["+index+"].uppWorkSystem == " + this.uppWorkSystem + "\r\n"
				   + className + "["+index+"].callType == " + this.callType + "\r\n"
				   + className + "["+index+"].companyCode == " + this.companyCode + "\r\n"
				   + className + "["+index+"].priority == " + this.priority + "\r\n"
				   + className + "["+index+"].actionDate == " + this.actionDate + "\r\n"
				   + className + "["+index+"].status == " + this.status + "\r\n"
				   + className + "["+index+"].note == " + this.note + "\r\n"
				   + className + "["+index+"].errorCode == " + this.errorCode + "\r\n"
				   + className + "["+index+"].errorMsg == " + this.errorMsg + "\r\n"
				   + className + "["+index+"].hostName == " + this.hostName + "\r\n"
				   + this.toOrderMastersString(index)
				   ;
		return str;
	}
	
	public String toOrderMastersString(int index) {
		if(this.orderMasters==null || this.orderMasters.size()==0) {
			return "";
		}
		String className = this.getClass().getSimpleName();
		String str = className + "["+index+"].orderMasters == [\r\n";
		for(int i=0; i<this.orderMasters.size(); i++) {
			PvsReqOrderMaster master = this.orderMasters.get(i);
			str += ((master!=null)?master.toString(i, i<this.orderMasters.size()-1):"");
		}
		return str + "]\r\n";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.cpcWorkId==null)?0:this.cpcWorkId.hashCode());
		result = prime * result + ((this.cpcWorkType==null)?0 :this.cpcWorkType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this==obj) {
			return true;
		}
		if(obj==null 
				|| this.getClass()!=obj.getClass()) {
			return false;
		}
		PvsRequest other = (PvsRequest) obj;
		if((this.cpcWorkId==null && other.cpcWorkId!=null) 
				|| !this.cpcWorkId.equals(other.cpcWorkId) 
				|| (this.cpcWorkType==null && other.cpcWorkType!=null)
				|| !this.cpcWorkType.equals(other.cpcWorkType)) {
			return false;
		}
		return true;
	}
	
	public String getReqUser() {
		return reqUser;
	}

	public void setReqUser(String reqUser) {
		this.reqUser = reqUser;
	}

	public String getReqPwd() {
		return reqPwd;
	}

	public void setReqPwd(String reqPwd) {
		this.reqPwd = reqPwd;
	}

	public String getCpcWorkType() {
		return cpcWorkType;
	}

	public void setCpcWorkType(String cpcWorkType) {
		this.cpcWorkType = cpcWorkType;
	}

	public String getCpcWorkId() {
		return cpcWorkId;
	}

	public void setCpcWorkId(String cpcWorkId) {
		this.cpcWorkId = cpcWorkId;
	}

	public String getUppWorkType() {
		return uppWorkType;
	}

	public void setUppWorkType(String uppWorkType) {
		this.uppWorkType = uppWorkType;
	}

	public String getCallType() {
		return callType;
	}

	public void setCallType(String callType) {
		this.callType = callType;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getActionDate() {
		return actionDate;
	}

	public void setActionDate(String actionDate) {
		this.actionDate = actionDate;
	}

	public List<PvsReqOrderMaster> getOrderMasters() {
		return orderMasters;
	}

	public PvsReqOrderMaster findMasterByCpcSequence(String cpcSequence) {
		if(StringUtils.isBlank(cpcSequence) 
				|| this.isEmptyOrderMasters()) {
			return null;
		}
		for(PvsReqOrderMaster master : this.orderMasters) {
			String detailName = ((master!=null)?master.getCpcSequence():null);
			if(StringUtils.isBlank(detailName) 
					|| !cpcSequence.equals(detailName)) {
				continue;
			}
			return master;
		}
		return null;
	}
	
	public void setOrderMasters(List<PvsReqOrderMaster> orderMasters) {
		this.orderMasters = orderMasters;
	}
	
	public void addOrderMasters(List<PvsReqOrderMaster> orderMasters) {
		if(orderMasters==null || orderMasters.size()==0) {
			return;
		}
		if(this.orderMasters==null) {
			this.orderMasters = new ArrayList<PvsReqOrderMaster>();
		}
		for(PvsReqOrderMaster orderMaster : orderMasters) {
			if(orderMaster==null) {
				continue;
			}
			this.orderMasters.add(orderMaster);
		}
	}
	
	public void addOrderMasters(PvsReqOrderMaster orderMaster) {
		if(orderMaster==null) {
			return;
		}
		if(this.orderMasters==null) {
			this.orderMasters = new ArrayList<PvsReqOrderMaster>();
		}
		this.orderMasters.add(orderMaster);
	}
	
	public void removeOrderMasters() {
		if(this.orderMasters!=null && this.orderMasters.size()>0) {
			this.orderMasters.clear();
		}
	}

	public String getOriJSON() {
		return oriJSON;
	}

	public void setOriJSON(String oriJSON) {
		this.oriJSON = oriJSON;
	}
	
	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
	public String getType() {
		return type;
	}
	
	@SuppressWarnings("unused")
	private void setType(String type) {
		this.type = type;
	}

	public String getUppWorkSeq() {
		return uppWorkSeq;
	}

	public void setUppWorkSeq(String workSeq) {
		this.uppWorkSeq = workSeq;
	}
	
	public String getUppWorkSystem() {
		return uppWorkSystem;
	}

	public void setUppWorkSystem(String uppWorkSystem) {
		this.uppWorkSystem = uppWorkSystem;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<String> getUppWorkSeqs() {
		return uppWorkSeqs;
	}

	public void setUppWorkSeqs(List<String> uppWorkSeqs) {
		this.uppWorkSeqs = uppWorkSeqs;
	}
	
	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	
	public String getErrCode() {
		return errorCode;
	}

	public void setErrCode(String errCode) {
		this.errorCode = errCode;
	}

	public String getErrMsg() {
		return errorMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errorMsg = errMsg;
	}
	
	public void resetErrorExcep(Throwable e) {
		if(e!=null) {
			e.printStackTrace();
			if(e instanceof CustException) {
				this.errorCode = ((CustException)e).getExceptionCode();
				this.errorMsg = ((CustException)e).getExceptionMsg();
			} else {
				this.errorCode = CustErrorCode.ERROR_99999.getCode();
				this.errorMsg = CustErrorCode.replaceException(e);
			}
		} else {
			this.errorCode = null;
			this.errorMsg = null;
		}
	}

	public void addUppWorkSeqs(List<String> uppWorkSeqs) {
		if(uppWorkSeqs==null || uppWorkSeqs.size()==0) {
			return;
		}
		if(this.uppWorkSeqs==null) {
			this.uppWorkSeqs = new ArrayList<String>();
		}
		for(String uppWorkSeq : uppWorkSeqs) {
			if(StringUtils.isBlank(uppWorkSeq) 
					|| this.uppWorkSeqs.contains(uppWorkSeq)) {
				continue;
			}
			if(StringUtils.isBlank(this.uppWorkSeq)) {
				this.uppWorkSeq = uppWorkSeq;  //response 1st record to CPC for SPS/PCRF
			}
			this.uppWorkSeqs.add(uppWorkSeq);
		}
	}
	
	public void addUppWorkSeqs(String uppWorkSeq, String cpcSequence) {
		if(StringUtils.isBlank(uppWorkSeq)) {
			return;
		}
		if(this.uppWorkSeqs==null) {
			this.uppWorkSeqs = new ArrayList<String>();
		}
		if(!this.uppWorkSeqs.contains(uppWorkSeq)) {
			this.uppWorkSeqs.add(uppWorkSeq);
			if(StringUtils.isBlank(this.uppWorkSeq)) {
				this.uppWorkSeq = uppWorkSeq;  //response 1st record to CPC for SPS/PCRF
			}
			if(!StringUtils.isBlank(cpcSequence)) {
				PvsReqOrderMaster master = this.findMasterByCpcSequence(cpcSequence);
				if(master!=null) {
					master.setUppRequestSeq(uppWorkSeq);
				}
			}
		}
	}
	
}

