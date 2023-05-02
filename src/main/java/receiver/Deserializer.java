package receiver;

import javax.json.*;
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
 */
public class Deserializer {
    private static Map<String, Object> idMap = new HashMap<>();

    private static void deserializeElementObject(Object obj, Class object_class,
                                                 JsonObject object_info) throws Exception {
        JsonArray array = object_info.getJsonArray("fields");

        Set<Field> fields = new HashSet<>();
        fields.addAll(Arrays.asList(object_class.getDeclaredFields()));
        fields.addAll(Arrays.asList(object_class.getFields()));

        int j;
        String actualFieldName, actualDeclaringClass, matchingFieldName, matchingDeclaringClass;
        String val;
        JsonObject objField;
        for (Field f : fields) {
            if (!Modifier.isStatic(f.getModifiers())) { // skip statics
                f.setAccessible(true);
                // get the correct field index with the matching name and declaring class
                actualFieldName = f.getName();
                actualDeclaringClass = f.getDeclaringClass().getName();

                j = 0;
                objField = array.getJsonObject(j);
                matchingFieldName = objField.getString("name");
                matchingDeclaringClass = objField.getString("declaringclass");

                while(!(actualFieldName.equals(matchingFieldName) &&
                        actualDeclaringClass.equals(matchingDeclaringClass))) {
                    j++;
                    objField = array.getJsonObject(j);
                    matchingFieldName = objField.getString("name");
                    matchingDeclaringClass = objField.getString("declaringclass");
                }

                if (f.getType().isPrimitive()) {
                    val = objField.getString("value");
                    f.setInt(obj, Integer.parseInt(val));
                }
                else {
                    val = objField.getString("reference");
                    f.set(obj, idMap.getOrDefault(val, null));
                }
            }
        }
    }

    private static void deserializeArray(Object obj, Class object_class, JsonObject object_info) {
        Class componentType = object_class.getComponentType();
        int length = Integer.parseInt(object_info.getString("length"));
        JsonArray array = object_info.getJsonArray("entries");

        if (componentType.isPrimitive()) {
            for (int j = 0; j < length; j++) {
                Array.setInt(obj, j, Integer.parseInt(array.getJsonObject(j).getString("value")));
            }
        }
        else {
            String ref;
            for (int j = 0; j < length; j++) {
                ref = array.getJsonObject(j).getString("reference");
                Array.set(obj, j, idMap.getOrDefault(ref, null));
            }
        }
    }

    // the class examples only have int primitives, so use setInt to set field values
    private static void assignFieldValues(JsonArray object_list) throws Exception {
        for (int i = 0; i < object_list.size(); i++) {
            Object obj = idMap.get(Integer.toString(i));
            JsonObject object_info = object_list.getJsonObject(i);
            Class object_class = obj.getClass();

            if (object_class.isArray()) {
                deserializeArray(obj, object_class, object_info);
            }
            else {
                deserializeElementObject(obj, object_class, object_info);
            }
        }
    }

    private static void createInstances(JsonArray object_list) throws Exception {
        for (int i = 0; i < object_list.size(); i++) {
            JsonObject object_info = object_list.getJsonObject(i);
            Class object_class = Class.forName(object_info.getString("class"));

            Object object_instance;
            if (object_class.isArray()) {
                Class componentType = object_class.getComponentType();
                int length = Integer.parseInt(object_info.getString("length"));
                object_instance = Array.newInstance(componentType, length);
            }
            else {
                Constructor constructor = object_class.getDeclaredConstructor();
                if (!Modifier.isPublic(constructor.getModifiers())) {
                    constructor.setAccessible(true);
                }

                object_instance = constructor.newInstance();
            }

            idMap.put(object_info.getString("id"), object_instance);
        }
    }

    public static Object deserializeObject(String source) throws Exception {
        // get the base object from JsonReader
        JsonReader jsonReader = Json.createReader(new StringReader(source));
        JsonObject json_object = jsonReader.readObject();
        jsonReader.close();
        JsonArray object_list = json_object.getJsonArray("objects");

        createInstances(object_list);
        assignFieldValues(object_list);

        return idMap.get("0"); // return the base object
    }
}