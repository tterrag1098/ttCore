package tterrag.core.common.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import tterrag.core.TTCore;
import tterrag.core.common.Handlers.Handler;
import tterrag.core.common.Handlers.Handler.HandlerType;
import tterrag.core.common.Handlers.Handler.Inst;
import tterrag.core.common.config.ConfigProcessor.IReloadCallback;
import tterrag.core.common.config.JsonConfigReader.ModToken;
import tterrag.core.common.config.annot.Comment;
import tterrag.core.common.config.annot.Config;
import tterrag.core.common.config.annot.NoSync;
import tterrag.core.common.config.annot.Range;
import tterrag.core.common.config.annot.RestartReq;
import tterrag.core.common.handlers.RightClickCropHandler;
import tterrag.core.common.handlers.RightClickCropHandler.PlantInfo;
import tterrag.core.common.transform.TTCorePlugin;
import tterrag.core.common.tweaks.Tweak;
import tterrag.core.common.tweaks.Tweaks;

@Handler(value = HandlerType.FML, getInstFrom = Inst.METHOD)
public class ConfigHandler extends AbstractConfigHandler implements ITweakConfigHandler, IReloadCallback
{
    private static final String sectionGeneral = Configuration.CATEGORY_GENERAL;
    private static final String sectionEnchants = "enchants";

    @Config
    @Comment("Show oredictionary names of every item in its tooltip.\n0 - Off\n1 - Always on\n2 - Only with shift\n3 - Only in debug mode")
    @Range(min = 0, max = 3)
    @NoSync
    public static int showOredictTooltips = 1;

    @Config
    @Comment("Show item registry names in tooltips.\n0 - Off\n1 - Always on\n2 - Only with shift\n3 - Only in debug mode")
    @Range(min = 0, max = 3)
    @NoSync
    public static int showRegistryNameTooltips = 3;

    @Config
    @Comment("Removes all void fog.\n0 = off\n1 = DEFAULT worldtype only\n2 = all world types")
    @NoSync
    @Range(min = 0, max = 2)
    public static int disableVoidFog = 1;

    @Config
    @Comment("The max amount of XP levels an anvil recipe can use.")
    public static int anvilMaxLevel = 40;

    @Config
    @Comment("The way the game should have been made (Yes this is the fireworks thing).")
    public static boolean betterAchievements = true;

    @Config
    @Comment("Disabling this option will prevent any crops added to the config json from being right clickable.")
    public static boolean allowCropRC = true;

    @Config
    @Comment("0 - Do nothing\n1 - Remove stacktraces, leave 1-line missing texture errors\n2 - Remove all missing texture errors completely. This option is not supported outside dev environments.")
    @NoSync
    @Range(min = 0, max = 2)
    public static int textureErrorRemover = 1;

    @Config
    @Comment("Controls the default sorting on the mod list GUI.\n\n0 - Default sort (load order)\n1 - A to Z sort\n2 - Z to A sort")
    @NoSync
    @Range(min = 0, max = 2)
    public static int defaultModSort = 1;

    @Config(sectionEnchants)
    @Comment("Enchant ID for the XP boost enchant.")
    @RestartReq(RestartReqs.REQUIRES_MC_RESTART)
    @Range(min = 0, max = 255)
    public static int enchantIDXPBoost = 43;

    @Config(sectionEnchants)
    @Comment("Allow the XP Boost enchant to be registered.")
    @RestartReq(RestartReqs.REQUIRES_MC_RESTART)
    public static boolean allowXPBoost = true;

    @Config(sectionEnchants)
    @Comment("Enchant ID for the Auto Smelt enchant.")
    @RestartReq(RestartReqs.REQUIRES_MC_RESTART)
    @Range(min = 0, max = 255)
    public static int enchantIDAutoSmelt = 44;

    @Config(sectionEnchants)
    @Comment("Allow the Auto Smelt enchant to be registered.")
    @RestartReq(RestartReqs.REQUIRES_MC_RESTART)
    public static boolean allowAutoSmelt = true;

    @Config(sectionEnchants)
    @Comment("Allow the Auto Smelt enchant to work with Fortune.")
    public static boolean allowAutoSmeltWithFortune = true;

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
        addSection(sectionGeneral);
        addSection(sectionEnchants);
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
