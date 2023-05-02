package receiver;

import java.io.DataInputStream;
import java.net.Socket;

public class Receiver {
    public static String receiveStringFromSocket(String ipServer, int portNumber) {
        // From tutorial; modified so the client receives instead of sending messages
        try {
            Socket s = new Socket(ipServer, portNumber);
            DataInputStream dis = new DataInputStream(s.getInputStream());
            String outcome = dis.readUTF();
            dis.close();
            s.close();

            return outcome;
        }
        catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
}
