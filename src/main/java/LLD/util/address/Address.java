package LLD.util.address;

public class Address implements Cloneable{
    private String street;
    private String city;
    private String state;
    private String zipCode;

    public Address(String street, String city, String state, String zipCode) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

    // copy constructor
    public Address(Address address){
        this.street = address.street;
        this.city = address.city;
        this.state = address.state;
        this.zipCode = address.zipCode;
    }

    @Override
    public Object clone(){
        try {
            return super.clone();
        } catch (CloneNotSupportedException e){
            throw new RuntimeException(e);
        }
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String formatAddress() {
        return street + ", " + city + ", " + state + " - " + zipCode;
    }

    public boolean isValidZipCode() {
        if (zipCode == null) {
            return false;
        }
        return zipCode.matches("\\d{5,6}");
    }

    public void printAddress() {
        System.out.println(formatAddress());
    }
}
