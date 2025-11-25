package A_interview_experiences.flipkart.flipkartminutes.service;


import A_interview_experiences.flipkart.flipkartminutes.model.Customer;
import A_interview_experiences.flipkart.flipkartminutes.model.DeliveryPartner;
import A_interview_experiences.flipkart.flipkartminutes.repository.CustomerRepository;
import A_interview_experiences.flipkart.flipkartminutes.repository.DeliveryPartnerRepository;

public class UserService {

    private final CustomerRepository customerRepo;
    private final DeliveryPartnerRepository partnerRepo;

    public UserService(CustomerRepository customerRepo, DeliveryPartnerRepository partnerRepo) {
        this.customerRepo = customerRepo;
        this.partnerRepo = partnerRepo;
    }

    public Customer registerCustomer(String id, String name) {
        validateInput(id, name);
        Customer c = new Customer(id, name);
        if (customerRepo.existsById(id)) {
            throw new IllegalArgumentException("Customer with id " + id + " already exists");
        }
        return customerRepo.save(c);
    }

    public DeliveryPartner registerPartner(String id, String name) {
        validateInput(id, name);
        if (partnerRepo.existsById(id)) {
            throw new IllegalArgumentException("Partner with id " + id + " already exists");
        }
        DeliveryPartner p = new DeliveryPartner(id, name);
        return partnerRepo.save(p);
    }

    private void validateInput(String id, String name) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Id cannot be null or empty");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
    }
}