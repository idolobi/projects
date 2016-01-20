package com.es.deployer.file;

import java.io.File;
import java.io.FilenameFilter;

public class InnerClassFilter implements FilenameFilter {
	private String className;
	
	public InnerClassFilter(String classFilename) {
		this.className = classFilename.substring(0, classFilename.lastIndexOf('.'));
	}
	
	@Override
	public boolean accept(File dir, String name) {
		return name.startsWith(className + "$");
	}

}
