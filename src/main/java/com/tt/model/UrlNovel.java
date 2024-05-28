package com.tt.model;

import java.util.Map;

import com.tt.enums.NovelType;


public class UrlNovel {

	private String tchapter = null;
	
	private String sourceUrl = null;

	private String fileType = null;
	
	private String updateDate = null;
	
	private String nextChapter = null;
	
	private StringBuilder text = null;
	
	public UrlNovel() {
	}
		
	public UrlNovel(Map<String,Object> rsMap) {
		this();
		try {
			if(rsMap!=null) {
				this.tchapter = (String) rsMap.get("title");
				this.sourceUrl = (String) rsMap.get("source");
				this.fileType = (String) rsMap.get("type");
				this.updateDate = (String) rsMap.get("update");
				this.nextChapter = (String) rsMap.get("lastChapter");
//				this.text = (StringBuilder) rsMap.get("text");
			}
		} catch(Throwable e) {
			e.printStackTrace();
		}
	}

}


