package com.tt.test.model;

import com.tt.model.PvsRequest;

public class JSONTest {
	
	private JSONTest() {
	}
	
	public static void main(String[] args) {
		try {
			
			String json = "{" 
						+ "\"REQ_USER\":\"AkjhTSGF\"," 
						+ "\"USER_PWD\":\"SkJQTQ==\"," 
						+ "\"PRIORITY\":\"3\"," 
						+ "\"PROV_ORDER_MASTER\" : [" 
						+ "{" 
						+ "	\"SUBSCR_ID\":\"65901529\"," 
						+ "	\"TST_SEND_QUEUE\":\"N\"," 
						+ "	\"PROV_ORDER_DETAIL\":[" 
						+ "	{" 
						+ "		\"PARAM_NAME\":\"INFO_SMS_PHONE\"," 
						+ "		\"PARAM_VALUE\":\"\"" 
						+ "	}," 
						+ "	{" 
						+ "		\"PARAM_NAME\":\"NE_ID\"," 
						+ "		\"PARAM_VALUE\":\"UPP\"" 
						+ "	}" 
						+ "	]" 
						+ "}" 
						+ "]" 
						+ "}"
						;
			
			System.out.println(PvsRequest.jsonToObj(json));
			
		} catch(Throwable e) {
			e.printStackTrace();
		}
	}

}

