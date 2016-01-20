package com.es.deployer.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ConfigLoader {
	private ApplicationContext context;

	public ConfigLoader() {
		ApplicationContext context = 
				new ClassPathXmlApplicationContext("deployer-context.xml", ConfigLoader.class);
		this.context = context;
	}

	public ApplicationContext getContext() {
		return context;
	}
}
