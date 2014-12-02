package tterrag.core.common.util;

import net.minecraft.entity.Entity;

public class TTEntityUtils
{
    public static void setEntityVelocity(Entity entity, double velX, double velY, double velZ)
    {
        entity.motionX = velX;
        entity.motionY = velY;
        entity.motionZ = velZ;
    }
}
