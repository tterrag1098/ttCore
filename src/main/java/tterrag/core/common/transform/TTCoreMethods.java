package tterrag.core.common.transform;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldType;
import net.minecraftforge.common.MinecraftForge;
import tterrag.core.common.config.ConfigHandler;
import tterrag.core.common.event.ArrowUpdateEvent;
import tterrag.core.common.event.ItemStackEvent.ItemEnchantabilityEvent;
import tterrag.core.common.event.ItemStackEvent.ItemRarityEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TTCoreMethods
{
    public static boolean hasVoidParticles(WorldType type, boolean hasSky)
    {
        if (ConfigHandler.disableVoidFog == 0)
        {
            return type != WorldType.FLAT && !hasSky;
        }
        else if (ConfigHandler.disableVoidFog == 1)
        {
            return type != WorldType.FLAT && type != WorldType.DEFAULT && !hasSky;
        }
        else
        {
            return false;
        }
    }

    public static int getMaxAnvilCost()
    {
        return ConfigHandler.anvilMaxLevel;
    }

    public static int getItemEnchantability(ItemStack stack, int base)
    {
        ItemEnchantabilityEvent event = new ItemEnchantabilityEvent(stack, base);
        MinecraftForge.EVENT_BUS.post(event);
        return event.enchantability;
    }

    public static EnumRarity getItemRarity(ItemStack stack)
    {
        ItemRarityEvent event = new ItemRarityEvent(stack, stack.getItem().getRarity(stack));
        MinecraftForge.EVENT_BUS.post(event);
        return event.rarity;
    }

    public static void onArrowUpdate(EntityArrow entity)
    {
        MinecraftForge.EVENT_BUS.post(new ArrowUpdateEvent(entity));
    }
}
