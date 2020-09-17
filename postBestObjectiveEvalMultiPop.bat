REM Usage:   postBestObjectiveEvalCooperative.bat <experiment directory> <log prefix> <run type> <run number> <number of trials per team>
REM Example: postBestObjectiveEvalCooperative.bat toruspred TorusPred CoOpMultiCCQ 0 5
java -jar "target/MM-NEAT-0.0.1-SNAPSHOT.jar" runNumber:%4 parallelEvaluations:false base:%1 log:%2-%3 saveTo:%3 trials:%5 watch:false showNetworks:false io:false netio:false onlyWatchPareto:true printFitness:true animateNetwork:false monitorInputs:false experiment:edu.southwestern.experiment.post.ObjectiveBestTeamsExperiment logLock:true watchLastBestOfTeams:true 