cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 ea:edu.southwestern.evolution.cmaes.CMAEvolutionStrategyEA base:loderunnerlevelscmaes log:LodeRunnerLevelsCMAES-AStarDistConnectivityComboOn20Levels saveTo:AStarDistConnectivityComboOn20Levels LodeRunnerGANModel:LodeRunnerAllGround20LevelsEpoch20000_10_7.pth watch:false GANInputSize:10 trials:1 mu:100 lambda:100 maxGens:500 io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.loderunner.LodeRunnerGANLevelTask cleanFrequency:-1 saveAllChampions:true cleanOldNetworks:false logTWEANNData:false logMutationAndLineage:false aStarSearchBudget:100000 lodeRunnerAllowsSimpleAStarPath:false lodeRunnerAllowsAStarConnectivityCombo:true lodeRunnerAllowsConnectivity:false lodeRunnerAllowsTSPSolutionPath:false lodeRunnerMaximizeEnemies:false 
