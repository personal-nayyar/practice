package A_interview_experiences.flipkart.flipkartminutes.Notification;

public interface INotificationService {
    void notifyUser(NotificationType notificationType, String message, String user);
    void notifyAsync(NotificationType notificationType, String message, String user);
    void shutdown();
}
