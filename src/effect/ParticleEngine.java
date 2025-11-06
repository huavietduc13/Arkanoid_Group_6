package effect;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
                .setColors(brickColor, brickColor.brighter(), brickColor.darker(), Color.WHITE);

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
//                        Color.rgb(137, 207, 241)
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

    public void explosion(double x, double y) {
        ParticleConfig config = new ParticleConfig()
                .setSpeed(200, 400)
                .setAngle(0, 360)
                .setLifeTime(0.5, 1.2)
                .setSize(5, 12)
                .setGravity(150)
                .setSpreadRadius(15)
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

    public void secondaryExplosion(double x, double y) {
        ParticleConfig config = new ParticleConfig()
                .setSpeed(100, 200)
                .setAngle(0, 360)
                .setLifeTime(0.3, 0.7)
                .setSize(3, 8)
                .setGravity(200)
                .setSpreadRadius(10)
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
}
