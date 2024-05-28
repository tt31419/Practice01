package com.tt.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;

import com.tt.CustException;
import com.tt.enums.CustErrorCode;
import com.tt.util.CommonUtils;

@XmlType(name="CPC_RESP", propOrder={
	"cpcWorkId",
	"uppProcessSeq",
	"processDate",
	"resultCode",
	"resultMsg"
})
public class PvsResponse {

	@XmlTransient
    private String oriJSON = null;

	@XmlTransient
    private String orderId = null;	
	
	@XmlTransient
    private String type = "PVS_RESP";	//insert UPP_GW_ORDER_JSON
	
	@XmlTransient
    private String responseType = null;	//EAI/NE/SPS/PCRF/FAIL  CPC_RESPONSE.RESPONSE_TYPE
	
	@XmlElement(name="WORK_ORDER_NBR")
	private String cpcWorkId = null;  //WORK_ORDER_NBR 異動聯單號碼
	
	@XmlElement(name="TST_PROCESS_NBR")
	private String uppProcessSeq = null;  //TST_PROCESS_NBR TST處理的單號 responseType+uppWorkSeq
	
	@XmlTransient
    private String uppWorkSeq = null;
	
	@XmlElement(name="PROCESS_DATE")
	private String processDate = null;  //PROCESS_DATE 處理時間(YYYY/MM/DD HH24:MI:SS)
	
	@XmlElement(name="RESULT_CODE")
	private String resultCode = null;  //RESULT_CODE 處理結果代碼
	
	@XmlElement(name="RESULT_MSG")
	private String resultMsg = null;  //RESULT_MSG 處理訊息內容
	
	@XmlTransient
    private String note = null;
	
	public PvsResponse() {
	}
	
	public PvsResponse(String orderId, PvsRequest pvs) {
		this(orderId, pvs, null);
	}
	
	public PvsResponse(Throwable e) {
		this();
		if(e!=null) {
			e.printStackTrace();
			if(e instanceof CustException) {
				this.resultCode = ((CustException)e).getExceptionCode();
				this.resultMsg = ((CustException)e).getExceptionMsg();
			} else {
				this.resultCode = CustErrorCode.ERROR_99999.getCode();
				this.resultMsg = CustErrorCode.replaceException(e);
			}
		} else {
			this.resultCode = CustErrorCode.ERROR_00000.getCode();
			this.resultMsg = CustErrorCode.ERROR_00000.getMsg();
		}
	}
	
	public PvsResponse(String orderId, PvsRequest pvs, Throwable e) {
		this(e);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date processDate = new Date();
		if(pvs!=null) {
			this.orderId = pvs.getOrderId();
			this.cpcWorkId = pvs.getCpcWorkId();
			this.uppProcessSeq = (StringUtils.isBlank(pvs.getUppWorkSeq())?this.orderId:pvs.getUppWorkSystem()+"_"+pvs.getUppWorkSeq());
			this.uppWorkSeq = (StringUtils.isBlank(pvs.getUppWorkSeq())?this.orderId:pvs.getUppWorkSeq());
			this.responseType = pvs.getUppWorkSystem();
		} else if(!StringUtils.isBlank(orderId)) {  //for json parse fail and PvsRequest init fail
			this.orderId = orderId;
			this.cpcWorkId = orderId;
			this.uppProcessSeq = orderId;
			this.uppWorkSeq = orderId;
			this.responseType = "Fail";
		} else {
			//fail orderId
			this.orderId = sdf.format(processDate);
			this.cpcWorkId = sdf.format(processDate);
			this.uppProcessSeq = sdf.format(processDate);
			this.uppWorkSeq = sdf.format(processDate);
			this.responseType = "Fail";
		}
		this.processDate = sdf.format(processDate);
	}
	
	public PvsResponse(Map<String,Object> dataMap) {
		this();
		if(dataMap!=null && dataMap.size()>0) {
			this.orderId = (String) dataMap.get("ORDER_ID");
			this.cpcWorkId = (String) dataMap.get("CPC_WORK_ID");
			this.uppProcessSeq = (String) dataMap.get("UPP_PROCESS_SEQ");
			this.uppWorkSeq = (String) dataMap.get("UPP_WORK_SEQ");
			this.processDate = (String) dataMap.get("PROCESS_DATE");
			this.resultCode = (String) dataMap.get("RESULT_CODE");
			this.resultMsg = (String) dataMap.get("RESULT_MSG");
		}
	}

	public String toJSONStr() {
		return CommonUtils.getInstance().objToJSON(this);
	}
	
	public String toJSONStr(int indentFactor) {
		return CommonUtils.getInstance().objToJSON(this, indentFactor);
	}
	
	public Map<String,Object> populateMap() {
		return CommonUtils.getInstance().populateMap(this);
	}
	
	public Map<String,String> popFieldMap() throws CustException {
		try {
			Map<String,String> popMap = CommonUtils.getInstance().popFieldMap(PvsResponse.class);
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
	
	public String toAnnJsonStr() throws CustException {
		try {
			String json = this.toJSONStr();
			if(StringUtils.isBlank(json)) {
				throw new CustException(CustErrorCode.ERROR_90000, "json");
			}
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
					json = json.replaceAll("\""+value+"\"", "\""+key+"\"");
				}
			}
			return json;
		} catch(CustException e) {
			e.printStackTrace();
			throw e;
		} catch(Throwable e) {
			e.printStackTrace();
			throw new CustException(e);
		}
	}
	
	public void resetOriJSON() throws CustException {
		this.resetOriJSON(null);
	}
	
	public void resetOriJSON(String json) throws CustException {
		this.oriJSON = ((StringUtils.isBlank(json))?this.toAnnJsonStr():json);
	}
	
	@Override
	public String toString() {
		return this.toString(0);
	}
	
	public String toString(int index) {
		String className = this.getClass().getSimpleName();
		String str = className + "["+index+"].cpcWorkId == " + this.cpcWorkId + "\r\n"
				   + className + "["+index+"].uppWorkSeq == " + this.uppWorkSeq + "\r\n"
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
		result = prime * result + ((this.uppWorkSeq==null)?0:this.uppWorkSeq.hashCode());
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
		PvsResponse other = (PvsResponse) obj;
		if((this.cpcWorkId==null && other.cpcWorkId!=null) 
				|| !this.cpcWorkId.equals(other.cpcWorkId) 
				|| (this.uppWorkSeq==null && other.uppWorkSeq!=null) 
				|| !this.uppWorkSeq.equals(other.uppWorkSeq)) {
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

	public String getUppWorkSeq() {
		return uppWorkSeq;
	}

	public void setUppWorkSeq(String uppWorkSeq) {
		this.uppWorkSeq = uppWorkSeq;
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

	public String getType() {
		return type;
	}

	public String getResponseType() {
		return responseType;
	}

	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}

	public String getUppProcessSeq() {
		return uppProcessSeq;
	}

	public void setUppProcessSeq(String uppProcessSeq) {
		this.uppProcessSeq = uppProcessSeq;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getOriJSON() {
		return oriJSON;
	}

	public void setOriJSON(String oriJSON) {
		this.oriJSON = oriJSON;
	}
	
}

