package ChunkLoader.Commands;

import ChunkLoader.Chunk;
import ChunkLoader.ChunkList.*;
import ChunkLoader.ChunkPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.awt.font.NumericShaper;

public class ChunkCommands implements CommandExecutor {
    private ChunkPlugin plugin;

    public ChunkCommands(ChunkPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("chunk")) {
            if(args.length < 1)
                return false;

            if(args[0].equalsIgnoreCase("add")) {
                // add the chunk the player is currently standing in to the list
                if(!(sender instanceof Player)) {
                    sender.sendMessage("Only players can use the add command.");
                }

                Player player = (Player)sender;
                int x = player.getLocation().getChunk().getX();
                int y = player.getLocation().getChunk().getZ();
                Chunk chunk = new Chunk(x, y);

                int radius = 0;

                if(args.length >= 2) {
                    try {
                        radius = Integer.parseInt(args[1]);
                    }
                    catch(NumberFormatException e) {
                        sender.sendMessage("Could not parse radius. Should be an integer.");
                        return true;
                    }
                }

                try {
                    plugin.addChunk(chunk);
                    sender.sendMessage("Set " + chunk.toString() + " to stay loaded");
                    return true;
                }
                catch(AlreadyExistsException e) {
                    sender.sendMessage(chunk.toString() + " is already set to stay loaded.");
                    return true;
                }
            }
            else if(args[0].equalsIgnoreCase("remove")) {
                // remove the chunk the player is currently standing in from the list
                if(!(sender instanceof Player)) {
                    sender.sendMessage("Only players can use the remove command.");
                }

                Player player = (Player)sender;
                int x = player.getLocation().getChunk().getX();
                int y = player.getLocation().getChunk().getZ();
                Chunk chunk = new Chunk(x, y);
                try {
                    plugin.removeChunk(chunk);
                    sender.sendMessage("Removed " + chunk.toString() + " from loaded list");
                    return true;
                }
                catch(ChunkNotFoundException e) {
                    sender.sendMessage(chunk.toString() + " is not set to load.");
                    return true;
                }
            }
            else if(args[0].equalsIgnoreCase("save")) {
                // save the current list to file
                plugin.saveChunks();
                sender.sendMessage("Saved chunks");
                return true;
            }
            else if(args[0].equalsIgnoreCase("load")) {
                // load the current list from file
                sender.sendMessage("Loaded chunks");
                return true;
            }
            else if(args[0].equalsIgnoreCase("check")) {
                // check if the chunk the player is currently standing on is in the list
                // remove the chunk the player is currently standing in from the list
                if(!(sender instanceof Player)) {
                    sender.sendMessage("Only players can use the check command.");
                }

                Player player = (Player)sender;
                int x = player.getLocation().getChunk().getX();
                int y = player.getLocation().getChunk().getZ();
                Chunk chunk = new Chunk(x, y);
                boolean result = plugin.findChunk(chunk);
                sender.sendMessage(chunk.toString() + " load status is: " + result);
                return true;
            }
        }
        return false;
    }
}
