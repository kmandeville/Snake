package me.kevinmandeville;

import java.awt.Color;
import java.awt.Point;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Represents the Snake in a grid-based Snake game. A Snake object manages its position, movement, and interaction with
 * the game's GameBoard.
 * <p>
 * The Snake's body is represented as a deque of points, where the first element (head) is the current position of the
 * snake's head, and the rest represent the snake's tail.
 * <p>
 * The snake can move in one of four directions (UP, DOWN, LEFT, RIGHT) as defined by the Direction enum. It handles
 * collisions with fruits, updating both its own body and the GameBoard visually and logically.
 */
public class Snake {

    private static final boolean SHOULD_WRAP = false;
    private final GameBoard gameBoard;
    private final Deque<Point> body;
    private final HashSet<Point> bodyHashSet = new HashSet<>();
    private boolean started = false;
    private Direction currentDirection;

    public Snake(GameBoard gameBoard, int startX, int startY) {
        this.gameBoard = gameBoard;
        this.body = new LinkedList<>();
        this.currentDirection = Direction.LEFT;
        initializeSnake(startX, startY);
    }

    /**
     * Initializes the snake by setting its starting position on the game board. The snake's initial segment is added at
     * the specified coordinates, and the corresponding cell on the game board is updated to reflect the snake's
     * presence.
     *
     * @param x the initial x-coordinate of the snake's starting position
     * @param y the initial y-coordinate of the snake's starting position
     */
    private void initializeSnake(int x, int y) {
        Point start = new Point(x, y);
        add(start);
        gameBoard.updateCell(start, Color.GREEN);
    }

    /**
     * Updates the position of the snake on the game board based on its current direction and detects interactions with
     * the fruit. If the snake's head reaches the fruit's position, it grows in size by keeping its tail untrimmed in
     * that move and returns true. Otherwise, the snake moves forward, and its tail segment is removed, returning
     * false.
     *
     * @param fruitPosition the current position of the fruit on the game board
     * @return true if the snake's head reaches the fruit's position, indicating the snake has grown; false otherwise
     */
    public boolean move(Point fruitPosition) {
        if (!started) {
            return false; // Do not move if the game hasn't started
        }
        Point head = body.getFirst();
        Point newHead = switch (currentDirection) {
            case UP -> new Point(head.x, wrapCoordinate(head.y - 1, gameBoard.getRows()));
            case DOWN -> new Point(head.x, wrapCoordinate(head.y + 1, gameBoard.getRows()));
            case LEFT -> new Point(wrapCoordinate(head.x - 1, gameBoard.getCols()), head.y);
            case RIGHT -> new Point(wrapCoordinate(head.x + 1, gameBoard.getCols()), head.y);
        };

        if (newHead.equals(fruitPosition)) {
            add(newHead);
            gameBoard.updateCell(newHead, Color.GREEN);
            return true;
        } else if (isCollision(newHead)) {
            GameEngine.getInstance().quit();
            return false;
        } else {
            add(newHead);
            Point tail = remove();
            gameBoard.updateCell(newHead, Color.GREEN);
            gameBoard.updateCell(tail, Color.BLACK);
            return false;
        }
    }

    /**
     * Adds a new point to the snake's body and updates the internal hash set representing the snake's segments.
     *
     * @param point the point to be added to the snake's body, representing a new segment of the snake
     */
    private void add(Point point) {
        body.addFirst(point);
        bodyHashSet.add(point);
    }

    /**
     * Removes the specified point from the snake's body segments and updates the corresponding internal structures to
     * reflect the removal.
     */
    private Point remove() {
        Point tail = body.removeLast();
        bodyHashSet.remove(tail);
        return tail;
    }

    /**
     * Determines whether the specified position results in a collision. A collision occurs if the position is outside
     * the boundaries of the game board or overlaps with the snake's body.
     *
     * @param position the position to check for a collision, represented as a Point object
     * @return true if the position results in a collision, false otherwise
     */
    private boolean isCollision(Point position) {
        return position.y < 0 || position.y >= gameBoard.getRows() ||
            position.x < 0 || position.x >= gameBoard.getCols() ||
            this.body.contains(position);
    }

    /**
     * Updates the direction of the snake, provided the new direction is not opposite to the current direction.
     *
     * @param newDirection the desired new direction for the snake's movement
     */
    public void setDirection(Direction newDirection) {
        if (!isOppositeDirection(newDirection)) {
            this.currentDirection = newDirection;
            if (!started) {
                start(); // Start the snake movement on the first direction press
            }
        }
    }

    /**
     * Determines whether the specified direction is opposite to the current direction.
     *
     * @param newDirection the direction to compare against the current direction
     * @return true if the specified direction is directly opposite to the current direction; false otherwise
     */
    private boolean isOppositeDirection(Direction newDirection) {
        return (currentDirection == Direction.UP && newDirection == Direction.DOWN) ||
            (currentDirection == Direction.DOWN && newDirection == Direction.UP) ||
            (currentDirection == Direction.LEFT && newDirection == Direction.RIGHT) ||
            (currentDirection == Direction.RIGHT && newDirection == Direction.LEFT);
    }

    private int wrapCoordinate(int coord, int max) {
        if (SHOULD_WRAP) {
            return (coord + max) % max;
        } else {
            return coord;
        }
    }

    public boolean contains(Point p) {
        return bodyHashSet.contains(p);
    }

    public void start() {
        this.started = true;
    }
}
