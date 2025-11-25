package LLD.chatApp.whatsApp;

import java.util.List;

class GroupChat extends Chat {
    String name;
    String admin;

    GroupChat(String chatId, String name, String admin, List<String> users) {
        super(chatId, users);
        this.name = name;
        this.admin = admin;
    }

    void addMember(String userId) {
        participants.add(userId);
    }

    void join(User user) {
        addObserver(user);
    }

    void exit(User user) {
        removeObserver(user);
    }
}
