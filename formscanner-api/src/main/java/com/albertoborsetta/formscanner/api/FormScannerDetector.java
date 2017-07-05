package com.albertoborsetta.formscanner.api;

import com.albertoborsetta.formscanner.api.commons.Constants;
import com.albertoborsetta.formscanner.api.commons.Constants.Corners;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;

import boofcv.abst.feature.associate.AssociateDescription;
import boofcv.abst.feature.associate.ScoreAssociation;
import boofcv.abst.feature.detdesc.DetectDescribePoint;
import boofcv.abst.feature.detect.interest.ConfigFastHessian;
import boofcv.factory.feature.associate.FactoryAssociation;
import boofcv.factory.feature.detdesc.FactoryDetectDescribe;
import boofcv.struct.feature.BrightFeature;
import boofcv.struct.image.GrayF32;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Alberto Borsetta
 * @version 1.2-SNAPSHOT
 */
public class FormScannerDetector implements Callable<FormTemplate> {

	private final File imageFile;
	private final FormScannerDetectorEngine engine;

	private final FormTemplate parent;
	private HashMap<String, FormQuestion> fields;
	private HashMap<String, FormArea> barcodes;
	

	public FormScannerDetector(File imageFile, FormScannerDetectorEngine engine, FormTemplate parent)
			throws IOException {
		this.imageFile = imageFile;
		this.engine = engine;
		this.parent = parent;
		fields = new HashMap<>();
		barcodes = new HashMap<>();
	}

	@Override
	public FormTemplate call() throws Exception {
		FormTemplate filledForm = new FormTemplate(imageFile, parent);
		BufferedImage image = filledForm.getImage();
		engine.process(image);

		HashMap<String, FormGroup> templateGroups = parent.getGroups();
		for (Entry<String, FormGroup> templateGroup : templateGroups.entrySet()) {
			String groupName = templateGroup.getKey();

			HashMap<String, FormQuestion> templateFields = templateGroup.getValue().getFields();

			for (Entry<String, FormQuestion> templateField : templateFields.entrySet()) {
				FormQuestion templateQuestion = templateField.getValue();
				findPoints(image, templateQuestion);
				filledForm.addFields(groupName, fields);
			}
			
			HashMap<String, FormArea> barcodeFields = templateGroup.getValue().getAreas();
			for (Entry<String, FormArea> barcodeField : barcodeFields.entrySet()) {
				FormArea templateArea = barcodeField.getValue();
				findArea(image, templateArea);
			}
			
		}
		
		return filledForm;
	}

	private void findPoints(BufferedImage image, FormQuestion templateQuestion) {
		HashMap<String, FormPoint> templatePoints = templateQuestion.getPoints();
		String fieldName = templateQuestion.getName();

		ArrayList<String> pointNames = new ArrayList<>(templatePoints.keySet());
		Collections.sort(pointNames);

		boolean found = false;
		int count = 0;
		for (String pointName : pointNames) {
			FormPoint responsePoint = engine.calcResponsePoint(templatePoints.get(pointName));
			
			if (engine.isFilled(responsePoint)) {
				found = true;
				count++;
				FormQuestion filledField = getField(fieldName, templateQuestion);

				filledField.setPoint(pointName, responsePoint);
				fields.put(fieldName, filledField);

				if (!templateQuestion.isMultiple()) {
					if (templateQuestion.rejectMultiple() && count > 1) {
						filledField.clearPoints();
						filledField.setPoint(Constants.NO_RESPONSE, Constants.EMPTY_POINT);
						fields.clear();
						fields.put(fieldName, filledField);
						break;
					}
					if (!templateQuestion.rejectMultiple()) {
						break;
					}
				}
			}
		}
		if (!found) {
			FormQuestion filledField = getField(fieldName, templateQuestion);
			filledField.setPoint(Constants.NO_RESPONSE, Constants.EMPTY_POINT);
			fields.put(fieldName, filledField);
		}
	}
	
	private FormQuestion getField(String fieldName, FormQuestion templateQuestion) {
		FormQuestion filledField = fields.get(fieldName);

		if (filledField == null) {
			filledField = new FormQuestion(fieldName);
			filledField.setMultiple(templateQuestion.isMultiple());
			filledField.setType(templateQuestion.getType());
			filledField.setRejectMultiple(templateQuestion.rejectMultiple());
		}

		return filledField;
	}
	
	
	
	public void findArea(BufferedImage image, FormArea templateArea) throws Exception {
		BufferedImage subImage = engine.getAreaImage(templateArea);
		LuminanceSource source = new BufferedImageLuminanceSource(subImage);
		BinaryBitmap bitmap = new BinaryBitmap(new GlobalHistogramBinarizer(source));

		Reader reader = new MultiFormatReader();
		Result resultBarcode = null;

		int attempts = 0;
		boolean lastAttempt = false;
		while ((resultBarcode == null) && !lastAttempt) {
			HashMap<DecodeHintType, Object> hints;
			switch (attempts) {
			case 2:
				bitmap = new BinaryBitmap(new HybridBinarizer(source));
				lastAttempt = true;
			case 1:
				hints = Constants.HINTS;
				break;
			default:
				hints = Constants.HINTS_PURE;
				break;
			}
			try {
				resultBarcode = reader.decode(bitmap, hints);
			} catch (NotFoundException nfe) {
				// Nothing to do
			}
			attempts++;
		}

		FormArea resultArea = engine.calcResultArea(templateArea);
		resultArea.setText(resultBarcode != null
				? resultBarcode.getText() : StringUtils.EMPTY);
		barcodes.put(templateArea.getName(), resultArea);
	}
}