package tterrag.core.common.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.StatisticsFile;
import net.minecraftforge.event.entity.player.AchievementEvent;
import tterrag.core.common.Handlers.Handler;
import tterrag.core.common.config.ConfigHandler;
import tterrag.core.common.util.BlockCoord;
import tterrag.core.common.util.TTEntityUtils;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;

@Handler
public class FireworkHandler
{
    @SubscribeEvent
    public void onAchievement(AchievementEvent event)
    {
        StatisticsFile file = ((EntityPlayerMP)event.entityPlayer).func_147099_x();
        if (!event.entity.worldObj.isRemote && file.canUnlockAchievement(event.achievement) && !file.hasAchievementUnlocked(event.achievement) && ConfigHandler.betterAchievements)
        {
            event.entityPlayer.getEntityData().setInteger("fireworksLeft", 5);
            event.entityPlayer.getEntityData().setBoolean("fireworkDelay", false);
        }
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event)
    {
        EntityPlayer player = event.player;
        int fireworksLeft = player.getEntityData().getInteger("fireworksLeft");
        if (!event.player.worldObj.isRemote && event.phase == Phase.END && fireworksLeft > 0 && (!player.getEntityData().getBoolean("fireworkDelay") || player.worldObj.getTotalWorldTime() % 20 == 0))
        {
            TTEntityUtils.spawnFireworkAround(getBlockCoord(player), player.worldObj.provider.dimensionId);
            player.getEntityData().setInteger("fireworksLeft", fireworksLeft - 1);
            player.getEntityData().setBoolean("fireworkDelay", true);

            if (fireworksLeft == 1)
            {
                for (int i = 0; i < 5; i++)
                {
                    TTEntityUtils.spawnFireworkAround(getBlockCoord(player), player.worldObj.provider.dimensionId);
                }
            }
        }
    }
    
    private BlockCoord getBlockCoord(EntityPlayer player)
    {
        return new BlockCoord((int) Math.floor(player.posX), (int) Math.floor(player.posY), (int) Math.floor(player.posZ));
    }
}
