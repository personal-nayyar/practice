package LLD.chatApp.whatsApp;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

// Main Class with Automated Test (No Console Input)
public class ChatApp {
    private static IUserService userService = new UserServiceImpl();
    private static IMessageService messageService = new MessageServiceImpl();
    private static IChatService chatService = new ChatServiceImpl(messageService);
    private static ChatManager manager = ChatManager.getInstance();
    public static void main(String[] args) {
        System.out.println("Starting Automated Test for Chat App...");
        // Register users
        User alice = new User("alice", "Alice");
        User bob = new User("bob", "Bob");
        User charlie = new User("charlie", "Charlie");
        System.out.println("Registering users...");
        userService.registerUser(alice);
        userService.registerUser(bob);
        userService.registerUser(charlie);
        System.out.println("Users registered: Alice, Bob, Charlie");

        // Add contacts
        userService.addContact("alice", "bob");
        userService.addContact("alice", "charlie");
        System.out.println("Alice added Bob and Charlie as contacts");

        // Create one-on-one chat
        String oneOnOneChatId = chatService.createOneOnOneChat("alice", "bob");
        System.out.println("Created one-on-one chat: " + oneOnOneChatId);

        // Send messages in one-on-one chat
        Message msg1 = new Message(UUID.randomUUID().toString(), "alice", "Hello Bob!", MessageStatus.SENT);
        messageService.sendMessage(oneOnOneChatId, msg1);
        System.out.println("Alice sent: Hello Bob!");
        Message msg2 = new Message(UUID.randomUUID().toString(), "bob", "Hi Alice!", MessageStatus.SENT);
        messageService.sendMessage(oneOnOneChatId, msg2);
        System.out.println("Bob sent: Hi Alice!");
        // View messages
        System.out.println("Messages in " + oneOnOneChatId + ":");
        List<Message> msgs = messageService.getMessages(oneOnOneChatId);
        for (Message m : msgs) {
            System.out.println(m.getSenderId() + ": " + m.getContent());
        }
        // Create group chat
        List<String> members = Arrays.asList("alice", "bob", "charlie");
        String groupChatId = chatService.createGroupChat("alice", members);
        System.out.println("Created group chat: " + groupChatId);
        // Send messages in group
        Message msg3 = new Message(UUID.randomUUID().toString(), "alice", "Welcome to the group!", MessageStatus.SENT);
        messageService.sendMessage(groupChatId, msg3);
        System.out.println("Alice sent to group: Welcome to the group!");

        Message msg4 = new Message(UUID.randomUUID().toString(), "charlie", "Thanks Alice!", MessageStatus.SENT);
        messageService.sendMessage(groupChatId, msg4);
        System.out.println("Charlie sent to group: Thanks Alice!");

        // View group messages
        System.out.println("Messages in " + groupChatId + ":");
        msgs = messageService.getMessages(groupChatId);
        for (Message m : msgs) {
            System.out.println(m.getSenderId() + ": " + m.getContent());
        }
        // Join another user to group (simulate)
        chatService.joinGroup(groupChatId, "charlie"); // Already in, but test
        System.out.println("Charlie joined the group (already a member)");
        manager.getExecutor().shutdown();
        System.out.println("Test completed!");
    }
}
