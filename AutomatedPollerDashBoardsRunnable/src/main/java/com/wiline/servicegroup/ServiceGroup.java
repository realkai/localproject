package com.wiline.servicegroup;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wiline.config.VarSetting;
import com.wiline.dao.IcingaLinksDao;
import com.wiline.dao.PollerLinksNewDao;
import com.wiline.dao.PollerMibsNewDao;
import com.wiline.report.ReportService;
import com.wiline.service.DashboardService;
import com.wiline.service.PollerDevsettingNewService;
import com.wiline.service.PollerLinksNewService;
import com.wiline.service.SnmpService;
import com.wiline.ssh.MySSH2;
import com.wiline.util.CSVUtil;

/**
 * @author Kai Zhang
 *
 */
@Service
public class ServiceGroup {

    @Autowired
    private IcingaLinksDao icingaLinksDao;

    @Autowired
    private PollerLinksNewDao pollerLinksNewDao;

    @Autowired
    private PollerLinksNewService pollerLinksNewService;

    @Autowired
    private PollerDevsettingNewService pollerDevsettingNewService;

    @Autowired
    private SnmpService snmpService;

    @Autowired
    private PollerMibsNewDao pollerMibsNewDao;

    @Autowired
    private MySSH2 sshService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private DashboardService dashboardService;

    private HashMap<Integer, HashMap<String, String>> icingaLinkMap;

    private ConcurrentHashMap<Integer, ConcurrentHashMap<String, String>> pollerLinkMap;

    public void updateDashboard() {
	pollerLinksNewService.createDashboardIfNotExist(pollerLinksNewDao.getPollerDashboardMap());
    }

    /**
     * sync icinga_links to poller_links_new only once
     */
    public void syncIcingaLinks() {
	preLoadIcingLinkMap();
	
	
	Set<Integer> pollerLinks = pollerLinksNewDao.getPollerLinks();

	pollerLinksNewService.syncPollerLinks(icingaLinkMap, pollerLinks);

	// for (Integer rfid : icingaLinkMap.keySet()) {
	// System.out.println(rfid);
	// System.out.println(icingaLinkMap.get(rfid) + "\n");
	// }
	
	snmpPollerLinkNew();
	updatePollerLinksAndDevsetting();
	getSnmpReport();
    }

    public void snmpPollerLinkNew() {
	initializePollerLinkMap();
	System.out.println("before snmp: " + pollerLinkMap);
	snmpService.snmpPollerLinks(icingaLinkMap, pollerLinkMap);
    }

    public void updatePollerLinksAndDevsetting() {

	pollerLinksNewService.fillPollerLinkSnmpInfo(pollerLinkMap);

	pollerDevsettingNewService.updatePollerDevsettingNew(pollerLinkMap);
    }

    public void writeLinkMapCSV() {
	String oid = VarSetting.OID;
	CSVUtil.writeLinkMapCSV(icingaLinkMap, pollerLinkMap, pollerMibsNewDao.getMibPrefixMap(), oid);
    }

    public void writeSikluDashCSV() {
	dashboardService.writeSikluDashCSV();
    }

    public void transferCSVToServer() {
	sshService.transferCSVToServer(VarSetting.USER, VarSetting.HOST, VarSetting.PASSWORD);
    }

    public void getSnmpReport() {
	reportService.getProjectReport(pollerLinkMap);
    }

    // below is supporting methods
    private void preLoadIcingLinkMap() {
	icingaLinkMap = icingaLinksDao.loadIcingaLinkMap();
    }

    private void initializePollerLinkMap() {
	pollerLinkMap = pollerLinksNewDao.genPollerLinkSnmpMap();
	System.out.println("	PollerLinkMap"+pollerLinkMap);
    }

    public ConcurrentHashMap<Integer, ConcurrentHashMap<String, String>> getPollerLinkMap() {
	return pollerLinkMap;
    }

    public void setPollerLinkMap(ConcurrentHashMap<Integer, ConcurrentHashMap<String, String>> pollerLinkMap) {
	this.pollerLinkMap = pollerLinkMap;
    }

}
