import java.util.Properties;
import bagel.*;

/**
 * Represents an enemy car in the game.
 */
public class EnemyCar extends Car {
    private static final int CREATE_FACTOR = 400;

    /**
     * Constructs a new EnemyCar with the given parameters.
     *
     * @param image The image representing the car.
     * @param damage The damage the car can inflict.
     * @param radius The radius of the car.
     * @param health The initial health of the car.
     * @param location The initial location of the car.
     * @param ySpeed The vertical speed of the car.
     */
    public EnemyCar(Image image,double damage, double radius, double health, Location location, double ySpeed){
        super(image, damage, radius, health, location, ySpeed);
    }

    /**
     * Make a new vertical speed for the car after collision.
     *
     * @param gameProps The property file containing configuration.
     */
    public void makeNewSpeed(Properties gameProps) {
        // Generate a new random speed for the enemy car
        setYSpeed(MiscUtils.getRandomInt(Integer.parseInt(gameProps.getProperty("gameObjects.enemyCar.minSpeedY")),
                Integer.parseInt(gameProps.getProperty("gameObjects.enemyCar.maxSpeedY"))+1));
    }

    /**
     * Creates a new instance of EnemyCar based on the random creation condition.
     * The new enemy car is added to the list of active cars in the game.
     *
     * @param gameProps The property file containing configuration.
     */
    public static void create(Properties gameProps){
        if (MiscUtils.canSpawn(CREATE_FACTOR)){
            Image image = new Image(gameProps.getProperty("gameObjects.enemyCar.image"));
            double damage = Double.parseDouble(gameProps.getProperty("gameObjects.enemyCar.damage")) * 100;
            double radius = Double.parseDouble(gameProps.getProperty("gameObjects.enemyCar.radius"));
            double health = Double.parseDouble(gameProps.getProperty("gameObjects.enemyCar.health")) * 100;
            double x = Double.parseDouble(gameProps.getProperty(String.format("roadLaneCenter%d",
                    MiscUtils.getRandomInt(1,4))));
            double y = MiscUtils.selectAValue(getY1(), getY2());
            Location location = new Location(x, y);
            double ySpeed = MiscUtils.getRandomInt(Integer.parseInt(gameProps.getProperty("gameObjects.enemyCar.minSpeedY")),
                    Integer.parseInt(gameProps.getProperty("gameObjects.enemyCar.maxSpeedY"))+1);

            // Add the newly generated enemy car to the car list
            Car.getCarList().add(new EnemyCar(image, damage, radius, health, location, ySpeed));
        }
    }
}
