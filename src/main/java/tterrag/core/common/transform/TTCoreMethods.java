package tterrag.core.common.transform;

import net.minecraft.world.WorldType;
import tterrag.core.common.config.ConfigHandler;

public class TTCoreMethods
{
    public static boolean hasVoidParticles(WorldType type, boolean hasSky)
    {
        if (ConfigHandler.disableVoidFog)
        {
            return type != WorldType.FLAT && type != WorldType.DEFAULT && !hasSky;
        }
        else
        {
            return type != WorldType.FLAT && !hasSky;
        }
    }
}
