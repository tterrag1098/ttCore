package tterrag.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import tterrag.core.common.Handlers;
import tterrag.core.common.Lang;
import tterrag.core.common.OreDict;
import tterrag.core.common.command.CommandReloadConfigs;
import tterrag.core.common.compat.CompatabilityRegistry;
import tterrag.core.common.config.ConfigHandler;
import tterrag.core.common.tweaks.ExtraRecipes;
import tterrag.core.common.tweaks.VanillaTweaks;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(modid = TTCore.MODID, name = TTCore.NAME, version = TTCore.VERSION)
public class TTCore implements IModTT
{
    public static final String MODID = "ttCore";
    public static final String NAME = "ttCore";
    public static final String BASE_PACKAGE = "tterrag";
    public static final String VERSION = "@VERSION@";

    public static final Logger logger = LogManager.getLogger(NAME);
    public static final Lang lang = new Lang(MODID);

    @Instance
    public static TTCore instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        ConfigHandler.init(event.getSuggestedConfigurationFile());
        
        CompatabilityRegistry.instance().handle(event);
        OreDict.registerVanilla();
        
        ExtraRecipes.INSTANCE.load();
        VanillaTweaks.INSTANCE.load();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        Handlers.register();
        CompatabilityRegistry.instance().handle(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        CompatabilityRegistry.instance().handle(event);
    }
    
    @EventHandler
    public void onServerStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandReloadConfigs());
    }

    @Override
    public String modid()
    {
        return MODID;
    }

    @Override
    public String name()
    {
        return NAME;
    }

    @Override
    public String version()
    {
        return VERSION;
    }
}
