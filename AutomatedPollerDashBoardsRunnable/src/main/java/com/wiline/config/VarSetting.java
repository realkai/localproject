package com.wiline.config;

public class VarSetting {
	// server setting
	public final static String HOST = "67.207.98.242";
	public final static String USER = "kzhang";
	public final static String PASSWORD = "kai310?905:121";
	
	// snmp setting
	public final static String OID = ".1.3.6.1.2.1.1.1.0";
	public final static String DEF_SNMP_PORT = "/161";
	// milliseconds
	public final static long DEF_SNMP_TIME_OUT = 2000L;
	public final static int DEF_SNMP_RETRIES = 2;
	public final static int DEF_SNMP_NUM_THREADS = 600;
	
	public final static String FILE_PATH = "";
	
	// dashboard
	public final static int DASHBOARD_NUM_THREADS = 10; 
	public final static int HTTP_REQ_WAIT_TIME = 0;
	public final static int DASHBOARD_THREADS_CRAZY = 20;
	public final static int HTTP_TIMEOUT = 2000;
	
	public final static String SIKLU_TEMPLATE_LOCATION = "src/main/resources/jsonTemplate/siklu.json";
}
