package tterrag.core.common.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.minecraftforge.common.config.Configuration;

/**
 * Used to mark a <i>static</i> field as a config option.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Config
{

    /**
     * The section of the config option.
     * 
     * (AKA Category)
     * 
     * @return A string section name.
     */
    String section() default Configuration.CATEGORY_GENERAL;

    /**
     * The comment for the config option.
     * 
     * @return A string comment.
     */
    String comment() default "";

    /**
     * The min value of the config.
     * <p>
     * For non-numeric values, or if there is no min value, this should remain
     * unset.
     * 
     * @return A double minimum value for the config.
     */
    double min() default Integer.MIN_VALUE;

    /**
     * The max value of the config.
     * <p>
     * For non-numeric values, or if there is no max value, this should remain
     * unset.
     * 
     * @return A double maximum value for the config.
     */
    double max() default Integer.MAX_VALUE;

    /**
     * If this is set to true, it will not be synced to the client upon
     * connection to a server.
     * <p>
     * This is useful for configs that have no need to be the same between
     * client and server (or are purely client-sided).
     * <p>
     * Default: false
     */
    boolean noSync() default false;
}
