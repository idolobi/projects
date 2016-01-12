package com.es.deployer.common;

import com.es.deployer.ftp.ServerInfo;

public class Parameter {
	private String service;
	private String depId;
	private String phase;
	private String task;
	private ServerInfo svrInfo;
	private String build;
	private String rglrUrgnt;
	private String depReqId;
	
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service.toLowerCase();
	}
	public String getDepId() {
		return depId;
	}
	public void setDepId(String depId) {
		this.depId = depId.toUpperCase();
	}
	public String getPhase() {
		return phase;
	}
	public void setPhase(String phase) {
		this.phase = phase.toLowerCase();
	}
	public String getTask() {
		return task;
	}
	public void setTask(String task) {
		this.task = task.toLowerCase();
	}
	public ServerInfo getSvrInfo() {
		return svrInfo;
	}
	public void setSvrInfo(ServerInfo svrInfo) {
		this.svrInfo = svrInfo;
	}	
	public String getBuild() {
		return build;
	}
	public void setBuild(String build) {
		this.build = build;
	}	
	public String getRglrUrgnt() {
		return rglrUrgnt;
	}
	public void setRglrUrgnt(String rglrUrgnt) {
		this.rglrUrgnt = rglrUrgnt;
	}	
	public String getDepReqId() {
		return depReqId;
	}
	public void setDepReqId(String depReqId) {
		this.depReqId = depReqId;
	}
	
	public String toString() {
		return service + "|" + depId + "|" + phase + "|" + task;
	}
}
