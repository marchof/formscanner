package org.albertoborsetta.formscanner.gui;

import org.albertoborsetta.formscanner.controller.AnalyzeImageController;
import org.albertoborsetta.formscanner.controller.InternalFrameController;
import org.albertoborsetta.formscanner.controller.RenameImageController;
import org.albertoborsetta.formscanner.model.FormScannerModel;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.BorderLayout;
import java.awt.image.BufferedImage;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;


public class AnalyzeFileImageFrame extends JInternalFrame {
	
	private static final long serialVersionUID = 1L;
	
	private ImagePanel imagePanel;
	private ImageScrollPane scrollPane;
	private FormScannerModel formScannerModel;
	private AnalyzeImageController analyzeImageController;
	private InternalFrameController internalFrameController;
	

	/**
	 * Create the frame.
	 */
	public AnalyzeFileImageFrame(FormScannerModel formScannerModel, File file) {
		this.formScannerModel = formScannerModel;
		analyzeImageController = new AnalyzeImageController(formScannerModel);
		analyzeImageController.add(this);
		internalFrameController = InternalFrameController.getInstance(formScannerModel);
		
		setClosable(true);
		setName("analyzeImageFrame");
		addInternalFrameListener(internalFrameController);
		setIconifiable(true);
		setResizable(true);
		setMaximizable(true);
		setTitle("Rename file image");
		
		int desktopWidth = formScannerModel.getDesktopSize().width;
		setBounds(220, 10, desktopWidth - 230, 300);
		
		imagePanel = new ImagePanel(file);
		scrollPane = new ImageScrollPane(imagePanel);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
	}
	
	private class ImageScrollPane extends JScrollPane {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ImageScrollPane(JPanel imagePanel) {
			super(imagePanel);
			verticalScrollBar.setValue(0);
			horizontalScrollBar.setValue(0);
			addMouseMotionListener(analyzeImageController);
			addMouseListener(analyzeImageController);
		}
		
		public void setScrollBars(int deltaX, int deltaY) {
			horizontalScrollBar.setValue(horizontalScrollBar.getValue() + deltaX);
			verticalScrollBar.setValue(verticalScrollBar.getValue() + deltaY);			
		}
	}
	
	private class ImagePanel extends JPanel {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private BufferedImage image;
		
		public ImagePanel(File file) {
			super();
			setImage(file);
			setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
		}
		
		@Override
	    public void paintComponent(Graphics g) {
			setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
	        g.drawImage(image, 0, 0, this);            
	    }
		
		public void setImage(File file) {
			try {		
				image = ImageIO.read(file);
			} catch (IOException ex) {
				image = null;
			}
		}
	}
	
	public void updateImage(File file) {
		imagePanel.setImage(file);
		update(getGraphics());
	}
	
	public void setScrollBars(int deltaX, int deltaY) {
		scrollPane.setScrollBars(deltaX, deltaY);
	}
	
	public void setImageCursor(int moveCursor) {
		Cursor cursor = Cursor.getPredefinedCursor(moveCursor);
		scrollPane.setCursor(cursor);
	}
}
