package tterrag.core.common.tweaks;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

public class VanillaTweaks implements ITweak
{
    public static final ITweak INSTANCE = new VanillaTweaks();
    private VanillaTweaks() {}
    
    @Override
    public void load()
    {
        Blocks.bed.setStepSound(Block.soundTypeCloth); // beds are not stone
        Items.boat.setMaxStackSize(16);
    }
}
