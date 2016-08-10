package com.wiline.config;

import javax.sql.DataSource;

import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.wiline.dao.DashboardDao;
import com.wiline.dao.IcingaLinksDao;
import com.wiline.dao.PollerDevsettingNewDao;
import com.wiline.dao.PollerLinksNewDao;
import com.wiline.dao.PollerMibsNewDao;
import com.wiline.httprequest.MyHttpRequest;
import com.wiline.report.ReportService;
import com.wiline.service.DashboardService;
import com.wiline.service.PollerDevsettingNewService;
import com.wiline.service.PollerLinksNewService;
import com.wiline.service.SnmpService;
import com.wiline.servicegroup.ServiceGroup;
import com.wiline.ssh.MySSH2;

@Configuration
public class AppConfig {

    @Bean
    public static DataSource dataSource() {
	PGSimpleDataSource pds = new PGSimpleDataSource();
	pds.setUrl("jdbc:postgresql://192.168.225.110/wilinecgs?searchpath=voiceco");
	pds.setUser("wiline_ro");
	pds.setPassword("n21Hdwk_[_;r");
	return pds;
    }

    @Bean
    public static DataSource dataSourceLocal() {
	PGSimpleDataSource pds = new PGSimpleDataSource();
	pds.setUrl("jdbc:postgresql://localhost:5432/voiceco");
	pds.setUser("voiceco");
	pds.setPassword("voiceco");
	return pds;
    }

    @Bean
    public ServiceGroup serviceGroup() {
	return new ServiceGroup();
    }

    @Bean
    public SnmpService snmpService() {
	return new SnmpService();
    }

    @Bean
    public PollerDevsettingNewService pollerDevsettingNewService() {
	return new PollerDevsettingNewService();
    }

    @Bean
    public DashboardService dashboardService() {
	return new DashboardService();
    }

    @Bean
    public PollerLinksNewService pollerLinksNewService() {
	return new PollerLinksNewService();
    }

    @Bean
    public PollerDevsettingNewDao pollerDevsettingNewDao() {
	return new PollerDevsettingNewDao();
    }

    @Bean
    public PollerLinksNewDao pollerLinksNewDao() {
	return new PollerLinksNewDao();
    }

    @Bean
    public PollerMibsNewDao pollerMibsNewDao() {
	return new PollerMibsNewDao();
    }

    @Bean
    public DashboardDao dashboardDao() {
	return new DashboardDao();
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
	return new JdbcTemplate(dataSource());
    }

    @Bean
    public IcingaLinksDao icingaLinksDao() {
	return new IcingaLinksDao();
    }

    @Bean
    public ReportService reportService() {
	return new ReportService();
    }

    @Bean
    public MySSH2 mySSH2() {
	return new MySSH2();
    }

    @Bean
    public MyHttpRequest myHttpRequest() {
	return new MyHttpRequest();
    }

}
