package LLD.chatApp.whatsApp;

import java.util.List;

class DirectChat extends Chat {
    DirectChat(String chatId, String user1, String user2) {
        super(chatId, List.of(user1, user2));
    }
}
