package edu.southwestern.tasks.loderunner;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.swing.SwingUtilities;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.loderunner.astar.LodeRunnerState;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.tasks.mario.gan.reader.JsonReader;
import edu.southwestern.util.random.RandomNumbers;
import icecreamyou.LodeRunner.LodeRunner;

/**
 * Runs the GAN process and the main method renders a single level that was generated by the GAN 
 * @author kdste
 *
 */
public class LodeRunnerGANUtil {
	//public static final int LODE_RUNNER_ORIGINAL_TILE_NUMBER = 8; //number of tiles in LodeRunner 
	//public static final int LATENT_VECTOR_SIZE = 10;//latent vector dimension, original size 
	public static final int LODE_RUNNER_ONE_GROUND_TILE_NUMBER = 6; //number of tiles in LodeRunner
	public static final int LODE_RUNNER_ALL_GROUND_TILE_NUMBER = 7; //number of tiles in LodeRunner

	/**
	 * Renders a level that was generated by the GAN 
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		final int LATENT_VECTOR_SIZE = 10;//latent vector dimension, 20 improved the model 
		
		GANProcess.type = GANProcess.GAN_TYPE.LODE_RUNNER; //sets GAN type to LodeRunner
		Parameters.initializeParameterCollections(new String[] {"GANInputSize:"+LATENT_VECTOR_SIZE});//input size is the size of the latent vector
		double[] latentVector = RandomNumbers.randomArray(LATENT_VECTOR_SIZE); //fills array of input size randomly
		List<List<Integer>> oneLevel = generateOneLevelListRepresentationFromGAN(latentVector); //one level to render
		List<Point> emptySpaces = fillEmptyList(oneLevel);
		Random rand = new Random(Double.doubleToLongBits(latentVector[0]));
		setSpawn(oneLevel, emptySpaces, rand);
////		BufferedImage[] images = LodeRunnerRenderUtil.loadImages(LodeRunnerRenderUtil.LODE_RUNNER_TILE_PATH); //Initializes the array that hold the tile images
////		LodeRunnerRenderUtil.getBufferedImage(oneLevel, images);//rendered level and displays it in a window 
//		BufferedImage[] images = LodeRunnerRenderUtil.loadImagesNoSpawnTwoGround(LodeRunnerRenderUtil.LODE_RUNNER_TILE_PATH); //Initializes the array that hold the tile images
//		LodeRunnerRenderUtil.getBufferedImage(oneLevel,images);//rendered level and displays it in a window 
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new LodeRunner(oneLevel);
			}
		});
		GANProcess.terminateGANProcess(); //ends GAN process 
	}
	
	/**
	 * Fills a List<Point> with the points of all the empty tiles, one of them to be replaced by a spawn point
	 * @param level
	 * @return
	 */
	public static List<Point> fillEmptyList(List<List<Integer>> level){
		List<Point> emptySpaces = new ArrayList<Point>();
		for(int i = 0; i < level.size(); i++) {
			for(int j = 0; j < level.get(i).size(); j++) {
				if(level.get(i).get(j) == 0) {
					emptySpaces.add(new Point(j, i));
				}
			}
		}
		return emptySpaces;
	}
	
	/**
	 * Sets the spawn point at a random point from the set of empty points 
	 * @param level The level 
	 * @param empty Set of empty points in the level 
	 * @param rand Random instance 
	 */
	public static void setSpawn(List<List<Integer>> level, List<Point> empty, Random rand) {
		Point spawn = empty.get(rand.nextInt(empty.size()));
		level.get(spawn.y).set(spawn.x, LodeRunnerState.LODE_RUNNER_TILE_SPAWN);
		assert level.stream().anyMatch(list -> list.contains(new Integer(7))) : "No spawn!\n" + level;
	}
	
	

	/**
	 * Gets a set of all of the levels from the latent vector 
	 * @param latentVector
	 * @return Set of all the levels
	 */
	public static List<List<List<Integer>>> getLevelListRepresentationFromGAN(double[] latentVector){
		latentVector = GANProcess.mapArrayToOne(latentVector); 
		// Generate level from vector
		String level;
		synchronized(GANProcess.getGANProcess()) { // Make sure GAN response corresponds to provided latent vector
			try {
				GANProcess.getGANProcess().commSend("[" + Arrays.toString(latentVector) + "]");
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1); // Cannot continue without the GAN process
			}
			level = GANProcess.getGANProcess().commRecv(); // Response to command just sent
		}
		level = "["+level+"]"; // Wrap level in another json array
		List<List<List<Integer>>> levels = JsonReader.JsonToInt(level);
		return levels;
	}

	/**
	 * Gets one level from the list of levels, chooses the first one in the list  
	 * @param latentVector
	 * @return A single level 
	 */
	public static List<List<Integer>> generateOneLevelListRepresentationFromGAN(double[] latentVector) {
		List<List<List<Integer>>> levelInList = getLevelListRepresentationFromGAN(latentVector);
		List<List<Integer>> oneLevel = levelInList.get(0); // gets first level in the set  
		return oneLevel;
	}




}
