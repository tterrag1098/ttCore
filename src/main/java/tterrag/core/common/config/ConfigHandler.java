package tterrag.core.common.config;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import net.minecraft.block.Block;
import tterrag.core.TTCore;
import tterrag.core.common.Handlers.Handler;
import tterrag.core.common.Handlers.Handler.HandlerType;
import tterrag.core.common.tweaks.Tweak;
import tterrag.core.common.tweaks.Tweaks;

import com.google.common.collect.ImmutableList;

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
    private static String[] supportedCrops      = {"minecraft:wheat", "minecraft:potatoes", "minecraft:carrots"};
    
    public static int       enchantIDXPBoost    = 43;
    public static boolean   allowXPBoost        = true;
    // @formatter:on

    @Getter
    private static List<Block> rightClickCrops = ImmutableList.of();
    
    public static final ConfigHandler INSTANCE = new ConfigHandler();

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
        supportedCrops = getValue("supportedCrops",
                "Crop blocks to attempt to enable right-click harvesting on. This may or may not work for mod crops, depending on their implementation.", supportedCrops);
                
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
        List<Block> baking = new ArrayList<Block>();
        for (String s : supportedCrops)
        {
            Block block = (Block) Block.blockRegistry.getObject(s);
            if (block == null)
            {
                TTCore.logger.error("Block %s not found for right clickable crops.", s);
            }
            else
            {
                baking.add(block);
            }
        }
        rightClickCrops = ImmutableList.copyOf(baking);
    }
}
