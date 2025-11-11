package object;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.Pane;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import object.powerup.PowerUp;
import object.powerup.ExtraLifePowerUp;
import java.util.ArrayList;
import java.util.List;
import static utils.Constants.*;

public class Boss {

    protected double x, y;
    protected double width, height;
    protected Image image;
    protected ImageView imageView;

    private int hp;
    private long lastAttackTime = 0;
    private final long ATTACK_COOLDOWN = 7000;
    private Rectangle collisionShape;

    private static final int BURST_COUNT = 4;
    private static final long BURST_DELAY = 200;
    private int bombsLeftInBurst = 0; // Đếm số bom còn lại trong loạt
    private long lastBurstFireTime = 0;
    private double vx; // Tốc độ ngang
    private double vy; // Tốc độ dọc (MỚI)

    private static final String DEFAULT_IMAGE = "file:assets/images/haunter.gif";
    private static final double DEFAULT_X = 100.0;
    private static final double DEFAULT_Y = 100.0;
    private static final double DEFAULT_WIDTH = 180.0;
    private static final double DEFAULT_HEIGHT = 180.0;
    private static final int DEFAULT_HEALTH = 50;

    private static final double DEFAULT_MOVE_SPEED_X = 0.7;
    private static final double DEFAULT_MOVE_SPEED_Y = 0.5;

    private static final double BOMB_SPEED = 2.5;

    private static final long SKILL_COOLDOWN = 3000;

    private static final int HEAL_AMOUNT = 10;
    private long lastSkillTime = 0;
    private boolean isActive = false;
    public Boss() {
        this(DEFAULT_IMAGE, DEFAULT_X, DEFAULT_Y, DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_HEALTH);
    }

    public Boss(String imagePath, double x, double y, double width, double height, int initialHealth) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.image = new Image(imagePath);
        this.imageView = new ImageView(image);
        this.imageView.setFitWidth(width);
        this.imageView.setFitHeight(height);
        this.imageView.setX(x);
        this.imageView.setY(y);

        this.hp = initialHealth;
        this.lastAttackTime = System.currentTimeMillis();

        this.collisionShape = new Rectangle(x, y, width, height);
        this.collisionShape.setVisible(false);

        this.vx = DEFAULT_MOVE_SPEED_X;
        this.vy = DEFAULT_MOVE_SPEED_Y;
    }

    private void move() {
        double newX = getX() + vx;
        double newY = getY() + vy;

        if (newX < 0 || newX + getWidth() > SCREEN_WIDTH) {
            vx *= -1;
            newX = getX() + vx;
        }

        if (vx < 0) {
            imageView.setScaleX(1);
        } else {
            imageView.setScaleX(-1);
        }

        double topBoundary = 0;

        double bottomBoundary = 300.0;

        if (newY < topBoundary || newY + getHeight() > bottomBoundary) {
            vy *= -1;
            newY = getY() + vy;
        }

        setX(newX);
        setY(newY);
    }

    public void update() {
        if (!this.isActive) {
            return;
        }

        move();
    }

    public void activate() {
        this.isActive = true;
        this.lastAttackTime = System.currentTimeMillis();
        this.bombsLeftInBurst = 0;
        this.lastSkillTime = System.currentTimeMillis();
    }

    public boolean isActive() {
        return this.isActive;
    }

    public void takeHit() {
        if (hp > 0) {
            hp--;
        }
    }

    public boolean isDestroyed() {
        return hp <= 0;
    }

    public int getHitPoints() {
        return hp;
    }

    public int getMaxHitPoints() {
        return DEFAULT_HEALTH;
    }

    public ImageView getImageView() {
        return this.imageView;
    }

    public Rectangle getCollisionShape() {
        return this.collisionShape;
    }

    public double getX() {
        return imageView.getX();
    }

    public void setX(double x) {
        this.x = x;
        imageView.setX(x);
        if (collisionShape != null) {
            collisionShape.setX(x);
        }
    }

    public double getY() {
        return imageView.getY();
    }

    public void setY(double y) {
        this.y = y;
        imageView.setY(y);
        if (collisionShape != null) {
            collisionShape.setY(y);
        }
    }

    public double getWidth() {
        return imageView.getFitWidth();
    }

    public double getHeight() {
        return imageView.getFitHeight();
    }

    private Bomb createOneBomb(double targetX, double targetY) {
        double startX = this.x + (this.width / 2);
        double startY = this.y + (this.height / 2);
        Bomb newBomb = new Bomb(startX, startY);

        double dx = targetX - startX;
        double dy = targetY - startY;
        double length = Math.sqrt(dx * dx + dy * dy);
        double normalizedDx = dx / length;
        double normalizedDy = dy / length;

        newBomb.setVelocity(normalizedDx * BOMB_SPEED, normalizedDy * BOMB_SPEED);
        return newBomb;
    }

    public Bomb updateAndAttack(double targetX, double targetY) {
        if (!this.isActive) {
            return null;
        }

        move();

        long currentTime = System.currentTimeMillis();

        if (this.bombsLeftInBurst > 0) {
            if (currentTime - this.lastBurstFireTime > BURST_DELAY) {
                this.lastBurstFireTime = currentTime;
                this.bombsLeftInBurst--;

                return createOneBomb(targetX, targetY);
            } else {
                return null;
            }
        }

        if (currentTime - this.lastAttackTime > ATTACK_COOLDOWN) {
            this.lastAttackTime = currentTime;
            this.bombsLeftInBurst = BURST_COUNT;

            this.lastBurstFireTime = currentTime;
            this.bombsLeftInBurst--;
            return createOneBomb(targetX, targetY);
        }

        return null;
    }

    public boolean canUseSkill() {
        if (!this.isActive) {
            return false;
        }

        long timeElapsed = System.currentTimeMillis() - this.lastSkillTime;
        return timeElapsed > SKILL_COOLDOWN;
    }

    public List<PowerUp> useSkill() {
        this.lastSkillTime = System.currentTimeMillis();

        this.hp = Math.min(this.hp + HEAL_AMOUNT, DEFAULT_HEALTH);
        System.out.println("Boss used skill! Healing 10 HP and dropping Extra Lives.");

        List<PowerUp> droppedItems = new ArrayList<>();

        double dropY = this.getY() + this.getHeight() + 10;
        double center = this.getX() + (this.getWidth() / 2);
        double offset = 50.0;

        PowerUp pu1 = new ExtraLifePowerUp(center - offset, dropY);

        PowerUp pu2 = new ExtraLifePowerUp(center + offset, dropY);

        droppedItems.add(pu1);
        droppedItems.add(pu2);

        return droppedItems;
    }

    public void playExplosionAnimation(Pane root, double x, double y, double durationInSeconds) {

        Image explosionImage = new Image("file:assets/images/duccop.jpg");
        ImageView explosionView = new ImageView(explosionImage);

        double explosionSize = 80.0;
        explosionView.setFitWidth(explosionSize);
        explosionView.setFitHeight(explosionSize);

        explosionView.setX(x - explosionSize / 2);
        explosionView.setY(y - explosionSize / 2);

        // 'root' bây giờ được lấy từ tham số
        root.getChildren().add(explosionView);

        PauseTransition removeAfterDelay = new PauseTransition(Duration.seconds(durationInSeconds));
        removeAfterDelay.setOnFinished(event -> {
            root.getChildren().remove(explosionView);
        });

        removeAfterDelay.play();
    }
}