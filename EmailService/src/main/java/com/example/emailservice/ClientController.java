package com.example.emailservice;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.util.*;

public class ClientController {

    private User userModel;
    private LoginHandler loginHandler;
    private Mail selectedMail;
    Timer t1;
    int newMails;


    @FXML
    private Label usernameLbl;


    @FXML
    private Button sentButton;
    @FXML
    private Button inboxButton;


    @FXML
    private TextField textFrom;
    @FXML
    private TextField textTo;
    @FXML
    private TextField emailSubjectText;
    @FXML
    private TextArea emailContentText;


    @FXML
    private Label timeLbl;
    @FXML
    private Button updateButton;



    @FXML
    private Button replyButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button replyAllButton;
    @FXML
    private Button forwardButton;
    @FXML
    private Button sendButton;


    @FXML
    private ListView<Mail> emailList;



    @FXML
    public void initialize(){
        if(this.userModel!=null) throw new IllegalStateException("Model can only be initialized once");
        userModel = new User();

        selectedMail = null;

        emailList.itemsProperty().bind(userModel.inboxProperty());
        onInboxButtonClick();
        emailList.setOnMouseClicked(this::showSelectedEmail);
        usernameLbl.textProperty().bind(userModel.emailAddressProperty());

        startTimer();

        initializeSelectedAreasAsEmpty();

        setButtons(true);
        updateButton.setDisable(true);


        textFrom.setEditable(false);
        setEditableText(false);

        inboxButton.setDisable(true);
        sendButton.setDisable(true);

        try{
            List<Mail> inbox = CommunicationProtocol.loadInbox();
            userModel.inboxContent = FXCollections.observableList(inbox);


            List<Mail> sent = CommunicationProtocol.loadSent();
            userModel.sentContent = FXCollections.observableList(sent);

            userModel.setInbox(userModel.inboxContent);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    protected void onDeleteButtonClick(){
        Answer deleted;
        try {

            deleted = CommunicationProtocol.deleteMail(selectedMail);
            CommunicationProtocol.getAlert(deleted.getAnswerText());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        userModel.removeEmail(selectedMail,inboxButton.isDisabled());
        initializeSelectedAreasAsEmpty();
    }

    @FXML
    protected void onSendButtonClick(){
        List<User> receiver = ClientController.identifyReceivers(textTo.getText());

        if (receiver.size() < 1) {
            String issuesReceivers = "Syntax of receiver/receivers is not correct";
            CommunicationProtocol.getAlert(issuesReceivers);
            return;
        }

        Mail toSend = new Mail(-1L, userModel, receiver, emailSubjectText.getText(), emailContentText.getText(), new Date());
        Answer serverAnswer;

        try {
            serverAnswer = CommunicationProtocol.sendMail(toSend);
        }
        catch (NullPointerException ex){
            serverAnswer = new Answer(-2, "Server is offline");
        }
        catch (Exception e) {
            serverAnswer = new Answer(-2, "Error while sending a mail");
            System.out.println(e.getMessage());
        }

        CommunicationProtocol.getAlert(serverAnswer.getAnswerText());
        sendButton.setDisable(true);
        userModel.sentContent.add(toSend);
    }

    public static List<User> identifyReceivers(String receivers) {

        String[] addresses = receivers.split(";\\s*");
        boolean checkSyntax = true;

        for(String s : addresses){
            if(!LoginHandler.checkEmailSyntax(s)){
                checkSyntax = false;
            }
        }
        if(!checkSyntax) return new ArrayList<>();

        System.out.println(Arrays.toString(addresses));
        User u;
        List<User> receivingUsers = new ArrayList<>();

        for (int i = 0; i < addresses.length; i++) {
            String addr = addresses[i];

            u = new User(addr);

            receivingUsers.add(u);
        }


        return receivingUsers;

    }

    @FXML
    void onNewMailButtonClick() {
        System.out.println("writing new message");
        textFrom.setText(userModel.getEmailAddress());
        textTo.setText("");
        emailSubjectText.setText("");
        emailContentText.setText("");
        setEditableText(true);
        sendButton.setDisable(false);
    }

    private void setEditableText(boolean b){
        textTo.setEditable(b);
        emailSubjectText.setEditable(b);
        emailContentText.setEditable(b);
    }

    private void initializeSelectedAreasAsEmpty(){
        textFrom.setText("");
        textTo.setText("");
        emailSubjectText.setText("");
        emailContentText.setText("");
        timeLbl.setText("");
    }


    private void startTimer() {
        t1 = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (CommunicationProtocol.isOnline()) {
                    try {
                        newMails = CommunicationProtocol.checkUpdates(userModel.inboxContent.size());
                        if (newMails!= 0) {
                            updateButton.setDisable(false);
                        }
                    } catch (Exception e) {
                        CommunicationProtocol.setOnline(false);
                    }
                } else {
                    try {
                        Answer response = CommunicationProtocol.loginRequest(userModel);

                        if (response.getAnswerCode() == 0)
                            CommunicationProtocol.setOnline(true);
                    } catch (Exception ignored) { }
                }
            }
        };
        t1.schedule(task, 5000, 5000);

    }

    @FXML
    private void onInboxButtonClick(){
        sentButton.setDisable(false);
        inboxButton.setDisable(true);
        userModel.setInbox(userModel.inboxContent);
    }

    @FXML
    void onSentButtonClick() {
        sentButton.setDisable(true);
        inboxButton.setDisable(false);
        userModel.setInbox(userModel.sentContent);

    }

    @FXML
    public void onLogoutButtonClick() throws IOException {
        try {
            CommunicationProtocol.logoutRequest();
            t1.cancel();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        loginHandler.logOut();
    }

    @FXML
    public void onUpdateButtonClick() {
        Answer response;
        if(newMails>0) {
            response = new Answer(0, "there are new mails in the inbox");
            CommunicationProtocol.getAlert(response.getAnswerText());
            try {
                List<Mail> inbox = CommunicationProtocol.loadInbox();
                userModel.inboxContent = FXCollections.observableList(inbox);
                onInboxButtonClick();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else{
            response = new Answer(-2, "Server Offline");
            CommunicationProtocol.getAlert(response.getAnswerText());
        }
        updateButton.setDisable(true);
    }

    @FXML
    protected void onReplyButtonClick() {
        replyManager(false, false);
    }

    @FXML
    protected void onReplyAllButtonClick() {
        replyManager(true, false);
    }

    @FXML
    protected void onForwardButtonClick() {
        replyManager(false, true);
    }


    private void replyManager(boolean isAll, boolean isForward){
        textTo.setEditable(true);
        emailSubjectText.setEditable(true);
        emailContentText.setEditable(true);
        if (isForward)
            textTo.setText("");
        else if (isAll)
            textTo.setText(textFrom.getText() + "; " + textTo.getText());
        else
        if(inboxButton.isDisabled()) {
            textTo.setText(textFrom.getText());
        }
        textFrom.setText(usernameLbl.getText());
        if (isForward)
            emailSubjectText.setText("Fw: " + emailSubjectText.getText());
        else
            emailSubjectText.setText("Re: " + emailSubjectText.getText());

        emailContentText.setText("\n\n" + "------------------------\n" + emailContentText.getText());

        setButtons(true);
        sendButton.setDisable(false);
    }

    private void showSelectedEmail(MouseEvent event){
        Mail email = emailList.getSelectionModel().getSelectedItem();
        selectedMail = email;

        if (email != null) {
            textFrom.setText(email.getSender().toString());

            List<User> receivers = email.getReceivers();
            List<String> listAddress = new ArrayList<>();
            for (User user : receivers) {
                listAddress.add(user.toString());
            }
            timeLbl.setText((email.getDateTime().toString()));
            textTo.setText(String.join(";", listAddress));
            emailSubjectText.setText(email.getSubject());
            emailContentText.setText(email.getText());
        }

        setButtons(false);

    }

    private void setButtons(boolean isDisabled) {
        deleteButton.setDisable(isDisabled);
        replyButton.setDisable(isDisabled);
        replyAllButton.setDisable(isDisabled);
        forwardButton.setDisable(isDisabled);
    }

    public void setUser(String user) {
        this.userModel.setEmailAddress(user);
        usernameLbl.textProperty().bind(userModel.emailAddressProperty());
    }

    public void setLoginHandler(LoginHandler loginHandler) {
        this.loginHandler = loginHandler;
    }
}
