package drawings;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

public class MovingPoint2D implements Comparable{
	
	private int xCoord;
	private int yCoord;
	private int xMovement;
	private int yMovement;
	
	public MovingPoint2D() {};
	
	public MovingPoint2D(int x, int y, int dx, int dy) {
		this.xCoord = x;
		this.yCoord = y;
		this.xMovement = dx;
		this.yMovement = dy;
	}
	
	public MovingPoint2D(MovingPoint2D another) {
		this.xCoord = another.getxCoord();
		this.yCoord = another.getyCoord();
		this.xMovement = another.getxMovement();
		this.yMovement = another.getyMovement();
	}

	
	/**
	 * Randomizes the coordinate and movement fields of this, according to the specified bounds
	 * @param xLeft Smallest possible x-coordinate of a point
	 * @param xRight Greatest possible x-coordinate of a point
	 * @param yLower Smallest possible y-coordinate of a point
	 * @param yUpper Greatest possible y-coordinate of a point
	 * @param dxLower Smallest possible horizontal movement of a point
	 * @param dxUpper Greatest possible horizontal movement of a point
	 * @param dyLower Smallest possible vertical movement of a point
	 * @param dyUpper Greatest possible vertical movement of a point
	 * @param random Randomness generator
	 */
	public void randomizePoint(int xLeft, int xRight, int yLower, int yUpper, int dxLower, int dxUpper, int dyLower, int dyUpper, Random random) {
		this.xCoord = (int) (xLeft + random.nextDouble() * (xRight - xLeft));
		this.yCoord = (int) (yLower + random.nextDouble() * (yUpper - yLower));
		this.xMovement = (int) (dxLower + random.nextDouble() * (dxUpper - dxLower));
		this.yMovement = (int) (dyLower + random.nextDouble() * (dyUpper - dyLower));
	}
	
	

	/**
	 * Computes the angle of the point with two other moving points in clockwise direction(or as negative angle in counterclockwise direction) in their initial positions. <br>
	 * F: (p,p1,p2) -> [-180, 180]
	 * @param p1 First other point
	 * @param p2 Second other point
	 * @return The clockwise angle between p1 and p2 in the point before their movement
	 */
	public double angle(MovingPoint2D p1, MovingPoint2D p2) {
		double dxa = this.xCoord - p1.getxCoord();
		double dya = this.yCoord - p1.getyCoord();
		double dxb = this.xCoord - p2.getxCoord();
		double dyb = this.yCoord - p2.getyCoord();
		double aDotb = (dxa*dxb) + (dya*dyb);
		double det = (dxa*dyb - dya*dxb);
		double angle = Math.toDegrees(Math.atan2(det, aDotb));
		return angle;
	}
	
	/**
	 * Computes the angle of the point with two other moving points in clockwise direction(or as negative angle in counterclockwise direction) in their final positions. <br>
	 * F: (p,p1,p2) -> [-180, 180]
	 * @param p1 First other point
	 * @param p2 Second other point
	 * @return The clockwise angle between p1 and p2 in the point after their movement
	 */
	public double angleAfterMoving(MovingPoint2D p1, MovingPoint2D p2) {
		double dxa = (this.xCoord + this.xMovement) - (p1.getxCoord() + p1.getxMovement());
		double dya = (this.yCoord + this.yMovement) - (p1.getyCoord() + p1.getyMovement());
		double dxb = (this.xCoord + this.xMovement) - (p2.getxCoord() + p2.getxMovement());
		double dyb = (this.yCoord + this.yMovement) - (p2.getyCoord() + p2.getyMovement());
		double aDotb = (dxa*dxb) + (dya*dyb);
		double det = (dxa*dyb - dya*dxb);
		double angle = Math.toDegrees(Math.atan2(det, aDotb));
		return angle;
	}
	
	/**
	 * Computes the euclidean distance between two non-moving points given their position
	 * @param x1 x-coordinate of first point
	 * @param y1 y-coordinate of first point
	 * @param x2 x-coordinate of second point
	 * @param y2 y-coordinate of second point
	 * @return Euclidean distance between two points
	 */
	public static double pointDistance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
	}

	/**
	 * Determines if the point coincides with another moving point throughout their movement
	 * @param p2 Another moving point
	 * @return True if the points coincide, false otherwise
	 */
	public boolean coincides(MovingPoint2D p2) {
		if (this.xCoord == p2.getxCoord() && this.yCoord == p2.getyCoord() && this.xMovement == p2.getxMovement() && this.yMovement == p2.getyMovement()){
			return true;
		}
		return false;
	}
	
	/**
	 * Computes the clockwise angle between three points given their coordinates
	 * @param x1 x-coordinate of first point
	 * @param y1 y-coordinate of first point
	 * @param x2 x-coordinate of second point
	 * @param y2 y-coordinate of second point
	 * @param x3 x-coordinate of third point
	 * @param y3 y-coordinate of third point
	 * @return Clockwise angle between second and third point in the first point
	 */
	public static double angleBetween(int x1, int y1, int x2, int y2, int x3, int y3) {

		  return Math.toDegrees(Math.atan2(x2 - x1, y2 - y1) -
		                        Math.atan2(x3 - x1, y3 - y1));
		}
	
	
	public int getxCoord() {
		return xCoord;
	}

	public void setxCoord(int xCoord) {
		this.xCoord = xCoord;
	}

	public int getyCoord() {
		return yCoord;
	}

	public void setyCoord(int yCoord) {
		this.yCoord = yCoord;
	}

	
	public int getxMovement() {
		return xMovement;
	}

	public void setxMovement(int xMovement) {
		this.xMovement = xMovement;
	}

	public int getyMovement() {
		return yMovement;
	}

	public void setyMovement(int yMovement) {
		this.yMovement = yMovement;
	}

	public String pointToString() {
		return ("(" + this.xCoord + ", " + this.yCoord +")");
	}
	
	public boolean startsEqualTo(MovingPoint2D p2) {
		return (this.xCoord == p2.getxCoord() && this.yCoord == p2.getyCoord());
	}
	
	
    @Override
    public int compareTo(Object o) {
    	if (this.yCoord == (((MovingPoint2D) o).getyCoord())) {
    		return Double.compare(this.xCoord, (((MovingPoint2D) o).getxCoord()));
    	}
    	else {
    		return Double.compare(this.yCoord, (((MovingPoint2D) o).getyCoord()));
    	}

    }
	
}
