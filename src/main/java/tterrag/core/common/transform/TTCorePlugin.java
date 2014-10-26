package tterrag.core.common.transform;

import lombok.AutoGenMethodStub;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@AutoGenMethodStub
public class TTCorePlugin implements IFMLLoadingPlugin
{
    @Override
    public String[] getASMTransformerClass()
    {
        return new String[] { "tterrag.core.common.transform.TTCoreTransformer" };
    }
}
