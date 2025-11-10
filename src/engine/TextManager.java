package engine;

import javafx.geometry.VPos;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import object.Paddle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static utils.Constants.*;

public class TextManager {
    private Text scoreText;
    private Text livesText;
    private Text launchHint;
    private Text gameOverText;
    private Font customFont;
    private Pane root;

    public TextManager(Pane root) {
        this.root = root;
        this.customFont = loadFont("assets/fonts/font.ttf");

//        // Lives
//        livesText = new Text("Lives: 3");
//        livesText.setX(LIVES_POS_X);
//        livesText.setY(LIVES_POS_Y);
//        livesText.setFill(Color.BLACK);
//        livesText.setFont(customFont);
//
//        // Score
//        scoreText = new Text("Score: 0");
//        scoreText.setFill(Color.BLACK);
//        scoreText.setFont(customFont);
//        scoreText.setTextOrigin(VPos.TOP);
//        scoreText.setTextAlignment(TextAlignment.LEFT);

        // Hint
        launchHint = new Text("PRESS 'SPACE' TO LAUNCH THE BALL!");
        launchHint.setFill(Color.BLACK);
        launchHint.setFont(customFont);
        launchHint.setX(LAUNCH_TEXT_POS_X);
        launchHint.setY(LAUNCH_TEXT_POS_Y);

        // Game Over
        gameOverText = new Text("GAME OVER - PRESS 'R' TO RESTART!");
        gameOverText.setFill(Color.RED);
        gameOverText.setFont(customFont);
        gameOverText.setX(GAME_OVER_POS_X);
        gameOverText.setY(GAME_OVER_POS_Y);
        gameOverText.setVisible(false);

        root.getChildren().addAll(launchHint, gameOverText);

//        // Align to the right pos
//        alignTexts();
    }

    private Font loadFont(String filePath) {
        Font font = null;
        try {
            File fontFile = new File(filePath);
            FileInputStream fontIS = new FileInputStream(fontFile);
            font = Font.loadFont(fontIS, TEXT_SIZE);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }

        return font;
    }

    private void alignTexts() {
//        livesText.setX(LIVES_POS_X);
//        livesText.setY(LIVES_POS_Y);
//
//        scoreText.setX(SCORE_POS_X);
//        scoreText.setX(SCORE_POS_Y);

        launchHint.setX((SCREEN_WIDTH - launchHint.getLayoutBounds().getWidth()) / 2);
        launchHint.setY(SCREEN_HEIGHT / 2 - 40);

        gameOverText.setX((SCREEN_WIDTH - gameOverText.getLayoutBounds().getWidth()) / 2);
        gameOverText.setY(SCREEN_HEIGHT / 2 + 20);
    }

//    public void updateScoreAndLives(int score, Paddle paddle) {
//        livesText.setText("Lives: " + paddle.getLives());
//        scoreText.setText("Score: " + score);
//        alignTexts();
//    }

    public void showLaunchHint(boolean show) {
        launchHint.setVisible(show);
    }

    public void showGameOver(boolean show) {
        gameOverText.setVisible(show);
    }

    public void removeText(Pane root) {
        root.getChildren().removeAll(launchHint, gameOverText);
    }
}