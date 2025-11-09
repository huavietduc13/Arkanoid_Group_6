package src.effect;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Particle {
    private double x;
    private double y;
    private double vx;
    private double vy;
    private double lifeTime;
    private double maxLifeTime;
    private double size;
    private double initialSize;
    private double gravity;
    private double alpha;
    private Color color;

    public Particle(double x, double y, double vx, double vy,
                    double lifeTime, double size, double gravity, Color color) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.lifeTime = lifeTime;
        this.maxLifeTime = lifeTime;
        this.size = size;
        this.initialSize = size;
        this.gravity = gravity;
        this.alpha = 1.0;
        this.color = color;
    }

    public void update(double deltaTime) {
        x += vx * deltaTime;
        y += vy * deltaTime;
        vy += gravity * deltaTime;

        lifeTime -= deltaTime;
        double lifeRatio = lifeTime / maxLifeTime;
        alpha = Math.max(0, lifeRatio);
        size = initialSize * lifeRatio;

        vx *= 0.98;
        vy *= 0.98;
    }

    public void render(GraphicsContext gc) {
        if (isAlive()) {
            Color centerColor = new Color(
                    color.getRed(),
                    color.getGreen(),
                    color.getBlue(),
                    alpha
            );

            Color outerColor = new Color(
                    color.getRed() * 0.5,
                    color.getGreen() * 0.5,
                    color.getBlue() * 0.5,
                    alpha * 0.3
            );

            int layers = 6;
            for (int i = layers; i >= 0; i--) {
                double layerRatio = i / (double) layers;
                double layerSize = size * layerRatio;
                double layerAlpha = alpha * layerRatio;

                Color layerColor = centerColor.interpolate(outerColor, 1 - layerRatio);
                layerColor = new Color(
                        layerColor.getRed(),
                        layerColor.getGreen(),
                        layerColor.getBlue(),
                        layerAlpha
                );

                gc.setFill(layerColor);
                gc.setStroke(layerColor);
                if (Math.random() < 0.25) {
                    gc.strokeRect(x - layerSize / 2, y - layerSize / 2, layerSize, layerSize);
                } else if (Math.random() < 0.5) {
                    gc.fillRect(x - layerSize / 2, y - layerSize / 2, layerSize, layerSize);
                } else if (Math.random() < 0.75) {
                    gc.strokeOval(x - layerSize / 2, y - layerSize / 2, layerSize, layerSize);
                } else {
                    gc.fillOval(x - layerSize / 2, y - layerSize / 2, layerSize, layerSize);
                }
            }
        }
    }

    public boolean isAlive() {
        return lifeTime > 0;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getVx() {
        return vx;
    }

    public double getVy() {
        return vy;
    }

    public double getLifeTime() {
        return lifeTime;
    }

    public double getSize() {
        return size;
    }

    public Color getColor() {
        return color;
    }
}
