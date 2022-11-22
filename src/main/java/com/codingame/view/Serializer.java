package com.codingame.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.codingame.game.Coord;
import com.codingame.game.Referee;

public class Serializer {
    public static final String MAIN_SEPARATOR = ";";
    
    static public String serialize(FrameViewData frameViewData) {
        List<Object> lines = new ArrayList<>();
        frameViewData.players
            .stream()
            .map(p -> join(p.money, p.warpCooldown, Optional.ofNullable(p.message).orElse("")))
            .forEach(lines::add);

        lines.add(frameViewData.events.size());
        frameViewData.events.stream()
            .flatMap(
                e -> Stream.of(
                    e.playerIndex == null ? "" : e.playerIndex,
                    e.amount == null ? "" : e.amount,
                    e.coord == null ? "" : serialize(e.coord),
                    e.target == null ? "" : serialize(e.target),
                    e.type,
                    e.animData.get(0).start,
                    e.animData.get(0).end
                )
            )
            .forEach(lines::add);

        return lines.stream()
            .map(String::valueOf)
            .collect(Collectors.joining(MAIN_SEPARATOR));
    }

    static public String serialize(GlobalViewData globalViewData) {
        List<Object> lines = new ArrayList<>();
        lines.add(globalViewData.width);
        lines.add(globalViewData.height);
        globalViewData.cells
            .stream()
            .sorted()
            .map(c -> Referee.join(c.durability, c.ownerIdx))
            .forEach(line -> lines.add(line));
        for (List<UnitDto> units : globalViewData.units) {
            lines.add(units.size());
            units
                .stream()
                .map(c -> join(c.coord.getX(), c.coord.getY(), c.strength))
                .forEach(line -> lines.add(line));
        }
        return lines.stream()
            .map(String::valueOf)
            .collect(Collectors.joining(MAIN_SEPARATOR));
    }

    static public <T> String serialize(List<T> list) {
        return list.stream().map(String::valueOf).collect(Collectors.joining(" "));
    }

    static public String serialize(Coord coord) {
        return coord.getX() + " " + coord.getY();
    }

    static public String join(Object... args) {
        return Stream.of(args)
            .map(String::valueOf)
            .collect(Collectors.joining(" "));
    }

}
