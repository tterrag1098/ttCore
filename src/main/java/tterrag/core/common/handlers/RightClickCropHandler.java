package tterrag.core.common.handlers;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import tterrag.core.common.Handlers.Handler;
import tterrag.core.common.Handlers.Handler.HandlerType;
import tterrag.core.common.config.ConfigHandler;
import tterrag.core.common.util.TTItemUtils;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Handler(HandlerType.FORGE)
public class RightClickCropHandler
{
    @SubscribeEvent
    public void handleCropRightClick(PlayerInteractEvent event)
    {
        int x = event.x, y = event.y, z = event.z;
        Block block = event.world.getBlock(x, y, z);
        int meta = event.world.getBlockMetadata(x, y, z);
        if (ConfigHandler.allowCropRC && event.action == Action.RIGHT_CLICK_BLOCK && ConfigHandler.getRightClickCrops().contains(block) && meta >= 7)
        {
            if (event.world.isRemote)
            {
                event.entityPlayer.swingItem();
            }
            else
            {
                ItemStack held = event.entityPlayer.getCurrentEquippedItem();
                int fortune = held == null ? 0 : EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, held);
                List<ItemStack> drops = block.getDrops(event.world, x, y, z, meta, fortune);
                boolean seedRemoved = false;
                for (ItemStack stack : drops)
                {
                    // TODO add some kind of json config for different values, mainly what the seed is
                    if (!(stack.getItem() instanceof ItemSeeds && !seedRemoved))
                    {
                        TTItemUtils.spawnItemInWorldWithRandomMotion(event.world, stack, x, y, z);
                    }
                    else
                    {
                        seedRemoved = true;
                    }
                }
                event.world.setBlockMetadataWithNotify(x, y, z, 0, 3);
                event.setCanceled(true);
            }
        }
    }
}
