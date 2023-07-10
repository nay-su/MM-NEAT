cd..
cd..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 minecraftXRange:3 minecraftYRange:3 minecraftZRange:3 minecraftEmptySpaceBufferX:27 mapElitesQDBaseOffset:1 minecraftEmptySpaceBufferZ:27 minecraftEmptySpaceBufferY:18 minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.DirectRepresentationShapeGenerator trials:1 minecraftMandatoryWaitTime:15000 minecraftContainsWholeMAPElitesArchive:false forceLinearArchiveLayoutInMinecraft:false minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.ExplosiveBlockSet io:true netio:true interactWithMapElitesInWorld:false mating:true spaceBetweenMinecraftShapes:30 launchMinecraftServerFromJava:false task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask watch:false saveAllChampions:true genotype:edu.southwestern.tasks.evocraft.genotype.MinecraftShapeGenotype vectorPresenceThresholdForEachBlock:true parallelEvaluations:true threads:10 parallelMAPElitesInitialize:true minecraftClearWithGlass:false minecraftClearSleepTimer:400 minecraftSkipInitialClear:true rememberParentScores:true extraSpaceBetweenMinecraftShapes:100 mu:20 maxGens:3005 minecraftAccumulateChangeInCenterOfMass:true minecraftCompassMissileTargets:true minecraftTargetDistancefromShapeX:30 minecraftTargetDistancefromShapeY:0 minecraftTargetDistancefromShapeZ:0 minecraftChangeCenterOfMassFitness:true minecraftMissileFitness:true base:minecraftcomplex log:MinecraftComplex-MissileNSGA saveTo:MissileNSGA




