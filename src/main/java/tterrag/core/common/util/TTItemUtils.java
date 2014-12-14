package tterrag.core.common.util;

import java.util.Random;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TTItemUtils
{
    private static final Random rand = new Random();
    
    /**
     * @author powercrystals
     */
    public static boolean stacksEqual(ItemStack s1, ItemStack s2)
    {
        if (s1 == null && s2 == null)
            return true;
        if (s1 == null || s2 == null)
            return false;
        if (!s1.isItemEqual(s2))
            return false;
        if (s1.getTagCompound() == null && s2.getTagCompound() == null)
            return true;
        if (s1.getTagCompound() == null || s2.getTagCompound() == null)
            return false;
        return s1.getTagCompound().equals(s2.getTagCompound());
    }

    public static void spawnItemInWorldWithRandomMotion(World world, ItemStack item, int x, int y, int z)
    {
        if (item != null)
        {
            spawnItemInWorldWithRandomMotion(new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, item));
        }
    }

    public static void spawnItemInWorldWithRandomMotion(EntityItem entity)
    {
        entity.delayBeforeCanPickup = 10;
        
        float f = (rand.nextFloat() * 0.1f) - 0.05f;
        float f1 = (rand.nextFloat() * 0.1f) - 0.05f;
        float f2 = (rand.nextFloat() * 0.1f) - 0.05f;

        entity.motionX += f;
        entity.motionY += f1;
        entity.motionZ += f2;

        entity.worldObj.spawnEntityInWorld(entity);
    }
}
