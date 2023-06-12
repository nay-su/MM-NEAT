package edu.southwestern.evolution.mome;

import java.util.ArrayList;
import java.util.Vector;
import java.util.stream.Stream;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.SteadyStateEA;
import edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.LonerTask;
import edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.util.PopulationUtil;

public class MOME<T> implements SteadyStateEA<T>{

	protected MOMEArchive<T> archive;
	protected int iterations;	//might want to rename? what is it, just the number of individuals created so far?
	protected LonerTask<T> task; ///seems to be for cleanup, not sure what else

	@Override
	public void initialize(Genotype<T> example) {
		// Do not allow Minecraft to contain archive when using MOME
		Parameters.parameters.setBoolean("minecraftContainsWholeMAPElitesArchive", false);
		
		// TODO Auto-generated method stub
		ArrayList<Genotype<T>> startingPopulation; // Will be new or from saved archive

		System.out.println("Fill up initial archive");		
		// Start from scratch
		int startSize = Parameters.parameters.integerParameter("mu");
		startingPopulation = PopulationUtil.initialPopulation(example, startSize);			
		
		assert startingPopulation.size() == 0 || !(startingPopulation.get(0) instanceof BoundedRealValuedGenotype) || ((BoundedRealValuedGenotype) startingPopulation.get(0)).isBounded() : "Initial individual not bounded: "+startingPopulation.get(0);
	
		//initialize ranges
		MinecraftCoordinates ranges = new MinecraftCoordinates(Parameters.parameters.integerParameter("minecraftXRange"),Parameters.parameters.integerParameter("minecraftYRange"),Parameters.parameters.integerParameter("minecraftZRange"));
		
		//not sure if I needed all the minecraft Init stuff, seems to be post evaluation related?
		
		//add initial population to the archive
		Vector<Score<T>> evaluatedPopulation = new Vector<Score<T>>(startingPopulation.size());
		//not sure if we need netio stuff at all
		boolean backupNetIO = CommonConstants.netio;
		CommonConstants.netio = false; // Some tasks require archive comparison to do this, but it does not exist yet.
		Stream<Genotype<T>> evaluateStream = Parameters.parameters.booleanParameter("parallelMAPElitesInitialize") ? 
												startingPopulation.parallelStream() :
												startingPopulation.stream();
		/**
		 * 
		boolean backupNetIO = CommonConstants.netio;
		CommonConstants.netio = false; // Some tasks require archive comparison to do this, but it does not exist yet.
		Stream<Genotype<T>> evaluateStream = Parameters.parameters.booleanParameter("parallelMAPElitesInitialize") ? 
												startingPopulation.parallelStream() :
												startingPopulation.stream();
		if(Parameters.parameters.booleanParameter("parallelMAPElitesInitialize"))
			System.out.println("Evaluate archive in parallel");
		// Evaluate initial population
		evaluateStream.forEach( (g) -> {
			Score<T> s = task.evaluate(g);
			evaluatedPopulation.add(s);
		});
		CommonConstants.netio = backupNetIO;
		 * 
		 * 		Vector<Score<T>> evaluatedPopulation = new Vector<>(startingPopulation.size());
		 * // Add initial population to archive, if add is true
			evaluatedPopulation.parallelStream().forEach( (s) -> {
				boolean result = archive.add(s); // Fill the archive with random starting individuals, only when this flag is true

			});
		 */
		
	}

	@Override
	public void newIndividual() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int currentIteration() {
		// TODO Auto-generated method stub
		return iterations;
	}

	@Override
	public void finalCleanup() {
		// TODO Auto-generated method stub
		task.finalCleanup();
	}

	/**
	 * gets an ArrayList of the populations genotypes
	 */
	@Override
	public ArrayList<Genotype<T>> getPopulation() {
		// TODO Auto-generated method stub
		 ArrayList<Genotype<T>> result = new ArrayList<Genotype<T>>(archive.archive.size());

		 archive.archive.forEach( (coords, subpop) -> {	////goes through the archive
			 for(Score<T> s : subpop) {		//goes through the scores of the subpop
				 result.add(s.individual);
			 }
		 });
		 
		return result;
	}

	@Override
	public boolean populationChanged() {
		// TODO Auto-generated method stub
		return false;
	}

}
