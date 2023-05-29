package edu.gatech.seclass;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Junit test class created for use in Georgia Tech CS6300.
 *
 * You should implement your tests in this class.
 */

public class MyStringTest {

    private MyStringInterface mystring;

    @Before
    public void setUp() {
        mystring = new MyString();
    }

    @After
    public void tearDown() {
        mystring = null;
    }

    @Test
    // Description: Instructor-provided test 1
    public void testCountNumbersS1() {
        mystring.setString("My numbers are 11, 96, and thirteen");
        assertEquals(2, mystring.countNumbers());
    }

    @Test
    // Description: Count Integer in String
    public void testCountNumbersS2() {
        mystring.setString("i l0ve 2 pr0gram.");
        assertEquals(3, mystring.countNumbers());
    }

    @Test
    // Description: Count decimal in String
    public void testCountNumbersS3() {
        mystring.setString("I don't handle real number such as 10.4");
        assertEquals(2, mystring.countNumbers());
    }

    @Test(expected = NullPointerException.class)
    // Description: Count Integer in empty(NULL) String
    public void testCountNumbersS4() {
        mystring.setString(null);
    }

    @Test
    // Description: Instructor-provided test 2
    public void testAddNumberS1() {
        mystring.setString("hello 90, bye 2");
        assertEquals("hello 92, bye 4", mystring.addNumber(2, false));
    }

    @Test(expected = IllegalArgumentException.class)
    // Description: Add negative number
    public void testAddNumberS2() {
        mystring.setString("hi 12345");
        mystring.addNumber(-11, false);
    }

    @Test
    // Description: Add number with revert
    public void testAddNumberS3() {
        mystring.setString("hello 90, bye 2");
        assertEquals("hello 89, bye 01", mystring.addNumber(8, true));
    }

    @Test
    // Description: Add number without revert
    public void testAddNumberS4() {
        mystring.setString("12345");
        assertEquals("12347", mystring.addNumber(2, false));
    }

    @Test
    // Description: Add to a negative number without revert
    public void testAddNumberS5() {
        mystring.setString("-12345");
        assertEquals("-12355", mystring.addNumber(10, false));
    }

    @Test
    // Description: Add to a negative number with revert
    public void testAddNumberS6() {
        mystring.setString("-12345");
        assertEquals("-00421", mystring.addNumber(55, true));
    }

    @Test
    // Description: Instructor-provided test 3
    public void testConvertDigitsToNamesInSubstringS1() {
        mystring.setString("I'd b3tt3r put s0me d161ts in this 5tr1n6, right?");
        mystring.convertDigitsToNamesInSubstring(17, 23);
        assertEquals("I'd b3tt3r put szerome donesix1ts in this 5tr1n6, right?", mystring.getString());
    }

    @Test
    // Description: Convert continues digits
    public void testConvertDigitsToNamesInSubstringS2() {
        mystring.setString("abc416d");
        mystring.convertDigitsToNamesInSubstring(2,7);
        assertEquals("abcfouronesixd", mystring.getString());
    }

    @Test(expected = NullPointerException.class)
    // Description: NullPointerException
    public void testConvertDigitsToNamesInSubstringS3() {
        mystring.setString(null);
        mystring.convertDigitsToNamesInSubstring(1,5);
    }

    @Test(expected = IllegalArgumentException.class)
    // Description: IllegalArgumentException initialPosition < 1
    public void testConvertDigitsToNamesInSubstringS4() {
        mystring.setString("nul1l");
        mystring.convertDigitsToNamesInSubstring(0,5);
    }

    @Test(expected = IllegalArgumentException.class)
    // Description: IllegalArgumentException initialPosition > finialPosition
    public void testConvertDigitsToNamesInSubstringS5() {
        mystring.setString("nul1l");
        mystring.convertDigitsToNamesInSubstring(5,3);
    }

    @Test(expected = MyIndexOutOfBoundsException.class)
    // Description: MyIndexOutOfBoundsException
    public void testConvertDigitsToNamesInSubstringS6() {
        mystring.setString("nul1l");
        mystring.convertDigitsToNamesInSubstring(3,6);
    }
}

