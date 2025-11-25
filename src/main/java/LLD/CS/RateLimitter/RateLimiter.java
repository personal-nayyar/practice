package LLD.CS.RateLimitter;

import java.time.Clock;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

// strategy pattern | abstract class
interface RateLimiter {
    boolean tryConsumeToken();
}

class TokenBucketRateLimiter implements RateLimiter {
    int maxTokens;
    AtomicInteger tokens; // to make this thread safe we can use atomic integer
    long lastRefillTime;
    long refillInterval;

    public TokenBucketRateLimiter(int maxTokens, long refillInterval) {
        this.maxTokens = maxTokens;
        this.tokens = new AtomicInteger(maxTokens);
        this.lastRefillTime = System.currentTimeMillis();
        this.refillInterval = refillInterval;
    }

    @Override
    public boolean tryConsumeToken() {
        refill();// first always refill
        return consumeToken();
    }

    private void refill(){
        long now = System.currentTimeMillis();
        long timeDiff = now - lastRefillTime;
        int tokensToAdd = (int) (timeDiff / refillInterval);
        if (tokensToAdd >0)
            tokens = new AtomicInteger(Math.min(tokens.addAndGet(tokensToAdd), maxTokens));
        lastRefillTime = now;
    }

    private boolean consumeToken(){
        if (tokens.get() > 0){
            tokens.decrementAndGet(); // consume token and process request
        }
        else{
            throw new RuntimeException("Rate limit exceeded");
        }
        return true;
    }
}

class SlidingWindowRateLimiter implements RateLimiter {
    int maxRequests; // max requests allowed in a window
    long windowSize; // in milliseconds
    Map<Long, Integer> window; // timestamp -> request count
    Clock clock; // Injectable clock to mock time

    public SlidingWindowRateLimiter(int maxRequests, long windowSize) {
        this.maxRequests = maxRequests;
        this.windowSize = windowSize;
        this.window = new ConcurrentHashMap<>();
        this.clock = Clock.systemDefaultZone();
    }

    @Override
    public boolean tryConsumeToken() {
        long now = clock.millis();
        long windowStart = now - windowSize;
        window.entrySet().removeIf(entry -> entry.getKey() < windowStart); // remove old entries
        int requestCount = window.values().stream().mapToInt(Integer::intValue).sum();
        if (requestCount >= maxRequests){
            throw new RuntimeException("Rate limit exceeded");
        }
        window.put(now, requestCount + 1);
        return true;
    }
}

// factory pattern
class RateLimiterFactory{
    public static RateLimiter getRateLimiter(String type){
        switch (type){
            case "SLIDING_WINDOW":
                return new SlidingWindowRateLimiter(10, 1000);
            case "TOKEN_BUCKET":
                return new TokenBucketRateLimiter(10, 1000);
            default:
                throw new RuntimeException("Invalid rate limiter type");
        }
    }
}

class Runner{
    public static void main(String[] args) {
        RateLimiter rateLimiter = RateLimiterFactory.getRateLimiter("SLIDING_WINDOW");
        for (int i = 0; i < 15; i++) {
            if (rateLimiter.tryConsumeToken()) {
                System.out.println("Request allowed");
            }
            else{
                System.out.println("Request not allowed");
            }
        }

        RateLimiter rateLimiter2 = RateLimiterFactory.getRateLimiter("TOKEN_BUCKET");
        for (int i = 0; i < 15; i++) {
            if (rateLimiter2.tryConsumeToken()) {
                System.out.println("Request allowed");
            }
            else{
                System.out.println("Request not allowed");
            }
        }
    }
}

interface RateLimiterUser{
    boolean isRequestAllowed(String userId);
}

class RateLimiterUserImpl implements RateLimiterUser{
    public static final Map<String, RateLimiter> buckets = new HashMap<>();
    public boolean isRequestAllowed(String userId){
       RateLimiter bucket =  buckets.computeIfAbsent(userId, key-> new TokenBucketRateLimiter(10, 1000));
       return bucket.tryConsumeToken();
    }
}

class Runner2{
    public static void main(String[] args) {
        RateLimiterUser rateLimiterUser = new RateLimiterUserImpl();
        for (int i = 0; i < 15; i++) {
            System.out.println(rateLimiterUser.isRequestAllowed("user1"));
        }
    }
}
