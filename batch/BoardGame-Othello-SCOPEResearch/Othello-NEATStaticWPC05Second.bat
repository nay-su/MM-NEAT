cd ..
cd ..
java -jar dist/MM-NEATv2.jar runNumber:%1 randomSeed:%1 base:othello trials:10 maxGens:300 mu:50 io:true netio:true mating:true task:edu.utexas.cs.nn.tasks.boardGame.StaticOpponentBoardGameTask cleanOldNetworks:true fs:false log:Othello-NEATStaticWPC05Second saveTo:NEATStaticWPC05Second boardGame:boardGame.othello.Othello boardGameOpponent:boardGame.agents.treesearch.BoardGamePlayerMinimaxAlphaBetaPruning boardGameOpponentHeuristic:boardGame.heuristics.StaticOthelloWPCHeuristic boardGamePlayer:boardGame.agents.treesearch.BoardGamePlayerMinimaxAlphaBetaPruning minimaxSearchDepth:2 minimaxSecondBestRate:0.05 boardGameOthelloFitness:true boardGameSimpleFitness:true boardGameWinPercentFitness:true randomArgMaxTieBreak:true heuristicOverrideTerminalStates:true 

REM evalReport:true