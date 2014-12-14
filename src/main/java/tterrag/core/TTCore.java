package tterrag.core;

import net.minecraft.command.CommandHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.ClientCommandHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import tterrag.core.common.CommonProxy;
import tterrag.core.common.Handlers;
import tterrag.core.common.Lang;
import tterrag.core.common.OreDict;
import tterrag.core.common.command.CommandReloadConfigs;
import tterrag.core.common.command.CommandScoreboardInfo;
import tterrag.core.common.compat.CompatabilityRegistry;
import tterrag.core.common.config.ConfigHandler;
import tterrag.core.common.enchant.EnchantXPBoost;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(modid = TTCore.MODID, name = TTCore.NAME, version = TTCore.VERSION, guiFactory = "tterrag.core.common.config.BaseConfigFactory")
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
    
    @SidedProxy(serverSide = "tterrag.core.common.CommonProxy", clientSide = "tterrag.core.client.ClientProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        ConfigHandler.INSTANCE.initialize(event.getSuggestedConfigurationFile());
        Handlers.findPackages();
        
        CompatabilityRegistry.INSTANCE.handle(event);
        OreDict.registerVanilla();
        
        EnchantXPBoost.INSTANCE.register();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        Handlers.register();
        CompatabilityRegistry.INSTANCE.handle(event);
        ClientCommandHandler.instance.registerCommand(CommandReloadConfigs.CLIENT);
        if (event.getSide().isServer())
        {
            ((CommandHandler) MinecraftServer.getServer().getCommandManager()).registerCommand(CommandReloadConfigs.SERVER);
        }
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        CompatabilityRegistry.INSTANCE.handle(event);
        ConfigHandler.INSTANCE.loadRightClickCrops();
    }

    @EventHandler
    public void onServerStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandScoreboardInfo());
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
