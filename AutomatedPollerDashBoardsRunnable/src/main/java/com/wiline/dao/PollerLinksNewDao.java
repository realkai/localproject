package com.wiline.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.wiline.util.TimeUtil;

/**
 * @author Kai Zhang
 *
 */
@Repository
@Transactional
public class PollerLinksNewDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<Integer, Boolean> getPollerDashboardMap() {
	String stmt = "SELECT rfid,has_dashboard FROM voiceco.poller_links_new ";

	Map<Integer, Boolean> pollerDashboardMap = new ConcurrentHashMap<>();

	try {

	    SqlRowSet rows = jdbcTemplate.queryForRowSet(stmt);

	    while (rows.next()) {
		pollerDashboardMap.put(rows.getInt("rfid"), rows.getBoolean("has_dashboard"));
	    }

	} catch (Exception e) {
	    e.printStackTrace();

	}

	// result display
	System.out.println(pollerDashboardMap);

	return pollerDashboardMap;
    }

    public void updatePollerLinksNewHashDashboard(Map<Integer, Boolean> pollerDashboardMap, Set<Integer> resultSet) {
	String stmt = "UPDATE voiceco.poller_links_new " + "SET has_dashboard=?, " + "modifydate=? " + "WHERE rfid=?";

	List<Object[]> list = new ArrayList<>();

	// object[]{rfid, has_dashboard}
	for (Integer rfid : resultSet) {
	    list.add(new Object[] { rfid, pollerDashboardMap.get(rfid) });
	}

	jdbcTemplate.batchUpdate(stmt, new BatchPreparedStatementSetter() {

	    @Override
	    public void setValues(PreparedStatement ps, int i) throws SQLException {
		Object[] temp = list.get(i);
		Timestamp ts = TimeUtil.getCurrentTimestamp();
		Calendar calendar = TimeUtil.getCalendarUTC();
		ps.setBoolean(1, (boolean) temp[1]);
		ps.setTimestamp(2, ts, calendar);
		ps.setInt(3, (int) temp[0]);
	    }

	    @Override
	    public int getBatchSize() {
		return list.size();
	    }
	});

    }

    public void updatePollerLinksNew(ConcurrentHashMap<Integer, ConcurrentHashMap<String, String>> pollerLinkMap) {
	String stmt = "UPDATE voiceco.poller_links_new " + "SET response_a=?, " + "last_response_a=?, "
		+ "response_z=?, " + "last_response_z=?, " + "modifydate=? " + "WHERE rfid=?";

	List<Object[]> list = new ArrayList<>();
	Calendar calendar = TimeUtil.getCalendarUTC();
	
	 for (Integer rfid : pollerLinkMap.keySet()) {
	     
	     list.add(new Object[]{
		     pollerLinkMap.get(rfid).get("response_a"),
		     pollerLinkMap.get(rfid).get("last_response_a"),
		     pollerLinkMap.get(rfid).get("response_z"),
		     pollerLinkMap.get(rfid).get("last_response_z"),
		     rfid
	     });
	 }
	 
	 jdbcTemplate.batchUpdate(stmt, new BatchPreparedStatementSetter() {
	    
	    @Override
	    public void setValues(PreparedStatement ps, int i) throws SQLException {
		Object[] temp = list.get(i);
		ps.setString(1, (String) temp[0]);
		
		String l_resp_a = (String)temp[1];
		if (l_resp_a.length() > 0){
		    ps.setTimestamp(2, Timestamp.valueOf(l_resp_a), calendar);
		} else {
		    ps.setTimestamp(2, null);
		}
		
		ps.setString(3, (String) temp[2]);
		
		String l_resp_z = (String)temp[3];
		if (l_resp_z.length() > 0){
		    ps.setTimestamp(4, Timestamp.valueOf(l_resp_z), calendar);
		} else {
		    ps.setTimestamp(4, null);
		}
		
		ps.setTimestamp(5, TimeUtil.getCurrentTimestamp(), calendar);
		ps.setInt(6, (int) temp[4]);
	    }
	    
	    @Override
	    public int getBatchSize() {
		return list.size();
	    }
	});
	
    }

    

    /**
     * Insert new rfids from icinga_links to poller_links_new
     *
     * @param icingaLinkMap
     * @param icingaLinks
     */
    public void insertPollerLinksNew(HashMap<Integer, HashMap<String, String>> icingaLinkMap,
	    Set<Integer> icingaLinks) {

	String stmt = "INSERT INTO voiceco.poller_links_new " + "(rfid) VALUES (?)";

	List<Object[]> list = new ArrayList<>();
	
	for (Integer rfid : icingaLinks) {
	    list.add(new Object[]{rfid});
	}
	
	jdbcTemplate.batchUpdate(stmt, new BatchPreparedStatementSetter() {
	    
	    @Override
	    public void setValues(PreparedStatement ps, int i) throws SQLException {
		// TODO Auto-generated method stub
		Object[] temp = list.get(i);
		ps.setInt(1, (int)temp[0]);
	    }
	    
	    @Override
	    public int getBatchSize() {
		return list.size();
	    }
	});
	
    }

    /**
     * Delete rfids in poller_links_new table which are not in icinga_links
     * table
     *
     * @param redundantPollerLinks
     */
    public void deleteElmentPollerLinksNew(Set<Integer> redundantPollerLinks) {
	String stmt = "DELETE FROM voiceco.poller_links_new " + "WHERE rfid=?";

	List<Object[]> list = new ArrayList<>();

	for (Integer rfid : redundantPollerLinks) {
	    list.add(new Object[] { rfid });
	}

	jdbcTemplate.batchUpdate(stmt, list);
    }

    /**
     * Retrieve rfid from voiceco.poller_links_new to HashSet
     * 
     * @return HashSet of pollerLinks.rfid
     */
    public Set<Integer> getPollerLinks() {

	String stmt = "select rfid from voiceco.poller_links_new";

	Set<Integer> pollerLinks = new HashSet<>();

	SqlRowSet rows = jdbcTemplate.queryForRowSet(stmt);

	while (rows.next()) {
	    pollerLinks.add(rows.getInt("rfid"));
	}

	// result display
	System.out.println(pollerLinks);

	return pollerLinks;
    }

    public ConcurrentHashMap<Integer, ConcurrentHashMap<String, String>> genPollerLinkSnmpMap() {

	String stmt = "select rfid,id from voiceco.poller_links_new";

	ConcurrentHashMap<Integer, ConcurrentHashMap<String, String>> pollerLinkMap = new ConcurrentHashMap<>();

	SqlRowSet rows = jdbcTemplate.queryForRowSet(stmt);

	ConcurrentHashMap<String, String> temp;

	while (rows.next()) {
	    temp = new ConcurrentHashMap<>();
	    pollerLinkMap.put(rows.getInt("rfid"), temp);

	    temp.put("poller_link_id", rows.getString("id"));

	    // important: to_scan is a boolean, default='true'
	    temp.put("to_scan", "true");

	    temp.put("response_a", "FAILED");
	    temp.put("last_response_a", "");

	    temp.put("response_z", "FAILED");
	    temp.put("last_response_z", "");

	    temp.put("community_a", "FAILED");
	    temp.put("snmp_version_a", "FAILED");

	    temp.put("community_z", "FAILED");
	    temp.put("snmp_version_z", "FAILED");
	}

	// result display
	for (Integer rfid : pollerLinkMap.keySet()) {
	    System.out.println(rfid + ": " + pollerLinkMap.get(rfid));
	}

	return pollerLinkMap;
    }

    public HashMap<Long, Integer> getRowIdToRfidMap() {
	String stmt = "select rfid,id from voiceco.poller_links_new";
	HashMap<Long, Integer> map = new HashMap<>();

	SqlRowSet rows = jdbcTemplate.queryForRowSet(stmt);

	while (rows.next()) {
	    map.put(rows.getLong("id"), rows.getInt("rfid"));
	}

	// result display
	System.out.println(map);

	return map;
    }

}
