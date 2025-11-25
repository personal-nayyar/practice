package A_interview_experiences.kpmg;

public interface ISport {
    String getName();
    int playerPerTeam();
    void play();

    // default print info
    default void printInfo(){
        System.out.printf("Sport: %s, Player per team: %d%n", getName(), playerPerTeam());
    }
}

abstract class Sport implements ISport{
    String name;
    int playerPerTeam;
    Sport(String name, int playerPerTeam){
        this.name = name;
        this.playerPerTeam = playerPerTeam;
    }

    // default implementation
    public String getName() {
        return name;
    }

    public int playerPerTeam(){
        return playerPerTeam;
    }

}

class Cricket extends Sport{
    int overs;
    int wicket;
    Cricket(int overs){
        super("Cricket", 11);
        this.overs = overs;;
    }

    // special implementation to start the game
    public void play() {
        System.out.println("Playing Cricket");
        this.wicket = 0;
    }

    // specific behaviour
    public void out(){
        this.wicket++;
    }
}

class Football extends Sport{
    int goals;
    Football(){
        super("Football", 11);
        this.goals = 0;
    }

    // special implementation to start the game
    public void play(){
        System.out.println("Playing Football");
        this.goals = 0;
    }

    // specific behaviour
    public void goal(){
        this.goals++;
    }
}
