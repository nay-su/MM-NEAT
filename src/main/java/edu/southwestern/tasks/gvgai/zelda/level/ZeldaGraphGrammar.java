package edu.southwestern.tasks.gvgai.zelda.level;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.datastructures.Graph;
import edu.southwestern.util.datastructures.GraphUtil;
import me.jakerg.rougelike.RougelikeApp;

public class ZeldaGraphGrammar extends GraphRuleManager<ZeldaGrammar> {
	public ZeldaGraphGrammar() {
		super();
		
		GraphRule<ZeldaGrammar> rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.START_S, ZeldaGrammar.ENEMY_S);
		rule.grammar().setStart(ZeldaGrammar.START);
		rule.grammar().setEnd(ZeldaGrammar.ENEMY);
		rule.grammar().addNodeToStart(ZeldaGrammar.ENEMY);
		rule.grammar().addNodeBetween(ZeldaGrammar.NOTHING);
		rule.grammar().addNodeBetween(ZeldaGrammar.ENEMY);
		graphRules.add(rule);
		
//		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.START_S, ZeldaGrammar.ENEMY_S);
//		rule.grammar().setStart(ZeldaGrammar.START);
//		rule.grammar().setEnd(ZeldaGrammar.ENEMY);
//		rule.grammar().addNodeToStart(ZeldaGrammar.ENEMY);
//		graphRules.add(rule);
		
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.START_S, ZeldaGrammar.KEY_S);
		rule.grammar().setStart(ZeldaGrammar.START);
		rule.grammar().setEnd(ZeldaGrammar.KEY);
		rule.grammar().addNodeToStart(ZeldaGrammar.ENEMY);
		rule.grammar().addNodeToStart(ZeldaGrammar.ENEMY);
		rule.grammar().addNodeBetween(ZeldaGrammar.ENEMY);
		graphRules.add(rule);
		
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.START_S, ZeldaGrammar.KEY_S);
		rule.grammar().setStart(ZeldaGrammar.START);
		rule.grammar().setEnd(ZeldaGrammar.KEY);
		rule.grammar().addNodeToStart(ZeldaGrammar.ENEMY);
		rule.grammar().addNodeBetween(ZeldaGrammar.ENEMY);
		graphRules.add(rule);
		
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.START_S, ZeldaGrammar.KEY_S);
		rule.grammar().setStart(ZeldaGrammar.START);
		rule.grammar().setEnd(ZeldaGrammar.KEY);
		rule.grammar().addNodeBetween(ZeldaGrammar.ENEMY);
		graphRules.add(rule);
		
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.KEY_S, ZeldaGrammar.LOCK_S);
		rule.grammar().setStart(ZeldaGrammar.KEY);
		rule.grammar().setEnd(ZeldaGrammar.LOCK);
		rule.grammar().addNodeBetween(ZeldaGrammar.ENEMY);
		graphRules.add(rule);
		
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.KEY_S, ZeldaGrammar.LOCK_S);
		rule.grammar().setStart(ZeldaGrammar.ENEMY);
		rule.grammar().setEnd(ZeldaGrammar.LOCK);
		rule.grammar().addNodeBetween(ZeldaGrammar.KEY);
		graphRules.add(rule);
		
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.KEY_S, ZeldaGrammar.LOCK_S);
		rule.grammar().setStart(ZeldaGrammar.KEY);
		rule.grammar().setEnd(ZeldaGrammar.LOCK);
		rule.grammar().addNodeToStart(ZeldaGrammar.ENEMY);
		graphRules.add(rule);
		
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.KEY_S, ZeldaGrammar.LOCK_S);
		rule.grammar().setStart(ZeldaGrammar.ENEMY);
		rule.grammar().setEnd(ZeldaGrammar.LOCK);
		rule.grammar().addNodeToStart(ZeldaGrammar.KEY);
		rule.grammar().addNodeBetween(ZeldaGrammar.ENEMY);
		graphRules.add(rule);
		
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.KEY_S, ZeldaGrammar.ENEMY_S);
		rule.grammar().setStart(ZeldaGrammar.KEY);
		rule.grammar().setEnd(ZeldaGrammar.ENEMY);
		rule.grammar().addNodeToStart(ZeldaGrammar.ENEMY);
		rule.grammar().addNodeBetween(ZeldaGrammar.ENEMY);
		graphRules.add(rule);
		
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.ENEMY_S, ZeldaGrammar.TREASURE);
		rule.grammar().setStart(ZeldaGrammar.ENEMY);
		rule.grammar().setEnd(ZeldaGrammar.TREASURE);
		rule.grammar().addNodeToStart(ZeldaGrammar.ENEMY);
		rule.grammar().addNodeBetween(ZeldaGrammar.ENEMY);
		graphRules.add(rule);
		
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.ENEMY_S, ZeldaGrammar.TREASURE);
		rule.grammar().setStart(ZeldaGrammar.ENEMY);
		rule.grammar().setEnd(ZeldaGrammar.TREASURE);
		rule.grammar().addNodeBetween(ZeldaGrammar.ENEMY);
		graphRules.add(rule);
		
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.ENEMY_S, ZeldaGrammar.TREASURE);
		rule.grammar().setStart(ZeldaGrammar.ENEMY);
		rule.grammar().setEnd(ZeldaGrammar.TREASURE);
		graphRules.add(rule);
		
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.LOCK_S);
		rule.grammar().setStart(ZeldaGrammar.LOCK);
		graphRules.add(rule);
		
		rule = new GraphRule<ZeldaGrammar>(ZeldaGrammar.KEY_S);
		rule.grammar().setStart(ZeldaGrammar.KEY);
		graphRules.add(rule);
	}
	
	public ZeldaGraphGrammar(File directory) {
		super(directory);
	}
	
	public static void main(String[] args) {
		List<ZeldaGrammar> initialList = new LinkedList<>();
		initialList.add(ZeldaGrammar.START_S);
		initialList.add(ZeldaGrammar.ENEMY_S);
		initialList.add(ZeldaGrammar.KEY_S);
		initialList.add(ZeldaGrammar.LOCK_S);
		initialList.add(ZeldaGrammar.ENEMY_S);
		initialList.add(ZeldaGrammar.TREASURE);
		
		Graph<ZeldaGrammar> graph = new Graph<>(initialList);
		System.out.println("Before size " + graph.size());
		List<Graph<ZeldaGrammar>.Node> traversal = graph.breadthFirstTraversal();
		for(Graph<ZeldaGrammar>.Node n : traversal) {
			System.out.println(n.getData().toString());
		}
		
		System.out.println("\n-----------------------------\n");
		
//		ZeldaGraphGrammar grammar = new ZeldaGraphGrammar();
		ZeldaGraphGrammar grammar = new ZeldaGraphGrammar(new File("data/VGLC/Zelda/rules/1"));
		grammar.applyRules(graph);
		traversal = graph.breadthFirstTraversal();
		for(Graph<ZeldaGrammar>.Node n : traversal) {
			System.out.println(n.getData().toString());
		}

		System.out.println("After size " + graph.size());
		
		try {
			GraphUtil.saveGrammarGraph(graph, "data/VGLC/graph.dot");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Parameters.initializeParameterCollections(new String[] {"zeldaGANUsesOriginalEncoding:false"});
		
		Dungeon d = null;
		try {
			d = GraphUtil.convertToDungeon(graph);
			
			BufferedImage image = GraphUtil.imageOfDungeon(d);
			File file = new File("data/VGLC/Zelda/dungeon.png");
			ImageIO.write(image, "png", file);
			
			Desktop desk = Desktop.getDesktop();
			desk.open(file);
			
			
//			RougelikeApp.startDungeon(d, true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		try {
			grammar.saveRules(new File("data/VGLC/Zelda/rules/1"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
