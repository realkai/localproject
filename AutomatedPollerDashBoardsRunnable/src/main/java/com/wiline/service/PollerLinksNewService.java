package com.wiline.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wiline.dao.PollerLinksNewDao;
import com.wiline.util.TimeUtil;

/**
 * @author Kai Zhang
 *
 */
@Service
@Transactional
public class PollerLinksNewService {

    @Autowired
    private PollerLinksNewDao pollerLinksNewDao;

    @Autowired
    private DashboardService dashboardService;

    public void createDashboardIfNotExist(Map<Integer, Boolean> dashboardMap) {
	Set<Integer> createDashboardSet = new HashSet<>();
	for (Integer rfid : dashboardMap.keySet()) {
	    if (dashboardMap.get(rfid) == true && dashboardMap.get(rfid) != null) {
		System.out.println("Dashboard of -" + rfid + "- is existing ");
	    } else {
		System.out.println("create dashboard kai_test-" + rfid + "status");
		createDashboardSet.add(rfid);
	    }
	}
	if (!createDashboardSet.isEmpty()) {
	    dashboardService.createDashboardOnGrafana(createDashboardSet);
	}
    }

    /**
     * @param icingaLinkMap
     * @param pollerLinks
     */
    public void syncPollerLinks(HashMap<Integer, HashMap<String, String>> icingaLinkMap, Set<Integer> pollerLinks) {
	
	Set<Integer> insertSet = new HashSet<>(icingaLinkMap.keySet());

	Set<Integer> deleteSet = new HashSet<>(pollerLinks);

	Set<Integer> updateSet = new HashSet<>(insertSet);

	updateSet.retainAll(deleteSet);
	insertSet.removeAll(updateSet);
	deleteSet.removeAll(updateSet);

	// insertSet.clear();
	// insertSet.add(12320);
	// insertSet.add(12329);
	// insertSet.add(8233);
	// insertSet.add(8234);

	// deleteSet.clear();
	// deleteSet.add(12320);
	// deleteSet.add(12329);
	// deleteSet.add(8233);
	// deleteSet.add(8234);

	// updateSet.clear();
	// updateSet.add(12320);
	// updateSet.add(12329);
	// updateSet.add(8233);
	// updateSet.add(8234);

	if (!insertSet.isEmpty()) {
	    // insert new rfids
	    System.out.println(" inserting in poller_links_new... ");

	    pollerLinksNewDao.insertPollerLinksNew(icingaLinkMap, insertSet);

	    // find siklu set, MUST BE the siklu ones
	    dashboardService.createDashboardOnGrafana(insertSet);

	    System.out.println(" inserting in poller_links_new complete ! ");
	}

	if (!updateSet.isEmpty()) {
	    // updating full table will process after snmp is complete, but not
	    // process at this stage
	    // the snmp is based on the most up-to-date poller_links_new table
	    System.out.println(" will updating poller_links_new ");

	    // if modifydate has changed during last 12h, re-create the
	    // dashboard
	    Set<Integer> updateSetWithinHalfDay = new HashSet<>();

//	    icingaLinkMap.get(12320).put("modified_date", "2016-08-08 15:29:24.9853");

	    for (Integer rfid : icingaLinkMap.keySet()) {
		if (TimeUtil.isNeededToUpdate(icingaLinkMap.get(rfid).get("modified_date"))) {
		    updateSetWithinHalfDay.add(rfid);
		}
	    }

	    if (!updateSetWithinHalfDay.isEmpty()) {
		dashboardService.updateDashboardOnGrafana(updateSet);
	    }
	}

	if (!deleteSet.isEmpty()) {
	    System.out.println(" deleting poller_links_new... ");

	    // delete dashboard in grafana
	    dashboardService.deleteDashboard(deleteSet);
	    System.out.println("	Deleting dashboard complete ! ");
	    // delete records in db
	    pollerLinksNewDao.deleteElmentPollerLinksNew(deleteSet);

	    // for test, just set has_dashboard to false
	    System.out.println(" Deleting poller_links_new complete ! ");
	}
    }

    public void fillPollerLinkSnmpInfo(ConcurrentHashMap<Integer, ConcurrentHashMap<String, String>> pollerLinkMap) {

	System.out.println(" updating poller_links_new... ");

	pollerLinksNewDao.updatePollerLinksNew(pollerLinkMap);

	System.out.println(" updating poller_links_new complete ");
    }

}
