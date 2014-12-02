package tterrag.core.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.entity.Entity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TTEntityUtils
{
    public static void setEntityVelocity(Entity entity, double velX, double velY, double velZ)
    {
        entity.motionX = velX;
        entity.motionY = velY;
        entity.motionZ = velZ;
    }
}
