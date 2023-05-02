package sender;

import javax.json.JsonObject;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Sender {
    public static void sendStringOverSocket(String outcome, int portNumber) {
        // From tutorial; modified so the server socket sends instead of receiving message
        try {
            ServerSocket ss = new ServerSocket(portNumber);
            System.out.println("Listening on " + InetAddress.getLocalHost() + ":" + ss.getLocalPort());
            Socket s = ss.accept();
            DataOutputStream d_out = new DataOutputStream(s.getOutputStream());
            d_out.writeUTF(outcome);
            d_out.flush();
            d_out.close();
            s.close();
            ss.close();
            System.out.println("Serialized data was successfully sent.");
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
}
