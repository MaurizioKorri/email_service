package com.example.emailservice;

import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class Server implements Runnable{

    private Socket incomingSocket;
    private ServerModel serverModel;

    public Server(Socket socket, ServerModel model){
        this.incomingSocket = socket;
        this.serverModel = model;
    }

    public void run(){
        ObjectInputStream inStream = null;
        ObjectOutputStream outStream = null;
        try{
            outStream = new ObjectOutputStream(incomingSocket.getOutputStream());
            outStream.flush();

            inStream = new ObjectInputStream(incomingSocket.getInputStream());//from client

            String action = inStream.readUTF();
            System.out.println("Action: " + action + " request");
            String idSession = inStream.readUTF();
            Answer answer;


            switch(action){

                case "login": {
                    User user = (User) inStream.readObject();
                    System.out.println(user.getEmailAddress() + "nel server");

                    Platform.runLater(() -> serverModel.addLog("Login request from: " + user.toString()));

                    if(DbUsers.userExists(user)){
                        answer = new Answer(0, "User " + user.toString() + "logged in");

                        Platform.runLater(()->{
                            serverModel.createSession(idSession, user);
                            serverModel.addUser(user.toString());
                        });
                    }
                    else{
                        answer = new Answer(-1, "User not existing");
                    }
                    outStream.writeObject(answer);
                    outStream.flush();

                    Answer finalAnswer = answer;

                    Platform.runLater(()->serverModel.addLog(finalAnswer.getAnswerText()));

                    break;
                }

                case "sendMail": {
                    User user = serverModel.getUserFromSession(idSession);
                    if (user == null) return;
                    Mail getMail = (Mail) inStream.readObject();
                    Platform.runLater(() -> serverModel.addLog(user.getEmailAddress() + " trying to send a mail to: " + getMail.receiversTostring()));
                    answer = DbUsers.sendMail(getMail);

                    outStream.writeObject(answer);
                    outStream.flush();

                    Answer finalAnswer = answer;
                    Platform.runLater(() -> serverModel.addLog(finalAnswer.getAnswerText()));

                    break;
                }

                case "inboxLoad": {
                    User user = serverModel.getUserFromSession(idSession);
                    if(user==null) return;

                    Platform.runLater(() -> serverModel.addLog("Loading inbox for: " + user.getEmailAddress()));

                    List<Mail> inboxMail = DbUsers.readMail(user, "Inbox");
                    outStream.writeObject(inboxMail);
                    outStream.flush();

                    break;
                }

                case "sentLoad" : {
                    User user = serverModel.getUserFromSession(idSession);
                    if (user == null) return;

                    List<Mail> outboxMail = DbUsers.readMail(user, "Sent");

                    Platform.runLater(() -> serverModel.addLog("Loading sent of " + user.getEmailAddress()));
                    outStream.writeObject(outboxMail);
                    outStream.flush();

                    break;
                }

                case "update" : {
                    User user = serverModel.getUserFromSession(idSession);
                    if(user==null) return;

                    int clientCount = inStream.readInt();
                    int inboxCount = DbUsers.getInboxSize(user);
                    int diffCount = inboxCount - clientCount;

                    outStream.writeInt(diffCount);
                    outStream.flush();

                    break;
                }

                case "delete" : {
                    User user = serverModel.getUserFromSession(idSession);
                    if (user == null) return;


                    Mail deleteMail = (Mail) inStream.readObject();

                    try {
                        answer = DbUsers.deleteMail(user, deleteMail);
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                        answer = new Answer(-2, "Error deleting the mail");
                    }

                    outStream.writeObject(answer);
                    outStream.flush();

                    Answer finalAnswer = answer;
                    Platform.runLater(() -> serverModel.addLog(finalAnswer.getAnswerText()));

                    break;
                }

                case "logout" : {
                    User user = serverModel.getUserFromSession(idSession);
                    if (user == null) return;


                    Platform.runLater(() -> {
                                serverModel.addLog(user.getEmailAddress() + " disconnected from server");
                                serverModel.removeUser(user.getEmailAddress());
                                serverModel.removeSession(idSession);
                            }
                    );

                    break;
                }

                default : {
                    System.out.println("Action not found");
                    break;
                }
            }

        }catch(IOException | ClassNotFoundException e){e.printStackTrace();}

        finally{
            if(incomingSocket!=null){
                try{
                    if(inStream!=null) inStream.close();
                    if(outStream!=null) outStream.close();
                    incomingSocket.close();
                } catch(IOException e){e.printStackTrace();}
            }
        }
    }
}
