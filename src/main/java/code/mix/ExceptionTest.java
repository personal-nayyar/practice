package code.mix;

public class ExceptionTest {
   public static void main(String[] args) {
      System.out.println(exceptionTest());
   }
   public static int exceptionTest() {
      int i = 6;
      try {
         throw new NullPointerException();
      } catch (Exception e) {
         i = 10;
         return i;
      } finally {
         i = 20;
         System.out.println("In finally block");
      }
   }


}

class Test{
   public static void main(String[] args) {
      runtimeOrComileTime();
   }
   public static void runtimeOrComileTime() {
      int[] n1 = new int[0];
      try {
         int[] n2 = new int[-200];
      } catch (NegativeArraySizeException e) {
         System.out.println("Negative array size not allowed at runtime time");
      }
//      boolean[] n2 = new boolean[-200];
      // maximum value we can provide for array size
      // https://stackoverflow.com/a/11743638/1080533
      int maxArraySize = Integer.MAX_VALUE - 5;
//      int maxArraySize2 = 2241423798;
//      double[] n3 = new double[2241423798];
      char[] ch = new char[20];
   }
}
class InterviewBit{
   public static void main(String[] args)
   {
      //'b' = 98, 'i' = 105, 't' = 116
      //'b' + 'i' + 't' = 98 + 105 + 116 = 319
      System.out.println('b' + 'i' + 't');
   }
}