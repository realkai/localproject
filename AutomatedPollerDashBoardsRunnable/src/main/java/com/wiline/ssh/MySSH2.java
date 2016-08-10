package com.wiline.ssh;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.stereotype.Service;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

@Service
public class MySSH2 {

    private Connection connection;
    private Session session;

    public Connection startConnection(String host, String user, String pswd) {
	try {
	    connection = new Connection(host);
	    connection.connect();
	    boolean isAuthenticated = connection.authenticateWithPassword(user, pswd);

	    if (isAuthenticated == false) {
		System.out.println(" authenticat fails ");
		return null;
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	}
	return connection;
    }

    public void transferCSVToServer(String user, String host, String pswd) {
	MySSH2 myssh = new MySSH2();
	Connection con = myssh.startConnection(host, user, pswd);
	SCPClient client = new SCPClient(con);
	File file_ub = new File("Ubiquiti.csv");
	File file_sk = new File("Siklu.csv");

	System.out.println("Ubiquiti size: " + file_ub.length() + ", Siklu size: " + file_sk.length());
	try {
	    client.put("Ubiquiti.csv", "/home/dublin/dev-test", "0644");
	    client.put("Siklu.csv", "/home/dublin/dev-test", "0644");
	    System.out.println("CSV transfer Complete !");

	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public static void main(String[] args) {
	String host = "67.207.98.242";
	String user = "kzhang";
	String pswd = "kai310?905:121";

	MySSH2 ms = new MySSH2();
	Connection con = ms.startConnection(host, user, pswd);
	ms.sendCommand("bash /home/dublin/dev-test/run_me.sh");
	ms.closeSession();
	System.out.println(con);
	// SCPClient client = new SCPClient(con);
	// File file_ub = new File("Ubiquiti.csv");
	// File file_sk = new File("Siklu.csv");
	//
	// System.out.println(file_ub.length() + ", " + file_sk.length());
	// try {
	// client.put("Ubiquiti.csv", "/home/dublin/dev-test", "0644");
	// client.put("Siklu.csv", "/home/dublin/dev-test", "0644");
	// System.out.println("Complete !");
	//
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
    }

    public void sendCommand(String command) {
	try {
	    session = connection.openSession();
	    session.execCommand(command);

	    InputStream stout = new StreamGobbler(session.getStdout());
	    BufferedReader br = new BufferedReader(new InputStreamReader(stout));
	    String line;
	    while ((line = br.readLine()) != null) {
		System.out.println(line);
	    }
	    br.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public void closeSession() {
	session.close();
	System.out.println("Exit: " + session.getExitStatus());
    }

}
