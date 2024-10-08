package com.albertoborsetta.formscanner.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;

import org.apache.commons.lang3.StringUtils;

import com.albertoborsetta.formscanner.commons.FormScannerConstants;
import com.albertoborsetta.formscanner.commons.FormScannerConstants.Frame;
import com.albertoborsetta.formscanner.commons.resources.FormScannerResources;
import com.albertoborsetta.formscanner.commons.resources.FormScannerResourcesKeys;
import com.albertoborsetta.formscanner.commons.translation.FormScannerTranslation;
import com.albertoborsetta.formscanner.commons.translation.FormScannerTranslationKeys;
import com.albertoborsetta.formscanner.controller.AboutFrameController;
import com.albertoborsetta.formscanner.gui.builder.ButtonBuilder;
import com.albertoborsetta.formscanner.gui.builder.PanelBuilder;
import com.albertoborsetta.formscanner.gui.builder.TabbedPaneBuilder;
import com.albertoborsetta.formscanner.model.FormScannerModel;

public class AboutFrame extends InternalFrame {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private static AboutFrameController aboutFrameController;

	/**
	 * Create the frame.
	 * 
	 * @param model
	 */
	public AboutFrame(FormScannerModel model) {
		super(model);

		aboutFrameController = new AboutFrameController(this.model);
		aboutFrameController.add(this);

		setName(Frame.ABOUT_FRAME.name());
		setTitle(FormScannerTranslation.getTranslationFor(FormScannerTranslationKeys.ABOUT_FRAME_TITLE));
		setBounds(model.getLastPosition(Frame.ABOUT_FRAME));
		setMinimumSize(new Dimension(300, 500));
		setResizable(false);
		setFrameIcon(FormScannerResources.getIconFor(FormScannerResourcesKeys.ABOUT_ICON_16));

		JPanel aboutPanel = getAboutPanel();
		JPanel licensePanel = getLicensePanel();
		JPanel buttonPanel = getButtonPanel();

		JTabbedPane tabbedPane = new TabbedPaneBuilder(JTabbedPane.TOP, orientation)
				.addTab(FormScannerTranslation.getTranslationFor(FormScannerTranslationKeys.ABOUT_TAB_NAME), aboutPanel)
				.addTab(FormScannerTranslation.getTranslationFor(FormScannerTranslationKeys.LICENSE_TAB_NAME),
						licensePanel)
				.build();
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		tabbedPane.setEnabledAt(1, true);
	}

	private JPanel getLicensePanel() {
		JScrollPane licenseTextPanel = getLiceseTextPanel();

		return new PanelBuilder(orientation).withLayout(new BorderLayout())
				.addComponent(licenseTextPanel, BorderLayout.CENTER).build();
	}

	private static JScrollPane getLiceseTextPanel() {
		JTextArea textArea = new JTextArea(300, 500);
		textArea.setEditable(false);
		textArea.setTabSize(4);
		textArea.getCaret().setDot(0);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEnabled(true);
		textArea.append(FormScannerResources.getLicense());
		textArea.getCaret().setDot(0);
		

		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setPreferredSize(new Dimension(400, 300));
		scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
		scrollPane.setAlignmentY(Component.TOP_ALIGNMENT);

		return scrollPane;
	}

	private JPanel getAboutPanel() {
		JLabel logo = new JLabel(FormScannerResources.getIconFor(FormScannerResourcesKeys.FORMSCANNER_SPLASH));

		JScrollPane aboutTextPanel = getAboutTextPanel();

		return new PanelBuilder(orientation).withLayout(new BorderLayout()).withBackgroundColor(Color.white)
				.addComponent(logo, BorderLayout.CENTER).addComponent(aboutTextPanel, BorderLayout.SOUTH).build();
	}

	private JScrollPane getAboutTextPanel() {
		JEditorPane text = new JEditorPane();
		text.setAlignmentX(Component.CENTER_ALIGNMENT);
		text.setAlignmentY(Component.TOP_ALIGNMENT);
		text.setContentType("text/html");
		text.setOpaque(true);
		text.addHyperlinkListener(aboutFrameController);
		String aboutText = FormScannerTranslation.getTranslationFor(FormScannerTranslationKeys.ABOUT_TEXT);
		aboutText = StringUtils.replace(aboutText,
				FormScannerConstants.VERSION_KEY, FormScannerConstants.VERSION);
		aboutText = StringUtils.replace(aboutText,
				FormScannerConstants.COPYRIGHT_KEY, FormScannerConstants.COPYRIGHT);
		text.setText(aboutText);
		text.setEditable(false);
		text.getCaret().setDot(0);

		JScrollPane scrollPane = new JScrollPane(text);
		scrollPane.setPreferredSize(new Dimension(400, 300));
		scrollPane.getVerticalScrollBar().setValue(0);

		return scrollPane;
	}

	private JPanel getButtonPanel() {
		JButton okButton = new ButtonBuilder(orientation)
				.withText(FormScannerTranslation.getTranslationFor(FormScannerTranslationKeys.OK_BUTTON))
				.withToolTip(FormScannerTranslation.getTranslationFor(FormScannerTranslationKeys.OK_BUTTON_TOOLTIP))
				.withActionCommand(FormScannerConstants.CONFIRM).withActionListener(aboutFrameController)
				.setEnabled(true).build();

		JPanel innerButtonPanel = new PanelBuilder(orientation).withLayout(new SpringLayout()).addComponent(okButton)
				.withGrid(1, 1).build();

		return new PanelBuilder(orientation).withLayout(new BorderLayout()).add(innerButtonPanel, BorderLayout.EAST)
				.build();
	}
}
