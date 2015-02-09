package tterrag.core.common.config;

import java.io.File;

import tterrag.core.TTCore;
import tterrag.core.common.Handlers.Handler;
import tterrag.core.common.Handlers.Handler.HandlerType;
import tterrag.core.common.config.JsonConfigReader.ModToken;
import tterrag.core.common.handlers.RightClickCropHandler;
import tterrag.core.common.handlers.RightClickCropHandler.PlantInfo;
import tterrag.core.common.tweaks.Tweak;
import tterrag.core.common.tweaks.Tweaks;

@Handler(HandlerType.FML)
public class ConfigHandler extends AbstractConfigHandler implements ITweakConfigHandler
{
    // @formatter:off
    public static boolean   showOredictTooltips = false;
    public static boolean   extraDebugStuff     = true;
    public static int       disableVoidFog      = 1;
    public static int       anvilMaxLevel       = 40;
    public static boolean   betterAchievements  = true;
    public static boolean   allowCropRC         = true;
    public static int       textureErrorRemover = 1;
    
    public static int       enchantIDXPBoost    = 43;
    public static boolean   allowXPBoost        = true;
    // @formatter:on
    
    public static final ConfigHandler INSTANCE = new ConfigHandler();
    public static File configFolder;

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
    }

    @Override
    protected void reloadIngameConfigs()
    {
        activateSection("general");
        showOredictTooltips = getValue("showOredictTooltips", "Show oredictionary names of every item in its tooltip.", showOredictTooltips);
        extraDebugStuff = getValue("extraDebugStuff", "Show item registry names and other things in debug mode (f3+h)", extraDebugStuff);
        disableVoidFog = getValue("disableVoidFog", "Removes all void fog.\n0 = off\n1 = DEFAULT worldtype only\n2 = all world types", disableVoidFog);
        anvilMaxLevel = getValue("anvilMaxLevel", "The max amount of XP levels an anvil recipe can use", anvilMaxLevel);
        betterAchievements = getValue("superDuperFunMode", "The way the game should have been made.", betterAchievements);
        allowCropRC = getValue("allowCropRC", "Disabling this option will prevent any crops added to the config json from being right clickable.", allowCropRC);
        textureErrorRemover = getValue("textureErrorRemover", "0 - Do nothing\n1 - Remove stacktraces, leave 1-line missing texture errors\n2 - Remove all missing texture errors completely", textureErrorRemover);

        Tweaks.loadIngameTweaks();
    }

    @Override
    protected void reloadNonIngameConfigs()
    {
        activateSection("enchants");
        enchantIDXPBoost = getValue("enchantIDXPBoost", "Enchant ID for the XP boost enchant.", enchantIDXPBoost);
        allowXPBoost = getValue("allowXPBoost", "Allow the XP Boost enchant to be registered.", allowXPBoost);

        Tweaks.loadNonIngameTweaks();
    }

    @Override
    public boolean addBooleanFor(Tweak tweak)
    {
        activateSection("tweaks");
        return getValue(tweak.getName(), tweak.getComment(), true);
    }
    
    public void loadRightClickCrops()
    {
        JsonConfigReader<PlantInfo> reader = new JsonConfigReader<PlantInfo>(new ModToken(TTCore.class, TTCore.MODID.toLowerCase() + "/config"), configFolder.getAbsolutePath()
                + "/ttCore/cropConfig.json", PlantInfo.class);
        for (PlantInfo i : reader)
        {
            i.init();
            RightClickCropHandler.INSTANCE.addCrop(i);
        }
    }
}
