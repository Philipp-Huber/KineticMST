package drawings;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Collections;
import java.util.LinkedList;

import javax.swing.JComponent;

import util.UnionFind;

public class GraphDrawing extends JComponent{
	
	private int size;
	private MovingPointSet2D vertices;
	private LinkedList<Integer>[] adjList;
	private LinkedList<Edge> edgeList;
	
	//contains differences to another graph drawing which can be visualized
	private LinkedList<Edge> additionalEdges;
	private LinkedList<Edge> missingEdges;
	
	//statistical data
	private int edgesVisitedGreedy;
	private int edgesVisitedOptimalSlow;
	private int edgesVisitedBranchAndBound;
	private int comparisonsCrossingCriterion;
	
	public GraphDrawing() {
		this.edgeList = new LinkedList<Edge>();
	}
	
	
	public GraphDrawing(MovingPointSet2D vertices) {
		this.vertices = vertices;
		this.size = vertices.getsize();
		this.edgeList = new LinkedList<Edge>();
		
		this.adjList = new LinkedList[this.size];
		//init neighbour entries for all vertices
		for(int i = 0; i < adjList.length; i++) {
			this.adjList[i] = new LinkedList<Integer>();
		}
		this.edgesVisitedGreedy = 0;
		this.edgesVisitedOptimalSlow = 0;
		this.edgesVisitedBranchAndBound = 0;
		this.comparisonsCrossingCriterion = 0;
	}
	
	
	public GraphDrawing(MovingPointSet2D vertices, LinkedList<Edge> edgeList) {
		this.vertices = vertices;
		this.size = vertices.getsize();
		this.edgeList = edgeList;
		
		this.adjList = new LinkedList[this.size];
		//init neighbour entries for all vertices
		for(int i = 0; i < adjList.length; i++) {
			this.adjList[i] = new LinkedList<Integer>();
		}
		this.edgesVisitedGreedy = 0;
		this.edgesVisitedOptimalSlow = 0;
		this.edgesVisitedBranchAndBound = 0;
	}
	
	
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		this.vertices.paint(g2d);
		if(!edgeList.isEmpty()) {
			for(Edge edge : edgeList) {
				edge.getPosition().paint(g2d);
			}
		}
		//paint edges that were found in the compared GraphDrawing but not in this one green
		if(!(missingEdges == null)) {
			for(Edge edge : missingEdges) {
				edge.getPosition().paintColorful(g2d, Color.GREEN);
			}
		}
		
		//paint edges that were not found in the compared GraphDrawing but that exist in this one red
		if(!(additionalEdges == null)) {
			for(Edge edge : additionalEdges) {
				edge.getPosition().paintColorful(g2d, Color.RED);
			}
		}
	}	
	
	
	/**
	 * Adds all edges of a complete graph on the pointset to the edge list
	 */
	public void completeGraph() {
		edgeList = new LinkedList<Edge>();
		LineSegment line;
		for(int i = 0; i < this.size - 1; i++) {
			for(int j = i+1; j < this.size; j++) {
				adjList[i].add(j);
				adjList[j].add(i);
				line = new LineSegment(vertices.getPoint(i), vertices.getPoint(j));
				edgeList.add(new Edge(i, j, line));
			}
		}
	}
	
	/**
	 * Adds all edges of a complete graph on the pointset that do not violate the point criterion to the edge list
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
					adjList[i].add(j);
					adjList[j].add(i);
					edgeList.add(edge);
				}
			}
		}
	}
	
	/**
	 * Uses Kruskal's algorithm to compute a euclidean minimum spanning tree on the initial position of the moving pointset
	 * @return Number of edges visited during computation
	 */	
	public int kruskal() {		
		this.completeGraph();
		return this.kruskalsAlgo(false);		
	}
	
	
	/**
	 * Computes a crossing-stable MST on the moving pointset
	 * @return Number of edges visited during computation
	 */
	public int crossingStableMST() {		
		this.completeGraphSetminusPK();
		return this.kruskalsAlgo(false);
	}
	
	/**
	 * Computes a cheap planar spanning tree on a moving pointset
	 * @return Number of edges visited during computation
	 */
	public int movingKruskal() {		
		this.completeGraphSetminusPK();
		return this.kruskalsAlgo(true);		
	}
	

	/**
	 * Uses Kruskal's algorithm to compute a euclidean minimum spanning tree on the initial position of the moving pointset
	 * @param checkForCrossings Specifies if planarity is enforced explicitly
	 * @throws RuntimeException
	 * @return Number of edges visited during computation
	 */
	private int kruskalsAlgo(boolean checkForCrossings) {
		
		this.edgesVisitedGreedy = 0;		
		//union-find
		UnionFind parents = new UnionFind(this.size);
		//output
		LinkedList<Edge> MST = new LinkedList<Edge>();
		
		Collections.sort(this.edgeList);
		
		boolean success = false;
		while(!edgeList.isEmpty()) {
			Edge nextEdge = edgeList.removeFirst();
			this.edgesVisitedGreedy += 1;
			
			int v = nextEdge.getSrc();
			int w = nextEdge.getDest();
			
			//Assert nextEdge doesn't close cycle AND assert nextEdge is not crossing if specified by checkForCrossings 
			if(!parents.inSameSet(v, w) && (!checkForCrossings || !isCrossing(nextEdge, MST))) {
				MST.add(nextEdge);
				parents.union(v, w);
			}
			if (MST.size() >= this.size - 1) {
				success = true;
				break;
			}
		}
		
		if(!success) {
			throw new RuntimeException("No solution possible");
		}
		
		this.edgeList = MST;
		this.adjList = new LinkedList[this.size];
		for(int i = 0; i < adjList.length; i++) {
			this.adjList[i] = new LinkedList<Integer>();
		}
		int src;
		int dest;
		for(Edge edge : edgeList) {
			src = edge.getSrc();
			dest = edge.getDest();
			adjList[src].add(dest);
			adjList[dest].add(src);
		}
		return this.edgesVisitedGreedy;
	}
	
	
	/**
	 * Brute-forces a planar minimum spanning tree on a moving pointset
	 * @return Number of edges visited during computation
	 */
	public int optimalSlow() {
		return recursiveMST(false);
	}
	
	
	/**
	 * Branch-and-bound algorithm to compute planar minimal spanning tree on the pointset
	 * @return Number of edges visited during computation
	 */
	public int branchAndBound() {
		return recursiveMST(true);
	}
	
	
	/**
	 * Recursively computes a planar minimum spanning tree on a moving pointset
	 * @param BABoptimized Specifies if branch-and-bound should be used for runtime optimization
	 * @return Number of edges visited during computation
	 */
	private int recursiveMST(boolean BABoptimized) {

		this.completeGraphSetminusPK();
		
		Collections.sort(this.edgeList);
		this.edgesVisitedOptimalSlow = 0;
		this.edgesVisitedBranchAndBound = 0;
		this.comparisonsCrossingCriterion = 0;
		
		LinkedList<Edge> currentCandidates = new LinkedList<Edge>();
		for(Edge edge : this.edgeList) {
			currentCandidates.add(edge);
		}
		
		UnionFind parents = new UnionFind(this.size);
		
		LinkedList<Edge> MST = new LinkedList<Edge>();
		LinkedList<Edge> deletedEdges = new LinkedList<Edge>();
		
		//branch-and-bound algo
		if(BABoptimized) {
			MST = recursiveCallMST(MST, currentCandidates, parents, deletedEdges, Double.MAX_VALUE, true);
			this.edgeList = MST;
			
			return this.edgesVisitedBranchAndBound;
		}
		
		//brute force algo
		else {
			MST = recursiveCallMST(MST, currentCandidates, parents, deletedEdges, Double.MAX_VALUE, false);
			this.edgeList = MST;
		
			return this.edgesVisitedOptimalSlow;
		}
	}
	
	
	/**
	 * Recursive procedure to compute a planar minimum spanning tree on a moving pointset
	 * @param MST Partial spanning tree as calculated in calling iteration
	 * @param currentCandidates List of viable candidate edges in the recursive branch
	 * @param unionFind Representation of connected components in the partial spanning tree
	 * @param deletedEdges Edges that were fully explored in other recursion branches
	 * @param lowerBound Weight of current minimal spanning tree for all explored recursion branches
	 * @param BABoptimized Specifies if lower bound is used for branch-and-bound style optimization
	 * @throws RuntimeException
	 * @return Locally minimal spanning tree for this recursive branch or NULL if no viable solution exists
	 */
	private LinkedList<Edge> recursiveCallMST(LinkedList<Edge> MST, LinkedList<Edge> currentCandidates, UnionFind unionFind, LinkedList<Edge> deletedEdges, double lowerBound, boolean BABoptimized) {
		//if ST is complete
		if(MST.size() == this.size-1) {
			return MST;
		}
		
		// if not enough edges left
		if(currentCandidates.size() < (this.size-1)-MST.size()) {
			//System.out.println("Ran out of edges during recursion");
			return null;
		}
		
		if(BABoptimized) {
			//prune recursions that can't beat the current optimum
			double bestCaseCost = 0;
			for (Edge edge : MST) {
				bestCaseCost += edge.getWeight();
			}
			
			for (int i = 0; i < (this.size-1)-MST.size(); i++) {
				bestCaseCost += currentCandidates.get(i).getWeight();
			}
			
			if(bestCaseCost >= lowerBound) {
				return null;
			}
		}
		
		LinkedList<Edge> deleted = new LinkedList<Edge>();
		deleted.addAll(deletedEdges);
		
		if(BABoptimized) {
			this.edgesVisitedBranchAndBound += 1;
		}
		else {
			this.edgesVisitedOptimalSlow += 1;
		}
		
		Edge nextEdge = currentCandidates.removeFirst();
		
		int v = nextEdge.getSrc();
		int w = nextEdge.getDest();
		
		if(!unionFind.inSameSet(v, w)) {
			if(isCrossing(nextEdge, MST)) {
				LinkedList<Edge> ST1 = new LinkedList<Edge>();
				LinkedList<Edge> currentMST = new LinkedList<Edge>();
				currentMST.addAll(MST);
				
				ST1 = recursiveCallMST(MST, currentCandidates, unionFind, deleted, lowerBound, BABoptimized);
				
				double weightST1 = 0;
				if(ST1 == null) {
					weightST1 = Double.MAX_VALUE;
				}
				else {					
					for(Edge edge : ST1) {
						weightST1 += edge.getWeight();
					}
				}
				
				if(BABoptimized) {
					lowerBound = Math.min(lowerBound, weightST1);
				}
				
				//calculate alternative spanning tree

				//remove crossing edges from alternative MST containing the edge
				LinkedList<Edge> ST2 = new LinkedList<Edge>();
				ST2 = nextEdge.nonCrossingEdges(currentMST);
				ST2.add(nextEdge);
				deleted.addAll(nextEdge.crossingEdges(currentMST));
				
				//recalculate unionFind for alternative MST
				UnionFind unionFind2 = new UnionFind(this.size);

				for (Edge edge : ST2) {
					v = edge.getSrc();
					w = edge.getDest();
					if(!unionFind2.inSameSet(v, w)) {
						unionFind2.union(v, w);
					}
					else throw new RuntimeException("Something went horribly wrong");
				}
				
				//recalculate viable candidate edges for alternative MST
				LinkedList<Edge> candidates2 = new LinkedList<Edge>();
				for(Edge edge : this.edgeList) {
					if (!(ST2.contains(edge) || deleted.contains(edge))) {
						//should be sorted
						candidates2.add(edge);					
					}
				}
				
				ST2 = recursiveCallMST(ST2, candidates2, unionFind2, deleted, lowerBound, BABoptimized);
				
				double weightST2 = 0;
				
				if(ST2 == null) {
					weightST2 = Double.MAX_VALUE;
				}
				else {
					for(Edge edge : ST2) {
						weightST2 += edge.getWeight();
					}
				}
				
				//choose more optimal spanning tree
				if(weightST1 <= weightST2) {
					MST = ST1;
				}
				else {
					MST = ST2;
				}
				return MST;
			}

			MST.add(nextEdge);
			unionFind.union(v,w);
			return recursiveCallMST(MST, currentCandidates, unionFind, deleted, lowerBound, BABoptimized);
		}
		
		return recursiveCallMST(MST, currentCandidates, unionFind, deleted, lowerBound, BABoptimized);
	}

	
	/**
	 * Computes a y-monotone path on the pointset
	 * @return The weight of a y-monotone path on the pointset
	 */
	public double yMonotonePath() {
		MovingPointSet2D points = new MovingPointSet2D(this.vertices);
		points.sortY();
		this.vertices = points;
		double weight = 0;
		this.edgeList = new LinkedList<Edge>();
		clearAdjList();
		LineSegment line;
		Edge nextEdge;
		
		for (int i = 0; i < this.size - 1; i++) {
			adjList[i].add(i+1);
			adjList[i+1].add(i);
			line = new LineSegment(vertices.getPoint(i), vertices.getPoint(i+1));
			nextEdge = new Edge(i, i+1, line);
			
			edgeList.add(nextEdge);
			weight += nextEdge.getWeight();
		}
		
		return weight;
	}
	
	/**
	 * Returns true if the argument crosses an edge in the given edge list
	 * @param edge An edge
	 * @param edgeList A list of edges
	 * @return
	 */
	private boolean isCrossing(Edge edge, LinkedList<Edge> edgeList) {
		boolean isCrossing = edge.isCrossing(edgeList);
		this.comparisonsCrossingCriterion += edge.getComparisons();
		return isCrossing;
	}
	
	/**
	 * Compares the object to another instance of GraphDrawing and stores deviations
	 * @param graph2 A graph drawing
	 */
	public void findDifferences(GraphDrawing graph2) {
		LinkedList<Edge> missing = new LinkedList<Edge>();
		LinkedList<Edge> additional = new LinkedList<Edge>();
		LinkedList<Edge> altEdgeList = graph2.getEdgeList();
		boolean found = false;
		for (Edge edge : altEdgeList) {
			found = false;
			for(Edge edge2 : this.edgeList) {
				if(edge.isIdentical(edge2)) {
					found = true;
				}
			}
			if(!found) {
				missing.add(edge);
			}
		}
		for (Edge edge : this.edgeList) {	
			found = false;
			for(Edge edge2 : altEdgeList) {
				if(edge.isIdentical(edge2)) {
					found = true;
				}
			}
			if(!found) {
				additional.add(edge);
			}
		}
		
		this.missingEdges = missing;
		this.additionalEdges = additional;
	}
	
	/**
	 * Returns true if an edge is contained whose endpoints stay coincident with the given points throughout their entire movement
	 * @param a First potential end point
	 * @param b Second potential end point
	 * @return
	 */
	public boolean containsEdge(MovingPoint2D a, MovingPoint2D b) {
		MovingPoint2D start;
			MovingPoint2D end;
		
		for(Edge edge : this.edgeList) {
			start = edge.getPosition().getStartPoint();
			end = edge.getPosition().getEndPoint();
			if (start.coincides(a)) {
				if(end.coincides(b)) {
					return true;
				}
			}
			else if(start.coincides(b)) {
				if(end.coincides(a)) {
					return true;
				}
			}
		}
		return false;
	}

	
	private void clearAdjList() {
		for (int i = 0; i < size; i++) {
			adjList[i] = new LinkedList<Integer>();
		}
	}
	
	/**
	 * Computes the sum total of weights for all edges contained in the graph drawing
	 * @return Weight of graph drawing
	 */
	public double getTotalWeight() {
		double totalWeight = 0;
		for (Edge edge : this.edgeList) {
			totalWeight += edge.getWeight();
		}
		return totalWeight;
	}
	
	public int getEdgesVisitedGreedy() {
		return this.edgesVisitedGreedy;
	}
	
	public int getEdgesVisitedOptimalSlow() {
		return this.edgesVisitedOptimalSlow;
	}
	
	public int getEdgesVisitedBranchAndBound() {
		return this.edgesVisitedBranchAndBound;
	}
	
	public void sortEdges() {
		Collections.sort(this.edgeList);
	}
	
	public LinkedList<Edge>getEdgeList() {
		return this.edgeList;
	}
	
	public int getN() {
		return this.size;
	}
	
	public LinkedList<Edge> getMissingEdges(){
		return this.missingEdges;	
		}

	public int getComparisonsCrossingCriterion() {
		return this.comparisonsCrossingCriterion;
	}
	
}
