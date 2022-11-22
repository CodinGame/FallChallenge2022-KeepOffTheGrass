package com.codingame.view;

import com.codingame.game.Player;

public class PlayerDto {
    int money;
    int warpCooldown;
    String message;

    public PlayerDto(Player player) {
        money = player.getMoney();
        warpCooldown = player.getWarpCooldown();
        message = player.getMessage();
    }
}
