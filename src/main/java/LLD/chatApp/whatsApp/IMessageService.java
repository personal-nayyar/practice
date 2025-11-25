package LLD.chatApp.whatsApp;

import java.util.List;

interface IMessageService {
    boolean sendMessage(String chatId, Message msg);

    List<Message> getMessages(String chatId);
}
