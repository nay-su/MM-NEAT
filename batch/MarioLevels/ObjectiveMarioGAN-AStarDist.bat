cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:$1 randomSeed:$1 base:mariogan log:MarioGAN-AStarDist saveTo:AStarDist marioGANLevelChunks:1 marioGANUsesOriginalEncoding:false marioGANModel:Mario1_Overworld_30_Epoch5000.pth GANInputSize:30 printFitness:true trials:1 mu:100 maxGens:500 io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.mario.MarioGANLevelTask cleanFrequency:-1 saveAllChampions:false cleanOldNetworks:false logTWEANNData:false logMutationAndLineage:false marioLevelLength:120 marioStuckTimeout:20 watch:false marioProgressPlusJumpsFitness:false marioRandomFitness:false marioSimpleAStarDistance:true