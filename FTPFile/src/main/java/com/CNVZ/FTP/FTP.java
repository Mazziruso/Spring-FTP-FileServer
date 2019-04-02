package com.CNVZ.FTP;

public class FTP {
	
	private String ipAddr;
	private Integer port;
	private String userName;
	private String pwd;
	private String path;
	
	public String getIpAddr() {
		return ipAddr;
	}
	
	public Integer getPort() {
		return port;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public String getPwd() {
		return pwd;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}
	
	public void setPort(Integer port) {
		this.port = port;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	
	public void setPath(String path) {
		this.path = path;
	}

}
