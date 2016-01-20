package com.es.deployer.deploy.service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.es.deployer.common.Const;
import com.es.deployer.common.DeployerException;
import com.es.deployer.common.Parameter;
import com.es.deployer.common.Property;
import com.es.deployer.deploy.dao.DeployDao;

public class DeployID {
	private static Logger logger = Logger.getLogger(DeployID.class);
	
	private String deployId;
	private DeployDao deployDao;
	
	public void setDeployDao(DeployDao deployDao) {
		this.deployDao = deployDao;
	}
	
	public void setDeployId(Parameter param) throws DeployerException {
		Property prpt = new Property(param.getService()+"."+param.getPhase());
		String projId = prpt.getProjId();
		String srcGrupCd = prpt.getSrcGrupCd();
		String deployId = deployDao.makeRglrDepId(projId, srcGrupCd);
		logger.debug("# projId = " + projId);
		logger.debug("# srcGrupCd = " + srcGrupCd);
		System.out.println("★ 배포ID : " + deployId);
		createPropertiesFile(param.getService(), deployId);
		this.deployId = deployId;
	}
	
	public String getDeployId() {
		return deployId;
	}
	
	private void createPropertiesFile(String service, String deployId) throws DeployerException {
		String text = "DEPLOY_ID=" + deployId;
		try {
			FileWriter fw = new FileWriter(Const.DEPLOYER_HOME + "/scripts/" + service + "_param.properties");
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(new String(text.getBytes("UTF-8")));
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new DeployerException("프로퍼티 파일 생성 중 오류가 발생했습니다.");
		}
	}
}
