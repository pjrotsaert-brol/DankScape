package eUpdater.main;

import dankscape.analysis.Analysis;
import dankscape.deobfuscation.Deobfuscation;
import eUpdater.misc.JarHandler;
import dankscape.misc.Timer;

import java.io.File;


public class eUpdater {
    public static final int Revision = 138;
    public static final boolean simbaPrint = true;
    public static final boolean logPrint = true;

    static boolean forceDownload = false;
    static boolean forceDeob = false;

    public static final boolean findMultis = true;
    public static final boolean doRefactor = false;


    private static void downloadPack() {
        System.out.println("GamePack downloading..");
        JarHandler.downloadJar("http://oldschool38.runescape.com/gamepack_2758227.jar", null, "res/Gamepacks/" + Revision + "/");
    }

    private static void deobPack() {
        JarHandler.Parse("res/Gamepacks/" + Revision + "/GamePack.jar");
        
        //Deobfuscator.deobfuscate(JarHandler.CLASSES);
        JarHandler.save("res/Gamepacks/" + Revision + "/Deob.jar");
    }

    private static void start() {
        Timer t = new Timer();
        File gamepacks = new File("res/Gamepacks/");
        if (!gamepacks.exists())
            gamepacks.mkdirs();
        File file = new File("res/Gamepacks/" + Revision);

        if (!file.exists()) {
            file.mkdir();
            downloadPack();
            deobPack();
        } else if (!forceDownload)
            System.out.println("Gamepack already downloaded..\n");

        if (forceDownload)
            downloadPack();

        if (forceDeob || forceDownload)
            deobPack();

        JarHandler.Parse("res/Gamepacks/" + Revision + "/Deob.jar");
        System.out.println(JarHandler.CLASSES.values().size() + " Classes Found \n");

        Analysis analyser = new Analysis();
        //analyser.analyze();
        System.out.println("Total Runtime was " + t.ellapsed() + "ms");

    }

    public static void main(String[] args) 
    {
        JarHandler.Parse("gamepack.jar");
        //Deobfuscator.deobfuscate(JarHandler.CLASSES);
        JarHandler.save("gamepack_out.jar");
        
        //eUpdater.start();
    }
}
