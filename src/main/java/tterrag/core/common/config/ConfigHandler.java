package tterrag.core.common.config;

import tterrag.core.TTCore;
import tterrag.core.common.Handlers.Handler;
import tterrag.core.common.Handlers.Handler.HandlerType;

@Handler(HandlerType.FML)
public class ConfigHandler extends AbstractConfigHandler
{
    // @formatter:off
    public static boolean   showOredictTooltips = false;
    public static boolean   extraDebugStuff     = true;
    public static int       disableVoidFog      = 1;
    public static int       anvilMaxLevel       = 40;
    // @formatter:on

    public static final ConfigHandler INSTANCE = new ConfigHandler();

    protected ConfigHandler()
    {
        super(TTCore.MODID);
    }

    @Override
    public void init()
    {
        addSection("general", "general");
    }

    @Override
    protected void reloadIngameConfigs()
    {
        activateSection("general");
        showOredictTooltips = getValue("showOredictTooltips", "Show oredictionary names of every item in its tooltip.", showOredictTooltips);
        extraDebugStuff = getValue("extraDebugStuff", "Show item registry names and other things in debug mode (f3+h)", extraDebugStuff);
        disableVoidFog = getValue("disableVoidFog", "Removes all void fog.\n0 = off\n1 = DEFAULT worldtype only\n2 = all world types", disableVoidFog);
        anvilMaxLevel = getValue("anvilMaxLevel", "The max amount of XP levels an anvil recipe can use", anvilMaxLevel, RestartReqs.REQUIRES_MC_RESTART);
    }

    @Override
    protected void reloadNonIngameConfigs()
    {
        ;
    }
}
