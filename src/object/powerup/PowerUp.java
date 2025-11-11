package object.powerup;

import enums.PowerUpType;
import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import object.Ball;
import object.GameObject;
import object.Paddle;

import static utils.Constants.*;

public abstract class PowerUp extends GameObject {
    protected PowerUpType type;

    protected double radius;
    protected double fallSpeed;
    protected double duration;
    protected boolean collected;
    protected boolean activated;
    protected long activationTime;

    private Circle collisionShape;

    public PowerUp(String imagePath, double x, double y, double duration
    ) {
        super(imagePath, x, y, POWERUP_RADIUS * 2, POWERUP_RADIUS * 2);
        this.radius = POWERUP_RADIUS;
        this.fallSpeed = POWERUP_FALLSPEED;
        this.duration = duration;
        this.collected = false;
        this.activated = false;
        this.activationTime = 0;

        this.collisionShape = new Circle(x + radius, y + radius, radius);
        this.collisionShape.setVisible(true);
        this.collisionShape.setFill(Color.TRANSPARENT);
        this.collisionShape.setStroke(Color.BLACK);

        this.imageView.setFitWidth(radius * 2);
        this.imageView.setFitHeight(radius * 2);
        this.imageView.setPreserveRatio(false);
    }

    @Override
    public void update() {
        if (collected) {
            return;
        }

        double newY = getY() + fallSpeed;
        setY(newY);
        updateCollisionShape();
    }

    private void updateCollisionShape() {
        collisionShape.setCenterX(getX() + radius);
        collisionShape.setCenterY(getY() + radius);
    }

    public boolean intersects(Paddle paddle) {
        if (collected) {
            return false;
        }

        Bounds powerUpBounds = collisionShape.getBoundsInParent();
        Bounds paddleBounds = paddle.getCollisionBounds();

        return powerUpBounds.intersects(paddleBounds);
    }

    public abstract void activate(Paddle paddle, Ball ball);

    public abstract void deactivate(Paddle paddle, Ball ball);

    public boolean isExpired() {
        if (duration == 0) {
            return false;   // Instant power up like extra life
        }

        if (activationTime == 0) {
            return false;   // Not activated
        }

        long currentTime = System.currentTimeMillis();
        return (currentTime - activationTime) >= duration;
    }

    public void setExpired() {
        if (duration > 0) {
            this.activationTime = 1;
        }
    }

    public void collect(Paddle paddle, Ball ball) {
//        if (collected) {
//            return;
//        }

        collected = true;
        activationTime = System.currentTimeMillis();
        activate(paddle, ball);
    }

    public PowerUpType getType() {
        return type;
    }

    public boolean isCollected() {
        return collected;
    }

    public boolean isOutOfScreen() {
        return getY() > SCREEN_HEIGHT;
    }

    public boolean isTimedPowerUp() {
        return getDuration() > 0;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    public double getDuration() {
        return duration;
    }

    public long getRemainingTime() {
        return ((long) duration - (System.currentTimeMillis() - activationTime)) / 1000;
    }

    public long getActivationTime() {
        return activationTime;
    }

    public Circle getCollisionShape() {
        return collisionShape;
    }

    public void setActivationTime(long activationTime) {
        this.activationTime = activationTime;
    }

    public void setCenterX(double x) {
        collisionShape.setCenterX(x);
        imageView.setX(x + radius);
    }

    public double getCenterX() {
        return getX() + radius;
    }

    public void setCenterY(double y) {
        collisionShape.setCenterY(y);
        imageView.setY(y + radius);
    }

    public double getCenterY() {
        return getY() + radius;
    }

    public static PowerUp createPowerUp(PowerUpType type, double x, double y) {
        switch (type) {
//            case EXPAND_PADDLE:
//                return new ExpandPaddlePowerUp(x, y);
//            case SHRINK_PADDLE:
//                return new ShrinkPaddlePowerUp(x, y);
//            case SLOW_BALL:
//                return new SlowBallPowerUp(x, y);
//            case FAST_BALL:
//                return new FastBallPowerUp(x, y);
//            case MULTI_BALL:
//                return new MultiBallPowerUp(x, y);
//            case POINTS_MULTIPLIER:
//                return new PointMultiplierPowerUp(x, y);
//            case LASER:
//                return new LaserPowerUp(x, y);
//            case SHIELD:
//                return new ShieldPowerUp(x, y);
            default:
                return new ShieldPowerUp(x, y);
        }
    }

    public static PowerUp createRandomPowerUp(double x, double y) {
        PowerUpType[] types = PowerUpType.values();
        int randomIndex = (int) (Math.random() * types.length);
        return createPowerUp(types[randomIndex], x, y);
    }

    public static PowerUp createWeightedRandomPowerUp(double x, double y) {
        double random = Math.random();

        if (random < 0.15) { // Common power up
            return new ExtraLifePowerUp(x, y);
        } else if (random < 0.3) {
            return new ExpandPaddlePowerUp(x, y);
        } else if (random < 0.5) {
            return new SlowBallPowerUp(x, y);
        } else if (random < 0.65) {
            return new PointMultiplierPowerUp(x, y);
        } else if (random < 0.75) { // Debuff power up
            return new ShrinkPaddlePowerUp(x, y);
        } else if (random < 0.85) {
            return new SlowBallPowerUp(x, y);
        } else if (random < 0.9) { // Rare (strong) power up
            return new MultiBallPowerUp(x, y);
        } else if (random < 0.95) {
            return new ShieldPowerUp(x, y);
        } else {
            return new LaserPowerUp(x, y);
        }
    }

    public Color getColor() {
        switch (this.type) {
            case EXPAND_PADDLE:
                return Color.GREEN;
            case SHRINK_PADDLE:
                return Color.RED;
            case SLOW_BALL:
                return Color.CYAN;
            case FAST_BALL:
                return Color.ORANGE;
            case MULTI_BALL:
                return Color.YELLOW;
            case POINTS_MULTIPLIER:
                return Color.GOLD;
            case LASER:
                return Color.LIGHTPINK;
            case EXTRA_LIFE:
                return Color.PINK;
            default:
                return Color.WHITE;
        }
    }
}
