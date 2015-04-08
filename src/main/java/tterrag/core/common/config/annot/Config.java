package tterrag.core.common.config.annot;

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
    String value() default Configuration.CATEGORY_GENERAL;
}
