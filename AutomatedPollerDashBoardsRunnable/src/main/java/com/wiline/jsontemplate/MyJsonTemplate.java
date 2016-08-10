package com.wiline.jsontemplate;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.wiline.util.TimeUtil;

public class MyJsonTemplate {

    public static void main(String[] args) throws IOException {
	Charset charset = StandardCharsets.UTF_8;
	String content = new String(Files.readAllBytes(Paths.get("siklu.json")), charset);
	getSendingContent(content, charset, Integer.valueOf(144), "100", "100", "100", "100", "100", "100", "100",
		"100", "100", "100", "100", "100", "100", "100", "100");
    }

    public static byte[] getSendingContent(String content, Charset charset, Integer rfid, String distanct_mi, String a,
	    String z, String at, String zt, String a_netname, String z_netname, String a_address, String z_address,
	    String a_lat, String a_log, String z_lat, String z_log, String a_host, String z_host) {

	// Path dst = Paths.get("test_siklu.json");

	if (content != null) {

	    content = content.replaceAll("DASHBOARDNAME", "KAITEST-" + rfid + "-STATS");
	    content = content.replaceAll("LINKDISTANCE", distanct_mi);
	    content = content.replaceAll("RFIDA", rfid.toString());

	    content = content.replaceAll("CREATEDATE", TimeUtil.getCurrentTimeUTCwithZone());

	    content = content.replaceAll("A_IP", a);
	    content = content.replaceAll("Z_IP", z);

	    content = content.replaceAll("A_THRESHOLD", at);
	    content = content.replaceAll("Z_THRESHOLD", zt);

	    content = content.replaceAll("A_NETNAME", a_netname);
	    content = content.replaceAll("Z_NETNAME", z_netname);

	    content = content.replaceAll("A_ADDRESS", a_address);
	    content = content.replaceAll("Z_ADDRESS", z_address);

	    content = content.replaceAll("A1", a_lat);
	    content = content.replaceAll("Z1", a_log);

	    content = content.replaceAll("A2", z_lat);
	    content = content.replaceAll("Z2", z_log);

	    content = content.replaceAll("A_HOST", a_host);
	    content = content.replaceAll("Z_HOST", z_host);

	    // Files.write(dst, content.getBytes(charset));

	    return content.getBytes(charset);
	}
	return new byte[1];
    }

}
