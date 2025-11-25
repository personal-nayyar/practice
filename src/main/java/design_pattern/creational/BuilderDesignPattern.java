package design_pattern.creational;
/**
 Separate the construction of a complex object creation from its object representation
 'provide a way to build a complex object step-by-step and provide a method that will return the final Object'.
 Implementation:
    First you need to create a static nested class(builder) and then copy all the arguments from the outer class to the Builder class.
    We should follow the naming convention and if the class name is Computer then builder class should be named as ComputerBuilder.
    Java Builder class should have a public constructor with all the required attributes as parameters.
    Java Builder class should have methods to set the optional parameters and it should return the same Builder object after setting the optional attribute.
    The final step is to provide a build() method in the builder class that will return the Object needed by client program.
    For this we need to have a private constructor in the Class with Builder class as argument.
 * */
public class BuilderDesignPattern {
}

class Computer{
    private String HDD;
    private String RAM;
    private boolean isGraphicsCardEnabled;
    private boolean isBluetoothEnabled;

    static class ComputerBuilder{
        private final String HDD;
        private final String RAM;
        private boolean isGraphicsCardEnabled; //optional
        private boolean isBluetoothEnabled; // optional

        public ComputerBuilder(String hdd, String ram){
            this.HDD = hdd;
            this.RAM = ram;
        }

        public ComputerBuilder setGraphicsCardEnabled(boolean isGraphicsCardEnabled){
            this.isGraphicsCardEnabled = isGraphicsCardEnabled;
            return this;
        }

        public ComputerBuilder setIsBluetoothEnabled(boolean isBluetoothEnabled){
            this.isBluetoothEnabled = isBluetoothEnabled;
            return this;
        }

        public Computer build(){
            return new Computer(this);
        }
    }

    private Computer(ComputerBuilder builder){
        this.HDD = builder.HDD;
        this.RAM = builder.RAM;
        this.isBluetoothEnabled = builder.isBluetoothEnabled;
        this.isGraphicsCardEnabled =  builder.isGraphicsCardEnabled;
    }

    public static void main(String[] args) {
        // generate code to test builder design pattern
        Computer computer = new Computer.ComputerBuilder("500GB", "2GB").setGraphicsCardEnabled(true).setIsBluetoothEnabled(true).build();
        Computer computer1 = new Computer.ComputerBuilder("500GB", "2GB").build();
    }
}