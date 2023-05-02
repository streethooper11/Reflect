package receiver;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Author: Jonathan Hudson
 * Reflection4GeneralPurpose.JSON.receiver.Deserializer
 *
 * Modified by: Sehwa Kim
 * This class is basically Deserializer except each line corresponds to XML instead of JSON
 */
public class DeserializerXML {
    private static Map<String, Object> idMap = new HashMap<>();

    private static void deserializeElementObject(Object obj, Class object_class,
                                                 Element object_info) throws Exception {
        NodeList fieldNodes = object_info.getChildNodes();

        Set<Field> fields = new HashSet<>();
        fields.addAll(Arrays.asList(object_class.getDeclaredFields()));
        fields.addAll(Arrays.asList(object_class.getFields()));

        int j;
        String actualFieldName, actualDeclaringClass, matchingFieldName, matchingDeclaringClass;
        String val;
        Element objField;
        for (Field f : fields) {
            if (!Modifier.isStatic(f.getModifiers())) { // skip statics
                f.setAccessible(true);
                // get the correct field index with the matching name and declaring class
                actualFieldName = f.getName();
                actualDeclaringClass = f.getDeclaringClass().getName();

                j = 0;
                objField = (Element) fieldNodes.item(j);
                matchingFieldName = objField.getAttribute("name");
                matchingDeclaringClass = objField.getAttribute("declaringclass");

                while(!(actualFieldName.equals(matchingFieldName) &&
                        actualDeclaringClass.equals(matchingDeclaringClass))) {
                    j++;
                    objField = (Element) fieldNodes.item(j);
                    matchingFieldName = objField.getAttribute("name");
                    matchingDeclaringClass = objField.getAttribute("declaringclass");
                }

                if (f.getType().isPrimitive()) {
                    val = ((Element) objField.getFirstChild()).getTextContent();
                    f.setInt(obj, Integer.parseInt(val));
                }
                else {
                    val = ((Element) objField.getFirstChild()).getTextContent();
                    f.set(obj, idMap.getOrDefault(val, null));
                }
            }
        }
    }

    private static void deserializeArray(Object obj, Class object_class, Element object_info) {
        Class componentType = object_class.getComponentType();
        int length = Integer.parseInt(object_info.getAttribute("length"));
        NodeList entries = object_info.getChildNodes(); // list of values

        if (componentType.isPrimitive()) {
            for (int j = 0; j < length; j++) {
                Array.setInt(obj, j, Integer.parseInt(entries.item(j).getTextContent()));
            }
        }
        else {
            String ref;
            for (int j = 0; j < length; j++) {
                ref = entries.item(j).getTextContent();
                Array.set(obj, j, idMap.getOrDefault(ref, null));
            }
        }
    }

    // the class examples only have int primitives, so use setInt to set field values
    private static void assignFieldValues(NodeList object_list) throws Exception {
        for (int i = 0; i < object_list.getLength(); i++) {
            Object obj = idMap.get(Integer.toString(i));
            Node object_node = object_list.item(i);

            if (object_node instanceof Element) {
                Element object_info = (Element) object_node;

                Class object_class = obj.getClass();

                if (object_class.isArray()) {
                    deserializeArray(obj, object_class, object_info);
                }
                else {
                    deserializeElementObject(obj, object_class, object_info);
                }
            }
        }
    }

    private static void createInstances(NodeList object_list) throws Exception {
        for (int i = 0; i < object_list.getLength(); i++) {
            Node object_node = object_list.item(i);

            if (object_node instanceof Element) {
                Element object_info = (Element) object_node;

                Class object_class = Class.forName(object_info.getAttribute("class"));

                Object object_instance;
                if (object_class.isArray()) {
                    Class componentType = object_class.getComponentType();
                    int length = Integer.parseInt(object_info.getAttribute("length"));
                    object_instance = Array.newInstance(componentType, length);
                }
                else {
                    Constructor constructor = object_class.getDeclaredConstructor();
                    if (!Modifier.isPublic(constructor.getModifiers())) {
                        constructor.setAccessible(true);
                    }

                    object_instance = constructor.newInstance();
                }

                idMap.put(object_info.getAttribute("id"), object_instance);
            }
        }
    }

    public static Object deserializeObject(String source) throws Exception {
        Document document = convertStringToDocument(source);
        NodeList object_node_list = document.getDocumentElement().getChildNodes();

        createInstances(object_node_list);
        assignFieldValues(object_node_list);

        return idMap.get("0"); // return the base object
    }

    // Source: https://www.digitalocean.com/community/tutorials/java-convert-string-to-xml-document-and-xml-document-to-string
    private static Document convertStringToDocument(String xmlStr) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try
        {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse( new InputSource( new StringReader( xmlStr ) ) );
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}