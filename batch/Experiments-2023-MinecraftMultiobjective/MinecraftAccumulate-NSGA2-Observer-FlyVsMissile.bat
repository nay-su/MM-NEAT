cd..
cd..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 minecraftXRange:5 minecraftYRange:5 minecraftZRange:5 minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.VectorToVolumeGenerator minecraftChangeCenterOfMassFitness:true minecraftMaximizeVolumeFitness:false minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.ExplosiveBlockSet trials:1 mu:20 maxGens:3005 launchMinecraftServerFromJava:false io:true netio:true mating:true fs:false spaceBetweenMinecraftShapes:15 task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true threads:10 minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:minecraftmoo log:MinecraftMOO-NSGA2FlyVsMissile saveTo:NSGA2FlyVsMissile extraSpaceBetweenMinecraftShapes:100 minecraftTargetDistancefromShapeY:0 minecraftTargetDistancefromShapeX:50 minecraftTargetDistancefromShapeZ:0 minecraftMissileFitness:true crossover:edu.southwestern.evolution.crossover.real.SBX rememberParentScores:true minecraftContainsWholeMAPElitesArchive:false