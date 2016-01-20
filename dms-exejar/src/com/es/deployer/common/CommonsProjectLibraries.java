package com.es.deployer.common;

import java.util.HashMap;

public class CommonsProjectLibraries {
	private HashMap<String, String[]> mapCmmProjLib;
	
	public CommonsProjectLibraries() {
		mapCmmProjLib = new HashMap<String, String[]>();
		mapCmmProjLib.put("00000000_0", new String[] {
				"zPDK/dist/commons-pionnet.jar", 
				"zInterfaces/dist/commons-interfaces.jar", 
				"zPriceMgr/dist/commons-pricemgr.jar"});
	}

	public String[] getCmmProjLib(String projId) {
		return mapCmmProjLib.get(projId);
	}
}
