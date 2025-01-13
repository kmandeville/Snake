package me.kevinmandeville;

import java.awt.Color;
import java.awt.Point;
import java.util.Random;

/**
 * @author kmandeville
 * @since NEXT_RELEASE_VERSION
 */
public class Fruit {

    private final GameBoard gameBoard;
    private final Random random;
    private Point position;

    public Fruit(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
        this.random = new Random();
    }

    public void place() {
        do {
            int x = random.nextInt(gameBoard.getCols());
            int y = random.nextInt(gameBoard.getRows());
            position = new Point(x, y);
        } while (gameBoard.getSnake().contains(position));
        gameBoard.updateCell(position, Color.RED);
    }

    public Point getPosition() {
        return position;
    }
}
