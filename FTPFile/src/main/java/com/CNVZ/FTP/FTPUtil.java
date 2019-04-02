package com.CNVZ.FTP;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

public class FTPUtil {
	
	private static Logger logger = Logger.getLogger(FTPUtil.class);
	
	private static FTPClient client;
	
	public static FTPClient getClient() {
		return client;
	}
	
	//get ftp connection
	public static boolean connectFTP(FTP server) throws Exception {
		client = new FTPClient();
		boolean flag = false;
		int reply;
		
		client.setControlEncoding("UTF-8");
		if(server.getPort() != null) {
			client.connect(server.getIpAddr(), 21);
		} else {
			client.connect(server.getIpAddr(), server.getPort());
		}
		
		client.login(server.getUserName(), server.getPwd());
		client.setFileType(FTPClient.BINARY_FILE_TYPE);
		
		reply = client.getReplyCode();
		
		if(!FTPReply.isPositiveCompletion(reply)) {
			client.disconnect();
			logger.error("FTP Connection Failure");
			return flag;
		}
		
		client.enterLocalPassiveMode();
		client.changeWorkingDirectory(server.getPath());
		flag = true;
		logger.info("FTP Connection Successfully");
		
		return flag;
	}
	
	//close ftp connection
	public static void closeFTP() {
		if(client!=null && client.isConnected()) {
			try {
				client.logout();
				client.disconnect();
				client = null;
				logger.info("Close Successfully");
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}
	
	//upload file recursively
	public static void uploadFile(File file) throws Exception {
			if(file.isDirectory()) {
				client.makeDirectory(file.getName());
				client.changeWorkingDirectory(file.getName());
				File ftmp;
				String[] files = file.list();
				for(String str : files) {
					ftmp = new File(file.getPath() + "/" + str);
					uploadFile(ftmp);
					if(ftmp.isDirectory()) {
						client.changeToParentDirectory();
					}
				}
			} else {
				File ftmp = new File(file.getPath());
				InputStream fis = new FileInputStream(ftmp);
				String remotePath = new String(ftmp.getName().getBytes("UTF-8"),"iso-8859-1");
				if(client.storeFile(remotePath, fis)) {
					logger.info("Upload SuccessFully");
				} else {
					logger.error("Upload Failure");
				}
				fis.close();
			}
	}
	
	//download configuration
	public static void startDown(String localBaseDir, String serverBaseDir) throws Exception {
		try {
			FTPFile[] files = null;
			boolean changeDir = client.changeWorkingDirectory(serverBaseDir);
			if(changeDir) {
				client.setControlEncoding("UTF-8");
				files = client.listFiles();
				for(int i=0; i<files.length; i++) {
					try {
						downloadFile(files[i], localBaseDir + "/");
					} catch (Exception e) {
						logger.error(e);
						logger.error("<" + files[i].getName() + "> Download Failure!");
					}
				}
			}
		} catch (Exception e) {
			logger.error(e);
			logger.error("No Such Directory In Server");
		}
	}
	
	//download file recursively
	public static void downloadFile(FTPFile file, String localFilePath) {
		if(file.isDirectory()) {
			String newLocalRelatePath = localFilePath + file.getName();
			File ftmp = new File(newLocalRelatePath);
			if(!ftmp.exists()) {
				ftmp.mkdir();
			}
			try {
				newLocalRelatePath = newLocalRelatePath + "/";
				String currentDir = file.getName();
				boolean changeDir = client.changeWorkingDirectory(currentDir);
				if(changeDir) {
					FTPFile[] files = null;
					files = client.listFiles();
					for(int i=0; i<files.length; i++) {
						downloadFile(files[i], newLocalRelatePath);
					}
					client.changeToParentDirectory();
				}
			} catch (Exception e) {
				logger.error(e);
			}
		} else {
			if(file.getName().indexOf("?") == -1) {
				OutputStream fos = null;
				try {
					File localFile = new File(localFilePath + file.getName());
					//if local machine has the file, then do nothing
					if(localFile.exists()) {
						return;
					} else {
						fos = new FileOutputStream(localFile);
						client.retrieveFile(file.getName(), fos);
						fos.flush();
						fos.close();
					}
				} catch (Exception e) {
					logger.error(e);
				} finally {
					try {
						if(fos != null) {
							fos.close();
						}
					} catch (Exception e) {
						logger.error("Output Stream Exception!");
					}
				}
			}
		}
	}
}
