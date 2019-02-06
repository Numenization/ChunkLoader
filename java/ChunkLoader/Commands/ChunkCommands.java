package ChunkLoader.Commands;

import ChunkLoader.Chunk;
import ChunkLoader.ChunkList.*;
import ChunkLoader.ChunkPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
                try {
                    plugin.addChunk(chunk);
                    sender.sendMessage("Set chunk to stay loaded");
                    return true;
                }
                catch(AlreadyExistsException e) {
                    sender.sendMessage("This chunk is already set to stay loaded.");
                    return true;
                }
            }
            else if(args[0].equalsIgnoreCase("remove")) {
                // remove the chunk the player is currently standing in from the list
                if(!(sender instanceof Player)) {
                    sender.sendMessage("Only players can use the add command.");
                }

                Player player = (Player)sender;
                int x = player.getLocation().getChunk().getX();
                int y = player.getLocation().getChunk().getZ();
                Chunk chunk = new Chunk(x, y);
                try {
                    plugin.removeChunk(chunk);
                    sender.sendMessage("Removed chunk from loaded list");
                    return true;
                }
                catch(ChunkNotFoundException e) {
                    sender.sendMessage("This chunk is not set to load.");
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
                    sender.sendMessage("Only players can use the add command.");
                }

                Player player = (Player)sender;
                int x = player.getLocation().getChunk().getX();
                int y = player.getLocation().getChunk().getZ();
                Chunk chunk = new Chunk(x, y);
                boolean result = plugin.findChunk(chunk);
                sender.sendMessage("Chunk load status is: " + result);
                return true;
            }
        }
        return false;
    }
}
