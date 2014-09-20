package tterrag.core.common.tweaks;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;

public class ExtraRecipes implements ITweak
{
    public static final ITweak INSTANCE = new ExtraRecipes();
    private ExtraRecipes() {}
    
    @Override
    public void load()
    {
        registerSlabToBlock();
        GameRegistry.addShapelessRecipe(new ItemStack(Items.paper, 2), Items.book);
    }
    
    private final String[] slabEndingsWood = { "WoodOak", "WoodSpruce", "WoodBirch", "WoodJungle", "WoodAcacia", "WoodDarkOak" };
    private final String[] slabEndingsStone = { "Stone", "Sandstone", "Cobblestone", "Bricks", "StoneBricks", "NetherBrick", "Quartz" };
    private final Block[] slabResults = { Blocks.stone, Blocks.sandstone, Blocks.cobblestone, Blocks.brick_block, Blocks.stonebrick, Blocks.nether_brick, Blocks.quartz_block };
    
    private void registerSlabToBlock()
    {
        for (int i = 0; i <= 1; i++)
        {
            String[] arr = i == 0 ? slabEndingsWood : slabEndingsStone;
            Block[] results = i == 1 ? slabResults : null;
            for (int j = 0; j < arr.length; j++)
            {
                GameRegistry.addRecipe(new ShapelessOreRecipe(i == 0 ? new ItemStack(Blocks.planks, 1, j) : new ItemStack(results[j]), "slab" + arr[j],  "slab" + arr[j]));
            }
        }
    }
}
