package me.kevinmandeville;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * Represents a game board consisting of a grid of {@link JPanel} cells, allowing for dynamic customization of rows and
 * columns. The board includes keyboard event handling for basic arrow key presses.
 * <p>
 * This class extends {@link JPanel} and displays the game board in a grid layout, where each cell is represented as a
 * JPanel with a default black background and dark gray borders. It is intended for visualization of grid-based games or
 * other grid-like structures.
 * <p>
 * Key Features:
 * - Configurable number of rows and columns for the grid.
 * - Visual representation of cells with default styling.
 * - Automatic handling of arrow key press events.
 */
public class GameBoard extends JPanel {

    private static final double TARGET_FPS = 12;
    private static final double OPTIMAL_TIME = 1000000000 / TARGET_FPS;
    private final transient ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final int rows;
    private final int cols;
    private final JPanel[][] cells;
    private final Random rnd = new Random();
    private boolean running = true;
    private Deque<Point> snake = new LinkedList<>();
    private Point fruitPosition;
    private Direction currentDirection = Direction.LEFT; // Initial Snake Direction

    /**
     * Constructs a new GameBoard with the specified number of rows and columns. The game board is initialized with a
     * grid of JPanel cells arranged in a GridLayout. Key event handling is enabled to allow user interaction, and the
     * game loop is set up to manage the game's progression.
     *
     * @param rows the number of rows in the game board grid
     * @param cols the number of columns in the game board grid
     */
    public GameBoard(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.cells = new JPanel[rows][cols];
        setLayout(new GridLayout(rows, cols));
        initializeBoard();
        setFocusable(true); // Enable key listening
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });

        setupGameLoop();
    }

    /**
     * Initializes the game board by populating it with a grid of JPanel cells. Each cell is configured with a default
     * black background color and a thin dark gray border to enhance grid visibility. The initialized cells are added to
     * the board layout and stored for future reference.
     * <p>
     * This method assumes that the dimensions of the board (rows and columns) and the cells array have already been
     * defined. It iterates over each cell position, creates a new JPanel, applies the default visual styling, stores
     * the panel in the cells array, and adds it to the grid layout.
     */
    private void initializeBoard() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                JPanel cell = new JPanel();
                cell.setBackground(Color.BLACK); // Default background color
                cell.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY)); // Thin border for grid visibility
                cells[row][col] = cell;
                add(cell);
            }
        }

        placeFruit();
        initializeSnake();
    }

    /**
     * Initializes the snake at the start of the game by positioning its initial segment at the center of the game board
     * grid. The initial position is determined by calculating the midpoint of the rows and columns of the board. The
     * segment is then added to the snake's body and visually updated on the game board with the appropriate color.
     * <p>
     * This method assumes that the grid dimensions (rows and columns) are already defined and that the board is
     * initialized. The method sets the initial state for the snake and is typically called during the game setup phase
     * before the game loop starts.
     */
    private void initializeSnake() {
        Point start = new Point(rows / 2, cols / 2); // Start at the center
        snake.add(start);
        updateCell(start, Color.GREEN);
    }

    /**
     * Places a fruit object at a random position on the game board. This method generates random x and y coordinates
     * within the bounds of the board dimensions (`rows` and `cols`) and updates the cell at the specified coordinates
     * with a red color to visually represent the fruit.
     * <p>
     * The random position of the fruit is represented as a {@code Point} object, which is stored in the
     * {@code fruitPosition} field. The cell corresponding to the fruit's position is updated using the
     * {@code updateCell} method.
     * <p>
     * This method is typically called during game initialization or when a new fruit needs to be placed after the
     * previous one is consumed by the snake.
     */
    private void placeFruit() {
        // pick 2 random values between 1-board size
        do {
            int x = rnd.nextInt(rows);
            int y = rnd.nextInt(cols);
            fruitPosition = new Point(x, y);
        } while (snake.contains(fruitPosition));

        updateCell(fruitPosition, Color.RED);
    }

    /**
     * Updates the background color of a specific cell in the game board grid.
     *
     * @param start the {@code Point} object representing the coordinates of the cell to be updated, where
     *              {@code start.x} is the column index and {@code start.y} is the row index.
     * @param clr   the {@code Color} object to set as the new background color for the specified cell.
     */
    private void updateCell(Point start, Color clr) {
        cells[start.y][start.x].setBackground(clr);
    }

    /**
     * Sets up and manages the game loop for the Snake game. This method operates in a continuous loop, executing the
     * core game logic until the game state indicates that the game should stop. The loop ensures tasks such as handling
     * player inputs, updating the snake's position, checking for collisions, and redrawing the game board are executed
     * in each iteration.
     * <p>
     * The loop maintains a consistent frame rate by calculating the elapsed time between iterations and adjusting the
     * sleep time of the thread accordingly. The aim is to achieve a smooth and predictable gameplay experience based on
     * the target frames per second (FPS).
     * <p>
     * Key operations performed during each iteration include:
     * 1. Handling user input to update the snake's direction.
     * 2. Updating the snake's position based on its current direction.
     * 3. Checking for collisions with walls or the snake itself to determine game over conditions.
     * 4. Redrawing the game board to reflect the updated state.
     * <p>
     * If the thread is interrupted during its sleep period, the method throws a runtime exception to handle the error.
     */
    private void setupGameLoop() {
        executorService.execute(() -> {
            while (checkGameState()) {
                // move snake position
                moveSnake();

                long sleepTime = (long) OPTIMAL_TIME / 1000000;

                try {
                    Thread.sleep(sleepTime); // Minimum sleep to prevent CPU spinning
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            // check end game condition
        });
    }

    /**
     * Updates the position of the snake on the game board by creating a new head segment based on the current movement
     * direction and removing the tail segment. The new head position wraps around the board edges if it moves out of
     * bounds.
     * <p>
     * The method performs the following operations:
     * 1. Calculates the new position for the head of the snake based on the current direction.
     * 2. Adds the new head position to the front of the snake's body.
     * 3. Visually updates the new head on the game board with a green color.
     * 4. Visually removes the old tail from the game board by setting it to black.
     * 5. Removes the tail position from the snake's body.
     */
    private void moveSnake() {
        Point newHead = switch (currentDirection) {
            case UP -> new Point(snake.getFirst().x, calculateWrappedPosition(snake.getFirst().y, -1));
            case DOWN -> new Point(snake.getFirst().x, calculateWrappedPosition(snake.getFirst().y, 1));
            case LEFT -> new Point(calculateWrappedPosition(snake.getFirst().x, -1), snake.getFirst().y);
            case RIGHT -> new Point(calculateWrappedPosition(snake.getFirst().x, 1), snake.getFirst().y);
        };

        snake.addFirst(newHead);

        updateCell(newHead, Color.GREEN); // Set new head position
        updateCell(snake.getLast(), Color.BLACK); // Erase the old tail

        // if new head position is the same as the fruitPosition, then don't remove the tail of snake so it grows
        if (newHead.equals(fruitPosition)) {
            placeFruit();
        } else {
            snake.removeLast();
        }
    }

    /**
     * Computes a new position on the game board based on the current position and a specified offset. Handles boundary
     * conditions to ensure the position wraps around the board edges if necessary.
     *
     * @param current the current position on the game board
     * @param space   the offset to apply to the current position
     * @return the new position after applying the offset, adjusted for wrapping at the board edges
     */
    private int calculateWrappedPosition(int current, int space) {
        return (current + space + Main.BOARD_SIZE) % Main.BOARD_SIZE;
    }

    /**
     * Checks the current state of the game to determine if it is still running or has ended.
     *
     * @return {@code true} if the game is running, {@code false} if the game has ended.
     */
    private boolean checkGameState() {
        return running;
    }


    /**
     * Handles key press events and performs actions based on the direction arrow keys. This method responds to the up,
     * down, left, and right arrow keys by executing specific logic when the corresponding key is pressed.
     *
     * @param e the KeyEvent that triggers this method, containing details of the key pressed
     */
    private void handleKeyPress(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                updateDirection(Direction.UP);
                break;
            case KeyEvent.VK_DOWN:
                updateDirection(Direction.DOWN);
                break;
            case KeyEvent.VK_LEFT:
                updateDirection(Direction.LEFT);
                break;
            case KeyEvent.VK_RIGHT:
                updateDirection(Direction.RIGHT);
                break;
            default:
                break;
        }
    }

    /**
     * Updates the current movement direction of the snake while ensuring that the snake does not reverse direction. For
     * example, if the snake is currently moving UP, it cannot be updated to move DOWN directly.
     *
     * @param newDirection the new direction to update the snake's movement to, specified as one of the values in the
     *                     {@code Direction} enum (UP, DOWN, LEFT, RIGHT).
     */
    private void updateDirection(Direction newDirection) {
        // Prevent the snake from reversing direction
        if ((currentDirection == Direction.UP && newDirection == Direction.DOWN) ||
            (currentDirection == Direction.DOWN && newDirection == Direction.UP) ||
            (currentDirection == Direction.LEFT && newDirection == Direction.RIGHT) ||
            (currentDirection == Direction.RIGHT && newDirection == Direction.LEFT)) {
            // Ignore the invalid direction change
            return;
        }
        // Update to the valid new direction
        currentDirection = newDirection;
    }

    public JPanel getCell(int row, int col) {
        if (row >= 0 && row < rows && col >= 0 && col < cols) {
            return cells[row][col];
        }
        throw new IllegalArgumentException("Cell out of bounds");
    }

    /**
     * Represents the possible movement directions in the Snake game. This enum defines four directional constants: UP,
     * DOWN, LEFT, and RIGHT. These directions are used to control the movement of the snake on the game board.
     * <p>
     * Each direction corresponds to one of the arrow keys, and it determines how the snake's position is updated during
     * the game loop. The direction is set based on user input from key press events.
     */
    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
}

