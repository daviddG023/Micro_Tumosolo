package RegFile;

import java.util.ArrayList;
import java.util.List;

import reservation_tables.Point;

public class RegFiles {
    private List<RegFile> registers; // List to hold all the RegFile objects

    // Constructor to create 'i' registers dynamically
    public RegFiles(int i) {
        registers = new ArrayList<>();
        for (int j = 1; j <= i; j++) {
            registers.add(new RegFile("R" + j)); // Generate register names (R1, R2, ...)
        }
    }

    // Getter for a specific register by index
    public RegFile getRegister(int index) {
        if (index >= 0 && index < registers.size()) {
            return registers.get(index);
        } else {
            throw new IndexOutOfBoundsException("Invalid register index: " + index);
        }
    }

    // Reset Qi of all registers
    public void resetAllQi() {
        for (RegFile register : registers) {
            register.resetRow();
        }
    }

    // Print all registers
    public void printAllRegisters() {
        for (RegFile register : registers) {
            System.out.println(register);
        }
    }
    public int size() {
    	return registers.size();
    }

	public void resetRow(int i) {
		registers.get(i).resetRow();
		
	}
	public RegFile get(int i) {
		return registers.get(i);
	}
	
	
}
