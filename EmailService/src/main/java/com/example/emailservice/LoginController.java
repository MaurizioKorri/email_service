package com.example.emailservice;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class LoginController {

    @FXML
    private TextField addressTextField;

    @FXML
    private Text errorMsg;

    private LoginHandler loginHandler;


    public void startHandler(LoginHandler handler){
        this.loginHandler = handler;
    }

    public void login(){
        Answer loginAnswer = null;
        User user = new User();
        boolean correctSyntax = LoginHandler.checkEmailSyntax(addressTextField.getText());

        if(correctSyntax){
            loginHandler.generateSessionID();
            user.emailAddressProperty().bind(addressTextField.textProperty());
            try{
                loginAnswer = CommunicationProtocol.loginRequest(user);

            }
            catch (IOException | ClassNotFoundException e) {
                loginAnswer = new Answer(-1,"some error happened during Login");
                Logger.getLogger(LoginHandler.class.getName()).log(Level.SEVERE, "Couldn't verify login", e);

            }
            catch (NullPointerException exception) {
                loginAnswer = new Answer(-2, "Server is down");
            }

            if (loginAnswer.getAnswerCode() == 0) {
                loginHandler.showClientPage(addressTextField.getText());
            }
            else CommunicationProtocol.getAlert(loginAnswer.getAnswerText());

        }
        else{
            errorMsg.setText("The input must be a valid E-Mail");
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(e -> errorMsg.setText(null));
            pause.play();
        }
    }




}
