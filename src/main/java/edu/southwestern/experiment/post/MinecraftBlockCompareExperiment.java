package edu.southwestern.experiment.post;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import com.google.common.util.concurrent.CycleDetectingLockFactory.WithExplicitOrdering;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.experiment.Experiment;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;

/**
 * creates two shapes side by side.
 * teleport to -506 100 520 to see shapes
 * shape on the right is the first file shape
 * shape on the left is the second file shape
 * @author lewisj
 *
 */
public class MinecraftBlockCompareExperiment implements Experiment {

	private static String shapeOneFileName;
	private static String shapeTwoFileName;
	
	@Override
	public void init() {
		shapeOneFileName = Parameters.parameters.stringParameter("minecraftBlockListTextFile");
		shapeTwoFileName = Parameters.parameters.stringParameter("minecraftBlockListTextFileSecond");
		System.out.println("Load: "+ shapeOneFileName + " & "+ shapeTwoFileName);
	}

	@Override
	public void run() {
		try {
			File shapeOneTextFile = new File(shapeOneFileName);
			File shapeTwoTextFile = new File(shapeTwoFileName);
			
			//System.out.println("Clear space for both shapes");
			// set up post evaluation corner & generate shapes
			MinecraftClient.clearAreaAroundPostEvaluationCorner(); // May need to be modified to clear space around both corners or just clear a larger area
			generateMultipleShapesFromFiles(shapeOneTextFile, shapeTwoTextFile);
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * takes two text files and generates shapes next to each other in minecraft 
	 * @param shapeOneTextFile the text file containing the blocks of the first shape
	 * @param shapeTwoTextFile the text file containing the blocks of the second shape
	 * @throws FileNotFoundException
	 */
	static public void generateMultipleShapesFromFiles(File shapeOneTextFile, File shapeTwoTextFile) throws FileNotFoundException {
		//System.out.println("inside generate multiple shapes from files");

		// create augmented corner for the second shape 
		MinecraftCoordinates shapeTwoAugmentedEvaluationCorner = new MinecraftCoordinates(MinecraftClient.POST_EVALUATION_SHAPE_CORNER); // augmented Evaluation Corner
		shapeTwoAugmentedEvaluationCorner.t1 = MinecraftClient.POST_EVALUATION_SHAPE_CORNER.t1 - Parameters.parameters.integerParameter("spaceBetweenMinecraftShapes");
		//System.out.println("created evaluation corners. Augmented:"+ shapeTwoAugmentedEvaluationCorner + "original:"+ MinecraftClient.POST_EVALUATION_CORNER);
		
		// creates the final shape list by shifting the blocks of both shapes to the evaluation area
		List<Block> shapeWithShiftedCoordinatesBlockList = shiftBlocks(shapeOneTextFile, MinecraftClient.POST_EVALUATION_SHAPE_CORNER); // sets first shape to POST_EVALUATION_CORNER
		List<Block> finalShapesBlockList = shapeWithShiftedCoordinatesBlockList;	// adds shifted blocks list to final shapes block list
		shapeWithShiftedCoordinatesBlockList = shiftBlocks(shapeTwoTextFile, shapeTwoAugmentedEvaluationCorner);	// creates a list with the shifted blocks of shape 2, shifted based on POST_EVALUATION_CORNER
		finalShapesBlockList.addAll(shapeWithShiftedCoordinatesBlockList); //adds shifted blocks from shape 2 to the final list of blocks for all shapes

		MinecraftClient.getMinecraftClient().spawnBlocks(finalShapesBlockList); // spawns the final shapes in minecraft at the POST_EVALUATION_CORNER

	}

	/**
	 * reads a file, creates a blockList for a shape from that file, shifts all the coordinates to the newEvaluationCorner
	 * shifts the original shape to the new corner, also turns a file into a blocklist
	 * @param shapeTextFile text file that contains the block list of the shape
	 * @param newEvaluationCorner the new corner to shift the shape two
	 * @return the list of blocks shifted to the new corner
	 * @throws FileNotFoundException
	 */
	static List<Block> shiftBlocks(File shapeTextFile, MinecraftCoordinates newEvaluationCorner) throws FileNotFoundException {
		List<Block> shapeOriginalBlockList = MinecraftUtilClass.loadMAPElitesOutputFile(shapeTextFile); // get block list from output file 
		
		//System.out.println("newCorner:"+ newEvaluationCorner);

		// originalShapeCorner contains the corner of the shape from the file
		MinecraftCoordinates originalShapeCorner = MinecraftUtilClass.minCoordinates(shapeOriginalBlockList); // Original (inner/shape) corner for shape two (or close to it)
		//System.out.println("originalCorner:"+ shapeOriginalShapeCorner);

		List<Block> shiftedBlockList = MinecraftUtilClass.shiftBlocksBetweenCorners(shapeOriginalBlockList, originalShapeCorner, newEvaluationCorner); //create list of blocks with shifted coordinates
				
		return shiftedBlockList;
	}

	@Override
	public boolean shouldStop() {
		return false;
	}
	
	public static void main(String[] args) {
		try {
			//MMNEAT.main("minecraftRaceFlyingMachines minecraftBlockListTextFile:testingForRacing\\NS2UD1EW1_61.51928_87197.txt minecraftBlockListTextFileSecond:testingForRacing\\NS3UD3EW0_236.00000_47286.txt".split(" "));
			MMNEAT.main("minecraftRaceFlyingMachines minecraftBlockListTextFile:testingForRacing\\NS0UD2EW0_75.17237_39635.txt minecraftBlockListTextFileSecond:testingForRacing\\NS3UD3EW0_236.00000_47286.txt".split(" "));
		} catch (FileNotFoundException | NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}