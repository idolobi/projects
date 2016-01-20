package com.es.deployer.ftp;

public class ServerInfo {
	private String ip;
	private int port;
	private String id;
	private String password;
	
	public ServerInfo() {}
	
	public ServerInfo(String ip, int port, String id, String password) {
		this.ip = ip;
		this.port = port;
		this.id = id;
		this.password = password;
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public String getId() {
		return id;
	}

	public String getPassword() {
		return password;
	}
}
