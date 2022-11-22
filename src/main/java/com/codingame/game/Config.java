package com.codingame.game;

import java.util.Properties;

public class Config {

    /**
     * Map gen
     */
    public static final int MAP_MIN_WIDTH = 12;
    public static int MAP_MAX_WIDTH = 24;
    public static final double MAP_ASPECT_RATIO = 1 / 2d;
    public static final int MIN_SPAWN_DISTANCE = 7;

    /**
     * Gameplay
     */
    public static final int CELL_MAX_DURABILITY = 10;
    public static final int RECYCLER_INCOME = 1;
    public static final int PLAYER_STARTING_MONEY = 10;
    public static final int PLAYER_MINIMAL_INCOME = 10;
    public static final int PLAYER_INCOME_CELL_COUNT_CEILING = 0;
    public static final int COST_UNIT = 10;
    public static final int COST_RECYCLER = 10;
    public static final int COST_WARP = 2;
    public static final int MAX_TURNS = 200;
    public static final int EARLY_FINISH_TURNS = 20;

    public static void takeFrom(Properties params) {
        MAP_MAX_WIDTH = getFromParams(params, "MAP_MAX_WIDTH", MAP_MAX_WIDTH);
    }

    public static void giveTo(Properties params) {
        params.put("MAP_MAX_WIDTH", MAP_MAX_WIDTH);
    }

    private static double getFromParams(Properties params, String name, double defaultValue) {
        String inputValue = params.getProperty(name);
        if (inputValue != null) {
            try {
                return Double.parseDouble(inputValue);
            } catch (NumberFormatException e) {
                // Do naught
            }
        }
        return defaultValue;
    }

    private static int getFromParams(Properties params, String name, int defaultValue) {
        String inputValue = params.getProperty(name);
        if (inputValue != null) {
            try {
                return Integer.parseInt(inputValue);
            } catch (NumberFormatException e) {
                // Do naught
            }
        }
        return defaultValue;
    }

    private static boolean getFromParams(Properties params, String name, boolean defaultValue) {
        String inputValue = params.getProperty(name);
        if (inputValue != null) {
            try {
                return new Boolean(inputValue);
            } catch (NumberFormatException e) {
                // Do naught
            }
        }
        return defaultValue;
    }

}
