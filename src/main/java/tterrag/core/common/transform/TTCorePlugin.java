package tterrag.core.common.transform;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;

@SuppressWarnings("unused")
@MCVersion("1.7.10")
@IFMLLoadingPlugin.SortingIndex(10001)
public class TTCorePlugin implements IFMLLoadingPlugin
{
    @Override
    public String[] getASMTransformerClass()
    {
        return new String[] { "tterrag.core.common.transform.TTCoreTransformer" };
    }

    @Override
    public String getModContainerClass()
    {
        return null;
    }

    @Override
    public String getSetupClass()
    {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data)
    {
        ;
    }

    @Override
    public String getAccessTransformerClass()
    {
        return null;
    }
}
