package com.wiline.dao;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Kai Zhang
 *
 */
@Repository
@Transactional
public class IcingaLinksDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * load icinga_links table to HashMap<rfid, HashMap<columnName,columnValue>>
     * 
     * @return icingaLinkMap
     */
    public HashMap<Integer, HashMap<String, String>> loadIcingaLinkMap() {

	String stmt = "SELECT cgs01.buildingrf.manufacturer_a AS manufacturer_a,"
		+ "voiceco.icinga_links.src_radio_ip AS src_radio_ip, "
		+ "voiceco.icinga_links.dst_radio_ip AS dst_radio_ip," + "voiceco.icinga_links.rfid AS rfid,"
		+ "voiceco.icinga_links.modified_date AS modified_date " + "FROM cgs01.buildingrf "
		+ "INNER JOIN voiceco.icinga_links " + "ON voiceco.icinga_links.rfid = cgs01.buildingrf.rfid "
		+ "WHERE voiceco.icinga_links.src_radio_ip IS NOT NULL";

	HashMap<Integer, HashMap<String, String>> icingaLinkMap = new HashMap<>();

	try {

	    SqlRowSet rows = jdbcTemplate.queryForRowSet(stmt);

	    while (rows.next()) {
		HashMap<String, String> temp = new HashMap<>();
		icingaLinkMap.put(rows.getInt("rfid"), temp);
		temp.put("src_radio_ip", rows.getString("src_radio_ip"));
		temp.put("dst_radio_ip", rows.getString("dst_radio_ip"));
		temp.put("manufacturer_a", rows.getString("manufacturer_a"));
		// the time will be auto convert to current time zone
		temp.put("modified_date", rows.getString("modified_date"));
	    }

	    // result display
	    for (Integer rfid : icingaLinkMap.keySet()) {
		System.out.println(rfid + ": " + icingaLinkMap.get(rfid));
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    // System.exit(-1);
	}

	return icingaLinkMap;
    }
}
