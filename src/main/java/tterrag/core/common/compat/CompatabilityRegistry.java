package tterrag.core.common.compat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import tterrag.core.TTCore;
import tterrag.core.common.util.RegisterTime;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLStateEvent;

public class CompatabilityRegistry
{
    private static class Registration
    {
        private final String[] modids;
        private final RegisterTime time;

        private Registration(RegisterTime time, String... modids)
        {
            this.modids = modids;
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
    
    public void registerCompat(RegisterTime time, Class<? extends ICompatability> clazz, String... modids)
    {
        compatMap.put(new Registration(time, modids), clazz);
    }
    
    public void handle(FMLStateEvent event) 
    {
        RegisterTime time = RegisterTime.timeFor(event);
        for (Registration r : compatMap.keySet())
        {
            if (r.time == time && allModsLoaded(r.modids))
            {
                doLoad(compatMap.get(r));
            }
        }
    }
    
    private boolean allModsLoaded(String[] modids)
    {
        for (String s : modids)
        {
            if (!Loader.isModLoaded(s))
            {
                return false;
            }
        }
        return true;
    }
    
    public void forceLoad(Class<? extends ICompatability> clazz)
    {
        Iterator<Registration> iter = compatMap.keySet().iterator();
        while(iter.hasNext())
        {
            Registration r = iter.next();
            Class<?> c = compatMap.get(r);
            if (c == clazz) 
            {
                doLoad(c);
                compatMap.remove(r);
            }
        }
    }
    
    private void doLoad(Class<?> clazz)
    {
        try
        {
            TTCore.logger.info("[Compat] Loading compatability class " + clazz.getSimpleName());
            clazz.getDeclaredMethod(ICompatability.METHOD_NAME).invoke(null);
        }
        catch (Exception e)
        {
            TTCore.logger.error("[Compat] ICompatability class {} did not contain static method {}!", clazz.getName(), ICompatability.METHOD_NAME);
            e.printStackTrace();
        }
    }
}
