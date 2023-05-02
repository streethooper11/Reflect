import receiver.*;

public class ReceiverDeserializerMain {
    public static void main(String[] args) throws Exception {
        String address = "127.0.0.1";
        int portNumber = 50150;
        if (args.length > 0) {
            address = args[0];
            if (args.length > 1) {
                try {
                    portNumber = Integer.parseInt(args[1]);
                    if ((portNumber < 0) || (portNumber > 65536)) {
                        throw new NumberFormatException("");
                    }
                }
                catch (NumberFormatException nfe) {
                    System.out.println("Invalid port number was given. Using default port of 50150...");
                }
            }
        }


        String outcome = Receiver.receiveStringFromSocket(address, portNumber);
        String xmlMode = outcome.substring(0, 1);
        String objString = outcome.substring(1);
        Object objReceived;

        if (xmlMode.equals("J")) {
            System.out.println("Received JSON");
            objReceived = Deserializer.deserializeObject(objString);
        }
        else {
            System.out.println("Received XML");
            objReceived = DeserializerXML.deserializeObject(objString);
        }

        Visualizer.inspect(objReceived, true);
    }
}
