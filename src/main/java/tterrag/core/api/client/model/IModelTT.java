package tterrag.core.api.client.model;

import java.util.List;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.EnumFacing;

public interface IModelTT
{
	List<BakedQuad> getFaceQuads(EnumFacing side);

	List<BakedQuad> getGeneralQuads();
}
