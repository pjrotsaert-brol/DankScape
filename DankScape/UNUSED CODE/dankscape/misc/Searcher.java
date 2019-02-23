package dankscape.misc;

import jdk.internal.org.objectweb.asm.tree.*;

public class Searcher {

    public static int WILDCARD = -1337;
    public static int IF = -13377;
    public static int CONSTPUSH = -133777;
    public static int SHORTIF = -133433;

    private final InsnList instructionList;
    AbstractInsnNode[] arrInstructions;

    public Searcher(MethodNode method) {
        instructionList = method.instructions;
        arrInstructions = instructionList.toArray();
        //System.out.println("Method Name: " + method.name + " Desc: " + method.desc)
    }

    public int findSingle(int pattern, int occurrence) {
        int indx = 0;
        //AbstractInsnNode[] arrInstructions = instructions.toArray();
        for (int i = 0; i < arrInstructions.length; ++i) {
            if (arrInstructions[i].getOpcode() == pattern) {
                if (indx == occurrence) {
                    return i;
                }
                ++indx;
            }
        }
        return -1;
    }

    public int findSingleFieldDesc(int pattern, String value) {
        int L = 0;
        for (int I = 0; L != -1; ++I) {
            L = findSingle(pattern, I);
            if (L != -1)
                if (((FieldInsnNode) arrInstructions[L]).desc.equals(value))
                    return L;
        }
        return -1;
    }

    public int findSingleFieldDesc(int pattern, String value, int startLine) {
        int L = 0;
        //AbstractInsnNode[] arrInstructions = instructions.toArray();
        for (int I = 0; L != -1; ++I) {
            L = find(new int[]{pattern}, I, startLine);
            if (L != -1) {
                if (((FieldInsnNode) arrInstructions[L]).desc.equals(value))
                    return L;
            }
        }
        return -1;
    }

    public int findSingleIntValue(int pattern, int value) {
        int L = 0;
        //AbstractInsnNode[] arrInstructions = instructions.toArray();
        for (int I = 0; L != -1; ++I) {
            L = findSingle(pattern, I);
            if (L != -1)
                if (((IntInsnNode) arrInstructions[L]).operand == value)
                    return L;
        }
        return -1;
    }

    public int findSingleLdcValue(int pattern, int value) {
        int L = 0;
        //AbstractInsnNode[] arrInstructions = instructions.toArray();
        for (int I = 0; L != -1; ++I) {
            L = findSingle(pattern, I);
            if (L != -1) {
                if (((LdcInsnNode) arrInstructions[L]).cst.equals(value))
                    return L;
            }
        }
        return -1;
    }

    public int findSingleLdcValue(int pattern, long value) {
        int L = 0;
        //AbstractInsnNode[] arrInstructions = instructions.toArray();
        for (int I = 0; L != -1; ++I) {
            L = findSingle(pattern, I);
            if (L != -1) {
                if (((LdcInsnNode) arrInstructions[L]).cst.equals(value))
                    return L;
            }
        }
        return -1;
    }

    public int findSingleIntValue(int pattern, int value, int startLine) {
        int L = 0;
        //AbstractInsnNode[] arrInstructions = instructions.toArray();
        for (int I = 0; L != -1; ++I) {
            L = find(new int[]{pattern}, I, startLine);
            if (L != -1)
                if (((IntInsnNode) arrInstructions[L]).operand == value)
                    return L;
        }
        return -1;
    }

    public int findSingleJump(int jump, int opcode, int startLine, int linesUp, int occurrence) {
        int curIndx = 0;
        int counter = 0;
        if (startLine == -1)
            return -1;
        for (int i = startLine; counter < linesUp; ++i) {
            ++counter;
            if (arrInstructions[i].getOpcode() == jump) {
                i = instructionList.indexOf(((JumpInsnNode) arrInstructions[i]).label);
            }
            if (arrInstructions[i].getOpcode() == opcode) {
                if (curIndx == occurrence)
                    return i;
                ++curIndx;
            }
        }
        return -1;
    }

    public int findSingleLines(int pattern, int startLine, int linesUp, int occurrence) {
        int C = 0;
        if (startLine == -1)
            return -1;
        for (int I = startLine; I < startLine + linesUp; ++I) {
            if (arrInstructions[I].getOpcode() == pattern) {
                if (C == occurrence) {
                    return I;
                }
                ++C;
            }
        }
        return -1;
    }

    public int amount(int opcode) {
        //AbstractInsnNode[] arrInstructions = instructions.toArray();
        int count = 0;
        for (int I = 0; I < arrInstructions.length; ++I) {
            if (arrInstructions[I].getOpcode() == opcode) {
                ++count;
            }

        }
        return count;
    }

    public int find(int pattern[], int occurrence) {
        AbstractInsnNode instructions[] = instructionList.toArray();
        int count = 0;
        for (int i = 0, j = 0; i < instructions.length; ++i) {
            int k = i, l = j;
            while ((instructions[k].getOpcode() == pattern[l] || WILDCARD == pattern[l])
                    || (IF == pattern[l] && instructions[k].getOpcode() > 158 && instructions[k].getOpcode() < 167) ||
                    (CONSTPUSH == pattern[l] && instructions[k].getOpcode() > 0 && instructions[k].getOpcode() < 18) ||
                    (SHORTIF == pattern[l] && instructions[k].getOpcode() > 152 && instructions[k].getOpcode() < 159)) {
                ++k;
                ++l;

                if (k >= instructions.length)
                    break;

                if (instructions[k].getOpcode() == -1)
                    ++k;

                if (l == pattern.length) {
                    if (count == occurrence)
                        return i;
                    else {
                        ++count;
                        break;
                    }
                }

                if (k == instructions.length) {
                    if (count == occurrence)
                        return j;
                    else {
                        ++count;
                        break;
                    }
                }
            }
        }
        return -1;
    }

    public int find(int Pattern[], int occurrence, int startLine) {
        if (startLine == -1)
            return -1;
        AbstractInsnNode instructions[] = instructionList.toArray();
        int count = 0;
        for (int i = startLine, j = 0; i < instructions.length; ++i) {
            int k = i, l = j;

            while ((instructions[k].getOpcode() == Pattern[l] || WILDCARD == Pattern[l])
                    || (IF == Pattern[l] && instructions[k].getOpcode() > 158 && instructions[k].getOpcode() < 167) ||
                    (CONSTPUSH == Pattern[l] && instructions[k].getOpcode() > 0 && instructions[k].getOpcode() < 18) ||
                    (SHORTIF == Pattern[l] && instructions[k].getOpcode() > 152 && instructions[k].getOpcode() < 159)) {
                ++k;
                ++l;

                if (instructions[k].getOpcode() == -1)
                    ++k;

                if (l == Pattern.length) {
                    if (count == occurrence)
                        return i;
                    else {
                        ++count;
                        break;
                    }
                }

                if (k == instructions.length) {
                    if (count == occurrence)
                        return j;
                    else {
                        ++count;
                        break;
                    }
                }
            }
        }
        return -1;
    }

    public int find(int[] pattern, int occurrence, int startLine, int endLine) {
        if (startLine == -1 || endLine < startLine) {
            return -1;
        }
        AbstractInsnNode instructions[] = instructionList.toArray();
        if (endLine == -1)
            endLine = instructions.length - 1;

        int count = 0;
        for (int i = startLine, j = 0; i < instructions.length && i <= endLine; ++i) {
            int k = i, l = j;

            while ((instructions[k].getOpcode() == pattern[l] || WILDCARD == pattern[l])
                    || (IF == pattern[l] && instructions[k].getOpcode() > 158 && instructions[k].getOpcode() < 167) ||
                    (CONSTPUSH == pattern[l] && instructions[k].getOpcode() > 0 && instructions[k].getOpcode() < 18) ||
                    (SHORTIF == pattern[l] && instructions[k].getOpcode() > 152 && instructions[k].getOpcode() < 159)) {
                ++k;
                ++l;

                if (instructions[k].getOpcode() == -1)
                    ++k;

                if (l == pattern.length) {
                    if (count == occurrence)
                        return i;
                    else {
                        ++count;
                        break;
                    }
                }

                if (k == instructions.length) {
                    if (count == occurrence)
                        return j;
                    else {
                        ++count;
                        break;
                    }
                }
            }
        }
        return -1;
    }

    public int findMultiPatterns(int[][] patterns, int occurrence) {
        for (int I = 0; I < patterns.length; ++I) {
            int L = find(patterns[I], occurrence);
            if (L != -1) {
                return L;
            }
        }
        return -1;
    }

    /*public int findPatterns(int sub[], int sub1[], int instance) {
        int L = find(sub, instance);
        if (L != -1)
            return L;
        else {
            L = find(sub1, instance);
            if (L != -1)
                return L;
            else
                return -1;
        }
    }
        public int findMulti(String field, String Owner, boolean Static) {
        int L;
        int[] aR = new int[100];
        int Count = 0;
        for (ClassNode classNode : CLASSES.values()) {
            List<MethodNode> methodList = classNode.methods;
            for (MethodNode Method : methodList) {
                Searcher search = new Searcher(Method);
                AbstractInsnNode[] arrInstructions = Method.instructions.toArray();
                try {
                    if (Static == false) {
                        for (int I = 0; I < 100; ++I) {
                            L = search.find(new int[]{Opcodes.ALOAD, Opcodes.GETFIELD, Opcodes.LDC, Opcodes.IMUL}, I);
                            if (L == -1) {
                                break;
                            }
                            if (((FieldInsnNode) arrInstructions[L + 1]).name.equals(field) && ((FieldInsnNode) arrInstructions[L + 1]).owner.equals(Owner)) {
                                aR[Count] = (int) ((LdcInsnNode) arrInstructions[L + 2]).cst;
                                ++Count;
                            }

                        }
                        for (int I = 0; I < 100; ++I) {
                            L = search.find(new int[]{Opcodes.GETFIELD, Opcodes.LDC, Opcodes.IMUL}, I);
                            if (L == -1) {
                                break;
                            }
                            if (((FieldInsnNode) arrInstructions[L]).name.equals(field) && ((FieldInsnNode) arrInstructions[L]).owner.equals(Owner)) {
                                aR[Count] = (int) ((LdcInsnNode) arrInstructions[L + 1]).cst;
                                ++Count;
                            }

                        }
                    } else {

                        for (int I = 0; I < 100; ++I) {
                            L = search.find(new int[]{Opcodes.GETSTATIC, Opcodes.LDC, Opcodes.IMUL}, I);
                            if (L == -1) {
                                break;
                            }
                            if (((FieldInsnNode) arrInstructions[L]).name.equals(field) && ((FieldInsnNode) arrInstructions[L]).owner.equals(Owner)) {
                                aR[Count] = (int) ((LdcInsnNode) arrInstructions[L + 1]).cst;
                                ++Count;
                            }

                        }

                    }

                } catch (Exception e) {
                }
            }

        }
        int[] b = Arrays.copyOf(aR, Count);
        int finalMult = Misc.mode(b);
        return finalMult;
    }
       public int findSingleBackwards(int pattern, int startLine, int Instance) {
        int C = 0;
        AbstractInsnNode[] arrInstructions = instructions.toArray();
        for (int I = startLine; I >= 0; --I) {
            if (arrInstructions[I].getOpcode() == pattern) {
                if (C == Instance) {
                    return I;
                }
                ++C;
            }
        }
        return -1;
    }

    public int findSingleBackwardsSpec(int pattern, int startLine, int Instance, int linesBack) {
        int C = 0;
        int back = startLine - linesBack;
        if (back < 0) {
            back = 0;
        }
        AbstractInsnNode[] arrInstructions = instructions.toArray();
        for (int I = startLine; I >= back; --I) {
            if (arrInstructions[I].getOpcode() == pattern) {
                if (C == Instance) {
                    return I;
                }
                ++C;
            }
        }
        return -1;
    }
    */

}

