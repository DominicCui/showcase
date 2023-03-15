package Service;

public class Person{
    private String account;
    private final String firstName;
    private final String lastName;
    private final String phoneNumber;

    public Person(String account, String first, String last, String phoneNumber){
        this.account = account;
        this.firstName = first;
        this.lastName = last;
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAccount() {
        return account;
    }
}