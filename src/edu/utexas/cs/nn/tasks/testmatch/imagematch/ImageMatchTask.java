package edu.utexas.cs.nn.tasks.testmatch.imagematch;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javax.imageio.*;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.graphics.DrawingPanel;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.testmatch.MatchDataTask;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.util2D.ILocated2D;
import edu.utexas.cs.nn.util.util2D.Tuple2D;

/**
 *Image match training task for a CPPN
 * 
 * @author gillespl
 */
public class ImageMatchTask<T extends Network> extends MatchDataTask<T> {

	public static final String IMAGE_MATCH_PATH = "data\\imagematch";
	private static final int IMAGE_PLACEMENT = 200;
	private static final int X_COORDINATE_INDEX = 0;
	private static final int Y_COORDINATE_INDEX = 1;
	private static final int DISTANCE_COORDINATE_IND = 2;
	private static final int hIndex = 0;
	private static final int sIndex = 1;
	private static final int bIndex = 2;
	private static final double MAX_COLOR_INTENSITY = 255.0;//this variable needed to scale RGB values to a 0-1 range
	private BufferedImage img = null;
	private int imageHeight, imageWidth;

	/**
	 * Default task constructor
	 */
	public ImageMatchTask() {
		this(Parameters.parameters.stringParameter("matchImageFile"));
		MatchDataTask.pauseForEachCase = false;
	}
	/**
	 * Constructor for ImageMatchTask
	 * 
	 * @param filename name of file pathway for image
	 */
	public ImageMatchTask(String filename) {
		try {//throws and exception if filename is not valid
			img = ImageIO.read(new File(IMAGE_MATCH_PATH + "\\" + filename));
		} catch (IOException e) {
		}
		imageHeight = img.getHeight();
		imageWidth = img.getWidth();
	}

	public Score<T> evaluate(Genotype<T> individual) {
		if(CommonConstants.watch) {
			Network n = individual.getPhenotype();
			double[] hsb = null;
			BufferedImage child = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);

			for(int x = 0; x < imageWidth; x++) {
				for(int y = 0; y < imageHeight; y++) {
					double[] input = {x,y,0,1};
					ImageMatchTask.scale(input, imageWidth, imageHeight);
					ILocated2D distance = new Tuple2D(input[0], input[1]);
					input[DISTANCE_COORDINATE_IND] = distance.distance(new Tuple2D(0,0)) * Math.sqrt(2);
					hsb = n.process(input);
					Color childColor = Color.getHSBColor((float) hsb[hIndex], (float) Math.max(0,Math.min(hsb[sIndex],1)), (float) Math.abs(hsb[bIndex]));
					child.setRGB(x, y, childColor.getRGB());
				}
			}


			DrawingPanel parentPanel = new DrawingPanel(imageWidth, imageHeight, "target");
			DrawingPanel childPanel = new DrawingPanel(imageWidth, imageHeight, "output");
			childPanel.setLocation(imageWidth + IMAGE_PLACEMENT, 0);
			Graphics2D parentGraphics = parentPanel.getGraphics();
			Graphics2D childGraphics = childPanel.getGraphics();

			parentGraphics.drawRenderedImage(img, null);
			childGraphics.drawRenderedImage(child, null);
			Scanner scan = new Scanner(System.in);
			System.out.println("Save image? y/n");
			if(scan.next().equals("y")) {
				System.out.println("enter filename");
				String filename = scan.next();
				childPanel.save(filename + ".bmp");
			}
			scan.close();
			parentPanel.dispose();
			childPanel.dispose();
		}
		return super.evaluate(individual);
	}

	/**
	 * Returns labels for input
	 */
	@Override
	public String[] sensorLabels() {
		return new String[]{"X-coordinate", "Y-coordinate"};
	}

	/**
	 * Returns labels for output
	 */
	@Override
	public String[] outputLabels() {
		return new String[]{"r-value", "g-value", "b-value"};
	}

	@Override
	public int numInputs() {
		return 4;
	}

	@Override
	public int numOutputs() {
		return 3;
	}

	@Override
	public ArrayList<Pair<double[], double[]>> getTrainingPairs() {
		ArrayList<Pair<double[], double[]>> pairs = new ArrayList<Pair<double[], double[]>>();
		for(int i = 0; i < imageWidth; i++) {
			for(int j = 0; j < imageHeight; j++) {
				Color color = new Color(img.getRGB((i/imageWidth)*2 - 1, (j/ imageHeight)*2 - 1));
				pairs.add(new Pair<double[], double[]>(new double[]{i, j}, new double[]
						{color.getRed()/MAX_COLOR_INTENSITY, color.getGreen()/MAX_COLOR_INTENSITY, color.getBlue()/MAX_COLOR_INTENSITY}));
			}
		}
		return pairs;
	}

	public static void scale(double[] toScale, int imageWidth, int imageHeight) {
		toScale[X_COORDINATE_INDEX] = (toScale[0]/imageWidth)*2 - 1;
		toScale[Y_COORDINATE_INDEX] = (toScale[1]/imageHeight)*2 - 1;
	}

	public static void main(String[] args) {

		randomCPPNimage();	

	}



	public static void randomCPPNimage() {

		MMNEAT.clearClasses();
		EvolutionaryHistory.setInnovation(0);
		EvolutionaryHistory.setHighestGenotypeId(0);
		Parameters.initializeParameterCollections(new String[]{"io:false", "netio:false", "allowMultipleFunctions:true", "recurrency:false", "includeHalfLinearPiecewiseFunction:true", "includeSawtoothFunction:true"});
		MMNEAT.loadClasses();

		final int NUM_MUTATIONS = 200;
		final int SIZE = 1000;
		int hIndex = 0;
		int sIndex = 1;
		int bIndex = 2;
		int color = BufferedImage.TYPE_INT_RGB;

		TWEANNGenotype toDraw = new TWEANNGenotype(4, 3, false, 0, 1, 0);
		for(int i = 0; i < NUM_MUTATIONS; i++) {	
			toDraw.mutate();
		}
		TWEANN n = toDraw.getPhenotype();
		BufferedImage child = new BufferedImage(SIZE, SIZE, color);
		double[] hsb = null;
		for(int x = 0; x < SIZE ; x++) {
			for(int y = 0; y < SIZE; y++) {
				double[] input = {x,y,0,1};
				scale(input, SIZE, SIZE);
				ILocated2D distance = new Tuple2D(input[0], input[1]);
				input[2] = distance.distance(new Tuple2D(0,0)) * Math.sqrt(2);
				hsb = n.process(input);
				if(hsb[hIndex] < 0 && hsb[hIndex] > 1|| hsb[sIndex] < 0 && hsb[sIndex] > 1 || hsb[bIndex] < 0 && hsb[bIndex] > 1){
					System.out.println("failed check");
					break;
				}else{
					Color childColor = Color.getHSBColor((float) hsb[hIndex], (float) Math.max(0,Math.min(hsb[sIndex],1)), (float) Math.abs(hsb[bIndex]));
					child.setRGB(x, y, childColor.getRGB());
				}
			}
		}
		System.out.println(Arrays.toString(hsb));
		System.out.println(n.toString());

		DrawingPanel network = new DrawingPanel(SIZE, SIZE, "network");
		n.draw(network);
		DrawingPanel childPanel = new DrawingPanel(SIZE, SIZE, "output");
		Graphics2D childGraphics = childPanel.getGraphics();
		childGraphics.drawRenderedImage(child, null);

		Scanner scan = new Scanner(System.in);
		System.out.println("would you like to save this image? y/n");
		if(scan.next().equals("y")) {
			System.out.println("enter filename");
			String filename = scan.next();
			childPanel.save(filename + ".bmp");
			System.out.println("save network? y/n");
			if(scan.next().equals("y")) {
				network.save(filename + "network.bmp");
			}
		}
		scan.close();
	}

}

