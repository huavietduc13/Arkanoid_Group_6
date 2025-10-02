import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class startMenu extends Application {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    @Override
    public void start(Stage stage) {
        Image startMenuImg = new Image("assets/startScreen.png");
        ImageView startView = new ImageView(startMenuImg);
        startView.setFitWidth(WIDTH);
        startView.setFitHeight(HEIGHT);

        StackPane root = new StackPane(startView);
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
