package util.Notification;

public class NotificationFactory {
    public static Notification getNotification(NotificationType notificationType, String message, String user) {
        switch (notificationType) {
            case SMS:
                return new SMSNotification(message, user);
            case EMAIL:
                return new EmailNotification(message, user);
            case IN_APP:
                return new PopUpNotification(message, user);
            default:
                throw new IllegalArgumentException("Invalid notification type");
        }
    }
}
