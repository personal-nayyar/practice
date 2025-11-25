package LLD.AuctionSystem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import utils.ThreadUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@ToString
@AllArgsConstructor
@Getter
abstract class User{
    String id;
    String name;
    String email;
    String phone;
}

class Bidder extends User{
    // add bidder specific fields
    double balance;
    List<Bid> bids;

    public Bidder(String id, String name, String email, String phone) {
        super(id, name, email, phone);
    }
}

class Seller extends User{
    // add seller specific fields
    List<AuctionItem> items;

    public Seller(String id, String name, String email, String phone) {
        super(id, name, email, phone);
    }
}

@ToString
@AllArgsConstructor
@Setter
@Getter
class AuctionItem{
    enum Status {ACTIVE, INACTIVE, ENDED}
    String id;
    String name;
    String description;
    double startPrice;
    LocalDateTime startTime;
    LocalDateTime endTime;
    User seller;
    List<Bid> bids = new ArrayList<>();
    User winner;
    Status status;
    ReentrantLock lock =  new ReentrantLock();

    AuctionItem(String id, String name, String description, double startPrice, LocalDateTime startTime, LocalDateTime endTime, User seller) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startPrice = startPrice;
        this.startTime = startTime;
        this.endTime = endTime;
        this.seller = seller;
        this.status = Status.INACTIVE;
    }
}

@ToString
@AllArgsConstructor
@Setter
@Getter
class Bid{
    String id;
    double amount;
    Bidder bidder;
    AuctionItem item;
    LocalDateTime bidTime;
}

// <------- interface/Repository -------->
interface IAuctionRepository{
    void save(AuctionItem item);
    void update(AuctionItem item);
    void delete(AuctionItem item);
    List<AuctionItem> getAll();
    List<AuctionItem> getAll(User user);
    AuctionItem get(String id);
    User getWinner(AuctionItem item);
}

// <------- interface/Repository -------->
interface IUserRepository{
    void save(User user);
    void update(User user);
    void delete(User user);
    List<User> getAll();
    User get(String id);
}


// <------- interface/Repository -------->
interface IBidRepository{
    void save(Bid bid);
    void update(Bid bid);
    void delete(Bid bid);
    List<Bid> getAll();
    List<Bid> getAll(AuctionItem item);
    List<Bid> getAll(User user);
    Bid get(String id);
}

// <------ internal implementation of repo------>
class AuctionRepository implements IAuctionRepository{
    Map<String, AuctionItem> auctionMap;

    // singleton class
    private static AuctionRepository instance;
    public static AuctionRepository getInstance(){
        if(instance == null){
            instance = new AuctionRepository();
        }
        return instance;
    }

    private AuctionRepository(){
        auctionMap = new HashMap<>();
    }

    @Override
    public void save(AuctionItem item){
        boolean locked  = item.lock.tryLock();
        if (!locked){
            throw new RuntimeException("Auction item is already in creation");
        }
        try {
            auctionMap.put(item.getId(), item);
        } finally {
            item.lock.unlock();
        }
    }

    @Override
    public void update(AuctionItem item){
        auctionMap.put(item.getId(), item);
    }

    @Override
    public void delete(AuctionItem item){
        auctionMap.remove(item.getId());
    }

    @Override
    public List<AuctionItem> getAll(){
        return new ArrayList<>(auctionMap.values());
    }

    @Override
    public List<AuctionItem> getAll(User user){
        return auctionMap.values().stream().filter(item -> item.getSeller().getId().equals(user.getId())).collect(Collectors.toList());
    }

    @Override
    public AuctionItem get(String id){
        return auctionMap.get(id);
    }

    @Override
    public User getWinner(AuctionItem item){
        return item.getWinner();
    }
}

class UserRepository implements IUserRepository{
    Map<String, User> userMap;

    // singleton class
    private static UserRepository instance;
    public static UserRepository getInstance(){
        if(instance == null){
            instance = new UserRepository();
        }
        return instance;
    }

    private UserRepository(){
        userMap = new HashMap<>();
    }

    @Override
    public void save(User user){
        userMap.put(user.getId(), user);
    }

    @Override
    public void update(User user){
        userMap.put(user.getId(), user);
    }

    @Override
    public void delete(User user){
        userMap.remove(user.getId());
    }

    @Override
    public List<User> getAll(){
        return new ArrayList<>(userMap.values());
    }

    @Override
    public User get(String id){
        return userMap.get(id);
    }
}

class BidRepository implements IBidRepository{
    Map<String, Bid> bidMap;

    // singleton class
    private static BidRepository instance;
    public static BidRepository getInstance(){
        if(instance == null){
            instance = new BidRepository();
        }
        return instance;
    }

    private BidRepository(){
        bidMap = new HashMap<>();
    }

    @Override
    public void save(Bid bid){
        bidMap.put(bid.getId(), bid);
    }

    @Override
    public void update(Bid bid){
        bidMap.put(bid.getId(), bid);
    }

    @Override
    public void delete(Bid bid){
        bidMap.remove(bid.getId());
    }

    @Override
    public List<Bid> getAll(){
        return new ArrayList<>(bidMap.values());
    }

    @Override
    public List<Bid> getAll(AuctionItem item){
        return bidMap.values().stream().filter(bid -> bid.getItem().getId().equals(item.getId())).collect(Collectors.toList());
    }

    @Override
    public List<Bid> getAll(User user){
        return bidMap.values().stream().filter(bid -> bid.getBidder().getId().equals(user.getId())).collect(Collectors.toList());
    }

    @Override
    public Bid get(String id){
        return bidMap.get(id);
    }
}

interface IAuctionService {
    User registerUser(User user);
    AuctionItem createAuction(User seller, String itemName, String desc, double startPrice, LocalDateTime startTime, LocalDateTime endTime);
    List<AuctionItem> getActiveAuctions();
    List<AuctionItem> searchAuction(Map<String, String> filterQuery);
    Bid placeBid(AuctionItem item, Bid bid);
    void closeAuction(AuctionItem item);
}

class AuctionService implements IAuctionService{
    IAuctionRepository auctionRepository;
    IUserRepository userRepository;
    IBidRepository bidRepository;

    public AuctionService(IAuctionRepository auctionRepository, IUserRepository userRepository, IBidRepository bidRepository){
        this.auctionRepository = auctionRepository;
        this.userRepository = userRepository;
        this.bidRepository = bidRepository;
    }

    @Override
    public User registerUser(User user){
        userRepository.save(user);
        return user;
    }

    @Override
    public AuctionItem createAuction(User seller, String itemName, String desc, double startPrice, LocalDateTime startTime, LocalDateTime endTime){
        AuctionItem auctionItem = new AuctionItem(UUID.randomUUID().toString(), itemName, desc,
                startPrice, startTime, endTime, seller);
        auctionRepository.save(auctionItem);
        return auctionItem;
    }

    @Override
    public List<AuctionItem> getActiveAuctions(){
        return auctionRepository.getAll().stream().filter(item -> item.getStatus() == AuctionItem.Status.ACTIVE).collect(Collectors.toList());
    }

    @Override
    public List<AuctionItem> searchAuction(Map<String, String> filterQuery){
        Predicate<AuctionItem> predicate = AuctionItem -> true;
        for (Map.Entry<String, String> entry: filterQuery.entrySet()){
            switch (entry.getKey()){
                case "itemName":
                    predicate = predicate.and(auctionItem -> auctionItem.getName().equals(entry.getValue()));
                    break;
                case "description":
                    predicate = predicate.and(auctionItem -> auctionItem.getDescription().contains(entry.getValue()));
                    break;
                case "seller":
                    predicate = predicate.and(auctionItem -> auctionItem.getSeller().equals(entry.getValue()));
                    break;
                case "status":
                    predicate = predicate.and(auctionItem -> auctionItem.getStatus().equals(entry.getValue()));
                    break;
            }
        }
        return auctionRepository.getAll().stream().filter(predicate).collect(Collectors.toList());
    }

    @Override
    public Bid placeBid(AuctionItem item, Bid bid){
        boolean locked  = item.lock.tryLock();
        ThreadUtils.sleepSeconds(2);
        if (!locked){
            throw new RuntimeException("Auction item is already in use");
        }
        try{
            bidRepository.save(bid);
        }finally {
            item.lock.unlock();
        }
        return bid;
    }

    @Override
    public void closeAuction(AuctionItem item){
        item.setStatus(AuctionItem.Status.ENDED);
        auctionRepository.update(item);
    }
}

class Runner{
    public static void main(String[] args) {
        AuctionService auctionService = new AuctionService(AuctionRepository.getInstance(), UserRepository.getInstance(), BidRepository.getInstance());

        // create seller
        Seller seller1 = new Seller("1", "Nayyar", "nayyar@gmail.com", "123");
        auctionService.registerUser(seller1);
        Seller seller2 = new Seller("2", "Nayyar", "nayyar@gmail.com", "123");
        auctionService.registerUser(seller2);

        // create bidder
        Bidder bidder1 = new Bidder("3", "Bidder", "bidder@gmail.com", "123");
        auctionService.registerUser(bidder1);
        Bidder bidder2 = new Bidder("4", "Bidder", "bidder@gmail.com", "123");
        auctionService.registerUser(bidder2);

        System.out.println(auctionService.userRepository.getAll());

        // add item to auction
        AuctionItem auctionItem1 = auctionService.createAuction(seller1, "Item1", "Description", 100, LocalDateTime.now(), LocalDateTime.now().plusHours(1));

        // add concurrent auction item
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Callable<AuctionItem> auctionTask = () -> {
            AuctionItem auctionItem2 = null;
            try{
                auctionItem2 = auctionService.createAuction(seller2, "Item2", "Description", 100, LocalDateTime.now(), LocalDateTime.now().plusHours(1));
            } catch (Exception e) {
                System.out.println("Concurrent attempt failed: " + e.getMessage());
            }
            return auctionItem2;
        };
        try {
            executorService.invokeAll(Arrays.asList(auctionTask, auctionTask));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        executorService.shutdown();


        // get All active auctions
        List<AuctionItem> activeAuctions = auctionService.searchAuction(Map.of());
        System.out.println("Active Auctions: " + activeAuctions.size());

        // add concurrent bid
        executorService = Executors.newFixedThreadPool(2);

        Callable<Bid> task = () -> {
            Bid bid = null;
            // place bid
            try{
                bid =  auctionService.placeBid(auctionItem1, new Bid("1", 110, bidder1, auctionItem1, LocalDateTime.now()));
            } catch (Exception e) {
                System.out.println("Concurrent attempt failed: " + e.getMessage());
            }
            return bid;
        };
        try {
            executorService.invokeAll(Arrays.asList(task, task));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        executorService.shutdown();

        System.out.println("bids: "+auctionService.bidRepository.getAll().size());
    }
}







