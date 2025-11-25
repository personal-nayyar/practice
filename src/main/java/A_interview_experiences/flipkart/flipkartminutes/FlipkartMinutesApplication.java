package A_interview_experiences.flipkart.flipkartminutes;


import A_interview_experiences.flipkart.flipkartminutes.Notification.NotificationService;
import A_interview_experiences.flipkart.flipkartminutes.model.Customer;
import A_interview_experiences.flipkart.flipkartminutes.model.DeliveryPartner;
import A_interview_experiences.flipkart.flipkartminutes.repository.CustomerRepository;
import A_interview_experiences.flipkart.flipkartminutes.repository.DeliveryPartnerRepository;
import A_interview_experiences.flipkart.flipkartminutes.repository.OrderRepository;
import A_interview_experiences.flipkart.flipkartminutes.schedulars.OrderAssignmentScheduler;
import A_interview_experiences.flipkart.flipkartminutes.service.OrderService;
import A_interview_experiences.flipkart.flipkartminutes.service.UserService;

import java.util.*;
import java.util.concurrent.*;

public class FlipkartMinutesApplication {
    public static void main(String[] args) throws InterruptedException {
        NotificationService notif = NotificationService.getInstance();
        CustomerRepository custRepo = CustomerRepository.getInstance();
        DeliveryPartnerRepository partnerRepo = DeliveryPartnerRepository.getInstance();
        OrderRepository orderRepo = OrderRepository.getInstance();

        // Demo setup
        UserService userService = new UserService(custRepo, partnerRepo);
        OrderService orderService = new OrderService(orderRepo, partnerRepo, custRepo, notif);


        // Initialize both schedulers (assignment + auto-cancel)
        OrderAssignmentScheduler scheduler =
                new OrderAssignmentScheduler(orderService, orderRepo, notif);
        scheduler.start();

        // register user and partner
        Customer c1 = userService.registerCustomer("C1", "Rohan");
        DeliveryPartner p1 = userService.registerPartner("P1", "Alice");

        // Place an order without free partner → goes to queue
        String orderId = orderService.placeOrder(c1.getId(), "Milk");
        Thread.sleep(2000);

        String orderId2 = orderService.placeOrder(c1.getId(), "Milk2");
        Thread.sleep(2000);

        final String orderId3 = orderService.placeOrder(c1.getId(), "Milk3");
        Thread.sleep(2000);

        // cancel order before assignment
        orderService.cancelOrder(c1.getId(), orderId);

        // Register partner later; scheduler will auto-assign it next minute
        orderService.registerFreePartner(p1.getId());

        // Wait a bit for schedulers to run (for demo)
        Thread.sleep(60000);

        // show order status
        System.out.println(orderService.showOrderStatus(orderId));
        System.out.println(orderService.showOrderStatus(orderId2));
        System.out.println(orderService.showOrderStatus(orderId3));

        // show deliver partner status
        System.out.println(orderService.showPartnerStatus(p1.getId()));

        // order pick up, pick up order only that is assigned to him
        orderService.partnerPickup(p1.getId());

        // complete order, deliver order only that is picked up by him
        orderService.partnerDeliver(p1.getId(), 5);

        // Concurrent order modification test
        ExecutorService executor = Executors.newCachedThreadPool();
        List<Callable<String>> tasks = Arrays.asList(
                () -> {
                    orderService.cancelOrder(c1.getId(), orderId3);
                    return "Order cancelled";
                },
                () -> {
                    orderService.cancelOrder(c1.getId(), orderId3);
                    return "Order cancelled";
                },
                () -> {
                    orderService.assignIfPossible(orderRepo.findById(orderId3).get());
                    return "Order cancelled";
                }
        );
        List<Future<String>> results = executor.invokeAll(tasks);
        for (Future<String> result : results) {
            try {
                System.out.println(result.get());
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        executor.shutdown();

        scheduler.stop(); // Clean shutdown
    }
}