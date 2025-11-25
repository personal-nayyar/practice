package A_interview_experiences.flipkart.flipkartminutes.repository;


import A_interview_experiences.flipkart.flipkartminutes.model.Order;

public class OrderRepository extends InMemoryRepository<Order, String> {

    private static final OrderRepository INSTANCE = new OrderRepository();

    private OrderRepository() {}

    public static OrderRepository getInstance() {
        return INSTANCE;
    }
    @Override
    protected String getId(Order entity) {
        return entity.id;
    }
}