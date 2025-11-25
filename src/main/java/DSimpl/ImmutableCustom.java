package DSimpl;

/*
Immutable properties:
1. Once created, cannot be modified
2. Thread-safe
3. Can be shared
4. Safe for use in concurrent environments
5. Immutable objects are thread-safe

creation logic:
    1. final class
    2. private final fields
    3. only getter no setter
    4. All argument constructor
    5. Reference fields should also be immutable or copied
* */


import LLD.util.address.Address;

public final class ImmutableCustom {
    private final int id;
    private final String name;
    private final Address address;

    public ImmutableCustom(int id, String name, Address address) throws CloneNotSupportedException {
        this.id = id;
        this.name = name;
//        this.address = address == null ? null : (Address) address.clone();
        this.address = new Address(address); // deep copy using copy constructor
    }

    // getter methods
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Address getAddress() throws CloneNotSupportedException {
    // the address field is not immutable, so we need to return a copy of it
    // otherwise, the address object can be modified outside of this class
    // and the immutability of this class will be broken
        // 'clone()' has protected access in 'java.lang.Object'
        // so it cannot be accessed directly from outside the class
        // we need to make the access public
        return address == null ? null : (Address) address.clone();
    }
}
