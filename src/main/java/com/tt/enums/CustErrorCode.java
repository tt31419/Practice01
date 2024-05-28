package com.tt.enums;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public enum CustErrorCode {
	
	ERROR_00000("00000", "Success", "Success"),
	
	ERROR_80000("80000", "By Pass $value$ HLR Cancel Location !!", "ByPass"),
	ERROR_80001("80001", "By Pass for $value$ !!", "ByPass"),
	
	ERROR_90000("90000", "Empty $param$ !!", "Fail"),
	ERROR_90001("90001", "$param$ must less than $value$ !!", "Fail"),
	ERROR_90002("90002", "Invalid $param$ format: $value$ !!", "Fail"),
	ERROR_90003("90003", "$param$ is null !!", "Fail"),
	ERROR_90004("90004", "Invalid $param$ !!", "Fail"),
	ERROR_90005("90005", "$param$ after $value$ !!", "Fail"),
	ERROR_90006("90006", "$param$ the same as $value$ !!", "Fail"),
	ERROR_90007("90007", "$param$ is not a number !!", "Fail"),
	ERROR_90008("90008", "Invalid $param$: $value$ !!", "Fail"),
	ERROR_90009("90009", "$param$: $value$", "Fail"),
	ERROR_90010("90010", "$param$ less than $value$ !!", "Fail"),
	ERROR_90011("90011", "Invalid $param$ length: $value$ !!", "Fail"),
	ERROR_90012("90012", "Only $param$ for 'Y' or 'N' !!", "Fail"),
	ERROR_90013("90013", "Can't find $param$ !!", "Fail"),
	ERROR_90014("90014", "$param$ have the same value: $value$ !!", "Fail"),
	ERROR_90015("90015", "Valid WorkType、CallType Error !!", "Fail"),
	ERROR_90016("90016", "Missing $param$ !!", "Fail"),
	ERROR_90017("90017", "UPP result Fail !!", "Fail"),  //for HotfixQueue retry
	
	ERROR_99995("99995", "Total $param$ Errors: [$value$]", "Fail"),
	ERROR_99996("99996", "Unknown SrcSystem: $value$", "Fail"),
	ERROR_99997("99997", "Undefind Error Code: $value$", "Fail"),
	ERROR_99998("99998", "Unknown Action: $value$", "Fail"),
	ERROR_99999("99999", "Unknown Exception: $value$", "Fail"),
	
	;
	
	private String code = null;
	
	private String msg = null;
	
	private String group = null;
	
	private CustErrorCode(String code, String msg,  String group) {
		this.code = code;
		this.msg = msg;
		this.group = group;
	}
	
	public static String replaceException(Throwable e) {
		return CustErrorCode.ERROR_99999.replaceParameter(null, ((e==null)?"":e.getMessage()));
	}
	
	public static String replaceParameter(CustErrorCode code, String key, String value) {
		if(code==null) {
			return ERROR_90003.replaceParameter("code", null);
		}
		return replaceParameter(code.getCode(), key, value);
	}
	
	public String replaceParameter(String key, String value) {
		 return this.msg.replace("$param$", ((!StringUtils.isBlank(key))?key:""))
				 		.replace("$value$", ((!StringUtils.isBlank(value))?value:""))
				 		;
	}
	
	public static String replaceParameter(String code, String key, String value) {
		CustErrorCode spsCode = findByCode(code);
		if(spsCode==null) {
			return ERROR_99997.replaceParameter(null, code);
		}
		return spsCode.replaceParameter(key, value);
	}
	
	public static String findMsgByCode(String code) {
		CustErrorCode spsCode = findByCode(code);
		return ((spsCode!=null)?spsCode.getMsg():null);
	}
	
	public static CustErrorCode findByCode(String code) {
		if(StringUtils.isBlank(code)) {
			return null;
		}
		CustErrorCode[] codes = CustErrorCode.values();
		if(codes!=null && codes.length>0) {
			for(CustErrorCode spsCode : codes) {
				if(spsCode==null || StringUtils.isBlank(spsCode.getCode())) {
					continue;
				}
				if(StringUtils.equals(code, spsCode.getCode())) {
					return spsCode;
				}
			}
		}
		return null;
	}
	
	public static List<CustErrorCode> findByGroup(String group) {
		if(StringUtils.isBlank(group)) {
			return null;
		}
		List<CustErrorCode> rtnList = new ArrayList<CustErrorCode>();
		CustErrorCode[] codes = CustErrorCode.values();
		if(codes!=null && codes.length>0) {
			for(CustErrorCode spsCode : codes) {
				if(spsCode==null || StringUtils.isBlank(spsCode.getGroup())) {
					continue;
				}
				if(StringUtils.equals(group, spsCode.getGroup())) {
					rtnList.add(spsCode);
				}
			}
		}
		return ((rtnList!=null && rtnList.size()>0)?rtnList:null);
	}
	
	
	public static boolean isByPass(String code) {
		if(StringUtils.isBlank(code)) {
			return false;
		}
		List<CustErrorCode> codes = findByGroup("ByPass");
		if(codes!=null && codes.size()>0) {
			for(CustErrorCode spsCode : codes) {
				if(spsCode==null || StringUtils.equals(code, spsCode.getCode())) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean isSuccess(String code) {
		if(StringUtils.isBlank(code)) {
			return false;
		}
		List<CustErrorCode> codes = findByGroup("Success");
		if(codes!=null && codes.size()>0) {
			for(CustErrorCode spsCode : codes) {
				if(spsCode==null || StringUtils.equals(code, spsCode.getCode())) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean isFail(String code) {
		if(StringUtils.isBlank(code)) {
			return false;
		}
		List<CustErrorCode> codes = findByGroup("Fail");
		if(codes!=null && codes.size()>0) {
			for(CustErrorCode spsCode : codes) {
				if(spsCode==null || StringUtils.equals(code, spsCode.getCode())) {
					return true;
				}
			}
		}
		return false;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

}

