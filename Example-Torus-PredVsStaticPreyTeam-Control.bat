java -jar dist/MM-NEATv2.jar runNumber:%1 randomSeed:%1 base:torus trials:10 maxGens:200 mu:100 io:true netio:true mating:false fs:true task:edu.utexas.cs.nn.tasks.gridTorus.TorusEvolvedPredatorsVsStaticPreyTask log:PredVsStaticPreyTeam-Control saveTo:Control allowDoNothingActionForPredators:true allowDoNothingActionForPreys:true torusPreys:2 torusPredators:5