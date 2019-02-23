package dankscape.analysis;

import dankscape.analysis.classes.AbstractClassAnalyzer;
import dankscape.analysis.classes.*;
import dankscape.analysis.methods.*;

import dankscape.analysis.methods.AbstractMethodAnalyzer;
import dankscape.misc.Hook;
import dankscape.misc.Misc;
import dankscape.misc.Timer;
import dankscape.misc.MultiplierSearcher;

import eUpdater.refactor.refactor;

import jdk.internal.org.objectweb.asm.tree.FieldNode;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

/**
 * Created by Kyle on 7/21/2015.
 */
public class Analysis {
    private static Analysis singleton = null;

    private final ArrayList<AbstractClassAnalyzer> classAnalyzers = new ArrayList();
    private final ArrayList<AbstractMethodAnalyzer> methodAnalyzers = new ArrayList();
    
    private final List<String> brokenFields = new ArrayList<>();
    private final List<String> duplicateFields = new ArrayList<>();
    
    private int brokenFieldsInt = 0, totalFieldsInt = 0;
    private int duplicateFieldsInt = 0;
    
    private HashMap<String, ClassNode> classList = new HashMap<>();
    
    public Analysis() {
        singleton = this;
        
        classAnalyzers.add(new NodeAnalyzer());
        classAnalyzers.add(new CacheableAnalyzer());
        classAnalyzers.add(new RenderableAnalyzer());
        classAnalyzers.add(new AnimableAnalyzer());
        classAnalyzers.add(new ModelAnalyzer());
        classAnalyzers.add(new AnimationSequenceAnalyzer());
        classAnalyzers.add(new NPCDefinitionAnalyzer());
        classAnalyzers.add(new LinkedListAnalyzer());
        classAnalyzers.add(new ActorAnalyzer());
        classAnalyzers.add(new NPCAnalyzer());
        classAnalyzers.add(new ObjectDefinitionAnalyzer());
        classAnalyzers.add(new BufferAnalyzer());
        classAnalyzers.add(new WidgetAnalyzer());
        classAnalyzers.add(new WidgetNodeAnalyzer());
        classAnalyzers.add(new HashTableAnalyzer());
        classAnalyzers.add(new GameShellAnalyzer());
        classAnalyzers.add(new PlayerAnalyzer());
        classAnalyzers.add(new ClientAnalyzer());
        classAnalyzers.add(new RegionAnalyzer());
        classAnalyzers.add(new BoundaryObjectAnalyzer());
        classAnalyzers.add(new GameObjectAnalyzer());
        classAnalyzers.add(new FloorDecorationAnalyzer());
        classAnalyzers.add(new WallDecorationAnalyzer());
        classAnalyzers.add(new SceneTileAnalyzer());
        classAnalyzers.add(new ItemAnalyzer());
        
        methodAnalyzers.add(new NodeMethodAnalyzer());
        methodAnalyzers.add(new CacheableMethodAnalyzer());
        methodAnalyzers.add(new RenderableMethodAnalyzer());
        methodAnalyzers.add(new NPCDefinitionMethodAnalyzer());
        methodAnalyzers.add(new LinkedListMethodAnalyzer());
        methodAnalyzers.add(new ActorMethodAnalyzer());
        methodAnalyzers.add(new NPCMethodAnalyzer());
        methodAnalyzers.add(new ObjectDefinitionMethodAnalyzer());
        methodAnalyzers.add(new WidgetMethodAnalyzer());
        methodAnalyzers.add(new WidgetNodeMethodAnalyzer());
        methodAnalyzers.add(new HashTableMethodAnalyzer());
        methodAnalyzers.add(new PlayerMethodAnalyzer());
        methodAnalyzers.add(new ClientMethodAnalyzer());
        methodAnalyzers.add(new RegionMethodAnalyzer());
        methodAnalyzers.add(new BoundaryObjectMethodAnalyzer());
        methodAnalyzers.add(new GameObjectMethodAnalyzer());
        methodAnalyzers.add(new FloorDecorationMethodAnalyzer());
        methodAnalyzers.add(new WallDecorationMethodAnalyzer());
        methodAnalyzers.add(new SceneTileMethodAnalyzer());
        methodAnalyzers.add(new ItemMethodAnalyzer());
    }
    
    public static Analysis getSingleton() {
        if(singleton == null)
            singleton = new Analysis();
        return singleton;
    }
    
    public ArrayList<AbstractClassAnalyzer> getClassAnalyzers() {
        return classAnalyzers;
    }
    
    public AbstractClassAnalyzer getClassAnalyzer(String id) {
        for(AbstractClassAnalyzer c : classAnalyzers)
        {
            if(c.getId().equals(id))
                return c;
        }
        return null;
    }
    
    public AbstractMethodAnalyzer getMethodAnalyzer(String id) {
        for(AbstractMethodAnalyzer c : methodAnalyzers)
        {
            if(c.getId().equals(id))
                return c;
        }
        return null;
    }

    private void runClassAnalysers(HashMap<String, ClassNode> classes) {
        Timer t = new Timer();
        System.out.println("%% Identifying Classes..");
        for (AbstractClassAnalyzer analyzer : this.classAnalyzers) {
            for (ClassNode c : classes.values()) {
                analyzer.identify(c);
            }
            if (analyzer.getName() == null) {
                Scanner inputClass = new Scanner(System.in);
                System.out.println(analyzer.getId() + " broke :/");
                System.out.print(analyzer.getId() + " = ");
                
                ClassNode manualInput = classes.get(inputClass.next());
                if (manualInput == null) {
                    System.out.println("Class not found, terminating..");
                    System.exit(1);
                }
                else {
                    analyzer.addClassNode(manualInput);
                }
            }
        }
        System.out.print("    (Identified " + this.classAnalyzers.size() + " out of " + this.classAnalyzers.size() + " Classes)");
        System.out.println(" ~ Took " + t.ellapsed() + " ms\n");
    }

    private void runMethodAnalysers() {
        Timer timer = new Timer();
        System.out.println("%% Identifying Fields..");
        for (AbstractClassAnalyzer analyzer : this.classAnalyzers) {
            if (analyzer.hasMethodAnalyzer) {
                analyzer.getMethodAnalyzer().identify();
                for (Hook field : analyzer.getMethodAnalyzer().getHooks()) {
                    if (field.isDuplicate()) {
                        duplicateFieldsInt += 1;
                        duplicateFields.add(field.getId());
                    }
                    if (field.broken)
                        brokenFieldsInt += 1;
                }
                brokenFieldsInt += (analyzer.getMethodAnalyzer().getNeededHooks().size() - analyzer.getMethodAnalyzer().getHooks().size());
                totalFieldsInt += analyzer.getMethodAnalyzer().getNeededHooks().size();
            }
        }
        System.out.print("    (Identified " + (totalFieldsInt - brokenFieldsInt - duplicateFieldsInt) + " out of " + totalFieldsInt + " Fields)");
        System.out.println(" ~ Took " + timer.ellapsed() + " ms\n");
    }

    private void checkFoundFields() {
        for (AbstractClassAnalyzer a : this.classAnalyzers) {
            if (!a.hasMethodAnalyzer)
                continue;
            if (a.getMethodAnalyzer().getNeededHooks().size() != a.getMethodAnalyzer().getHooks().size()) {
                for (String f : a.getMethodAnalyzer().getNeededHooks()) {
                    if (!a.getMethodAnalyzer().containsHook(f)) {
                        Scanner inputField = new Scanner(System.in);
                        System.out.println(a.getId() + "." + f + " broke :/");
                        System.out.print(f + " = ");
                        String id = inputField.next();
                        System.out.println(id);
                        FieldNode newField = Misc.getField(id, a.getFirstClassNode());
                        if (newField == null)
                            System.out.println("Field" + id + " Not Found");
                        a.getMethodAnalyzer().addHook(new Hook(f, newField));
                    }
                }
            }
        }
    }

    private void findMultipliers() {
        int multis = 0;
        boolean isStatic;
        System.out.println("%% Populating Multipliers..");
        Timer t = new Timer();
        for (AbstractClassAnalyzer a : this.classAnalyzers) {
            if (a.getId().contains("Client"))
                isStatic = true;
            else
                isStatic = false;
            if (!a.hasMethodAnalyzer)
                continue;
            for (Hook f : a.getMethodAnalyzer().getHooks()) {
                if (f.getDesc().equals("I")) {
                    if (isStatic)
                        f.setMultiplier(MultiplierSearcher.get(f.getName(), f.getOwner(), true));
                    else
                        f.setMultiplier(MultiplierSearcher.get(f.getName(), a.getName(), false));
                    if (f.getMultiplier() != 0)
                        ++multis;
                }
            }
        }
        System.out.print("    (Populated " + multis + " Multipliers)");
        System.out.println(" ~ Took " + t.ellapsed() + " ms\n\n");

    }

 /*   private void jsonPrint() {

        JsonBuilderFactory factory = Json.createBuilderFactory(null);
        JsonObjectBuilder build = factory.createObjectBuilder();
        JsonObjectBuilder build2 = factory.createObjectBuilder();

        for (AbstractClassAnalyzer a : this.classAnalyzers) {
            if (!a.hasMethodAnalyzer)
                continue;
            for (Hook f : a.getMethodAnalyser().getHooks()) {

                if (!a.getId().equals("Client")) {
                    build2.add(f.getId(), factory.createObjectBuilder()
                            .add("Hook", f.getName())
                            .add("multi", f.getMultiplier()));
                    build2.add("name", a.getName());
                } else {
                    build2.add(f.getId(), factory.createObjectBuilder()
                            .add("Hook", f.getOwner() + "." + f.getName())
                            .add("multi", f.getMultiplier()));
                    build2.add("name", a.getName());
                }
            }
            build.add(a.getId(), build2);
        }
        System.out.println(build.build().toString());
    }
*/
    private void logPrint() {
        for (AbstractClassAnalyzer a : this.classAnalyzers) {
            System.out.print(" # " + a.getId() + ": " + a.getClassNodes().get(0).name + ", " + (a.getClassNodes().size() - 1) + " duplicates");
            if (a.getClassNodes().size() > 1) {
                System.out.print(": ");
                for (int i = 1; i < a.getClassNodes().size(); ++i)
                    System.out.print(a.getClassNodes().get(i).name + ", ");
            }
            System.out.println("");
            if (a.hasMethodAnalyzer) {
                for (Hook f : a.getMethodAnalyzer().getHooks()) {
                    if (f.broken) {
                        brokenFields.add(f.getId());
                        System.out.println("     ~> " + f.getId() + " : Broken");
                    } else {
                        if (f.getMultiplier() != 0) {
                            if (a.getId().equals("Client")) {
                                System.out.println("     ~> " + f.getId() + " : " + f.getOwner() + "." + f.getName() + " * " + f.getMultiplier());
                            } else {
                                System.out.println("     ~> " + f.getId() + " : " + a.getClassNodes().get(0).name + "." + f.getName() + " * " + f.getMultiplier());
                            }
                        } else {
                            if (a.getId().equals("Client")) {
                                System.out.println("     ~> " + f.getId() + " : " + f.getOwner() + "." + f.getName());
                            } else {
                                System.out.println("     ~> " + f.getId() + " : " + a.getClassNodes().get(0).name + "." + f.getName());
                            }

                        }
                    }
                }
                int brokenFields = a.getMethodAnalyzer().getBrokenHooks().size();
                int totalFields = a.getMethodAnalyzer().getNeededHooks().size();
                System.out.println(" **Identified (" + (totalFields - brokenFields) + " / " + totalFields + ") Fields**");
            }
            System.out.println("");
        }
        if (brokenFields.size() > 0) {
            System.out.println("Broken Fields:");
            for (String s : brokenFields)
                System.out.println(" ~> " + s);
            System.out.println("");
        } else
            System.out.println("");

        if (duplicateFieldsInt > 0) {
            System.out.println("Duplicate Fields:");
            for (String s : duplicateFields)
                System.out.println(" ~> " + s);
            System.out.println("");
        } else
            System.out.println("");
    }

    private void banzaiPrint() {
        int length = 0;
        try {
            FileWriter write = new FileWriter("hooks.py", false);
            PrintWriter printer = new PrintWriter(write);
            printer.println("ReflectionRevision = '1337'\n");
            for (AbstractClassAnalyzer a : this.classAnalyzers) {
                printer.print("#  " + a.getId() + ": " + a.getClassNodes().get(0).name);
                printer.println("");
                if (a.hasMethodAnalyzer) {
                    for (Hook f : a.getMethodAnalyzer().getHooks()) {
                        length = (a.getId().length() + f.getId().length());
                        if (f.getMultiplier() != 0) {
                            printer.print(a.getId() + "_" + f.getId() + " = ");
                            for (int I = 0; I < 25 - length; ++I)
                                printer.print(" ");
                            if (a.getId().equals("Client"))
                                printer.println("['" + f.getOwner() + "." + f.getName() + "', " + f.getMultiplier() + "]");
                            else
                                printer.println("['" + f.getName() + "', " + f.getMultiplier() + "]");

                        } else {
                            printer.print(a.getId() + "_" + f.getId() + " = ");
                            for (int I = 0; I < 25 - length; ++I)
                                printer.print(" ");
                            if (a.getId().equals("Client"))
                                printer.println("['" + f.getOwner() + "." + f.getName() + "', 1]");
                            else
                                printer.println("['" + f.getName() + "', 1]");


                        }
                    }
                }
                printer.println("");
            }
            printer.close();
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
        }
    }

    private void simbaPrint() {
        int length = 0;
        try {
            FileWriter write = new FileWriter("hooks.simba", false);
            PrintWriter printer = new PrintWriter(write);
            printer.println("const");
            printer.println("    ReflectionRevision = '1337';");
            for (AbstractClassAnalyzer a : this.classAnalyzers) {
                printer.print("{" + a.getId() + ": " + a.getClassNodes().get(0).name + "}");
                printer.println("");
                if (a.hasMethodAnalyzer) {
                    for (Hook f : a.getMethodAnalyzer().getHooks()) {
                        length = (a.getId().length() + f.getId().length());
                        if (f.getMultiplier() != 0) {
                            printer.print(" " + a.getId() + "_" + f.getId() + ": THook = ");
                            for (int I = 0; I < 25 - length; ++I)
                                printer.print(" ");
                            if (a.getId().equals("Client"))
                                printer.println("['" + f.getOwner() + "." + f.getName() + "', " + f.getMultiplier() + "];");
                            else
                                printer.println("['" + f.getName() + "', " + f.getMultiplier() + "];");

                        } else {
                            printer.print(" " + a.getId() + "_" + f.getId() + ": THook = ");
                            for (int I = 0; I < 25 - length; ++I)
                                printer.print(" ");
                            if (a.getId().equals("Client"))
                                printer.println("['" + f.getOwner() + "." + f.getName() + "', 1];");
                            else
                                printer.println("['" + f.getName() + "', 1];");


                        }
                    }
                }
                printer.println("");
            }
            printer.close();
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
        }
    }

    public void analyze(HashMap<String, ClassNode> classes) {
        
        classList = classes;
        
        runClassAnalysers(classes);
        runMethodAnalysers();
        //checkFoundFields();
        
        findMultipliers();
        
        //if (eUpdater.simbaPrint)
            simbaPrint();
        //if (eUpdater.logPrint)
            logPrint();
            
        //banzaiPrint();

        //if (eUpdater.doRefactor)
        //    refactor.run();
    }

    public HashMap<String, ClassNode> getClassList() {
        return classList;
    }
}
