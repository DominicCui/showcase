package edu.gatech.seclass.processfile;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class MyMainTest {

    private ByteArrayOutputStream outStream;
    private ByteArrayOutputStream errStream;
    private PrintStream outOrig;
    private PrintStream errOrig;
    private final Charset charset = StandardCharsets.UTF_8;
    private final String input = "Hello" + System.lineSeparator() +
            "Beatrice" + System.lineSeparator() +
            "albert" + System.lineSeparator() +
            "@#$%" + System.lineSeparator() +
            "#%Albert" + System.lineSeparator() +
            "--’’--911" + System.lineSeparator() +
            "hello";

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp() {
        outStream = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outStream);
        errStream = new ByteArrayOutputStream();
        PrintStream err = new PrintStream(errStream);
        outOrig = System.out;
        errOrig = System.err;
        System.setOut(out);
        System.setErr(err);
    }

    @After
    public void tearDown() {
        System.setOut(outOrig);
        System.setErr(errOrig);
    }

    /*
     *  TEST UTILITIES
     */

    // Create File Utility
    private File createTmpFile() throws Exception {
        File tmpfile = temporaryFolder.newFile();
        tmpfile.deleteOnExit();
        return tmpfile;
    }

    // Write File Utility
    private File createInputFile(String input) throws Exception {
        File file = createTmpFile();

        OutputStreamWriter fileWriter =
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);

        fileWriter.write(input);

        fileWriter.close();
        return file;
    }

    private String getFileContent(String filename) {
        String content = null;
        try {
            content = Files.readString(Paths.get(filename), charset);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    private void inFileEdit(File inputFile, String modified) {
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertTrue("Output differs from expected", outStream.toString().isEmpty());
        assertEquals("Unexpected edited file", modified, getFileContent(inputFile.getPath()));
    }

    private void notInFile(File inputFile, String expected) {
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertEquals("Output differs from expected", expected, outStream.toString());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    private void error(File inputFile) {
        assertEquals("Usage: processfile [ -f | -n | -s string | -r string1 string2 | -g | -i | -p  ] FILE", errStream.toString().trim());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    /*
     *   TEST CASES
     */

    // Frame #: Test Case 1 | empty argument
    @Test
    public void processfileTest1() throws Exception {
        String[] args = new String[0];
        Main.main(args);
        assertEquals("Usage: processfile [ -f | -n | -s string | -r string1 string2 | -g | -i | -p  ] FILE", errStream.toString().trim());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
    }

    // Frame #: Test Case 2 | -s
    @Test
    public void processfileTest2() throws Exception {
        File inputFile = createInputFile(input);
        String[] args = {"-s", inputFile.getPath()};
        Main.main(args);
        assertEquals("Usage: processfile [ -f | -n | -s string | -r string1 string2 | -g | -i | -p  ] FILE", errStream.toString().trim());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: Test Case 3 |  -r string
    @Test
    public void processfileTest3() throws Exception {
        File inputFile = createInputFile(input);
        String[] args = {"-r", "Albert", inputFile.getPath()};
        Main.main(args);
        assertEquals("Usage: processfile [ -f | -n | -s string | -r string1 string2 | -g | -i | -p  ] FILE", errStream.toString().trim());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));

    }

    // Frame #: Test Case 4 |  -g
    @Test
    public void processfileTest4() throws Exception {
        File inputFile = createInputFile(input);
        String[] args = {"-g", inputFile.getPath()};
        Main.main(args);
        assertEquals("Usage: processfile [ -f | -n | -s string | -r string1 string2 | -g | -i | -p  ] FILE", errStream.toString().trim());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: Test Case 5 |  -i
    @Test
    public void processfileTest5() throws Exception {
        File inputFile = createInputFile(input);
        String[] args = {"-i", inputFile.getPath()};
        Main.main(args);
        assertEquals("Usage: processfile [ -f | -n | -s string | -r string1 string2 | -g | -i | -p  ] FILE", errStream.toString().trim());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: Test Case 6 |  double -r with one string
    @Test
    public void processfileTest6() throws Exception {
        File inputFile = createInputFile(input);
        String[] args = {"-r", "Albert", "Dominic", "-r", "120", inputFile.getPath()};
        Main.main(args);
        assertEquals("Usage: processfile [ -f | -n | -s string | -r string1 string2 | -g | -i | -p  ] FILE", errStream.toString().trim());
        assertTrue("Output differs from expected", outStream.toString().isEmpty());
    }

    // Frame #: Test Case 7 |  double -r
    @Test
    public void processfileTest7() throws Exception {
        String expected = "Hello" + System.lineSeparator() +
                "Beatrice" + System.lineSeparator() +
                "albert" + System.lineSeparator() +
                "@#$%" + System.lineSeparator() +
                "#%Albert" + System.lineSeparator() +
                "--’’--120" + System.lineSeparator() +
                "hello";
        File inputFile = createInputFile(input);
        String[] args = {"-r", "Albert", "Dominic", "-r", "911", "120", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertEquals("Output differs from expected", expected, outStream.toString());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: Test Case 8 |  double -s with one empty string
    @Test
    public void processfileTest8() throws Exception {
        File inputFile = createInputFile(input);
        String[] args = {"-s", "Hello", "-s", "", inputFile.getPath()};
        Main.main(args);
        notInFile(inputFile, input);
    }

    // Frame #: Test Case 9 |  double -s
    @Test
    public void processfileTest9() throws Exception {
        String expected = "#%Albert";
        File inputFile = createInputFile(input);
        String[] args = {"-s", "Hello", "-s", "Albert", inputFile.getPath()};
        Main.main(args);
        notInFile(inputFile, expected);
    }

    // Frame #: Test Case 10 |  repeat -p
    @Test
    public void processfileTest10() throws Exception {
        String expected = "##Hello" + System.lineSeparator() +
                "##Beatrice" + System.lineSeparator() +
                "##albert" + System.lineSeparator() +
                "##@#$%" + System.lineSeparator() +
                "###%Albert" + System.lineSeparator() +
                "##--’’--911" + System.lineSeparator() +
                "##hello";
        File inputFile = createInputFile(input);
        String[] args = {"-p", "--", "-p", "##", inputFile.getPath()};
        Main.main(args);
        notInFile(inputFile, expected);
    }

    // Frame #: Test Case 11 |  no file argument
    @Test
    public void processfileTest11() throws Exception {
        File inputFile = createInputFile(input);
        String[] args = {"processfile"};
        Main.main(args);
        assertEquals("Usage: processfile [ -f | -n | -s string | -r string1 string2 | -g | -i | -p  ] FILE", errStream.toString().trim());
        assertTrue("Output differs from expected", outStream.toString().isEmpty());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: Test Case 12 |  empty option flags
    @Test
    public void processfileTest12() throws Exception {
        File inputFile = createInputFile(input);
        String[] args = {inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertEquals("Output differs from expected", input, outStream.toString());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: Test Case 13 |  -p string
    @Test
    public void processfileTest13() throws Exception {
        String expected = "##Hello" + System.lineSeparator() +
                "##Beatrice" + System.lineSeparator() +
                "##albert" + System.lineSeparator() +
                "##@#$%" + System.lineSeparator() +
                "###%Albert" + System.lineSeparator() +
                "##--’’--911" + System.lineSeparator() +
                "##hello";
        File inputFile = createInputFile(input);
        String[] args = {"-p", "##", inputFile.getPath()};
        Main.main(args);
        notInFile(inputFile, expected);
    }

    // Frame #: Test Case 14 |  -n
    @Test
    public void processfileTest14() throws Exception {
        String input = "0123456789" + System.lineSeparator() + "abcdefghi";
        String expected = "1 0123456789" + System.lineSeparator() + "2 abcdefghi";

        File inputFile = createInputFile(input);
        String[] args = {"-n", inputFile.getPath()};
        Main.main(args);
        assertTrue("Unexpected stderr output", errStream.toString().isEmpty());
        assertEquals("Output differs from expected", expected, outStream.toString());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: Test Case 15 |  -n -p string
    @Test
    public void processfileTest15() throws Exception {
        String expected = "1 ##Hello" + System.lineSeparator() +
                "2 ##Beatrice" + System.lineSeparator() +
                "3 ##albert" + System.lineSeparator() +
                "4 ##@#$%" + System.lineSeparator() +
                "5 ###%Albert" + System.lineSeparator() +
                "6 ##--’’--911" + System.lineSeparator() +
                "7 ##hello";
        File inputFile = createInputFile(input);
        String[] args = {"-n", "-p", "##", inputFile.getPath()};
        Main.main(args);
        notInFile(inputFile, expected);
    }

    // Frame #: Test Case 16 |  -f
    @Test
    public void processfileTest16() throws Exception {
        File inputFile = createInputFile(input);
        String[] args = {"-f", inputFile.getPath()};
        Main.main(args);
        inFileEdit(inputFile, input);
    }

    // Frame #: Test Case 17 |  -f -p string
    @Test
    public void processfileTest17() throws Exception {
        String modified = "##Hello" + System.lineSeparator() +
                "##Beatrice" + System.lineSeparator() +
                "##albert" + System.lineSeparator() +
                "##@#$%" + System.lineSeparator() +
                "###%Albert" + System.lineSeparator() +
                "##--’’--911" + System.lineSeparator() +
                "##hello";
        File inputFile = createInputFile(input);
        String[] args = {"-f", "-p", "##", inputFile.getPath()};
        Main.main(args);
        inFileEdit(inputFile, modified);
    }

    // Frame #: Test Case 18 |  -f -n
    @Test
    public void processfileTest18() throws Exception {
        String modified = "1 Hello" + System.lineSeparator() +
                "2 Beatrice" + System.lineSeparator() +
                "3 albert" + System.lineSeparator() +
                "4 @#$%" + System.lineSeparator() +
                "5 #%Albert" + System.lineSeparator() +
                "6 --’’--911" + System.lineSeparator() +
                "7 hello";
        File inputFile = createInputFile(input);
        String[] args = {"-f", "-n", inputFile.getPath()};
        Main.main(args);
        inFileEdit(inputFile, modified);
    }

    // Frame #: Test Case 19 |  -f -n -p string
    @Test
    public void processfileTest19() throws Exception {
        String modified = "1 ##Hello" + System.lineSeparator() +
                "2 ##Beatrice" + System.lineSeparator() +
                "3 ##albert" + System.lineSeparator() +
                "4 ##@#$%" + System.lineSeparator() +
                "5 ###%Albert" + System.lineSeparator() +
                "6 ##--’’--911" + System.lineSeparator() +
                "7 ##hello";
        File inputFile = createInputFile(input);
        String[] args = {"-f", "-n", "-p", "##", inputFile.getPath()};
        Main.main(args);
        inFileEdit(inputFile, modified);
    }

    // Frame #: Test Case 20 |  -r string string
    @Test
    public void processfileTest20() throws Exception {
        String expected = "Hello" + System.lineSeparator() +
                "Beatrice" + System.lineSeparator() +
                "albert" + System.lineSeparator() +
                "@#$%" + System.lineSeparator() +
                "#%Dominic" + System.lineSeparator() +
                "--’’--911" + System.lineSeparator() +
                "hello";
        File inputFile = createInputFile(input);
        String[] args = {"-r", "Albert", "Dominic", inputFile.getPath()};
        Main.main(args);
        notInFile(inputFile, expected);
    }

    // Frame #: Test Case 21 |  -r string string -p string
    @Test
    public void processfileTest21() throws Exception {
        String expected = "##Hello" + System.lineSeparator() +
                "##Beatrice" + System.lineSeparator() +
                "##albert" + System.lineSeparator() +
                "##@#$%" + System.lineSeparator() +
                "###%Albert" + System.lineSeparator() +
                "##--’’--911" + System.lineSeparator() +
                "##Hi";
        File inputFile = createInputFile(input);
        String[] args = {"-r", "hello", "Hi", "-p", "##", inputFile.getPath()};
        Main.main(args);
        notInFile(inputFile, expected);
    }

    // Frame #: Test Case 22 |  -r string string -n
    @Test
    public void processfileTest22() throws Exception {
        String expected = "1 Hello" + System.lineSeparator() +
                "2 Beatrice" + System.lineSeparator() +
                "3 dominic" + System.lineSeparator() +
                "4 @#$%" + System.lineSeparator() +
                "5 #%Albert" + System.lineSeparator() +
                "6 --’’--911" + System.lineSeparator() +
                "7 hello";
        File inputFile = createInputFile(input);
        String[] args = {"-r", "albert", "dominic", "-n", inputFile.getPath()};
        Main.main(args);
        notInFile(inputFile, expected);
    }

    // Frame #: Test Case 23 |  -r string string -n -p string
    @Test
    public void processfileTest23() throws Exception {
        String expected = "1 ##Hello" + System.lineSeparator() +
                "2 ##dominic" + System.lineSeparator() +
                "3 ##albert" + System.lineSeparator() +
                "4 ##@#$%" + System.lineSeparator() +
                "5 ###%Albert" + System.lineSeparator() +
                "6 ##--’’--911" + System.lineSeparator() +
                "7 ##hello";
        File inputFile = createInputFile(input);
        String[] args = {"-r", "Beatrice", "dominic", "-n", "-p", "##", inputFile.getPath()};
        Main.main(args);
        notInFile(inputFile, expected);
    }

    // Frame #: Test Case 24 |  -r string string -f
    @Test
    public void processfileTest24() throws Exception {
        String modified = "Hello" + System.lineSeparator() +
                "dominic" + System.lineSeparator() +
                "albert" + System.lineSeparator() +
                "@#$%" + System.lineSeparator() +
                "#%Albert" + System.lineSeparator() +
                "--’’--911" + System.lineSeparator() +
                "hello";
        File inputFile = createInputFile(input);
        String[] args = {"-r", "Beatrice", "dominic", "-f", inputFile.getPath()};
        Main.main(args);
        inFileEdit(inputFile, modified);
    }

    // Frame #: Test Case 25 |  -r string string -f -p string
    @Test
    public void processfileTest25() throws Exception {
        String modified = "##Hello" + System.lineSeparator() +
                "##Beatrice" + System.lineSeparator() +
                "##albert" + System.lineSeparator() +
                "##@#$%" + System.lineSeparator() +
                "###%Albert" + System.lineSeparator() +
                "##--’’--911" + System.lineSeparator() +
                "##Hi";
        File inputFile = createInputFile(input);
        String[] args = {"-r", "hello", "Hi", "-f", "-p", "##", inputFile.getPath()};
        Main.main(args);
        inFileEdit(inputFile, modified);
    }

    // Frame #: Test Case 26 |  -r string string -f -n
    @Test
    public void processfileTest26() throws Exception {
        String modified = "1 Hello" + System.lineSeparator() +
                "2 Beatrice" + System.lineSeparator() +
                "3 dominic" + System.lineSeparator() +
                "4 @#$%" + System.lineSeparator() +
                "5 #%Albert" + System.lineSeparator() +
                "6 --’’--911" + System.lineSeparator() +
                "7 hello";
        File inputFile = createInputFile(input);
        String[] args = {"-r", "albert", "dominic", "-f", "-n", inputFile.getPath()};
        Main.main(args);
        inFileEdit(inputFile, modified);
    }

    // Frame #: Test Case 27 |  -r string string -f -n -p string
    @Test
    public void processfileTest27() throws Exception {
        String modified = "1 ##Hello" + System.lineSeparator() +
                "2 ##dominic" + System.lineSeparator() +
                "3 ##albert" + System.lineSeparator() +
                "4 ##@#$%" + System.lineSeparator() +
                "5 ###%Albert" + System.lineSeparator() +
                "6 ##--’’--911" + System.lineSeparator() +
                "7 ##hello";
        File inputFile = createInputFile(input);
        String[] args = {"-r", "Beatrice", "dominic", "-f", "-n", "-p", "##", inputFile.getPath()};
        Main.main(args);
        inFileEdit(inputFile, modified);
    }

    // Frame #: Test Case 28 |  -r string string -g
    @Test
    public void processfileTest28() throws Exception {
        String expected = "Hi" + System.lineSeparator() +
                "Beatrice" + System.lineSeparator() +
                "albert" + System.lineSeparator() +
                "@#$%" + System.lineSeparator() +
                "#%Albert" + System.lineSeparator() +
                "--’’--911" + System.lineSeparator() +
                "hi";
        File inputFile = createInputFile(input);
        String[] args = {"-r", "ello", "i", "-g", inputFile.getPath()};
        Main.main(args);
        notInFile(inputFile, expected);
    }
//
//    // Frame #: Test Case 29 |  -r string string -g -p string
//    @Test
//    public void processfileTest29() throws Exception {
//        String expected = "##Hi" + System.lineSeparator() +
//                "##Beatrice" + System.lineSeparator() +
//                "##albert" + System.lineSeparator() +
//                "##@#$%" + System.lineSeparator() +
//                "###%Albert" + System.lineSeparator() +
//                "##--’’--911" + System.lineSeparator() +
//                "##hi";
//        File inputFile = createInputFile(input);
//        String[] args = {"-r", "ello", "i", "-g","-p", "##", inputFile.getPath()};
//        Main.main(args);
//        notInFile(inputFile, expected);
//    }
//
//    // Frame #: Test Case 30 |  -r string string -g -n
//    @Test
//    public void processfileTest30() throws Exception {
//        String expected = "1 Hi" + System.lineSeparator() +
//                "2 Beatrice" + System.lineSeparator() +
//                "3 albert" + System.lineSeparator() +
//                "4 @#$%" + System.lineSeparator() +
//                "5 #%Albert" + System.lineSeparator() +
//                "6 --’’--911" + System.lineSeparator() +
//                "7 hi";
//        File inputFile = createInputFile(input);
//        String[] args = {"-r", "ello", "i", "-g","-n", inputFile.getPath()};
//        Main.main(args);
//        notInFile(inputFile, expected);
//    }
//
//    // Frame #: Test Case 31 |  -r string string -g -n -p string
//    @Test
//    public void processfileTest31() throws Exception {
//        String expected = "1 ##Hi" + System.lineSeparator() +
//                "2 ##Beatrice" + System.lineSeparator() +
//                "3 ##albert" + System.lineSeparator() +
//                "4 ##@#$%" + System.lineSeparator() +
//                "5 ###%Albert" + System.lineSeparator() +
//                "6 ##--’’--911" + System.lineSeparator() +
//                "7 ##hi";
//        File inputFile = createInputFile(input);
//        String[] args = {"-r", "ello", "i", "-g","-n","-p","##", inputFile.getPath()};
//        Main.main(args);
//        notInFile(inputFile, expected);
//    }
//
//    // Frame #: Test Case 32 |  -r string string -g -f
//    @Test
//    public void processfileTest32() throws Exception {
//        String modified = "Hi" + System.lineSeparator() +
//                "Beatrice" + System.lineSeparator() +
//                "albert" + System.lineSeparator() +
//                "@#$%" + System.lineSeparator() +
//                "#%Albert" + System.lineSeparator() +
//                "--’’--911" + System.lineSeparator() +
//                "hi";
//        File inputFile = createInputFile(input);
//        String[] args = {"-r", "ello", "i", "-g","-f", inputFile.getPath()};
//        Main.main(args);
//        inFileEdit(inputFile, modified);
//    }
//
//    // Frame #: Test Case 33 |  -r string string -g -f -p string
//    @Test
//    public void processfileTest33() throws Exception {
//        String modified = "##Hi" + System.lineSeparator() +
//                "##Beatrice" + System.lineSeparator() +
//                "##albert" + System.lineSeparator() +
//                "##@#$%" + System.lineSeparator() +
//                "###%Albert" + System.lineSeparator() +
//                "##--’’--911" + System.lineSeparator() +
//                "##hi";
//        File inputFile = createInputFile(input);
//        String[] args = {"-r", "ello", "i", "-g","-f", "-p", "##", inputFile.getPath()};
//        Main.main(args);
//        inFileEdit(inputFile, modified);
//    }
//
//    // Frame #: Test Case 34 |  -r string string -g -f -n
//    @Test
//    public void processfileTest34() throws Exception {
//        String modified = "1 Hi" + System.lineSeparator() +
//                "2 Beatrice" + System.lineSeparator() +
//                "3 albert" + System.lineSeparator() +
//                "4 @#$%" + System.lineSeparator() +
//                "5 #%Albert" + System.lineSeparator() +
//                "6 --’’--911" + System.lineSeparator() +
//                "7 hi";
//        File inputFile = createInputFile(input);
//        String[] args = {"-r", "ello", "i", "-g","-f","-n", inputFile.getPath()};
//        Main.main(args);
//        inFileEdit(inputFile, modified);
//    }
//
//    // Frame #: Test Case 35 |  -r string string -g -f -n -p string
//    @Test
//    public void processfileTest35() throws Exception {
//        String modified = "1 ##Hi" + System.lineSeparator() +
//                "2 ##Beatrice" + System.lineSeparator() +
//                "3 ##albert" + System.lineSeparator() +
//                "4 ##@#$%" + System.lineSeparator() +
//                "5 ###%Albert" + System.lineSeparator() +
//                "6 ##--’’--911" + System.lineSeparator() +
//                "7 ##hi";
//        File inputFile = createInputFile(input);
//        String[] args = {"-r", "ello", "i", "-g","-f","-n","-p","##", inputFile.getPath()};
//        Main.main(args);
//        inFileEdit(inputFile, modified);
//    }

    // Frame #: Test Case 36 |  -s string
    @Test
    public void processfileTest36() throws Exception {
        String expected = "Hello";
        File inputFile = createInputFile(input);
        String[] args = {"-s", "Hello", inputFile.getPath()};
        Main.main(args);
        notInFile(inputFile, expected);
    }

    // Frame #: Test Case 37 |  -s string -p string
    @Test
    public void processfileTest37() throws Exception {
        String expected = "##Hello";
        File inputFile = createInputFile(input);
        String[] args = {"-s", "Hello", "-p", "##", inputFile.getPath()};
        Main.main(args);
        notInFile(inputFile, expected);
    }

    // Frame #: Test Case 38 |  -s string -n
    @Test
    public void processfileTest38() throws Exception {
        String expected = "1 Hello";
        File inputFile = createInputFile(input);
        String[] args = {"-s", "Hello", "-n", inputFile.getPath()};
        Main.main(args);
        notInFile(inputFile, expected);
    }

    // Frame #: Test Case 39 |  -s string -n -p string
    @Test
    public void processfileTest39() throws Exception {
        String expected = "1 ##Hello";
        File inputFile = createInputFile(input);
        String[] args = {"-s", "Hello", "-n", "-p", "##", inputFile.getPath()};
        Main.main(args);
        notInFile(inputFile, expected);
    }

    // Frame #: Test Case 40 |  -s string -f
    @Test
    public void processfileTest40() throws Exception {
        String modified = "Hello";
        File inputFile = createInputFile(input);
        String[] args = {"-s", "Hello", "-f", inputFile.getPath()};
        Main.main(args);
        inFileEdit(inputFile, modified);
    }

    // Frame #: Test Case 41 |  -s string -f -p string
    @Test
    public void processfileTest41() throws Exception {
        String modified = "##Hello";
        File inputFile = createInputFile(input);
        String[] args = {"-s", "Hello", "-f", "-p", "##", inputFile.getPath()};
        Main.main(args);
        inFileEdit(inputFile, modified);
    }

    // Frame #: Test Case 42 |  -s string -f -n
    @Test
    public void processfileTest42() throws Exception {
        String modified = "1 Hello";
        File inputFile = createInputFile(input);
        String[] args = {"-s", "Hello", "-f", "-n", inputFile.getPath()};
        Main.main(args);
        inFileEdit(inputFile, modified);
    }

    // Frame #: Test Case 43 |  -s string -f -n -p string
    @Test
    public void processfileTest43() throws Exception {
        String modified = "1 ##Hello";
        File inputFile = createInputFile(input);
        String[] args = {"-s", "Hello", "-f", "-n", "-p", "##", inputFile.getPath()};
        Main.main(args);
        inFileEdit(inputFile, modified);
    }

    // Frame #: Test Case 44 |  -s string -r string string
    @Test
    public void processfileTest44() throws Exception {
        String expected = "Hi";
        File inputFile = createInputFile(input);
        String[] args = {"-s", "Hello", "-r", "Hello", "Hi", inputFile.getPath()};
        Main.main(args);
        notInFile(inputFile, expected);
    }

    // Frame #: Test Case 45 |  -s string -r string string -p string
    @Test
    public void processfileTest45() throws Exception {
        String expected = "##Hi";
        File inputFile = createInputFile(input);
        String[] args = {"-s", "Hello", "-r", "Hello", "Hi", "-p", "##", inputFile.getPath()};
        Main.main(args);
        notInFile(inputFile, expected);
    }

    // Frame #: Test Case 46 |  -s string -r string string -n
    @Test
    public void processfileTest46() throws Exception {
        String expected = "1 Hi";
        File inputFile = createInputFile(input);
        String[] args = {"-s", "Hello", "-r", "Hello", "Hi", "-n", inputFile.getPath()};
        Main.main(args);
        notInFile(inputFile, expected);
    }

    // Frame #: Test Case 47 |  -s string -r string string -n -p string
    @Test
    public void processfileTest47() throws Exception {
        String expected = "1 ##Hi";
        File inputFile = createInputFile(input);
        String[] args = {"-s", "Hello", "-r", "Hello", "Hi", "-n", "-p", "##", inputFile.getPath()};
        Main.main(args);
        notInFile(inputFile, expected);
    }

    // Frame #: Test Case 48 |  -s string -r string string -f
    @Test
    public void processfileTest48() throws Exception {
        String modified = "Hi";
        File inputFile = createInputFile(input);
        String[] args = {"-s", "Hello", "-r", "Hello", "Hi", "-f", inputFile.getPath()};
        Main.main(args);
        inFileEdit(inputFile, modified);
    }

    // Frame #: Test Case 49 |  -s string -r string string -f -p string
    @Test
    public void processfileTest49() throws Exception {
        String modified = "##Hi";
        File inputFile = createInputFile(input);
        String[] args = {"-s", "Hello", "-r", "Hello", "Hi", "-f", "-p", "##", inputFile.getPath()};
        Main.main(args);
        inFileEdit(inputFile, modified);
    }

    // Frame #: Test Case 50 |  -s string -r string string -f -n
    @Test
    public void processfileTest50() throws Exception {
        String modified = "1 Hi";
        File inputFile = createInputFile(input);
        String[] args = {"-s", "Hello", "-r", "Hello", "Hi", "-f", "-n", inputFile.getPath()};
        Main.main(args);
        inFileEdit(inputFile, modified);
    }

    // Frame #: Test Case 51 |  -s string -r string string -f -n -p string
    @Test
    public void processfileTest51() throws Exception {
        String modified = "1 ##Hi";
        File inputFile = createInputFile(input);
        String[] args = {"-s", "Hello", "-r", "Hello", "Hi", "-f", "-n", "-p", "##", inputFile.getPath()};
        Main.main(args);
        inFileEdit(inputFile, modified);
    }

//    // Frame #: Test Case 52 |  -s string -r string string -i
//    @Test
//    public void processfileTest52() throws Exception {
//        String expected = "Hi" + System.lineSeparator() +
//                    "Hi";
//        File inputFile = createInputFile(input);
//        String[] args = {"-s", "Hello", "-r","Hello","Hi","-i",  inputFile.getPath()};
//        Main.main(args);
//        notInFile(inputFile, expected);
//    }
//
//    // Frame #: Test Case 53 |  -s string -r string string -i -p string
//    @Test
//    public void processfileTest53() throws Exception {
//        String expected = "##Hi" + System.lineSeparator() +
//                "##Hi";
//        File inputFile = createInputFile(input);
//        String[] args = {"-s", "Hello", "-r","Hello","Hi","-i","-p","##",  inputFile.getPath()};
//        Main.main(args);
//        notInFile(inputFile, expected);
//    }
//
//    // Frame #: Test Case 54 |  -s string -r string string -i -n
//    @Test
//    public void processfileTest54() throws Exception {
//        String expected = "1 Hi" + System.lineSeparator() +
//                "2 Hi";
//        File inputFile = createInputFile(input);
//        String[] args = {"-s", "Hello", "-r","Hello","Hi","-i","-n", inputFile.getPath()};
//        Main.main(args);
//        notInFile(inputFile, expected);
//    }
//
//    // Frame #: Test Case 55 |  -s string -r string string -i -n -p string
//    @Test
//    public void processfileTest55() throws Exception {
//        String expected = "1 ##Hi" + System.lineSeparator() +
//                "2 ##Hi";
//        File inputFile = createInputFile(input);
//        String[] args = {"-s", "Hello", "-r","Hello","Hi","-i","-n","-p","##",  inputFile.getPath()};
//        Main.main(args);
//        notInFile(inputFile, expected);
//    }
//
//    // Frame #: Test Case 56 |  -s string -r string string -i -f
//    @Test
//    public void processfileTest56() throws Exception {
//        String modified = "Hi" + System.lineSeparator() +
//                "Hi";
//        File inputFile = createInputFile(input);
//        String[] args = {"-s", "Hello", "-r","Hello","Hi","-i","-f",  inputFile.getPath()};
//        Main.main(args);
//        inFileEdit(inputFile, modified);
//    }
//
//    // Frame #: Test Case 57 |  -s string -r string string -i -f -p string
//    @Test
//    public void processfileTest57() throws Exception {
//        String modified = "##Hi" + System.lineSeparator() +
//                "##Hi";
//        File inputFile = createInputFile(input);
//        String[] args = {"-s", "Hello", "-r","Hello","Hi","-i","-f","-p","##",  inputFile.getPath()};
//        Main.main(args);
//        inFileEdit(inputFile, modified);
//    }
//
//    // Frame #: Test Case 58 |  -s string -r string string -i -f -n
//    @Test
//    public void processfileTest58() throws Exception {
//        String modified = "1 Hi" + System.lineSeparator() +
//                "2 Hi";
//        File inputFile = createInputFile(input);
//        String[] args = {"-s", "Hello", "-r","Hello","Hi","-i","-f","-n", inputFile.getPath()};
//        Main.main(args);
//        inFileEdit(inputFile, modified);
//    }
//
//    // Frame #: Test Case 59 |  -s string -r string string -i -f -n -p string
//    @Test
//    public void processfileTest59() throws Exception {
//        String modified = "1 ##Hi" + System.lineSeparator() +
//                "2 ##Hi";
//        File inputFile = createInputFile(input);
//        String[] args = {"-s", "Hello", "-r","Hello","Hi","-i","-f","-n","-p","##",  inputFile.getPath()};
//        Main.main(args);
//        inFileEdit(inputFile, modified);
//    }
//
//    // Frame #: Test Case 60 |  -s string -r string string -g
//    @Test
//    public void processfileTest60() throws Exception {
//        String expected = "alHi" + System.lineSeparator() +
//                    "#%AlHi";
//        File inputFile = createInputFile(input);
//        String[] args = {"-s", "bert", "-r","bert","Hi","-g", inputFile.getPath()};
//        Main.main(args);
//        notInFile(inputFile, expected);
//    }
//
//    // Frame #: Test Case 61 |  -s string -r string string -g -p string
//    @Test
//    public void processfileTest61() throws Exception {
//        String expected = "##alHi" + System.lineSeparator() +
//                "###%AlHi";
//        File inputFile = createInputFile(input);
//        String[] args = {"-s", "bert", "-r","bert","Hi","-g","-p","##", inputFile.getPath()};
//        Main.main(args);
//        notInFile(inputFile, expected);
//    }
//
//    // Frame #: Test Case 62 |  -s string -r string string -g -n
//    @Test
//    public void processfileTest62() throws Exception {
//        String expected = "1 alHi" + System.lineSeparator() +
//                "2 #%AlHi";
//        File inputFile = createInputFile(input);
//        String[] args = {"-s", "bert", "-r","bert","Hi","-g","-n", inputFile.getPath()};
//        Main.main(args);
//        notInFile(inputFile, expected);
//    }
//
//    // Frame #: Test Case 63 |  -s string -r string string -g -n -p string
//    @Test
//    public void processfileTest63() throws Exception {
//        String expected = "1 ##alHi" + System.lineSeparator() +
//                "2 ###%AlHi";
//        File inputFile = createInputFile(input);
//        String[] args = {"-s", "bert", "-r","bert","Hi","-g","-n","-p","##", inputFile.getPath()};
//        Main.main(args);
//        notInFile(inputFile, expected);
//    }
//
//    // Frame #: Test Case 64 |  -s string -r string string -g -f
//    @Test
//    public void processfileTest64() throws Exception {
//        String modified = "alHi" + System.lineSeparator() +
//                "#%AlHi";
//        File inputFile = createInputFile(input);
//        String[] args = {"-s", "bert", "-r","bert","Hi","-g", "-f", inputFile.getPath()};
//        Main.main(args);
//        inFileEdit(inputFile, modified);
//    }
//
//    // Frame #: Test Case 65 |  -s string -r string string -g -f -p string
//    @Test
//    public void processfileTest65() throws Exception {
//        String modified = "##Hi" + System.lineSeparator() +
//                "###%Albert";
//        File inputFile = createInputFile(input);
//        String[] args = {"-s", "bert", "-r","albert","Hi","-g", "-f","-p","##", inputFile.getPath()};
//        Main.main(args);
//        inFileEdit(inputFile, modified);
//    }
//
//    // Frame #: Test Case 66 |  -s string -r string string -g -f -n
//    @Test
//    public void processfileTest66() throws Exception {
//        String modified = "1 Hi" + System.lineSeparator() +
//                "2 #%Albert";
//        File inputFile = createInputFile(input);
//        String[] args = {"-s", "bert", "-r","albert","Hi","-g", "-f","-n", inputFile.getPath()};
//        Main.main(args);
//        inFileEdit(inputFile, modified);
//    }
//
//    // Frame #: Test Case 67 |  -s string -r string string -g -f -n -p string
//    @Test
//    public void processfileTest67() throws Exception {
//        String modified = "1 ##Hi" + System.lineSeparator() +
//                "2 ###%Albert";
//        File inputFile = createInputFile(input);
//        String[] args = {"-s", "bert", "-r","albert","Hi","-g", "-f","-n","-p","##", inputFile.getPath()};
//        Main.main(args);
//        inFileEdit(inputFile, modified);
//    }
//
//    // Frame #: Test Case 68 |  -s string -r string string -g -i
//    @Test
//    public void processfileTest68() throws Exception {
//        String expected = "Hi" + System.lineSeparator() +
//                "#%Hi";
//        File inputFile = createInputFile(input);
//        String[] args = {"-s", "albert", "-r","albert","Hi","-g", "-i", inputFile.getPath()};
//        Main.main(args);
//        notInFile(inputFile, expected);
//    }
//
//    // Frame #: Test Case 69 |  -s string -r string string -g -i -p string
//    @Test
//    public void processfileTest69() throws Exception {
//        String expected = "##Hi" + System.lineSeparator() +
//                "###%Hi";
//        File inputFile = createInputFile(input);
//        String[] args = {"-s", "albert", "-r","albert","Hi","-g", "-i", "-p","##", inputFile.getPath()};
//        Main.main(args);
//        notInFile(inputFile, expected);
//    }
//
//    // Frame #: Test Case 70 |  -s string -r string string -g -i -n
//    @Test
//    public void processfileTest70() throws Exception {
//        String expected = "1 Hi" + System.lineSeparator() +
//                "2 #%Hi";
//        File inputFile = createInputFile(input);
//        String[] args = {"-s", "albert", "-r","albert","Hi","-g", "-i", "-n", inputFile.getPath()};
//        Main.main(args);
//        notInFile(inputFile, expected);
//    }
//
//    // Frame #: Test Case 71 |  -s string -r string string -g -i -n -p string
//    @Test
//    public void processfileTest71() throws Exception {
//        String expected = "1 ##Hi" + System.lineSeparator() +
//                "2 ###%Hi";
//        File inputFile = createInputFile(input);
//        String[] args = {"-s", "albert", "-r","albert","Hi","-g", "-i", "-n","-p","##", inputFile.getPath()};
//        Main.main(args);
//        notInFile(inputFile, expected);
//    }
//
//    // Frame #: Test Case 72 |  -s string -r string string -g -i -f
//    @Test
//    public void processfileTest72() throws Exception {
//        String modified = "Hi" + System.lineSeparator() +
//                "#%Hi";
//        File inputFile = createInputFile(input);
//        String[] args = {"-s", "albert", "-r","albert","Hi","-g", "-i", "-f", inputFile.getPath()};
//        Main.main(args);
//        inFileEdit(inputFile, modified);
//    }
//
//    // Frame #: Test Case 73 |  -s string -r string string -g -i -f -p string
//    @Test
//    public void processfileTest73() throws Exception {
//        String modified = "##Hi" + System.lineSeparator() +
//                "###%Hi";
//        File inputFile = createInputFile(input);
//        String[] args = {"-s", "albert", "-r","albert","Hi","-g", "-i", "-f","-p","##", inputFile.getPath()};
//        Main.main(args);
//        inFileEdit(inputFile, modified);
//    }
//
//    // Frame #: Test Case 74 |  -s string -r string string -g -i -f -n
//    @Test
//    public void processfileTest74() throws Exception {
//        String modified = "1 Hi" + System.lineSeparator() +
//                "2 #%Hi";
//        File inputFile = createInputFile(input);
//        String[] args = {"-s", "albert", "-r","albert","Hi","-g", "-i", "-f","-n", inputFile.getPath()};
//        Main.main(args);
//        inFileEdit(inputFile, modified);
//    }

    // Frame #: Test Case 75 |  -s string -r string string -g -i -f -n -p string   // count 28/50
    @Test
    public void processfileTest75() throws Exception {
        String modified = "1 ##Hi" + System.lineSeparator() +
                "2 ###%Hi";
        File inputFile = createInputFile(input);
        String[] args = {"-s", "albert", "-r", "albert", "Hi", "-g", "-i", "-f", "-n", "-p", "##", inputFile.getPath()};
        Main.main(args);
        inFileEdit(inputFile, modified);
    }

    // Frame #: Test Case 76 |  -s string -i
    @Test
    public void processfileTest76() throws Exception {
        String expected = "Hello" + System.lineSeparator() +
                "hello";
        File inputFile = createInputFile(input);
        String[] args = {"-s", "Hello", "-i", inputFile.getPath()};
        Main.main(args);
        notInFile(inputFile, expected);
    }

    // Frame #: Test Case 77 |  -n -i   not count cover
    @Test
    public void processfileTest77() throws Exception {
        String input = "0123456789" + System.lineSeparator() + "abcdefghi";
        String expected = "1 0123456789" + System.lineSeparator() + "2 abcdefghi";

        File inputFile = createInputFile(input);
        String[] args = {"-n", "-i", inputFile.getPath()};
        Main.main(args);
        assertEquals("Usage: processfile [ -f | -n | -s string | -r string1 string2 | -g | -i | -p  ] FILE", errStream.toString().trim());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: Test Case 13 |  -p string -i    not count cover
    @Test
    public void processfileTest78() throws Exception {
        String expected = "##Hello" + System.lineSeparator() +
                "##Beatrice" + System.lineSeparator() +
                "##albert" + System.lineSeparator() +
                "##@#$%" + System.lineSeparator() +
                "###%Albert" + System.lineSeparator() +
                "##--’’--911" + System.lineSeparator() +
                "##hello";
        File inputFile = createInputFile(input);
        String[] args = {"-p", "##", "-i", inputFile.getPath()};
        Main.main(args);
        assertEquals("Usage: processfile [ -f | -n | -s string | -r string1 string2 | -g | -i | -p  ] FILE", errStream.toString().trim());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: Test Case 79 |  -r string string -i
    @Test
    public void processfileTest79() throws Exception {
        String expected = "Hello" + System.lineSeparator() +
                "Beatrice" + System.lineSeparator() +
                "Dominic" + System.lineSeparator() +
                "@#$%" + System.lineSeparator() +
                "#%Dominic" + System.lineSeparator() +
                "--’’--911" + System.lineSeparator() +
                "hello";
        File inputFile = createInputFile(input);
        String[] args = {"-r", "Albert", "Dominic", "-i", inputFile.getPath()};
        Main.main(args);
        notInFile(inputFile, expected);
    }

    // Frame #: Test Case 80 |  -f -i   not count cover
    @Test
    public void processfileTest80() throws Exception {
        File inputFile = createInputFile(input);
        String[] args = {"-f", "-i", inputFile.getPath()};
        Main.main(args);
        assertEquals("Usage: processfile [ -f | -n | -s string | -r string1 string2 | -g | -i | -p  ] FILE", errStream.toString().trim());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: Test Case 81 |  -g -i
    @Test
    public void processfileTest81() throws Exception {
        File inputFile = createInputFile(input);
        String[] args = {"-g", "-i", inputFile.getPath()};
        Main.main(args);
        assertEquals("Usage: processfile [ -f | -n | -s string | -r string1 string2 | -g | -i | -p  ] FILE", errStream.toString().trim());
        assertTrue("Unexpected stdout output", outStream.toString().isEmpty());
        assertEquals("input file modified", input, getFileContent(inputFile.getPath()));
    }

    // Frame #: Test Case 82 |  -r string string -g -i
    @Test
    public void processfileTest82() throws Exception {
        String expected = "Hi" + System.lineSeparator() +
                "Beatrice" + System.lineSeparator() +
                "albert" + System.lineSeparator() +
                "@#$%" + System.lineSeparator() +
                "#%Albert" + System.lineSeparator() +
                "--’’--911" + System.lineSeparator() +
                "hi";
        File inputFile = createInputFile(input);
        String[] args = {"-r", "ello", "i", "-g", "-i", inputFile.getPath()};
        Main.main(args);
        notInFile(inputFile, expected);
    }

    // Frame #: Test Case 83 |  -s string -g
    @Test
    public void processfileTest83() throws Exception {
        String expected = "Hello";
        File inputFile = createInputFile(input);
        String[] args = {"-s", "Hello", "-g", inputFile.getPath()};
        Main.main(args);
        error(inputFile);
    }

    // Frame #: Test Case 84 |  -s string -g -i
    @Test
    public void processfileTest84() throws Exception {
        String expected = "Hello" + System.lineSeparator() +
                "hello";
        File inputFile = createInputFile(input);
        String[] args = {"-s", "Hello", "-g", "-i", inputFile.getPath()};
        Main.main(args);
        error(inputFile);
    }

    // Frame #: Test Case 85 |  -s string -p string -i
    @Test
    public void processfileTest85() throws Exception {
        String expected = "##Hello" + System.lineSeparator() +
                "##hello";
        File inputFile = createInputFile(input);
        String[] args = {"-s", "Hello", "-p", "##", "-i", inputFile.getPath()};
        Main.main(args);
        notInFile(inputFile, expected);
    }

    // Frame #: Test Case 86 |  -s string -n -i
    @Test
    public void processfileTest86() throws Exception {
        String expected = "1 Hello" + System.lineSeparator() +
                "2 hello";
        File inputFile = createInputFile(input);
        String[] args = {"-s", "Hello", "-n", "-i", inputFile.getPath()};
        Main.main(args);
        notInFile(inputFile, expected);
    }

    // Frame #: Test Case 87 |  -s string -f -i
    @Test
    public void processfileTest87() throws Exception {
        String modified = "Hello" + System.lineSeparator() +
                "hello";
        File inputFile = createInputFile(input);
        String[] args = {"-s", "Hello", "-f", "-i", inputFile.getPath()};
        Main.main(args);
        inFileEdit(inputFile, modified);
    }

    // Frame #: Test Case 88 | -p
    @Test
    public void processfileTest88() throws Exception {
        File inputFile = createInputFile(input);
        String[] args = {"-p", inputFile.getPath()};
        Main.main(args);
        error(inputFile);
    }

    // Frame #: Test Case 89 | -r
    @Test
    public void processfileTest89() throws Exception {
        File inputFile = createInputFile(input);
        String[] args = {"-r", inputFile.getPath()};
        Main.main(args);
        error(inputFile);
    }

    // -g -f, -g -n, -g -p, not count
    // sgf, sgn, sgp, gif, gin, gip, gfn ,gfp, gnp  not count
    // rif, rin, rip, ifp, ifn, inp error not count

    // Frame #: Test Case 2 | -s empty
    @Test
    public void processfileTest90() throws Exception {
        File inputFile = createInputFile(input);
        String[] args = {"-s", "", inputFile.getPath()};
        Main.main(args);
        notInFile(inputFile, input);
    }

    // is directory, not valid command
    @Test
    public void processfileTest91() throws Exception {
        File inputFile = createInputFile(input);
        String[] args = {Files.createDirectories(Paths.get("")).toString()};
        Main.main(args);
        error(inputFile);
    }
}