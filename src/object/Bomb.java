package object;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

public class Bomb {

    private static final String DEFAULT_IMAGE = "file:assets/images/koffing.gif";
    private static final double DEFAULT_WIDTH = 30.0;
    private static final double DEFAULT_HEIGHT = 30.0;

    private double x;
    private double y;
    private double width;
    private double height;

    private double vx;
    private double vy;

    private Image image;
    private ImageView imageView;

    private Rectangle collisionShape;
    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getWidth() {
        return this.width;
    }

    public double getHeight() {
        return this.height;
    }

    public Bomb(double x, double y) {
        this.x = x;
        this.y = y;
        this.width = DEFAULT_WIDTH;
        this.height = DEFAULT_HEIGHT;
        this.vx = 0;
        this.vy = 0;

        this.image = new Image(DEFAULT_IMAGE);
        this.imageView = new ImageView(this.image);

        this.imageView.setX(this.x);
        this.imageView.setY(this.y);
        this.imageView.setFitWidth(this.width);
        this.imageView.setFitHeight(this.height);

        this.collisionShape = new Rectangle(x, y, this.width, this.height);
        this.collisionShape.setVisible(false);
    }

    public void setVelocity(double vx, double vy) {
        this.vx = vx;
        this.vy = vy;
    }

    public void update() {
        this.x += this.vx;
        this.y += this.vy;

        this.imageView.setX(this.x);
        this.imageView.setY(this.y);
        this.collisionShape.setX(this.x);
        this.collisionShape.setY(this.y);
    }

    public Rectangle getCollisionShape() {
        return this.collisionShape;
    }

    public ImageView getImageView() {
        return this.imageView;
    }
}