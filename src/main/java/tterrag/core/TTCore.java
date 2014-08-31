package tterrag.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import tterrag.core.common.ExtraRecipes;
import tterrag.core.common.Handlers;
import tterrag.core.common.OreDict;
import tterrag.core.common.compat.CompatabilityRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = TTCore.NAME, name = TTCore.NAME, version = TTCore.VERSION)
public class TTCore
{
    public static final String NAME = "ttCore";
    public static final String BASE_PACKAGE = "tterrag";
    public static final String VERSION = "@VERSION@";

    public static final Logger logger = LogManager.getLogger(NAME);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        Handlers.register();
        CompatabilityRegistry.instance().handle(event);
        OreDict.registerVanilla();
        ExtraRecipes.register();
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        CompatabilityRegistry.instance().handle(event);
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        CompatabilityRegistry.instance().handle(event);
    }
}
