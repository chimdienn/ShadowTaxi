/**
 * Represents a location in a 2D space with X and Y coordinates.
 */
public class Location {
    private double x;
    private double y;

    /**
     * Constructs a new Location with the specified X and Y coordinates.
     *
     * @param x The X-coordinate of the location.
     * @param y The Y-coordinate of the location.
     */
    public Location(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the X-coordinate of the location.
     *
     * @return The X-coordinate of the location.
     */
    public double getX() {
        return x;
    }

    /**
     * Gets the Y-coordinate of the location.
     *
     * @return The Y-coordinate of the location.
     */
    public double getY() {
        return y;
    }

    /**
     * Sets the X-coordinate of the location.
     *
     * @param x The X-coordinate to set.
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Sets the Y-coordinate of the location.
     *
     * @param y The Y-coordinate to set.
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Calculates the Euclidean distance between this location and the provided location.
     *
     * @param location The provided location to calculate the distance to.
     * @return The Euclidean distance between this location and the specified location.
     */
    public double distance(Location location) {
        return Math.sqrt(Math.pow(location.x - this.x, 2) + Math.pow(location.y - this.y, 2));
    }

}
