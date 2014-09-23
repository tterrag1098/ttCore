package tterrag.core.common.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class ResourcePackAssembler
{
    private List<File> icons = new ArrayList<File>();
    private List<File> langs = new ArrayList<File>();

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

    public ResourcePackAssembler assemble() throws IOException
    {
        String pathToDir = dir.getAbsolutePath();
        File mcmeta = new File(pathToDir + "/pack.mcmeta");
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

        return this;
    }

    public void inject(File resourcePacksDir) throws IOException
    {
        FileUtils.copyDirectory(dir, new File(resourcePacksDir.getAbsolutePath() + "/" + dir.getName()));
        FileUtils.deleteDirectory(dir);
    }

    private void writeDefaultMcmeta(File file) throws IOException
    {
        file.delete();
        file.getParentFile().mkdirs();
        file.createNewFile();

        FileWriter fw = new FileWriter(file);
        fw.write(mcmeta);
        fw.flush();
        fw.close();
    }
}
