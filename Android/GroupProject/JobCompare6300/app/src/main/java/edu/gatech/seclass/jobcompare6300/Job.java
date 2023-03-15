package edu.gatech.seclass.jobcompare6300;


public class Job {

    private String title;
    private String company;
    private String location;
    private int livingCost;
    private double yearlySalary;
    private double yearlyBonus;
    private int remoteDays;
    private int leaveDays;
    private int numberOfShares;
    private boolean isCurrentJob;
    private double score;
    private double ays;
    private double ayb;

    public Job(String title,
               String company,
               String location,
               int livingCost,
               double yearlySalary,
               double yearlyBonus,
               int remoteDays,
               int leaveDays,
               int numberOfShares,
               boolean isCurrentJob,
               ComparisonSettings settings) {
        this.title = title;
        this.company = company;
        this.location = location;
        this.livingCost = livingCost;
        this.yearlySalary = yearlySalary;
        this.yearlyBonus = yearlyBonus;
        this.remoteDays = remoteDays;
        this.leaveDays = leaveDays;
        this.numberOfShares = numberOfShares;
        this.isCurrentJob = isCurrentJob;
        this.calculateScores(settings);
    }

    // store to DB; compare job needs ays, ayb
    public Job(String title,
               String company,
               String location,
               int living_cost,
               double yearly_salary,
               double yearly_bonus,
               int allowed_weekly_telework,
               int leave_days,
               int number_of_shares,
               double ays,
               double ayb,
               boolean current_job,
               double score) {
        this.title = title;
        this.company = company;
        this.location = location;
        this.livingCost = living_cost;
        this.yearlySalary = yearly_salary;
        this.yearlyBonus = yearly_bonus;
        this.remoteDays = allowed_weekly_telework;
        this.leaveDays = leave_days;
        this.numberOfShares = number_of_shares;
        this.ays = ays;
        this.ayb = ayb;
        this.isCurrentJob = current_job;
        this.score = score;
    }

    public void calculateScores(ComparisonSettings settings) {
        // weights
        double w1 = (double) settings.getwYearlySalary() / settings.getwTotal();
        double w2 = (double) settings.getwYearlyBonus() / settings.getwTotal();
        double w3 = (double) settings.getwShares() / settings.getwTotal();
        double w4 = (double) settings.getwLeaveDays() / settings.getwTotal();
        double w5 = (double) settings.getwRemoteDays() / settings.getwTotal();

        double AYS = this.yearlySalary / this.livingCost * 100.0;
        double AYB = this.yearlyBonus / this.livingCost * 100.0;
        this.ays = AYS;
        this.ayb = AYB;
        double CSO = this.numberOfShares;
        double LT  = this.leaveDays;
        double RWT = this.remoteDays;

        double score = w1 * AYS
                     + w2 * AYB
                     + w3 * CSO/4
                     + w4 * (LT * AYS / 260)
                     - w5 * ((260 - 52 * RWT) * (AYS / 260) / 8);
        this.score = score;
    }

    public String getTitle() { return title; }
    public String getCompany() { return company; }
    public String getLocation() { return location; }
    public int getLivingCost() { return livingCost; }
    public double getYearlySalary() { return yearlySalary; }
    public double getYearlyBonus() { return yearlyBonus; }
    public int getRemoteDays() { return remoteDays; }
    public int getLeaveDays() { return leaveDays; }
    public int getNumberOfShares() { return numberOfShares; }
    public double getAys() { return ays; }
    public double getAyb() { return ayb; }
    public boolean isCurrentJob() { return isCurrentJob; }
    public double getScore() { return score; }
}
