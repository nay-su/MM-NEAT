package edu.southwestern.tasks.gvgai.zelda.study;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon.Node;
import edu.southwestern.tasks.gvgai.zelda.dungeon.LoadOriginalDungeon;
import edu.southwestern.util.file.NullPrintStream;
import edu.southwestern.util.stats.StatisticsUtilities;
import me.jakerg.rougelike.Tile;

public class DungeonNovelty {

	// These variables provide the area to determine the novelty value
	// Should look through the playable area of a given room
	static final int ROWS = 7; // Number of rows to look through
	static final int COLUMNS = 12; // Number of columns to look through
	static final Point START = new Point(2, 2); // Starting point
	
	/**
	 * Find the novelty of a room (given by focus) with respect to all the other rooms of the list.
	 * This is essentially the average distance of the room from all other rooms.
	 * 
	 * @param rooms List of rooms to compare with. Could be List of Lists or Dungeon.Nodes
	 * @param focus Index in rooms representing the room to compare the other rooms with
	 * @return Real number between 0 and 1, 0 being non-novel and 1 being completely novel
	 */
	public static double roomNovelty(List<List<List<Integer>>> rooms, int focus) {
		double novelty = 0;
		
		for(int i = 0; i < rooms.size(); i++) { // For each other room
			if(i != focus) { // don't compare with self
				novelty += roomDistance(rooms.get(focus), rooms.get(i));
			}
		}
		
		return novelty / rooms.size(); // Novelty is average distance from other rooms
	}
	
	
	/**
	 * Version of the same method that takes an Integer List representation of the levels
	 * @param list List of Lists representing a level
	 * @param list2 List of Lists representing a level
	 * @return Real number between 0 and 1, 0 being identical and 1 being completely different
	 */
	public static double roomDistance(List<List<Integer>> list, List<List<Integer>> list2) {
//		for(int x = START.x; x < START.x+ROWS; x++) {
//			System.out.println(room1.get(x) + "\t" + room2.get(x));
//		}
		
		double distance = 0;
		for(int x = START.x; x < START.x+ROWS; x++) {
			for(int y = START.y; y < START.y+COLUMNS; y++) {
				
				int compare1 = list.get(x).get(y); 
				if(compare1 == Tile.TRIFORCE.getNum()|| // Triforce is item, not tile
						compare1 == Tile.KEY.getNum()||
						compare1 == 2)  // I think this represents an enemy
					compare1 = Tile.FLOOR.getNum(); 
				// These all look like a block 
				if(compare1 == Tile.WALL.getNum()||
						compare1 == Tile.MOVABLE_BLOCK_UP.getNum()||
						compare1 == Tile.MOVABLE_BLOCK_DOWN.getNum()||
						compare1 == Tile.MOVABLE_BLOCK_LEFT.getNum()||
						compare1 == Tile.MOVABLE_BLOCK_RIGHT.getNum()) 
					compare1 = Tile.BLOCK.getNum();
				
				int compare2 = list2.get(x).get(y); 
				if(compare2 == Tile.TRIFORCE.getNum()|| // Triforce is item, not tile
						compare2 == Tile.KEY.getNum()||
						compare2 == 2)  // I think this represents an enemy 
					compare2 = Tile.FLOOR.getNum();  
				// These all look like a block 
				if(compare2 == Tile.WALL.getNum()||
						compare2 == Tile.MOVABLE_BLOCK_UP.getNum()||
						compare2 == Tile.MOVABLE_BLOCK_DOWN.getNum()||
						compare2 == Tile.MOVABLE_BLOCK_LEFT.getNum()||
						compare2 == Tile.MOVABLE_BLOCK_RIGHT.getNum()) 
					compare2 = Tile.BLOCK.getNum();
				
				if(compare1 != compare2) // If the blocks at the same position are not the same, increment novelty
					distance++;
			}
			System.out.println();
		}
		
//		System.out.println("dist = "+distance);
//		MiscUtil.waitForReadStringAndEnterKeyPress();
		
		return distance / (ROWS * COLUMNS);
	}
	
	/**
	 * Get the novelty of a dungeon by computing average novelty of all rooms in the dungeon
	 * with respect to each other.
	 * 
	 * @param dungeon Dungeon to check the novelty of
	 * @return Real number between 0 and 1, 0 being non-novel and 1 being completely novel
	 */
	public static double averageDungeonNovelty(Dungeon dungeon) {
		List<List<List<Integer>>> rooms = new LinkedList<>();
		for(Node x : dungeon.getLevels().values()) {
			rooms.add(x.level.getLevel());
		}
		return averageRoomNovelty(rooms);
	}
	
	/**
	 * Average novelty across a list of rooms.
	 * 
	 * @param rooms List of rooms
	 * @return Real number between 0 and 1, 0 being non-novel and 1 being completely novel
	 */
	public static double averageRoomNovelty(List<List<List<Integer>>> rooms) {
		return StatisticsUtilities.average(roomNovelties(rooms));
	}

	/**
	 * Get array of novelties of all rooms with respect to each other.
	 * 
	 * @param rooms List of rooms
	 * @return double array where each index is the novelty of the same index in the rooms list.
	 */
	public static double[] roomNovelties(List<List<List<Integer>>> rooms) {
		// It is assumed that dungeons loaded as lists if lists of integers will 
		// be in consistent order, so extra sorting is not needed.
//		if(rooms.get(0) instanceof Node) {
//			// Because the room list is derived from a HashMap, the order of the rooms
//			// can be different each time the rooms are loaded. Put into a consistent order.
//			Collections.sort(rooms, new Comparator<Node>() {
//				@Override
//				public int compare(Node o1, Node o2) {
//					String level1 = "";
//					for(Tile[] row: o1.level.getTiles()) {
//						for(Tile t: row) {
//							level1 += t.name();
//						}
//					}
//					String level2 = "";
//					for(Tile[] row: o2.level.getTiles()) {
//						for(Tile t: row) {
//							level2 += t.name();
//						}
//					}
//					return level1.compareTo(level2);
//				}
//			});
//		}
		
//		System.out.println("FIRST ROOM: " + rooms.get(0));
//		MiscUtil.waitForReadStringAndEnterKeyPress();
		
		double[] novelties = new double[rooms.size()];
		for(int i = 0; i < rooms.size(); i++) { // For each room in the list
			novelties[i] = roomNovelty(rooms, i); // Calculate novelty of room 
			//System.out.println(i+":" + novelties[i]);
		}
		return novelties;
	}

	
	/**
	 * Perform an analysis of the novelty of of various dungeons from the original game and
	 * from the human subject study conducted in 2019. Note that this command assumes the 
	 * availability of saved dungeon data from the study, stored in the location specified
	 * by the basePath variable.
	 * 
	 * @param args Empty array ... just use default parameters
	 * @throws FileNotFoundException 
	 * @throws Exception
	 */
	public static void main(String[] args) throws FileNotFoundException {
		final String basePath = "G:\\My Drive\\Research\\SCOPE Artifacts\\Zelda Human Subject Data\\Experiments-2019-ZeldaGAN\\Subject-";
		
		// To suppress output from file loading
		PrintStream original = System.out;
		System.setOut(new NullPrintStream());
		
		Parameters.initializeParameterCollections(args);
		String[] names = new String[] {"tloz1_1_flip", "tloz2_1_flip", "tloz3_1_flip", "tloz4_1_flip", "tloz5_1_flip", "tloz6_1_flip", "tloz7_1_flip", "tloz8_1_flip", "tloz9_1_flip"};
		HashMap<String,Double> originalNovelties = new HashMap<String,Double>();
		for(String name: names) {
			Dungeon dungeon = LoadOriginalDungeon.loadOriginalDungeon(name);
			originalNovelties.put(name, averageDungeonNovelty(dungeon));			
		}
		
		// Resume outputting text
		System.setOut(original);
		
		System.out.println("Novelty of Original Dungeons");
		PrintStream originalStream = new PrintStream(new File("Zelda-Original.csv"));
		double originalDungeonAverage = 0;
		for(String name: names) {
			double novelty = originalNovelties.get(name);
			System.out.println(novelty);
			originalStream.println(novelty);
			originalDungeonAverage += novelty;
		}
		originalStream.close();
		// Average novelty of dungeons from original game
		originalDungeonAverage /= names.length; 

		
		// Mute output again
		System.setOut(new NullPrintStream());

		HashMap<String,Double> graphNovelties = new HashMap<String,Double>();
		for(int i = 0; i < 30; i++) {
			String path = basePath + i + "\\";
			Dungeon originalDungeon = Dungeon.loadFromJson(path + "OriginalLoader_dungeon.json");
			graphNovelties.put("graphSubject"+i, averageDungeonNovelty(originalDungeon));
		}
		
		// Resume outputting text
		System.setOut(original);

		System.out.println("Novelty of Graph Grammar Dungeons");
		PrintStream graphGrammarStream = new PrintStream(new File("Zelda-GraphGrammar.csv"));
		double graphGrammarAverage = 0;
		for(int i = 0; i < 30; i++) {
			double novelty = graphNovelties.get("graphSubject"+i);
			System.out.println(novelty);
			graphGrammarStream.println(novelty);
			graphGrammarAverage += novelty;
		}
		graphGrammarStream.close();
		// Average novelty of Graph Grammar dungeons from study
		graphGrammarAverage /= 30;
		
		// Mute output again
		System.setOut(new NullPrintStream());

		HashMap<String,Double> graphGANNovelties = new HashMap<String,Double>();
		for(int i = 0; i < 30; i++) {
			String path = basePath + i + "\\";
			Dungeon ganDungeon = Dungeon.loadFromJson(path + "GANLoader_dungeon.json");
			//Dungeon originalDungeon = Dungeon.loadFromJson(path + "OriginalLoader_dungeon.json");
			graphGANNovelties.put("graphGANSubject"+i, averageDungeonNovelty(ganDungeon));
		}
		
		// Resume outputting text
		System.setOut(original);

		System.out.println("Novelty of Graph GAN Dungeons");
		PrintStream graphGANStream = new PrintStream(new File("Zelda-GraphGAN.csv"));
		double graphGANAverage = 0;
		for(int i = 0; i < 30; i++) {
			double novelty = graphGANNovelties.get("graphGANSubject"+i);
			System.out.println(novelty);
			graphGANStream.println(novelty);
			graphGANAverage += novelty;
		}
		graphGANStream.close();
		// Average novelty of Graph GAN dungeons from study
		graphGANAverage /= 30;
	
		System.out.println();
		System.out.println("Original Average: "+originalDungeonAverage);
		System.out.println("Grammar  Average: "+graphGrammarAverage);
		System.out.println("GraphGAN Average: "+graphGANAverage);
	}
	
}
