cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 base:loderunnerlevels log:LodeRunnerLevels-AStarDistAndConnectivityOn5Levels saveTo:AStarDistAndConnectivityOn5Levels LodeRunnerGANModel:LodeRunnerAllGround5LevelsEpoch20000_10_7.pth watch:false GANInputSize:10 trials:1 mu:100 maxGens:500 io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.loderunner.LodeRunnerGANLevelTask cleanFrequency:-1 saveAllChampions:true cleanOldNetworks:false logTWEANNData:false logMutationAndLineage:false aStarSearchBudget:100000 lodeRunnerAllowsSimpleAStarPath:true lodeRunnerAllowsConnectivity:true lodeRunnerAllowsTSPSolutionPath:false allowWeirdLodeRunnerActions:false lodeRunnerMaximizeEnemies:false