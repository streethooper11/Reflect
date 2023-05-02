package exampleclass;// Class that contains a HashSet (Collection) of a class with 2 primitives

import java.io.Serializable;
import java.util.ArrayList;

public class ClassCollectionObjects implements Serializable {
    private ArrayList<ClassPrimitives> objArrayList;

    public ClassCollectionObjects() {
        objArrayList = new ArrayList<>();
    }

    public ClassCollectionObjects(ArrayList<ClassPrimitives> obj) {
        objArrayList = obj;
    }

    public void addClassPrimitive(ClassPrimitives prims) {
        objArrayList.add(prims);
    }
}
