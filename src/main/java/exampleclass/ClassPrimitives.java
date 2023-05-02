package exampleclass;// Simple class with 2 primitives

import java.io.Serializable;

public class ClassPrimitives implements Serializable {
    private int int1;
    private int int2;

    public ClassPrimitives() {}

    public ClassPrimitives(int int1, int int2) {
        this.int1 = int1;
        this.int2 = int2;
    }

    public void setInt1(int int1) {
        this.int1 = int1;
    }

    public void setInt2(int int2) {
        this.int2 = int2;
    }
}
