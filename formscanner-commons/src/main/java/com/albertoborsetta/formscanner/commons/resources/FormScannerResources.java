package com.albertoborsetta.formscanner.commons.resources;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FormScannerResources {

	private static final String LICENSE_PATH= "/license/license.txt";

	private static final String ICONS_PATH= "/icons/%s.png";
	
	private static String template;

	private static final Logger logger = LogManager
			.getLogger(FormScannerResources.class.getName());

	public static ImageIcon getIconFor(String key) {
		String iconFile = String.format(ICONS_PATH, key);
		return new ImageIcon(FormScannerResources.class.getResource(iconFile));
	}

	public static void setTemplate(String tpl) {
		template = tpl;
	}

	public static File getTemplate() {
		return new File(template);
	}

	public static String getLicense() {
		StringBuilder result = new StringBuilder();
		char[] buffer = new char[1024];
		try (Reader r = new InputStreamReader(FormScannerResources.class.getResourceAsStream(LICENSE_PATH))) {
			int len;
			while ((len = r.read(buffer)) != -1) {
				result.append(buffer, 0, len);
			}
		} catch (IOException e) {
			logger.error("Error while reading license text.", e);
		}
		return result.toString();
	}

	public static Image getFormScannerIcon() {
		String iconFile = String.format(ICONS_PATH, FormScannerResourcesKeys.FORMSCANNER_ICON);
		try (InputStream in = FormScannerResources.class.getResourceAsStream(iconFile)) {
			return ImageIO.read(in);
		} catch (IOException e) {
			logger.catching(e);
			return null;
		}
	}
}
