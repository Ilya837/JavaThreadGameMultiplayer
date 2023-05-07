package com.game.javathreadgamemultiplayer2;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ClientFrame extends Application{


    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientFrame.class.getResource("ClientFrame.fxml"));

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                ClientCont cc = fxmlLoader.getController();
                cc.CloseWindow();
                Platform.exit();
                System.exit(0);
            }
        });

        Scene scene = new Scene(fxmlLoader.load(), 700, 550);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) { launch();  }
}
