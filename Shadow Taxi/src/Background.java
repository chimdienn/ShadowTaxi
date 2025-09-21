import bagel.*;
import java.util.ArrayList;

/**
 * Represents a background image in the game.
 */
public class Background {
    private static final double MAX_Y_COORDINATE = 1152;

    private final Image IMAGE;
    private Location location;

    /**
     * Constructs a background object.
     * The background is positioned at the center of the screen.
     *
     * @param filename The filename of the image to load.
     */
    public Background(String filename) {
        IMAGE = new Image(filename);
        this.location = new Location(Window.getWidth()/2.0, Window.getHeight()/2.0); // Center of the screen
    }

    /**
     * Constructs a background object with position at the specified X and Y coordinates.
     *
     * @param filename The filename of the image to load.
     * @param x The X-coordinate of the background.
     * @param y The Y-coordinate of the background.
     */
    public Background(String filename, double x, double y) {
        IMAGE = new Image(filename);
        this.location = new Location(x, y);
    }

    /**
     * Render the background at the center of the screen.
     */
    public void drawCenter() {
        IMAGE.draw(Window.getWidth()/2.0, Window.getHeight()/2.0);
    }

    /**
     * Implement the logic to render and scroll two connected backgrounds for the gameplay.
     *
     * @param input The current keyboard input.
     * @param isRaining A flag indicating whether the raining background should be used.
     * @param gamePlayImages The list of background images used for gameplay.
     * @param ySpeed The number of pixels backgrounds move horizontally per keyboard input.
     */
    public static void drawGamePlayBackgrounds(Input input, boolean isRaining, ArrayList<Background> gamePlayImages,
                                               double ySpeed){
        if (!isRaining) {
            // Display the sun background when it is sunny
            gamePlayImages.get(0).IMAGE.draw(gamePlayImages.get(0).location.getX(),
                    gamePlayImages.get(0).location.getY());
            gamePlayImages.get(1).IMAGE.draw(gamePlayImages.get(1).location.getX(),
                    gamePlayImages.get(1).location.getY());
        }
        else {
            // Display the rain background when it is raining
            gamePlayImages.get(2).IMAGE.draw(gamePlayImages.get(2).location.getX(),
                    gamePlayImages.get(2).location.getY());
            gamePlayImages.get(3).IMAGE.draw(gamePlayImages.get(3).location.getX(),
                    gamePlayImages.get(3).location.getY());
        }

        // Scroll down all backgrounds when UP key is pressed
        if (input.isDown(Keys.UP)) {
            gamePlayImages.get(0).location.setY(gamePlayImages.get(0).location.getY() + ySpeed);
            gamePlayImages.get(1).location.setY(gamePlayImages.get(1).location.getY() + ySpeed);
            gamePlayImages.get(2).location.setY(gamePlayImages.get(2).location.getY() + ySpeed);
            gamePlayImages.get(3).location.setY(gamePlayImages.get(3).location.getY() + ySpeed);
        }

        // Relocate the backgrounds when out of the screen
        if (gamePlayImages.get(0).location.getY() >= MAX_Y_COORDINATE) {
            gamePlayImages.get(0).location.setY(gamePlayImages.get(1).location.getY() - Window.getHeight());
        }
        if (gamePlayImages.get(1).location.getY() >= MAX_Y_COORDINATE) {
            gamePlayImages.get(1).location.setY(gamePlayImages.get(0).location.getY() - Window.getHeight());
        }
        if (gamePlayImages.get(2).location.getY() >= MAX_Y_COORDINATE) {
            gamePlayImages.get(2).location.setY(gamePlayImages.get(3).location.getY() - Window.getHeight());
        }
        if (gamePlayImages.get(3).location.getY() >= MAX_Y_COORDINATE) {
            gamePlayImages.get(3).location.setY(gamePlayImages.get(2).location.getY() - Window.getHeight());
        }
    }
}
