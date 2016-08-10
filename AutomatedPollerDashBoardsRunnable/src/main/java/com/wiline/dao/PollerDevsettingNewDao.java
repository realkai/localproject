package com.wiline.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class PollerDevsettingNewDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void insertPollerDevsettingInfo(
	    ConcurrentHashMap<Integer, ConcurrentHashMap<String, String>> pollerLinkMap) {
	String stmt = "INSERT INTO voiceco.poller_devsetting_new "
		+ "(community_a,snmp_version_a,community_z,snmp_version_z,to_scan,poller_link_id,mibs) "
		+ "VALUES (?,?,?,?,?,?,?)";

	List<Object[]> list = new ArrayList<>();
	
	System.out.println(pollerLinkMap);

	for (Integer rfid : pollerLinkMap.keySet()) {
	    list.add(new Object[] { pollerLinkMap.get(rfid).get("community_a"),
		    pollerLinkMap.get(rfid).get("snmp_version_a"), pollerLinkMap.get(rfid).get("community_z"),
		    pollerLinkMap.get(rfid).get("snmp_version_z"), pollerLinkMap.get(rfid).get("to_scan"),
		    pollerLinkMap.get(rfid).get("poller_link_id"), 0 });
	}

	jdbcTemplate.batchUpdate(stmt, new BatchPreparedStatementSetter() {
	    
	    @Override
	    public void setValues(PreparedStatement ps, int i) throws SQLException {
		// TODO Auto-generated method stub
		Object[] temp = list.get(i);
		ps.setString(1, (String) temp[0]);
		ps.setString(2, (String) temp[1]);
		ps.setString(3, (String) temp[2]);
		ps.setString(4, (String) temp[3]);
		ps.setBoolean(5, Boolean.valueOf((String)temp[4]));
		ps.setLong(6, Long.parseLong((String)temp[5]));
		ps.setString(7, "");
	    }
	    
	    @Override
	    public int getBatchSize() {
		return list.size();
	    }
	});

    }

    public void updatePollerDevsettingInfo(
	    ConcurrentHashMap<Integer, ConcurrentHashMap<String, String>> pollerLinkMap) {
	String stmt = "UPDATE voiceco.poller_devsetting_new " + "SET community_a=?, " + "snmp_version_a=?, "
		+ "community_z=?, " + "snmp_version_z=?, " + "to_scan=? " + "WHERE poller_link_id=?";

	List<Object[]> list = new ArrayList<>();

	for (Integer rfid : pollerLinkMap.keySet()) {
	    list.add(new Object[] { pollerLinkMap.get(rfid).get("community_a"),
		    pollerLinkMap.get(rfid).get("snmp_version_a"), pollerLinkMap.get(rfid).get("community_z"),
		    pollerLinkMap.get(rfid).get("snmp_version_z"), pollerLinkMap.get(rfid).get("to_scan"),
		    pollerLinkMap.get(rfid).get("poller_link_id") });
	}

	jdbcTemplate.batchUpdate(stmt, new BatchPreparedStatementSetter() {
	    
	    @Override
	    public void setValues(PreparedStatement ps, int i) throws SQLException {
		Object[] temp = list.get(i);
		ps.setString(1, (String) temp[0]);
		ps.setString(2, (String) temp[1]);
		ps.setString(3, (String) temp[2]);
		ps.setString(4, (String) temp[3]);
		ps.setBoolean(5, Boolean.valueOf((String)temp[4]));
		ps.setLong(6, Long.parseLong((String)temp[5]));
	    }
	    
	    @Override
	    public int getBatchSize() {
		return list.size();
	    }
	});
    }

    public void deletePollerDevsettingInfo(HashSet<Long> deleteSet) {
	String stmt = "DELETE FROM voiceco.poller_devsetting_new " + "WHERE poller_link_id=? " + "AND to_scan=true";

	List<Object[]> list = new ArrayList<>();
	
	for (Long poller_link_id : deleteSet) {
	    list.add(new Object[]{
		    poller_link_id
	    });
	}
	
	jdbcTemplate.batchUpdate(stmt, list);
    }

    public HashMap<Long, Long> getDevsettingPollerIdPointRowIdMap() {
	String stmt = "SELECT poller_link_id, id FROM voiceco.poller_devsetting_new";
	HashMap<Long, Long> map = new HashMap<>();

	SqlRowSet rows = jdbcTemplate.queryForRowSet(stmt);

	while (rows.next()) {
	    map.put(rows.getLong("poller_link_id"), rows.getLong("id"));
	}

	// reuslt display
	System.out.println(map);

	return map;
    }

}
