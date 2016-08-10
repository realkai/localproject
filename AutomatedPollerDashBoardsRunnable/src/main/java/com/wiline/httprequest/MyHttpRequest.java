package com.wiline.httprequest;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.springframework.stereotype.Service;

import com.wiline.config.VarSetting;
import com.wiline.jsontemplate.MyJsonTemplate;

@Service
public class MyHttpRequest {

    public static void main(String[] args) {
	// createDashboard();
	// deleteDashboard(8193);
    }

    public static void sendPost(String body, Integer rfid, boolean isRedo, ConcurrentSkipListSet<Integer> remainSet) {
	if (remainSet.isEmpty() && isRedo)
	    return;

	String urlString = "http://67.207.98.242:3000/api/dashboards/db";
	try {
	    URL url = new URL(urlString);
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    conn.setRequestMethod("POST");
	    conn.setConnectTimeout(VarSetting.HTTP_TIMEOUT);
	    conn.setDoOutput(true);
	    conn.setRequestProperty("Content-Type", "application/json");
	    conn.setRequestProperty("Authorization",
		    "Bearer eyJrIjoiODFnWjNRMmQybVBwQTVIYVVWbW53a01LN3UzQzRWZmwiLCJuIjoidGVzdCIsImlkIjoxfQ==");

	    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
	    wr.writeBytes(body);
	    wr.close();

	    Thread.currentThread();
	    Thread.sleep(VarSetting.HTTP_REQ_WAIT_TIME);

	    switch (conn.getResponseCode()) {
	    case 200:
		if (isRedo) {
		    remainSet.remove(rfid);
		    System.out.println(remainSet);
		} else {
		    remainSet.add(rfid);
		}
		printResponse(conn);
		break;
	    case 412:
		if (isRedo) {
		    remainSet.remove(rfid);
		    System.out.println(remainSet);
		}
		System.out.println("Record is already there!");
		break;
	    default:
		System.out.println("RemainSet: " + remainSet);
		System.out.println(conn.getResponseCode() + ": " + conn.getResponseMessage());
		break;
	    }

	    conn.disconnect();
	} catch (SocketTimeoutException ste) {
	    System.out.println(rfid + " is timeout in dashboard inserting, please have a look!");
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public static void deleteDashboard(Integer rfid, boolean isRedo, ConcurrentSkipListSet<Integer> remainSet) {
	String urlString = "http://67.207.98.242:3000/api/dashboards/db/kaitest-" + rfid + "-stats";

	if (remainSet.isEmpty() && isRedo)
	    return;

	try {

	    URL url = new URL(urlString);
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    conn.setRequestMethod("DELETE");
	    conn.setRequestProperty("Authorization",
		    "Bearer eyJrIjoiODFnWjNRMmQybVBwQTVIYVVWbW53a01LN3UzQzRWZmwiLCJuIjoidGVzdCIsImlkIjoxfQ==");

	    conn.setConnectTimeout(VarSetting.HTTP_TIMEOUT);

	    Thread.currentThread();
	    Thread.sleep(VarSetting.HTTP_REQ_WAIT_TIME);

	    switch (conn.getResponseCode()) {
	    case 404:
		if (isRedo) {
		    remainSet.remove(rfid);
		    System.out.println(remainSet);
		}
		System.out.println("Target link is not exist in Grafana !");
		break;
	    case 200:
		printResponse(conn);

		if (isRedo) {
		    remainSet.remove(rfid);
		    System.out.println(remainSet);
		} else {
		    remainSet.add(rfid);
		}
		break;
	    default:
		System.out.println("RemainSet: " + remainSet);
		break;
	    }

	    conn.disconnect();

	} catch (SocketTimeoutException ste) {
	    System.out.println(rfid + " is timeout in dashboard deleting, please have a look!");

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private static void printResponse(HttpURLConnection conn) {

	try (InputStream in = conn.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
	    String line;
	    while ((line = br.readLine()) != null) {
		System.out.println(conn.getResponseCode() + ": " + line);
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}

    }

    // this is a test method
    public static void createDashboard(ConcurrentHashMap<Integer, ConcurrentHashMap<String, String>> sikluDashMap,
	    Integer re_rfid, boolean isRedo, ConcurrentSkipListSet<Integer> remainSet) {
	Charset charset = StandardCharsets.UTF_8;
	Path template = Paths.get(VarSetting.SIKLU_TEMPLATE_LOCATION);
	String content = null;

	try {
	    content = new String(Files.readAllBytes(template), charset);
	} catch (IOException e1) {
	    e1.printStackTrace();
	    System.exit(0);
	}

	for (Integer rfid : sikluDashMap.keySet()) {
	    String body = new String(
		    MyJsonTemplate.getSendingContent(content, charset, rfid, sikluDashMap.get(rfid).get("distance_mi"),
			    sikluDashMap.get(rfid).get("a"), sikluDashMap.get(rfid).get("z"),
			    sikluDashMap.get(rfid).get("at"), sikluDashMap.get(rfid).get("zt"),
			    sikluDashMap.get(rfid).get("a_netname"), sikluDashMap.get(rfid).get("z_netname"),
			    sikluDashMap.get(rfid).get("a_address"), sikluDashMap.get(rfid).get("z_address"),
			    sikluDashMap.get(rfid).get("a_lat"), sikluDashMap.get(rfid).get("a_log"),
			    sikluDashMap.get(rfid).get("z_lat"), sikluDashMap.get(rfid).get("z_log"),
			    sikluDashMap.get(rfid).get("a_host"), sikluDashMap.get(rfid).get("z_host")),
		    charset);
	    sendPost(body, re_rfid, isRedo, remainSet);
	}
    }

}
