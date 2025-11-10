package engine;

import enums.PowerUpType;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import object.Paddle;
import object.powerup.PowerUp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import static utils.Constants.*;

public class InfoPanel {
    private GraphicsContext gc;
    private double animationTime = 0;
    private double pulseTime = 0;

    // Colors
    private final Color PANEL_BG = Color.rgb(20, 20, 40, 0.95);
    private final Color ACCENT_COLOR = Color.rgb(100, 200, 255);
    private final Color TEXT_COLOR = Color.rgb(230, 230, 250);
    private final Color HIGHLIGHT_COLOR = Color.rgb(255, 215, 0);

    // Fonts
    private final Font MY_FONT_8 = loadFont(8);
    private final Font MY_FONT_10 = loadFont(10);
    private final Font MY_FONT_12 = loadFont(12);
    private final Font MY_FONT_14 = loadFont(14);
    private final Font MY_FONT_18 = loadFont(18);
    private final Font MY_FONT_24 = loadFont(24);
    private final Font MY_FONT_36 = loadFont(36);

    public InfoPanel(GraphicsContext gc) {
        this.gc = gc;
    }

    public void render(int score, Paddle paddle, int levelNumber,
                       List<PowerUp> activePowerUps, double deltaTime) {
        animationTime += deltaTime;
        pulseTime += deltaTime * 3;

        drawPanelBackground();
        drawDividerLine();

        double currentY = INFO_PANEL_PADDING + 20;

        currentY = drawTitle(currentY);

        currentY = drawLevel(currentY, levelNumber);

        currentY = drawScore(currentY, score);

        currentY = drawLives(currentY, paddle);

        currentY = drawPowerUps(currentY, activePowerUps);

        drawStats(currentY, paddle, activePowerUps);
    }

    private void drawPanelBackground() {
        // Background
        gc.setFill(PANEL_BG);
        gc.fillRect(INFO_PANEL_X, 0, INFO_PANEL_WIDTH, SCREEN_HEIGHT);

        // Gradient effect
        double offset = Math.sin(animationTime) * 20;
        LinearGradient gradient = new LinearGradient(
                0, offset, 0, SCREEN_HEIGHT + offset,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(100, 150, 255, 0.05)),
                new Stop(0.5, Color.rgb(150, 100, 255, 0.1)),
                new Stop(1, Color.rgb(100, 150, 255, 0.05))
        );
        gc.setFill(gradient);
        gc.fillRect(INFO_PANEL_X, 0, INFO_PANEL_WIDTH, SCREEN_HEIGHT);

        // Floating particles effect
        drawFloatingParticles();
    }

    private void drawFloatingParticles() {
        gc.setFill(Color.rgb(150, 200, 255, 0.3));
        for (int i = 0; i < 30; i++) {
            double x = INFO_PANEL_X + 50 + Math.sin(animationTime * 0.5 + i) * 150;
            double y = (animationTime * 20 + i * 50) % SCREEN_HEIGHT;
            double size = 2 + Math.sin(animationTime + i) * 1;
            gc.setEffect(new DropShadow(8, Color.rgb(200, 240, 255, 0.9)));
            gc.fillOval(x, y, size, size);
            gc.setEffect(null);
        }
    }

    private void drawDividerLine() {
        // Glowing divider line
        gc.setStroke(ACCENT_COLOR);
        gc.setLineWidth(3);
        gc.setEffect(new DropShadow(15, ACCENT_COLOR));
        gc.strokeLine(INFO_PANEL_X, 0, INFO_PANEL_X, SCREEN_HEIGHT);
        gc.setEffect(null);
    }

    private double drawTitle(double currentY) {
        gc.setFont(MY_FONT_24);
        gc.setFill(HIGHLIGHT_COLOR);
        gc.setEffect(new DropShadow(10, Color.rgb(255, 215, 0, 0.8)));

        String title = "ARKANOID";
        double textWidth = getTextWidth(title, 24);
        gc.fillText(title, INFO_PANEL_X + (INFO_PANEL_WIDTH - textWidth) / 2, currentY);
        gc.setEffect(null);

        // Underline
        gc.setStroke(ACCENT_COLOR);
        gc.setLineWidth(2);
        gc.strokeLine(
                INFO_PANEL_X + INFO_PANEL_PADDING,
                currentY + 10,
                INFO_PANEL_X + INFO_PANEL_WIDTH - INFO_PANEL_PADDING,
                currentY + 10
        );

        return currentY + 40;
    }

    private double drawLevel(double currentY, int levelNumber) {
        currentY = drawSectionHeader("LEVEL", currentY);

        // Level number with animation
        gc.setFont(MY_FONT_36);
        double pulse = Math.sin(pulseTime) * 0.1 + 1;
        gc.setFill(Color.rgb(
                (int)(100 + Math.sin(animationTime * 2) * 50),
                (int)(200 + Math.sin(animationTime * 2 + 1) * 50),
                255
        ));

        String levelText = String.valueOf(levelNumber);
        double textWidth = getTextWidth(levelText, 36);
        double x = INFO_PANEL_X + (INFO_PANEL_WIDTH - textWidth) / 2;

        gc.save();
        gc.translate(x + textWidth / 2, currentY + 18);
        gc.scale(pulse, pulse);
        gc.fillText(levelText, -textWidth / 2, 0);
        gc.restore();

        return currentY + 60;
    }

    private double drawScore(double currentY, int score) {
        currentY = drawSectionHeader("SCORE", currentY);

        gc.setFont(MY_FONT_24);
        gc.setFill(HIGHLIGHT_COLOR);
        gc.setEffect(new DropShadow(8, Color.rgb(255, 215, 0, 0.6)));

        String scoreText = String.format("%,d", score);
        double textWidth = getTextWidth(scoreText, 24);
        gc.fillText(scoreText, INFO_PANEL_X + (INFO_PANEL_WIDTH - textWidth) / 2, currentY);
        gc.setEffect(null);

        return currentY + 50;
    }

    private double drawLives(double currentY, Paddle paddle) {
        currentY = drawSectionHeader("LIVES", currentY);

        // Draw hearts
        double heartSize = 28;
        double spacing = 5;
        double totalWidth = paddle.getLives() * heartSize + (paddle.getLives() - 1) * spacing;
        double startX = INFO_PANEL_X + (INFO_PANEL_WIDTH - totalWidth) / 2;

        for (int i = 0; i < paddle.getLives(); i++) {
            double x = startX + i * (heartSize + spacing);
            double pulse = Math.sin(pulseTime + i * 0.5) * 0.1 + 1;

            gc.save();
            gc.translate(x + heartSize / 2, currentY);
            gc.scale(pulse, pulse);
            drawHeart(-heartSize / 2, -heartSize / 2, heartSize);
            gc.restore();
        }

        return currentY + 50;
    }

    private void drawHeart(double x, double y, double size) {
        gc.setFill(Color.rgb(255, 50, 80));
        gc.setEffect(new DropShadow(10, Color.rgb(255, 0, 50, 0.8)));

        // Simple heart shape using bezier curves
        gc.beginPath();
        gc.moveTo(x + size / 2, y + size * 0.3);
        gc.bezierCurveTo(x + size / 2, y, x, y, x, y + size * 0.3);
        gc.bezierCurveTo(x, y + size * 0.6, x + size / 2, y + size, x + size / 2, y + size);
        gc.bezierCurveTo(x + size / 2, y + size, x + size, y + size * 0.6, x + size, y + size * 0.3);
        gc.bezierCurveTo(x + size, y, x + size / 2, y, x + size / 2, y + size * 0.3);
        gc.closePath();
        gc.fill();
        gc.setEffect(null);
    }

    private double drawPowerUps(double currentY, List<PowerUp> activePowerUps) {
        currentY = drawSectionHeader("POWER-UPS", currentY);

        if (activePowerUps.isEmpty()) {
            gc.setFont(MY_FONT_12);
            gc.setFill(Color.rgb(150, 150, 170));
            String text = "NONE";
            double textWidth = getTextWidth(text, 12);
            gc.fillText(text, INFO_PANEL_X + (INFO_PANEL_WIDTH - textWidth) / 2, currentY);
            return currentY + 30;
        }

        for (PowerUp powerUp : activePowerUps) {
            currentY = drawActivePowerUp(currentY, powerUp);
        }

        return currentY + 15;
    }

    private double drawActivePowerUp(double currentY, PowerUp powerUp) {
        double boxX = INFO_PANEL_X + INFO_PANEL_PADDING;
        double boxWidth = INFO_PANEL_WIDTH - INFO_PANEL_PADDING * 2;
        double boxHeight = 35;

        // Box background with glow
        Color powerUpColor = powerUp.getColor();
        gc.setFill(Color.rgb(
                (int)(powerUpColor.getRed() * 255 * 0.2),
                (int)(powerUpColor.getGreen() * 255 * 0.2),
                (int)(powerUpColor.getBlue() * 255 * 0.2),
                0.5
        ));
        gc.fillRoundRect(boxX, currentY, boxWidth, boxHeight, 8, 8);

        // Border with glow effect
        gc.setStroke(powerUpColor);
        gc.setLineWidth(1.5);
        gc.setEffect(new DropShadow(6, powerUpColor));
        gc.strokeRoundRect(boxX, currentY, boxWidth, boxHeight, 8, 8);
        gc.setEffect(null);

        // Power up icon
        double iconSize = 20;
        double iconX = boxX + 8;
        double iconY = currentY + boxHeight / 2 - iconSize / 2;

        gc.setFill(powerUpColor);
        gc.setEffect(new DropShadow(4, powerUpColor));
        gc.fillOval(iconX, iconY, iconSize, iconSize);
        gc.setEffect(null);

        // Power up name
        gc.setFont(MY_FONT_12);
        gc.setFill(TEXT_COLOR);
        String name = getPowerUpName(powerUp.getType());
        gc.fillText(name, boxX + 35, currentY + 13);

        // Duration bar
        if (powerUp.getDuration() > 0) {
            double durationLeft = powerUp.getDurationLeft();
            double totalDuration = powerUp.getDuration() / 1000.0;
            double progress = Math.max(0, Math.min(1, durationLeft / totalDuration));

            double barX = boxX + 35;
            double barY = currentY + 20;
            double barWidth = boxWidth - 45;
            double barHeight = 6;

            // Background bar
            gc.setFill(Color.rgb(50, 50, 70));
            gc.fillRoundRect(barX, barY, barWidth, barHeight, 3, 3);

            // Progress bar
            gc.setFill(powerUpColor);
            gc.fillRoundRect(barX, barY, barWidth * progress, barHeight, 3, 3);

            // Time text
            gc.setFont(MY_FONT_12);
            gc.setFill(Color.rgb(200, 200, 220));
            gc.fillText(durationLeft < 10 ? String.format("0:0%.0f", durationLeft) : String.format("0:%.0f", durationLeft), boxX + 110, currentY + 13);
        }

        return currentY + boxHeight + 8;
    }

    private double drawStats(double currentY, Paddle paddle, List<PowerUp> activePowerUps) {
        currentY = drawSectionHeader("STATS", currentY);

        gc.setFont(MY_FONT_14);
        gc.setFill(TEXT_COLOR);

        double x = INFO_PANEL_X + INFO_PANEL_PADDING + 10;

        // Active effects count
        currentY = drawStatLine(x, currentY, "Active Effects:",
                String.valueOf(activePowerUps.size()));

        return currentY;
    }

    private double drawStatLine(double x, double y, String label, String value) {
        gc.setFill(Color.rgb(180, 180, 200));
        gc.fillText(label, x, y);

        gc.setFill(ACCENT_COLOR);
        gc.setFont(MY_FONT_14);
        gc.fillText(value, x + 110, y);
        gc.setFont(MY_FONT_14);

        return y + 25;
    }

    private double drawSectionHeader(String title, double currentY) {
        gc.setFont(MY_FONT_18);
        gc.setFill(ACCENT_COLOR);

        double textWidth = getTextWidth(title, 18);
        gc.fillText(title, INFO_PANEL_X + (INFO_PANEL_WIDTH - textWidth) / 2, currentY);

        // Decorative lines
        double lineY = currentY + 5;
        gc.setStroke(ACCENT_COLOR);
        gc.setLineWidth(1);
        gc.strokeLine(INFO_PANEL_X + INFO_PANEL_PADDING, lineY,
                INFO_PANEL_X + (INFO_PANEL_WIDTH - textWidth) / 2 - 10, lineY);
        gc.strokeLine(INFO_PANEL_X + (INFO_PANEL_WIDTH + textWidth) / 2 + 10, lineY,
                INFO_PANEL_X + INFO_PANEL_WIDTH - INFO_PANEL_PADDING, lineY);

        return currentY + 35;
    }

    private String getPowerUpName(PowerUpType type) {
        switch (type) {
            case EXPAND_PADDLE: return "EXPAND";
            case SHRINK_PADDLE: return "SHRINK";
            case SLOW_BALL: return "SLOW";
            case FAST_BALL: return "FAST";
            case MULTI_BALL: return "MULTI";
            case POINTS_MULTIPLIER: return "POINTS X2";
            case LASER: return "LASER";
            case SHIELD: return "SHIELD";
            case EXTRA_LIFE: return "LIFE";
            default: return "???";
        }
    }

    private double getTextWidth(String text, double fontSize) {
        return text.length() * fontSize * 0.6;
    }

    private Font loadFont(double size) {
        Font font = null;
        try {
            File fontFile = new File("assets/fonts/font.ttf");
            FileInputStream fontIS = new FileInputStream(fontFile);
            font = Font.loadFont(fontIS, size);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }

        return font;
    }
}