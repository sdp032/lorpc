package test.service;

/**
 * Created by lo on 1/16/17.
 */
public class Address {
    private AddressType type;
    private String address;

    public Address(AddressType type, String address) {
        this.type = type;
        this.address = address;
    }

    public AddressType getType() {
        return type;
    }

    public void setType(AddressType type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
