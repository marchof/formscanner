package com.albertoborsetta.formscanner.api;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.ddogleg.struct.FastQueue;
import org.ejml.data.DenseMatrix64F;

import boofcv.abst.feature.associate.AssociateDescription;
import boofcv.abst.feature.detdesc.DetectDescribePoint;
import boofcv.alg.descriptor.UtilFeature;
import boofcv.alg.distort.PointTransformHomography_F64;
import boofcv.alg.geo.h.HomographyLinear4;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.feature.AssociatedIndex;
import boofcv.struct.feature.TupleDesc;
import boofcv.struct.geo.AssociatedPair;
import boofcv.struct.image.ImageGray;
import georegression.struct.point.Point2D_F64;

public class DetectorEngine<T extends ImageGray, TD extends TupleDesc> {

	private DetectDescribePoint<T, TD> detDesc;
	private AssociateDescription<TD> associate;

	private ArrayList<Point2D_F64> pointsA;
	private ArrayList<Point2D_F64> pointsB;
	
	private FastQueue<TD> descA;
	private FastQueue<TD> descB;

	private Class<T> imageType;
	
	private PointTransformHomography_F64 th;
	
	private List<AssociatedIndex> matchesList;

	public DetectorEngine(DetectDescribePoint<T, TD> detDesc,
									  AssociateDescription<TD> associate,
									  Class<T> imageType) {
			this.detDesc = detDesc;
			this.associate = associate;
			this.imageType = imageType;
			
			pointsA = new ArrayList<>();
			descA = UtilFeature.createQueue(detDesc, 100);

			pointsB = new ArrayList<>();
			descB = UtilFeature.createQueue(detDesc, 100);
		}
	
	public void process() {
		associate();		
		ArrayList<AssociatedPair> p = calculateBestMatches();
		calculateHomography(p);
	}

	private void calculateHomography(ArrayList<AssociatedPair> p) {
		HomographyLinear4 linear = new HomographyLinear4(true);
		DenseMatrix64F matrix = new DenseMatrix64F(3,3);
		linear.process(p, matrix);

		th = new PointTransformHomography_F64(matrix);
	}

	private ArrayList<AssociatedPair> calculateBestMatches() {
		FastQueue<AssociatedIndex> matches = associate.getMatches();
		matchesList = matches.toList();
		sortMatches();
		ArrayList<AssociatedPair> pairs = new ArrayList<>();
		for (int i=0; i<4; i++) {
			pairs.add(i, new AssociatedPair(pointsA.get(matchesList.get(i).src), pointsB.get(matchesList.get(i).dst)));
		}

		//		double minScore = matches.get(0).fitScore;
//		for (int i = 0; i < matches.getSize(); i++) {
//			double currentScore = matches.get(i).fitScore;
//			minScore = (currentScore < minScore) ? currentScore : minScore;
//		}
//		for (int i = 0; i < matches.getSize(); i++) {
//			AssociatedIndex ai = matches.get(i); 
//			AssociatedPair p = new AssociatedPair(pointsA.get(ai.src), pointsB.get(ai.dst));
//			pairs.add(p);
//		}
		return pairs;
	}

	private void associate() {
		associate.setSource(descA);
		associate.setDestination(descB);
		associate.associate();
	}

	private void describeImage(T input, ArrayList<Point2D_F64> points, FastQueue<TD> descs) {
		detDesc.detect(input);

		for (int i = 0; i < detDesc.getNumberOfFeatures(); i++) {
			points.add(detDesc.getLocation(i).copy());
			descs.grow().setTo(detDesc.getDescription(i));
		}
	}

	public void describeSourceImage(BufferedImage image) {
		T inputA = ConvertBufferedImage.convertFromSingle(image, null, imageType);
		describeImage(inputA, pointsA, descA);
	}

	public void describeDestinationImage(BufferedImage image) {
		T inputB = ConvertBufferedImage.convertFromSingle(image, null, imageType);
		describeImage(inputB, pointsB, descB);
	}

	public FormPoint transform(FormPoint responsePoint) {
		Point2D_F64 p = new Point2D_F64();
		th.compute(responsePoint.getX(), responsePoint.getY(), p);
		return new FormPoint(p.getX(), p.getY());
	}
	
	public void sortMatches() {
		sortMatches(0, matchesList.size());
	}
	
	private void sortMatches(int min, int max) {
		int size = (max - min) + 1;
		if (size > 20) {
			int middle = (min + max) / 2;
			sortMatches(min, middle);
			sortMatches(middle, max);
			mergeMatches(min, middle, max);
		} else {
			insertionSortMatches(min, max);
		}
	}

	private void insertionSortMatches(int min, int max) {
		for (int i = min+1; i < max; i++) {
			int x = i;
			int j = i - 1;
			while (j >= min) {
				AssociatedIndex aj = matchesList.get(j);
				AssociatedIndex ax = matchesList.get(x);
				if (ax.fitScore < aj.fitScore) {
					AssociatedIndex ai = matchesList.get(i);
					matchesList.set(x, aj);
					matchesList.set(j, ai);
					x = j;
					j--;
				} else
					break;
			}
		}
	}

	private void mergeMatches(int min, int middle, int max) {
		ArrayList<AssociatedIndex> tempList = new ArrayList<>();

		int i1 = min;
		int i2 = middle;
		int i3 = 0;
		while ((i1 <= middle) && (i2 < max)) {
			AssociatedIndex ai1 = matchesList.get(i1);
			AssociatedIndex ai2 = matchesList.get(i2);
			if (ai1.fitScore < ai2.fitScore) {
				tempList.add(i3, ai1);
				i1++;
			} else {
				tempList.add(i3, ai2);
				i2++;
			}
			i3++;
		}
		while (i1 <= middle) {
			AssociatedIndex ai1 = matchesList.get(i1);
			tempList.add(i3, ai1);
			i3++;
			i1++;
		}
		while (i2 < max) {
			AssociatedIndex ai2 = matchesList.get(i2);
			tempList.add(i3, ai2);
			i3++;
			i2++;
		}
		
		i3 = 0;
		for (i1 = min; i1 < max; i1++) {
			matchesList.set(i1, tempList.get(i3));
			i3++;
		}
	}
}
