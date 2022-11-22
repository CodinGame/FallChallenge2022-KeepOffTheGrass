import java.util.*;
import java.util.stream.*;

class Tile {
    final int x, y, scrapAmount, owner, units;
    final boolean recycler, canBuild, canSpawn, inRangeOfRecycler;

    public Tile(int x, int y, int scrapAmount, int owner, int units, boolean recycler, boolean canBuild, boolean canSpawn,
            boolean inRangeOfRecycler) {
        this.x = x;
        this.y = y;
        this.scrapAmount = scrapAmount;
        this.owner = owner;
        this.units = units;
        this.recycler = recycler;
        this.canBuild = canBuild;
        this.canSpawn = canSpawn;
        this.inRangeOfRecycler = inRangeOfRecycler;
    }
}

class Player {

    static final int ME = 1;
    static final int OPP = 0;
    static final int NOONE = -1;

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int width = in.nextInt();
        int height = in.nextInt();

        // game loop
        while (true) {
            List<Tile> tiles = new ArrayList<>();
            List<Tile> myTiles = new ArrayList<>();
            List<Tile> oppTiles = new ArrayList<>();
            List<Tile> neutralTiles = new ArrayList<>();
            List<Tile> myUnits = new ArrayList<>();
            List<Tile> oppUnits = new ArrayList<>();
            List<Tile> myRecyclers = new ArrayList<>();
            List<Tile> oppRecyclers = new ArrayList<>();

            int myMatter = in.nextInt();
            int oppMatter = in.nextInt();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Tile tile = new Tile(
                            x,
                            y,
                            in.nextInt(),
                            in.nextInt(),
                            in.nextInt(),
                            in.nextInt() == 1,
                            in.nextInt() == 1,
                            in.nextInt() == 1,
                            in.nextInt() == 1);

                    tiles.add(tile);
                    if (tile.owner == ME) {
                        myTiles.add(tile);
                        if (tile.units > 0) {
                            myUnits.add(tile);
                        } else if (tile.recycler) {
                            myRecyclers.add(tile);
                        }
                    } else if (tile.owner == OPP) {
                        oppTiles.add(tile);
                        if (tile.units > 0) {
                            oppUnits.add(tile);
                        } else if (tile.recycler) {
                            oppRecyclers.add(tile);
                        }
                    } else {
                        neutralTiles.add(tile);
                    }
                }
            }

            List<String> actions = new ArrayList<>();

            for (Tile tile : myTiles) {
                if (tile.canSpawn) {
                    int amount = 0; // TODO: pick amount of robots to spawn here
                    if (amount > 0) {
                        actions.add(String.format("SPAWN %d %d %d", amount, tile.x, tile.y));
                    }
                }
                if (tile.canBuild) {
                    boolean shouldBuild = false; // TODO: pick whether to build recycler here
                    if (shouldBuild) {
                        actions.add(String.format("BUILD %d %d", tile.x, tile.y));
                    }
                }
            }

            for (Tile tile : myUnits) {
                Tile target = null; // TODO: pick a destination
                if (target != null) {
                    int amount = 0; // TODO: pick amount of units to move
                    actions.add(String.format("MOVE %d %d %d %d %d", amount, tile.x, tile.y, target.x, target.y));
                }
            }

            // To debug: System.err.println("Debug messages...");
            if (actions.isEmpty()) {
                System.out.println("WAIT");
            } else {
                actions.stream().collect(Collectors.joining(";"));
            }
        }
    }
}