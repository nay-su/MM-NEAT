cd..
cd..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 base:minecraft log:Minecraft-VectorCountNegative saveTo:VectorCountNegative mapElitesBinLabels:edu.southwestern.tasks.evocraft.characterizations.MinecraftMAPElitesBlockCountEmptyCountBinLabels minecraftXRange:6 minecraftYRange:6 minecraftZRange:6 minecraftShapeGenerator:edu.southwestern.tasks.evocraft.shapegeneration.VectorToVolumeGenerator minecraftChangeCenterOfMassFitness:true minecraftBlockSet:minecraftBlockSet:edu.southwestern.tasks.evocraft.blocks.MachineBlockSet trials:1 mu:100 maxGens:100000 minecraftContainsWholeMAPElitesArchive:true forceLinearArchiveLayoutInMinecraft:false launchMinecraftServerFromJava:false io:true netio:true interactWithMapElitesInWorld:true mating:true fs:false ea:edu.southwestern.evolution.mapelites.MAPElites experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment steadyStateIndividualsPerGeneration:100 spaceBetweenMinecraftShapes:5 parallelMAPElitesInitialize:false task:edu.southwestern.tasks.evocraft.MinecraftLonerShapeTask allowMultipleFunctions:true ftype:0 watch:false netChangeActivationRate:0.3 cleanFrequency:-1 recurrency:false saveAllChampions:true cleanOldNetworks:false includeFullSigmoidFunction:true includeFullGaussFunction:true includeCosineFunction:true includeGaussFunction:false includeIdFunction:true includeTriangleWaveFunction:false includeSquareWaveFunction:false includeFullSawtoothFunction:false includeSigmoidFunction:false includeAbsValFunction:false includeSawtoothFunction:false