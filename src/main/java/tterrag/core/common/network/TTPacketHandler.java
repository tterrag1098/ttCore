package tterrag.core.common.network;

import tterrag.core.TTCore;
import tterrag.core.common.config.PacketConfigSync;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class TTPacketHandler
{
    public static final SimpleNetworkWrapper INSTANCE = new SimpleNetworkWrapper(TTCore.NAME);

    static
    {
        INSTANCE.registerMessage(PacketConfigSync.class, PacketConfigSync.class, 0, Side.CLIENT);
    }
}
