package design_pattern.creational;

import java.io.Serializable;
import java.lang.reflect.Constructor;

/**
 Design_patterns: standard solutions/patterns to common problems in software design.
    Creational:
         👉 Definition: Deal with object creation mechanisms, trying to create objects in a manner suitable to the situation.
         👉 Purpose: make system independent of how objects are created and managed.
         •	Singleton → Ensures only one instance exists.
                Example: DB connection pool, all spring beans
         •	Factory Method → Creates objects without exposing instantiation logic.
                Example: Logger implementation (SLF4J / Log4j)
         •	Builder → Builds complex objects step by step.
             •	StringBuilder / StringBuffer in Java (step-by-step append).
             •	Building HTTP requests in libraries like OkHttp:
    Structural:
         👉 Definition: Deal with how classes/objects structure are changed/manipulated to form larger structures.
         👉 Purpose: Simplify the structure of classes and objects to form larger, more flexible structures.
         •	Adapter → Acts as a bridge/adapter between two incompatible interfaces, allowing them to work together.
             Example: A USB-to-Ethernet adapter that allows a computer with only USB ports to connect to an Ethernet network.
             The adapter implements the USB interface that the computer expects, while internally converting the connection to work with the Ethernet standard.
         •	Decorator → Adds functionality to objects dynamically.
             Example: Customizing a coffee order at a cafe - start with a basic coffee and add milk, sugar, or flavors as decorators.
             Each addition wraps the original object, adding new features while keeping the core functionality intact.
         •	Facade → Provides simplified interface to build complex eco-subsystems.
             Example: A home theater remote control that provides simple buttons like 'Watch Movie' which internally handles
             turning on the TV, setting the input, starting the sound system, and dimming the lights.
             •	Hibernate → Provides a simple API (Session.save(obj)) hiding complexity of JDBC, SQL, transaction handling.
             •	Spring’s JdbcTemplate → wraps complex JDBC calls into simple methods.
    Behavioral:
         👉 Definition: Deal with the communication between objects (how they interact with each other), assigning responsibilities.
         👉 Purpose: Increase flexibility in carrying out communication.
         •	Observer → One-to-many dependency (event listeners).
             Example: YouTube channel subscriptions where subscribers get notified when a new video is uploaded.
             The YouTube channel (subject) maintains a list of subscribers and notifies them automatically of new uploads.
            •	Event-driven systems → KafkaFile consumers subscribed to a topic.
         •	Strategy → Defines a family of algorithms and makes them interchangeable.
             Example: Payment processing system supporting multiple payment methods (Credit Card, UPI, PayPal) where the payment
             algorithm can be swapped at runtime without changing the client code.
         •	Iterator → Provides a way to access elements of a collection sequentially without exposing its underlying representation.
             Example: A TV remote's channel buttons that let you cycle through channels (collection) without needing to know
             how the channels are stored or organized internally.
            Java Collections → Iterator allows traversing List, Set, Map without exposing internal structure
 * */

/**
 A class of which only a single instance can exist
 prob:
    Application needs one, and only one, instance of an object.
    Additionally, lazy initialization and global access are necessary.
 Check list
     constructors to be private.
    private static attribute/field of type class
    public static accessor function
    Do "lazy initialization".

 uses:
    java.lang.Runtime
    SpringBoot beans (default scope)
    Hardware resource access (DB connection, File-system, DriverClasses)
    Logger
    Configuration File
    Cache objects
 * */

class singletonEagerInitialization{
    private static singletonEagerInitialization instance = new singletonEagerInitialization();
    private singletonEagerInitialization(){}

    public static  singletonEagerInitialization getInstance() {
        return instance;
    }
}


public class Singleton {
    private static Singleton instance;

    private Singleton(){}

    public static Singleton getInstance(){     // not a thread-safe
        if(instance == null)
            instance = new Singleton();
        return instance;
    }
}

class SingletonThreadSafe{
    private static SingletonThreadSafe instance;
    private SingletonThreadSafe(){}

    public static synchronized SingletonThreadSafe getInstance() { // write optimised but expensive read
        if(instance == null)
            instance = new SingletonThreadSafe();
        return instance;
    }
}

class SingletonDoubleLocking{
    //the values of the volatile variable will never be cached and all writes/reads will be done to and from the main memory only.
    private static volatile SingletonDoubleLocking instance;
    private SingletonDoubleLocking(){}

    public static SingletonDoubleLocking getInstance() {
        if(instance == null)
            // To make thread safe
            synchronized (SingletonDoubleLocking.class){
                // check again as multiple thread can reach above step
                if (instance == null)
                    instance = new SingletonDoubleLocking();
            }
        return instance;
    }
}


class ReflectionSingleton {
    // public instance initialized when loading the class
    private static ReflectionSingleton instance = new ReflectionSingleton();

    private ReflectionSingleton()
    {
        // private constructor
    }

    public static ReflectionSingleton getInstance(){return instance;}
}

/**
 Reflection gives us information about the class to which an object belongs and also the methods of that class that can be executed by using the object.
 Through reflection, we can invoke methods at runtime irrespective of the access specifier used with them.
 * */
class ReflectionRunner {

    public static void main(String[] args)
    {
        ReflectionSingleton instance1 = ReflectionSingleton.getInstance();
        ReflectionSingleton instance2 = null;
        try {
            Constructor[] constructors = ReflectionSingleton.class.getDeclaredConstructors();
            for (Constructor constructor : constructors) {
                // Below code will destroy the singleton pattern
                constructor.setAccessible(true);
                instance2 = (ReflectionSingleton)constructor.newInstance();
                break;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("instance1.hashCode():- "
                + instance1.hashCode());
        System.out.println("instance2.hashCode():- "
                + instance2.hashCode());
    }
}
/**
 Reflection:
    As enums don’t have any constructor so it is not possible for Reflection to utilize it.
 * */
//Java program for Enum type singleton
enum SingletonEnum
{
    INSTANCE;
}

/**
 Serialization/deSerialization:
    To overcome this issue, we have to implement method readResolve() method.
 * */
class SingletonReadResolved implements Serializable
{
    // public instance initialized when loading the class
    private static SingletonReadResolved instance = new SingletonReadResolved();

    private SingletonReadResolved()
    {
        // private constructor
    }

    public static SingletonReadResolved getInstance(){return instance;}

    // implement readResolve method
    protected Object readResolve()
    {
        return instance;
    }
}


/**
 clone:
    To overcome this issue, override clone() method and throw an exception from clone method that is CloneNotSupportedException.
 * */
class SingletonCloneNonSupported implements Cloneable
{
    // public instance initialized when loading the class
    public static SingletonCloneNonSupported instance = new SingletonCloneNonSupported();

    private SingletonCloneNonSupported()
    {
        // private constructor
    }

    // implement clone() method
    @Override
    protected Object clone() throws CloneNotSupportedException
    {
        throw new CloneNotSupportedException();
    }
}