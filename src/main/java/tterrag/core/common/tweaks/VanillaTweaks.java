package tterrag.core.common.tweaks;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerRepair;
import tterrag.core.common.Handlers.Handler;
import tterrag.core.common.Handlers.Handler.HandlerType;
import tterrag.core.common.config.ConfigHandler;

@Handler(types = HandlerType.FML)
public class VanillaTweaks implements ITweak
{
    private static ITweak INSTANCE;
    public VanillaTweaks()
    {
        if (INSTANCE != null)
        {
            throw new IllegalStateException("This class may only be instantiated once."); // for @Handler
        }
        
        INSTANCE = this;
    }
    
    public static ITweak instance()
    {
        return INSTANCE;
    }
    
    @Override
    public void load()
    {
        Blocks.bed.setStepSound(Block.soundTypeCloth); // beds are not stone
        Items.boat.setMaxStackSize(16);
    }
    
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onContainer(PlayerTickEvent event)
    {
        if (event.phase == Phase.END && !event.player.worldObj.isRemote && event.player.openContainer instanceof ContainerRepair)
        {
            ((ContainerRepair)event.player.openContainer).maximumCost = ConfigHandler.anvilMaxLevel;
        }
    }
}
