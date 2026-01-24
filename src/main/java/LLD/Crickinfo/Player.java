package LLD.Crickinfo;

import java.util.*;
import java.util.concurrent.*;

// --- Player & Team --- //
class Player {
    String name;
    String role;
    int runs;
    int wickets;

    Player(String name, String role) {
        this.name = name;
        this.role = role;
    }

    void updateStats(int runs, int wickets) {
        this.runs += runs;
        this.wickets += wickets;
    }

    public String toString() {
        return name + " (" + role + ") - Runs: " + runs + ", Wickets: " + wickets;
    }
}

class Team {
    String name;
    List<Player> players = new ArrayList<>();

    Team(String name) { this.name = name; }

    void addPlayer(Player player) { players.add(player); }

    public String toString() { return name; }
}

// --- Match Types --- //
enum MatchStatus { SCHEDULED, LIVE, COMPLETED }

abstract class Match {
    String matchId;
    Team teamA;
    Team teamB;
    Date date;
    MatchStatus status;
    Map<Player, Integer> playerRuns = new HashMap<>();
    int totalRuns = 0;
    int totalWickets = 0;
    String commentary;

    Match(String matchId, Team teamA, Team teamB, Date date) {
        this.matchId = matchId;
        this.teamA = teamA;
        this.teamB = teamB;
        this.date = date;
        this.status = MatchStatus.SCHEDULED;
        this.commentary = "";
        initPlayerRuns();
    }

    private void initPlayerRuns() {
        for (Player p : teamA.players) playerRuns.put(p, 0);
        for (Player p : teamB.players) playerRuns.put(p, 0);
    }

    synchronized void updateScore(Player player, int runs, boolean wicket, String newCommentary) {
        player.updateStats(runs, wicket ? 1 : 0);
        playerRuns.put(player, playerRuns.get(player) + runs);
        totalRuns += runs;
        if (wicket) totalWickets += 1;
        commentary = newCommentary;
        status = MatchStatus.LIVE;
        // Only show overall score in live updates
        System.out.println("[LIVE] " + getScore() + " | " + commentary);
    }

    synchronized void completeMatch() {
        status = MatchStatus.COMPLETED;
        System.out.println("[MATCH COMPLETED] " + this + " | Final Score: " + getScore());
    }

    String getScore() { return totalRuns + "/" + totalWickets; }

    abstract String matchType();

    public String toString() {
        return matchType() + ": " + teamA + " vs " + teamB + " | " + status + " | Score: " + getScore();
    }

    void displayPlayerRuns() {
        System.out.println("\n--- Player-wise Runs ---");
        for (Map.Entry<Player, Integer> entry : playerRuns.entrySet()) {
            System.out.println(entry.getKey().name + ": " + entry.getValue());
        }
    }
}

class ODI extends Match { ODI(String id, Team a, Team b, Date d) { super(id,a,b,d); } String matchType() { return "ODI"; } }
class T20 extends Match { T20(String id, Team a, Team b, Date d) { super(id,a,b,d); } String matchType() { return "T20"; } }
class TestMatch extends Match { TestMatch(String id, Team a, Team b, Date d) { super(id,a,b,d); } String matchType() { return "TEST"; } }

// --- Factory --- //
class MatchFactory {
    static Match createMatch(String type, String id, Team a, Team b, Date d) {
        switch(type.toUpperCase()) {
            case "ODI": return new ODI(id,a,b,d);
            case "T20": return new T20(id,a,b,d);
            case "TEST": return new TestMatch(id,a,b,d);
            default: throw new IllegalArgumentException("Unknown match type");
        }
    }
}

// --- Service Layer --- //
class CricInfoService {
    private static CricInfoService instance;
    private Map<String, Match> matches = new ConcurrentHashMap<>();
    private List<Team> teams = new CopyOnWriteArrayList<>();
    private CricInfoService() {}
    static CricInfoService getInstance() { if (instance==null) instance=new CricInfoService(); return instance; }

    void addTeam(Team t) { teams.add(t); }
    void addMatch(Match m) { matches.put(m.matchId,m); }

    List<Match> getUpcomingMatches() {
        List<Match> result = new ArrayList<>();
        for(Match m : matches.values()) if(m.status==MatchStatus.SCHEDULED) result.add(m);
        return result;
    }

    Match searchMatch(String id) { return matches.get(id); }

    List<Player> searchPlayer(String name) {
        List<Player> found = new ArrayList<>();
        for(Team t : teams)
            for(Player p : t.players)
                if(p.name.equalsIgnoreCase(name)) found.add(p);
        return found;
    }

    synchronized void updateLiveScore(String matchId, Player player, int runs, boolean wicket, String commentary) {
        Match match = matches.get(matchId);
        if(match!=null) match.updateScore(player,runs,wicket,commentary);
    }

    synchronized void completeMatch(String matchId) {
        Match match = matches.get(matchId);
        if(match!=null) match.completeMatch();
    }
}

// --- Live Score Simulator --- //
class LiveScoreSimulator implements Runnable {
    CricInfoService service;
    String matchId;
    List<Player> players;
    LiveScoreSimulator(CricInfoService s, String id, List<Player> players) {
        this.service=s; this.matchId=id; this.players=players;
    }

    public void run() {
        Random rand = new Random();
        try {
            for(int over=1; over<=5; over++) {
                for(Player p : players) {
                    int runs=rand.nextInt(7);
                    boolean wicket = rand.nextInt(20)==0;
                    service.updateLiveScore(matchId,p,runs,wicket,"Over "+over);
                    Thread.sleep(500);
                }
            }
            service.completeMatch(matchId);
        } catch(InterruptedException e) { e.printStackTrace(); }
    }
}

// --- Demo --- //
class CricInfoApp {
    public static void main(String[] args) {
        CricInfoService service = CricInfoService.getInstance();

        // Teams
        Team india = new Team("India");
        Player rohit = new Player("Rohit Sharma","Batsman");
        Player bumrah = new Player("Bumrah","Bowler");
        india.addPlayer(rohit); india.addPlayer(bumrah);

        Team aus = new Team("Australia");
        Player warner = new Player("Warner","Batsman");
        Player starc = new Player("Starc","Bowler");
        aus.addPlayer(warner); aus.addPlayer(starc);

        service.addTeam(india); service.addTeam(aus);

        // Create T20 match
        Match match = MatchFactory.createMatch("T20","M1",india,aus,new Date());
        service.addMatch(match);

        System.out.println("\n--- Upcoming Matches ---");
        for(Match m : service.getUpcomingMatches()) System.out.println(m);

        // Start live simulation
        List<Player> allPlayers = new ArrayList<>();
        allPlayers.addAll(india.players); allPlayers.addAll(aus.players);
        new Thread(new LiveScoreSimulator(service,"M1",allPlayers)).start();

        // Wait a bit and then display player-wise scores on demand
        try { Thread.sleep(4000); } catch(InterruptedException e) {}
        System.out.println("\n--- Player-wise Runs (on-demand) ---");
        match.displayPlayerRuns();
    }
}