package LLD.chatApp.whatsApp;

import java.util.List;

// Factory Pattern
class ChatFactory {
    public static Chat createChat(String type, String chatId, List<String> participants, String adminId, String groupName) {
        if ("direct".equals(type)) return new DirectChat(chatId, participants.get(0), participants.get(1));
        if ("group".equals(type)) return new GroupChat(chatId, groupName, adminId, participants);
        return null;
    }
}
