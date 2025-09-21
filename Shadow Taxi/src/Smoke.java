import java.util.Properties;
import bagel.*;

/**
 * Represents a smoke animation item in the game.
 */
public class Smoke extends AnimationItem {
    /**
     * Constructs a new Smoke animation item using properties from the game configuration.
     *
     * @param gameProps The property file containing configuration.
     * @param x The x-coordinate of the smoke's location
     * @param y The y-coordinate of the smoke's location
     */
    public Smoke(Properties gameProps, double x, double y){
        super(new Image(gameProps.getProperty("gameObjects.smoke.image")),
                Integer.parseInt(gameProps.getProperty("gameObjects.smoke.ttl")), new Location(x, y));
    }
}
