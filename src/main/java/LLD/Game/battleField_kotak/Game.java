package LLD.Game.battleField_kotak;

import java.util.*;

class Coordinate{
    int x;
    int y;

    Coordinate(int x, int y){
        this.x = x;
        this.y = y;
    }
    public boolean equals(Object o){
        if (!(o instanceof Coordinate))
            return false;
        else{
            Coordinate c = (Coordinate) o;
            return c.x == x && c.y == y;
        }
    }

    public String toString(){
        return "(" + x + ", " + y + ")";
    }
}

enum User {
    PlayerA,
    PlayerB;
}

class Ship{
    String name; // id for the ship
    User user;
    Set<Coordinate> coordinatesOccupied = new HashSet<>();

    Ship(String name, User user){
        this.name = name;
        this.user = user;
    }

    boolean containsCoordinates(Coordinate coordinate){
        return coordinatesOccupied.contains(coordinate);
    }
}

class Player{
    String name;
    User user;
    Set<Ship> ships = new HashSet<>();
    Set<Coordinate> firedAt = new HashSet<>();
    // coords this player has fired at (on opponent side) because we need to keep track of fired coords

    Player(String name, User user){
        this.name = name;
        this.user = user;
    }

    int remainingShips(){
        return ships.size();
    }

    boolean hasShips(){
        return !ships.isEmpty();
    }
}

class Battlefield{
    int N;
    int rightStart; // Starting column index for player 2

    Battlefield(int N){
        this.N= N;
        this.rightStart = N/2;
    }

    boolean isBound(Coordinate c){
        return c.x >= 0 && c.x < N && c.y >= 0 && c.y < N;
    }
}
// Strategy Pattern
interface FireStrategy{
    Coordinate move(Player shooter, Player target, Battlefield bf, Random rnd);
}

class RandomFireStrategy implements FireStrategy{
    @Override
    public Coordinate move(Player shooter, Player target, Battlefield bf, Random rnd) {
            List<Coordinate> candidates = new ArrayList<>();
            int N = bf.N;
            int yStart = target.user == User.PlayerA ? 0 : bf.rightStart;
            int yEnd = target.user == User.PlayerB ? bf.rightStart - 1 : N - 1;

            // collect all target-half coordinates that shooter has not fired at yet
            for (int x = 0; x < N; x++) {
                for (int y = yStart; y <= yEnd; y++) {
                    Coordinate c = new Coordinate(x, y);
                    // coordinate which are not fired at yet
                    if (!shooter.firedAt.contains(c)) candidates.add(c);
                }
            }

            if (candidates.isEmpty()) return null; // no moves left
            return candidates.get(rnd.nextInt(candidates.size()));
    }
}

interface Game{
    void initGame(int n);
    void addShip(String id, int size, int xA, int yA, int xB, int yB);
    void viewBattleField();
    void startGame();
    void setStrategy(FireStrategy strategy);
}

class GameService implements Game{
    Player playerA;
    Player playerB;
    Battlefield bf;
    FireStrategy fireStrategy;
    Random rnd;

    @Override
    public void initGame(int n) {
        if (n <=1)
            System.out.println("N must be greater than 1");
        bf = new Battlefield(n);
        fireStrategy = new RandomFireStrategy();
        playerA = new Player("PlayerA", User.PlayerA);
        playerB = new Player("PlayerB", User.PlayerB);
        rnd = new Random();
    }

    @Override
    public void addShip(String id, int size, int xA, int yA, int xB, int yB) {

        Ship shipA = new Ship(id, User.PlayerA);
        Ship shipB = new Ship(id, User.PlayerB);
        Set<Coordinate> cellsA = computerCells(size, xA, yA);
        Set<Coordinate> cellsB = computerCells(size, xB, yB);

        // check if ship is valid
        shipA.coordinatesOccupied.addAll(cellsA);
        shipB.coordinatesOccupied.addAll(cellsB);

        // add to playerA coordinate
        playerA.ships.add(shipA);
        // add to playerB coordinates
        playerB.ships.add(shipB);
        return;
    }

    @Override
    public void startGame() {
        System.out.println("Starting game. PlayerA begins.");
        Player current = playerA;
        Player opponent = playerA;

        while (playerA.hasShips() && playerB.hasShips()) {
            // choose next coordinate using strategy
            Coordinate shot = fireStrategy.move(current, opponent, bf, rnd);
//            if (current == playerA)
//                shot = new Coordinate(3, 0);
//            else
//                shot = new Coordinate(1, 1);
//            shot = new Coordinate(5, 3);

            if (shot == null) {
                System.out.println(current.name + " has no valid moves left. Game ends in a draw.");
                return;
            }

            // record the shot to prevent repetitions
            current.firedAt.add(shot);

            // check if any opponent ship occupies this coordinate
            Ship destroyed = null;
            for (Ship s : new HashSet<>(opponent.ships)) {
                if (s.containsCoordinates(shot)) {
                    destroyed = s;
                    break;
                }
            }

            // print result and remove ship if hit
            if (destroyed != null) {
                opponent.ships.remove(destroyed); // entire ship destroyed on any hit
                System.out.println(current.name + " turn: Missile fired at " + shot + " : \"Hit\" : " + destroyed + " destroyed.");
            } else {
                System.out.println(current.name + " turn: Missile fired at " + shot + " : \"Miss\"");
            }

            // print remaining ships summary
            System.out.println("Ships Remaining - PlayerA: " + playerA.remainingShips() + ", PlayerB: " + playerB.remainingShips());
            System.out.println("----------------------------------------------------------------");

            // swap turns
            Player tmp = current;
            current = opponent;
            opponent = tmp;
        }

        // determine winner
        if (playerA.hasShips() && !playerB.hasShips()) System.out.println("GameOver. PlayerA wins.");
        else if (!playerA.hasShips() && playerB.hasShips()) System.out.println("GameOver. PlayerB wins.");
        else System.out.println("GameOver. Draw.");
    }


    private Set<Coordinate> computerCells(int size, int cx, int cy){
        // halfFloor and halfCeil split the side around center to support even/odd sizes
        int halfFloor = (size - 1) / 2;
        int halfCeil = size / 2;
        int x0 = cx - halfFloor;
        int x1 = cx + halfCeil;
        int y0 = cy - halfFloor;
        int y1 = cy + halfCeil;

        Set<Coordinate> cells = new HashSet<>();
        for (int x = x0; x <= x1; x++) {
            for (int y = y0; y <= y1; y++) {
                cells.add(new Coordinate(x, y));
            }
        }
        return cells;
    }

    @Override
    public void viewBattleField() {
        int n = bf.N;
        String[][] grid = new String[n][n];
    }

    @Override
    public void setStrategy(FireStrategy strategy) {
        this.fireStrategy = strategy;
    }
}


class Main{
    public static void main(String[] args) {
        GameService gameService = new GameService();

        // initiate the game
        gameService.initGame(6);

        // add the ships
        gameService.addShip("ship1", 2, 1, 5, 4, 4);

        // Start the game
        gameService.startGame();
    }
}



