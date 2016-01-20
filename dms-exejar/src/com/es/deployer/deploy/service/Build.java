package com.es.deployer.deploy.service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.es.deployer.common.Parameter;
import com.es.deployer.common.Property;
import com.es.deployer.deploy.dao.RequestDao;

public class Build {
	private static Logger logger = Logger.getLogger(Build.class);
	
	private RequestDao requestDao;
	
	public void setRequestDao(RequestDao requestDao) {
		this.requestDao = requestDao;
	}
	
	public void build(Parameter param) throws Exception {
		Property prpt = new Property(param.getService()+"."+param.getPhase());
		
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("projId", prpt.getProjId());
		paramMap.put("depId", param.getDepId());
		paramMap.put("depPhsDivCd", prpt.getDepPhsDivCd());
		paramMap.put("phsCd", prpt.getPhsCd());
		
		String buildInfoPath = prpt.getHdsnWrkSpPath() + "/" + prpt.getSvnReposNm();
		
		int commCnt = requestDao.getCommonSourceCount(paramMap); // 사용하지 않음
						
		BufferedWriter out = new BufferedWriter(new FileWriter(buildInfoPath + "/" + param.getDepId() + ".info"));
		if(commCnt > 0) {
			out.write("commYn=Y");
		} else {
			out.write("commYn=N");
		}			 
		out.newLine();
		
		String[] srcJavaPaths = prpt.getSrcJavaPath();
		for(int i = 0; i < srcJavaPaths.length; i++) {
			out.write("srcJavaPath"+i+"="+srcJavaPaths[i]); 
			out.newLine();
		}
		
		String[] srcClassesPaths = prpt.getSrcClassesPath();
		for(int i = 0; i < srcClassesPaths.length; i++) {
			out.write("srcClassesPath"+i+"="+srcClassesPaths[i]); 
			out.newLine();
		}
		
		out.close();
	}
}
