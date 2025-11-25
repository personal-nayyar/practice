package LLD.chatApp.whatsApp;

// Service Interfaces (ISP and DIP)
interface IUserService {
    boolean registerUser(User user);

    User login(String userId);

    boolean addContact(String userId, String friendId);
}
