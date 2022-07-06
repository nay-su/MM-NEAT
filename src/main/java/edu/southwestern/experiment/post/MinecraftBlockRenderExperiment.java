package edu.southwestern.experiment.post;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.experiment.Experiment;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;
import edu.southwestern.util.file.FileUtilities;

/**
 * Load a single elite from MAP Elites output text file to spawn it
 * into the Minecraft world for observation.
 * 
 * @author Alejandro Medina
 *
 */
public class MinecraftBlockRenderExperiment implements Experiment {
	
	private static String dir; 
	
	@Override
	public void init() {
		dir = FileUtilities.getSaveDirectory() + "/archive/" + Parameters.parameters.stringParameter("minecraftBlockListTextFile");
		System.out.println("Load: "+ dir);
	}

	@Override
	public void run() {
		List<Block> blocks;
		try {
			blocks = MinecraftUtilClass.loadMAPElitesOutputFile(new File(dir)); // get block list from output file
			System.out.println("Spawning " + blocks.size() + " blocks from " + dir);
			for(Block b: blocks) {
				System.out.println(b);
			}
				
			MinecraftClient.getMinecraftClient().spawnBlocks(blocks); // spawn blocks in minecraft world
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean shouldStop() {
		return false;
	}

	
	public static void main(String[] args) {
		try {
			MMNEAT.main(new String[] { "io:false", "netio:false", "launchMinecraftServerFromJava:false", "base:minecraftaccumulate", "saveTo:CPPNToVectorCountNegative", "runNumber:99",
					"log:Minecraft-CPPNToVectorCountNegative",
					"minecraftBlockListTextFile:BlockCount62NegativeSpace2_0.45374_17351.txt", "experiment:edu.southwestern.experiment.post.MinecraftBlockRenderExperiment"});
					 
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	
}
