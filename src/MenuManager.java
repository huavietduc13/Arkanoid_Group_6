package wgame;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class MenuManager {
    private Scene scene;
    private Main mainApp;

    public MenuManager(Main mainApp) {
        this.mainApp = mainApp;

        Text title = new Text("BRICK BREAKER");
        title.setFont(Font.font("Arial",58));

        String normalStyle = "-fx-background-color: #222; -fx-text-fill: white; -fx-background-radius: 10;";
        String hoverStyle  = "-fx-background-color: #555; -fx-text-fill: white; -fx-background-radius: 10;";
        String clickStyle  = "-fx-background-color: #777; -fx-text-fill: white; -fx-background-radius: 10;";

        Button startB = new Button("START");
        Button settingB = new Button("SETTING");
        Button exitB = new Button("EXIT");

        //Cho kích thước nút = nhau
        double buttonWidth = 200;
        startB.setPrefWidth(buttonWidth);
        settingB.setPrefWidth(buttonWidth);
        exitB.setPrefWidth(buttonWidth);
        double buttonHeight = 40;
        startB.setPrefHeight(buttonHeight);
        settingB.setPrefHeight(buttonHeight);
        exitB.setPrefHeight(buttonHeight);
        Font buttonFont = Font.font("Arial", 24);
        startB.setFont(buttonFont);
        settingB.setFont(buttonFont);
        exitB.setFont(buttonFont);

        startB.setStyle(normalStyle);
        settingB.setStyle(normalStyle);
        exitB.setStyle(normalStyle);

        //chỉ chuột vào đổi màu
        Button[] buttons = { startB, settingB, exitB };
        for (Button btn : buttons) {
            btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
            btn.setOnMouseExited(e -> btn.setStyle(normalStyle));
            btn.setOnMousePressed(e -> btn.setStyle(clickStyle));
            btn.setOnMouseReleased(e -> btn.setStyle(hoverStyle));
        }

        VBox menu = new VBox(20);
        menu.getChildren().addAll(title,startB,settingB,exitB);
        menu.setAlignment(Pos.CENTER);
        scene = new Scene(menu, 600, 800);

        startB.setOnAction(e -> mainApp.startGame());
        //TODO: làm setting
        exitB.setOnAction(e -> mainApp.exitGame());
    }

    public Scene getScene() {
        return scene;
    }
}
