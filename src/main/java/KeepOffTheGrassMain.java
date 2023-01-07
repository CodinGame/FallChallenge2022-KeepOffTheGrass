import java.io.File;
import java.io.IOException;

import com.codingame.gameengine.runner.MultiplayerGameRunner;
import com.google.common.io.Files;

public class KeepOffTheGrassMain {

    public static void main(String[] args) throws IOException, InterruptedException {

        Options opts = new Options().read(args);
        String cli1 = opts.getCommandLine1();
        String cli2 = opts.getCommandLine2();
        Long cli3 = opts.getCommandLine3();

        System.out.format("bot1 %s%n", cli1);
        System.out.format("bot2 %s%n", cli2);
        System.out.format("seed %d%n", cli3);

        MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();

        // Set seed
        Long seed = cli3 != null ? cli3 : null;
        gameRunner.setSeed(seed);
        System.out.format("Seed %d%n", seed);

        gameRunner.addAgent("C:/git/StevenTCramer/Training/CodingGame/KeepOffTheGrass/Source/bin/Debug/net6.0/KeepOffTheGrass.exe", "StevenTCramer");
        gameRunner.addAgent("C:/git/StevenTCramer/Training/CodingGame/KeepOffTheGrass/bots/SnowFrogDev.exe", "SnowfrogDev");

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
    private String commandLine1 = null;
    private String commandLine2 = null;
    private String commandLine3 = null;

    public Options read(String[] args) {
        if (args == null) {
            return this;
        }

        switch (args.length) {
            case 1: {
                this.commandLine1 = args[0];
                break;
            }
            case 2: {
                this.commandLine1 = args[0];
                this.commandLine2 = args[1];
                break;
            }
            case 3: {
                this.commandLine1 = args[0];
                this.commandLine2 = args[1];
                this.commandLine3 = args[2];
                break;
            }
        }

        return this;
    }

    public String getCommandLine1() {
        return this.commandLine1;
    }

    public String getCommandLine2() {
        return this.commandLine2;
    }

    public Long getCommandLine3() {
        if(this.commandLine3 == null) return null;
        return Long.parseLong(this.commandLine3);
    }
}