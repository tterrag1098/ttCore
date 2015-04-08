package tterrag.core.common.config.annot;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import tterrag.core.common.config.AbstractConfigHandler.RestartReqs;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface RestartReq
{
    /**
     * What requirements this config has for restarting the game.
     * 
     * @see RestartReqs#NONE
     * @see RestartReqs#REQUIRES_WORLD_RESTART
     * @see RestartReqs#REQUIRES_MC_RESTART
     */
    RestartReqs value() default RestartReqs.NONE;
}
