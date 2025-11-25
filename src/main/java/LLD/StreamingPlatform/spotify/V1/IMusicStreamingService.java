package LLD.StreamingPlatform.spotify.V1;

import LLD.util.repository.IRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface IMusicStreamingService {
    List<Song> browse(Map<String, String> queryFilter);
}

class User{
    String id;
    String name;
    Map<String, Playlist> playlists;
    List<Song> likedSongs;
    List<Song> history;
    User(String name){
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.playlists = new HashMap<>();
        this.likedSongs = new ArrayList<>();
        this.history = new ArrayList<>();
    }

    void addToPlayList(Song song){
        addToPlayList("default", song);
    }

    void addToPlayList(String playlistName, Song song){
        this.playlists.computeIfAbsent(playlistName, k-> new Playlist(playlistName)).songs.add(song);
    }
}

@ToString
@Getter
@AllArgsConstructor
class Song{
    String id;
    String name;
    Artist artist;
    Genre genre;
    String lyrics;

    void play(){
        System.out.println("Playing song: "+this.name);
    }
}

@Getter
class Artist{
    String id;
    String name;
    List<Song> songs;

    Artist(String id, String name){
        this.id = id;
        this.name = name;
        this.songs = new ArrayList<>();
    }

    Artist(String id, String name, List<Song> songs){
        this.id = id;
        this.name = name;
        this.songs = songs;
    }
}

enum Genre{
    POP,
    CLASSIC,
    ROCK,
    HIP_HOP,
    JAZZ,
    RAP
}

class Playlist{
    String id;
    String name;
    List<Song> songs;
    Playlist(String name){
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.songs = new ArrayList<>();
    }
}

@AllArgsConstructor
class Album{
    String id;
    String name;
    List<Song> songs;
}

// <--------- interface/Repository ---------->
interface IUserRepository extends IRepository<User>{ }
interface ISongRepository extends IRepository<User>{ }
interface IAlbumRepository extends IRepository<User>{ }
interface IArtistRepository extends IRepository<User>{ }

class MusicStreamingService implements IMusicStreamingService{
    Map<String, User> userRepo = new HashMap<>(){{
        put("user1", new User("user1"));
        put("user2", new User("user2"));
    }};

    Map<String, Artist> artistRepo = new HashMap<>(){{
        put("art1", new Artist("art1", "Arijit"));
        put("art2", new Artist("art2","Atif"));
    }};

    Map<String, Song> songRepo = new HashMap<>(){{
        put("song1", new Song("song1", "song1", artistRepo.get("art1"), Genre.CLASSIC, ""));
        put("song2", new Song("song2", "song2", artistRepo.get("art2"), Genre.POP, ""));
    }};

    {
    artistRepo.get("art1").songs.add(songRepo.get("song1"));
    artistRepo.get("art2").songs.add(songRepo.get("song2"));
    }

    Map<String, Album> albums = new HashMap<>(){{
        put("alb1", new Album("alb1", "alb1", List.of(songRepo.get("song1"))));
        put("alb2", new Album("alb2","alb2", List.of(songRepo.get("song2"))));
    }};


    @Override
    public List<Song> browse(Map<String, String> queryFilter) {
        Predicate<Song>[] predicate =new Predicate[]{Song -> true};
        queryFilter.forEach((key, value) -> {
            switch (key){
                case "text":
                    predicate[0] = predicate[0].and(s -> s.getName().contains(value));
                    break;
                case "Genre":
                    predicate[0] = predicate[0].and(s -> s.genre.name().equals(value));
                    break;
                case "Artist":
                    predicate[0] = predicate[0].and(s -> s.getArtist().getName().contains(value));
                    break;
            }
        });
        return songRepo.values().stream().filter(predicate[0]).collect(Collectors.toList());
    }
}

class Runner{
    public static void main(String[] args) {
        MusicStreamingService musicStreamingService = new MusicStreamingService();
        System.out.println(musicStreamingService.browse(Map.of("Genre", "POP")));
        System.out.println(musicStreamingService.browse(Map.of("Artist", "Arijit")));

        User user = musicStreamingService.userRepo.get("user1");
        user.addToPlayList(musicStreamingService.songRepo.get("song1"));
        System.out.println(user.playlists.get("default").songs);
    }
}


