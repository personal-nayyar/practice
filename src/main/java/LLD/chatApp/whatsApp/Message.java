package LLD.chatApp.whatsApp;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
class Message {
    String id;
    String senderId;
//    String chatId;
    String content;
    MessageStatus status;
}
