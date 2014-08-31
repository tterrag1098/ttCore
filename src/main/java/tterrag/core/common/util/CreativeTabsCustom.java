package tterrag.core.common.util;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CreativeTabsCustom extends CreativeTabs
{
    private ItemStack displayStack;
    
    /**
     * @param unloc Unlocalized name of the tab
     * @param item Item to display
     */
    public CreativeTabsCustom(String unloc, Item item)
    {
        this(unloc, item, 0);
    }
    
    /**
     * @param unloc Unlocalized name of the tab
     * @param item Item to display
     * @param damage Damage of item to display
     */
    public CreativeTabsCustom(String unloc, Item item, int damage)
    {
        this(unloc, new ItemStack(item, 1, damage));
    }
    
    /**
     * @param unloc Unlocalized name of the tab
     * @param display ItemStack to display
     */
    public CreativeTabsCustom(String unloc, ItemStack display)
    {
        super(unloc);
        this.displayStack = display.copy();
    }

    @Override
    public Item getTabIconItem() { return null; } // use getIconItemStack()
    
    @Override
    public ItemStack getIconItemStack()
    {
        return displayStack;
    }
}
