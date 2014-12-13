package tterrag.core.common.handlers;

import java.util.Map;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tterrag.core.api.common.enchant.IAdvancedEnchant;
import tterrag.core.common.Handlers.Handler;
import tterrag.core.common.Handlers.Handler.HandlerType;

@Handler(HandlerType.FORGE)
public class EnchantTooltipHandler
{
    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public void handleTooltip(ItemTooltipEvent event)
    {
        if (event.itemStack.getTagCompound() != null)
        {
            Map<Integer, Integer> enchantments = EnchantmentHelper.getEnchantments(event.itemStack);

            for (Integer integer : enchantments.keySet())
            {
                Enchantment enchant = Enchantment.getEnchantmentById(integer);
                
                if (enchant instanceof IAdvancedEnchant)
                {
                    for (int i = 0; i < event.toolTip.size(); i++)
                    {
                        if (event.toolTip.get(i).contains(StatCollector.translateToLocal(enchant.getName())))
                        {
                            for (String s : ((IAdvancedEnchant) enchant).getTooltipDetails(event.itemStack))
                            {
                                event.toolTip.add(i + 1, EnumChatFormatting.DARK_GRAY.toString() + EnumChatFormatting.ITALIC + "  - " + s);
                                i++;
                            }
                        }
                    }
                }
            }
        }
    }
}
