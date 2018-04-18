import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A Java implementation of an incremental 2D Delaunay triangulation algorithm.
 */
public class DelaunayTriangulator {

    private List<Point> pointSet;
    private TriangleSoup triangleSoup;

    /**
     * Constructor of the SimpleDelaunayTriangulator class used to create a new
     * triangulator instance.
     * 
     * @param pointSet
     *            The point set to be triangulated
     * @throws NotEnoughPointsException
     *             Thrown when the point set contains less than three points
     */
    public DelaunayTriangulator(List<Point> pointSet) {
        this.pointSet = pointSet;
        this.triangleSoup = new TriangleSoup();
    }

    /**
     * This method generates a Delaunay triangulation from the specified point
     * set.
     * 
     * @throws NotEnoughPointsException
     */
    public void triangulate() throws NotEnoughPointsException {
        triangleSoup = new TriangleSoup();

        if (pointSet == null || pointSet.size() < 3) {
            throw new NotEnoughPointsException("Less than three points in point set.");
        }

        /**
         * In order for the in circumcircle test to not consider the vertices of
         * the super triangle we have to start out with a big triangle
         * containing the whole point set. We have to scale the super triangle
         * to be very large. Otherwise the triangulation is not convex.
         */
        double maxOfAnyCoordinate = 0.0d;

        for (Point vector : getPointSet()) {
            maxOfAnyCoordinate = Math.max(Math.max(vector.x, vector.y), maxOfAnyCoordinate);
        }

        maxOfAnyCoordinate *= 16.0d;

        Point p1 = new Point(0.0d, 3.0d * maxOfAnyCoordinate);
        Point p2 = new Point(3.0d * maxOfAnyCoordinate, 0.0d);
        Point p3 = new Point(-3.0d * maxOfAnyCoordinate, -3.0d * maxOfAnyCoordinate);

        Triangle superTriangle = new Triangle(p1, p2, p3);

        triangleSoup.add(superTriangle);

        for (Point point : pointSet) {
            Triangle triangle = triangleSoup.findContainingTriangle(point);

            if (triangle == null) {
                /**
                 * If no containing triangle exists, then the vertex is not
                 * inside a triangle (this can also happen due to numerical
                 * errors) and lies on an edge. In order to find this edge we
                 * search all edges of the triangle soup and select the one
                 * which is nearest to the point we try to add. This edge is
                 * removed and four new edges are added.
                 */
            	
            	System.out.println(point);
            	if (point.x == 8.) {
            		triangle = null;
            	}
                Edge edge = triangleSoup.findNearestEdge(point);

                Triangle first = triangleSoup.findOneTriangleSharing(edge);
                Triangle second = first.getNoneEdgeNeigbourTiangle(edge);

                Point firstNoneEdgeVertex = first.getNoneEdgeVertex(edge);
                Point secondNoneEdgeVertex = second.getNoneEdgeVertex(edge);

                triangleSoup.remove(first);
                triangleSoup.remove(second);

                Triangle triangle1 = new Triangle(edge.a, firstNoneEdgeVertex, point);
                Triangle triangle2 = new Triangle(edge.b, firstNoneEdgeVertex, point);
                Triangle triangle3 = new Triangle(edge.a, secondNoneEdgeVertex, point);
                Triangle triangle4 = new Triangle(edge.b, secondNoneEdgeVertex, point);
                triangle1.neighbourOppositeA = triangle2;
                triangle1.neighbourOppositeB = triangle3;
                triangle2.neighbourOppositeA = triangle1;
                triangle2.neighbourOppositeB = triangle4;
                triangle3.neighbourOppositeA = triangle4;
                triangle3.neighbourOppositeB = triangle1;
                triangle4.neighbourOppositeA = triangle3;
                triangle4.neighbourOppositeB = triangle2;
                
                triangle1.neighbourOppositeC = first.getNoneEdgeNeigbourTiangle(edge.a, firstNoneEdgeVertex);
                if (triangle1.neighbourOppositeC != null) {
                	triangle1.neighbourOppositeC.setNeighbour(edge.a, firstNoneEdgeVertex, triangle1);
                }
                triangle2.neighbourOppositeC = first.getNoneEdgeNeigbourTiangle(edge.b, firstNoneEdgeVertex);
                if (triangle2.neighbourOppositeC != null) {
                	triangle2.neighbourOppositeC.setNeighbour(edge.b, firstNoneEdgeVertex, triangle2);
                }
                triangle3.neighbourOppositeC = second.getNoneEdgeNeigbourTiangle(edge.a, secondNoneEdgeVertex); 
                if (triangle3.neighbourOppositeC != null) {
                	triangle3.neighbourOppositeC.setNeighbour(edge.a, secondNoneEdgeVertex, triangle3);
                }
                triangle4.neighbourOppositeC = second.getNoneEdgeNeigbourTiangle(edge.b, secondNoneEdgeVertex);             
                if (triangle4.neighbourOppositeC != null) {
                	triangle4.neighbourOppositeC.setNeighbour(edge.b, secondNoneEdgeVertex, triangle4);
                }

                triangleSoup.add(triangle1);
                triangleSoup.add(triangle2);
                triangleSoup.add(triangle3);
                triangleSoup.add(triangle4);

                legalizeEdge(triangle1, new Edge(edge.a, firstNoneEdgeVertex), point);
                legalizeEdge(triangle2, new Edge(edge.b, firstNoneEdgeVertex), point);
                legalizeEdge(triangle3, new Edge(edge.a, secondNoneEdgeVertex), point);
                legalizeEdge(triangle4, new Edge(edge.b, secondNoneEdgeVertex), point);
            } else {
                /**
                 * The vertex is inside a triangle.
                 */
                Point a = triangle.a;
                Point b = triangle.b;
                Point c = triangle.c;

                triangleSoup.remove(triangle);

                Triangle first = new Triangle(a, b, point);
                Triangle second = new Triangle(b, c, point);
                Triangle third = new Triangle(c, a, point);
                
                first.neighbourOppositeA = second;
                first.neighbourOppositeB = third;
                first.neighbourOppositeC = triangle.neighbourOppositeC;
                second.neighbourOppositeA = third;
                second.neighbourOppositeB = first;
                second.neighbourOppositeC = triangle.neighbourOppositeA;
                third.neighbourOppositeA = first;
                third.neighbourOppositeB = second;
                third.neighbourOppositeC = triangle.neighbourOppositeB;
                
                
                if (triangle.neighbourOppositeA != null) 
                	triangle.neighbourOppositeA.setNeighbour(c, b, second);
                if (triangle.neighbourOppositeB != null) 
                	triangle.neighbourOppositeB.setNeighbour(a, c, third);
                if (triangle.neighbourOppositeC != null) 
                	triangle.neighbourOppositeC.setNeighbour(a, b, first);

                triangleSoup.add(first);
                triangleSoup.add(second);
                triangleSoup.add(third);

                legalizeEdge(first, new Edge(a, b), point);
                legalizeEdge(second, new Edge(b, c), point);
                legalizeEdge(third, new Edge(c, a), point);
            }
        }

        /**
         * Remove all triangles that contain vertices of the super triangle.
         */
        triangleSoup.removeTrianglesUsing(superTriangle.a);
        triangleSoup.removeTrianglesUsing(superTriangle.b);
        triangleSoup.removeTrianglesUsing(superTriangle.c);
    }

    /**
     * This method legalizes edges by recursively flipping all illegal edges.
     * 
     * @param triangle
     *            The triangle
     * @param edge
     *            The edge to be legalized
     * @param newVertex
     *            The new vertex
     */
    private void legalizeEdge(Triangle triangle, Edge edge, Point newVertex) {
        Triangle neighbourTriangle = triangle.getNoneEdgeNeigbourTiangle(edge);

        /**
         * If the triangle has a neighbor, then legalize the edge
         */
        if (neighbourTriangle != null) {
            if (neighbourTriangle.isPointInCircumcircle(newVertex)) {
                triangleSoup.remove(triangle);
                triangleSoup.remove(neighbourTriangle);

                Point noneEdgeVertex = neighbourTriangle.getNoneEdgeVertex(edge);

                Triangle firstTriangle = new Triangle(noneEdgeVertex, edge.a, newVertex);
                Triangle secondTriangle = new Triangle(noneEdgeVertex, edge.b, newVertex);
                firstTriangle.neighbourOppositeA = triangle.getNoneEdgeNeigbourTiangle(edge.a, newVertex);
                firstTriangle.neighbourOppositeC = neighbourTriangle.getNoneEdgeNeigbourTiangle(edge.a, noneEdgeVertex);
                firstTriangle.neighbourOppositeB = secondTriangle;
                secondTriangle.neighbourOppositeA = triangle.getNoneEdgeNeigbourTiangle(edge.b, newVertex);
                secondTriangle.neighbourOppositeC =  neighbourTriangle.getNoneEdgeNeigbourTiangle(edge.b, noneEdgeVertex);
                secondTriangle.neighbourOppositeB = firstTriangle;
                
                firstTriangle.neighbourOppositeA.setNeighbour(edge.a, newVertex, firstTriangle);
                secondTriangle.neighbourOppositeA.setNeighbour(edge.b, newVertex, secondTriangle);
                if (firstTriangle.neighbourOppositeC != null) {
                	firstTriangle.neighbourOppositeC.setNeighbour(edge.a, noneEdgeVertex, firstTriangle);
                }
                if (secondTriangle.neighbourOppositeC != null) {
                	secondTriangle.neighbourOppositeC.setNeighbour(edge.b, noneEdgeVertex, secondTriangle);
                }

                triangleSoup.add(firstTriangle);
                triangleSoup.add(secondTriangle);

                legalizeEdge(firstTriangle, new Edge(noneEdgeVertex, edge.a), newVertex);
                legalizeEdge(secondTriangle, new Edge(noneEdgeVertex, edge.b), newVertex);
            }
        }
    }

    /**
     * Creates a random permutation of the specified point set. Based on the
     * implementation of the Delaunay algorithm this can speed up the
     * computation.
     */
    public void shuffle() {
        Collections.shuffle(pointSet);
    }

    /**
     * Shuffles the point set using a custom permutation sequence.
     * 
     * @param permutation
     *            The permutation used to shuffle the point set
     */
    public void shuffle(int[] permutation) {
        List<Point> temp = new ArrayList<Point>();
        for (int i = 0; i < permutation.length; i++) {
            temp.add(pointSet.get(permutation[i]));
        }
        pointSet = temp;
    }

    /**
     * Returns the point set in form of a vector of 2D vectors.
     * 
     * @return Returns the points set.
     */
    public List<Point> getPointSet() {
        return pointSet;
    }

    /**
     * Returns the trianges of the triangulation in form of a vector of 2D
     * triangles.
     * 
     * @return Returns the triangles of the triangulation.
     */
    public TriangleSoup getTriangleSoup() {
        return triangleSoup;
    }

}