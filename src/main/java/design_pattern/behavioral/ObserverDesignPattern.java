package design_pattern.behavioral;

import java.util.ArrayList;
import java.util.List;

/**
 in observer design pattern objects are bound such that changes in target/subject object are observed by subscribers/observer.
 Define a one-to-many dependency between objects so that
 when 'one object changes state, all its dependents/subscriber are notified and updated automatically'.
 * */
public class ObserverDesignPattern {
}

interface Subject{
    void register(Observer observer);
    void remove(Observer observer);
    void notifyObservers();
}

interface Observer{
    void update();
}

class YoutubeChannel implements Subject{
    List<Observer> observers;
    List<String> videos;

    YoutubeChannel(){
        observers = new ArrayList<>();
    }

    @Override
    public void register(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void remove(Observer observer){
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        observers.stream().forEach(observer -> observer.update());
    }

    public void addVideo(String movie){
        this.videos.add(movie);
        notifyObservers();
    }
}

class Subscriber implements Observer{
    String name;
    YoutubeChannel channel;

    Subscriber(String name, YoutubeChannel channel){
        this.name = name;
        this.channel = channel;
        this.channel.register(this);
    }

    @Override
    public void update() {
        System.out.println("Hey "+name+" new video uploaded "+channel.videos);
    }
}