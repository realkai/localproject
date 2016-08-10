package com.wiline.snmp;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import com.wiline.config.VarSetting;
import com.wiline.util.TimeUtil;

public class MySnmp {

    private static String port = VarSetting.DEF_SNMP_PORT;
    private static long time_out = VarSetting.DEF_SNMP_TIME_OUT;
    private static int retries = VarSetting.DEF_SNMP_RETRIES;

    private ConcurrentHashMap<Integer, ConcurrentHashMap<String, String>> pollerLinkMap;
    private String mode;
    private Integer rfid;

    public MySnmp() {
    }

    public MySnmp(ConcurrentHashMap<Integer, ConcurrentHashMap<String, String>> pollerLinkMap, Integer rfid,
	    String mode) {
	this.pollerLinkMap = pollerLinkMap;
	this.mode = mode;
	this.rfid = rfid;
    }

    public void snmpGET(String ip, String oid) {
	String[] communities = { "public", "sc00ter" };
	int versions[] = { SnmpConstants.version2c, SnmpConstants.version1 };

	for (String community : communities) {
	    for (int version : versions) {
		if (snmpGET(ip, community, version, oid)) {
		    break;
		}
	    }
	}
    }

    public boolean snmpGET(String ip, String community, int version, String oid) {

	CommunityTarget target = createCommunity(ip, community, version);
	Snmp snmp = null;
	boolean isDone = false;
	try {
	    PDU pdu = new PDU();
	    pdu.add(new VariableBinding(new OID(oid)));
	    DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
	    snmp = new Snmp(transport);
	    snmp.listen();

	    pdu.setType(PDU.GET);
	    ResponseEvent respEvent = snmp.send(pdu, target);

	    PDU resp = respEvent.getResponse();

	    if (resp == null) {
		return isDone;
	    } else {

		String last_response = TimeUtil.getCurrentTime();

		String versionString = null;
		if (version == 0) {
		    versionString = "v1";
		} else if (version == 1) {
		    versionString = "v2c";
		}

		System.out.println("----version = " + versionString + ", community = " + community
			+ "\n	PeerAddress: " + respEvent.getPeerAddress() + "\n	" + resp.get(0).getVariable());

		String response_text = String.valueOf(resp.get(0).getVariable());

		if ("src_radio_ip".equals(mode)) {
		    pollerLinkMap.get(rfid).put("response_a", response_text);
		    pollerLinkMap.get(rfid).put("last_response_a", last_response);
		    pollerLinkMap.get(rfid).put("community_a", community);
		    pollerLinkMap.get(rfid).put("snmp_version_a", versionString);
		}

		if ("dst_radio_ip".equals(mode)) {
		    pollerLinkMap.get(rfid).put("response_z", response_text);
		    pollerLinkMap.get(rfid).put("last_response_z", last_response);
		    pollerLinkMap.get(rfid).put("community_z", community);
		    pollerLinkMap.get(rfid).put("snmp_version_z", versionString);
		}

		// to_scan should be boolean
		pollerLinkMap.get(rfid).put("to_scan", "true");
		pollerLinkMap.get(rfid).put("mib", oid);

		isDone = true;
	    }

	    System.out.println();

	} catch (Exception e) {
	    e.printStackTrace();
	    // System.exit(0);
	} finally {
	    if (snmp != null) {
		try {
		    snmp.close();
		} catch (IOException e) {
		    e.printStackTrace();
		    snmp = null;
		}
	    }
	}
	return isDone;

    }

    public static CommunityTarget createCommunity(String ip, String community, int version) {
	Address address = GenericAddress.parse(ip + port);
	CommunityTarget target = new CommunityTarget();
	target.setCommunity(new OctetString(community));
	target.setAddress(address);
	target.setVersion(version);
	target.setTimeout(time_out);
	target.setRetries(retries);
	return target;
    }

}
