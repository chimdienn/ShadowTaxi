import bagel.*;

/**
 * Represents a collectible invincible power in the game.
 */
public class InvinciblePower extends Token{
    /**
     * Constructs a new InvinciblePower object.
     *
     * @param image The image representing the invincible power.
     * @param radius The radius of the invincible power.
     * @param x The X-coordinate of the invincible power.
     * @param y The Y-coordinate of the invincible power.
     */
    public InvinciblePower(Image image, double radius, double x, double y){
        super(image, radius, x, y);
    }
}
