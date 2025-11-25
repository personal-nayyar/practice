package design_pattern.structural;

import java.util.*;

public class RepositoryPattern{}

// Single-class representation of Repository Pattern
class UserRepositoryDemo {

    // --- Entity / Domain Model ---
    static class User {
        private final int id;
        private String name;

        public User(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        @Override
        public String toString() {
            return "User{id=" + id + ", name='" + name + "'}";
        }
    }

    // --- Repository Interface (Abstraction) ---
    interface UserRepository {
        void save(User user);
        User findById(int id);
        List<User> findAll();
        void delete(int id);
    }

    // --- Repository Implementation (In-memory example) ---
    static class InMemoryUserRepository implements UserRepository {
        private final Map<Integer, User> userStore = new HashMap<>();

        @Override
        public void save(User user) {
            userStore.put(user.getId(), user);
        }

        @Override
        public User findById(int id) {
            return userStore.get(id);
        }

        @Override
        public List<User> findAll() {
            return new ArrayList<>(userStore.values());
        }

        @Override
        public void delete(int id) {
            userStore.remove(id);
        }
    }

    // --- Service Layer (Business Logic using Repository) ---
    static class UserService {
        private final UserRepository userRepository;

        public UserService(UserRepository userRepository) {
            this.userRepository = userRepository;
        }

        public void registerUser(int id, String name) {
            userRepository.save(new User(id, name));
            System.out.println("✅ User registered: " + name);
        }

        public void printAllUsers() {
            System.out.println("All users: " + userRepository.findAll());
        }

        public void deleteUser(int id) {
            userRepository.delete(id);
            System.out.println("🗑️ User deleted with ID: " + id);
        }
    }

    // --- Main (Client) ---
    public static void main(String[] args) {
        UserRepository repo = new InMemoryUserRepository(); // data access logic
        UserService service = new UserService(repo);       // business logic

        service.registerUser(1, "Alice");
        service.registerUser(2, "Bob");

        service.printAllUsers();

        service.deleteUser(1);
        service.printAllUsers();
    }
}