package tterrag.core.client.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.oredict.OreDictionary;

import org.lwjgl.input.Keyboard;

import tterrag.core.TTCore;
import tterrag.core.common.Handlers.Handler;
import tterrag.core.common.Handlers.Handler.HandlerType;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import static tterrag.core.common.config.ConfigHandler.*;

@Handler(HandlerType.FORGE)
public class OreDictTooltipHandler
{
    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event)
    {
        boolean shiftDown = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
        boolean debugMode = Minecraft.getMinecraft().gameSettings.advancedItemTooltips;
        boolean doRegistry = showRegistryNameTooltips == 3 ? debugMode : showRegistryNameTooltips == 2 ? shiftDown : showRegistryNameTooltips == 1;
        boolean doOredict = showOredictTooltips == 3 ? debugMode : showOredictTooltips == 2 ? shiftDown : showOredictTooltips == 1;

        if (doRegistry)
        {
            event.toolTip.add(Item.itemRegistry.getNameForObject(event.itemStack.getItem()));
        }

        if (doOredict)
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
