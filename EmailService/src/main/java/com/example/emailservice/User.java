package com.example.emailservice;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.LinkedList;

public class User implements Externalizable {
    private static final long serialVersionUID = -3579562875503665712L;
    private ListProperty<Mail> inbox;
    public ObservableList<Mail> inboxContent;
    private transient StringProperty emailAddress;
    public ObservableList<Mail> sentContent;

    public User(String emailAddress) {
        this.emailAddress = new SimpleStringProperty(emailAddress);
        this.inbox = new SimpleListProperty<>();
        this.inboxContent = FXCollections.observableList(new LinkedList<>());
        this.sentContent = FXCollections.observableList(new LinkedList<>());

    }

    public User(){
        init();
    }
    private void init(){
        this.emailAddress = new SimpleStringProperty();
        this.inbox = new SimpleListProperty<>();
        this.inboxContent = FXCollections.observableList(new LinkedList<>());
        this.sentContent = FXCollections.observableList(new LinkedList<>());
    }


    public ObservableList<Mail> getInbox() {
        return inboxProperty().get();
    }

    public ListProperty<Mail> inboxProperty() {
        return inbox;
    }

    public void setInbox(ObservableList<Mail> inbox) {
        this.inboxProperty().set(inbox);
    }

    public String getEmailAddress() {
        return this.emailAddressProperty().get();
    }

    public StringProperty emailAddressProperty() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddressProperty().set(emailAddress);
    }

    public void removeEmail(Mail email, boolean b){
        inboxProperty().remove(email);
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(this.getEmailAddress());
    }

    public void readExternal(ObjectInput in) throws IOException {
        init();
        this.setEmailAddress(in.readUTF());
    }

    @Override
    public boolean equals(Object o){
        User user = (User) o;
        return user.getEmailAddress().equals(this.getEmailAddress());
    }

    public String toString(){
        return this.getEmailAddress();
    }



}
