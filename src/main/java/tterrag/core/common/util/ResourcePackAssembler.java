package tterrag.core.common.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;

import org.apache.commons.io.FileUtils;

import tterrag.core.TTCore;
import cpw.mods.fml.common.FMLCommonHandler;

public class ResourcePackAssembler
{
    @AllArgsConstructor
    private class CustomFile 
    {
        private String ext;
        private File file;
    }
    
    private List<File> icons = new ArrayList<File>();
    private List<File> langs = new ArrayList<File>();
    private List<CustomFile> customs = new ArrayList<CustomFile>();

    private static final String MC_META_BASE = "{\"pack\":{\"pack_format\":1,\"description\":\"%s\"}}";

    private File dir;
    private String name;
    private String mcmeta;
    private String modid;
    private boolean hasPackPng = false;
    private Class<?> jarClass;

    public ResourcePackAssembler(File directory, String packName, String modid)
    {
        this.dir = directory;
        this.name = packName;
        this.modid = modid.toLowerCase();
        this.mcmeta = String.format(MC_META_BASE, this.name);
    }

    public ResourcePackAssembler setHasPackPng(Class<?> jarClass)
    {
        this.jarClass = jarClass;
        hasPackPng = true;
        return this;
    }

    public void addIcon(File icon)
    {
        icons.add(icon);
    }

    public void addLang(File lang)
    {
        langs.add(lang);
    }
    
    public void addCustomFile(String path, File file)
    {
        customs.add(new CustomFile(path, file));
    }
    
    /**
     * Adds at the base dir
     * @param file
     */
    public void addCustomFile(File file)
    {
        addCustomFile(null, file);
    }

    public ResourcePackAssembler assemble()
    {
        String pathToDir = dir.getAbsolutePath();
        File mcmeta = new File(pathToDir + "/pack.mcmeta");

        try
        {
            writeDefaultMcmeta(mcmeta);

            if (hasPackPng)
            {
                IOUtils.copyFromJar(jarClass, modid + "/" + "pack.png", new File(dir.getAbsolutePath() + "/pack.png"));
            }

            String itemsDir = pathToDir + "/assets/" + modid + "/textures/items";
            String blocksDir = pathToDir + "/assets/" + modid + "/textures/blocks";
            String langDir = pathToDir + "/assets/" + modid + "/lang";

            for (File icon : icons)
            {
                FileUtils.copyFile(icon, new File(itemsDir + "/" + icon.getName()));
                FileUtils.copyFile(icon, new File(blocksDir + "/" + icon.getName()));
            }

            for (File lang : langs)
            {
                FileUtils.copyFile(lang, new File(langDir + "/" + lang.getName()));
            }
            
            for (CustomFile custom : customs)
            {
                File directory = new File(pathToDir + (custom.ext != null ? "/" + custom.ext : ""));
                directory.mkdirs();
                FileUtils.copyFile(custom.file, new File(directory.getAbsolutePath() + "/" + custom.file.getName()));
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        return this;
    }

    @Deprecated
    public void inject(File resourcePacksDir) throws IOException
    {
        inject();
    }

    public void inject()
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
        {
            File dest = new File(FMLCommonHandler.instance().getSavesDirectory().getParentFile() + "/resourcepacks/" + dir.getName());

            try
            {
                IOUtils.safeDeleteDirectory(dest);
                FileUtils.copyDirectory(dir, dest);
                IOUtils.safeDeleteDirectory(dir);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        else
        {
            TTCore.logger.info("Skipping resource pack, we are on a dedicated server.");
        }
    }

    private void writeDefaultMcmeta(File file) throws IOException
    {
        IOUtils.safeDelete(file);
        file.getParentFile().mkdirs();
        file.createNewFile();

        FileWriter fw = new FileWriter(file);
        fw.write(mcmeta);
        fw.flush();
        fw.close();
    }
}
