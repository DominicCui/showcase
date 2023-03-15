package edu.gatech.seclass.jobcompare6300;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ComparisonSettingsUnitTest {
    ComparisonSettings testSettings;

    @Before
    public void setup() {
        ComparisonSettings testSettings = new ComparisonSettings(1,
                                                                2,
                                                                3,
                                                                4,
                                                                5);
    }

    @Test
    public void test_getSumWeights() {

        assertEquals(15, testSettings.getwTotal(), 0.001);
    }

    @Test
    public void test_getYearlySalaryWeight() {

        assertEquals(1, testSettings.getwYearlySalary());
    }

    @Test
    public void test_getYearlyBonusWeight() {

        assertEquals(2, testSettings.getwYearlyBonus());
    }

    @Test
    public void test_getRemoteDaysWeight() {

        assertEquals(3, testSettings.getwRemoteDays());
    }

    @Test
    public void test_getLeaveDaysWeight() {

        assertEquals(4, testSettings.getwLeaveDays());
    }

    @Test
    public void test_getSharesWeight() {

        assertEquals(5, testSettings.getwShares());
    }

}