package tterrag.core.common.config;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.config.Configuration;

public abstract class AbstractConfigHandler
{
    public class Section
    {
        public final String name;
        public final String lang;

        public Section(String name, String lang)
        {
            this.name = name;
            this.lang = lang;
            register();
        }

        private void register()
        {
            sections.add(this);
        }

        public String lc()
        {
            return name.toLowerCase();
        }
    }

    protected Configuration config;
    
    private List<Section> sections = new ArrayList<Section>();

    public AbstractConfigHandler addSection(String sectionName, String langKey)
    {
        new Section(sectionName, langKey).register();
        return this;
    }
}
