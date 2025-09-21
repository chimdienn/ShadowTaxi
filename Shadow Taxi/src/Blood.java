import java.util.Properties;
import bagel.*;

/**
 * Represents a blood animation item in the game.
 */
public class Blood extends AnimationItem {
    /**
     * Constructs a new Blood animation item using properties from the game configuration.
     *
     * @param gameProps The property file containing configuration.
     * @param x The x-coordinate of the blood's location
     * @param y The y-coordinate of the blood's location
     */
    public Blood(Properties gameProps, double x, double y){
        super(new Image(gameProps.getProperty("gameObjects.blood.image")),
                Integer.parseInt(gameProps.getProperty("gameObjects.blood.ttl")), new Location(x, y));
    }
}