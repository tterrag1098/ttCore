package tterrag.core.common.tweaks;

import lombok.Getter;
import tterrag.core.common.config.ConfigHandler;

@Getter
public abstract class Tweak
{
    private String name, comment;

    public Tweak(String key, String comment)
    {
        this.name = key;
        this.comment = comment;
        if (ConfigHandler.INSTANCE.addBooleanFor(this))
        {
            load();
        }
    }

    public abstract void load();
}
