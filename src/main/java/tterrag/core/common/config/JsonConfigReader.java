package tterrag.core.common.config;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import tterrag.core.common.util.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class JsonConfigReader<T>
{
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final JsonParser parser = new JsonParser();
    private static final String KEY = "data";

    private File file;    
    private JsonObject root;
    
    private Class<T> type = null;
    private TypeToken<T> typeToken = null;

    public JsonConfigReader(Class<?> mainClass, String fullFileName, Class<T> objClass)
    {
        this(mainClass, new File(fullFileName), objClass);
    }

    public JsonConfigReader(Class<?> mainClass, File file, Class<T> objClass)
    {
        this.type = objClass;
        initialize(mainClass, file);
    }
    
    public JsonConfigReader(Class<?> mainClass, String fullFileName, TypeToken<T> objType)
    {
        this(mainClass, new File(fullFileName), objType);
    }

    public JsonConfigReader(Class<?> mainClass, File file, TypeToken<T> objType)
    {
        this.typeToken = objType;
        initialize(mainClass, file);
    }
    
    private void initialize(Class<?> mainClass, File file)
    {
        this.file = file;

        if (!file.exists())
        {
            file.getParentFile().mkdirs();
            IOUtils.copyFromJar(mainClass, "customthings/misc/" + file.getName(), file);
        }
        
        refresh();  
    }

    public JsonObject parseFile()
    {
        try
        {
            return parser.parse(new FileReader(file)).getAsJsonObject();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void refresh()
    {
        this.root = parseFile();
    }

    @SuppressWarnings("unchecked")
    public List<T> getElements()
    {
        JsonArray elements = root.get(KEY).getAsJsonArray();
        List<T> list = new ArrayList<T>();
        for (int i = 0; i < elements.size(); i++)
        {
            if (type == null) 
            {
                list.add((T) gson.fromJson(elements.get(i), typeToken.getType()));
            }
            else
            {
                list.add(gson.fromJson(elements.get(i), type));
            }
        }
        return list;
    }
}
