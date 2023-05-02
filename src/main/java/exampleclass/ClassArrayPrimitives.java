package exampleclass;// Simple class with an array of primitives

import java.io.Serializable;

public class ClassArrayPrimitives implements Serializable {
    private int[] ints;

    public ClassArrayPrimitives() {}

    public ClassArrayPrimitives(int[] ints) {
        this.ints = ints;
    }

    public void initializeArray(int length) {
        ints = new int[length];
    }

    public void setIntOfIndex(int index, int num) {
        ints[index] = num;
    }
}
