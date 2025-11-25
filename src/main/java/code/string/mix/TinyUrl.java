package code.string.mix;

public class TinyUrl {
    // convert base10 to base62
    static String idToShortUrl(int n){
        char[] base62possibles = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        StringBuffer shortUrl = new StringBuffer();
        while (n>0){
            shortUrl.append(base62possibles[n%62]);
            n /=62;
        }
        return shortUrl.toString();
    }

    static int shortUrlToId(String url){
        int id = 0;
        // convert base62 to base10
        for (int i = url.length(); i >=0; i--) {
            if ('a' < url.charAt(i) && url.charAt(i) < 'z')
                id = id*62 + url.charAt(i) -'a';
            if ('A' < url.charAt(i) && url.charAt(i) < 'Z')
                id = id*62 + url.charAt(i) -'A'+26;
            if ('0' < url.charAt(i) && url.charAt(i) < '9')
                id = id*62 + url.charAt(i) -'0'+52;
        }
        return id;
    }
}
