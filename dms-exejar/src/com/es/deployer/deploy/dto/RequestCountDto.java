package com.es.deployer.deploy.dto;

public class RequestCountDto {
	private String projId;
	private String srcGrupCd;
	private String depReqId;
	private int reqCnt;
	private int rcptCnt;
	
	public String getProjId() {
		return projId;
	}
	public void setProjId(String projId) {
		this.projId = projId;
	}
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
	public int getReqCnt() {
		return reqCnt;
	}
	public void setReqCnt(int reqCnt) {
		this.reqCnt = reqCnt;
	}
	public int getRcptCnt() {
		return rcptCnt;
	}
	public void setRcptCnt(int rcptCnt) {
		this.rcptCnt = rcptCnt;
	}
}
