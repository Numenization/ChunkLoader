package ChunkLoader;

import java.io.Serializable;

// class for storing chunk data
public final class Chunk implements Comparable<Chunk>, Serializable {
    private int x;
    private int y;
    private String world;

    public Chunk(int x, int y) {
        this.x = x;
        this.y = y;
        this.world = null;
    }

    public Chunk(int x, int y, String world) {
        this.x = x;
        this.y = y;
        this.world = world;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getWorld() {
        return world;
    }

    @Override
    public int compareTo(Chunk other) {
        return this.toString().compareTo(other.toString());
    }

    @Override
    public String toString() {
        return "[Chunk: ("+ x + "," + y + ")]";
    }
}
