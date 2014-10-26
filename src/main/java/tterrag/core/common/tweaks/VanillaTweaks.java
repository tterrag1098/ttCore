package tterrag.core.common.tweaks;

import lombok.Singleton;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

@Singleton
public class VanillaTweaks implements ITweak
{
    @Override
    public void load()
    {
        Blocks.bed.setStepSound(Block.soundTypeCloth); // beds are not stone
        Items.boat.setMaxStackSize(16);
    }
}
