package tterrag.core.common;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.minecraftforge.common.MinecraftForge;

import org.apache.commons.lang3.ArrayUtils;

import tterrag.core.TTCore;
import tterrag.core.common.Handlers.Handler.HandlerType;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;

public class Handlers
{
    @Target(value = ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Handler
    {
        public enum HandlerType
        {
            FORGE, FML
        }

        HandlerType[] types() default { HandlerType.FORGE, HandlerType.FML };
    }
    
    private static Set<String> packageSet = new HashSet<String>();
    
    static { packageSet.add(TTCore.BASE_PACKAGE); }
    
    public static void addPackage(String packageName)
    {
        if (Loader.instance().hasReachedState(LoaderState.INITIALIZATION))
        {
            throw new RuntimeException("This method must only be called in preinit");
        }
        
        TTCore.logger.info("Adding package " + packageName + " to handler search.");
        packageSet.add(packageName);
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

        for (String packageName : packageSet)
        {
            Set<ClassInfo> classes = classpath.getTopLevelClassesRecursive(packageName);

            for (ClassInfo info : classes)
            {
                if (!info.getPackageName().contains("client") || FMLCommonHandler.instance().getEffectiveSide().isClient()) // if not client handler, or we are on client, continue
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
                        FMLCommonHandler.instance().raiseException(t,
                                String.format("[%s] [Handlers] Client class %s was not inside client package! Report this to the mod owner!", TTCore.NAME, info.getName()),
                                true);
                    }
                }
                else
                {
                    TTCore.logger.info(String.format("[Handlers] Skipping client class %s, we are on a dedicated server", info.getSimpleName()));
                }
            }
        }
    }

    private static void registerHandler(Class<?> c, Handler handler) throws InstantiationException, IllegalAccessException
    {
        TTCore.logger.info(String.format("[Handlers] Registering handler %s to busses: %s", c.getSimpleName(), Arrays.deepToString(handler.types())));

        if (ArrayUtils.contains(handler.types(), HandlerType.FORGE))
            MinecraftForge.EVENT_BUS.register(c.newInstance());

        if (ArrayUtils.contains(handler.types(), HandlerType.FML))
            FMLCommonHandler.instance().bus().register(c.newInstance());
    }
}
