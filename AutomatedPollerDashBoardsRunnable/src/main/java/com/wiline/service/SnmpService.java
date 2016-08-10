package com.wiline.service;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wiline.config.VarSetting;
import com.wiline.snmp.SingleSnmpThread;

@Service
@Transactional
public class SnmpService {

    private static final int NUM_THREADS = VarSetting.DEF_SNMP_NUM_THREADS;

    public void snmpPollerLinks(HashMap<Integer, HashMap<String, String>> icingaLinkMap,
	    ConcurrentHashMap<Integer, ConcurrentHashMap<String, String>> pollerLinkMap) {

	String[] modes = { "src_radio_ip", "dst_radio_ip" };

	ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

	String oid = VarSetting.OID;

	for (Integer rfid : icingaLinkMap.keySet()) {
	    executor.execute(
		    new SingleSnmpThread(pollerLinkMap, rfid, icingaLinkMap.get(rfid).get(modes[0]), modes[0], oid));
	    executor.execute(
		    new SingleSnmpThread(pollerLinkMap, rfid, icingaLinkMap.get(rfid).get(modes[1]), modes[1], oid));
	}
	executor.shutdown();

	try {
	    executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.MILLISECONDS);
	    System.out.println(" Snmp Get finished! ");
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

}
