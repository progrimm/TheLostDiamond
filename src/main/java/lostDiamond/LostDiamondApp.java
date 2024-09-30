package lostDiamond;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LostDiamondApp extends Application {
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("The Lost Diamond");
        primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("LostDiamond.fxml"))));
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
