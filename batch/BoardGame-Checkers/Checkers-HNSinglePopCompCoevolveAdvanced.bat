cd ..
cd ..
java -jar dist/MM-NEATv2.jar runNumber:%1 randomSeed:%1 base:checkers trials:10 maxGens:500 mu:100 io:true netio:true mating:true task:edu.utexas.cs.nn.tasks.boardGame.SinglePopulationCompetativeCoevolutionBoardGameTask cleanOldNetworks:true fs:false log:Checkers-HNSinglePopCompCoevolveAdvanced saveTo:HNSinglePopCompCoevolveAdvanced boardGame:boardGame.checkers.Checkers genotype:edu.utexas.cs.nn.evolution.genotypes.HyperNEATCPPNGenotype hyperNEAT:true boardGameFitnessFunction:boardGame.fitnessFunction.CheckersAdvancedFitness boardGameOpponent:boardGame.agents.treesearch.BoardGamePlayerMinimaxAlphaBetaPruning boardGameOpponentHeuristic:boardGame.heuristics.PieceDifferentialBoardGameHeuristic boardGamePlayer:boardGame.agents.treesearch.BoardGamePlayerMinimaxAlphaBetaPruning