package ChunkLoader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class ChunkList implements Iterable<Chunk>, Serializable {
    // keep chunks in a sorted arraylist. sort by using a sorted insert. use a string generated based on the chunks
    // coordinates for comparison, "[Chunk: (x,y)]"

    // exception intended to be thrown when a chunk is added to a list in which it is already in
    public final class AlreadyExistsException extends Exception {
        public AlreadyExistsException(String errorMessage) {
            super(errorMessage);
        }
    }

    // exception intended to be thrown when a chunk is removed from a list in which it is not in
    public final class ChunkNotFoundException extends Exception {
        public ChunkNotFoundException(String errorMessage) {
            super(errorMessage);
        }
    }

    private ArrayList<Chunk> chunks;

    public ChunkList() {
        chunks = new ArrayList<>();
    }

    // linear sorted insert. iterates through the list until it finds a chunk that is bigger than it,
    // then the chunk will be inserted at that index and every other chunk is shifted right
    // throws AlreadyExistsException if the chunk is already in the list
    public void insert(Chunk chunk) throws AlreadyExistsException {
        if(chunks.size() == 0) {
            chunks.add(chunk);
            return;
        }

        for(int i = 0; i < chunks.size(); i++) {
            int comparison = chunk.compareTo(chunks.get(i));
            if (comparison == 0) {
                throw new AlreadyExistsException("Chunk already in list!");
            }
            else if(comparison < 0) {
                chunks.add(i, chunk);
                return;
            }
        }

        chunks.add(chunk);
    }

    // binary sorted insert. find the correct insertion point using binary search methods, insert at that position,
    // then shift everything that was at that position and beyond to the right.
    public void insert(Chunk chunk, boolean binary) {
        if(chunks.size() == 0) {
            
        }

    }

    // finds a chunk in a list and removes it if it exists. throws ChunkNotFound exception if it doesn't exist.
    public void remove(Chunk chunk) throws ChunkNotFoundException {
        int index = find(chunk);
        if(index < 0) {
            throw new ChunkNotFoundException("Chunk not found!");
        }

        chunks.remove(index);
    }

    // searches chunk list with a binary search. this public level method that is intended to be called elsewhere
    // starts off the search assuming the beginning and end indicies to search are the beginning and end of the list.
    // from there it will recursively search with the findRecursive method, with dynamic start/end
    public int find(Chunk chunk) {
        if(chunks.size() == 0) {
            return -1;
        }

        if(chunks.size() == 1) {
            if(chunks.get(0).compareTo(chunk) == 0)
                return 0;
            else
                return -1;
        }

        int right = chunks.size() - 1;
        int left = 0;
        if (right > left) {
            int mid = (left + (right - 1)) / 2;

            // return true if we are on the element
            if(chunks.get(mid).compareTo(chunk) == 0) {
                return mid;
            }

            // fixes a weird bug with rounding
            if(mid < chunks.size() - 1) {
                if(chunks.get(mid + 1).compareTo(chunk) == 0) {
                    return mid + 1;
                }
            }

            // look to the left if we are smaller and right if bigger
            if(chunk.compareTo(chunks.get(mid)) < 0) {
                return findRecursive(chunk, left, mid - 1);
            }
            else if(chunk.compareTo(chunks.get(mid)) > 0) {
                return findRecursive(chunk, mid + 1, right);
            }

        }

        return -1;
    }

    // recursive portion of find method. private
    private int findRecursive(Chunk chunk, int start, int end) {
        if (end > start) {
            int mid = (start + (end - 1)) / 2;

            // return true if we are on the element
            if(chunks.get(mid).compareTo(chunk) == 0) {
                return mid;
            }

            // fixes a weird bug with rounding
            if(mid < chunks.size() - 1) {
                if(chunks.get(mid + 1).compareTo(chunk) == 0) {
                    return mid + 1;
                }
            }

            // look to the left if we are smaller and right if bigger
            if(chunk.compareTo(chunks.get(mid)) < 0) {
                return findRecursive(chunk, start, mid - 1);
            }
            else if(chunk.compareTo(chunks.get(mid)) > 0) {
                return findRecursive(chunk, mid + 1, end);
            }
        }

        return -1;
    }

    // just returns the size of the chunk list
    public int size() {
        return chunks.size();
    }

    @Override
    public Iterator<Chunk> iterator() {
        return chunks.iterator();
    }

    // toString override that will build a string with the name of every chunk in the list separated by spaces
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for(Chunk chunk : chunks) {
            builder.append(chunk.toString());
            builder.append(" ");
        }
        return builder.toString();
    }
}


