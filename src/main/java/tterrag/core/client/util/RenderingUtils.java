package tterrag.core.client.util;

import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import tterrag.core.client.handlers.ClientHandler;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RenderingUtils
{
    /**
     * Renders an item entity in 3D
     * 
     * @param item The item to render
     * @param rotate Whether to "spin" the item like it would if it were a real dropped entity
     */
    public static void render3DItem(EntityItem item, boolean rotate)
    {
        float rot = getRotation(1.0f);

        glPushMatrix();
        glDepthMask(true);
        rotate &= Minecraft.getMinecraft().gameSettings.fancyGraphics;

        if (rotate)
        {
            glRotatef(rot, 0, 1, 0);
        }

        item.hoverStart = 0.0F;
        Minecraft.getMinecraft().getRenderManager().renderEntityWithPosYaw(item, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);

        glPopMatrix();
    }

    public static float getRotation(float mult)
    {
        return ClientHandler.getTicksElapsed() * mult;
    }

    public static void renderBillboardQuad(float rot, double scale)
    {
        glPushMatrix();

        RenderManager render = Minecraft.getMinecraft().getRenderManager();
        glRotatef(-render.playerViewY, 0.0F, 1.0F, 0.0F);
        glRotatef(render.playerViewX, 1.0F, 0.0F, 0.0F);

        glPushMatrix();

        glRotatef(rot, 0, 0, 1);

        WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();
        worldRenderer.startDrawingQuads();
        glColor3f(1, 1, 1);
        worldRenderer.setColorRGBA(255, 255, 255, 255);
        worldRenderer.addVertexWithUV(-scale, -scale, 0, 0, 0);
        worldRenderer.addVertexWithUV(-scale, scale, 0, 0, 1);
        worldRenderer.addVertexWithUV(scale, scale, 0, 1, 1);
        worldRenderer.addVertexWithUV(scale, -scale, 0, 1, 0);
        Tessellator.getInstance().draw();
        glPopMatrix();
    }

    public static void rotateToPlayer(RenderManager render)
    {
        glRotatef(-render.playerViewY, 0.0F, 1.0F, 0.0F);
        glRotatef(render.playerViewX, 1.0F, 0.0F, 0.0F);
    }
}
