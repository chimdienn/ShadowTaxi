import java.util.Properties;

/**
 * Represents an abstract screen in the game
 */
public abstract class Screen {
    private final Properties GAME_PROPS;
    private final Properties MESSAGE_PROPS;

    /**
     * Constructs a screen with the specified game properties and message properties.
     *
     * @param gameProps The property file containing configuration for the game.
     * @param messageProps The property file containing messages to display on the screen.
     */
    public Screen(Properties gameProps, Properties messageProps){
        this.GAME_PROPS = gameProps;
        this.MESSAGE_PROPS = messageProps;
    }

    /**
     * Get the property file containing configuration of the game.
     *
     * @return The game property file.
     */
    public Properties getGameProps(){
        return GAME_PROPS;
    }

    /**
     * Get the property file containing messages of the game.
     *
     * @return The message property file.
     */
    public Properties getMessageProps() {
        return MESSAGE_PROPS;
    }
}
