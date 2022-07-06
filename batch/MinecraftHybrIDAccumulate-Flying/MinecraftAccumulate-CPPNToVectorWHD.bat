cd..
cd..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 base:minecraftaccumulate log:MinecraftAccumulate-CPPNToVectorWHD saveTo:CPPNToVectorWHD mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesWidthHeightDepthBinLabels minecraftXRange:4 minecraftYRange:4 minecraftZRange:4 oneOutputLabelForBlockTypeCPPN:true oneOutputLabelForBlockOrientationCPPN:true vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.CPPNOrVectorToVolumeGenerator minecraftChangeCenterOfMassFitness:true minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet trials:1 mu:100 maxGens:100000 minecraftContainsWholeMAPElitesArchive:true forceLinearArchiveLayoutInMinecraft:false launchMinecraftServerFromJava:false io:true netio:true interactWithMapElitesInWorld:true mating:true fs:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 spaceBetweenMinecraftShapes:5 parallelMAPElitesInitialize:false task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask minecraftSkipInitialClear:true watch:false saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.CPPNOrBlockVectorGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 allowMultipleFunctions:true ftype:0 watch:false netChangeActivationRate:0.3 cleanFrequency:-1 recurrency:false saveAllChampions:true cleanOldNetworks:false includeFullSigmoidFunction:true includeFullGaussFunction:true includeCosineFunction:true includeGaussFunction:false includeIdFunction:true includeTriangleWaveFunction:false includeSquareWaveFunction:false includeFullSawtoothFunction:false includeSigmoidFunction:false includeAbsValFunction:false includeSawtoothFunction:false minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true