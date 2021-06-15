cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 base:loderunnerkldivergence log:LodeRunnerKLDivergence-CMAME15Improvement saveTo:CMAME15Improvement LodeRunnerGANModel:LodeRunnerAllGround100LevelsEpoch200000_10_7.pth watch:false GANInputSize:10 trials:1 mu:100 maxGens:100000 io:true netio:true genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype mating:true fs:false task:edu.southwestern.tasks.loderunner.LodeRunnerGANLevelTask cleanFrequency:-1 saveAllChampions:true cleanOldNetworks:false logTWEANNData:false logMutationAndLineage:false steadyStateIndividualsPerGeneration:100 aStarSearchBudget:100000 mapElitesBinLabels:edu.southwestern.evolution.mapelites.generalmappings.KLDivergenceBinLabels ea:edu.southwestern.evolution.mapelites.CMAME experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment allowWeirdLodeRunnerActions:false lodeRunnerMaximizeEnemies:false lodeRunnerTSPBudget:0 mapElitesKLDivLevel1:data\\VGLC\\LODERU~1\\Processed\\LEVEL1~1.TXT mapElitesKLDivLevel2:data\\VGLC\\LODERU~1\\Processed\\LEVEL2~1.TXT klDivBinDimension:100 klDivMaxValue:2.0 numImprovementEmitters:15 numOptimizingEmitters:0
