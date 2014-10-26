package tterrag.core.common.transform;

import static org.objectweb.asm.Opcodes.*;

import java.util.Iterator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import tterrag.core.TTCore;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;

@MCVersion(value = "1.7.10")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TTCoreTransformer implements IClassTransformer
{
    private static final String worldTypeCLassDeobf = "net.minecraft.world.WorldType";
    private static final String worldTypeClassObf = "ahm";
    private static final String voidFogMethodDeobf = "hasVoidParticles";
    private static final String voidFogMethodObf = "func_76564_j";
    private static final String voidFogMethodSigDeobf = "(Lnet/minecraft/world/WorldType;Z)Z";
    private static final String voidFogMethodSigObf = "(Lahm;Z)Z";

    private static final String anvilContainerClassDeobf = "net.minecraft.inventory.ContainerRepair";
    private static final String anvilContainerClassObf = "zu";
    private static final String anvilContainerMethodDeobf = "updateRepairOutput";
    private static final String anvilContainerMethodObf = "func_82848_d";

    private static final String anvilGuiClassDeobf = "net.minecraft.client.gui.GuiRepair";
    private static final String anvilGuiClassObf = "bey";
    private static final String anvilGuiMethodDeobf = "drawGuiContainerForegroundLayer";
    private static final String anvilGuiMethodObf = "func_146979_b";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass)
    {
        if (name.equals(worldTypeCLassDeobf) || name.equals(worldTypeClassObf))
        {
            basicClass = transformWorldType(name, basicClass);
        }
        else if (name.equals(anvilContainerClassDeobf) || name.equals(anvilContainerClassObf))
        {
            boolean obf = name.equals(anvilContainerClassObf);
            basicClass = transformAnvil(name, basicClass, obf ? anvilContainerMethodObf : anvilContainerMethodDeobf);
        }
        else if (name.equals(anvilGuiClassDeobf) || name.equals(anvilGuiClassObf))
        {
            boolean obf = name.equals(anvilGuiClassObf);
            basicClass = transformAnvil(name, basicClass, obf ? anvilGuiMethodObf : anvilGuiMethodDeobf);
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
        String methodSig = obf ? voidFogMethodSigObf : voidFogMethodSigDeobf;

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

    private byte[] transformAnvil(String name, byte[] basicClass, String methodName)
    {
        TTCore.logger.info("Transfroming anvil XP limit - Class " + name + "...");

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);

        Iterator<MethodNode> methods = classNode.methods.iterator();
        while (methods.hasNext())
        {
            MethodNode m = methods.next();
            if (m.name.equals(methodName))
            {
                for (int i = 0; i < m.instructions.size(); i++)
                {
                    AbstractInsnNode next = m.instructions.get(i);

                    next = m.instructions.get(i);
                    if (next instanceof IntInsnNode && ((IntInsnNode) next).operand == 40)
                    {
                        m.instructions.set(next, new MethodInsnNode(INVOKESTATIC, "tterrag/core/common/transform/TTCoreMethods", "getMaxAnvilCost", "()I"));
                    }
                }
            }
        }

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(cw);
        return cw.toByteArray();
    }
}
