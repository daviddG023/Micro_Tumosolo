package reader;

public class Instruction {
    public String operation; // e.g., "ADD.D"
    public String destination; // Destination register
    public String source1; // Source register 1
    public String source2; // Source register 2

    public Instruction(String operation, String destination, String source1, String source2) {
        this.operation = operation;
        this.destination = destination;
        this.source1 = source1;
        this.source2 = source2;
    }

    @Override
    public String toString() {
        return operation + " " + destination + " " + source1 + " " + source2;
    }
}
