package code.mix;

public class DiamondProblem {
}

interface A{
    default void print(){
        System.out.println("A");
    }
}

interface B{
    default void print(){
        System.out.println("B");
    }
}

interface C extends A,B{
    @Override
    default void print() {
        // force to implement print method to decide which interface print method to call
        A.super.print();
        B.super.print();
    }
}

class DiamondProblemImpl implements C{
    public static void main(String[] args) {
        new DiamondProblemImpl().print();
    }
}