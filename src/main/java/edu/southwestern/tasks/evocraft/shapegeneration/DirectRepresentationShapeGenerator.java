package edu.southwestern.tasks.evocraft.shapegeneration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;
import edu.southwestern.tasks.evocraft.blocks.BlockSet;
import edu.southwestern.util.datastructures.Pair;

/**
 * Shape generator for MinecraftShapeGenotype. Takes a hash map and turns it into an ArrayList.
 * @author raffertyt
 *
 */
public class DirectRepresentationShapeGenerator implements ShapeGenerator<Pair<HashMap<MinecraftCoordinates, Block>, HashSet<MinecraftCoordinates>>> {

	@Override
	public String[] getNetworkOutputLabels() {
		throw new UnsupportedOperationException("This should not be called for DirectRepresentationShapeGenerator");
	}

	@Override
	public List<Block> generateShape(
			Genotype<Pair<HashMap<MinecraftCoordinates, Block>, HashSet<MinecraftCoordinates>>> genome,
			MinecraftCoordinates corner, BlockSet blockSet) {
		Collection<Block> collectionOfBlocks =  genome.getPhenotype().t1.values();
		ArrayList<Block> blocks = new ArrayList<>(collectionOfBlocks);
		return MinecraftUtilClass.shiftBlocksBetweenCorners(blocks, new MinecraftCoordinates(0), corner);
	}
}
