package com.codingame.game.pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.function.Function;

import com.codingame.game.Coord;
import com.codingame.game.Grid;

public class AStar {
    Map<Coord, PathItem> closedList = new HashMap<>();
    PriorityQueue<PathItem> openList = new PriorityQueue<PathItem>(Comparator.comparingInt(PathItem::getTotalPrevisionalLength));
    List<PathItem> path = new ArrayList<PathItem>();

    Grid grid;
    Coord from;
    Coord target;
    Coord nearest;

    int dirOffset;
    private Function<Coord, Integer> weightFunction;
    private List<Coord> restricted;
    private double centreX, centreY;

    public AStar(Grid grid, Coord from, Coord target, Function<Coord, Integer> weightFunction, List<Coord> restricted) {
        this.grid = grid;
        this.from = from;
        this.target = target;
        this.weightFunction = weightFunction;
        this.nearest = from;
        this.restricted = restricted;
        this.centreX = grid.width / 2d;
        this.centreY = grid.height / 2d;
    }

    public List<PathItem> find() {
        PathItem item = getPathItemLinkedList();
        path.clear();
        if (item != null) {
            calculatePath(item);
        }
        return path;
    }

    void calculatePath(PathItem item) {
        PathItem i = item;
        while (i != null) {
            path.add(0, i);
            i = i.precedent;
        }
    }

    PathItem getPathItemLinkedList() {
        PathItem root = new PathItem();
        root.coord = this.from;
        openList.add(root);

        while (openList.size() > 0) {
            PathItem visiting = openList.remove();
            Coord visitingCoord = visiting.coord;

            if (visitingCoord.equals(target)) {
                return visiting;
            }
            if (closedList.containsKey(visitingCoord)) {
                continue;
            }
            closedList.put(visitingCoord, visiting);

            List<Coord> neighbors = grid.getNeighbours(visitingCoord);

            Comparator<Coord> byDistanceToCentre = Comparator.comparing(coord -> coord.sqrEuclideanTo(centreX, centreY));

            Collections.sort(neighbors, byDistanceToCentre);
            for (Coord neighbor : neighbors) {
                if (!grid.get(neighbor).isHole() && !restricted.contains(neighbor)) {
                    addToOpenList(visiting, visitingCoord, neighbor);
                }
            }

            int visitingDist = visitingCoord.manhattanTo(target);
            int nearestDist = nearest.manhattanTo(target);

            if (visitingDist < nearestDist) {
                this.nearest = visitingCoord;
            } else if (visitingDist == nearestDist) {
                if (byDistanceToCentre.compare(visitingCoord, nearest) < 0) {
                    this.nearest = visitingCoord;
                }
            }
        }
        return null; // not found !
    }

    void addToOpenList(PathItem visiting, Coord fromCoord, Coord toCoord) {
        if (closedList.containsKey(toCoord)) {
            return;
        }
        PathItem pi = new PathItem();
        pi.coord = toCoord;
        pi.cumulativeLength = visiting.cumulativeLength + weightFunction.apply(toCoord);
        int manh = fromCoord.manhattanTo(toCoord);
        pi.totalPrevisionalLength = pi.cumulativeLength + manh;
        pi.precedent = visiting;
        openList.add(pi);
    }

    public Coord getNearest() {
        return nearest;
    }

}
