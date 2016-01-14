package com.es.deployer.deploy.service;

import com.es.deployer.common.Parameter;
import com.es.deployer.common.Property;
import com.es.deployer.svn.SvnClientManager;

public class Update {
	public void update(Parameter param) {
		Property prpt = new Property(param.getService()+"."+param.getPhase());
		
		SvnClientManager scm = new SvnClientManager(
				prpt.getSvnSvrUrl() + "/" + prpt.getSvnReposNm(), 
				prpt.getSvnUserId(), prpt.getSvnUserPassword());
		
		String trunkPath = prpt.getHdsnWrkSpPath() + "/" + prpt.getSvnReposNm() + "/" + prpt.getHdsnTrunkPath();
		String branchesPath = prpt.getHdsnWrkSpPath() + "/" + prpt.getSvnReposNm() + "/" + prpt.getHdsnBranchPath();
		
		scm.update(trunkPath+"/"+prpt.getEclipseProjNm());
		scm.update(branchesPath+"/"+prpt.getEclipseProjNm());
	}
}
