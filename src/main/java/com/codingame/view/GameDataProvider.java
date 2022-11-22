package com.codingame.view;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.codingame.game.Cell;
import com.codingame.game.Coord;
import com.codingame.game.Game;
import com.codingame.game.Player;
import com.codingame.game.Unit;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class GameDataProvider {
    @Inject private Game game;
    @Inject private MultiplayerGameManager<Player> gameManager;

    public GlobalViewData getGlobalData() {
        GlobalViewData data = new GlobalViewData();
        data.width = game.grid.width;
        data.height = game.grid.height;
        
        data.units = new ArrayList<List<UnitDto>>(2);
        for (Player player : gameManager.getPlayers()) {
            data.units.add(
                player.getUnits().entrySet().stream()
                    .map(e -> {
                        Coord c = e.getKey();
                        Unit u = e.getValue();
                        return new UnitDto(c, u.getStrength());
                    })
                    .collect(Collectors.toList())
            );
        }

        data.cells = game.grid.cells.entrySet()
            .stream()
            .map(e -> {
                Coord coord = e.getKey();
                Cell cell = e.getValue();
                CellDto cellDto = new CellDto(coord, cell);
                return cellDto;
            })
            .collect(Collectors.toList());

        return data;
    }

    public FrameViewData getCurrentFrameData() {
        FrameViewData data = new FrameViewData();

        

        data.players = gameManager.getPlayers().stream()
            .map(PlayerDto::new)
            .collect(Collectors.toList());

        data.events = game.getViewerEvents();

        return data;
    }

}
