package com.albertoborsetta.formscanner.api;

import com.albertoborsetta.formscanner.api.commons.Constants;
import com.albertoborsetta.formscanner.api.commons.Constants.Corners;

import boofcv.abst.feature.associate.AssociateDescription;
import boofcv.abst.feature.associate.ScoreAssociation;
import boofcv.abst.feature.detdesc.DetectDescribePoint;
import boofcv.abst.feature.detect.interest.ConfigFastHessian;
import boofcv.factory.feature.associate.FactoryAssociation;
import boofcv.factory.feature.detdesc.FactoryDetectDescribe;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.feature.BrightFeature;
import boofcv.struct.feature.TupleDesc;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.ImageGray;
import georegression.struct.point.Point2D_F64;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.ddogleg.struct.FastQueue;
import org.ejml.data.DenseMatrix64F;

/**
 *
 * @author Alberto Borsetta
 * @version 1.2-SNAPSHOT
 */
public class FormScannerDetectorEngine {

    private final int threshold;
    private final int density;
    private final int size;
    
    private BufferedImage image;

    private DetectorEngine<GrayF32, BrightFeature> detectorEngine;
    
    public FormScannerDetectorEngine(int threshold, int density, int size, BufferedImage image) {
    	Class<GrayF32> imageType = GrayF32.class;
		DetectDescribePoint<GrayF32, BrightFeature> detDesc = FactoryDetectDescribe.surfStable(new ConfigFastHessian(1, 10, 300, 1, 9, 4, 4), null, null, imageType);
		ScoreAssociation<BrightFeature> scorer = FactoryAssociation.defaultScore(detDesc.getDescriptionType());
		AssociateDescription<BrightFeature> associate = FactoryAssociation.greedy(scorer, Double.MAX_VALUE, true);

		detectorEngine = new DetectorEngine<GrayF32, BrightFeature>(detDesc, associate, imageType);
		
		detectorEngine.describeSourceImage(image);

//        this.template = template;
//        parent = template != null ? template.getParentTemplate() : null;
		
        this.threshold = threshold;
        this.density = density;
        this.size = size; 
    }
    
    protected void process(BufferedImage image) {
    	this.image = image;
    	detectorEngine.describeDestinationImage(image);
    	detectorEngine.process();
    }
    
	protected FormPoint calcResponsePoint(FormPoint responsePoint) {
		return detectorEngine.transform(responsePoint);
    }
	
	protected boolean isFilled(FormPoint responsePoint) {
		int total = size * size;
		int halfSize = size / 2;
		int[] rgbArray = new int[total];
		int count = 0;
		
		int xCoord = (int) responsePoint.getX();
		int yCoord = (int) responsePoint.getY();

		image.getRGB(xCoord - halfSize, yCoord - halfSize, size, size, rgbArray, 0, size);

		for (int i = 0; i < total; i++) {
			if ((rgbArray[i] & (0xFF)) < threshold) {
				count++;
			}
		}
		return (count / (double) total) >= (density / 100.0);
	}
	
	protected BufferedImage getAreaImage(FormArea barcodeArea) {
		FormPoint topLeftCorner = calcResponsePoint(barcodeArea.getCorner(Corners.TOP_LEFT)); 
		FormPoint bottomLeftCorner = calcResponsePoint(barcodeArea.getCorner(Corners.BOTTOM_LEFT));
		FormPoint topRightCorner = calcResponsePoint(barcodeArea.getCorner(Corners.TOP_RIGHT));
		FormPoint bottomRightCorner = calcResponsePoint(barcodeArea.getCorner(Corners.BOTTOM_RIGHT));
		
		int minX = (int) Math.min(topLeftCorner.getX(), bottomLeftCorner.getX());
		int minY = (int) Math.min(topLeftCorner.getY(), topRightCorner.getY());
		int maxX = (int) Math.max(topRightCorner.getX(), bottomRightCorner.getX());
		int maxY = (int) Math.max(bottomLeftCorner.getY(), bottomRightCorner.getY());
		int subImageWidth = maxX - minX;
		int hsubImageHeight = maxY - minY;
		BufferedImage subImage = image.getSubimage(
				minX, minY, subImageWidth, hsubImageHeight);
		return subImage;
	}
	
	protected FormArea calcResultArea(FormArea barcodeArea) {
		FormArea responseArea = new FormArea(barcodeArea.getName());

		for (Corners corner : Corners.values()) {
			responseArea.setCorner(corner, calcResponsePoint(barcodeArea.getCorner(corner)));
		}

		responseArea.setType(barcodeArea.getType());
		return responseArea;
	}
}
