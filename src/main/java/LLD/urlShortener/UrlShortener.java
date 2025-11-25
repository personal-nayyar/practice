package LLD.urlShortener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

interface IUrlShorteningStrategy{
    String generateShortCode(String longUrl);
}

interface IUrlShortenerStorage{
    void saveShortUrl(String shortCode, String shortUrl);
    String getShortUrl(String shortCode);
}

abstract class Base62Encoder{
    private final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private final int BASE = 62;
    protected String encodeToBase62(long id) {
        if (id == 0) return String.valueOf(BASE62_CHARS.charAt(0));
        StringBuilder sb = new StringBuilder();
        while (id > 0) {
            sb.append(BASE62_CHARS.charAt((int) (id % BASE)));
            id /= BASE;
        }
        return sb.reverse().toString();
    }
}

abstract class Base62Decoder{
    private String base62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private int base = 62;
    public String decode(String shortCode){
        long id = 0;
        for(int i=0; i<shortCode.length(); i++){
            id = id * base + base62.indexOf(shortCode.charAt(i));
        }
        return Long.toString(id);
    }
}

class CounterStrategy extends Base62Encoder implements IUrlShorteningStrategy{
    private static AtomicLong counter = new AtomicLong(1);
    private static final int SHORTCODE_LENGTH = 7; // this will generate around 3 trillion unique shortcodes

    CounterStrategy(){}

    @Override
    public String generateShortCode(String url){
        // generate short code
        long id = counter.getAndIncrement();
        // pad id to 6 digits
        String shortCode = encodeToBase62(id);

        // pad zero if shortcode length is less than 7
        int diff = SHORTCODE_LENGTH - shortCode.length();
        if (diff > 0) {
            shortCode = String.format("%0" + diff + "d", 0) + shortCode;
        }
        return shortCode.substring(0, SHORTCODE_LENGTH); // Fixed 6 chars (pad if needed, but base62 grows)
    }
}

class HashBasedStrategy implements IUrlShorteningStrategy{

    @Override
    public String generateShortCode(String longUrl) {
        return Long.toString(longUrl.hashCode(), 62);
    }
}

// factory pattern
class ShortenStrategyFactory{
    public static IUrlShorteningStrategy getStrategy(String strategy){
        switch (strategy){
            case "counter":
                return new CounterStrategy();
            case "hash":
                return new HashBasedStrategy();
            default:
                throw new IllegalArgumentException("Invalid strategy");
        }
    }
}



class InMemoryUrlShortenerStorage implements IUrlShortenerStorage{

    // implement singleton pattern here
    private static InMemoryUrlShortenerStorage instance = new InMemoryUrlShortenerStorage();
    private InMemoryUrlShortenerStorage(){
        instance = this;
    }
    public static InMemoryUrlShortenerStorage getInstance(){
        return instance;
    }


    private Map<String, String> storage = new ConcurrentHashMap<>();
    @Override
    public void saveShortUrl(String shortCode, String shortUrl){
        storage.put(shortCode, shortUrl);
    }

    @Override
    public String  getShortUrl(String shortCode){
        return storage.get(shortCode);
    }
}

interface UrlShortener{
    String shortenUrl(String longUrl);
    String expandUrl(String shortUrl);
}

class UrlShortenerImpl implements UrlShortener{
    private IUrlShorteningStrategy strategy;
    private IUrlShortenerStorage storage;
    private static final String BASE_URL = "http://shortly.in/";

    public UrlShortenerImpl(IUrlShorteningStrategy strategy, IUrlShortenerStorage storage){
        this.strategy = strategy;
        this.storage = storage;
    }

    public String shortenUrl(String longUrl){
        // validate url
        validateUrl(longUrl);

        // generate short code
        String shortCode = strategy.generateShortCode(longUrl);
        // check for collision for safer side
        while (storage.getShortUrl(shortCode) != null){
            shortCode = strategy.generateShortCode(longUrl);
        }

        // save short url in storage
        storage.saveShortUrl(shortCode, longUrl);

        return BASE_URL+shortCode;
    }

    public String expandUrl(String shortUrl){
        // get shortCode from short Url
        String shortCode = shortUrl.replace(BASE_URL, "");

        // check shortCode
        if (shortCode == null || shortCode.isEmpty()){
            throw new IllegalArgumentException("Invalid short code");
        }
        // get short url from storage
        String longUrl = storage.getShortUrl(shortCode);
        if (longUrl == null){
            throw new IllegalArgumentException("Invalid short code");
        }

        return longUrl;
    }

    private void validateUrl(String url){
        if (!url.startsWith("http://") && !url.startsWith("https://")){
            throw new IllegalArgumentException("Invalid URL");
        }
    }
}

class Runner{
    public static void main(String[] args){
        UrlShortener urlShortener = new UrlShortenerImpl(new CounterStrategy(), InMemoryUrlShortenerStorage.getInstance());
        String shortUrl = urlShortener.shortenUrl("https://www.google.com");
        System.out.println(shortUrl);
        String expandedUrl = urlShortener.expandUrl(shortUrl);
        System.out.println(expandedUrl);
    }
}