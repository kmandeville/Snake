package me.kevinmandeville;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * Represents a game board for a grid-based Snake game. This class is responsible for managing the visual and logical
 * representation of the game, including the snake's movements, fruit placement, and board updates. It extends JPanel
 * and uses a grid layout to represent the game's cells.
 * <p>
 * The GameBoard initializes the game environment by creating the grid, placing the snake at the center of the board,
 * and randomly placing the fruit. It also sets up a key listener for controlling the snake's direction and starts the
 * game engine for the game loop.
 */
public class GameBoard extends JPanel {

    private final int rows;
    private final int cols;
    private final JPanel[][] cells;
    private final Snake snake;
    private final Fruit fruit;

    public GameBoard(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.cells = new JPanel[rows][cols];
        initializeBoard();

        this.snake = new Snake(this, rows / 2, cols / 2); // Place snake at center
        this.fruit = new Fruit(this);
        this.fruit.place();

        setLayout(new GridLayout(rows, cols));
        setFocusable(true);
        addKeyListener(new SnakeDirectionListener(snake));
        GameEngine.getInstance(snake, fruit).start();
    }

    private void initializeBoard() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                JPanel cell = new JPanel();
                cell.setBackground(Color.BLACK);
                cell.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
                cells[row][col] = cell;
                add(cell);
            }
        }
    }

    public void updateCell(Point point, Color color) {
        cells[point.y][point.x].setBackground(color);
    }

    public Snake getSnake() {
        return snake;
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }
}
