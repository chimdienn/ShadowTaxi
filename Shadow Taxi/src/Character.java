import bagel.*;
import java.util.Properties;

/**
 * Represents a character in the game, including driver and passenger.
 */
public abstract class Character implements Collideable{
    private static final double PUSH_OUT_DISTANCE = 2; // Pushed by two pixels away per frame during collision

    private final Image IMAGE;
    private final double RADIUS;
    private final double WALK_X_SPEED;
    private final double WALK_Y_SPEED;
    private double health;
    private Location location;

    private int collisionFrames;
    private int timeoutFrames;
    private Collideable inCollisionObject;
    private boolean isInvincible;

    /**
     * Constructs a new character.
     *
     * @param image The image representing the character
     * @param radius The radius of the character.
     * @param walkXSpeed The horizontal speed of the character.
     * @param walkYSpeed The vertical speed of the character.
     * @param health The initial health of the character.
     * @param location The location of the character on the screen.
     */
    public Character(Image image, double radius, double walkXSpeed, double walkYSpeed, double health, Location location){
        this.IMAGE = image;
        this.RADIUS = radius;
        this.WALK_X_SPEED = walkXSpeed;
        this.WALK_Y_SPEED = walkYSpeed;
        this.health = health;
        this.location= location;
        this.isInvincible = false;

        // At start, character has no collision and timeout frames
        collisionFrames = 0;
        timeoutFrames = 0;
    }

    /**
     * Gets the image of the character.
     *
     * @return The image of the character.
     */
    public Image getImage() {
        return IMAGE;
    }

    /**
     * Gets the radius of the character.
     *
     * @return The radius of the character.
     */
    public double getRadius() {
        return RADIUS;
    }

    /**
     * Gets the walking speed along the X-axis.
     *
     * @return The walking speed along the X-axis.
     */
    public double getWalkXSpeed() {
        return WALK_X_SPEED;
    }

    /**
     * Gets the walking speed along the Y-axis.
     *
     * @return The walking speed along the Y-axis.
     */
    public double getWalkYSpeed() {
        return WALK_Y_SPEED;
    }

    /**
     * Gets the current health of the character.
     *
     * @return The health of the character.
     */
    public double getHealth() {
        return health;
    }

    /**
     * Gets the current location of the character.
     *
     * @return The current location of the character.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Gets the damage the character inflicts, default is 0 as character does not inflict damage during collision
     *
     * @return The damage value.
     */
    public double getDamage() {
        return 0;
    }

    /**
     * Gets the number of timeout frames left for the character.
     *
     * @return The timeout frame count.
     */
    public int getTimeoutFrames() {
        return timeoutFrames;
    }

    /**
     * Sets the invincibility status.
     *
     * @param isInvincible The invincibility status.
     */
    public void setIsInvincible(boolean isInvincible) {
        this.isInvincible = isInvincible;
    }

    /**
     * Abstract method for ejecting a character outside.
     *
     * @param location The location to be ejected from.
     */
    public abstract void ejectTaxi(Location location);

    /**
     * Update the character's coordinates to match with the taxi's while in trip.
     * @param input The current keyboard input.
     * @param xSpeed The number of pixels passengers move horizontally per keyboard input.
     */
    public void setLocationInTaxi(Input input, double xSpeed) {
        if (input.isDown(Keys.LEFT)){
            getLocation().setX(getLocation().getX() - xSpeed);
        }

        if (input.isDown(Keys.RIGHT)){
            getLocation().setX(getLocation().getX() + xSpeed);
        }
    }

    /**
     * Handles the collision between the character and another collideable object.
     *
     * @param gameProps The property file containing configuration.
     * @param other The other entity involved in the collision.
     */
    @Override
    public void collide (Properties gameProps, Collideable other){
        if (location.distance(other.getLocation()) <= (RADIUS + other.getRadius())) {
            if (collisionFrames == 0){
                inCollisionObject = other;
                collisionFrames = TOTAL_COLLISION_FRAMES;
            }
            if (timeoutFrames == 0 && other.getTimeoutFrames()==0 && !isInvincible){
                timeoutFrames = TOTAL_TIMEOUT_FRAMES;
                health -= inCollisionObject.getDamage();
                if (health <= 0) {
                    // Generate blood if 0 health
                    new Blood(gameProps, location.getX(), location.getY());
                }
            }
            if (other instanceof Fireball fireball){
                fireball.setHasHitTarget(true);
            }
        }
    }

    /**
     * Handles collisions between the character and cars or fireballs when the character is outside the taxi.
     *
     * @param gameProps The property file containing configuration.
     * @param isCharacterOutside The flag indicating whether the character is outside the taxi.
     */
    public void handleCharacterCollision(Properties gameProps, boolean isCharacterOutside) {
        // Just implement collision logic when driver or passenger is outside taxi
        if (isCharacterOutside) {
            for (Car car : Car.getCarList()) {
                collide(gameProps, car);
                car.collide(gameProps, this);
            }
            for (Fireball fireball : Fireball.getFireballList()){
                collide(gameProps, fireball);
            }
        }
        if (collisionFrames > 0){
            // Collision push-away logic
            if (location.getY() < inCollisionObject.getLocation().getY()){
                location.setY(location.getY() - PUSH_OUT_DISTANCE);
            }
            else {
                location.setY(location.getY() + PUSH_OUT_DISTANCE);
            }
            if (location.getX() < inCollisionObject.getLocation().getX()){
                location.setX(location.getX() - PUSH_OUT_DISTANCE);
            }
            else {
                location.setX(location.getX() + PUSH_OUT_DISTANCE);
            }
            collisionFrames -= 1;
        }

        if (timeoutFrames > 0) {
            timeoutFrames -= 1;
        }
    }
}