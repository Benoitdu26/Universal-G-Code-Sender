/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.willwinder.universalgcodesender;

import com.google.common.collect.ImmutableList;
import com.willwinder.universalgcodesender.gcode.GcodePreprocessorUtils;
import com.willwinder.universalgcodesender.gcode.GcodePreprocessorUtils.SplitCommand;
import com.willwinder.universalgcodesender.gcode.util.Code;
import static com.willwinder.universalgcodesender.gcode.util.Code.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import static org.assertj.core.api.Assertions.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author wwinder
 */
public class GcodePreprocessorUtilsTest {
    
    public GcodePreprocessorUtilsTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of overrideSpeed method, of class CommUtils.
     */
    @Test
    public void testOverrideSpeed() {
        System.out.println("overrideSpeed");
        String command;
        double speed;
        String expResult;
        String result;

        
        command = "some command F100 blah blah blah";
        speed = 22.5;
        expResult = "some command F22.5 blah blah blah";
        result = GcodePreprocessorUtils.overrideSpeed(command, speed);
        assertEquals(expResult, result);
        
        command = "some command F100.0 blah blah blah";
        result = GcodePreprocessorUtils.overrideSpeed(command, speed);
        assertEquals(expResult, result);
    }

    /**
     * Test of parseComment method, of class GrblUtils.
     */
    @Test
    public void testParseComment() {
        System.out.println("parseComment");
        String command;
        String expResult;
        String result;
        
        command   = "some command ;comment";
        expResult = "comment";
        result = GcodePreprocessorUtils.parseComment(command);
        assertEquals(expResult, result);
        
        command   = "some (comment here) command ;comment";
        expResult = "comment here";
        result = GcodePreprocessorUtils.parseComment(command);
        assertEquals(expResult, result);
    }

    /**
     * Test of truncateDecimals method, of class GcodePreprocessorUtils.
     */
    @Test
    public void testTruncateDecimals() {
        System.out.println("truncateDecimals");
        int length;
        String command;
        String result;
        String expResult;
        
        // Length tests.
        length = 0;
        command = "G1 X0.11111111111111111111";
        expResult = "G1 X0";
        result = GcodePreprocessorUtils.truncateDecimals(length, command);
        assertEquals(expResult, result);
        
        length = 8;
        expResult = "G1 X0.11111111";
        result = GcodePreprocessorUtils.truncateDecimals(length, command);
        assertEquals(expResult, result);
        
        length = 800;
        expResult = command;
        result = GcodePreprocessorUtils.truncateDecimals(length, command);
        assertEquals(expResult, result);
        
        // Rounding tests.
        length = 3;
        command = "G1 X1.5555555";
        expResult = "G1 X1.556";
        result = GcodePreprocessorUtils.truncateDecimals(length, command);
        assertEquals(expResult, result);
        
        length = 0;
        expResult = "G1 X2";
        result = GcodePreprocessorUtils.truncateDecimals(length, command);
        assertEquals(expResult, result);
        
        length = 5;
        command = "G1 X1.99999999";
        expResult = "G1 X2";
        result = GcodePreprocessorUtils.truncateDecimals(length, command);
        assertEquals(expResult, result);
        
        // Multiple hits.
        length = 3;
        command = "G1 X1.23456 Y9.87654 Z104.49443";
        expResult = "G1 X1.235 Y9.877 Z104.494";
        result = GcodePreprocessorUtils.truncateDecimals(length, command);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testParseCodes() {
        System.out.println("parseCodes");
        
        // Basic case, find one gcode.
        List<String> sl = new ArrayList<>();
        sl.add("G0");
        sl.add("X7");
        sl.add("Y5.235235");
        List<String> l = GcodePreprocessorUtils.parseCodes(sl, 'G');
        assertEquals(1, l.size());
        assertEquals("0", l.get(0));
        
        // Find two gcodes.
        sl.add("G20");
        l = GcodePreprocessorUtils.parseCodes(sl, 'G');
        assertEquals(2, l.size());
        assertEquals("0", l.get(0));
        assertEquals("20", l.get(1));
        
        // Find X, mismatched case.
        sl.add("G20");
        l = GcodePreprocessorUtils.parseCodes(sl, 'x');
        assertEquals(1, l.size());
        assertEquals("7", l.get(0));
    }

    @Test
    public void parseCoord() throws Exception {
        List<String> args = ImmutableList.of("G10", "G3", "X100", "y-.5", "Z0.25");
        assertThat(GcodePreprocessorUtils.parseCoord(args, 'x')).isEqualTo(100);
        assertThat(GcodePreprocessorUtils.parseCoord(args, 'y')).isEqualTo(-0.5);
        assertThat(GcodePreprocessorUtils.parseCoord(args, 'z')).isEqualTo(0.25);

        assertThat(GcodePreprocessorUtils.parseCoord(args, 'X')).isEqualTo(100);
        assertThat(GcodePreprocessorUtils.parseCoord(args, 'Y')).isEqualTo(-0.5);
        assertThat(GcodePreprocessorUtils.parseCoord(args, 'Z')).isEqualTo(0.25);
    }

    @Test
    public void extractWord() throws Exception {
        List<String> args = ImmutableList.of("G10", "G3", "X100", "y-.5", "Z0.25");
        assertThat(GcodePreprocessorUtils.extractWord(args, 'x')).isEqualTo("X100");
        assertThat(GcodePreprocessorUtils.extractWord(args, 'y')).isEqualTo("y-.5");
        assertThat(GcodePreprocessorUtils.extractWord(args, 'z')).isEqualTo("Z0.25");

        assertThat(GcodePreprocessorUtils.extractWord(args, 'X')).isEqualTo("X100");
        assertThat(GcodePreprocessorUtils.extractWord(args, 'Y')).isEqualTo("y-.5");
        assertThat(GcodePreprocessorUtils.extractWord(args, 'Z')).isEqualTo("Z0.25");
    }
    
    @Test
    public void testGetGcodes() throws Exception {
        List<String> args = ImmutableList.of("F100", "M30", "G1", "G2", "F100", "G3", "G92.1", "G38.2", "S1300");
        Set<Code> codes = GcodePreprocessorUtils.getGCodes(args);
        assertThat(codes).containsExactlyInAnyOrder(G1, G2, G3, G92_1, G38_2);
    }

    @Test
    public void testExtractMotion() throws Exception {
        assertThat(GcodePreprocessorUtils.extractMotion(G3, "G17 G03 X0 Y12 I0.25 J-0.25 K1.99 F100"))
                .hasFieldOrPropertyWithValue("extracted", "G03X0Y12I0.25J-0.25K1.99")
                .hasFieldOrPropertyWithValue("remainder", "G17F100");

        assertThat(GcodePreprocessorUtils.extractMotion(G1, "G17 G03 X0 Y12 I0.25 J-0.25 K1.99 F100"))
                .isNull();

        assertThat(GcodePreprocessorUtils.extractMotion(G1, ""))
                .isNull();

        assertThat(GcodePreprocessorUtils.extractMotion(G1, "G53 G0 X0"))
                .isNull();

        assertThat(GcodePreprocessorUtils.extractMotion(G1, "G53 G01 X0 F100 S1300"))
                .hasFieldOrPropertyWithValue("extracted", "G53G01X0")
                .hasFieldOrPropertyWithValue("remainder", "F100S1300");

        assertThat(GcodePreprocessorUtils.extractMotion(G3, "G53 G03 X0 F100 S1300"))
                .hasFieldOrPropertyWithValue("extracted", "G03X0")
                .hasFieldOrPropertyWithValue("remainder", "G53F100S1300");
    }
}
