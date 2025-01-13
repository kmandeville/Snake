package me.kevinmandeville;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author kmandeville
 * @since NEXT_RELEASE_VERSION
 */
public class ScorePanel extends JPanel {

    private final JLabel scoreLabel;
    private int score = 1;

    /**
     * The ScorePanel constructor initializes the score panel with a label.
     */
    public ScorePanel() {
        setBackground(Color.LIGHT_GRAY); // Optional: Set a background color
        scoreLabel = new JLabel("Score: " + score); // Initialize the label
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Optional: Customize font
        add(scoreLabel); // Add the label to this panel
    }

    /**
     * Increments the current score by 1 and updates the associated score label with the new value. The updated score is
     * displayed in the format "Score: [updatedScore]".
     */
    public void incrementScore() {
        scoreLabel.setText("Score: " + ++score); // Update the label text
    }
}
