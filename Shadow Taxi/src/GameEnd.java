import bagel.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Properties;

/**
 * Represents the game end screen of the game.
 */
public class GameEnd extends Screen{
    private static final double DISTANCE_BETWEEN_SCORE_LINES = 40;

    private final Background GAME_END_IMAGE;
    private final boolean IS_WON;
    private Font font;

    /**
     * Constructs a new GameEnd screen with the specified game properties and message properties.
     *
     * @param gameProps The property file containing configuration.
     * @param messageProps The property file containing messages.
     * @param isWon A flag indicating if the game was won or lost.
     */
    public GameEnd(Properties gameProps, Properties messageProps, boolean isWon){
        super(gameProps, messageProps);
        GAME_END_IMAGE = new Background(gameProps.getProperty("backgroundImage.gameEnd"));
        IS_WON = isWon;
        font = null;
    }

    /**
     * Displays the game end screen, including the top scores and win/loss status.
     */
    public void showGameEnd(){
        GAME_END_IMAGE.drawCenter();

        font = new Font(getGameProps().getProperty("font"),
                Integer.parseInt(getGameProps().getProperty("gameEnd.scores.fontSize")));

        // Print top score and game-end details, implementing the win/loss detection
        printTopScores();

        font.drawString(getMessageProps().getProperty("gameEnd.highestScores"),
                (Window.getWidth() - font.getWidth(getMessageProps().getProperty("gameEnd.highestScores")))/2,
                Double.parseDouble(getGameProps().getProperty("gameEnd.scores.y")));

        font = new Font(getGameProps().getProperty("font"),
                Integer.parseInt(getGameProps().getProperty("gameEnd.status.fontSize")));
        if (IS_WON) {
            font.drawString(getMessageProps().getProperty("gameEnd.won"),
                    (Window.getWidth() - font.getWidth(getMessageProps().getProperty("gameEnd.won")))/2,
                    Double.parseDouble(getGameProps().getProperty("gameEnd.status.y")));
        }
        else {
            font.drawString(getMessageProps().getProperty("gameEnd.lost"),
                    (Window.getWidth() - font.getWidth(getMessageProps().getProperty("gameEnd.lost")))/2,
                    Double.parseDouble(getGameProps().getProperty("gameEnd.status.y")));
        }
    }

    /**
     * Reads and renders the top 5 scores from the CSV file specified in the game properties.
     */
    private void printTopScores() {
        // Read the score file into a 2D array
        String[][] results = IOUtils.readCommaSeparatedFile(getGameProps().getProperty("gameEnd.scoresFile"));

        // Sort the array in descending order
        Arrays.sort(results, new Comparator<String[]>() {
            @Override
            public int compare(String[] a, String[] b) {
                // Convert the score (second string) to a double and compare
                double scoreA = Double.parseDouble(a[1]);
                double scoreB = Double.parseDouble(b[1]);
                // For descending order, reverse the comparison
                return Double.compare(scoreB, scoreA);
            }
        });
        double yCoordinate = Double.parseDouble(getGameProps().getProperty("gameEnd.scores.y"));

        // Print the top players and their scores in order
        for (int i = 0; i < 5 && i < results.length; i++) {
            yCoordinate += DISTANCE_BETWEEN_SCORE_LINES;
            String result = results[i][0] + " - " + results[i][1];
            font.drawString(result,  (Window.getWidth() - font.getWidth(result))/2, yCoordinate);
        }
    }
}
