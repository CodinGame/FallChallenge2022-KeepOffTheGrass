package com.codingame.game.action;

import com.codingame.game.Coord;

public class Action {
    final ActionType type;
    private Coord coord;
    private int amount;

    private Coord originCoord;

    private String message;

    public Action(ActionType type) {
        this.type = type;
    }

    public ActionType getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    public boolean isMove() {
        return this.getType() == ActionType.MOVE;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Coord getCoord() {
        return coord;
    }

    public void setCoord(Coord coord) {
        this.coord = coord;
    }

    public Coord getOriginCoord() {
        return originCoord;
    }

    public void setOriginCoord(Coord originCoord) {
        this.originCoord = originCoord;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Action [type=" + type + ", coord=" + coord + ", amount=" + amount + ", originCoord=" + originCoord
            + "]";
    }

}