package com.albertoborsetta.formscanner.commons.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FormScannerConfiguration extends Properties {
	
	private static final String DEFAULT_CONFIG_PATH= "/config/formscanner.properties";
	private static final String CONFIG_FILE_NAME = "formscanner.properties";
	
	private static String userConfigFile;

	/**
     *
     */
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LogManager
			.getLogger(FormScannerConfiguration.class.getName());
	private static FormScannerConfiguration configurations = null;

	private FormScannerConfiguration() {
		super();
		try {
			load(new FileInputStream(userConfigFile));
		} catch (IOException e) {
			logger.debug("Error", e);
		}
	}

	public static FormScannerConfiguration getConfiguration(String userPath) {
		if (configurations == null) {
			userConfigFile = userPath + CONFIG_FILE_NAME;

			File userFile = new File(userConfigFile);
			if (!userFile.exists() || userFile.isDirectory()) {
				try {
					FileUtils.copyURLToFile(FormScannerConfiguration.class.getResource(DEFAULT_CONFIG_PATH), userFile);
				} catch (IOException e) {
					logger.error("Cannot create user configuration from default configurations.", e);
				}
			}

			configurations = new FormScannerConfiguration();
		}
		return configurations;
	}

	public void store() {
		try {
			store(new FileOutputStream(userConfigFile), null);
		} catch (IOException e) {
			logger.debug("Error", e);
		}
	}

	public <T> Object getProperty(String key, T defaultValue) {
		String val = getProperty(key);

		if (StringUtils.isEmpty(val))
			return defaultValue;
		
		if (defaultValue instanceof Integer)
			return Integer.valueOf(val);
		
		if (defaultValue instanceof Boolean)
			return Boolean.valueOf(val);
		
		if (defaultValue instanceof String)
			return val;
		
		return val;
	}

//	public Integer getProperty(String key, Integer defaultValue) {
//		String val = getProperty(key);
//		return (StringUtils.isEmpty(val)) ? defaultValue : Integer.valueOf(val);
//	}
//	
//	public boolean getProperty(String key, boolean defaultValue) {
//		String val = getProperty(key);
//		return (StringUtils.isEmpty(val)) ? defaultValue : Boolean.parseBoolean(val);
//	}

	public <T> void setProperty(String key, T value) {
		setProperty(key, String.valueOf(value));
	}
}
