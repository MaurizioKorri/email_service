package com.example.emailservice;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ServerStart extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ServerStart.class.getResource("server.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 758, 562);
        stage.setTitle("Server");
        stage.setScene(scene);
        stage.show();
        stage.setOnHiding(event -> Platform.runLater(() -> System.exit(0)));
    }

    public static void main(String[] args) {
        launch();
    }
}