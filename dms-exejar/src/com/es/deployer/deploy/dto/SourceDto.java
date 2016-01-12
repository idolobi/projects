package com.es.deployer.deploy.dto;

import java.sql.Timestamp;

public class SourceDto extends BaseSourceDto {
	
	private String srcGrupCd;
	private String depReqId;
	private int seq;
	
	private String svnSrcAthrId;
	private Timestamp svnModiTime;
	private String svnReposCd;
	
	
	public String getSrcGrupCd() {
		return srcGrupCd;
	}
	public void setSrcGrupCd(String srcGrupCd) {
		this.srcGrupCd = srcGrupCd;
	}
	public String getDepReqId() {
		return depReqId;
	}
	public void setDepReqId(String depReqId) {
		this.depReqId = depReqId;
	}
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public String getSvnSrcAthrId() {
		return svnSrcAthrId;
	}
	public void setSvnSrcAthrId(String svnSrcAthrId) {
		this.svnSrcAthrId = svnSrcAthrId;
	}
	public Timestamp getSvnModiTime() {
		return svnModiTime;
	}
	public void setSvnModiTime(Timestamp svnModiTime) {
		this.svnModiTime = svnModiTime;
	}
	public String getSvnReposCd() {
		return svnReposCd;
	}
	public void setSvnReposCd(String svnReposCd) {
		this.svnReposCd = svnReposCd;
	}
}
