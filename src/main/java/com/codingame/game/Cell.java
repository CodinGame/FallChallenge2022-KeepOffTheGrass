package com.codingame.game;

import java.util.Optional;

public class Cell {
    public static final Cell NO_CELL = new Cell() {
        @Override
        public boolean isValid() {
            return false;
        }
    };

    public boolean isValid() {
        return true;
    }

    private int durability;
    private Player owner;

    public Cell(int durability) {
        this.durability = durability;
    }

    public Cell() {
        this(0);
    }

    public Cell(Cell cell) {
        this(cell.durability);
    }

    public boolean isHole() {
        return durability == 0;
    }

    public void garanteeNotHole() {
        if (isHole()) {            
            this.durability = 1;
        }

    }

    public void setOwner(Player player) {
        this.owner = player;
    }

    public Optional<Player> getOwner() {
        return Optional.ofNullable(owner);
    }

    public int getDurability() {
        return durability;
    }

    public boolean damage() {
        durability--;
        if (durability == 0) {
            owner = null;
            return true;
        }
        return false;

    }

    public boolean isOwnedBy(Player p) {
        return owner == p;
    }

    public void setDurability(int durability) {
        this.durability = durability;

    }
}
