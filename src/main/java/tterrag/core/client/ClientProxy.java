package tterrag.core.client;

import tterrag.core.common.CommonProxy;
import tterrag.core.common.util.Scheduler;

public class ClientProxy extends CommonProxy
{
    private static final Scheduler scheduler = new Scheduler();
    
    @Override
    public Scheduler getScheduler()
    {
        return scheduler;
    }
}
