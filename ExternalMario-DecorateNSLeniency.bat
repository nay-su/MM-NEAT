java -jar ExternalRealValuedGenotypeTask.jar runNumber:%1 randomSeed:%1 marioGANLevelChunks:10 marioGANUsesOriginalEncoding:false marioGANModel:Mario1_Overworld_5_Epoch5000.pth GANInputSize:5 trials:1 mu:0 maxGens:100000 io:false netio:false genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.mario.MarioGANLevelTask cleanFrequency:-1 saveAllChampions:false cleanOldNetworks:false logTWEANNData:false logMutationAndLineage:false marioStuckTimeout:20 watch:false marioProgressPlusJumpsFitness:false marioRandomFitness:false marioSimpleAStarDistance:true ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.mario.MarioMAPElitesDecorNSAndLeniencyBinLabels steadyStateIndividualsPerGeneration:100 aStarSearchBudget:100000