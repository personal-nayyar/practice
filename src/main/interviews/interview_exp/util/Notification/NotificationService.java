package util.Notification;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotificationService implements INotificationService{
    private static NotificationService instance;

    private NotificationService() {}

    public static NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }
    ExecutorService executorService = Executors.newCachedThreadPool();

    @Override
    public void notifyUser(NotificationType notificationType, String message, String user) {
        Notification notification = NotificationFactory.getNotification(notificationType, message, user);
        notification.notifyUser();
    }

    @Override
    public void notifyAsync(NotificationType notificationType, String message, String user) {
        executorService.submit(() -> notifyUser(notificationType, message, user));
    }

    @Override
    public void shutdown() {
        executorService.shutdown();
    }
}
