package tterrag.core.common.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

public class CommandScoreboardInfo extends CommandBase
{
    @Override
    public String getName()
    {
        return "scoreboardinfo";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return "/scoreboardinfo <board> <name>";
    }

    @SuppressWarnings("unchecked")
    @Override
    public void execute(ICommandSender player, String[] args) throws CommandException
    {
        if (args.length < 2)
        {
            throw new WrongUsageException("This command requires 2 args: <board> <name>");
        }

        Scoreboard board = player.getEntityWorld().getScoreboard();

        ScoreObjective obj = board.getObjective(args[0]);

        if (obj == null)
        {
            player.addChatMessage(new ChatComponentText("No such board " + args[0]));
        }

        Collection<Score> collection = board.getScores();

        for (Score score : collection)
        {
            if (score.getPlayerName().equals(args[1]))
            {
                player.addChatMessage(new ChatComponentText(args[1] + "'s score on board \"" + args[0] + "\": " + score.getScorePoints()));
                return;
            }
        }

        player.addChatMessage(new ChatComponentText("No score for " + args[1] + " on board \"" + args[0] + "\""));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public List addTabCompletionOptions(ICommandSender player, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            List<String> boards = new ArrayList<String>();
            for (ScoreObjective obj : (Collection<ScoreObjective>) player.getEntityWorld().getScoreboard().getScoreObjectives())
            {
                boards.add(obj.getName());
            }
            
            return getListOfStringsMatchingLastWord(args, boards);
        }

        if (args.length == 2)
        {
            List<String> players = new ArrayList<String>();
            for (EntityPlayer p : (List<EntityPlayer>) player.getEntityWorld().playerEntities)
            {
                players.add(p.getName());
            }
            
            return getListOfStringsMatchingLastWord(args, players);
        }
        
        return null;
    }
}
