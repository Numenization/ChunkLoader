package ChunkLoader;

import ChunkLoader.Commands.ChunkCommands;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import ChunkLoader.ChunkList.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public final class ChunkPlugin extends JavaPlugin implements Listener {
    private ChunkList chunks;

    @Override
    public void onEnable() {
        // runs when the program starts, essentially the main()

        // try to load the chunk data from file
        try {
            chunks = loadChunks();
        }
        catch(FileNotFoundException e) {
            log("No chunks file found! Initializing new list.");
            chunks = new ChunkList();
        }

        for(Chunk chunk : chunks) {
            if(chunk.getWorld() != null) {
                getServer().getWorld(chunk.getWorld()).loadChunk(chunk.getX(), chunk.getY());
            }
        }

        // link up command executors and event listeners
        getCommand("chunk").setExecutor(new ChunkCommands(this));
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // runs when the server closes or the plugin is reloaded
        saveChunks();
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e) {
        // this will run whenever a chunk is unloaded

        // get the location of the chunk so that we can use it to create our own chunk object
        int x = e.getChunk().getX();
        int y = e.getChunk().getZ();

        // create our own chunk object and check if its in the list
        Chunk chunk = new Chunk(x, y);
        if(chunks.find(chunk) >= 0) {
            // cancel the event if the chunk is in the list. this will keep the chunk loaded.
            e.setCancelled(true);
        }
    }

    public void addChunk(Chunk chunk) throws AlreadyExistsException {
        // public method to allow other sources to add chunks to the list
        log("Adding chunk " + chunk.toString());
        chunks.insert(chunk);
    }

    public void removeChunk(Chunk chunk) throws ChunkNotFoundException {
        // public method to allow other sources to remove chunks from the list
        log("Removing chunk " + chunk.toString());
        chunks.remove(chunk);
    }

    public boolean findChunk(Chunk chunk) {
        // public method to allow other sources to check if a chunk is in the list
        boolean result = (chunks.find(chunk) >= 0);
        log("Checking " + chunk.toString() + " (loaded: " + result + ")");
        return result;
    }

    public void saveChunks() {
        // tries to save the chunk data to a file "plugins/chunkloader/chunks.cl"
        log("Saving chunks to file...");

        // don't bother if the list is empty
        if(chunks.size() == 0) {
            log("No chunks to save!");
            return;
        }

        // either open or create a new file
        File file = new File("plugins/chunkloader/chunks.cl");
        try {
            if(!file.exists()) {
                if(!file.createNewFile())
                    throw new IOException("Could not make new file!");
            }
        }
        catch(IOException e) {
            err("Error in making output file! Will not be able to save chunks.");
            err(e.getMessage());
            return;
        }

        // open up a file stream and object stream to store the chunk data in a file
        FileOutputStream fileStream;
        ObjectOutputStream out;
        try {
            fileStream = new FileOutputStream(file);
            try {
                // write data
                out = new ObjectOutputStream(fileStream);
                out.writeObject(chunks);
                out.close();
            }
            catch(IOException e) {
                err("Error in serializing chunk list! Will not be able to save chunks.");
                err(e.getMessage());
                return;
            }
            fileStream.close();
        }
        catch(FileNotFoundException e) {
            err("Error in making output file! Will not be able to save chunks.");
            err(e.getMessage());
        }
        catch(IOException e) {
            err("Error in saving to output file! Will not be able to save chunks.");
            err(e.getMessage());
        }
    }

    public ChunkList loadChunks() throws FileNotFoundException {
        // tries to load chunk data from "plugins/chunkloader/chunks.cl"
        log("Loading chunks from file...");

        // open up the file stream and object stream
        FileInputStream file;
        ObjectInputStream in;
        ChunkList list = null;

        file = new FileInputStream("plugins/chunkloader/chunks.cl");
        try {
            // read object data from file
            in = new ObjectInputStream(file);
            // cast raw data to chunklist object
            list = (ChunkList)in.readObject();
            in.close();
        }
        catch(IOException | ClassNotFoundException e) {
            err("Error in loading chunks from file! Will not be able to load saved chunks.");
            err(e.getMessage());
        }

        return list;
    }

    public void log(String msg) {
        // wrapper for the bukkit logger to allow shorthand logging
        getLogger().info(msg);
    }

    public void err(String msg) {
        // wrapper for the bukkit logger to allow shorthand logging
        getLogger().severe(msg);
    }

    public void testChunkList() {
        // tests several aspects of the ChunkList object, mainly for how long it takes to process data
        ChunkList temp = new ChunkList();
        log("|-------------------- START TESTING --------------------|");
        long startTime = System.currentTimeMillis();
        Random rand = new Random();
        ArrayList<Chunk> testList = new ArrayList<>();
        for(int i = 0; i < 1000000; i++) {
            int x = rand.nextInt(1000000) - 500000;
            int y = rand.nextInt(1000000) - 500000;
            try {
                Chunk tempChunk = new Chunk(x, y);
                if(rand.nextInt(2) == 1)
                    testList.add(tempChunk);
                temp.insert(tempChunk);
            }
            catch(AlreadyExistsException e) {
                //getLogger().info("Chunk already exists, cannot add...");
            }
        }

        log("Added " + temp.size() + " chunks. Took " +
                (System.currentTimeMillis() - startTime) + " ms");
        long medianTime = System.currentTimeMillis();
        log("Testing chunk finding, looking for " + testList.size() + " chunks...");

        for(Chunk chunk : testList) {
            int result = temp.find(chunk);
            if (result < 0) {
                err("Something went very wrong with chunk list!");
                return;
            }
        }

        log("Found all chunks successfully. Took " +
                (System.currentTimeMillis() - medianTime) + " ms");

        log("Testing chunk that shouldn't exist...");
        long bsTimer = System.currentTimeMillis();
        Chunk bsChunk = new Chunk(-999999,999999);
        int in = temp.find(bsChunk);
        boolean result = in >= 0;
        log("Result: " + result + " Took: " + (System.currentTimeMillis() - bsTimer) + " ms");

        log("Testing how long it takes to add a chunk at this point...");
        for(int i = 0; i < 5; i++) {
            long time = System.currentTimeMillis();
            Chunk chunk = new Chunk(100000 + i, 100000 + i);
            try {
                temp.insert(chunk);
            }
            catch (Exception e) {
                // hi
            }
            log((System.currentTimeMillis() - time) + " ms");
        }

        log("Done testing chunk list. Total time elapsed: " +
                (System.currentTimeMillis() - startTime) + " ms");
        log("|--------------------- END TESTING ---------------------|");
    }
}
