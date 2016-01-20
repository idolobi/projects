package com.es.deployer.file;

public class FileCopyEntry {
	private String fromPath;
	private String fromName;
	private String toPath;
	private String toName;
	
	public String getFromPath() {
		return fromPath;
	}
	public void setFromPath(String fromPath) {
		this.fromPath = fromPath;
	}
	public String getFromName() {
		return fromName;
	}
	public void setFromName(String fromName) {
		this.fromName = fromName;
	}
	public String getToPath() {
		return toPath;
	}
	public void setToPath(String toPath) {
		this.toPath = toPath;
	}
	public String getToName() {
		return toName;
	}
	public void setToName(String toName) {
		this.toName = toName;
	}
}
