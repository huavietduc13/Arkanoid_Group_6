package src.effect;

import javafx.scene.paint.Color;

public class ParticleConfig {
    public double minSpeed;
    public double maxSpeed;
    public double minAngle;
    public double maxAngle;
    public double minLifeTime;
    public double maxLifeTime;
    public double minSize;
    public double maxSize;
    public double gravity;
    public double emissionRate;
    public double spreadRadius;
    public Color[] colors;

    public ParticleConfig() {
        this.minSpeed = 50.0;
        this.maxSpeed = 150.0;
        this.minAngle = 0.0;
        this.maxAngle = 360.0;
        this.minLifeTime = 0.5;
        this.maxLifeTime = 1.5;
        this.minSize = 2.0;
        this.maxSize = 6.0;
        this.gravity = 100.0;
        this.emissionRate = 30.0;
        this.spreadRadius = 10.0;
        this.colors = new Color[]{Color.WHITE};
    }

    public ParticleConfig setSpeed(double min, double max) {
        this.minSpeed = min;
        this.maxSpeed = max;
        return this;
    }

    public ParticleConfig setAngle(double min, double max) {
        this.minAngle = min;
        this.maxAngle = max;
        return this;
    }

    public ParticleConfig setLifeTime(double min, double max) {
        this.minLifeTime = min;
        this.maxLifeTime = max;
        return this;
    }

    public ParticleConfig setSize(double min, double max) {
        this.minSize = min;
        this.maxSize = max;
        return this;
    }

    public ParticleConfig setGravity(double gravity) {
        this.gravity = gravity;
        return this;
    }

    public ParticleConfig setEmissionRate(double rate) {
        this.emissionRate = rate;
        return this;
    }

    public ParticleConfig setSpreadRadius(double radius) {
        this.spreadRadius = radius;
        return this;
    }

    public ParticleConfig setColors(Color... colors) {
        this.colors = colors;
        return this;
    }
}
