package LoadBuffer;

import java.util.ArrayList;
import java.util.List;

import executionTable.ExecutionEntry;

public class LoadBuffers {
    private List<LoadBuffer> loadBuffers; // List to hold all the LoadBuffer objects

    // Constructor to create 'n' load buffers dynamically
    public LoadBuffers(int n) {
        loadBuffers = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            loadBuffers.add(new LoadBuffer("L" + i)); // Generate buffer names (L1, L2, ...)
        }
    }

    // Getter for a specific load buffer by index
    public LoadBuffer getLoadBuffer(int index) {
        if (index >= 0 && index < loadBuffers.size()) {
            return loadBuffers.get(index);
        } else {
            throw new IndexOutOfBoundsException("Invalid buffer index: " + index);
        }
    }

    // Reset all load buffers (set busy to false and clear address)
    public void resetAll() {
        for (LoadBuffer buffer : loadBuffers) {
            buffer.reset();
        }
    }
    public LoadBuffer get(int i) {
    	return loadBuffers.get(i);
    }
    public int size() {
    	return loadBuffers.size();
    }

    // Print all load buffers
    public void printAllBuffers() {
        for (LoadBuffer buffer : loadBuffers) {
            System.out.println(buffer);
        }
    }
}
