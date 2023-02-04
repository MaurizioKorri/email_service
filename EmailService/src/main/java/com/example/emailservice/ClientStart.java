package com.example.emailservice;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientStart extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Client");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        LoginHandler loginHandler = new LoginHandler(scene);
        loginHandler.showLoginPage();
        primaryStage.show();


        primaryStage.setOnHiding( event -> Platform.runLater(() -> {
            if(!LoginHandler.sessionID.equals("")) {
                try {
                    CommunicationProtocol.logoutRequest();
                } catch (IOException exception) {
                    System.out.println(exception.getMessage());
                }
            }
            System.exit(0);
        }));
    }

    public static void main(String[] args) {
        launch(args);
    }

}
