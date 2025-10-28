import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        SceneManager sceneManager = new SceneManager(stage);
        sceneManager.createStartMenu();
        sceneManager.createLevelSelectionScene();
        stage.setTitle("Arkanoid");
        stage.getIcons().add(new Image("file:assets/images/image.png"));
        stage.setResizable(false);
        stage.setScene(sceneManager.getStartMenuScene());
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}