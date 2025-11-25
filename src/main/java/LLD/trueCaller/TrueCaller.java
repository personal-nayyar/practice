package LLD.trueCaller;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Data
class User{
    String name;
    String phoneNumber;
}

// Repository pattern
interface IRepositoryStorage{
    void saveUser(User user);
    User findByName(String phoneNumber);
}

// Strategy pattern
interface IValidator{
    boolean validatePhoneNumber(String phoneNumber);
    boolean validateName(String name);
}

// Strategy pattern + Factory pattern
interface ISearchStrategy{
    User searchUser(String phoneNumber, IRepositoryStorage repositoryStorage);
}

class InMemoryStorage implements IRepositoryStorage{
    Map<String, User> storage;
    public InMemoryStorage(){
        storage = new HashMap<>();
    }

    @Override
    public void saveUser(User user){
        String normalisePhone = normalizePhone(user.getPhoneNumber());
        storage.put(normalisePhone, user);
    }

    @Override
    public User findByName(String phoneNumber){
        String normalized = normalizePhone(phoneNumber);
        return storage.get(phoneNumber);
    }

    private String normalizePhone(String phone) {
        // Simple normalization: Remove non-digits except '+', ensure starts with '+'
        return phone.replaceAll("[^\\d+]", "").replaceFirst("^([^+])", "+$1");
    }
}

class NameValidator implements IValidator{
    @Override
    public boolean validateName(String name){
        return name != null && name.length() > 0;
    }

    @Override
    public boolean validatePhoneNumber(String phoneNumber){
        return true;
    }
}

class PhoneNumberValidator implements IValidator{
    @Override
    public boolean validatePhoneNumber(String phoneNumber){

        // Phone number should start with '+'
        if (!phoneNumber.matches("^\\+\\d{10}$") && phoneNumber.matches("^\\d{10}$")) {
            return false;
        }
        return true;
    }

    @Override
    public boolean validateName(String name){
        return true;
    }
}

class CompositeValidator implements IValidator{
    IValidator nameValidator;
    IValidator phoneNumberValidator;
    public CompositeValidator(IValidator nameValidator, IValidator phoneNumberValidator){
        this.nameValidator = nameValidator;
        this.phoneNumberValidator = phoneNumberValidator;
    }

    @Override
    public boolean validateName(String name){
        return nameValidator.validateName(name);
    }

    @Override
    public boolean validatePhoneNumber(String phoneNumber){
        return phoneNumberValidator.validatePhoneNumber(phoneNumber);
    }
}

class ExactMatchSearchStrategy implements ISearchStrategy{
    @Override
    public User searchUser(String phoneNumber, IRepositoryStorage repositoryStorage){
        return repositoryStorage.findByName(phoneNumber);
    }
}

interface TrueCaller{
    void saveUser(User user);
    String searchUser(String phoneNumber);
}

class TrueCallerImpl implements TrueCaller{
    IRepositoryStorage iRepositoryStorage;
    IValidator iValidator;
    ISearchStrategy iSearchStrategy;
    public TrueCallerImpl(IRepositoryStorage iRepositoryStorage, IValidator iValidator, ISearchStrategy iSearchStrategy){
        this.iRepositoryStorage = iRepositoryStorage;
        this.iValidator = iValidator;
        this.iSearchStrategy = iSearchStrategy;
    }

    @Override
    public void saveUser(User user){
        if(!iValidator.validateName(user.getName())){
            throw new IllegalArgumentException("Invalid name");
        }

        if(!iValidator.validatePhoneNumber(user.getPhoneNumber())){
            throw new IllegalArgumentException("Invalid phone number");
        }

        iRepositoryStorage.saveUser(user);
    }

    @Override
    public String searchUser(String phoneNumber){
        if(!iValidator.validatePhoneNumber(phoneNumber)){
            throw new IllegalArgumentException("Invalid phone number");
        }
        return iSearchStrategy.searchUser(phoneNumber, iRepositoryStorage).getName();
    }
}

class Runner{
    public static void main(String[] args){
        TrueCaller trueCaller = new TrueCallerImpl(new InMemoryStorage(), new CompositeValidator(new NameValidator(), new PhoneNumberValidator()), new ExactMatchSearchStrategy());
        trueCaller.saveUser(new User("John", "+1234567890"));
        System.out.println(trueCaller.searchUser("+1234567890"));
    }
}