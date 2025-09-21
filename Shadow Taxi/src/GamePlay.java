import bagel.*;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Represents the gameplay screen of the game.
 */
public class GamePlay extends Screen{
    private static final double EARNING_DISTANCE_TO_TRIP_INFO = 30;
    private static final double PRIORITY_DISTANCE_TO_TRIP_INFO = 60;
    private static final double PENALTY_DISTANCE_TO_TRIP_INFO = 90;

    private ArrayList<Background> gamePlayImages; // Containing sunny and raining backgrounds

    private final double X_SPEED;
    private final double Y_SPEED;
    private final double TARGET;
    private final int MAX_FRAMES;
    private final int MAX_COIN_TIME;
    private final int MAX_INVINCIBLE_TIME;
    private final String PLAYER_NAME;

    private Taxi taxi;
    private Driver driver;
    private Passenger[] passengers;
    private ArrayList<Token> tokenList;
    private Font font;

    private boolean inTrip;
    private boolean lastTripCompleted;
    private boolean gameCompleted;
    private boolean isWon;

    private Passenger waitingPassenger;
    private Passenger inTripPassenger;
    private Passenger lastTripPassenger;

    private double currentScore;
    private int remainingFrames;
    private int remainingLostFrames;
    private int coinTimer;
    private int invincibleTimer;
    private double penalty;

    private boolean isRaining;
    private final String[][] WEATHER_CONDITION;

    /**
     * Initializes a new game session with game properties, message properties, game objects, and the player's name.
     *
     * @param gameProps The property file containing configuration.
     * @param messageProps The property file containing game messages.
     * @param gameObjects A 2D array containing game objects and their properties.
     * @param playerName The name of the player.
     */
    public GamePlay(Properties gameProps, Properties messageProps, String[][] gameObjects, String playerName){
        super(gameProps, messageProps);

        // Add sun and rain backgrounds to the background list
        gamePlayImages = new ArrayList<>();
        gamePlayImages.add(new Background(gameProps.getProperty("backgroundImage.sunny"),
                Window.getWidth()/2.0, Window.getHeight()/2.0));
        gamePlayImages.add(new Background(gameProps.getProperty("backgroundImage.sunny"),
                Window.getWidth()/2.0, -Window.getHeight()/2.0));
        gamePlayImages.add(new Background(gameProps.getProperty("backgroundImage.raining"),
                Window.getWidth()/2.0, Window.getHeight()/2.0));
        gamePlayImages.add(new Background(gameProps.getProperty("backgroundImage.raining"),
                Window.getWidth()/2.0, -Window.getHeight()/2.0));

        X_SPEED = Double.parseDouble(gameProps.getProperty("gameObjects.taxi.speedX"));
        Y_SPEED = Double.parseDouble(gameProps.getProperty("gameObjects.taxi.speedY"));
        TARGET = Double.parseDouble(gameProps.getProperty("gamePlay.target"));
        MAX_FRAMES = Integer.parseInt(gameProps.getProperty("gamePlay.maxFrames"));
        MAX_COIN_TIME = Integer.parseInt(gameProps.getProperty("gameObjects.coin.maxFrames"));
        MAX_INVINCIBLE_TIME = Integer.parseInt(gameProps.getProperty("gameObjects.invinciblePower.maxFrames"));
        PLAYER_NAME = playerName;

        WEATHER_CONDITION = IOUtils.readCommaSeparatedFile(gameProps.getProperty("gamePlay.weatherFile"));

        inTrip = false;
        lastTripCompleted = true;
        gameCompleted= false;

        waitingPassenger = null;
        inTripPassenger = null;
        lastTripPassenger = null;

        currentScore = 0;
        remainingFrames = MAX_FRAMES;
        remainingLostFrames = Integer.parseInt(gameProps.getProperty("gameObjects.fire.ttl"));
        coinTimer = 0;
        invincibleTimer = 0;
        penalty = 0;

        // Initialise game entities and tokens
        taxi = new Taxi(gameProps, gameObjects);
        driver = new Driver(gameProps, taxi.getLocation().getX(), taxi.getLocation().getY());
        passengers = Passenger.readPassengers(gameProps, gameObjects);
        tokenList = Token.readToken(gameProps, gameObjects);
        font = null;

        // Refresh game entities list for the new game
        Taxi.makeNewDamagedTaxiList();
        Car.makeNewCarList();
        Fireball.makeNewFireballList();
        AnimationItem.makeNewAnimationList();
    }

    /**
     * Gets whether the game has completed.
     *
     * @return true if game completed, false otherwise.
     */
    public boolean getGameCompleted(){
        return gameCompleted;
    }

    /**
     * Gets whether the game is won or lost
     *
     * @return true if game is won, false otherwise.
     */
    public boolean getIsWon(){
        return isWon;
    }

    /**
     * Render the Game Play Screen
     *
     * @param input The current keyboard input.
     */
    public void showGamePlay(Input input) {
        showGameElements(input);
        handleAllGameLogic(input);
        if (!inTrip){
            findNewTrip(input);
        }
        else {
            commenceTrip(input);
        }
    }

    private void showGameElements(Input input){
        // Draw two connected backgrounds
        showGamePlayBackgrounds(input);

        // Show game entities and tokens
        taxi.showTaxi(input, X_SPEED, Y_SPEED);
        Token.showTokens(input, tokenList, Y_SPEED);
        driver.showDriver(getGameProps(), input, taxi, inTripPassenger);
        Passenger.showSelectedPassengers(input, passengers, taxi, driver, isRaining, getGameProps(),
                inTripPassenger,lastTripPassenger, Y_SPEED);
        showCarsAndFireballs(input);
        AnimationItem.showAnimationItems(input, Y_SPEED);

        // Show game details
        showGameDetails();
    }

    // Handle all the logic in game
    private void handleAllGameLogic(Input input){
        handleNewTaxiCreation();
        handleTokenCollision();
        handleEntitiesCollision();
        handleGameCompletion();
        remainingFrames -= 1;
    }

    // Generate a new taxi when the current taxi is damaged
    private void handleNewTaxiCreation(){
        if (taxi.getHealth() <= 0){
            taxi = Taxi.makeNewTaxi(getGameProps(), taxi, driver, inTripPassenger);
        }
    }

    // Randomly create and render other cars, enemy cars and fireballs
    private void showCarsAndFireballs(Input input){
        Car.create(getGameProps());
        Car.showCars(input, getGameProps(), Y_SPEED);
        Fireball.showFireballs(input, getGameProps(), Y_SPEED);
    }

    // Handle the collision logic of coin and invincible power
    private void handleTokenCollision(){
        for (Token token : tokenList) {
            // Check if the taxi or driver has collided with token
            if ((taxi.getLocation().distance(token.getLocation()) <= (taxi.getRadius() + token.getRadius())
                    && taxi.getHasDriver()) ||
                    (driver.getLocation().distance(token.getLocation()) <= (driver.getRadius() + token.getRadius())
                            && !taxi.getHasDriver())){
                token.setIsUsed(true);
                if (token instanceof Coin){
                    coinTimer = MAX_COIN_TIME;
                }
                else {
                    invincibleTimer = MAX_INVINCIBLE_TIME;
                }
            }
        }

        if (coinTimer > 0) {
            if (inTripPassenger != null){
                // Apply coin effect to the passenger
                inTripPassenger.useCoin();
            }
            coinTimer -= 1;
        }

        if (invincibleTimer > 0){
            // Apply invincible effect to taxi and driver
            if (taxi.getHasDriver()){
                taxi.setIsInvincible(true);
            }
            driver.setIsInvincible(true);
            invincibleTimer -= 1;
        }
        else {
            taxi.setIsInvincible(false);
            driver.setIsInvincible(false);
        }
    }

    // Handle the collision logic between game entities
    private void handleEntitiesCollision(){
        driver.handleCharacterCollision(getGameProps(), !taxi.getHasDriver());
        if (inTripPassenger != null){
            inTripPassenger.handleCharacterCollision(getGameProps(), !taxi.getHasDriver());
        }
        if (lastTripPassenger != null){
            lastTripPassenger.handleCharacterCollision(getGameProps(), !taxi.getHasDriver());
        }
        taxi.handleTaxiCollision(getGameProps());
        Car.handleCarsCollision(getGameProps());
    }

    // Check various condition if the game has completed
    private boolean isGameCompleted(){
        // If driver or passenger has no health, set the timer for remaining frame until lost
        if (driver.getHealth() <= 0 || Passenger.getMinHealth(passengers) <= 0){
            remainingLostFrames -= 1;
        }
        return currentScore >= TARGET || remainingFrames == 0 ||
                taxi.getLocation().getY() >= Double.parseDouble(getGameProps().getProperty("window.height"))||
                taxi.getLocation().getY() <= 0 || remainingLostFrames == 0;
    }

    private void handleGameCompletion(){
        if (isGameCompleted()) {
            // Only wining condition, all other condition lead too game lost
            isWon = currentScore >= TARGET;
            IOUtils.writeScoreToFile(getGameProps().getProperty("gameEnd.scoresFile"),
                    PLAYER_NAME +"," + String.format("%.2f", currentScore));
            gameCompleted = true;
        }
    }

    // Set the game state to sun or rain based on current frame
    private void checkRaining(){
        for (String[] weather : WEATHER_CONDITION){
            if (MAX_FRAMES - remainingFrames >= Integer.parseInt(weather[1])
            && MAX_FRAMES - remainingFrames < Integer.parseInt(weather[2])){
                isRaining = weather[0].equals("RAINING");
                break;
            }
        }
    }

    // Render the scrolling backgrounds of the game
    private void showGamePlayBackgrounds(Input input){
        checkRaining();
        Background.drawGamePlayBackgrounds(input, isRaining, gamePlayImages, Y_SPEED);
    }

    // Handle situation where there is no passenger currently in a trip.
    private void findNewTrip(Input input) {
        handleLastTripCompletion();
        if (taxi.getHasDriver()) {
            if ((waitingPassenger =
                    taxi.findNearPassenger(input, getGameProps(), passengers, lastTripPassenger)) != null) {
                // Found a near passenger waiting to be picked up
                if (waitingPassenger.moveTowardLocation(taxi.getLocation())) {
                    // Passenger has moved to the taxi, trip commences
                    inTripPassenger = waitingPassenger;
                    inTrip = true;
                }
            }
        }
    }

    // Handle situations where a passenger is currently in a trip.
    private void commenceTrip (Input input) {
        handleLastTripCompletion();
        // Update the in-trip passenger location and show their end flag
        if (taxi.getHasDriver()) {
            inTripPassenger.setLocationInTaxi(input, X_SPEED);
        }
        inTripPassenger.getEndFlag().showEndFlag();
        // Check if the trip has completed
        handleInTripCompletion(input);
    }

    // Handle the completion of a trip
    private void handleInTripCompletion (Input input) {
        if (taxi.getHasDriver() && input.isUp(Keys.UP) && input.isUp(Keys.RIGHT) && input.isUp(Keys.LEFT)){
            // The taxi has fully stopped
            if (inTripPassenger.getLocation().distance(inTripPassenger.getEndFlag().getLocation()) <=
                    inTripPassenger.getEndFlag().getRadius() ||
                    inTripPassenger.getLocation().getY() <= inTripPassenger.getEndFlag().getLocation().getY()){
                // The locations are satisfied
                penalty = inTripPassenger.calculatePenalty(getGameProps()); // Calculate penalty
                currentScore += Double.max(0, inTripPassenger.getEarnings() - penalty); // Update total score
                lastTripPassenger = inTripPassenger;
                inTripPassenger = null;

                inTrip = false; // Start looking for new trip
                lastTripCompleted = false; // To handle the process of passenger walking to their end flag
            }
        }
    }

    // Handle the process where the passenger leaving taxi and walk to their end flag
    private void handleLastTripCompletion(){
        if (!lastTripCompleted) {
            // The passenger from last trip has not moved to the end flag
            lastTripPassenger.getEndFlag().showEndFlag();
            if (lastTripPassenger.moveTowardLocation(lastTripPassenger.getEndFlag().getLocation())){
                // The passenger has moved to the end flag
                lastTripCompleted = true;
            }
        }
    }

    // Render the trip details on the screen based on the current game state.
    private void showGameDetails(){
        // Set up font and print game details
        font = new Font(getGameProps().getProperty("font"),
                Integer.parseInt(getGameProps().getProperty("gamePlay.info.fontSize")));

        // Show the current score, target and frame remaining
        font.drawString(getMessageProps().getProperty("gamePlay.earnings") + String.format("%.2f", currentScore),
                Double.parseDouble(getGameProps().getProperty("gamePlay.earnings.x")),
                Double.parseDouble(getGameProps().getProperty("gamePlay.earnings.y")));

        font.drawString(getMessageProps().getProperty("gamePlay.target") + String.format("%.2f", TARGET),
                Double.parseDouble(getGameProps().getProperty("gamePlay.target.x")),
                Double.parseDouble(getGameProps().getProperty("gamePlay.target.y")));

        font.drawString(getMessageProps().getProperty("gamePlay.remFrames")  + remainingFrames,
                Double.parseDouble(getGameProps().getProperty("gamePlay.maxFrames.x")),
                Double.parseDouble(getGameProps().getProperty("gamePlay.maxFrames.y")));

        if (coinTimer > 0) {
            // Set up font and print the coin timer
            font = new Font(getGameProps().getProperty("font"),
                    Integer.parseInt(getGameProps().getProperty("gamePlay.info.fontSize")));
            font.drawString(String.valueOf(MAX_COIN_TIME - coinTimer),
                    Double.parseDouble(getGameProps().getProperty("gameplay.coin.x")),
                    Double.parseDouble(getGameProps().getProperty("gameplay.coin.y")));
        }

        // Print the taxi, driver and passenger health
        font.drawString(getMessageProps().getProperty("gamePlay.taxiHealth") +
                        String.format("%.2f", taxi.getHealth()),
                Double.parseDouble(getGameProps().getProperty("gamePlay.taxiHealth.x")),
                Double.parseDouble(getGameProps().getProperty("gamePlay.taxiHealth.y")));

        font.drawString(getMessageProps().getProperty("gamePlay.driverHealth") +
                        String.format("%.2f", driver.getHealth()),
                Double.parseDouble(getGameProps().getProperty("gamePlay.driverHealth.x")),
                Double.parseDouble(getGameProps().getProperty("gamePlay.driverHealth.y")));
        if (inTripPassenger != null){
            font.drawString(getMessageProps().getProperty("gamePlay.passengerHealth") +
                            String.format("%.1f", inTripPassenger.getHealth()),
                    Double.parseDouble(getGameProps().getProperty("gamePlay.passengerHealth.x")),
                    Double.parseDouble(getGameProps().getProperty("gamePlay.passengerHealth.y")));
        }
        else {
            font.drawString(getMessageProps().getProperty("gamePlay.passengerHealth") +
                            String.format("%.1f", Passenger.getMinHealth(passengers)),
                    Double.parseDouble(getGameProps().getProperty("gamePlay.passengerHealth.x")),
                    Double.parseDouble(getGameProps().getProperty("gamePlay.passengerHealth.y")));
        }

        // No current trip or last trip
        if (!inTrip && lastTripPassenger == null) return;

        font = new Font(getGameProps().getProperty("font"),
                Integer.parseInt(getGameProps().getProperty("gamePlay.info.fontSize")));

        if (inTrip) {
            // Currently in-trip, show title, expected earnings, priority
            font.drawString(getMessageProps().getProperty("gamePlay.onGoingTrip.title"),
                    Double.parseDouble(getGameProps().getProperty("gamePlay.tripInfo.x")),
                    Double.parseDouble(getGameProps().getProperty("gamePlay.tripInfo.y")));

            font.drawString(getMessageProps().getProperty("gamePlay.trip.expectedEarning") +
                            String.format("%.1f", inTripPassenger.getEarnings()),
                    Double.parseDouble(getGameProps().getProperty("gamePlay.tripInfo.x")),
                    Double.parseDouble(getGameProps().getProperty("gamePlay.tripInfo.y")) +
                            EARNING_DISTANCE_TO_TRIP_INFO);

            font.drawString(getMessageProps().getProperty("gamePlay.trip.priority") + inTripPassenger.getPriority(),
                    Double.parseDouble(getGameProps().getProperty("gamePlay.tripInfo.x")),
                    Double.parseDouble(getGameProps().getProperty("gamePlay.tripInfo.y")) +
                            PRIORITY_DISTANCE_TO_TRIP_INFO);
        }

        else {
            // Currently not in-trip, show title, last earnings, priority and penalty
            font.drawString(getMessageProps().getProperty("gamePlay.completedTrip.title"),
                    Double.parseDouble(getGameProps().getProperty("gamePlay.tripInfo.x")),
                    Double.parseDouble(getGameProps().getProperty("gamePlay.tripInfo.y")));

            font.drawString(getMessageProps().getProperty("gamePlay.trip.expectedEarning") +
                            String.format("%.1f", lastTripPassenger.getEarnings()),
                    Double.parseDouble(getGameProps().getProperty("gamePlay.tripInfo.x")),
                    Double.parseDouble(getGameProps().getProperty("gamePlay.tripInfo.y")) +
                            EARNING_DISTANCE_TO_TRIP_INFO);

            font.drawString(getMessageProps().getProperty("gamePlay.trip.priority") + lastTripPassenger.getPriority(),
                    Double.parseDouble(getGameProps().getProperty("gamePlay.tripInfo.x")),
                    Double.parseDouble(getGameProps().getProperty("gamePlay.tripInfo.y")) +
                            PRIORITY_DISTANCE_TO_TRIP_INFO);

            font.drawString(getMessageProps().getProperty("gamePlay.trip.penalty") + String.format("%.2f", penalty),
                    Double.parseDouble(getGameProps().getProperty("gamePlay.tripInfo.x")),
                    Double.parseDouble(getGameProps().getProperty("gamePlay.tripInfo.y")) +
                            PENALTY_DISTANCE_TO_TRIP_INFO);
        }
    }
}

