import javafx.geometry.VPos;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class TextManager {
    private Text scoreText;
    private Text launchHint;
    private Text gameOverText;
    private Font customFont;
    private Pane root;

    public TextManager(Pane root) {
        this.root = root;
        loadFont();

        // --- Score ---
        scoreText = new Text("Score: 0");
        scoreText.setFill(Color.BLACK);
        scoreText.setFont(customFont);
        scoreText.setTextOrigin(VPos.TOP);
        scoreText.setTextAlignment(TextAlignment.LEFT);

        // --- Hướng dẫn ---
        launchHint = new Text("Press 'SPACE' to launch the ball!");
        launchHint.setFill(Color.BLACK);
        launchHint.setFont(customFont);
        launchHint.setTextAlignment(TextAlignment.CENTER);

        // --- Game Over ---
        gameOverText = new Text("GAME OVER - Press R to Restart");
        gameOverText.setFill(Color.RED);
        gameOverText.setFont(customFont);
        gameOverText.setTextAlignment(TextAlignment.CENTER);
        gameOverText.setVisible(false);

        // Thêm vào root
        root.getChildren().addAll(scoreText, launchHint, gameOverText);

        // Cập nhật vị trí ban đầu
        alignTexts();
    }

    //
    private void loadFont() {
        try {
            File fontFile = new File("assets/fonts/game.ttf");
            FileInputStream fontIS = new FileInputStream(fontFile);
            customFont = Font.loadFont(fontIS, 20);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    private void alignTexts() {
        double paneWidth = root.getWidth();
        double paneHeight = root.getHeight();

        // Score: canh trên cùng bên trái
        scoreText.setX(10);
        scoreText.setY(10);

        // Hint: căn giữa màn hình
        launchHint.setX((paneWidth - launchHint.getLayoutBounds().getWidth()) / 2);
        launchHint.setY(paneHeight / 2 - 40);

        // Game Over: căn giữa
        gameOverText.setX((paneWidth - gameOverText.getLayoutBounds().getWidth()) / 2);
        gameOverText.setY(paneHeight / 2 + 20);
    }

    public void updateScore(int score) {
        scoreText.setText("Score: " + score);
        alignTexts();
    }

    public void showLaunchHint(boolean show) {
        launchHint.setVisible(show);
    }

    public void showGameOver(boolean show) {
        gameOverText.setVisible(show);
    }

    public void removeText(Pane root) {
        root.getChildren().removeAll(scoreText, launchHint, gameOverText);
    }
}