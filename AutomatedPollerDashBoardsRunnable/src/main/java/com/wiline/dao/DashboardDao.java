package com.wiline.dao;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class DashboardDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public ConcurrentHashMap<Integer, ConcurrentHashMap<String, String>> getSikluFullInfoMap() {
	String stmt = "select * from (" + "select bl.distance_mi,rf.rfid,il.src_radio_ip as a,il.dst_radio_ip as z,"
		+ "case when rf.actual_rsl_a is null then 0 else rf.actual_rsl_a end as at,"
		+ "case when rf.actual_rsl_z is null then 0 else rf.actual_rsl_z end as zt,"
		+ "b1.netname as a_netname, b2.netname as z_netname," + "b1.address1 || ' ' || b1.city as a_address,"
		+ "b2.address1 || ' ' || b2.city as z_address," + "b1.latitude as a_lat,b1.longitude as a_log,"
		+ "b2.latitude as z_lat,b2.longitude as z_log,"
		+ "replace((json_array_elements(irl.src_attributes::json)->'hostName')::text,'\"','') as a_host,"
		+ "replace((json_array_elements(irl.dst_attributes::json)->'hostName')::text,'\"','') as z_host "
		+ "from buildingrf rf " + "inner join buildinglink bl on bl.lid=rf.lid "
		+ "inner join building b1 on b1.bid=bl.srcbid " + "inner join building b2 on b2.bid=bl.dstbid "
		+ "left outer join voiceco.icinga_links irl on irl.rfid=rf.rfid "
		+ "join voiceco.icinga_links il on il.rfid = rf.rfid "
		+ "join voiceco.poller_links_new pl on pl.rfid = il.rfid " + "where manufacturer_a in ('Siklu') "
		+ "and (pl.response_a != 'FAILED' and pl.response_z != 'FAILED') ) as fff ";

	ConcurrentHashMap<Integer, ConcurrentHashMap<String, String>> map = new ConcurrentHashMap<>();

	SqlRowSet rows = jdbcTemplate.queryForRowSet(stmt);

	ConcurrentHashMap<String, String> temp;
	int counter = 0;

	while (rows.next()) {

	    temp = new ConcurrentHashMap<>();
	    map.put(rows.getInt("rfid"), temp);
	    temp.put("distance_mi", String.valueOf(rows.getDouble("distance_mi")));
	    temp.put("a", rows.getString("a"));
	    temp.put("z", rows.getString("z"));
	    temp.put("at", String.valueOf(rows.getDouble("at")));
	    temp.put("zt", String.valueOf(rows.getDouble("zt")));
	    temp.put("a_netname", rows.getString("a_netname"));
	    temp.put("z_netname", rows.getString("z_netname"));
	    temp.put("a_address", rows.getString("a_address"));
	    temp.put("z_address", rows.getString("z_address"));
	    temp.put("a_lat", String.valueOf(rows.getDouble("a_lat")));
	    temp.put("a_log", String.valueOf(rows.getDouble("a_log")));
	    temp.put("z_lat", String.valueOf(rows.getDouble("z_lat")));
	    temp.put("z_log", String.valueOf(rows.getDouble("z_log")));
	    temp.put("a_host", rows.getString("a_host"));
	    temp.put("z_host", rows.getString("z_host"));
	    counter++;

	}
	
	// result display
	System.out.println("Find records: " + counter);

//	for (Integer rfid : map.keySet()) {
//	    System.out.println(map.get(rfid));
//	}

	return map;
    }

}
