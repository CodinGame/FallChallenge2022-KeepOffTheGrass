package com.codingame.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.codingame.game.action.Action;
import com.codingame.gameengine.core.AbstractMultiplayerPlayer;

public class Player extends AbstractMultiplayerPlayer {

    int money, warpCooldown;
    Map<Coord, Unit> units;
    String message;
    List<Action> builds;
    List<Action> spawns;
    List<Action> moves;
    List<Action> warps;

    public Player() {
        units = new HashMap<>();
        builds = new ArrayList<>();
        spawns = new ArrayList<>();
        moves = new ArrayList<>();
        warps = new ArrayList<>();
        warpCooldown = 0;
    }

    @Override
    public int getExpectedOutputLines() {
        return 1;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        if (message != null) {
            String trimmed = message.trim();
            if (trimmed.length() > 48) {
                trimmed = trimmed.substring(0, 46) + "...";
            }
            if (trimmed.length() > 0) {
                this.message = trimmed;
            }
        }
    }

    public void reset() {
        message = null;
        moves.clear();
        builds.clear();
        spawns.clear();
        warps.clear();
    }

    public Map<Coord, Unit> getUnits() {
        return units;
    }

    public Unit getUnitAt(Coord coord) {
        return units.getOrDefault(coord, Unit.NO_UNIT);
    }

    public Unit getUnitAt(int x, int y) {
        return getUnitAt(new Coord(x, y));
    }

    public void addAction(Action action) {
        switch (action.getType()) {
        case BUILD:
            builds.add(action);
            break;
        case MOVE:
            moves.add(action);
            break;
        case SPAWN:
            spawns.add(action);
            break;
        case MESSAGE:
            setMessage(action.getMessage());
            break;
        default:
            break;
        }
    }

    public void placeStartUnit(Coord coord) {
        units.put(coord, new Unit(1, 0));
    }

    public void placeUnits(Coord target, int amount) {
        units.put(target, getUnitAt(target).add(0, amount));
    }

    public void resetUnits() {
        Set<Coord> coords = units.keySet().stream().collect(Collectors.toSet());
        for (Coord coord : coords) {
            Unit u = units.get(coord);
            u.reset();
            if (u.availableCount <= 0) {
                units.remove(coord);
            }
        }

    }

    public void removeUnits(Coord coord, int n) {
        if (n == 0) {
            return;
        }
        units.compute(coord, (k, v) -> {
            Unit unit = getUnitAt(coord);
            if (unit.availableCount - n <= 0) {
                return null;
            }
            return v.remove(n);
        });

    }

    public int getMoney() {
        return money;
    }

    public int getWarpCooldown() {
        return warpCooldown;
    }

}
