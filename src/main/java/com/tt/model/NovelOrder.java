package com.tt.model;

import java.util.Map;


import com.tt.enums.NovelType;


public class NovelOrder {

	private String title = null;
	
	private String source = null;

	private String type = null;
	
	private String update = null;
	
	private String lastChapter = null;
	
//	private StringBuilder text = null;
	
	public NovelOrder() {
	}
		
	public NovelOrder(Map<String,Object> rsMap) {
		this();
		try {
			if(rsMap!=null) {
				this.title = (String) rsMap.get("title");
				this.source = (String) rsMap.get("source");
				this.type = (String) rsMap.get("type");
				this.update = (String) rsMap.get("update");
				this.lastChapter = (String) rsMap.get("lastChapter");
//				this.text = (StringBuilder) rsMap.get("text");
			}
		} catch(Throwable e) {
			e.printStackTrace();
		}
	}

}


