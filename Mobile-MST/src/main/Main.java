package main;

import java.io.FileNotFoundException;
import java.util.Random;

import javax.swing.JFrame;

import drawings.DebugGraphDrawing;
import drawings.GraphDrawing;
import drawings.MovingPointSet2D;
import util.Data;



public class Main {
	
	public static void main(String[] args) throws FileNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
	

		//////////////////////////////////////////////////////////////////////////////////////////////////
		                                 //Should statistical evaluation be performed?
										 boolean statistics = true;
										 
										 //Should a custom program be run?
		                                 boolean testChamber = false;
		                    			 
		                                 //Should computed spanning trees be shown graphically?
		                                 boolean visualisation = true;
		                                 
		                                 //Should the Branch-and-Bound algorithm be visualized step by step?
		                                 //(Debug Mode recommended)
		                                 boolean watchBaB = false;
		                                 
		                                 //How many samples should be examined?
		                    			 int sampleSize = 11044;
		                    			 
		                    			 //What should the pointset size n be sat as?
		                    			 int pointsetSize = 10;
		                    			 
		                    			 //Should statistical data be saved to a file (CSV format)?
		                    			 boolean printToFile = true;
		                    			 
		                    			 //Path at which data is printed if "printToFile" is set to true
		                    			 String path = "C:\\Users\\Philipp\\Desktop\\test.csv";
		                    			 
		//////////////////////////////////////////////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////////////////////
		
		
		//initialization
		
		// seed can be chosen arbitrarily. Accompanying raw data was produced using seed 13052021 //
		long seed = 13052021;
		Random random = new Random(
				seed
				);
		
		//sample pointset
		MovingPointSet2D pointset = new MovingPointSet2D(pointsetSize);
		Data data = new Data(sampleSize, pointsetSize);
		
		GraphDrawing nonPlanarMST = null;
		GraphDrawing crossingStable = null;
		GraphDrawing greedy = null;
		GraphDrawing bruteForce = null;
		GraphDrawing branchAndBound = null;
		GraphDrawing yMono = null;
		
		//number of edges visited in each algorithm
		int nonPlanarEdgesConsidered = 0;
		int crossingStableEdgesConsidered = 0;
		int greedyEdgesConsidered = 0;
		int bruteForceEdgesConsidered = 0;
		int branchAndBoundEdgesConsidered = 0;
		
		//number of comparisons required to maintain planarity
		int greedyCrossingsConsidered = 0;
		int bruteForceCrossingsConsidered = 0;
		int branchAndBoundCrossingsConsidered = 0;
		
		//weight of the spanning tree produced by each algorithm
		double nonPlanarWeight = 0;
		double crossingStableWeight = 0;
		double greedyWeight = 0;
		double bruteForceWeight = 0;
		double branchAndBoundWeight = 0;
		double yMonotoneWeight = 0;
		
		//statistical evaluation
		if (statistics) {
			for (int i = 1; i <= sampleSize; i++) {
				float progressPercent = ((float) i/ (float) sampleSize)*100;
				if(progressPercent == (int) progressPercent) {
					System.out.println("Sampling " + progressPercent + "% completed");
				}

				pointset.randomizeMovingPointSet(random);
				pointset.enforceGeneralPosition(random);
				
				nonPlanarMST = new GraphDrawing(pointset);
				nonPlanarEdgesConsidered = nonPlanarMST.kruskal();
				nonPlanarWeight = nonPlanarMST.getTotalWeight();
				
				data.addEntry(Data.KRUSKAL, nonPlanarWeight, nonPlanarEdgesConsidered);
				
				crossingStable = new GraphDrawing(pointset);
				crossingStableEdgesConsidered = crossingStable.crossingStableMST();
				crossingStableWeight = crossingStable.getTotalWeight();
				
				data.addEntry(Data.CROSSING_STABLE, crossingStableWeight, crossingStableEdgesConsidered);
				
				greedy = new GraphDrawing(pointset);
				greedyEdgesConsidered = greedy.movingKruskal();
				greedyCrossingsConsidered = greedy.getComparisonsCrossingCriterion();
				greedyWeight = greedy.getTotalWeight();
				
				data.addEntry(Data.MOVING_KRUSKAL, greedyWeight, greedyEdgesConsidered, greedyCrossingsConsidered);
				
				bruteForce = new GraphDrawing(pointset);
				bruteForceEdgesConsidered = bruteForce.optimalSlow();
				bruteForceCrossingsConsidered = bruteForce.getComparisonsCrossingCriterion();
				bruteForceWeight = bruteForce.getTotalWeight();
				
				data.addEntry(Data.BRUTE_FORCE, bruteForceWeight, bruteForceEdgesConsidered, bruteForceCrossingsConsidered);
				
				branchAndBound = new GraphDrawing(pointset);
				branchAndBoundEdgesConsidered = branchAndBound.branchAndBound();
				branchAndBoundCrossingsConsidered = branchAndBound.getComparisonsCrossingCriterion();
				branchAndBoundWeight = branchAndBound.getTotalWeight();
				
				data.addEntry(Data.BAB, branchAndBoundWeight, branchAndBoundEdgesConsidered, branchAndBoundCrossingsConsidered);
				
				if(watchBaB) {
					DebugGraphDrawing debug = new DebugGraphDrawing(pointset);
					debug.setBestMST(bruteForce.getEdgeList());
					debug.branchAndBound();
				}
				
				yMono = new GraphDrawing(pointset);
				yMonotoneWeight = yMono.yMonotonePath();
				
				data.addEntry(Data.Y_MONOTONE, yMonotoneWeight);
				
			}
			if(printToFile) {
				data.printToFile(path);
			}
			
		}
		
		//
		//---------------------------------------------------------------------------------------------------------------------------------
		//
		
		else if(testChamber) {
		
			//Code block for running custom programs
			
		}
		
		//
		//---------------------------------------------------------------------------------------------------------------------------------
		//	
		
		if(visualisation) {
			
			JFrame window = new JFrame();
			window.setSize(1500, 1500);
			window.setTitle("Non-planar MST");
			window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			window.setVisible(true);
			window.add(nonPlanarMST);
			
			//uncomment to visualize deviations from the planar MST
			crossingStable.findDifferences(bruteForce);
			
			JFrame window1 = new JFrame();
			window1.setSize(1500, 1500);
			window1.setTitle("Crossing-Stable MST");
			window1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			window1.setVisible(true);
			window1.add(crossingStable);
			
			
			//uncomment to visualize deviations from the planar MST
			greedy.findDifferences(bruteForce);
			
			JFrame window2 = new JFrame();
			window2.setSize(1500, 1500);
			window2.setTitle("Moving Kruskal");
			window2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			window2.setVisible(true);
			window2.add(greedy);
			
			JFrame window3 = new JFrame();
			window3.setSize(1500, 1500);
			window3.setTitle("Optimal");
			window3.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			window3.setVisible(true);
			window3.add(bruteForce);
			
			JFrame window4 = new JFrame();
			window4.setSize(1500, 1500);
			window4.setTitle("OptimalBaB");
			window4.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			window4.setVisible(true);
			window4.add(branchAndBound);
			
			JFrame window5 = new JFrame();
			window5.setSize(1500, 1500);
			window5.setTitle("Y-monotone Path");
			window5.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			window5.setVisible(true);
			window5.add(yMono);
			
		}	
	}
	
}