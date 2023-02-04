package com.example.emailservice;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

public class CommunicationProtocol {

    private static final int port = 1234;
    private static boolean online = false;


    public static boolean isOnline(){
        return online;
    }

    public static void setOnline(boolean online){
        CommunicationProtocol.online = online;
    }

    public static Socket getSocket(){
        try{
            String hostName = InetAddress.getLocalHost().getHostName();
            setOnline(true);
            return new Socket(hostName, port);
        } catch (IOException e) {
            setOnline(false);
            return null;
        }
    }

    public static Answer loginRequest(User user) throws IOException, ClassNotFoundException {
        Socket serverConn = getSocket();
        ObjectOutputStream outputStream = new ObjectOutputStream(serverConn.getOutputStream());
        ObjectInputStream inputStream = new ObjectInputStream(serverConn.getInputStream());

        Answer loginAnswer;

        outputStream.writeUTF("login");
        outputStream.flush();

        outputStream.writeUTF(LoginHandler.sessionID);
        outputStream.flush();

        outputStream.writeObject(user);
        outputStream.flush();

        loginAnswer = (Answer) inputStream.readObject();


        outputStream.close();
        inputStream.close();
        serverConn.close();

        return loginAnswer;
    }

    public static void logoutRequest() throws IOException {
        Socket serverSocket = isOnline() ? getSocket() : null;

        if(serverSocket != null){
            ObjectOutputStream outputStream = new ObjectOutputStream(serverSocket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(serverSocket.getInputStream());
            outputStream.writeUTF("logout");
            outputStream.flush();

            outputStream.writeUTF(LoginHandler.sessionID);
            outputStream.flush();

            outputStream.close();
            inputStream.close();
            serverSocket.close();
        }
    }

    public static List<Mail> loadInbox() throws IOException, ClassNotFoundException {
        List<Mail> mailList = null;
        Socket socket = isOnline() ? CommunicationProtocol.getSocket() : null;
        if (socket != null) {

            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

            outputStream.writeUTF("inboxLoad");
            outputStream.flush();

            outputStream.writeUTF(LoginHandler.sessionID);
            outputStream.flush();

            mailList = (List<Mail>) inputStream.readObject();

            inputStream.close();
            outputStream.close();
            socket.close();
        }
        return mailList;
    }

    public static List<Mail> loadSent() throws IOException, ClassNotFoundException {
        List<Mail> mailList = null;
        Socket socket = isOnline() ? CommunicationProtocol.getSocket() : null;
        if (socket != null) {
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

            outputStream.writeUTF("sentLoad");
            outputStream.flush();

            outputStream.writeUTF(LoginHandler.sessionID);
            outputStream.flush();

            mailList = (List<Mail>) inputStream.readObject();

            inputStream.close();
            outputStream.close();
            socket.close();
        }
        return mailList;
    }

    public static int checkUpdates(int clientNumber) throws Exception {

        int val = -1;
        Socket socket = isOnline() ? CommunicationProtocol.getSocket() : null;
        if (socket != null) {

            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

            outputStream.writeUTF("update");
            outputStream.flush();

            outputStream.writeUTF(LoginHandler.sessionID);
            outputStream.flush();

            outputStream.writeInt(clientNumber);//numero di mail viste dal client
            outputStream.flush();

            val = inputStream.readInt();

            inputStream.close();
            outputStream.close();
            socket.close();
        }
        return val;

    }

    public static Answer deleteMail(Mail mailToDelete) throws Exception {

        Answer retAnswer = new Answer(-1, "Server currently offline");

        Socket socket = isOnline() ? CommunicationProtocol.getSocket() : null;
        if (socket != null) {

            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

            outputStream.writeUTF("delete");
            outputStream.flush();

            outputStream.writeUTF(LoginHandler.sessionID);
            outputStream.flush();

            outputStream.writeObject(mailToDelete);
            outputStream.flush();

            retAnswer = (Answer) inputStream.readObject();

            inputStream.close();
            outputStream.close();
            socket.close();
        }
        return retAnswer;


    }

    public static Answer sendMail(Mail mailToSend) throws Exception {

        Answer serverAnswer = new Answer(-1, "Server currently offline");

        Socket serverConn = isOnline() ? CommunicationProtocol.getSocket() : null;
        if (serverConn != null) {
            ObjectOutputStream sendMsg;
            ObjectInputStream receiveState;

            sendMsg = new ObjectOutputStream(serverConn.getOutputStream());

            receiveState = new ObjectInputStream(serverConn.getInputStream());

            sendMsg.writeUTF("sendMail");
            sendMsg.flush();

            sendMsg.writeUTF(LoginHandler.sessionID);
            sendMsg.flush();

            sendMsg.writeObject(mailToSend);
            sendMsg.flush();


            serverAnswer = (Answer) receiveState.readObject();
        }

        return serverAnswer;
    }


    public static void getAlert(String message){
        ButtonType confirm = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        Alert a = new Alert(Alert.AlertType.NONE, message, confirm);
        a.setTitle("Alert");
        a.setResizable(true);
        a.show();
    }
}
