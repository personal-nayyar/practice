package LLD.chatApp.whatsApp;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Singleton Manager
class ChatManager {
    private static ChatManager instance;
    private Map<String, User> users = new ConcurrentHashMap<>();
    private Map<String, Chat> chats = new ConcurrentHashMap<>();
    private ExecutorService executor = Executors.newCachedThreadPool();

    private ChatManager() {
    }

    public static synchronized ChatManager getInstance() {
        if (instance == null) instance = new ChatManager();
        return instance;
    }

    public void addUser(User user) {
        users.put(user.getUserId(), user);
    }

    public User getUser(String userId) {
        return users.get(userId);
    }

    public void addChat(Chat chat) {
        chats.put(chat.getChatId(), chat);
    }

    public Chat getChat(String chatId) {
        return chats.get(chatId);
    }

    public ExecutorService getExecutor() {
        return executor;
    }
}
