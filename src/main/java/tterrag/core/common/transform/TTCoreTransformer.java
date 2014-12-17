package tterrag.core.common.transform;

import static org.objectweb.asm.Opcodes.*;

import java.util.Iterator;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import tterrag.core.TTCore;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;

@MCVersion(value = "1.7.10")
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

    private static final String itemStackClassDeobf = "net.minecraft.item.ItemStack";
    private static final String itemStackClassObf = "add";
    private static final String itemStackMethodDeobf = "getRarity";
    private static final String itemStackMethodObf = "func_77953_t";
    private static final String itemStackMethodSigDeobf = "(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/EnumRarity;";
    private static final String itemStackMethodSigObf = "(Ladd;)Ladq;";

    private static final String enchantHelperClassDeobf = "net.minecraft.enchantment.EnchantmentHelper";
    private static final String enchantHelperClassObf = "afv";
    private static final String enchantHelperMethodSigDeobf = "(Lnet/minecraft/item/ItemStack;I)I";
    private static final String enchantHelperMethodSigObf = "(Ladd;I)I";
    private static final String buildEnchantListMethodDeobf = "buildEnchantmentList";
    private static final String buildEnchantListMethodObf = "func_77513_b";
    private static final String calcEnchantabilityMethodDeobf = "calcItemStackEnchantability";
    private static final String calcEnchantabilityMethodObf = "func_77514_a";

    private static final String entityArrowClassDeobf = "net.minecraft.entity.projectile.EntityArrow";
    private static final String entityArrowClassObf = "zc";
    private static final String entityArrowMethodDeobf = "onUpdate";
    private static final String entityArrowMethodObf = "func_70071_h_";
    private static final String entityArrowMethodSigDeobf = "(Lnet/minecraft/entity/projectile/EntityArrow;)V";
    private static final String entityArrowMethodSigObf = "(Lzc;)V";

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
        // Item Enchantability
        else if (name.equals(enchantHelperClassDeobf) || name.equals(enchantHelperClassObf))
        {
            TTCore.logger.info("Transforming EnchantmentHelper - Class [" + name + "]...");
            boolean obf = name.equals(enchantHelperClassObf);
            basicClass = transformEnchantHelper(name, basicClass, obf ? buildEnchantListMethodObf : buildEnchantListMethodDeobf, 1, 4);
            basicClass = transformEnchantHelper(name, basicClass, obf ? calcEnchantabilityMethodObf : calcEnchantabilityMethodDeobf, 3, 5);
            TTCore.logger.info("Transforming EnchantmentHelper - Finished.");
        }
        // ItemRarity
        else if (name.equals(itemStackClassDeobf) || name.equals(itemStackClassObf))
        {
            boolean obf = name.equals(itemStackClassObf);
            basicClass = transformItemStack(name, basicClass, obf ? itemStackMethodObf : itemStackMethodDeobf);
        }
        // ArrowUpdate
        else if (name.equals(entityArrowClassDeobf) || name.equals(entityArrowClassObf))
        {
            boolean obf = name.equals(entityArrowClassObf);
            basicClass = transformArrow(name, basicClass, obf ? entityArrowMethodObf : entityArrowMethodDeobf);
        }

        return basicClass;
    }

    private byte[] transformWorldType(String name, byte[] basicClass)
    {
        TTCore.logger.info("Transforming WorldType - Class [" + name + "]...");

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
        TTCore.logger.info("Transforming WorldType - Finished.");
        return cw.toByteArray();
    }

    private byte[] transformAnvil(String name, byte[] basicClass, String methodName)
    {
        TTCore.logger.info("Transforming Anvil XP Cap - Class [" + name + "]...");

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
        TTCore.logger.info("Transforming Anvil XP Cap - Finished.");
        return cw.toByteArray();
    }

    private byte[] transformEnchantHelper(String name, byte[] basicClass, String methodName, int stackIndex, int enchantabilityIndex)
    {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);

        boolean obf = name.equals(enchantHelperClassObf);
        String methodSig = obf ? enchantHelperMethodSigObf : enchantHelperMethodSigDeobf;

        Iterator<MethodNode> methods = classNode.methods.iterator();
        while (methods.hasNext())
        {
            MethodNode m = methods.next();
            if (m.name.equals(methodName))
            {
                for (int i = 0; i < m.instructions.size(); i++)
                {
                    AbstractInsnNode next = m.instructions.get(i);
                    if (next instanceof VarInsnNode)
                    {
                        VarInsnNode varNode = (VarInsnNode)next;
                        if (varNode.getOpcode() == ISTORE && varNode.var == enchantabilityIndex)
                        {
                            InsnList toAdd = new InsnList();
                            toAdd.add(new VarInsnNode(ALOAD, stackIndex));
                            toAdd.add(new VarInsnNode(ILOAD, enchantabilityIndex));
                            toAdd.add(new MethodInsnNode(INVOKESTATIC, "tterrag/core/common/transform/TTCoreMethods", "getItemEnchantability", methodSig));
                            toAdd.add(new VarInsnNode(ISTORE, enchantabilityIndex));
                            m.instructions.insert(next, toAdd);
                            break;
                        }
                    }
                }

                break;
            }
        }

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(cw);
        return cw.toByteArray();
    }

    private byte[] transformItemStack(String name, byte[] basicClass, String methodName)
    {
        TTCore.logger.info("Transforming ItemStack - Class [" + name + "]...");

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);

        boolean obf = name.equals(itemStackClassObf);
        String methodSig = obf ? itemStackMethodSigObf : itemStackMethodSigDeobf;

        Iterator<MethodNode> methods = classNode.methods.iterator();
        while (methods.hasNext())
        {
            MethodNode m = methods.next();
            if (m.name.equals(methodName) && m.desc.equals("()Ladq;"))
            {
                m.instructions.clear();

                m.instructions.add(new VarInsnNode(ALOAD, 0));
                m.instructions.add(new MethodInsnNode(INVOKESTATIC, "tterrag/core/common/transform/TTCoreMethods", "getItemRarity", methodSig));
                m.instructions.add(new InsnNode(ARETURN));

                break;
            }
        }

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(cw);

        TTCore.logger.info("Transforming ItemStack - Finished.");
        return cw.toByteArray();
    }

    private byte[] transformArrow(String name, byte[] basicClass, String methodName)
    {
        TTCore.logger.info("Transforming EntityArrow - Class [" + name + "]...");

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);

        boolean obf = name.equals(entityArrowClassObf);
        String methodSig = obf ? entityArrowMethodSigObf : entityArrowMethodSigDeobf;

        Iterator<MethodNode> methods = classNode.methods.iterator();
        while (methods.hasNext())
        {
            MethodNode m = methods.next();
            if (m.name.equals(methodName) && m.desc.equals("()V"))
            {
                for (int i = 0; i < m.instructions.size(); i++)
                {
                    AbstractInsnNode next = m.instructions.get(i);
                    if (next instanceof MethodInsnNode)
                    {
                        InsnList toAdd = new InsnList();
                        toAdd.add(new VarInsnNode(ALOAD, 0));
                        toAdd.add(new MethodInsnNode(INVOKESTATIC, "tterrag/core/common/transform/TTCoreMethods", "onArrowUpdate", methodSig));
                        m.instructions.insert(next, toAdd);
                        break;
                    }
                }

                break;
            }
        }

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(cw);

        TTCore.logger.info("Transforming EntityArrow - Finished.");
        return cw.toByteArray();
    }
}
