package edu.southwestern.tasks.zelda;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.parameters.Parameters;

/**
 * Bins are based on the percentage of floor space occupied by walls, the percentage of floor space occupied by water, and the number of rooms.
 * 
 * @author schrum2
 *
 */
public class ZeldaMAPElitesWallWaterRoomsBinLabels implements BinLabels {

	public static final int TILE_GROUPS = 10;
	
	List<String> labels = null;

	private int maxNumRooms;
		
	@Override
	public List<String> binLabels() {
		if(labels == null) { // Create once and re-use, but wait until after Parameters are loaded
			maxNumRooms = Parameters.parameters.integerParameter("zeldaGANLevelWidthChunks") * Parameters.parameters.integerParameter("zeldaGANLevelHeightChunks");
			labels = new ArrayList<String>(TILE_GROUPS*TILE_GROUPS*(maxNumRooms+1));
			for(int i = 0; i < TILE_GROUPS; i++) { // Wall tile percent
				for(int j = 0; j < TILE_GROUPS; j++) { // Water tile percent
					for(int r = 0; r <= maxNumRooms; r++) {
						labels.add("Wall"+i+"0-"+(i+1)+"0Water"+j+"0-"+(j+1)+"0Rooms"+r);
					}
				}
			}
		}
		return labels;
	}

	@Override
	public int oneDimensionalIndex(int[] multi) {
		int wallTileIndex = multi[0];
		int waterTileIndex = multi[1];
		int numRoomsReachable = multi[2];
		
		int mapElitesBinIndex = (wallTileIndex*TILE_GROUPS + waterTileIndex)*(maxNumRooms+1) + numRoomsReachable;

		return mapElitesBinIndex;
	}

	@Override
	public String[] dimensions() {
		return new String[] {"Wall Tile Percent", "Water Tile Percent", "Reachable Rooms"};
	}

	@Override
	public int[] dimensionSizes() {
		return new int[] {TILE_GROUPS, TILE_GROUPS, maxNumRooms+1};
	}
}
