package com.es.deployer.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class FileUtil {
	private static Logger logger = Logger.getLogger(FileUtil.class.getName());
		
	public static void copy(String fromPath, String toPath, 
			boolean isForcedOverwrite) {
		FileInputStream	 is = null;
		FileOutputStream os = null;
		FileChannel fcis = null;
		FileChannel fcos = null;
		
		try {
			//from
			is = new FileInputStream(fromPath);
			fcis = is.getChannel();
			//to
			File fromDir = new File(fromPath); // 원본이 파일이어야 함
			File toDir = new File(toPath);
			if(fromDir.isFile() && ((!toDir.exists() || (toDir.exists() && isForcedOverwrite)))) {
				File toDirOnly = new File(toPath.substring(0, toPath.lastIndexOf("/")));
				if(!toDirOnly.isDirectory()) { // 없으면 만든다.
					toDirOnly.mkdirs();
				}
			}
			os = new FileOutputStream(toPath);
			fcos = os.getChannel();
			//copy		
			long size = fcis.size();
			fcis.transferTo(0, size, fcos);
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(fcos != null) fcos.close();
				if(fcis != null) fcis.close();
				if(os   != null) os.close();
				if(is   != null) is.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * fromDir에 있는 entries를 toDir에 복사한다.
	 * @param entries
	 * @param fromDir
	 * @param toDir
	 * @throws IOException
	 */
	public static int copy(List<FileCopyEntry> entries, boolean isForcedOverwrite) throws IOException {
		int cntPass = 0;
		int cntFail = 0;
		for(int i = 0; i < entries.size(); i++) {
			String fromPath = entries.get(i).getFromPath();
			String fromName = entries.get(i).getFromName();
			String from = fromPath + "/" + fromName;
			
			String toPath = entries.get(i).getToPath();
			String toName = entries.get(i).getToName();
			String to = toPath + "/" + toName;
			
			File fileFrom = new File(from);
			File fileTo = new File(to);
			if(fileFrom.isFile() && ((!fileTo.exists()) || (fileTo.exists() && isForcedOverwrite))) { // 원본 필수!!!
				File fileToPath	= new File(toPath);
				if(!fileToPath.isDirectory()) {
					fileToPath.mkdirs();
				}

				FileInputStream	is = new FileInputStream(from);
				FileChannel fcis = is.getChannel();
				//to
				FileOutputStream os = new FileOutputStream(to);
				FileChannel fcos = os.getChannel();
				//copy		
				long size = fcis.size();
				fcis.transferTo(0, size, fcos);
				//close
				if(fcos != null) fcos.close();
				if(fcis != null) fcis.close();
				if(os   != null) os.close();
				if(is   != null) is.close();
				cntPass++;
				System.out.println("★ 복사됨(" + cntPass + ") : " + from + " --> " + to);
			} else {
				cntFail++;
				System.out.println("★ 복사되지 않음(" + cntFail + ") : " + from);
			}
			
		}
		System.out.println("★  복사한 수 : " + cntPass + ", 복사되지 않은 수 : " + cntFail);
		return cntPass;
	}
	
	/**
	 * 특정 폴더의 파일 목록
	 * @param baseDir
	 * @param files
	 * @param exceptPath
	 * @return
	 */
	public static List<String> getFileList(String path) {
		List<String> files = new ArrayList<String>();
		File dir = new File(path);
		File subDir;
		
		String[] dirList = dir.list();
		for(int i = 0; i < dirList.length; i++) {
			subDir = new File(dir.getAbsolutePath() + "/" + dirList[i]);
			if(subDir.isDirectory()) {
				files.addAll(getFileList(subDir.getAbsolutePath()));
			} else {
				files.add(subDir.getAbsoluteFile().toString());
			}
		}
		return files;
	}
}
