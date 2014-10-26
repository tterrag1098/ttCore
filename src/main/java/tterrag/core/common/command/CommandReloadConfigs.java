package tterrag.core.common.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import tterrag.core.TTCore;
import tterrag.core.common.event.ConfigFileChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public class CommandReloadConfigs extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "reloadConfigs";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return "/reloadConfigs <modid> (<modid2> <modid3> ...)";
    }

    @Override
    public void processCommand(ICommandSender player, String[] args)
    {
        for (String s : args)
        {
            boolean validModid = false;
            for (ModContainer mod : Loader.instance().getModObjectList().keySet())
            {
                if (mod.getModId().equals(s))
                {
                    validModid = true;
                }
            }

            if (validModid)
            {
                ConfigFileChangedEvent event = new ConfigFileChangedEvent(s);
                FMLCommonHandler.instance().bus().post(event);

                if (event.isSuccessful())
                {
                    sendResult(player, s, "success");
                }
                else
                {
                    sendResult(player, s, "fail");
                }
            }
            else
            {
                sendResult(player, s, "invalid");
            }
        }
    }

    private void sendResult(ICommandSender player, String modid, String result)
    {
        player.addChatMessage(new ChatComponentText(String.format(TTCore.lang.localize("command.config.result." + result), modid)));
    }
}
