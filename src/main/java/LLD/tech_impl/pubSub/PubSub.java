package LLD.tech_impl.pubSub;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.*;

// <---- Entity ---->
@AllArgsConstructor
class Message{
    String topic;
    String payload;
    Instant timestamp;
}

interface ISubject{
    void subscribe(ISubscriber subscriber);
    void unsubscribe(ISubscriber subscriber);
    void notifySubscribers(Message message);
}

@FunctionalInterface
interface ISubscriber{
    void update(Message message);
}

@Getter
class Topic implements ISubject{
    private String name;
    private Set<ISubscriber> subscribers = new HashSet<>();

    Topic(String name){
        this.name = name.toLowerCase();
    }
    @Override
    public void subscribe(ISubscriber subscriber) {
        subscribers.add(subscriber);
    }

    @Override
    public void unsubscribe(ISubscriber subscriber) {
        subscribers.remove(subscriber);
    }

    @Override
    public void notifySubscribers(Message message) {
        for (ISubscriber subscriber : subscribers) {
            subscriber.update(message);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Topic other = (Topic) obj;
        return Objects.equals(name, other.name);
    }

    public String toString(){
        return "Topic{" + "name='" + name + '\'' + ", subscribers=" + subscribers + '}';
    }
}

@ToString
class Consumer implements ISubscriber{
    String name;
    Consumer(String name){
        this.name = name;
    }
    @Override
    public void update(Message message) {
        System.out.println("Received message for topic " + message.topic + " by "+name+ ": " + message.payload);
    }
}

@FunctionalInterface
interface IProducer{
    void publish(String topic, Message message);
}

class Producer implements IProducer{
    private ISubjectRepository subjectRepository;
    Producer(){
        this.subjectRepository = InMemorySubjectRepository.getInstance();
    }
    @Override
    public void publish(String topic, Message message) {
        topic = topic.toLowerCase();
        ISubject subject = subjectRepository.getSubject(topic);
        if(subject == null){
            subject = new Topic(topic);
            subjectRepository.save(subject);
        }
        subject.notifySubscribers(message);
    }
}

interface ISubjectRepository{
    ISubject getSubject(String topic);
    void save(ISubject subject);
}

class InMemorySubjectRepository implements ISubjectRepository{
    private static InMemorySubjectRepository instance = null;
    private InMemorySubjectRepository(){}
    public static InMemorySubjectRepository getInstance(){
        if(instance == null){
            instance = new InMemorySubjectRepository();
        }
        return instance;
    }

    Map<String, ISubject> subjectMap = new HashMap<>();

    @Override
    public ISubject getSubject(String topic) {
        return subjectMap.get(topic);
    }

    @Override
    public void save(ISubject subject) {
        subjectMap.put(((Topic)subject).getName(), subject);
    }
}

// Facade
public interface PubSub {
    void createTopic(String topic);
    void subscribe(String topic, ISubscriber subscriber);
    void unsubscribe(String topic, ISubscriber subscriber);
    void publish(String topic, Message message);
}

class Runner{
    public static void main(String[] args){
        InMemorySubjectRepository subjectRepository = InMemorySubjectRepository.getInstance();
        Topic topic1  =  new Topic("Topic1");
        subjectRepository.save(topic1);

        ISubscriber subscriber1 = new Consumer("subscriber1");
        ISubscriber subscriber2 = new Consumer("subscriber2");

        topic1.subscribe(subscriber1);
        topic1.subscribe(subscriber2);

        Producer producer = new Producer();
        producer.publish("topic1", new Message("Hello", "topic1", Instant.now()));


        topic1.unsubscribe(subscriber1);
        producer.publish("topic1", new Message("Hello Again", "topic1", Instant.now()));


    }
}



