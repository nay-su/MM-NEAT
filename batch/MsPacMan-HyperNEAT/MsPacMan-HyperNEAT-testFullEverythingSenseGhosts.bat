cd ..
cd ..
java -jar target/MM-NEAT-0.0.1-SNAPSHOT.jar runNumber:%1 randomSeed:%1 maxGens:500 mu:25 io:true netio:true mating:true base:HNMsPacMan task:edu.southwestern.tasks.mspacman.MsPacManTask cleanOldNetworks:true pacManLevelTimeLimit:8000 pacmanInputOutputMediator:edu.southwestern.tasks.mspacman.sensors.MsPacManHyperNEATMediator trials:3 log:HyperNEAT-testFullEverythingSenseGhosts saveTo:testFullEverythingSenseGhosts hyperNEAT:true genotype:edu.southwestern.evolution.genotypes.HyperNEATCPPNGenotype allowMultipleFunctions:true fs:false ftype:1 netChangeActivationRate:0.3 pacManFullScreenOutput:true pacmanFullScreenPowerInput:true pacmanBothThreatAndEdibleSubstrate:true pacmanFullScreenProcess:true senseHyperNEATGhostPath:true monitorSubstrates:true showVizDoomInputs:true showCPPN:true stepByStep:true substrateGridSize:10 showHighestActivatedOutput:true sortOutputActivations:true inheritFitness:false watch:true showNetworks:true printFitness:true animateNetwork:false monitorInputs:true 