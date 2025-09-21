import bagel.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Properties;

/**
 *
 * @author Quy Trong Duc Tran ID: 1489523
 */
public class ShadowTaxi extends AbstractGame {

    private final Properties GAME_PROPS;
    private final Properties MESSAGE_PROPS;
    private final String[][] GAME_OBJECTS;

    private Home homeScreen;
    private PlayerInformation playerInfoScreen;
    private GamePlay gamePlayScreen;
    private GameEnd gameEndScreen;

    private int currentScreen;

    /**
     * Constructs a new ShadowTaxi game instance, setting up game properties, message properties, and world objects.
     *
     * @param gameProps The property file containing configuration for the game.
     * @param messageProps The property file containing messages to display in the game.
     * @param worldFile A 2D array representing the world file.
     */
    public ShadowTaxi(Properties gameProps, Properties messageProps, String[][] worldFile) {
        super(Integer.parseInt(gameProps.getProperty("window.width")),
                Integer.parseInt(gameProps.getProperty("window.height")),
                messageProps.getProperty("home.title"));

        this.GAME_PROPS = gameProps;
        this.MESSAGE_PROPS = messageProps;
        this.GAME_OBJECTS = worldFile;
        this.homeScreen = new Home(gameProps, messageProps);
        this.currentScreen = 1;
    }

    /**
     * Render the relevant screens and game objects based on current game state and keyboard input
     *
     * @param input The current keyboard input.
     */
    @Override
    protected void update(Input input) {
        // Handle screen stages
        switch (currentScreen) {
            // Home Screen
            case 1:
                homeScreen.showHome();
                if (input.wasPressed(Keys.ENTER)){
                    playerInfoScreen = new PlayerInformation(GAME_PROPS, MESSAGE_PROPS);
                    currentScreen++; // Next screen
                }
                break;

            // Player Information Screen
            case 2:
                playerInfoScreen.showPlayerInformation(input);

                if (input.wasPressed(Keys.ENTER)){
                    gamePlayScreen = new GamePlay(GAME_PROPS, MESSAGE_PROPS, GAME_OBJECTS,
                            playerInfoScreen.getPlayerName());
                    currentScreen++; // Next screen
                }
                break;

            // Game Play Screen
            case 3:
                gamePlayScreen.showGamePlay(input);

                if (gamePlayScreen.getGameCompleted()){
                    gameEndScreen = new GameEnd(GAME_PROPS, MESSAGE_PROPS, gamePlayScreen.getIsWon());
                    currentScreen++; // Game ends, move to next screen
                }
                break;

            // Game End Screen
            case 4:
                gameEndScreen.showGameEnd();
                if (input.wasPressed(Keys.SPACE)){
                    // Commence a new game
                    homeScreen = new Home(GAME_PROPS, MESSAGE_PROPS);
                    currentScreen = 1;
                }
                break;
        }

        if (input.wasPressed(Keys.ESCAPE)){
            Window.close(); // Game closes when the ESCAPE is pressed
        }

    }

    /**
     * Initializes game properties, messages, and world data from configuration files, then starts the game.
     *
     * @param args The command-line arguments passed to the program.
     */
    public static void main(String[] args) {
        Properties game_props = IOUtils.readPropertiesFile("res/app.properties");
        Properties message_props = IOUtils.readPropertiesFile("res/message_en.properties");
        String[][] world_file = IOUtils.readCommaSeparatedFile("res/gameObjects.csv");
        ShadowTaxi game = new ShadowTaxi(game_props, message_props, world_file);
        game.run();
    }
}
