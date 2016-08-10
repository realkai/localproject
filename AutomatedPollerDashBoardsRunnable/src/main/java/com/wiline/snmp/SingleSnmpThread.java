package com.wiline.snmp;

import java.util.concurrent.ConcurrentHashMap;

public class SingleSnmpThread implements Runnable {

    private String mode;
    private ConcurrentHashMap<Integer, ConcurrentHashMap<String, String>> pollerLinkMap;
    private Integer rfid;
    private String ip;
    private String oid;

    public SingleSnmpThread(ConcurrentHashMap<Integer, ConcurrentHashMap<String, String>> pollerLinkMap, Integer rfid,
	    String ip, String mode, String oid) {
	this.pollerLinkMap = pollerLinkMap;
	this.ip = ip;
	this.mode = mode;
	this.rfid = rfid;
	this.oid = oid;
    }

    @Override
    public void run() {
	MySnmp vs = new MySnmp(pollerLinkMap, rfid, mode);
	vs.snmpGET(ip, oid);
    }

}
