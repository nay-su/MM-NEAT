cd..
cd..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 minecraftXRange:3 minecraftYRange:3 minecraftZRange:3 minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.VectorToVolumeGenerator minecraftChangeCenterOfMassFitness:true minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet trials:1 mu:20 maxGens:1505 launchMinecraftServerFromJava:false io:true netio:true mating:true fs:false spaceBetweenMinecraftShapes:7 task:edu.southwestern.tasks.evocraft.MinecraftShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype vectorPresenceThresholdForEachBlock:true voxelExpressionThreshold:0.5 minecraftAccumulateChangeInCenterOfMass:true parallelEvaluations:true threads:10 minecraftClearSleepTimer:400 minecraftSkipInitialClear:true base:minecraftaccumulate log:MinecraftAccumulate-ESVector saveTo:ESVector