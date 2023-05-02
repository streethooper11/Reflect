package sender;

import exampleclass.*;
import org.junit.jupiter.api.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

// Use of setIn from Assignment 1
// Tests compare manually running serialize method and running through ObjectCreator with given input
// and make sure the outcome is identical
public class SenderSideTest {

    static Scanner p_input;
    InputStream stream;
    static InputStream original;
    static ObjectCreator main;

    @BeforeAll
    public static void setupAll() {
        original = System.in;
        main = new ObjectCreator();
    }

    public void setupBeforeEach(String filepath) throws FileNotFoundException {
        stream = new FileInputStream(filepath);
        System.setIn(stream);
        p_input = new Scanner(System.in);
    }

    @Test
    public void Serializer_serializeClassPrimitiveTest() throws Exception {
        setupBeforeEach("input1.txt"); // contains inputs to run the test
        String outcome = main.serializeClassPrimitive(p_input);
        ClassPrimitives obj = new ClassPrimitives(3, 1);
        String outcome2 = Serializer.serializeObject(obj);
        Assertions.assertEquals(outcome, outcome2);
    }

    @Test
    public void Serializer_serializeClassObjectTest() throws Exception {
        setupBeforeEach("input2.txt");
        String outcome = main.serializeClassObject(p_input);
        ClassObject obj = new ClassObject(5, 4);
        String outcome2 = Serializer.serializeObject(obj);
        Assertions.assertEquals(outcome, outcome2);
    }

    @Test
    public void Serializer_serializeArrayPrimitiveTest() throws Exception {
        setupBeforeEach("input3.txt");
        String outcome = main.serializeClassArrayPrimitives(p_input);
        int[] ints = new int[]{3, 5, 3, 5, 4, 1, 2, 8, 0, -2};
        String outcome2 = Serializer.serializeObject(new ClassArrayPrimitives(ints));
        Assertions.assertEquals(outcome, outcome2);
    }

    @Test
    public void Serializer_serializeArrayObjectTest() throws Exception {
        setupBeforeEach("input4.txt");
        String outcome = main.serializeClassArrayObjects(p_input);
        ClassPrimitives[] objs = new ClassPrimitives[5];
        objs[0] = new ClassPrimitives(3, 5);
        objs[1] = new ClassPrimitives(3, 5);
        objs[2] = new ClassPrimitives(4, 1);
        objs[3] = new ClassPrimitives(2, 8);
        objs[4] = new ClassPrimitives(0, -2);
        String outcome2 = Serializer.serializeObject(new ClassArrayObjects(objs));
        Assertions.assertEquals(outcome, outcome2);
    }

    @Test
    public void Serializer_serializeCollectionObjectTest() throws Exception {
        setupBeforeEach("input5.txt");
        String outcome = main.serializeClassCollectionObjects(p_input);
        ArrayList<ClassPrimitives> objs = new ArrayList<>();
        objs.add(new ClassPrimitives(1, 9));
        objs.add(new ClassPrimitives(2, 8));
        objs.add(new ClassPrimitives(0, 3));
        objs.add(new ClassPrimitives(-1, -2));
        objs.add(new ClassPrimitives(-4, 8));
        String outcome2 = Serializer.serializeObject(new ClassCollectionObjects(objs));
        Assertions.assertEquals(outcome, outcome2);
    }

    @Test
    public void Serializer_serializeArrayPrimitiveTest_NonpositiveLength() throws Exception {
        // non-positive should be rejected and re-prompted for a correct value
        setupBeforeEach("input6.txt");
        String outcome = main.serializeClassArrayPrimitives(p_input);
        int[] ints = new int[]{1, 2, 3};
        String outcome2 = Serializer.serializeObject(new ClassArrayPrimitives(ints));
        Assertions.assertEquals(outcome, outcome2);
    }

    @Test
    public void Serializer_serializeObjectCircularRef() throws Exception {
        setupBeforeEach("input1.txt");
        String outcome = main.serializeClassCircularRef(p_input);
        ClassCircularOne obj = new ClassCircularOne(3, 1);
        String outcome2 = Serializer.serializeObject(obj);
        Assertions.assertEquals(outcome, outcome2);
    }

    @AfterEach
    public void cleanEach() throws IOException {
        System.setIn(original);
        stream.close();
    }

    @AfterAll
    public static void cleanFinal() {
        p_input.close();
    }
}
