import java.io.File;
import java.io.IOException;

import com.codingame.gameengine.runner.MultiplayerGameRunner;
import com.google.common.io.Files;

public class KeepOffTheGrassMain {

    public static void main(String[] args) throws IOException, InterruptedException {

        Options opts = new Options().read(args);

        System.out.format("bot1Command %s%n", opts.getBot1Command());
        System.out.format("bot1Nickname %s%n", opts.getBot1Nickname());
        System.out.format("bot2Command %s%n", opts.getBot2Command());
        System.out.format("bot2Nickname %s%n", opts.getBot2Nickname());
        System.out.format("seed %d%n", opts.getSeed());

        MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();

        gameRunner.setSeed(opts.getSeed());
        gameRunner.addAgent(opts.getBot1Command(), opts.getBot1Nickname());
        gameRunner.addAgent(opts.getBot2Command(), opts.getBot2Nickname());

        gameRunner.start();
    }

    private static String compile(String botFile) throws IOException, InterruptedException {

        File outFolder = Files.createTempDir();

        System.out.println("Compiling Boss.java... " + botFile);
        Process compileProcess = Runtime.getRuntime()
                .exec(new String[] { "bash", "-c", "javac " + botFile + " -d " + outFolder.getAbsolutePath() });
        compileProcess.waitFor();
        return "java -cp " + outFolder + " Player";
    }

    private static String[] compileTS(String botFile) throws IOException, InterruptedException {

        System.out.println("Compiling ... " + botFile);

        Process compileProcess = Runtime.getRuntime().exec(
                new String[] { "bash", "-c", "tsc --target ES2018 --inlineSourceMap --types ./typescript/readline/ "
                        + botFile + " --outFile /tmp/Boss.js" }
        );
        compileProcess.waitFor();

        return new String[] { "bash", "-c", "node -r ./typescript/polyfill.js /tmp/Boss.js" };
    }
}

class Options {
    private String bot1Command = null;
    private String bot1Nickname = null;
    private String bot2Command = null;
    private String bot2Nickname = null;
    private String seed = null;

    public Options read(String[] args) {
        if (args == null || args.length == 0) {
            return this;
        }

        this.bot1Command = args[0];
        this.bot1Nickname = args.length > 1 ? args[1] : null;
        this.bot2Command = args.length > 2 ? args[2] : null;
        this.bot2Nickname = args.length > 3 ? args[3] : null;
        this.seed = args.length > 4 ? args[4] : null;

        return this;
    }

    public String getBot1Nickname(){
        return bot1Nickname == null ? "Player1" : bot1Nickname;
    }

    public String getBot2Nickname(){
        return bot1Nickname == null ? "Player1" : bot1Nickname;
    }

    public String getBot1Command() {
        return
                bot1Command == null ?
                        "C:/git/github/StevenTCramer/Training/CodingGame/KeepOffTheGrass/Source/bin/Release/net6.0/KeepOffTheGrass.exe" :
                        bot1Command;
    }

    public String getBot2Command() {
        return
                bot2Command == null ?
                        "C:/git/github/StevenTCramer/Training/CodingGame/KeepOffTheGrass/bots/SnowFrogDev.exe" :
                        bot2Command;
    }

    public Long getSeed() {
        if(this.seed == null) return null;
        return Long.parseLong(this.seed);
    }
}