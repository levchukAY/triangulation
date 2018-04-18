/**
 * 2D vector class implementation.
 */
public class Point {

    public double x;
    public double y;
    public double z;
    
    /**
     * Constructor of the 2D vector class used to create new vector instances.
     * 
     * @param x
     *            The x coordinate of the new vector
     * @param y
     *            The y coordinate of the new vector
     */
    public Point(double x, double y) {
    	this(x, y, 0);
    }

    public Point(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
    }
    /**
     * Subtracts the given vector from this.
     * 
     * @param vector
     *            The vector to be subtracted from this
     * @return A new instance holding the result of the vector subtraction
     */
    public Point sub(Point vector) {
        return new Point(this.x - vector.x, this.y - vector.y, this.z - vector.z);
    }

    /**
     * Adds the given vector to this.
     * 
     * @param vector
     *            The vector to be added to this
     * @return A new instance holding the result of the vector addition
     */
    public Point add(Point vector) {
        return new Point(this.x + vector.x, this.y + vector.y);
    }

    /**
     * Multiplies this by the given scalar.
     * 
     * @param scalar
     *            The scalar to be multiplied by this
     * @return A new instance holding the result of the multiplication
     */
    public Point mult(double scalar) {
        return new Point(this.x * scalar, this.y * scalar);
    }

    /**
     * Computes the magnitude or length of this.
     * 
     * @return The magnitude of this
     */
    public double mag() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    /**
     * Computes the dot product of this and the given vector.
     * 
     * @param vector
     *            The vector to be multiplied by this
     * @return A new instance holding the result of the multiplication
     */
    public double dot(Point vector) {
        return this.x * vector.x + this.y * vector.y;
    }

    /**
     * Computes the 2D pseudo cross product Dot(Perp(this), vector) of this and
     * the given vector.
     * 
     * @param vector
     *            The vector to be multiplied to the perpendicular vector of
     *            this
     * @return A new instance holding the result of the pseudo cross product
     */
    public double cross(Point vector) {
        return this.y * vector.x - this.x * vector.y;
    }
    
    public Point crossProduct(Point vector) {
    	return new Point(z * vector.y - y * vector.z, x * vector.z - z * vector.x, 
    			y * vector.x - x * vector.y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
    
    public Point divide(Point point, float value) {
    	float lambda = (float) ((value - Math.min(z, point.z)) / (Math.max(z, point.z) - Math.min(z, point.z)));
    	return new Point(
    			x + (point.x - x) * lambda, 
    			y + (point.y - y) * lambda,
    			value);
    }

}