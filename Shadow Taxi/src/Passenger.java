import bagel.*;
import java.util.Properties;

/**
 * Represents a passenger character in the game.
 */
public class Passenger extends Character {
    private static final double EARNINGS_DISTANCE_TO_PASSENGER = 100;
    private static final double PRIORITY_DISTANCE_TO_PASSENGER = 30;
    private static final int IN_RAIN_PRIORITY = 1;
    private static final double EJECTED_DISTANCE = 100;

    private final boolean HAS_UMBRELLA;

    private double yDistance;
    private EndFlag endFlag;

    private int current_priority;
    private int original_priority;

    private boolean usedCoin;
    private double earnings;

    /**
     * Constructs a new Passenger with the given parameters.
     *
     * @param filename The filename of the image representing the passenger.
     * @param radius The radius of the passenger.
     * @param walkXSpeed The horizontal speed of the passenger.
     * @param walkYSpeed The vertical speed of the passenger.
     * @param health The initial health of the passenger.
     * @param has_umbrella An integer indicating if the passenger has an umbrella (1 for yes, 0 for no).
     * @param x The X-coordinate of the passenger's location.
     * @param y The Y-coordinate of the passenger's location.
     * @param endFlag The flag representing the passenger's destination.
     */
    public Passenger(String filename, double radius, double walkXSpeed, double walkYSpeed, double health,
                     int has_umbrella, double x, double y, EndFlag endFlag) {
        super(new Image(filename), radius, walkXSpeed, walkYSpeed, health, new Location(x, y));
        this.HAS_UMBRELLA = has_umbrella == 1;
        this.endFlag = endFlag;
    }

    /**
     * Gets the end flag (destination) of the passenger.
     *
     * @return the end flag of the passenger
     */
    public EndFlag getEndFlag() {
        return endFlag;
    }

    /**
     * Gets the current priority of the passenger.
     *
     * @return the current priority of the passenger
     */
    public int getPriority() {
        return current_priority;
    }

    /**
     * Gets the current earnings of the passenger.
     *
     * @return the current earnings of the passenger
     */
    public double getEarnings() {
        return earnings;
    }

    /**
     * Read and return an array of passengers from the provided game objects.
     *
     * @param gameProps The property filename containing configuration.
     * @param gameObjects Game objects array containing all passengers' information.
     * @return An array of Passenger objects initialized with the specified properties and game object data.
     */
    public static Passenger[] readPassengers(Properties gameProps, String[][] gameObjects) {
        int count = 0;

        // Count the total number of passengers
        for (String[] line : gameObjects) {
            if (line[0].equals("PASSENGER")) {
                count++;
            }
        }

        Passenger[] passengers = new Passenger[count]; // Initialize the passenger array
        int index = 0;
        for (String[] line : gameObjects) {
            if (line[0].equals("PASSENGER")) {
                // Add new passenger object to the array
                passengers[index] = new Passenger(gameProps.getProperty("gameObjects.passenger.image"),
                        Double.parseDouble(gameProps.getProperty("gameObjects.passenger.radius")),
                        Double.parseDouble(gameProps.getProperty("gameObjects.passenger.walkSpeedX")),
                        Double.parseDouble(gameProps.getProperty("gameObjects.passenger.walkSpeedY")),
                        Double.parseDouble(gameProps.getProperty("gameObjects.passenger.health")) * 100,
                        Integer.parseInt(line[6]),
                        Double.parseDouble(line[1]), Double.parseDouble(line[2]),
                        new EndFlag(gameProps.getProperty("gameObjects.tripEndFlag.image"),
                                Double.parseDouble(gameProps.getProperty("gameObjects.tripEndFlag.radius")),
                                Double.parseDouble(line[4]),
                                Double.parseDouble(line[2]) - Double.parseDouble(line[5])));
                passengers[index].original_priority = Integer.parseInt(line[3]);
                passengers[index].current_priority = passengers[index].original_priority;
                passengers[index].usedCoin = false;
                passengers[index].yDistance= Double.parseDouble(line[5]);
                passengers[index].earnings = passengers[index].calculateEarnings(gameProps);
                index++;
            }
        }
        return passengers;
    }

    /**
     * Renders and moves passengers based on user input, excluding those in the current or last trip.
     * Shows earnings and priority of passengers based on game properties.
     *
     * @param input The user current keyboard input.
     * @param passengers The array of all passengers in the game.
     * @param taxi The taxi object.
     * @param driver The driver character.
     * @param isRaining A flag indicating whether it is raining in the game.
     * @param gameProps The property file containing configuration.
     * @param inTripPassenger Passenger currently in a trip.
     * @param lastTripPassenger Passenger who has just completed a trip.
     * @param ySpeed The number of pixels passengers move vertically per keyboard input.
     */
    public static void showSelectedPassengers(Input input, Passenger[] passengers, Taxi taxi, Driver driver,
                                              boolean isRaining, Properties gameProps, Passenger inTripPassenger,
                                              Passenger lastTripPassenger, double ySpeed) {
        // Render passengers' images and earnings
        for (Passenger passenger : passengers) {
            if (passenger != lastTripPassenger) {
                if (isRaining) {
                    // Implement passenger in rain logic
                    if (!(passenger.HAS_UMBRELLA)) {
                        passenger.current_priority = IN_RAIN_PRIORITY;
                    } else {
                        passenger.current_priority = passenger.original_priority;
                    }
                } else {
                    passenger.current_priority = passenger.original_priority;
                }
                // Calculate the current earning of the passenger
                passenger.earnings = passenger.calculateEarnings(gameProps);
            }

            if (passenger == inTripPassenger) {
                if (!taxi.getHasDriver()) {
                    // Implement walking movement for in trip passenger after being ejected from the taxi
                    passenger.getImage().draw(passenger.getLocation().getX(), passenger.getLocation().getY());
                    if (!driver.getIsWaitingForPassenger()) {
                        if (input.isDown(Keys.UP)) {
                            passenger.getLocation().setY(passenger.getLocation().getY() - passenger.getWalkYSpeed());
                        }
                        if (input.isDown(Keys.DOWN)) {
                            passenger.getLocation().setY(passenger.getLocation().getY() + passenger.getWalkYSpeed());
                        }
                        if (input.isDown(Keys.LEFT)) {
                            passenger.getLocation().setX(passenger.getLocation().getX() - passenger.getWalkXSpeed());
                        }
                        if (input.isDown(Keys.RIGHT)) {
                            passenger.getLocation().setX(passenger.getLocation().getX() + passenger.getWalkXSpeed());
                        }
                    }
                }
                continue; // Exclude rendering the in-trip passenger's image and earnings
            }

            passenger.getImage().draw(passenger.getLocation().getX(), passenger.getLocation().getY());

            if (passenger == lastTripPassenger) {
                continue; // Exclude rendering the last-trip passenger's earnings
            }
            // Render passengers' earnings and priorities
            Font font = new Font(gameProps.getProperty("font"),
                    Integer.parseInt(gameProps.getProperty("gameObjects.passenger.fontSize")));
            font.drawString(Double.toString(passenger.earnings),
                    passenger.getLocation().getX() - EARNINGS_DISTANCE_TO_PASSENGER, passenger.getLocation().getY());
            font.drawString(Integer.toString(passenger.current_priority),
                    passenger.getLocation().getX() - PRIORITY_DISTANCE_TO_PASSENGER, passenger.getLocation().getY());

        }

        if (input.isDown(Keys.UP)){
            // Apply vertical movement to passengers and their flags
            for (Passenger passenger : passengers) {
                passenger.endFlag.getLocation().setY(passenger.endFlag.getLocation().getY() + ySpeed);
                if (passenger == inTripPassenger) {
                    if (!driver.getIsWaitingForPassenger()) {
                        continue;
                    }
                }
                passenger.getLocation().setY(passenger.getLocation().getY() + ySpeed);
            }
        }
    }

    /**
     * Moves the passenger toward a specific location.
     *
     * @param location The destination location that the passenger is moving toward.
     * @return true if the passenger has reached the location, false otherwise.
     */
    public boolean moveTowardLocation(Location location){
        if (this.getLocation().distance(location) != 0) {
            // Update movement in X direction
            if (this.getLocation().getX() < location.getX()) {
                this.getLocation().setX(this.getLocation().getX() + this.getWalkXSpeed());
            }
            else if (this.getLocation().getX() > location.getX()) {
                this.getLocation().setX(this.getLocation().getX() - this.getWalkXSpeed());
            }

            // Update movement in Y direction
            if (this.getLocation().getY() < location.getY()) {
                this.getLocation().setY(this.getLocation().getY() + this.getWalkYSpeed());
            }
            else if (this.getLocation().getY() > location.getY()) {
                this.getLocation().setY(this.getLocation().getY() - this.getWalkYSpeed());
            }
        }
        // Return true if the passenger has reached the location, false otherwise
        else {

            return true;
        }
        return false;
    }

    /**
     * Calculate the expected earnings of the passenger based on travelled distance and priority
     *
     * @param gameProps The property object containing configuration.
     * @return the calculated earnings of the passenger.
     */
    private double calculateEarnings(Properties gameProps) {
        return Double.parseDouble(gameProps.getProperty("trip.rate.priority" + current_priority)) +
                yDistance *  Double.parseDouble(gameProps.getProperty("trip.rate.perY"));
    }

    /**
     * Calculate the penalty of the passenger based on current passenger's and their flag's coordinates
     *
     * @param gameProps The property object containing configuration.
     * @return The calculated penalty of the passenger.
     */
    public double calculatePenalty (Properties gameProps){
        // Check if passenger has moved beyond the end flag
        if (getLocation().getY() < endFlag.getLocation().getY()){
            // Check if the distance is greater than the end flag's radius
            if (getLocation().distance(endFlag.getLocation()) > endFlag.getRadius()){
                return Double.parseDouble(gameProps.getProperty("trip.penalty.perY")) *
                        (endFlag.getLocation().getY() - getLocation().getY());
            }
        }
        return 0; // No penalty
    }


    /**
     * Applies the effect of the coin on the passenger.
     */
    public void useCoin () {
        if (!usedCoin) {
            if (original_priority > 1){
                original_priority -= 1;
                usedCoin = true;
            }
        }
    }

    /**
     * Returns the minimum health value among all passengers.
     *
     * @param passengers The array of passengers.
     * @return The minimum health value among the passengers.
     */
    public static double getMinHealth (Passenger[] passengers){
        double minHealth = passengers[0].getHealth();
        for (Passenger passenger : passengers){
            if (passenger.getHealth() < minHealth) {
                minHealth = passenger.getHealth();
            }
        }
        return minHealth;
    }

    /**
     * Ejects the passenger from a location by setting the driver's location to a position nearby the location.
     *
     * @param location The location to be ejected from.
     */
    public void ejectTaxi(Location location){
        // Eject the passenger out of the taxi when the taxi is damaged
        getLocation().setY(location.getY());
        getLocation().setX(location.getX() - EJECTED_DISTANCE);
    }
}
