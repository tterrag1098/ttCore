package tterrag.core.common;

import tterrag.core.common.util.Scheduler;

public class CommonProxy
{
    private static final Scheduler scheduler = new Scheduler();
    
    public Scheduler getScheduler()
    {
        return scheduler;
    }
}
