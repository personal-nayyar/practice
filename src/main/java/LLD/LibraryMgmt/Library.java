package LLD.LibraryMgmt;

import lombok.Getter;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

interface ILibrary {
    void addBook(Book2 book);
    boolean borrowBook(String memberId, String bookId);
    boolean returnBook(String memberId, String loanId);
}

// Library - orchestrator (uses repos + policy)
// Handles concurrency using per-member lock map
public class Library implements ILibrary{
    private final CatalogRepository catalogRepo;
    private final MemberRepository memberRepo;
    private final LoanRepository loanRepo;
    private final BorrowPolicy borrowPolicy;

    // locks per member
    private final ConcurrentHashMap<String, ReentrantLock> memberLocks = new ConcurrentHashMap<>();

    public Library(CatalogRepository c, MemberRepository m, LoanRepository l, BorrowPolicy p) {
        this.catalogRepo = c;
        this.memberRepo = m;
        this.loanRepo = l;
        this.borrowPolicy = p;
    }

    private ReentrantLock lockForMember(String memberId) {
        return memberLocks.computeIfAbsent(memberId, id -> new ReentrantLock());
    }

    public synchronized void addBook(Book2 book) { catalogRepo.addBook(book); } // small method, simple sync ok

    public boolean borrowBook(String memberId, String bookId) {
        ReentrantLock lock = lockForMember(memberId);
        lock.lock();
        try {
            Optional<Member> om = memberRepo.findById(memberId);
            Optional<Book2> ob = catalogRepo.findById(bookId);
            if (!om.isPresent() || !ob.isPresent()) return false;
            Member member = om.get();
            Book2 book = ob.get();

            if (member.getActiveLoanCount() >= borrowPolicy.maxBooksAllowed()) return false;

            // try to reserve a copy
            boolean reserved = book.reserveOne();
            if (!reserved) return false;

            String loanId = UUID.randomUUID().toString();
            LocalDate borrowDate = LocalDate.now();
            LocalDate dueDate = borrowPolicy.computeDueDate(borrowDate);
            Loan loan = new Loan(loanId, book.getId(), memberId, borrowDate, dueDate);
            loanRepo.save(loan);

            // update member
            member.incActiveLoans();
            member.addToHistory(loanId);
            memberRepo.updateMember(member);
            // book is already updated via atomic reserve
            return true;
        } finally {
            lock.unlock();
        }
    }

    public boolean returnBook(String memberId, String loanId) {
        ReentrantLock lock = lockForMember(memberId);
        lock.lock();
        try {
            Optional<Loan> ol = loanRepo.findById(loanId);
            if (!ol.isPresent()) return false;
            Loan loan = ol.get();
            if (!loan.getMemberId().equals(memberId) || loan.getStatus() == Loan.Status.RETURNED) return false;

            Optional<Book2> ob = catalogRepo.findById(loan.getBookId());
            if (!ob.isPresent()) return false;
            Book2 book = ob.get();

            // mark returned
            loan.markReturned(LocalDate.now());
            loanRepo.update(loan);

            // update book and member
            book.releaseOne();
            Optional<Member> om = memberRepo.findById(memberId);
            om.ifPresent(member -> {
                member.decActiveLoans();
                memberRepo.updateMember(member);
            });
            return true;
        } finally {
            lock.unlock();
        }
    }

    // Additional methods: findAvailableBooks, searchByTitle, extendLoan, overdueChecks etc.
}

// Domain;
class Book2 {
    private final String id; // UUID or ISBN+copy
    private final String title;
    private final String author;
    private final String isbn;
    private final int publicationYear;
    private final AtomicInteger availableCount;

    public Book2(String id, String title, String author, String isbn, int publicationYear, int initialCount) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publicationYear = publicationYear;
        this.availableCount = new AtomicInteger(initialCount);
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public boolean reserveOne() {
        while (true) {
            int curr = availableCount.get();
            if (curr <= 0) return false;
            if (availableCount.compareAndSet(curr, curr - 1)) return true;
        }
    }
    public void releaseOne() { availableCount.incrementAndGet(); }
    public int getAvailableCount() { return availableCount.get(); }
    // equals/hashCode/toString omitted for brevity
}

class Member {
    private final String memberId;
    private final String name;
    private final String contact;
    private final List<String> borrowingHistory = new ArrayList<>();
    private final AtomicInteger activeLoanCount = new AtomicInteger(0);

    public Member(String memberId, String name, String contact) {
        this.memberId = memberId; this.name = name; this.contact = contact;
    }
    public String getMemberId() { return memberId; }
    public int getActiveLoanCount() { return activeLoanCount.get(); }
    public void incActiveLoans() { activeLoanCount.incrementAndGet(); }
    public void decActiveLoans() { activeLoanCount.decrementAndGet(); }
    public void addToHistory(String loanId) { borrowingHistory.add(loanId); }
    // getters omitted
}

@Getter
class Loan {
    public enum Status { BORROWED, RETURNED }
    private final String loanId;
    private final String BookId;
    private final String memberId;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;
    private LocalDate returnedDate;
    private Status status;

    public Loan(String loanId, String Book2Id, String memberId, LocalDate borrowDate, LocalDate dueDate) {
        this.loanId = loanId; this.BookId = Book2Id; this.memberId = memberId;
        this.borrowDate = borrowDate; this.dueDate = dueDate; this.status = Status.BORROWED;
    }
    public void markReturned(LocalDate returnedDate) {
        this.returnedDate = returnedDate; this.status = Status.RETURNED;
    }
    // getters omitted
}

// Repositories (thread-safe, in-memory)
interface CatalogRepository {
    void addBook(Book2 Book2);
    Optional<Book2> findById(String id);
    void updateBook(Book2 Book2);
    void removeBook(String id);
}

class InMemoryCatalogRepository implements CatalogRepository {
    private final ConcurrentHashMap<String, Book2> store = new ConcurrentHashMap<>();
    @Override public void addBook(Book2 Book2) { store.put(Book2.getId(), Book2); }
    @Override public Optional<Book2> findById(String id) { return Optional.ofNullable(store.get(id)); }
    @Override public void updateBook(Book2 Book2) { store.put(Book2.getId(), Book2); }
    @Override public void removeBook(String id) { store.remove(id); }
}

// Member repo
interface MemberRepository {
    void addMember(Member m);
    Optional<Member> findById(String memberId);
    void updateMember(Member m);
}

class InMemoryMemberRepository implements MemberRepository {
    private final ConcurrentHashMap<String, Member> store = new ConcurrentHashMap<>();
    @Override public void addMember(Member m) { store.put(m.getMemberId(), m); }
    @Override public Optional<Member> findById(String memberId) { return Optional.ofNullable(store.get(memberId)); }
    @Override public void updateMember(Member m) { store.put(m.getMemberId(), m); }
}

// Loan repo
interface LoanRepository {
    Loan save(Loan loan);
    Optional<Loan> findById(String loanId);
    List<Loan> findByMember(String memberId);
    void update(Loan loan);
}

class InMemoryLoanRepository implements LoanRepository {
    private final ConcurrentHashMap<String, Loan> store = new ConcurrentHashMap<>();
    @Override public Loan save(Loan loan) { store.put(loan.getLoanId(), loan); return loan; }
    @Override public Optional<Loan> findById(String loanId) { return Optional.ofNullable(store.get(loanId)); }
    @Override public List<Loan> findByMember(String memberId) {
        List<Loan> res = new ArrayList<>();
        for (Loan l : store.values()) if (l.getMemberId().equals(memberId)) res.add(l);
        return res;
    }
    @Override public void update(Loan loan) { store.put(loan.getLoanId(), loan); }
}

// Borrow policy (Strategy)
interface BorrowPolicy {
    int maxBooksAllowed();
    int loanDurationDays(); // default loan duration
    default LocalDate computeDueDate(LocalDate borrowDate) {
        return borrowDate.plusDays(loanDurationDays());
    }
}

class StandardBorrowPolicy implements BorrowPolicy {
    private final int max;
    private final int days;
    public StandardBorrowPolicy(int max, int days) { this.max = max; this.days = days; }
    @Override public int maxBooksAllowed() { return max; }
    @Override public int loanDurationDays() { return days; }
}

class Runner2{
    public static void main(String[] args) {
        CatalogRepository catalog = new InMemoryCatalogRepository();
        MemberRepository members = new InMemoryMemberRepository();
        LoanRepository loans = new InMemoryLoanRepository();
        BorrowPolicy policy = new StandardBorrowPolicy(3, 14);

        Library lib = new Library(catalog, members, loans, policy);

        Book2 b = new Book2("b1","Effective Java","Joshua Bloch","9780134685991",2018,2);
        catalog.addBook(b);

        Member m = new Member("m1","Alice","+91-9999999999");
        members.addMember(m);

        boolean success = lib.borrowBook("m1", "b1"); // true
        System.out.println("Borrow success: " + success);
    }
}