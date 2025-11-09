package src.object.powerup;

import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import src.object.Ball;

import static src.utils.Constants.*;

public class Shield {
    private double y;
    private double width;
    private double height;
    private boolean active;
    private Rectangle collisionShape;

    private double alpha;
    private double pulsePhase;

    private Color shieldColor;
    private Color glowColor;

    public Shield() {
        this.y = SHIELD_POS_Y;
        this.width = SCREEN_WIDTH;
        this.height = SHIELD_HEIGHT;
        this.active = false;
        this.alpha = 0.0;
        this.pulsePhase = 0;
        this.shieldColor = Color.CYAN;
        this.glowColor = Color.LIGHTBLUE;

        this.collisionShape = new Rectangle(0, y, width, height);
        this.collisionShape.setVisible(false);
    }

    public void activate() {
        this.active = true;
        this.alpha = 0.8;
    }

    public void deactivate() {
        this.active = false;
        this.alpha = 0.0;
    }

    public void update(double deltaTime) {
        if (active) {
            pulsePhase += deltaTime * 3.0;
            if (pulsePhase > Math.PI * 2) {
                pulsePhase -= Math.PI * 2;
            }
            alpha = 0.5 + 0.3 * Math.sin(pulsePhase);
        } else {
            alpha = Math.max(0, alpha - deltaTime * 2);
        }
    }

    public void render(GraphicsContext gc) {
        if (alpha > 0.01) {
            double pulseIntensity = 0.5 + 0.5 * Math.sin(pulsePhase);

            Color mainColor = new Color(
                    shieldColor.getRed(),
                    shieldColor.getGreen(),
                    shieldColor.getBlue(),
                    alpha
            );
            gc.setStroke(mainColor);
            gc.setLineWidth(5);
            gc.strokeRoundRect(0, y, width, height, 20, 20);

            Color bottomGlow = new Color(
                    glowColor.getRed(),
                    glowColor.getGreen(),
                    glowColor.getBlue(),
                    alpha * 0.3 * pulseIntensity
            );
            gc.setFill(bottomGlow);
            gc.fillRect(0, y + height, width, height);

            Color highlightColor = new Color(
                    1.0,
                    1.0,
                    1.0,
                    alpha * 0.6 * pulseIntensity
            );
            gc.setFill(highlightColor);
            gc.fillRect(0, y, width, height);

            if (active) {
                double scanLineY = y + (pulsePhase / (Math.PI * 2)) * height;
                Color scanColor = new Color(1.0, 1.0, 1.0, alpha * 0.8);
                gc.setStroke(scanColor);
                gc.setLineWidth(2);
                gc.strokeLine(0, scanLineY, width, scanLineY);
            }

            if (active) {
                gc.setStroke(new Color(1.0, 1.0, 1.0, alpha * 0.3));
                gc.setLineWidth(1);

                int verticalLines = 20;
                for (int i = 0; i < verticalLines; i++) {
                    double x = (width / verticalLines) * i;
                    gc.strokeLine(x, y, x, y + height);
                }
            }
        }
    }

    public boolean intersects(Ball ball) {
        if (!active) {
            return false;
        }

        Bounds ballBounds = ball.getCollisionBounds();

        double ballBottom = ballBounds.getMaxY();
        double ballCenterX = ballBounds.getMinX() + ballBounds.getWidth() / 2;

        return ballBottom >= y &&
                ballBottom <= y + height + 5 &&
                ball.getVy() > 0;
    }

    public void handleBallCollision(Ball ball) {
        if (!active) {
            return;
        }

        if (intersects(ball)) {
            ball.reverseY();
            ball.setCenterY(y - ball.getRadius());
        }
    }

    public boolean isActive() {
        return active;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public Rectangle getCollisionShape() {
        return collisionShape;
    }

    public void setShieldColor(Color color) {
        this.shieldColor = color;
    }

    public void setGlowColor(Color color) {
        this.glowColor = color;
    }
}
