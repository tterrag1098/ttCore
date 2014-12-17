package tterrag.core.common.transform;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import lombok.AllArgsConstructor;
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

import static org.objectweb.asm.Opcodes.*;

@MCVersion(value = "1.7.10")
public class TTCoreTransformer implements IClassTransformer
{
    @AllArgsConstructor
    private static class ObfSafeName 
    {
        private String mcp, srg;
        
        private String getName() 
        {
            return TTCorePlugin.runtimeDeobfEnabled ? srg : mcp;
        }
        
        @Override
        public boolean equals(Object obj)
        {
            if (obj instanceof String)
            {
                return obj.equals(mcp) || obj.equals(srg);
            }
            else
            {
                return ((ObfSafeName)obj).mcp.equals(mcp) && ((ObfSafeName)obj).srg.equals(srg);
            }
        }
        
        // no hashcode because I'm naughty
    }
    
    private static abstract class Transform
    {
        abstract void transform(Iterator<MethodNode> methods);
    }
    
    private static final String worldTypeClass     = "net.minecraft.world.WorldType";
    private static final ObfSafeName voidFogMethod      = new ObfSafeName("hasVoidParticles", "func_76564_j");
    private static final String voidFogMethodSig   = "(Lnet/minecraft/world/WorldType;Z)Z";

    private static final String anvilContainerClass    = "net.minecraft.inventory.ContainerRepair";
    private static final ObfSafeName anvilContainerMethod   = new ObfSafeName("updateRepairOutput", "func_82848_d");

    private static final String anvilGuiClass  = "net.minecraft.client.gui.GuiRepair";
    private static final ObfSafeName anvilGuiMethod = new ObfSafeName("drawGuiContainerForegroundLayer", "func_146979_b");

    private static final String enchantHelperClass = "net.minecraft.enchantment.EnchantmentHelper";
    private static final String enchantHelperMethodSig = "(Lnet/minecraft/item/ItemStack;I)I";
    private static final ObfSafeName buildEnchantListMethod = new ObfSafeName("buildEnchantmentList", "func_77513_b");
    private static final ObfSafeName calcEnchantabilityMethod = new ObfSafeName("calcItemStackEnchantability", "func_77514_a");

    private static final String itemStackClass = "net.minecraft.item.ItemStack";
    private static final ObfSafeName itemStackMethod = new ObfSafeName("getRarity", "func_77953_t");
    private static final String itemStackMethodSig = "(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/EnumRarity;";

    private static final String entityArrowClass = "net.minecraft.entity.projectile.EntityArrow";
    private static final ObfSafeName entityArrowMethod = new ObfSafeName("onUpdate", "func_70071_h_");
    private static final String entityArrowMethodSig = "(Lnet/minecraft/entity/projectile/EntityArrow;)V";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass)
    {
        // Void fog removal
        if (transformedName.equals(worldTypeClass))
        {
            basicClass = transform(basicClass, worldTypeClass, voidFogMethod, new Transform()
            {
                @Override
                void transform(Iterator<MethodNode> methods)
                {
                    while (methods.hasNext())
                    {
                        MethodNode m = methods.next();
                        if (voidFogMethod.equals(m.name))
                        {
                            m.instructions.clear();

                            m.instructions.add(new VarInsnNode(ALOAD, 0));
                            m.instructions.add(new VarInsnNode(ILOAD, 1));
                            m.instructions.add(new MethodInsnNode(INVOKESTATIC, "tterrag/core/common/transform/TTCoreMethods", "hasVoidParticles", voidFogMethodSig));
                            m.instructions.add(new InsnNode(IRETURN));

                            break;
                        }
                    }
                }
            });
        }
        // Anvil max level
        else if (transformedName.equals(anvilContainerClass) || transformedName.equals(anvilGuiClass))
        {
            basicClass = transform(basicClass, anvilContainerClass, anvilContainerMethod, new Transform()
            {
                @Override
                void transform(Iterator<MethodNode> methods)
                {
                    while (methods.hasNext())
                    {
                        MethodNode m = methods.next();
                        if (anvilContainerMethod.equals(m.name) || anvilGuiMethod.equals(m.name))
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
                }
            });
        }
        // Item Enchantability Event
        else if (transformedName.equals(enchantHelperClass))
        {
            final Map<String, int[]> data = new HashMap<String, int[]>();
            data.put(buildEnchantListMethod.getName(), new int[] {1, 4});
            data.put(calcEnchantabilityMethod.getName(), new int[] {3, 5});
            Transform transformer = new Transform()
            {
                @Override
                void transform(Iterator<MethodNode> methods)
                {
                    while (methods.hasNext())
                    {
                        MethodNode m = methods.next();
                        if (data.keySet().contains(m.name))
                        {
                            int[] indeces = data.get(m.name);
                            for (int i = 0; i < m.instructions.size(); i++)
                            {
                                AbstractInsnNode next = m.instructions.get(i);
                                if (next instanceof VarInsnNode)
                                {
                                    VarInsnNode varNode = (VarInsnNode)next;
                                    if (varNode.getOpcode() == ISTORE && varNode.var == indeces[1])
                                    {
                                        InsnList toAdd = new InsnList();
                                        toAdd.add(new VarInsnNode(ALOAD, indeces[0]));
                                        toAdd.add(new VarInsnNode(ILOAD, indeces[1]));
                                        toAdd.add(new MethodInsnNode(INVOKESTATIC, "tterrag/core/common/transform/TTCoreMethods", "getItemEnchantability", enchantHelperMethodSig));
                                        toAdd.add(new VarInsnNode(ISTORE, indeces[1]));
                                        m.instructions.insert(next, toAdd);
                                        break;
                                    }
                                }
                            }

                            break;
                        }
                    }
                }
            };
            
            basicClass = transform(basicClass, enchantHelperClass, buildEnchantListMethod, transformer);
        }
        // ItemRarity Event
        else if (transformedName.equals(itemStackClass))
        {
            basicClass = transform(basicClass, itemStackClass, itemStackMethod, new Transform()
            {
                @Override
                void transform(Iterator<MethodNode> methods)
                {
                    while (methods.hasNext())
                    {
                        MethodNode m = methods.next();
                        if (itemStackMethod.equals(m.name))
                        {
                            m.instructions.clear();

                            m.instructions.add(new VarInsnNode(ALOAD, 0));
                            m.instructions.add(new MethodInsnNode(INVOKESTATIC, "tterrag/core/common/transform/TTCoreMethods", "getItemRarity", itemStackMethodSig));
                            m.instructions.add(new InsnNode(ARETURN));

                            break;
                        }
                    }
                }
            });
        }
        // ArrowUpdate Event
        else if (transformedName.equals(entityArrowClass))
        {
            basicClass = transform(basicClass, entityArrowClass, entityArrowMethod, new Transform()
            {
                @Override
                void transform(Iterator<MethodNode> methods)
                {
                    while (methods.hasNext())
                    {
                        MethodNode m = methods.next();
                        if (entityArrowMethod.equals(m.name))
                        {
                            for (int i = 0; i < m.instructions.size(); i++)
                            {
                                AbstractInsnNode next = m.instructions.get(i);
                                if (next instanceof MethodInsnNode)
                                {
                                    InsnList toAdd = new InsnList();
                                    toAdd.add(new VarInsnNode(ALOAD, 0));
                                    toAdd.add(new MethodInsnNode(INVOKESTATIC, "tterrag/core/common/transform/TTCoreMethods", "onArrowUpdate", entityArrowMethodSig));
                                    m.instructions.insert(next, toAdd);
                                    break;
                                }
                            }
                            break;
                        }
                    }       
                }
            });
        }

        return basicClass;
    }
    
    private byte[] transform(byte[] classBytes, String className, ObfSafeName methodName, Transform transformer)
    {
        TTCore.logger.info("Transforming Class [" + className + "], Method [" + methodName.getName() + "]");
       
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(classBytes);
        classReader.accept(classNode, 0);

        Iterator<MethodNode> methods = classNode.methods.iterator();

        transformer.transform(methods);
        
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(cw);
        TTCore.logger.info("Transforming " + className + " Finished.");
        return cw.toByteArray();
    }
}
