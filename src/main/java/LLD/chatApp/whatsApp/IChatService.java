package LLD.chatApp.whatsApp;

import java.util.List;

interface IChatService {
    String createOneOnOneChat(String user1, String user2);

    String createGroupChat(String adminId, List<String> members);

    boolean joinGroup(String groupId, String userId);
}
