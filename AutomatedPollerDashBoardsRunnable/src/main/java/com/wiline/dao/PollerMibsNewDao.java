package com.wiline.dao;

import java.util.HashMap;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional
public class PollerMibsNewDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public HashMap<String, HashSet<String>> getMibPrefixMap() {
	String stmt_ub = "select mib_prefix from voiceco.poller_mibs where description like 'Ubiquiti%'";
	String stmt_sk = "select mib_prefix from voiceco.poller_mibs where description like 'Siklu%'";

	HashMap<String, HashSet<String>> map = new HashMap<>();

	{
	    SqlRowSet rows_ub = jdbcTemplate.queryForRowSet(stmt_ub);
	    HashSet<String> set = new HashSet<>();
	    while (rows_ub.next()) {
		set.add(rows_ub.getString("mib_prefix"));
	    }
	    map.put("Ubiquiti", set);
	}

	{
	    SqlRowSet rows_sk = jdbcTemplate.queryForRowSet(stmt_sk);
	    HashSet<String> set = new HashSet<>();
	    while (rows_sk.next()) {
		set.add(rows_sk.getString("mib_prefix"));
	    }
	    map.put("Siklu", set);
	}
	
	// result display
	System.out.println(map);

	return map;
    }

    public HashMap<Long, HashMap<String, String>> fetchDataMap() {
	String stmt = "select * from voiceco.poller_mibs";

	HashMap<Long, HashMap<String, String>> map = new HashMap<>();

	SqlRowSet rows = jdbcTemplate.queryForRowSet(stmt);
	HashMap<String, String> temp;
	
	while (rows.next()) {
	    temp = new HashMap<>();
	    map.put(rows.getLong("id"), temp);
	    temp.put("mib", rows.getString("mib"));
	    temp.put("mib_prefix", rows.getString("mib_prefix"));
	    temp.put("description", rows.getString("description"));
	}
	
	// result display
	for (Long id : map.keySet()) {
	    System.out.println(map.get(id));
	}
	
	return map;
    }

}
