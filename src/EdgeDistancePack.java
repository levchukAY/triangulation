/**
 * Edge distance pack class implementation used to describe the distance to a
 * given edge.
 */
public class EdgeDistancePack implements Comparable<EdgeDistancePack> {

	public Edge edge;
	public double distance;

	/**
	 * Constructor of the edge distance pack class used to create a new edge
	 * distance pack instance from a 2D edge and a scalar value describing a
	 * distance.
	 * 
	 * @param edge
	 *            The edge
	 * @param distance
	 *            The distance of the edge to some point
	 */
	public EdgeDistancePack(Edge edge, double distance) {
		this.edge = edge;
		this.distance = distance;
	}

	@Override
	public int compareTo(EdgeDistancePack o) {
		return Double.compare(this.distance, o.distance);
	}

}