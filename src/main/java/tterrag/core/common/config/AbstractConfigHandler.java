package tterrag.core.common.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import tterrag.core.TTCore;
import tterrag.core.api.common.config.IConfigHandler;
import tterrag.core.common.event.ConfigFileChangedEvent;

import com.google.common.collect.ImmutableList;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public abstract class AbstractConfigHandler implements IConfigHandler
{
    public class Section
    {
        public final String name;
        public final String lang;

        public Section(String name, String lang)
        {
            this.name = name;
            this.lang = "section." + lang;
        }

        private Section register()
        {
            sections.add(this);
            return this;
        }

        public String lc()
        {
            return name.toLowerCase();
        }
    }

    public enum RestartReqs
    {
        /**
         * No restart needed for this config to be applied. Default value.
         */
        NONE,

        /**
         * This config requires the world to be restarted to take effect.
         */
        REQUIRES_WORLD_RESTART,

        /**
         * This config requires the game to be restarted to take effect. {@code REQUIRES_WORLD_RESTART} is implied when using this.
         */
        REQUIRES_MC_RESTART;

        public Property apply(Property prop)
        {
            if (this == REQUIRES_MC_RESTART)
            {
                prop.setRequiresMcRestart(true);
            }
            else if (this == REQUIRES_WORLD_RESTART)
            {
                prop.setRequiresWorldRestart(true);
            }
            return prop;
        }
    }

    private String modid;
    private Configuration config;
    private List<Section> sections = new ArrayList<Section>();
    private Section activeSection = null;

    protected AbstractConfigHandler(String modid)
    {
        this.modid = modid;
        FMLCommonHandler.instance().bus().register(this);
    }

    public final void initialize(File cfg)
    {
        config = new Configuration(cfg);
        init();
        reloadAllConfigs();
        saveConfigFile();
    }

    protected void loadConfigFile()
    {
        config.load();
    }

    protected void saveConfigFile()
    {
        if (config.hasChanged())
        {
            config.save();
        }
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.modID.equals(modid))
        {
            TTCore.logger.info("Reloading all configs for modid: " + modid);
            reloadAllConfigs();
            saveConfigFile();
        }
    }

    @SubscribeEvent
    public void onConfigFileChanged(ConfigFileChangedEvent event)
    {
        if (event.modID.equals(modid))
        {
            TTCore.logger.info("Reloading ingame configs for modid: " + modid);
            loadConfigFile();
            reloadIngameConfigs();
            event.setSuccessful();
            saveConfigFile();
        }
    }

    // convenience for reloading all configs
    private void reloadAllConfigs()
    {
        reloadNonIngameConfigs();
        reloadIngameConfigs();
    }

    /**
     * Called after config is loaded, but before properties are processed.
     * <p>
     * Use this method to add your sections and do other setup.
     */
    protected abstract void init();

    /**
     * Refresh all config values that can only be loaded when NOT in-game.
     * <p>
     * {@code reloadIngameConfigs()} will be called after this, do not duplicate calls in this method and that one.
     */
    protected abstract void reloadNonIngameConfigs();

    /**
     * Refresh all config values that can only be loaded when in-game.
     * <p>
     * This is separated from {@code reloadNonIngameConfigs()} because some values may not be able to be modified at runtime.
     */
    protected abstract void reloadIngameConfigs();

    protected Section addSection(String sectionName, String langKey)
    {
        return addSection(sectionName, langKey, null);
    }

    protected Section addSection(String sectionName, String langKey, String comment)
    {
        Section section = new Section(sectionName, langKey);

        if (activeSection == null && sections.isEmpty())
        {
            activeSection = section;
        }

        if (comment != null)
        {
            config.addCustomCategoryComment(sectionName, comment);
        }

        return section.register();
    }

    private void checkInitialized()
    {
        if (activeSection == null)
        {
            throw new IllegalStateException("No section is active!");
        }
    }

    protected void activateSection(@NonNull String sectionName)
    {
        activateSection(getSectionByName(sectionName));
    }

    protected void activateSection(Section section)
    {
        activeSection = section;
    }

    protected Section getSectionByName(@NonNull String sectionName)
    {
        for (Section s : sections)
        {
            if (s.name.equalsIgnoreCase(sectionName))
            {
                return s;
            }
        }
        return null;
    }

    /**
     * Gets a value from this config handler
     * 
     * @param key Name of the key for this property
     * @param defaultVal Default value so a new property can be created
     * @return The value of the property
     * 
     * @throws IllegalArgumentException If defaultVal is not a valid property type
     * @throws IllegalStateException If there is no active section
     */
    protected <T> T getValue(String key, T defaultVal)
    {
        return getValue(key, defaultVal, RestartReqs.NONE);
    }

    /**
     * Gets a value from this config handler
     * 
     * @param key Name of the key for this property
     * @param defaultVal Default value so a new property can be created
     * @param req Restart requirement of the property to be created
     * @return The value of the property
     * 
     * @throws IllegalArgumentException If defaultVal is not a valid property type
     * @throws IllegalStateException If there is no active section
     */
    protected <T> T getValue(String key, T defaultVal, RestartReqs req)
    {
        return getValue(key, null, defaultVal, req);
    }

    /**
     * Gets a value from this config handler
     * 
     * @param key Name of the key for this property
     * @param comment The comment to put on this property
     * @param defaultVal Default value so a new property can be created
     * @return The value of the property
     * 
     * @throws IllegalArgumentException if defaultVal is not a valid property type
     * @throws IllegalStateException if there is no active section
     */
    protected <T> T getValue(String key, String comment, T defaultVal)
    {
        return getValue(key, comment, defaultVal, RestartReqs.NONE);
    }

    /**
     * Gets a value from this config handler
     * 
     * @param key Name of the key for this property
     * @param comment The comment to put on this property
     * @param defaultVal Default value so a new property can be created
     * @param req Restart requirement of the property to be created
     * @return The value of the property
     * 
     * @throws IllegalArgumentException if defaultVal is not a valid property type
     * @throws IllegalStateException if there is no active section
     */
    protected <T> T getValue(String key, String comment, T defaultVal, RestartReqs req)
    {
        Property prop = getProperty(key, defaultVal, req);
        prop.comment = comment;

        return getValue(prop, defaultVal);
    }

    /**
     * Gets a value from a property
     * 
     * @param prop Property to get value from
     * @param defaultVal Default value so a new property can be created
     * 
     * @throws IllegalArgumentException if defaultVal is not a valid property type
     * @throws IllegalStateException if there is no active section
     */
    @SuppressWarnings("unchecked")
    // we check type of defaultVal but compiler still complains about a cast to T
    protected <T> T getValue(Property prop, T defaultVal)
    {
        checkInitialized();

        // @formatter:off
        if (defaultVal instanceof Integer) { return (T) Integer.valueOf(prop.getInt()); }
        if (defaultVal instanceof Boolean) { return (T) Boolean.valueOf(prop.getBoolean()); }
        if (defaultVal instanceof int[])   { return (T) prop.getIntList(); }
        if (defaultVal instanceof String)  { return (T) prop.getString(); }
        if (defaultVal instanceof String[]){ return (T) prop.getStringList(); }
        //@formatter:on

        if (defaultVal instanceof Float || defaultVal instanceof Double) // there is no float type...yeah idk either
        {
            return (T) Double.valueOf(prop.getDouble());
        }

        throw new IllegalArgumentException("default value is not a config value type.");
    }

    /**
     * Gets a property from this config handler
     * 
     * @param key name of the key for this property
     * @param defaultVal default value so a new property can be created
     * @return The property in the config
     * 
     * @throws IllegalArgumentException if defaultVal is not a valid property type
     * @throws IllegalStateException if there is no active section
     */
    protected <T> Property getProperty(String key, T defaultVal)
    {
        return getProperty(key, defaultVal, RestartReqs.NONE);
    }

    /**
     * Gets a property from this config handler
     * 
     * @param key name of the key for this property
     * @param defaultVal default value so a new property can be created
     * @return The property in the config
     * 
     * @throws IllegalArgumentException if defaultVal is not a valid property type
     * @throws IllegalStateException if there is no active section
     */
    protected <T> Property getProperty(String key, T defaultVal, RestartReqs req)
    {
        checkInitialized();
        Section section = activeSection;
        Property prop = null;

        // @formatter:off
        // same logic as above method, mostly
        if (defaultVal instanceof Integer) { prop = config.get(section.name, key, (Integer)  defaultVal); }
        if (defaultVal instanceof Boolean) { prop = config.get(section.name, key, (Boolean)  defaultVal); }
        if (defaultVal instanceof int[])   { prop = config.get(section.name, key, (int[])    defaultVal); }
        if (defaultVal instanceof String)  { prop = config.get(section.name, key, (String )  defaultVal); }
        if (defaultVal instanceof String[]){ prop = config.get(section.name, key, (String[]) defaultVal); }
        // @formatter:on

        if (defaultVal instanceof Float || defaultVal instanceof Double)
        {
            double val = defaultVal instanceof Float ? ((Float) defaultVal).doubleValue() : ((Double) defaultVal).doubleValue();
            prop = config.get(section.name, key, val);
        }

        if (prop != null)
        {
            return req.apply(prop);
        }

        throw new IllegalArgumentException("default value is not a config value type.");
    }

    /* IConfigHandler impl */

    // no need to override these, they are merely utilities, and reference private fields anyways

    @Override
    public final List<Section> getSections()
    {
        return ImmutableList.copyOf(sections);
    }

    @Override
    public final ConfigCategory getCategory(String name)
    {
        return config.getCategory(name);
    }

    @Override
    public final String getModID()
    {
        return modid;
    }
}
