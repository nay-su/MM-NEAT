cd..
cd..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 minecraftXRange:3 minecraftYRange:3 minecraftZRange:3 minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.DirectRepresentationShapeGenerator minecraftChangeCenterOfMassFitness:true minecraftMaximizeVolumeFitness:false minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.ExplosiveBlockSet trials:1 mu:100 maxGens:60000 launchMinecraftServerFromJava:false io:true netio:true mating:true fs:false spaceBetweenMinecraftShapes:20 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.tasks.evocraft.genotype.MinecraftShapeGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true threads:10 minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:minecraftmoo log:MinecraftMOO-MOMEFlyVsMissileDirectSmallPOCapped saveTo:MOMEFlyVsMissileDirectSmallPOCapped extraSpaceBetweenMinecraftShapes:100 minecraftTargetDistancefromShapeY:0 minecraftTargetDistancefromShapeX:25 minecraftTargetDistancefromShapeZ:0 minecraftMissileFitness:true rememberParentScores:true minecraftContainsWholeMAPElitesArchive:false experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 rememberParentScores:true minecraftContainsWholeMAPElitesArchive:false interactWithMapElitesInWorld:false ea:edu.southwestern.evolution.mome.MOME mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesPistonOrientationCountBinLabels minecraftPistonLabelSize:5 minecraftClearWithGlass:false maximumMOMESubPopulationSize:10 parallelMAPElitesInitialize:true