package me.kevinmandeville;

import java.awt.Point;

/**
 * Represents the core game engine responsible for managing the main game loop. The GameEngine class handles updating
 * the game's state (such as Snake movement and interaction with Fruit) and maintains a fixed frame rate to ensure
 * smooth gameplay.
 * <p>
 * The game engine operates on a separate thread, repeatedly executing the game logic (via the update() method) and
 * controlling the frame rate with the sleep method.
 */
public class GameEngine {

    private static final double TARGET_FPS = 12;
    private final Snake snake;
    private final Fruit fruit;
    private final ScorePanel scorePanel; // Reference to the ScorePanel for score updates
    private boolean running;

    private static GameEngine instance;

    /**
     * Constructs a GameEngine with the specified Snake and Fruit objects.
     *
     * @param snake      the Snake object representing the snake in the game
     * @param fruit      the Fruit object representing the fruit in the game
     * @param scorePanel Reference to scorePanle for updating
     */
    private GameEngine(Snake snake, Fruit fruit, ScorePanel scorePanel) {
        this.snake = snake;
        this.fruit = fruit;
        this.scorePanel = scorePanel;
        this.running = true;
    }

    /**
     * Retrieves or creates the singleton instance of the GameEngine. If the instance does not already exist, it is
     * initialized with the provided Snake and Fruit objects.
     *
     * @param snake      the Snake object representing the game's snake
     * @param fruit      the Fruit object representing the game's fruit
     * @param scorePanel Reference to scorePanle for updating
     * @return the singleton instance of the GameEngine
     */
    public static GameEngine getInstance(Snake snake, Fruit fruit, ScorePanel scorePanel) {
        if (instance == null) {
            initialize(snake, fruit, scorePanel);
        }
        return instance;
    }

    /**
     * Retrieves the singleton instance of the GameEngine. This method ensures that a GameEngine instance has already
     * been initialized using the overloaded getInstance(Snake, Fruit) method before invoking this version.
     *
     * @return the singleton instance of the GameEngine
     * @throws IllegalStateException if the GameEngine has not been initialized
     */
    public static GameEngine getInstance() {
        if (instance == null) {
            throw new IllegalStateException(
                "GameEngine has not been initialized. Call getInstance(Snake, Fruit) first.");
        }
        return instance;
    }

    /**
     * Initializes the GameEngine instance with the provided Snake and Fruit objects. This method creates a new
     * GameEngine object if one does not currently exist.
     *
     * @param snake      the Snake object representing the game's snake
     * @param fruit      the Fruit object representing the game's fruit
     * @param scorePanel Reference to scorePanle for updating
     */
    private static void initialize(Snake snake, Fruit fruit, ScorePanel scorePanel) {
        instance = new GameEngine(snake, fruit, scorePanel);
    }

    /**
     * Starts the game loop on a separate thread. The loop continuously updates the game's state and maintains a fixed
     * frame rate as long as the game is running.
     * <p>
     * The thread executes the following:
     * - Repeatedly calls the `update()` method to update the game state, managing interactions between the Snake and
     * the Fruit.
     * - Invokes the `sleep()` method to regulate the frame rate.
     * <p>
     * This method ensures the game loop operates independently of the main thread, allowing the game logic to run
     * smoothly in the background without blocking the UI or other processes.
     */
    public void start() {
        new Thread(() -> {
            while (running) {
                update();
                sleep();
            }
        }).start();
    }

    /**
     * Updates the state of the game by managing the interactions between the Snake and the Fruit.
     * <ul>
     * <li>Retrieves the current position of the fruit.</li>
     * <li>Moves the snake and checks if it has eaten the fruit.</li>
     * <li>If the fruit is eaten, places it at a new random position on the game board.</li>
     * </ul>
     * <p>
     * This method encapsulates the core logic of detecting collisions between the Snake and the Fruit
     * and ensures the game state is updated accordingly.
     */
    private void update() {
        Point fruitPosition = fruit.getPosition();
        boolean ateFruit = snake.move(fruitPosition);
        if (ateFruit) {
            fruit.place();
            scorePanel.incrementScore();
        }
    }

    /**
     * Regulates the frame rate of the game loop by pausing execution for a calculated duration.
     * <p>
     * The method calculates the sleep time based on the target frames per second (FPS) defined by the constant
     * TARGET_FPS. If the thread is interrupted during sleep, it restores the interrupted status.
     * <p>
     * Used within the game loop to ensure the game maintains a consistent frame rate for smooth gameplay.
     */
    private void sleep() {
        try {
            Thread.sleep((long) (1000 / TARGET_FPS));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void quit() {
        running = false;
    }
}
