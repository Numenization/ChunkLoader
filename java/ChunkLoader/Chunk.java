package ChunkLoader;

import java.io.Serializable;

// class for storing chunk data
public final class Chunk implements Comparable<Chunk>, Serializable {
    private int x;
    private int y;

    public Chunk(int x_coord, int y_coord) {
        x = x_coord;
        y = y_coord;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
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
