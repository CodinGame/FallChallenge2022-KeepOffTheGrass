package com.codingame.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Grid {
    public int width;
    public int height;
    public Map<Coord, Cell> cells;
    Random random;
    private boolean ySymetry;
    List<List<Coord>> spawns;
    private boolean snaky;

    public Grid(Random random, List<Player> players) {
        this.random = random;
        snaky = random.nextDouble() < 0.1;
        width = randInt(Config.MAP_MIN_WIDTH, Config.MAP_MAX_WIDTH + 1);
        height = (int) (width * Config.MAP_ASPECT_RATIO);

        ySymetry = random.nextBoolean();

        cells = new HashMap<>();
        List<Coord> possibleSpawnPoints = new ArrayList<>();
        Stack<Coord> emptyCells = new Stack<>();

        Coord centre = new Coord(width / 2, height / 2);
        cells.clear();
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                Coord coord = new Coord(x, y);

                if (cells.containsKey(coord)) {
                    continue;
                }

                int durability = randomDurability(x, y, centre);
                Cell cell = new Cell(durability);
                cells.put(coord, cell);

                if (durability == 0) {
                    emptyCells.add(coord);
                }

                Coord opposite = opposite(coord);
                if (!opposite.equals(coord)) {
                    cells.put(opposite, new Cell(cell));
                    if (
                        durability == Config.CELL_MAX_DURABILITY && x > 1 && y > 1 && x < width - 1 && y < height - 1
                            && coord.manhattanTo(opposite) >= Config.MIN_SPAWN_DISTANCE
                            && coord.manhattanTo(opposite) >= (width * 0.5d)
                    ) {
                        possibleSpawnPoints.add(coord);
                    }
                }
            }
        }

        spawns = new ArrayList<>(2);
        if (possibleSpawnPoints.isEmpty()) {
            possibleSpawnPoints.add(new Coord(1, 1));
        }
        int cellIdx = randInt(possibleSpawnPoints.size());
        Coord spawnPoint = possibleSpawnPoints.get(cellIdx);
        Coord opposite = opposite(spawnPoint);

        Coord[] spawnPoints = random.nextBoolean()
            ? new Coord[] { spawnPoint, opposite }
            : new Coord[] { opposite, spawnPoint };

        for (int playerIdx = 0; playerIdx < players.size(); ++playerIdx) {
            List<Coord> unitSpawns = new ArrayList<>(8);

            Player player = players.get(playerIdx);
            Coord spawn = spawnPoints[playerIdx];
            Cell spawnCentre = cells.get(spawn);
            spawnCentre.setOwner(player);
            spawnCentre.garanteeNotHole();

            getCoordsAround(spawn).stream().forEach(coord -> {
                Cell cell = cells.get(coord);
                cell.garanteeNotHole();
                cell.setOwner(player);
                unitSpawns.add(coord);
            });

            spawns.add(unitSpawns);
        }

        emptyCells.removeAll(
            spawns
                .stream()
                .flatMap(n -> n.stream())
                .collect(Collectors.toList())
        );
        fixIslands(emptyCells);

    }

    private List<Set<Coord>> detectIslands() {
        List<Set<Coord>> islands = new ArrayList<>();
        Set<Coord> computed = new HashSet<>();
        Set<Coord> current = new HashSet<>();

        for (Coord p : cells.keySet()) {
            if (get(p).isHole()) {
                continue;
            }
            if (!computed.contains(p)) {
                Queue<Coord> fifo = new LinkedList<>();
                fifo.add(p);
                computed.add(p);

                while (!fifo.isEmpty()) {
                    Coord e = fifo.poll();
                    for (Coord delta : Adjacency.FOUR.deltas) {
                        Coord n = e.add(delta);
                        Cell cell = get(n);
                        if (cell.isValid() && !cell.isHole() && !computed.contains(n)) {
                            fifo.add(n);
                            computed.add(n);
                        }
                    }
                    current.add(e);
                }
                islands.add(new HashSet<>(current));
                current.clear();
            }
        }

        return islands;
    }

    private boolean closeIslandGap(Stack<Coord> emptyCells, List<Set<Coord>> islands) {
        List<Set<Coord>> connectingIslands = null;
        Coord bridge = null;

        for (Coord coord : emptyCells) {
            List<Coord> neighs = getNeighbours(coord);
            connectingIslands = neighs.stream()
                .map(n -> getIslandFrom(islands, n))
                .filter(opt -> opt.isPresent())
                .map(opt -> opt.get())
                .distinct()
                .collect(Collectors.toList());
            if (connectingIslands.size() > 1) {
                bridge = coord;
                break;
            }
        }

        if (bridge != null) {
            final List<Set<Coord>> bridging = connectingIslands;
            Coord coord = bridge;
            Coord opposite = opposite(coord);

            get(coord).setDurability(6);
            get(opposite).setDurability(6);

            emptyCells.remove(coord);
            emptyCells.remove(opposite);

            List<Set<Coord>> newIslands = islands.stream()
                .filter(set -> !bridging.contains(set))
                .collect(Collectors.toList());

            Set<Coord> newIsland = new HashSet<>();
            bridging.forEach(set -> newIsland.addAll(set));

            islands.clear();
            islands.addAll(newIslands);
            islands.add(newIsland);
            return true;
        }
        return false;
    }

    private void fixIslands(Stack<Coord> emptyCells) {
        Collections.shuffle(emptyCells, random);
        List<Set<Coord>> islands = detectIslands();

        while (islands.size() > 1) {
            boolean closed = closeIslandGap(emptyCells, islands);
            if (!closed) {
                break;
            }
        }

    }

    private Optional<Set<Coord>> getIslandFrom(List<Set<Coord>> islands, Coord coord) {
        return islands.stream()
            .filter(set -> set.contains(coord))
            .findFirst();
    }

    private int randomDurability(int x, int y, Coord centre) {
        double dist = new Coord(x, y).manhattanTo(centre);
        double maxDist = centre.manhattanTo(0, 0);
        double d = random.nextDouble();
        d *= d;
        double n = 1 / (1 + (1 - (dist / maxDist)) * 3);
        d = Math.pow(d, n);

        if (d < .15) {
            return 0;
        }

        if (d < .35) {
            return snaky ? 0 : 4;
        }
        if (d < .5) {
            return 6;
        }

        if (d < .8) {
            return 8;
        }
        if (d < .9) {
            return 9;
        }
        return Config.CELL_MAX_DURABILITY;

    }

    private List<Coord> getCoordsAround(Coord c) {
        return Stream.of(Adjacency.FOUR.deltas).map(delta -> c.add(delta)).collect(Collectors.toList());
    }

    private Coord opposite(Coord c) {
        return new Coord(width - c.x - 1, ySymetry ? (height - c.y - 1) : c.y);
    }

    private int randInt(int from, int to) {
        return random.nextInt(to - from) + from;
    }

    private int randInt(int to) {
        return randInt(0, to);
    }

    public boolean isYSymetric() {
        return ySymetry;
    }

    public List<Coord> getNeighbours(Coord pos) {
        List<Coord> neighs = new ArrayList<>();
        for (Coord delta : Adjacency.FOUR.deltas) {
            Coord n = pos.add(delta);
            if (get(n).isValid()) {
                neighs.add(n);
            }
        }
        return neighs;
    }

    public Cell get(Coord n) {
        return cells.getOrDefault(n, Cell.NO_CELL);

    }

    public Cell get(int x, int y) {
        return get(new Coord(x, y));
    }

    boolean isOwner(Coord coord, Player player) {
        return get(coord).isOwnedBy(player);
    }

    public List<Coord> getClosestTarget(Coord from, List<Coord> targets) {
        List<Coord> closest = new ArrayList<>();
        int closestBy = 0;
        for (Coord neigh : targets) {
            int distance = from.manhattanTo(neigh);
            if (closest.isEmpty() || closestBy > distance) {
                closest.clear();
                closest.add(neigh);
                closestBy = distance;
            } else if (!closest.isEmpty() && closestBy == distance) {
                closest.add(neigh);
            }
        }
        return closest;
    }

}
