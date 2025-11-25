package LLD.coursePlatform;

import LLD.util.Notification.NotificationService;
import LLD.util.Notification.NotificationType;
import LLD.util.repository.IRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;


// <----------- entity ------->
// User -> Student, Instructror
// Course
// Enrollment

@Getter
abstract class User{
    String id;
    String name;
    User(String id){
        this.id = id;
    }
}

@Getter
class Student extends User{
    String rollNo;
    List<Enrollment> enrollments; // compute and add runtime
    public Student(String id, String rollNo) {
        super(id);
        this.rollNo = rollNo;
        this.enrollments = new ArrayList<>();
    }
}

@Getter
class Instructor extends User{
    String employeeId;
    Instructor(String id, String employeeId){
        super(id);
        this.employeeId = employeeId;
    }
}

@ToString
@AllArgsConstructor
class Course{
    String courseId;
    String courseName;
    int capacity;
    double price;
    List<Instructor> instructors;
    List<Enrollment> enrollments; // /compute and add
    ReentrantLock lock;

    Course(String name, int capacity){
        this.courseId = name;
        this.courseName = name;
        this.capacity = capacity;
        this.instructors = new ArrayList<>();
        this.enrollments = new ArrayList<>();
        this.lock = new ReentrantLock();
    }

    boolean isFull(){
        return capacity == enrollments.size();
    }
}

enum EnrollmentStatus{
    ENROLLED,
    WAITLIST,
    CANCELLED
}

@Setter
@Getter
@AllArgsConstructor
class Enrollment {
    String enrollmentId;
    String studentId;
    String courseId;
    EnrollmentStatus status;
}

// <----------- repository ------->
interface IStudentRepository extends IRepository<Student>{ }
interface IInstructorRepository extends IRepository<Instructor>{}
interface ICourseRepository extends IRepository<Course>{}
interface IEnrollmentRepository extends IRepository<Enrollment>{}


public interface IOnlineCoursePlatform {
    Course addCourse(String courseName, int capacity);
    List<Course> viewCatalog();
    Enrollment enrollCourse(String userId, String courseId);
    void cancelEnrollment(String enrollmentId);
}

class CoursePlatform implements IOnlineCoursePlatform{
    Map<String, Student> studentRepository = new HashMap<>(){{
        put("st1", new Student("st1" , "1"));
        put("st2", new Student("st2" , "2"));
    }};

    Map<String, Instructor> instructorRepository = new HashMap<>(){{
        put("it1", new Instructor("it1" , "1"));
        put("it2", new Instructor("it2" , "2"));
    }};

    Map<String, Course> courseRepository = new HashMap<>();
    Map<String, Enrollment> enrollmentRepository = new HashMap<>();
    Map<String, Queue<String>> waitlistRepository = new HashMap<>();
    NotificationService notificationService = NotificationService.getInstance();

    @Override
    public Course addCourse(String courseName, int capacity) {
        Course course = new Course(courseName, capacity);
        courseRepository.put(courseName, course);
        return course;
    }

    @Override
    public List<Course> viewCatalog(){
        return courseRepository.values().stream().toList();
    }

    @Override
    public Enrollment enrollCourse(String userId, String courseId) {
        Course course = courseRepository.get(courseId);
        Enrollment enrollment = new Enrollment(UUID.randomUUID().toString(), userId, courseId, EnrollmentStatus.ENROLLED);
        course.lock.lock();
        try {
            if (course.isFull()){
                // add to waitlist ano notify status
                enrollment.setStatus(EnrollmentStatus.WAITLIST);
                waitlistRepository.computeIfAbsent(courseId, k-> new LinkedList<>())
                        .add(enrollment.getStudentId());
                notificationService.notifyUser(NotificationType.SMS, userId, "You are on waitlist for course " + courseId);
                return enrollment;
//                throw new RuntimeException("Course is full");
            }
            enrollmentRepository.put(enrollment.getEnrollmentId(), enrollment);
            courseRepository.get(courseId).enrollments.add(enrollment);
            studentRepository.get(userId).enrollments.add(enrollment);
            // notify student
            notificationService.notifyUser(NotificationType.SMS, userId, "You are enrolled for course " + courseId);
        } finally {
            course.lock.unlock();
        }
        return enrollment;
    }

    @Override
    public void cancelEnrollment(String enrollmentId) {
        Enrollment enrollment = enrollmentRepository.get(enrollmentId);
        enrollmentRepository.remove(enrollmentId);
        courseRepository.get(enrollment.getCourseId()).enrollments.remove(enrollment);
        studentRepository.get(enrollment.getStudentId()).enrollments.remove(enrollment);
        notificationService.notifyUser(NotificationType.SMS, enrollment.getStudentId(), "You have cancelled your enrollment for course " + enrollment.getCourseId());

        // notify waitlist student
        String firstWaitedStudent = waitlistRepository.get(enrollment.getCourseId()).poll();
        enrollCourse(firstWaitedStudent, enrollment.getCourseId());
    }
}

class Runner{
    public static void main(String[] args) {
        CoursePlatform coursePlatform = new CoursePlatform();

        coursePlatform.addCourse("DSA", 2);
        System.out.println(coursePlatform.viewCatalog());

        Enrollment enrollment = coursePlatform.enrollCourse("st1", "DSA");
        System.out.println(
                coursePlatform.studentRepository.get("st1").getEnrollments().stream()
                        .map(Enrollment::getCourseId).collect(Collectors.toUnmodifiableList()));

        // concurrent enrollment
        Callable<Enrollment> enrollmentCallable = () ->{
            return coursePlatform.enrollCourse("st1", "DSA");
        };

        Callable<Enrollment> enrollmentCallable2 = () ->{
            return coursePlatform.enrollCourse("st2", "DSA");
        };

        ExecutorService executorService  = Executors.newFixedThreadPool(2);
        try {
            executorService.invokeAll(List.of(enrollmentCallable, enrollmentCallable2));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        executorService.shutdown();

        System.out.println(
                coursePlatform.studentRepository.get("st1").getEnrollments().stream()
                        .map(Enrollment::getCourseId).collect(Collectors.toUnmodifiableList()));

        System.out.println(
                coursePlatform.studentRepository.get("st2").getEnrollments().stream()
                        .map(Enrollment::getCourseId).collect(Collectors.toUnmodifiableList()));

        coursePlatform.cancelEnrollment(enrollment.getEnrollmentId());

        System.out.println(
                coursePlatform.studentRepository.get("st1").getEnrollments().stream()
                        .map(Enrollment::getCourseId).collect(Collectors.toUnmodifiableList()));

        System.out.println(
                coursePlatform.studentRepository.get("st2").getEnrollments().stream()
                        .map(Enrollment::getCourseId).collect(Collectors.toUnmodifiableList()));
    }
}
