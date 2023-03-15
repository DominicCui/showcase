package edu.gatech.seclass.jobcompare6300;

public class ComparisonSettings {

    // Weights
    static private int wYearlySalary;
    static private int wYearlyBonus;
    static private int wRemoteDays;
    static private int wLeaveDays;
    static private int wShares;
    static private double wTotal;

    public ComparisonSettings() {
        wYearlySalary = 1;
        wYearlyBonus = 1;
        wRemoteDays = 1;
        wLeaveDays = 1;
        wShares = 1;

        sumWeights();
    }

    public ComparisonSettings(int wSalary, int wBonus, int wRemoteDays, int wLeaveDays, int wShares) {
        this.wYearlySalary = wSalary;
        this.wYearlyBonus = wBonus;
        this.wRemoteDays = wRemoteDays;
        this.wLeaveDays = wLeaveDays;
        this.wShares = wShares;
        this.sumWeights();
    }

    private static void sumWeights() {
        wTotal = wYearlySalary + wYearlyBonus +
                wRemoteDays + wLeaveDays + wShares;
    }

    public static int getwYearlySalary() {
        return wYearlySalary;
    }

    public static int getwYearlyBonus() {
        return wYearlyBonus;
    }

    public static int getwRemoteDays() {
        return wRemoteDays;
    }

    public static int getwLeaveDays() {
        return wLeaveDays;
    }

    public static int getwShares() {
        return wShares;
    }

    public static double getwTotal() {
        return wTotal;
    }
}
