package Service;

public class Employee extends Person{
    private final String taxID;

    public Employee(String account, String first, String last, String phoneNumber, String taxID) {
        super(account, first, last, phoneNumber);
            this.taxID = taxID;
    }

    public String getTaxID() {
        return taxID;
    }
}
