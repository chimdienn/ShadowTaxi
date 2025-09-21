import bagel.*;
import java.util.Properties;

/**
 * Represents the driver character in the game.
 */
public class Driver extends Character{
    private static final double EJECTED_DISTANCE = 50;
    private boolean isWaitingForPassenger;

    /**
     * Constructs a new Driver.
     *
     * @param gameProps The property file containing configuration.
     * @param x The X-coordinate of the driver's initial location.
     * @param y The Y-coordinate of the driver's initial location.
     */
    public Driver(Properties gameProps, double x, double y){
        super(new Image(gameProps.getProperty("gameObjects.driver.image")),
                Double.parseDouble(gameProps.getProperty("gameObjects.driver.radius")),
                Double.parseDouble(gameProps.getProperty("gameObjects.driver.walkSpeedX")),
                Double.parseDouble(gameProps.getProperty("gameObjects.driver.walkSpeedY")),
                Double.parseDouble(gameProps.getProperty("gameObjects.driver.health")) * 100,
                new Location(x, y));
        isWaitingForPassenger = false;
    }

    /**
     * Gets whether the driver is waiting for the passenger to come in the new taxi.
     *
     * @return true if driver is waiting, false otherwise.
     */
    public boolean getIsWaitingForPassenger(){return isWaitingForPassenger;}

    /**
     * Sets the waiting flag of the driver.
     *
     * @param isWaiting The waiting for passenger flag.
     */
    public void setWaitingForPassenger(boolean isWaiting){isWaitingForPassenger = isWaiting;}

    /**
     * Displays and moves the driver on the screen based on user input.
     *
     * @param gameProps The property file containing configuration.
     * @param input The user keyboard input.
     * @param taxi The taxi object.
     * @param inTripPassenger The passenger currently in the trip (if any).
     */
    public void showDriver(Properties gameProps, Input input, Taxi taxi, Passenger inTripPassenger){
        if (taxi.getHasDriver()){
            // In the taxi, moves with the taxi
            getLocation().setX(taxi.getLocation().getX());
            getLocation().setY(taxi.getLocation().getY());
        }
        else {
            if (!isWaitingForPassenger) {
                // Outside the taxi
                getImage().draw(getLocation().getX(), getLocation().getY());
                if (input.isDown(Keys.UP)) {
                    getLocation().setY(getLocation().getY() - getWalkYSpeed());
                }
                if (input.isDown(Keys.DOWN)) {
                    getLocation().setY(getLocation().getY() + getWalkYSpeed());
                }
                if (input.isDown(Keys.LEFT)) {
                    getLocation().setX(getLocation().getX() - getWalkXSpeed());
                }
                if (input.isDown(Keys.RIGHT)) {
                    getLocation().setX(getLocation().getX() + getWalkXSpeed());
                }
            }

            if (getLocation().distance(taxi.getLocation()) <=
                    Double.parseDouble(gameProps.getProperty("gameObjects.driver.taxiGetInRadius"))){
                // Have got in the new taxi, waiting for the in trip passenger to get in
                if (inTripPassenger != null){
                    isWaitingForPassenger = true;
                    getLocation().setX(taxi.getLocation().getX());
                    getLocation().setY(taxi.getLocation().getY());
                    if (inTripPassenger.moveTowardLocation(taxi.getLocation())){
                        taxi.setHasDriver(true);
                        isWaitingForPassenger = false;
                    }

                }
                else {
                    taxi.setHasDriver(true);
                    isWaitingForPassenger = false;
                }
            }
        }
    }

    /**
     * Ejects the driver from a location by setting the driver's location to a position nearby the location.
     *
     * @param location The location to be ejected from.
     */
    public void ejectTaxi(Location location){
        // Eject the driver out of the taxi when the taxi is damaged
        getLocation().setY(location.getY());
        getLocation().setX(location.getX() - EJECTED_DISTANCE);
    }
}
