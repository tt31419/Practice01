package com.tt;

import com.tt.util.DbUtils;

public class Constrain {
	
	public static String ORACLE_DRIVER = "oracle.jdbc.driver.OracleDriver";
	
	//define in web.xml	
	public static String DEFAULT_DATASOURCE_NAME = "LTE_Datasource";
	
	//Novel
	public static String LOCAL_CONFIG_PATH = "D:/TEST/";
	public static String TEST_URL = "https://www.google.com/";
	public static String TEST_COOKIES = "depends on wbesites";
	
	public static DbUtils DEFAULT_DB = null;
	
	private static boolean init = false;
	
	public static int DEFAULT_INDENTFACTOR = 2;
	
	//log4j
	public static String LOG_TYPE_INFO = "info";
	public static String LOG_TYPE_DEBUG = "debug";
	public static String LOG_TYPE_FINE = "fine";
	public static String LOG_TYPE_WARNING = "warning";
	
	private Constrain() {
	}
	
	static {
		try {
			if(!init) {
				// DEFAULT_DB = DbUtils.getInstance();
//				DEFAULT_DB = new DbUtils("jdbc:oracle:thin:@ipv4:port:SIT", "userName", "password");
//				Map<String,String> configMap = UPPFacade.getInstance(DEFAULT_DB).findConfigs(null, null, null, true);
			}
		} catch(Throwable e) {
			e.printStackTrace();
		} finally {
			init = true;
		}
	}
	
}
