package LLD.chatApp.whatsApp;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
abstract class Chat implements Observable {
    String chatId;
    List<String> participants = new ArrayList<>();
    Set<Observer> observers = ConcurrentHashMap.newKeySet();
    List<Message> messages = new ArrayList<>();

    public Chat(String chatId, List<String> participants) {
        this.chatId = chatId;
        this.participants = participants;
//        this.observers.addAll(participants);
    }

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    public void addMessage(Message msg) {
        messages.add(msg);
        notifyObservers(msg);
    }

    @Override
    public void notifyObservers(Message msg) {
        for (Observer o : observers) o.update(msg);
    }
}
