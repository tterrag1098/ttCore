package tterrag.core.common;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraftforge.common.MinecraftForge;

import org.apache.commons.lang3.ArrayUtils;

import tterrag.core.TTCore;
import tterrag.core.common.Handlers.Handler.HandlerType;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Handlers
{
    /**
     * To be put on classes that are Forge/FML event handlers. If you are using this from another mod, be sure to call <code>Handlers.addPackage("your.base.package")</code> so that this class can
     * search your classes
     * <p>
     * Class must have either a public no args constructor (or lombok {@link NoArgsConstructor}) or a singleton object with field name <code>INSTANCE</code> (public or private).
     */
    @Target(value = ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Handler
    {
        public enum HandlerType
        {
            /**
             * Represents the {@link MinecraftForge}<code>.EVENT_BUS</code>
             */
            FORGE,
            
            /**
             * Represents the {@link FMLCommonHandler.instance().bus()}
             */
            FML
        }

        /**
         * Array of buses to register this handler to. Leave blank for all.
         */
        HandlerType[] value() default { HandlerType.FORGE, HandlerType.FML };
        
        /**
         * Switching to <code>value</code> for easier usage
         */
        @Deprecated
        HandlerType[] types() default { HandlerType.FORGE, HandlerType.FML };
    }
    
    private static Set<String> packageSet = new HashSet<String>();

    static
    {
        packageSet.add(TTCore.BASE_PACKAGE);
    }

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
                        TTCore.logger.error(String.format("[Handlers] %s threw an error on load, skipping...", info.getName()));
                        t.printStackTrace();
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

        HandlerType[] types = handler.value();
        
        if (ArrayUtils.contains(types, HandlerType.FORGE))
            MinecraftForge.EVENT_BUS.register(tryInit(c));

        if (ArrayUtils.contains(types, HandlerType.FML))
            FMLCommonHandler.instance().bus().register(tryInit(c));
    }

    private static Object tryInit(Class<?> c)
    {
        try
        {
            return c.newInstance();
        }
        catch (Exception e)
        {
            try
            {
                Field inst = c.getDeclaredField("INSTANCE");
                inst.setAccessible(true);
                return inst.get(null);
            }
            catch (Exception e1)
            {
                throw new RuntimeException("Could not instantiate @Handler class " + c.getName() + " or access INSTANCE field.");
            }
        }
    }
}
