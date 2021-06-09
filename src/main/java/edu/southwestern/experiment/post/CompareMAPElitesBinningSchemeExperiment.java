package edu.southwestern.experiment.post;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.Scanner;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.mapelites.Archive;
import edu.southwestern.evolution.mapelites.MAPElites;
import edu.southwestern.experiment.Experiment;
import edu.southwestern.log.MMNEATLog;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.LonerTask;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.file.FileUtilities;
import edu.southwestern.util.stats.StatisticsUtilities;
import wox.serial.Easy;

public class CompareMAPElitesBinningSchemeExperiment<T> implements Experiment {

	//private ArrayList<Score<T>> oldScores = new ArrayList<Score<T>>();
	MAPElites<T> newMAPElites;
	
	@Override
	public void init() {
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void run() {
		String lastLine = "";
		try {
			String fillLogName = FileUtilities.getSaveDirectory() + "\\" + Parameters.parameters.stringParameter("log") + Parameters.parameters.integerParameter("runNumber") + "_Fill_log.txt";// creates file prefix
			File oldFill = new File(fillLogName);
			Scanner oldFile = new Scanner(oldFill);
			while (oldFile.hasNextLine()) {
				lastLine = oldFile.nextLine();
			}
			oldFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String[] oldEndValues = lastLine.split("\t");
		
		String dir = MMNEAT.getArchive().getArchiveDirectory(); // get old directory
		String binLabelName = Parameters.parameters.classParameter("mapElitesBinLabels").getName();
		String binLabelOutName = "comparedTo_" + binLabelName.substring(1+binLabelName.lastIndexOf('.'));
		String binLabelLastName = "comparedTo_" + binLabelName.substring(1+binLabelName.lastIndexOf('.')) + "_MAPElites";
		newMAPElites = new MAPElites<T>(binLabelOutName, true, true, false); // setup new MAP Elites with new directory
		
		MMNEAT.ea = newMAPElites; // set EA to new MAP Elites
		Archive<T> comparedArchive = newMAPElites.getArchive(); // Get new archive
		
		FilenameFilter filter = new FilenameFilter() { // filter only *.xml files from old directory
            public boolean accept(File dir, String name) {
                return name.endsWith(".xml");
            }
        };
        LonerTask task = (LonerTask) MMNEAT.task;
		for (String oneFile : new File(dir).list(filter)) { // get each xml, evaluate it, and add it to the new archive
			Genotype<T> geno = (Genotype<T>) Easy.load(dir+"\\"+oneFile);
			Score<T> evalScore = task.evaluateOne(geno);
			comparedArchive.add(evalScore);
		}

		Float[] elite = ArrayUtils.toObject(comparedArchive.getEliteScores());
		MMNEATLog compareLog = new MMNEATLog(binLabelOutName, false, false, false, true, false);
		MMNEATLog lastLog = new MMNEATLog(binLabelLastName, false, false, false, true, false);
		
		int occupiedBins = elite.length - ArrayUtil.countOccurrences(Float.NEGATIVE_INFINITY, elite);
		compareLog.log("Occupied Bins: " + occupiedBins);
		compareLog.log("Occupied Bins Percent: " + (occupiedBins/((float) elite.length))*100 + "% ("+occupiedBins+"/"+elite.length+")");
		compareLog.log("Surviving Bins Percent: " + (occupiedBins/((float) Integer.parseInt(oldEndValues[1])))*100 + "% ("+occupiedBins+"/"+Integer.parseInt(oldEndValues[1])+")");
		compareLog.log("QD Score: " + MAPElites.calculateQDScore(elite));
		compareLog.log("Maximum Fitness: " + StatisticsUtilities.maximum(elite));		
		lastLog.log(oldEndValues[0] + "\t" + StringUtils.join(elite, "\t"));
	}

	@Override
	public boolean shouldStop() {
		return true; // always
	}
	
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		String arg1 = "mariolevelsdecoratensleniency";
		String arg2 = "MarioLevelsDecorateNSLeniency";
		String arg3 = "Direct2GAN";
		String arg4 = "1";
		String arg5 = "1";
		String arg6 = "edu.southwestern.tasks.mario.MarioMAPElitesDistinctChunksNSAndDecorationBinLabels";
		//MMNEAT.main(("runNumber:0 parallelEvaluations:false base:mariolevelsdecoratensleniency log:MarioLevelsDecorateNSLeniency-CPPNThenDirect2GAN saveTo:CPPNThenDirect2GAN trials:1 experiment:edu.southwestern.experiment.post.CompareMAPElitesBinningSchemeExperiment mapElitesBinLabels:edu.southwestern.tasks.mario.MarioMAPElitesDistinctChunksNSAndDecorationBinLabels").split(" "));
		MMNEAT.main(("runNumber:"+arg4+" parallelEvaluations:false base:"+arg1+" log:"+arg2+"-"+arg3+" saveTo:"+arg3+" trials:"+arg5+" experiment:edu.southwestern.experiment.post.CompareMAPElitesBinningSchemeExperiment mapElitesBinLabels:"+arg6+" logLock:true io:false").split(" "));
	}
	
}
