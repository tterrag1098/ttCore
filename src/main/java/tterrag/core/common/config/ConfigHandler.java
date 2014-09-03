package tterrag.core.common.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ConfigHandler
{
    private static Configuration config;
    
    public static boolean showOredictTooltips = false;
    
    public static void init(File file)
    {
        if (config == null) config = new Configuration(file);
        
        config.load();
        
        showOredictTooltips = config.get(Configuration.CATEGORY_GENERAL, "showOredictTooltips", showOredictTooltips, "Show oredictionary names of every item in its tooltip.").getBoolean();
        
        if (config.hasChanged()) config.save();
    }
}
