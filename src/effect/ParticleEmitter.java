package src.effect;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class ParticleEmitter {
    private double x;
    private double y;
    private double emissionRate;
    private double timeSinceLastEmission;
    private boolean active;
    private Random random;
    private List<Particle> particles;
    private ParticleConfig config;

    public ParticleEmitter(double x, double y, ParticleConfig config) {
        this.x = x;
        this.y = y;
        this.emissionRate = config.emissionRate;
        this.timeSinceLastEmission = 0;
        this.active = false;
        this.random = new Random();
        this.particles = new ArrayList<>();
        this.config = config;
    }

    public void emit(int count) {
        for (int i = 0; i < count; i++) {
            emitSingleParticle();
        }
    }

    public void emitSingleParticle() {
        double angle = config.minAngle + random.nextDouble() * (config.maxAngle - config.minAngle);
        double speed = config.minSpeed + random.nextDouble() * (config.maxSpeed - config.minSpeed);
        double vx = Math.cos(Math.toRadians(angle)) * speed;
        double vy = Math.sin(Math.toRadians(angle)) * speed;
        double lifeTime = config.minLifeTime + random.nextDouble() * (config.maxLifeTime - config.minLifeTime);
        double size = config.minSize + random.nextDouble() * (config.maxSize - config.minSize);
        Color color = config.colors[random.nextInt(config.colors.length)];
        double offsetX = (random.nextDouble() - 0.5) * config.spreadRadius;
        double offsetY = (random.nextDouble() - 0.5) * config.spreadRadius;
        Particle particle = new Particle(
                x + offsetX,
                y + offsetY,
                vx,
                vy,
                lifeTime,
                size,
                config.gravity,
                color
        );
        particles.add(particle);
    }

    public void update(double deltaTime) {
        if (active) {
            timeSinceLastEmission += deltaTime;
            double emissionInterval = 1.0 / emissionRate;
            while (timeSinceLastEmission >= emissionInterval) {
                emitSingleParticle();
                timeSinceLastEmission -= emissionInterval;
            }
        }

        Iterator<Particle> iterator = particles.iterator();
        while (iterator.hasNext()) {
            Particle particle = iterator.next();
            particle.update(deltaTime);
            if (!particle.isAlive()) {
                iterator.remove();
            }
        }
    }

    public void render(GraphicsContext gc) {
        for (Particle particle : particles) {
            particle.render(gc);
        }
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void start() {
        active = true;
    }

    public void stop() {
        active = false;
    }

    public void burst(int count) {
        emit(count);
    }

    public void clear() {
        particles.clear();
    }

    public int getParticleCount() {
        return particles.size();
    }

    public boolean isActive() {
        return active;
    }
}
