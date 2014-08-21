package tterrag.core.common;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreDict
{
    public static void registerVanilla()
    {
        safeRegister("barsIron", Blocks.iron_bars);
        safeRegister("blockHopper", Blocks.hopper);
        safeRegister("blockObsidian", Blocks.obsidian);
        safeRegister("itemNetherStar", Items.nether_star);
    }
    
    public static void safeRegister(String name, Block block)
    {
        safeRegister(name, Item.getItemFromBlock(block));
    }

    public static void safeRegister(String name, Item item)
    {
        if (!isRegistered(item, OreDictionary.getOres(name)))
            OreDictionary.registerOre(name, item);
    }
    
    private static boolean isRegistered(Item item, ArrayList<ItemStack> toCheck)
    {
        for (ItemStack stack : toCheck)
        {
            if (stack != null && stack.getItem() == item)
            {
                return true;
            }
        }
        return false;
    }
}
