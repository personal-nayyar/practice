package A_interview_experiences.flipkart.flipkartminutes.Notification;

// abstraction
abstract class Notification {
    String message;
    String user;

    public Notification(String message, String user) {
        this.message = message;
        this.user = user;
    }

    abstract void notifyUser();
}

class PopUpNotification extends Notification {
    public PopUpNotification(String message, String user) {
        super(message, user);
    }

    @Override
    void notifyUser() {
        System.out.println("Push Notification sent to " + user + " : " + message);
    }
}

class EmailNotification extends Notification {
    public EmailNotification(String message, String user) {
        super(message, user);
    }

    @Override
    void notifyUser() {
        System.out.println("Email Notification sent to " + user + " : " + message);
    }
}

class SMSNotification extends Notification {
    public SMSNotification(String message, String user) {
        super(message, user);
    }

    @Override
    void notifyUser() {
        System.out.println("SMS Notification sent to " + user + " : " + message);
    }
}

