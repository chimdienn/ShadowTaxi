import bagel.*;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Represents a generic token in the game.
 */
public abstract class Token {
    private final Image IMAGE;
    private final double RADIUS;
    private boolean isUsed;
    private Location location;

    /**
     * Constructs a new Token.
     *
     * @param image The image representing the token.
     * @param radius The radius of the token.
     * @param x The X-coordinate of the token.
     * @param y The Y-coordinate of the token.
     */
    public Token(Image image, double radius, double x, double y){
        this.IMAGE = image;
        this.RADIUS = radius;
        this.isUsed = false;
        this.location = new Location(x, y);
    }

    /**
     * Gets the radius of the token.
     *
     * @return The radius of the token.
     */
    public double getRadius() {
        return RADIUS;
    }

    /**
     * Sets whether the token has been used or not.
     *
     * @param isUsed A boolean indicating whether the object is marked as used.
     */
    public void setIsUsed(boolean isUsed) {
        this.isUsed = isUsed;
    }

    /**
     * Gets the current location of the token.
     *
     * @return The location of the token.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Reads token data from the provided game properties and game object definitions.
     * Creates and returns a list of all tokens.
     *
     * @param gameProps The property file containing configuration.
     * @param gameObjects An 2D array representing game object data.
     * @return A list of tokens in the game
     */
    public static ArrayList<Token> readToken(Properties gameProps, String[][] gameObjects){
        ArrayList<Token> tokenList = new ArrayList<>();
        for (String[] line : gameObjects) {
            // Check if the game object is token
            if (line[0].equals("COIN") || line[0].equals("INVINCIBLE_POWER")) {
                double x = Double.parseDouble(line[1]);
                double y = Double.parseDouble(line[2]);
                if (line[0].equals("COIN")){
                    // Add the new coin to token list
                    tokenList.add(new Coin(new Image(gameProps.getProperty("gameObjects.coin.image")),
                            Double.parseDouble(gameProps.getProperty("gameObjects.coin.radius")), x, y));
                }
                else {
                    // Add the new invincible power to the token list
                    tokenList.add(new InvinciblePower(new Image(gameProps.getProperty("gameObjects.invinciblePower.image")),
                            Double.parseDouble(gameProps.getProperty("gameObjects.invinciblePower.radius")), x, y));
                }
            }
        }
        return tokenList;
    }

    /**
     * Renders and scrolls the list of tokens on the screen.
     * Tokens that have been used are removed from the list.
     *
     * @param input The user keyboard input.
     * @param tokenList The list of tokens to be rendered.
     * @param ySpeed The number of pixels tokens move vertically per keyboard input.
     */
    public static void showTokens(Input input, ArrayList<Token> tokenList, double ySpeed) {
        tokenList.removeIf(token -> token.isUsed);
        // Render tokens' images
        for (Token token: tokenList) {
            token.IMAGE.draw(token.location.getX(), token.location.getY());
        }

        // Apply tokens' vertical movement
        if (input.isDown(Keys.UP)){
            for (Token token: tokenList) {
                token.location.setY(token.location.getY() + ySpeed);
            }
        }
    }
}

