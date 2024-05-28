package com.tt.model.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;

import com.tt.CustException;
import com.tt.enums.CustErrorCode;
import com.tt.util.CommonUtils;

@XmlType(name="PROV_ORDER_DETAIL", propOrder={
	"paramName",
	"paramValue"
})
public class PvsReqOrderDetail {
	
	@XmlTransient
    private String detailId = null;
	
	@XmlTransient
    private String masterId = null;
	
	@XmlElement(name="PARAM_NAME", required=true)
	private String paramName = null;  //PARAM_NAME
	
	@XmlElement(name="PARAM_VALUE", required=true)
	private String paramValue = null;  //PARAM_VALUE
	
	@XmlTransient
    private String note = null;
	
	public PvsReqOrderDetail() {
	}
		
	public PvsReqOrderDetail(Map<String,Object> dataMap) {
		this();
		if(dataMap!=null && dataMap.size()>0) {
			this.detailId = (String) dataMap.get("DETAIL_ID");
			this.masterId = (String) dataMap.get("MASTER_ID");
			this.paramName = (String) dataMap.get("PARAM_NAME");
			this.paramValue = (String) dataMap.get("PARAM_VALUE");
			this.note = (String) dataMap.get("NOTE");
		}
	}
	
	public static Map<String,String> popFieldMap() throws CustException {
		return CommonUtils.getInstance().popFieldMap(PvsReqOrderDetail.class);
	}
	
	public List<CustException> validParameters(PvsReqOrderMaster master, String names) {
		List<CustException> errList = new ArrayList<CustException>();
		try {
			//names >> order:REQ_USER,USER_PWD;master:IMSI,SUBSCR_ID;detail:ORDER_TYPE,CRM_ID
//			String validNames = ValidUtils.getInstance().findByPassNames("detail", names);
			if(StringUtils.isBlank(this.paramName)) {
				errList.add(new CustException(CustErrorCode.ERROR_90000, "paramName"));
			}
			//doesn't valid empty paramValue because there have non-necessary parameter
//			if((","+validNames+",").indexOf(","+this.paramName+",")==-1
//					&& StringUtils.isBlank(this.paramValue)) {
//				errList.add(new CPCException(CPCErrorCode.ERROR_90000, this.paramName));
//			}
//			if(master!=null) {
//				if(master.isDpiProduct()) {
//					if("TET_CONTROL_TYPE".equals(this.paramName)) {  //for UCS asked to valid
//						if(",none,TETHERING_ON,TETHERING_OFF,".indexOf(","+this.paramValue+",")==-1) {
//							errList.add(new CustException(CustErrorCode.ERROR_90008, this.paramName, this.paramValue));
//						}
//					}
//				}
//				if(master.isPcrfProduct() 
//						|| master.isSpsProduct()) {
//					if("TARRIF_ID".equals(this.paramName)) {  //for UCS asked to valid
//						if(!StringUtils.isBlank(this.paramValue)) {
//							errList.addAll(ValidUtils.getInstance().validTarrifId(this.paramValue));
//						}
//					}
//				}
//			}
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
		String str = className + "["+index+"].detailId == " + this.detailId + "\r\n"
				   + className + "["+index+"].masterId == " + this.masterId + "\r\n"
				   + className + "["+index+"].paramName == " + this.paramName + "\r\n"
				   + className + "["+index+"].paramValue == " + this.paramValue + "\r\n"
				   + className + "["+index+"].note == " + this.note + ((hasNext)?",":"") + "\r\n"
				   ;
		return str;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.paramName==null)?0:this.paramName.hashCode());
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
		PvsReqOrderDetail other = (PvsReqOrderDetail) obj;
		if((this.paramName==null && other.paramName!=null) 
				|| !this.paramName.equals(other.paramName)) {
			return false;
		}
		return true;
	}
	
	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

	public String getDetailId() {
		return detailId;
	}

	public void setDetailId(String detailId) {
		this.detailId = detailId;
	}

	public String getMasterId() {
		return masterId;
	}

	public void setMasterId(String masterId) {
		this.masterId = masterId;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
}

