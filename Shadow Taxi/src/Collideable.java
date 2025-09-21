import java.util.Properties;

/**
 * An interface representing objects that can participate in collisions within the game.
 */
public interface Collideable {
    /**
     * The total number of frames in a collision state.
     */
    int TOTAL_COLLISION_FRAMES = 10;

    /**
     * The total number of frames in a timeout state after a collision.
     */
    int TOTAL_TIMEOUT_FRAMES = 200;

    /**
     * Gets the radius of the object.
     *
     * @return The radius of the object.
     */
    double getRadius();

    /**
     * Gets the location of the object.
     *
     * @return The location of the object.
     */
    Location getLocation();

    /**
     * Gets the amount of damage the object inflicts during a collision.
     *
     * @return The damage dealt by the object.
     */
    double getDamage();

    /**
     * Gets the number of timeout frames left for the object.
     *
     * @return The number of timeout frames.
     */
    int getTimeoutFrames();

    /**
     * Handles the collision between this object and another collideable object.
     *
     * @param gameProps The property file containing configuration.
     * @param other The other object involved in the collision.
     */
    void collide(Properties gameProps, Collideable other);
}
