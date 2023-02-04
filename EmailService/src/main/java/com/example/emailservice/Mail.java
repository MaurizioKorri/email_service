package com.example.emailservice;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mail implements Externalizable {

    private static long serialVersionUID = -34579022623L;
    private transient LongProperty id = new SimpleLongProperty();
    private StringProperty text = new SimpleStringProperty();
    private transient StringProperty subject = new SimpleStringProperty();
    private transient ObjectProperty sender = new SimpleObjectProperty();
    private transient ListProperty<User> receivers = new SimpleListProperty<>();
    private transient ObjectProperty dateTime = new SimpleObjectProperty();
    private transient BooleanProperty isSentFlag = new SimpleBooleanProperty(false);


    public Mail(Long id, User sender, List<User> receivers, String subject, String text, Date dateTime){
        setId(id);
        setReceivers(receivers);
        setSender(sender);
        setSubject(subject);
        setText(text);
        setDateTime(dateTime);
        setIsSentFlag(false);
    }



    public long getId() {
        return idProperty().get();
    }

    public LongProperty idProperty() {
        return id;
    }

    public void setId(long id) {
        this.idProperty().set(id);
    }

    public String getText() {
        return textProperty().get();
    }

    public StringProperty textProperty() {
        return text;
    }

    public void setText(String text) {
        this.textProperty().set(text);
    }

    public String getSubject() {
        return subjectProperty().get();
    }

    public StringProperty subjectProperty() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subjectProperty().set(subject);
    }

    public User getSender() {
        return (User) senderProperty().get();
    }

    public ObjectProperty senderProperty() {
        return sender;
    }

    public void setSender(User sender) {
        this.senderProperty().set(sender);
    }

    public List<User> getReceivers() {
        return receiversProperty();
    }

    public ListProperty<User> receiversProperty() {
        return receivers;
    }

    public void setReceivers(List<User> receivers) {
        this.receiversProperty().set(FXCollections.observableArrayList(receivers));
    }

    public Object getDateTime() {
        return dateTimeProperty().get();
    }

    public ObjectProperty dateTimeProperty() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTimeProperty().set(dateTime);
    }

    public boolean getIsSentFlag() {
        return isSentFlagProperty().get();
    }

    public BooleanProperty isSentFlagProperty() {
        return isSentFlag;
    }

    public void setIsSentFlag(boolean sentFlag) {
        this.isSentFlagProperty().set(sentFlag);
    }





    @Override
    public String toString() {
        return String.join(" - ", List.of(this.getSender().toString(), this.getSubject()));
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(getId());
        out.writeObject(getSender());
        List<User> receiver = new ArrayList<>(getReceivers());
        out.writeObject(receiver);
        out.writeUTF(getSubject()); //message to send
        out.writeUTF(getText());
        out.writeBoolean(getIsSentFlag());
        out.writeObject(getDateTime());

    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        setId(in.readLong());
        setSender((User) in.readObject());
        List<User> toSet = (List<User>) in.readObject();
        setReceivers(toSet);
        setSubject(in.readUTF());
        setText(in.readUTF());
        setIsSentFlag(in.readBoolean());
        setDateTime((Date)in.readObject());
    }

    public void generateUUID() {

        UUID identification = UUID.randomUUID();
        long id = identification.getMostSignificantBits() & Long.MAX_VALUE;
        this.id.set(id);
    }

    public Mail(){};

    public String receiversTostring(){
        String s = "";
        List<User>users = getReceivers();
        for (User user : users){
            s = s + user.toString()+ " ";
        }
        return s;
    }


}
