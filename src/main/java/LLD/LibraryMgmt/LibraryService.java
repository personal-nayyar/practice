package LLD.LibraryMgmt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

interface ILibraryMgmt {
    void addBook(String bookId, int copies, String self);
    void issueBook(String bookId, String userId);
    void returnBook(String bookId, String userId);
    int checkCopies(String bookId);
    List<Book>  getIssuedBooks(String userId);
}

public class LibraryService implements ILibraryMgmt{
    Map<String, User> userRepo = new HashMap<>(){{
        put("user1", new Student("user1", "user1"));
        put("user2", new Student("user2", "user2"));
    }};

    Map<String, Book> bookRepo = new HashMap<>(){{
       put("book1", Book.builder().lock(new ReentrantLock()).bookId("book1").copies(2).available(true).self("self1").build());
       put("book2", Book.builder().lock(new ReentrantLock()).bookId("book2").copies(1).available(true).self("self1").build());
    }};

    @Override
    public void addBook(String bookId, int copies, String self) {
        if (bookRepo.containsKey(bookId)){
           bookRepo.get(bookId).addCopies(copies);
        }else{
            bookRepo.put(bookId, Book.builder().lock(new ReentrantLock()).bookId(bookId).copies(copies).available(true).self(self).build());
        }
    }

    @Override
    public void issueBook(String bookId, String userId) {
        Book book = bookRepo.get(bookId);
        book.reserve();
        ((Student)userRepo.get(userId)).issuedBooks.add(book);
    }

    @Override
    public void returnBook(String bookId, String userId) {
        Book book = bookRepo.get(bookId);
        book.release();
        ((Student)userRepo.get(userId)).issuedBooks.remove(book);
    }

    @Override
    public int checkCopies(String bookId) {
        return bookRepo.get(bookId).copies;
    }

    @Override
    public List<Book> getIssuedBooks(String userId) {
        return ((Student)userRepo.get(userId)).issuedBooks;
    }
}

// <-------------Model------------->
@AllArgsConstructor
abstract class User{
    String userId;
    String name;
//    String email;
//    String address;
//    String phone;
}

class Student extends User{
    int rollNo;
    List<Book> issuedBooks;

    public Student(String userId, String name) {
        super(userId, name);
        this.issuedBooks =  new ArrayList<>();
    }
}

@ToString
@Getter
@Builder
class Book {
    String bookId;
    String title;
    String author;
    String isbn;
    String publisher;
    int copies;
    boolean available = true;
    String self;
    ReentrantLock lock =  new ReentrantLock();

    public void addCopies(int qty){
        lock.lock();
        try {
            copies += qty;
            available = true;
        } finally {
            lock.unlock();
        }
    }

    public void reserve(){
        lock.lock();
        try {
            if (!available) {
                System.out.println(String.format("Book %s not available", this.bookId));
                return;
            }
            copies -=1;
            available = copies > 0;
        } finally {
            lock.unlock();
        }
    }

    public void release(){
        lock.lock();
        try {
            copies +=1;
            available = true;
        } finally {
            lock.unlock();
        }
    }
}

class Runner{
    public static void main(String[] args) {
        LibraryService libraryService = new LibraryService();

        // check book avail
        System.out.println(libraryService.checkCopies("book1"));

        // add book
        libraryService.addBook("book1", 1, "self1");
        System.out.println(libraryService.checkCopies("book1"));

        // issue book
        libraryService.issueBook("book1", "user1");
        System.out.println(libraryService.checkCopies("book1"));

        // user issued books
        System.out.println(libraryService.getIssuedBooks("user1"));

        // return book
        libraryService.returnBook("book1", "user1");
        System.out.println(libraryService.checkCopies("book1"));

        System.out.println(libraryService.getIssuedBooks("user1"));

        // concurrent issue
        Callable<Void> callable1 =  () -> {
            libraryService.issueBook("book1", "user1");
            return null;
        };
        Callable<Void> callable2 =  () -> {
            libraryService.issueBook("book1", "user2");
            return null;
        };

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        try {
            executorService.invokeAll(List.of(callable1,callable2));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        executorService.shutdown();

        System.out.println(libraryService.checkCopies("book1"));

        // issue book
        libraryService.issueBook("book1", "user2");


        // concurrent return
        Callable<Void> callable3 =  () -> {
            libraryService.returnBook("book1", "user1");
            return null;
        };
        Callable<Void> callable4 =  () -> {
            libraryService.returnBook("book1", "user2");
            return null;
        };
        executorService = Executors.newFixedThreadPool(2);
        try {
            executorService.invokeAll(List.of(callable3,callable4));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        executorService.shutdown();
        System.out.println(libraryService.checkCopies("book1"));
    }
}



