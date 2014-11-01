package tterrag.core.common.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import tterrag.core.TTCore;
import tterrag.core.common.Handlers.Handler;
import tterrag.core.common.Handlers.Handler.HandlerType;
import tterrag.core.common.event.ConfigFileChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Handler(HandlerType.FML)
public class ConfigHandler
{
    private static Configuration config;
    
    public static boolean showOredictTooltips = false;
    public static boolean extraDebugStuff = true;

    public static int disableVoidFog = 1;

    public static int anvilMaxLevel = 40;
    
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
        extraDebugStuff = config.get(Configuration.CATEGORY_GENERAL, "extraDebugStuff", extraDebugStuff, "Show item registry names and other things in debug mode (f3+h)").getBoolean();
        disableVoidFog = config.get(Configuration.CATEGORY_GENERAL, "disableVoidFog", disableVoidFog, "Removes all void fog.\n0 = off\n1 = DEFAULT worldtype only\n2 = all world types").getInt();
        anvilMaxLevel = config.getInt("anvilMaxLevel", Configuration.CATEGORY_GENERAL, anvilMaxLevel, 1, Integer.MAX_VALUE, "The max amount of XP levels an anvil recipe can use");
        
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
