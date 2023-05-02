package sender;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Author: Jonathan Hudson
 * Reflection4GeneralPurpose.JSON.sender.Serializer
 *
 * Modified by: Sehwa Kim
 *
 * This file generally follows the format of storage4.xml shown in lectures,
 * while including everything in assignment requirements, such as type
 *
 * Following source was used to learn methods of Node for Element:
 * https://docs.oracle.com/en/java/javase/17/docs/api/java.xml/org/w3c/dom/Node.html
 */
public class SerializerXML {
    private static Map<Object, String> idMap;
    private static ArrayList<Object> objectsToInspect;

    private static void updateEachReference(Element object_info, Object source) {
        if (source == null) {
            object_info.setTextContent("null");
        }
        else if (idMap.containsKey(source)) {
            object_info.setTextContent(idMap.get(source));
        }
        else {
            String object_id = Integer.toString(idMap.size());
            object_info.setTextContent(object_id);
            idMap.put(source, object_id); // add to IdentityHashMap
            objectsToInspect.add(source); // one more object to inspect as it isn't in the list yet
        }
    }

    private static void serializeObject(Document document, Element object_info, Class object_class, Object source) throws IllegalAccessException {
        object_info.setAttribute("type", "object");

        Set<Field> fields = new HashSet<>();
        fields.addAll(Arrays.asList(object_class.getDeclaredFields()));
        fields.addAll(Arrays.asList(object_class.getFields()));

        Object objField;
        Element eachInfo, eachVal;
        for (Field f : fields) {
            eachInfo = document.createElement("field");
            if (!Modifier.isStatic(f.getModifiers())) { // skip statics
                f.setAccessible(true);
                eachInfo.setAttribute("name", f.getName());
                eachInfo.setAttribute("declaringclass", f.getDeclaringClass().getName());

                objField = f.get(source);

                if (f.getType().isPrimitive()) {
                    eachVal = document.createElement("value");
                    eachVal.setTextContent(objField.toString());
                }
                else {
                    eachVal = document.createElement("reference");
                    updateEachReference(eachVal, objField);
                }

                eachInfo.appendChild(eachVal);
                object_info.appendChild(eachInfo);
            }
        }
    }

    private static void serializeArray(Document document, Element object_info, Class object_class, Object source) {
        int objArrayLength;

        object_info.setAttribute("type", "array");

        objArrayLength = Array.getLength(source);
        object_info.setAttribute("length", Integer.toString(objArrayLength));

        Object objElement;
        Class componentType = object_class.getComponentType();
        Element eachInfo;
        if (componentType.isPrimitive()) {
            for (int j = 0; j < objArrayLength; j++) {
                objElement = Array.get(source, j);
                eachInfo = document.createElement("value");
                eachInfo.setTextContent(objElement.toString());
                object_info.appendChild(eachInfo);
            }
        }
        else {
            for (int j = 0; j < objArrayLength; j++) {
                objElement = Array.get(source, j);
                eachInfo = document.createElement("reference");
                updateEachReference(eachInfo, objElement);
                object_info.appendChild(eachInfo);
            }
        }
    }

    // Most reflection methods from Inspector class in Assignment 3
    private static void serializeHelper(Document document) throws IllegalAccessException {
        for (int i = 0; i < objectsToInspect.size(); i++) {
            Object source = objectsToInspect.get(i);
            String object_id = Integer.toString(i);

            Class object_class = source.getClass();
            Element object_info = document.createElement("object");
            object_info.setAttribute("class", object_class.getName());
            object_info.setAttribute("id", object_id);

            if (object_class.isArray()) {
                serializeArray(document, object_info, object_class, source);
            }
            else {
                serializeObject(document, object_info, object_class, source);
            }

            document.getDocumentElement().appendChild(object_info);
        }
    }

    public static String serializeObject(Object object) throws Exception {
        idMap = new IdentityHashMap<>();
        objectsToInspect = new ArrayList<>();
        objectsToInspect.add(object); // add the source object to inspect arraylist
        idMap.put(object, "0"); // add original to IdentityHashMap

        DocumentBuilderFactory document_factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder document_builder = document_factory.newDocumentBuilder();
        Document document = document_builder.newDocument();
        document.appendChild(document.createElement("serialized"));

        serializeHelper(document);

        return convertDocumentToString(document);
    }

    // Source: https://www.digitalocean.com/community/tutorials/java-convert-string-to-xml-document-and-xml-document-to-string
    private static String convertDocumentToString(Document doc) {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tf.newTransformer();
            // below code to remove XML declaration
            // transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            String output = writer.getBuffer().toString();
            return output;
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        return null;
    }
}
