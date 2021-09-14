package drawings;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.util.LinkedList;

import javax.swing.JComponent;

public class LineSegment extends JComponent{
	
	private MovingPoint2D startPoint;
	private MovingPoint2D endPoint;
	private final boolean drawFinalPosition = true;
	
	public LineSegment() {}
	
	public LineSegment(MovingPoint2D p1, MovingPoint2D p2) {
		//line segments start at their lower point and end at their upper point (left to right in case of tie) at initial time
		if (p1.compareTo(p2) < 0) {
			this.startPoint = p1;
			this.endPoint = p2;
		}
		else {
			this.startPoint = p2;
			this.endPoint = p1;
		}

	}
	
	public LineSegment(LineSegment another) {
		this.startPoint = new MovingPoint2D(another.getStartPoint());
		this.endPoint = new MovingPoint2D(another.getEndPoint());
	}
	
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(Color.BLACK);
		g2d.drawLine(startPoint.getxCoord() + MovingPointSet2D.pointRad/2, startPoint.getyCoord() + MovingPointSet2D.pointRad/2, 
				endPoint.getxCoord() + MovingPointSet2D.pointRad/2, endPoint.getyCoord() + MovingPointSet2D.pointRad/2);
		if(this.drawFinalPosition) {
			g2d.setColor(Color.BLUE);
			g2d.drawLine(startPoint.getxCoord() + startPoint.getxMovement() + MovingPointSet2D.pointRad/2, startPoint.getyCoord() + startPoint.getyMovement() + MovingPointSet2D.pointRad/2, 
					endPoint.getxCoord() + endPoint.getxMovement() + MovingPointSet2D.pointRad/2, endPoint.getyCoord() + endPoint.getyMovement() + MovingPointSet2D.pointRad/2);
		}
	}
	
	public void paintColorful(Graphics g, Color color) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(color);
		g2d.drawLine(startPoint.getxCoord() + MovingPointSet2D.pointRad/2, startPoint.getyCoord() + MovingPointSet2D.pointRad/2, 
				endPoint.getxCoord() + MovingPointSet2D.pointRad/2, endPoint.getyCoord() + MovingPointSet2D.pointRad/2);
		if(this.drawFinalPosition) {
			Stroke defaultStroke = g2d.getStroke();
			Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
			g2d.setStroke(dashed);
			g2d.drawLine(startPoint.getxCoord() + startPoint.getxMovement() + MovingPointSet2D.pointRad/2, startPoint.getyCoord() + startPoint.getyMovement() + MovingPointSet2D.pointRad/2, 
				endPoint.getxCoord() + endPoint.getxMovement() + MovingPointSet2D.pointRad/2, endPoint.getyCoord() + endPoint.getyMovement() + MovingPointSet2D.pointRad/2);
			g2d.setStroke(defaultStroke);
		}
	}
	
	/**
	 * Computes the euclidean length of a line segment at its initial position
	 * @return Length of a line segment in its initial position
	 */
	public double getStartingLength() {
		return MovingPoint2D.pointDistance(startPoint.getxCoord(), startPoint.getyCoord(), endPoint.getxCoord(), endPoint.getyCoord());
	}

	/**
	 * Determines if any point of the given point set lies on the line segment at some time during their movement
	 * @param p Moving point in the plane
	 * @return true if the point crosses the line segment, false otherwise
	 */
	public boolean violatesPointCriterion(MovingPoint2D p) {
		//assume dy=0 for now
		int y1 = this.startPoint.getyCoord();
		int y2 = this.endPoint.getyCoord();
		int yp = p.getyCoord();
		if (y1 > y2) {
			int temp = y1;
			y1 = y2;
			y2 = temp;
		}
		//y1 is smaller now
		if(yp < y1 || yp > y2) {
			return false;
		}
		else {
			double halfPlaneBefore = Math.signum(startPoint.angle(this.endPoint, p));
			double halfPlaneAfter = Math.signum(startPoint.angleAfterMoving(this.endPoint, p));
			if(halfPlaneBefore != halfPlaneAfter) {
				return true;
			}
		return false;
		}
	}
	
	/**
	 * Calculates the swept area of a line segment whose endpoints move horizontally
	 * @return Swept area of the line segment
	 */
	public float coveredArea() {
		
		MovingPoint2D startPointMoved = new MovingPoint2D(startPoint.getxCoord() + startPoint.getxMovement(), startPoint.getyCoord() + startPoint.getyMovement(), 0, 0);
		MovingPoint2D endPointMoved = new MovingPoint2D(endPoint.getxCoord() + endPoint.getxMovement(), endPoint.getyCoord() + endPoint.getyMovement(), 0, 0);
		
		
		double a = MovingPoint2D.pointDistance(startPoint.getxCoord(), startPoint.getyCoord(), 												// start
				startPointMoved.getxCoord(), startPointMoved.getyCoord());																	// start'

		double b = MovingPoint2D.pointDistance(startPointMoved.getxCoord(), startPointMoved.getyCoord(), 									// start'
				endPointMoved.getxCoord(), endPointMoved.getyCoord());																		// end'

		double c = MovingPoint2D.pointDistance(endPointMoved.getxCoord(), endPointMoved.getyCoord(),										// end'
				endPoint.getxCoord(), endPoint.getyCoord());																				// end

		double d = MovingPoint2D.pointDistance(endPoint.getxCoord(), endPoint.getyCoord(), 													// end
				startPoint.getxCoord(), startPoint.getyCoord());																			// start

		double s;
		
		if(Math.signum(startPoint.getxMovement()) == 0 && Math.signum(endPoint.getxMovement()) == 0) {
			return 0;
		}
		//Heron
		else if (Math.signum(startPoint.getxMovement()) == 0) {
			s = (b+c+d)/2.0;
			return (float)Math.sqrt(s*(s-b)*(s-c)*(s-d));
		}
		//Heron
		else if (Math.signum(endPoint.getxMovement()) == 0) {
			s = (a+b+d)/2.0;
			return (float)(Math.sqrt(s*(s-a)*(s-b)*(s-d)));
		}
		
		else if(Math.signum(startPoint.getxMovement()) == Math.signum(endPoint.getxMovement())) {
			
			boolean startBottom = startPoint.getyCoord() < endPoint.getyCoord();
			boolean rightMoving = startPoint.getxMovement() > 0;
			s = (a+b+c+d)/2.0;
			double alpha;
			double gamma;
			
			//endPoint is right-hand
			if(startBottom ^ rightMoving) {
				alpha = startPoint.angle(endPoint, startPointMoved);
				gamma = endPointMoved.angle(startPointMoved, endPoint);
			}
			else {
				alpha = startPoint.angle(startPointMoved, endPoint);
				gamma = endPointMoved.angle(endPoint, startPointMoved);
			}
			//Bretschneider
			return (float)Math.sqrt((s-a)*(s-b)*(s-c)*(s-d)-(((a*b*c*d)/2.0) * (1 + Math.cos(Math.toRadians(alpha + gamma)))));
		}
		else {
			LinearFunct g = new LinearFunct(startPoint.getxCoord(), startPoint.getyCoord(), endPoint.getxCoord(), endPoint.getyCoord());
			LinearFunct gPrime = new LinearFunct(startPointMoved.getxCoord(), startPointMoved.getyCoord(), endPointMoved.getxCoord(), endPointMoved.getyCoord());
			Point2D.Double crossing = g.getIntersection(gPrime);
			
			//First triangle (start, start', crossing)
			double area1;
			a = MovingPoint2D.pointDistance(startPoint.getxCoord(), startPoint.getyCoord(), 												//(start, start')
					startPointMoved.getxCoord(), startPointMoved.getyCoord());
			b = MovingPoint2D.pointDistance(startPointMoved.getxCoord(), startPointMoved.getyCoord(), 										// (start', crossing)
					crossing.getX(), crossing.getY());
			c = MovingPoint2D.pointDistance(crossing.getX(), crossing.getY(), 																// (crossing, start)
					startPoint.getxCoord(), startPoint.getyCoord());
			s = (a+b+c)/2.0;
			//Heron
			area1 = Math.sqrt(s*(s-a)*(s-b)*(s-c));
			
			//Second triangle (end, end', crossing)
			double area2;
			a = MovingPoint2D.pointDistance(endPoint.getxCoord(), endPoint.getyCoord(), 													//(end, end')
					endPointMoved.getxCoord(), endPointMoved.getyCoord());
			b = MovingPoint2D.pointDistance(endPointMoved.getxCoord(), endPointMoved.getyCoord(),											// (end', crossing)
					crossing.getX(), crossing.getY());
			c = MovingPoint2D.pointDistance(crossing.getX(), crossing.getY(), 																// (crossing, end)
					endPoint.getxCoord(), endPoint.getyCoord());
			s = (a+b+c)/2.0;
			//Heron
			area2 = Math.sqrt(s*(s-a)*(s-b)*(s-c));
			return (float)(area1 + area2);
		}
			
	}
	
	/**
	 * Determines if the line segment shares an endpoint with the argument
	 * @param l2 Another line segment
	 * @return true if the line segments share at least one endpoint, false otherwise
	 */
	private boolean sharesEndpoint(LineSegment l2) {
		MovingPoint2D l1Start = this.getStartPoint();
		MovingPoint2D l1End = this.getEndPoint();
		MovingPoint2D l2Start = l2.getStartPoint();
		MovingPoint2D l2End = l2.getEndPoint();
		
		return (l1Start.startsEqualTo(l2Start) || l1Start.startsEqualTo(l2End) || l1End.startsEqualTo(l2Start) || l1End.startsEqualTo(l2End));
		
	}
	
	/**
	 * Determines if a point is strictly within a rectangle (not on the boundary) that has this segment as a diagonal
	 * @param p A point in the plane
	 * @return true if the point lies within the spanned rectangle, false otherwise
	 */
	public boolean isPointInArea(Point2D.Double p) {
		double x = p.getX();
		double y = p.getY();
		double yUpper = Math.max(this.startPoint.getyCoord(), this.endPoint.getyCoord());
		double yLower = Math.min(this.startPoint.getyCoord(), this.endPoint.getyCoord());
		double xUpper = Math.max(this.startPoint.getxCoord(), this.endPoint.getxCoord());
		double xLower = Math.min(this.startPoint.getxCoord(), this.endPoint.getxCoord());
		
		//Vertical segment
		if(xLower == xUpper) {
			if (x == xUpper) {
				return (yLower < y && y < yUpper);
			}
			else {
				return false;
			}
			
		}
		//Horizontal segment
		if(yLower == yUpper) {
			if (y == yUpper) {
				return (xLower < x && x < xUpper);
			}
			else {
				return false;
			}
		}
		
		if (xLower < x && x < xUpper && yLower < y && y < yUpper) {
			return true;
		}
		
		return false;
		
	}
	
	/**
	 * Determines if the line segment has an intersection with the argument line segment
	 * @param l2 Another line segment
	 * @return true if the line segments are crossing, false otherwise
	 */
	public boolean isCrossing(LineSegment l2) {
		
		MovingPoint2D s1 = this.getStartPoint();
		MovingPoint2D s2 = l2.getStartPoint();
		MovingPoint2D e1 = this.getEndPoint();
		MovingPoint2D e2 = l2.getEndPoint();
		
		LinearFunct g1 = new LinearFunct(s1.getxCoord(), s1.getyCoord(), e1.getxCoord(), e1.getyCoord());
		LinearFunct g2 = new LinearFunct(s2.getxCoord(), s2.getyCoord(), e2.getxCoord(), e2.getyCoord());
		
		//if both line segments lie on one line
		if (g1.isIdentical(g2)) {
			// if the line segments overlap
			if((s1.compareTo(s2) <= 0 && e1.compareTo(s2) > 0) ||
					(e1.compareTo(e2) >= 0 && s1.compareTo(e2) < 0) || 
					(s1.compareTo(s2) > 0 && e1.compareTo(e2) < 0)) {
				return true;
			}
			else {
				return false;
			}
		}
	
	//if the segments are not parallel and share an endpoint, they intersect only in this shared endpoint and are only considered touching
	if(this.sharesEndpoint(l2)) {
		return false;
	}
	
	//if the segments are not parallel, find the intersection if one exists
	Point2D.Double intersection = g1.getIntersection(g2);
	if (intersection != null) {
		if (this.isPointInArea(intersection) && l2.isPointInArea(intersection)) {
			return true;
		}
	}
	
		return false;
	}
	
	/**
	 * Determines if at least one of the given edges are drawn crossing the line segment
	 * @param edgeList List of edges
	 * @return True if at least one edge crosses the line segment, false otherwise
	 */
	public boolean isCrossing(LinkedList<Edge> edgeList) {
		for(Edge edge : edgeList) {
			if(this.isCrossing(edge.getPosition())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Determines if the line segment is identical with the argument LineSegment
	 * @param l2 Another line segment
	 * @return True if the line segments are identical, false otherwise
	 */
	public boolean isIdentical(LineSegment l2) {
		return (this.startPoint == l2.startPoint && this.endPoint == l2.endPoint);
	}
	

	public MovingPoint2D getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(MovingPoint2D startPoint) {
		this.startPoint = startPoint;
	}

	public MovingPoint2D getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(MovingPoint2D endPoint) {
		this.endPoint = endPoint;
	}
	
	

}
