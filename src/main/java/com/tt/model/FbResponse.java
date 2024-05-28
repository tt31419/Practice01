package com.tt.model;

import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;

import com.tt.CustException;
import com.tt.enums.CustErrorCode;
import com.tt.model.request.PvsReqOrderMaster;
import com.tt.util.CommonUtils;

@XmlType(name="FB_RESP", propOrder={
	"cpcWorkId",
	"uppProcessSeq",
	"processDate",
	"resultCode",
	"resultMsg"
})
public class FbResponse {

	@XmlTransient
    private String oriJSON = null;

	@XmlTransient
    private String orderId = null;	
	
	@XmlTransient
    private String responseType = null;	
	
	@XmlTransient
    private String type = "FB_RESP";	//insert UPP_GW_ORDER_JSON
	
	@XmlElement(name="WORK_ORDER_NBR")
	private String cpcWorkId = null;  //WORK_ORDER_NBR 異動聯單號碼
	
	@XmlElement(name="TST_PROCESS_NBR")
	private String uppProcessSeq = null;  //TST_PROCESS_NBR TST處理的單號
	
	@XmlElement(name="PROCESS_DATE")
	private String processDate = null;  //PROCESS_DATE 處理時間(YYYY/MM/DD HH24:MI:SS)
	
	@XmlElement(name="RESULT_CODE")
	private String resultCode = null;  //RESULT_CODE 處理結果代碼
	
	@XmlElement(name="RESULT_MSG")
	private String resultMsg = null;  //RESULT_MSG 處理訊息內容

	@XmlTransient
    private String note = null;
	
	public FbResponse() {
	}
	
	public static Map<String,String> popFieldMap() throws CustException {
		try {
			Map<String,String> popMap = CommonUtils.getInstance().popFieldMap(FbResponse.class);
			if(popMap==null || popMap.size()==0) {
				throw new CustException(CustErrorCode.ERROR_90003, "popMap");
			}
			popMap.putAll(PvsReqOrderMaster.popFieldMap());
			return popMap;
		} catch(CustException e) {
			e.printStackTrace();
			throw e;
		} catch(Throwable e) {
			e.printStackTrace();
			throw new CustException(e);
		}
	}
	
	public static FbResponse jsonToObj(String json) throws CustException {
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
			FbResponse fbResponse = (FbResponse) CommonUtils.getInstance().jsonToObj(json, FbResponse.class);
			if(fbResponse!=null) {
				fbResponse.setOriJSON(oriJson);
			}
			return fbResponse;
		} catch(CustException e) {
			e.printStackTrace();
			throw e;
		} catch(Throwable e) {
			e.printStackTrace();
			throw new CustException(e);
		} 
	}
	
	public String toJSONStr() {
		return CommonUtils.getInstance().objToJSON(this);
	}
	
	@Override
	public String toString() {
		return this.toString(0);
	}
	
	public String toString(int index) {
		String className = this.getClass().getSimpleName();
		String str = className + "["+index+"].cpcWorkId == " + this.cpcWorkId + "\r\n"
				   + className + "["+index+"].uppProcessSeq == " + this.uppProcessSeq + "\r\n"
				   + className + "["+index+"].processDate == " + this.processDate + "\r\n"
				   + className + "["+index+"].resultCode == " + this.resultCode + "\r\n"
				   + className + "["+index+"].resultMsg == " + this.resultMsg + "\r\n"
				   ;
		return str;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.cpcWorkId==null)?0:this.cpcWorkId.hashCode());
		result = prime * result + ((this.uppProcessSeq==null)?0:this.uppProcessSeq.hashCode());
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
		FbResponse other = (FbResponse) obj;
		if((this.cpcWorkId==null && other.cpcWorkId!=null) 
				|| !this.cpcWorkId.equals(other.cpcWorkId) 
				|| (this.uppProcessSeq==null && other.uppProcessSeq!=null) 
				|| !this.uppProcessSeq.equals(other.uppProcessSeq)) {
			return false;
		}
		return true;
	}

	public String getCpcWorkId() {
		return cpcWorkId;
	}

	public void setCpcWorkId(String cpcWorkId) {
		this.cpcWorkId = cpcWorkId;
	}

	public String getUppProcessSeq() {
		return uppProcessSeq;
	}

	public void setUppProcessSeq(String uppProcessSeq) {
		this.uppProcessSeq = uppProcessSeq;
	}
	
	public String getProcessDate() {
		return processDate;
	}

	public void setProcessDate(String processDate) {
		this.processDate = processDate;
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
	
	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getResponseType() {
		return responseType;
	}

	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	public String getType() {
		return type;
	}

	public String getOriJSON() {
		return oriJSON;
	}

	public void setOriJSON(String oriJSON) {
		this.oriJSON = oriJSON;
	}

}

