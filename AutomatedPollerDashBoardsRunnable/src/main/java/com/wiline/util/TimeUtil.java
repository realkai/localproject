package com.wiline.util;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.TimeZone;

public class TimeUtil {

    public static void main(String[] args) {
	String d = "2016-08-10 10:29:24.9853";
	System.out.println(isNeededToUpdate(d));
    }

    // true if within 12h, else false
    public static boolean isNeededToUpdate(String timeString) {
	if (timeString != null && !"".equals(timeString)) {
	    String[] temp = timeString.split("\\.");
	    int num_of_s = temp[1].split("\\+")[0].length();
	    String part = "";
	    for (int i = 0; i < num_of_s; i++) {
		part += "S";
	    }
	    String pattern = "yyyy-MM-dd HH:mm:ss." + part;
	    DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
	    LocalDateTime ldt = LocalDateTime.parse(timeString, dtf);

	    Duration dur = Duration.between(ldt,
		    ZonedDateTime.now().toLocalDateTime());
	    return dur.getSeconds() < 14 * 3600;
	}

	return false;
    }

    public static Calendar getCalendarUTC() {
	return Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    }

    public static String getCurrentTimeUTC() {
	LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
	return now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
    }
    
    public static String getCurrentTime() {
	LocalDateTime now = LocalDateTime.now();
	return now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
    }

    public static String getCurrentTimeUTCwithZone() {
	ZonedDateTime zdt = getZonedDateTime();
	String zdtime = zdt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssZ"));
	return zdtime;
    }

    public static Timestamp getCurrentTimestamp() {
	return Timestamp.valueOf(getTimeWithTimeZone());
    }

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

}
