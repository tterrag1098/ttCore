package tterrag.core.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

import tterrag.core.TTCore;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TTFileUtils
{
    public static final FileFilter pngFilter = FileFilterUtils.suffixFileFilter(".png");
    public static final FileFilter langFilter = FileFilterUtils.suffixFileFilter(".lang");

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

    /**
     * @author Ilias Tsagklis
     *         <p>
     *         From <a href=
     *         "http://examples.javacodegeeks.com/core-java/util/zip/extract-zip-file-with-subdirectories/"
     *         > this site.</a>
     * 
     * @param zip - The zip file to extract
     * 
     * @return The folder extracted to
     */
    @NonNull
    public static File extractZip(File zip)
    {
        String zipPath = zip.getParent() + "/extracted";
        File temp = new File(zipPath);
        temp.mkdir();

        ZipFile zipFile = null;

        try
        {
            zipFile = new ZipFile(zip);

            // get an enumeration of the ZIP file entries
            Enumeration<? extends ZipEntry> e = zipFile.entries();

            while (e.hasMoreElements())
            {
                ZipEntry entry = e.nextElement();

                File destinationPath = new File(zipPath, entry.getName());

                // create parent directories
                destinationPath.getParentFile().mkdirs();

                // if the entry is a file extract it
                if (entry.isDirectory())
                {
                    continue;
                }
                else
                {
                    System.out.println("Extracting file: " + destinationPath);

                    BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));

                    int b;
                    byte buffer[] = new byte[1024];

                    FileOutputStream fos = new FileOutputStream(destinationPath);

                    BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);

                    while ((b = bis.read(buffer, 0, 1024)) != -1)
                    {
                        bos.write(buffer, 0, b);
                    }

                    bos.close();
                    bis.close();
                }
            }
        }
        catch (IOException e)
        {
            System.out.println("Error opening zip file" + e);
        }
        finally
        {
            try
            {
                if (zipFile != null)
                {
                    zipFile.close();
                }
            }
            catch (IOException e)
            {
                System.out.println("Error while closing zip file" + e);
            }
        }

        return temp;
    }

    @NonNull
    public static File writeToFile(String filepath, String json)
    {
        File file = new File(filepath);

        try
        {
            file.createNewFile();
            FileWriter fw = new FileWriter(file);
            fw.write(json);
            fw.flush();
            fw.close();
            return file;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    public static void safeDelete(File file)
    {
        try
        {
            file.delete();
        }
        catch (Exception e)
        {
            TTCore.logger.error("Deleting file " + file.getAbsolutePath() + " failed.");
        }
    }

    @NonNull
    public static void safeDeleteDirectory(File file)
    {
        try
        {
            FileUtils.deleteDirectory(file);
        }
        catch (Exception e)
        {
            TTCore.logger.error("Deleting directory " + file.getAbsolutePath() + " failed.");
        }
    }
}
