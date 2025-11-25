package LLD.chatApp.whatsApp;

import lombok.Getter;

import java.util.Set;

@Getter
class User implements Observer {
    String userId;
    String name;
    String phoneNumber;
    Set<String> contacts;

    User(String userId, String name){
        this.userId = userId;
        this.name = name;
    }

    void addContact(String userId) {
        contacts.add(userId);
    }

    @Override
    public void update(Message msg) {
        System.out.println("[" + name + "] New message from " + msg.getSenderId() + ": " + msg.getContent());
    }
}
