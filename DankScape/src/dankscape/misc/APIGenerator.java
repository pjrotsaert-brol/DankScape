/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.misc;

import dankscape.nativeinterface.NativeInterface;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.tree.FieldNode;
import jdk.internal.org.objectweb.asm.Opcodes;

/**
 *
 * @author Pieterjan
 */
public class APIGenerator 
{
    private static APIGenerator singleton = null;
    
    private HashMap<String, ClassHook> hooks = null;
    private HashMap<String, ClassNode> classes = null;
    
    // Variable names used for looping through multidimensional arrays
    private static String[] LOOPVARS = { "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t" }; // This ought to be enough..
    private static String endl = "\r\n", tab = "    ";
    
    private ArrayList<StaticFieldLocation> staticFieldLocations = new ArrayList<>();
    
    private class SignatureInfo 
    {
        int arrayDim = 0;
        String typeName;
        String apiTypename;
        boolean isApiClass = false;
        
        public SignatureInfo(){}
        public SignatureInfo(String typename, String _apiTypename)
        {
            typeName = typename;
            apiTypename = _apiTypename;
        }
        
        public String getFullTypename()
        {
            String s = apiTypename;
            for(int i = 0;i < arrayDim;i++)
                s += "[]";
            return s;
        }
    }
    
    
    private void addFieldLoc(String fieldName, String typename, String designatedClass)
    {
        staticFieldLocations.add(new StaticFieldLocation(fieldName, typename, designatedClass));
    }
    
    public APIGenerator()
    {
        // Register Fixed Field Locations
        /*********** Field Name ********************* Type ******************* Class ***************/
        addFieldLoc("localPlayer",                "RSPlayer",               "RSClient");
        addFieldLoc("clientInstance",             "RSClient",               "RSClient");
        addFieldLoc("menuX",                      "int",                    "RSClient");
        addFieldLoc("menuY",                      "int",                    "RSClient");
        addFieldLoc("menuWidth",                  "int",                    "RSClient");
        addFieldLoc("menuHeight",                 "int",                    "RSClient");
        addFieldLoc("topContextMenuRow",          "RSContextMenuRow",       "RSClient");
        addFieldLoc("preferences",                "RSPreferences",          "RSClient");
        addFieldLoc("widgets",                    "RSWidget[][]",           "RSClient");
        addFieldLoc("selectedItemIndex",          "int",                    "RSClient");
        addFieldLoc("lastSelectedItemName",       "java.lang.String",       "RSClient");
        addFieldLoc("isMenuOpen",                 "boolean",                "RSClient");
        
        addFieldLoc("revision",                   "int",                    "RSClient");
        
        addFieldLoc("worldList",                  "RSWorld[]",              "RSClient");
        addFieldLoc("worldCount",                 "int",                    "RSClient");
        
        addFieldLoc("mouseRecorder",              "RSMouseRecorder",        "RSClient");
        
        addFieldLoc("cameraPitch",                "int",                    "RSClient");
        addFieldLoc("cameraYaw",                  "int",                    "RSClient");
        addFieldLoc("cameraX",                    "int",                    "RSClient");
        addFieldLoc("cameraY",                    "int",                    "RSClient");
        addFieldLoc("cameraZ",                    "int",                    "RSClient");
        addFieldLoc("cameraX2",                   "int",                    "RSClient");
        addFieldLoc("cameraY2",                   "int",                    "RSClient");
        addFieldLoc("cameraZ2",                   "int",                    "RSClient");
        addFieldLoc("plane",                      "int",                    "RSClient");
        addFieldLoc("Scene_plane",                "int",                    "RSClient");
        addFieldLoc("region",                     "RSRegion",               "RSClient");
        
        addFieldLoc("baseX",                      "int",                    "RSClient");
        addFieldLoc("baseY",                      "int",                    "RSClient");
        addFieldLoc("tileHeights",                "int[][][]",              "RSClient");
        addFieldLoc("tileSettings",               "byte[][][]",             "RSClient");
        
        addFieldLoc("items",                      "RSNodeCache",            "RSClient");
        
        
        addFieldLoc("Login_isUsernameRemembered", "boolean",                "RSClient");
        addFieldLoc("password",                   "java.lang.String",       "RSClient");
        addFieldLoc("username",                   "java.lang.String",       "RSClient");
        addFieldLoc("Login_response0",            "java.lang.String",       "RSClient");
        addFieldLoc("loginMessage1",              "java.lang.String",       "RSClient");
        addFieldLoc("loginMessage2",              "java.lang.String",       "RSClient");
        addFieldLoc("loginMessage3",              "java.lang.String",       "RSClient");
        addFieldLoc("loginIndex",                 "int",                    "RSClient");
        
        addFieldLoc("worldSelectShown",           "boolean",                "RSClient");
    }
    
    public static void run(HashMap<String, ClassHook> hooks, HashMap<String, ClassNode> classes)
    {
        if(singleton == null)
            singleton = new APIGenerator();
        singleton.generate(hooks, classes);
    }
    
    private void generate(HashMap<String, ClassHook> hooks, HashMap<String, ClassNode> classes)
    {
        this.hooks = hooks;
        this.classes = classes;
        
        File dir = new File("api/rs");
        if(!dir.isDirectory())
            dir.mkdirs();
        
        debug("Generating RS API...");
        long tStart = System.nanoTime();
           
        // Prepare necessary relocations for static methods
        int nFieldsRelocated = 0;
        for(StaticFieldLocation loc : staticFieldLocations)
        {
            String className = findStaticField(loc.fieldName, loc.typename);
            if(className != null && !loc.dstClassName.equals(toApiClassName(className)))
            {
                hooks.get(className).ignoredFields.add(loc.fieldName);
                getClassHookByApiName(loc.dstClassName).relocatedFields.add(new RelocatedField(loc.fieldName, className));
                nFieldsRelocated++;
            }
        }
        
        debug("" + nFieldsRelocated + " static field relocations required.");
        
        // Start generating code
        String summary = new String();
        for(ClassHook classHook : hooks.values())
        {
            String classCode = new String();
            String apiClassName = toApiClassName(classHook.name);
            
            ClassNode classNode = getClassNode(classHook.name);
            //if((classNode.access & Opcodes.ACC_INTERFACE) == 0) // Skip interfaces
            //{    
            
            //boolean isInterface = (classNode.access & Opcodes.ACC_INTERFACE) != 0;
            
            classCode += "// Original name: " + classHook.internalName;
            
            ClassNode superApiClass = classNode;
            while(superApiClass != null)
            {
                classCode += " => " + superApiClass.superName.replace('/', '.');
                if(superApiClass.superName.length() > 2) 
                    superApiClass = null;
                else
                {
                    String resolvedName = resolveClassName(superApiClass.superName);
                    if(!resolvedName.equals(superApiClass.superName))
                        classCode += " (" + resolvedName + ")";
                    superApiClass = classes.get(superApiClass.superName);
                }
            }
            
            classCode += endl;
            
            boolean extendsApiClass = false;

            classCode += "public class " + apiClassName;
            if(classNode.superName.length() <= 2) // RS Classes are max 2 chars long
            {
                String inheritedClassName = toApiClassName(resolveClassName(classNode.superName).replace('/', '.'));
                classCode += " extends " + inheritedClassName;
                extendsApiClass = true;
            }
            else 
                classCode += " extends RSClassWrapper";
            
            classCode += " {" + endl + endl;

            // Give the class a constructor that takes a reference to the real RS Client object
            classCode += tab + "public " + apiClassName + "(Object ref) {" + endl;  
            classCode += tab + tab + "super(ref);" + endl;
            classCode += tab + "}" + endl + endl;

            // Create getter methods
            for(FieldHook fieldHook : classHook.fields.values())
            {   
                if(!classHook.isIgnoredField(fieldHook.name))
                    classCode += generateMethod(classHook, fieldHook);
            }
            for(RelocatedField f : classHook.relocatedFields)
            {
                ClassHook hC = hooks.get(f.className);
                FieldHook hF = hC.fields.get(f.fieldName);
                
                classCode += tab + "// Relocated from " + toApiClassName(f.className) + endl;
                classCode += generateMethod(hC, hF);
            }

            classCode += "}" + endl;

            String header = "/*" + endl +
                        " * " + apiClassName + ".java" + endl +
                        " * ---------------------------------------------------------------------------" + endl +
                        " * Note: this file was automatically generated by the DankScape API Generator," + endl +
                        " * modifications to this file are useless as it will have to be regenerated often in the future." + endl +
                        " */" + endl + endl +
                        "package dankscape.api.rs;" + endl + endl +
                        "import dankscape.loader.AppletLoader;";
                        
            if(!extendsApiClass)
                header += endl + "import dankscape.api.internal.RSClassWrapper;";
            
            header += endl + endl;
            
            classCode = header + classCode;

            summary += classCode + endl + endl;

            try
            {
                File file = new File("api/rs/" + apiClassName + ".java");
                FileOutputStream out = new FileOutputStream(file);
                out.write(classCode.getBytes());
                out.close();
            } 
            catch (IOException ex)
            {
                Logger.getLogger(APIGenerator.class.getName()).log(Level.SEVERE, null, ex);
                debug("Error writing '" + "api/rs/" + apiClassName + ".java" + "': " + ex.getMessage());
            }
        }
        
        try
        {
            File file = new File("api_summary.txt");
            FileOutputStream out = new FileOutputStream(file);
            out.write(summary.getBytes());
            out.close();
        } 
        catch (IOException ex)
        {
            Logger.getLogger(APIGenerator.class.getName()).log(Level.SEVERE, null, ex);
            debug("Error writing 'api_summary.txt': " + ex.getMessage());
        }
        
        debug("API generation completed in " + ((System.nanoTime() - tStart) / 1000000.0) + " ms.");
        debug("A summary of all classes was saved in 'api_summary.txt' for you to Ctrl+F in :).");
    }
    
    private String generateMethod(ClassHook classHook, FieldHook fieldHook)
    {
        String methodCode = tab + "public ";
        FieldNode fieldNode = getFieldNode(classHook.name, fieldHook.name);
        if((fieldNode.access & Opcodes.ACC_STATIC) != 0)
            methodCode += "static ";

        SignatureInfo sig = parseSignature(fieldNode.desc).get(0);

        String fullTypename = sig.apiTypename;
        for(int i = 0;i < sig.arrayDim;i++)
            fullTypename += "[]";

        methodCode += fullTypename;
        methodCode += " get" + fieldHook.name.substring(0, 1).toUpperCase() + fieldHook.name.substring(1) + "() {" + endl;

        if(sig.isApiClass)
        {
            // This getter returns a class that is part of the RS client,
            // which means we need to wrap every object returned into an API wrapper class.
            String abstractTypename = "Object";
            for(int i = 0;i < sig.arrayDim;i++)
                abstractTypename += "[]";

            methodCode += tab + tab + abstractTypename + " objects = (" + abstractTypename + ")";
            methodCode += "AppletLoader.getSingleton().getFieldValue(\"" + classHook.name + "\", \"" + fieldHook.name + "\", ";
            if((fieldNode.access & Opcodes.ACC_STATIC) != 0)
                methodCode += "null);" + endl;
            else
                methodCode += "ref);" + endl;

            methodCode += tab + tab + fullTypename + " wrappers = null;" + endl;
            
            // Do not instantiate a wrapper if the object was null.
            if(sig.arrayDim > 0)
            {
                methodCode += tab + tab + "if(objects == null)" + endl;
                methodCode += tab + tab + tab + "return null;" + endl;
            }
            
            for(int i = 0;i < sig.arrayDim;i++)
            {
                int nBraces = sig.arrayDim - i - 1;
                String destination = "wrappers";
                String source = "objects";
                String indent = "";

                for(int j = 0;j < i;j++)
                {
                    destination += "[" + LOOPVARS[j] + "]";
                    source += "[" + LOOPVARS[j] + "]";
                    indent += tab;
                }
                
                if(i > 0)
                {
                    methodCode += tab + tab + indent + "if(" + source + " == null)" + endl;
                    methodCode += tab + tab + tab + indent + "continue;" + endl;
                }
                
                methodCode += tab + tab + indent + destination + " = new " + sig.apiTypename + "[" + source + ".length]";
                for(int j = 0;j < nBraces;j++)
                    methodCode += "[]";
                methodCode += ";" + endl + tab + tab;

                for(int j = 0;j < i;j++)
                    methodCode += tab;

                methodCode += "for(int " + LOOPVARS[i] + " = 0;" + LOOPVARS[i] + " < " + source + ".length;" + LOOPVARS[i] + "++) {" + endl;
            }
            
            String indent = "", wrapperName = "wrappers", objectName = "objects";
            
            for(int i = 0;i < sig.arrayDim;i++)
            {    
                indent += tab;
                wrapperName += "[" + LOOPVARS[i] + "]";
                objectName += "[" + LOOPVARS[i] + "]";
            }
            
            methodCode += tab + tab + indent + "if(" + objectName + " != null)" + endl;
            methodCode += tab + tab + tab + indent + wrapperName + " = (" + sig.apiTypename + ")getWrapper(" + objectName + ");" + endl;           

            for(int i = 0;i < sig.arrayDim;i++)
            {
                methodCode += tab + tab;
                for(int j = 0;j < sig.arrayDim - i - 1;j++)
                    methodCode += tab;
                methodCode += "}" + endl;
            }

            methodCode += tab + tab + "return wrappers;" + endl;
        }
        else
        {                      
            methodCode += tab + tab + "return (" + fullTypename + ")AppletLoader.getSingleton().getFieldValue(\"" + classHook.name + "\", " +
                    "\"" + fieldHook.name + "\", ";
            if((fieldNode.access & Opcodes.ACC_STATIC) != 0)
                methodCode += "null);" + endl;
            else
                methodCode += "ref);" + endl;
        }

        methodCode += tab + "}" + endl + endl;
        
        if(sig.isApiClass && sig.arrayDim > 0) // Create an additional method for retrieving references to arrays directly
        {
            methodCode += tab + "public ";
            if((fieldNode.access & Opcodes.ACC_STATIC) != 0)
                methodCode += "static ";
            methodCode += "Object";
            for(int i = 0;i < sig.arrayDim;i++)
                methodCode += "[]";
            
            methodCode += " getRSRef_" + fieldHook.name.substring(0, 1).toUpperCase() + fieldHook.name.substring(1) + "() {" + endl;
            methodCode += tab + tab + "return (Object";
            for(int i = 0;i < sig.arrayDim;i++)
                methodCode += "[]";
            methodCode += ")AppletLoader.getSingleton().getFieldValue(\"" + classHook.name + "\", " +
                    "\"" + fieldHook.name + "\", ";
            if((fieldNode.access & Opcodes.ACC_STATIC) != 0)
                methodCode += "null);" + endl;
            else
                methodCode += "ref);" + endl;
            methodCode += tab + "}" + endl + endl;
        }
        
        return methodCode;
    }
    
    public static String toApiClassName(String className)
    {
        return toApiClassName(className, false);
    }
    
    public static String toApiClassName(String className, boolean isInterface)
    {
        String prefix = isInterface ? "RSI" : "RS";
        String apiClassName = className.substring(0, 1).toUpperCase() + className.substring(1);
        if(!apiClassName.startsWith(prefix))
            apiClassName = prefix + apiClassName;
        return apiClassName;
    }
    
    private List<SignatureInfo> parseSignature(String sig)
    {
        List<SignatureInfo> infoList = new ArrayList<>();
        
        int arrayDim = 0;
        SignatureInfo info = null;
        
        for(int i = 0;i < sig.length();i++)
        {
            char c = sig.charAt(i);
            if(c == '[')
                arrayDim++;
            else
            {
                if(c == 'Z')
                    info = new SignatureInfo("boolean", "boolean");
                if(c == 'B')
                    info = new SignatureInfo("byte", "byte");
                if(c == 'C')
                    info = new SignatureInfo("char", "char");
                if(c == 'S')
                    info = new SignatureInfo("short", "short");
                if(c == 'I')
                    info = new SignatureInfo("int", "int");
                if(c == 'J')
                    info = new SignatureInfo("long", "long");
                if(c == 'F')
                    info = new SignatureInfo("float", "float");
                if(c == 'D')
                    info = new SignatureInfo("double", "double");
                if(c == 'V')
                    info = new SignatureInfo("void", "void"); // Note: this should NEVER be in a field descriptor
                if(c == 'L')
                {
                    int endIdx = sig.indexOf(";", i + 1);
                    String className = sig.substring(i + 1, endIdx);
                    if(className.length() <= 2 || className.equals("client"))
                    {
                        className = resolveClassName(className);
                        info = new SignatureInfo(className, toApiClassName(className));
                        info.isApiClass = true;
                    }
                    else
                    {
                        className = className.replace('/', '.');
                        info = new SignatureInfo(className, className);
                    }
                    
                    i = endIdx;
                }
                
                info.arrayDim = arrayDim;
                infoList.add(info);
                arrayDim = 0;
            }
        }
        return infoList;
    }
    
    private static String getAccessSpecifiers(int accessMask)
    {
        String specifiers = new String();
        
        if((accessMask & Opcodes.ACC_ABSTRACT) != 0 && (accessMask & Opcodes.ACC_INTERFACE) == 0) // Interfaces are always abstract.
            specifiers += (specifiers.length() > 0 ? " " : "") + "abstract";
        if((accessMask & Opcodes.ACC_ENUM) != 0)
            specifiers += (specifiers.length() > 0 ? " " : "") + "enum";
        if((accessMask & Opcodes.ACC_FINAL) != 0)
            specifiers += (specifiers.length() > 0 ? " " : "") + "final";
        if((accessMask & Opcodes.ACC_NATIVE) != 0)
            specifiers += (specifiers.length() > 0 ? " " : "") + "native";
        if((accessMask & Opcodes.ACC_PRIVATE) != 0)
            specifiers += (specifiers.length() > 0 ? " " : "") + "private";
        if((accessMask & Opcodes.ACC_PROTECTED) != 0)
            specifiers += (specifiers.length() > 0 ? " " : "") + "protected";
        if((accessMask & Opcodes.ACC_PUBLIC) != 0)
            specifiers += (specifiers.length() > 0 ? " " : "") + "public";
        if((accessMask & Opcodes.ACC_STATIC) != 0)
            specifiers += (specifiers.length() > 0 ? " " : "") + "static";
        if((accessMask & Opcodes.ACC_STRICT) != 0)
            specifiers += (specifiers.length() > 0 ? " " : "") + "strict";
        //if((accessMask & Opcodes.ACC_SUPER) != 0)
        //    specifiers += (specifiers.length() > 0 ? " " : "") + "super";
        if((accessMask & Opcodes.ACC_VOLATILE) != 0)
            specifiers += (specifiers.length() > 0 ? " " : "") + "volatile";
        if((accessMask & Opcodes.ACC_DEPRECATED) != 0)
            specifiers += (specifiers.length() > 0 ? " " : "") + "deprecated";
        
        if((accessMask & Opcodes.ACC_INTERFACE) != 0)
            specifiers += (specifiers.length() > 0 ? " " : "") + "interface";
        
        return specifiers;
    }
    
    private String findStaticField(String fieldName, String typename)
    {
        for(ClassHook hC : hooks.values())
        {
            for(FieldHook hF : hC.fields.values())
            {
                if(fieldName.equals(hF.name))
                {
                    FieldNode n = getFieldNode(hC.name, hF.name);
                    if((n.access & Opcodes.ACC_STATIC) != 0)
                    {
                        SignatureInfo sig = parseSignature(n.desc).get(0);
                        if(sig.getFullTypename().equals(typename))
                            return hC.name;
                    }
                    
                }
            }
        }
        return null;
    }
    
    private ClassHook getClassHookByApiName(String apiName)
    {
        for(ClassHook hC : hooks.values())
        {
            if(toApiClassName(hC.name).equals(apiName))
                return hC;
        }
        return null;
    }
    
    private ClassNode getClassNode(String className)
    {
        ClassHook hC = hooks.get(className);
        for(ClassNode n : classes.values())
        {
            if(n.name.equals(hC.internalName))
                return n;
        }
        return null;
    }
    
    private FieldNode getFieldNode(String className, String fieldName)
    {
        ClassHook hC = hooks.get(className);
        FieldHook hF = hC.fields.get(fieldName);
        
        ClassNode cn = getClassNode(className);
        for(FieldNode n : cn.fields)
        {
            if(n.name.equals(hF.internalName))
                return n;
        }
        return null;
    }
    
    private String resolveFieldName(String internalClassName, String internalFieldName)
    {
        for(ClassHook hC : hooks.values())
        {
            if(hC.internalName == null ? internalClassName == null : hC.internalName.equals(internalClassName))
            {
                for(FieldHook hF : hC.fields.values())
                {
                    if(hF.internalName.equals(internalFieldName))
                        return hF.name;
                }
            }
        }
        return internalFieldName; // Unable to resolve, just return the internal name..
    }
    
    private String resolveClassName(String internalClassName)
    {
        for(ClassHook hC : hooks.values())
        {
            if(hC.internalName == null ? internalClassName == null : hC.internalName.equals(internalClassName))
                return hC.name;
        }
        return internalClassName; // Unable to resolve, just return the internal name..
    }
    
    private void debug(String s)
    {
        NativeInterface.println("API Generator", s + "\n");
    }
    
}
