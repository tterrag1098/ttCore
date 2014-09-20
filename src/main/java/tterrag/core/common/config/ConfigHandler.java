package tterrag.core.common.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ConfigHandler
{
    private static Configuration config;
    
    public static boolean showOredictTooltips = false;
    public static boolean enableMCropsWaila = true;
    
    public static void init(File file)
    {
        if (config == null) config = new Configuration(file);
        
        config.load();
        
        showOredictTooltips = config.get(Configuration.CATEGORY_GENERAL, "showOredictTooltips", showOredictTooltips, "Show oredictionary names of every item in its tooltip.").getBoolean();
        enableMCropsWaila = config.get(Configuration.CATEGORY_GENERAL, "enableMCropsWaila", enableMCropsWaila, "Enable the Magical Crops WAILA plugin").getBoolean();
        
        if (config.hasChanged()) config.save();
    }
}
