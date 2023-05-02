package receiver;

import exampleclass.*;
import org.junit.jupiter.api.*;
import sender.ObjectCreator;
import sender.Sender;
import sender.Serializer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

// Use of setIn from Assignment 1
public class ReceiverSideTest {

    static ObjectCreator main;

    // test exchanging message
    @Test
    public void SenderAndReceiver_SendAndReceiveStringTest() throws InterruptedException {
        String message = "Hello world!";
        String receivedMessage;
        Thread sendThread = new Thread(new Runnable() {
            public void run() {
                Sender.sendStringOverSocket(message, 50150);
            }
        });
        sendThread.start();
        TimeUnit.SECONDS.sleep(1); // pause so the server can listen
        receivedMessage = Receiver.receiveStringFromSocket("127.0.0.1", 50150);

        sendThread.join();
        Assertions.assertEquals(message, receivedMessage);
    }

    private String receiveStringFromSender(String message) throws InterruptedException {
        Thread sendThread = new Thread(new Runnable() {
            public void run() {
                Sender.sendStringOverSocket(message, 50150);
            }
        });
        sendThread.start();
        TimeUnit.SECONDS.sleep(1); // pause so the server can listen
        String receivedMessage = Receiver.receiveStringFromSocket("127.0.0.1", 50150);

        sendThread.join();

        return receivedMessage;
    }

    @Test
    public void Deserializer_deserializeClassPrimitiveTest() throws Exception {
        ClassPrimitives obj = new ClassPrimitives(3, 1);
        String outcome = Serializer.serializeObject(obj);
        String outcome2 = receiveStringFromSender(outcome);
        Object obj2 = Deserializer.deserializeObject(outcome2);

        Assertions.assertEquals(obj.getClass().hashCode(), obj2.getClass().hashCode());
    }

    @Test
    public void Deserializer_deserializeClassObjectTest() throws Exception {
        ClassObject obj = new ClassObject(5, 4);
        String outcome = Serializer.serializeObject(obj);
        String outcome2 = receiveStringFromSender(outcome);
        Object obj2 = Deserializer.deserializeObject(outcome2);

        Assertions.assertEquals(obj.getClass().hashCode(), obj2.getClass().hashCode());
    }

    @Test
    public void Deserializer_deserializeArrayPrimitiveTest() throws Exception {
        int[] ints = new int[]{3, 5, 3, 5, 4, 1, 2, 8, 0, -2};
        ClassArrayPrimitives obj = new ClassArrayPrimitives(ints);
        String outcome = Serializer.serializeObject(obj);
        String outcome2 = receiveStringFromSender(outcome);
        Object obj2 = Deserializer.deserializeObject(outcome2);

        Assertions.assertEquals(obj.getClass().hashCode(), obj2.getClass().hashCode());
    }

    @Test
    public void Deserializer_deserializeArrayObjectTest() throws Exception {
        ClassPrimitives[] objs = new ClassPrimitives[5];
        objs[0] = new ClassPrimitives(3, 5);
        objs[1] = new ClassPrimitives(3, 5);
        objs[2] = new ClassPrimitives(4, 1);
        objs[3] = new ClassPrimitives(2, 8);
        objs[4] = new ClassPrimitives(0, -2);
        ClassArrayObjects obj = new ClassArrayObjects(objs);
        String outcome = Serializer.serializeObject(obj);
        String outcome2 = receiveStringFromSender(outcome);
        Object obj2 = Deserializer.deserializeObject(outcome2);

        Assertions.assertEquals(obj.getClass().hashCode(), obj2.getClass().hashCode());
    }

    @Test
    public void Deserializer_deserializeCollectionObjectTest() throws Exception {
        ArrayList<ClassPrimitives> objs = new ArrayList<>();
        objs.add(new ClassPrimitives(1, 9));
        objs.add(new ClassPrimitives(2, 8));
        objs.add(new ClassPrimitives(0, 3));
        objs.add(new ClassPrimitives(-1, -2));
        objs.add(new ClassPrimitives(-4, 8));
        ClassCollectionObjects obj = new ClassCollectionObjects(objs);
        String outcome = Serializer.serializeObject(obj);
        String outcome2 = receiveStringFromSender(outcome);
        Object obj2 = Deserializer.deserializeObject(outcome2);

        Assertions.assertEquals(obj.getClass().hashCode(), obj2.getClass().hashCode());
    }

    @Test
    public void Deserializer_deserializeObjectCircularRef() throws Exception {
        ClassCircularOne obj = new ClassCircularOne(3, 1);
        String outcome = Serializer.serializeObject(obj);
        String outcome2 = receiveStringFromSender(outcome);
        Object obj2 = Deserializer.deserializeObject(outcome2);

        Assertions.assertEquals(obj.getClass().hashCode(), obj2.getClass().hashCode());
    }
}
