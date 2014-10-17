package tterrag.core.common.transform;

import net.minecraft.world.WorldType;
import tterrag.core.common.config.ConfigHandler;

public class TTCoreMethods
{
    public static boolean hasVoidParticles(WorldType type, boolean hasSky)
    {
        if (ConfigHandler.disableVoidFog == 0)
        {
            return type != WorldType.FLAT && !hasSky;

        }
        else if (ConfigHandler.disableVoidFog == 1)
        {
            return type != WorldType.FLAT && type != WorldType.DEFAULT && !hasSky;
        }
        else
        {
            return false;
        }
    }
    
    public static int getMaxAnvilCost()
    {
        return ConfigHandler.anvilMaxLevel;
    }
    
    private int test = 20;
    public void test()
    {
        if (this.test > getMaxAnvilCost())
        {
            ;
        }
    }
}
