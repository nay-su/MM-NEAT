cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 base:targetimage mu:400 maxGens:100000000 io:true netio:true mating:true task:edu.southwestern.tasks.innovationengines.PictureTargetTask log:TargetImage-screamNeuronBinningEnhancedCPPNGenotype saveTo:screamNeuronBinningEnhancedCPPNGenotype allowMultipleFunctions:true ftype:0 netChangeActivationRate:0.3 cleanFrequency:400 recurrency:false logTWEANNData:false logMutationAndLineage:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.innovationengines.CPPNNeuronCountBinLabels fs:true genotype:edu.southwestern.evolution.genotypes.EnhancedCPPNPictureGenotype trainingAutoEncoder:false useWoolleyImageMatchFitness:false useRMSEImageMatchFitness:true matchImageFile:theScream256.png fitnessSaveThreshold:0.8 imageArchiveSaveFrequency:50000 includeSigmoidFunction:true includeTanhFunction:false includeIdFunction:true includeFullApproxFunction:false includeApproxFunction:false includeGaussFunction:true includeSineFunction:true includeCosineFunction:true includeSawtoothFunction:false includeAbsValFunction:false includeHalfLinearPiecewiseFunction:false includeStretchedTanhFunction:false includeReLUFunction:false includeSoftplusFunction:false includeLeakyReLUFunction:false includeFullSawtoothFunction:false includeTriangleWaveFunction:false includeSquareWaveFunction:false blackAndWhitePicbreeder:true