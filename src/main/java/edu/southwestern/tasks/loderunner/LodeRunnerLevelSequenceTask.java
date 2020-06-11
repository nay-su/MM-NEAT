package edu.southwestern.tasks.loderunner;

import java.util.List;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;


public abstract class LodeRunnerLevelSequenceTask<T> extends LodeRunnerLevelTask<T> {

	public LodeRunnerLevelSequenceTask() {
		super();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num){
		List<List<Integer>>[] levelSequence = getLevelSequence(individual);
		long genotypeId = individual.getId();
		Pair<double[], double[]>[] scoreSequence = new Pair[levelSequence.length];
		for(int i = 0; i < levelSequence.length; i++) {
			double psuedoRandomSeed = -1; // TODO: Different seed for each level in the sequnce ... needs abstract method
			scoreSequence[i] = evaluateOneLevel(levelSequence[i], psuedoRandomSeed, genotypeId);
		}
		if(Parameters.parameters.booleanParameter("lodeRunnerLevelSequenceAverages")) {
			
		}
		else if(Parameters.parameters.booleanParameter("lodeRunnerLevelSequenceIndividual")) {
			
		}
		return null;	
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<List<Integer>> getLodeRunnerLevelListRepresentationFromGenotype(Genotype<T> individual) {
		return getLodeRunnerLevelListRepresentationFromStaticGenotype((List<Double>) individual.getPhenotype());
	}

	private static List<List<Integer>> getLodeRunnerLevelListRepresentationFromStaticGenotype(List<Double> phenotype) {
		return LodeRunnerGANLevelTask.getLodeRunnerLevelListRepresentationFromGenotypeStatic(phenotype);
	}


	@Override
	public double getRandomSeedForSpawnPoint(Genotype<T> individual) {
		return getRandomSeedForSpawnPointStatic(individual);
	}
	
	@SuppressWarnings("unchecked")
	private double getRandomSeedForSpawnPointStatic(Genotype<T> individual) {
		List<Double> latentVector = (List<Double>) individual.getPhenotype(); //creates a double array for the spawn to be placed in GAN levels 
		double[] doubleArray = ArrayUtil.doubleArrayFromList(latentVector);
		double firstLatentVariable = doubleArray[0];
		return firstLatentVariable;
	}


	public abstract List<List<Integer>>[] getLevelSequence(Genotype<T> individual);
	

}
