package com.es.deployer.deploy.service;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.es.deployer.common.CommonsProjectLibraries;
import com.es.deployer.common.Parameter;
import com.es.deployer.common.Property;
import com.es.deployer.deploy.dao.RequestDao;
import com.es.deployer.deploy.dto.SourceDto;
import com.es.deployer.file.FileCopyEntry;
import com.es.deployer.file.FileUtil;
import com.es.deployer.file.InnerClassFilter;

public class Pack {
	private static Logger logger = Logger.getLogger(Pack.class);
	
	private RequestDao requestDao;
	
	public void setRequestDao(RequestDao requestDao) {
		this.requestDao = requestDao;
	}
	
	public void pack(Parameter param) throws Exception {
		List<FileCopyEntry> entries = new ArrayList<FileCopyEntry>();
		
		Property prpt = new Property(param.getService()+"."+param.getPhase());
		
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("projId", prpt.getProjId());
		paramMap.put("depId", param.getDepId());
		paramMap.put("depPhsDivCd", prpt.getDepPhsDivCd());
		paramMap.put("phsCd", prpt.getPhsCd());

		List<SourceDto> depSrcList = requestDao.getSvnSource(paramMap); // DB에서 조회한 소스목록
		
		System.out.println("★ 배포ID " + param.getDepId() + "의 소스 발췌 작업(PACK)을 시작합니다.");
		
		for(int i = 0; i < depSrcList.size(); i++) {
			SourceDto ssd = depSrcList.get(i);
			// DB의 svnSrcUrl의 데이터 형식 : "https://~/svn/lotte2010/trunk/mo/~/dir"
			// 실제 필요한 데이터 : "mo/~/dir"
			String removePath = prpt.getSvnSvrUrl() + "/" + prpt.getSvnReposNm() + "/" + prpt.getHdsnTrunkPath() + "/";
			String svnSrcPath = ssd.getSvnSrcUrl().replaceFirst(removePath, "");
			
			String fromPath = "";
			String fromName = ssd.getSvnSrcNm();
			String toPath = "";
			String toName = ssd.getSvnSrcNm();
			
			boolean isDone = false;
			// web
			if(svnSrcPath.startsWith(prpt.getSrcWebrootPath()) && !isDone) {
				logger.debug("WEB CONTENTS");
				fromPath = prpt.getHdsnWrkSpPath() + "/" + prpt.getSvnReposNm() + "/"
				         + prpt.getHdsnBranchPath() + "/" + svnSrcPath;
				
				toPath = prpt.getHdsnWrkSpPath() + "/" + prpt.getSvnReposNm() + "/" 
	                   + prpt.getHdsnPackPath() + "/" + param.getDepId() + "/" 
					   + svnSrcPath.replaceFirst(prpt.getSrcWebrootPath(), prpt.getSvrWebrootPath());
				logger.debug(fromPath+"/"+fromName);
				logger.debug(toPath+"/"+toName);
				isDone = true;
			}
			
			// was(lib)
			String[] srcLibPaths = prpt.getSrcLibPath();
			String[] svrLibPaths = prpt.getSvrLibPath();
			if(srcLibPaths != null && svrLibPaths != null) {
				for(int k = 0; k < srcLibPaths.length; k++) {
					if(svnSrcPath.startsWith(srcLibPaths[k]) && !isDone) {
						logger.debug("LIBRARY");
						fromPath = prpt.getHdsnWrkSpPath() + "/" + prpt.getSvnReposNm() + "/"
						         + prpt.getHdsnBranchPath() + "/" + svnSrcPath;
						
						toPath = prpt.getHdsnWrkSpPath() + "/" + prpt.getSvnReposNm() + "/" 
			                   + prpt.getHdsnPackPath() + "/" + param.getDepId() + "/" 
							   + svnSrcPath.replaceFirst(srcLibPaths[k], svrLibPaths[k]);
						logger.debug(fromPath+"/"+fromName);
						logger.debug(toPath+"/"+toName);
						isDone = true;
					}
				}
			}
			
			// was(java)
			boolean isJava = false;
			String[] srcJavaPaths = prpt.getSrcJavaPath();
			String[] srcClassesPaths = prpt.getSrcClassesPath();
			String[] svrClassesPaths = prpt.getSvrClassesPath();
			for(int j = 0; j < srcJavaPaths.length; j++) {
				if(svnSrcPath.startsWith(srcJavaPaths[j]) && !isDone) {
					logger.debug("JAVA or XML or PROPERTY");
					// java source path --> class result path				
					fromPath = prpt.getHdsnWrkSpPath() + "/" + prpt.getSvnReposNm() + "/"
					        + prpt.getHdsnBranchPath() + "/" 
							+ svnSrcPath.replaceFirst(srcJavaPaths[j].trim(), srcClassesPaths[j]);
					
					toPath = prpt.getHdsnWrkSpPath() + "/" + prpt.getSvnReposNm() + "/" 
			                + prpt.getHdsnPackPath() + "/" + param.getDepId() + "/" 
							+ svnSrcPath.replaceFirst(srcJavaPaths[j].trim(), svrClassesPaths[j]);
					
					if(fromName.endsWith(".java")) {
						// .java --> .class
						fromName = fromName.replace(".java", ".class");
						toName = toName.replace(".java", ".class");
						isJava = true;
					}
					logger.debug(fromPath+"/"+fromName);
					logger.debug(toPath+"/"+toName);
					isDone = true;
				}
			}
			
			// was(jsp)
			String srcWebappPath = prpt.getSrcWebappPath();
			String svrWebappPath = prpt.getSvrWebappPath();
			if(svnSrcPath.startsWith(srcWebappPath) && !isDone) {
				logger.debug("WAS CONTENTS");
				fromPath = prpt.getHdsnWrkSpPath() + "/" + prpt.getSvnReposNm() + "/"
				         + prpt.getHdsnBranchPath() + "/" + svnSrcPath;
				
				toPath = prpt.getHdsnWrkSpPath() + "/" + prpt.getSvnReposNm() + "/" 
	                   + prpt.getHdsnPackPath() + "/" + param.getDepId() + "/" 
					   + svnSrcPath.replaceFirst(srcWebappPath, svrWebappPath);
				logger.debug(fromPath+"/"+fromName);
				logger.debug(toPath+"/"+toName);
				isDone = true;
			}
			
			// was(etc)
			String[] srcConfPaths = prpt.getSrcConfPath();
			String[] svrConfPaths = prpt.getSvrConfPath();
			if(srcConfPaths != null && svrConfPaths != null) {
				for(int a = 0; a < srcConfPaths.length; a++) {
					if(svnSrcPath.startsWith(srcConfPaths[a]) && !isDone) {
						logger.debug("ETC");
						fromPath = prpt.getHdsnWrkSpPath() + "/" + prpt.getSvnReposNm() + "/"
						        + prpt.getHdsnBranchPath() + "/" + svnSrcPath;
						
						toPath = prpt.getHdsnWrkSpPath() + "/" + prpt.getSvnReposNm() + "/" 
				                + prpt.getHdsnPackPath() + "/" + param.getDepId() + "/" 
								+ svnSrcPath.replace(srcConfPaths[a], svrConfPaths[a]);					
						logger.debug(fromPath+"/"+fromName);
						logger.debug(toPath+"/"+toName);
						isDone = true;
					}
				}
			}
			
			FileCopyEntry entry = new FileCopyEntry();
			entry.setFromPath(fromPath);
			entry.setFromName(fromName);
			entry.setToPath(toPath);
			entry.setToName(toName);
			entries.add(entry);
			
			String[] inClassArr = null;
			if(isJava) {
				File classesDir = new File(fromPath);
				if(classesDir.isDirectory()) {
					logger.debug("INNER CLASS");
					FilenameFilter filter = new InnerClassFilter(fromName);
					inClassArr = classesDir.list(filter);
					
					for(int j = 0; j < inClassArr.length; j++) {
						String fromInClassPath = fromPath;
						String fromInClassName = inClassArr[j];
						
						String toInClassPath = toPath;
						String toInClassName = inClassArr[j];
						
						logger.debug(fromInClassPath+"/"+fromInClassName);
						logger.debug(toInClassPath+"/"+toInClassName);
						
						FileCopyEntry inClassEntry = new FileCopyEntry();
						inClassEntry.setFromPath(fromInClassPath);
						inClassEntry.setFromName(fromInClassName);
						inClassEntry.setToPath(toInClassPath);
						inClassEntry.setToName(toInClassName);
						entries.add(inClassEntry);
					}
				}
			}
			
			CommonsProjectLibraries cpl = new CommonsProjectLibraries();
			String[] libs = cpl.getCmmProjLib(paramMap.get("projId"));
			if(libs != null) {
				for(int l = 0; l < libs.length; l++) {
					String cmmProjLib = libs[l];
					String cmmProjNm = cmmProjLib.substring(0, libs[l].indexOf("/"));
					if(svnSrcPath.startsWith(cmmProjNm)) { // common jar
						logger.debug("COMMON JAR");
						
						String cmmJarPath = cmmProjLib.substring(0, cmmProjLib.lastIndexOf("/"));
						String cmmJarFilename = cmmProjLib.substring(cmmProjLib.lastIndexOf("/")+1, cmmProjLib.length());
						String fromCmmJarPath = prpt.getHdsnWrkSpPath() + "/" + prpt.getSvnReposNm() + "/"
				         + prpt.getHdsnBranchPath() + "/" + cmmJarPath;
						String toCmmJarPath = prpt.getHdsnWrkSpPath() + "/" + prpt.getSvnReposNm() + "/" 
				                   + prpt.getHdsnPackPath() + "/" + param.getDepId() + "/" + cmmJarPath;
						logger.debug(fromCmmJarPath+"/"+cmmJarFilename);
						logger.debug(toCmmJarPath+"/"+cmmJarFilename);
						
						FileCopyEntry cmmJarEntry = new FileCopyEntry();
						cmmJarEntry.setFromPath(fromCmmJarPath);
						cmmJarEntry.setFromName(cmmJarFilename);
						cmmJarEntry.setToPath(toCmmJarPath);
						cmmJarEntry.setToName(cmmJarFilename);
						entries.add(cmmJarEntry);
					}
				}
			}
		}
		
		FileUtil.copy(removeDupEntry(entries), true);
		System.out.println("★  소스 발췌 작업(PACK)을 종료합니다.");
	}
	
	/**
	 * 복사 목록 중 중복된 것 제거
	 * @param list
	 * @return
	 */
	private List<FileCopyEntry> removeDupEntry(List<FileCopyEntry> list) {
		for(int i = 0; i < list.size(); i++) {
			FileCopyEntry fceLeft = list.get(i);
			String left = fceLeft.getFromPath() + fceLeft.getFromName() + fceLeft.getToPath() + fceLeft.getToName(); 
			int j = i+1;
			while(j < list.size()) {
				FileCopyEntry fceRight = list.get(j);
				String right = fceRight.getFromPath() + fceRight.getFromName() + fceRight.getToPath() + fceRight.getToName();
				if(left.equals(right)) {
					list.remove(j);
					logger.debug(j + ":" + right);
				}
				j++;
			}
		}
		return list;
	}

}
