package tterrag.core.api.common.config;

import java.io.File;
import java.util.List;

import net.minecraftforge.common.config.ConfigCategory;
import tterrag.core.common.config.AbstractConfigHandler.Section;

public interface IConfigHandler
{
    void initialize(File cfg);
    
    List<Section> getSections();

    ConfigCategory getCategory(String name);
    
    String getModID();
}
