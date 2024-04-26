package edu.southwestern.tasks.evocraft.fitness;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftClient.Orientation;
import edu.southwestern.tasks.evocraft.MinecraftServer;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;
import edu.southwestern.util.datastructures.Vertex;

public class ChangeCenterOfMassFitnessTest {
	
	MinecraftCoordinates ranges = new MinecraftCoordinates(10, 10, 10);
	ChangeCenterOfMassFitness ff;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CommonConstants.netio = false;
		Parameters.initializeParameterCollections(new String[] {"minecraftClearWithGlass:false","minecraftXRange:6","minecraftYRange:6","minecraftZRange:6","spaceBetweenMinecraftShapes:6","minecraftAccumulateChangeInCenterOfMass:true","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet"});
		if(!MinecraftServer.serverIsRunning()) {
			MinecraftServer.launchServer();
			MinecraftClient.getMinecraftClient();
		}
		CommonConstants.watch = true; // Displays debugging info
	}

	@Before
	public void setUp() throws Exception {
		ff = new ChangeCenterOfMassFitness();
		CommonConstants.watch = true; // Displays debugging info
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		MinecraftServerTestTracker.decrementServerTestCount();
		
		// Only terminate the server if no other tests will need it
		if(MinecraftServerTestTracker.checkServerTestCount() == 0) {
			long waitTime = Parameters.parameters.longParameter("minecraftMandatoryWaitTime");
			Thread.sleep(waitTime);
			
			MinecraftClient.terminateClientScriptProcess();
			MinecraftServer.terminateServer();
		}
		CommonConstants.watch = false; // Displays debugging info
	}

	// Passes
	@Test
	public void testStagnantStructureQuickly() {
		Parameters.initializeParameterCollections(new String[] {"watch:false","minecraftClearWithGlass:false","minecraftXRange:6","minecraftYRange:6","minecraftZRange:6","spaceBetweenMinecraftShapes:6","minecraftAccumulateChangeInCenterOfMass:true","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet"});
		CommonConstants.netio = false;
		MinecraftCoordinates cornerBS1 = new MinecraftCoordinates(-26,7,-35);
		
		System.out.println("\n TEST: ChangeCenterOfMassFitnessTest: testStagnantStructureQuickly");

		//set up test corner and clear area
		MinecraftCoordinates testCorner = MinecraftClient.getMinecraftClient().checkForYOutOfBoundsAndShiftUp(cornerBS1);
		MinecraftClient.getMinecraftClient().clearEvaluationSpaceForJUnitTests(testCorner);
		
		List<Block> blockSet1 = new ArrayList<>();
		blockSet1.add(new Block(-25,7,-35,BlockType.REDSTONE_BLOCK, Orientation.WEST));
		blockSet1.add(new Block(-24,7,-35,BlockType.PISTON, Orientation.EAST));
		
		//shift coordinates based on the testCorner
		MinecraftCoordinates originalShapeCoordinates = MinecraftUtilClass.minCoordinates(blockSet1);
		blockSet1 = MinecraftUtilClass.shiftBlocksBetweenCorners(blockSet1, originalShapeCoordinates, testCorner);

//		MinecraftClient.getMinecraftClient().spawnBlocks(blockSet1);

		assertEquals(0.5, ff.fitnessScore(testCorner,blockSet1),0.0);		
		System.out.println("\n");
	}
	
	
	@Test
	public void testSimpleCases() {
		Parameters.initializeParameterCollections("spaceBetweenMinecraftShapes:22 minecraftXRange:4 minecraftYRange:4 minecraftZRange:4 minecraftChangeCenterOfMassFitness:true launchMinecraftServerFromJava:false io:false netio:false spaceBetweenMinecraftShapes:5 voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true minecraftClearSleepTimer:400".split(" "));
		CommonConstants.netio = false;
		CommonConstants.watch = false; // For extra debug info
		
		System.out.println("\n TEST: ChangeCenterOfMassFitnessTest: testSimpleCases");

		MinecraftCoordinates cornerBS1 = new MinecraftCoordinates(0,8,0);
		//set up test corner and clear area
		MinecraftCoordinates testCorner = MinecraftClient.getMinecraftClient().checkForYOutOfBoundsAndShiftUp(cornerBS1);
		MinecraftClient.getMinecraftClient().clearEvaluationSpaceForJUnitTests(testCorner);

		ArrayList<Block> blockSet1 = new ArrayList<>();
		blockSet1.add(new Block(0,8,0,BlockType.REDSTONE_BLOCK,Orientation.SOUTH));
		
//		MinecraftClient.getMinecraftClient().spawnBlocks(blockSet1);
		double fitness = ff.fitnessScore(cornerBS1,blockSet1);
		System.out.println("fitness = "+fitness);
		assertEquals(0.0, fitness, 0.0);
		
		// Now that the fitness function has the original blocks from the generator, it can correctly
		// award a non-zero fitness to a simple piston extension.
		blockSet1.add(new Block(1,8,0,BlockType.PISTON,Orientation.NORTH));
//		MinecraftClient.getMinecraftClient().spawnBlocks(blockSet1);
		double fitness2 = ff.fitnessScore(cornerBS1,blockSet1);
		System.out.println("fitness = "+fitness2);
		assertEquals(0.3726779962499649, fitness2, 0.0000000000001);
		
		blockSet1.add(new Block(1,8,-1,BlockType.SLIME,Orientation.NORTH));
//		MinecraftClient.getMinecraftClient().spawnBlocks(blockSet1);
		double fitness3 = ff.fitnessScore(cornerBS1,blockSet1);
		System.out.println("fitness = "+fitness3);
		assertEquals(0.42491829279939874, fitness3, 0.0000000000001);
		System.out.println("\n");
	}
	
	@Test
	public void testBigSmallMove() {
		Parameters.initializeParameterCollections("minecraftXRange:4 minecraftYRange:4 minecraftZRange:4 watch:false minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.VectorToVolumeGenerator minecraftChangeCenterOfMassFitness:true minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet trials:1 mu:100 maxGens:100000 minecraftContainsWholeMAPElitesArchive:true forceLinearArchiveLayoutInMinecraft:false launchMinecraftServerFromJava:false io:false netio:false interactWithMapElitesInWorld:true mating:true fs:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 spaceBetweenMinecraftShapes:25 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearSleepTimer:400 mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesWidthHeightDepthBinLabels".split(" "));
		CommonConstants.netio = false;
		
		System.out.println("\n TEST: ChangeCenterOfMassFitnessTest: testBigSmallMove");

		MinecraftCoordinates cornerBS1 = new MinecraftCoordinates(28,33,28);

		//set up test corner and clear area
		MinecraftCoordinates testCorner = MinecraftClient.getMinecraftClient().checkForYOutOfBoundsAndShiftUp(cornerBS1);
		MinecraftClient.getMinecraftClient().clearEvaluationSpaceForJUnitTests(testCorner);	

		List<Block> blockSet1 = new ArrayList<>();
		
		blockSet1.add(new Block(29,35,29,BlockType.REDSTONE_BLOCK,Orientation.SOUTH)); 
		blockSet1.add(new Block(29,35,31,BlockType.PISTON,Orientation.DOWN)); 
		blockSet1.add(new Block(29,36,30,BlockType.STICKY_PISTON,Orientation.EAST)); 
		blockSet1.add(new Block(29,36,32,BlockType.OBSERVER,Orientation.UP)); 
		blockSet1.add(new Block(29,37,32,BlockType.OBSERVER,Orientation.WEST)); 
		blockSet1.add(new Block(30,34,32,BlockType.PISTON,Orientation.SOUTH)); 
		blockSet1.add(new Block(30,35,31,BlockType.SLIME,Orientation.EAST)); 
		blockSet1.add(new Block(30,36,30,BlockType.QUARTZ_BLOCK,Orientation.NORTH)); 
		blockSet1.add(new Block(30,37,29,BlockType.STICKY_PISTON,Orientation.UP)); 
		blockSet1.add(new Block(30,37,30,BlockType.OBSERVER,Orientation.SOUTH)); 
		blockSet1.add(new Block(30,37,32,BlockType.PISTON,Orientation.DOWN)); 
		blockSet1.add(new Block(31,34,30,BlockType.OBSERVER,Orientation.EAST)); 
		blockSet1.add(new Block(31,34,31,BlockType.STICKY_PISTON,Orientation.UP)); 
		blockSet1.add(new Block(31,34,32,BlockType.SLIME,Orientation.NORTH)); 
		blockSet1.add(new Block(31,35,29,BlockType.QUARTZ_BLOCK,Orientation.DOWN)); 
		blockSet1.add(new Block(31,35,30,BlockType.QUARTZ_BLOCK,Orientation.WEST)); 
		blockSet1.add(new Block(31,35,31,BlockType.SLIME,Orientation.NORTH)); 
		blockSet1.add(new Block(31,36,31,BlockType.STICKY_PISTON,Orientation.DOWN)); 
		blockSet1.add(new Block(31,37,32,BlockType.SLIME,Orientation.WEST)); 
		blockSet1.add(new Block(32,35,29,BlockType.PISTON,Orientation.EAST)); 
		blockSet1.add(new Block(32,35,30,BlockType.QUARTZ_BLOCK,Orientation.WEST)); 
		blockSet1.add(new Block(32,35,31,BlockType.REDSTONE_BLOCK,Orientation.WEST)); 
		blockSet1.add(new Block(32,35,32,BlockType.QUARTZ_BLOCK,Orientation.NORTH)); 
		blockSet1.add(new Block(32,36,32,BlockType.REDSTONE_BLOCK,Orientation.DOWN)); 
		blockSet1.add(new Block(32,37,29,BlockType.PISTON,Orientation.WEST)); 
		blockSet1.add(new Block(32,37,30,BlockType.STICKY_PISTON,Orientation.NORTH)); 
		blockSet1.add(new Block(32,37,32,BlockType.REDSTONE_BLOCK,Orientation.DOWN));		

		//shift coordinates based on the testCorner
		MinecraftCoordinates originalShapeCoordinates = MinecraftUtilClass.minCoordinates(blockSet1);
		blockSet1 = MinecraftUtilClass.shiftBlocksBetweenCorners(blockSet1, originalShapeCoordinates, testCorner);


//		MinecraftClient.getMinecraftClient().spawnBlocks(blockSet1);
		double fitness = ff.fitnessScore(testCorner,blockSet1);
		System.out.println("fitness = "+fitness);
		assertTrue(0.03 <= fitness);
		assertTrue(0.5825731613780569 >= fitness);
		System.out.println("\n");
	}
	
	// Passes
	@Test
	public void testChangeInTotalDistance() throws InterruptedException {
		Parameters.initializeParameterCollections(new String[] {"minecraftXRange:6","minecraftYRange:6","minecraftZRange:6","spaceBetweenMinecraftShapes:6","minecraftAccumulateChangeInCenterOfMass:true","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 1000L, "minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet"});
		CommonConstants.netio = false;
		MinecraftCoordinates cornerBS2 = new MinecraftCoordinates(0,11,-5);
		
		//set up test corner and clear area
		MinecraftCoordinates testCorner = MinecraftClient.getMinecraftClient().checkForYOutOfBoundsAndShiftUp(cornerBS2);
		MinecraftClient.getMinecraftClient().clearEvaluationSpaceForJUnitTests(testCorner);

		System.out.println("\n TEST: ChangeCenterOfMassFitnessTest: testChangeInTotalDistance");

		// List of flying machine blocks that should move
		// Not really sure what the fitness would be after 10 seconds
		List<Block> blockSet2 = new ArrayList<>();
		// Bottom layer
		blockSet2.add(new Block(1,11,1,BlockType.PISTON,Orientation.NORTH));
		blockSet2.add(new Block(1,11,0,BlockType.SLIME,Orientation.NORTH));
		blockSet2.add(new Block(1,11,-1,BlockType.STICKY_PISTON,Orientation.SOUTH));
		blockSet2.add(new Block(1,11,-2,BlockType.PISTON,Orientation.NORTH));
		blockSet2.add(new Block(1,11,-4,BlockType.SLIME,Orientation.NORTH));
		// Top layer
		blockSet2.add(new Block(1,12,0,BlockType.REDSTONE_BLOCK,Orientation.NORTH));
		blockSet2.add(new Block(1,12,-4,BlockType.REDSTONE_BLOCK,Orientation.NORTH));
		// Activate
		blockSet2.add(new Block(1,12,-1,BlockType.QUARTZ_BLOCK,Orientation.NORTH));
		
		
		//shift coordinates based on the testCorner
		MinecraftCoordinates originalShapeCoordinates = MinecraftUtilClass.minCoordinates(blockSet2);
		blockSet2 = MinecraftUtilClass.shiftBlocksBetweenCorners(blockSet2, originalShapeCoordinates, testCorner);
		
		
		System.out.println("shortTimeBetweenMinecraftReads = " + Parameters.parameters.longParameter("shortTimeBetweenMinecraftReads") + " testChangeInTotalDistance");
//		MinecraftClient.getMinecraftClient().spawnBlocks(blockSet2);

		// Since it is moving out completely, and all the ranges are the same value (10)
		// That means the max fitness is 10 + 6 / 2 = 8
		// However, the movement speed of the flying machine depends on the speed of the independently
		// executing Minecraft server, which is subject to variation. The main point is that the ship
		// flies for a bit, but the exact amount is hard to pin down. Thus, we only assert that the amount
		// is 6.0 or more
		//System.out.println("Fitness for the blockSet 2: "+ ff.fitnessScore(cornerBS2));
		assertEquals(ff.maxFitness(), ff.fitnessScore(testCorner,blockSet2),0.0);
		
		//MinecraftClient.getMinecraftClient().clearSpaceForShapes(cornerBS2, ranges, 1, 100);
		System.out.println("\n");
	}
	
	
	// added minecraftRewardFastFlyingMachine parameter, created variables to control expected fitness and wiggle room
	// created more space between shapes and moved the shape up to keep from going out of bounds
	// created while testing rewarding flying machine speed functionality
	//important: minecraftEmptySpaceBuffer=18, minecraftXRange=10, spaceBetweenMinecraftShapes=30
	//this returns as not flying, but if the spaceBetweenMinecraftShapes is reduced to 20 it will count as flying
	//this is also testing something we found impossible (moving faster)
	//passed
	@Test
	public void testFlyingRewardSpeed() throws InterruptedException {
		Parameters.initializeParameterCollections(new String[] {"minecraftEmptySpaceBufferX:18", "minecraftEmptySpaceBufferY:18", "minecraftEmptySpaceBufferZ:18", "watch:true", "minecraftRewardFastFlyingMachines:true", "minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:30","minecraftAccumulateChangeInCenterOfMass:true","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 1000L, "minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet"});
		CommonConstants.netio = false;
		MinecraftCoordinates cornerBS2 = new MinecraftCoordinates(0,16,-5);
		
		//set up test corner and clear area
		MinecraftCoordinates testCorner = MinecraftClient.getMinecraftClient().checkForYOutOfBoundsAndShiftUp(cornerBS2);
		MinecraftClient.getMinecraftClient().clearEvaluationSpaceForJUnitTests(testCorner);	
		
		System.out.println("\n TEST: ChangeCenterOfMassFitnessTest: testFlyingRewardSpeed");

		// List of flying machine blocks that should move
		// Not really sure what the fitness would be after 10 seconds
		List<Block> blockSet2 = new ArrayList<>();
		// Bottom layer
		blockSet2.add(new Block(1,11,1,BlockType.PISTON,Orientation.NORTH));
		blockSet2.add(new Block(1,11,0,BlockType.SLIME,Orientation.NORTH));
		blockSet2.add(new Block(1,11,-1,BlockType.STICKY_PISTON,Orientation.SOUTH));
		blockSet2.add(new Block(1,11,-2,BlockType.PISTON,Orientation.NORTH));
		blockSet2.add(new Block(1,11,-4,BlockType.SLIME,Orientation.NORTH));
		// Top layer
		blockSet2.add(new Block(1,12,0,BlockType.REDSTONE_BLOCK,Orientation.NORTH));
		blockSet2.add(new Block(1,12,-4,BlockType.REDSTONE_BLOCK,Orientation.NORTH));
		// Activate
		blockSet2.add(new Block(1,12,-1,BlockType.QUARTZ_BLOCK,Orientation.NORTH));
		
		System.out.println("shortTimeBetweenMinecraftReads = " + Parameters.parameters.longParameter("shortTimeBetweenMinecraftReads"));
		//MinecraftClient.getMinecraftClient().spawnBlocks(blockSet2);

		//shift coordinates based on the testCorner
//		MinecraftCoordinates originalShapeCoordinates = MinecraftUtilClass.minCoordinates(blockSet2);
//		blockSet2 = MinecraftUtilClass.shiftBlocksBetweenCorners(blockSet2, originalShapeCoordinates, testCorner);
		
		//System.out.println("Fitness for the blockSet 2: "+ ff.fitnessScore(cornerBS2));
		
		//changing settings that are being tested and minecraftRewardFastFlyingMachines
		double wiggleRoom = 15.0;
		double expected = 70.0;
		//69.97351321372638 , 70.15409121596679 , 69.55696170066884 , 69.48746633678942
		assertEquals(expected, ff.fitnessScore(cornerBS2,blockSet2),wiggleRoom);
		System.out.println("\n");
		//expected 80 was 164
	}
	
	// Notes from Joanna:
	// tests flying machine with changed space between shapes, expected fitness and wiggle room
	// moved the y access for spawning the shape
	// created while testing rewarding flying machine speed functionality
	// passed
	//
	// Notes from Dr. Schrum:
	// Now we try to detect and reward flying machines with max fitness even if they do not leave
	// the evaluation area (assuming it is made large enough), so this fitness calculation is invalid,
	// as is the test.
//	@Test
//	public void testFlyingWithoutMaxFitness() throws InterruptedException {
//		Parameters.initializeParameterCollections(new String[] { "minecraftClearWithGlass:false", "minecraftXRange:6","minecraftYRange:6","minecraftZRange:6","spaceBetweenMinecraftShapes:30","minecraftAccumulateChangeInCenterOfMass:true","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 1000L, "minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet"});
//		MinecraftCoordinates cornerBS2 = new MinecraftCoordinates(0,11,-5);	// what is this the corner of? - shape corner
//
//		//set up test corner and clear area
//		MinecraftCoordinates testCorner = MinecraftClient.getMinecraftClient().checkForYOutOfBoundsAndShiftUp(cornerBS2);
//		MinecraftClient.getMinecraftClient().clearEvaluationSpaceForJUnitTests(testCorner);	
//
//		System.out.println("testFlyingWithoutMaxFitness");
//
//		// List of flying machine blocks that should move
//		// Not really sure what the fitness would be after 10 seconds
//		List<Block> blockSet2 = new ArrayList<>();
//		// Bottom layer
//		blockSet2.add(new Block(1,11,1,BlockType.PISTON,Orientation.NORTH));
//		blockSet2.add(new Block(1,11,0,BlockType.SLIME,Orientation.NORTH));
//		blockSet2.add(new Block(1,11,-1,BlockType.STICKY_PISTON,Orientation.SOUTH));
//		blockSet2.add(new Block(1,11,-2,BlockType.PISTON,Orientation.NORTH));
//		blockSet2.add(new Block(1,11,-4,BlockType.SLIME,Orientation.NORTH));
//		// Top layer
//		blockSet2.add(new Block(1,12,0,BlockType.REDSTONE_BLOCK,Orientation.NORTH));
//		blockSet2.add(new Block(1,12,-4,BlockType.REDSTONE_BLOCK,Orientation.NORTH));
//		// Activate
//		blockSet2.add(new Block(1,12,-1,BlockType.QUARTZ_BLOCK,Orientation.NORTH));
//
//		//shift coordinates based on the testCorner
//		MinecraftCoordinates originalShapeCoordinates = MinecraftUtilClass.minCoordinates(blockSet2);
//		List<Block> newblockSet2 = MinecraftUtilClass.shiftBlocksBetweenCorners(blockSet2, originalShapeCoordinates, testCorner);
//		
//		System.out.println("shortTimeBetweenMinecraftReads = " + Parameters.parameters.longParameter("shortTimeBetweenMinecraftReads"));
//		MinecraftClient.getMinecraftClient().spawnBlocks(blockSet2);
//		
//		double wiggleRoom = 2.0;
//		double expected = 17.0;
//		assertEquals(expected, ff.fitnessScore(testCorner,newblockSet2),wiggleRoom);
//	}
		
	// Passes
	@Test
	public void testChangeInPositionWithRemainingBlocks() {
		Parameters.initializeParameterCollections(new String[] {"netio:false", "minecraftClearWithGlass:false", "watch:false","minecraftXRange:6","minecraftYRange:6","minecraftZRange:6","spaceBetweenMinecraftShapes:6","minecraftAccumulateChangeInCenterOfMass:true","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet"});
		CommonConstants.netio = false;
		
		System.out.println("\n TEST: ChangeCenterOfMassFitnessTest: testChangeInPositionWithRemainingBlocks");

		boolean result = flyingMachineWithRemainingBlocks();
		if(!result) {
			// Get a second chance
			result = flyingMachineWithRemainingBlocks();
		}

		assertTrue(result); // Should be able to succeed in one of two attempts
		System.out.println("\n");
	}

	/**
	 * TODO:  JavaDoc 
	 * @return
	 */
	public boolean flyingMachineWithRemainingBlocks() {
		Parameters.initializeParameterCollections(new String[] {"watch:false", "minecraftClearWithGlass:false", "minecraftXRange:6","minecraftYRange:6","minecraftZRange:6","spaceBetweenMinecraftShapes:6","minecraftAccumulateChangeInCenterOfMass:false","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 1000L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet"});
		CommonConstants.netio = false;
		MinecraftCoordinates cornerBS2 = new MinecraftCoordinates(0,11,-5);
		//set up test corner and clear area
		MinecraftCoordinates testCorner = MinecraftClient.getMinecraftClient().checkForYOutOfBoundsAndShiftUp(cornerBS2);
		MinecraftClient.getMinecraftClient().clearEvaluationSpaceForJUnitTests(testCorner);		
		
		// List of flying machine blocks that should move
		// Not really sure what the fitness would be after 10 seconds
		List<Block> blockSet2 = new ArrayList<>();
		// Bottom layer
		blockSet2.add(new Block(1,11,1,BlockType.PISTON,Orientation.NORTH));
		blockSet2.add(new Block(1,11,0,BlockType.SLIME,Orientation.NORTH));
		blockSet2.add(new Block(1,11,-1,BlockType.STICKY_PISTON,Orientation.SOUTH));
		blockSet2.add(new Block(1,11,-2,BlockType.PISTON,Orientation.NORTH));
		blockSet2.add(new Block(1,11,-4,BlockType.SLIME,Orientation.NORTH));
		// Top layer
		blockSet2.add(new Block(1,12,0,BlockType.REDSTONE_BLOCK,Orientation.NORTH));
		blockSet2.add(new Block(1,12,-4,BlockType.REDSTONE_BLOCK,Orientation.NORTH));
		// Activate
		blockSet2.add(new Block(1,12,-1,BlockType.QUARTZ_BLOCK,Orientation.NORTH));

		// Left-over garbage: 3 blocks
		blockSet2.add(new Block(2,10,-1,BlockType.QUARTZ_BLOCK,Orientation.NORTH));
		blockSet2.add(new Block(2,10,0,BlockType.QUARTZ_BLOCK,Orientation.NORTH));
		blockSet2.add(new Block(2,10,1,BlockType.QUARTZ_BLOCK,Orientation.NORTH));

		//MinecraftClient.getMinecraftClient().spawnBlocks(blockSet2);
		//shift coordinates based on the testCorner
		MinecraftCoordinates originalShapeCoordinates = MinecraftUtilClass.minCoordinates(blockSet2);
		blockSet2 = MinecraftUtilClass.shiftBlocksBetweenCorners(blockSet2, originalShapeCoordinates, testCorner);


		// Three blocks remaining, so -0.3
		boolean result1 = ff.maxFitness()-0.3 == ff.fitnessScore(testCorner,blockSet2);
		return result1;
	}
	
	//passed
	//a small shape that explodes and leaves no blocks behind
	@Test
	public void testTNTnoMovement() {
		Parameters.initializeParameterCollections(new String[] {"watch:false", "minecraftClearWithGlass:false","minecraftXRange:6","minecraftYRange:6","minecraftZRange:6","spaceBetweenMinecraftShapes:6","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.ExplosiveBlockSet"});
		
		CommonConstants.netio = false;
		System.out.println("\n TEST: ChangeCenterOfMassFitnessTest: testTNTnoMovement");

		MinecraftCoordinates testCorner = new MinecraftCoordinates(-26,27,-35);
		testCorner = MinecraftClient.getMinecraftClient().checkForYOutOfBoundsAndShiftUp(testCorner);
		MinecraftClient.getMinecraftClient().clearEvaluationSpaceForJUnitTests(testCorner);

		List<Block> testBlockSet = new ArrayList<>();
		testBlockSet.add(new Block(-25,27,-35,BlockType.REDSTONE_BLOCK, Orientation.NORTH));
		testBlockSet.add(new Block(-24,27,-35,BlockType.TNT, Orientation.NORTH));
		
		//coordinates for the base
		int xMinCoordinate = -25;
		int xMaxCoordinate = -23;
		int zMinCoordinate = -36;
		int zMaxCoordinate = -34;
		int yBaseCoodinate = 25;

		//creates the base platform
		for(int xIndex = xMinCoordinate; xIndex <= xMaxCoordinate; xIndex++) {
			for(int zIndex = zMinCoordinate; zIndex <= zMaxCoordinate; zIndex++) {
				testBlockSet.add(new Block(xIndex,yBaseCoodinate,zIndex,BlockType.SLIME, Orientation.NORTH));
			}
		}
		//shift coordinates based on the testCorner
		MinecraftCoordinates originalShapeCoordinates = MinecraftUtilClass.minCoordinates(testBlockSet);
		testBlockSet = MinecraftUtilClass.shiftBlocksBetweenCorners(testBlockSet, originalShapeCoordinates, testCorner);

		assertEquals(0.16388869433927275, ff.fitnessScore(testCorner,testBlockSet),0.0); // Seems like a lot of wiggle room ... too much?
		System.out.println("\n");
	}
	
	//passed
	//uses a string to create shape. Is a large shape that explodes and leaves some blocks behind with no movement
	@Test
	public void testTNTnoMovementLarger() {
		// Had to reduce minecraftEmptySpaceBuffer to avoid crashing server
		Parameters.initializeParameterCollections(new String[] {"watch:false", "minecraftEmptySpaceBufferX:18", "minecraftEmptySpaceBufferY:18", "minecraftEmptySpaceBufferZ:18", "minecraftClearWithGlass:false","minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:22","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.ExplosiveBlockSet"});
		CommonConstants.netio = false;

		System.out.println("\n TEST: ChangeCenterOfMassFitnessTest: testTNTnoMovementLarger");

		//set up test corner and clear area
		MinecraftCoordinates testCorner = new MinecraftCoordinates(-26,27,-35);
		testCorner = MinecraftClient.getMinecraftClient().checkForYOutOfBoundsAndShiftUp(testCorner);
		MinecraftClient.getMinecraftClient().clearEvaluationSpaceForJUnitTests(testCorner);

		//this string is for a machine that explodes, is large, and leaves a few blocks behind
		String listString = "[QUARTZ_BLOCK at (-1433,37,-1432) oriented NORTH, TNT at (-1433,37,-1431) oriented UP, QUARTZ_BLOCK at (-1433,37,-1429) oriented NORTH, REDSTONE_BLOCK at (-1433,38,-1433) oriented NORTH, OBSERVER at (-1433,38,-1431) oriented DOWN, QUARTZ_BLOCK at (-1433,39,-1433) oriented UP, OBSERVER at (-1433,39,-1431) oriented NORTH, TNT at (-1433,40,-1431) oriented WEST, QUARTZ_BLOCK at (-1433,40,-1430) oriented NORTH, OBSERVER at (-1433,41,-1433) oriented NORTH, QUARTZ_BLOCK at (-1432,37,-1432) oriented NORTH, QUARTZ_BLOCK at (-1432,37,-1430) oriented UP, PISTON at (-1432,39,-1429) oriented NORTH, STICKY_PISTON at (-1432,40,-1433) oriented NORTH, QUARTZ_BLOCK at (-1432,40,-1432) oriented NORTH, QUARTZ_BLOCK at (-1432,40,-1431) oriented NORTH, OBSERVER at (-1432,41,-1433) oriented EAST, QUARTZ_BLOCK at (-1432,41,-1430) oriented EAST, QUARTZ_BLOCK at (-1431,37,-1431) oriented NORTH, QUARTZ_BLOCK at (-1431,37,-1429) oriented NORTH, STICKY_PISTON at (-1431,38,-1432) oriented NORTH, QUARTZ_BLOCK at (-1431,39,-1433) oriented NORTH, QUARTZ_BLOCK at (-1431,41,-1432) oriented NORTH, QUARTZ_BLOCK at (-1430,38,-1430) oriented WEST, TNT at (-1430,39,-1432) oriented NORTH, QUARTZ_BLOCK at (-1430,40,-1433) oriented DOWN, QUARTZ_BLOCK at (-1430,41,-1433) oriented EAST, REDSTONE_BLOCK at (-1430,41,-1432) oriented WEST, QUARTZ_BLOCK at (-1429,37,-1430) oriented UP, QUARTZ_BLOCK at (-1429,37,-1429) oriented EAST, TNT at (-1429,39,-1432) oriented NORTH, QUARTZ_BLOCK at (-1429,41,-1433) oriented NORTH, TNT at (-1429,41,-1432) oriented SOUTH, SLIME at (-1429,41,-1429) oriented NORTH]";
		List<Block> testBlockSet = MinecraftUtilClass.readMinecraftBlockListFromString(listString);

		//System.out.println("blocklist: " + testBlockSet);

		//shift coordinates based on the testCorner
		MinecraftCoordinates originalShapeCoordinates = MinecraftUtilClass.minCoordinates(testBlockSet);
		List<Block> newTestBlockSet = MinecraftUtilClass.shiftBlocksBetweenCorners(testBlockSet, originalShapeCoordinates, testCorner);

		newTestBlockSet.add(new Block(-26,25,-30,BlockType.OBSIDIAN,Orientation.NORTH));
//get's 4.6
		assertEquals(0.09488358802109932, ff.fitnessScore(testCorner,newTestBlockSet),0.0); 
		System.out.println("\n");
	}
	
	//testing a flying machine with TNT on it, evaluation ends before TNT explodes, shape leaves evaluation area before exploding
	@Test
	public void testTNTwithFlyingMachine() {
		//testing a flying machine that has tnt on it
		Parameters.initializeParameterCollections(new String[] {"minecraftEmptySpaceBufferX:18", "minecraftEmptySpaceBufferY:18", "minecraftEmptySpaceBufferZ:18", "minecraftRewardFastFlyingMachines:false", "watch:false","minecraftClearWithGlass:false","minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:6","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.ExplosiveBlockSet"});
		CommonConstants.netio = false;

		//set up test corner and clear area
		MinecraftCoordinates testCorner = new MinecraftCoordinates(-26,27,-35);
		testCorner = MinecraftClient.getMinecraftClient().checkForYOutOfBoundsAndShiftUp(testCorner);
		MinecraftClient.getMinecraftClient().clearEvaluationSpaceForJUnitTests(testCorner);

		List<Block> testShapeBlockList = new ArrayList<>();
		// Bottom layer
		testShapeBlockList.add(new Block(-26,28,-30,BlockType.PISTON,Orientation.NORTH));
		testShapeBlockList.add(new Block(-26,28,-31,BlockType.SLIME,Orientation.NORTH));
		testShapeBlockList.add(new Block(-26,28,-32,BlockType.STICKY_PISTON,Orientation.SOUTH));
		testShapeBlockList.add(new Block(-26,28,-33,BlockType.PISTON,Orientation.NORTH));
		testShapeBlockList.add(new Block(-26,28,-35,BlockType.SLIME,Orientation.NORTH));

		// Top layer
		testShapeBlockList.add(new Block(-26,29,-31,BlockType.REDSTONE_BLOCK,Orientation.NORTH));
		testShapeBlockList.add(new Block(-26,29,-35,BlockType.REDSTONE_BLOCK,Orientation.NORTH));
		
		//tnt
		testShapeBlockList.add(new Block(-26,29,-34,BlockType.TNT,Orientation.NORTH));
		
		// Activate
		testShapeBlockList.add(new Block(-26,30,-4,BlockType.QUARTZ_BLOCK,Orientation.NORTH));
		
		System.out.println("\n TEST: ChangeCenterOfMassFitnessTest: testTNTwithFlyingMachine");
		
		MinecraftCoordinates originalShapeCoordinates = MinecraftUtilClass.minCoordinates(testShapeBlockList);
		testShapeBlockList = MinecraftUtilClass.shiftBlocksBetweenCorners(testShapeBlockList, originalShapeCoordinates, testCorner);

		
		double wiggleRoom = 0.0;
		double expected = 8.0;
		assertEquals(expected, ff.fitnessScore(testCorner,testShapeBlockList),wiggleRoom);
		System.out.println("\n");
	}
	
	@Test
	public void testTNTOscillating() {		
		Parameters.initializeParameterCollections(new String[] {"minecraftClearWithGlass:false","minecraftXRange:6","minecraftYRange:6","minecraftZRange:6","spaceBetweenMinecraftShapes:22","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.ExplosiveBlockSet"});
		CommonConstants.netio = false;

		System.out.println("\n TEST: ChangeCenterOfMassFitnessTest: testTNTOscillating");

		MinecraftCoordinates testCorner = new MinecraftCoordinates(-26,27,-35);
		testCorner = MinecraftClient.getMinecraftClient().checkForYOutOfBoundsAndShiftUp(testCorner);
		MinecraftClient.getMinecraftClient().clearEvaluationSpaceForJUnitTests(testCorner);

		List<Block> testBlockSet = new ArrayList<>();
		testBlockSet.add(new Block(-25,27,-35,BlockType.REDSTONE_BLOCK, Orientation.NORTH));
		testBlockSet.add(new Block(-24,27,-35,BlockType.TNT, Orientation.NORTH));
		testBlockSet.add(new Block(-25,29,-35,BlockType.STICKY_PISTON,Orientation.NORTH));
		testBlockSet.add(new Block(-25,29,-36,BlockType.SLIME,Orientation.NORTH));
		testBlockSet.add(new Block(-25,28,-35,BlockType.REDSTONE_BLOCK,Orientation.NORTH));
		testBlockSet.add(new Block(-25,28,-36,BlockType.SLIME,Orientation.NORTH));
		
		//coordinates for the base
		int xMinCoordinate = -25;
		int xMaxCoordinate = -23;
		int zMinCoordinate = -36;
		int zMaxCoordinate = -34;
		int yBaseCoodinate = 25;

		//creates the base platform
		for(int xIndex = xMinCoordinate; xIndex <= xMaxCoordinate; xIndex++) {
			for(int zIndex = zMinCoordinate; zIndex <= zMaxCoordinate; zIndex++) {
				testBlockSet.add(new Block(xIndex,yBaseCoodinate,zIndex,BlockType.SLIME, Orientation.NORTH));
			}
		}
		//shift coordinates based on the testCorner
		MinecraftCoordinates originalShapeCoordinates = MinecraftUtilClass.minCoordinates(testBlockSet);
		testBlockSet = MinecraftUtilClass.shiftBlocksBetweenCorners(testBlockSet, originalShapeCoordinates, testCorner);

		assertEquals(0.06263307827602911, ff.fitnessScore(testCorner,testBlockSet),1.0); 
		System.out.println("\n");
	}	
	
	//passed
	@Test
	public void testSinglePistonShapeEarlyTermination() {
		Parameters.initializeParameterCollections(new String[] {"minecraftRewardFastFlyingMachines:false", "watch:false","minecraftClearWithGlass:false","minecraftXRange:6","minecraftYRange:6","minecraftZRange:6","spaceBetweenMinecraftShapes:6","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.ExplosiveBlockSet"});
		CommonConstants.netio = false;

		//set up test corner and clear area
		MinecraftCoordinates testCorner = new MinecraftCoordinates(-26,27,-35);
		testCorner = MinecraftClient.getMinecraftClient().checkForYOutOfBoundsAndShiftUp(testCorner);
		MinecraftClient.getMinecraftClient().clearEvaluationSpaceForJUnitTests(testCorner);

		List<Block> testShapeBlockList = new ArrayList<>();
		
		testShapeBlockList.add(new Block(-26,25,-35,BlockType.PISTON,Orientation.WEST));
		testShapeBlockList.add(new Block(-26,25,-34,BlockType.REDSTONE_BLOCK,Orientation.NORTH));

		System.out.println("\n TEST: ChangeCenterOfMassFitnessTest: testSinglePistonShapeEarlyTermination");

		//get the min coordinates of the shape to create the shifted shape block list
		MinecraftCoordinates originalShapeCoordinates = MinecraftUtilClass.minCoordinates(testShapeBlockList);
		testShapeBlockList = MinecraftUtilClass.shiftBlocksBetweenCorners(testShapeBlockList, originalShapeCoordinates, testCorner);

		
		double wiggleRoom = 0.0;
		double expected = 0.37267799624996284;
		assertEquals(expected, ff.fitnessScore(testCorner,testShapeBlockList),wiggleRoom);
		System.out.println("\n");
	}
	
	@Test
	public void testSinglePistonShapeClearWithExtraSpace() {
		Parameters.initializeParameterCollections(new String[] {"minecraftRewardFastFlyingMachines:false", "watch:false","minecraftClearWithGlass:false","minecraftXRange:6","minecraftYRange:6","minecraftZRange:6","spaceBetweenMinecraftShapes:6","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.ExplosiveBlockSet"});
		CommonConstants.netio = false;

		//set up test corner and clear area
		MinecraftCoordinates testCorner = new MinecraftCoordinates(-26,27,-35);
		testCorner = MinecraftClient.getMinecraftClient().checkForYOutOfBoundsAndShiftUp(testCorner);
		MinecraftClient.getMinecraftClient().clearEvaluationSpaceForJUnitTests(testCorner);

		ArrayList<Block> testShapeBlockList = new ArrayList<>();
		
		testShapeBlockList.add(new Block(-26,25,-35,BlockType.PISTON,Orientation.WEST));
		testShapeBlockList.add(new Block(-26,25,-34,BlockType.REDSTONE_BLOCK,Orientation.NORTH));

		System.out.println("\n TEST: ChangeCenterOfMassFitnessTest: testSinglePistonShapeClearWithExtraSpace");

		//get the min coordinates of the shape to create the shifted shape block list
		MinecraftCoordinates originalShapeCoordinates = MinecraftUtilClass.minCoordinates(testShapeBlockList);
		List<Block> listBlockSet = MinecraftUtilClass.shiftBlocksBetweenCorners(testShapeBlockList, originalShapeCoordinates, testCorner);

		double wiggleRoom = 0.0;
		double expected = 0.37267799624996284;
		assertEquals(expected, ff.fitnessScore(testCorner,listBlockSet),wiggleRoom);
		System.out.println("\n");
	}

	// Passes
	@Test
	public void testChangeInPosition() {
		Parameters.initializeParameterCollections(new String[] {"minecraftXRange:6","minecraftYRange:6","minecraftZRange:6","spaceBetweenMinecraftShapes:6","minecraftAccumulateChangeInCenterOfMass:false","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 1000L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet"});
		CommonConstants.netio = false;
		MinecraftCoordinates cornerBS2 = new MinecraftCoordinates(0,11,-5);

		//check and clear coordinates
		MinecraftCoordinates testCorner = MinecraftClient.getMinecraftClient().checkForYOutOfBoundsAndShiftUp(cornerBS2);
		MinecraftClient.getMinecraftClient().clearEvaluationSpaceForJUnitTests(testCorner);
		
		System.out.println("corner = "+testCorner);
		
		// List of flying machine blocks that should move
		// Not really sure what the fitness would be after 10 seconds
		ArrayList<Block> blockSet2 = new ArrayList<>();
		// Bottom layer
		blockSet2.add(new Block(1,1,5,BlockType.PISTON,Orientation.NORTH));
		blockSet2.add(new Block(1,1,4,BlockType.SLIME,Orientation.NORTH));
		blockSet2.add(new Block(1,1,3,BlockType.STICKY_PISTON,Orientation.SOUTH));
		blockSet2.add(new Block(1,1,2,BlockType.PISTON,Orientation.NORTH));
		blockSet2.add(new Block(1,1,0,BlockType.SLIME,Orientation.NORTH));
		// Top layer
		blockSet2.add(new Block(1,2,4,BlockType.REDSTONE_BLOCK,Orientation.NORTH));
		blockSet2.add(new Block(1,2,0,BlockType.REDSTONE_BLOCK,Orientation.NORTH));
		// Activate
		blockSet2.add(new Block(1,2,3,BlockType.QUARTZ_BLOCK,Orientation.NORTH));

		System.out.println("shortTimeBetweenMinecraftReads = " + Parameters.parameters.longParameter("shortTimeBetweenMinecraftReads"));
		System.out.println("\n TEST: ChangeCenterOfMassFitnessTest: testChangeInPosition");
		//get the min coordinates of the shape to create the shifted shape block list
		MinecraftCoordinates originalShapeCoordinates = MinecraftUtilClass.minCoordinates(blockSet2);
		List<Block> listBlockSet = MinecraftUtilClass.shiftBlocksBetweenCorners(blockSet2, originalShapeCoordinates, testCorner);

		
		//MinecraftClient.getMinecraftClient().spawnBlocks(listBlockSet);
		// Machine flies away completely
		System.out.println("Flying away should earn "+ff.maxFitness());
		//System.out.println("Second flying machine fitness: " + ff.fitnessScore(cornerBS2));
		assertEquals(ff.maxFitness(), ff.fitnessScore(testCorner,listBlockSet), 0.0);
		System.out.println("\n");
	}
	
	// Passes
	// This test seems to pass reliably in isolation, but not always when run as part of the test suite, so it is disabled.
	@Test
	public void testOscillatingMachine() {
		Parameters.initializeParameterCollections(new String[] {"netio:false", "minecraftXRange:6","minecraftYRange:6","minecraftZRange:6","spaceBetweenMinecraftShapes:6","minecraftAccumulateChangeInCenterOfMass:true","minecraftEndEvalNoMovement:false","shortTimeBetweenMinecraftReads:" + 100L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet"});
		CommonConstants.netio = false;

		System.out.println("\n TEST: ChangeCenterOfMassFitnessTest: testOscillatingMachine");

		MinecraftCoordinates cornerBS2 = new MinecraftCoordinates(0,11,-5);
		
		//check and clear space around test corner
		MinecraftCoordinates testCorner = MinecraftClient.getMinecraftClient().checkForYOutOfBoundsAndShiftUp(cornerBS2);
		MinecraftClient.getMinecraftClient().clearEvaluationSpaceForJUnitTests(testCorner);

		// Machine that moves back and forth (in the same spot)
		ArrayList<Block> oscillatingMachine = new ArrayList<>();
		oscillatingMachine.add(new Block(1,12,1,BlockType.STICKY_PISTON,Orientation.NORTH));
		oscillatingMachine.add(new Block(1,12,0,BlockType.SLIME,Orientation.NORTH));
		oscillatingMachine.add(new Block(1,11,1,BlockType.REDSTONE_BLOCK,Orientation.NORTH));
		oscillatingMachine.add(new Block(1,11,0,BlockType.SLIME,Orientation.NORTH));

		MinecraftCoordinates originalShapeCoordinates = MinecraftUtilClass.minCoordinates(oscillatingMachine);

		List<Block> testBlockSet = MinecraftUtilClass.shiftBlocksBetweenCorners(oscillatingMachine, originalShapeCoordinates, testCorner);

		// When the time is small (50L) then the score becomes large
		MinecraftClient.getMinecraftClient().spawnBlocks(testBlockSet);
		double amount = ff.fitnessScore(testCorner,testBlockSet);
		System.out.println("movement fitness when oscillating: "+ amount);
		MinecraftClient.getMinecraftClient().clearEvaluationSpaceForJUnitTests(testCorner);
		assertTrue(30 <= amount);
		System.out.println("\n");
	}

	// Passes
	@Test
	public void testGetCenterOfMass() {
		Parameters.initializeParameterCollections(new String[] {"netio:false", "minecraftXRange:6","minecraftYRange:6","minecraftZRange:6","spaceBetweenMinecraftShapes:6","minecraftAccumulateChangeInCenterOfMass:true","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet"});
		CommonConstants.netio = false;
		// Small list of blocks
		ArrayList<Block> blockSet1 = new ArrayList<>();
		blockSet1.add(new Block(-5,7,-35,BlockType.REDSTONE_BLOCK, Orientation.WEST));
		blockSet1.add(new Block(-4,7,-35,BlockType.PISTON, Orientation.EAST));
		
		System.out.println("\n TEST: ChangeCenterOfMassFitnessTest: testGetCenterOfMass");

		assertEquals(new Vertex(-4.5,7.0,-35.0), MinecraftUtilClass.getCenterOfMass(blockSet1));
		
		
		// List of flying machine blocks
		ArrayList<Block> blockSet2 = new ArrayList<>();
		// Bottom layer
		blockSet2.add(new Block(1,5,1,BlockType.PISTON,Orientation.NORTH));
		blockSet2.add(new Block(1,5,0,BlockType.SLIME,Orientation.NORTH));
		blockSet2.add(new Block(1,5,-1,BlockType.STICKY_PISTON,Orientation.SOUTH));
		blockSet2.add(new Block(1,5,-2,BlockType.PISTON,Orientation.NORTH));
		blockSet2.add(new Block(1,5,-4,BlockType.SLIME,Orientation.NORTH));
		// Top layer
		blockSet2.add(new Block(1,6,0,BlockType.REDSTONE_BLOCK,Orientation.NORTH));
		blockSet2.add(new Block(1,6,-4,BlockType.REDSTONE_BLOCK,Orientation.NORTH));
		// Activate
		blockSet2.add(new Block(1,6,-1,BlockType.QUARTZ_BLOCK,Orientation.NORTH));
		
		assertEquals(new Vertex(1.0,5.375,-1.375), MinecraftUtilClass.getCenterOfMass(blockSet2));
		System.out.println("\n");
	}

	@Test
	public void testMaxFitness() {
		Parameters.initializeParameterCollections(new String[] {"minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:6","minecraftAccumulateChangeInCenterOfMass:true","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 1000L, "minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet"});
		CommonConstants.netio = false;
		System.out.println("\n TEST: ChangeCenterOfMassFitnessTest: testMaxFitness");
 
		double maxFitness = (10 + 1) * ((6 + 10) / 2.0);
		 assertEquals(maxFitness, ff.maxFitness(), 0.0);
		 
		// changes space between shapes 
		Parameters.initializeParameterCollections(new String[] {"minecraftXRange:10","minecraftYRange:10","minecraftZRange:10","spaceBetweenMinecraftShapes:2","minecraftAccumulateChangeInCenterOfMass:true","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 1000L, "minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet"});
		 maxFitness = (10 + 1) * ((2 + 10) / 2.0);
		 assertEquals(maxFitness, ff.maxFitness(), 0.0);
		
		// changes x/y/z ranges 
		Parameters.initializeParameterCollections(new String[] {"minecraftXRange:5","minecraftYRange:5","minecraftZRange:5","spaceBetweenMinecraftShapes:6","minecraftAccumulateChangeInCenterOfMass:true","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 1000L, "minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet"});
		 maxFitness = (10 + 1) * ((6 + 5) / 2.0);
		 assertEquals(maxFitness, ff.maxFitness(), 0.0);
		
	    Parameters.initializeParameterCollections(new String[] {"minecraftXRange:5","minecraftYRange:15","minecraftZRange:2","spaceBetweenMinecraftShapes:6","minecraftAccumulateChangeInCenterOfMass:true","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 1000L, "minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet"});
		 maxFitness = (10 + 1) * ((6 + 15) / 2.0);
		 assertEquals(maxFitness, ff.maxFitness(), 0.0);
		
		// change x/y/z ranges and space between shape 
		Parameters.initializeParameterCollections(new String[] {"minecraftXRange:17","minecraftYRange:14","minecraftZRange:15","spaceBetweenMinecraftShapes:16","minecraftAccumulateChangeInCenterOfMass:true","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 1000L, "minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet"});
		 maxFitness = (10 + 1) * ((16 + 17) / 2.0);
		 assertEquals(maxFitness, ff.maxFitness(), 0.0);
		
		// change time params 
		Parameters.initializeParameterCollections(new String[] {"minecraftXRange:17","minecraftYRange:14","minecraftZRange:15","spaceBetweenMinecraftShapes:5","minecraftAccumulateChangeInCenterOfMass:true","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 500L, "minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet"});
		 maxFitness = (20 + 1) * ((5 + 17) / 2.0);
		 assertEquals(maxFitness, ff.maxFitness(), 0.0);
		 
		 
		// change x/y/z ranges and space between shape 
		Parameters.initializeParameterCollections(new String[] {"minecraftXRange:17","minecraftYRange:14","minecraftZRange:15","spaceBetweenMinecraftShapes:16","minecraftAccumulateChangeInCenterOfMass:true","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 1000L, "minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet"});
		 maxFitness = (10 + 1) * ((16 + 17) / 2.0);
		 assertEquals(maxFitness, ff.maxFitness(), 0.0);
	
		// minecraftAccumulateChangeInCenterOfMass = false   
		Parameters.initializeParameterCollections(new String[] {"minecraftXRange:17","minecraftYRange:14","minecraftZRange:15","spaceBetweenMinecraftShapes:16","minecraftAccumulateChangeInCenterOfMass:false","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 1000L, "minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet"});
		 maxFitness = ((16 + 17) /2.0);
		 assertEquals(maxFitness, ff.maxFitness(), 0.0);
		
		Parameters.initializeParameterCollections(new String[] {"minecraftXRange:567","minecraftYRange:567","minecraftZRange:568","spaceBetweenMinecraftShapes:2","minecraftAccumulateChangeInCenterOfMass:false","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 1000L, "minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet"});
		 maxFitness = ((2 + 568) /2.0);
		 assertEquals(maxFitness, ff.maxFitness(), 0.0);
		 
			System.out.println("\n");
	}
	
	@Test
	public void testNotFlyingNotGivingMaxFitnessOne() {
		Parameters.initializeParameterCollections(new String[] {"watch:false","minecraftClearWithGlass:false","minecraftXRange:6","minecraftYRange:6","minecraftZRange:6","spaceBetweenMinecraftShapes:6","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet"});
		CommonConstants.netio = false;

		System.out.println("\n TEST: ChangeCenterOfMassFitnessTest: testNotFlyingNotGivingMaxFitnessOne");

		//set up test corner and clear area
		MinecraftCoordinates tempTestCorner = new MinecraftCoordinates(-26,27,-35);
		MinecraftCoordinates testCorner = MinecraftClient.getMinecraftClient().checkForYOutOfBoundsAndShiftUp(tempTestCorner);
		MinecraftClient.getMinecraftClient().clearEvaluationSpaceForJUnitTests(testCorner);
		
		//this string is for a machine that explodes, is large, and leaves a few blocks behind
		String listString = "[AIR at (-665,99,-765) oriented NORTH, REDSTONE_BLOCK at (-663,99,-763) oriented NORTH, SLIME at (-663,99,-764) oriented WEST, REDSTONE_BLOCK at (-665,99,-763) oriented EAST, AIR at (-663,99,-765) oriented NORTH, SLIME at (-665,98,-765) oriented EAST, STICKY_PISTON at (-664,100,-764) oriented NORTH, AIR at (-663,98,-763) oriented NORTH, AIR at (-663,98,-764) oriented NORTH, OBSERVER at (-663,98,-765) oriented DOWN, AIR at (-665,98,-764) oriented NORTH, STICKY_PISTON at (-664,99,-765) oriented WEST, AIR at (-664,99,-763) oriented NORTH, SLIME at (-663,100,-765) oriented WEST, AIR at (-665,100,-764) oriented NORTH, STICKY_PISTON at (-665,100,-765) oriented EAST, AIR at (-664,98,-763) oriented NORTH, PISTON at (-664,98,-764) oriented SOUTH, REDSTONE_BLOCK at (-663,100,-763) oriented EAST, QUARTZ_BLOCK at (-664,98,-765) oriented SOUTH, PISTON at (-663,100,-764) oriented EAST]";
		List<Block> testBlockSet = MinecraftUtilClass.readMinecraftBlockListFromString(listString);

		//System.out.println("blocklist: " + testBlockSet);

		//shift coordinates based on the testCorner
		MinecraftCoordinates originalShapeCoordinates = MinecraftUtilClass.minCoordinates(testBlockSet);
		testBlockSet = MinecraftUtilClass.shiftBlocksBetweenCorners(testBlockSet, originalShapeCoordinates, testCorner);
		
		assertEquals(0.0, ff.fitnessScore(testCorner,testBlockSet),1.0); 
		System.out.println("\n");
	}
	@Test
	public void testNotFlyingNotGivingMaxFitnessTwo() {
		Parameters.initializeParameterCollections(new String[] {"watch:false","minecraftClearWithGlass:false","minecraftXRange:6","minecraftYRange:6","minecraftZRange:6","spaceBetweenMinecraftShapes:6","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 150L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet"});
		CommonConstants.netio = false;

		System.out.println( "\n TEST: ChangeCenterOfMassFitnessTest: testNotFlyingNotGivingMaxFitnessTwo");

		//set up test corner and clear area
		MinecraftCoordinates tempTestCorner = new MinecraftCoordinates(-26,27,-35);
		MinecraftCoordinates testCorner = MinecraftClient.getMinecraftClient().checkForYOutOfBoundsAndShiftUp(tempTestCorner);
		MinecraftClient.getMinecraftClient().clearEvaluationSpaceForJUnitTests(testCorner);

		//this string is for a machine that explodes, is large, and leaves a few blocks behind
		String listString = "[REDSTONE_BLOCK at (-665,99,-764) oriented NORTH, AIR at (-665,99,-765) oriented NORTH, AIR at (-663,99,-763) oriented NORTH, AIR at (-663,99,-764) oriented NORTH, AIR at (-663,99,-765) oriented NORTH, QUARTZ_BLOCK at (-664,100,-764) oriented SOUTH, QUARTZ_BLOCK at (-664,100,-765) oriented EAST, AIR at (-663,98,-763) oriented NORTH, QUARTZ_BLOCK at (-663,98,-764) oriented NORTH, AIR at (-665,98,-763) oriented NORTH, SLIME at (-664,100,-763) oriented SOUTH, QUARTZ_BLOCK at (-665,98,-764) oriented WEST, AIR at (-664,99,-763) oriented NORTH, QUARTZ_BLOCK at (-665,100,-763) oriented DOWN, STICKY_PISTON at (-663,100,-765) oriented UP, AIR at (-665,100,-764) oriented NORTH, OBSERVER at (-665,100,-765) oriented SOUTH, OBSERVER at (-664,98,-763) oriented SOUTH, STICKY_PISTON at (-664,98,-764) oriented NORTH]";
		List<Block> testBlockSet = MinecraftUtilClass.readMinecraftBlockListFromString(listString);

		//System.out.println("blocklist: " + testBlockSet);

		//shift coordinates based on the testCorner
		MinecraftCoordinates originalShapeCoordinates = MinecraftUtilClass.minCoordinates(testBlockSet);
		testBlockSet = MinecraftUtilClass.shiftBlocksBetweenCorners(testBlockSet, originalShapeCoordinates, testCorner);
		
		assertEquals(0.0, ff.fitnessScore(testCorner,testBlockSet),1.0); 
		System.out.println("\n");
	}
	
	// Notes from Dr. Schrum:
	// The shape here was taken from an evolutionary run.
	// It was mistaken for a flying machine by an earlier version of
	// the fitness function, but it is not. The shape blows up and leaves
	// a block behind which is displaced from the center of the original
	// shape, and therefore gives the impression that the shape has moved
	// a lot (since the center of mass changes to focus on the one block)
	@Test
	public void testExplodingMachineShouldNotGetMaxFitness() {
		Parameters.initializeParameterCollections(new String[] {"watch:false","minecraftAccumulateChangeInCenterOfMass:true","minecraftClearWithGlass:false","minecraftXRange:5","minecraftYRange:5","minecraftZRange:5","spaceBetweenMinecraftShapes:10","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 1000L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet"});
		CommonConstants.netio = false;

		System.out.println( "\n TEST: ChangeCenterOfMassFitnessTest: testExplodingMachineShouldNotGetMaxFitness");

		//set up test corner and clear area
		MinecraftCoordinates tempTestCorner = new MinecraftCoordinates(-26,27,-35);
		MinecraftCoordinates testCorner = MinecraftClient.getMinecraftClient().checkForYOutOfBoundsAndShiftUp(tempTestCorner);
		MinecraftClient.getMinecraftClient().clearEvaluationSpaceForJUnitTests(testCorner);

		//this string is for a machine that explodes, is large, and leaves a few blocks behind
		String listString = "[TNT at (152,35,-198) oriented NORTH, STICKY_PISTON at (152,35,-197) oriented SOUTH, STICKY_PISTON at (152,35,-196) oriented NORTH, TNT at (152,36,-198) oriented EAST, SLIME at (152,36,-197) oriented WEST, REDSTONE_BLOCK at (152,36,-196) oriented EAST, QUARTZ_BLOCK at (152,37,-198) oriented EAST, QUARTZ_BLOCK at (152,37,-197) oriented EAST, STICKY_PISTON at (152,37,-196) oriented WEST, REDSTONE_BLOCK at (153,35,-197) oriented EAST, PISTON at (153,35,-196) oriented EAST, SLIME at (153,36,-197) oriented SOUTH, STICKY_PISTON at (153,36,-196) oriented NORTH, REDSTONE_BLOCK at (153,37,-197) oriented DOWN, TNT at (153,37,-196) oriented WEST, PISTON at (154,35,-197) oriented SOUTH, STICKY_PISTON at (154,35,-196) oriented NORTH, QUARTZ_BLOCK at (154,36,-198) oriented EAST, PISTON at (154,36,-196) oriented UP, PISTON at (154,37,-198) oriented DOWN, REDSTONE_BLOCK at (154,37,-197) oriented EAST, REDSTONE_BLOCK at (154,37,-196) oriented DOWN]";
		List<Block> testBlockSet = MinecraftUtilClass.readMinecraftBlockListFromString(listString);

		//System.out.println("blocklist: " + testBlockSet);

		//shift coordinates based on the testCorner
		MinecraftCoordinates originalShapeCoordinates = MinecraftUtilClass.minCoordinates(testBlockSet);
		testBlockSet = MinecraftUtilClass.shiftBlocksBetweenCorners(testBlockSet, originalShapeCoordinates, testCorner);
		
		// 4.4 is for a bit of oscillation, but is not the max fitness
		assertEquals(4.4, ff.fitnessScore(testCorner,testBlockSet),1.0); 
		System.out.println("\n");
	}
	
	// Notes from Dr. Schrum
	// This is a flying machine, but it leaves some blocks behind that
	// are destroyed by TNT, so that in the end there are no blocks left
	// except for the flying machine that leaves the evaluation are.
	@Test
	public void testFlyingMachineBlowsUpLeftovers() {
		Parameters.initializeParameterCollections(new String[] {"watch:false","minecraftAccumulateChangeInCenterOfMass:true","minecraftClearWithGlass:false","minecraftXRange:3","minecraftYRange:3","minecraftZRange:3","spaceBetweenMinecraftShapes:10","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 1000L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet"});
		CommonConstants.netio = false;

		System.out.println( "\n TEST: ChangeCenterOfMassFitnessTest: testFlyingMachineBlowsUpLeftovers");

		//set up test corner and clear area
		MinecraftCoordinates tempTestCorner = new MinecraftCoordinates(-26,27,-35);
		MinecraftCoordinates testCorner = MinecraftClient.getMinecraftClient().checkForYOutOfBoundsAndShiftUp(tempTestCorner);
		MinecraftClient.getMinecraftClient().clearEvaluationSpaceForJUnitTests(testCorner);

		//this string is for a machine that explodes, is large, and leaves a few blocks behind
		String listString = "[STICKY_PISTON at (0,0,2) oriented DOWN, PISTON at (0,1,0) oriented EAST, SLIME at (0,1,2) oriented EAST, PISTON at (0,2,1) oriented SOUTH, REDSTONE_BLOCK at (0,2,2) oriented SOUTH, REDSTONE_BLOCK at (1,1,0) oriented WEST, SLIME at (1,1,1) oriented SOUTH, OBSERVER at (1,1,2) oriented SOUTH, PISTON at (1,2,0) oriented SOUTH, SLIME at (1,2,1) oriented NORTH, OBSERVER at (1,2,2) oriented WEST, TNT at (2,0,2) oriented NORTH, OBSERVER at (2,1,0) oriented NORTH, TNT at (2,1,2) oriented WEST, SLIME at (2,2,1) oriented SOUTH, STICKY_PISTON at (2,2,2) oriented NORTH]";
		List<Block> testBlockSet = MinecraftUtilClass.readMinecraftBlockListFromString(listString);

		//System.out.println("blocklist: " + testBlockSet);

		//shift coordinates based on the testCorner
		MinecraftCoordinates originalShapeCoordinates = MinecraftUtilClass.minCoordinates(testBlockSet);
		testBlockSet = MinecraftUtilClass.shiftBlocksBetweenCorners(testBlockSet, originalShapeCoordinates, testCorner);
		
		// Earns max fitness since remaining blocks blow up
		assertEquals(ff.maxFitness(), ff.fitnessScore(testCorner,testBlockSet),1.0); 
		System.out.println("\n");
	}
	
	//testing flying machine fitness
	@Test
	public void flyingMarchineFitnessChecking() {
		Parameters.initializeParameterCollections(new String[] {"watch:false","minecraftAccumulateChangeInCenterOfMass:true","minecraftClearWithGlass:false","minecraftXRange:3","minecraftYRange:3","minecraftZRange:3","spaceBetweenMinecraftShapes:10","minecraftEndEvalNoMovement:true","shortTimeBetweenMinecraftReads:" + 1000L,"minecraftMandatoryWaitTime:" + 10000L,"minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet"});
		CommonConstants.netio = false;

		System.out.println( "\n TEST: ChangeCenterOfMassFitnessTest: flyingMarchineFitnessChecking");

		//set up test corner and clear area
		MinecraftCoordinates tempTestCorner = new MinecraftCoordinates(-26,27,-35);
		MinecraftCoordinates testCorner = MinecraftClient.getMinecraftClient().checkForYOutOfBoundsAndShiftUp(tempTestCorner);
		MinecraftClient.getMinecraftClient().clearEvaluationSpaceForJUnitTests(testCorner);

		//this string is for a machine that explodes, is large, and leaves a few blocks behind
		String listString = "[SLIME at (3,2,4) oriented SOUTH, REDSTONE_BLOCK at (2,1,3) oriented WEST, REDSTONE_BLOCK at (2,1,2) oriented EAST, REDSTONE_BLOCK at (2,1,1) oriented NORTH, PISTON at (2,1,0) oriented EAST, REDSTONE_BLOCK at (4,3,4) oriented WEST, SLIME at (3,2,2) oriented WEST, REDSTONE_BLOCK at (3,2,1) oriented WEST, QUARTZ_BLOCK at (3,2,0) oriented SOUTH, STICKY_PISTON at (4,3,3) oriented UP, PISTON at (4,3,2) oriented UP, OBSERVER at (4,3,1) oriented NORTH, STICKY_PISTON at (4,3,0) oriented UP, STICKY_PISTON at (0,2,2) oriented UP, PISTON at (0,2,1) oriented UP, SLIME at (0,2,0) oriented UP, STICKY_PISTON at (0,2,4) oriented DOWN, PISTON at (0,2,3) oriented NORTH, OBSERVER at (4,0,4) oriented DOWN, SLIME at (4,0,3) oriented SOUTH, STICKY_PISTON at (4,0,2) oriented SOUTH, QUARTZ_BLOCK at (4,0,1) oriented WEST, PISTON at (4,0,0) oriented UP, OBSERVER at (1,0,4) oriented EAST, QUARTZ_BLOCK at (2,1,4) oriented WEST, REDSTONE_BLOCK at (1,0,3) oriented DOWN, QUARTZ_BLOCK at (1,0,2) oriented SOUTH, PISTON at (1,0,1) oriented EAST, REDSTONE_BLOCK at (1,0,0) oriented WEST, PISTON at (3,3,0) oriented SOUTH, SLIME at (3,3,4) oriented WEST, SLIME at (3,3,3) oriented SOUTH, STICKY_PISTON at (3,3,2) oriented NORTH, SLIME at (3,3,1) oriented DOWN, STICKY_PISTON at (4,4,0) oriented UP, SLIME at (4,4,4) oriented WEST, SLIME at (4,4,3) oriented NORTH, REDSTONE_BLOCK at (4,4,2) oriented DOWN, OBSERVER at (4,4,1) oriented NORTH, QUARTZ_BLOCK at (0,3,3) oriented DOWN, SLIME at (0,3,2) oriented WEST, SLIME at (0,3,1) oriented UP, QUARTZ_BLOCK at (0,3,0) oriented NORTH, STICKY_PISTON at (0,3,4) oriented NORTH, QUARTZ_BLOCK at (1,4,3) oriented WEST, SLIME at (1,4,2) oriented SOUTH, QUARTZ_BLOCK at (1,4,1) oriented NORTH, STICKY_PISTON at (1,4,0) oriented UP, STICKY_PISTON at (1,4,4) oriented WEST, STICKY_PISTON at (4,1,1) oriented SOUTH, SLIME at (4,1,0) oriented DOWN, REDSTONE_BLOCK at (0,0,0) oriented UP, STICKY_PISTON at (0,0,4) oriented NORTH, QUARTZ_BLOCK at (0,0,3) oriented WEST, REDSTONE_BLOCK at (0,0,2) oriented EAST, REDSTONE_BLOCK at (0,0,1) oriented SOUTH, SLIME at (1,1,0) oriented WEST, SLIME at (1,1,4) oriented NORTH, STICKY_PISTON at (1,1,3) oriented WEST, REDSTONE_BLOCK at (1,1,2) oriented EAST, PISTON at (1,1,1) oriented UP, SLIME at (2,2,0) oriented EAST, SLIME at (2,2,4) oriented DOWN, OBSERVER at (2,2,3) oriented UP, PISTON at (2,2,2) oriented EAST, REDSTONE_BLOCK at (0,4,4) oriented DOWN, SLIME at (0,4,3) oriented DOWN, STICKY_PISTON at (0,4,2) oriented DOWN, REDSTONE_BLOCK at (0,4,1) oriented NORTH, PISTON at (3,0,4) oriented EAST, STICKY_PISTON at (3,0,3) oriented EAST, SLIME at (3,0,2) oriented WEST, SLIME at (0,4,0) oriented WEST, REDSTONE_BLOCK at (4,1,4) oriented EAST, STICKY_PISTON at (4,1,3) oriented EAST, QUARTZ_BLOCK at (4,1,2) oriented DOWN, PISTON at (3,0,1) oriented SOUTH, QUARTZ_BLOCK at (3,0,0) oriented UP, QUARTZ_BLOCK at (0,1,1) oriented NORTH, STICKY_PISTON at (0,1,0) oriented UP, SLIME at (0,1,4) oriented SOUTH, QUARTZ_BLOCK at (0,1,2) oriented WEST, SLIME at (1,2,1) oriented UP, REDSTONE_BLOCK at (1,2,0) oriented WEST, QUARTZ_BLOCK at (1,2,2) oriented EAST, QUARTZ_BLOCK at (2,3,1) oriented WEST, PISTON at (2,3,0) oriented SOUTH, OBSERVER at (2,3,4) oriented SOUTH, OBSERVER at (2,3,3) oriented NORTH, SLIME at (2,3,2) oriented NORTH, STICKY_PISTON at (3,4,1) oriented NORTH, OBSERVER at (3,4,4) oriented EAST, SLIME at (2,0,4) oriented UP, OBSERVER at (2,0,3) oriented EAST, STICKY_PISTON at (3,1,4) oriented UP, OBSERVER at (2,0,2) oriented UP, SLIME at (2,0,1) oriented UP, PISTON at (2,0,0) oriented WEST, STICKY_PISTON at (4,2,4) oriented SOUTH, SLIME at (4,2,3) oriented EAST, REDSTONE_BLOCK at (3,1,2) oriented EAST, QUARTZ_BLOCK at (3,1,1) oriented EAST, OBSERVER at (3,1,0) oriented SOUTH, SLIME at (4,2,2) oriented NORTH, OBSERVER at (4,2,1) oriented EAST, SLIME at (4,2,0) oriented DOWN, OBSERVER at (1,3,2) oriented EAST, STICKY_PISTON at (1,3,1) oriented EAST, PISTON at (1,3,0) oriented SOUTH, OBSERVER at (1,3,4) oriented NORTH, PISTON at (1,3,3) oriented UP, PISTON at (2,4,2) oriented WEST, OBSERVER at (2,4,1) oriented DOWN, SLIME at (2,4,0) oriented WEST, OBSERVER at (2,4,4) oriented EAST, REDSTONE_BLOCK at (2,4,3) oriented SOUTH]";
		List<Block> testBlockSet = MinecraftUtilClass.readMinecraftBlockListFromString(listString);

		//System.out.println("blocklist: " + testBlockSet);

		//shift coordinates based on the testCorner
		MinecraftCoordinates originalShapeCoordinates = MinecraftUtilClass.minCoordinates(testBlockSet);
		testBlockSet = MinecraftUtilClass.shiftBlocksBetweenCorners(testBlockSet, originalShapeCoordinates, testCorner);
		
		// Earns max fitness since flying
		assertEquals(60.1, ff.fitnessScore(testCorner,testBlockSet),0.0); 
		System.out.println("\n");
	}
	
	
	// Another flying machine that at one point was being classified as flying when really it was not
	@Test
	public void disallowCheatingFlyingMachine() {
		// The shape in this test was originalyl evolved with spaceBetweenMinecraftShapes:10, but changing it to spaceBetweenMinecraftShapes:20 prevents it from cheating
		Parameters.initializeParameterCollections(new String[] {"watch:false","minecraftAccumulateChangeInCenterOfMass:true","minecraftClearWithGlass:false","minecraftXRange:3","minecraftYRange:3","minecraftZRange:3","spaceBetweenMinecraftShapes:20","minecraftEndEvalNoMovement:true","minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet"});
		CommonConstants.netio = false;

		System.out.println( "\n TEST: ChangeCenterOfMassFitnessTest: disallowCheatingFlyingMachine");

		//set up test corner and clear area
		MinecraftCoordinates tempTestCorner = new MinecraftCoordinates(-26,27,-35);
		MinecraftCoordinates testCorner = MinecraftClient.getMinecraftClient().checkForYOutOfBoundsAndShiftUp(tempTestCorner);
		MinecraftClient.getMinecraftClient().clearEvaluationSpaceForJUnitTests(testCorner);

		//this string is for a machine that explodes, is large, and leaves a few blocks behind
		String listString = "[PISTON at (-98,35,-196) oriented EAST, PISTON at (-98,36,-198) oriented WEST, SLIME at (-98,36,-197) oriented DOWN, SLIME at (-98,36,-196) oriented DOWN, REDSTONE_BLOCK at (-98,37,-197) oriented UP, STICKY_PISTON at (-98,37,-196) oriented DOWN, STICKY_PISTON at (-97,35,-198) oriented EAST, STICKY_PISTON at (-97,35,-197) oriented NORTH, SLIME at (-97,36,-198) oriented EAST, STICKY_PISTON at (-97,36,-197) oriented NORTH, OBSERVER at (-97,36,-196) oriented UP, REDSTONE_BLOCK at (-97,37,-198) oriented DOWN, QUARTZ_BLOCK at (-97,37,-197) oriented WEST, SLIME at (-96,35,-198) oriented NORTH, REDSTONE_BLOCK at (-96,35,-197) oriented EAST, PISTON at (-96,35,-196) oriented WEST, SLIME at (-96,36,-198) oriented NORTH, STICKY_PISTON at (-96,36,-197) oriented NORTH, STICKY_PISTON at (-96,37,-197) oriented WEST]";
		List<Block> testBlockSet = MinecraftUtilClass.readMinecraftBlockListFromString(listString);

		//System.out.println("blocklist: " + testBlockSet);

		//shift coordinates based on the testCorner
		MinecraftCoordinates originalShapeCoordinates = MinecraftUtilClass.minCoordinates(testBlockSet);
		testBlockSet = MinecraftUtilClass.shiftBlocksBetweenCorners(testBlockSet, originalShapeCoordinates, testCorner);

		// Does not actually fly
		assertEquals(3, ff.fitnessScore(testCorner,testBlockSet),2.0); 
		System.out.println("\n");
	}
}