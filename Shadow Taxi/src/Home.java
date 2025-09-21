import bagel.*;
import java.util.Properties;

/**
 * Represents the home screen of the game.
 */
public class Home extends Screen {
    private final Background BACKGROUND_IMAGE;
    private Font font;

    /**
     * Constructs the home screen with the specified game properties and message properties.
     *
     * @param gameProps The property file containing configuration.
     * @param messageProps The property file containing messages.
     */
    public Home(Properties gameProps, Properties messageProps){
        super(gameProps, messageProps);
        BACKGROUND_IMAGE = new Background(gameProps.getProperty("backgroundImage.home"));
        font = null;
    }

    /**
     * Renders the home screen, displaying the title and game instructions.
     */
    public void showHome(){
        BACKGROUND_IMAGE.drawCenter();

        // Set up font and print title
        font = new Font(getGameProps().getProperty("font"),
                Integer.parseInt(getGameProps().getProperty("home.title.fontSize")));
        font.drawString(getMessageProps().getProperty("home.title"),
                (Window.getWidth() - font.getWidth(getMessageProps().getProperty("home.title")))/2,
                Integer.parseInt(getGameProps().getProperty("home.title.y")));

        // Set up font and print instruction
        font = new Font(getGameProps().getProperty("font"),
                Integer.parseInt(getGameProps().getProperty("home.instruction.fontSize")));
        font.drawString(getMessageProps().getProperty("home.instruction"),
                (Window.getWidth() - font.getWidth(getMessageProps().getProperty("home.instruction")))/2,
                Integer.parseInt(getGameProps().getProperty("home.instruction.y")));

    }
}
