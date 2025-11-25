package design_pattern.structural;
/***
 * Facade Pattern: Simplifies complex subsystems by providing a single entry point.
 * •	Without Facade → You’d need to know about all components (DVD, projector, sound).
 * •	With Facade → You just say “watch movie” or “end movie.”
 * •	Why useful? → Makes code cleaner, more maintainable, and easier to use.
 */
public class Facade {}

class DVDPlayer {
    void on() { System.out.println("DVD Player ON"); }
    void play(String movie) { System.out.println("Playing movie: " + movie); }
    void off() { System.out.println("DVD Player OFF"); }
}

class Projector {
    void on() { System.out.println("Projector ON"); }
    void setInput(DVDPlayer dvd) { System.out.println("Projector input set to DVD Player"); }
    void off() { System.out.println("Projector OFF"); }
}

class SoundSystem {
    void on() { System.out.println("Sound System ON"); }
    void setVolume(int level) { System.out.println("Sound volume set to " + level); }
    void off() { System.out.println("Sound System OFF"); }
}

class HomeTheaterFacade {
    private DVDPlayer dvd;
    private Projector projector;
    private SoundSystem sound;

    public HomeTheaterFacade(DVDPlayer dvd, Projector projector, SoundSystem sound) {
        this.dvd = dvd;
        this.projector = projector;
        this.sound = sound;
    }

    public void watchMovie(String movie) {
        System.out.println("\nGet ready to watch a movie...");
        dvd.on();
        projector.on();
        projector.setInput(dvd);
        sound.on();
        sound.setVolume(10);
        dvd.play(movie);
    }

    public void endMovie() {
        System.out.println("\nShutting down the home theater...");
        dvd.off();
        projector.off();
        sound.off();
    }
}

class FacadeDesignPatternDemoRunner {
    public static void main(String[] args) {
        DVDPlayer dvd = new DVDPlayer();
        Projector projector = new Projector();
        SoundSystem sound = new SoundSystem();

        HomeTheaterFacade homeTheater = new HomeTheaterFacade(dvd, projector, sound);

        // Client only interacts with Facade
        homeTheater.watchMovie("Inception");
        homeTheater.endMovie();
    }
}