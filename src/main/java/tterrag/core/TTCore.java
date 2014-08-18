package tterrag.core;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Set;

import net.minecraftforge.common.MinecraftForge;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import tterrag.core.utils.Handler;
import tterrag.core.utils.Handler.HandlerType;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = TTCore.NAME, name = TTCore.NAME)
public class TTCore
{
    public static final String NAME = "ttCore";
    public static final String BASE_PACKAGE = "tterrag";

    public static final Logger logger = LogManager.getLogger(NAME);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        registerEventHandlers();
    }

    private void registerEventHandlers()
    {
        ClassPath classpath;

        try
        {
            classpath = ClassPath.from(TTCore.class.getClassLoader());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        Set<ClassInfo> classes = classpath.getTopLevelClassesRecursive(BASE_PACKAGE);

        for (ClassInfo info : classes)
        {
            try
            {
                Class<?> c = info.load();

                Annotation a = c.getAnnotation(Handler.class);
                if (a != null)
                {
                    registerHandler(c, (Handler) a);
                }
            }
            catch (Throwable t)
            {
                if ((t instanceof NoClassDefFoundError || t instanceof IllegalStateException) && FMLCommonHandler.instance().getSide().isServer())
                {
                    logger.info("Not registering handler " + info.getName() + " because necessary classes are missing.");
                }
                else
                {
                    logger.fatal(String.format("Failed to register handler %s, this is a serious bug, certain functions will not be avaialble!", info.getName()));
                    t.printStackTrace();
                }
            }
        }
    }

    private void registerHandler(Class<?> c, Handler handler) throws InstantiationException, IllegalAccessException
    {
        if (ArrayUtils.contains(handler.types(), HandlerType.FORGE))
            MinecraftForge.EVENT_BUS.register(c.newInstance());

        if (ArrayUtils.contains(handler.types(), HandlerType.FML))
            FMLCommonHandler.instance().bus().register(c.newInstance());
    }
}
