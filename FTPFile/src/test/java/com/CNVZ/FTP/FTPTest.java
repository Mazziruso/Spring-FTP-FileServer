package com.CNVZ.FTP;

import java.io.File;

import org.apache.commons.net.ftp.FTPClient;

public class FTPTest {
	
	public static void main(String[] args) throws Exception {
		FTP server = new FTP();
		server.setIpAddr("192.168.20.132");
		server.setPort(21);
		server.setUserName("ZK");
		server.setPwd("1234");
		server.setPath("data");
		
		File file = new File("/home/rootu/Java/Workspace");
		
		FTPUtil.connectFTP(server);
		FTPUtil.startDown("/home/rootu/Java/FTPDownload", "Workspace/FTPFile");
		FTPUtil.closeFTP();
		
	}

}
