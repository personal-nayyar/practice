package A_interview_experiences.flipkart.flipkartminutes.repository;


import A_interview_experiences.flipkart.flipkartminutes.model.Customer;

public class CustomerRepository extends InMemoryRepository<Customer, String> {
    private static CustomerRepository instance;

    private CustomerRepository() {}

    public static CustomerRepository getInstance() {
        if (instance == null) {
            instance = new CustomerRepository();
        }
        return instance;
    }
    @Override
    protected String getId(Customer entity) {
        return entity.getId();
    }
}