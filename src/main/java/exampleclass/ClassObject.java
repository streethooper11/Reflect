package exampleclass;// Class that refers to a class with 2 primitives

import java.io.Serializable;

public class ClassObject implements Serializable {
    private ClassPrimitives classPrimitives;

    public ClassObject() {
        classPrimitives = new ClassPrimitives();
    }

    public ClassObject(int int1, int int2) {
        classPrimitives = new ClassPrimitives(int1, int2);
    }

    public void setInt1OfClassPrimitives(int int1) {
        classPrimitives.setInt1(int1);
    }

    public void setInt2OfClassPrimitives(int int2) {
        classPrimitives.setInt2(int2);
    }
}
