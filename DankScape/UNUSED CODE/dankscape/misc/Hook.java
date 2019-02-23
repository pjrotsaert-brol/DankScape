package dankscape.misc;

import dankscape.analysis.Analysis;
import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;
import jdk.internal.org.objectweb.asm.tree.FieldInsnNode;
import jdk.internal.org.objectweb.asm.tree.FieldNode;

import java.util.List;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

/**
 * Created by Kyle on 7/27/2015.
 */
public class Hook {

    String id, name, desc, signature, owner;
    int access, multiplier;
    public boolean broken = false;
    public boolean duplicate = false;

    public void setId(String s) {

        id = s;
    }

    public void setName(String s) {

        name = s;
    }

    public void setDesc(String s) {

        desc = s;
    }

    public void setSignature(String s) {

        signature = s;
    }

    public void setAccess(int i) {

        access = i;
    }

    public void setMultiplier(int i) {
        multiplier = i;
    }

    public String getId() {

        return id;
    }

    public String getName() {

        return name;
    }

    public boolean isDuplicate() {
        return duplicate;
    }

    public void setDuplicate(boolean duplicate) {
        this.duplicate = duplicate;
    }

    public String getDesc() {

        return desc;
    }

    public String getSignature() {

        return signature;
    }

    public int getAccess() {

        return access;
    }

    public void setOwner(String s) {
        owner = s;
    }

    public String getOwner() {
        return owner;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public Hook() {

    }

    public Hook(String id, FieldNode f) {
        setId(id);
        setName(f.name);
        setDesc(f.desc);
        setSignature(f.signature);
        setAccess(f.access);
    }

    public Hook(String id, AbstractInsnNode[] instructions, int L) {
        if (L != -1 && instructions[L] instanceof FieldInsnNode) {
            for (ClassNode c : Analysis.getSingleton().getClassList().values()) {
                List<FieldNode> fields = c.fields;
                for (FieldNode f : fields) {
                    if ((f.name.equals(((FieldInsnNode) instructions[L]).name)) && (f.desc.equals(((FieldInsnNode) instructions[L]).desc))) {
                        setId(id);
                        setName(f.name);
                        setDesc(f.desc);
                        setSignature(f.signature);
                        setAccess(f.access);
                        setOwner(((FieldInsnNode) instructions[L]).owner);
                    }
                }
            }
        } else {
            this.setId(id);
            this.setOwner("NULL");
            this.setMultiplier(0);
            this.setName("NULL");
            this.setDesc("NULL");
            this.broken = true;
        }

    }

}
