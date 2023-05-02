package receiver;

import java.lang.reflect.*;
import java.util.HashSet;

/**
 * Based on Inspector class from Assignment 3
 *
 * It is simplified that it no longer inspects superclass, interfaces, constructors and methods
 * HashSet is used so that no same object is inspected twice
 */
public class Visualizer {
    private static HashSet<Object> inspectedSet = new HashSet<>();
    public static void inspect(Object obj, boolean recursive) {
        if (obj == null) {
            throw new IllegalArgumentException("Given object cannot be null");
        }

        Class c = obj.getClass();
        inspectClass(c, obj, recursive, 0);
    }

    private static void inspectClass(Class c, Object obj, boolean recursive, int depth) {
        inspectedSet.add(obj);
        inspectClassName(c, depth);
        inspectFields(c, obj, recursive, depth);
    }

    private static void inspectClassName(Class c, int depth) {
        String indent = getIndentation(depth);

        System.out.println(indent + "CLASS");
        String className = c.getName();
        System.out.println(indent + "Class: " + className);
    }

    private static void inspectFields(Class c, Object obj, boolean recursive, int depth) {
        String indent = getIndentation(depth);

        System.out.println(indent + "FIELDS( " + c.getName() + " )");
        System.out.print(indent + "Fields->");
        inspectArray(c.getDeclaredFields(), obj, recursive, depth, indent);
    }

    private static String getIndentation(int depth) {
        if (depth < 0) {
            throw new IllegalArgumentException("Invalid depth");
        }

        String indent = "";
        for (int i = 0; i < depth; i++) {
            indent += "\t";
        }

        return indent;
    }

    private static void inspectArray(Field arr[], Object obj, boolean recursive, int depth, String indent) {
        if (arr.length == 0) {
            System.out.println(" NONE");
        }
        else {
            System.out.println();
            for (Field f : arr) {
                inspectElement(f, obj, recursive, depth, indent);
            }
        }
    }

    private static void inspectElement(Field f, Object obj, boolean recursive, int depth, String indent) {
        Class fieldType;
        Object objField;

        System.out.println(indent + " FIELD");
        System.out.println(indent + "  Name: " + f.getName());

        fieldType = f.getType();
        System.out.println(indent + "  Type: " + fieldType);

        printModifiers(f.getModifiers(), indent);

        try {
            f.setAccessible(true); // need this to access private fields
            objField = f.get(obj); // get object
            // check if the field type is an array, and display more information if so
            if (fieldType.isArray()) {
                // an array
                Class componentType;
                int objArrayLength;

                componentType = fieldType.getComponentType();
                System.out.println(indent + "  Component Type: " + componentType);
                objArrayLength = Array.getLength(objField);
                System.out.println(indent + "  Length: " + objArrayLength);
                System.out.println(indent + "  Entries->");

                for (int i = 0; i < objArrayLength; i++) {
                    printFieldValueAndInspectRef(componentType, Array.get(objField, i), recursive, depth, indent + " ");
                }
            }
            else {
                // not an array
                printFieldValueAndInspectRef(fieldType, objField, recursive, depth, indent);
            }
        }
        catch (IllegalAccessException iae) {
            // should not happen
        }
        catch (InaccessibleObjectException ioe) {
            // cannot set accessible to true; should not happen
        }
    }

    private static void printExceptions(Class exceptions[], String indent) {
        System.out.print(indent + "  Exceptions->");
        if (exceptions.length == 0) {
            System.out.println(" NONE");
        }
        else {
            System.out.println();
            for (Class eachException : exceptions) {
                System.out.println(indent + "   " + eachException.toString());
            }
        }
    }

    private static void printParameterTypes(Class parameters[], String indent) {
        System.out.print(indent + "  Parameter types->");
        if (parameters.length == 0) {
            System.out.println(" NONE");
        }
        else {
            System.out.println();
            for (Class eachParamType : parameters) {
                System.out.println(indent + "   " + eachParamType.toString());
            }
        }
    }

    private static void printModifiers(int modifiers, String indent) {
        System.out.print(indent + "  Modifiers: ");
        if (modifiers == 0) {
            System.out.println("NONE");
        }
        else {
            System.out.println(Modifier.toString(modifiers));
        }
    }

    private static void printFieldValueAndInspectRef(Class type, Object obj, boolean recursive, int depth, String indent) {
        System.out.print(indent + "  Value");
        if (obj == null) {
            System.out.println(": null");
        }
        else if (type.isPrimitive()) {
            System.out.println(": " + obj.toString());
        }
        else {
            // element is non-null class; referenced
            System.out.println(indent + " (ref): " + obj.toString());
            if (!inspectedSet.contains(obj)) {
                if (recursive) {
                    System.out.println(indent + "    -> Recursively inspect");
                    depth++;
                    inspectClass(obj.getClass(), obj, recursive, depth);
                }
            }
        }
    }
}
