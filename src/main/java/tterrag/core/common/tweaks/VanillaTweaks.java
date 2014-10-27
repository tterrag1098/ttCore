package tterrag.core.common.tweaks;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VanillaTweaks implements ITweak
{
    public static final VanillaTweaks INSTANCE = new VanillaTweaks();
    
    @Override
    public void load()
    {
        Blocks.bed.setStepSound(Block.soundTypeCloth); // beds are not stone
        Items.boat.setMaxStackSize(16);
    }
}
