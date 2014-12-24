package tterrag.core.api.common.load;

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
public interface ILoadEventReceiver<T extends FMLStateEvent>
{
    /**
     * Method called when the event is processed
     * 
     * @param event The {@link FMLStateEvent event} of the class defined by {@link #getEventClass()}
     */
    void onEvent(T event);

    /**
     * The exact type of the event this class receives. Does not check for extension.
     */
    Class<T> getEventClass();
}
