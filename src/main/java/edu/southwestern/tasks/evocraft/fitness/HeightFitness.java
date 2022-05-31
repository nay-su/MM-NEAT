package edu.southwestern.tasks.evocraft.fitness;

import java.util.function.ToIntFunction;

import edu.southwestern.tasks.evocraft.MinecraftClient.Block;

public class HeightFitness extends RangeFitness {

	@Override
	public ToIntFunction<Block> dimension() {
		return Block::y;
	}

}
