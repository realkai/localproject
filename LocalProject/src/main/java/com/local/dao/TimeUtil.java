package com.local.dao;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtil {

	private static String getTimeWithTimeZone() {

		ZonedDateTime zdt = getZonedDateTime();

		String zdtime = zdt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSS"));
		
		return zdtime;
	}
	
	private static ZonedDateTime getZonedDateTime() {
		LocalDateTime dt = LocalDateTime.now();

		ZoneId zone = ZoneId.of("UTC");

		return dt.atZone(zone);
	}
	
	public static Calendar getCalendarUTC() {
		return Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	}
	
	public static String getCurrentTimeUTC() {
		LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
		return now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
	}
	
	public static long getTimeUTC() {
		return getZonedDateTime().toEpochSecond();
	}
	

}
