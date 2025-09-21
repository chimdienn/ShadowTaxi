import bagel.*;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Represents the taxi in the game.
 */
public class Taxi implements Collideable {
    private static final double PUSH_OUT_DISTANCE = 1; // Pushed away 1 pixel per frame during collision

    private Image image;
    private final double DAMAGE;
    private final double RADIUS;
    private Location location;
    private double health;
    private boolean hasDriver;
    private boolean isInvincible;

    private int collisionFrames;
    private int timeoutFrames;
    private Collideable inCollisionObject;

    private static ArrayList<Taxi> damagedTaxiList;

    /**
     * Constructs a new Taxi object with the specified game properties and game objects.
     *
     * @param gameProps The property file containing configuration of the game.
     * @param gameObjects A 2D array representing the game objects' configuration.
     */
    public Taxi(Properties gameProps, String[][] gameObjects){
        image = new Image(gameProps.getProperty("gameObjects.taxi.image"));
        DAMAGE = Double.parseDouble(gameProps.getProperty("gameObjects.taxi.damage")) * 100;
        RADIUS = Double.parseDouble(gameProps.getProperty("gameObjects.taxi.radius"));
        for (String[] line : gameObjects) {
            if (line[0].equals("TAXI")) {
                location = new Location(Double.parseDouble(line[1]), Double.parseDouble(line[2]));
                break;
            }
        }
        health = Double.parseDouble(gameProps.getProperty("gameObjects.taxi.health")) * 100;
        hasDriver = true;
        isInvincible = false;

        // At start, taxi has no collision and timeout frames.
        collisionFrames = 0;
        timeoutFrames = 0;
    }

    /**
     * Constructs a new Taxi object with the specified location coordinates.
     *
     * @param gameProps The property file containing configuration of the game.
     * @param x The X-coordinate of the taxi.
     * @param y The Y-coordinate of the taxi.
     */
    public Taxi(Properties gameProps, double x, double y){
        image = new Image(gameProps.getProperty("gameObjects.taxi.image"));
        DAMAGE = Double.parseDouble(gameProps.getProperty("gameObjects.taxi.damage")) * 100;
        RADIUS = Double.parseDouble(gameProps.getProperty("gameObjects.taxi.radius"));
        location = new Location(x, y);
        health = Double.parseDouble(gameProps.getProperty("gameObjects.taxi.health")) * 100;
        hasDriver = false;
    }

    /**
     * Gets the current location of the taxi.
     *
     * @return The location of the taxi.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Gets the radius of the taxi.
     *
     * @return The radius of the taxi.
     */
    public double getRadius() {
        return RADIUS;
    }

    /**
     * Gets the amount of damage the taxi can inflict in a collision.
     *
     * @return The damage value of the taxi.
     */
    public double getDamage() {
        return DAMAGE;
    }

    /**
     * Gets the current health of the taxi.
     *
     * @return The health of the taxi.
     */
    public double getHealth() {
        return health;
    }

    /**
     * Gets whether the taxi currently has a driver.
     *
     * @return True if the taxi has a driver, false otherwise.
     */
    public boolean getHasDriver() {
        return hasDriver;
    }

    /**
     * Sets whether the taxi has a driver.
     *
     * @param hasDriver A boolean indicating if the taxi has a driver.
     */
    public void setHasDriver(boolean hasDriver) {
        this.hasDriver = hasDriver;
    }

    /**
     * Gets the current number of timeout frames.
     *
     * @return The number of timeout frames.
     */
    public int getTimeoutFrames() {
        return timeoutFrames;
    }

    /**
     * Sets the invincibility status of the taxi.
     *
     * @param isInvincible A boolean indicating whether the taxi is invincible.
     */
    public void setIsInvincible(boolean isInvincible) {
        this.isInvincible = isInvincible;
    }

    /**
     * Initializes a new list to store damaged taxi instances.
     */
    public static void makeNewDamagedTaxiList(){
        damagedTaxiList = new ArrayList<>();
    }

    /**
     * Handles the collision between the taxi and another collideable object.
     *
     * @param gameProps The property file containing configuration.
     * @param other The other object involved in the collision.
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
                if (health > 0){
                    // Generate a smoke
                    new Smoke(gameProps, location.getX(), location.getY());
                }
                else {
                    // Generate a fire if taxi is damaged
                    new Fire(gameProps, location.getX(), location.getY());
                }
            }
            if (other instanceof Fireball fireball){
                // Taxi has hit a fireball
                fireball.setHasHitTarget(true);
            }
        }
    }

    /**
     * Handles collisions between the taxi and cars or fireballs.
     *
     * @param gameProps The property file containing configuration.
     */
    public void handleTaxiCollision(Properties gameProps) {
        for (Car car : Car.getCarList()){
            // Collision between taxi and cars
            collide(gameProps, car);
            car.collide(gameProps, this);
        }

        for (Fireball fireball : Fireball.getFireballList()){
            // Collision between taxi and fireballs
            collide(gameProps, fireball);
        }

        if (collisionFrames > 0){
            // Implement collision animation
            if (location.getY() < inCollisionObject.getLocation().getY()){
                location.setY(location.getY() - PUSH_OUT_DISTANCE);
            }
            else {
                location.setY(location.getY() + PUSH_OUT_DISTANCE);
            }
            collisionFrames -= 1;
        }

        if (timeoutFrames > 0) {
            timeoutFrames -= 1;
        }
    }

    /**
     * Creates a damaged taxi and spawns a new taxi at a random location on the road.
     *
     * @param gameProps The property file containing configuration for the game.
     * @param taxi The current taxi to be damaged and replaced.
     * @param driver The driver of the taxi.
     * @param inTripPassenger The passenger currently in the taxi, who will be ejected if present.
     * @return A new Taxi object.
     */
    public static Taxi makeNewTaxi(Properties gameProps, Taxi taxi, Driver driver, Passenger inTripPassenger){
        // Change the image to a damaged taxi
        taxi.image = new Image(gameProps.getProperty("gameObjects.taxi.damagedImage"));
        // Add taxi to the damaged list
        damagedTaxiList.add(taxi);
        if (taxi.hasDriver || driver.getIsWaitingForPassenger()) {
            if (driver.getIsWaitingForPassenger()){
                driver.setWaitingForPassenger(false);
            }
            // Eject driver and passenger from the damaged taxi
            driver.ejectTaxi(taxi.location);
            if (inTripPassenger != null){
                inTripPassenger.ejectTaxi(taxi.location);
            }
        }
        // Make and return a new taxi
        return new Taxi(gameProps,
                MiscUtils.selectAValue(Integer.parseInt(gameProps.getProperty("roadLaneCenter1")),
                        Integer.parseInt(gameProps.getProperty("roadLaneCenter3"))),
                MiscUtils.getRandomInt(Integer.parseInt(gameProps.getProperty("gameObjects.taxi.nextSpawnMinY")),
                        Integer.parseInt(gameProps.getProperty("gameObjects.taxi.nextSpawnMaxY"))+1));
    }


    /**
     * Renders the taxi on the screen and handles its horizontal movement based on user input.
     *
     * @param input The current keyboard input.
     * @param xSpeed The speed at which the taxi moves horizontally.
     * @param ySpeed The speed at which the taxi moves vertically when there is no driver in the taxi.
     */
    public void showTaxi(Input input, double xSpeed, double ySpeed){
        image.draw(location.getX(), location.getY());

        if (!hasDriver){
            if (input.isDown(Keys.UP)) {
                location.setY(location.getY() + ySpeed);
            }
        }
        else {
            if (input.isDown(Keys.LEFT)) {
                location.setX(location.getX() - xSpeed);
            }

            if (input.isDown(Keys.RIGHT)) {
                location.setX(location.getX() + xSpeed);
            }
        }

        for (Taxi damagedTaxi: damagedTaxiList){
            damagedTaxi.image.draw(damagedTaxi.location.getX(), damagedTaxi.location.getY());
            if (input.isDown(Keys.UP)) {
                damagedTaxi.location.setY(damagedTaxi.location.getY() + ySpeed);
            }
        }
    }

    /**
     * Find and return the passenger with in taxi's detect radius, when the taxi has fully stopped,
     * excluding the passenger who just completed the last trip
     * @param input The current mouse/keyboard input.
     * @param gameProps The property filename containing configuration.
     * @param passengers The array of passengers in game.
     * @param lastTripPassenger Passenger just completing the last trip.
     *
     * @return The passenger in taxi's detection, or null if no such passenger is found.
     */
    public Passenger findNearPassenger(Input input, Properties gameProps,
                                              Passenger[] passengers, Passenger lastTripPassenger){
        if (input.isUp(Keys.UP) && input.isUp(Keys.RIGHT) && input.isUp(Keys.LEFT)){
            // Taxi has stopped
            for (Passenger passenger : passengers){
                if (passenger == lastTripPassenger){
                    continue; // Exclude the previous trip passenger
                }
                if (location.distance(passenger.getLocation()) <=
                        Double.parseDouble(gameProps.getProperty("gameObjects.passenger.taxiDetectRadius"))){
                    // The passenger is in the taxi detect radius
                    return passenger;
                }
            }
        }
        return null;
    }
}
