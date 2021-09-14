package drawings;

import java.awt.geom.Point2D;

public class LinearFunct {

	private double slope;
	//y-value at which this intersects the y-axis
	private double yOffset;
	//x-value at which this intersects the x-axis
	private double xOffset;
	
	public LinearFunct(double slope, double yOffset) {
		this.slope = slope;
		this.yOffset = yOffset;
		if(slope != 0) {
			this.xOffset = (-yOffset/slope);
		}
		else {
			xOffset = Double.POSITIVE_INFINITY;
		}
	}
	
	public LinearFunct(int x1, int y1, int x2, int y2) {
		if(x1 != x2) {
			this.slope = (double)(y2 - y1)/(double)(x2 - x1);
			this.yOffset = y1 - (x1 * this.slope);
			if(this.slope != 0) {
				this.xOffset = (-this.yOffset/this.slope);
			}
			else {
				this.xOffset = Double.POSITIVE_INFINITY;
			}
		}
		else if(y1 != y2) {
			this.slope = Double.POSITIVE_INFINITY;
			this.yOffset = Double.POSITIVE_INFINITY;
			this.xOffset = x1;
		}
	}
	
	/**
	 * Computes the intersection with another LinearFunct if one exists
	 * @param g2 Another linear function
	 * @return Point in which the linear functions intersect, null if no intersection exists, unspecified point if the functions are identical
	 */
	public Point2D.Double getIntersection(LinearFunct g2) {
		Point2D.Double intersection = null;
		//CASE: parallel
		if (this.slope == g2.getSlope()) {
			//CASE: parallel but non-identical
			if (this.yOffset != g2.getyOffset()) {
				return intersection;
			}
			//CASE: identical => return unspecified point
			else return new Point2D.Double();
		}
		
		intersection = new Point2D.Double(0, 0);
		//SPECIAL CASE: one vertical line
		if(this.slope == Double.POSITIVE_INFINITY) {
			intersection.setLocation(this.xOffset, g2.getSlope() * this.xOffset + g2.getyOffset());
		}
		else if(g2.getSlope() == Double.POSITIVE_INFINITY) {
			intersection.setLocation(g2.getxOffset(), this.slope * g2.getxOffset() + this.yOffset);
		}
		//CASE: intersecting lines
		else {
			//mx + c = nx + d <=> x = (d-c)/(m-n)
			double xVal = (this.yOffset - g2.getyOffset()) / (g2.getSlope() - this.slope);
			intersection.setLocation(xVal, this.slope * xVal + this.yOffset);
		}
		return intersection;
	}

	/**
	 * Determines if both linear functions are identical
	 * @param g2 Another linear function
	 * @return true if the functions are identical, false otherwise
	 */
	public boolean isIdentical(LinearFunct g2) {
		if (this.slope == g2.getSlope() && this.yOffset == g2.getyOffset() && this.xOffset == g2.getxOffset()) {
			return true;
		}
		return false;
	}
	
	
	public double getSlope() {
		return slope;
	}

	public void setSlope(double slope) {
		this.slope = slope;
	}

	public double getyOffset() {
		return yOffset;
	}

	public void setyOffset(double yOffset) {
		this.yOffset = yOffset;
	}

	public double getxOffset() {
		return xOffset;
	}

	public void setxOffset(double xOffset) {
		this.xOffset = xOffset;
	}
	
	
	
}
