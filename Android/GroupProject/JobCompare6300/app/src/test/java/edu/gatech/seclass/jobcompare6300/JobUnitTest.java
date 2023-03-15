package edu.gatech.seclass.jobcompare6300;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class JobUnitTest {
    ComparisonSettings testSettings;
    Job testJob;

    @Before
    public void setup() {
        ComparisonSettings testSettings = new ComparisonSettings();
        testJob = new Job("ut_title",
                            "ut_company",
                            "ut_location",
                            30,
                            75000,
                            5000,
                            2,
                            20,
                            1000,
                            true,
                            testSettings);
    }

    @Test
    public void test_setupNewJob() {
        Job scoreJob = new Job("scoreJob",
                "scoreCompany",
                "scoreLocation",
                200,
                100000,
                10000,
                3,
                20,
                1000,
                500,
                50,
                false,
                1234);
        assertEquals(1234, scoreJob.getScore(), 0.001);
    }

    @Test
    public void test_calcScore() {

        testJob.calculateScores(testSettings);
        assertEquals(53479.4872, testJob.getScore(), 0.001);
    }

    @Test
    public void test_calcAYS() {

        testJob.calculateScores(testSettings);
        assertEquals(250000.0, testJob.getAys(), 0.001);
    }

    @Test
    public void test_calcAYB() {

        testJob.calculateScores(testSettings);
        assertEquals(16666.6667, testJob.getAyb(), 0.001);
    }

    @Test
    public void test_getTitle() {

        assertEquals("ut_title", testJob.getTitle());
    }

    @Test
    public void test_getCompany() {

        assertEquals("ut_company", testJob.getCompany());
    }

    @Test
    public void test_getLocation() {

        assertEquals("ut_location", testJob.getLocation());
    }
    @Test
    public void test_getLivingCost() {

        assertEquals(30, testJob.getLivingCost());
    }

    @Test
    public void test_getYearlySalary() {

        assertEquals(75000, testJob.getYearlySalary(), 0.001);
    }

    @Test
    public void test_getYearlyBonus() {

        assertEquals(5000, testJob.getYearlyBonus(), 0.001);
    }

    @Test
    public void test_getRemoteDays() {

        assertEquals(2, testJob.getRemoteDays());
    }

    @Test
    public void test_getLeaveDays() {

        assertEquals(20, testJob.getLeaveDays());
    }

    @Test
    public void test_getNumberOfShares() {

        assertEquals(1000, testJob.getNumberOfShares());
    }

    @Test
    public void test_getCurrentJon() {

        assertEquals(true, testJob.isCurrentJob());
    }

}