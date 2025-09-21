import bagel.*;

/**
 * Represents the end flag in the game, which marks the destination for passengers.
 */
public class EndFlag {
    private final Image IMAGE;
    private final double RADIUS;
    private Location location;

    /**
     * Constructs an EndFlag object.
     *
     * @param filename The filename of the image representing the end flag.
     * @param radius The radius of the enf flag.
     * @param x The X-coordinate of the end flag's location.
     * @param y The Y-coordinate of the end flag's location.
     */
    public EndFlag(String filename, double radius, double x, double y) {
        IMAGE = new Image(filename);
        this.RADIUS = radius;
        location = new Location(x, y);
    }

    /**
     * Gets the location of the end flag.
     * @return The location of the end flag.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Gets the radius of the end flag.
     * @return The radius of the end flag.
     */
    public double getRadius() {
        return RADIUS;
    }

    /**
     * Renders the end flag's image on the screen.
     */
    public void showEndFlag() {
        IMAGE.draw(location.getX(), location.getY());
    }
}