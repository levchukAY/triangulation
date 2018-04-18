import java.util.Arrays;

/**
 * 2D triangle class implementation.
 */
public class Triangle {

    public Point a;
    public Point b;
    public Point c;
    
    public Triangle neighbourOppositeA;
    public Triangle neighbourOppositeB;
    public Triangle neighbourOppositeC;
    
    private Point normal;
    
    public boolean isVisited;

    /**
     * Constructor of the 2D triangle class used to create a new triangle
     * instance from three 2D vectors describing the triangle's vertices.
     * 
     * @param a
     *            The first vertex of the triangle
     * @param b
     *            The second vertex of the triangle
     * @param c
     *            The third vertex of the triangle
     */
    public Triangle(Point a, Point b, Point c) {
        this.a = a;
        this.b = b;
        this.c = c;
        isVisited = false;
    }

    /**
     * Tests if a 2D point lies inside this 2D triangle. See Real-Time Collision
     * Detection, chap. 5, p. 206.
     * 
     * @param point
     *            The point to be tested
     * @return Returns true iff the point lies inside this 2D triangle
     */
    public boolean contains(Point point) {
        double pab = point.sub(a).cross(b.sub(a));
        double pbc = point.sub(b).cross(c.sub(b));

        if (!hasSameSign(pab, pbc)) {
            return false;
        }

        double pca = point.sub(c).cross(a.sub(c));

        if (!hasSameSign(pab, pca)) {
            return false;
        }

        return true;
    }
    

    public boolean containsNew(Point point) {
        double pab = point.sub(a).cross(b.sub(a));
        double pbc = point.sub(b).cross(c.sub(b));
        double pca = point.sub(c).cross(a.sub(c));
        
        if (pab == 0 && pca * pbc >= 0) {
        	return true;
        }        
        if (pbc == 0 && pca * pab >= 0) {
        	return true;
        }        
        if (pca == 0 && pab * pbc >= 0) {
        	return true;
        }
        
        if (!hasSameSign(pab, pbc)) {
            return false;
        }

        if (!hasSameSign(pab, pca)) {
            return false;
        }

        return true;
    }

    /**
     * Tests if a given point lies in the circumcircle of this triangle. Let the
     * triangle ABC appear in counterclockwise (CCW) order. Then when det &gt;
     * 0, the point lies inside the circumcircle through the three points a, b
     * and c. If instead det &lt; 0, the point lies outside the circumcircle.
     * When det = 0, the four points are cocircular. If the triangle is oriented
     * clockwise (CW) the result is reversed. See Real-Time Collision Detection,
     * chap. 3, p. 34.
     * 
     * @param point
     *            The point to be tested
     * @return Returns true iff the point lies inside the circumcircle through
     *         the three points a, b, and c of the triangle
     */
    public boolean isPointInCircumcircle(Point point) {
        double a11 = a.x - point.x;
        double a21 = b.x - point.x;
        double a31 = c.x - point.x;

        double a12 = a.y - point.y;
        double a22 = b.y - point.y;
        double a32 = c.y - point.y;

        double a13 = (a.x - point.x) * (a.x - point.x) + (a.y - point.y) * (a.y - point.y);
        double a23 = (b.x - point.x) * (b.x - point.x) + (b.y - point.y) * (b.y - point.y);
        double a33 = (c.x - point.x) * (c.x - point.x) + (c.y - point.y) * (c.y - point.y);

        double det = a11 * a22 * a33 + a12 * a23 * a31 + a13 * a21 * a32 - a13 * a22 * a31 - a12 * a21 * a33
                - a11 * a23 * a32;

        if (isOrientedCCW()) {
            return det > 0.0d;
        }

        return det < 0.0d;
    }

    /**
     * Test if this triangle is oriented counterclockwise (CCW). Let A, B and C
     * be three 2D points. If det &gt; 0, C lies to the left of the directed
     * line AB. Equivalently the triangle ABC is oriented counterclockwise. When
     * det &lt; 0, C lies to the right of the directed line AB, and the triangle
     * ABC is oriented clockwise. When det = 0, the three points are colinear.
     * See Real-Time Collision Detection, chap. 3, p. 32
     * 
     * @return Returns true iff the triangle ABC is oriented counterclockwise
     *         (CCW)
     */
    public boolean isOrientedCCW() {
        double a11 = a.x - c.x;
        double a21 = b.x - c.x;

        double a12 = a.y - c.y;
        double a22 = b.y - c.y;

        double det = a11 * a22 - a12 * a21;

        return det > 0.0d;
    }

    /**
     * Returns true if this triangle contains the given edge.
     * 
     * @param edge
     *            The edge to be tested
     * @return Returns true if this triangle contains the edge
     */
    public boolean isNeighbour(Edge edge) {
        return (a == edge.a || b == edge.a || c == edge.a) && (a == edge.b || b == edge.b || c == edge.b);
    }

    /**
     * Returns the vertex of this triangle that is not part of the given edge.
     * 
     * @param edge
     *            The edge
     * @return The vertex of this triangle that is not part of the edge
     */
    public Point getNoneEdgeVertex(Edge edge) {
        if (a != edge.a && a != edge.b) {
            return a;
        } else if (b != edge.a && b != edge.b) {
            return b;
        } else if (c != edge.a && c != edge.b) {
            return c;
        }

        return null;
    }

    /**
     * Returns true if the given vertex is one of the vertices describing this
     * triangle.
     * 
     * @param vertex
     *            The vertex to be tested
     * @return Returns true if the Vertex is one of the vertices describing this
     *         triangle
     */
    public boolean hasVertex(Point vertex) {
        if (a == vertex || b == vertex || c == vertex) {
            return true;
        }

        return false;
    }

    /**
     * Returns an EdgeDistancePack containing the edge and its distance nearest
     * to the specified point.
     * 
     * @param point
     *            The point the nearest edge is queried for
     * @return The edge of this triangle that is nearest to the specified point
     */
    public EdgeDistancePack findNearestEdge(Point point) {
        EdgeDistancePack[] edges = new EdgeDistancePack[3];

        edges[0] = new EdgeDistancePack(new Edge(a, b),
                computeClosestPoint(new Edge(a, b), point).sub(point).mag());
        edges[1] = new EdgeDistancePack(new Edge(b, c),
                computeClosestPoint(new Edge(b, c), point).sub(point).mag());
        edges[2] = new EdgeDistancePack(new Edge(c, a),
                computeClosestPoint(new Edge(c, a), point).sub(point).mag());

        Arrays.sort(edges);
        return edges[0];
    }
    
    public void setNeighbour(Point firstPoint, Point secondPoint, Triangle neighbour) {
    	if (!a.equals(firstPoint) && !a.equals(secondPoint)) {
    		neighbourOppositeA = neighbour;
    	} else if (!b.equals(firstPoint) && ! b.equals(secondPoint)) {
    		neighbourOppositeB = neighbour;
    	} else {
    		neighbourOppositeC = neighbour;
    	}
    }

    public Triangle getNoneEdgeNeigbourTiangle(Edge edge) {
    	return getNoneEdgeNeigbourTiangle(edge.a, edge.b);
    }
    
    public Triangle getNoneEdgeNeigbourTiangle(Point firstPoint, Point secondPoint) {
    	if (!a.equals(firstPoint) && ! a.equals(secondPoint)) {
    		return neighbourOppositeA;
    	} else if (!b.equals(firstPoint) && ! b.equals(secondPoint)) {
    		return neighbourOppositeB;
    	}
    	return neighbourOppositeC;
    }
    
    public void calculateNormal() {
		normal = a.sub(b).crossProduct(a.sub(c));
    }
    
    public double getDepth(double x, double y) {
    	if (normal == null) {
    		calculateNormal();
    	}
    	if (normal.z == 0) {
    		return a.z;
    	}
		return a.z - (normal.x * (x - a.x) + normal.y * (y - a.y)) / normal.z;
    }

    /**
     * Computes the closest point on the given edge to the specified point.
     * 
     * @param edge
     *            The edge on which we search the closest point to the specified
     *            point
     * @param point
     *            The point to which we search the closest point on the edge
     * @return The closest point on the given edge to the specified point
     */
    private Point computeClosestPoint(Edge edge, Point point) {
        Point ab = edge.b.sub(edge.a);
        double t = point.sub(edge.a).dot(ab) / ab.dot(ab);

        if (t < 0.0d) {
            t = 0.0d;
        } else if (t > 1.0d) {
            t = 1.0d;
        }

        return edge.a.add(ab.mult(t));
    }

    /**
     * Tests if the two arguments have the same sign.
     * 
     * @param a
     *            The first floating point argument
     * @param b
     *            The second floating point argument
     * @return Returns true iff both arguments have the same sign
     */
    private boolean hasSameSign(double a, double b) {
        return Math.signum(a) == Math.signum(b);
    }
    
    private boolean hasSameSignNew(double a, double b) {
    	if (a == 0 || b == 0) {
    		return true;
    	}
        return Math.signum(a) == Math.signum(b);
    }

    @Override
    public String toString() {
        return "Triangle2D[" + a + ", " + b + ", " + c + "]";
    }

}