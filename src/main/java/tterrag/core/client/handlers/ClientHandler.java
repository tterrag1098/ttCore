package tterrag.core.client.handlers;

import lombok.Getter;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import tterrag.core.common.Handlers.Handler;
import tterrag.core.common.Handlers.Handler.HandlerType;

@Handler(HandlerType.FML)
public class ClientHandler
{
    @Getter
    private static int ticksElapsed;

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event)
    {
        if (event.phase == Phase.END)
        {
            ticksElapsed++;
        }
    }
}
