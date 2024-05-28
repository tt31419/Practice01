package com.tt.enums;

import org.apache.commons.lang.StringUtils;

public enum NovelType {
	
	URL("url", true, "check from url"),
	TXT("txt", false, "local files"),
	;
	
	private String type = null;

	private boolean isUpdate = false;
	
	private String desc = null;
	
	private NovelType(String type, boolean isUpdate, String desc) {
		this.type = type;
		this.isUpdate = isUpdate;
		this.desc = desc;
	}
	
	public static NovelType findByType(String type) {
		if(StringUtils.isBlank(type)) {
			return null;
		}
		NovelType[] httpTypes = NovelType.values();
		if(httpTypes!=null && httpTypes.length>0) {
			for(NovelType httpType : httpTypes) {
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

	public boolean isUpdate() {
		return isUpdate;
	}

	public void setUpdate(boolean isUpdate) {
		this.isUpdate = isUpdate;
	}
	
}

