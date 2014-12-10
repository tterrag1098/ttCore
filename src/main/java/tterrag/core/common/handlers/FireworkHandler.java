package tterrag.core.common.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.StatisticsFile;
import net.minecraft.util.BlockPos;
import net.minecraftforge.event.entity.player.AchievementEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import tterrag.core.common.Handlers.Handler;
import tterrag.core.common.config.ConfigHandler;
import tterrag.core.common.util.TTEntityUtils;

@Handler
public class FireworkHandler
{
    @SubscribeEvent
    public void onAchievement(AchievementEvent event)
    {
        StatisticsFile file = ((EntityPlayerMP) event.entityPlayer).getStatFile();
        if (!event.entity.worldObj.isRemote && file.canUnlockAchievement(event.achievement) && !file.hasAchievementUnlocked(event.achievement)
                && ConfigHandler.betterAchievements)
        {
            event.entityPlayer.getEntityData().setInteger("fireworksLeft", 9);
            event.entityPlayer.getEntityData().setBoolean("fireworkDelay", false);
        }
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event)
    {
        EntityPlayer player = event.player;
        int fireworksLeft = player.getEntityData().getInteger("fireworksLeft");
        if (!event.player.worldObj.isRemote && event.phase == Phase.END && fireworksLeft > 0
                && (!player.getEntityData().getBoolean("fireworkDelay") || player.worldObj.getTotalWorldTime() % 20 == 0))
        {
            TTEntityUtils.spawnFireworkAround(new BlockPos(player), player.worldObj.provider.getDimensionId());
            player.getEntityData().setInteger("fireworksLeft", fireworksLeft - 1);

            if (fireworksLeft > 5)
            {
                player.getEntityData().setBoolean("fireworkDelay", true);
            }
            else
            {
                player.getEntityData().setBoolean("fireworkDelay", false);
            }
        }
    }
}
