package tterrag.core.common.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.oredict.OreDictionary;
import tterrag.core.TTCore;
import tterrag.core.common.Handlers.Handler;
import tterrag.core.common.Handlers.Handler.HandlerType;
import tterrag.core.common.config.ConfigHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Handler(HandlerType.FORGE)
public class OreDictTooltipHandler
{
    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event)
    {
        if (ConfigHandler.extraDebugStuff && Minecraft.getMinecraft().gameSettings.advancedItemTooltips)
        {
            event.toolTip.add(Item.itemRegistry.getNameForObject(event.itemStack.getItem()));
        }
     
        if (ConfigHandler.showOredictTooltips)
        {
            int[] ids = OreDictionary.getOreIDs(event.itemStack);

            if (ids.length > 0)
            {
                event.toolTip.add(TTCore.lang.localize("tooltip.oreDictNames"));
                for (int i : ids)
                {
                    event.toolTip.add("  - " + OreDictionary.getOreName(i));
                }
            }
        }
    }
}
