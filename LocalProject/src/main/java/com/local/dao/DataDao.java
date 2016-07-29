package com.local.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class DataDao {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	public void batchinsert2() {
		String sql = "insert into voiceco.poller_links_new "
				+ "(rfid,last_response_a,last_response_z,response_a,response_z,modifydate) "
				+ "values (?,?,?,?,?,?)";
		
		BatchPreparedStatementSetter pss = new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				// TODO Auto-generated method stub
				ps.setInt(1, new Integer(5421));
				ps.setTimestamp(2, Timestamp.valueOf(TimeUtil.getCurrentTimeUTC()), TimeUtil.getCalendarUTC());
//				ps.setDate(2, , TimeUtil.getCalendarUTC());
				ps.setTimestamp(3, Timestamp.valueOf(TimeUtil.getCurrentTimeUTC()), TimeUtil.getCalendarUTC());
				ps.setString(4, "ubiquiti");
				ps.setString(5, "siklu");
				ps.setTimestamp(6, Timestamp.valueOf(TimeUtil.getCurrentTimeUTC()), TimeUtil.getCalendarUTC());
			}
			
			@Override
			public int getBatchSize() {
				// TODO Auto-generated method stub
				return 1844;
			}
		};
		
		jdbcTemplate.batchUpdate(sql, pss);
	}		
	
	public void insert() {
		String sql = "insert into voiceco.poller_links_new "
				+ "(rfid,last_response_a,last_response_z,response_a,response_z,modifydate) "
				+ "values (?,?,?,?,?,?)";
		
		for(int i=0; i<1844;i++) {
			jdbcTemplate.update(sql, 
					new Integer(8686),
					Timestamp.valueOf(TimeUtil.getCurrentTimeUTC()),
					Timestamp.valueOf(TimeUtil.getCurrentTimeUTC()),
					"Ubiquiti",
					"Siklu",
					Timestamp.valueOf(TimeUtil.getCurrentTimeUTC())
					);
		}
	}
	
	@Deprecated
	public void batchinsert() {
		String sql = "insert into voiceco.poller_links_new "
				+ "(rfid,last_response_a,last_response_z,response_a,response_z,modifydate) "
				+ "values (?,?,?,?,?,?)";
		
		List<Object[]> list = new ArrayList<>(); 
		
		try {
			for(int i=0; i<1844;i++) {
				list.add(new Object[] {
						new Integer(5432),
						new Timestamp(TimeUtil.getTimeUTC()),
						new Timestamp(TimeUtil.getTimeUTC()),
						"Ubiquiti",
						"Siklu",
						new Timestamp(TimeUtil.getTimeUTC()),
				});
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.exit(0);
		}
		
		jdbcTemplate.batchUpdate(sql, list);
	}

}
