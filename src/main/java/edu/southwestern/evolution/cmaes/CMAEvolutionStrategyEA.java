package edu.southwestern.evolution.cmaes;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.mulambda.MuLambda;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.SinglePopulationTask;
import edu.southwestern.util.PopulationUtil;
import fr.inria.optimization.cmaes.CMAEvolutionStrategy;

public class CMAEvolutionStrategyEA extends MuLambda<ArrayList<Double>> {

	public CMAEvolutionStrategy cma = new CMAEvolutionStrategy();
	
	@SuppressWarnings("unchecked")
	public CMAEvolutionStrategyEA() {
		this(MuLambda.MLTYPE_COMMA, (SinglePopulationTask<ArrayList<Double>>) MMNEAT.task, Parameters.parameters.integerParameter("mu"), Parameters.parameters.integerParameter("lambda"), Parameters.parameters.booleanParameter("io"));
	}
	
	public CMAEvolutionStrategyEA(int mltype, SinglePopulationTask<ArrayList<Double>> task, int mu, int lambda, boolean io) {
		super(mltype, task, mu, lambda, io);
		if (task.numObjectives() != 1) {
			System.out.println("CMA-ES is meant to be used with only one objective, but "+task.numObjectives()+" were provided!");
			System.exit(1);
		}
		cma.readProperties(); // read options, see file CMAEvolutionStrategy.properties
		cma.setDimension(10); // overwrite some loaded properties
		cma.setInitialX(0.05); // in each dimension, also setTypicalX can be used
		cma.setInitialStandardDeviation(0.2); // also a mandatory setting 
		cma.options.stopFitness = -9999; //1e-14;       // optional setting
		cma.parameters.setMu(mu);
		cma.parameters.setPopulationSize(lambda);
		
		// Not saving initial fitnesses
		cma.init();  // new double[cma.parameters.getPopulationSize()];

		// initial output to files
		cma.writeToDefaultFilesHeaders(0); // 0 == overwrites old files
	}

	@Override
	public ArrayList<Genotype<ArrayList<Double>>> generateChildren(int numChildren, ArrayList<Score<ArrayList<Double>>> parentScores) {
		double[][] pop = cma.samplePopulation(); // get a new population of solutions, this is equivalent to getting children
		assert pop.length == numChildren : "Length of population is not equal to the number of children specified!"; // ensure population is correct size
		return PopulationUtil.genotypeArrayListFromDoubles(pop); // convert population to be usable like normal
	}

	@Override
	public ArrayList<Genotype<ArrayList<Double>>> selection(int numParents, ArrayList<Score<ArrayList<Double>>> scores) {
		double[] fitness = new double[scores.size()]; // CMA-ES needs scores to be converted into a double[] to be usable
		ArrayList<Genotype<ArrayList<Double>>> genotypes = new ArrayList<Genotype<ArrayList<Double>>>(numParents); // need to copy genotypes from score input; see below
		for (int i = 0; i < scores.size(); i++) {
			fitness[i] = -scores.get(i).scores[0]; // Negate values because CMA-ES is a minimizer, not a maximizer
			genotypes.add(scores.get(i).individual); // Get genotypes from input score to be returned
		}
		cma.updateDistribution(fitness); // Not actually doing selection here, but for CMA-ES, this is functionally equivalent
		assert genotypes.size() == numParents : "Length of genotypes is not equal to the number of parents specified!";
		return genotypes; // Just returns the input score genotypes to keep MM-NEAT happy, actual data is internal to CMA-ES
	}

	
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		System.out.println("Testing CMA-ES");
		MMNEAT.main("ea:edu.southwestern.evolution.cmaes.CMAEvolutionStrategyEA runNumber:0 randomSeed:0 base:mariogan log:MarioGAN-Test saveTo:Test trials:1 printFitness:true mu:50 maxGens:500 io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.mario.MarioGANLevelTask saveAllChampions:false cleanOldNetworks:true logTWEANNData:false logMutationAndLineage:false marioLevelLength:120 marioStuckTimeout:20 watch:true".split(" "));
	}
}
