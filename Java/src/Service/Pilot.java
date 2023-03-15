package Service;

public class Pilot extends Employee{
    private String licenseID;
    int level;
    boolean assigned = false;
    Drone operatedDrone;

    public Pilot(String account, String first, String last, String phoneNumber,
                 String taxID, String licenseID, String level){
        super(account, first, last, phoneNumber, taxID);
        this.licenseID = licenseID;
        this.level = Integer.parseInt(level);
    }

    public int getLevel() {
        return level;
    }

    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
    }

    public void setOperatedDrone(Drone operatedDrone) {
        this.operatedDrone = operatedDrone;
    }
}
