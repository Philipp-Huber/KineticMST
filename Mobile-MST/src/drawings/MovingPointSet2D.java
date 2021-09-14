package drawings;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;
import java.util.stream.IntStream;

import javax.swing.JComponent;

public class MovingPointSet2D extends JComponent{
	
	//should be an even number
	static final int pointRad = 2*2;
	
	//bounds for the coordinates of the contained points
	private final int xLeftBound = 500;
	private final int xRightBound = 1000;
	private final int yLowerBound = 0;
	private final int yUpperBound = 500;
	
	//bounds on the movement vectors of the points
	private final int dxLower = -500;
	private final int dxUpper = 500;
	private final int dyLower = 0;
	private final int dyUpper = 0;
	
	private int size;
	private MovingPoint2D[] points;
	
	public MovingPointSet2D() {
		this.size = 0;
	}
	
	public MovingPointSet2D(int size) {
		this.size = size;
		this.points = new MovingPoint2D[size];
	}
	
	public MovingPointSet2D(MovingPoint2D[] points) {
		this.points = points;
		this.size = points.length;
	}
	
	public MovingPointSet2D(MovingPointSet2D other) {
		MovingPoint2D[] otherPoints = other.getPoints();
		this.size = otherPoints.length;
		this.points = new MovingPoint2D[this.size];
		for(int i = 0; i < this.size; i++) {
			this.points[i] = new MovingPoint2D(otherPoints[i]);
		}
	}

	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		int xMoved;
		int yMoved;
		for(int i = 0; i < this.size; i++) {
			if(!(points[i] == null)) {
				g2d.setColor(Color.BLACK);
				g2d.drawOval(points[i].getxCoord(), points[i].getyCoord(), pointRad, pointRad);
				xMoved = points[i].getxCoord() + points[i].getxMovement();
				yMoved = points[i].getyCoord() + points[i].getyMovement();
				g2d.setColor(Color.BLUE);
				g2d.drawOval(xMoved, yMoved, pointRad, pointRad);
				g2d.setColor(Color.LIGHT_GRAY);
				g2d.drawLine(points[i].getxCoord() + pointRad/2, points[i].getyCoord() + pointRad/2, xMoved + pointRad/2, yMoved + pointRad/2);
			}
		}
	}
	
	/**
	 * Randomize all points in the pointsets according within the specified bounds
	 * @param random Randomness generator
	 */
	public void randomizeMovingPointSet(Random random) {
		for(int i = 0; i < points.length; i++) {
			points[i] = new MovingPoint2D();
			points[i].randomizePoint(xLeftBound, xRightBound, yLowerBound, yUpperBound, dxLower, dxUpper, dyLower, dyUpper, random);
		}
	}
	
	/**
	 * Add moving point to the pointset
	 * @param p Moving Point
	 */
	public void addPoint(MovingPoint2D p) {
		MovingPoint2D[] newSet = new MovingPoint2D[this.size + 1];
		for(int i = 0; i < this.size; i++) {
			newSet[i] = this.points[i];
		}
		newSet[this.size] = p;
		this.size += 1;
		this.points = newSet;
	}
	
	
	/**
	 * Brute forces general position by randomizing identical/collinear points
	 * @param random Randomness generator
	 */
	public void enforceGeneralPosition(Random random) {
		for (int i = 0; i < points.length-2; i++) {
			for (int j = i+1; j < points.length-1; j++) {
				//enforce unique y-values
				if (points[i].getyCoord() == points[j].getyCoord()) {
					points[j].randomizePoint(xLeftBound, xRightBound, yLowerBound, yUpperBound, dxLower, dxUpper, dyLower, dyUpper, random);
					i = -1;
				}
				if (i == -1) {
					break;
				}
				for (int k = j+1; k < points.length; k++) {
					if(points[j].getyCoord() == points[k].getyCoord() || points[i].getyCoord() == points[k].getyCoord()) {
						points[k].randomizePoint(xLeftBound, xRightBound, yLowerBound, yUpperBound, dxLower, dxUpper, dyLower, dyUpper, random);
						i = -1;
						break;
					}
					double angle = points[i].angle(points[j], points[k]);
					if(angle == 0 || angle == 180 || angle == -180) {
						points[k].randomizePoint(xLeftBound, xRightBound, yLowerBound, yUpperBound, dxLower, dxUpper, dyLower, dyUpper, random);
						i = -1;
						break;
					}
				}
				if (i == -1) {
					break;
				}
			}
		}
	}
	
	/**
	 * Generates a pointset that forces bad results with MovingKruskal
	 * @param iterations
	 */
	public void iterativeWorstCase(int iterations) {
		
		MovingPointSet2D evilSet = new MovingPointSet2D();
		
		MovingPoint2D point1;
		MovingPoint2D point2;
		MovingPoint2D point3;
		MovingPoint2D point4;
		MovingPoint2D point5;
		MovingPoint2D point6;
		MovingPoint2D point7;
		MovingPoint2D point8;
		
		
		for (int i = 0; i < iterations; i++) {
			point1 = new MovingPoint2D(350, 70 + (i*70), 0, 0);
			point2 = new MovingPoint2D(355, 40 + (i*70), 0, 0);
			point3 = new MovingPoint2D(350, 0 + (i*70), 0, 0);
			point4 = new MovingPoint2D(300, 40 + (i*70), 0, 0);
			
			point5 = new MovingPoint2D(10, 20 + (i*70), 338, 0);
			point6 = new MovingPoint2D(690, 20 + (i*70), -338, 0);
			point7 = new MovingPoint2D(10, 40 + (i*70), 0, 0);
			point8 = new MovingPoint2D(690, 40 + (i*70), 0, 0);
			
			evilSet.addPoint(point1);
			evilSet.addPoint(point2);
			evilSet.addPoint(point3);
			evilSet.addPoint(point4);
			evilSet.addPoint(point5);
			evilSet.addPoint(point6);
			evilSet.addPoint(point7);
			evilSet.addPoint(point8);
		}
		
		this.points = evilSet.getPoints();
		this.size = evilSet.getsize();
	}
	
	/**
	 * Adds a set of moving points to the pointset
	 * @param points Number of moving points
	 */
	public void addPoints(MovingPoint2D[] points) {
		this.size += points.length;
		MovingPoint2D[] newSet = new MovingPoint2D[size];
		int counter = 0;
		while (counter < points.length) {
			newSet[counter] = points[counter];
			counter++;
		}
		for (int i = 0; i < this.points.length; i++) {
			newSet[counter + i] = this.points[i];
		}
		this.points = newSet;
		
	}
	
	/**
	 * Subtracts MovingPoints from the MovingPointset (all points must be contained)
	 * @param set Subset of the pointset
	 * @throws IllegalArgumentException
	 * @return Subset of the pointset that does not contain any of the given points
	 */
	public MovingPoint2D[] setminus(MovingPoint2D[] set) {
		boolean isValid = true;
		for(int i = 0; i < set.length; i++) {
			if (!(Arrays.asList(this.points).contains(set[i]))){
				isValid = false;
				break;
			}
		}
		if(!isValid){
			throw new IllegalArgumentException("Argument is no subset of this MovingPointset2D");
			}
		MovingPoint2D[] setDifference = new MovingPoint2D[this.points.length - set.length];
		int openSpot = 0;
		for(int i = 0; i < this.points.length; i++) {
			if(!(Arrays.asList(set)).contains(this.points[i])) {
				setDifference[openSpot] = this.points[i];
				openSpot++;
			}
		}
		return setDifference;
	}
	
	/**
	 * Determines if any point of the pointset crosses a given line segment
	 * @param line Line Segment with moving endpoints
	 * @return True if at least one moving point crosses the line segment, false otherwise
	 */
	public boolean violatesPointCriterion(LineSegment line) {
		for(int i = 0; i < this.size; i++) {
			if (points[i].compareTo(line.getStartPoint()) != 0 && points[i].compareTo(line.getEndPoint()) != 0) {
				if(line.violatesPointCriterion(points[i])) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Sorts the contained points by their y-axis position
	 */
	public void sortY() {
		Arrays.sort(this.points);
		}
	
	/**
	 * Retrieves the moving point at the specified index in the pointset
	 * @param number Index of point in the pointset
	 * @throws IndexOutOfBoundsException
	 * @return Point at the specified index in the pointset
	 */
	public MovingPoint2D getPoint(int number) {
		if(0 <= number && number < size) {
			return points[number];
		}
		else
			throw new IndexOutOfBoundsException("The specified index lies out of range for the indices of points contained in this pointset");
	}
	
	/**
	 * Set the size of this pointset. This removes all points!
	 * @param size Target size for the pointset
	 */
	public void setSize(int size) {
		this.size = size;
		this.points = new MovingPoint2D[size];
	}

	public int getxLeftBound() {
		return xLeftBound;
	}
	public int getxRightBound() {
		return xRightBound;
	}
	public int getyLowerBound() {
		return yLowerBound;
	}
	public int getyUpperBound() {
		return yUpperBound;
	}

	public int getDxLower() {
		return dxLower;
	}

	public int getDxUpper() {
		return dxUpper;
	}

	public int getDyLower() {
		return dyLower;
	}

	public int getDyUpper() {
		return dyUpper;
	}
	
	public int getsize() {
		return size;
	}

	public MovingPoint2D[] getPoints() {
		return points;
	}
	
	public void setPoints(MovingPoint2D[] points) {
		this.points = points;
		this.size = points.length;
	}

	
}
