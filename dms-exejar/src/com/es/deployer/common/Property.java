package com.es.deployer.common;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Property {
	/* PROJECT */
	private String projId;
	private String srcGrupCd;
	private String eclipseProjNm;
	private String depPhsDivCd;
	private String phsCd;
	
	/* SUBVERSION */
	private String svnSvrUrl;
	private String svnReposNm;
	private String svnUserId;
	private String svnUserPassword;
	
	/* HUDSON */
	private String hdsnWrkSpPath;
	private String hdsnTrunkPath;
	private String hdsnBranchPath;
	private String hdsnPackPath;
	
	/* SOURCE */
	private String srcWebrootPath;
	private String srcWebappPath;
	private String[] srcJavaPath;
	private String[] srcClassesPath;
	private String[] srcLibPath;
	private String[] srcConfPath;
	
	/* SERVER */
	private String svrDocRoot;
	private String svrWebrootPath;
	private String svrWebappPath;
	private String[] svrClassesPath;
	private String[] svrLibPath;
	private String[] svrConfPath;
	
	public Property(String propertyName) {
		Properties prpt = getProperties(Const.DEPLOYER_HOME + "/scripts/" + propertyName + ".properties");
		/* PROJECT */
		this.projId = prpt.getProperty("projId");
		this.srcGrupCd = prpt.getProperty("srcGrupCd");
		this.eclipseProjNm = prpt.getProperty("eclipseProjNm");
		this.depPhsDivCd = prpt.getProperty("depPhsDivCd");
		this.phsCd = prpt.getProperty("phsCd");
		/* SUBVERSION */
		this.svnSvrUrl = prpt.getProperty("svnSvrUrl");
		this.svnReposNm = prpt.getProperty("svnReposNm");
		this.svnUserId = prpt.getProperty("svnUserId");
		this.svnUserPassword = prpt.getProperty("svnUserPassword");
		/* HUDSON */
		this.hdsnWrkSpPath = prpt.getProperty("hdsnWrkSpPath");
		this.hdsnTrunkPath = prpt.getProperty("hdsnTrunkPath");
		this.hdsnBranchPath = prpt.getProperty("hdsnBranchPath");
		this.hdsnPackPath = prpt.getProperty("hdsnPackPath");
		/* SOURCE */
		this.srcWebrootPath = prpt.getProperty("srcWebrootPath");
		this.srcWebappPath = prpt.getProperty("srcWebappPath");
		this.srcJavaPath = prpt.getProperty("srcJavaPath").split(",");
		this.srcClassesPath = prpt.getProperty("srcClassesPath").split(",");
		this.srcLibPath = prpt.getProperty("srcLibPath").split(",");
		this.srcConfPath = prpt.getProperty("srcConfPath").split(",");
		/* SERVER */
		this.svrDocRoot = prpt.getProperty("svrDocRoot");
		this.svrWebrootPath = prpt.getProperty("svrWebrootPath");
		this.svrWebappPath = prpt.getProperty("svrWebappPath");
		this.svrClassesPath = prpt.getProperty("svrClassesPath").split(",");
		this.svrLibPath = prpt.getProperty("svrLibPath").split(",");
		this.svrConfPath = prpt.getProperty("svrConfPath").split(",");
	}
		
	public String getProjId() {
		return projId;
	}
	public String getSrcGrupCd() {
		return srcGrupCd;
	}
	public String getEclipseProjNm() {
		return eclipseProjNm;
	}
	public String getDepPhsDivCd() {
		return depPhsDivCd;
	}
	public String getPhsCd() {
		return phsCd;
	}
	public String getSvnSvrUrl() {
		return svnSvrUrl;
	}
	public String getSvnReposNm() {
		return svnReposNm;
	}
	public String getSvnUserId() {
		return svnUserId;
	}
	public String getSvnUserPassword() {
		return svnUserPassword;
	}
	public String getHdsnWrkSpPath() {
		return hdsnWrkSpPath;
	}
	public String getHdsnTrunkPath() {
		return hdsnTrunkPath;
	}
	public String getHdsnBranchPath() {
		return hdsnBranchPath;
	}
	public String getHdsnPackPath() {
		return hdsnPackPath;
	}
	public String getSrcWebrootPath() {
		return srcWebrootPath;
	}
	public String getSrcWebappPath() {
		return srcWebappPath;
	}
	public String[] getSrcJavaPath() {
		return srcJavaPath;
	}
	public String[] getSrcClassesPath() {
		return srcClassesPath;
	}
	public String[] getSrcLibPath() {
		return srcLibPath;
	}
	public String getSvrDocRoot() {
		return svrDocRoot;
	}
	public String getSvrWebrootPath() {
		return svrWebrootPath;
	}
	public String getSvrWebappPath() {
		return svrWebappPath;
	}
	public String[] getSvrClassesPath() {
		return svrClassesPath;
	}
	public String[] getSvrLibPath() {
		return svrLibPath;
	}
	public String[] getSrcConfPath() {
		return srcConfPath;
	}
	public String[] getSvrConfPath() {
		return svrConfPath;
	}

	private Properties getProperties(String filePath) {
		File profile = new File(filePath);
		Properties prop = new Properties();
	    FileInputStream fis = null;
		try {
            fis = new FileInputStream(profile);
            prop.load(new BufferedInputStream(fis));
        } catch (IOException e) {
            e.printStackTrace();
        }
		return prop;
	}
	
	public String getBaseSrcPath() {
    	return hdsnWrkSpPath + "/" + svnReposNm + "/" + hdsnTrunkPath;
    }
    
	public String getBaseTgtPath() {
    	return hdsnWrkSpPath + "/" + svnReposNm + "/" + hdsnBranchPath;
    }
    
	public String getBaseSrcUrl() {
    	return svnSvrUrl + "/" + svnReposNm + "/" + hdsnTrunkPath;
    }
    
	public String getBaseTgtUrl(Property prpt) {
    	return svnSvrUrl + "/" + svnReposNm + "/" + hdsnBranchPath;
    }
}
