java -jar dist/MM-NEAT.jar runNumber:0 randomSeed:0 base:onelifeconflict maxGens:200 mu:100 io:true netio:true mating:true task:edu.utexas.cs.nn.tasks.mspacman.MsPacManTask highLevel:true infiniteEdibleTime:false imprisonedWhileEdible:false pacManLevelTimeLimit:8000 pacmanInputOutputMediator:edu.utexas.cs.nn.tasks.mspacman.sensors.mediators.IICheckEachDirectionMediator trials:10 log:OneLifeConflict-ThreeModuleMultitask saveTo:ThreeModuleMultitask fs:false edibleTime:200 trapped:true multitaskModes:3 pacmanMultitaskScheme:edu.utexas.cs.nn.tasks.mspacman.multitask.GhostThreatEdibleOrBothModeSelector
