package com.es.deployer.deploy.dto;

import java.sql.Timestamp;

public class BaseSourceDto {
	private String projId;
	
	private String svnSrcUrl;
	private String svnSrcNm;
	private long svnSrcRevNum;
	
	private String firstInptUsrId;
	private Timestamp firstInptTime;
	private String lastModiUsrId;
	private Timestamp lastModiTime;
	
	
	public String getProjId() {
		return projId;
	}
	public void setProjId(String projId) {
		this.projId = projId;
	}
	public String getSvnSrcUrl() {
		return svnSrcUrl;
	}
	public void setSvnSrcUrl(String svnSrcUrl) {
		this.svnSrcUrl = svnSrcUrl;
	}
	public String getSvnSrcNm() {
		return svnSrcNm;
	}
	public void setSvnSrcNm(String svnSrcNm) {
		this.svnSrcNm = svnSrcNm;
	}
	public long getSvnSrcRevNum() {
		return svnSrcRevNum;
	}
	public void setSvnSrcRevNum(long svnSrcRevNum) {
		this.svnSrcRevNum = svnSrcRevNum;
	}
	public String getFirstInptUsrId() {
		return firstInptUsrId;
	}
	public void setFirstInptUsrId(String firstInptUsrId) {
		this.firstInptUsrId = firstInptUsrId;
	}
	public Timestamp getFirstInptTime() {
		return firstInptTime;
	}
	public void setFirstInptTime(Timestamp firstInptTime) {
		this.firstInptTime = firstInptTime;
	}
	public String getLastModiUsrId() {
		return lastModiUsrId;
	}
	public void setLastModiUsrId(String lastModiUsrId) {
		this.lastModiUsrId = lastModiUsrId;
	}
	public Timestamp getLastModiTime() {
		return lastModiTime;
	}
	public void setLastModiTime(Timestamp lastModiTime) {
		this.lastModiTime = lastModiTime;
	}
}
