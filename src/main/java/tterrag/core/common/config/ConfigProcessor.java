package tterrag.core.common.config;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import tterrag.core.common.event.ConfigFileChangedEvent;
import tterrag.core.common.network.TTPacketHandler;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class ConfigProcessor
{
    public interface IReloadCallback
    {
        void callback(ConfigProcessor inst);
    }

    static final Map<String, ConfigProcessor> processorMap = Maps.newHashMap();
    String modid;

    private Class<?> configs;
    private Configuration configFile;
    private IReloadCallback callback;

    Map<String, Object> configValues = Maps.newHashMap();

    private Set<String> sections = Sets.newHashSet();

    /**
     * This constructor omits the callback arg.
     * 
     * @see #ConfigProcessor(Class, File, String, IReloadCallback)
     */
    public ConfigProcessor(Class<?> configs, File configFile, String modid)
    {
        this(configs, configFile, modid, null);
    }

    /**
     * Constructs a new ConfigProcessor to read and set {@link Config} values.
     * 
     * @param configs
     *            The class which contains your {@link Config} annotations
     * @param configFile
     *            The file to use as the configuration file
     * @param modid
     *            The modid of the owner mod
     * @param callback
     *            an {@link IReloadCallback} object which will be called
     *            whenever config values are edited.
     */
    public ConfigProcessor(Class<?> configs, File configFile, String modid, IReloadCallback callback)
    {
        this.configs = configs;
        this.configFile = new Configuration(configFile);
        this.modid = modid;
        this.callback = callback;
        processorMap.put(modid, this);
        FMLCommonHandler.instance().bus().register(this);
    }

    public void process(boolean load)
    {
        if (load)
        {
            configFile.load();
        }

        try
        {
            boolean fieldsChanged = false;
            for (Field f : configs.getDeclaredFields())
            {
                fieldsChanged |= processField(f);
            }
            if (fieldsChanged && callback != null)
            {
                callback.callback(this);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        if (configFile.hasChanged())
        {
            configFile.save();
        }
    }

    // returns true if the config value changed
    private boolean processField(Field f) throws Exception
    {
        Config cfg = f.getAnnotation(Config.class);
        if (cfg == null)
        {
            return false;
        }
        Object value = f.get(null);
        Object newValue = getConfigValue(cfg, f, value);

        configValues.put(f.getName(), newValue);
        f.set(null, newValue);

        sections.add(cfg.section());

        return !value.equals(newValue);
    }

    private Object getConfigValue(Config cfg, Field f, Object defVal)
    {
        Property prop = null;
        Object ret = null;
        if (defVal instanceof Boolean)
        {
            prop = configFile.get(cfg.section(), f.getName(), (Boolean) defVal);
            ret = prop.getBoolean();
        }
        else if (defVal instanceof Integer)
        {
            prop = configFile.get(cfg.section(), f.getName(), (Integer) defVal);
            ret = prop.getInt();
        }
        else if (defVal instanceof Double)
        {
            prop = configFile.get(cfg.section(), f.getName(), (Double) defVal);
            ret = prop.getDouble();
        }
        else if (defVal instanceof String)
        {
            prop = configFile.get(cfg.section(), f.getName(), (String) defVal);
            ret = prop.getString();
        }
        else if (defVal instanceof String[])
        {
            prop = configFile.get(cfg.section(), f.getName(), (String[]) defVal);
            ret = prop.getStringList();
        }
        if (cfg.min() > Integer.MIN_VALUE)
        {
            prop.setMinValue((defVal instanceof Integer) ? (int) cfg.min() : cfg.min());
        }
        if (cfg.max() < Integer.MAX_VALUE)
        {
            prop.setMinValue((defVal instanceof Integer) ? (int) cfg.max() : cfg.max());
        }
        return ret;
    }

    public ImmutableSet<String> sections()
    {
        return ImmutableSet.copyOf(sections);
    }

    public ConfigCategory getCategory(String category)
    {
        return configFile.getCategory(category);
    }

    public void syncTo(Map<String, Object> values)
    {
        this.configValues = values;
        for (String s : configValues.keySet())
        {
            try
            {
                Field f = configs.getDeclaredField(s);
                Config annot = f.getAnnotation(Config.class);
                if (annot != null && !annot.noSync())
                {
                    Object newVal = configValues.get(s);
                    boolean changed = false;
                    if (!f.get(null).equals(newVal))
                    {
                        f.set(null, newVal);
                        changed = true;
                    }
                    if (changed && callback != null)
                    {
                        callback.callback(this);
                    }
                }
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    /* Event Handling */

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        TTPacketHandler.INSTANCE.sendTo(new PacketConfigSync(this), (EntityPlayerMP) event.player);
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.modID.equals(modid))
        {
            process(false);
        }
    }

    @SubscribeEvent
    public void onConfigFileChanged(ConfigFileChangedEvent event)
    {
        if (event.modID.equals(modid))
        {
            process(true);
        }
    }
}
