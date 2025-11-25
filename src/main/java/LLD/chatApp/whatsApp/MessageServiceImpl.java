package LLD.chatApp.whatsApp;

import java.util.ArrayList;
import java.util.List;

class MessageServiceImpl implements IMessageService {
    private ChatManager manager = ChatManager.getInstance();

    @Override
    public boolean sendMessage(String chatId, Message msg) {
        Chat chat = manager.getChat(chatId);
        if (chat != null && chat.getParticipants().contains(msg.getSenderId())) {
            chat.addMessage(msg);
            return true;
        }
        return false;
    }

    @Override
    public List<Message> getMessages(String chatId) {
        Chat chat = manager.getChat(chatId);
        return chat != null ? chat.getMessages() : new ArrayList<>();
    }
}
