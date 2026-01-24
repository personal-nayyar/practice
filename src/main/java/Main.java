import LLD.util.address.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.net.http.HttpRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@ToString
@AllArgsConstructor
class Task{
    String name;
    int priority;

    public static void main(String[] args) {
        List<Task> tasks = Arrays.asList(
                new Task("Email", 3),
                new Task("Deploy", 1),
                new Task("backup", 2),
                new Task("FixBug", 1));

        tasks.sort((a,b) -> a.priority - b.priority);
        tasks.stream().forEach(System.out::println);
    }
}


@FunctionalInterface
interface I1{
    void m1();
    default void d1(){
        System.out.println("d1");
    }

//    void m3();.
}

@FunctionalInterface
interface I2{
    void m2();
    default void d2(){
        System.out.println("d2");
    }
}


class Test1{
    public static void main(String[] args) {
        I1 i1 =  () -> {
            System.out.println("m1");
        };

        I2 i2 = () -> {
            System.out.println("m2");
        };
        i1.m1();
        i1.d1();

        i2.m2();
        i2.d2();


    }
}

interface M1{
    default void m1(){
        //... lots of business logic
        System.out.println("m1");
    }
}


interface M2 {
    default void m1() {
        System.out.println("m1");
    }
}

// I want to call m1 without overloading it
class MyClass implements M1, M2{

    @Override
    public void m1(){
        M1.super.m1();
    }

    public static void main(String[] args) {
        MyClass o =  new MyClass();
        o.m1();
    }
}

abstract class M1Abs implements M1{
    @Override
    public void m1(){
        M1.super.m1();
    }
}

// without actual overriding
class MyClass2 extends M1Abs implements M2{
    public static void main(String[] args) {
        new MyClass2().m1();
    }
}


class Main{
//    public static void main(String[] args) {
//        JavaRDD<Integer> rdd = sparkContext.parallelize(Arrays.asList(1, 2, 3, 4, 5));
//        JavaRDD<Integer> squared = rdd.map(x -> x * x);
//        squared.collect().forEach(System.out::println);
//    }
//}
//
//@Configuration
//class SparkConfig {
//
//    @Bean
//    public SparkSession sparkSession() {
//        return SparkSession.builder()
//                .appName("SpringBootSparkDemo")
//                .master("local[*]") // run locally using all cores
//                .getOrCreate();
//    }

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // Step 1: Run m1, then m2, then m3 in sequence
        CompletableFuture<List<Integer>> resultFuture =
                CompletableFuture.supplyAsync(() -> {
                            int r1 = m1();
                            return new ArrayList<>(List.of(r1));
                        }, executor)
                        .thenApplyAsync(list -> {
                            int r2 = m2();
                            list.add(r2);
                            return list;
                        }, executor)
                        .thenApplyAsync(list -> {
                            int r3 = m3();
                            list.add(r3);
                            return list;
                        }, executor);

        // Step 2: Wait for all to complete and get results
        List<Integer> results = resultFuture.join();

        System.out.println("Final Results: " + results);

        executor.shutdown();
    }

//    public static void main(String[] args) {
//        ExecutorService executorService = Executors.newFixedThreadPool(3);
//        Future<Integer> future1 = executorService.submit(() -> m1());
//        Future<Integer> future2 = executorService.submit(() -> m2());
//        Future<Integer> future3 = executorService.submit(() -> m3());
//        try {
//            System.out.println(future1.get());
//            System.out.println(future2.get());
//            System.out.println(future3.get());
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//        }
//    }
//
    public static Integer m1() {
        System.out.println("Executing m1");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public static Integer m2() {
        System.out.println("Executing m2");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 2;
    }

    public static Integer m3() {
        System.out.println("Executing m3");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 3;
    }

    void m4(){
        IntStream.range(1, 10)
                .forEach(System.out::println);
    }
}

class Main2 {
    public static void main(String[] args) {
        Integer a = 5;
        System.out.println(System.identityHashCode(a));
        a = 10;
        System.out.println(System.identityHashCode(a));
    }
}

class StringComparison{
    public static void main(String[] args) {
        String s1 = "Java";
        String s2 = "Java";
        System.out.println(s1 == s2); // true

        String s3 = new String("Hello");
        String s4 = "Hello";
        System.out.println(s3 == s4); // false

        String s5 = "Java";
        String s6 = "Ja" + "va";
        System.out.println(s5 == s6); // true

        String s7 = "Ja";
        String s8 = s1 + "va";
        System.out.println("Java" == s8); // false

        String s9 = "abc";
        s9.concat("xyz");
        System.out.println(s9); // abc

        String s10 = "abc";
        s10 = s10.concat("xyz");
        System.out.println(s10); // abcxyz

        String s11 = "HELLO";
        String s12 = s1.toLowerCase();
        System.out.println(s11 == s12); // false

        String s13 = new String("Java");
        String s14 = s1.intern();
        System.out.println(s13 == s14); // false
    }
}

class CustomeException extends Exception{
    public CustomeException(String message){
        super(message);
    }
}

@Data
@AllArgsConstructor
class Employee{
    String id;
    String name;
    String dept;
    int age;
    int salary;
}

class Test
{
    public static void test(){
        System.out.println("test");
    }
    public static void main(String[] args) {
        List<Employee> list =  new ArrayList<>();
        list.add(new Employee("1", "A", "IT", 25, 10000));
        list.add(new Employee("2", "B", "IT", 26, 20000));
        list.add(new Employee("3", "C", "CSE", 27, 30000));
        list.add(new Employee("4", "D", "IT", 28, 40000));
        list.add(new Employee("5", "E", "CSE", 29, 50000));
        list.add(new Employee("6", "F", "CSE", 30, 60000));
        list.add(new Employee("7", "G", "IT", 31, 70000));
        // list of employee
        // id, name, age, salary
        // group by name
        list.stream().collect(Collectors.groupingBy(Employee::getDept)); // return dept --> list of employee
        list.stream().collect(Collectors.groupingBy(Employee::getDept, Collectors.counting()));  // return dept --> count
        list.stream().collect(Collectors.groupingBy(Employee::getDept, Collectors.mapping(Employee::getName, Collectors.toList()))); // return dept --> list of name
        list.stream().collect(Collectors.groupingBy(Employee::getDept, Collectors.summingInt(Employee::getSalary))); // return dept --> sum of salary
        list.stream().collect(Collectors.groupingBy(Employee::getDept, Collectors.maxBy(Comparator.comparing(Employee::getSalary)))); // return dept --> max salary
        list.stream().collect(Collectors.groupingBy(Employee::getDept, Collectors.minBy(Comparator.comparing(Employee::getSalary)))); // return dept --> min salary
        list.stream().collect(Collectors.groupingBy(Employee::getDept, Collectors.averagingInt(Employee::getSalary))); // return dept --> avg salary
        list.stream().collect(Collectors.groupingBy(Employee::getDept, Collectors.summarizingInt(Employee::getSalary)));// return dept --> summary of salary
        list.stream().collect(Collectors.groupingBy(Employee::getDept, Collectors.mapping(Employee::getName, Collectors.joining(", ")))) ;// return dept --> comma separated name
        list.stream().collect(Collectors.groupingBy(Employee::getDept, Collectors.mapping(Employee::getName, Collectors.joining(", ", "[", "]")))) ;// return dept --> comma separated name with prefix and suffix
    }
}


class Student {
    String name;

    Student(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}

class MutableElementExample {
    public static void main(String[] args) {
        // Step 1: Create a modifiable list of mutable objects
        List<Student> students = new ArrayList<>();
        students.add(new Student("Alice"));
        students.add(new Student("Bob"));

        // Step 2: Create an unmodifiable view
        List<Student> unmodifiableStudents = Collections.unmodifiableList(students);

        System.out.println("Before modification: " + unmodifiableStudents);

        // Step 3: Try structural modification (will throw exception)
        try {
            unmodifiableStudents.add(new Student("Charlie")); // ❌ not allowed
        } catch (UnsupportedOperationException e) {
            System.out.println("Cannot modify list structure: " + e);
        }

        // Step 4: Modify the internal state of an element (✅ allowed)
        unmodifiableStudents.get(0).name = "Alicia";

        System.out.println("After element modification: " + unmodifiableStudents);
    }
}
