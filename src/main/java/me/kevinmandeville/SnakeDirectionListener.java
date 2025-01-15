package me.kevinmandeville;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * SnakeDirectionListener listens for key events and updates the direction of the Snake object accordingly. It extends
 * KeyAdapter, allowing simplified handling of keyboard input.
 * <p>
 * This class facilitates the movement controls of a snake in a grid-based game. Depending on the arrow key pressed, it
 * changes the snake's direction by invoking the setDirection method on the Snake object. The allowed directions include
 * UP, DOWN, LEFT, and RIGHT as defined in the Direction enum.
 * <p>
 * It ensures that the snake's direction cannot be reversed directly by preventing opposite directional inputs (e.g., UP
 * to DOWN or LEFT to RIGHT) based on the logic implemented in the Snake class.
 * <p>
 * KeyEvent Mappings:
 * - VK_UP: Sets the snake's direction to UP.
 * - VK_DOWN: Sets the snake's direction to DOWN.
 * - VK_LEFT: Sets the snake's direction to LEFT.
 * - VK_RIGHT: Sets the snake's direction to RIGHT.
 */
public class SnakeDirectionListener extends KeyAdapter {

    private final Snake snake;

    public SnakeDirectionListener(Snake snake) {
        this.snake = snake;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        Direction direction = mapKeyCodeToDirection(e.getKeyCode());
        if (direction != null) {
            snake.setDirection(direction);
        }
    }

    private Direction mapKeyCodeToDirection(int keyCode) {
        return switch (keyCode) {
            case KeyEvent.VK_UP -> Direction.UP;
            case KeyEvent.VK_DOWN -> Direction.DOWN;
            case KeyEvent.VK_LEFT -> Direction.LEFT;
            case KeyEvent.VK_RIGHT -> Direction.RIGHT;
            default -> null;
        };
    }
}
