package effect;

import javafx.animation.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static utils.Constants.*;

public class ParticleEngine {
    private GraphicsContext gc;
    private List<ParticleEmitter> emitters;

    public ParticleEngine(GraphicsContext gc) {
        this.gc = gc;
        this.emitters = new ArrayList<>();
    }

    public ParticleEmitter createEmitter(double x, double y, ParticleConfig config) {
        ParticleEmitter emitter = new ParticleEmitter(x, y, config);
        emitters.add(emitter);
        return emitter;
    }

    public void removeEmitter(ParticleEmitter emitter) {
        emitters.remove(emitter);
    }

    public void update(double deltaTime) {
        Iterator<ParticleEmitter> iterator = emitters.iterator();
        while (iterator.hasNext()) {
            ParticleEmitter emitter = iterator.next();
            emitter.update(deltaTime);
            if (!emitter.isActive() && emitter.getParticleCount() == 0) {
                iterator.remove();
            }
        }
    }

    public void render() {
        for (ParticleEmitter emitter : emitters) {
            emitter.render(gc);
        }
    }

    public void clear() {
        emitters.clear();
    }

    public int getTotalParticleCount() {
        int count = 0;
        for (ParticleEmitter emitter : emitters) {
            count += emitter.getParticleCount();
        }
        return count;
    }

    public void brickExplosion(double x, double y, Color brickColor) {
        ParticleConfig config = new ParticleConfig()
                .setSpeed(100, 250)
                .setAngle(0, 360)
                .setLifeTime(0.3, 0.8)
                .setSize(3, 8)
                .setGravity(300)
                .setSpreadRadius(15)
                .setColors(
                        brickColor,
                        brickColor.brighter(),
                        brickColor.darker(),
                        Color.WHITE
                );

        ParticleEmitter emitter = createEmitter(x, y, config);
        emitter.burst(20);
    }

    public void ballTrail(double x, double y) {
        ParticleConfig config = new ParticleConfig()
                .setSpeed(10, 30)
                .setAngle(0, 360)
                .setLifeTime(0.2, 0.4)
                .setSize(10, 15)
                .setGravity(0)
                .setSpreadRadius(12)
                .setColors(
                        Color.rgb(66, 33, 0),
                        Color.rgb(102, 51, 0),
                        Color.rgb(150, 75, 0),
                        Color.rgb(178, 89, 0),
                        Color.rgb(222, 146, 79),
                        Color.rgb(236, 173, 124)
                );

        ParticleEmitter emitter = createEmitter(x, y, config);
        emitter.burst(3);
    }

    public void paddleTrail(double x, double y) {
        ParticleConfig config = new ParticleConfig()
                .setSpeed(10, 30)
                .setAngle(0, 360)
                .setLifeTime(0.5, 0.8)
                .setSize(10, 15)
                .setGravity(0)
                .setSpreadRadius(12)
                .setColors(
                        Color.rgb(66, 33, 0),
                        Color.rgb(102, 51, 0),
                        Color.rgb(150, 75, 0),
                        Color.rgb(178, 89, 0),
                        Color.rgb(222, 146, 79),
                        Color.rgb(236, 173, 124)
                );

        ParticleEmitter emitter = createEmitter(x, y, config);
        emitter.burst(3);
    }

    public void redBallTrail(double x, double y) {
        ParticleConfig config = new ParticleConfig()
                .setSpeed(10, 30)
                .setAngle(0, 360)
                .setLifeTime(0.12, 0.36)
                .setSize(10, 15)
                .setGravity(0)
                .setSpreadRadius(12)
                .setColors(
                        Color.rgb(255,0,0),
                        Color.rgb(255,90,0),
                        Color.rgb(255,154,0),
                        Color.rgb(255,206,0),
                        Color.rgb(255,232,8)
                );

        ParticleEmitter emitter = createEmitter(x, y, config);
        emitter.burst(3);
    }

    public void blueBallTrail(double x, double y) {
        ParticleConfig config = new ParticleConfig()
                .setSpeed(10, 30)
                .setAngle(0, 360)
                .setLifeTime(0.36, 0.63)
                .setSize(10, 15)
                .setGravity(0)
                .setSpreadRadius(12)
                .setColors(
                        Color.rgb(231,251,255),
                        Color.rgb(207,247,255),
                        Color.rgb(179,242,255),
                        Color.rgb(149,237,255),
                        Color.rgb(120,233,255)
                );

        ParticleEmitter emitter = createEmitter(x, y, config);
        emitter.burst(3);
    }

    public void powerUpCollect(double x, double y, Color color) {
        ParticleConfig config = new ParticleConfig()
                .setSpeed(50, 150)
                .setAngle(0, 360)
                .setLifeTime(0.5, 1.0)
                .setSize(3, 6)
                .setGravity(-50)
                .setSpreadRadius(10)
                .setColors(color, color.brighter(), Color.WHITE, Color.YELLOW);

        ParticleEmitter emitter = createEmitter(x, y, config);
        emitter.burst(30);
    }

    public void powerUpTrail(double x, double y, Color powerUpColor) {
        ParticleConfig config = new ParticleConfig()
                .setSpeed(10, 30)
                .setAngle(0, 360)
                .setLifeTime(0.6, 0.8)
                .setSize(6, 12)
                .setGravity(0)
                .setSpreadRadius(10)
                .setColors(
                        powerUpColor,
                        powerUpColor.brighter(),
                        powerUpColor.darker(),
                        powerUpColor.desaturate(),
                        powerUpColor.saturate()
                );

        ParticleEmitter emitter = createEmitter(x, y, config);
        emitter.burst(5);
    }

    public void paddleHit(double x, double y) {
        ParticleConfig config = new ParticleConfig()
                .setSpeed(80, 180)
                .setAngle(-150, -30)
                .setLifeTime(0.3, 0.6)
                .setSize(2, 5)
                .setGravity(200)
                .setSpreadRadius(20)
                .setColors(Color.WHITE, Color.LIGHTGRAY, Color.YELLOW);

        ParticleEmitter emitter = createEmitter(x, y, config);
        emitter.burst(15);
    }

    public void hitLeftBound(double x, double y) {
        ParticleConfig config = new ParticleConfig()
                .setSpeed(80, 180)
                .setAngle(-60, 60)
                .setLifeTime(0.3, 0.6)
                .setSize(5, 10)
                .setGravity(200)
                .setSpreadRadius(20)
                .setColors(
                        Color.rgb(16, 52, 166),
                        Color.rgb(65, 47, 136),
                        Color.rgb(114, 43, 106),
                        Color.rgb(162, 38, 75),
                        Color.rgb(211, 33, 45),
                        Color.rgb(246, 45, 45)
                );

        ParticleEmitter emitter = createEmitter(x, y, config);
        emitter.burst(20);
    }

    public void hitRightBound(double x, double y) {
        ParticleConfig config = new ParticleConfig()
                .setSpeed(80, 180)
                .setAngle(-120, 120)
                .setLifeTime(0.3, 0.6)
                .setSize(5, 10)
                .setGravity(200)
                .setSpreadRadius(20)
                .setColors(
                        Color.rgb(16, 52, 166),
                        Color.rgb(65, 47, 136),
                        Color.rgb(114, 43, 106),
                        Color.rgb(162, 38, 75),
                        Color.rgb(211, 33, 45),
                        Color.rgb(246, 45, 45)
                );

        ParticleEmitter emitter = createEmitter(x, y, config);
        emitter.burst(20);
    }

    public void hitUpperBound(double x, double y) {
        ParticleConfig config = new ParticleConfig()
                .setSpeed(80, 180)
                .setAngle(30, 150)
                .setLifeTime(0.3, 0.6)
                .setSize(5, 10)
                .setGravity(200)
                .setSpreadRadius(20)
                .setColors(
                        Color.rgb(16, 52, 166),
                        Color.rgb(65, 47, 136),
                        Color.rgb(114, 43, 106),
                        Color.rgb(162, 38, 75),
                        Color.rgb(211, 33, 45),
                        Color.rgb(246, 45, 45)
                );

        ParticleEmitter emitter = createEmitter(x, y, config);
        emitter.burst(20);
    }

    public void firework(double x, double y) {
        ParticleConfig config = new ParticleConfig()
                .setSpeed(150, 300)
                .setAngle(0, 360)
                .setLifeTime(0.8, 1.5)
                .setSize(4, 8)
                .setGravity(100)
                .setSpreadRadius(5)
                .setColors(
                        Color.RED,
                        Color.ORANGE,
                        Color.YELLOW,
                        Color.PINK,
                        Color.PURPLE,
                        Color.CYAN,
                        Color.rgb(255,179,186),
                        Color.rgb(255,223,186),
                        Color.rgb(255,255,186),
                        Color.rgb(186,255,201),
                        Color.rgb(186,225,255),
                        Color.rgb(168,230,207),
                        Color.rgb(220,237,193),
                        Color.rgb(255,211,182),
                        Color.rgb(255,170,165),
                        Color.rgb(255,139,148)
                );

        ParticleEmitter emitter = createEmitter(x, y, config);
        emitter.burst(50);
    }

    public void electricExplosion(double x, double y) {
        ParticleConfig config = new ParticleConfig()
                .setSpeed(150, 300)
                .setAngle(0, 360)
                .setLifeTime(0.3, 0.8)
                .setSize(3, 8)
                .setGravity(0)
                .setSpreadRadius(10)
                .setColors(
                        Color.CYAN,
                        Color.LIGHTBLUE,
                        Color.WHITE,
                        Color.YELLOW
                );

        ParticleEmitter emitter = createEmitter(x, y, config);
        emitter.burst(30);
    }

    public void firstExplosion(double x, double y) {
        ParticleConfig config = new ParticleConfig()
                .setSpeed(200, 400)
                .setAngle(0, 360)
                .setLifeTime(0.5, 1.2)
                .setSize(5, 12)
                .setGravity(150)
                .setSpreadRadius(100)
                .setColors(
                        Color.ORANGE,
                        Color.RED,
                        Color.YELLOW,
                        Color.WHITE,
                        Color.DARKORANGE
                );

        ParticleEmitter emitter = createEmitter(x, y, config);
        emitter.burst(60);
    }

    public void secondExplosion(double x, double y) {
        ParticleConfig config = new ParticleConfig()
                .setSpeed(100, 200)
                .setAngle(0, 360)
                .setLifeTime(0.3, 0.7)
                .setSize(3, 8)
                .setGravity(200)
                .setSpreadRadius(30)
                .setColors(
                        Color.ORANGE,
                        Color.RED,
                        Color.YELLOW
                );

        ParticleEmitter emitter = createEmitter(x, y, config);
        emitter.burst(25);
    }

    public void electricSpark(double x, double y) {
        ParticleConfig config = new ParticleConfig()
                .setSpeed(50, 120)
                .setAngle(0, 360)
                .setLifeTime(0.2, 0.5)
                .setSize(2, 5)
                .setGravity(0)
                .setSpreadRadius(5)
                .setColors(
                        javafx.scene.paint.Color.CYAN,
                        javafx.scene.paint.Color.WHITE
                );

        ParticleEmitter emitter = createEmitter(x, y, config);
        emitter.burst(10);
    }

    public void fire(double x, double y) {
        ParticleConfig config = new ParticleConfig()
                .setSpeed(10, 30)
                .setAngle(-120, - 60)
                .setLifeTime(0.5, 1.5)
                .setSize(10, 15)
                .setGravity(-200)
                .setSpreadRadius(12)
                .setColors(
                        Color.rgb(66, 33, 0),
                        Color.rgb(102, 51, 0),
                        Color.rgb(150, 75, 0),
                        Color.rgb(178, 89, 0),
                        Color.rgb(222, 146, 79),
                        Color.rgb(236, 173, 124)
                );

        ParticleEmitter emitter = createEmitter(x, y, config);
        emitter.burst(3);
    }

    public void laserHit(double x, double y) {
        ParticleConfig config = new ParticleConfig()
                .setSpeed(100, 250)
                .setAngle(0, 360)
                .setLifeTime(0.3, 0.8)
                .setSize(3, 8)
                .setGravity(300)
                .setSpreadRadius(15)
                .setColors(
                        Color.CYAN,
                        Color.LIGHTBLUE,
                        Color.WHITE
                );

        ParticleEmitter emitter = createEmitter(x, y, config);
        emitter.burst(15);
    }

    public void lightningEffect(Pane root, double x1, double y1, double x2, double y2) {
        Path path = new Path();

        // Di chuyển con trỏ vẽ đến vị trí (x1, y1)
        path.getElements().add(new MoveTo(x1, y1));

        // Vector hướng từ điểm đầu đến điểm cuối
        double dx = x2 - x1;
        double dy = y2 - y1;
        double distance = Math.hypot(dx, dy);

        // Mỗi segment cách nhau 20 pixels, +2 để phòng trường hợp distance nhỏ
        int segments = (int) (distance / 20) + 2;

        Random random = new Random();

        for (int i = 1; i < segments; i++) {
            // Vị trí ban đầu trên đường thẳng
            double ratio = i / (double) segments;
            double baseX = x1 + dx * ratio;
            double baseY = y1 + dy * ratio;

            // Độ lệch ngẫu nhiên
            double perpX = -dy / distance; // perpendicular
            double perpY = dx / distance; // Vector vuông góc với đường thẳng chính
            double offset = (random.nextDouble() - 0.5 * 30); // Lệch ~15 pixels

            double finalX = baseX + perpX * offset;
            double finalY = baseY + perpY * offset;

            // Vẽ 1 đường từ vị trí trước đó đến ví trí mới
            path.getElements().add(new LineTo(finalX, finalY));
        }
        // Vị trí kết thúc
        path.getElements().add(new LineTo(x2, y2));

        // Thêm hiệu ứng
        path.setStroke(Color.CYAN);
        path.setStrokeWidth(3);
        Glow glow = new Glow(0.8);
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.CYAN);
        shadow.setRadius(10);
        shadow.setSpread(0.5);
        glow.setInput(shadow);
        path.setEffect(glow);

        root.getChildren().add(path);

        // Hiệu ứng nhấp nháy
        Timeline flashTimeline = new Timeline(
                new KeyFrame(Duration.millis(0), event -> path.setVisible(true)),
                new KeyFrame(Duration.millis(30), event -> path.setVisible(false)),
                new KeyFrame(Duration.millis(60), event -> path.setVisible(true))
        );
        flashTimeline.play();

        PauseTransition pause = new PauseTransition(Duration.millis(100));
        pause.setOnFinished(event -> root.getChildren().remove(path));
        pause.play();
        electricSpark(x2, y2);
    }

    public void shockwave(Pane root, double centerX, double centerY, double radius) {
        Circle shockwave = new Circle(centerX, centerY, 10);
        shockwave.setFill(Color.TRANSPARENT);
        shockwave.setStroke(Color.ORANGE);
        shockwave.setStrokeWidth(3);
        shockwave.setEffect(new Glow(0.8));
        root.getChildren().add(shockwave);

        // Hiệu ứng lan toả
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(shockwave.radiusProperty(), 10),
                        new KeyValue(shockwave.opacityProperty(), 1.0)
                ),
                new KeyFrame(Duration.millis(1000),
                        new KeyValue(shockwave.radiusProperty(), radius),
                        new KeyValue(shockwave.opacityProperty(), 0.0)
                )
        );

        timeline.setOnFinished(event -> root.getChildren().remove(shockwave));
        timeline.play();

        PauseTransition pause = new PauseTransition(Duration.millis(100));
        pause.setOnFinished(event -> {
            Circle shockwave2 = new Circle(centerX, centerY, 10);
            shockwave2.setFill(Color.TRANSPARENT);
            shockwave2.setStroke(Color.RED);
            shockwave2.setStrokeWidth(2);
            shockwave2.setEffect(new Glow(0.6));
            root.getChildren().add(shockwave2);

            Timeline timeline2 = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(shockwave2.radiusProperty(), 10),
                            new KeyValue(shockwave2.opacityProperty(), 0.8)
                    ),
                    new KeyFrame(Duration.millis(900),
                            new KeyValue(shockwave2.radiusProperty(), radius),
                            new KeyValue(shockwave2.opacityProperty(), 0.0)
                    )
            );

            timeline2.setOnFinished(event2 -> root.getChildren().remove(shockwave2));
            timeline2.play();
        });
        pause.play();
    }

    public void screenShake(Pane root, int duration, int intensity) {
        double originalX = 0.0;
        double originalY = 0.0;

        Timeline timeline = new Timeline();
        int frames = duration / 16; // ~60 FPS

        Random random = new Random();

        for (int i = 0; i < frames; i++) {
            double offsetX = (random.nextDouble() - 0.5) * 2 * intensity;
            double offsetY = (random.nextDouble() - 0.5) * 2 * intensity;
            timeline.getKeyFrames().add(
                    new KeyFrame(
                            Duration.millis(i * 16),
                            new KeyValue(root.translateXProperty(), originalX + offsetX),
                            new KeyValue(root.translateYProperty(), originalY + offsetY)
                    )
            );
        }

        timeline.getKeyFrames().add(
                new KeyFrame(
                        Duration.millis(duration),
                        new KeyValue(root.translateXProperty(), originalX),
                        new KeyValue(root.translateYProperty(), originalY)
                )
        );

        timeline.setOnFinished(event -> {
            root.setTranslateX(originalX);
            root.setTranslateY(originalY);
        });

        timeline.play();
    }

    public void shieldActivate(double x, double y) {
        ParticleConfig config = new ParticleConfig()
                .setSpeed(100, 250)
                .setAngle(-120, -60)
                .setLifeTime(0.5, 1.2)
                .setSize(4, 10)
                .setGravity(-50)
                .setSpreadRadius(SCREEN_WIDTH / 2)
                .setColors(
                        Color.CYAN,
                        Color.DEEPSKYBLUE,
                        Color.LIGHTBLUE,
                        Color.WHITE
                );

        ParticleEmitter emitter = createEmitter(x, y, config);
        emitter.burst(50);
    }

    public void shieldDeflect(double x, double y) {
        ParticleConfig config = new ParticleConfig()
                .setSpeed(80, 180)
                .setAngle(-150, -30)
                .setLifeTime(0.3, 0.7)
                .setSize(3, 7)
                .setGravity(100)
                .setSpreadRadius(15)
                .setColors(
                        Color.CYAN,
                        Color.LIGHTBLUE,
                        Color.WHITE
                );

        ParticleEmitter emitter = createEmitter(x, y, config);
        emitter.burst(20);
    }
}
