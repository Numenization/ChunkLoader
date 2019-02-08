package ChunkLoader.Commands;

import ChunkLoader.Chunk;
import ChunkLoader.ChunkList.*;
import ChunkLoader.ChunkPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.awt.font.NumericShaper;
import java.io.FileNotFoundException;

public class ChunkCommands implements CommandExecutor {

    // this class will allow us to easily pass on all the parameters of a command execution on to the functions
    // that will be executed by the commands
    private final class CommandContext {
        public CommandSender sender;
        public Command cmd;
        public String label;
        public String[] args;

        public CommandContext(CommandSender sender, Command cmd, String label, String[] args) {
            this.sender = sender;
            this.cmd = cmd;
            this.label = label;
            this.args = args;
        }
    }

    private ChunkPlugin plugin;

    public ChunkCommands(ChunkPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        CommandContext context = new CommandContext(sender, cmd, label, args);

        // make sure the main command is the chunk command
        if(cmd.getName().equalsIgnoreCase("chunk")) {
            if(args.length < 1)
                return false;

            // parse the arguments to run sub-commands
            if(args[0].equalsIgnoreCase("add")) {
                return chunkAdd(context);
            }
            else if(args[0].equalsIgnoreCase("remove")) {
                return chunkRemove(context);
            }
            else if(args[0].equalsIgnoreCase("save")) {
                return chunkSave(context);
            }
            else if(args[0].equalsIgnoreCase("load")) {
                return chunkLoad(context);
            }
            else if(args[0].equalsIgnoreCase("check")) {
                return chunkCheck(context);
            }
        }
        return false;
    }

    private boolean chunkAdd(CommandContext context) {
        // add the chunk the player is currently standing in to the list

        // only ingame players can use this command
        if(!(context.sender instanceof Player)) {
            context.sender.sendMessage("Only players can use the add command.");
        }

        // get chunk the player is standing in
        Player player = (Player)context.sender;
        int x = player.getLocation().getChunk().getX();
        int y = player.getLocation().getChunk().getZ();
        Chunk chunk = new Chunk(x, y);

        // if the player entered a radius, lets get that
        int radius = 0;

        if(context.args.length >= 2) {
            try {
                radius = Integer.parseInt(context.args[1]);
            }
            catch(NumberFormatException e) {
                context.sender.sendMessage("Could not parse radius. Should be an integer.");
                return true;
            }
        }

        // add the selected chunks to the chunklist
        if(radius == 0) {
            try {
                plugin.addChunk(chunk);
                context.sender.sendMessage("Set " + chunk.toString() + " to stay loaded");
                return true;
            }
            catch(AlreadyExistsException e) {
                context.sender.sendMessage(chunk.toString() + " is already set to stay loaded.");
                return true;
            }
        }
        for(Chunk ch : getChunksInSquare(radius, chunk)) {
            try {
                plugin.addChunk(ch);
                context.sender.sendMessage("Set " + ch.toString() + " to stay loaded");
            }
            catch(AlreadyExistsException e) {
                // ignore
            }
        }
        return true;
    }

    private boolean chunkRemove(CommandContext context) {
        // remove the chunk the player is currently standing in from the list

        // only ingame players can use this command
        if(!(context.sender instanceof Player)) {
            context.sender.sendMessage("Only players can use the remove command.");
        }

        // get the chunk the player is standing in
        Player player = (Player)context.sender;
        int x = player.getLocation().getChunk().getX();
        int y = player.getLocation().getChunk().getZ();
        Chunk chunk = new Chunk(x, y);

        // if the player entered a radius, lets get that
        int radius = 0;

        if(context.args.length >= 2) {
            try {
                radius = Integer.parseInt(context.args[1]);
            }
            catch(NumberFormatException e) {
                context.sender.sendMessage("Could not parse radius. Should be an integer.");
                return true;
            }
        }

        // try to remove that chunk from the list
        if(radius == 0) {
            try {
                plugin.removeChunk(chunk);
                context.sender.sendMessage("Removed " + chunk.toString() + " from list");
                return true;
            }
            catch(ChunkNotFoundException e) {
                context.sender.sendMessage("Chunk " + chunk + " not in loaded list!");
                return true;
            }
        }
        for(Chunk ch : getChunksInSquare(radius, chunk)) {
            try {
                plugin.removeChunk(ch);
                context.sender.sendMessage("Removed " + ch.toString() + " from list");
            }
            catch(ChunkNotFoundException e) {
                // ignore
            }
        }
        return true;
    }

    private boolean chunkSave(CommandContext context) {
        // save the current list to file
        plugin.saveChunks();
        context.sender.sendMessage("Saved chunks");
        return true;
    }

    private boolean chunkLoad(CommandContext context) {
        // load the current list from file
        context.sender.sendMessage("Loaded chunks");
        try {
            plugin.loadChunks();
        }
        catch(FileNotFoundException e) {
            context.sender.sendMessage("No chunk file found.");
        }
        return true;
    }

    private boolean chunkCheck(CommandContext context) {
        // check if the chunk the player is currently standing on is in the list
        // remove the chunk the player is currently standing in from the list

        // only ingame players can use this command
        if(!(context.sender instanceof Player)) {
            context.sender.sendMessage("Only players can use the check command.");
        }

        // get the chunk the player is standing on
        Player player = (Player)context.sender;
        int x = player.getLocation().getChunk().getX();
        int y = player.getLocation().getChunk().getZ();
        Chunk chunk = new Chunk(x, y);

        // check if the chunk is set to stay loaded and tell the player
        boolean result = plugin.findChunk(chunk);
        context.sender.sendMessage(chunk.toString() + " load status is: " + result);
        return true;
    }

    private Chunk[] getChunksInSquare(int radius, Chunk origin) {
        int rowLength = (radius * 2) + 1;
        int numChunks = (int)Math.pow(rowLength, 2);
        Chunk[] chunks = new Chunk[numChunks];
        // get bottom left corner to be (x, y)
        int x = origin.getX() - radius;
        int y = origin.getY() - radius;
        int index = 0;
        for(int i = 0; i < rowLength; i++) {
            int offsetY = y + i;
            for(int j = 0; j < rowLength; j++) {
                int offsetX = x + j;
                Chunk chunk = new Chunk(offsetX, offsetY);
                chunks[index] = chunk;
                index++;
            }
        }

        return chunks;
    }
}
