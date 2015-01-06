package tterrag.core.api.common.load;

import java.util.List;

import tterrag.core.TTCore;
import cpw.mods.fml.common.event.FMLStateEvent;

/**
 * You can register an object that implements this interface to receive {@link FMLStateEvent}s
 * outside your {@code @Mod} class.
 * <p>
 * To do this call {@link TTCore#registerLoadEventReceiver(ILoadEventReceiver)}.
 * <p>
 * Do note that they will be received likely BEFORE your {@code @Mod} processes the same event.
 */
public interface ILoadEventReceiver
{
    /**
     * Method called when the event is processed
     * 
     * @param event The {@link FMLStateEvent event} of the class defined by {@link #getEventClass()}
     */
    void onEvent(FMLStateEvent event);

    /**
     * The exact type of the event this class receives. Does not check for extension.
     */
    List<Class<? extends FMLStateEvent>> getEventClasses();
}
