package LLD.StreamingPlatform.spotify.V2;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

class Song {
    private String title;
    private String artist;
    private String album;
    private String genre;
    private int duration; // in seconds
    public Song(String title, String artist, String album, String genre, int duration) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.duration = duration;
    }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getAlbum() { return album; }
    public String getGenre() { return genre; }
    public int getDuration() { return duration; }
    @Override
    public String toString() {
        return title + " by " + artist + " (" + album + ")";
    }
}

class Album {
    private String name;
    private String artist;
    private List<Song> songs;
    public Album(String name, String artist) {
        this.name = name;
        this.artist = artist;
        this.songs = new ArrayList<>();
    }
    public String getName() { return name; }
    public String getArtist() { return artist; }
    public List<Song> getSongs() { return songs; }
    public void addSong(Song song) {
        songs.add(song);
    }
}

class Artist {
    private String name;
    private List<Album> albums;
    public Artist(String name) {
        this.name = name;
        this.albums = new ArrayList<>();
    }
    public String getName() { return name; }
    public List<Album> getAlbums() { return albums; }
    public void addAlbum(Album album) {
        albums.add(album);
    }
}

class User {
    private String username;
    private String password;
    private List<Playlist> playlists;
    private List<String> listeningHistory; // List of song titles
    private Set<String> preferences; // Genres
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.playlists = new ArrayList<>();
        this.listeningHistory = new ArrayList<>();
        this.preferences = new HashSet<>();
    }
    public String getUsername() { return username; }
    public boolean authenticate(String password) { return this.password.equals(password); }
    public List<Playlist> getPlaylists() { return playlists; }
    public List<String> getListeningHistory() { return listeningHistory; }
    public Set<String> getPreferences() { return preferences; }
    public void createPlaylist(String name) {
        playlists.add(new Playlist(name));
    }
    public Playlist getPlaylist(String name) {
        return playlists.stream().filter(p -> p.getName().equals(name)).findFirst().orElse(null);
    }
    public void addToHistory(String songTitle) {
        listeningHistory.add(songTitle);
    }
    public void addPreference(String genre) {
        preferences.add(genre);
    }
}

class Playlist {
    private String name;
    private List<Song> songs;
    public Playlist(String name) {
        this.name = name;
        this.songs = new ArrayList<>();
    }
    public String getName() { return name; }
    public List<Song> getSongs() { return songs; }
    public void addSong(Song song) {
        songs.add(song);
    }
    public void removeSong(Song song) {
        songs.remove(song);
    }
}

class MusicLibrary{
    private static MusicLibrary instance;
    public static MusicLibrary getInstance() {
        if (instance == null) {
            synchronized (MusicLibrary.class) {
                if (instance == null) {
                    instance = new MusicLibrary();
                }
            }
        }
        return instance;
    }

    private Map<String, Song> songs;
    private Map<String, Album> albums;
    private Map<String, Artist> artists;
    private Map<String, User> users;
    private ReentrantLock lock; // For thread safety

    public MusicLibrary() {
        this.songs = new HashMap<>();
        this.albums = new HashMap<>();
        this.artists = new HashMap<>();
        this.users = new HashMap<>();
        this.lock = new ReentrantLock();
    }

    public void addSong(Song song){
        lock.lock();
        try {
            songs.put(song.getTitle(), song);
            // Add to album and artist
            albums.computeIfAbsent(song.getAlbum(), k -> new Album(k, song.getArtist())).addSong(song);
            artists.computeIfAbsent(song.getArtist(), k -> new Artist(k)).addAlbum(albums.get(song.getAlbum()));
        } finally {
            lock.unlock();
        }
    }
    public List<Song> searchSongs(String query) {
        return songs.values().stream()
                .filter(s -> s.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        s.getArtist().toLowerCase().contains(query.toLowerCase()) ||
                        s.getAlbum().toLowerCase().contains(query.toLowerCase()))
                .toList();
    }
    public List<Album> searchAlbums(String query) {
        return albums.values().stream()
                .filter(a -> a.getName().toLowerCase().contains(query.toLowerCase()) ||
                        a.getArtist().toLowerCase().contains(query.toLowerCase()))
                .toList();
    }
    public List<Artist> searchArtists(String query) {
        return artists.values().stream()
                .filter(ar -> ar.getName().toLowerCase().contains(query.toLowerCase()))
                .toList();
    }
    public Song getSong(String title) {
        return songs.get(title);
    }
    public Album getAlbum(String name) {
        return albums.get(name);
    }
    public Artist getArtist(String name) {
        return artists.get(name);
    }

    public List<Song> getSongs(){
        return songs.values().stream().toList();
    }
}

class MusicPlayer{
    private boolean isPlaying;
    private Song currentSong;
    private int currentPosition;
    private ReentrantLock lock; // For thread safety

    public MusicPlayer() {
        this.isPlaying = false;
        this.currentSong = null;
        this.currentPosition = 0;
        this.lock = new ReentrantLock();
    }

    public void playSong(Song song){
        lock.lock();
        try {
            this.currentSong = song;
            this.isPlaying = true;
            this.currentPosition = 0;
            System.out.println("Now playing: "+song.getTitle());
        } finally {
            lock.unlock();
        }
    }
    public void pause(){
        lock.lock();
        try {
            this.isPlaying = false;
            System.out.println("Paused: "+currentSong.getTitle());
        } finally {
            lock.unlock();
        }
    }
    public void resume(){
        lock.lock();
        try {
            this.isPlaying = true;
            System.out.println("Resumed: "+currentSong.getTitle());
        } finally {
            lock.unlock();
        }
    }
    public void skip() {
        lock.lock();
        try {
            if (currentSong != null) {
                System.out.println("Skipped: " + currentSong);
                stop();
            }
        } finally {
            lock.unlock();
        }
    }

    public void stop() {
        lock.lock();
        try {
            if (currentSong != null) {
                System.out.println("Stopped playing: " + currentSong);
                currentSong = null;
                isPlaying = false;
                currentPosition = 0;
            }
        } finally {
            lock.unlock();
        }
    }

    public void rewind(){
        lock.lock();
        try {
            this.currentPosition -= 5;
            System.out.println("Rewound to: "+currentSong.getTitle());
        } finally {
            lock.unlock();
        }
    }

    public Song getCurrentSong() { return currentSong; }
    public boolean isPlaying() { return isPlaying; }
    public int getCurrentPosition() { return currentPosition; }
}

class RecommendationEngine{
    private static RecommendationEngine instance;
    public static RecommendationEngine getInstance() {
        if (instance == null) {
            synchronized (RecommendationEngine.class) {
                if (instance == null) {
                    instance = new RecommendationEngine();
                }
            }
        }
        return instance;
    }

    public List<Song> recommendSongs(User user, MusicLibrary library){
        Set<String> prefs = user.getPreferences();
        List<String> history = user.getListeningHistory();
        return library.getSongs().stream()
                .filter(s -> prefs.contains(s.getGenre()) || history.contains(s.getTitle()))
                .limit(5) // Simple limit for demo
                .toList();
    }

    public List<Playlist> recommendPlaylists(User user, List<User> allUsers) {
        // Simple collaborative filtering: recommend playlists from users with similar preferences
        return allUsers.stream()
                .filter(u -> !u.getUsername().equals(user.getUsername()) &&
                        !Collections.disjoint(u.getPreferences(), user.getPreferences())) // common preferences
                .flatMap(u -> u.getPlaylists().stream())
                .limit(3)
                .toList();
    }
}

class UserManager {
    private Map<String, User> users;
    private ReentrantLock lock;
    public UserManager() {
        users = new ConcurrentHashMap<>();
        lock = new ReentrantLock();
    }
    public boolean register(String username, String password) {
        lock.lock();
        try {
            if (users.containsKey(username)) return false;
            users.put(username, new User(username, password));
            return true;
        } finally {
            lock.unlock();
        }
    }
    public User login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.authenticate(password)) {
            return user;
        }
        return null;
    }
}

class StreamingService{
    private UserManager userManager;
    private MusicLibrary library;
    private RecommendationEngine recommender;
    private ExecutorService executor; // For concurrent requests
    public StreamingService() {
        userManager = new UserManager();
        library = MusicLibrary.getInstance();
        recommender = RecommendationEngine.getInstance();
        executor = Executors.newFixedThreadPool(10); // Simulate concurrency
    }

    public boolean registerUser(String username, String password) {
        return userManager.register(username, password);
    }
    public User loginUser(String username, String password) {
        return userManager.login(username, password);
    }

    public void addSong(Song song) {
        library.addSong(song);
    }
    public List<Song> searchSongs(String query) {
        return library.searchSongs(query);
    }
    public List<Album> searchAlbums(String query) {
        return library.searchAlbums(query);
    }
    public List<Artist> searchArtists(String query) {
        return library.searchArtists(query);
    }
    public List<Song> getRecommendations(User user) {
        return recommender.recommendSongs(user, library);
    }
    public List<Playlist> getPlaylistRecommendations(User user, List<User> allUsers) {
        return recommender.recommendPlaylists(user, allUsers);
    }
    // Simulate concurrent streaming
    public void streamSong(User user, Song song, MusicPlayer player) {
        executor.submit(() -> {
            player.playSong(song);
            user.addToHistory(song.getTitle());
            user.addPreference(song.getGenre());
            try {
                Thread.sleep(song.getDuration() * 100); // Simulate duration
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            player.stop();
        });
    }
    public void shutdown() {
        executor.shutdown();
    }
}

class MusicStreamingApp{
    public static void main(String[] args) {
        StreamingService service = new StreamingService();
        // Register and login users
        service.registerUser("john_doe", "pass123");
        service.registerUser("jane_smith", "pass456");
        User john = service.loginUser("john_doe", "pass123");
        User jane = service.loginUser("jane_smith", "pass456");
        // Add songs
        service.addSong(new Song("Bohemian Rhapsody", "Queen", "A Night at the Opera", "Rock", 355));
        service.addSong(new Song("Stairway to Heaven", "Led Zeppelin", "Led Zeppelin IV", "Rock", 482));
        service.addSong(new Song("Hotel California", "Eagles", "Hotel California", "Rock", 391));
        service.addSong(new Song("Billie Jean", "Michael Jackson", "Thriller", "Pop", 294));
        // John creates playlist and adds songs
        john.createPlaylist("My Favorites");
        List<Song> queenSongs = service.searchSongs("Queen");
        if (!queenSongs.isEmpty()) {
            john.getPlaylist("My Favorites").addSong(queenSongs.get(0));
        }
        // Jane adds preferences
        jane.addPreference("Pop");
        // Recommendations
        List<Song> recs = service.getRecommendations(john);
        System.out.println("Recommendations for John: " + recs.stream().map(Song::getTitle).toList());
        // Simulate concurrent streaming
        MusicPlayer player1 = new MusicPlayer();
        MusicPlayer player2 = new MusicPlayer();
        service.streamSong(john, service.searchSongs("Bohemian Rhapsody").get(0), player1);
        service.streamSong(jane, service.searchSongs("Billie Jean").get(0), player2);
        // Wait for streams to finish
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Demo player controls
        player1.pause();
        player1.resume();
//        player1.seek(100);
        player1.skip();
        service.shutdown();
        // Demo output
        System.out.println("John's playlists: " + john.getPlaylists().stream().map(Playlist::getName).toList());
        System.out.println("Jane's preferences: " + jane.getPreferences());
    }
}