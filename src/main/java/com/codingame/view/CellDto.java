package com.codingame.view;

import com.codingame.game.Cell;
import com.codingame.game.Coord;
import com.codingame.game.Player;

public class CellDto implements Comparable<CellDto> {
    int x, y, ownerIdx, durability;

    public CellDto(Coord coord, Cell cell) {
        this.x = coord.getX();
        this.y = coord.getY();
        this.ownerIdx = cell.getOwner()
            .map(Player::getIndex)
            .orElse(-1);
        this.durability = cell.getDurability();
    }

    @Override
    public int compareTo(CellDto o) {
        if (x < o.x) {
            return -1;
        } else if (x > o.x) {
            return 1;
        } else if (y < o.y) {
            return -1;
        } else if (y > o.y) {
            return 1;
        } else {
            return 0;
        }

    }

}
