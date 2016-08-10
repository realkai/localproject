package com.wiline;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

@Repository
public class TestPollerMibsDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void show() {
	String stmt = "select * from voiceco.poller_mibs";
	SqlRowSet rows = jdbcTemplate.queryForRowSet(stmt);
	while (rows.next()) {
	    System.out.println(rows.getString("id") + "\t" + rows.getString("mib"));
	}
    }

    public void insert_3() {
	String stmt = "insert into voiceco.poller_mibs_new(mib,mib_prefix,description) " + "values ('123','123','123')";
	jdbcTemplate.update(stmt);
    }

    public void insert_1() {
	String stmt = "INSERT INTO voiceco.poller_mibs_new (mib,mib_prefix,description) " + "VALUES (?,?,?)";

	List<Object[]> list = new ArrayList<>();
	for (int i = 0; i < 20; i++) {
	    list.add(new Object[] { "12313123", "qweqet", "qweqeqweqwe" });
	}

	jdbcTemplate.batchUpdate(stmt, list);

    }

    public void insert_2() {
	String stmt = "INSERT INTO voiceco.poller_mibs_new (mib,mib_prefix,description) " + "VALUES (?,?,?)";

	jdbcTemplate.batchUpdate(stmt, new BatchPreparedStatementSetter() {

	    @Override
	    public void setValues(PreparedStatement ps, int i) {
		// try {
		// ps.setString(1, temp.getMibs());
		// ps.setString(2, temp.getMib_prefix());
		// ps.setString(3, temp.getDescription());
		// }catch (Exception e) {
		// e.printStackTrace();
		// }
	    }

	    @Override
	    public int getBatchSize() {
		return 30;
	    }
	});

    }
}
