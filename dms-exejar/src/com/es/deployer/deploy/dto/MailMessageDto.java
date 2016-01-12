package com.es.deployer.deploy.dto;

public class MailMessageDto {
	private String depManId;
	private String msgSubj;
	private String msgCnts;
	
	/**
	 * @return the depManId
	 */
	public String getDepManId() {
		return depManId;
	}
	/**
	 * @param depManId the depManId to set
	 */
	public void setDepManId(String depManId) {
		this.depManId = depManId;
	}
	/**
	 * @return the msgSubj
	 */
	public String getMsgSubj() {
		return msgSubj;
	}
	/**
	 * @param msgSubj the msgSubj to set
	 */
	public void setMsgSubj(String msgSubj) {
		this.msgSubj = msgSubj;
	}
	/**
	 * @return the msgCnts
	 */
	public String getMsgCnts() {
		return msgCnts;
	}
	/**
	 * @param msgCnts the msgCnts to set
	 */
	public void setMsgCnts(String msgCnts) {
		this.msgCnts = msgCnts;
	}
}
