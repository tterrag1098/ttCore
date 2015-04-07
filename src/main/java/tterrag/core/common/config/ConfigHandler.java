package tterrag.core.common.config;

import java.io.File;

import tterrag.core.TTCore;
import tterrag.core.common.Handlers.Handler;
import tterrag.core.common.Handlers.Handler.HandlerType;
import tterrag.core.common.Handlers.Handler.Inst;
import tterrag.core.common.config.ConfigProcessor.IReloadCallback;
import tterrag.core.common.config.JsonConfigReader.ModToken;
import tterrag.core.common.handlers.RightClickCropHandler;
import tterrag.core.common.handlers.RightClickCropHandler.PlantInfo;
import tterrag.core.common.transform.TTCorePlugin;
import tterrag.core.common.tweaks.Tweak;
import tterrag.core.common.tweaks.Tweaks;

@Handler(value = HandlerType.FML, getInstFrom = Inst.METHOD)
public class ConfigHandler extends AbstractConfigHandler implements ITweakConfigHandler, IReloadCallback
{
    private static final String sectionGeneral = "general";

    // @formatter:off
    @Config(section = sectionGeneral, comment = "Show oredictionary names of every item in its tooltip.", noSync = true)
    public static boolean showOredictTooltips = false;

    @Config(section = sectionGeneral, comment = "Show item registry names and other things in debug mode (f3+h)", noSync = true)
    public static boolean extraDebugStuff = true;

    @Config(section = sectionGeneral, comment = "Removes all void fog.\n0 = off\n1 = DEFAULT worldtype only\n2 = all world types", noSync = true)
    public static int disableVoidFog = 1;

    @Config(section = sectionGeneral, comment = "The max amount of XP levels an anvil recipe can use.")
    public static int anvilMaxLevel = 40;

    @Config(section = sectionGeneral, comment = "The way the game should have been made (Yes this is the fireworks thing).")
    public static boolean betterAchievements = true;

    @Config(section = sectionGeneral, comment = "Disabling this option will prevent any crops added to the config json from being right clickable.")
    public static boolean allowCropRC = true;

    @Config(section = sectionGeneral, comment = "0 - Do nothing\n1 - Remove stacktraces, leave 1-line missing texture errors\n2 - Remove all missing texture errors completely. This option is not supported outside dev environments.", noSync = true)
    public static int textureErrorRemover = 0;

    public static int enchantIDXPBoost = 43;
    public static boolean allowXPBoost = true;
    
    public static int enchantIDAutoSmelt = 44;
    public static boolean allowAutoSmelt = true;
    public static boolean allowAutoSmeltWithFortune = true;
    // @formatter:on

    private static ConfigHandler INSTANCE;
    
    public static File configFolder, ttConfigFolder;
    public static File configFile;
    public static ConfigProcessor processor;
    
    public static ConfigHandler instance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new ConfigHandler();
        }
        return INSTANCE;
    }

    protected ConfigHandler()
    {
        super(TTCore.MODID);
    }

    @Override
    public void init()
    {
        addSection("general");
        addSection("enchants");
        addSection("tweaks");
        processor = new ConfigProcessor(getClass(), this, this);
        processor.process(true);
    }

    @Override
    protected void reloadIngameConfigs()
    {
    }

    @Override
    protected void reloadNonIngameConfigs()
    {
        activateSection("enchants");
        enchantIDXPBoost = getValue("enchantIDXPBoost", "Enchant ID for the XP boost enchant.", enchantIDXPBoost);
        allowXPBoost = getValue("allowXPBoost", "Allow the XP Boost enchant to be registered.", allowXPBoost);
        enchantIDAutoSmelt = getValue("enchantIDAutoSmelt", "Enchant ID for the Auto Smelt enchant.", enchantIDAutoSmelt);
        allowAutoSmelt = getValue("allowAutoSmelt", "Allow the Auto Smelt enchant to be registered.", allowAutoSmelt);
        allowAutoSmeltWithFortune = getValue("allowAutoSmeltWithFortune", "Allow the Auto Smelt enchant to work with Fortune.", allowAutoSmeltWithFortune);
        
        Tweaks.loadNonIngameTweaks();
    }

    @Override
    public void callback(ConfigProcessor inst)
    { 
        if (!TTCorePlugin.runtimeDeobfEnabled)
        {
            textureErrorRemover = Math.min(textureErrorRemover, 1);
        }
        Tweaks.loadIngameTweaks();
    }

    @Override
    public boolean addBooleanFor(Tweak tweak)
    {
        activateSection("tweaks");
        return getValue(tweak.getName(), tweak.getComment(), true);
    }

    public void loadRightClickCrops()
    {
        JsonConfigReader<PlantInfo> reader = new JsonConfigReader<PlantInfo>(new ModToken(TTCore.class, TTCore.MODID.toLowerCase() + "/config"),
                ttConfigFolder.getAbsolutePath() + "/cropConfig.json", PlantInfo.class);
        for (PlantInfo i : reader)
        {
            i.init();
            RightClickCropHandler.INSTANCE.addCrop(i);
        }
    }
}
