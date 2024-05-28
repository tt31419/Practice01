package com.tt.model.request;

import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.tt.CustException;
import com.tt.enums.CustErrorCode;
import com.tt.util.CommonUtils;

@XmlType(name="TST_PROV_ORDER_MASTER", propOrder={
	"cpcSequence",
	"areaCode",
	"phoneNumber",
	"tstImsi",
	"twmContractId",
	"tstContractId",
	"serviceItem",
	"actionCode",
	"productId",
	"uppRequestSeq",
	"status",
	"completeDate",
	"resultCode",
	"resultMsg"
	
})
public class FbReqOrderMaster {
	
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
	
	@XmlElement(name="TST_REQ_SEQ", required=true)
    private String uppRequestSeq = null;  //TST_REQ_SEQ TST指令執行順序(UPP_BATCH_NE_REQUEST_ALL.REQEUST_SEQ)
	
	@XmlElement(name="STATUS", required=true)
    private String status = null;  //STATUS 開通結果 ( S=成功, X=失敗 )
	
	@XmlElement(name="COMPLETE_DATE", required=true)
	private String completeDate = null;  //COMPLETE_DATE 開通完成日期(YYYY/MM/DD HH24:MI:SS)
	
	@XmlElement(name="RESULT_CODE", required=true)
	private String resultCode = null;  //RESULT_CODE 處理結果代碼
	
	@XmlElement(name="RESULT_MSG", required=true)
	private String resultMsg = null;  //RESULT_MSG 處理訊息內容
	
	@XmlTransient
    private String note = null;
	
	public FbReqOrderMaster() {
	}
	
	public FbReqOrderMaster(Map<String,Object> dataMap) {
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
			this.uppRequestSeq = (String) dataMap.get("UPP_REQUEST_SEQ");
			this.status = (String) dataMap.get("STATUS");
			this.completeDate = (String) dataMap.get("COMPLETE_DATE");
			this.resultCode = (String) dataMap.get("RESULT_CODE");
			this.resultMsg = (String) dataMap.get("RESULT_MSG");
			this.note = (String) dataMap.get("NOTE");
		}
	}
	
	public static Map<String,String> popFieldMap() throws CustException {
		try {
			Map<String,String> popMap = CommonUtils.getInstance().popFieldMap(FbReqOrderMaster.class);
			if(popMap==null || popMap.size()==0) {
				throw new CustException(CustErrorCode.ERROR_90003, "popMap");
			}
			return popMap;
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
				   + className + "["+index+"].uppRequestSeq == " + this.uppRequestSeq + "\r\n"
				   + className + "["+index+"].status == " + this.status + "\r\n"
				   + className + "["+index+"].completeDate == " + this.completeDate + "\r\n"
				   + className + "["+index+"].resultCode == " + this.resultCode + "\r\n"
				   + className + "["+index+"].resultMsg == " + this.resultMsg + "\r\n"
				   + className + "["+index+"].note == " + this.note + ((hasNext)?",":"") + "\r\n"
				   ;
		return str;
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
		FbReqOrderMaster other = (FbReqOrderMaster) obj;
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

	public String getUppRequestSeq() {
		return uppRequestSeq;
	}

	public void setUppRequestSeq(String uppRequestSeq) {
		this.uppRequestSeq = uppRequestSeq;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCompleteDate() {
		return completeDate;
	}

	public void setCompleteDate(String completeDate) {
		this.completeDate = completeDate;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultMsg() {
		return resultMsg;
	}

	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
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

	public String getTstImsi() {
		return tstImsi;
	}

	public void setTstImsi(String tstImsi) {
		this.tstImsi = tstImsi;
	}
	
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
}

