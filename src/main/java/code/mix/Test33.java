package code.mix;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

class Person{
    String id;
    String name;
    int age;
}
public class Test33 {
    public static void main(String[] args) {
        List<Person> personList = new ArrayList<>(); // 5

        if(!personList.isEmpty()) {
            List<Person> filteredList = personList.stream().filter(person -> {
                return person != null && person.name.charAt(0) == 'A'  && person.age > 50;
            }).collect(Collectors.toList());
        }
        System.out.println(isProvided(Optional.ofNullable(null)));
    }

    public static boolean isProvided(Optional<Person> optionalPerson){
        return optionalPerson.isPresent();
    }

}

class StringPoolObjective{
}


class MultipleCatchBlock5{
    public static void main(String args[]){
        try{
            int a[]=new int[5];
            a[5]=30/0;
        }
        catch(ArithmeticException e){
            System.out.println("task1 is completed");
        }
        catch(ArrayIndexOutOfBoundsException e){
            System.out.println("task 2 completed");
        } catch(Exception e)
        {
            System.out.println("common task completed");
        }
        System.out.println("rest of the code...");
    }
}