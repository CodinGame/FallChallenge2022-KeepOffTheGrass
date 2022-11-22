package com.codingame.game.action;

import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.codingame.game.Coord;

public enum ActionType {

    MOVE(
        "^(MOVE|WARP) (?<amount>\\d+) (?<originX>\\d+) (?<originY>\\d+) (?<targetX>-?\\d+) (?<targetY>-?\\d+)",
        (match, action) -> {
            action.setAmount(Integer.valueOf(match.group("amount")));
            action.setOriginCoord(
                new Coord(Integer.valueOf(match.group("originX")), Integer.valueOf(match.group("originY")))
            );
            action.setCoord(
                new Coord(Integer.valueOf(match.group("targetX")), Integer.valueOf(match.group("targetY")))
            );
        }
    ),
    SPAWN("^SPAWN (?<amount>\\d+) (?<x>-?\\d+) (?<y>-?\\d+)", (match, action) -> {
        action.setAmount(Integer.valueOf(match.group("amount")));
        action.setCoord(new Coord(Integer.valueOf(match.group("x")), Integer.valueOf(match.group("y"))));
    }),
    BUILD("^BUILD (?<x>-?\\d+) (?<y>-?\\d+)", (match, action) -> {
        action.setCoord(new Coord(Integer.valueOf(match.group("x")), Integer.valueOf(match.group("y"))));
    }),
    MESSAGE(
        "^MESSAGE (?<message>.*)",
        (match, action) -> {
            action.setMessage(match.group("message"));
        }
    ),
    WAIT("^WAIT", ActionType::doNothing);

    private Pattern pattern;
    private BiConsumer<Matcher, Action> consumer;

    private static void doNothing(Matcher m, Action a) {
    }

    ActionType(String pattern, BiConsumer<Matcher, Action> consumer) {
        this.pattern = Pattern.compile(pattern);
        this.consumer = consumer;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public BiConsumer<Matcher, Action> getConsumer() {
        return consumer;
    }

}
