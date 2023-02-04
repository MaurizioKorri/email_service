package com.example.emailservice;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LoginHandler {

    private final Scene scene;
    private final String sessionDictionary = "ABCDEFGHILMNOPQRSTUVZXWKJ1234567890";
    public static String sessionID = "";


    public LoginHandler(Scene scene) {
        this.scene = scene;
    }


    public void generateSessionID() {
        Random rand = new Random();
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < 64; i++) {
            int randIndex = rand.nextInt(sessionDictionary.length());
            res.append(sessionDictionary.charAt(randIndex));
        }

        this.sessionID = res.toString();
    }


    public void logOut() throws IOException {
        this.sessionID = "";
        this.showLoginPage();
    }

    public void showLoginPage() throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            scene.setRoot(loader.load());
            scene.getWindow().setHeight(606);
            scene.getWindow().setWidth(806);

            LoginController controller = loader.getController();
            controller.startHandler(this);
        } catch (IOException e) {Logger.getLogger(LoginHandler.class.getName()).log(Level.SEVERE, "Login transition failed", e);}
    }


    public void showClientPage(String user){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("client.fxml"));
            scene.setRoot(loader.load());
            scene.getWindow().setHeight(706);
            scene.getWindow().setWidth(806);



            ClientController controller = loader.getController();
            controller.setUser(user);
            controller.setLoginHandler(this);

        } catch(IOException e){Logger.getLogger(LoginHandler.class.getName()).log(Level.SEVERE, "Failed loading client page", e);}

    }


    public static boolean checkEmailSyntax(String username) {
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher mat = pattern.matcher(username);
        if(mat.matches()) return true;
        else{
            System.out.println("Syntax error in the given E-mail");
            return false;
        }

    }
}


