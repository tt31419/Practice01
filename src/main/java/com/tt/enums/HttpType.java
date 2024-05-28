package com.tt.enums;

import org.apache.commons.lang.StringUtils;

public enum HttpType {
	
	HTTP_GET("GET", ""),
	HTTP_POST("POST", ""),
	HTTP_DELETE("DELETE", ""),
	HTTP_PUT("PUT", ""),
	;
	
	private String type = null;
	
	private String desc = null;
	
	private HttpType(String type, String desc) {
		this.type = type;
		this.desc = desc;
	}
	
	public static HttpType findByType(String type) {
		if(StringUtils.isBlank(type)) {
			return null;
		}
		HttpType[] httpTypes = HttpType.values();
		if(httpTypes!=null && httpTypes.length>0) {
			for(HttpType httpType : httpTypes) {
				if(httpType!=null && StringUtils.equals(httpType.getType(), type)) {
					return httpType;
				}
			}
		}
		return null;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
}

