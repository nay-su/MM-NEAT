cd ..
cd ..
java -ea -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 io:true numImprovementEmitters:1 numOptimizingEmitters:0 base:mapelitesfunctionoptimization log:mapelitesfunctionoptimization-CMAMESphere1ImprovementDim500FunctionOptimization saveTo:CMAMESphere1ImprovementDim500FunctionOptimization netio:false lambda:37 maxGens:166500 ea:edu.southwestern.evolution.mapelites.CMAME task:edu.southwestern.tasks.functionoptimization.FunctionOptimizationTask foFunction:fr.inria.optimization.cmaes.fitness.SphereFunction steadyStateIndividualsPerGeneration:10000 genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.functionoptimization.FunctionOptimizationRastriginBinLabels foBinDimension:500 foVectorLength:20 foUpperBounds:5.12 foLowerBounds:-5.12 mapElitesQDBaseOffset:525