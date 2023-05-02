package sender;

import exampleclass.*;
import java.util.Scanner;

public class ObjectCreator {
    private boolean xmlMode = false;
    public int getFirstOption(Scanner input) {
        int objOption;

        System.out.println("Please select an option (1-8):");
        System.out.println("1: Serialize an object with two integer variables");
        System.out.println("2. Serialize an object that contains another object with two integer variables");
        System.out.println("3. Serialize an array of integers");
        System.out.println("4. Serialize an array of objects that contain two integer variables each");
        System.out.println("5. Serialize a collection (ArrayList) of objects that contain two integer variables each.");
        System.out.println("6. Serialize an object with circular references.");
        if (xmlMode) {
            System.out.println("7. Switch mode to JSON");
        }
        else {
            System.out.println("7. Switch mode to XML");
        }
        System.out.println("8. Quit");

        // make sure the input is between 1 and 5 inclusive and is not a non-numerical character
        while (true) {
            try {
                objOption = Integer.parseInt(input.nextLine());
                if ((objOption < 1) || (objOption > 8)) {
                    throw new NumberFormatException("Invalid entry. Try again: ");
                }
                break;
            }
            catch (NumberFormatException nfe) {
                System.out.print("Invalid entry. Try again: ");
            }
        }

        return objOption;
    }

    public int getIntegerValueFromInput(Scanner input) {
        while (true) {
            try {
                return Integer.parseInt(input.nextLine());
            }
            catch (NumberFormatException nfe) {
                System.out.print("Invalid entry. Try again: ");
            }
        }
    }

    public ClassPrimitives getClassPrimitiveFromInputs(Scanner input) {
        ClassPrimitives obj = new ClassPrimitives();

        System.out.print("Enter an integer value for the first primitive: ");
        obj.setInt1(getIntegerValueFromInput(input));

        System.out.print("Enter an integer value for the second primitive: ");
        obj.setInt2(getIntegerValueFromInput(input));

        return obj;
    }

    public String serializeClassPrimitive(Scanner input) throws Exception {
        if (xmlMode) {
            return SerializerXML.serializeObject(getClassPrimitiveFromInputs(input));
        }
        else {
            return Serializer.serializeObject(getClassPrimitiveFromInputs(input));
        }
    }

    public String serializeClassObject(Scanner input) throws Exception {
        ClassObject obj = new ClassObject();

        System.out.println("This class contains an object with two primitives.");
        System.out.print("Enter an integer value for the first primitive: ");
        obj.setInt1OfClassPrimitives(getIntegerValueFromInput(input));

        System.out.print("Enter an integer value for the second primitive: ");
        obj.setInt2OfClassPrimitives(getIntegerValueFromInput(input));

        if (xmlMode) {
            return SerializerXML.serializeObject(obj);
        }
        else {
            return Serializer.serializeObject(obj);
        }
    }

    public String serializeClassArrayPrimitives(Scanner input) throws Exception {
        int length;
        System.out.print("Choose the size of the array: ");
        while (true) {
            try {
                length = Integer.parseInt(input.nextLine());
                if (length < 1) {
                    throw new NumberFormatException();
                }
                break;
            }
            catch (NumberFormatException nfe) {
                System.out.print("Invalid entry. Try again: ");
            }
        }

        ClassArrayPrimitives obj = new ClassArrayPrimitives();
        obj.initializeArray(length);
        for (int i = 0; i < length; i++) {
            System.out.println("Enter an integer value for element " + i + ".");
            obj.setIntOfIndex(i, getIntegerValueFromInput(input));
        }

        if (xmlMode) {
            return SerializerXML.serializeObject(obj);
        }
        else {
            return Serializer.serializeObject(obj);
        }
    }

    public String serializeClassArrayObjects(Scanner input) throws Exception {
        int length;
        System.out.print("Choose the size of an array: ");
        while (true) {
            try {
                length = Integer.parseInt(input.nextLine());
                if (length < 1) {
                    throw new NumberFormatException();
                }
                break;
            }
            catch (NumberFormatException nfe) {
                System.out.print("Invalid entry. Try again: ");
            }
        }

        ClassArrayObjects obj = new ClassArrayObjects();
        obj.initializeArray(length);
        System.out.println("Each element is an object with two primitives.");
        for (int i = 0; i < length; i++) {
            System.out.println("For element " + i);
            obj.setClassPrimitiveOfIndex(i, getClassPrimitiveFromInputs(input));
        }

        if (xmlMode) {
            return SerializerXML.serializeObject(obj);
        }
        else {
            return Serializer.serializeObject(obj);
        }
    }

    public String serializeClassCollectionObjects(Scanner input) throws Exception {
        int length;
        System.out.print("Choose the size of the ArrayList: ");
        while (true) {
            try {
                length = Integer.parseInt(input.nextLine());
                if (length < 1) {
                    throw new NumberFormatException();
                }
                break;
            }
            catch (NumberFormatException nfe) {
                System.out.print("Invalid entry. Try again: ");
            }
        }

        ClassCollectionObjects obj = new ClassCollectionObjects();
        System.out.println("Each element is an object with two primitives.");
        for (int i = 0; i < length; i++) {
            System.out.println("For element " + i);
            obj.addClassPrimitive(getClassPrimitiveFromInputs(input));
        }

        if (xmlMode) {
            return SerializerXML.serializeObject(obj);
        }
        else {
            return Serializer.serializeObject(obj);
        }
    }

    public String serializeClassCircularRef(Scanner input) throws Exception {
        System.out.println("There are two class objects that refers to each other.");
        System.out.println("Each class has 1 integer variable.");
        System.out.print("Enter an integer value for the first class: ");
        ClassCircularOne obj = new ClassCircularOne();
        obj.setNumber(getIntegerValueFromInput(input));
        System.out.println("Serialization will occur after entering a value for the second class.");
        System.out.print("Enter an integer value for the second class: ");
        obj.setUp(getIntegerValueFromInput(input));

        if (xmlMode) {
            return SerializerXML.serializeObject(obj);
        }
        else {
            return Serializer.serializeObject(obj);
        }
    }

    public void printAndSendResult(String outcome, int portNumber) {
        System.out.println(outcome);

        // indicates if this is XML or JSON with X/J value
        if (xmlMode) {
            outcome = "X" + outcome;
        }
        else {
            outcome = "J" + outcome;
        }

        Sender.sendStringOverSocket(outcome, portNumber);
    }

    public void setXmlMode(boolean xmlMode) {
        this.xmlMode = xmlMode;
    }

    public boolean getXmlMode() {
        return xmlMode;
    }
}
