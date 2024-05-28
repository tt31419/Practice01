package com.tt.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * UPP_GW_ORDER.STATUS
 */
public enum OrderStatus {
	
	WAIT("W", "待處理"),  //尚未驗證參數
	OPEN("O", "已取得"),  //基本驗證參數後 準備呼叫 EAIUtils gwToEAI
	PROCESS("P", "處理中"),  //建立UPP_EAI_WORKING.WORK_SEQ/LTE_ORDER.ORDER_ID/SPS_ORDER.ORDER_ID完成，等待tstprovisionresult完成
	DONE("D", "UPP完成"),  //GW Queue 確認UPP完成並更新GW_ORDER後 準備回傳
	COMPLETE("C", "完成"),
	FAIL("F", "失敗"),
	RETRY("R", "等待重送"),  //D->C Fail 20220314 等測試後續再觀察是否有特定errMsg可以指定到連不回CPC的狀態，將這些單子另外進行RubTask重送
	
	;
	
	private String value = null;
	
	private String label = null;
	
	private OrderStatus(String value, String label) {
		this.value = value;
		this.label = label;
	}
	
	public static List<String> listValue() {
		List<String> values = new ArrayList<String>();
		values.add(WAIT.getValue());
		values.add(OPEN.getValue());
		values.add(PROCESS.getValue());
		values.add(DONE.getValue());
		values.add(COMPLETE.getValue());
		values.add(FAIL.getValue());
		return values;
	}
	
	public static boolean isWait(String status) {
		return WAIT.getValue().equals(status);
	}
	
	public static boolean isOpen(String status) {
		return OPEN.getValue().equals(status);
	}
	
	public static boolean isProcess(String status) {
		return PROCESS.getValue().equals(status);
	}
	
	public static boolean isDone(String status) {
		return DONE.getValue().equals(status);
	}
	
	public static boolean isComplete(String status) {
		return COMPLETE.getValue().equals(status);
	}
	
	public static boolean isFail(String status) {
		return FAIL.getValue().equals(status);
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}

