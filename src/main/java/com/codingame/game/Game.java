package com.codingame.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.codingame.event.Animation;
import com.codingame.event.EventData;
import com.codingame.game.action.Action;
import com.codingame.game.exception.GameException;
import com.codingame.game.pathfinding.PathFinder;
import com.codingame.game.pathfinding.PathFinder.PathFinderResult;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.endscreen.EndScreenModule;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class Game {
    @Inject private MultiplayerGameManager<Player> gameManager;
    @Inject private EndScreenModule endScreenModule;
    @Inject private PathFinder pathfinder;
    @Inject private Animation animation;
    @Inject private GameSummaryManager gameSummaryManager;

    private Random random;

    public Grid grid;
    public List<Recycler> recyclers;
    List<Player> players;

    private Set<Coord> fightLocations;
    private List<EventData> viewerEvents;

    private int gameTurn;
    private int earlyFinishCounter = Config.EARLY_FINISH_TURNS;
    private int unitStartTime;

    public void init() {
        players = gameManager.getPlayers();
        random = gameManager.getRandom();
        grid = new Grid(random, players);
        recyclers = new ArrayList<>();
        fightLocations = new HashSet<>();
        viewerEvents = new ArrayList<>();
        gameTurn = 0;
        initPlayers();
    }

    private void resetEarlyTurnCounter() {
        earlyFinishCounter = Config.EARLY_FINISH_TURNS;
    }

    private void initPlayers() {
        for (Player player : gameManager.getActivePlayers()) {
            player.money = Config.PLAYER_STARTING_MONEY;
            if (grid.spawns.size() > player.getIndex()) {
                grid.spawns.get(player.getIndex())
                    .stream()
                    .forEach(coord -> {
                        player.placeStartUnit(coord);
                    });
            }
        }
    }

    public void resetGameTurnData() {
        viewerEvents.clear();
        animation.reset();
        players.stream().forEach(Player::reset);
        fightLocations.clear();
    }

    public List<String> getGlobalInfoFor(Player player) {
        List<Object> lines = new ArrayList<>();
        lines.add(Referee.join(grid.width, grid.height));
        return lines.stream()
            .map(String::valueOf)
            .collect(Collectors.toList());
    }

    public List<String> getCurrentFrameInfoFor(Player player) {
        Set<Coord> coordsInRangeOfRecyclers = recyclers.stream()
            .flatMap(recycler -> getWithinRange(recycler.getCoord()).stream())
            .filter(coord -> !grid.get(coord).isHole())
            .collect(Collectors.toSet());

        List<String> lines = new ArrayList<>();
        Player other = players
            .stream()
            .filter(p -> p != player)
            .findFirst().get();
        lines.add(Referee.join(player.money, other.money));

        for (int y = 0; y < grid.height; ++y) {
            for (int x = 0; x < grid.width; ++x) {
                Coord coord = new Coord(x, y);
                Cell c = grid.get(coord);
                int durability = c.getDurability();
                int ownerIdx = c.getOwner()
                    .map(owner -> owner == player ? 1 : 0)
                    .orElse(-1);
                Unit myUnit = player.getUnitAt(x, y);
                Unit foeUnit = other.getUnitAt(x, y);
                Optional<Recycler> excavator = getExcavatorAt(x, y);

                int unitStrength = ownerIdx == 1 ? myUnit.getStrength() : foeUnit.getStrength();

                boolean canBuildHere = ownerIdx == 1 && !excavator.isPresent() && unitStrength == 0;
                boolean canSpawnHere = ownerIdx == 1 && !excavator.isPresent();
                boolean willGetDamaged = coordsInRangeOfRecyclers.contains(coord);

                String row = Referee.join(
                    durability,
                    ownerIdx,
                    unitStrength,
                    excavator.map(e -> 1).orElse(0),
                    canBuildHere ? 1 : 0,
                    canSpawnHere ? 1 : 0,
                    willGetDamaged ? 1 : 0

                );
                lines.add(row);
            }
        }

        return lines.stream().map(String::valueOf).collect(Collectors.toList());

    }

    public void performGameUpdate(int frameIdx) {
        doBuilds();
        doUnits();
        animation.catchUp();
        doRecycle();

        gameTurn++;
        earlyFinishCounter--;
        doPassiveIncome();
        if (checkGameOver()) {
            gameManager.endGame();
        }

        gameManager.addToGameSummary(gameSummaryManager.toString());
        gameSummaryManager.clear();

        computeEvents();
    }

    private void doPassiveIncome() {
        int cellCount = (int) grid.cells.values()
            .stream()
            .filter(c -> !c.isHole())
            .count();
        for (Player p : players) {
            int passiveIncome = Config.PLAYER_MINIMAL_INCOME;
            p.money += passiveIncome;
        }

    }

    private List<Coord> getWithinRange(Coord coord) {
        List<Coord> inRange = grid.getNeighbours(coord);
        inRange.add(coord);
        return inRange;
    }

    private boolean doRecycle() {
        Map<Integer, Set<Coord>> excavatedByPlayer = players.stream()
            .collect(Collectors.toMap(Player::getIndex, p -> Sets.newHashSet()));

        for (Recycler e : recyclers) {
            AtomicInteger nbExcavatedByRecycler = new AtomicInteger(0);
            Collection<Coord> coords = getWithinRange(e.getCoord());
            coords.stream().forEach(coord -> {
                Cell cell = grid.get(coord);
                if (!cell.isHole()) {
                    if (!excavatedByPlayer.get(e.getOwnerIdx()).contains(coord)) { // Prevents counting twice the same tile for matterCollection animation
                        nbExcavatedByRecycler.getAndIncrement();
                    }
                    excavatedByPlayer.get(e.getOwnerIdx()).add(coord);
                }
            });
            if (nbExcavatedByRecycler.get() > 0) {
                launchMatterCollectEvent(e.getCoord(), nbExcavatedByRecycler.get() * Config.RECYCLER_INCOME, e.getOwnerIdx());
            }
        }

        players.stream().forEach(player -> {
            Set<Coord> coords = excavatedByPlayer.get(player.getIndex());
            int income = coords.size() * Config.RECYCLER_INCOME;
            player.money += income;
        });

        List<Coord> destroyedCells = new ArrayList<>();
        players.stream()
            .flatMap(player -> excavatedByPlayer.get(player.getIndex()).stream())
            .distinct()
            .forEach(coord -> {
                Cell cell = grid.get(coord);
                boolean broken = cell.damage();
                launchCellDamageEvent(cell, coord);
                if (broken) {
                    destroyedCells.add(coord);
                }
                resetEarlyTurnCounter();
            });

        animation.wait(Animation.THIRD);

            animation.catchUp();
        
        recyclers.stream()
            .filter(e -> destroyedCells.contains(e.getCoord()))
            .forEach(r -> {
                launchRecyclerFallEvent(r);
            });
        recyclers.removeIf(e -> destroyedCells.contains(e.getCoord()));

        players.forEach(p -> {
            destroyedCells.forEach(coord -> {
                Unit u = p.getUnitAt(coord);
                if (u.availableCount > 0) {
                    p.units.remove(coord);
                    launchUnitFallEvent(coord, u, p);
                }
            });
        });
        return excavatedByPlayer.values()
            .stream()
            .anyMatch(set -> !set.isEmpty());
    }

    private void launchUnitFallEvent(Coord coord, Unit u, Player player) {
        EventData e = new EventData();
        e.type = EventData.UNIT_FALL;
        e.coord = coord;
        e.amount = u.getStrength();
        e.playerIndex = player.getIndex();
        animation.startAnim(e.animData, Animation.THIRD);
        animation.wait(Animation.TWENTIETH);
        viewerEvents.add(e);
    }

    private void launchRecyclerFallEvent(Recycler r) {
        EventData e = new EventData();
        e.type = EventData.RECYCLER_FALL;
        e.coord = r.getCoord();
        e.playerIndex = r.getOwnerIdx();
        animation.startAnim(e.animData, Animation.THIRD);
        animation.wait(Animation.TWENTIETH);
        viewerEvents.add(e);
    }

    private boolean doBuilds() {
        boolean buildHappened = false;
        int buildStartTime = animation.getFrameTime();

        for (Player player : players) {
            animation.setFrameTime(buildStartTime);
            for (Action build : player.builds) {
                Coord buildTarget = build.getCoord();

                try {
                    if (!grid.isOwner(buildTarget, player)) {
                        throw new GameException(
                            String.format("tried to build a recycler at (%d, %d), which is not owned by the player", buildTarget.x, buildTarget.y)
                        );
                    } else if (getRecyclerAt(buildTarget).isPresent()) {
                        throw new GameException(
                            String.format("tried to build a recycler at (%d, %d), into another recycler", buildTarget.x, buildTarget.y)
                        );
                    } else if (unitExistsAt(buildTarget)) {
                        throw new GameException(
                            String.format("tried to build a recycler at (%d, %d), where units are already present", buildTarget.x, buildTarget.y)
                        );
                    } else if (player.money < Config.COST_RECYCLER) {
                        throw new GameException(
                            String.format("tried to build a recycler at (%d, %d), but has not enough matter", buildTarget.x, buildTarget.y)
                        );
                    } else {
                        recyclers.add(new Recycler(buildTarget, player));
                        player.money -= Config.COST_RECYCLER;

                        launchBuildEvent(build, player);
                        buildHappened = true;
                    }
                } catch (GameException e) {
                    gameSummaryManager.addError(player, e.getMessage());
                }
            }
        }

        animation.catchUp();
        return buildHappened;
    }

    private boolean unitExistsAt(Coord coord) {
        return players.stream()
            .anyMatch(
                p -> p.getUnitAt(coord).isValid()
            );
    }

    private boolean doUnits() {
        doSpawn();
        animation.catchUp();
        doMove();
        animation.catchUp();

        resetUnits();
        doFights();

        return !fightLocations.isEmpty();
    }

    private double getOwnedCells(Player player) {
        return grid.cells.values()
            .stream()
            .filter(cell -> cell.isOwnedBy(player))
            .count();
    }

    private boolean mapIsFinal() {
        for (Entry<Coord, Cell> entry : grid.cells.entrySet()) {
            Cell cell = entry.getValue();
            Coord coord = entry.getKey();

            Optional<Player> owner = cell.getOwner();
            if (owner.isPresent()) {
                List<Coord> neighs = grid.getNeighbours(coord);
                boolean hasSomethingToCapture = neighs
                    .stream()
                    .filter(n -> !grid.get(n).isHole())
                    .anyMatch(n -> !grid.isOwner(n, owner.get()));
                if (hasSomethingToCapture) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkGameOver() {
        if (earlyFinishCounter <= 0) {
            Optional<Player> winner = players
                .stream()
                .max((a, b) -> (int) (getOwnedCells(a) - getOwnedCells(b)));
            gameManager.addTooltip(
                winner.get(), String.format(
                    "Field stable for %d turns. %s wins!",
                    Config.EARLY_FINISH_TURNS,
                    winner.get().getNicknameToken()
                )
            );
            return true;
        }

        return players.stream().anyMatch(p -> {
            double ownedCells = getOwnedCells(p);

            if (ownedCells == 0) {
                Player winner = players
                    .stream()
                    .max((a, b) -> (int) (getOwnedCells(a) - getOwnedCells(b)))
                    .get();

                gameManager.addTooltip(
                    winner, String.format(
                        "%s has no more cells and is disqualified",
                        p.getNicknameToken()
                    )
                );
                return true;
            }

            return false;
        });
    }

    //Eloi idea for future version of game: new action: "explode" a cell, moving cell hp to neighbours
    

    //TODO: leagues and bosses
    //TODO: FR statment
    //TODO: Starter AI
    

    private void resetUnits() {
        for (Player player : players) {
            player.resetUnits();
        }

    }

    private void doFights() {
        for (Coord coord : fightLocations) {
            Player player = players.get(0);
            Player other = players.get(1);
            int playerUnitStrength = player.getUnitAt(coord).availableCount;
            int otherUnitStrength = other.getUnitAt(coord).availableCount;

            if (playerUnitStrength > 0 && otherUnitStrength > 0) {
                Player winner = null;
                if (playerUnitStrength > otherUnitStrength) {
                    winner = player;
                } else if (otherUnitStrength > playerUnitStrength) {
                    winner = other;
                }
                launchFightEvent(coord, winner, Math.abs(playerUnitStrength - otherUnitStrength));
            }

            player.removeUnits(coord, otherUnitStrength);
            other.removeUnits(coord, playerUnitStrength);

            Cell cell = grid.get(coord);
            if (playerUnitStrength > otherUnitStrength && !cell.isOwnedBy(player)) {
                cell.setOwner(player);
                launchChangeOwnerEvent(coord, player);
                resetEarlyTurnCounter();
            } else if (otherUnitStrength > playerUnitStrength && !cell.isOwnedBy(other)) {
                cell.setOwner(other);
                launchChangeOwnerEvent(coord, other);
                resetEarlyTurnCounter();
            }
        }

    }

    private class CoordTuple {
        Coord a, b;

        public CoordTuple(Coord a, Coord b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public int hashCode() {
            return Objects.hash(a, b);
        }

        @Override
        public boolean equals(Object obj) {
            CoordTuple other = (CoordTuple) obj;
            return Objects.equals(a, other.a) && Objects.equals(b, other.b);
        }

    }

    private void doMove() {
        List<Coord> restricted = grid.cells.entrySet().stream()
            .filter(e -> getRecyclerAt(e.getKey()).isPresent())
            .map(e -> e.getKey())
            .collect(Collectors.toList());

        int moveStartTime = animation.getFrameTime();
        for (Player player : gameManager.getActivePlayers()) {
            animation.setFrameTime(moveStartTime);

            Map<CoordTuple, Integer> actualMoves = Maps.newHashMap();

            for (Action move : player.moves) {
                Coord origin = move.getOriginCoord();
                Coord target = move.getCoord();
                Unit originUnit = player.getUnitAt(origin);

                try {
                    if (originUnit.availableCount < move.getAmount()) {
                        throw new GameException(
                            String.format(
                                "tried to move %d units from (%d, %d) where only %d were available", move.getAmount(), origin.x, origin.y,
                                originUnit.availableCount
                            )
                        );
                    } else if (origin.equals(target)) {
                        throw new GameException(
                            String.format("tried to move %d units from (%d, %d) to the same tile", move.getAmount(), origin.x, origin.y)
                        );
                    } else if (move.getAmount() <= 0) {
                        throw new GameException(String.format("tried to move a null amount of units from (%d, %d)", origin.x, origin.y));
                    } else {
                        PathFinderResult pfr = pathfinder.setGrid(grid)
                            .restrict(restricted)
                            .from(origin)
                            .to(target)
                            .findPath();
                        List<Coord> wholePath = pfr.path;

                        if (wholePath.size() > 1) {
                            Coord step = wholePath.get(1);

                            originUnit.availableCount -= move.getAmount();
                            player.placeUnits(step, move.getAmount());

                            CoordTuple key = new CoordTuple(origin, step);
                            actualMoves.compute(
                                key,
                                (k, v) -> (v == null ? 0 : v) + move.getAmount()
                            );
                        }
                    }
                } catch (GameException e) {
                    gameSummaryManager.addError(player, e.getMessage());
                }

            }
            actualMoves.entrySet().stream()
                .forEach(e -> {
                    CoordTuple path = e.getKey();
                    Integer amount = e.getValue();

                    launchMoveEvent(path.a, path.b, amount, player);
                    fightLocations.add(path.b);
                });
        }
    }

    private void launchBuildEvent(Action build, Player player) {
        EventData e = new EventData();
        e.type = EventData.BUILD;
        e.playerIndex = player.getIndex();
        e.coord = build.getCoord();
        animation.startAnim(e.animData, 2 * Animation.THIRD);
        animation.wait(Animation.THIRD);
        viewerEvents.add(e);
    }

    private void launchMoveEvent(Coord from, Coord to, int amount, Player player) {
        EventData e = new EventData();
        e.type = EventData.MOVE;
        e.playerIndex = player.getIndex();
        e.coord = from;
        e.target = to;
        e.amount = amount;
        animation.startAnim(e.animData, Animation.HALF);
        animation.wait(Animation.TWENTIETH);
        viewerEvents.add(e);
    }

    private void launchCellDamageEvent(Cell cell, Coord coord) {
        EventData e = new EventData();
        e.type = EventData.CELL_DAMAGE;
        e.coord = coord;
        e.amount = cell.getDurability();
        animation.startAnim(e.animData, Animation.HALF);
        animation.wait(Animation.HUNDREDTH);
        viewerEvents.add(e);
    }

    private void launchMatterCollectEvent(Coord coord, int amount, int playerIdx) {
        EventData e = new EventData();
        e.type = EventData.MATTER_COLLECT;
        e.playerIndex = playerIdx;
        e.coord = coord;
        e.amount = amount;
        animation.startAnim(e.animData, Animation.WHOLE);
        animation.wait(Animation.HUNDREDTH);
        viewerEvents.add(e);
    }

    private void launchFightEvent(Coord coord, Player survivor, int amount) {
        EventData e = new EventData();
        e.type = EventData.FIGHT;
        e.playerIndex = survivor == null ? 2 : survivor.getIndex();
        e.coord = coord;
        e.amount = amount;
        animation.startAnim(e.animData, Animation.THIRD);
        viewerEvents.add(e);
    }

    private void launchSpawnEvent(Coord coord, int amount, Player player) {
        EventData e = new EventData();
        e.type = EventData.SPAWN;
        e.playerIndex = player.getIndex();
        e.coord = coord;
        e.amount = amount;
        animation.startAnim(e.animData, 2 * Animation.THIRD);
        animation.wait(Animation.TENTH);
        viewerEvents.add(e);
    }

    private void launchChangeOwnerEvent(Coord coord, Player player) {
        EventData e = new EventData();
        e.type = EventData.CELL_OWNER_SWAP;
        e.playerIndex = player.getIndex();
        e.coord = coord;
        animation.startAnim(e.animData, Animation.THIRD);
        viewerEvents.add(e);
    }

    private void doJump() {
        for (Player player : gameManager.getActivePlayers()) {
            animation.setFrameTime(this.unitStartTime);
            for (Action warp : player.warps) {
                Coord origin = warp.getOriginCoord();
                Coord target = warp.getCoord();
                Unit originUnit = player.getUnitAt(origin);

                int cost = getWarpCost(origin, target, player);

                try {
                    if (getRecyclerAt(target).isPresent()) {
                        throw new GameException(
                            String.format("tried to warp %d units from (%d, %d) into a recycler", warp.getAmount(), origin.x, origin.y)
                        );
                    } else if (originUnit.availableCount < warp.getAmount()) {
                        throw new GameException(
                            String.format(
                                "tried to warp %d units from (%d, %d) where only %d were available", warp.getAmount(), origin.x, origin.y,
                                originUnit.availableCount
                            )
                        );
                    } else if (!grid.get(target).isValid()) {
                        throw new GameException(
                            String.format("tried to warp %d units from (%d, %d) to oblivion", warp.getAmount(), origin.x, origin.y)
                        );
                    } else if (grid.get(target).isHole()) {
                        throw new GameException(
                            String.format(
                                "tried to warp %d units from (%d, %d) to a grass tile in (%d, %d)", warp.getAmount(), origin.x, origin.y, target.x,
                                target.y
                            )
                        );
                    } else if (player.warpCooldown > 0) {
                        throw new GameException(
                            String.format(
                                "tried to warp %d units from (%d, %d) while the cooldown wasn't completed", warp.getAmount(), origin.x, origin.y
                            )
                        );
                    } else if (origin.equals(target)) {
                        throw new GameException(
                            String.format("tried to warp %d units from (%d, %d) to the same tile", warp.getAmount(), origin.x, origin.y)
                        );
                    } else {
                        originUnit.availableCount -= warp.getAmount();
                        player.placeUnits(target, warp.getAmount());
                        fightLocations.add(target);

                        launchMoveEvent(origin, target, warp.getAmount(), player);
                        player.warpCooldown = cost;
                    }
                } catch (GameException e) {
                    gameSummaryManager.addError(player, e.getMessage());
                }
            }
            if (player.warpCooldown > 0) {
                player.warpCooldown--;
                resetEarlyTurnCounter();
            }
        }
    }

    private void doSpawn() {
        int spawnStartTime = animation.getFrameTime();
        for (Player player : gameManager.getActivePlayers()) {
            animation.setFrameTime(spawnStartTime);

            Map<Coord, Integer> actualSpawns = Maps.newHashMap();

            for (Action spawn : player.spawns) {
                int spawnCost = Config.COST_UNIT * spawn.getAmount();
                Coord target = spawn.getCoord();

                try {
                    if (!grid.isOwner(target, player)) {
                        throw new GameException(
                            String.format(
                                "tried to spawn %d units at (%d, %d), which is not owned by the player", spawn.getAmount(), target.x, target.y
                            )
                        );
                    } else if (getRecyclerAt(target).isPresent()) {
                        throw new GameException(
                            String.format("tried to spawn %d units at (%d, %d), into a recycler", spawn.getAmount(), target.x, target.y)
                        );
                    } else if (player.money / Config.COST_UNIT < spawn.getAmount()) {
                        throw new GameException(
                            String.format("tried to spawn %d units at (%d, %d), but has not enough matter", spawn.getAmount(), target.x, target.y)
                        );
                    } else if (spawn.getAmount() == 0) {
                        throw new GameException(
                            String.format("tried to spawn a null amount of units at (%d, %d)", target.x, target.y)
                        );
                    } else {
                        player.placeUnits(target, spawn.getAmount());
                        player.money -= spawnCost;
                        actualSpawns.compute(
                            target,
                            (k, v) -> (v == null ? 0 : v) + spawn.getAmount()
                        );
                    }
                } catch (GameException e) {
                    gameSummaryManager.addError(player, e.getMessage());
                }
            }

            actualSpawns.forEach((coord, amount) -> {
                launchSpawnEvent(coord, amount, player);
                fightLocations.add(coord);
            });

        }
    }

    public void onEnd() {
        players.stream().forEach(p -> {
            if (p.isActive()) {
                p.setScore((int) getOwnedCells(p));
            } else {
                p.setScore(-1);
            }
        });
        endScreenModule.setScores(
            players.stream()
                .mapToInt(p -> p.getScore())
                .toArray()
        );
    }

    public Optional<Recycler> getRecyclerAt(Coord coord) {
        return recyclers.stream().filter(e -> e.coord.equals(coord)).findFirst();
    }

    private Optional<Recycler> getExcavatorAt(int x, int y) {
        Coord coord = new Coord(x, y);
        return getRecyclerAt(coord);
    }

    public int getWarpCost(Coord origin, Coord target, Player player) {
        Optional<Player> targetOwner = grid.get(target).getOwner();
        double coeff;
        if (!targetOwner.isPresent()) {
            coeff = 1;
        } else if (targetOwner.get() == player) {
            coeff = 0.5;
        } else {
            coeff = 2;
        }
        return Math.max(0, (int) (Config.COST_WARP * (origin.manhattanTo(target) - 1) * coeff));
    }

    private void computeEvents() {
        int minTime = 1000;

        animation.catchUp();

        int frameTime = Math.max(
            animation.getFrameTime(),
            minTime
        );
        gameManager.setFrameDuration(frameTime);

    }

    public boolean isKeyFrame() {
        return true;
    }

    public List<EventData> getViewerEvents() {
        return viewerEvents;
    }

    public static String getExpected(String playerOutput) {
        String attempt = playerOutput.toUpperCase();
        if (attempt.startsWith("MOVE")) {
            return "MOVE <n> <x1> <y1> <x2> <y2>";
        }
        if (attempt.startsWith("SPAWN")) {
            return "SPAWN <n> <x> <y>";
        }
        if (attempt.startsWith("BUILD")) {
            return "BUILD <n> <x> <y>";
        }
        if (attempt.startsWith("MESSAGE")) {
            return "MESSAGE <text>";
        }
        if (attempt.startsWith("WAIT")) {
            return "WAIT";
        }
        return "MOVE |"
            + " SPAWN |"
            + " BUILD |"
            + " MESSAGE |"
            + " WAIT";
    }

}
