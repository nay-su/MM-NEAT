package edu.southwestern.tasks.mario;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.parameters.Parameters;

/**
 * Binning scheme for Mario GAN using Level
 * Novelty, Decoration Frequency, and Leniency
 * 
 * @author Maxx Batterton
 *
 */
public class MarioMAPElitesNoveltyDecorAndLeniencyBinLabels implements BinLabels {

	List<String> labels = null;
	private int levelBinsPerDimension; // amount of bins for the Decor and Leniency dimensions
	private int noveltyBinsPerDimension; // amount of bins for the Novelty dimension
	
	@Override
	public List<String> binLabels() {
		if(labels == null) { // Create once and re-use, but wait until after Parameters are loaded	
			levelBinsPerDimension = Parameters.parameters.integerParameter("marioGANLevelChunks");
			noveltyBinsPerDimension = Parameters.parameters.integerParameter("noveltyBinAmount");
			
			int size = (levelBinsPerDimension+1)*levelBinsPerDimension*levelBinsPerDimension;
			labels = new ArrayList<String>(size);
			for(int i = 0; i < noveltyBinsPerDimension; i++) { // Distinct Segments
				for(int j = 0; j < levelBinsPerDimension; j++) { // Negative Space
					for(int r = -(levelBinsPerDimension/2); r < levelBinsPerDimension/2; r++) { // Leniency allows negative range
						labels.add("Novelty["+((double) i/noveltyBinsPerDimension)+"-"+((double) (i+1)/noveltyBinsPerDimension)+"]DecorFrequency["+j+"0-"+(j+1)+"0]Leniency["+r+"0-"+(r+1)+"0]");
					}
				}
			}
		}
		return labels;
	}

	@Override
	public int oneDimensionalIndex(int[] multi) {
		int binIndex = (int) ((multi[0])*levelBinsPerDimension + (multi[1])*levelBinsPerDimension + multi[2]);
		return binIndex;
	}
}
