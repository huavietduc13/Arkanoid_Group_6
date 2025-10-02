package wgame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

public class GameManager {
    private GraphicsContext gc;
    // TODO: Tạo file Ball, Paddle thêm feat.
    /*
    private Ball ball;
    private Paddle paddle;
    */

    private int score = 0;
    private boolean running = true;

    public GameManager(GraphicsContext gc) {
        this.gc = gc;
        // TODO:
        /*
        ball = new Ball(400, 300);
        paddle = new Paddle(350, 550);
        */
    }

    public void render() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, 800, 600);

        // TODO: Vẽ object.
        /*
        ball.render(gc);
        paddle.render(gc);
        */

        gc.setFill(Color.WHITE);
        gc.fillText("Score: " + score, 10, 20);

        if (!running) {
            gc.fillText("GAME OVER - Press R to Restart", 250, 300);
        }
    }

    public void update() {
        if (!running) return;

        // TODO:
        /*
        ball.update();
        paddle.update();
        */

        // TODO:
        /*
        if (ball.getY() > 600) {
            running = false;
            System.out.println("Game Over!");
        }
         */
    }

    public void keyPressed(KeyEvent e) {
        // TODO: paddle.keyPressed(e);
        if (!running && e.getCode().toString().equals("R")) {
            restart();
        }
    }

    public void keyReleased(KeyEvent e) {
        // TODO: paddle.keyReleased(e);
    }

    private void restart() {
        running = true;
        score = 0;
        /*
        ball.reset();
        paddle.reset();
        */
    }
}
