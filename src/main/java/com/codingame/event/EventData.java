package com.codingame.event;

import java.util.ArrayList;
import java.util.List;

import com.codingame.game.Coord;

public class EventData {
    public static final int BUILD = 0;
    public static final int MOVE = 1;
    public static final int JUMP = 2;
    public static final int SPAWN = 3;
    public static final int FIGHT = 4;
    public static final int CELL_DAMAGE = 7;
    public static final int RECYCLER_FALL = 8;
    public static final int CELL_OWNER_SWAP = 9;
    public static final int UNIT_FALL = 10;
    public static final int MATTER_COLLECT = 11;

    public int type;
    public List<AnimationData> animData;
    
    public Integer playerIndex, amount;
    public Coord coord, target;

    public EventData() {
        animData = new ArrayList<>();
    }

}
