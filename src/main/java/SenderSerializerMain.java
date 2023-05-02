import sender.ObjectCreator;

import java.util.Scanner;

public class SenderSerializerMain {
    public static void main(String[] args) throws Exception {
        ObjectCreator thisObj = new ObjectCreator();

        int portNumber = 50150;
        if (args.length > 0) {
            try {
                portNumber = Integer.parseInt(args[0]);
            }
            catch (NumberFormatException nfe) {
                // port number doesn't change as invalid port was given
            }
        }

        Scanner input = new Scanner(System.in);
        int objOption;
        while (true) {
            objOption = thisObj.getFirstOption(input);
            if (objOption == 1) {
                thisObj.printAndSendResult(thisObj.serializeClassPrimitive(input), portNumber);
            }
            else if (objOption == 2) {
                thisObj.printAndSendResult(thisObj.serializeClassObject(input), portNumber);
            }
            else if (objOption == 3) {
                thisObj.printAndSendResult(thisObj.serializeClassArrayPrimitives(input), portNumber);
            }
            else if (objOption == 4) {
                thisObj.printAndSendResult(thisObj.serializeClassArrayObjects(input), portNumber);
            }
            else if (objOption == 5) {
                thisObj.printAndSendResult(thisObj.serializeClassCollectionObjects(input), portNumber);
            }
            else if (objOption == 6) {
                thisObj.printAndSendResult(thisObj.serializeClassCircularRef(input), portNumber);
            }
            else if (objOption == 7) {
                if (thisObj.getXmlMode()) {
                    System.out.println("Mode changed to JSON.");
                    thisObj.setXmlMode(false);
                }
                else {
                    System.out.println("Mode changed to XML.");
                    thisObj.setXmlMode(true);
                }
            }
            else {
                break; // quit
            }
        }
    }
}
