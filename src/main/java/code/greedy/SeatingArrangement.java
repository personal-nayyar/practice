package code.greedy;

import java.util.*;

/**
 * https://www.geeksforgeeks.org/dsa/seating-arrangement-without-adjacent-persons/?utm_source=chatgpt.com
 * Given an integer n, denoting the number of people who needs to be seated, and a list of m integer seats, where 0 represents a vacant seat and 1 represents an already occupied seat, the task is to find whether all n people can find a seat, provided that no two people can sit next to each other.
 * Examples:
 * Input: n = 2, m = 7, seats[] = {0, 0, 1, 0, 0, 0, 1}
 * Output: Yes
 * Explanation: The two people can sit at index 0 and 4.
 *
 * Input:n = 1, m = 3, seats[] = {0, 1, 0}
 * Output: No
 * Explanation: There is no way to get a seat for one person.
 * */
public class SeatingArrangement {
    public static void main(String[] args) {
        int[] seats = {1,0,0,0,0,0,1,0,0};
        System.out.println(canSeat(seats.clone(), 3)); // true
        System.out.println(canSeat(seats.clone(), 4)); // false
    }

    static boolean canSeat(int[] row, int k){
        int count = 0, n = row.length;
        for (int i = 0; i < n; i++) {
            if (row[i] == 0){ // can sit here
                boolean leftEmpty = (i == 0) || row[i-1] == 0;
                boolean rightEmpty = (i == n-1) || row[i+1] == 0;
                if (leftEmpty && rightEmpty){ // adjacent empty
                    row[i] = 1;
                    count++;
                    if (count >= k)
                        return true;
                }
            }
        }
        return false;
    }
}

/**
 * https://leetcode.com/problems/cinema-seat-allocation/description/?utm_source=chatgpt.com
 * */
class CinemaSeatAllocation {
    public int maxNumberOfFamilies(int n, int[][] reservedSeats) {
        // Map of row → reserved seats in that row
        Map<Integer, Set<Integer>> reservedMap = new HashMap<>();

        for (int[] seat : reservedSeats) {
            reservedMap.computeIfAbsent(seat[0], x -> new HashSet<>()).add(seat[1]);
        }

        int total = 2 * n; // start with assumption of 2 families per row

        for (int row : reservedMap.keySet()) {
            Set<Integer> reserved = reservedMap.get(row);
            boolean left = !(reserved.contains(2) || reserved.contains(3) || reserved.contains(4) || reserved.contains(5));
            boolean middle = !(reserved.contains(4) || reserved.contains(5) || reserved.contains(6) || reserved.contains(7));
            boolean right = !(reserved.contains(6) || reserved.contains(7) || reserved.contains(8) || reserved.contains(9));

            if (left && right) {
                // both groups fit → 2 families
                continue;
            } else if (left || middle || right) {
                // one group fits → 1 family
                total -= 1;
            } else {
                // no group fits → 0 family
                total -= 2;
            }
        }

        return total;
    }

    public static void main(String[] args) {
        CinemaSeatAllocation obj = new CinemaSeatAllocation();
        int[][] reserved = {{1,2},{1,3},{1,8},{2,6},{3,1},{3,10}};
        System.out.println(obj.maxNumberOfFamilies(3, reserved)); // Output: 4
    }
}

/**
 https://leetcode.com/problems/booking-concert-tickets-in-groups/description/
 A concert hall has n rows numbered from 0 to n - 1, each with m seats, numbered from 0 to m - 1. You need to design a ticketing system that can allocate seats in the following cases:

 If a group of k spectators can sit together in a row.
 If every member of a group of k spectators can get a seat. They may or may not sit together.
 Note that the spectators are very picky. Hence:

 They will book seats only if each member of their group can get a seat with row number less than or equal to maxRow. maxRow can vary from group to group.
 In case there are multiple rows to choose from, the row with the smallest number is chosen. If there are multiple seats to choose in the same row, the seat with the smallest number is chosen.
 Implement the BookMyShow class:

 BookMyShow(int n, int m) Initializes the object with n as number of rows and m as number of seats per row.
 int[] gather(int k, int maxRow) Returns an array of length 2 denoting the row and seat number (respectively) of the first seat being allocated to the k members of the group, who must sit together. In other words, it returns the smallest possible r and c such that all [c, c + k - 1] seats are valid and empty in row r, and r <= maxRow. Returns [] in case it is not possible to allocate seats to the group.
 boolean scatter(int k, int maxRow) Returns true if all k members of the group can be allocated seats in rows 0 to maxRow, who may or may not sit together. If the seats can be allocated, it allocates k seats to the group with the smallest row numbers, and the smallest possible seat numbers in each row. Otherwise, returns false.


 Example 1:
 Input
 ["BookMyShow", "gather", "gather", "scatter", "scatter"]
 [[2, 5], [4, 0], [2, 0], [5, 1], [5, 1]]
 Output
 [null, [0, 0], [], true, false]

 Explanation
 BookMyShow bms = new BookMyShow(2, 5); // There are 2 rows with 5 seats each
 bms.gather(4, 0); // return [0, 0]
 // The group books seats [0, 3] of row 0.
 bms.gather(2, 0); // return []
 // There is only 1 seat left in row 0,
 // so it is not possible to book 2 consecutive seats.
 bms.scatter(5, 1); // return True
 // The group books seat 4 of row 0 and seats [0, 3] of row 1.
 bms.scatter(5, 1); // return False
 // There is only one seat left in the hall.
 * */
class BookMyShow {
    private int n;
    private int m;
    private long[] nextFree; // next available seat index for each row
    private long[] freeSeats; // remaining free seats per row
    private long totalFree;   // total free seats overall

    public BookMyShow(int n, int m) {
        this.n = n;
        this.m = m;
        this.nextFree = new long[n];
        this.freeSeats = new long[n];
        Arrays.fill(freeSeats, m);
        this.totalFree = (long) n * m;
    }

    // gather(k, maxRow) => k consecutive seats in a single row <= maxRow
    public int[] gather(int k, int maxRow) {
        for (int i = 0; i <= maxRow; i++) {
            if (freeSeats[i] >= k) {
                int start = (int) nextFree[i];
                nextFree[i] += k;
                freeSeats[i] -= k;
                totalFree -= k;
                return new int[]{i, start};
            }
        }
        return new int[0];
    }

    // scatter(k, maxRow) => allocate k seats anywhere up to maxRow
    public boolean scatter(int k, int maxRow) {
        // Check if enough total free seats exist in [0..maxRow]
        long available = 0;
        for (int i = 0; i <= maxRow; i++) {
            available += freeSeats[i];
            if (available >= k) break;
        }
        if (available < k) return false;

        // Allocate seats greedily
        for (int i = 0; i <= maxRow && k > 0; i++) {
            long take = Math.min(freeSeats[i], k);
            freeSeats[i] -= take;
            nextFree[i] += take;
            k -= take;
            totalFree -= take;
        }
        return true;
    }

    public static void main(String[] args) {
        BookMyShow bms = new BookMyShow(3, 5);
        System.out.println(Arrays.toString(bms.gather(3, 2))); // [0, 0]
        System.out.println(Arrays.toString(bms.gather(2, 2))); // [0, 3]
        System.out.println(bms.scatter(4, 2)); // true
        System.out.println(bms.scatter(2, 2)); // false
    }
}

/**
 https://leetcode.com/problems/exam-room/description/
 There is an exam room with n seats in a single row labeled from 0 to n - 1.

 When a student enters the room, they must sit in the seat that maximizes the distance to the closest person. If there are multiple such seats, they sit in the seat with the lowest number. If no one is in the room, then the student sits at seat number 0.

 Design a class that simulates the mentioned exam room.

 Implement the ExamRoom class:

 ExamRoom(int n) Initializes the object of the exam room with the number of the seats n.
 int seat() Returns the label of the seat at which the next student will set.
 void leave(int p) Indicates that the student sitting at seat p will leave the room. It is guaranteed that there will be a student sitting at seat p.

 Example 1:
 Input
 ["ExamRoom", "seat", "seat", "seat", "seat", "leave", "seat"]
 [[10], [], [], [], [], [4], []]
 Output
 [null, 0, 9, 4, 2, null, 5]

 Explanation
 ExamRoom examRoom = new ExamRoom(10);
 examRoom.seat(); // return 0, no one is in the room, then the student sits at seat number 0.
 examRoom.seat(); // return 9, the student sits at the last seat number 9.
 examRoom.seat(); // return 4, the student sits at the last seat number 4.
 examRoom.seat(); // return 2, the student sits at the last seat number 2.
 examRoom.leave(4);
 examRoom.seat(); // return 5, the student sits at the last seat number 5.
 * */
class ExamRoom {
    private int N;
    private TreeSet<Integer> occupied;

    public ExamRoom(int N) {
        this.N = N;
        this.occupied = new TreeSet<>();
    }

    public int seat() {
        // Case 1: First person
        if (occupied.isEmpty()) {
            occupied.add(0);
            return 0;
        }

        int seatToTake = 0;
        int maxDist = occupied.first(); // distance from seat 0 to first occupied

        Integer prev = null;
        for (int curr : occupied) {
            if (prev != null) {
                int dist = (curr - prev) / 2;
                if (dist > maxDist) {
                    maxDist = dist;
                    seatToTake = prev + dist;
                }
            }
            prev = curr;
        }

        // Check last segment (after the last occupied seat)
        if (N - 1 - occupied.last() > maxDist) {
            seatToTake = N - 1;
        }

        occupied.add(seatToTake);
        return seatToTake;
    }

    public void leave(int p) {
        occupied.remove(p);
    }
}