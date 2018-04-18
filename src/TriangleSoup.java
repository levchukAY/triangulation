import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Triangle soup class implementation.
 */
class TriangleSoup extends ArrayList<Triangle> {


    /**
     * Returns the triangle from this triangle soup that contains the specified
     * point or null if no triangle from the triangle soup contains the point.
     * 
     * @param point
     *            The point
     * @return Returns the triangle from this triangle soup that contains the
     *         specified point or null
     */
    public Triangle findContainingTriangle(Point point) {
        for (Triangle triangle : this) {
            if (triangle.contains(point)) {
                return triangle;
            }
        }
        return null;
    }

    public Triangle findContainingTriangleNew(Point point) {
        for (Triangle triangle : this) {
            if (triangle.containsNew(point)) {
                return triangle;
            }
        }
        return null;
    }
    
    /**
     * Returns one of the possible triangles sharing the specified edge. Based
     * on the ordering of the triangles in this triangle soup the returned
     * triangle may differ. To find the other triangle that shares this edge use
     * the {@link findNeighbour(Triangle2D triangle, Edge2D edge)} method.
     * 
     * @param edge
     *            The edge
     * @return Returns one triangle that shares the specified edge
     */
    public Triangle findOneTriangleSharing(Edge edge) {
        for (Triangle triangle : this) {
            if (triangle.isNeighbour(edge)) {
                return triangle;
            }
        }
        return null;
    }

    /**
     * Returns the edge from the triangle soup nearest to the specified point.
     * 
     * @param point
     *            The point
     * @return The edge from the triangle soup nearest to the specified point
     */
    public Edge findNearestEdge(Point point) {
        List<EdgeDistancePack> edgeList = new ArrayList<EdgeDistancePack>();

        for (Triangle triangle : this) {
            edgeList.add(triangle.findNearestEdge(point));
        }

        EdgeDistancePack[] edgeDistancePacks = new EdgeDistancePack[edgeList.size()];
        edgeList.toArray(edgeDistancePacks);

        Arrays.sort(edgeDistancePacks);
        return edgeDistancePacks[0].edge;
    }

    /**
     * Removes all triangles from this triangle soup that contain the specified
     * vertex.
     * 
     * @param vertex
     *            The vertex
     */
    public void removeTrianglesUsing(Point vertex) {
        List<Triangle> trianglesToBeRemoved = new ArrayList<Triangle>();

        for (Triangle triangle : this) {
            if (triangle.hasVertex(vertex)) {
                trianglesToBeRemoved.add(triangle);
            }
        }

        removeAll(trianglesToBeRemoved);
    }
    
    public void flipTriangle(Triangle triangle, Triangle neighbourTriangle) {
    	
    }
    
    public ArrayList<ArrayList<Point>> getIsoline(float threshold) {
    	ArrayList<ArrayList<Point>> isoline = new ArrayList<>();
    	ArrayList<Triangle> triangles = (ArrayList<Triangle>) clone();
    	for (Triangle triangle : triangles) {
    		if (!triangle.isVisited) {
    			ArrayList<Point> part = recursion(new ArrayList<Point>(), triangle, threshold, null);
    			if (!part.isEmpty()) {
    				isoline.add(part);
    			}
    		}
    	}
    	return isoline;
    }
    
    private ArrayList<Point> determineIsoline(ArrayList<Point> isoline, Triangle triangle, float threshold, Point point) {
    	if (triangle == null) return isoline;
    	triangle.isVisited = true;
    	return recursion(isoline, triangle, threshold, point);
    }

	private ArrayList<Point> recursion(ArrayList<Point> isoline, Triangle triangle, float threshold, Point point) {
		Point newPoint = null;
    	Triangle nextTriangle = null;
    	Point depricatedPoint = null;
		if (!triangle.c.equals(point) && Math.min(triangle.a.z, triangle.b.z) < threshold && Math.max(triangle.a.z, triangle.b.z) > threshold) {
			newPoint = triangle.a.divide(triangle.b, threshold);
			nextTriangle = triangle.neighbourOppositeC;
			depricatedPoint = nextTriangle.getNoneEdgeVertex(new Edge(triangle.a, triangle.b));
		} 
		if (!triangle.b.equals(point) && Math.min(triangle.a.z, triangle.c.z) < threshold && Math.max(triangle.a.z, triangle.c.z) > threshold) {
			newPoint = triangle.a.divide(triangle.c, threshold);
			nextTriangle = triangle.neighbourOppositeB;
			depricatedPoint = nextTriangle.getNoneEdgeVertex(new Edge(triangle.a, triangle.c));
		} 
		if (!triangle.a.equals(point) && Math.min(triangle.b.z, triangle.c.z) < threshold && Math.max(triangle.b.z, triangle.c.z) > threshold) {
			newPoint = triangle.b.divide(triangle.c, threshold);
			nextTriangle = triangle.neighbourOppositeA;
			depricatedPoint = nextTriangle.getNoneEdgeVertex(new Edge(triangle.b, triangle.c));
		}
		if ((isoline.isEmpty() || !newPoint.equals(isoline.get(isoline.size() - 1))) && !nextTriangle.isVisited) {
			isoline.add(newPoint);
			determineIsoline(isoline, nextTriangle, threshold, depricatedPoint);
		}
    	return isoline;
	}

}