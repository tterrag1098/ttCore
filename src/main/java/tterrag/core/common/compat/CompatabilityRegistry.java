package tterrag.core.common.compat;

import java.util.HashMap;
import java.util.Map;

import tterrag.core.TTCore;
import tterrag.core.common.util.RegisterTime;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLStateEvent;

public class CompatabilityRegistry
{
    private static class Registration
    {
        private final String modid;
        private final RegisterTime time;

        private Registration(String modid, RegisterTime time)
        {
            this.modid = modid;
            this.time = time;
        }
    }
    
    private Map<Registration, Class<? extends ICompatability>> compatMap;
    private static final CompatabilityRegistry INSTANCE = new CompatabilityRegistry();
    
    private CompatabilityRegistry()
    {
        compatMap = new HashMap<Registration, Class<? extends ICompatability>>();
    }
    
    public static CompatabilityRegistry instance() { return INSTANCE; }
    
    public void registerCompat(String modid, RegisterTime time, Class<? extends ICompatability> clazz)
    {
        compatMap.put(new Registration(modid, time), clazz);
    }
    
    public void handle(FMLStateEvent event) 
    {
        RegisterTime time = RegisterTime.timeFor(event);
        for (Registration r : compatMap.keySet())
        {
            if (r.time == time && Loader.isModLoaded(r.modid))
            {
                try
                {
                    Class<? extends ICompatability> clazz = compatMap.get(r);
                    TTCore.logger.info("[Compat] Loading compatability class " + compatMap.get(r).getSimpleName());
                    clazz.getDeclaredMethod(ICompatability.METHOD_NAME).invoke(null);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
