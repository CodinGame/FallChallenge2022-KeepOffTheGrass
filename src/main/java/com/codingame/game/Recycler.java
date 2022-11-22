package com.codingame.game;

// Recyclotron
public class Recycler {

    public Coord coord;
    public Player owner;

    public Recycler(Coord coord, Player owner) {
        this.coord = coord;
        this.owner = owner;
    }

    public int getOwnerIdx() {
        return owner.getIndex();
    }

    public Coord getCoord() {
        return coord;
    }

}
