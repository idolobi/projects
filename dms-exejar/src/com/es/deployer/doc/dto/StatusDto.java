package com.es.deployer.doc.dto;


public class StatusDto extends BaseStatusDto {
	private String fwardPhsCd;
	private String depReqDate;
	private String rglrUrgntDivCd;
	private String[] rglrUrgntDivCds;
	private String[] depReqIds;

	public String getFwardPhsCd() {
		return fwardPhsCd;
	}
	public void setFwardPhsCd(String fwardPhsCd) {
		this.fwardPhsCd = fwardPhsCd;
	}
	public String getDepReqDate() {
		return depReqDate;
	}
	public void setDepReqDate(String depReqDate) {
		this.depReqDate = depReqDate;
	}
	public String getRglrUrgntDivCd() {
		return rglrUrgntDivCd;
	}
	public void setRglrUrgntDivCd(String rglrUrgntDivCd) {
		this.rglrUrgntDivCd = rglrUrgntDivCd;
	}
	public String[] getRglrUrgntDivCds() {
		return rglrUrgntDivCds;
	}
	public void setRglrUrgntDivCds(String[] rglrUrgntDivCds) {
		this.rglrUrgntDivCds = rglrUrgntDivCds;
	}
	public String[] getDepReqIds() {
		return depReqIds;
	}
	public void setDepReqIds(String[] depReqIds) {
		this.depReqIds = depReqIds;
	}
}
