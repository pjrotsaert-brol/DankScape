package dankscape.deobfuscation;
/**
 * Created by Kyle on 1/12/2015.
 */

import dankscape.misc.ClassNodeWrapper;
import dankscape.misc.Searcher;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class EqualSwapDeobber extends AbstractDeobfuscator {

    public EqualSwapDeobber(HashMap<String, ClassNode> classList){
        super(classList);
    }
    
    // Checks if there are no NULL instructions
    private void verify(MethodNode method){ 
        AbstractInsnNode[] instr = method.instructions.toArray();
        for(AbstractInsnNode n : instr){
            if(n == null){
                throw new java.lang.NullPointerException("You done fucked up.");
            }
        }
    }

    private int run() {
        int patterns[][] = new int[][]{
                //{Opcodes.ICONST_0, Opcodes.GETSTATIC, Opcodes.LDC},
                {Opcodes.BIPUSH, Opcodes.GETSTATIC, Opcodes.LDC},
                {Opcodes.BIPUSH, Opcodes.ALOAD, Opcodes.GETFIELD, Opcodes.LDC},
        };
        int nFixed = 0;
        for (ClassNode clazz : classes.values()) {
            List<MethodNode> methodList = clazz.methods;
            for (MethodNode method : methodList) {
                Searcher searcher = new Searcher(method);
                ArrayList<AbstractInsnNode> instructions = new ArrayList(Arrays.asList(method.instructions.toArray()));
                for (int[] pattern : patterns) {
                    int patternIndex = searcher.find(pattern, 0);
                    int count = 0;
                    while (patternIndex != -1) {
                        int afterMul;
                        afterMul = searcher.findSingleLines(Opcodes.IMUL, patternIndex, 5, 0);
                        if (afterMul == -1)
                            afterMul = searcher.findSingleLines(Opcodes.LMUL, patternIndex, 5, 0);
                        if (afterMul == -1)
                            break;
                        instructions.add(afterMul + 1, instructions.get(patternIndex));
                        instructions.remove(patternIndex);
                        ++count;
                        ++nFixed;
                        patternIndex = searcher.find(pattern, count);
                    }
                }
                method.instructions.clear();
                for (AbstractInsnNode n : instructions)
                    method.instructions.add(n);
                
                verify(method);
            }
        }
        
        
        patterns = new int[][]{
                {Opcodes.BIPUSH, Opcodes.ALOAD, Opcodes.GETFIELD, Opcodes.LDC}
        };
        for (ClassNode clazz : classes.values()) {
            List<MethodNode> methodList = clazz.methods;
            for (MethodNode method : methodList) {
                Searcher searcher = new Searcher(method);
                ArrayList<AbstractInsnNode> instructions = new ArrayList(Arrays.asList(method.instructions.toArray()));

                for (int[] pattern : patterns) {
                    int patternIndex = searcher.find(pattern, 0);
                    int count = 0;
                    while (patternIndex != -1) {
                        AbstractInsnNode[] arrInstructions = method.instructions.toArray();
                        int after5 = 0;
                        for (int I = 0; I < pattern.length; ++I) {
                            if (arrInstructions[patternIndex + I] instanceof FieldInsnNode) {
                                after5 = patternIndex + I + 5;
                                break;
                            }
                        }
                        instructions.add(after5, instructions.get(patternIndex));
                        instructions.remove(patternIndex);
                        ++count;
                        ++nFixed;
                        patternIndex = searcher.find(pattern, count);
                    }
                }
                method.instructions.clear();
                for (AbstractInsnNode n : instructions) {
                    method.instructions.add(n);
                }
                verify(method);
            }
        }
        
        // ------------------------------- //
        /*
        patterns = new int[][]{
                {Opcodes.BIPUSH, Opcodes.ALOAD, Opcodes.GETFIELD, Opcodes.LDC}
        };
        for (ClassNode clazz : classes.values()) {
            List<MethodNode> methodList = clazz.methods;
            for (MethodNode method : methodList) {
                Searcher searcher = new Searcher(method);
                ArrayList<AbstractInsnNode> instructions = new ArrayList(Arrays.asList(method.instructions.toArray()));

                for (int[] pattern : patterns) {
                    int patternIndex = searcher.find(pattern, 0);
                    int count = 0;
                    while (patternIndex != -1) {
                        AbstractInsnNode[] arrInstr = method.instructions.toArray();
                        int after5 = 0;
                        out:
                        for (int I = 0; I < pattern.length; ++I) {
                            int Op = arrInstr[patternIndex + I].getOpcode();
                            if (Op >= Opcodes.IF_ICMPEQ && Op <= Opcodes.IF_ACMPNE) {
                                after5 = patternIndex + I + 5;
                                break out;
                            }
                        }
                        instructions.add(after5, instructions.get(patternIndex));
                        instructions.remove(patternIndex);
                        ++count;
                        ++nFixed;
                        patternIndex = searcher.find(pattern, count);
                    }
                }
                method.instructions.clear();
                for (AbstractInsnNode n : instructions) {
                    method.instructions.add(n);
                }
                verify(method);
            }
        }*/
        
        // ------------------------------- //
        
        patterns = new int[][]{
                {Opcodes.BIPUSH, Opcodes.ALOAD, Opcodes.GETFIELD, Opcodes.GETFIELD, Opcodes.LDC},
                {Opcodes.BIPUSH, Opcodes.GETSTATIC, Opcodes.GETSTATIC, Opcodes.LDC}
        };
        for (ClassNode clazz : classes.values()) {
            List<MethodNode> methodList = clazz.methods;
            for (MethodNode method : methodList) {
                Searcher searcher = new Searcher(method);
                ArrayList<AbstractInsnNode> instructions = new ArrayList(Arrays.asList(method.instructions.toArray()));

                for (int[] pattern : patterns) {
                    int patternIndex = searcher.find(pattern, 0);
                    int count = 0;
                    while (patternIndex != -1) {
                        AbstractInsnNode[] arrInstr = method.instructions.toArray();
                        int after4 = 0;
                        for (int I = 0; I < pattern.length; ++I) {
                            if (arrInstr[patternIndex + I] instanceof FieldInsnNode) {
                                after4 = patternIndex + I + 4;
                                break;
                            }
                        }

                        instructions.add(after4, instructions.get(patternIndex));
                        instructions.remove(patternIndex);
                        ++count;
                        ++nFixed;
                        patternIndex = searcher.find(pattern, count);
                    }
                }
                method.instructions.clear();
                for (AbstractInsnNode n : instructions) {
                    method.instructions.add(n);
                }
                verify(method);
            }
        }
        patterns = new int[][]{
                {Opcodes.GETSTATIC, Opcodes.LDC, Opcodes.IMUL, Opcodes.ILOAD, Searcher.IF},
                {Opcodes.GETSTATIC, Opcodes.LDC, Opcodes.LMUL, Opcodes.ILOAD, Searcher.IF},
        };
        for (ClassNode Class : classes.values()) {
            List<MethodNode> methodList = Class.methods;
            for (MethodNode method : methodList) {
                Searcher searcher = new Searcher(method);
                ArrayList<AbstractInsnNode> instructions = new ArrayList(Arrays.asList(method.instructions.toArray()));
                for (int[] pattern : patterns) {
                    int patternIndex = searcher.find(pattern, 0);
                    int count = 0;
                    while (patternIndex != -1) {
                        instructions.add(patternIndex, instructions.get(patternIndex + 3));
                        instructions.remove(patternIndex + 4);
                        ++count;
                        ++nFixed;
                        patternIndex = searcher.find(pattern, count);
                    }
                }
                method.instructions.clear();
                for (AbstractInsnNode n : instructions) {
                    method.instructions.add(n);
                }
                verify(method);
            }
        }
        patterns = new int[][]{
                {Searcher.CONSTPUSH, Opcodes.ILOAD, Searcher.IF},
                {Searcher.CONSTPUSH, Opcodes.ALOAD, Searcher.IF},
        };
        for (ClassNode Class : classes.values()) {
            List<MethodNode> methodList = Class.methods;
            for (MethodNode method : methodList) {
                Searcher searcher = new Searcher(method);
                ArrayList<AbstractInsnNode> instructions = new ArrayList(Arrays.asList(method.instructions.toArray()));
                for (int[] pattern : patterns) {
                    int patternIndex = searcher.find(pattern, 0);
                    int count = 0;
                    while (patternIndex != -1) {
                        instructions.add(patternIndex, instructions.get(patternIndex + 1));
                        instructions.remove(patternIndex + 2);
                        ++count;
                        ++nFixed;
                        patternIndex = searcher.find(pattern, count);
                    }
                }
                method.instructions.clear();
                for (AbstractInsnNode n : instructions) {
                    method.instructions.add(n);
                }
                verify(method);
            }
        }

        patterns = new int[][]{
                {Opcodes.ACONST_NULL, Opcodes.ALOAD, Opcodes.GETFIELD},
        };
        for (ClassNode Class : classes.values()) {
            List<MethodNode> methodList = Class.methods;
            for (MethodNode method : methodList) {
                Searcher searcher = new Searcher(method);
                ArrayList<AbstractInsnNode> instructions = new ArrayList(Arrays.asList(method.instructions.toArray()));
                for (int[] pattern : patterns) {
                    int patternIndex = searcher.find(pattern, 0);
                    int count = 0;
                    while (patternIndex != -1) {
                        instructions.add(patternIndex + 3, instructions.get(patternIndex));
                        instructions.remove(patternIndex);
                        ++count;
                        ++nFixed;
                        patternIndex = searcher.find(pattern, count);
                    }
                }
                method.instructions.clear();
                for (AbstractInsnNode n : instructions) {
                    method.instructions.add(n);
                }
                verify(method);
            }
        }

        patterns = new int[][]{
                {Opcodes.LDC, Opcodes.ILOAD, Opcodes.IMUL, Opcodes.PUTSTATIC},
                {Opcodes.LDC, Opcodes.ILOAD, Opcodes.IMUL, Opcodes.PUTFIELD},
        };
        for (ClassNode Class : classes.values()) {
            List<MethodNode> methodList = Class.methods;
            for (MethodNode method : methodList) {
                Searcher searcher = new Searcher(method);
                ArrayList<AbstractInsnNode> instructions = new ArrayList(Arrays.asList(method.instructions.toArray()));
                for (int[] pattern : patterns) {
                    int patternIndex = searcher.find(pattern, 0);
                    int count = 0;
                    while (patternIndex != -1) {
                        instructions.add(patternIndex, instructions.get(patternIndex + 1));
                        instructions.remove(patternIndex + 2);
                        ++count;
                        ++nFixed;
                        patternIndex = searcher.find(pattern, count);
                    }
                }
                method.instructions.clear();
                for (AbstractInsnNode n : instructions) {
                    method.instructions.add(n);
                }
                verify(method);
            }
        }

        patterns = new int[][]{
                {Opcodes.LDC, Opcodes.GETSTATIC, Opcodes.GETFIELD, Opcodes.IMUL},
        };
        for (ClassNode Class : classes.values()) {
            List<MethodNode> methodList = Class.methods;
            for (MethodNode method : methodList) {
                Searcher searcher = new Searcher(method);
                ArrayList<AbstractInsnNode> instructions = new ArrayList(Arrays.asList(method.instructions.toArray()));
                for (int[] pattern : patterns) {
                    int patternIndex = searcher.find(pattern, 0);
                    int count = 0;
                    while (patternIndex != -1) {
                        instructions.add(patternIndex + 3, instructions.get(patternIndex));
                        instructions.remove(patternIndex);
                        ++count;
                        ++nFixed;
                        patternIndex = searcher.find(pattern, count);
                    }
                }
                method.instructions.clear();
                for (AbstractInsnNode n : instructions) {
                    method.instructions.add(n);
                }
                verify(method);
            }
        }

        patterns = new int[][]{
                {Opcodes.ALOAD, Opcodes.LDC, Opcodes.ALOAD, Opcodes.GETFIELD, Opcodes.GETFIELD, Opcodes.IMUL, Opcodes.PUTFIELD},
        };
        for (ClassNode Class : classes.values()) {
            List<MethodNode> methodList = Class.methods;
            for (MethodNode method : methodList) {
                Searcher searcher = new Searcher(method);
                ArrayList<AbstractInsnNode> instructions = new ArrayList(Arrays.asList(method.instructions.toArray()));
                for (int[] pattern : patterns) {
                    int patternIndex = searcher.find(pattern, 0);
                    int count = 0;
                    while (patternIndex != -1) {
                        instructions.add(patternIndex + 5, instructions.get(patternIndex + 1));
                        instructions.remove(patternIndex + 1);
                        ++count;
                        ++nFixed;
                        patternIndex = searcher.find(pattern, count);
                    }
                }
                method.instructions.clear();
                for (AbstractInsnNode n : instructions) {
                    method.instructions.add(n);
                }
                verify(method);
            }
        }

        patterns = new int[][]{
                {Searcher.CONSTPUSH, Opcodes.GETSTATIC, Searcher.IF},
        };
        for (ClassNode Class : classes.values()) {
            List<MethodNode> methodList = Class.methods;
            for (MethodNode method : methodList) {
                Searcher searcher = new Searcher(method);
                ArrayList<AbstractInsnNode> instructions = new ArrayList(Arrays.asList(method.instructions.toArray()));
                for (int[] pattern : patterns) {
                    int patternIndex = searcher.find(pattern, 0);
                    int count = 0;
                    while (patternIndex != -1) {
                        instructions.add(patternIndex, instructions.get(patternIndex + 1));
                        instructions.remove(patternIndex + 2);
                        ++count;
                        ++nFixed;
                        patternIndex = searcher.find(pattern, count);
                    }
                }
                method.instructions.clear();
                for (AbstractInsnNode n : instructions) {
                    method.instructions.add(n);
                }
                verify(method);
            }
        }

        patterns = new int[][]{
                {Searcher.CONSTPUSH, Opcodes.GETSTATIC, Opcodes.LDC, Opcodes.IMUL, Searcher.IF},
                {Searcher.CONSTPUSH, Opcodes.GETSTATIC, Opcodes.LDC, Opcodes.LMUL, Searcher.IF},
        };
        for (ClassNode Class : classes.values()) {
            List<MethodNode> methodList = Class.methods;
            for (MethodNode method : methodList) {
                Searcher searcher = new Searcher(method);
                ArrayList<AbstractInsnNode> instructions = new ArrayList(Arrays.asList(method.instructions.toArray()));
                for (int[] pattern : patterns) {
                    int patternIndex = searcher.find(pattern, 0);
                    int count = 0;
                    while (patternIndex != -1) {
                        instructions.add(patternIndex + 3, instructions.get(patternIndex));
                        instructions.remove(patternIndex);
                        ++count;
                        ++nFixed;
                        patternIndex = searcher.find(pattern, count);
                    }
                }
                method.instructions.clear();
                for (AbstractInsnNode n : instructions) {
                    method.instructions.add(n);
                }
                verify(method);
            }
        }

        patterns = new int[][]{
                {Opcodes.LDC, Opcodes.ALOAD, Opcodes.GETFIELD, Opcodes.LDC, Opcodes.IMUL, Searcher.IF},
                {Searcher.CONSTPUSH, Opcodes.ALOAD, Opcodes.GETFIELD, Opcodes.LDC, Opcodes.IMUL, Searcher.IF},

        };
        for (ClassNode Class : classes.values()) {
            List<MethodNode> methodList = Class.methods;
            for (MethodNode method : methodList) {
                Searcher searcher = new Searcher(method);
                ArrayList<AbstractInsnNode> instructions = new ArrayList(Arrays.asList(method.instructions.toArray()));
                for (int[] pattern : patterns) {
                    int patternIndex = searcher.find(pattern, 0);
                    int count = 0;
                    while (patternIndex != -1) {
                        instructions.add(patternIndex + 5, instructions.get(patternIndex));
                        instructions.remove(patternIndex);
                        ++count;
                        ++nFixed;
                        patternIndex = searcher.find(pattern, count);
                    }
                }
                method.instructions.clear();
                for (AbstractInsnNode n : instructions) {
                    method.instructions.add(n);
                }
                verify(method);
            }
        }

        patterns = new int[][]{
                {Opcodes.GETSTATIC, Opcodes.LDC, Searcher.CONSTPUSH, Opcodes.IMUL},
        };
        for (ClassNode Class : classes.values()) {
            List<MethodNode> methodList = Class.methods;
            for (MethodNode method : methodList) {
                Searcher searcher = new Searcher(method);
                ArrayList<AbstractInsnNode> instructions = new ArrayList(Arrays.asList(method.instructions.toArray()));
                for (int[] pattern : patterns) {
                    int patternIndex = searcher.find(pattern, 0);
                    int count = 0;
                    while (patternIndex != -1) {
                        instructions.add(patternIndex + 2, instructions.get(patternIndex + 3));
                        instructions.remove(patternIndex + 4);
                        ++count;
                        ++nFixed;
                        patternIndex = searcher.find(pattern, count);
                    }
                }
                method.instructions.clear();
                for (AbstractInsnNode n : instructions) {
                    method.instructions.add(n);
                }
                verify(method);
            }
        }

        patterns = new int[][]{
                {Searcher.CONSTPUSH, Opcodes.ALOAD, Opcodes.GETFIELD, Opcodes.ALOAD, Opcodes.GETFIELD, Opcodes.LDC},

        };
        for (ClassNode Class : classes.values()) {
            List<MethodNode> methodList = Class.methods;
            for (MethodNode method : methodList) {
                Searcher searcher = new Searcher(method);
                ArrayList<AbstractInsnNode> instructions = new ArrayList(Arrays.asList(method.instructions.toArray()));
                for (int[] pattern : patterns) {
                    int patternIndex = searcher.find(pattern, 0);
                    int count = 0;
                    while (patternIndex != -1) {
                        instructions.add(patternIndex + 10, instructions.get(patternIndex));
                        instructions.remove(patternIndex);
                        ++count;
                        ++nFixed;
                        patternIndex = searcher.find(pattern, count);
                    }
                }
                method.instructions.clear();
                for (AbstractInsnNode n : instructions) {
                    method.instructions.add(n);
                }
                verify(method);
            }
        }

        patterns = new int[][]{
                {Opcodes.ALOAD, Opcodes.GETFIELD, Opcodes.LDC, Opcodes.IMUL, Opcodes.ILOAD, Opcodes.IMUL},

        };
        for (ClassNode Class : classes.values()) {
            List<MethodNode> methodList = Class.methods;
            for (MethodNode method : methodList) {
                Searcher searcher = new Searcher(method);
                ArrayList<AbstractInsnNode> instructions = new ArrayList(Arrays.asList(method.instructions.toArray()));
                for (int[] pattern : patterns) {
                    int patternIndex = searcher.find(pattern, 0);
                    int count = 0;
                    while (patternIndex != -1) {
                        instructions.add(patternIndex, instructions.get(patternIndex + 4));
                        instructions.remove(patternIndex + 5);
                        ++count;
                        ++nFixed;
                        patternIndex = searcher.find(pattern, count);
                    }
                }
                method.instructions.clear();
                for (AbstractInsnNode n : instructions)
                    method.instructions.add(n);
                verify(method);
            }
        }

        patterns = new int[][]{
                {Opcodes.GETSTATIC, Opcodes.GETFIELD, Opcodes.LDC, Opcodes.IMUL, Opcodes.BIPUSH, Opcodes.ISHR, Opcodes.GETSTATIC, Opcodes.LDC, Opcodes.IMUL, Opcodes.IADD},
                {Opcodes.GETSTATIC, Opcodes.GETFIELD, Opcodes.LDC, Opcodes.IMUL, Opcodes.BIPUSH, Opcodes.ISHR, Opcodes.GETSTATIC, Opcodes.LDC, Opcodes.IMUL, Searcher.IF},
                {Opcodes.GETSTATIC, Opcodes.GETFIELD, Opcodes.LDC, Opcodes.IMUL, Opcodes.BIPUSH, Opcodes.ISHL, Opcodes.GETSTATIC, Opcodes.LDC, Opcodes.IMUL, Opcodes.IADD},
                {Opcodes.GETSTATIC, Opcodes.GETFIELD, Opcodes.LDC, Opcodes.IMUL, Opcodes.BIPUSH, Opcodes.ISHL, Opcodes.GETSTATIC, Opcodes.LDC, Opcodes.IMUL, Searcher.IF},

        };
        for (ClassNode Class : classes.values()) {
            List<MethodNode> methodList = Class.methods;
            for (MethodNode method : methodList) {
                Searcher searcher = new Searcher(method);
                ArrayList<AbstractInsnNode> instructions = new ArrayList(Arrays.asList(method.instructions.toArray()));
                for (int[] pattern : patterns) {
                    int patternIndex = searcher.find(pattern, 0);
                    int count = 0;
                    while (patternIndex != -1) {
                        instructions.add(patternIndex, instructions.get(patternIndex + 6));
                        instructions.remove(patternIndex + 7);
                        instructions.add(patternIndex + 1, instructions.get(patternIndex + 7));
                        instructions.remove(patternIndex + 8);
                        instructions.add(patternIndex + 2, instructions.get(patternIndex + 8));
                        instructions.remove(patternIndex + 9);
                        ++count;
                        ++nFixed;
                        patternIndex = searcher.find(pattern, count);
                    }
                }
                method.instructions.clear();
                for (AbstractInsnNode n : instructions)
                    method.instructions.add(n);
                verify(method);
            }
        }

        patterns = new int[][]{
                {Opcodes.GETSTATIC, Opcodes.ILOAD, Opcodes.IINC, Opcodes.ILOAD, Opcodes.ILOAD, Opcodes.BIPUSH, Opcodes.ISHL, Opcodes.ILOAD, Opcodes.BIPUSH, Opcodes.ISHL},

        };
        for (ClassNode Class : classes.values()) {
            List<MethodNode> methodList = Class.methods;
            for (MethodNode method : methodList) {
                Searcher searcher = new Searcher(method);
                ArrayList<AbstractInsnNode> instructions = new ArrayList(Arrays.asList(method.instructions.toArray()));
                for (int[] pattern : patterns) {
                    int patternIndex = searcher.find(pattern, 0);
                    int count = 0;
                    while (patternIndex != -1) {
                        instructions.add(patternIndex + 3, instructions.get(patternIndex + 7));
                        instructions.remove(patternIndex + 8);
                        instructions.add(patternIndex + 4, instructions.get(patternIndex + 8));
                        instructions.remove(patternIndex + 9);
                        instructions.add(patternIndex + 5, instructions.get(patternIndex + 9));
                        instructions.remove(patternIndex + 10);
                        ++count;
                        ++nFixed;
                        patternIndex = searcher.find(pattern, count);
                    }
                }
                method.instructions.clear();
                for (AbstractInsnNode n : instructions)
                    method.instructions.add(n);
                verify(method);
            }
        }

        patterns = new int[][]{
                {Opcodes.GETSTATIC, Opcodes.ILOAD, Opcodes.IINC, Opcodes.ILOAD, Opcodes.BIPUSH, Opcodes.ISHL, Opcodes.ILOAD, Opcodes.ILOAD, Opcodes.BIPUSH},

        };
        for (ClassNode Class : classes.values()) {
            List<MethodNode> methodList = Class.methods;
            for (MethodNode method : methodList) {
                Searcher searcher = new Searcher(method);
                ArrayList<AbstractInsnNode> instructions = new ArrayList(Arrays.asList(method.instructions.toArray()));
                for (int[] pattern : patterns) {
                    int patternIndex = searcher.find(pattern, 0);
                    int count = 0;
                    while (patternIndex != -1) {
                        instructions.add(patternIndex + 11, instructions.get(patternIndex + 6));
                        instructions.remove(patternIndex + 6);
                        ++count;
                        ++nFixed;
                        patternIndex = searcher.find(pattern, count);
                    }
                }
                method.instructions.clear();
                for (AbstractInsnNode n : instructions)
                    method.instructions.add(n);
                verify(method);
            }
        }

        for (ClassNode Class : classes.values()) {
            List<MethodNode> methodList = Class.methods;
            for (MethodNode method : methodList) {
                Searcher searcher = new Searcher(method);
                ArrayList<AbstractInsnNode> instructions = new ArrayList(Arrays.asList(method.instructions.toArray()));
                int patternIndex = searcher.find(new int[]{Opcodes.ALOAD, Opcodes.ACONST_NULL, Opcodes.IF_ACMPNE}, 0);
                while (patternIndex != -1) {
                    LabelNode label = ((JumpInsnNode) instructions.get(patternIndex + 2)).label;
                    instructions.remove(patternIndex + 1);
                    instructions.remove(patternIndex + 1);
                    instructions.add(patternIndex + 1, new JumpInsnNode(Opcodes.IFNONNULL, label));
                    method.instructions.clear();
                    for (AbstractInsnNode n : instructions)
                        method.instructions.add(n);
                    verify(method);
                    patternIndex = searcher.find(new int[]{Opcodes.ALOAD, Opcodes.ACONST_NULL, Opcodes.IF_ACMPNE}, 0);
                    ++nFixed;
                }
            }
        }

        patterns = new int[][]{
                {Opcodes.ALOAD, Opcodes.GETFIELD, Opcodes.ALOAD, Opcodes.GETFIELD, Opcodes.LDC, Opcodes.IMUL,
                        Opcodes.ICONST_1, Opcodes.ISUB, Opcodes.IALOAD, Opcodes.SIPUSH, Opcodes.IMUL, Opcodes.ALOAD,
                        Opcodes.GETFIELD, Opcodes.LDC, Opcodes.IMUL, Opcodes.IADD, Opcodes.ISTORE
                },

        };
        boolean bool = false;
        for (ClassNode Class : classes.values()) {
            List<MethodNode> methodList = Class.methods;
            for (MethodNode method : methodList) {
                Searcher searcher = new Searcher(method);
                ArrayList<AbstractInsnNode> instructions = new ArrayList(Arrays.asList(method.instructions.toArray()));
                for (int[] pattern : patterns) {
                    int patternIndex = searcher.find(pattern, 0);
                    int count = 0;
                    while (patternIndex != -1) {
                        bool = true;
                        instructions.add(patternIndex, instructions.get(patternIndex + 11));
                        instructions.remove(patternIndex + 12);
                        instructions.add(patternIndex + 1, instructions.get(patternIndex + 12));
                        instructions.remove(patternIndex + 13);
                        instructions.add(patternIndex + 2, instructions.get(patternIndex + 13));
                        instructions.remove(patternIndex + 14);
                        instructions.add(patternIndex + 3, instructions.get(patternIndex + 14));
                        instructions.remove(patternIndex + 15);
                        ++count;
                        ++nFixed;
                        patternIndex = searcher.find(pattern, count);
                    }
                }
                method.instructions.clear();
                for (AbstractInsnNode n : instructions)
                    method.instructions.add(n);
                verify(method);
                if (bool) {
                    bool = false;
                }
            }
        }

        return nFixed;
    }

    @Override
    public int deobfuscate() {
        int nTotal = 0;
        int nFixed = -1;
        while (nFixed != 0) {
            nFixed = run();
            nTotal = nTotal + nFixed;
        }
        System.out.print("Reordered " + nTotal + " Arithmetic Statements");
        return nTotal;
    }
}