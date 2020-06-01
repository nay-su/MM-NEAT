package edu.southwestern.tasks.mario;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.parameters.Parameters;
/**
 * new binning scheme for distinct chunks, negative space, and decoration
 * 
 *
 */
public class MarioMAPElitesDistinctChunksNSAndDecorationBinLabels implements BinLabels {
	List<String> labels = null;
	
	@Override
	/**
	 * sets the bin labels
	 */
	public List<String> binLabels() {
		if(labels == null) { // Create once and re-use, but wait until after Parameters are loaded	
			final int BINS_PER_DIMENSION = Parameters.parameters.integerParameter("marioGANLevelChunks");
			
			int size = (BINS_PER_DIMENSION+1)*BINS_PER_DIMENSION*BINS_PER_DIMENSION;
			labels = new ArrayList<String>(size);
			for(int i = 0; i <= BINS_PER_DIMENSION; i++) { // Distinct Segments
				for(int j = 0; j < BINS_PER_DIMENSION; j++) { // Negative Space
					for(int r = -(BINS_PER_DIMENSION/2); r < BINS_PER_DIMENSION/2; r++) { // Leniency allows negative range
						labels.add("DistinctSegments["+i+"]NS["+j+"0-"+(j+1)+"0]Decoration["+r+"0-"+(r+1)+"0]");
					}
				}
			}
		}
		return labels;
	}
}
