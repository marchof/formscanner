package com.albertoborsetta.formscanner.main;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.UIManager;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXException;

import com.albertoborsetta.formscanner.api.FormTemplate;
import com.albertoborsetta.formscanner.api.exceptions.FormScannerException;
import com.albertoborsetta.formscanner.commons.FormFileUtils;
import com.albertoborsetta.formscanner.commons.FormScannerConstants;
import com.albertoborsetta.formscanner.gui.FormScannerWorkspace;
import com.albertoborsetta.formscanner.model.FormScannerModel;

import java.io.UnsupportedEncodingException;

import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import ch.randelshofer.quaqua.QuaquaLookAndFeel;

public class FormScanner {

	/**
	 * Launch the application.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						FormScannerModel model = new FormScannerModel();
						
						UIManager.installLookAndFeel("Quaqua", QuaquaLookAndFeel.class.getName());
						
						for (LookAndFeelInfo info : UIManager
								.getInstalledLookAndFeels()) {
							if (model.getLookAndFeel().equals(info.getName())) {
								UIManager.setLookAndFeel(info.getClassName());
								break;
							}
						}
						FormScannerWorkspace desktop = new FormScannerWorkspace(model);
						desktop.setIconImage(model.getIcon());
					} catch (UnsupportedEncodingException
							| ClassNotFoundException | InstantiationException
							| IllegalAccessException
							| UnsupportedLookAndFeelException e) {
						e.printStackTrace();
					}
				}
			});
		} else {
			Locale locale = Locale.getDefault();
			String osName = System.getProperty("os.name");			
			String temporaryPath = System.getProperty("user.home");
			if (StringUtils.contains(osName, "Windows")) {
				temporaryPath += "/AppData/Local/FormScanner/temp";
			} else {
				temporaryPath += "/.FormScanner/temp";
			}

			FormFileUtils fileUtils = FormFileUtils.getInstance(locale, temporaryPath);
			
			File compressedTemplate = new File(args[0]);
			HashMap<String, String> files = new HashMap<>();
			FormTemplate template = null; 
			try {
				files = fileUtils.chooseTemplate(compressedTemplate);
				template = new FormTemplate();
				template.presetFormTemplate(fileUtils.getTemplate(files.get(FormScannerConstants.TEMPLATE)));
				template.setImage(fileUtils.getImage(files.get(FormScannerConstants.IMAGE)));
				
				// TODO Verificare il discorso aggiornamento di versione
				if (!FormScannerConstants.CURRENT_TEMPLATE_VERSION.equals(template.getVersion())) {
					fileUtils.saveToFile(FilenameUtils.getFullPath(args[0]), template, false);
				}
			} catch (ParserConfigurationException | SAXException | IOException e) {
				e.printStackTrace();
				System.exit(-1);
			}
			String[] extensions = ImageIO.getReaderFileSuffixes();
			Iterator<?> fileIterator = FileUtils.iterateFiles(
					new File(args[1]), extensions, false);
			HashMap<String, FormTemplate> filledForms = new HashMap<>();
//			while (fileIterator.hasNext()) {
//				File imageFile = (File) fileIterator.next();
//				
//				try {
//					FormTemplate filledForm = new FormTemplate(
//							imageFile, template);
//					filledForm.findCorners(
//							template.getThreshold(),
//							template.getDensity(), template.getCornerType(), template.getCrop());
//					filledForm.findPoints(
//							template.getThreshold(),
//							template.getDensity(), template.getSize());
//					filledForm.findAreas();
//					filledForms
//					.put(
//							filledForm.getName(),
//							filledForm);
//				} catch (IOException | FormScannerException e) {
//					e.printStackTrace();;
//					System.exit(-1);
//				}
//				
//			}

			Date today = Calendar.getInstance().getTime();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			File outputFile = new File(
					args[1] + System.getProperty("file.separator") + "results_" + sdf
							.format(today) + ".csv");
			fileUtils.saveCsvAs(outputFile, filledForms, false);
			fileUtils.saveToFile(FilenameUtils.getFullPath(args[0]), template, false);
			System.exit(0);
		}
	}
}
