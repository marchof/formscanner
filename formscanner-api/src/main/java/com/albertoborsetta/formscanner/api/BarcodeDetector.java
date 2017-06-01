package com.albertoborsetta.formscanner.api;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;

import com.albertoborsetta.formscanner.api.commons.Constants;
import com.albertoborsetta.formscanner.api.commons.Constants.Corners;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Reader;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.Result;

/**
*
* @author Alberto Borsetta
* @version 1.1.4
*/
public class BarcodeDetector extends FormScannerDetector
		implements Callable<HashMap<String, FormArea>> {

	private final FormArea barcodeArea;
	private final HashMap<String, FormArea> barcodes;

	public BarcodeDetector(FormTemplate template, FormArea barcodeArea, BufferedImage image) {
		super(image, template);
		this.barcodeArea = barcodeArea;
		barcodes = new HashMap<>();
	}

	@Override
	public HashMap<String, FormArea> call() throws Exception {
		BufferedImage subImage = getAreaImage(image);
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
				// Try again with other binarizer
				bitmap = new BinaryBitmap(new HybridBinarizer(source));
				lastAttempt = true;
			case 1:
				// Look for normal barcode in photo
				hints = Constants.HINTS;
				break;
			default:
				// Look for pure barcode
				hints = Constants.HINTS_PURE;
				break;
			}
			try {
				resultBarcode = reader.decode(bitmap, hints);
			} catch (Exception e) {
				// Nothing to do
			}
			attempts++;
		}

		FormArea resultArea = calcResultArea();
		resultArea.setText(resultBarcode != null
				? resultBarcode.getText() : StringUtils.EMPTY);
		barcodes.put(barcodeArea.getName(), resultArea);
		return barcodes;
	}
	
	private BufferedImage getAreaImage(BufferedImage image) {
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

	private FormArea calcResultArea() {
		FormArea responseArea = new FormArea(barcodeArea.getName());

		for (Corners corner : Corners.values()) {
			responseArea.setCorner(corner, calcResponsePoint(barcodeArea.getCorner(corner)));
		}

		responseArea.setType(barcodeArea.getType());
		return responseArea;
	}
}
