package edu.southwestern.util.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.zoo.util.imagenet.ImageNetLabels;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.VGG16ImagePreProcessor;
import org.nd4j.linalg.factory.Nd4j;

import edu.southwestern.networks.dl4j.TensorNetwork;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.stats.StatisticsUtilities;

public class ImageNetClassification {
	public static final int NUM_IMAGE_NET_CLASSES = 1000;
	public static final int IMAGE_NET_INPUT_HEIGHT = 224;
	public static final int IMAGE_NET_INPUT_WIDTH = 224;
	public static final int IMAGE_NET_INPUT_CHANNELS = 3;
	
	// Do not take the time to initialize this if not needed
	private static TensorNetwork imageNet = null; // Default model
	private static ImageNetLabels imageNetLabels = null;
	
	/**
	 * One model can be designated as a command line parameter. This retrieves it.
	 * @return
	 */
	private static TensorNetwork getChosenImageNetModel() {
		if(imageNet == null) initImageNet();
		return imageNet;
	}
	
	/**
	 * Initialize the ImageNet if it hasn't been done yet. This is only done
	 * once because the net weights should never change. Saving the result allows
	 * it to be re-used without re-initialization
	 */
	private static void initImageNet() {
		// This was my attempt to import a pre-trained set of AlexNet weights from a Keras model for ImageNet
//		try {
//			String modelHdf5Filename = "../alexnet_weights.h5";
//			imageNet = KerasModelImport.importKerasSequentialModelAndWeights(modelHdf5Filename);
//		} catch (IOException | InvalidKerasConfigurationException | UnsupportedKerasConfigurationException e) {
//			System.out.println("Could not initialize ImageNet!");
//			e.printStackTrace();
//			System.exit(1);
//		}
		
		try {
			imageNet = (TensorNetwork) ClassCreation.createObject("imageNetModel");
		} catch (NoSuchMethodException e) {
			System.out.println("Could not initialize ImageNet!");
			e.printStackTrace();
			System.exit(1);
		}
		// If image net is being used, then the labels will be needed as well
		imageNetLabels = new ImageNetLabels();
	}
	
	/**
	 * Creates an INDArray from a BufferedImage, assuming that the image needs to be loaded
	 * into a size appropriate for ImageNet.
	 * @param image Buffered Image, such as those generated by CPPNs
	 * @return Image stored in an INDArray
	 */
	public static INDArray bufferedImageToINDArray(BufferedImage image) {
		NativeImageLoader loader = new NativeImageLoader(IMAGE_NET_INPUT_HEIGHT, IMAGE_NET_INPUT_WIDTH, IMAGE_NET_INPUT_CHANNELS);
		try {
			return loader.asMatrix(image);
		} catch (IOException e) {
			System.out.println("Could not convert image to INDArray");
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}
	
	/**
	 * Takes an image represented within an INDArray and returns all of the scores
	 * that ImageNet assigns for each of its 1000 labels. Image Net may need to be
	 * initialized first.
	 * 
	 * @param image Image is a 2D matrix within DL4J's INDArray class
	 * @param preprocess Whether the image/matrix needs to be scaled to the appropriate size for ImageNet
	 * @return map of label to score for each of 1000 labels
	 */
	public static Map<String, Float> getImageNetLabelledPredictions(INDArray image, boolean preprocess) {		
		INDArray currentBatch = getImageNetPredictions(image, preprocess);
		return getImageNetLabelledPredictions(currentBatch);
	}
	
	/**
	 * Take an INDArray already that has already been processes by ImageNet (the output scores)
	 * and assign the ImageNet labels to them.
	 * @param precomputerScores Output of getImageNetPredictions
	 * @return Map of ImageNet labels to scores
	 */
	public static Map<String, Float> getImageNetLabelledPredictions(INDArray precomputedScores) {
		Map<String, Float> result = new HashMap<>();		
		for (int i = 0; i < NUM_IMAGE_NET_CLASSES; i++) {
			//System.out.println(labels.getLabel(i) + ": "+(currentBatch.getFloat(0,i)*100) + "%");
			result.put(imageNetLabels.getLabel(i), precomputedScores.getFloat(0,i));
		}
		return result;
	}
	
	/**
	 * Get raw ImageNet prediction scores from ImageNet without any labels.
	 * Uses the default model specified as a command line parameter.
	 * @param image Image is a 2N matrix within DL4J's INDArray class
	 * @param preprocess Whether the image/matrix needs to be scaled to the appropriate size for ImageNet
	 * @return INDArray of prediction scores for ImageNet's categories/labels
	 */	
	public static INDArray getImageNetPredictions(INDArray image, boolean preprocess) {
		return getImageNetPredictions(getChosenImageNetModel(), image, preprocess);
	}
	
	/**
	 * Get ImageNet predictions with any specified model
	 * @param model A ComputationGraph that takes images and outputs classifications (can this be more general)
	 * @param image Image as 2D INDArray
	 * @param preprocess Whether image pre-processing is required
	 * @return Array of classification scores
	 */
	private static INDArray getImageNetPredictions(TensorNetwork model, INDArray image, boolean preprocess) {
		if(preprocess) {
			imagePreprocess(image);
			// Check pre-processing behavior/results
//			BufferedImage processed = GraphicsUtil.imageFromINDArray(image);
//			DrawingPanel panel = GraphicsUtil.drawImage(processed, "Transformed Image", IMAGE_NET_INPUT_WIDTH, IMAGE_NET_INPUT_HEIGHT);
//			MiscUtil.waitForReadStringAndEnterKeyPress();
//			panel.dispose();
		}
		INDArray predictions = model.output(image);
		return predictions.getRow(0); //.dup(); // Should I duplicate with dup? Worth the load? Needed?
	}
	
	/**
	 * Preprocess an image by scaling it to the correct size.
	 * HOWEVER, are more preprocessing steps needed here? 
	 * Or are model-specific steps needed?
	 * Investigate further.
	 * @param image Image represented as 2D INDArray
	 */
	public static void imagePreprocess(INDArray image) {
		// VGG16 is appropriate for VGG19 as well
		DataNormalization scaler = new VGG16ImagePreProcessor();
		// Not sure that GoogLeNetImagePreprocessor works yet. Note that it also requires different image loading.
		//DataNormalization scaler = new GoogLeNetImagePreprocessor();
		scaler.transform(image);
	}
		
	/**
	 * Get ImageNet label with the highest score in the collection of prediction scores
	 * @param precomputedScores Computed by getImageNetPredictions
	 * @return String label with highest score
	 */
	public static String bestLabel(INDArray precomputedScores) {
		int index = Nd4j.argMax(precomputedScores, 1).getInt(0, 0);
		// Mod division is used because the concat all model has multiple copies of the image labels in the same order
		return imageNetLabels.getLabel(index % NUM_IMAGE_NET_CLASSES);
	}

	/**
	 * Same as above, but for when the scores have been converted to an ArrayList, as required
	 * to store them as a behavior characterization in a Score instance.
	 * @param precomputedScores from a Score, but based off of getImageNetPredictions
	 * @return String label with highest score
	 */
	public static String bestLabel(ArrayList<Double> precomputedScores) {
		int index = StatisticsUtilities.argmax(ArrayUtil.doubleArrayFromList(precomputedScores));
		return imageNetLabels.getLabel(index);
	}
		
	/**
	 * Get best/max score, the one corresponding to the predicted label
	 * @param precomputedScores Computed by getImageNetPredictions
	 * @returnhighest score
	 */
	public static double bestScore(INDArray precomputedScores) {
		return Nd4j.max(precomputedScores, 1).getDouble(0,0);
	}
}
