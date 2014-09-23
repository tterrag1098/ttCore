package tterrag.core.common.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

import tterrag.core.TTCore;

public class IOUtils
{
    public static final FileFilter pngFilter = FileFilterUtils.suffixFileFilter(".png");
    public static final FileFilter langFilter =  FileFilterUtils.suffixFileFilter(".lang");

    /**
     * @param jarClass - A class from the jar in question
     * @param filename - Name of the file to copy, automatically prepended with "/assets/"
     * @param to - File to copy to
     */
    public static void copyFromJar(Class<?> jarClass, String filename, File to)
    {
        TTCore.logger.info("Copying file " + filename + " from jar");
        URL url = jarClass.getResource("/assets/" + filename);
        
        try
        {
            FileUtils.copyURLToFile(url, to);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
