package tterrag.core.client.render;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.ISmartItemModel;

import org.lwjgl.opengl.GL11;

import tterrag.core.api.client.model.IModelTT;

/**
 * Renders an {@link IModelTT} with directional placement
 */
public class DirectionalModelRenderer<T extends TileEntity> extends TileEntitySpecialRenderer implements ISmartItemModel
{
    private TextureAtlasSprite texture;
    private IModelTT modelSMT;

    public DirectionalModelRenderer(IModelTT model, TextureAtlasSprite texture)
    {
        this.modelSMT = model;
        this.texture = texture;
    }

    private void renderDirectionalTileEntityAt(T tile, double x, double y, double z, int metaOverride)
    {
        int meta = getMetadata(tile, metaOverride);
        setup(x, y, z, metaOverride);
        rotate(getRotation(tile, metaOverride));
        renderModel(tile, meta);
    }

    protected final int getMetadata(T tile, int metaOverride)
    {
        return metaOverride >= 0 ? metaOverride : tile.getBlockMetadata();
    }

    protected void setup(double x, double y, double z, int metaOverride)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5f, (float) y - (metaOverride >= 0 ? 0.1f : 0), (float) z + 0.5f);
    }

    protected int getRotation(T tile, int metaOverride)
    {
        return getMetadata(tile, metaOverride);
    }

    protected void rotate(int rotation)
    {
        switch (rotation)
        {
        case 1:
            GL11.glRotatef(180f, 0, 0, 1);
            GL11.glTranslatef(0, -1f, 0);
            break;
        case 2:
            GL11.glRotatef(90f, 1f, 0, 0);
            GL11.glTranslatef(0, -0.5f, -0.5f);
            break;
        case 3:
            GL11.glRotatef(90f, -1f, 0, 0);
            GL11.glTranslatef(0, -0.5f, 0.5f);
            break;
        case 4:
            GL11.glRotatef(90f, 0, 0, -1f);
            GL11.glTranslatef(-0.5f, -0.5f, 0);
            break;
        case 5:
            GL11.glRotatef(90f, 0, 0, 1f);
            GL11.glTranslatef(0.5f, -0.5f, 0);
            break;
        }
    }
    
    protected void renderModel(T tile, int meta)
    {
    	GL11.glTranslated(0, -0.5, 0);
    	IBlockState state = tile.getWorld().getBlockState(tile.getPos());
    	Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModel(tile.getWorld(), this, state, tile.getPos(), Tessellator.getInstance().getWorldRenderer());
        GL11.glPopMatrix();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float yaw, int num)
    {
        renderDirectionalTileEntityAt((T) tile, x, y, z, -1);
    }

	@Override
	public List<BakedQuad> getFaceQuads(EnumFacing side) {
		return modelSMT.getFaceQuads(side);
	}

	@Override
	public List<BakedQuad> getGeneralQuads() {
		return modelSMT.getGeneralQuads();
	}

	@Override
	public boolean isAmbientOcclusion() {
		return true;
	}

	@Override
	public boolean isGui3d() {
		return true;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getTexture() {
		return texture;
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return ItemCameraTransforms.DEFAULT;
	}

	@Override
	public IBakedModel handleItemState(ItemStack stack) {
		return this;
	}
}
