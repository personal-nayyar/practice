package LLD.chatApp.whatsApp;

// Service Implementations (SRP)
class UserServiceImpl implements IUserService {
    private ChatManager manager = ChatManager.getInstance();

    @Override
    public boolean registerUser(User user) {
        if (manager.getUser(user.getUserId()) != null) return false;
        manager.addUser(user);
        return true;
    }

    @Override
    public User login(String userId) {
        return manager.getUser(userId);
    }

    @Override
    public boolean addContact(String userId, String friendId) {
        User user = manager.getUser(userId);
        if (user != null && manager.getUser(friendId) != null) {
            user.addContact(friendId);
            return true;
        }
        return false;
    }
}
