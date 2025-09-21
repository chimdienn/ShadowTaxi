import java.util.ArrayList;
import bagel.*;

/**
 * Represents an item that has an animation effect.
 * These items are generated during a collision of other entities.
 * This class also manages a static list of animation items.
 */
public abstract class AnimationItem {
    private final Image IMAGE;
    private int remainingFrames;
    private Location location;

    // A static list of all currently active animation items
    private static ArrayList<AnimationItem> animationList;

    /**
     * Constructs a new AnimationItem.
     * The new AnimationItem added to the animation list.
     *
     * @param image the image to be displayed
     * @param remainingFrames the number of frames this item should be displayed
     * @param location the location of the item on the screen
     */
    public AnimationItem(Image image, int remainingFrames, Location location){
        this.IMAGE = image;
        this.remainingFrames = remainingFrames;
        this.location = location;
        animationList.add(this); // Add the item to the active item list
    }

    /**
     * Initializes the animation list.
     */
    public static void makeNewAnimationList(){
        animationList = new ArrayList<>();
    }

    /**
     * Displays all active animation items, removing items with zero remaining frames.
     * Moves items vertically based on user input and decreases the remaining frame count.
     *
     * @param input the user keyboard input
     * @param ySpeed The number of pixels items move vertically per keyboard input.
     */
    public static void showAnimationItems(Input input, double ySpeed){
        // Remove the item which have rendered all its frames.
        animationList.removeIf(item -> item.remainingFrames == 0);
        for (AnimationItem item : animationList){
            item.IMAGE.draw(item.location.getX(), item.location.getY());
            if (input.isDown(Keys.UP)) {
                // Item move downward when UP key is pressed
                item.location.setY(item.location.getY() + ySpeed);
            }
            item.remainingFrames -= 1;
        }
    }
}
