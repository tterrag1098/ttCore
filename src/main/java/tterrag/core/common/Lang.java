package tterrag.core.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.StatCollector;

@AllArgsConstructor
@Getter
public class Lang
{
    private static final String REGEX = "\\" + '|';
    public static final char CHAR = '|';

    private String locKey;

    /**
     * Ignores the prefix stored in this instance of the class and localizes the raw string passed.
     * 
     * @param unloc
     *            The unlocalized string.
     * @param args
     *            The args to format the localized text withi.
     * 
     * @return A localized string.
     */
    public String localizeExact(String unloc, Object... args)
    {
        return translateToLocal(unloc, args);
    }

    /**
     * Localizes the string passed, first appending the prefix stored in this instance of the class.
     * 
     * @param unloc
     *            The unlocalized string.
     * @param args
     *            The args to format the localized text withi.
     * 
     * @return A localized string.
     */
    public String localize(String unloc, Object... args)
    {
        return translateToLocal(locKey + "." + unloc, args);
    }

    private String translateToLocal(String unloc, Object... args)
    {
        return StatCollector.translateToLocalFormatted(unloc, args);
    }

    /**
     * Splits the localized text on "|" into a String[].
     * 
     * @param unloc
     *            The unlocalized string.
     * @param args
     *            The args to format the localized text withi.
     * @return A localized list of strings.
     */
    public String[] localizeList(String unloc, String... args)
    {
        return splitList(localize(unloc, true, args));
    }

    /**
     * Splits a list of strings based on {@value #CHAR}
     * 
     * @param list
     *            The list of strings to split
     * @return An array of strings split on {@value #CHAR}
     */
    public String[] splitList(String list)
    {
        return list.split(REGEX);
    }
}
