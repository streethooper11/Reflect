package exampleclass;// Class with an array of objects eacn of which has 2 primitives

import java.io.Serializable;

public class ClassArrayObjects implements Serializable {
    private ClassPrimitives[] classPrimitives;

    public ClassArrayObjects() {}

    public ClassArrayObjects(ClassPrimitives[] array) {
        this.classPrimitives = array;
    }

    public void initializeArray(int length) {
        classPrimitives = new ClassPrimitives[length];
    }

    public void setClassPrimitiveOfIndex(int index, ClassPrimitives prims) {
        classPrimitives[index] = prims;
    }
}
