package com.example.emailservice;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ServerController implements Initializable {

    @FXML
    private ListView<String> logs;
    @FXML
    private ListView<String> adrList;
    private ServerModel serverModel;
    private Executor executor;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.serverModel = new ServerModel();
        logs.setItems(serverModel.getLogs());
        adrList.setItems(serverModel.getLoggedUsers());

        Thread serverThread = new Thread(() -> {
            try {
                executor = Executors.newFixedThreadPool(8);
                ServerSocket s = new ServerSocket(1234);
                serverModel.addLog("Started the Server");

                while (true) {
                    Socket incoming = s.accept();
                    executor.execute(new Server(incoming, serverModel));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        );

        serverThread.setDaemon(true);
        serverThread.start();
    }

    public void terminateServer(){
                Platform.exit();
                System.exit(0);
    }
}
