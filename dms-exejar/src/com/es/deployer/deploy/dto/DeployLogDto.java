package com.es.deployer.deploy.dto;


public class DeployLogDto extends BaseSourceDto {

	private String depId;
	private String depPhsDivCd;
	private int depFreq;
	private int fileSeq;

	private String svnSrcProcCd;
	
	
	public String getDepId() {
		return depId;
	}
	public void setDepId(String depId) {
		this.depId = depId;
	}
	public String getDepPhsDivCd() {
		return depPhsDivCd;
	}
	public void setDepPhsDivCd(String depPhsDivCd) {
		this.depPhsDivCd = depPhsDivCd;
	}
	public int getDepFreq() {
		return depFreq;
	}
	public void setDepFreq(int depFreq) {
		this.depFreq = depFreq;
	}
	public int getFileSeq() {
		return fileSeq;
	}
	public void setFileSeq(int fileSeq) {
		this.fileSeq = fileSeq;
	}
	public String getSvnSrcProcCd() {
		return svnSrcProcCd;
	}
	public void setSvnSrcProcCd(String svnSrcProcCd) {
		this.svnSrcProcCd = svnSrcProcCd;
	}	
}
