package LLD.chatApp.whatsApp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class ChatServiceImpl implements IChatService {
    private ChatManager manager = ChatManager.getInstance();
    private IMessageService messageService; // DIP: Injected dependency

    public ChatServiceImpl(IMessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public String createOneOnOneChat(String user1, String user2) {
        String chatId = "chat_" + user1 + "_" + user2;
        List<String> participants = Arrays.asList(user1, user2);
        Chat chat = ChatFactory.createChat("oneOnOne", chatId, participants, null, "group1");
        manager.addChat(chat);
        // Add observers
        for (String p : participants) {
            User u = manager.getUser(p);
            if (u != null) chat.addObserver(u);
        }
        return chatId;
    }

    @Override
    public String createGroupChat(String adminId, List<String> members) {
        String chatId = "group_" + adminId + "_" + System.currentTimeMillis();
        Chat chat = ChatFactory.createChat("group", chatId, new ArrayList<>(members), adminId, "group1");
        manager.addChat(chat);
        // Add observers
        for (String p : members) {
            User u = manager.getUser(p);
            if (u != null) chat.addObserver(u);
        }
        return chatId;
    }

    @Override
    public boolean joinGroup(String groupId, String userId) {
        Chat chat = manager.getChat(groupId);
        if (chat instanceof GroupChat) {
            ((GroupChat) chat).addMember(userId);
            User u = manager.getUser(userId);
            if (u != null) chat.addObserver(u);
            return true;
        }
        return false;
    }
}
