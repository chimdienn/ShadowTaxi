import bagel.*;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Represents a fireball in the game.
 */
public class Fireball implements Collideable {
    private static final int CREATE_FACTOR = 300;
    private static final double STARTING_Y_DISTANCE = 50; // Starting distance to avoid collision with its car
    private final Image IMAGE;
    private final double DAMAGE;
    private final double RADIUS;
    private final double Y_SPEED;
    private Location location;
    private boolean hasHitTarget;

    private static ArrayList<Fireball> fireballList;

    /**
     * Constructs a new Fireball object with the specified properties and initial location.
     *
     * @param gameProps The property file containing configuration.
     * @param x The X-coordinate of the fireball's initial location.
     * @param y The Y-coordinate of the fireball's initial location.
     */
    public Fireball(Properties gameProps, double x, double y) {
        this.IMAGE = new Image(gameProps.getProperty("gameObjects.fireball.image"));
        this.DAMAGE = Double.parseDouble(gameProps.getProperty("gameObjects.fireball.damage")) * 100;
        this.RADIUS = Double.parseDouble(gameProps.getProperty("gameObjects.fireball.radius"));
        this.Y_SPEED = Double.parseDouble(gameProps.getProperty("gameObjects.fireball.shootSpeedY"));
        this.location = new Location(x, y - STARTING_Y_DISTANCE);
        this.hasHitTarget = false;
    }

    /**
     * Gets the image representing the fireball.
     *
     * @return The image of the fireball.
     */
    public Image getImage() {
        return IMAGE;
    }

    /**
     * Gets the damage dealt by the fireball on collision.
     *
     * @return The damage value of the fireball.
     */
    public double getDamage() {
        return DAMAGE;
    }

    /**
     * Gets the radius of the fireball used for collision detection.
     *
     * @return The radius of the fireball.
     */
    public double getRadius() {
        return RADIUS;
    }

    /**
     * Gets the current location of the fireball.
     *
     * @return The location of the fireball.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Gets the number of timeout frames after the fireball collides. This is always 0 for fireballs.
     *
     * @return The number of timeout frames (always 0).
     */
    public int getTimeoutFrames() {
        return 0;
    }

    /**
     * Sets the flag indicating whether the fireball has hit its target.
     *
     * @param hasHit A boolean indicating if the fireball has hit its target.
     */
    public void setHasHitTarget(boolean hasHit) {
        this.hasHitTarget = hasHit;
    }

    /**
     * Gets the static list of all active fireballs in the game.
     *
     * @return The list of active fireballs.
     */
    public static ArrayList<Fireball> getFireballList() {
        return fireballList;
    }

    /**
     * Handles the collision between the fireball and another collideable object.
     * Currently, fireball does not take any effect during collision.
     *
     * @param gameProps The property file containing configuration.
     * @param other The other object involved in the collision.
     */
    public void collide(Properties gameProps, Collideable other) {
        // No implementation for fireball collision.
    }

    /**
     * Initializes a new empty fireball list.
     */
    public static void makeNewFireballList() {
        fireballList = new ArrayList<>();
    }

    /**
     * Creates a new fireball applying the random creation condition.
     *
     * @param gameProps The property file containing configuration.
     * @param x The X-coordinate of the fireball's initial location.
     * @param y The Y-coordinate of the fireball's initial location.
     */
    public static void create(Properties gameProps, double x, double y) {
        if (MiscUtils.canSpawn(CREATE_FACTOR)) {
            fireballList.add(new Fireball(gameProps, x, y));
        }
    }

    /**
     * Renders and moves the fireballs on the screens.
     *
     * @param input The user current keyboard input.
     * @param gameProps The property file containing configuration.
     * @param ySpeed The number of pixels fireballs move vertically per keyboard input.
     */
    public static void showFireballs(Input input, Properties gameProps, double ySpeed) {
        // Remove fireball if it is off-screen or has collided
        fireballList.removeIf(fireball -> fireball.location.getY() >
                Double.parseDouble(gameProps.getProperty("window.height")) || fireball.hasHitTarget);
        for (Fireball fireball : fireballList) {
            fireball.IMAGE.draw(fireball.location.getX(), fireball.location.getY());
            fireball.location.setY(fireball.location.getY() - fireball.Y_SPEED);
            if (input.isDown(Keys.UP)) {
                // Fireball moves down when UP key is pressed
                fireball.location.setY(fireball.location.getY() + ySpeed);
            }
        }
    }
}
