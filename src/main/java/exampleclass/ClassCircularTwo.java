package exampleclass;// Simple class with 2 primitives

import java.io.Serializable;

public class ClassCircularTwo implements Serializable {
    ClassCircularOne one;
    int number;

    public ClassCircularTwo() {}

    public ClassCircularTwo(int number) {
        this.number = number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setObjOne(ClassCircularOne one) {
        this.one = one;
    }
}
