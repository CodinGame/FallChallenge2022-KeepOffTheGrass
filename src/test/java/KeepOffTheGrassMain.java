import java.io.File;
import java.io.IOException;

import com.codingame.gameengine.runner.MultiplayerGameRunner;
import com.google.common.io.Files;

public class KeepOffTheGrassMain {

    public static void main(String[] args) throws IOException, InterruptedException {

        MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();
//         gameRunner.setSeed(-8358938852454912011l);
        gameRunner.addAgent("python3.8 bots/julien.py", "JulienBot");
        gameRunner.addAgent("python3.8 bots/kevin.py", "InsectBot");

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
