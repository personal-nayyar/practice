package LLD.tech_impl.RateLimitter;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface RateLimiter {
    // provide implementation to this function
    boolean allowRequest(String userId);
}

class TokenBucketRateLimiter{
    // maxToken,token,refillPerSecond
    private final int maxTokens;
    private final int refillPerSecond;

    private int tokens;
    private long lastRefillTime;

    TokenBucketRateLimiter(int maxTokens, int refillPerSecond){
        this.maxTokens = maxTokens;
        this.tokens = maxTokens;
        this.refillPerSecond =  refillPerSecond;
        this.lastRefillTime = System.currentTimeMillis();
    }

    // consume
    public synchronized boolean allow(){
        refill();
        if (tokens >= 1){
            tokens--;
            return true;
        }
        return false;
    }
    // refill
    public void refill(){
        long now =  System.currentTimeMillis();
        int secPasses = (int) (now -  lastRefillTime)/1000;
        int tokensToAdd = secPasses * refillPerSecond;
        tokens =  Math.min(maxTokens, tokens + tokensToAdd);
        lastRefillTime = now;
    }
}

@AllArgsConstructor
class UserRateLimiter implements RateLimiter{
    private final int maxTokens;
    private final int refillPerSecond;
    private final Map<String, TokenBucketRateLimiter> userRateLimiter;

    @Override
    public boolean allowRequest(String userId) {
        TokenBucketRateLimiter rateLimiter =
                userRateLimiter.computeIfAbsent(userId,
        id -> new TokenBucketRateLimiter(maxTokens, refillPerSecond));
       return rateLimiter.allow();
    }
}

class GlobalRateLimiter implements RateLimiter{
    private final int maxToken;
    private final int refillPerSecond;
    private final TokenBucketRateLimiter rateLimiter;
    GlobalRateLimiter(int maxToken, int refillPerSecond){
        this.maxToken =  maxToken;
        this.refillPerSecond = refillPerSecond;
        rateLimiter =  new TokenBucketRateLimiter(maxToken, refillPerSecond);
    }


    @Override
    public boolean allowRequest(String userId) {
        return rateLimiter.allow();
    }

    public ResponseEntity<String> allowRequestSpring(String userId){
        if (!allowRequest(userId))
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .header("retry-after", "60")
                    .body("Too many request");

        return ResponseEntity.ok("Request processed successfully");

    }
}

class RateLimiterRunner{
    public static void main(String[] args) {
        UserRateLimiter userRateLimiter = new UserRateLimiter(5, 1, new ConcurrentHashMap<>());

        Runnable r1 = () -> {
            for (int i = 0; i < 10; i++) {
                System.out.println("Request allowed_"+i+":"+userRateLimiter.allowRequest("user1"));
            }
        };

        Runnable r2 = () -> {
            for (int i = 0; i < 10; i++) {
                System.out.println("Request allowed_"+i+":"+userRateLimiter.allowRequest("user2"));
            }
        };

        r1.run();
        r2.run();
    }
}
