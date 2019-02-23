package dankscape.analysis.classes;

import dankscape.analysis.Analysis;
import dankscape.analysis.methods.AbstractMethodAnalyzer;
import dankscape.misc.ClassNodeWrapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.tree.FieldNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;

/**
 * Created by Kyle on 7/21/2015.
 */
public abstract class AbstractClassAnalyzer {

    private String id; // id is our own custom name, aka 'Player', 'Item', etc.
    private final ArrayList<ClassNode> nodes = new ArrayList<>();
    
    public boolean hasMethodAnalyzer = false;
    private AbstractMethodAnalyzer methodAnalyzer;

    public abstract void identify(ClassNode c);
    
    public void setMethodAnalyzer(AbstractMethodAnalyzer m) {
        this.methodAnalyzer = m;
        this.hasMethodAnalyzer = true;
    }

    public AbstractMethodAnalyzer getMethodAnalyzer() {
        return this.methodAnalyzer;
    }

    public void setId(String s) {

        this.id = s;
    }

    public String getId() {

        return this.id;
    }

    public void addClassNode(ClassNode c) {

        for(ClassNode existingNode : nodes)
        {
            if(existingNode.name.equals(c.name))
                return; // Do not add the same class twice
        }
        
        nodes.add(c);
    }

    public ArrayList<ClassNode> getClassNodes() {
        return nodes;
    }

    public ClassNode getFirstClassNode() {
        return getClassNodes().get(0);
    }

    public String getName() {
        return (getFirstClassNode() == null) ? null : getFirstClassNode().name;
    }

    // Some Convenience functions //
    
    public AbstractClassAnalyzer getOther(String id) {
        return Analysis.getSingleton().getClassAnalyzer(id);
    }
    
    protected HashMap<String, ClassNode> getClasses() {
        return Analysis.getSingleton().getClassList();
    }
    
    protected List<FieldNode> getFields(ClassNode c, String desc) {
        List<FieldNode> fields = c.fields;
        List<FieldNode> temp = new ArrayList();
        for (FieldNode fN : fields) {
            if (fN.desc.equals(desc)) {
                temp.add(fN);
            }
        }
        return temp;
    }

    protected List<FieldNode> getFields(ClassNode c, Integer nonAccess) {
        List<FieldNode> fields = c.fields;
        List<FieldNode> temp = new ArrayList();
        for (FieldNode fN : fields)
            if ((fN.access & nonAccess) == 0)
                temp.add(fN);
        return temp;
    }

    protected List<FieldNode> getFields(ClassNode c, Integer nonAccess1, Integer nonAccess2) {
        List<FieldNode> fields = c.fields;
        List<FieldNode> temp = new ArrayList();
        for (FieldNode fN : fields)
            if ((fN.access & nonAccess1) == 0 && (fN.access & nonAccess2) == 0)
                temp.add(fN);
        return temp;
    }

    protected List<FieldNode> getFields(ClassNode c, Integer none, Integer Access, Integer nonAccess2) {
        List<FieldNode> fields = c.fields;
        List<FieldNode> temp = new ArrayList();
        for (FieldNode fN : fields)
            if ((fN.access & Access) != 0 && (fN.access & nonAccess2) == 0)
                temp.add(fN);
        return temp;
    }
    
    protected MethodNode getMethod(ClassNode c, boolean exact, String desc) {
        List<MethodNode> methods = new ArrayList<>();
        List<MethodNode> methodList = c.methods;
        for (MethodNode m : methodList) {
            if (exact) {
                if (m.desc.equals(desc))
                    methods.add(m);
            } else if (m.desc.contains(desc))
                methods.add(m);
        }
        if (methods.size() > 1)
            System.out.println(methods.get(231312312));
        if (methods.size() == 0)
            return null;
        return methods.get(0);
    }

    protected List<MethodNode> getMethods(ClassNode c, boolean exact, String desc) {
        List<MethodNode> methods = new ArrayList<>();
        List<MethodNode> methodList = c.methods;
        for (MethodNode m : methodList) {
            if (exact) {
                if (m.desc.equals(desc))
                    methods.add(m);
            } else if (m.desc.contains(desc))
                methods.add(m);
        }
        return methods;
    }

    protected List<MethodNode> getMethodsName(ClassNode c, boolean exact, String desc) {
        List<MethodNode> methods = new ArrayList<>();
        List<MethodNode> methodList = c.methods;
        for (MethodNode m : methodList) {
            if (exact) {
                if (m.name.equals(desc))
                    methods.add(m);
            } else if (m.name.contains(desc))
                methods.add(m);
        }
        return methods;
    }

}
