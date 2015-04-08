package tterrag.core.common.config;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import tterrag.core.common.config.AbstractConfigHandler.Bound;
import tterrag.core.common.config.AbstractConfigHandler.RestartReqs;
import tterrag.core.common.config.annot.Comment;
import tterrag.core.common.config.annot.Config;
import tterrag.core.common.config.annot.NoSync;
import tterrag.core.common.config.annot.Range;
import tterrag.core.common.config.annot.RestartReq;
import tterrag.core.common.event.ConfigFileChangedEvent;
import tterrag.core.common.network.TTPacketHandler;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

/**
 * This class can be used to automatically process {@link Config} annotations on fields, and sync the data in those fields to clients. It will also
 * automatically respond to all config changed events and handle them appropriately.
 * 
 * @see #process(boolean)
 */
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
     *            an {@link IReloadCallback} object which will be called whenever config values are edited.
     */
    public ConfigProcessor(Class<?> configs, File configFile, String modid, IReloadCallback callback)
    {
        this(configs, new Configuration(configFile), modid, callback);
    }

    /**
     * Use this constructor if you are using a {@code ConfigProcessor} alongside an {@link AbstractConfigHandler}. Do not pass a handler that has not
     * been {@link AbstractConfigHandler#initialize(File) initialized}!
     * 
     * @param configs
     *            The class which contains your {@link Config} annotations (typically same class as your {@link AbstractConfigHandler handler})
     * @param handler
     *            Your {@link AbstractConfigHandler}
     */
    public ConfigProcessor(Class<?> configs, AbstractConfigHandler handler)
    {
        this(configs, handler, null);
    }

    /**
     * Use this constructor if you are using a {@code ConfigProcessor} alongside an {@link AbstractConfigHandler}. Do not pass a handler that has not
     * been {@link AbstractConfigHandler#initialize(File) initialized}!
     * 
     * @param configs
     *            The class which contains your {@link Config} annotations (typically same class as your {@link AbstractConfigHandler handler})
     * @param handler
     *            Your {@link AbstractConfigHandler}
     * @param callback
     *            an {@link IReloadCallback} object which will be called whenever config values are edited.
     */
    public ConfigProcessor(Class<?> configs, AbstractConfigHandler handler, IReloadCallback callback)
    {
        this(configs, handler.config, handler.modid, callback);
    }

    private ConfigProcessor(Class<?> configs, Configuration configFile, String modid, IReloadCallback callback)
    {
        this.configs = configs;
        this.configFile = configFile;
        this.modid = modid;
        this.callback = callback;
        processorMap.put(modid, this);
        FMLCommonHandler.instance().bus().register(this);
    }

    /**
     * Processes all the configs in this processors class, optionally loading them from file first.
     * 
     * @param load
     *            If true, the values from the file will be loaded. Otherwise, the values existing in memory will be used.
     */
    public void process(boolean load)
    {
        if (load)
        {
            configFile.load();
        }

        try
        {
            for (Field f : configs.getDeclaredFields())
            {
                processField(f);
            }
            if (callback != null)
            {
                callback.callback(this);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        configFile.save();
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
        Object newValue = getConfigValue(cfg.value(), getComment(f), f, value);

        configValues.put(f.getName(), newValue);
        f.set(null, newValue);

        sections.add(cfg.value());

        return !value.equals(newValue);
    }

    private Object getConfigValue(String section, String comment, Field f, Object defVal)
    {
        Property prop = null;
        Object ret = null;
        Bound<Double> bound = getBound(f);
        if (defVal instanceof Boolean)
        {
            prop = configFile.get(section, f.getName(), (Boolean) defVal, comment);
            ret = prop.getBoolean();
        }
        else if (defVal instanceof Integer)
        {
            prop = configFile.get(section, f.getName(), (Integer) defVal, comment);
            ret = AbstractConfigHandler.boundValue(prop, Bound.of(bound.min.intValue(), bound.max.intValue()), (Integer) defVal);
        }
        else if (defVal instanceof Double)
        {
            prop = configFile.get(section, f.getName(), (Double) defVal, comment);
            ret = AbstractConfigHandler.boundValue(prop, Bound.of(bound.min.doubleValue(), bound.max.doubleValue()), (Double) defVal);
        }
        else if (defVal instanceof String)
        {
            prop = configFile.get(section, f.getName(), (String) defVal, comment);
            ret = prop.getString();
        }
        else if (defVal instanceof String[])
        {
            prop = configFile.get(section, f.getName(), (String[]) defVal, comment);
            ret = prop.getStringList();
        }
        AbstractConfigHandler.setBounds(prop, bound);
        getRestartReq(f).apply(prop);
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
                if (annot != null && !getNoSync(f))
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

    private String getComment(Field f)
    {
        Comment c = f.getAnnotation(Comment.class);
        return c == null ? "" : c.value();
    }

    private Bound<Double> getBound(Field f)
    {
        Range r = f.getAnnotation(Range.class);
        return r == null ? Bound.MAX_BOUND : Bound.of(r.min(), r.max());
    }

    private boolean getNoSync(Field f)
    {
        return f.getAnnotation(NoSync.class) != null;
    }

    private RestartReqs getRestartReq(Field f)
    {
        RestartReq r = f.getAnnotation(RestartReq.class);
        return r == null ? RestartReqs.NONE : r.value();
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
