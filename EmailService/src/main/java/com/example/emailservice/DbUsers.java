package com.example.emailservice;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DbUsers {

    private static final String path = "C:\\Users\\korri\\Documents\\GitHub Projects\\email_service\\EmailService\\database";
    private static final String usersFile = "userList.txt";



    public static FileChannel getLockExclusive(String path) throws IOException {
        return FileChannel.open(Paths.get(path), StandardOpenOption.READ, StandardOpenOption.WRITE);
    }

    public static FileChannel getLockShared(String path) throws IOException {
        return FileChannel.open(Paths.get(path), StandardOpenOption.READ);
    }

    private static boolean createUserFolder(String path) throws IOException {
        boolean ret = new File(path).mkdir();

        ret = ret && new File(path + "Inbox").mkdir();
        ret = ret && new File(path + "Inbox" + File.separator + "lock").createNewFile();
        ret = ret && new File(path + "Sent").mkdir();
        ret = ret && new File(path + "Sent" + File.separator + "lock").createNewFile();

        return ret;
    }

    public static Answer sendMail(Mail mail) throws IOException {
        ObjectOutputStream objectOut;
        User sender = mail.getSender();
        List<User> receivers = mail.getReceivers();
        String senderPath = path + File.separator + sender.getEmailAddress() + File.separator;

        if(!userExists(sender)) return new Answer(-1, "Sender doesnt exist");
        if(receivers.isEmpty()) return new Answer(-1, "There must be at least one receiver");

        if(Files.notExists(Paths.get(senderPath))){
            if(!createUserFolder(senderPath)){
                return new Answer(-3, "error in fileSystem");
            }
        }

        //check if receivers folder exists.
        for(User tempUser : receivers){
            String receiverPath = path + File.separator + tempUser.getEmailAddress() + File.separator;

            if(!userExists(tempUser)) return new Answer(-2, "Some receiver doesnt exist");

            if(Files.notExists(Paths.get(receiverPath))){
                if(!createUserFolder(receiverPath)){
                    return new Answer(-3, "Error in fileSystem");
                }
            }
        }

        //locking
        FileChannel fileLock = getLockShared(senderPath + "Sent" + File.separator + "lock");
        fileLock.lock(0, Long.MAX_VALUE, true);

        mail.generateUUID();
        //write in sender@mail.com/sent
        FileOutputStream fileOut = new FileOutputStream(senderPath + "Sent" + File.separator + mail.getId() + ".txt");
        objectOut = new ObjectOutputStream(fileOut);
        mail.setIsSentFlag(true);
        objectOut.writeObject(mail);
        objectOut.close();
        fileLock.close();

        //write in receiver@mail.com/inbox
        mail.setIsSentFlag(false);
        for(User tempUser : receivers){
            String receiverPath = path + File.separator + tempUser.getEmailAddress() + File.separator;
            mail.generateUUID();

            fileLock = getLockShared(receiverPath + "Inbox" + File.separator + "lock");
            fileLock.lock(0, Long.MAX_VALUE, true);

            fileOut = new FileOutputStream(receiverPath + "Inbox" + File.separator + mail.getId() + ".txt");
            objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(mail);
            objectOut.close();

            fileLock.close();
        }
        return new Answer(0, "Mail sent");
    }

    public static List<Mail> readMail(User user, String type) throws IOException, ClassNotFoundException {
        List<String> mails = getExistingMails(user, type);
        List<Mail> mailListReturn = new ArrayList<>();
        Mail temp;
        ObjectInputStream objectOut;

        String userDir = path + File.separator + user.getEmailAddress() + File.separator + type + File.separator;
        FileChannel fileLock = getLockExclusive(userDir + "lock");
        fileLock.lock();

        for (String mail : mails) {

            objectOut = new ObjectInputStream(new FileInputStream(userDir + mail));
            temp = (Mail) objectOut.readObject();
            mailListReturn.add(temp);
            objectOut.close();

        }

        fileLock.close();
        return mailListReturn;
    }

    public static Answer deleteMail(User user, Mail toDelete) throws IOException {

        Answer answer = new Answer(0, "Mail deleted successfully");

        String type = toDelete.getIsSentFlag() ? "Sent" : "Inbox";
        String userPath = path + File.separator + user.getEmailAddress() +File.separator;

        if (Files.notExists(Paths.get(userPath))) {
            if (Files.notExists(Paths.get(userPath)))
                if(!createUserFolder(userPath))
                    return new Answer(-2, "Error in file system");

            return new Answer(-1, "Mail does not exists");
        }

        FileChannel fileLock = getLockShared(userPath + type + File.separator + "lock");
        fileLock.lock(0, Long.MAX_VALUE, true);

        File fileToDelete = new File(userPath + type + File.separator + toDelete.getId() + ".txt");
        if (!fileToDelete.delete()) {
            answer = new Answer(-1, "Error deleting the file, file not existing");
        }

        fileLock.close();

        return answer;

    }

    private static List<String> getExistingMails(User user, String type) throws IOException {//type se voglio inbox o sent
        List<String> textFiles = new ArrayList<>();
        String userDir = path + File.separator + user.getEmailAddress() + File.separator;

        if (Files.notExists(Paths.get(userDir))) {
            createUserFolder(userDir);
            return textFiles;
        }


        FileChannel fileLock = getLockExclusive(userDir + type + File.separator+ "lock");
        fileLock.lock();

        File dir = new File(userDir + type);

        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.getName().endsWith((".txt"))) {
                textFiles.add(file.getName());
            }
        }

        fileLock.close();

        return textFiles;
    }

    public static int getInboxSize(User user) throws IOException {
        return getExistingMails(user, "Inbox").size();
    }

    public static boolean userExists(User user) throws IOException {
        File users = new File(path + File.separator + "userList.txt");
        String line;

        BufferedReader br = new BufferedReader(new FileReader(users));
        while ((line = br.readLine()) != null) {
            System.out.println(line);
            System.out.println(user.getEmailAddress() + "la user adress:");
            if (user.getEmailAddress().equals(line))
                return true;
        }
        return false;
    }


}
