package dankscape.analysis.methods;

import dankscape.analysis.Analysis;
import dankscape.analysis.classes.AbstractClassAnalyzer;
import dankscape.misc.Hook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.tree.FieldNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;

/**
 * Created by Kyle on 7/26/2015.
 */
public abstract class AbstractMethodAnalyzer {
    
    private String classId;
    private AbstractClassAnalyzer parentAnalyzer;
    private ArrayList<Hook> fields = new ArrayList<>();
    private List<String> neededFields;
    
    public AbstractMethodAnalyzer() {
    }

    public abstract void identify();
    
    protected void setId(String id) {
        classId = id;
        parentAnalyzer = Analysis.getSingleton().getClassAnalyzer(id);
        parentAnalyzer.setMethodAnalyzer(this);
    }
    
    public String getId(){
        return classId;
    }

    public ClassNode getClassNode() {
        return parentAnalyzer.getFirstClassNode();
    }
    
    public AbstractClassAnalyzer getParent() {
        return parentAnalyzer;
    }
    
    public void setNeededHooks(List<String> s) {
        neededFields = s;
    }

    public ArrayList<Hook> getHooks() {
        return this.fields;
    }

    public Hook getHook(String id) {
        ArrayList<Hook> fs = this.getHooks();
        for (Hook f : fs) {
            if (f.getId().equals(id))
                return f;
        }
        return null;
    }

    public boolean containsHook(String s) {
        ArrayList<Hook> ours = this.fields;
        for (Hook ourField : ours)
            if (ourField.getId().equals(s)) {
                return true;
            }
        return false;
    }

    private boolean duplicateHook(Hook h) {
        ArrayList<Hook> ours = this.fields;
        for (Hook ourField : ours)
            if (ourField.getId().equals(h.getId()) && !ourField.getName().equals(h.getName())) {
                ourField.setDuplicate(true);
                return true;
            }
        return false;
    }

    public List<String> getNeededHooks() {
        return neededFields;
    }

    public void addHook(Hook f) {
        if (!this.containsHook(f.getId())) {
            this.fields.add(f);
            duplicateHook(f);
        }

    }

    public ArrayList<Hook> getBrokenHooks() {
        ArrayList<Hook> temp = new ArrayList<>();
        for (String f : this.getNeededHooks()) {
            if (!this.containsHook(f)) {
                temp.add(this.getHook(f));
            }
        }
        for (Hook f : this.getHooks()) {
            if (f.getName().equals("NULL"))
                temp.add(f);
        }
        return temp;
    }

    public ArrayList<Hook> getDuplicateHooks() {
        ArrayList<Hook> temp = new ArrayList<>();
        for (String f : this.getNeededHooks()) {
            if (!this.containsHook(f)) {
                temp.add(this.getHook(f));
            }
        }
        for (Hook f : this.getHooks()) {
            if (f.getName().equals("NULL"))
                temp.add(f);
        }
        return temp;
    }
    
    // Convenience funcs //
    
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
