package com.es.deployer.common;

public class DeployerException extends Exception {
	private static final long serialVersionUID = 5928206365169412609L;

	public DeployerException() {}
	
	public DeployerException(String msg) {
		super(msg);
	}
}
