package com.codingame.game;

public class Unit {

    public static final Unit NO_UNIT = new Unit(0, 0);

    int availableCount;
    int unavailableCount;

    public Unit(int availableCount, int unavailableCount) {
        this.availableCount = availableCount;
        this.unavailableCount = unavailableCount;
    }

    public int getStrength() {
        return availableCount;
    }

    public Unit add(int available, int unavailable) {
        return new Unit(availableCount + available, unavailableCount + unavailable);
    }

    public boolean isValid() {
        return this != NO_UNIT;
    }

    public void reset() {
        availableCount += unavailableCount;
        unavailableCount = 0;
    }

    public Unit remove(int n) {
        return add(-n, 0);
    }
}
