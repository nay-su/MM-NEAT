cd..
cd..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 minecraftXRange:5 minecraftYRange:5 minecraftZRange:5 mapElitesQDBaseOffset:1 minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.DirectRepresentationShapeGenerator trials:1 minecraftMandatoryWaitTime:10000 minecraftContainsWholeMAPElitesArchive:false forceLinearArchiveLayoutInMinecraft:false minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet io:true netio:true interactWithMapElitesInWorld:false mating:true spaceBetweenMinecraftShapes:22 launchMinecraftServerFromJava:false task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.tasks.evocraft.genotype.MinecraftShapeGenotype vectorPresenceThresholdForEachBlock:true parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearWithGlass:true minecraftClearSleepTimer:400 minecraftSkipInitialClear:true rememberParentScores:true extraSpaceBetweenMinecraftShapes:100 mu:100 maxGens:60000 experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesBlockCountEmptyCountBinLabels minecraftPistonLabelSize:5 minecraftAccumulateChangeInCenterOfMass:true ea:edu.southwestern.evolution.mapelites.MAPElites minecraftWeightedSumsAccumulateNewBlockPositionsAndChangeCenterOfMassFitness:true base:minecraftcomplex log:MinecraftComplex-POBigFlyMEWS saveTo:POBigFlyMEWS




