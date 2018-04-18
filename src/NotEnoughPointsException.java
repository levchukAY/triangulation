/**
 * Exception thrown by the Delaunay triangulator when it is initialized with
 * less than three points.
 */
public class NotEnoughPointsException extends Exception {

    public NotEnoughPointsException() {
    }

    public NotEnoughPointsException(String s) {
        super(s);
    }

}