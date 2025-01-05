package me.kevinmandeville;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * The Main class is the entry point for the application that initializes and displays the game board within a GUI
 * window. The main method uses SwingUtilities to ensure the GUI creation and updates are performed on the Event
 * Dispatch Thread (EDT). It sets up a JFrame that hosts an instance of the GameBoard class and allows for user
 * interaction.
 * <p>
 * Responsibilities:
 * - Creates the application window with a specified title.
 * - Configures the window default close operation.
 * - Instantiates the GameBoard with a customizable number of rows and columns.
 * - Adds the GameBoard to the JFrame and adjusts its size dynamically based on the grid dimensions.
 * - Makes the window visible and ready for user interaction.
 */
public class Main {

    public static final int BOARD_SIZE = 40;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Game Board");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            int rows = BOARD_SIZE; // Customize the grid size
            int cols = BOARD_SIZE;
            GameBoard gameBoard = new GameBoard(rows, cols);
            frame.add(gameBoard);
            frame.setSize(cols * 20, rows * 20); // Adjust window size based on the grid size
            frame.setVisible(true);
        });

    }
}