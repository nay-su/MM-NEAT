package edu.southwestern.evolution.mapelites;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.util.PopulationUtil;
import fr.inria.optimization.cmaes.CMAEvolutionStrategy;

public class CMAME extends MAPElites<ArrayList<Double>> {
	
	private Emitter[] emitters = null;
	
	public void initialize(Genotype<ArrayList<Double>> example) {
		super.initialize(example);
		int dimension = MMNEAT.getLowerBounds().length;
		if (emitters == null) {
			emitters = new Emitter[] {newEmitter(dimension, "Emitter 1")};
		}
	}
	
	/**
	 * Create new individuals based on set
	 * population size of Emitters, and update
	 * distribution of Emitters with fitnesses 
	 * afterwards
	 */
	public void newIndividual() {
		Emitter thisEmitter = emitters[0]; 
		
		// TODO Needs to be able to shuffle through emitters, currently 
		// only uses a single emitter set to thisEmitter, which should 
		// change when multiple emitters are used.
		
		double[][] pop = thisEmitter.samplePopulation(); // need to check feasibility?
		double[] deltaI = new double[pop.length]; // create array for deltas
		ArrayList<Genotype<ArrayList<Double>>> popGenotypes = PopulationUtil.genotypeArrayListFromDoubles(pop); // convert population to genotypes
		assert task.evaluate(popGenotypes.get(0)).usesMAPElitesBinSpecification() : "Cannot use a traditional behavior vector with CMA-ME";
		for (int i = 0; i < popGenotypes.size(); i++) { // for each individual
			Genotype<ArrayList<Double>> individual = popGenotypes.get(i);
			Score<ArrayList<Double>> individualScore = task.evaluate(individual); // evaluate score for individual
			//boolean replacedBool = archive.add(individualScore); // attempt to add individual to archive
			
			double individualBinScore = individualScore.behaviorIndexScore(archive.getBinMapping().oneDimensionalIndex(individualScore.MAPElitesBinIndex()));
			double currentBinScore = archive.getElite(individualScore.MAPElitesBinIndex()) == null ? Double.NEGATIVE_INFINITY : archive.getElite(individualScore.MAPElitesBinIndex()).behaviorIndexScore(archive.getBinMapping().oneDimensionalIndex(individualScore.MAPElitesBinIndex()));
			
			if (currentBinScore >= individualBinScore) { // if bin was better or equal
				System.out.println("Current bin ("+currentBinScore+") was already better than or equal to new bin ("+individualBinScore+").");
			} else if (currentBinScore == Double.NEGATIVE_INFINITY) { // if bin was empty
				System.out.println("Added new bin ("+individualBinScore+").");
				thisEmitter.solutionCount++;
				deltaI[i] = -individualBinScore;
			} else { // if bin existed, but was worse than the new one
				System.out.println("Improved current bin ("+currentBinScore+")");
				deltaI[i] = -(individualBinScore - currentBinScore);
				thisEmitter.solutionCount++;
			}
			boolean replacedBool = archive.add(individualScore); // attempt to add individual to archive
			
			System.out.println("Emitter: \""+thisEmitter.emitterTag+"\"\tSolutions: "+thisEmitter.solutionCount);
			fileUpdates(replacedBool); // log failure or success
		}
		thisEmitter.updateDistribution(deltaI); // update distribution once population is done evaluating
	}
	
	private class Emitter implements Comparable<Emitter> {
		int solutionCount = 0;
		String emitterTag;
		CMAEvolutionStrategy CMAESInstance;
		
		// Create the emitter with a CMAEvolutionStrategy instance and "tag" name
		public Emitter(CMAEvolutionStrategy CMAESInstance, String tag) {
			this.CMAESInstance = CMAESInstance;
			this.emitterTag = tag;
		}
		
		// get the population from the internal CMA-ES instance
		public double[][] samplePopulation() {
			return CMAESInstance.samplePopulation();
		}
		
		// update the distribution of the internal CMA-ES instance
		public void updateDistribution(double[] functionValues) {
			CMAESInstance.updateDistribution(functionValues);
		}
		
		// comparable through solution count
		@Override
		public int compareTo(Emitter other) {
			return this.solutionCount - other.solutionCount;
		}
	}
	
	/**
	 * Create a new emitter with the default parameters, 
	 * and a provided name
	 * 
	 * @param dimension Dimension of the emitter
	 * @param tag Name corresponding to this emitter
	 * @return An initalized Emitter with the provided name
	 */
	private Emitter newEmitter(int dimension, String tag) {
		CMAEvolutionStrategy optEmitter = new CMAEvolutionStrategy();
		optEmitter.setDimension(dimension);
		optEmitter.setInitialX(0.0);
		optEmitter.setInitialStandardDeviation(0.2); // TODO unsure if should be hardcoded or not
		int mu = Parameters.parameters.integerParameter("mu");
		optEmitter.parameters.setMu(mu);
		optEmitter.parameters.setPopulationSize(mu); // set Mu and Lambda to mu
		optEmitter.init();
		optEmitter.writeToDefaultFilesHeaders(0); // Overwrite existing CMA-ES files
		return new Emitter(optEmitter, tag);
	}
	
	// Test CMA-ME with the MarioGAN
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		System.out.println("Testing CMA-ME");
		int runNum = 0;
		MMNEAT.main(("runNumber:"+runNum+" randomSeed:"+runNum+" marioGANLevelChunks:10 marioSimpleAStarDistance:true ea:edu.southwestern.evolution.mapelites.CMAME base:mariogan marioGANModel:GECCO2018GAN_World1-1_32_Epoch5000.pth GANInputSize:32 log:MarioGAN-CMAMETest saveTo:CMAMETest trials:1 printFitness:true mu:50 maxGens:500 io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.mario.MarioGANLevelTask saveAllChampions:false cleanOldNetworks:true logTWEANNData:false logMutationAndLineage:false marioLevelLength:120 marioStuckTimeout:20 watch:false steadyStateIndividualsPerGeneration:100 aStarSearchBudget:100000 mapElitesBinLabels:edu.southwestern.tasks.mario.MarioMAPElitesDistinctChunksNSAndDecorationBinLabels experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment").split(" "));
	}
}
