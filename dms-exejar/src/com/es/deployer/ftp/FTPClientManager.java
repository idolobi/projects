package com.es.deployer.ftp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

import com.es.deployer.file.FileCopyEntry;

public class FTPClientManager {
	private static Logger logger = Logger.getLogger(FTPClientManager.class);
	
	/**
	 * 원격지 파일을 로컬로 다운로드 함
	 * @param svrInfo
	 * @param entries
	 * @param isForcedOverwrite
	 * @return
	 * @throws IOException
	 */
	public static int download(ServerInfo svrInfo, List<FileCopyEntry> entries, boolean isForcedOverwrite) 
			throws IOException {
		
		FTPClient ftp = new FTPClient();
		ftp = config(ftp);
		if(!connect(ftp, svrInfo)) {
			logger.error("connect fail.");
		}
		if(!login(ftp, svrInfo)) {
			disconnect(ftp);
		}
		ftp.enterLocalPassiveMode();
		
		int cnt = 0;
		for(int i = 0; i < entries.size(); i++) {
			String fromPath = entries.get(i).getFromPath(); // local path
			String toPath = entries.get(i).getToPath(); // remote path
			
			setFileTransferType(ftp, toPath.substring(toPath.lastIndexOf('.')+1));
			
			String toPathDir = toPath.substring(0, toPath.lastIndexOf('/'));
			String toPathFile = toPath.substring(toPath.lastIndexOf('/')+1);
			
			if (ftp.changeWorkingDirectory(toPathDir)) {
				File dir = new File(fromPath.substring(0, fromPath.lastIndexOf('/')));
				if(!dir.isDirectory()) {
					dir.mkdirs();
				}
				File localFile = new File(fromPath);
				if((!localFile.isFile()) || (localFile.isFile() && isForcedOverwrite)) {
					FileOutputStream fos = new FileOutputStream(localFile);
					if(ftp.retrieveFile(toPathFile, fos)) {
						cnt++;
						System.out.println("★ 다운로드(" + cnt + ") : " + toPathFile);
					} else {
						System.out.println("★ 파일 없음 : " + toPathFile);
					}
					fos.close();
					checkZeroByte(localFile);
				}
			} else {
				System.out.println("★ 디렉토리 없음 : " + toPathDir);
			}
		}
		
		logout(ftp);
		disconnect(ftp);
		
		return cnt;
	}
	
	/**
	 * 
	 * @param ftp
	 * @return
	 */
	private static FTPClient config(FTPClient ftp) {
		FTPClientConfig config = new FTPClientConfig(FTPClientConfig.SYST_UNIX);
		
		config.setServerLanguageCode("ko");
		config.setDefaultDateFormatStr("yyyy년 M월 d일");
		config.setRecentDateFormatStr("M월 d일 HH:mm");
		
		ftp.configure(config);
		
		ftp.setControlEncoding("euc-kr");
		return ftp;
	}
	
	/**
	 * 
	 * @param ftp
	 * @param server
	 * @return
	 */
	private static boolean connect(FTPClient ftp, ServerInfo svrInfo) {
		boolean isConnect = false;
		int reply = 0;
		try {
			ftp.connect(svrInfo.getIp(), svrInfo.getPort());
			reply = ftp.getReplyCode();
			if(FTPReply.isPositiveCompletion(reply)) {
				isConnect = true;
			} else {
				ftp.disconnect();
				isConnect = false;
				logger.error("error : connect fail.");
			}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return isConnect;
	}
	
	/**
	 * 
	 * @param ftp
	 */
	private static void disconnect(FTPClient ftp) {
		try {
			if(ftp.isConnected()) {
				ftp.disconnect();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param ftp
	 * @param server
	 * @return
	 */
	private static boolean login(FTPClient ftp, ServerInfo svrInfo) {
		boolean isLogin = false;
		String userId = svrInfo.getId();
		String userPwd = svrInfo.getPassword();
		if(userId.length() < 0 || userPwd.length() < 0) {
			logger.error("login error.");
		}
		try {
			if(ftp.login(userId, userPwd)) {
				isLogin = true;
			} else {
				isLogin = false;
				ftp.disconnect();
				logger.error("login fail.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return isLogin;
	}
	
	/**
	 * 
	 * @param ftp
	 * @return
	 */
	private static boolean logout(FTPClient ftp) {
		boolean isLogout = false;
		try {
			if(ftp.logout()) {
				isLogout = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return isLogout;
	}
	
	/**
	 * 
	 * @param ftp
	 * @param extension
	 */
	private static void setFileTransferType(FTPClient ftp, String extension) {
		String[] binaryExt = {"class", "jar", "cab", "swf", "gif", "jpg", "png"};
		String[] asciiExt = {"html", "htm", "jsp", "properties", "xml", "js", "css"};
		try {
			if (matchExtension(binaryExt, extension)) {
				ftp.setFileType(FTP.BINARY_FILE_TYPE);
			} else if(matchExtension(asciiExt, extension)) {
				ftp.setFileType(FTP.ASCII_FILE_TYPE);
			} else {
				ftp.setFileType(FTP.BINARY_FILE_TYPE);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param exts
	 * @param ext
	 * @return
	 */
	private static boolean matchExtension(String[] exts, String ext) {
		boolean isMatch = false;
		for(int i = 0; i < exts.length; i++) {
			if(exts[i].equals(ext)) {
				isMatch = true;
			}
		}
		return isMatch;
	}
	
	/**
	 * 0byte 파일 생성 방지
	 * @param file
	 */
	private static void checkZeroByte(File file) {
		if(file.exists()) {
			if(file.length() == 0) {
				file.delete();
			}
		}
	}
}
