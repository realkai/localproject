package com.wiline.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wiline.config.VarSetting;
import com.wiline.dao.DashboardDao;
import com.wiline.dao.PollerLinksNewDao;
import com.wiline.httprequest.MyHttpRequest;
import com.wiline.httprequest.SingleHttpReqThread;
import com.wiline.util.CSVUtil;

@Service
@Transactional
public class DashboardService {

    @Autowired
    private DashboardDao dashboardDao;

    @Autowired
    private PollerLinksNewDao pollerLinksNewDao;

    private ConcurrentHashMap<Integer, ConcurrentHashMap<String, String>> sikluDashMap;
    private ConcurrentSkipListSet<Integer> remainSet = new ConcurrentSkipListSet<>();

    private Map<Integer, Boolean> dashboardStatusHolderMap;

    // not the most elegant way
    public void updateDashboardOnGrafana(Set<Integer> updateSet) {
	deleteDashboard(updateSet);
	createDashboardOnGrafana(updateSet);
    }

    public void createDashboardOnGrafana(Set<Integer> insertSikluSet) {
	Path template = Paths.get(VarSetting.SIKLU_TEMPLATE_LOCATION);
	String content = null;

	try {
	    content = new String(Files.readAllBytes(template), StandardCharsets.UTF_8);
	} catch (Exception e1) {
	    e1.printStackTrace();
	    System.exit(0);
	    return;
	}

	System.out.println(insertSikluSet);

	sikluDashMap = dashboardDao.getSikluFullInfoMap();
	String mode = "create";
	boolean isRedo = false;
	ExecutorService es = Executors.newFixedThreadPool(VarSetting.DASHBOARD_NUM_THREADS);

	for (Integer rfid : insertSikluSet) {
	    // only allow siklu ones
	    if (sikluDashMap.containsKey(rfid)) {
		es.execute(new SingleHttpReqThread(content, rfid, sikluDashMap.get(rfid).get("distance_mi"),
			sikluDashMap.get(rfid).get("a"), sikluDashMap.get(rfid).get("z"),
			sikluDashMap.get(rfid).get("at"), sikluDashMap.get(rfid).get("zt"),
			sikluDashMap.get(rfid).get("a_netname"), sikluDashMap.get(rfid).get("z_netname"),
			sikluDashMap.get(rfid).get("a_address"), sikluDashMap.get(rfid).get("z_address"),
			sikluDashMap.get(rfid).get("a_lat"), sikluDashMap.get(rfid).get("a_log"),
			sikluDashMap.get(rfid).get("z_lat"), sikluDashMap.get(rfid).get("z_log"),
			sikluDashMap.get(rfid).get("a_host"), sikluDashMap.get(rfid).get("z_host"), mode, isRedo,
			remainSet));
	    }
	}
	es.shutdown();

	try {
	    es.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);

	    // careful, the whole set is the first argument
	    remainSet = getUnfinishedSet(new ConcurrentSkipListSet<Integer>(insertSikluSet), remainSet);

	    if (es.isTerminated() && !remainSet.isEmpty()) {
		isRedo = true;
		while (!remainSet.isEmpty() && !sikluDashMap.isEmpty()) {
		    ExecutorService ee = Executors.newFixedThreadPool(VarSetting.DASHBOARD_NUM_THREADS);
		    for (Integer id : remainSet) {
			if (sikluDashMap.containsKey(id)) {
			    ee.submit(new SingleHttpReqThread(content, id, sikluDashMap.get(id).get("distance_mi"),
				    sikluDashMap.get(id).get("a"), sikluDashMap.get(id).get("z"),
				    sikluDashMap.get(id).get("at"), sikluDashMap.get(id).get("zt"),
				    sikluDashMap.get(id).get("a_netname"), sikluDashMap.get(id).get("z_netname"),
				    sikluDashMap.get(id).get("a_address"), sikluDashMap.get(id).get("z_address"),
				    sikluDashMap.get(id).get("a_lat"), sikluDashMap.get(id).get("a_log"),
				    sikluDashMap.get(id).get("z_lat"), sikluDashMap.get(id).get("z_log"),
				    sikluDashMap.get(id).get("a_host"), sikluDashMap.get(id).get("z_host"), mode,
				    isRedo, remainSet));
			}
		    }
		    ee.shutdown();
		    ee.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
		}
	    }

	    // update has_dashboard to true
	    dashboardStatusHolderMap = pollerLinksNewDao.getPollerDashboardMap();

	    // System.out.println(dashboardStatusHolderMap);

	    for (Integer rfid : insertSikluSet) {
		dashboardStatusHolderMap.put(rfid, true);
	    }
	    pollerLinksNewDao.updatePollerLinksNewHashDashboard(dashboardStatusHolderMap, insertSikluSet);

	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
    }

    public void deleteDashboard(Set<Integer> deleteSet) {

	if (sikluDashMap == null) {
	    sikluDashMap = dashboardDao.getSikluFullInfoMap();
	}

	dashboardStatusHolderMap = pollerLinksNewDao.getPollerDashboardMap();

	String mode = "delete";
	boolean isRedo = false;
	ExecutorService es = Executors.newFixedThreadPool(VarSetting.DASHBOARD_THREADS_CRAZY);
	for (Integer rfid : deleteSet) {
	    if (sikluDashMap.containsKey(rfid) && dashboardStatusHolderMap.containsKey(rfid)
		    && dashboardStatusHolderMap.get(rfid)) {
		es.execute(new SingleHttpReqThread(rfid, mode, isRedo, remainSet));
	    }
	}
	es.shutdown();

	try {
	    es.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);

	    remainSet = getUnfinishedSet(new ConcurrentSkipListSet<Integer>(deleteSet), remainSet);

	    if (es.isTerminated() && !remainSet.isEmpty()) {
		isRedo = true;
		while (!remainSet.isEmpty()) {
		    ExecutorService ee = Executors.newFixedThreadPool(VarSetting.DASHBOARD_THREADS_CRAZY);
		    for (Integer id : remainSet) {
			MyHttpRequest.deleteDashboard(id, isRedo, remainSet);
			ee.execute(new SingleHttpReqThread(id, mode, isRedo, remainSet));
		    }
		    ee.shutdown();
		    ee.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
		}
		System.out.println(" Dashboard deleting complete! ");
	    }

	    // update has_dashboard to false
	    for (Integer rfid : deleteSet) {
		dashboardStatusHolderMap.put(rfid, false);
	    }
	    pollerLinksNewDao.updatePollerLinksNewHashDashboard(dashboardStatusHolderMap, deleteSet);
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
    }

    // testing method without updating table poller_links_new
    public void creatTestDashboardOnGrafana() {
	Path template = Paths.get("siklu.json");
	String content = null;

	try {
	    content = new String(Files.readAllBytes(template), StandardCharsets.UTF_8);
	} catch (IOException e1) {
	    e1.printStackTrace();
	}

	sikluDashMap = dashboardDao.getSikluFullInfoMap();
	String mode = "create";
	boolean isRedo = false;
	ExecutorService es = Executors.newFixedThreadPool(VarSetting.DASHBOARD_NUM_THREADS);
	for (Integer rfid : sikluDashMap.keySet()) {
	    es.execute(new SingleHttpReqThread(content, rfid, sikluDashMap.get(rfid).get("distance_mi"),
		    sikluDashMap.get(rfid).get("a"), sikluDashMap.get(rfid).get("z"), sikluDashMap.get(rfid).get("at"),
		    sikluDashMap.get(rfid).get("zt"), sikluDashMap.get(rfid).get("a_netname"),
		    sikluDashMap.get(rfid).get("z_netname"), sikluDashMap.get(rfid).get("a_address"),
		    sikluDashMap.get(rfid).get("z_address"), sikluDashMap.get(rfid).get("a_lat"),
		    sikluDashMap.get(rfid).get("a_log"), sikluDashMap.get(rfid).get("z_lat"),
		    sikluDashMap.get(rfid).get("z_log"), sikluDashMap.get(rfid).get("a_host"),
		    sikluDashMap.get(rfid).get("z_host"), mode, isRedo, remainSet));
	}
	es.shutdown();

	try {
	    es.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);

	    remainSet = getUnfinishedSet(new ConcurrentSkipListSet<Integer>(sikluDashMap.keySet()), remainSet);

	    if (es.isTerminated() && !remainSet.isEmpty()) {
		isRedo = true;
		while (!remainSet.isEmpty()) {
		    ExecutorService ee = Executors.newFixedThreadPool(VarSetting.DASHBOARD_NUM_THREADS);
		    for (Integer id : remainSet) {
			ee.submit(new SingleHttpReqThread(content, id, sikluDashMap.get(id).get("distance_mi"),
				sikluDashMap.get(id).get("a"), sikluDashMap.get(id).get("z"),
				sikluDashMap.get(id).get("at"), sikluDashMap.get(id).get("zt"),
				sikluDashMap.get(id).get("a_netname"), sikluDashMap.get(id).get("z_netname"),
				sikluDashMap.get(id).get("a_address"), sikluDashMap.get(id).get("z_address"),
				sikluDashMap.get(id).get("a_lat"), sikluDashMap.get(id).get("a_log"),
				sikluDashMap.get(id).get("z_lat"), sikluDashMap.get(id).get("z_log"),
				sikluDashMap.get(id).get("a_host"), sikluDashMap.get(id).get("z_host"), mode, isRedo,
				remainSet));
		    }
		    ee.shutdown();
		    ee.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
		}
	    }
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
    }

    // testing method without updating table poller_links_new
    public void deleteTestDashboard() {
	sikluDashMap = dashboardDao.getSikluFullInfoMap();
	String mode = "delete";
	boolean isRedo = false;
	ExecutorService es = Executors.newFixedThreadPool(VarSetting.DASHBOARD_THREADS_CRAZY);
	for (Integer rfid : sikluDashMap.keySet()) {
	    es.execute(new SingleHttpReqThread(rfid, mode, isRedo, remainSet));
	}
	es.shutdown();

	try {
	    es.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);

	    remainSet = getUnfinishedSet(new ConcurrentSkipListSet<Integer>(sikluDashMap.keySet()), remainSet);

	    if (es.isTerminated() && !remainSet.isEmpty()) {
		isRedo = true;
		while (!remainSet.isEmpty()) {
		    ExecutorService ee = Executors.newFixedThreadPool(VarSetting.DASHBOARD_THREADS_CRAZY);
		    for (Integer id : remainSet) {
			MyHttpRequest.deleteDashboard(id, isRedo, remainSet);
			ee.execute(new SingleHttpReqThread(id, mode, isRedo, remainSet));
		    }
		    ee.shutdown();
		    ee.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
		}
	    }
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
    }

    public void writeSikluDashCSV() {
	CSVUtil.writeSikluDashboardCSV(sikluDashMap);
    }

    private ConcurrentSkipListSet<Integer> getUnfinishedSet(ConcurrentSkipListSet<Integer> fullSet,
	    ConcurrentSkipListSet<Integer> oldRemainSet) {
	fullSet.removeAll(oldRemainSet);
	return fullSet;
    }

}
