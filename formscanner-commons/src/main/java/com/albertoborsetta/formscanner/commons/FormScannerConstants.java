package com.albertoborsetta.formscanner.commons;

import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;

import com.albertoborsetta.formscanner.api.commons.Constants;
import com.albertoborsetta.formscanner.commons.configuration.FormScannerConfigurationKeys;
import com.albertoborsetta.formscanner.commons.translation.FormScannerTranslationKeys;

public class FormScannerConstants extends Constants {

	// Version
	public static final String VERSION_KEY = "$VERSION";
	public static final String VERSION = "1.1.6-SNAPSHOT";
	public static final String COPYRIGHT_KEY = "$COPYRIGHT";
	public static final String COPYRIGHT = "Copyright 2013-2017";

	// Actions
	public static final String RENAME_FILES_FIRST = "RENAME_FILES_FIRST";
	public static final String RENAME_FILES_CURRENT = "RENAME_FILES_CURRENT";
	public static final String RENAME_FILES_SKIP = "RENAME_FILES_SKIP";
	public static final String OPEN_IMAGES = "OPEN_IMAGES";
	public static final String SAVE_RESULTS = "SAVE_RESULTS";
	public static final String ANALYZE_FILES_ALL = "ANALYZE_FILES_ALL";
	public static final String ANALYZE_FILES_FIRST = "ANALYZE_FILES_FIRST";
	public static final String ANALYZE_FILES_CURRENT = "ANALYZE_FILES_CURRENT";
	public static final String CREATE_TEMPLATE = "LOAD_TEMPLATE";
	public static final String LOAD_TEMPLATE = "USE_TEMPLATE";
	public static final String EDIT_TEMPLATE = "EDIT_TEMPLATE";
	public static final String CONFIRM = "CONFIRM";
	public static final String CANCEL = "CANCEL";
	public static final String SAVE_TEMPLATE = "SAVE_TEMPLATE";
	public static final String REMOVE_FIELD = "REMOVE_FIELD";
	public static final String ADD_FIELD = "ADD_FIELD";
	public static final String EXIT = "EXIT";
	public static final String HELP = "HELP";
	public static final String ABOUT = "ABOUT";
	public static final String LANGUAGE = "LANGUAGE";
	public static final String OPTIONS = "OPTIONS";
	public static final String CORNER_TYPE = "CORNER_TYPE";
	public static final String FONT_TYPE = "FONT_TYPE";
	public static final String FONT_SIZE = "FONT_SIZE";
	public static final String LOOK_AND_FEEL = "LOOK_AND_FEEL";
	public static final String SHAPE_TYPE = "SHAPE_TYPE";

	// Frames
	public static final String RENAME_FILES_FRAME_NAME = "RENAME_FILES_FRAME_NAME";
	public static final String FILE_LIST_FRAME_NAME = "FILE_LIST_FRAME_NAME";
	public static final String MANAGE_TEMPLATE_FRAME_NAME = "MANAGE_TEMPLATE_FRAME_NAME";
	public static final String IMAGE_FRAME_NAME = "IMAGE_FRAME_NAME";
	public static final String ABOUT_FRAME_NAME = "ABOUT_FORM_SCANNER";
	public static final String DESKTOP_NAME = "DESKTOP";
	public static final String OPTIONS_FRAME_NAME = "OPTIONS";
	public static final String RESULTS_GRID_FRAME_NAME = "RESULTS_GRID_FRAME_NAME";

	// Frames positions and size
	private static final Rectangle FILE_LIST_FRAME_POSITION = new Rectangle(10, 10, 200, 600);
	private static final Rectangle RENAME_FILES_FRAME_POSITION = new Rectangle(220, 320, 370, 100);
	private static final Rectangle MANAGE_TEMPLATE_FRAME_POSITION = new Rectangle(100, 100, 600, 500);
	private static final Rectangle IMAGE_FRAME_POSITION = new Rectangle(10, 10, 1000, 600);
	private static final Rectangle RESULTS_GRID_FRAME_POSITION = new Rectangle(100, 100, 230, 300);
	private static final Rectangle ABOUT_FRAME_POSITION = new Rectangle(100, 100, 600, 500);
	private static final Rectangle OPTIONS_FRAME_POSITION = new Rectangle(100, 100, 400, 500);
	private static final Rectangle DESKTOP_SIZE = new Rectangle(0, 0, 1024, 768);

	// Properties
	public static final String IS_MULTIPLE = "IS_MULTIPLE";
	public static final String NUMBER_COLS = "NUMBER_VALUES";
	public static final String NUMBER_ROWS = "NUMBER_COLS_ROWS";
	public static final String TYPE_COMBO_BOX = "TYPE_COMBO_BOX";
	public static final String REJECT_IF_NOT_MULTIPLE = "REJECT_IF_NOT_MULTIPLE";

	// Image Mode
	public static final String VIEW = "VIEW";
	public static final String SETUP_POINTS = "SETUP_POINTS";
	public static final String MODIFY_POINTS = "MODIFY_POINTS";

	// Languages
	public static final String ENGLISH = "ENGLISH";
	public static final String ITALIAN = "ITALIAN";
	public static final String PORTUGUES = "PORTUGUES";
	public static final String SPANISH = "SPANISH";
	public static final String GERMAN = "GERMAN";
	public static final String GREEK = "GREEK";
	public static final String POLISH = "POLISH";
	public static final String FARSI = "FARSI";
	public static final String JAPANESE = "JAPANESE";
	public static final String FRENCH = "FRENCH";
	public static final String ARABIC = "ARABIC";

	public static final String INSTALLATION_ARABIC = "Arabic";
	public static final String INSTALLATION_CHINESE = "Chinese (Simplified)";
	public static final String INSTALLATION_GERMAN = "Deutsch";
	public static final String INSTALLATION_ENGLISH = "English";
	public static final String INSTALLATION_SPANISH = "Espa¤ol";
	public static final String INSTALLATION_FRENCH = "Fran‡ais";
	public static final String INSTALLATION_GREEK = "Greek";
	public static final String INSTALLATION_ITALIAN = "Italiano";
	public static final String INSTALLATION_MAGYAR = "Magyar";
	public static final String INSTALLATION_NEDERLANDS = "Nederlands";
	public static final String INSTALLATION_PORTUGUES = "Portugu^s (Brasil)";
	public static final String INSTALLATION_RUSSIAN = "Russian";
	public static final String INSTALLATION_TURKISH = "T�rk‡e";

	public static final Set<String> ENGLISH_LANGUAGES = new HashSet<>(Arrays.asList(new String[] { INSTALLATION_CHINESE,
			INSTALLATION_ENGLISH, INSTALLATION_MAGYAR, INSTALLATION_RUSSIAN, INSTALLATION_TURKISH }));
	public static final Set<String> ITALIAN_LANGUAGES = new HashSet<>(
			Arrays.asList(new String[] { INSTALLATION_ITALIAN }));
	public static final Set<String> PORTUGUES_LANGUAGES = new HashSet<>(
			Arrays.asList(new String[] { INSTALLATION_PORTUGUES }));
	public static final Set<String> SPANISH_LANGUAGES = new HashSet<>(
			Arrays.asList(new String[] { INSTALLATION_SPANISH }));
	public static final Set<String> GERMAN_LANGUAGES = new HashSet<>(
			Arrays.asList(new String[] { INSTALLATION_GERMAN }));
	public static final Set<String> GREEK_LANGUAGES = new HashSet<>(Arrays.asList(new String[] { INSTALLATION_GREEK }));
	public static final Set<String> POLISH_LANGUAGES = new HashSet<>(Arrays.asList(new String[] {}));
	public static final Set<String> FARSI_LANGUAGES = new HashSet<>(Arrays.asList(new String[] {}));
	public static final Set<String> DUTCH_LANGUAGES = new HashSet<>(
			Arrays.asList(new String[] { INSTALLATION_NEDERLANDS }));
	public static final Set<String> JAPANESE_LANGUAGES = new HashSet<>(Arrays.asList(new String[] {}));
	public static final Set<String> FRENCH_LANGUAGES = new HashSet<>(
			Arrays.asList(new String[] { INSTALLATION_FRENCH }));
	public static final Set<String> ARABIC_LANGUAGES = new HashSet<>(
			Arrays.asList(new String[] { INSTALLATION_ARABIC }));

	
	public static final String WIKI_PAGE = "http://www.formscanner.org/forum";
	public static final String THRESHOLD = "THRESHOLD";
	public static final String DENSITY = "DENSITY";
	public static final String SAVE_OPTIONS = "SAVE_OPTIONS";
	public static final String SHAPE_COMBO_BOX = "SHAPE_COMBO_BOX";
	public static final String SHAPE_SIZE = "SHAPE_SIZE";
	public static final String LOOK_AND_FEEL_COMBO_BOX = "LOOK_AND_FEEL_COMBO_BOX";

	// Zoom
	public static final Integer FIT_WIDTH = 0;
	public static final Integer FIT_PAGE = -1;
	public static final String ZOOM_COMBO_BOX = "ZOOM_COMBO_BOX";

	// Font
	public static final String FONT_TYPE_COMBO_BOX = "FONT_TYPE_COMBO_BOX";
	public static final String FONT_SIZE_COMBO_BOX = "FONT_SIZE_COMBO_BOX";

	// Corner types
	public static final String CORNER_TYPE_COMBO_BOX = "CORNER_TYPE_COMBO_BOX";

	// Groups
	public static final String GROUPS_ENABLED = "GROUPS_ENABLED";
	public static final String RESET_AUTO_NUMBERING = "RESET_AUTO_NUMBERING";
	public static final String GROUP = "GROUP";
	public static final String QUESTION = "QUESTION";
	public static final String GROUP_NAME_TEMPLATE = "GROUP_NAME_TEMPLATE";
	public static final String QUESTION_NAME_TEMPLATE = "QUESTION_NAME_TEMPLATE";
	public static final String BARCODE_NAME_TEMPLATE = "BARCODE_NAME_TEMPLATE";
	public static final String FIELD_TYPE = "FIELD_TYPE";

	// Crop image
	public static final String CROP_FROM_TOP = "CROP_FROM_TOP";
	public static final String CROP_FROM_BOTTOM = "CROP_FROM_BOTTOM";
	public static final String CROP_FROM_LEFT = "CROP_FROM_LEFT";
	public static final String CROP_FROM_RIGHT = "CROP_FROM_RIGHT";

	public enum Frame {

		RENAME_FILES_FRAME(RENAME_FILES_FRAME_NAME, FormScannerConfigurationKeys.RENAME_FILES_FRAME,
				RENAME_FILES_FRAME_POSITION), FILE_LIST_FRAME(FILE_LIST_FRAME_NAME,
						FormScannerConfigurationKeys.FILE_LIST_FRAME, FILE_LIST_FRAME_POSITION), MANAGE_TEMPLATE_FRAME(
								MANAGE_TEMPLATE_FRAME_NAME, FormScannerConfigurationKeys.MANAGE_TEMPLATE_FRAME,
								MANAGE_TEMPLATE_FRAME_POSITION), IMAGE_FRAME(IMAGE_FRAME_NAME,
										FormScannerConfigurationKeys.IMAGE_FRAME,
										IMAGE_FRAME_POSITION), RESULTS_GRID_FRAME(RESULTS_GRID_FRAME_NAME,
												FormScannerConfigurationKeys.RESULTS_GRID_FRAME,
												RESULTS_GRID_FRAME_POSITION), OPTIONS_FRAME(OPTIONS_FRAME_NAME,
														FormScannerConfigurationKeys.OPTIONS_FRAME,
														OPTIONS_FRAME_POSITION), ABOUT_FRAME(ABOUT_FRAME_NAME,
																FormScannerConfigurationKeys.ABOUT_FRAME,
																ABOUT_FRAME_POSITION), DESKTOP_FRAME(DESKTOP_NAME,
																		FormScannerConfigurationKeys.DESKTOP_FRAME,
																		DESKTOP_SIZE);

		private final String value;
		private final String key;
		private final Rectangle position;

		private Frame(String value, String key, Rectangle position) {
			this.value = value;
			this.key = key;
			this.position = position;
		}

		public String getValue() {
			return value;
		}

		public String getConfigurationKey() {
			return key;
		}

		public Rectangle getDefaultPosition() {
			return position;
		}
	}

	public enum Action {

		RENAME_FILES_FIRST, 
		RENAME_FILES_CURRENT, 
		RENAME_FILES_SKIP, 
		OPEN_IMAGES, 
		SAVE_RESULTS, 
		ANALYZE_FILES_FIRST, 
		LOAD_TEMPLATE, 
		USE_TEMPLATE, 
		EDIT_TEMPLATE, 
		CONFIRM, 
		CANCEL, 
		SAVE_TEMPLATE, 
		REMOVE_FIELD, 
		ADD_FIELD, 
		HELP, 
		ABOUT, 
		LANGUAGE, 
		EXIT, 
		TOP_LEFT, 
		TOP_RIGHT, 
		BOTTOM_LEFT, 
		BOTTOM_RIGHT, 
		OPTIONS, 
		SAVE_OPTIONS, 
		ANALYZE_FILES_ALL, 
		ANALYZE_FILES_CURRENT, 
		IS_MULTIPLE, 
		GROUPS_ENABLED, 
		RESET_AUTO_NUMBERING, 
		BARCODE, 
		QUESTION, 
		GROUP,
		SHAPE_TYPE,
		FONT_TYPE,
		FONT_SIZE,
		LOOK_AND_FEEL,
		FIELD_TYPE,
		CORNER_TYPE;
	}

	public enum Mode {

		VIEW, SETUP_POINTS, MODIFY_POINTS, SETUP_AREA;
	}

	public enum Zoom {

		ZOOM_25(25), ZOOM_50(50), ZOOM_75(75), ZOOM_100(100), ZOOM_125(125), ZOOM_150(150), ZOOM_200(200), ZOOM_WIDTH(
				FormScannerConstants.FIT_WIDTH), ZOOM_PAGE(FormScannerConstants.FIT_PAGE);

		private final Integer value;

		private Zoom(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}
	}

	public enum FontSize {

		SIZE_10(10), SIZE_12(12), SIZE_14(14), SIZE_16(16), SIZE_20(20), SIZE_24(24), SIZE_30(30), SIZE_36(36);

		private final Integer value;

		private FontSize(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}
	}

	// Languages
	public enum Language {

		ENGLISH("en", FormScannerTranslationKeys.ENGLISH, ENGLISH_LANGUAGES), ITALIAN("it",
				FormScannerTranslationKeys.ITALIAN, ITALIAN_LANGUAGES), PORTUGUES("pt_BR",
						FormScannerTranslationKeys.PORTUGUES, PORTUGUES_LANGUAGES), SPANISH("es",
								FormScannerTranslationKeys.SPANISH, SPANISH_LANGUAGES), GERMAN("de",
										FormScannerTranslationKeys.GERMAN, GERMAN_LANGUAGES), FARSI("fa",
												FormScannerTranslationKeys.FARSI,
												FARSI_LANGUAGES), POLISH("pl", FormScannerTranslationKeys.POLISH,
														POLISH_LANGUAGES), GREEK("el", FormScannerTranslationKeys.GREEK,
																GREEK_LANGUAGES), DUTCH("nl",
																		FormScannerTranslationKeys.DUTCH,
																		DUTCH_LANGUAGES), JAPANESE("jp",
																				FormScannerTranslationKeys.JAPANESE,
																				JAPANESE_LANGUAGES), FRENCH("fr",
																						FormScannerTranslationKeys.FRENCH,
																						FRENCH_LANGUAGES), ARABIC("ar",
																								FormScannerTranslationKeys.ARABIC,
																								ARABIC_LANGUAGES);

		private final String value;
		private final String translation;
		private final Set<String> installationLanguages;

		private Language(String value, String translation, Set<String> installationLanguages) {
			this.value = value;
			this.translation = translation;
			this.installationLanguages = installationLanguages;
		}

		public String getValue() {
			return value;
		}

		public String getTranslation() {
			return translation;
		}

		public Set<String> getInstallationLanguages() {
			return installationLanguages;
		}
	}

	public enum FieldsTableColumn {

		GROUP_COLUMN(FormScannerTranslationKeys.GROUP_COLUMN), NAME_COLUMN(
				FormScannerTranslationKeys.NAME_COLUMN), TYPE_COLUMN(
						FormScannerTranslationKeys.TYPE_COLUMN), MULTIPLE_COLUMN(
								FormScannerTranslationKeys.MULTIPLE_COLUMN), RESPONSES_COLUMN(
										FormScannerTranslationKeys.RESPONSES_COLUMN);

		private final String value;

		private FieldsTableColumn(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}
}
