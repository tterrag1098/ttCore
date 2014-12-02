package tterrag.core.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.lwjgl.opengl.GL11;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TTColorUtils
{
    /**
     * Turns an int into a glColor4f function
     * 
     * @author Buildcraft team
     */
    public static void setGLColorFromInt(int color)
    {
        float red = (color >> 16 & 255) / 255.0F;
        float green = (color >> 8 & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;
        GL11.glColor4f(red, green, blue, 1.0F);
    }

    public static int toHex(int r, int g, int b)
    {
        int hex = 0;
        hex = hex | ((r) << 16);
        hex = hex | ((g) << 8);
        hex = hex | ((b));
        return hex;
    }
}
