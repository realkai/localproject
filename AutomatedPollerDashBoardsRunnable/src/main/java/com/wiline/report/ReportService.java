package com.wiline.report;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.wiline.config.VarSetting;

@Service
public class ReportService {

    public void getProjectReport(ConcurrentHashMap<Integer, ConcurrentHashMap<String, String>> pollerLinkMap) {
	int totalNumIp = pollerLinkMap.keySet().size() * 2;
	int failCounter = 0;

	for (Integer rfid : pollerLinkMap.keySet()) {
	    if ("FAILED".equals(pollerLinkMap.get(rfid).get("response_a"))) {
		failCounter += 1;
	    }

	    if ("FAILED".equals(pollerLinkMap.get(rfid).get("response_z"))) {
		failCounter += 1;
	    }
	}

	System.out.println("Current snmp timeout: " + VarSetting.DEF_SNMP_TIME_OUT + " ms");
	System.out.println("Success rate: " + (totalNumIp - failCounter) + "/" + totalNumIp);
	System.out.println("Failed rate: " + failCounter + "/" + totalNumIp);
	System.out.println("Number of threads: " + VarSetting.DEF_SNMP_NUM_THREADS);
    }
}
