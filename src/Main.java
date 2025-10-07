import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import object.Ball;
import object.Paddle;

public class Main extends Application {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 800;

    private Stage priStage;
    private Scene startMenuScene;
    private Scene gameScene;

    private Paddle paddle;
    private Ball ball;

    private void createStartMenu() {
        Image startMenuImg = new Image("file:assets/images/startScreen.png");
        ImageView startView = new ImageView(startMenuImg);
        startView.setFitWidth(WIDTH);
        startView.setFitHeight(HEIGHT);

        Image buttonImg = new Image("file:assets/images/button.png");
        ImageView startButton = new ImageView(buttonImg);
        startButton.setFitWidth(200);
        startButton.setFitHeight(80);

        startButton.setOnMouseEntered(e -> startButton.setOpacity(0.9));
        startButton.setOnMouseExited(e -> startButton.setOpacity(1.0));
        startButton.setOnMouseClicked(e -> startGame());

        StackPane root = new StackPane();
        root.getChildren().addAll(startView, startButton);

        StackPane.setAlignment(startButton, Pos.BOTTOM_CENTER);
        StackPane.setMargin(startButton, new Insets(0, 0, 50, 0));
        startMenuScene = new Scene(root, WIDTH, HEIGHT);
    }

    private void startGame() {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Pane root = new Pane(canvas);
        gameScene = new Scene(root);

        GameManager game = new GameManager(gc, root);

        gameScene.setOnKeyPressed(e -> game.keyPressed(e));
        gameScene.setOnKeyReleased(e -> game.keyReleased(e));

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                game.update();
                game.render(root);
            }
        };
        timer.start();

        priStage.setScene(gameScene);
    }

    @Override
    public void start(Stage stage) {
        priStage = stage;
        createStartMenu();
        stage.setTitle("Arkanoid");
        stage.getIcons().add(new Image("file:assets/images/image.png"));
        stage.setResizable(false);
        stage.setScene(startMenuScene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
