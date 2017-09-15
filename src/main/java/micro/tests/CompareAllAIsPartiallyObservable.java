/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package micro.tests;

import micro.ai.core.AI;
import micro.ai.core.AIWithComputationBudget;
import micro.ai.core.ContinuingAI;
import micro.ai.core.PseudoContinuingAI;
import micro.ai.portfolio.PortfolioAI;
import micro.ai.*;
import micro.ai.abstraction.LightRush;
import micro.ai.abstraction.RangedRush;
import micro.ai.abstraction.WorkerRush;
import micro.ai.abstraction.pathfinding.BFSPathFinding;
import micro.ai.abstraction.pathfinding.GreedyPathFinding;
import micro.ai.evaluation.SimpleSqrtEvaluationFunction3;
import micro.ai.mcts.naivemcts.NaiveMCTS;
import micro.ai.mcts.uct.DownsamplingUCT;
import micro.ai.mcts.uct.UCT;
import micro.ai.mcts.uct.UCTUnitActions;
import micro.ai.minimax.ABCD.IDABCD;
import micro.ai.minimax.RTMiniMax.IDRTMinimax;
import micro.ai.minimax.RTMiniMax.IDRTMinimaxRandomized;
import micro.ai.montecarlo.*;

import java.io.File;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import micro.rts.PhysicalGameState;
import micro.rts.units.UnitTypeTable;
import micro.ai.core.InterruptibleAI;

/**
 *
 * @author santi
 */
public class CompareAllAIsPartiallyObservable {
    
    public static void main(String args[]) throws Exception 
    {
    	boolean CONTINUING = true;
        int TIME = 100;
        int MAX_ACTIONS = 100;
        int MAX_PLAYOUTS = -1;
        int PLAYOUT_TIME = 100;
        int MAX_DEPTH = 10;
        int RANDOMIZED_AB_REPEATS = 10;
        
        List<AI> bots = new LinkedList<AI>();
        UnitTypeTable utt = new UnitTypeTable();

        bots.add(new RandomAI());
        bots.add(new RandomBiasedAI());
        bots.add(new LightRush(utt, new BFSPathFinding()));
        bots.add(new RangedRush(utt, new BFSPathFinding()));
        bots.add(new WorkerRush(utt, new BFSPathFinding()));
        bots.add(new PortfolioAI(new AI[]{new WorkerRush(utt, new BFSPathFinding()),
                                          new LightRush(utt, new BFSPathFinding()),
                                          new RangedRush(utt, new BFSPathFinding()),
                                          new RandomBiasedAI()}, 
                                 new boolean[]{true,true,true,false}, 
                                 TIME, MAX_PLAYOUTS, PLAYOUT_TIME*4, new SimpleSqrtEvaluationFunction3()));
      
        bots.add(new IDRTMinimax(TIME, new SimpleSqrtEvaluationFunction3()));
        bots.add(new IDRTMinimaxRandomized(TIME, RANDOMIZED_AB_REPEATS, new SimpleSqrtEvaluationFunction3()));
        bots.add(new IDABCD(TIME, MAX_PLAYOUTS, new LightRush(utt, new GreedyPathFinding()), PLAYOUT_TIME, new SimpleSqrtEvaluationFunction3(), false));

        bots.add(new MonteCarlo(TIME, PLAYOUT_TIME, MAX_PLAYOUTS, new RandomBiasedAI(), new SimpleSqrtEvaluationFunction3()));
        bots.add(new MonteCarlo(TIME, PLAYOUT_TIME, MAX_PLAYOUTS, MAX_ACTIONS, new RandomBiasedAI(), new SimpleSqrtEvaluationFunction3()));
        // by setting "MAX_DEPTH = 1" in the next two bots, this effectively makes them Monte Carlo search, instead of Monte Carlo Tree Search
        bots.add(new NaiveMCTS(TIME, MAX_PLAYOUTS, PLAYOUT_TIME, 1, 0.33f, 0.0f, 0.75f, new RandomBiasedAI(), new SimpleSqrtEvaluationFunction3(), true));
        bots.add(new NaiveMCTS(TIME, MAX_PLAYOUTS, PLAYOUT_TIME, 1, 1.00f, 0.0f, 0.25f, new RandomBiasedAI(), new SimpleSqrtEvaluationFunction3(), true));

        bots.add(new UCT(TIME, MAX_PLAYOUTS, PLAYOUT_TIME, MAX_DEPTH, new RandomBiasedAI(), new SimpleSqrtEvaluationFunction3()));
        bots.add(new DownsamplingUCT(TIME, MAX_PLAYOUTS, PLAYOUT_TIME, MAX_ACTIONS, MAX_DEPTH, new RandomBiasedAI(), new SimpleSqrtEvaluationFunction3()));
        bots.add(new UCTUnitActions(TIME, PLAYOUT_TIME, MAX_DEPTH*10, new RandomBiasedAI(), new SimpleSqrtEvaluationFunction3()));
        bots.add(new NaiveMCTS(TIME, MAX_PLAYOUTS, PLAYOUT_TIME, MAX_DEPTH, 0.33f, 0.0f, 0.75f, new RandomBiasedAI(), new SimpleSqrtEvaluationFunction3(), true));
        bots.add(new NaiveMCTS(TIME, MAX_PLAYOUTS, PLAYOUT_TIME, MAX_DEPTH, 1.00f, 0.0f, 0.25f, new RandomBiasedAI(), new SimpleSqrtEvaluationFunction3(), true));

        if (CONTINUING) {
        	// Find out which of the bots can be used in "continuing" mode:
        	List<AI> bots2 = new LinkedList<>();
        	for(AI bot:bots) {
        		if (bot instanceof AIWithComputationBudget) {
        			if (bot instanceof InterruptibleAI) {
        				bots2.add(new ContinuingAI(bot));
        			} else {
        				bots2.add(new PseudoContinuingAI((AIWithComputationBudget)bot));        				
        			}
        		} else {
        			bots2.add(bot);
        		}
        	}
        	bots = bots2;
        }        
        
        PrintStream out = new PrintStream(new File("results-PO.txt"));
        
        // Separate the matchs by map:
        List<PhysicalGameState> maps = new LinkedList<PhysicalGameState>();        

        maps.clear();
        maps.add(PhysicalGameState.load("maps/8x8/basesWorkers8x8.xml",utt));
        Experimenter.runExperimentsPartiallyObservable(bots, maps, utt, 10, 3000, 300, true, out);
      
        maps.clear();
        maps.add(PhysicalGameState.load("maps/16x16/basesWorkers16x16.xml",utt));
        Experimenter.runExperimentsPartiallyObservable(bots, maps, utt, 10, 3000, 300, true, out);
    }
}