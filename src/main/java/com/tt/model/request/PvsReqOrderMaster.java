package com.tt.model.request;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;
import com.tt.util.CommonUtils;
import com.tt.CustException;
import com.tt.enums.CustErrorCode;

@XmlType(name="PROV_ORDER_MASTER", propOrder={
	"cpcSequence",
	"areaCode",
	"phoneNumber",
	"tstImsi",
	"twmContractId",
	"tstContractId",
	"serviceItem",
	"actionCode",
	"productId",
	"sendQueue",
	"twmBC",
	"orderDetails"
})
public class PvsReqOrderMaster {
	
	@XmlTransient
    private String masterId = null;
	
	@XmlTransient
    private String orderId = null;

	@XmlElement(name="CPC_SEQ_NBR", required=true)
	private String cpcSequence = null;  //CPC_SEQ_NBR 開通序號
	
	@XmlElement(name="AREA_CDE", required=true)
	private String areaCode = null;  //AREA_CDE 區碼
	
	@XmlElement(name="PHONE_NBR", required=true)
	private String phoneNumber = null;  //PHONE_NBR 電話號碼
	
	@XmlElement(name="IMSI", required=true)
	private String tstImsi = null;  //IMSI SIM卡序號
	
	@XmlElement(name="SUBSCR_ID", required=true)
	private String twmContractId = null;  //SUBSCR_ID TWM用戶識別碼
	
	@XmlElement(name="TST_CONTRACT_ID", required=true)
	private String tstContractId = null;  //TST_CONTARCT_ID TST用戶識別碼
	
	@XmlElement(name="SVC_ITEM", required=true)
	private String serviceItem = null;  //SVC_ITEM 服務項目代碼
	
	@XmlElement(name="ACTION_CDE", required=true)
	private String actionCode = null;  //ACTION_CDE 開通命令類別 (A/D)
	
	@XmlElement(name="TST_PRODUCT_ID", required=true)
	private String productId = null;  //TST_PRODUCT_ID TST產品代碼
	
	@XmlElement(name="TST_SEND_QUEUE", required=true)
	private String sendQueue = "N";  //TST_SEND_QUEUE 是否送往Queue進行申裝異動(Y/N)
	
	@XmlElement(name="BILLING_CYCLE", required=false)
	private String twmBC = null;  //BILLING_CYCLE TWM帳週
	
	@XmlTransient
    private String uppRequestSeq = null;
	
	@XmlTransient
    private String uppStatus = null;
	
	@XmlTransient
	private String uppCompleteDate = null;
	
	@XmlTransient
	private String uppResultCode = null;
	
	@XmlTransient
	private String uppResultMsg = null;
	
	@XmlTransient
    private String note = null;
	
	@XmlElement(name="PROV_ORDER_DETAIL", required=false)
	private List<PvsReqOrderDetail> orderDetails = null;  //PROV_ORDER_DETAIL
	
	public PvsReqOrderMaster() {
	}
	
	public PvsReqOrderMaster(Map<String,Object> dataMap) {
		this();
		if(dataMap!=null && dataMap.size()>0) {
			this.masterId = (String) dataMap.get("MASTER_ID");
			this.orderId = (String) dataMap.get("ORDER_ID");
			this.cpcSequence = (String) dataMap.get("CPC_SEQUENCE");
			this.areaCode = (String) dataMap.get("CPC_AREA_CODE");
			this.phoneNumber = (String) dataMap.get("CPC_PHONE_NUMBER");
			this.tstImsi = (String) dataMap.get("TST_IMSI");
			this.twmContractId = (String) dataMap.get("TWM_CONTRACT_ID");
			this.tstContractId = (String) dataMap.get("TST_CONTRACT_ID");
			this.serviceItem = (String) dataMap.get("CPC_SVC_ITEM");
			this.actionCode = (String) dataMap.get("CPC_ACTION_CODE");
			this.productId = (String) dataMap.get("UPP_PRODUCT_ID");
			this.sendQueue = (String) dataMap.get("SEND_QUEUE");
			this.twmBC = (String) dataMap.get("TWM_BC");
			this.uppRequestSeq = (String) dataMap.get("UPP_REQUEST_SEQ");
			this.uppStatus = (String) dataMap.get("UPP_STATUS");
			this.uppCompleteDate = (String) dataMap.get("UPP_COMPLETE_DATE");
			this.uppResultCode = (String) dataMap.get("UPP_RESULT_CODE");
			this.uppResultMsg = (String) dataMap.get("UPP_RESULT_MSG");
			this.note = (String) dataMap.get("NOTE");
		}
	}
	
	public boolean isEmptyOrderDetails() {
		return this.orderDetails==null || this.orderDetails.size()==0;
	}
	
	public PvsReqOrderDetail findDetailByName(String name) {
		if(StringUtils.isBlank(name) 
				|| this.isEmptyOrderDetails()) {
			return null;
		}
		for(PvsReqOrderDetail detail : this.orderDetails) {
			String detailName = ((detail!=null)?detail.getParamName():null);
			if(StringUtils.isBlank(detailName) 
					|| !name.equals(detailName)) {
				continue;
			}
			return detail;
		}
		return null;
	}
	
	public String findDetailValueByName(String name) {
		PvsReqOrderDetail detail = ((StringUtils.isBlank(name))?null:this.findDetailByName(name));
		return ((detail!=null)?detail.getParamValue():null);
	}
	
	public boolean isSendQueue() {
		return "Y".equals(this.sendQueue);
	}

	public boolean isFbDone() {
		return ("S".equals(this.uppStatus));
	}
	
	public boolean isFbFail() {
		return ("X".equals(this.uppStatus));
	}
	
	public Map<String,String> findFbInfos() throws CustException {
		try {
			if(!this.isFbDone()) {
				return null;
			}
			Map<String,String> fbInfos = new HashMap<String,String>();
			fbInfos.put("CPC_SEQUENCE", this.cpcSequence);
			fbInfos.put("CPC_AREA_CODE", this.areaCode);
			fbInfos.put("CPC_PHONE_NUMBER", this.phoneNumber);
			fbInfos.put("TST_IMSI", this.getTstImsi());
			fbInfos.put("TWM_CONTRACT_ID", this.twmContractId);
			fbInfos.put("TST_CONTRACT_ID", this.tstContractId);
			fbInfos.put("CPC_SVC_ITEM", this.serviceItem);
			fbInfos.put("CPC_ACTION_CODE", this.actionCode);
			fbInfos.put("UPP_PRODUCT_ID", this.productId);
			fbInfos.put("UPP_REQUEST_SEQ", this.uppRequestSeq);
			fbInfos.put("STATUS", this.uppStatus);
			fbInfos.put("COMPLETE_DATE", this.uppCompleteDate);
			fbInfos.put("RESULT_CODE", this.uppResultCode);
			fbInfos.put("RESULT_MSG", this.uppResultMsg);
			return fbInfos;
		} catch(Throwable e) {
			e.printStackTrace();
			throw new CustException(e);
		}
	}
	
	public static Map<String,String> popFieldMap() throws CustException {
		try {
			Map<String,String> popMap = CommonUtils.getInstance().popFieldMap(PvsReqOrderMaster.class);
			if(popMap==null || popMap.size()==0) {
				throw new CustException(CustErrorCode.ERROR_90003, "popMap");
			}
			popMap.putAll(PvsReqOrderDetail.popFieldMap());
			popMap.put("PROV_ORDER_DETAIL", "orderDetails");
			return popMap;
		} catch(CustException e) {
			e.printStackTrace();
			throw e;
		} catch(Throwable e) {
			e.printStackTrace();
			throw new CustException(e);
		}
	}
	
	public List<CustException> validDetailParameters(String names) {
		List<CustException> errList = new ArrayList<CustException>();
		try {
			//names >> order:REQ_USER,USER_PWD;master:IMSI,SUBSCR_ID;detail:ORDER_TYPE,CRM_ID
			if(!this.isEmptyOrderDetails()) {
				for(PvsReqOrderDetail detail : this.orderDetails) {
					try {
						if(detail==null) {
							errList.add(new CustException(CustErrorCode.ERROR_90003, "detail"));
							continue;
						}
						errList.addAll(detail.validParameters(this, names));
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
	
	public List<CustException> validParameters(String names) {
		List<CustException> errList = new ArrayList<CustException>();
		try {
			//names >> order:REQ_USER,USER_PWD;master:IMSI,SUBSCR_ID;detail:ORDER_TYPE,CRM_ID
			String[] fieldNames = this.getClass().getAnnotation(XmlType.class).propOrder();
			if(fieldNames!=null && fieldNames.length>0) {
				for(String fieldName : fieldNames) {
//					if("orderDetails".equals(fieldName)) {
//						continue;
//					}
					Field field = ((!StringUtils.isBlank(fieldName))?this.getClass().getDeclaredField(fieldName):null);
					String annName = ((field!=null)?field.getAnnotation(XmlElement.class).name():null);
					boolean required = ((field!=null)?field.getAnnotation(XmlElement.class).required():false);
					if((","+"_validNames"+",").indexOf(","+annName+",")!=-1 
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
//					if("CPC_SEQ_NBR".equals(annName)) {  //開通序號
//						if(!StringUtils.isBlank(this.cpcSequence)) {
//							if(CommonUtils.getInstance().isNaN(this.cpcSequence)) {
//								errList.add(new CustException(CustErrorCode.ERROR_90007, annName));
//							}
//						}
//					} else if("AREA_CDE".equals(annName)) {  //區碼
//						if(!StringUtils.isBlank(this.areaCode)) {
//							if(CommonUtils.getInstance().isNaN(this.areaCode)) {
//								errList.add(new CustException(CustErrorCode.ERROR_90007, annName));
//							}
//						}
//					} else if("PHONE_NBR".equals(annName)) {  //電話號碼
//						if(!StringUtils.isBlank(this.phoneNumber)) {
//							if(CommonUtils.getInstance().isNaN(this.phoneNumber)) {
//								errList.add(new CustException(CustErrorCode.ERROR_90007, annName));
//							}
//						}
//					} else if("IMSI".equals(annName)) {  //SIM卡序號
//						errList.addAll(ValidUtils.getInstance().validImsi(annName, this.tstImsi));
//					} else if("SUBSCR_ID".equals(annName)) {  //TWM用戶識別碼
//						if(!StringUtils.isBlank(this.twmContractId)) {
//							if(CommonUtils.getInstance().isNaN(this.twmContractId)) {
//								errList.add(new CustException(CustErrorCode.ERROR_90007, annName));
//							}
//						}
//					} else if("TST_CONTRACT_ID".equals(annName)) {  //TST用戶識別碼
//						errList.addAll(ValidUtils.getInstance().validContractId(annName, this.tstContractId));
//					} else if("SVC_ITEM".equals(annName)) {  //服務項目代碼
//						//do nothing
//					} else if("ACTION_CDE".equals(annName)) {  //開通命令類別 (A/D)
//						if(!StringUtils.isBlank(this.actionCode)) {
//							if(",A,D,".indexOf(","+this.actionCode+",")==-1) {
//								errList.add(new CustException(CustErrorCode.ERROR_90008, annName, this.actionCode));
//							}
//						}
//					} else if("TST_PRODUCT_ID".equals(annName)) {  //TST產品代碼
//						if(!StringUtils.isBlank(this.productId)) {
//							if(CommonUtils.getInstance().isNaN(this.productId)) {
//								errList.add(new CustException(CustErrorCode.ERROR_90007, annName));
//							} else if(CPCSvcItem.findByProductId(this.productId)==null) {
//								errList.add(new CustException(CustErrorCode.ERROR_90008, annName, this.productId));
//							}
//						}
//					} else if("TST_SEND_QUEUE".equals(annName)) {  //是否送往Queue進行申裝異動(Y/N)
//						if(!StringUtils.isBlank(this.sendQueue)) {
//							if(",Y,N,".indexOf(","+this.sendQueue+",")==-1) {
//								errList.add(new CustException(CustErrorCode.ERROR_90008, annName, this.sendQueue));
//							}
//						}
//					} else if("BILLING_CYCLE".equals(annName)) {  //TWM帳週
//						if(!StringUtils.isBlank(this.twmBC)) {
//							errList.addAll(ValidUtils.getInstance().validBC(annName, this.twmBC));
//						}
//					}
				}
			}
			errList.addAll(this.validDetailParameters(names));
		} catch(Throwable e) {
			e.printStackTrace();
			errList.add(new CustException(e));
		}
		return errList;
	}
	
	@Override
	public String toString() {
		return this.toString(0, false);
	}
	
	public String toString(int index, boolean hasNext) {
		String className = this.getClass().getSimpleName();
		String str = className + "["+index+"].masterId == " + this.masterId + "\r\n"
				   + className + "["+index+"].orderId == " + this.orderId + "\r\n"
				   + className + "["+index+"].cpcSequence == " + this.cpcSequence + "\r\n"
				   + className + "["+index+"].areaCode == " + this.areaCode + "\r\n"
				   + className + "["+index+"].phoneNumber == " + this.phoneNumber + "\r\n"
				   + className + "["+index+"].tstImsi == " + this.tstImsi + "\r\n"
				   + className + "["+index+"].twmContractId == " + this.twmContractId + "\r\n"
				   + className + "["+index+"].tstContractId == " + this.tstContractId + "\r\n"
				   + className + "["+index+"].serviceItem == " + this.serviceItem + "\r\n"
				   + className + "["+index+"].actionCode == " + this.actionCode + "\r\n"
				   + className + "["+index+"].productId == " + this.productId + "\r\n"
				   + className + "["+index+"].sendQueue == " + this.sendQueue + "\r\n"
				   + className + "["+index+"].twmBC == " + this.twmBC + "\r\n"
				   + className + "["+index+"].uppRequestSeq == " + this.uppRequestSeq + "\r\n"
				   + className + "["+index+"].uppStatus == " + this.uppStatus + "\r\n"
				   + className + "["+index+"].uppCompleteDate == " + this.uppCompleteDate + "\r\n"
				   + className + "["+index+"].uppResultCode == " + this.uppResultCode + "\r\n"
				   + className + "["+index+"].uppResultMsg == " + this.uppResultMsg + "\r\n"
				   + className + "["+index+"].note == " + this.note + "\r\n"
				   + this.toOrderDetailsString(index, hasNext)
				   ;
		return str;
	}
	
	public String toOrderDetailsString(int index, boolean hasNext) {
		if(this.orderDetails==null || this.orderDetails.size()==0) {
			return "";
		}
		String className = this.getClass().getSimpleName();
		String str = className + "["+index+"].orderDetails == [\r\n";
		for(int i=0; i<this.orderDetails.size(); i++) {
			PvsReqOrderDetail detail = this.orderDetails.get(i);
			str += ((detail!=null)?detail.toString(i, i<this.orderDetails.size()-1):"");
		}
		return str + "]" + ((hasNext)?",":"") + "\r\n";
	}
	
	public boolean resetUppResult(Map<String,Object> dataMap) throws CustException {
		try {
			if(dataMap!=null && dataMap.size()>0) {
				this.uppRequestSeq = (String) dataMap.get("ORDER_ID");
				this.uppCompleteDate = (String) dataMap.get("UPDATE_DATE");
				String status = (String) dataMap.get("STATUS");
				String errCode = (String) dataMap.get("ERROR_CODE");
				String errMsg = (String) dataMap.get("ERROR_MSG");
				boolean isByPass = "Y".equals((String)dataMap.get("IS_BYPASS"));
				boolean isErr = false;
				try {
					isErr = "F".equals(status) || (!StringUtils.isBlank(errCode) && Integer.parseInt(errCode)>0);
				} catch(Throwable ee) {
					isErr = true;
				}
				this.uppResultCode = ((isErr)?errCode:CustErrorCode.ERROR_00000.getCode());
				this.uppResultMsg = ((isErr)?errMsg:CustErrorCode.ERROR_00000.getMsg()+((isByPass)?"(ByPass)":""));
				if("F".equals(status)) {
					this.uppStatus = "X";
					return false;
				} else if(",C,X,".indexOf(","+status+",")!=-1) {
					this.uppStatus = "S";
				} else if(",O,W,P,".indexOf(","+status+",")!=-1) {  //Flow、Queue's hotfix retrying
					this.uppStatus = "X";
					this.note = "hotfix retrying:" + status;
					return false;
				} else if(",S,Z,".indexOf(","+status+",")!=-1) {  //TODO for test 
					this.uppStatus = "S";
				} else {
					this.uppStatus = status;
					return false;
				}
			}
		} catch(Throwable e) {
			e.printStackTrace();
			throw new CustException(e);
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.cpcSequence==null)?0:this.cpcSequence.hashCode());
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
		PvsReqOrderMaster other = (PvsReqOrderMaster) obj;
		if((this.cpcSequence==null && other.cpcSequence!=null) 
				|| !this.cpcSequence.equals(other.cpcSequence)) {
			return false;
		}
		return true;
	}
	
	public String getCpcSequence() {
		return cpcSequence;
	}

	public void setCpcSequence(String cpcSequence) {
		this.cpcSequence = cpcSequence;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getTstImsi() {
		return tstImsi;
	}

	public void setTstImsi(String tstImsi) {
		this.tstImsi = tstImsi;
	}

	public String getTwmContractId() {
		return twmContractId;
	}

	public void setTwmContractId(String twmContractId) {
		this.twmContractId = twmContractId;
	}

	public String getTstContractId() {
		return tstContractId;
	}

	public void setTstContractId(String tstContractId) {
		this.tstContractId = tstContractId;
	}

	public String getServiceItem() {
		return serviceItem;
	}

	public void setServiceItem(String serviceItem) {
		this.serviceItem = serviceItem;
	}

	public String getActionCode() {
		return actionCode;
	}

	public void setActionCode(String actionCode) {
		this.actionCode = actionCode;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getSendQueue() {
		return sendQueue;
	}

	public void setSendQueue(String sendQueue) {
		this.sendQueue = sendQueue;
	}

	public String getTwmBC() {
		return twmBC;
	}

	public void setTwmBC(String twmBC) {
		this.twmBC = twmBC;
	}

	public List<PvsReqOrderDetail> getOrderDetails() {
		return orderDetails;
	}

	public void setOrderDetails(List<PvsReqOrderDetail> orderDetails) {
		this.orderDetails = orderDetails;
	}
	
	public void addOrderDetails(List<PvsReqOrderDetail> orderDetails) {
		if(orderDetails==null || orderDetails.size()==0) {
			return;
		}
		if(this.orderDetails==null) {
			this.orderDetails = new ArrayList<PvsReqOrderDetail>();
		}
		for(PvsReqOrderDetail orderDetail : orderDetails) {
			if(orderDetail==null) {
				continue;
			}
			this.orderDetails.add(orderDetail);
		}
	}
	
	public void addOrderDetails(PvsReqOrderDetail orderDetail) {
		if(orderDetail==null) {
			return;
		}
		if(this.orderDetails==null) {
			this.orderDetails = new ArrayList<PvsReqOrderDetail>();
		}
		this.orderDetails.add(orderDetail);
	}

	public String getMasterId() {
		return masterId;
	}

	public void setMasterId(String masterId) {
		this.masterId = masterId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getUppRequestSeq() {
		return uppRequestSeq;
	}

	public void setUppRequestSeq(String uppRequestSeq) {
		this.uppRequestSeq = uppRequestSeq;
	}

	public String getUppStatus() {
		return uppStatus;
	}

	public void setUppStatus(String uppStatus) {
		this.uppStatus = uppStatus;
	}

	public String getUppCompleteDate() {
		return uppCompleteDate;
	}

	public void setUppCompleteDate(String uppCompleteDate) {
		this.uppCompleteDate = uppCompleteDate;
	}

	public String getUppResultCode() {
		return uppResultCode;
	}

	public void setUppResultCode(String uppResultCode) {
		this.uppResultCode = uppResultCode;
	}

	public String getUppResultMsg() {
		return uppResultMsg;
	}

	public void setUppResultMsg(String uppResultMsg) {
		this.uppResultMsg = uppResultMsg;
	}
	
}

