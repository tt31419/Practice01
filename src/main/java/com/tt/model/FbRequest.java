package com.tt.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;

import com.tt.CustException;
import com.tt.enums.CustErrorCode;
import com.tt.model.request.FbReqOrderMaster;
import com.tt.util.CommonUtils;

/**
 * For POST / tstprovisionresult
 */
@XmlType(name="FB_REQ", propOrder={
	"reqUser",
	"reqPwd",
	"cpcWorkId",
	"uppProcessSeq",
	"orderMasters"
})
public class FbRequest {

	@XmlTransient
    private String oriJSON = null;
	
	@XmlTransient
    private String orderId = null;  

	@XmlTransient
    private String type = "FB_REQ";	//insert UPP_GW_ORDER_JSON
	
	@XmlElement(name="REQ_USER")
	private String reqUser = null;  //REQ_USER 登入帳號(以 Base64 加密)
	
	@XmlElement(name="USER_PWD")
	private String reqPwd = null;  //USER_PWD 登入密碼(以 Base64 加密)
	
	@XmlElement(name="WORK_ORDER_NBR")
	private String cpcWorkId = null;  //WORK_ORDER_NBR 異動聯單號碼

	@XmlElement(name="TST_PROCESS_NBR")
	private String uppProcessSeq = null;  //TST_PROCESS_NBR TST處理的單號
	
	@XmlTransient
    private String note = null;
	
	@XmlElement(name="TST_PROV_ORDER_MASTER")
	private List<FbReqOrderMaster> orderMasters = null;  //TST_PROV_ORDER_MASTER
	
	public FbRequest() {
	}

	public FbRequest(Map<String,Object> dataMap) {
		this();
		if(dataMap!=null && dataMap.size()>0) {  //for GWOrder
			this.orderId = (String) dataMap.get("ORDER_ID");
			this.reqUser = (String) dataMap.get("REQ_USER");
			this.reqPwd = (String) dataMap.get("USER_PWD");
			this.cpcWorkId = (String) dataMap.get("CPC_WORK_ID");
			this.uppProcessSeq = (String) dataMap.get("UPP_PROCESS_SEQ");
		}
	}

	public String toJSONStr() {
		return CommonUtils.getInstance().objToJSON(this);
	}
	
	public Map<String,String> popFieldMap() throws CustException {
		try {
			Map<String,String> popMap = CommonUtils.getInstance().popFieldMap(FbRequest.class);
			if(popMap==null || popMap.size()==0) {
				throw new CustException(CustErrorCode.ERROR_90003, "popMap");
			}
			popMap.putAll(FbReqOrderMaster.popFieldMap());
			popMap.put("TST_PROV_ORDER_MASTER", "orderMasters");
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

	@Override
	public String toString() {
		return this.toString(0);
	}
	
	public String toString(int index) {
		String className = this.getClass().getSimpleName();
		String str = className + "["+index+"].orderId == " + this.orderId + "\r\n"
				   + className + "["+index+"].reqUser == " + this.reqUser + "\r\n"
				   + className + "["+index+"].reqPwd == " + this.reqPwd + "\r\n"
				   + className + "["+index+"].cpcWorkId == " + this.cpcWorkId + "\r\n"
				   + className + "["+index+"].uppProcessSeq == " + this.uppProcessSeq + "\r\n"
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
			FbReqOrderMaster master = this.orderMasters.get(i);
			str += ((master!=null)?master.toString(i, i<this.orderMasters.size()-1):"");
		}
		return str + "]\r\n";
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
		FbRequest other = (FbRequest) obj;
		if((this.cpcWorkId==null && other.cpcWorkId!=null) 
				|| !this.cpcWorkId.equals(other.cpcWorkId) 
				|| (this.uppProcessSeq==null && other.uppProcessSeq!=null) 
				|| !this.uppProcessSeq.equals(other.uppProcessSeq)) {
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

	public List<FbReqOrderMaster> getOrderMasters() {
		return orderMasters;
	}

	public void setOrderMasters(List<FbReqOrderMaster> orderMasters) {
		this.orderMasters = orderMasters;
	}
	
	public void addOrderMasters(List<FbReqOrderMaster> orderMasters) {
		if(orderMasters==null || orderMasters.size()==0) {
			return;
		}
		if(this.orderMasters==null) {
			this.orderMasters = new ArrayList<FbReqOrderMaster>();
		}
		for(FbReqOrderMaster orderMaster : orderMasters) {
			if(orderMaster==null) {
				continue;
			}
			this.orderMasters.add(orderMaster);
		}
	}
	
	public void addOrderMasters(FbReqOrderMaster orderMaster) {
		if(orderMaster==null) {
			return;
		}
		if(this.orderMasters==null) {
			this.orderMasters = new ArrayList<FbReqOrderMaster>();
		}
		this.orderMasters.add(orderMaster);
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

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getType() {
		return type;
	}
	
}

