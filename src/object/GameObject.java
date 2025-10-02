package object;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;


public abstract class GameObject {
    protected double x, y;
    protected double width, height;
    protected Image image;
    protected ImageView imageView;

    public GameObject(String imagePath, double x, double y,double width, double height) {
        image = new Image(imagePath);
        imageView = new ImageView(image);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setX(x);
        imageView.setY(y);
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, width, height);
    }

    public double getX() {
        return imageView.getX();
    }

    public void setX(double x) {
        imageView.setX(x);
    }

    public double getY() {
        return imageView.getY();
    }

    public void setY(double y) {
        imageView.setY(y);
    }

    public double getWidth() {
        return imageView.getFitWidth();
    }

    public double getHeight() {
        return imageView.getFitHeight();
    }

    public Image getImage() {
        return image;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public abstract void update();

    public void render() {

    }
}
