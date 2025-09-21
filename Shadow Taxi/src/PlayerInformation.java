import bagel.*;

import java.util.Properties;

/**
 * Represents the player information screen of the game.
 */
public class PlayerInformation extends Screen{
    private final Background PLAYER_INFO_IMAGE;

    private Font font;
    private String playerName;

    /**
     * Constructs the player information screen with the specified game properties and message properties.
     *
     * @param gameProps The property file containing configuration.
     * @param messageProps The property file containing messages.
     */
    public PlayerInformation(Properties gameProps, Properties messageProps){
        super(gameProps, messageProps);
        PLAYER_INFO_IMAGE = new Background(gameProps.getProperty("backgroundImage.playerInfo"));
        font = null;
        playerName = "";
    }

    /**
     * Gets the name of the player.
     *
     * @return The player's name.
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Render the Player Information Screen and player's name
     * based on the keyboard input given by the user
     * @param input The current keyboard input.
     */
    public void showPlayerInformation(Input input){
        PLAYER_INFO_IMAGE.drawCenter();

        // Set up font and print instruction
        font = new Font(getGameProps().getProperty("font"),
                Integer.parseInt(getGameProps().getProperty("playerInfo.fontSize")));
        font.drawString(getMessageProps().getProperty("playerInfo.playerName"),
                (Window.getWidth() - font.getWidth(getMessageProps().getProperty("playerInfo.playerName")))/2,
                Integer.parseInt(getGameProps().getProperty("playerInfo.playerName.y")));


        String newChar = MiscUtils.getKeyPress(input);
        if (newChar != null){
            playerName += newChar; // Concat input to player's name
        }

        if (input.wasPressed(Keys.DELETE)|| input.wasPressed(Keys.BACKSPACE)){
            if (!playerName.isEmpty()){
                playerName = playerName.substring(0, playerName.length() - 1); // Remove the last character
            }
        }

        // Display player's name
        font.drawString(playerName, (Window.getWidth() - font.getWidth(playerName)) / 2,
                Integer.parseInt(getGameProps().getProperty("playerInfo.playerNameInput.y")),
                new DrawOptions().setBlendColour(0,0,0));

        // Print instruction
        font.drawString(getMessageProps().getProperty("playerInfo.start"),
                (Window.getWidth() - font.getWidth(getMessageProps().getProperty("playerInfo.start")))/2,
                Integer.parseInt(getGameProps().getProperty("playerInfo.start.y")));

    }
}
