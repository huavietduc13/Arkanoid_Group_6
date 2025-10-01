import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        // Label
        Label label = new Label("Hello, JavaFX!");

        // Layout
        StackPane root = new StackPane();
        root.getChildren().add(label);

        // Scene
        Scene scene = new Scene(root, 400, 600);

        // Stage
        stage.setTitle("Arkanoid");
        stage.getIcons().add(new Image("file:assets/image.png"));
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}