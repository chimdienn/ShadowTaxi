import bagel.*;

/**
 * Represents a collectible coin in the game.
 */
public class Coin extends Token {
    /**
     * Constructs a new Coin object.
     *
     * @param image The image representing the coin
     * @param radius The radius of the coin.
     * @param x The X-coordinate of the coin.
     * @param y The Y-coordinate of the coin.
     */
    public Coin(Image image, double radius, double x, double y){
        super(image, radius, x, y);
    }
}
