package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Data {
	
	private int sampleSize;
	
	private int pointsetSize;
	
	public static String KRUSKAL = "KRUSKAL";
	public static String CROSSING_STABLE = "CROSSING_STABLE";
	public static String MOVING_KRUSKAL = "MOVING_KRUSKAL";
	public static String BRUTE_FORCE = "BRUTE_FORCE";
	public static String BAB = "BAB";
	public static String Y_MONOTONE = "Y_MONOTONE";
	
	//collected data on Kruskal results
	private float[] nonPlanarWeight;
	private int[] nonPlanarEdgesVisited;
	private int nonPlanarPointer;
	
	//collected data on crossingStableMST algorithm
	private float[] crossingStableWeight;
	private int[] crossingStableEdgesVisited;
	private int crossingStablePointer;

	//collected data on movingKruskal algorithm
	private float[] greedyWeight;
	private int[] greedyEdgesVisited;
	private int[] greedyCrossingsConsidered;
	private int greedyPointer;
	
	//collected data on brute force algorithm for planar MST
	private float[] optimalWeight;
	private int[] optimalEdgesVisited;
	private int[] optimalCrossingsConsidered;
	private int optimalPointer;
	
	//collected data on branch-and-bound algorithm for planar MST
	private float[] branchAndBoundWeight;
	private int[] branchAndBoundEdgesVisited;
	private int[] branchAndBoundCrossingsConsidered;
	private int branchAndBoundPointer;
	
	//collected data on y-monotone path weights
	private float[] yMonotoneWeight;
	private int yMonotonePointer;
	
	
	public Data(int sampleSize, int pointsetSize) {
		this.pointsetSize = pointsetSize;
		this.sampleSize = sampleSize;
		
		this.nonPlanarWeight = new float[sampleSize];
		this.crossingStableWeight = new float[sampleSize];
		this.greedyWeight = new float[sampleSize];
		this.optimalWeight = new float[sampleSize];
		this.branchAndBoundWeight = new float[sampleSize];
		
		this.yMonotoneWeight = new float[sampleSize];
		
		this.nonPlanarEdgesVisited = new int[sampleSize];
		this.crossingStableEdgesVisited = new int[sampleSize];
		this.greedyEdgesVisited = new int[sampleSize];
		this.optimalEdgesVisited = new int[sampleSize];
		this.branchAndBoundEdgesVisited = new int[sampleSize];
		
		this.greedyCrossingsConsidered = new int[sampleSize];
		this.optimalCrossingsConsidered = new int[sampleSize];
		this.branchAndBoundCrossingsConsidered = new int[sampleSize];
		
		this.nonPlanarPointer = 0;
		this.crossingStablePointer = 0;
		this.greedyPointer = 0;
		this.optimalPointer = 0;
		this.branchAndBoundPointer = 0;
		this.yMonotonePointer = 0;
	}
	
	/**
	 * Adds the measured weight of a data point for the specified class of entries. Some entry classes may have attributes other than weight, which default to 0 if left unspecified.
	 * @param entry Class of the entry
	 * @param weight Measured weight
	 */
	public void addEntry(String entry, double weight) {
		switch(entry) {
		case "KRUSKAL":
			this.nonPlanarWeight[this.nonPlanarPointer] = (float) weight;
			this.nonPlanarPointer++;
			break;
			
		case "CROSSING_STABLE":
			this.crossingStableWeight[this.crossingStablePointer] = (float) weight;
			this.crossingStablePointer++;
			break;
			
		case "MOVING_KRUSKAL":
			this.greedyWeight[this.greedyPointer] = (float) weight;
			this.greedyPointer++;
			break;
			
		case "BRUTE_FORCE":
			this.optimalWeight[this.optimalPointer] = (float) weight;
			this.optimalPointer++;
			break;
		
		case "BAB":
			this.branchAndBoundWeight[this.branchAndBoundPointer] = (float) weight;
			this.branchAndBoundPointer++;
			break;
			
		case "Y_MONOTONE":
			this.yMonotoneWeight[this.yMonotonePointer] = (float) weight;
			this.yMonotonePointer++;
			break;
			
		default:
			throw new IllegalArgumentException("Entry denominator unknown");
		}
	}
	
	/**
	 * Adds the measured weight and visited edges of a data point for the specified class of entries. Some entry classes may have attributes other than weight and visited edges, which default to 0 if left unspecified.
	 * @param entry Class of the entry
	 * @param weight Measured weight
	 * @param edgesVisited Number of edges visited during the computation
	 */
	public void addEntry(String entry, double weight, int edgesVisited) {
		switch(entry) {
		case "KRUSKAL":
			this.nonPlanarWeight[this.nonPlanarPointer] = (float) weight;
			this.nonPlanarEdgesVisited[this.nonPlanarPointer] = edgesVisited;
			this.nonPlanarPointer++;
			break;
			
		case "CROSSING_STABLE":
			this.crossingStableWeight[this.crossingStablePointer] = (float) weight;
			this.crossingStableEdgesVisited[this.crossingStablePointer] = edgesVisited;
			this.crossingStablePointer++;
			break;
			
		case "MOVING_KRUSKAL":
			this.greedyWeight[this.greedyPointer] = (float) weight;
			this.greedyEdgesVisited[this.greedyPointer] = edgesVisited;
			this.greedyPointer++;
			break;
			
		case "BRUTE_FORCE":
			this.optimalWeight[this.optimalPointer] = (float) weight;
			this.optimalEdgesVisited[this.optimalPointer] = edgesVisited;
			this.optimalPointer++;
			break;
		
		case "BAB":
			this.branchAndBoundWeight[this.branchAndBoundPointer] = (float) weight;
			this.branchAndBoundEdgesVisited[this.branchAndBoundPointer] = edgesVisited;
			this.branchAndBoundPointer++;
			break;
			
		default:
			throw new IllegalArgumentException("Entry denominator unknown or incompatible with one or more parameters");
			}
	}
	
	/**
	 * Adds the measured weight, visited edges and considered crossings of a data point for the specified class of entries.
	 * @param entry Class of the entry
	 * @param weight Measured weight
	 * @param edgesVisited Number of edges visited during the computation
	 * @param crossingsConsidered Number of considered Crossings during the computation
	 */
	public void addEntry(String entry, double weight, int edgesVisited, int crossingsConsidered) {
		switch(entry) {
		
		case "MOVING_KRUSKAL":
			this.greedyWeight[this.greedyPointer] = (float) weight;
			this.greedyEdgesVisited[this.greedyPointer] = edgesVisited;
			this.greedyCrossingsConsidered[this.greedyPointer] = crossingsConsidered;
			this.greedyPointer++;
			break;
			
		case "BRUTE_FORCE":
			this.optimalWeight[this.optimalPointer] = (float) weight;
			this.optimalEdgesVisited[this.optimalPointer] = edgesVisited;
			this.optimalCrossingsConsidered[this.optimalPointer] = crossingsConsidered;
			this.optimalPointer++;
			break;
		
		case "BAB":
			this.branchAndBoundWeight[this.branchAndBoundPointer] = (float) weight;
			this.branchAndBoundEdgesVisited[this.branchAndBoundPointer] = edgesVisited;
			this.branchAndBoundCrossingsConsidered[this.branchAndBoundPointer] = crossingsConsidered;
			this.branchAndBoundPointer++;
			break;
			
		default:
			throw new IllegalArgumentException("Entry denominator unknown or incompatible with one or more parameters");
		}
	}

	/**
	 * Prints all contained data to the file in specified path
	 * @param path Destination for the output file
	 * @throws FileNotFoundException
	 */
	public void printToFile(String path) throws FileNotFoundException {
		File file = new File(path);
		PrintWriter toFile = new PrintWriter(file);
			toFile.println("Test data for pointsets of size " + this.pointsetSize + ", sample size: " + this.sampleSize);
			toFile.println();
			toFile.println("Weight Comparison,,,,,,,Edges Visited Comparison,,,,,,Crossings Considered Comparison");
			toFile.println("Non-Planar MST,Crossing-Stable MST,Moving Kruskal,Brute Force MST,Branch-and-Bound MST,y-Monotone Path,,Non-Planar MST,Crossing-Stable MST,Moving Kruskal,Brute Force MST,Branch-and-Bound MST,,Moving Kruskal,Brute Force MST,Branch-and-Bound MST");
			
			String dataPoint;
			
			for (int i = 0; i < sampleSize; i++) {
				dataPoint = "";
				dataPoint += (this.nonPlanarWeight[i] + ",");
				dataPoint += (this.crossingStableWeight[i] + ",");
				dataPoint += (this.greedyWeight[i] + ",");
				dataPoint += (this.optimalWeight[i] + ",");
				dataPoint += (this.branchAndBoundWeight[i] + ",");
				dataPoint += (this.yMonotoneWeight[i]);
				dataPoint += ",,";
				dataPoint += (this.nonPlanarEdgesVisited[i] + ",");
				dataPoint += (this.crossingStableEdgesVisited[i] + ",");
				dataPoint += (this.greedyEdgesVisited[i] + ",");
				dataPoint += (this.optimalEdgesVisited[i] + ",");
				dataPoint += (this.branchAndBoundEdgesVisited[i]);
				dataPoint += ",,";
				dataPoint += (this.greedyCrossingsConsidered[i] + ",");
				dataPoint += (this.optimalCrossingsConsidered[i] + ",");
				dataPoint += (this.branchAndBoundCrossingsConsidered[i] + ",");
				
				toFile.println(dataPoint);
			}
			toFile.close();
	}

}
