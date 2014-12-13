package tterrag.core.common.enchant;

import tterrag.core.common.config.ConfigHandler;

public class Enchants
{
    public static EnchantXPBoost XP_BOOST;

    public static void init()
    {
        if (ConfigHandler.allowXPBoost)
        {
            XP_BOOST = new EnchantXPBoost(ConfigHandler.enchantIDXPBoost);
        }
    }
}