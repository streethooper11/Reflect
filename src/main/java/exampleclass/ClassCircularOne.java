package exampleclass;// Simple class with 2 primitives

import java.io.Serializable;

public class ClassCircularOne implements Serializable {
    ClassCircularTwo two;
    int number;

    public ClassCircularOne() {}

    public ClassCircularOne(int number1, int number2) {
        this.number = number1;
        setUp(number2);
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setUp(int number) {
        two = new ClassCircularTwo();
        two.setNumber(number);
        two.setObjOne(this);
    }
}
