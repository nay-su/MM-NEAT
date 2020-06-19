package megaManMaker;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.tasks.mario.gan.reader.JsonReader;
import edu.southwestern.util.random.RandomNumbers;

public class MegaManGANUtil {
	public static final int LATENT_VECTOR_SIZE = 5;//latent vector dimension, 20 improved the model 
	public static final int MEGA_MAN_ALL_TERRAIN = 7; //number of tiles in MegaMan
	public static final int MEGA_MAN_TILES_WITH_ENEMIES = 16; //number of tiles in MegaMan
	public static final int MEGA_MAN_FIRST_LEVEL_ALL_TILES = 21; //number of tiles in MegaMan
	public static final int MEGA_MAN_LEVEL_WIDTH = 16;
	public static final int MEGA_MAN_LEVEL_HEIGHT = 14;
	

	/**
	 * Renders a level that was generated by the GAN 
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		GANProcess.type = GANProcess.GAN_TYPE.MEGA_MAN; //sets GAN type to LodeRunner
		Parameters.initializeParameterCollections(new String[] {"GANInputSize:"+LATENT_VECTOR_SIZE});//input size is the size of the latent vector
		double[] latentVector = RandomNumbers.randomArray(LATENT_VECTOR_SIZE); //fills array of input size randomly
		List<List<Integer>> oneLevel = generateOneLevelListRepresentationFromGANHorizontal(latentVector); //one level to render
		BufferedImage[] images = MegaManRenderUtil.loadImagesForASTAR(MegaManRenderUtil.MEGA_MAN_TILE_PATH); //Initializes the array that hold the tile images
		MegaManRenderUtil.getBufferedImage(oneLevel,images);//rendered level and displays it in a window 
		GANProcess.terminateGANProcess(); //ends GAN process 
	}
	
	public static GANProcess initializeGAN(String modelType) {
		GANProcess newGAN = new GANProcess(GANProcess.PYTHON_BASE_PATH+"MegaManGAN"+ File.separator + Parameters.parameters.stringParameter(modelType), 
				Parameters.parameters.integerParameter("GANInputSize"), 
				Parameters.parameters.stringParameter(modelType).contains("With7Tile") ? MegaManGANUtil.MEGA_MAN_ALL_TERRAIN : MegaManGANUtil.MEGA_MAN_TILES_WITH_ENEMIES,
				GANProcess.MEGA_MAN_OUT_WIDTH, GANProcess.MEGA_MAN_OUT_HEIGHT);
		return newGAN;
	}

	public static void startGAN(GANProcess gan) {
		gan.start();
		String response = "";
		while(!response.equals("READY")) {

			response = gan.commRecv();
		}
	}
	/**
	 * Gets a set of all of the levels from the latent vector 
	 * @param gan Specific GAN model to use as a generator
	 * @param latentVector
	 * @return Set of all the levels
	 */
	public static List<List<List<Integer>>> getLevelListRepresentationFromGAN(GANProcess gan, double[] latentVector){
		
		latentVector = GANProcess.mapArrayToOne(latentVector); // Range restrict the values
		int chunk_length = Integer.valueOf(gan.GANDim);
		String levelString = "";
		for(int i = 0; i < latentVector.length; i+=chunk_length){
			double[] chunk = Arrays.copyOfRange(latentVector, i, i+chunk_length);
			// Generate a level from the vector
			// Brackets required since generator.py expects of list of multiple levels, though only one is being sent here
			try {
				gan.commSend("[" + Arrays.toString(chunk) + "]");
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1); // Cannot continue without the GAN process
			}
			String oneLevelChunk = gan.commRecv(); // Response to command just sent
			levelString = levelString + ", " + oneLevelChunk;  
		}
		// These two lines remove the , from the first append to an empty string
		levelString = levelString.replaceFirst(",", "");
		levelString = levelString.replaceFirst(" ", "");
		levelString = "["+levelString+"]"; // Make a bundle of several levels
		// Create one level from all
		List<List<List<Integer>>> allLevels = JsonReader.JsonToInt(levelString);
		// This list contains several separate levels. The following code
		// merges the levels by appending adjacent rows
		List<List<Integer>> oneLevel = new ArrayList<List<Integer>>();
		// Create the appropriate number of rows in the array
		for(@SuppressWarnings("unused") List<Integer> row : allLevels.get(0)) { // Look at first level (assume all are same size)
			oneLevel.add(new ArrayList<Integer>()); // Empty row
		}
		// Now fill up the rows, one level at a time
		for(List<List<Integer>> aLevel : allLevels) {
			int index = 0;
			for(List<Integer> row : aLevel) { // Loot at each row
				oneLevel.get(index++).addAll(row);
			}	
		}
		return allLevels;
	}

	/**
	 * Gets one level from the list of levels, chooses the first one in the list  
	 * @param latentVector
	 * @return A single level 
	 */
	public static List<List<Integer>> generateOneLevelListRepresentationFromGANHorizontal(double[] latentVector) {
		// Since only one model is needed, using the standard getGANProcess
		List<List<List<Integer>>> levelInList = getLevelListRepresentationFromGAN(GANProcess.getGANProcess(), latentVector);
		List<List<Integer>> oneLevel = levelInList.get(0); // gets first level in the set 
		//List<List<Integer>> fullLevel = new ArrayList<List<Integer>>();
		for(int level = 1;level<levelInList.size();level++) {
			for(int i = 0;i<oneLevel.size();i++) {
				//for(int integer = 0; integer<oneLevel.get(0).size();integer++) {
				oneLevel.get(i).addAll(levelInList.get(level).get(i));

				
			}
		}
		
		
		return oneLevel;
	}
	
	/**
	 * Generates level segments from GAN, but stitches them together vertically rather than horizontally
	 * @param latentVector
	 * @return
	 */
	public static List<List<Integer>> generateOneLevelListRepresentationFromGANVertical(double[] latentVector) {
		// Since only one model is needed, using the standard getGANProcess
		List<List<List<Integer>>> levelInList = getLevelListRepresentationFromGAN(GANProcess.getGANProcess(), latentVector);
		List<List<Integer>> oneLevel = levelInList.get(0); // gets first level in the set 
		//List<List<Integer>> fullLevel = new ArrayList<List<Integer>>();
		for(int level = 1;level<levelInList.size();level++) {
			//for(int i = 0;i<oneLevel.size();i++) {
				//for(int integer = 0; integer<oneLevel.get(0).size();integer++) {
				oneLevel.addAll(levelInList.get(level));

				
			//}
		}
		
		
		return oneLevel;
	}
	public enum Direction {UP, RIGHT, DOWN};

	public static List<List<Integer>> generateOneLevelListRepresentationFromGANVerticalAndHorizontal(GANProcess horizontalGAN, GANProcess upGAN, GANProcess downGAN, double[] latentVector) {
		// Just grabbing the static GANProcess for now, but you will need to make this method accept two separate GAN models eventually.
		Random rand = new Random(Double.doubleToLongBits(latentVector[0]));
		List<List<List<Integer>>> levelInListHorizontal;
		List<List<List<Integer>>> levelInListUp;
		List<List<List<Integer>>> levelInListDown;

		boolean startRight = rand.nextBoolean();
		//if(startRight) {
//		System.out.println(horizontalGAN);
//		MiscUtil.waitForReadStringAndEnterKeyPress();
			levelInListHorizontal = getLevelListRepresentationFromGAN(horizontalGAN, latentVector);
	//	}else {
			levelInListUp = getLevelListRepresentationFromGAN(upGAN, latentVector);
			levelInListDown = getLevelListRepresentationFromGAN(downGAN, latentVector);

		//}
			//boolean startUp = rand.nextBoolean();

			Direction d;
	//	if(startRight)
		d = Direction.RIGHT;
//		else if(startUp) {
//			d = Direction.UP;
//		}else {
//			d = Direction.DOWN;
		//}
		//List<Point> allPreviousMoves = new ArrayList<Point>();
		Point previousMove = new Point(0,0);
		int numberOfChunks = levelInListHorizontal.size();
		boolean right;
		boolean up;
//		boolean wasRight = true;
//		boolean wasUp = false;
		List<List<Integer>> oneLevel;
		
		if(startRight) oneLevel= levelInListHorizontal.get(0); // gets first level in the set 
		else oneLevel = levelInListUp.get(0);
		List<Integer> nullLine = new ArrayList<Integer>(16);
		placeSpawn(oneLevel);
		if(numberOfChunks==1) {
			placeOrb(oneLevel);
		}
		for(int i=0;i<MEGA_MAN_LEVEL_WIDTH;i++) {
			nullLine.add(MegaManState.MEGA_MAN_TILE_NULL);
		}
		for(int level = 1;level<numberOfChunks;level++) {
			right = rand.nextBoolean();
				if(level==numberOfChunks-1&&right) {
					placeOrb(levelInListHorizontal.get(level));
				}else if(level==numberOfChunks-1&&!right) {
					placeOrb(levelInListUp.get(level));
					placeOrb(levelInListDown.get(level));
				}

			if(right) {
				placeRight(levelInListHorizontal, previousMove, oneLevel, nullLine, level);
				//wasRight = true;
				d = Direction.RIGHT;
				previousMove=new Point((int) previousMove.getX()+MEGA_MAN_LEVEL_WIDTH,(int) previousMove.getY());

			}else {
				up = rand.nextBoolean();
				
				if(d.equals(Direction.UP)) {
					d = Direction.UP;
				}else if(d.equals(Direction.DOWN)) { //can't add up then down then up
					d = Direction.DOWN;
				}else {
					if(up) {
						d = Direction.UP;
					}else {
						d = Direction.DOWN;
					}
				}
				if(d.equals(Direction.UP)) { //add null lines all on top
					placeUp(levelInListUp, previousMove, oneLevel, level);
				}else {
					placeDown(levelInListDown, previousMove, oneLevel, level);
					previousMove=new Point((int) previousMove.getX(),(int) previousMove.getY()+MEGA_MAN_LEVEL_HEIGHT);

					
					
				}
				//wasRight=false;
			}
		}
		
		
		return oneLevel;
	}

	private static void placeDown(List<List<List<Integer>>> levelInListDown, Point previousMove,
			List<List<Integer>> oneLevel, int level) {
		int y1 = (int) previousMove.getY();
		if(y1+15>=oneLevel.size()) {
			List<List<Integer>> nullScreen = new ArrayList<List<Integer>>();
			for(int i = 0;i<MEGA_MAN_LEVEL_HEIGHT;i++) {
				List<Integer> nullLines = new ArrayList<Integer>();
				for(int j = 0;j<oneLevel.get(0).size();j++) {
					nullLines.add(MegaManState.MEGA_MAN_TILE_NULL);
				}
				nullScreen.add(nullLines);
			}
			oneLevel.addAll(oneLevel.size(), nullScreen);
		}
		
		
		for(int y = (int) previousMove.getY()+MEGA_MAN_LEVEL_HEIGHT;y<previousMove.getY()+2*MEGA_MAN_LEVEL_HEIGHT;y++) {
			for(int x = (int) previousMove.getX();x<previousMove.getX()+MEGA_MAN_LEVEL_WIDTH;x++) {
				oneLevel.get(y).set(x, levelInListDown.get(level).get((int) (y -MEGA_MAN_LEVEL_HEIGHT- previousMove.getY())).get((int) (x-previousMove.getX())));
			}
		}
	}

	private static void placeUp(List<List<List<Integer>>> levelInListUp, Point previousMove,
			List<List<Integer>> oneLevel, int level) {
		List<List<Integer>> nullScreen = new ArrayList<List<Integer>>();
		for(int i = 0;i<MEGA_MAN_LEVEL_HEIGHT;i++) {
			List<Integer> nullLines = new ArrayList<Integer>();
			for(int j = 0;j<oneLevel.get(0).size();j++) {
				nullLines.add(MegaManState.MEGA_MAN_TILE_NULL);
			}
			nullScreen.add(nullLines);
		}
		oneLevel.addAll(0, nullScreen);
		//now at the previous point, add in a new version of levelInList.get(level)
		for(int y = (int) previousMove.getY();y<previousMove.getY()+MEGA_MAN_LEVEL_HEIGHT;y++) {
			for(int x = (int) previousMove.getX();x<previousMove.getX()+MEGA_MAN_LEVEL_WIDTH;x++) {
				oneLevel.get(y).set(x, levelInListUp.get(level).get((int) (y - (int)previousMove.getY())).get((int) (x-(int)previousMove.getX())));
			}
		}
	}

	private static void placeRight(List<List<List<Integer>>> levelInListHorizontal, Point previousMove,
			List<List<Integer>> oneLevel, List<Integer> nullLine, int level) {
		for(int i = 0;i<oneLevel.size();i++) { //add null to all spaces to the right TODO possibly change
			oneLevel.get(i).addAll(nullLine);
		}
		//take the information from the previous run to replace null with a level in the appropriate spots
		for(int x = (int) previousMove.getX()+MEGA_MAN_LEVEL_WIDTH;x<(int) previousMove.getX()+2*MEGA_MAN_LEVEL_WIDTH;x++) {
			for(int y = (int) previousMove.getY();y<(int) previousMove.getY()+MEGA_MAN_LEVEL_HEIGHT;y++) {
//						System.out.println(x+", "+y);
//						System.out.println(previousMove.getX()+", "+previousMove.getY());

				oneLevel.get(y).set(x, levelInListHorizontal.get(level).get((int) (y - previousMove.getY())).get((int) (x-MEGA_MAN_LEVEL_WIDTH-previousMove.getX())));
			}
		}
	}
	
	
	private static void placeSpawn(List<List<Integer>> level) {
		boolean placed = false;
		for(int x = 0;x<level.get(0).size();x++) {
			for(int y = 0;y<level.size();y++) {
				if(y-2>=0&&level.get(y).get(x)==1&&level.get(y-1).get(x)==0&&level.get(y-2).get(x)==0) {
					level.get(y-1).set(x, 8);
					placed = true;
					break;
				}
			}
			if(placed) {
				break;
			}
			
		
		}
		for(int i = 0; i<level.get(0).size();i++) {
			if(!placed) {
				level.get(level.size()-1).set(0, 1);
				level.get(level.size()-2).set(0, 8);
				placed = true;
			}
		}
//		placed = false;
//		for(int y = 0; y<level.size();y++) {
//			for(int x = level.get(0).size()-1;x>=0; x--) {
//				if(y-1>=0&&level.get(y).get(x)==2&&level.get(y-1).get(x)==0) {
//					level.get(y-1).set(x, 7);
//					placed=true;
//					break;
//					
//				}else if(y-1>=0&&level.get(y).get(x)==1&&level.get(y-1).get(x)==0) {
//					level.get(y-1).set(x, 7);
//					placed=true;
//					break;
//				}
//			}
//			if(placed) break;
//		}
	}
	private static void placeOrb(List<List<Integer>> level) {
		boolean placed = false;
//		for(int x = 0;x<level.get(0).size();x++) {
//			for(int y = 0;y<level.size();y++) {
//				if(y-2>=0&&level.get(y).get(x)==1&&level.get(y-1).get(x)==0&&level.get(y-2).get(x)==0) {
//					level.get(y-1).set(x, 8);
//					placed = true;
//					break;
//				}
//			}
//			if(placed) {
//				break;
//			}
//			
//		
//		}
//		for(int i = 0; i<level.get(0).size();i++) {
//			if(!placed) {
//				level.get(level.size()-1).set(0, 1);
//				level.get(level.size()-2).set(0, 8);
//				placed = true;
//			}
//		}
//		placed = false;
		
			for(int x = level.get(0).size()-1;x>=0; x--) {
				for(int y = level.size()-1; y>=0;y--) {
				if(y-1>=0&&level.get(y).get(x)==2&&level.get(y-1).get(x)==0) {
					level.get(y-1).set(x, 7);
					placed=true;
					break;
					
				}else if(y-1>=0&&level.get(y).get(x)==1&&level.get(y-1).get(x)==0) {
					level.get(y-1).set(x, 7);
					placed=true;
					break;
				}else if(y-1>=0&&level.get(y).get(x)==5&&level.get(y-1).get(x)==0) {
					level.get(y-1).set(x, 7);
					placed=true;
					break;
				}
			}
			if(placed) break;
		}
	}
	

}
