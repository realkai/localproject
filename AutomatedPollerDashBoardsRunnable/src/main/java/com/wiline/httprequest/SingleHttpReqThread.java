package com.wiline.httprequest;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentSkipListSet;

import com.wiline.jsontemplate.MyJsonTemplate;

public class SingleHttpReqThread implements Runnable {

	public final static Charset charset = StandardCharsets.UTF_8;
	private String content;
	private Integer rfid;
	private String distanct_mi;
	private String a;
	private String z;
	private String at;
	private String zt;
	private String a_netname;
	private String z_netname;
	private String a_address;
	private String z_address;
	private String a_lat;
	private String a_log;
	private String z_lat;
	private String z_log;
	private String a_host;
	private String z_host;
	private String mode;
	private boolean isRedo;
	private ConcurrentSkipListSet<Integer> remainSet;

	public SingleHttpReqThread() {
		// TODO Auto-generated constructor stub
	}
	
	public SingleHttpReqThread(Integer rfid, String mode, boolean isRedo, ConcurrentSkipListSet<Integer> remainSet) {
		this.rfid = rfid;
		this.mode = mode;
		this.isRedo = isRedo;
		this.remainSet = remainSet;
	}

	public SingleHttpReqThread(String content, Integer rfid, String distanct_mi, String a, String z, String at, String zt,
			String a_netname, String z_netname, String a_address, String z_address, String a_lat, String a_log,
			String z_lat, String z_log, String a_host, String z_host, String mode, boolean isRedo, ConcurrentSkipListSet<Integer> remainSet) {
		super();
		this.content = content;
		this.rfid = rfid;
		this.distanct_mi = distanct_mi;
		this.a = a;
		this.z = z;
		this.at = at;
		this.zt = zt;
		this.a_netname = a_netname;
		this.z_netname = z_netname;
		this.a_address = a_address;
		this.z_address = z_address;
		this.a_lat = a_lat;
		this.a_log = a_log;
		this.z_lat = z_lat;
		this.z_log = z_log;
		this.a_host = a_host;
		this.z_host = z_host;
		this.mode = mode;
		this.isRedo = isRedo;
		this.remainSet = remainSet;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if ("create".equals(mode)) {
			byte[] btcontent = MyJsonTemplate.getSendingContent(content, charset, rfid, distanct_mi, a, z, at, zt, a_netname, z_netname,
					a_address, z_address, a_lat, a_log, z_lat, z_log, a_host, z_host);
			String content = new String(btcontent, charset);
			MyHttpRequest.sendPost(content, rfid, isRedo, remainSet);
		}
		
		if ("delete".equals(mode)) {
			MyHttpRequest.deleteDashboard(rfid, isRedo, remainSet);
		}
		
	}

}
