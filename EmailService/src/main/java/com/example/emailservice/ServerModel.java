package com.example.emailservice;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ServerModel {
    private ObservableList<String> logs = FXCollections.observableArrayList();
    private ObservableList<String> loggedUsers = FXCollections.observableArrayList();
    private Map<String, User> sessions = new HashMap<>();


    public ObservableList<String> getLogs(){
        return logs;
    }

    public ObservableList<String> getLoggedUsers(){
        return loggedUsers;
    }

    public void createSession(String idSession, User user){
        sessions.put(idSession, user);
    }

    public void removeSession(String idSession){
        sessions.remove(idSession);
    }

    public void addUser(String user){
        loggedUsers.add(user);
    }

    public void removeUser(String user){
        loggedUsers.remove(user);
    }

    public User getUserFromSession(String idSession){
        return sessions.getOrDefault(idSession, null);
    }

    public void addLog(String log){
        String when = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
        logs.add("<" + when + ">" + " : " + log);
    }
}
