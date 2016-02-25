REM Usage:   postBestObjectiveEval.bat <experiment directory> <log prefix> <run type> <run number> <number of trials per individual>
REM Example: postBestObjectiveEval.bat onelifeconflict OneLifeConflict OneModule 0 5
java -jar "dist/MM-NEAT.jar" runNumber:%4 parallelEvaluations:false base:%1 log:%2-%3 saveTo:%3 trials:%5 watch:false showNetworks:false io:false netio:false onlyWatchPareto:true printFitness:true animateNetwork:false ucb1Evaluation:false showSubnetAnalysis:false monitorInputs:false experiment:edu.utexas.cs.nn.experiment.ObjectiveBestNetworksExperiment logLock:true
