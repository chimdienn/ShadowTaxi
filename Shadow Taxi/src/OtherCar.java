import java.util.Properties;
import bagel.*;

/**
 * Represents the OtherCar car type in the game.
 */
public class OtherCar extends Car {
    private static final int CREATE_FACTOR = 200;

    /**
     * Constructs a new OtherCar with the given parameters.
     *
     * @param image The image representing the car.
     * @param damage The damage the car can inflict.
     * @param radius The radius of the car.
     * @param health The initial health of the car.
     * @param location The initial location of the car.
     * @param ySpeed The vertical speed of the car.
     */
    public OtherCar(Image image,double damage, double radius, double health, Location location, double ySpeed){
        super(image, damage, radius, health, location, ySpeed);
    }

    /**
     * Make a new vertical speed for the car after collision.
     *
     * @param gameProps The property file containing configuration.
     */
    @Override
    public void makeNewSpeed(Properties gameProps) {
        // Generate new random speed for the car
        setYSpeed(MiscUtils.getRandomInt(Integer.parseInt(gameProps.getProperty("gameObjects.otherCar.minSpeedY")),
                Integer.parseInt(gameProps.getProperty("gameObjects.otherCar.maxSpeedY"))+1));
    }

    /**
     * Creates a new instance of OtherCar based on the random creation condition.
     * The new car is added to the list of active cars in the game.
     *
     * @param gameProps The property file containing configuration.
     */
    public static void create(Properties gameProps){
        if (MiscUtils.canSpawn(CREATE_FACTOR)){
            Image image = new Image(String.format(gameProps.getProperty("gameObjects.otherCar.image"),
                    MiscUtils.getRandomInt(1,3)));
            double damage = Double.parseDouble(gameProps.getProperty("gameObjects.otherCar.damage")) * 100;
            double radius = Double.parseDouble(gameProps.getProperty("gameObjects.otherCar.radius"));
            double health = Double.parseDouble(gameProps.getProperty("gameObjects.otherCar.health")) * 100;
            double x = Double.parseDouble(gameProps.getProperty(String.format("roadLaneCenter%d",
                    MiscUtils.getRandomInt(1,4))));
            double y = MiscUtils.selectAValue(getY1(), getY2());
            Location location = new Location(x, y);
            double ySpeed = MiscUtils.getRandomInt(Integer.parseInt(gameProps.getProperty("gameObjects.otherCar.minSpeedY")),
                    Integer.parseInt(gameProps.getProperty("gameObjects.otherCar.maxSpeedY"))+1);

            // Add the newly generated car to the car list
            Car.getCarList().add(new OtherCar(image, damage, radius, health, location, ySpeed));
        }
    }
}
