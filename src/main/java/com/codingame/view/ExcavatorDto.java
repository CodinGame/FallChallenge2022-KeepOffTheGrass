package com.codingame.view;

import com.codingame.game.Coord;

public class ExcavatorDto {
    int ownerIdx;
    Coord coord;

    public ExcavatorDto(Coord coord, int ownerIdx) {
        this.coord = coord;
        this.ownerIdx = ownerIdx;
    }

}
