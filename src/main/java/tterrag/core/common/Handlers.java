package tterrag.core.common;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Set;

import net.minecraftforge.common.MinecraftForge;

import org.apache.commons.lang3.ArrayUtils;

import tterrag.core.TTCore;
import tterrag.core.common.Handlers.Handler.HandlerType;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

import cpw.mods.fml.common.FMLCommonHandler;

public class Handlers
{
    @Target(value = ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Handler
    {
        public enum HandlerType {
            FORGE,
            FML
        }
        
        HandlerType[] types() default {HandlerType.FORGE, HandlerType.FML};
    }
    
    public static void register()
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

        Set<ClassInfo> classes = classpath.getTopLevelClassesRecursive(TTCore.BASE_PACKAGE);

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
                    TTCore.logger.info("Not registering handler " + info.getName() + " because necessary classes are missing.");
                }
                else
                {
                    TTCore.logger.fatal(String.format("Failed to register handler %s, this is a serious bug, certain functions will not be avaialble!", info.getName()));
                    t.printStackTrace();
                }
            }
        }
    }

    private static void registerHandler(Class<?> c, Handler handler) throws InstantiationException, IllegalAccessException
    {
        if (ArrayUtils.contains(handler.types(), HandlerType.FORGE))
            MinecraftForge.EVENT_BUS.register(c.newInstance());

        if (ArrayUtils.contains(handler.types(), HandlerType.FML))
            FMLCommonHandler.instance().bus().register(c.newInstance());
    }
}
