package com.es.deployer.deploy.dto;

import java.sql.Timestamp;

public class DeployDto {
	private String projId;
	private String depId;
	private String fnshYn;
	private String srcGrupCd;
	private String depDate;
	private String rglrUrgntDivCd;
	private String depBginHhmm;
	private String depEndHhmm;
	private String faultRecogHhmm;
	private String svcNormHhmm;
	private String rlbkCmpltHhmm;
	private String note;
	private String firstInptUsrId;
	private Timestamp firstInptTime;
	private String lastModiUsrId;
	private Timestamp lastModiTime;
	private String dvlpFnshYn;
	
	public String getProjId() {
		return projId;
	}
	public void setProjId(String projId) {
		this.projId = projId;
	}
	public String getDepId() {
		return depId;
	}
	public void setDepId(String depId) {
		this.depId = depId;
	}
	public String getFnshYn() {
		return fnshYn;
	}
	public void setFnshYn(String fnshYn) {
		this.fnshYn = fnshYn;
	}
	public String getSrcGrupCd() {
		return srcGrupCd;
	}
	public void setSrcGrupCd(String srcGrupCd) {
		this.srcGrupCd = srcGrupCd;
	}
	public String getDepDate() {
		return depDate;
	}
	public void setDepDate(String depDate) {
		this.depDate = depDate;
	}
	public String getRglrUrgntDivCd() {
		return rglrUrgntDivCd;
	}
	public void setRglrUrgntDivCd(String rglrUrgntDivCd) {
		this.rglrUrgntDivCd = rglrUrgntDivCd;
	}
	public String getDepBginHhmm() {
		return depBginHhmm;
	}
	public void setDepBginHhmm(String depBginHhmm) {
		this.depBginHhmm = depBginHhmm;
	}
	public String getDepEndHhmm() {
		return depEndHhmm;
	}
	public void setDepEndHhmm(String depEndHhmm) {
		this.depEndHhmm = depEndHhmm;
	}
	public String getFaultRecogHhmm() {
		return faultRecogHhmm;
	}
	public void setFaultRecogHhmm(String faultRecogHhmm) {
		this.faultRecogHhmm = faultRecogHhmm;
	}
	public String getSvcNormHhmm() {
		return svcNormHhmm;
	}
	public void setSvcNormHhmm(String svcNormHhmm) {
		this.svcNormHhmm = svcNormHhmm;
	}
	public String getRlbkCmpltHhmm() {
		return rlbkCmpltHhmm;
	}
	public void setRlbkCmpltHhmm(String rlbkCmpltHhmm) {
		this.rlbkCmpltHhmm = rlbkCmpltHhmm;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
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
	public String getDvlpFnshYn() {
		return dvlpFnshYn;
	}
	public void setDvlpFnshYn(String dvlpFnshYn) {
		this.dvlpFnshYn = dvlpFnshYn;
	}
}
