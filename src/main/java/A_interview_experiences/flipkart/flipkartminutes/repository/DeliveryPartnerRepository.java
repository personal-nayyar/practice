package A_interview_experiences.flipkart.flipkartminutes.repository;


import A_interview_experiences.flipkart.flipkartminutes.model.DeliveryPartner;

public class DeliveryPartnerRepository extends InMemoryRepository<DeliveryPartner, String> {
    private static DeliveryPartnerRepository instance;

    private DeliveryPartnerRepository() {}

    public static DeliveryPartnerRepository getInstance() {
        if (instance == null) {
            instance = new DeliveryPartnerRepository();
        }
        return instance;
    }
    @Override protected String getId(DeliveryPartner entity) { return entity.getId(); }
}