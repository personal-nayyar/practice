package A_interview_experiences.kpmg;

import LLD.util.address.Address;

interface IBuilding{
    String getName();
    Address getAddress();
    void open(); // implement specific open behaviours
    void close();

    default void printInfo(){
        // pretty print
        System.out.printf("Building: %s, Address: %s%n", getName(), getAddress());
    }
}

abstract class Building implements IBuilding{
    String name;
    Address address;
    Building(String name, Address address) {
        this.name = name;
        this.address = (Address) address.clone();
    }

    public String getName() {
        return name;
    }

    public Address getAddress() {
        return address;
    }
}

class School extends Building{
    int studentCapacity;
    int enrolledStudent;
    public School(String name, Address address, int studentCapacity) {
        super(name, address);
        this.studentCapacity = studentCapacity;
    }

    // specific implementation
    @Override
    public void open() {
        System.out.println("School is open");
    }

    @Override
    public void close() {
        System.out.println("School is closed");
    }

    // school specific behaviours
    public void enrollStudent(){
        if(enrolledStudent < studentCapacity){
            enrolledStudent++;
        }
        else{
            System.out.println("School is full");
        }
    }

}

class Hospital extends Building{
    int numberOfDoctors;
    int numberOfBeds;
    public Hospital(String name, Address address, int numberOfDoctors, int numberOfBeds) {
        super(name, address);
        this.numberOfBeds = numberOfBeds;
        this.numberOfDoctors =  numberOfDoctors;
    }

    // specific implementation
    @Override
    public void open() {
        System.out.println("Hospital is open 24*7");
    }

    @Override
    public void close() {
        System.out.println("Hospital is closed");
    }

    // hospital specific behaviours
    void admitPatient(){
        if(numberOfBeds > 0){
            numberOfBeds--;
        }
        else{
            System.out.println("Hospital is full");
        }
    }
}

class BuildingDemo{
    public static void main(String[] args) {
        Address address = new Address("123 Main St", "Anytown", "CA", "12345");
        School school = new School("Public School", address, 100);
        school.open();
        school.enrollStudent();
        school.printInfo();

        Hospital hospital = new Hospital("Public Hospital", address, 10, 100);
        hospital.open();
        hospital.admitPatient();
        hospital.printInfo();
    }
}
