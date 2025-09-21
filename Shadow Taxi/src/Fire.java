import java.util.Properties;
import bagel.*;

/**
 * Represents a fire animation item in the game.
 */
public class Fire extends AnimationItem {
    /**
     * Constructs a new Fire animation item using properties from the game configuration.
     *
     * @param gameProps The property file containing configuration.
     * @param x The x-coordinate of the fire's location
     * @param y The y-coordinate of the fire's location
     */
    public Fire(Properties gameProps, double x, double y){
        super(new Image(gameProps.getProperty("gameObjects.fire.image")),
                Integer.parseInt(gameProps.getProperty("gameObjects.fire.ttl")), new Location(x, y));
    }
}
