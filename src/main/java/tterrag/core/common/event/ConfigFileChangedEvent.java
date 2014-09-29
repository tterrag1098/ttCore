package tterrag.core.common.event;

import cpw.mods.fml.client.event.ConfigChangedEvent;

/**
 * This event is posted when the /reloadConfigs command is run, and should be used to reload your config file and parse its contents.
 * <p>
 * This differs from the existing events because you should call config.load() before re-parsing the contents, to reload the file from disk.
 * <p>
 * Note that requiresMcRestart is always false, so this might not work 100% of the time
 * <p>
 * <b>If <code>setSuccessful()</code> is not called, the event post will be considered a failure!</b>
 */
public class ConfigFileChangedEvent extends ConfigChangedEvent
{
    private boolean successful = false;
    
    public ConfigFileChangedEvent(String modID)
    {
        super(modID, "null", true, false);
    }

    public void setSuccessful()
    {
        this.successful = true;
    }
    
    public boolean wasSuccessful()
    {
        return successful;
    }
}
