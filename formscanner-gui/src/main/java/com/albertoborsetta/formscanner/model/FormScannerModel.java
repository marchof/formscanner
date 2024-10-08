package com.albertoborsetta.formscanner.model;

import java.awt.ComponentOrientation;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import com.albertoborsetta.formscanner.api.FormArea;
import com.albertoborsetta.formscanner.api.FormPoint;
import com.albertoborsetta.formscanner.api.FormQuestion;
import com.albertoborsetta.formscanner.api.FormTemplate;
import com.albertoborsetta.formscanner.api.commons.Constants.CornerType;
import com.albertoborsetta.formscanner.api.commons.Constants.Corners;
import com.albertoborsetta.formscanner.api.commons.Constants.ShapeType;
import com.albertoborsetta.formscanner.commons.FormFileUtils;
import com.albertoborsetta.formscanner.commons.FormScannerConstants;
import com.albertoborsetta.formscanner.commons.FormScannerConstants.Action;
import com.albertoborsetta.formscanner.commons.FormScannerConstants.Frame;
import com.albertoborsetta.formscanner.commons.FormScannerConstants.Language;
import com.albertoborsetta.formscanner.commons.FormScannerConstants.Mode;
import com.albertoborsetta.formscanner.commons.FormScannerFont;
import com.albertoborsetta.formscanner.commons.configuration.FormScannerConfiguration;
import com.albertoborsetta.formscanner.commons.configuration.FormScannerConfigurationKeys;
import com.albertoborsetta.formscanner.commons.resources.FormScannerResources;
import com.albertoborsetta.formscanner.commons.translation.FormScannerTranslation;
import com.albertoborsetta.formscanner.commons.translation.FormScannerTranslationKeys;
import com.albertoborsetta.formscanner.gui.AboutFrame;
import com.albertoborsetta.formscanner.gui.FileListFrame;
import com.albertoborsetta.formscanner.gui.FormScannerDesktop;
import com.albertoborsetta.formscanner.gui.ImageFrame;
import com.albertoborsetta.formscanner.gui.InternalFrame;
import com.albertoborsetta.formscanner.gui.ManageTemplateFrame;
import com.albertoborsetta.formscanner.gui.OptionsFrame;
import com.albertoborsetta.formscanner.gui.RenameFileFrame;
import com.albertoborsetta.formscanner.gui.ResultsGridFrame;

public class FormScannerModel {
	
	private static final Logger logger = LogManager.getLogger(FormScannerModel.class.getName());

	public static final String COL_DX = "COL_DX";
	public static final String COL_DY = "COL_DY";
	public static final String ROW_DX = "ROW_DX";
	public static final String ROW_DY = "ROW_DY";

	public static final String HISTORY_SEPARATOR = "|";

	private String templatePath;
	private String resultsPath;
	private String propertiesPath;
	private final HashMap<Integer, File> openedFiles = new HashMap<>();
	private FileListFrame fileListFrame;
	private RenameFileFrame renameFileFrame;
	private FormScannerDesktop view;
	private int renamedFileIndex = 0;
	private int analyzedFileIndex = 0;
	private boolean firstPass = true;

	private final FormScannerConfiguration configurations;
	private ManageTemplateFrame manageTemplateFrame;
	private ImageFrame imageFrame;
	private FormTemplate formTemplate;
	private BufferedImage templateImage;
	private final FormFileUtils fileUtils;
	private HashMap<String, FormTemplate> filledForms = new HashMap<>();

	private ArrayList<FormPoint> points = new ArrayList<>();
	private final String lang;
	private Integer threshold;
	private Integer density;
	private ResultsGridFrame resultsGridFrame;
	private FormTemplate filledForm;
	private Integer shapeSize;
	private ShapeType shapeType;
	private Rectangle fileListFramePosition;
	private Rectangle renameFilesFramePosition;
	private Rectangle manageTemplateFramePosition;
	private Rectangle imageFramePosition;
	private Rectangle resultsGridFramePosition;
	private Rectangle defaultPosition;
	private Rectangle aboutFramePosition;
	private Rectangle optionsFramePosition;
	private Rectangle desktopSize;
	private final Locale locale;
	private final ComponentOrientation orientation;
	private String fontType;
	private Integer fontSize;
	private ArrayList<FormArea> areas = new ArrayList<>();
	private CornerType cornerType;
	private String lookAndFeel;
	private Image defaultIcon;

	private Boolean resetAutoNumbering;
	private Boolean groupsEnabled;
	private String questionNameTemplate;
	private String barcodeNameTemplate;
	private String groupNameTemplate;
	private ArrayList<String> historyQuestionNameTemplate;
	private ArrayList<String> historyBarcodeNameTemplate;
	private ArrayList<String> historyGroupNameTemplate;
	private HashMap<String, Integer> crop = new HashMap<>();
	private ArrayList<String> usedGroupNamesList = new ArrayList<>();
	private int nextGroupIndex=1;

	public FormScannerModel() throws UnsupportedEncodingException {

		String installationLanguage = StringUtils.defaultIfBlank(System.getProperty("FormScanner_LANGUAGE"),
				System.getenv("FormScanner_LANGUAGE"));

		String userHome = System.getProperty("user.home");
		String osName = System.getProperty("os.name");

		resultsPath = userHome + "/Documents";
		templatePath = userHome + "/Documents";

		if (StringUtils.contains(osName, "Windows")) {
			propertiesPath = userHome + "/AppData/Local/FormScanner";
		} else {
			propertiesPath = userHome + "/.FormScanner";
		}

		propertiesPath = propertiesPath + "/properties/";

		configurations = FormScannerConfiguration.getConfiguration(propertiesPath);

		templatePath = configurations.getProperty(FormScannerConfigurationKeys.TEMPLATE_SAVE_PATH,
				templatePath + "/FormScanner/templates/");
		resultsPath = configurations.getProperty(FormScannerConfigurationKeys.RESULTS_SAVE_PATH,
				resultsPath + "/FormScanner/results/");

		lang = configurations.getProperty(FormScannerConfigurationKeys.LANG, getDefaultLanguage(installationLanguage));

		String[] locales = StringUtils.split(lang, '_');
		if (locales.length == 2) {
			locale = new Locale(locales[0], locales[1]);
		} else {
			locale = new Locale(locales[0]);
		}
		fileUtils = FormFileUtils.getInstance(locale);

		orientation = ComponentOrientation.getOrientation(locale);

		FormScannerTranslation.setTranslation(lang);

		threshold = (Integer) configurations.getProperty(FormScannerConfigurationKeys.THRESHOLD,
				FormScannerConfigurationKeys.DEFAULT_THRESHOLD);
		density = (Integer) configurations.getProperty(FormScannerConfigurationKeys.DENSITY,
				FormScannerConfigurationKeys.DEFAULT_DENSITY);
		shapeSize = (Integer) configurations.getProperty(FormScannerConfigurationKeys.SHAPE_SIZE,
				FormScannerConfigurationKeys.DEFAULT_SHAPE_SIZE);
		shapeType = ShapeType.valueOf(configurations.getProperty(FormScannerConfigurationKeys.SHAPE_TYPE,
				FormScannerConfigurationKeys.DEFAULT_SHAPE_TYPE));
		cornerType = CornerType.valueOf(configurations.getProperty(FormScannerConfigurationKeys.CORNER_TYPE,
				FormScannerConfigurationKeys.DEFAULT_CORNER_TYPE));
		resetAutoNumbering = (Boolean) configurations.getProperty(FormScannerConfigurationKeys.RESET_AUTO_NUMBERING,
				FormScannerConfigurationKeys.DEFAULT_RESET_AUTO_NUMBERING);
		groupsEnabled = (Boolean) configurations.getProperty(FormScannerConfigurationKeys.GROUPS_ENABLED,
				FormScannerConfigurationKeys.DEFAULT_GROUPS_ENABLED);
		questionNameTemplate = configurations.getProperty(FormScannerConfigurationKeys.QUESTION_NAME_TEMPLATE,
				FormScannerConfigurationKeys.DEFAULT_QUESTION_NAME_TEMPLATE);
		barcodeNameTemplate = configurations.getProperty(FormScannerConfigurationKeys.BARCODE_NAME_TEMPLATE,
				FormScannerConfigurationKeys.DEFAULT_BARCODE_NAME_TEMPLATE);
		groupNameTemplate = configurations.getProperty(FormScannerConfigurationKeys.GROUP_NAME_TEMPLATE,
				FormScannerConfigurationKeys.DEFAULT_GROUP_NAME_TEMPLATE);
		historyGroupNameTemplate = new ArrayList<String>(Arrays.asList(
				StringUtils.split(configurations.getProperty(FormScannerConfigurationKeys.HISTORY_GROUP_NAME_TEMPLATE,
						FormScannerConfigurationKeys.DEFAULT_GROUP_NAME_TEMPLATE), HISTORY_SEPARATOR)));
		historyBarcodeNameTemplate = new ArrayList<String>(
				Arrays.asList(
						StringUtils.split(
								configurations.getProperty(FormScannerConfigurationKeys.HISTORY_BARCODE_NAME_TEMPLATE,
										FormScannerConfigurationKeys.DEFAULT_BARCODE_NAME_TEMPLATE),
						HISTORY_SEPARATOR)));
		historyQuestionNameTemplate = new ArrayList<String>(
				Arrays.asList(
						StringUtils.split(
								configurations.getProperty(FormScannerConfigurationKeys.HISTORY_QUESTION_NAME_TEMPLATE,
										FormScannerConfigurationKeys.DEFAULT_QUESTION_NAME_TEMPLATE),
						HISTORY_SEPARATOR)));

		lookAndFeel = configurations.getProperty(FormScannerConfigurationKeys.LOOK_AND_FEEL,
				FormScannerConfigurationKeys.DEFAULT_LOOK_AND_FEEL);
		fontType = configurations.getProperty(FormScannerConfigurationKeys.FONT_TYPE,
				FormScannerConfigurationKeys.DEFAULT_FONT_TYPE);
		fontSize = (Integer) configurations.getProperty(FormScannerConfigurationKeys.FONT_SIZE,
				FormScannerConfigurationKeys.DEFAULT_FONT_SIZE);
		FormScannerFont.getFont(fontType, fontSize);

		crop.put(FormScannerConstants.TOP, 0);
		crop.put(FormScannerConstants.LEFT, 0);
		crop.put(FormScannerConstants.RIGHT, 0);
		crop.put(FormScannerConstants.BOTTOM, 0);

		String tmpl = configurations.getProperty(FormScannerConfigurationKeys.TEMPLATE, (String) null);
		if (!StringUtils.isEmpty(tmpl)) {
			FormScannerResources.setTemplate(templatePath + tmpl);
			openTemplate(FormScannerResources.getTemplate(), false);
		}

		defaultIcon = FormScannerResources.getFormScannerIcon();
	}

	private static String getDefaultLanguage(String installationLanguage) {
		for (Language language : Language.values()) {
			if (!language.getInstallationLanguages().isEmpty()
					&& language.getInstallationLanguages().contains(installationLanguage)) {
				return language.getValue();
			}
		}
		return FormScannerConfigurationKeys.DEFAULT_LANG;
	}

	public void setDefaultFramePositions() {
		for (Frame frame : Frame.values()) {
			String pos = configurations.getProperty(frame.getConfigurationKey(), (String) null);
			Rectangle position;
			if (pos == null) {
				position = frame.getDefaultPosition();
			} else {
				String[] positions = StringUtils.split(pos, ',');

				position = new Rectangle(Integer.parseInt(positions[0]), Integer.parseInt(positions[1]),
						Integer.parseInt(positions[2]), Integer.parseInt(positions[3]));
			}

			setLastPosition(frame, position);
		}
	}

	public void openImages() {
		filledForms.clear();
		openedFiles.clear();
		firstPass = true;
		File[] fileArray = fileUtils.chooseImages();
		if (fileArray != null) {
			Integer fileIndex = 0;
			for (File file : fileArray) {
				openedFiles.put(fileIndex++, file);
			}
			if (!openedFiles.isEmpty()) {
				fileListFrame = new FileListFrame(this, getOpenedFileList());
				view.arrangeFrame(fileListFrame);
				view.setRenameControllersEnabled(true);
				view.setScanControllersEnabled(true);
				view.setScanAllControllersEnabled(true);
				view.setScanCurrentControllersEnabled(false);
			}
		}
	}

	public void renameFiles(String action) {
		Action act = Action.valueOf(action);
		switch (act) {
		case RENAME_FILES_FIRST:
			if (!openedFiles.isEmpty()) {
				view.setRenameControllersEnabled(false);
				view.setScanControllersEnabled(false);
				view.setScanAllControllersEnabled(false);
				view.setScanCurrentControllersEnabled(false);

				renamedFileIndex = fileListFrame.getSelectedItemIndex();
				fileListFrame.selectFile(renamedFileIndex);
				File imageFile = openedFiles.get(renamedFileIndex);

				try {
					BufferedImage image = ImageIO.read(imageFile);
					String name = FilenameUtils.removeExtension(imageFile.getName());
					filledForm = new FormTemplate(name);
					createFormImageFrame(image, filledForm, Mode.VIEW);
					renameFileFrame = new RenameFileFrame(this, getFileNameByIndex(renamedFileIndex));
					view.arrangeFrame(renameFileFrame);
				} catch (Exception e) {
					logger.debug("Error", e);
				}
			}
			break;
		case RENAME_FILES_CURRENT:
			String newFileName = renameFileFrame.getNewFileName();
			String oldFileName = getFileNameByIndex(renamedFileIndex);

			if (!newFileName.equalsIgnoreCase(oldFileName)) {
				File newFile = renameFile(renamedFileIndex, newFileName);

				updateFileList(renamedFileIndex, newFile);
			}

			fileListFrame.updateFileList(getOpenedFileList());
		case RENAME_FILES_SKIP:
			renamedFileIndex++;

			if (openedFiles.size() > renamedFileIndex) {
				fileListFrame.selectFile(renamedFileIndex);
				File imageFile = openedFiles.get(renamedFileIndex);
				try {
					BufferedImage image = ImageIO.read(imageFile);
					imageFrame.updateImage(image);
					renameFileFrame = new RenameFileFrame(this, getFileNameByIndex(renamedFileIndex));
					view.arrangeFrame(renameFileFrame);
				} catch (Exception e) {
					logger.debug("Error", e);
				}
			} else {
				view.disposeFrame(renameFileFrame);
				view.disposeFrame(imageFrame);

				view.setRenameControllersEnabled(true);
				view.setScanControllersEnabled(true);
				view.setScanAllControllersEnabled(true);
				view.setScanCurrentControllersEnabled(false);
			}
			break;
		default:
			break;
		}
	}

	public void analyzeFiles(String action) {
		if (formTemplate != null) {

			Action act = Action.valueOf(action);
			switch (act) {
			case ANALYZE_FILES_ALL:
				if (!openedFiles.isEmpty()) {
					view.setRenameControllersEnabled(false);
					view.setScanControllersEnabled(false);
					view.setScanAllControllersEnabled(false);
					view.setScanCurrentControllersEnabled(false);

					for (Entry<Integer, File> openedFile : openedFiles.entrySet()) {
						analyzedFileIndex = openedFile.getKey();
						fileListFrame.selectFile(analyzedFileIndex);
						File imageFile = openedFiles.get(analyzedFileIndex);
						try {
							BufferedImage image = ImageIO.read(imageFile);
							String name = FilenameUtils.removeExtension(imageFile.getName());
							filledForm = new FormTemplate(name, formTemplate);
							filledForm.findCorners(image, threshold, density, cornerType, crop);
							filledForm.findPoints(image, threshold, density, shapeSize);
							filledForm.findAreas(image);
							filledForms.put(filledForm.getName(), filledForm);
						} catch (Exception e) {
							logger.debug("Error", e);
						}

					}

					Date today = Calendar.getInstance().getTime();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
					File outputFile = new File(resultsPath
							+ FormScannerTranslation.getTranslationFor(FormScannerTranslationKeys.RESULTS_DEFAULT_FILE)
							+ "_" + sdf.format(today) + ".csv");
					File savedFile = fileUtils.saveCsvAs(outputFile, filledForms, true);
					configurations.setProperty(FormScannerConfigurationKeys.RESULTS_SAVE_PATH,
							FilenameUtils.getFullPath(savedFile.getAbsolutePath()));
					configurations.store();

					view.setRenameControllersEnabled(true);
					view.setScanControllersEnabled(true);
					view.setScanAllControllersEnabled(true);
					view.setScanCurrentControllersEnabled(false);
					resetFirstPass();
				}
				break;
			case ANALYZE_FILES_FIRST:
				if (!openedFiles.isEmpty()) {
					view.setRenameControllersEnabled(false);
					view.setScanAllControllersEnabled(false);
					view.setScanControllersEnabled(true);
					view.setScanCurrentControllersEnabled(true);

					if (firstPass) {
						analyzedFileIndex = fileListFrame.getSelectedItemIndex();
						firstPass = false;
					} else {
						analyzedFileIndex++;
					}

					if (openedFiles.size() > analyzedFileIndex) {
						fileListFrame.selectFile(analyzedFileIndex);
						File imageFile = openedFiles.get(analyzedFileIndex);
						try {
							BufferedImage image = ImageIO.read(imageFile);
							String name = FilenameUtils.removeExtension(imageFile.getName());
							filledForm = new FormTemplate(name, formTemplate);
							filledForm.findCorners(image, threshold, density, cornerType, crop);
							filledForm.findPoints(image, threshold, density, shapeSize);
							filledForm.findAreas(image);
							points = filledForm.getFieldPoints();
							areas = filledForm.getFieldAreas();
							filledForms.put(filledForm.getName(), filledForm);
							createFormImageFrame(image, filledForm, Mode.MODIFY_POINTS);
							createResultsGridFrame();
						} catch (Exception e) {
							logger.debug("Error", e);
						}
					} else {
						Date today = Calendar.getInstance().getTime();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
						File outputFile = new File(resultsPath
								+ FormScannerTranslation
										.getTranslationFor(FormScannerTranslationKeys.RESULTS_DEFAULT_FILE)
								+ "_" + sdf.format(today) + ".csv");
						File savedFile = fileUtils.saveCsvAs(outputFile, filledForms, true);
						configurations.setProperty(FormScannerConfigurationKeys.RESULTS_SAVE_PATH,
								FilenameUtils.getFullPath(savedFile.getAbsolutePath()));
						configurations.store();

						view.disposeFrame(imageFrame);
						view.disposeFrame(resultsGridFrame);

						view.setRenameControllersEnabled(true);
						view.setScanControllersEnabled(true);
						view.setScanAllControllersEnabled(true);
						view.setScanCurrentControllersEnabled(false);
						resetFirstPass();
					}
				}
				break;
			case ANALYZE_FILES_CURRENT:
				fileListFrame.selectFile(analyzedFileIndex);
				File imageFile = openedFiles.get(analyzedFileIndex);

				try {
					BufferedImage image = ImageIO.read(imageFile);
					filledForm = imageFrame.getTemplate();
					filledForm.clearPoints();
					filledForm.findPoints(image, threshold, density, shapeSize);
					filledForm.findAreas(image);
					points = filledForm.getFieldPoints();
					areas = filledForm.getFieldAreas();
					filledForms.put(filledForm.getName(), filledForm);
					createFormImageFrame(image, filledForm, Mode.MODIFY_POINTS);
					createResultsGridFrame();
				} catch (Exception e) {
					logger.debug("Error", e);
				}
				break;
			default:
				break;
			}
		} else {
			JOptionPane.showMessageDialog(null,
					FormScannerTranslation.getTranslationFor(FormScannerTranslationKeys.TEMPLATE_NOT_FOUND),
					FormScannerTranslation.getTranslationFor(FormScannerTranslationKeys.TEMPLATE_NOT_FOUND_POPUP),
					JOptionPane.WARNING_MESSAGE);
		}
	}

	public void createResultsGridFrame() {
		if (resultsGridFrame == null) {
			resultsGridFrame = new ResultsGridFrame(this);
		} else {
			resultsGridFrame.updateResults();
		}
		view.arrangeFrame(resultsGridFrame);
	}

	private void updateFileList(Integer index, File file) {
		openedFiles.remove(index);
		openedFiles.put(index, file);
	}

	private File renameFile(int index, String newFileName) {
		File oldFile = openedFiles.get(index);

		String filePath = FilenameUtils.getFullPath(oldFile.getAbsolutePath());

		File newFile = new File(filePath + newFileName);

		if (newFile.exists()) {
			newFile = oldFile;
		}

		if (!oldFile.renameTo(newFile)) {
			newFile = oldFile;
		}
		return newFile;
	}

	private String[] getOpenedFileList() {
		String[] fileList = new String[openedFiles.size()];

		for (int i = 0; i < openedFiles.size(); i++) {
			fileList[i] = getFileNameByIndex(i);
		}

		return fileList;
	}

	private String getFileNameByIndex(int index) {
		return openedFiles.get(index).getName();
	}

	public void disposeRelatedFrame(InternalFrame frame) {
		Frame frm = Frame.valueOf(frame.getName());
		setLastPosition(frm, frame.getBounds());
		switch (frm) {
		case RENAME_FILES_FRAME:
			view.disposeFrame(imageFrame);
			imageFrame = null;
			break;
		case IMAGE_FRAME:
			view.disposeFrame(renameFileFrame);
			view.disposeFrame(manageTemplateFrame);
			view.disposeFrame(resultsGridFrame);
			renameFileFrame = null;
			manageTemplateFrame = null;
			resultsGridFrame = null;
			resetPoints();
			break;
		case MANAGE_TEMPLATE_FRAME:
			view.disposeFrame(imageFrame);
			imageFrame = null;
			break;
		default:
			break;
		}
	}

	public void loadTemplate() {
		File templateFile = fileUtils.chooseImage();
		if (templateFile != null) {
			try {
				templateImage = ImageIO.read(templateFile);
				String name = FilenameUtils.removeExtension(templateFile.getName());
				formTemplate = new FormTemplate(name);
				formTemplate.findCorners(templateImage, threshold, density, cornerType, crop);
				manageTemplateFrame = new ManageTemplateFrame(this);

				view.arrangeFrame(manageTemplateFrame);
			} catch (Exception e) {
				logger.debug("Error", e);
			}
		}
	}

	public void createTemplateImageFrame(String fieldsType) {
		switch (fieldsType) {
		case FormScannerConstants.QUESTIONS_BY_ROWS:
		case FormScannerConstants.QUESTIONS_BY_COLS:
		case FormScannerConstants.RESPONSES_BY_GRID:
			imageFrame = new ImageFrame(this, templateImage, formTemplate, Mode.SETUP_POINTS);
			break;
		case FormScannerConstants.BARCODE:
			imageFrame = new ImageFrame(this, templateImage, formTemplate, Mode.SETUP_AREA);
			break;
		}
		view.arrangeFrame(imageFrame);
	}

	public void createFormImageFrame(BufferedImage image, FormTemplate template, Mode mode) {
		if (imageFrame == null) {
			imageFrame = new ImageFrame(this, image, template, mode);
		} else {
			imageFrame.updateImage(image, template);
		}
		view.arrangeFrame(imageFrame);
	}

	public void addPoint(ImageFrame view, FormPoint p) {
		FormArea area = null;
		switch (view.getMode()) {
		case SETUP_AREA:
			area = new FormArea();
		case SETUP_POINTS:
			if (manageTemplateFrame != null) {
				int rows = manageTemplateFrame.getRowsNumber();
				int values = manageTemplateFrame.getValuesNumber();

				if (rows == 1 && values == 1) {
					if (points.isEmpty()) {
						points.add(p);
						manageTemplateFrame.setupPositionTable(points);
						manageTemplateFrame.toFront();
					}
				} else {
					if (points.isEmpty() || (points.size() > 1)) {
						resetPoints();
						resetAreas();
						points.add(p);
					} else {
						FormPoint p1 = points.get(0);
						resetPoints();
						resetAreas();

						FormPoint orig = formTemplate.getCorners().get(Corners.TOP_LEFT);
						double rotation = formTemplate.getRotation();

						p1.rotoTranslate(orig, rotation, true);
						p.rotoTranslate(orig, rotation, true);

						HashMap<String, Double> delta = calcDelta(rows, values, p1, p);

						double rowsMultiplier;
						double colsMultiplier;
						for (int i = 0; i < rows; i++) {
							for (int j = 0; j < values; j++) {
								switch (manageTemplateFrame.getFieldType()) {
								case QUESTIONS_BY_COLS:
									rowsMultiplier = j;
									colsMultiplier = i;
									break;
								case QUESTIONS_BY_ROWS:
								default:
									rowsMultiplier = i;
									colsMultiplier = j;
									break;
								}
								FormPoint pi = new FormPoint((p1.getX() + (delta.get("x") * colsMultiplier)),
										(p1.getY() + (delta.get("y") * rowsMultiplier)));
								pi.rotoTranslate(orig, rotation, false);

								points.add(pi);

								if (view.getMode().equals(Mode.SETUP_AREA)) {
									switch ((2 * i) + j) {
									case 0:
										area.setCorner(Corners.TOP_LEFT, pi);
										break;
									case 1:
										area.setCorner(Corners.TOP_RIGHT, pi);
										break;
									case 2:
										area.setCorner(Corners.BOTTOM_LEFT, pi);
										break;
									case 3:
										area.setCorner(Corners.BOTTOM_RIGHT, pi);
										break;
									}
								}
							}
						}

						manageTemplateFrame.setupPositionTable(points);
						manageTemplateFrame.toFront();

						if (view.getMode().equals(Mode.SETUP_AREA)) {
							areas.add(area);
							resetPoints();
						}

						view.setMode(Mode.VIEW);
					}
				}
			}
			break;
		case MODIFY_POINTS:
			filledForm.addPoint(p);
			createResultsGridFrame();
			break;
		default:
			break;
		}
		view.repaint();
	}

	private HashMap<String, Double> calcDelta(int rows, int values, FormPoint p1, FormPoint p2) {
		double dX = Math.abs((p2.getX() - p1.getX()));
		double dY = Math.abs((p2.getY() - p1.getY()));

		double valuesDivider = ((values > 1) ? (values - 1) : 1);
		double questionDivider = ((rows > 1) ? (rows - 1) : 1);

		HashMap<String, Double> delta = new HashMap<>();

		switch (manageTemplateFrame.getFieldType()) {
		case QUESTIONS_BY_COLS:
			delta.put("x", dX / questionDivider);
			delta.put("y", dY / valuesDivider);
			break;
		case QUESTIONS_BY_ROWS:
		default:
			delta.put("x", dX / valuesDivider);
			delta.put("y", dY / questionDivider);
			break;
		}
		return delta;
	}

	public void updateTemplateFields(String groupName, HashMap<String, FormQuestion> fields) {
		formTemplate.addFields(groupName, fields);
		resetPoints();
	}

	public void updateTemplateAreas(String groupName, FormArea area) {
		formTemplate.addArea(groupName, area);
		resetPoints();
		resetAreas();
	}

	private void resetAreas() {
		areas.clear();
	}

	public ArrayList<FormPoint> getPoints() {
		return points;
	}

	public void removeField(String groupName, String fieldName) {
		formTemplate.removeFieldByName(groupName, fieldName);
	}

	public void saveTemplate(boolean notify) {
		formTemplate.setThreshold(threshold);
		formTemplate.setDensity(density);
		formTemplate.setSize(shapeSize);
		formTemplate.setShape(shapeType);
		formTemplate.setCornerType(cornerType);
		formTemplate.setGroupsEnabled(groupsEnabled);
		formTemplate.setCrop(crop);

		File template = fileUtils.saveToFile(templatePath, formTemplate, notify);

		if (template != null) {
			if (notify) {
				JOptionPane.showMessageDialog(null,
						FormScannerTranslation.getTranslationFor(FormScannerTranslationKeys.TEMPLATE_SAVED),
						FormScannerTranslation.getTranslationFor(FormScannerTranslationKeys.TEMPLATE_SAVED_POPUP),
						JOptionPane.INFORMATION_MESSAGE);
			}
			templatePath = FilenameUtils.getFullPath(template.getAbsolutePath());
			String templateFile = FilenameUtils.getName(template.getAbsolutePath());
			configurations.setProperty(FormScannerConfigurationKeys.TEMPLATE, templateFile);
			configurations.setProperty(FormScannerConfigurationKeys.TEMPLATE_SAVE_PATH, templatePath);
			configurations.store();
		} else {
			if (notify) {
				JOptionPane.showMessageDialog(null,
						FormScannerTranslation.getTranslationFor(FormScannerTranslationKeys.TEMPLATE_NOT_SAVED),
						FormScannerTranslation.getTranslationFor(FormScannerTranslationKeys.TEMPLATE_NOT_SAVED_POPUP),
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public void exitFormScanner() {
		view.dispose();
		System.exit(0);
	}

	public boolean openTemplate() {
		return openTemplate(fileUtils.chooseTemplate(), true);
	}

	private boolean openTemplate(File template, boolean notify) {
		if (template == null) {
			return false;
		}

		try {
			formTemplate = new FormTemplate(template);

			threshold = formTemplate.getThreshold() < 0 ? threshold : formTemplate.getThreshold();
			density = formTemplate.getDensity() < 0 ? density : formTemplate.getDensity();
			shapeSize = formTemplate.getSize() < 0 ? shapeSize : formTemplate.getSize();
			shapeType = formTemplate.getShape() == null ? shapeType : formTemplate.getShape();
			cornerType = formTemplate.getCornerType() == null ? cornerType : formTemplate.getCornerType();
			groupsEnabled = formTemplate.isGroupsEnabled();
			usedGroupNamesList = formTemplate.getUsedGroupNames();

			crop = formTemplate.getCrop();

			if (!FormScannerConstants.CURRENT_TEMPLATE_VERSION.equals(formTemplate.getVersion())) {
				saveTemplate(false);
				return true;
			}
			if (notify) {
				JOptionPane.showMessageDialog(null,
						FormScannerTranslation.getTranslationFor(FormScannerTranslationKeys.TEMPLATE_LOADED),
						FormScannerTranslation.getTranslationFor(FormScannerTranslationKeys.TEMPLATE_LOADED_POPUP),
						JOptionPane.INFORMATION_MESSAGE);
			}
			templatePath = FilenameUtils.getFullPath(template.getAbsolutePath());
			String templateFile = FilenameUtils.getName(template.getAbsolutePath());
			configurations.setProperty(FormScannerConfigurationKeys.TEMPLATE, templateFile);
			configurations.setProperty(FormScannerConfigurationKeys.TEMPLATE_SAVE_PATH, templatePath);
			configurations.store();

			return true;
		} catch (ParserConfigurationException | SAXException | IOException | HeadlessException e) {
			if (notify) {
				JOptionPane.showMessageDialog(null,
						FormScannerTranslation.getTranslationFor(FormScannerTranslationKeys.TEMPLATE_NOT_LOADED),
						FormScannerTranslation.getTranslationFor(FormScannerTranslationKeys.TEMPLATE_NOT_LOADED_POPUP),
						JOptionPane.ERROR_MESSAGE);
			}
			logger.debug("Error", e);
			return false;
		}

	}

	public void linkToHelp(URL url) {
		String osName = System.getProperty("os.name").toLowerCase();
		try {

			if (osName.indexOf( "win" ) >= 0) {
				// Windows
				Runtime.getRuntime().exec(
						(new StringBuilder()).append("rundll32 url.dll,FileProtocolHandler ").append(url).toString());
			}
			else if (osName.indexOf( "mac" ) >= 0) {
				// Mac
				Runtime.getRuntime().exec(
						(new StringBuilder()).append("open ").append(url).toString());
			} else if (osName.indexOf( "nix") >=0 || osName.indexOf( "nux") >=0) {
				// Linux/Unix
				String browsers[] = {"firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape", "safari", "links","lynx"};
				String browser = null;
				for (int i = 0; i < browsers.length && browser == null; i++) {
					if (Runtime.getRuntime().exec(new String[] { "which", browsers[i] }).waitFor() == 0) {
						browser = browsers[i];
					}
				}

				if (browser == null) {
					throw new Exception("Could not find web browser");
				}

				Runtime.getRuntime().exec(new String[] { browser, FormScannerConstants.WIKI_PAGE });
			}
		} catch (Exception e) {
			logger.debug("An error occured while trying to open the web browser!", e);
		}
	}

	public void showAboutFrame() {
		InternalFrame aboutFrame = new AboutFrame(this);
		view.arrangeFrame(aboutFrame);
	}

	public String getLanguage() {
		return lang;
	}

	public void setLanguage(String name) {
		configurations.setProperty(FormScannerConfigurationKeys.LANG, name);
		configurations.store();
		JOptionPane.showMessageDialog(null,
				FormScannerTranslation.getTranslationFor(FormScannerTranslationKeys.LANGUAGE_CHANGED),
				FormScannerTranslation.getTranslationFor(FormScannerTranslationKeys.SETTINGS_POPUP),
				JOptionPane.INFORMATION_MESSAGE);
	}

	public void resetPoints() {
		points.clear();
	}

	public void showOptionsFrame() {
		InternalFrame optionsFrame = new OptionsFrame(this);
		view.arrangeFrame(optionsFrame);
	}

	public int getThreshold() {
		return threshold;
	}

	public int getDensity() {
		return density;
	}

	public void saveOptions(OptionsFrame view) {
		threshold = view.getThresholdValue();
		density = view.getDensityValue();
		shapeSize = view.getShapeSize();
		shapeType = view.getShape();
		cornerType = view.getCornerType();
		fontType = view.getFontType();
		fontSize = view.getFontSize();
		lookAndFeel = view.getLookAndFeel();
		resetAutoNumbering = view.isResetAutoNumberingQuestions();
		groupsEnabled = view.isGroupsEnabled();
		groupNameTemplate = view.getGroupNameTemplate(); 
//				historyGroupNameTemplate.get(0);
		questionNameTemplate = view.getQuestionNameTemplate(); 
//				historyQuestionNameTemplate.get(0);
		barcodeNameTemplate = view.getBarcodeNameTemplate(); 
//				historyBarcodeNameTemplate.get(0);
		crop = view.getCrop();

		configurations.setProperty(FormScannerConfigurationKeys.THRESHOLD, String.valueOf(threshold));
		configurations.setProperty(FormScannerConfigurationKeys.DENSITY, String.valueOf(density));
		configurations.setProperty(FormScannerConfigurationKeys.SHAPE_SIZE, String.valueOf(shapeSize));
		configurations.setProperty(FormScannerConfigurationKeys.SHAPE_TYPE, shapeType.getName());
		configurations.setProperty(FormScannerConfigurationKeys.CORNER_TYPE, cornerType.getName());
		configurations.setProperty(FormScannerConfigurationKeys.FONT_TYPE, fontType);
		configurations.setProperty(FormScannerConfigurationKeys.FONT_SIZE, fontSize);
		configurations.setProperty(FormScannerConfigurationKeys.LOOK_AND_FEEL, lookAndFeel);
		configurations.setProperty(FormScannerConfigurationKeys.RESET_AUTO_NUMBERING, resetAutoNumbering);
		configurations.setProperty(FormScannerConfigurationKeys.GROUPS_ENABLED, groupsEnabled);
		configurations.setProperty(FormScannerConfigurationKeys.QUESTION_NAME_TEMPLATE, questionNameTemplate);
		configurations.setProperty(FormScannerConfigurationKeys.BARCODE_NAME_TEMPLATE, barcodeNameTemplate);
		configurations.setProperty(FormScannerConfigurationKeys.GROUP_NAME_TEMPLATE, groupNameTemplate);
		configurations.setProperty(FormScannerConfigurationKeys.HISTORY_GROUP_NAME_TEMPLATE,
				StringUtils.join(historyGroupNameTemplate, HISTORY_SEPARATOR));
		configurations.setProperty(FormScannerConfigurationKeys.HISTORY_QUESTION_NAME_TEMPLATE,
				StringUtils.join(historyQuestionNameTemplate, HISTORY_SEPARATOR));
		configurations.setProperty(FormScannerConfigurationKeys.HISTORY_BARCODE_NAME_TEMPLATE,
				StringUtils.join(historyBarcodeNameTemplate, HISTORY_SEPARATOR));
		configurations.store();

		if (formTemplate != null) {
			saveTemplate(false);
		}
	}

	public void resetFirstPass() {
		firstPass = true;
	}

	public void deleteNearestPointTo(FormPoint cursorPoint) {
		if (filledForm != null) {
			filledForm.removePoint(cursorPoint);
			createResultsGridFrame();
		} else {
			formTemplate.removePoint(cursorPoint);
		}
	}

	public int getShapeSize() {
		return shapeSize;
	}

	public ShapeType getShapeType() {
		return shapeType;
	}

	public CornerType getCornerType() {
		return cornerType;
	}

	public FormTemplate getFilledForm() {
		return filledForm;
	}

	public FormTemplate getTemplate() {
		return formTemplate;
	}

	public Rectangle getLastPosition(Frame frame) {
		Frame frm = Frame.valueOf(frame.name());
		switch (frm) {
		case FILE_LIST_FRAME:
			return fileListFramePosition;
		case RENAME_FILES_FRAME:
			return renameFilesFramePosition;
		case MANAGE_TEMPLATE_FRAME:
			return manageTemplateFramePosition;
		case IMAGE_FRAME:
			return imageFramePosition;
		case RESULTS_GRID_FRAME:
			return resultsGridFramePosition;
		case ABOUT_FRAME:
			return aboutFramePosition;
		case OPTIONS_FRAME:
			return optionsFramePosition;
		case DESKTOP_FRAME:
			return desktopSize;
		default:
			return defaultPosition;
		}
	}

	public void setLastPosition(Frame frame, Rectangle position) {
		Frame frm = Frame.valueOf(frame.name());

		String[] positions = { String.valueOf(position.x), String.valueOf(position.y), String.valueOf(position.width),
				String.valueOf(position.height) };

		configurations.setProperty(frm.getConfigurationKey(), StringUtils.join(positions, ','));
		configurations.store();

		switch (frm) {
		case FILE_LIST_FRAME:
			fileListFramePosition = position;
			break;
		case RENAME_FILES_FRAME:
			renameFilesFramePosition = position;
			break;
		case MANAGE_TEMPLATE_FRAME:
			manageTemplateFramePosition = position;
			break;
		case IMAGE_FRAME:
			imageFramePosition = position;
			break;
		case RESULTS_GRID_FRAME:
			resultsGridFramePosition = position;
			break;
		case ABOUT_FRAME:
			aboutFramePosition = position;
			break;
		case OPTIONS_FRAME:
			optionsFramePosition = position;
			break;
		case DESKTOP_FRAME:
			desktopSize = position;
			break;
		default:
			defaultPosition = position;
			break;
		}
	}

	public Locale getLocale() {
		return locale;
	}

	public ComponentOrientation getOrientation() {
		return orientation;
	}

	public String getFontType() {
		return fontType;
	}

	public Integer getFontSize() {
		return fontSize;
	}

	public ArrayList<FormArea> getAreas() {
		return areas;
	}

	public String getLookAndFeel() {
		return lookAndFeel;
	}

	public void setDesktop(FormScannerDesktop desktop) {
		this.view = desktop;
	}

	public Image getIcon() {
		return defaultIcon;
	}

	public boolean isResetAutoNumberingQuestions() {
		return resetAutoNumbering;
	}

	public int lastIndexOfGroup(String text) {
		return formTemplate.lastIndexOfGroup(text);
	}

	public boolean isGroupsEnabled() {
		return groupsEnabled;
	}

	public String getNameTemplate(String type) {
		switch (type) {
		case FormScannerConstants.BARCODE:
			return barcodeNameTemplate;
		case FormScannerConstants.GROUP:
			return groupNameTemplate;
		case FormScannerConstants.QUESTION:
		default:
			return questionNameTemplate;
		}
	}

	public ArrayList<String> getHistoryNameTemplate(String type) {
		switch (type) {
		case FormScannerConstants.BARCODE:
			return historyBarcodeNameTemplate;
		case FormScannerConstants.GROUP:
			return historyGroupNameTemplate;
		case FormScannerConstants.QUESTION:
			return historyQuestionNameTemplate;
		default:
			return null;
		}
	}

	public HashMap<String, Integer> getCrop() {
		return crop;
	}

	public ArrayList<String> getUsedGroupNamesList() {
		return usedGroupNamesList;
	}
	
	public int getLastGroupIndex() {
		return nextGroupIndex;
	}
	
	public void addUsedGroupName(String groupName) {
		if (usedGroupNamesList.contains(groupName))
			return;
		usedGroupNamesList.add(groupName);
		nextGroupIndex++;
	}

	public void addHistoryNameTemplate(String type, String item) {
		switch (type) {
		case FormScannerConstants.BARCODE:
			if (!historyBarcodeNameTemplate.contains(item))
				historyBarcodeNameTemplate.add(item);
			break;
		case FormScannerConstants.GROUP:
			if (!historyGroupNameTemplate.contains(item))
				historyGroupNameTemplate.add(item);
			break;
		case FormScannerConstants.QUESTION:
			if (!historyQuestionNameTemplate.contains(item))
				historyQuestionNameTemplate.add(item);
			break;
		default:
			break;
		}
	}
}
