package sender;

import javax.json.*;
import javax.json.stream.JsonGenerator;
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
 */
public class Serializer {
    private static Map<Object, String> idMap;
    private static ArrayList<Object> objectsToInspect;

    private static void updateEachReference(JsonObjectBuilder object_info, Object source) {
        if (source == null) {
            object_info.add("reference", "null");
        }
        else if (idMap.containsKey(source)) {
            object_info.add("reference", idMap.get(source));
        }
        else {
            String object_id = Integer.toString(idMap.size());
            object_info.add("reference", object_id);
            idMap.put(source, object_id); // add to IdentityHashMap
            objectsToInspect.add(source); // one more object to inspect as it isn't in the list yet
        }
    }

    private static void serializeObject(JsonObjectBuilder object_info, Class object_class, Object source) throws IllegalAccessException {
        JsonArrayBuilder list = Json.createArrayBuilder();
        JsonObjectBuilder eachInfo;
        object_info.add("type", "object");

        Set<Field> fields = new HashSet<>();
        fields.addAll(Arrays.asList(object_class.getDeclaredFields()));
        fields.addAll(Arrays.asList(object_class.getFields()));

        Object objField;
        for (Field f : fields) {
            eachInfo = Json.createObjectBuilder();
            if (!Modifier.isStatic(f.getModifiers())) { // skip statics
                f.setAccessible(true);
                eachInfo.add("name", f.getName());
                eachInfo.add("declaringclass", f.getDeclaringClass().getName());

                objField = f.get(source);

                if (f.getType().isPrimitive()) {
                    eachInfo.add("value", objField.toString());
                }
                else {
                    updateEachReference(eachInfo, objField);
                }

                list.add(eachInfo);
            }
        }

        object_info.add("fields", list);
    }

    private static void serializeArray(JsonObjectBuilder object_info, Class object_class, Object source) {
        JsonArrayBuilder list = Json.createArrayBuilder();
        JsonObjectBuilder eachInfo;
        int objArrayLength;

        object_info.add("type", "array");

        objArrayLength = Array.getLength(source);
        object_info.add("length", Integer.toString(objArrayLength));

        Object objElement;
        Class componentType = object_class.getComponentType();
        if (componentType.isPrimitive()) {
            for (int j = 0; j < objArrayLength; j++) {
                objElement = Array.get(source, j);
                eachInfo = Json.createObjectBuilder();
                eachInfo.add("value", objElement.toString());
                list.add(eachInfo);
            }
        }
        else {
            for (int j = 0; j < objArrayLength; j++) {
                objElement = Array.get(source, j);
                eachInfo = Json.createObjectBuilder();
                updateEachReference(eachInfo, objElement);
                list.add(eachInfo);
            }
        }

        object_info.add("entries", list);
    }

    // Most reflection methods from Inspector class in Assignment 3
    private static void serializeHelper(JsonArrayBuilder object_list) throws IllegalAccessException {
        for (int i = 0; i < objectsToInspect.size(); i++) {
            Object source = objectsToInspect.get(i);
            String object_id = Integer.toString(i);

            Class object_class = source.getClass();
            JsonObjectBuilder object_info = Json.createObjectBuilder();
            object_info.add("class", object_class.getName());
            object_info.add("id", object_id);

            if (object_class.isArray()) {
                serializeArray(object_info, object_class, source);
            }
            else {
                serializeObject(object_info, object_class, source);
            }

            object_list.add(object_info);
        }
    }

    public static String serializeObject(Object object) throws Exception {
        idMap = new IdentityHashMap<>();
        objectsToInspect = new ArrayList<>();
        JsonArrayBuilder object_list = Json.createArrayBuilder();
        objectsToInspect.add(object); // add the source object to inspect arraylist
        idMap.put(object, "0"); // add original to IdentityHashMap
        serializeHelper(object_list);
        JsonObjectBuilder json_base_object = Json.createObjectBuilder();
        json_base_object.add("objects", object_list);

        // pretty print JSON Source:
        // http://www.mastertheboss.com/java-ee/json/how-to-pretty-print-a-jsonobject-using-jakarta-ee-api/
        JsonObject sendObj = json_base_object.build();
        Map<String, Object> properties = new HashMap<>();
        properties.put(JsonGenerator.PRETTY_PRINTING, true);
        StringWriter sw = new StringWriter();
        JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
        JsonWriter jsonWriter = writerFactory.createWriter(sw);
        jsonWriter.writeObject(sendObj);
        jsonWriter.close();

        return sw.toString();
    }
}
