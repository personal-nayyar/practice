package LLD.chatApp.whatsApp;

interface Observable {
    void addObserver(Observer observer);

    void removeObserver(Observer observer);

    void notifyObservers(Message msg);
}
