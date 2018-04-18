/**
 * 2D edge class implementation.
 */
public class Edge {

    public Point a;
    public Point b;

    /**
     * Constructor of the 2D edge class used to create a new edge instance from
     * two 2D vectors describing the edge's vertices.
     * 
     * @param a
     *            The first vertex of the edge
     * @param b
     *            The second vertex of the edge
     */
    public Edge(Point a, Point b) {
        this.a = a;
        this.b = b;
    }

}