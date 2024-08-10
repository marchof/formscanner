package com.albertoborsetta.formscanner.commons.translation;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FormScannerTranslation extends Properties {
	
	private static final String TRANSLATION_PATH= "/language/formscanner-%s.lang";

	/**
     *
     */
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LogManager.getLogger(FormScannerTranslation.class.getName());
	protected static FormScannerTranslation translations = null;

	private FormScannerTranslation(String language) {
		String translationFile = String.format(TRANSLATION_PATH, language);
		try (InputStream in = FormScannerTranslation.class.getResourceAsStream(translationFile)) {
			load(in);
		} catch (IOException e) {
			logger.debug("Error", e);
		}
	}

	public static void setTranslation(String language) {
		translations = new FormScannerTranslation(language);
	}

	public static String getTranslationFor(String key) {
		String value = translations.getProperty(key, key);
		try {
			value = new String(value.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.debug("Error", e);
		}
		return value;
	}

	public static char getMnemonicFor(String key) {
		String value = translations.getProperty(key, key);
		try {
			value = new String(value.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.debug("Error", e);
		}
		return value.charAt(0);
	}
}
