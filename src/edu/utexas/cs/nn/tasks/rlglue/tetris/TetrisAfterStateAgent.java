package edu.utexas.cs.nn.tasks.rlglue.tetris;

import java.util.ArrayList;
import java.util.HashSet;

import org.rlcommunity.environments.tetris.TetrisState;
import org.rlcommunity.rlglue.codec.types.Action;
import org.rlcommunity.rlglue.codec.types.Observation;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.tasks.rlglue.RLGlueAgent;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;

public class TetrisAfterStateAgent<T extends Network> extends RLGlueAgent<T>{

	public TetrisAfterStateAgent() {
		super();
	}
    
    public Action getAction(Observation o) {
    	//TODO: I think there's an issue with converting Observations to TetrisStates, because there is missing information we cannot get from the obsservation
    	//We need to find a way around this or a way to fix this.
    	
    	//convert Observation to TetrisState
    	TetrisState tempState = new TetrisState();
    	tempState.currentX = o.intArray[o.intArray.length - 5]; // adds the Observation's X to tempState
    	tempState.currentY = o.intArray[o.intArray.length - 4]; // adds the Observation's Y to tempState
    	tempState.currentRotation = o.intArray[o.intArray.length - 3]; // adds the Observation's rotation to tempState
    	for(int p = 0; p < 7; p++){ // sorry for the magic number here -Gab
    		if(o.intArray[tempState.worldState.length + p] == 1){ // this checks for the current block Id
    			tempState.currentBlockId = p;
    		}
    	}
    	
    	for (int i = 0; i < tempState.worldState.length; i++) { // replaces tempState's worldState with the Observation's worldState
    		tempState.worldState[i] = o.intArray[i];
        }

      	//make a Set of evalAfterStates(testState)
    	HashSet<Pair<TetrisState, ArrayList<Integer>>> tetrisStateHolder = TertisAfterStateGenerator.evaluateAfterStates(tempState);
    	
    	ArrayList<Pair<Double, Integer>> outputPairs = new ArrayList<Pair<Double, Integer>>(); //arraylist to hold the actions and outputs for later
    	

    	double[] outputs;
    	//for(pairs in the set){
    	for(Pair<TetrisState, ArrayList<Integer>> i : tetrisStateHolder){
    		
    		//	outputs = constultPolicy(features) REMEMBER outputs is an array of 1
    		double[] inputs = MMNEAT.rlGlueExtractor.extract(i.t1.get_observation()); // TODO: Remove this? -Gab
            
    		for(int j = 0; j < inputs.length - 2; j++) {
    			inputs[j] /= o.intArray[o.intArray.length - 2]; // second part gives us the height of board
    		}
    		inputs[inputs.length - 2] /= tempState.worldState.length;
    		
    		outputs = this.consultPolicy(inputs); //TODO: check and fix this too -Gab

    		//	array(list?).add(outputs[0], first action*) 
            Pair<Double, Integer> tempPair = new Pair<Double, Integer>(outputs[0], i.t2.get(0));
            outputPairs.add(tempPair);
            
    	}
    	
    	double[] outputForArgmax = new double[outputPairs.size()];
    	for(int i = 0; i < outputForArgmax.length; i++){
    		outputForArgmax[i] = outputPairs.get(1).t1;
    	}
		//action = argmax(list)
    	int index = StatisticsUtilities.argmax(outputForArgmax);
    	
    	Action action = new Action(TSO.getNumDiscreteActionDims(), TSO.getNumContinuousActionDims());
    	action.intArray[0]= outputPairs.get(index).t2;
    	return action;
    }
}