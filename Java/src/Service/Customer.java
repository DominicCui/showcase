package Service;

public class Customer extends Person{
    private final String rating;
    private int credit;

    public Customer(String account,String first, String last, String phoneNumber, String rating, String credit){
        super(account, first, last, phoneNumber);
        this.rating = rating;
        this.credit = Integer.parseInt(credit);
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }
}