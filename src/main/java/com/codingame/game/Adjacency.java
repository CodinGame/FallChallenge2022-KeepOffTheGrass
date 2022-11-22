package com.codingame.game;

public enum Adjacency {
    FOUR(new Coord(-1, 0), new Coord(1, 0), new Coord(0, -1), new Coord(0, 1)), EIGHT(
        new Coord(-1, 0), new Coord(1, 0), new Coord(0, -1), new Coord(0, 1), new Coord(-1, -1),
        new Coord(1, -1), new Coord(-1, 1), new Coord(1, 1)
        );
    
    public final Coord[] deltas;
    
    private Adjacency(Coord... deltas) {
        this.deltas = deltas;
    }
}
