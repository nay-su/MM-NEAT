package edu.southwestern.tasks.evocraft.fitness;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;
import edu.southwestern.util.datastructures.Pair;

/**
 * This evaluates the number of blocks created after the shape is spawned
 * currently overrides calculateFinalScore and maxFitness
 * 
 * @author lewisj
 *
 */
public abstract class SecondaryBlockCreationFitness extends TimedEvaluationMinecraftFitnessFunction{

	//the type of blocks you are looking for
	private BlockType[] acceptedBlockTypes; //= {BlockType.COBBLESTONE, BlockType.OBSIDIAN};
	
	//constructor to create the accepted block type list
	public SecondaryBlockCreationFitness(BlockType[] acceptedBlockTypes) {
		this.acceptedBlockTypes = acceptedBlockTypes;
	}

	public BlockType[] getAcceptedBlockTypes() {
		return acceptedBlockTypes;
	}

	public void setAcceptedBlockTypes(BlockType[] acceptedBlockTypes) {
		this.acceptedBlockTypes = acceptedBlockTypes;
	}
	
	@Override
	public double calculateFinalScore (ArrayList<Pair<Long,List<Block>>> history, MinecraftCoordinates corner, List<Block> originalBlocks) {
		
		//initialized to the last recoded shape in history
		List<Block> finalBlocksList = history.get(history.size()-1).t2;
		//check that block list against accepted block types list to get a list of only the desired blocks
		finalBlocksList = MinecraftUtilClass.getDesiredBlocks(finalBlocksList, acceptedBlockTypes);
		
		//create watch print statement for finalBlocksList
		if(CommonConstants.watch) System.out.println("Filtered Blocks List: "+finalBlocksList);

		
		//the number of blocks of the desired type(s) equals the length of the list of blocks
		return finalBlocksList.size();
		
	}
	

	//maximum number of blocks that could be of the desired type (if the whole evaluation area was filled)
	@Override
	public double maxFitness() {
		return MinecraftUtilClass.reservedSpace().x()*MinecraftUtilClass.reservedSpace().y();
	}
	
}