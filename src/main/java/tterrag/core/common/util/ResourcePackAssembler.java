package tterrag.core.common.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.FileResourcePack;
import net.minecraft.client.resources.IResourcePack;

import org.apache.commons.io.FileUtils;

import tterrag.core.TTCore;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.ReflectionHelper;

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
    
    private static List<IResourcePack> defaultResourcePacks;

    private static final String MC_META_BASE = "{\"pack\":{\"pack_format\":1,\"description\":\"%s\"}}";

    private File dir;
    private File zip;
    private String name;
    private String mcmeta;
    private String modid;
    private boolean hasPackPng = false;
    private Class<?> jarClass;

    public ResourcePackAssembler(File directory, String packName, String modid)
    {
        this.dir = directory;
        this.zip = new File(dir.getAbsolutePath() + ".zip");
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
     * 
     * @param file
     */
    public void addCustomFile(File file)
    {
        addCustomFile(null, file);
    }

    public ResourcePackAssembler assemble()
    {
        TTFileUtils.safeDeleteDirectory(dir);
        dir.mkdirs();
        
        String pathToDir = dir.getAbsolutePath();
        File metaFile = new File(pathToDir + "/pack.mcmeta");

        try
        {
            writeNewFile(metaFile, mcmeta);

            if (hasPackPng)
            {
                TTFileUtils.copyFromJar(jarClass, modid + "/" + "pack.png", new File(dir.getAbsolutePath() + "/pack.png"));
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

            TTFileUtils.zipFolderContents(dir, zip);
            TTFileUtils.safeDeleteDirectory(dir);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        return this;
    }

    public void inject()
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
        {
            try
            {
                if (defaultResourcePacks == null)
                {
                    defaultResourcePacks = ReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), "defaultResourcePacks",
                            "field_110449_ao", "ap");
                }

                File dest = new File(dir.getParent() + "/resourcepack/" + zip.getName());
                TTFileUtils.safeDelete(dest);
                FileUtils.copyFile(zip, dest);
                TTFileUtils.safeDelete(zip);
                writeNewFile(new File(dest.getParent() + "/readme.txt"),
                        TTCore.lang.localize("resourcepack.readme") + "\n\n" + TTCore.lang.localize("resourcepack.readme2"));
                defaultResourcePacks.add(new FileResourcePack(dest));
            }
            catch (Exception e)
            {
                TTCore.logger.error("Failed to inject resource pack for mod {}", modid, e);
            }
        }
        else
        {
            TTCore.logger.info("Skipping resource pack, we are on a dedicated server.");
        }
    }

    private void writeNewFile(File file, String defaultText) throws IOException
    {
        TTFileUtils.safeDelete(file);
        file.delete();
        file.getParentFile().mkdirs();
        file.createNewFile();

        FileWriter fw = new FileWriter(file);
        fw.write(defaultText);
        fw.flush();
        fw.close();
    }
}
