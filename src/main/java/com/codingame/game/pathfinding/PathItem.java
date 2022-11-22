package com.codingame.game.pathfinding;

import com.codingame.game.Coord;

public class PathItem {
    public int cumulativeLength = 0;
    int totalPrevisionalLength = 0;
    PathItem precedent = null;
    Coord coord;

    public int getTotalPrevisionalLength() {
        return totalPrevisionalLength;
    }
}