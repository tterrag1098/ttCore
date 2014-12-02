package tterrag.core.common.util;

import java.util.Random;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TTEntityUtils
{
    private static final Random rand = new Random();
    
    public static void setEntityVelocity(Entity entity, double velX, double velY, double velZ)
    {
        entity.motionX = velX;
        entity.motionY = velY;
        entity.motionZ = velZ;
    }
    
    // I don't really expect this to be very readable...but it works
    public static void spawnFireworkAround(BlockCoord block, int dimID)
    {
        World world = DimensionManager.getWorld(dimID);

        BlockCoord pos = new BlockCoord(0, 0, 0);

        while (!world.isAirBlock(pos.x, pos.y, pos.z))
        {
            pos.setPosition(moveRandomly(block.x), block.y + 2, moveRandomly(block.z));
        }

        ItemStack firework = new ItemStack(Items.fireworks);
        firework.stackTagCompound = new NBTTagCompound();
        NBTTagCompound expl = new NBTTagCompound();
        expl.setBoolean("Flicker", true);
        expl.setBoolean("Trail", true);

        int[] colors = new int[rand.nextInt(8) + 1];
        for (int i = 0; i < colors.length; i++)
        {
            colors[i] = ItemDye.field_150922_c[rand.nextInt(16)];
        }
        expl.setIntArray("Colors", colors);
        byte type = (byte) (rand.nextInt(3) + 1);
        type = type == 3 ? 4 : type;
        expl.setByte("Type", type);

        NBTTagList explosions = new NBTTagList();
        explosions.appendTag(expl);

        NBTTagCompound fireworkTag = new NBTTagCompound();
        fireworkTag.setTag("Explosions", explosions);
        fireworkTag.setByte("Flight", (byte) 1);
        firework.stackTagCompound.setTag("Fireworks", fireworkTag);

        EntityFireworkRocket e = new EntityFireworkRocket(world, pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, firework);
        world.spawnEntityInWorld(e);
    }

    private static final double distMult = 12d;

    private static double moveRandomly(double base)
    {
        return base + 0.5 + rand.nextDouble() * distMult - (distMult / 2);
    }
}
