java -jar dist/MM-NEAT.jar runNumber:0 randomSeed:0 base:multiplelivesconflict maxGens:200 mu:100 io:true netio:true mating:true task:edu.utexas.cs.nn.tasks.mspacman.MsPacManTask highLevel:true infiniteEdibleTime:false imprisonedWhileEdible:false pacManLevelTimeLimit:30000 pacmanInputOutputMediator:edu.utexas.cs.nn.tasks.mspacman.sensors.mediators.IICheckEachDirectionMediator trials:10 log:MultipleLivesConflict-TUGMMD saveTo:TUGMMD fs:false edibleTime:200 trapped:true pacManGainsLives:true pacmanLives:3 ea:edu.utexas.cs.nn.evolution.nsga2.tug.TUGNSGA2 constantTUGGoalIncrements:true tugGoalIncrement0:5 tugGoalIncrement1:20 mmdRate:0.1
