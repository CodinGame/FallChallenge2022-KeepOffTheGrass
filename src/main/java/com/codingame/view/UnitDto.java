package com.codingame.view;

import com.codingame.game.Coord;

public class UnitDto {
    Coord coord;
    int strength;

    public UnitDto(Coord coord, int strength) {
        this.coord = coord;
        this.strength = strength;
    }

}
