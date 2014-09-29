package tterrag.core.common.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import tterrag.core.TTCore;
import tterrag.core.common.Handlers.Handler;
import tterrag.core.common.Handlers.Handler.HandlerType;
import tterrag.core.common.event.ConfigFileChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Handler(types = HandlerType.FML)
public class ConfigHandler
{
    private static Configuration config;
    
    public static boolean showOredictTooltips = false;
    public static boolean enableMCropsWaila = true;
    public static boolean disableVoidFog = true;
    
    public static void init(File file)
    {
        if (config == null) config = new Configuration(file);
        loadConfig();
        doConfig();
    }
    
    private static void loadConfig()
    {
        config.load();
    }
    
    private static void doConfig()
    {   
        showOredictTooltips = config.get(Configuration.CATEGORY_GENERAL, "showOredictTooltips", showOredictTooltips, "Show oredictionary names of every item in its tooltip.").getBoolean();
        enableMCropsWaila = config.get(Configuration.CATEGORY_GENERAL, "enableMCropsWaila", enableMCropsWaila, "Enable the Magical Crops WAILA plugin").getBoolean();
        disableVoidFog = config.get(Configuration.CATEGORY_GENERAL, "disableVoidFog", disableVoidFog, "Removes all void fog from DEFAULT worldtype worlds").getBoolean();

        if (config.hasChanged()) config.save();
    }
    
    @SubscribeEvent
    public void onConfigFileChanged(ConfigFileChangedEvent event)
    {
        if (event.modID.equals(TTCore.instance.modid()))
        {
            loadConfig();
            doConfig();
            event.setSuccessful();
        }
    }
}
