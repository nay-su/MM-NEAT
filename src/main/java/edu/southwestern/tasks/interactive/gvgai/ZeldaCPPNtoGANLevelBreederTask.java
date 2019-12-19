package edu.southwestern.tasks.interactive.gvgai;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
import edu.southwestern.tasks.interactive.InteractiveEvolutionTask;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.util.CartesianGeometricUtilities;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.graphics.GraphicsUtil;
import edu.southwestern.util.util2D.ILocated2D;
import edu.southwestern.util.util2D.Tuple2D;
import me.jakerg.rougelike.RougelikeApp;

public class ZeldaCPPNtoGANLevelBreederTask extends InteractiveEvolutionTask<TWEANN> {

	public static final int NUM_NON_LATENT_INPUTS = 2;
	public static final int INDEX_ROOM_PRESENCE = 0;
	public static final int INDEX_TRIFORCE_PREFERENCE = 1;
	
	public static final int PLAY_BUTTON_INDEX = -20;
	private static final int LEVEL_MIN_CHUNKS = 1;
	private static final int LEVEL_MAX_CHUNKS = 10; 
	private String[] outputLabels;

	private boolean initializationComplete = false;

	public ZeldaCPPNtoGANLevelBreederTask() throws IllegalAccessException {
		super();
		configureGAN();

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
		outputLabels = new String[latentVectorLength + NUM_NON_LATENT_INPUTS];
		outputLabels[INDEX_ROOM_PRESENCE] = "Room Presence";
		outputLabels[INDEX_TRIFORCE_PREFERENCE] = "Triforce Preference";
		for(int i = NUM_NON_LATENT_INPUTS; i < outputLabels.length; i++) {
			outputLabels[i] = "LV"+(i-NUM_NON_LATENT_INPUTS);
		}
	}


	@Override
	public String[] sensorLabels() {
		return new String[] {"x-coordinate", "y-coordinate", "radius", "bias"};
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
		BufferedImage image = DungeonUtil.imageOfDungeon(dungeon);
		return image;
	}

	protected boolean respondToClick(int itemID) {
		boolean undo = super.respondToClick(itemID);
		if(undo) return true; // Click must have been a bad activation checkbox choice. Skip rest
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

	public static Dungeon cppnToDungeon(Network cppn, int width, int height, double[] inputMultipliers) {
		Pair<double[][][],double[][][]> cppnOutput = latentVectorGridFromCPPN(cppn, width, height, inputMultipliers);		
		double[][][] auxiliaryInformation = cppnOutput.t1;
		double[][][] latentVectorGrid = cppnOutput.t2;
		List<List<Integer>>[][] levelAsListsGrid = levelGridFromLatentVectorGrid(latentVectorGrid,auxiliaryInformation);
		Level[][] levelGrid = DungeonUtil.roomGridFromJsonGrid(levelAsListsGrid);
		Point triforceRoom = decideTriforceLocation(levelGrid,auxiliaryInformation);
		Dungeon dungeon = dungeonFromLevelGrid(levelGrid);
		levelGrid[triforceRoom.y][triforceRoom.x] = levelGrid[triforceRoom.y][triforceRoom.x].placeTriforce(dungeon);
		dungeon.setGoalPoint(new Point(triforceRoom.x, triforceRoom.y));
		dungeon.setGoal("("+triforceRoom.x+","+triforceRoom.y+")");
		DungeonUtil.makeDungeonPlayable(dungeon);
		return dungeon;
	}
	
	private static Point decideTriforceLocation(Level[][] levelGrid, double[][][] auxiliaryInformation) {
		int triforceX = -1;
		int triforceY = -1;
		double highestActivation = Double.NEGATIVE_INFINITY;
		
		for(int y = 0; y < levelGrid.length; y++) {
			for(int x = 0; x < levelGrid[y].length; x++) {
				if(levelGrid[y][x] != null) {
					if(auxiliaryInformation[y][x][INDEX_TRIFORCE_PREFERENCE] > highestActivation) {
						highestActivation = auxiliaryInformation[y][x][INDEX_TRIFORCE_PREFERENCE];
						triforceX = x;
						triforceY = y;
					}
				}
			}
		}		
		return new Point(triforceX,triforceY);
	}

	/**
	 * Make a playable Rogue-like dungeon from a 2D Level grid. Copied some code
	 * from SimpleDungeon. Might need to refactor at some point
	 * @param levelGrid 2D Level grid for dungeon (each cell is a room)
	 * @return Complete Dungeon representing the given Level grid
	 */
	public static Dungeon dungeonFromLevelGrid(Level[][] levelGrid) {
		Dungeon dungeonInstance = new Dungeon();

		String[][] uuidLabels = new String[levelGrid.length][levelGrid[0].length];
		
		for(int y = 0; y < levelGrid.length; y++) {
			for(int x = 0; x < levelGrid[y].length; x++) {
				if(levelGrid[y][x] != null) {
					if(uuidLabels[y][x] == null) {
						uuidLabels[y][x] = "("+x+","+y+")";
					}
					String name = uuidLabels[y][x];
					Node newNode = dungeonInstance.newNode(name, levelGrid[y][x]);
					
					ZeldaDungeon.addAdjacencyIfAvailable(dungeonInstance, levelGrid, uuidLabels, newNode, x + 1, y, "RIGHT");
					ZeldaDungeon.addAdjacencyIfAvailable(dungeonInstance, levelGrid, uuidLabels, newNode, x, y - 1, "UP");
					ZeldaDungeon.addAdjacencyIfAvailable(dungeonInstance, levelGrid, uuidLabels, newNode, x - 1, y, "LEFT");
					ZeldaDungeon.addAdjacencyIfAvailable(dungeonInstance, levelGrid, uuidLabels, newNode, x, y + 1, "DOWN");
				}	
			}
		}
		
		// Put start in middle
		String name = uuidLabels[(uuidLabels.length - 1) / 2][(uuidLabels[0].length - 1) /2].toString();
		
		//System.out.println(Arrays.deepToString(uuidLabels));
		
		dungeonInstance.setCurrentLevel(name);
		dungeonInstance.setLevelThere(uuidLabels);
		
		return dungeonInstance;

	}
	
	/**
	 * CPPN is queried at each point in a 2D grid and generates a latent vector for the GAN to store at that location in an array.
	 * @param cppn Neural network that creates latent vectors
	 * @param width Width of Dungeon grid (second dimension of array)
	 * @param height Height of Dungeon grid (first dimension of array)
	 * @param inputMultipliers Multipliers for CPPN inputs which has potential to disable them
	 * @return 3D array that is a 2D grid of latent vectors
	 */
	public static Pair<double[][][],double[][][]> latentVectorGridFromCPPN(Network cppn, int width, int height, double[] inputMultipliers) {
		double[][][] latentVectorGrid = new double[height][width][];
		double[][][] presenceAndTriforceGrid = new double[height][width][];
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				ILocated2D scaled = CartesianGeometricUtilities.centerAndScale(new Tuple2D(x, y), width, height);
				double[] remixedInputs = { scaled.getX(), scaled.getY(), scaled.distance(new Tuple2D(0, 0)) * GraphicsUtil.SQRT2, GraphicsUtil.BIAS };
				// Might turn some inputs on/off
				for(int i = 0; i < remixedInputs.length; i++) {
					remixedInputs[i] *= inputMultipliers[i];
				}
				double[] vector = cppn.process(remixedInputs);
				double[] latentVector = new double[GANProcess.latentVectorLength()]; // Shorter
				System.arraycopy(vector, NUM_NON_LATENT_INPUTS, latentVector, 0, latentVector.length);
				latentVectorGrid[y][x] = latentVector;
				
				double[] auxiliaryInformation = new double[NUM_NON_LATENT_INPUTS];
				System.arraycopy(vector, 0, auxiliaryInformation, 0, NUM_NON_LATENT_INPUTS);
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
	public static List<List<Integer>>[][] levelGridFromLatentVectorGrid(double[][][] latentVectorGrid,double[][][] auxiliaryInformation) {
		@SuppressWarnings("unchecked")
		List<List<Integer>>[][] levelAsListsGrid = (List<List<Integer>>[][]) new List[latentVectorGrid.length][latentVectorGrid[0].length];
		for(int y = 0; y < levelAsListsGrid.length; y++) {
			for(int x = 0; x < levelAsListsGrid[0].length; x++) {
				if(auxiliaryInformation[y][x][INDEX_ROOM_PRESENCE] > 0) { // Room presence threshold is 0: TODO: Make parameter?
					levelAsListsGrid[y][x] = ZeldaGANUtil.generateOneRoomListRepresentationFromGAN(latentVectorGrid[y][x]);
				} else {
					levelAsListsGrid[y][x] = null;
				}
			}
		}
		return levelAsListsGrid;
	}
	
	
	public static void main(String[] args) {
		try {
			MMNEAT.main(new String[]{"runNumber:0","randomSeed:1","showKLOptions:false","trials:1","mu:16","zeldaGANModel:ZeldaFixedDungeonsAll_5000_10.pth","maxGens:500","io:false","netio:false","GANInputSize:10","mating:true","fs:false","task:edu.southwestern.tasks.interactive.gvgai.ZeldaCPPNtoGANLevelBreederTask","cleanOldNetworks:false", "zeldaGANUsesOriginalEncoding:false","allowMultipleFunctions:true","ftype:0","watch:true","netChangeActivationRate:0.3","cleanFrequency:-1","simplifiedInteractiveInterface:false","recurrency:false","saveAllChampions:true","cleanOldNetworks:false","ea:edu.southwestern.evolution.selectiveBreeding.SelectiveBreedingEA","imageWidth:2000","imageHeight:2000","imageSize:200","includeFullSigmoidFunction:true","includeFullGaussFunction:true","includeCosineFunction:true","includeGaussFunction:false","includeIdFunction:true","includeTriangleWaveFunction:true","includeSquareWaveFunction:true","includeFullSawtoothFunction:true","includeSigmoidFunction:false","includeAbsValFunction:false","includeSawtoothFunction:false"});
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
}
