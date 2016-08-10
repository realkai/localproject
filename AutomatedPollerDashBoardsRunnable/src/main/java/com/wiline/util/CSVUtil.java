package com.wiline.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.opencsv.CSVWriter;

public class CSVUtil {

    public static void main(String[] args) throws IOException {
	CSVWriter wr = new CSVWriter(new FileWriter("AAA.csv"), ',', CSVWriter.NO_QUOTE_CHARACTER);
	String[] abs = { "a", "a", "a", "a", "a", "a", "a", "a" };
	wr.writeNext(abs);
	wr.close();
    }

    public static void writeSikluDashboardCSV(
	    ConcurrentHashMap<Integer, ConcurrentHashMap<String, String>> sikluDashMap) {
	try (CSVWriter wr = new CSVWriter(new FileWriter("dash_Siklu.csv"), ',', CSVWriter.NO_QUOTE_CHARACTER)) {

	    List<String> list = new ArrayList<>();
	    list.addAll(Arrays.asList("distance_mi", "rfid", "a", "z", "at", "zt", "a_netname", "z_netname",
		    "a_address", "z_address", "a_lat", "a_log", "z_lat", "z_log", "a_host", "z_host"));
	    wr.writeNext(list.toArray(new String[0]));
	    list.clear();

	    System.out.println("Will write to dash_skilu.csv: " + sikluDashMap.keySet().size() + " lines");

	    for (Integer rfid : sikluDashMap.keySet()) {
		list.add(sikluDashMap.get(rfid).get("distance_mi"));
		list.add(String.valueOf(rfid));
		list.add(sikluDashMap.get(rfid).get("a"));
		list.add(sikluDashMap.get(rfid).get("z"));
		list.add(sikluDashMap.get(rfid).get("at"));
		list.add(sikluDashMap.get(rfid).get("zt"));
		list.add(sikluDashMap.get(rfid).get("a_netname"));
		list.add(sikluDashMap.get(rfid).get("z_netname"));
		list.add(sikluDashMap.get(rfid).get("a_address"));
		list.add(sikluDashMap.get(rfid).get("z_address"));
		list.add(sikluDashMap.get(rfid).get("a_lat"));
		list.add(sikluDashMap.get(rfid).get("a_log"));
		list.add(sikluDashMap.get(rfid).get("z_lat"));
		list.add(sikluDashMap.get(rfid).get("z_log"));
		list.add(sikluDashMap.get(rfid).get("a_host"));
		list.add(sikluDashMap.get(rfid).get("z_host"));
		wr.writeNext(list.toArray(new String[0]));
		list.clear();
	    }
	    System.out.println(" Writing dash_siklu.csv is complete !");
	} catch (IOException e) {
	    e.printStackTrace();
	    System.exit(0);
	}
    }

    public static void writeLinkMapCSV(HashMap<Integer, HashMap<String, String>> icingaLinkMap,
	    ConcurrentHashMap<Integer, ConcurrentHashMap<String, String>> pollerLinkMap,
	    HashMap<String, HashSet<String>> manufacturer, String oid) {
	List<CSVWriter> writerList = new ArrayList<>();

	try {
	    CSVWriter writer_ub = new CSVWriter(new FileWriter("Ubiquiti.csv"), ',', CSVWriter.NO_QUOTE_CHARACTER);
	    writerList.add(writer_ub);
	    CSVWriter writer_sk = new CSVWriter(new FileWriter("Siklu.csv"), ',', CSVWriter.NO_QUOTE_CHARACTER);
	    writerList.add(writer_sk);

	    List<String> src_list = new ArrayList<>();
	    List<String> dst_list = new ArrayList<>();
	    for (Integer rfid : icingaLinkMap.keySet()) {

		if ("Ubiquiti".equals(icingaLinkMap.get(rfid).get("manufacturer_a"))) {

		    src_list.add(icingaLinkMap.get(rfid).get("src_radio_ip"));
		    src_list.add(pollerLinkMap.get(rfid).get("community_a"));
		    src_list.add(pollerLinkMap.get(rfid).get("snmp_version_a"));

		    dst_list.add(icingaLinkMap.get(rfid).get("dst_radio_ip"));
		    dst_list.add(pollerLinkMap.get(rfid).get("community_z"));
		    dst_list.add(pollerLinkMap.get(rfid).get("snmp_version_z"));

		    for (String prefix : manufacturer.get("Ubiquiti")) {
			src_list.add(prefix + "-" + oid);
			dst_list.add(prefix + "-" + oid);
		    }
		    writer_ub.writeNext(src_list.toArray(new String[0]));
		    writer_ub.writeNext(dst_list.toArray(new String[0]));
		    src_list.clear();
		    dst_list.clear();
		}

		if ("Siklu".equals(icingaLinkMap.get(rfid).get("manufacturer_a"))) {

		    src_list.add(icingaLinkMap.get(rfid).get("src_radio_ip"));
		    src_list.add(pollerLinkMap.get(rfid).get("community_a"));
		    src_list.add(pollerLinkMap.get(rfid).get("snmp_version_a"));

		    dst_list.add(icingaLinkMap.get(rfid).get("dst_radio_ip"));
		    dst_list.add(pollerLinkMap.get(rfid).get("community_z"));
		    dst_list.add(pollerLinkMap.get(rfid).get("snmp_version_z"));

		    for (String prefix : manufacturer.get("Siklu")) {
			src_list.add(prefix + "-" + oid);
			dst_list.add(prefix + "-" + oid);
		    }
		    writer_sk.writeNext(src_list.toArray(new String[0]));
		    writer_sk.writeNext(dst_list.toArray(new String[0]));
		    src_list.clear();
		    dst_list.clear();
		}

	    }
	    for (CSVWriter cw : writerList) {
		cw.close();
	    }
	    System.out.println("Writing to CSV completed !");
	} catch (IOException e) {
	    e.printStackTrace();
	}

    }

}
