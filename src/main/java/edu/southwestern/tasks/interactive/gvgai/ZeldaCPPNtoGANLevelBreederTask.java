package edu.southwestern.tasks.interactive.gvgai;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.zelda.ZeldaGANUtil;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon.Node;
import edu.southwestern.tasks.gvgai.zelda.dungeon.DungeonUtil;
import edu.southwestern.tasks.gvgai.zelda.dungeon.ZeldaDungeon;
import edu.southwestern.tasks.gvgai.zelda.dungeon.ZeldaDungeon.Level;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaLevelUtil;
import edu.southwestern.tasks.interactive.InteractiveEvolutionTask;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.tasks.zelda.ZeldaCPPNtoGANVectorMatrixBuilder;
import edu.southwestern.tasks.zelda.ZeldaGANVectorMatrixBuilder;
//import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.random.RandomNumbers;
import me.jakerg.rougelike.Ladder;
import me.jakerg.rougelike.RougelikeApp;
import me.jakerg.rougelike.Tile;



/**
 * Uses a CPPN to map a latent vector for a GAN to each cell in a grid,
 * and create a room for Zelda by sending the latent vector to the GAN.
 * 
 * @author Jacob Schrum
 */
public class ZeldaCPPNtoGANLevelBreederTask extends InteractiveEvolutionTask<TWEANN> {

	public static final String[] SENSOR_LABELS = new String[] {"x-coordinate", "y-coordinate", "radius", "bias"};
	
	public static final int NUM_NON_LATENT_INPUTS = 6; //the first six values in the latent vector
	public static final int INDEX_ROOM_PRESENCE = 0;	// Whether a room is present
	public static final int INDEX_TRIFORCE_PREFERENCE = 1; // Determines both Triforce location AND starting location
	public static final int INDEX_DOOR_DOWN = 2; // Determines if there is a door heading down (and thus a door up in the connecting room)
	public static final int INDEX_DOOR_RIGHT = 3; // Determines if there is a door heading right (and thus a door left in the connecting room)
	public static final int INDEX_DOWN_DOOR_TYPE = 4; // Encodes the type of the down door
	public static final int INDEX_RIGHT_DOOR_TYPE = 5; // Encodes the type of the right door
	public static final int INDEX_RAFT_PREFERENCE = 6; //determines if there is a raft in placed in the level 
	
	

	public static final int PLAY_BUTTON_INDEX = -20;
	private static final int FILE_LOADER_BUTTON_INDEX = -21;

	private static final int LEVEL_MIN_CHUNKS = 1;
	private static final int LEVEL_MAX_CHUNKS = 10; 
	private String[] outputLabels;

	private boolean initializationComplete = false;

	public ZeldaCPPNtoGANLevelBreederTask() throws IllegalAccessException {
		super();
		configureGAN();
		// These dungeons are generated by CPPN, not grammar
		DungeonUtil.NO_GRAMMAR_AT_ALL = true;

		JButton fileLoadButton = new JButton();
		fileLoadButton.setText("SelectGANModel");
		fileLoadButton.setName("" + FILE_LOADER_BUTTON_INDEX);
		fileLoadButton.addActionListener(this);

		if(!Parameters.parameters.booleanParameter("simplifiedInteractiveInterface")) {
			top.add(fileLoadButton);
		}

		//Construction of button that lets user plays the level
		JButton play = new JButton("Play");
		// Name is first available numeric label after the input disablers
		play.setName("" + PLAY_BUTTON_INDEX);
		play.addActionListener(this);
		top.add(play);

		JSlider widthSlider = new JSlider(JSlider.HORIZONTAL, LEVEL_MIN_CHUNKS, LEVEL_MAX_CHUNKS, Parameters.parameters.integerParameter("zeldaGANLevelWidthChunks"));
		widthSlider.setMinorTickSpacing(1);
		widthSlider.setPaintTicks(true);
		Hashtable<Integer,JLabel> widthLabels = new Hashtable<>();
		widthLabels.put(LEVEL_MIN_CHUNKS, new JLabel("Narrower"));
		widthLabels.put(LEVEL_MAX_CHUNKS, new JLabel("Wider"));
		widthSlider.setLabelTable(widthLabels);
		widthSlider.setPaintLabels(true);
		widthSlider.setPreferredSize(new Dimension(200, 40));
		widthSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if(!initializationComplete) return;
				// get value
				JSlider source = (JSlider)e.getSource();
				if(!source.getValueIsAdjusting()) {

					int oldValue = Parameters.parameters.integerParameter("zeldaGANLevelWidthChunks");
					int newValue = (int) source.getValue();
					Parameters.parameters.setInteger("zeldaGANLevelWidthChunks", newValue);

					if(oldValue != newValue) {
						resetLatentVectorAndOutputs();
						reset(); // resets whole population
					}
				}
			}
		});

		JSlider heightSlider = new JSlider(JSlider.HORIZONTAL, LEVEL_MIN_CHUNKS, LEVEL_MAX_CHUNKS, Parameters.parameters.integerParameter("zeldaGANLevelHeightChunks"));
		heightSlider.setMinorTickSpacing(1);
		heightSlider.setPaintTicks(true);
		Hashtable<Integer,JLabel> heightLabels = new Hashtable<>();
		heightLabels.put(LEVEL_MIN_CHUNKS, new JLabel("Shorter"));
		heightLabels.put(LEVEL_MAX_CHUNKS, new JLabel("Taller"));
		heightSlider.setLabelTable(heightLabels);
		heightSlider.setPaintLabels(true);
		heightSlider.setPreferredSize(new Dimension(200, 40));
		heightSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if(!initializationComplete) return;
				// get value
				JSlider source = (JSlider)e.getSource();
				if(!source.getValueIsAdjusting()) {

					int oldValue = Parameters.parameters.integerParameter("zeldaGANLevelHeightChunks");
					int newValue = (int) source.getValue();
					Parameters.parameters.setInteger("zeldaGANLevelHeightChunks", newValue);

					if(oldValue != newValue) {
						resetLatentVectorAndOutputs();
						reset(); // resets whole population
					}
				}
			}
		});

		JPanel size = new JPanel();
		size.setLayout(new GridLayout(2,1));
		size.add(widthSlider);
		size.add(heightSlider);

		top.add(size);

		resetLatentVectorAndOutputs();
		initializationComplete = true;
	}
	
	/**
	 * Returns the number of non latent variables 
	 * @return
	 */
	public static int numberOfNonLatentVariables() {
		int numOfNonLatentVectors = NUM_NON_LATENT_INPUTS;
		if(Parameters.parameters.booleanParameter("zeldaCPPNtoGANAllowsRaft")) {
			numOfNonLatentVectors++;
		}
		return numOfNonLatentVectors;
	}

	/**
	 * Set the GAN Process to type ZELDA
	 */
	public void configureGAN() {
		GANProcess.type = GANProcess.GAN_TYPE.ZELDA;
	}

	/**
	 * Function to get the file name of the Zelda GAN Model
	 * @returns String the file name of the GAN Model
	 */
	public String getGANModelParameterName() {
		return "zeldaGANModel";
	}

	private void resetLatentVectorAndOutputs() {
		int latentVectorLength = GANProcess.latentVectorLength();
		outputLabels = new String[latentVectorLength + numberOfNonLatentVariables()];
		outputLabels[INDEX_ROOM_PRESENCE] = "Room Presence";
		outputLabels[INDEX_TRIFORCE_PREFERENCE] = "Triforce Preference";
		outputLabels[INDEX_DOOR_DOWN] = "Door Down";
		outputLabels[INDEX_DOOR_RIGHT] = "Door Right";
		outputLabels[INDEX_DOWN_DOOR_TYPE] = "Down Door Type";
		outputLabels[INDEX_RIGHT_DOOR_TYPE] = "Right Door Type";
		if(Parameters.parameters.booleanParameter("zeldaCPPNtoGANAllowsRaft")) {
			outputLabels[INDEX_RAFT_PREFERENCE] = "Raft in Level";
		}
		for(int i = numberOfNonLatentVariables(); i < outputLabels.length; i++) {
			outputLabels[i] = "LV"+(i-numberOfNonLatentVariables());
		}
	}

	@Override
	public String[] sensorLabels() {
		return SENSOR_LABELS;
	}

	@Override
	public String[] outputLabels() {
		return outputLabels;
	}

	@Override
	protected String getWindowTitle() {
		return "Zelda CPPN To GAN Dungeon Breeder";
	}

	@Override
	protected void save(String file, int i) {
		Dungeon dungeon = cppnToDungeon(scores.get(i).individual.getPhenotype(), Parameters.parameters.integerParameter("zeldaGANLevelWidthChunks"), Parameters.parameters.integerParameter("zeldaGANLevelHeightChunks"), inputMultipliers);
		try {
			dungeon.saveToJson(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected BufferedImage getButtonImage(TWEANN cppn, int width, int height, double[] inputMultipliers) {
		Dungeon dungeon = cppnToDungeon(cppn, Parameters.parameters.integerParameter("zeldaGANLevelWidthChunks"), Parameters.parameters.integerParameter("zeldaGANLevelHeightChunks"), inputMultipliers);
		dungeon.markReachableRooms();
		BufferedImage image = DungeonUtil.imageOfDungeon(dungeon);
		return image;
	}

	protected boolean respondToClick(int itemID) {
		boolean undo = super.respondToClick(itemID);
		if(undo) return true; // Click must have been a bad activation checkbox choice. Skip rest

		if(itemID == FILE_LOADER_BUTTON_INDEX) {
			JFileChooser chooser = new JFileChooser();//used to get new file
			chooser.setApproveButtonText("Open");
			FileNameExtensionFilter filter = new FileNameExtensionFilter("GAN Model", "pth");
			chooser.setFileFilter(filter);
			// This is where all the GANs are stored (only allowable spot)
			chooser.setCurrentDirectory(new File(getGANModelDirectory()));
			int returnVal = chooser.showOpenDialog(frame);
			if(returnVal == JFileChooser.APPROVE_OPTION) {//if the user decides to save the image
				String model = chooser.getSelectedFile().getName();
				Parameters.parameters.setString(getGANModelParameterName(), model);
				resetAndReLaunchGAN(model);
				reset(); // Reset the whole population, since the CPPNs need to have a different number of output neurons
				resetLatentVectorAndOutputs();
			}
			resetButtons(true);
		}


		// Human plays level
		if(itemID == PLAY_BUTTON_INDEX && selectedItems.size() > 0) {
			Network cppn = scores.get(selectedItems.get(selectedItems.size() - 1)).individual.getPhenotype();
			Dungeon dungeon = cppnToDungeon(cppn, Parameters.parameters.integerParameter("zeldaGANLevelWidthChunks"), Parameters.parameters.integerParameter("zeldaGANLevelHeightChunks"), inputMultipliers);
			new Thread() {
				@Override
				public void run() {
					RougelikeApp.startDungeon(dungeon);
				}
			}.start();
		}
		return false; // no undo: every thing is fine
	}

	@Override
	protected void additionalButtonClickAction(int scoreIndex, Genotype<TWEANN> individual) {
		// Not used
	}

	/**
	 * 
	 * @param model Name of the model to reconfigure
	 * @returns Pair of the old latent vector and the net latent vector
	 */
	public Pair<Integer, Integer> resetAndReLaunchGAN(String model) {
		return ZeldaGANLevelBreederTask.staticResetAndReLaunchGAN(model);
	}

	/**
	 * Set the path of the Zelda GAN Model
	 * @returns String path to GAN model
	 */
	public String getGANModelDirectory() {
		return "src"+File.separator+"main"+File.separator+"python"+File.separator+"GAN"+File.separator+"ZeldaGAN";
	}

	/**
	 * Override the type of file we want to generate
	 * @return String of file type
	 */
	@Override
	protected String getFileType() {
		return "Text File";
	}

	/**
	 * The extenstion of the file type
	 * @return String file extension
	 */
	@Override
	protected String getFileExtension() {
		return "txt";
	}

	@Override
	public int numCPPNInputs() {
		return this.sensorLabels().length;
	}

	@Override
	public int numCPPNOutputs() {
		return this.outputLabels().length;
	}

	/**
	 * Create a Dungeon of the given size (in rooms) using a CPPN that generates latent vectors that are sent to a GAN.
	 * 
	 * @param cppn Network that takes the location of the room in the dungeon, and returns a latent vector and some other information
	 * @param width Number of rooms wide
	 * @param height Number of rooms high
	 * @param inputMultipliers Multipliers for CPPN inputs (can turn them on or off)
	 * @return A Rogue-like Dungeon instance
	 */
	public static Dungeon cppnToDungeon(Network cppn, int width, int height, double[] inputMultipliers) {
		Pair<double[][][],double[][][]> cppnOutput = latentVectorGridFromCPPN(new ZeldaCPPNtoGANVectorMatrixBuilder(cppn,inputMultipliers), width, height);		
		double[][][] auxiliaryInformation = cppnOutput.t1;
		double[][][] latentVectorGrid = cppnOutput.t2;
		// Because a CPPN can make disconnected dungeons, it is legitimately possible for a level
		// to be unbeatable, even after repair by A*. This loop randomly fills in empty rooms in the
		// dungeon grid until A* succeeds.
		return gridDataToDungeon(auxiliaryInformation, latentVectorGrid);
	}

	/**
	 * Takes two grids of same width and height. One has auxiliary information about constructing the
	 * dungeon, such as whether a room is even present. The other contains the latent vectors associated
	 * with each room location. A Dungeon is created based on these.
	 * 
	 * @param auxiliaryInformation auxiliaryInformation[y][x] determines some miscellaneous features of the room at (x,y)
	 * @param latentVectorGrid latentVectorGrid[y][x] is the latent vector for creating a room at (x,y) if a room is present
	 * @return Resulting Dungeon
	 */
	public static Dungeon gridDataToDungeon(double[][][] auxiliaryInformation, double[][][] latentVectorGrid) {
		Dungeon dungeon = null;
		boolean unbeatable;
		double presenceThreshold = 0;
		int numTries = 1;
		do {
			System.out.println("Generate for CPPN: try: " + numTries);
			unbeatable = false;
			try {
				List<List<Integer>>[][] levelAsListsGrid = levelGridFromLatentVectorGrid(latentVectorGrid,auxiliaryInformation,presenceThreshold);
				addRandomEnemies(levelAsListsGrid);
				Level[][] levelGrid = DungeonUtil.roomGridFromJsonGrid(levelAsListsGrid);
				Pair<Point,Point> startAndGoal = decideStartAndTriforceLocations(levelGrid,auxiliaryInformation);
				Point startRoom = startAndGoal.t1;
				// CPPN could make an empty dungeon
				if(startRoom.x == -1 || startRoom.y == -1) {
					throw new IllegalArgumentException("The dungeon must be empty since the start room is not in the grid");
				}
				Point triforceRoom = startAndGoal.t2;
				dungeon = dungeonFromLevelGrid(levelGrid,startRoom,auxiliaryInformation,presenceThreshold);
				levelGrid[triforceRoom.y][triforceRoom.x] = levelGrid[triforceRoom.y][triforceRoom.x].placeTriforce(dungeon);
				dungeon.setGoalPoint(new Point(triforceRoom.x, triforceRoom.y));
				dungeon.setGoal("("+triforceRoom.x+","+triforceRoom.y+")");
				// Use A* to modify the level to make sections passable
				if(Parameters.parameters.booleanParameter("makeZeldaLevelsPlayable")) 
					DungeonUtil.makeDungeonPlayable(dungeon);
				
			} catch(IllegalArgumentException e) {
				// Make a new room appear in dungeon
				//enableRoomActivation(auxiliaryInformation);
				presenceThreshold -= 0.01 * (numTries++); // Make rooms more likely to appear, each time more likelier
				// Force loop
				unbeatable = true;
			} catch(IllegalStateException e) {
				// This should not happen, but seems possible if A* has problems.
				// In this case, make all rooms visible
				presenceThreshold -= 100; // All rooms should appear
				numTries++;
				// Also give A* more time to run
				//Parameters.parameters.setInteger("aStarSearchBudget", (int)(Parameters.parameters.integerParameter("aStarSearchBudget")*1.1));
				System.out.println("A* failed. New budget: "+Parameters.parameters.integerParameter("aStarSearchBudget"));
				// Force loop
				unbeatable = true;
			}
			if(numTries > Parameters.parameters.integerParameter("dungeonGenerationFailChances")) {
				//DungeonUtil.viewDungeon(dungeon,DungeonUtil.mostRecentVisited);
				//System.out.println("Press a key to fail");
				//MiscUtil.waitForReadStringAndEnterKeyPress();
				//throw new IllegalStateException("Can't find a way to make this level beatable!");
				System.out.println("Can't find a way to make this level beatable!");
				return null;
			}
		} while(unbeatable);
		return dungeon;
	}

	/**
	 * Add random enemies to EVERY room in the dungeon
	 * @param levelAsListsGrid List representation of the dungeon rooms
	 */
	private static void addRandomEnemies(List<List<Integer>>[][] levelAsListsGrid) {
		for(int y = 0; y < levelAsListsGrid.length; y++) {
			for(int x = 0; x < levelAsListsGrid[y].length; x++) {
				if(levelAsListsGrid[y][x] != null) {
					ZeldaLevelUtil.addRandomEnemy(levelAsListsGrid[y][x]);
				}
			}
		}
	}

	//	private static void enableRoomActivation(double[][][] auxiliaryInformation) {
	//		for(int y = 0; y < auxiliaryInformation.length; y++) {
	//			for(int x = 0; x < auxiliaryInformation[y].length; x++) {
	//				if(auxiliaryInformation[y][x][INDEX_ROOM_PRESENCE] <= 0) {
	//					auxiliaryInformation[y][x][INDEX_ROOM_PRESENCE] = 1.0;
	//					return; // Found room to add ... stop
	//				}
	//			}
	//		}
	//		throw new IllegalStateException("There should have been a room that needed adding!");
	//	}

	/**
	 * This method decides the room that will be the start, and a room that will hold the triforce
	 * first is the start, last is the triforce
	 * @param levelGrid The dungeon 
	 * @param auxiliaryInformation Tile information
	 * @return A pair of points with the first being the starting room, and the second being the triforce room 
	 */
	private static Pair<Point,Point> decideStartAndTriforceLocations(Level[][] levelGrid, double[][][] auxiliaryInformation) {
		int triforceX = -1;
		int triforceY = -1;
		int startX = -1;
		int startY = -1;
		double highestActivation = Double.NEGATIVE_INFINITY;
		double lowestActivation = Double.POSITIVE_INFINITY;

		for(int y = 0; y < levelGrid.length; y++) {
			for(int x = 0; x < levelGrid[y].length; x++) {
				if(levelGrid[y][x] != null) {
					if(auxiliaryInformation[y][x][INDEX_TRIFORCE_PREFERENCE] > highestActivation) {
						highestActivation = auxiliaryInformation[y][x][INDEX_TRIFORCE_PREFERENCE];
						triforceX = x;
						triforceY = y;
					}
					if(auxiliaryInformation[y][x][INDEX_TRIFORCE_PREFERENCE] < lowestActivation) {
						lowestActivation = auxiliaryInformation[y][x][INDEX_TRIFORCE_PREFERENCE];
						startX = x;
						startY = y;
					}
				}
			}
		}		
		return new Pair<Point,Point>(new Point(startX,startY),new Point(triforceX,triforceY));
	}

	/**
	 * Make a playable Rogue-like dungeon from a 2D Level grid. Copied some code
	 * from SimpleDungeon. Might need to refactor at some point
	 * @param levelGrid 2D Level grid for dungeon (each cell is a room)
	 * @param startRoom Coordinates in grid where player starts
	 * @return Complete Dungeon representing the given Level grid
	 */
	public static Dungeon dungeonFromLevelGrid(Level[][] levelGrid,Point startRoom,double[][][] auxiliaryInformation,double presenceThreshold) {
		Dungeon dungeonInstance = new Dungeon();

		String[][] uuidLabels = new String[levelGrid.length][levelGrid[0].length];

		// Set all String names first
		for(int y = 0; y < levelGrid.length; y++) {
			for(int x = 0; x < levelGrid[y].length; x++) {
				if(levelGrid[y][x] != null) {
					uuidLabels[y][x] = "("+x+","+y+")";
				}	
			}
		}

		// Create all Nodes first to be referenced later using the String name associated with it from the previous loop
		for(int y = 0; y < levelGrid.length; y++) {
			for(int x = 0; x < levelGrid[y].length; x++) {
				if(levelGrid[y][x] != null) {
					String name = uuidLabels[y][x];
					dungeonInstance.newNode(name, levelGrid[y][x]);
				}	
			}
		}

		for(int y = 0; y < levelGrid.length; y++) {
			for(int x = 0; x < levelGrid[y].length; x++) {
				if(levelGrid[y][x] != null) {
					String name = uuidLabels[y][x];
					Node currentNode = dungeonInstance.getNode(name);
					//adds door to adjacent room, sets up and down doors
					if(auxiliaryInformation[y][x][INDEX_DOOR_DOWN] > presenceThreshold && y+1 < levelGrid.length && levelGrid[y+1][x] != null) {
						// Create door down in this room, and door up in connecting room
						ZeldaDungeon.addAdjacencyIfAvailable(dungeonInstance, levelGrid, uuidLabels, currentNode, x, y + 1, "DOWN", auxiliaryInformation[y][x][INDEX_DOWN_DOOR_TYPE]);
						String nameBelow = 	uuidLabels[y+1][x];
						Node nodeBelow = dungeonInstance.getNode(nameBelow);
						ZeldaDungeon.addAdjacencyIfAvailable(dungeonInstance, levelGrid, uuidLabels, nodeBelow, x, y, "UP", auxiliaryInformation[y][x][INDEX_DOWN_DOOR_TYPE]); // Coordinates of this room
					}
					//adds door to adjacent room, sets up and down doors
					if(auxiliaryInformation[y][x][INDEX_DOOR_RIGHT] > presenceThreshold && x+1 < levelGrid[y].length && levelGrid[y][x+1] != null) {
						// Create door right in this room, and door left in connecting room
						ZeldaDungeon.addAdjacencyIfAvailable(dungeonInstance, levelGrid, uuidLabels, currentNode, x + 1, y, "RIGHT", auxiliaryInformation[y][x][INDEX_RIGHT_DOOR_TYPE]);
						String nameRight = 	uuidLabels[y][x+1];
						Node nodeRight = dungeonInstance.getNode(nameRight);
						ZeldaDungeon.addAdjacencyIfAvailable(dungeonInstance, levelGrid, uuidLabels, nodeRight, x, y, "LEFT", auxiliaryInformation[y][x][INDEX_RIGHT_DOOR_TYPE]); // Coordinates of this room
					}	
					
				}	
			}
		}
		//places a raft in the level if allowed
		if(Parameters.parameters.booleanParameter("zeldaCPPNtoGANAllowsRaft")) {
			Point p = pointForRandomRaft(levelGrid, auxiliaryInformation); //gets the point for the room to place the raft
			String name = uuidLabels[p.y][p.x]; //gets the label for the room to get the node
 			Node raftRoom = dungeonInstance.getNode(name); //gets the node for that room 
			ZeldaLevelUtil.placeRandomRaft(raftRoom, RandomNumbers.randomGenerator); //places raft randomly in that room 
		}
		// name of start room
		String name = uuidLabels[startRoom.y][startRoom.x].toString();
		
		//add code to use place random key 

		dungeonInstance.setCurrentLevel(name);
		dungeonInstance.setLevelThere(uuidLabels);

		return dungeonInstance;
	}

	/**
	 * This method chooses the room that the raft will go in 
	 * @param levelGrid 2D array that represented the level 
	 * @param auxiliaryInformation Information about tile 
	 * @param rand Random object
	 * @return THe point that the raft will be placed 
	 */
	public static Point pointForRandomRaft(Level[][] levelGrid, double[][][] auxiliaryInformation) {
		int xRaft = -1;
		int yRaft = -1;
		double highestActivation = Double.NEGATIVE_INFINITY;

		//these loops find the room to put the raft in 
		for(int y = 0; y < levelGrid.length; y++) {
			for(int x = 0; x < levelGrid[y].length; x++) {
				if(levelGrid[y][x] != null) {
					if(auxiliaryInformation[y][x][INDEX_RAFT_PREFERENCE] > highestActivation) {
						highestActivation = auxiliaryInformation[y][x][INDEX_RAFT_PREFERENCE];
						xRaft = x;
						yRaft = y;
					}
				}
			}
		}
		return new Point(xRaft,yRaft);
	}



	/**
	 * CPPN is queried at each point in a 2D grid and generates a latent vector for the GAN to store at that location in an array.
	 * @param cppn Neural network that creates latent vectors
	 * @param width Width of Dungeon grid (second dimension of array)
	 * @param height Height of Dungeon grid (first dimension of array)
	 * @param inputMultipliers Multipliers for CPPN inputs which has potential to disable them
	 * @return 3D array that is a 2D grid of latent vectors
	 */
	public static Pair<double[][][],double[][][]> latentVectorGridFromCPPN(ZeldaGANVectorMatrixBuilder builder, int width, int height) {
		double[][][] latentVectorGrid = new double[height][width][];
		double[][][] presenceAndTriforceGrid = new double[height][width][];
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				double[] vector = builder.latentVectorAndMiscDataForPosition(width, height, x, y);
				double[] latentVector = new double[GANProcess.latentVectorLength()]; // Shorter
				System.arraycopy(vector, numberOfNonLatentVariables(), latentVector, 0, latentVector.length);
				latentVectorGrid[y][x] = latentVector;

				double[] auxiliaryInformation = new double[numberOfNonLatentVariables()];
				System.arraycopy(vector, 0, auxiliaryInformation, 0, numberOfNonLatentVariables());
				presenceAndTriforceGrid[y][x] = auxiliaryInformation;
			}
		}
		return new Pair<double[][][],double[][][]>(presenceAndTriforceGrid,latentVectorGrid);
	}

	/**
	 * Given 2D grid of latent vectors, send each to the GAN to get a 2D grid of List representations of the rooms.
	 * @param latentVectorGrid 3D array that is 2D grid of latent vectors
	 * @param auxiliaryInformation Indicates whether rooms are actually present, and where triforce should be
	 * @return Grid of corresponding Lists of Lists of Integers, which each such list is the room for a latent vector
	 */
	public static List<List<Integer>>[][] levelGridFromLatentVectorGrid(double[][][] latentVectorGrid,double[][][] auxiliaryInformation, double presenceThreshold) {
		@SuppressWarnings("unchecked")
		List<List<Integer>>[][] levelAsListsGrid = (List<List<Integer>>[][]) new List[latentVectorGrid.length][latentVectorGrid[0].length];
		for(int y = 0; y < levelAsListsGrid.length; y++) {
			for(int x = 0; x < levelAsListsGrid[0].length; x++) {
				if(auxiliaryInformation[y][x][INDEX_ROOM_PRESENCE] > presenceThreshold) { // Room presence threshold is 0: TODO: Make parameter?
					levelAsListsGrid[y][x] = ZeldaGANUtil.generateOneRoomListRepresentationFromGAN(latentVectorGrid[y][x]); //generates a single room 

					//removes doors that are placed automatically by the GAN 
					//helps to fix invalid door problem 
					int door = Tile.DOOR.getNum(); // Is 3 
					for(List<Integer> l : levelAsListsGrid[y][x]) {
						//removes all door tiles and replaces them with wall tiles to avoid invalid doors
						while(l.contains(door)) {
							//System.out.println(l.indexOf(door));
							int index = l.indexOf(door);
							l.remove(l.indexOf(door));
							l.add(index, 1);
						}
					}


				} else {
					levelAsListsGrid[y][x] = null;
				}
			}
		}
		return levelAsListsGrid;
	}


	public static void main(String[] args) {
		try {
			MMNEAT.main(new String[]{"runNumber:0","randomSeed:1","zeldaCPPNtoGANAllowsPuzzleDoors:true","zeldaCPPNtoGANAllowsRaft:true","makeZeldaLevelsPlayable:false","zeldaStudySavesParticipantData:false","showKLOptions:false","trials:1","mu:16","zeldaGANModel:ZeldaFixedDungeonsAll_5000_10.pth","maxGens:500","io:false","netio:false","GANInputSize:10","mating:true","fs:false","task:edu.southwestern.tasks.interactive.gvgai.ZeldaCPPNtoGANLevelBreederTask","cleanOldNetworks:false", "zeldaGANUsesOriginalEncoding:false","allowMultipleFunctions:true","ftype:0","watch:true","netChangeActivationRate:0.3","cleanFrequency:-1","simplifiedInteractiveInterface:false","recurrency:false","saveAllChampions:true","cleanOldNetworks:false","ea:edu.southwestern.evolution.selectiveBreeding.SelectiveBreedingEA","imageWidth:2000","imageHeight:2000","imageSize:200","includeFullSigmoidFunction:true","includeFullGaussFunction:true","includeCosineFunction:true","includeGaussFunction:false","includeIdFunction:true","includeTriangleWaveFunction:true","includeSquareWaveFunction:true","includeFullSawtoothFunction:true","includeSigmoidFunction:false","includeAbsValFunction:false","includeSawtoothFunction:false"});
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
}
