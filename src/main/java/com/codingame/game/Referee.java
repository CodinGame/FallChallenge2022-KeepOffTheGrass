package com.codingame.game;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.endscreen.EndScreenModule;
import com.codingame.view.ViewModule;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class Referee extends AbstractReferee {

    @Inject private MultiplayerGameManager<Player> gameManager;
    @Inject private CommandManager commandManager;
    @Inject private Game game;
    @Inject private ViewModule viewModule;

    @Override
    public void init() {
        try {

            Config.takeFrom(gameManager.getGameParameters());

            int leagueLevel = gameManager.getLeagueLevel();
            if (leagueLevel == 1) {
                // Smaller maps in first league
                Config.MAP_MAX_WIDTH = 15;
            }

            game.init();
            sendGlobalInfo();

            gameManager.setFrameDuration(500);
            gameManager.setMaxTurns(Config.MAX_TURNS);
            gameManager.setTurnMaxTime(50);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Referee failed to initialize");
            abort();
        }
    }

    private void abort() {
        gameManager.endGame();

    }

    private void sendGlobalInfo() {
        // Give input to players
        for (Player player : gameManager.getActivePlayers()) {
            for (String line : game.getGlobalInfoFor(player)) {
                player.sendInputLine(line);
            }
        }
    }

    @Override
    public void gameTurn(int turn) {
        game.resetGameTurnData();

        if (game.isKeyFrame()) {
            // Give input to players
            for (Player player : gameManager.getActivePlayers()) {
                for (String line : game.getCurrentFrameInfoFor(player)) {
                    player.sendInputLine(line);
                }
                player.execute();
            }
            // Get output from players
            handlePlayerCommands();
        }

        game.performGameUpdate(turn);

        if (gameManager.getActivePlayers().size() < 2) {
            abort();
        }
    }

    private void handlePlayerCommands() {

        for (Player player : gameManager.getActivePlayers()) {
            try {
                commandManager.parseCommands(player, player.getOutputs());
            } catch (TimeoutException e) {
                player.deactivate("Timeout!");
                gameManager.addToGameSummary(player.getNicknameToken() + " has not provided " + player.getExpectedOutputLines() + " lines in time");
            }
        }

    }

    static public String join(Object... args) {
        return Stream.of(args).map(String::valueOf).collect(Collectors.joining(" "));
    }

    @Override
    public void onEnd() {
        game.onEnd();
    }
}
