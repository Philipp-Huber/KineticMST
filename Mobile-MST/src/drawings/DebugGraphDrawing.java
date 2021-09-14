package drawings;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Collections;
import java.util.LinkedList;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class DebugGraphDrawing extends JComponent {

	private int size;
	private MovingPointSet2D vertices;
	private LinkedList<Edge> edgeList;
	private LinkedList<Edge> currentMST;
	private LinkedList<Edge> deletedEdges;
	private LinkedList<Edge> conflictEdges;
	private LinkedList<Edge> bestMST;
	private Edge currentCandidate;
	
	
	public DebugGraphDrawing(MovingPointSet2D vertices) {
		this.vertices = vertices;
		this.size = vertices.getsize();
		this.edgeList = new LinkedList<Edge>();
		this.currentMST = new LinkedList<Edge>();
		this.deletedEdges = new LinkedList<Edge>();
		this.conflictEdges = new LinkedList<Edge>();
		this.bestMST = new LinkedList<Edge>();
	}
	
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		this.vertices.paint(g2d);
		
		if(bestMST != null) {
			for(Edge edge : bestMST) {
				edge.getPosition().paintColorful(g2d, Color.BLUE);
			}
		}		
		if(edgeList != null) {
			for(Edge edge : currentMST) {
				edge.getPosition().paint(g2d);
			}
		}
		if(deletedEdges != null) {
			for(Edge edge : deletedEdges) {
				edge.getPosition().paintColorful(g2d, Color.RED);
			}
		}
		if(conflictEdges != null) {
			for(Edge edge : conflictEdges) {
				edge.getPosition().paintColorful(g2d, Color.RED);
			}
		}
	
		if(currentCandidate != null) {
			currentCandidate.getPosition().paintColorful(g2d, Color.GREEN);
		}
	}
	
	
	/**
	 * Adds all edges of a complete graph on the pointset that do not violate the point criterion
	 */
	public void completeGraphSetminusPK() {
		edgeList = new LinkedList<Edge>();
		LineSegment line;
		Edge edge;
		for(int i = 0; i < this.size - 1; i++) {
			for(int j = i+1; j < this.size; j++) {
				line = new LineSegment(vertices.getPoint(i), vertices.getPoint(j));
				edge = new Edge(i, j, line);
				if(!this.vertices.violatesPointCriterion(edge.getPosition())) {
					edgeList.add(edge);
				}
			}
		}
	}
	
	/**
	 * Implements the branch-and-bound algorithm for planar MSTs on the pointset and visualizes its progress
	 */
	public void branchAndBound() {
		this.completeGraphSetminusPK();
		Collections.sort(this.edgeList);
		LinkedList<Edge> currentCandidates = new LinkedList<Edge>();

		for(Edge edge : this.edgeList) {
			currentCandidates.add(edge);
		}
		
		//union-find
		int[] parents = new int[this.size];
		for (int i = 0; i < parents.length; i++) {
			parents[i] = -1;
		}
		
		LinkedList<Edge> MST = new LinkedList<Edge>();
		LinkedList<Edge> deletedEdges = new LinkedList<Edge>();
		
		//visualization
		JFrame window = new JFrame();
		window.setSize(1500, 1500);
		window.setTitle("Debug Window");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		window.add(this);
		
		MST = branchAndBoundRecursive(MST, currentCandidates, parents, deletedEdges, Double.MAX_VALUE, window);
		this.currentMST = MST;
		window.repaint();
		window.revalidate();
	}
	
	
	private LinkedList<Edge> branchAndBoundRecursive(LinkedList<Edge> MST, LinkedList<Edge> currentCandidates, int[] unionFind, LinkedList<Edge> deletedEdges , double lowerBound, JFrame window) {
		//if done
		if(MST.size() == this.size-1) {
			System.out.println("MST has weight: " + this.getTotalWeight());
			return MST;
		}
		// if not enough edges left
		if(currentCandidates.size() < (this.size-1)-MST.size()) {
			System.out.println("Ran out of edges during recursion");
			return null;
		}
		//prune recursions that can't beat the current optimum
		double bestCaseCost = 0;
		for (Edge edge : MST) {
			bestCaseCost += edge.getWeight();
		}
		for (int i = 0; i < (this.size-1)-MST.size(); i++) {
			bestCaseCost += currentCandidates.get(i).getWeight();
		}
		if(bestCaseCost >= lowerBound) {
			System.out.println("Pruning branch");
			return null;
		}
		
		LinkedList<Edge> deleted = new LinkedList<Edge>();
		deleted.addAll(deletedEdges);
		this.currentMST = MST;
		this.deletedEdges = deleted;
		window.repaint();
		window.revalidate();
		
		Edge nextEdge = currentCandidates.removeFirst();
		
		this.currentCandidate = nextEdge;
 		window.repaint();
		window.revalidate();
		
		int v = getRepresentative(unionFind, nextEdge.getSrc());
		int w = getRepresentative(unionFind, nextEdge.getDest());
		
		if(v != w) {
			if(nextEdge.isCrossing(MST)) {
				LinkedList<Edge> ST1 = new LinkedList<Edge>();
				LinkedList<Edge> currentMST = new LinkedList<Edge>();
				currentMST.addAll(MST);
				ST1 = branchAndBoundRecursive(MST, currentCandidates, unionFind, deleted, lowerBound, window);
				window.repaint();
				window.revalidate();
				
				double weightST1 = 0;
				if(ST1 == null) {
					weightST1 = Double.MAX_VALUE;
				}
				else {
					
					for(Edge edge : ST1) {
						weightST1 += edge.getWeight();
					}
				}
				
				lowerBound = Math.min(lowerBound, weightST1);

				//remove crossing edges from alternative MST containing the edge
				LinkedList<Edge> ST2 = new LinkedList<Edge>();
				ST2 = nextEdge.nonCrossingEdges(currentMST);
				ST2.add(nextEdge);
				deleted.addAll(nextEdge.crossingEdges(currentMST));
				this.deletedEdges = deleted;
				window.repaint();
				window.revalidate();
				
				//recalculate unionFind for alternative MST
				int[] unionFind2 = new int[this.size];
				for (int i = 0; i < unionFind2.length; i++) {
					unionFind2[i] = -1;
				}
				for (Edge edge : ST2) {
					v = getRepresentative(unionFind2, edge.getSrc());
					w = getRepresentative(unionFind2, edge.getDest());
					if(v != w) {
						union(unionFind2, v, w);
					}
					else System.out.println("Something went horribly wrong");
				}
				
				//recalculate viable candidate edges for alternative MST
				LinkedList<Edge> candidates2 = new LinkedList<Edge>();
				for(Edge edge : this.edgeList) {
					if (!(ST2.contains(edge) || deleted.contains(edge))) {
						//should be sorted
						candidates2.add(edge);					
					}
				}
				
				ST2 = branchAndBoundRecursive(ST2, candidates2, unionFind2, deleted, lowerBound, window);
				
				double weightST2 = 0;
				
				if(ST2 == null) {
					weightST2 = Double.MAX_VALUE;
				}
				else {
					for(Edge edge : ST2) {
						weightST2 += edge.getWeight();
					}
				}
				if(weightST1 <= weightST2) {
					MST = ST1;
				}
				else {
					MST = ST2;
				}
				return MST;
			}
			MST.add(nextEdge);
			union(unionFind, v,w);
			return branchAndBoundRecursive(MST, currentCandidates, unionFind, deleted, lowerBound, window);
		}
		return branchAndBoundRecursive(MST, currentCandidates, unionFind, deleted, lowerBound, window);
	}


	//Union-Find functionality
	private static int getRepresentative(int[] parents, int child) {
		if (parents[child] == -1) {
			return child;
		}
		return getRepresentative(parents, parents[child]);
	}
	
	private static void union(int[] parents, int src, int dest) {
		int rep1 = getRepresentative(parents, src);
		int rep2 = getRepresentative(parents, dest);
		parents[rep1] = rep2;
	}
	
	public void setBestMST(LinkedList<Edge> MST) {
		this.bestMST = MST;
	}
	
	public double getTotalWeight() {
		double totalWeight = 0;
		for (Edge edge : this.edgeList) {
			totalWeight += edge.getWeight();
		}
		return totalWeight;
	}
	
}
