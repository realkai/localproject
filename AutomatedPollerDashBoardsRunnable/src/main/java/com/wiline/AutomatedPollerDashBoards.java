package com.wiline;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.BasicConfigurator;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.wiline.config.AppConfig;
import com.wiline.dao.DashboardDao;
import com.wiline.dao.IcingaLinksDao;
import com.wiline.dao.PollerDevsettingNewDao;
import com.wiline.dao.PollerLinksNewDao;
import com.wiline.dao.PollerMibsNewDao;
import com.wiline.servicegroup.ServiceGroup;

@ComponentScan
@Configuration
public class AutomatedPollerDashBoards {

    public static void main(String[] args) {
	BasicConfigurator.configure();

	try {

	    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
	    context.register(AppConfig.class);
	    context.refresh();
	    // PollerMibService mib = context.getBean(PollerMibService.class);
	    // mib.comeon();

	    // PollerMibsDao mibDao = context.getBean(PollerMibsDao.class);
	    // mibDao.insert_1();
	    // mibDao.insert_3();

	    // IcingaLinksDao icingaDao = context.getBean(IcingaLinksDao.class);
	    // icingaDao.loadIcingaLinkMap();

	    // DashboardDao dd = context.getBean(DashboardDao.class);
	    // dd.getSikluFullInfoMap();

	    // PollerMibsNewDao pmd = context.getBean(PollerMibsNewDao.class);
	    // pmd.fetchDataMap();
	    // pmd.getMibPrefixMap();

	    // PollerLinksNewDao pld = context.getBean(PollerLinksNewDao.class);
	    // pld.getPollerDashboardMap();
	    // pld.getPollerLinks();
	    // pld.genPollerLinkSnmpMap();
	    // pld.getRowIdToRfidMap();

	    // PollerDevsettingNewDao pdd =
	    // context.getBean(PollerDevsettingNewDao.class);
	    // pdd.getDevsettingPollerIdPointRowIdMap();

	     ServiceGroup sg = context.getBean(ServiceGroup.class);
	     sg.syncIcingaLinks();
//	     sg.snmpPollerLinkNew();

//	    Charset charset = StandardCharsets.UTF_8;
//	    Path template = Paths.get("src/main/resources/jsonTemplate/siklu.json");
//	    String content = new String(Files.readAllBytes(template), charset);
//	    System.out.println(content);

	    context.close();
	} catch (Exception e) {
	    e.printStackTrace();
	    System.exit(0);
	}
    }

}
