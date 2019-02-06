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
        try {
            chunks = loadChunks();
        }
        catch(FileNotFoundException e) {
            getLogger().info("No chunks file found! Initializing new list.");
            chunks = new ChunkList();
        }

        this.getCommand("chunk").setExecutor(new ChunkCommands(this));
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        saveChunks();
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e) {
        /*
        TODO: Make it where we keep a 5x5 grid of chunks loaded with specified chunks as the center
              So that we can keep chunks loaded in a way that will process entities
         */
        int x = e.getChunk().getX();
        int y = e.getChunk().getZ();

        Chunk chunk = new Chunk(x, y);
        if(chunks.find(chunk) >= 0) {
            // don't unload this chunk its in the list come on what are you doing
            getLogger().info("Server wanted to unload " + chunk.toString() + " but we saved it.");
            e.setCancelled(true);
        }
    }

    public void addChunk(Chunk chunk) throws AlreadyExistsException {
        getLogger().info("Adding chunk " + chunk.toString());
        chunks.insert(chunk);
        getLogger().info("Chunks are now " + chunks.toString());
    }

    public void removeChunk(Chunk chunk) throws ChunkNotFoundException {
        getLogger().info("Removing chunk " + chunk.toString());
        chunks.remove(chunk);
        getLogger().info("Chunks are now " + chunks.toString());
    }

    public boolean findChunk(Chunk chunk) {
        getLogger().info("Chunks are " + chunks.toString());
        boolean result = (chunks.find(chunk) >= 0);
        getLogger().info("Looking for " + chunk.toString() + " (" + result + ")");
        return result;
    }

    public void saveChunks() {
        getLogger().info("Saving chunks to file...");

        if(chunks.size() == 0) {
            getLogger().info("No chunks to save!");
            return;
        }

        File file = new File("plugins/chunkloader/chunks.cl");
        try {
            if(!file.exists()) {
                if(!file.createNewFile())
                    throw new IOException("Could not make new file!");
            }
        }
        catch(IOException e) {
            getLogger().severe("Error in making output file! Will not be able to save chunks.");
            getLogger().severe(e.getMessage());
        }

        FileOutputStream fileStream;
        ObjectOutputStream out;
        try {
            fileStream = new FileOutputStream(file);
            try {
                out = new ObjectOutputStream(fileStream);
                out.writeObject(chunks);
                out.close();
            }
            catch(IOException e) {
                getLogger().severe("Error in serializing chunk list! Will not be able to save chunks.");
                getLogger().severe(e.getMessage());
                return;
            }
            fileStream.close();
        }
        catch(FileNotFoundException e) {
            getLogger().severe("Error in making output file! Will not be able to save chunks.");
            getLogger().severe(e.getMessage());
        }
        catch(IOException e) {
            getLogger().severe("Error in saving to output file! Will not be able to save chunks.");
            getLogger().severe(e.getMessage());
        }
    }

    public ChunkList loadChunks() throws FileNotFoundException {
        getLogger().info("Loading chunks from file...");

        FileInputStream file;
        ObjectInputStream in;
        ChunkList list = null;

        file = new FileInputStream("plugins/chunkloader/chunks.cl");
        try {
            in = new ObjectInputStream(file);
            list = (ChunkList)in.readObject();
            in.close();
        }
        catch(IOException | ClassNotFoundException e) {
            getLogger().severe("Error in loading chunks from file! Will not be able to load saved chunks.");
            getLogger().severe(e.getMessage());
        }

        return list;
    }

    public void testChunkList() {
        getLogger().info("|-------------------- START TESTING --------------------|");
        long startTime = System.currentTimeMillis();
        Random rand = new Random();
        ArrayList<Chunk> testList = new ArrayList<Chunk>();
        for(int i = 0; i < 50000; i++) {
            int x = rand.nextInt(100000) - 50000;
            int y = rand.nextInt(100000) - 50000;
            try {
                Chunk tempChunk = new Chunk(x, y);
                if(rand.nextInt(3) == 2)
                    testList.add(tempChunk);
                chunks.insert(tempChunk);
            }
            catch(AlreadyExistsException e) {
                //getLogger().info("Chunk already exists, cannot add...");
            }
        }

        getLogger().info("Added " + chunks.size() + " chunks. Took " +
                (System.currentTimeMillis() - startTime) + " ms");
        long medianTime = System.currentTimeMillis();
        getLogger().info("Testing chunk finding, looking for " + testList.size() + " chunks...");

        for(Chunk chunk : testList) {
            int result = chunks.find(chunk);
            if (result < 0) {
                getLogger().severe("Something went very wrong with chunk list!");
                return;
            }
        }

        getLogger().info("Found all chunks successfully. Took " +
                (System.currentTimeMillis() - medianTime) + " ms");

        getLogger().info("Testing chunk that shouldn't exist...");
        long bsTimer = System.currentTimeMillis();
        Chunk bsChunk = new Chunk(-99999,99999);
        int in = chunks.find(bsChunk);
        boolean result = in >= 0;
        getLogger().info("Result: " + result + " Took: " + (System.currentTimeMillis() - bsTimer) + " ms");

        getLogger().info("Testing how long it takes to add a chunk at this point...");
        for(int i = 0; i < 5; i++) {
            long time = System.currentTimeMillis();
            Chunk chunk = new Chunk(100000 + i, 100000 + i);
            try {
                chunks.insert(chunk);
            }
            catch (Exception e) {
                // hi
            }
            getLogger().info((System.currentTimeMillis() - time) + " ms");
        }

        getLogger().info("Done testing chunk list. Total time elapsed: " +
                (System.currentTimeMillis() - startTime) + " ms");
        getLogger().info("|--------------------- END TESTING ---------------------|");
    }
}
