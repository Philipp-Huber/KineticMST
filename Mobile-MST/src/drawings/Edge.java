package drawings;

import java.util.LinkedList;

public class Edge implements Comparable<Edge>{

	private int src;
	private int dest;
	private LineSegment position;
	private double weight;
	private int comparisons;
	
	public Edge() {
		
	}
	
	public Edge(int src, int dest, LineSegment position) {
		this.src = src;
		this.dest = dest;
		this.position = position;
		
		//set the weight to the euclidean length of the edge at t=0
		this.weight = position.getStartingLength();
	}	
	
	public Edge(int src, int dest, LineSegment position, double weight) {
		this.src = src;
		this.dest = dest;
		this.position = position;
		this.weight = weight;
	}	
	
	public Edge(MovingPoint2D p1, MovingPoint2D p2) {
		this.position = new LineSegment(p1, p2);
	}
	
	public Edge(Edge another) {
		this.src = another.getSrc();
		this.dest = another.getDest();
		this.position = new LineSegment(another.getPosition());
		this.weight = this.position.getStartingLength();
	}
	
	/**
	 * Sets the swept area of an edge as its weight
	 */
	public void areaAsWeight() {
		this.weight = this.position.coveredArea();
	}
	
	/**
	 * Returns true if edge is crossing at least one edge given in the argument edgeList
	 * @param edgeList List of potentially crossing edges
	 * @return true if edge has at least one crossing with given edges, false otherwise
	 */
	public boolean isCrossing(LinkedList<Edge> edgeList) {
		this.comparisons = 0;
		for (Edge edge : edgeList) {
			comparisons++;
			if (isCrossing(edge)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns all edges in the argument edgeList that do not cross the edge
	 * @param edgeList List of potentially crossing edges
	 * @return Subset of given edges that do not cross this edge
	 */
	public LinkedList<Edge> nonCrossingEdges(LinkedList<Edge> edgeList){
		LinkedList<Edge> nonConflicting = new LinkedList<Edge>();
		for(Edge edge : edgeList) {
			if (!isCrossing(edge)) {
				nonConflicting.add(edge);
			}
		}
		return nonConflicting;
	}
	
	/**
	 * Returns all edges in the argument edgeList that cross the edge
	 * @param edgeList List of potentially crossing edges
	 * @return Subset of given edges that cross this edge
	 */
	public LinkedList<Edge> crossingEdges(LinkedList<Edge> edgeList){
		LinkedList<Edge> conflicting = new LinkedList<Edge>();
		for(Edge edge : edgeList) {
			if (isCrossing(edge)) {
				conflicting.add(edge);
			}
		}
		return conflicting;
	}
	
	/**
	 * Returns whether the argument crosses the edge
	 * @param e2 Another edge
	 * @return true if e2 is crossing this edge, false otherwise
	 */
	public boolean isCrossing(Edge e2) {
		return this.position.isCrossing(e2.getPosition());
	}
	
	/**
	 * Returns true if both edges are coincident in their initial position and their final position
	 * @param e2 Another edge
	 * @return true if both edges are coincident throughout their movement, false otherwise
	 */
	public boolean isIdentical(Edge e2) {
		return ((this.src == e2.getSrc() && this.dest == e2.getDest()) || (this.dest == e2.getSrc() && this.src == e2.getDest()));
	}
	
	
	public int getSrc() {
		return src;
	}

	public void setSrc(int src) {
		this.src = src;
	}

	public int getDest() {
		return dest;
	}

	public void setDest(int dest) {
		this.dest = dest;
	}

	public LineSegment getPosition() {
		return position;
	}

	public void setPosition(LineSegment position) {
		this.position = position;
	}
	
	public double getWeight(){
		return this.weight;
	}
	
	public int getComparisons() {
		return this.comparisons;
	}
	
	public int compareTo(Edge compareEdge) 
    { 
        if (this.weight < compareEdge.getWeight()) {
        	return -1;
        }
        if (this.weight == compareEdge.getWeight()) {
        	return 0;
        }
        else
        	return 1;
    } 
	
	
}
