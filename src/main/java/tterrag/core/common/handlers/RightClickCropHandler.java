package tterrag.core.common.handlers;

import net.minecraft.block.Block;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import tterrag.core.common.Handlers.Handler;
import tterrag.core.common.Handlers.Handler.HandlerType;
import tterrag.core.common.config.ConfigHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Handler(HandlerType.FORGE)
public class RightClickCropHandler
{
    private boolean doRemoveSeed = false;
    
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
                doRemoveSeed = true;
                block.dropBlockAsItem(event.world, x, y, z, meta, 0);
                doRemoveSeed = false;
                event.world.setBlockMetadataWithNotify(x, y, z, 0, 3);
                event.setCanceled(true);
            }
        }
    }
    
    @SubscribeEvent
    public void onHarvestDrop(HarvestDropsEvent event)
    {
        if (doRemoveSeed)
        {
            for (int i = 0; i < event.drops.size(); i++)
            {
                ItemStack stack = event.drops.get(i);
                // TODO add some kind of json config for different values, mainly what the seed is
                if (stack.getItem() instanceof ItemSeeds)
                {
                    event.drops.remove(i);
                    break;
                }
            }
        }
    }
}
