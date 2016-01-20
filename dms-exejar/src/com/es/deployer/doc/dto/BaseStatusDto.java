package com.es.deployer.doc.dto;

import java.sql.Timestamp;

public class BaseStatusDto {
	private String projId;
	private String srcGrupCd;
	private String depReqId;
	private int phsProcSeq;
	private String phsCd;
	private String comments;
	private String depId;
	private String docId;
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
	public int getPhsProcSeq() {
		return phsProcSeq;
	}
	public void setPhsProcSeq(int phsProcSeq) {
		this.phsProcSeq = phsProcSeq;
	}
	public String getPhsCd() {
		return phsCd;
	}
	public void setPhsCd(String phsCd) {
		this.phsCd = phsCd;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getDepId() {
		return depId;
	}
	public void setDepId(String depId) {
		this.depId = depId;
	}
	public String getDocId() {
		return docId;
	}
	public void setDocId(String docId) {
		this.docId = docId;
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
