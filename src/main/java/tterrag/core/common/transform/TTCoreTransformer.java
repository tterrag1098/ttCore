package tterrag.core.common.transform;

import static org.objectweb.asm.Opcodes.*;

import java.util.Iterator;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import tterrag.core.TTCore;

public class TTCoreTransformer implements IClassTransformer
{
    private static final String worldTypeCLassDeobf       = "net.minecraft.world.WorldType";
    private static final String worldTypeClassObf         = "ahm";
    private static final String voidFogMethodDeobf        = "hasVoidParticles";
    private static final String voidFogMethodObf          = "func_76564_j";
    private static final String voidFogMethodSigDeobf     = "(Lnet/minecraft/world/WorldType;Z)Z";
    private static final String voidFogMethodSigObf       = "(Lahm;Z)Z";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass)
    {
        if (name.equals(worldTypeCLassDeobf) || name.equals(worldTypeClassObf))
        {
            basicClass = transformWorldType(name, basicClass);
        }

        return basicClass;
    }

    private byte[] transformWorldType(String name, byte[] basicClass)
    {
        TTCore.logger.info("Transforming WorldType...");
        
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);
        
        boolean obf = name.equals(worldTypeClassObf);
        String methodSig =  obf ? voidFogMethodSigObf : voidFogMethodSigDeobf;
        
        Iterator<MethodNode> methods = classNode.methods.iterator();
        while (methods.hasNext())
        {
            MethodNode m = methods.next();
            if (m.name.equals(voidFogMethodDeobf) || m.name.equals(voidFogMethodObf))
            {
                m.instructions.clear();
                
                m.instructions.add(new VarInsnNode(ALOAD, 0));
                m.instructions.add(new VarInsnNode(ILOAD, 1));
                m.instructions.add(new MethodInsnNode(INVOKESTATIC, "tterrag/core/common/transform/TTCoreMethods", "hasVoidParticles", methodSig));
                m.instructions.add(new InsnNode(IRETURN));

                break;
            }
        }
        
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(cw);
        return cw.toByteArray();
    }
}
