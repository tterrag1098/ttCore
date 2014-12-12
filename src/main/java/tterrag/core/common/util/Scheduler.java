package tterrag.core.common.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import tterrag.core.TTCore;
import tterrag.core.common.Handlers.Handler;
import tterrag.core.common.Handlers.Handler.HandlerType;
import tterrag.core.common.Handlers.Handler.Inst;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Handler(value = HandlerType.FML, getInstFrom = Inst.METHOD)
public class Scheduler
{    
    @EqualsAndHashCode
    @ToString
    @AllArgsConstructor
    static final class Task
    {
        int delay;
        Runnable toRun;
        Side side;

        public boolean run()
        {
            if (delay <= 0)
            {
                toRun.run();
                return true;
            }
            delay--;
            return false;
        }
    }

    private final List<Task> tasks = new ArrayList<Task>();
    
    public void schedule(int delay, Runnable task)
    {
        schedule(delay, task, Side.SERVER);
    }
    
    public void schedule(int delay, Runnable task, Side side)
    {
        tasks.add(new Task(delay, task, side));
    }
    
    public static Scheduler instance()
    {
        return TTCore.proxy.getScheduler();
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent event)
    {
        runTasks(Side.SERVER);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onClientTick(ClientTickEvent event)
    {
        runTasks(Side.CLIENT);
    }

    private void runTasks(Side side)
    {
        Iterator<Task> iter = tasks.iterator();
        while (iter.hasNext())
        {
            Task next = iter.next();
            if (next.side == side && next.run())
            {
                iter.remove();
            }
        }
    }
}
