package tterrag.core.client.config;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import tterrag.core.TTCore;
import tterrag.core.api.common.config.IConfigHandler;
import tterrag.core.common.config.AbstractConfigHandler.Section;
import tterrag.core.common.config.ConfigHandler;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;

public class BaseConfigGui extends GuiConfig
{
    @SuppressWarnings("rawtypes")
    public BaseConfigGui(GuiScreen parentScreen)
    {
        // dummy super so we can call instance methods
        super(parentScreen, new ArrayList<IConfigElement>(), null, false, false, null);
        
        try
        {
            // pffft final, what a wimpy modifier
            Field modID = GuiConfig.class.getDeclaredField("modID");
            Field configElements = GuiConfig.class.getDeclaredField("configElements");

            modID.setAccessible(true);
            configElements.setAccessible(true);

            modID.set(this, getConfigHandler().getModID());
            configElements.set(this, getConfigElements());
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        this.title = getTitle();
    }

    /**
     * The <b>localized</b> title of this config screen
     */
    protected String getTitle()
    {
        return TTCore.lang.localize("config.title");
    }

    /**
     * The {@link IConfigHandler} to refer to when generating this config screen
     */
    protected IConfigHandler getConfigHandler()
    {
        return ConfigHandler.INSTANCE;
    }

    /**
     * The lang prefix to use before your section lang keys. Default is "config.".
     */
    protected String getLangPrefix()
    {
        return "config.";
    }

    @SuppressWarnings("rawtypes")
    private List<IConfigElement> getConfigElements()
    {
        List<IConfigElement> list = new ArrayList<IConfigElement>();
        String prefix = getLangPrefix();
        IConfigHandler config = getConfigHandler();

        prefix = prefix.endsWith(".") ? prefix : prefix + ".";

        for (Section s : config.getSections())
        {
            list.add(new ConfigElement<ConfigCategory>(config.getCategory(s.lc()).setLanguageKey(prefix + s.lang)));
        }

        return list;
    }
}
