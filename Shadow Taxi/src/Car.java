import bagel.*;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Represents a generic car in the game that can interact with other game's entities.
 */
public abstract class Car implements Collideable{
    private static final int Y_1 = -50; // First random y-coordinate
    private static final int Y_2 = 768; // Second random y-coordinate
    private static final double PUSH_OUT_DISTANCE = 1; // Pushed out by 1 pixel per frame when collide

    private final Image IMAGE;
    private final double DAMAGE;
    private final double RADIUS;
    private double health;
    private Location location;
    private double ySpeed;

    private int collisionFrames;
    private int timeoutFrames;
    private int standingFrames;
    private Collideable inCollisionObject;

    private static ArrayList<Car> carList;

    /**
     * Constructs a new car.
     *
     * @param image The image representing the car.
     * @param damage The amount of damage the car can inflict.
     * @param radius The radius of the car.
     * @param health The initial health of the car.
     * @param location The initial location of the car.
     * @param ySpeed The vertical speed of the car.
     */
    public Car(Image image,double damage, double radius, double health, Location location, double ySpeed){
        this.IMAGE = image;
        this.DAMAGE = damage;
        this.RADIUS = radius;
        this.health = health;
        this.location = location;
        this.ySpeed = ySpeed;

        // At start, car has no collision and timeout frames.
        this.collisionFrames = 0;
        this.timeoutFrames = 0;
        this.standingFrames = 0;
    }

    /**
     * Gets the first random Y coordinate.
     *
     * @return The Y1 coordinate.
     */
    public static int getY1() {
        return Y_1;
    }

    /**
     * Gets the second random Y coordinate.
     *
     * @return The Y2 coordinate.
     */
    public static int getY2() {
        return Y_2;
    }

    /**
     * Gets the amount of damage the car can inflict in a collision.
     *
     * @return The damage value of the car.
     */
    public double getDamage() {
        return DAMAGE;
    }

    /**
     * Gets the radius of the car.
     *
     * @return The radius of the car.
     */
    public double getRadius() {
        return RADIUS;
    }

    /**
     * Gets the current location of the car.
     *
     * @return The location of the car.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Gets the vertical speed of the car.
     *
     * @return The Y-speed of the car.
     */
    public double getYSpeed() {
        return ySpeed;
    }

    /**
     * Sets the vertical speed of the car.
     *
     * @param ySpeed The new Y-speed of the car.
     */
    public void setYSpeed(double ySpeed) {
        this.ySpeed = ySpeed;
    }

    /**
     * Gets the list of all cars currently in the game.
     *
     * @return The list of cars.
     */
    public static ArrayList<Car> getCarList() {
        return carList;
    }

    /**
     * Gets the number of timeout frames left for the car.
     *
     * @return The number of timeout frames.
     */
    public int getTimeoutFrames() {
        return timeoutFrames;
    }

    /**
     * Abstract method to set a new random speed for the car.
     *
     * @param gameProps The property file containing configuration.
     */
    public abstract void makeNewSpeed(Properties gameProps);

    /**
     * Initializes a new empty static car list.
     */
    public static void makeNewCarList(){
        carList = new ArrayList<>();
    }

    /**
     * Creates new cars for the game by calling creation methods of subclasses including OtherCar and EnemyCar.
     *
     * @param gameProps The property file containing configuration.
     */
    public static void create(Properties gameProps){
        OtherCar.create(gameProps);
        EnemyCar.create(gameProps);
    }

    /**
     * Handles collision detection between this car and another collideable object.
     *
     * @param gameProps The property file containing configuration.
     * @param other The other entity this car collides with.
     */
    @Override
    public void collide (Properties gameProps, Collideable other){
        // Check if car is in collision radius with other entities
        if (location.distance(other.getLocation()) <= (RADIUS + other.getRadius())) {
            if (collisionFrames == 0){
                collisionFrames = TOTAL_COLLISION_FRAMES;
                inCollisionObject = other;
            }
            if (timeoutFrames == 0) {
                if (other.getTimeoutFrames() == 0 || other.getTimeoutFrames() == TOTAL_TIMEOUT_FRAMES) {
                    timeoutFrames = TOTAL_TIMEOUT_FRAMES;
                    standingFrames = 0;
                    health -= inCollisionObject.getDamage();

                    if (health > 0 && !(other instanceof Character)){
                        // Generate a smoke on screen
                        new Smoke(gameProps, location.getX(), location.getY());
                    }
                    else if (health <= 0) {
                        // Generate a fire on screen when health is below 0
                        new Fire(gameProps, location.getX(), location.getY());
                    }
                } else {
                    standingFrames = TOTAL_TIMEOUT_FRAMES;
                }
                makeNewSpeed(gameProps);
            }
            if (other instanceof Fireball fireball){
                fireball.setHasHitTarget(true);
            }
        }
    }

    /**
     * Handles collision detection and resolution between all cars in the car list.
     *
     * @param gameProps The property file containing configuration.
     */
    public static void handleCarsCollision(Properties gameProps){
        // Nested loops to check collision of every car with every other car in last
        for (int i = 0; i < carList.size(); i++){
            for(int j = i + 1; j < carList.size(); j++){
                carList.get(i).collide(gameProps, carList.get(j));
                carList.get(j).collide(gameProps, carList.get(i));
            }
            for (Fireball fireball : Fireball.getFireballList()){
                carList.get(i).collide(gameProps, fireball);
            }
        }

        for (Car car : carList){
            if (car.collisionFrames > 0){
                // Implement collision animation
                if (car.location.getY() < car.inCollisionObject.getLocation().getY()){
                    car.location.setY(car.location.getY() - PUSH_OUT_DISTANCE);
                }
                else {
                    car.location.setY(car.location.getY() + PUSH_OUT_DISTANCE);
                }
                car.collisionFrames -= 1;
            }

            if (car.timeoutFrames > 0) {
                car.timeoutFrames -= 1;
            }
            if (car.standingFrames > 0){
                car.standingFrames -= 1;
            }
        }
    }

    /**
     * Displays all active cars and handles their movement based on user input and game state.
     *
     * @param input The user keyboard input.
     * @param gameProps The property file containing configuration.
     * @param ySpeed The number of pixels cars move vertically per keyboard input.
     */
    public static void showCars(Input input, Properties gameProps, double ySpeed){
        // Remove the car that have no health
        carList.removeIf(car-> (car.collisionFrames == 0 && car.health<=0));

        for (Car car : carList){
            car.IMAGE.draw(car.location.getX(), car.location.getY());
            if (car.timeoutFrames == 0 && car.collisionFrames == 0 && car.standingFrames == 0) {
                // Car move upward when it is not in timeout
                car.location.setY(car.location.getY() - car.getYSpeed());
            }
            if (input.isDown(Keys.UP)) {
                // Cars move downward when UP key is pressed
                car.location.setY(car.location.getY() + ySpeed);
            }

            if (car instanceof EnemyCar){
                // Implement fireball logic for enemy cars
                Fireball.create(gameProps, car.location.getX(), car.location.getY());
            }
        }

    }
}
