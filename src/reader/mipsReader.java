package reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class mipsReader {
	   public List<Instruction> parseFile(String filePath) {
	        List<Instruction> instructions = new ArrayList<>();
	        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
	            String line;
	            while ((line = br.readLine()) != null) {
	                // Trim whitespace and ignore empty lines or comments
	                line = line.trim();
	                if (line.isEmpty() || line.startsWith("#")) {
	                    continue;
	                }

	                // Parse the line into an Instruction object
	                Instruction instruction = parseInstruction(line);
	                if (instruction != null) {
	                    instructions.add(instruction);
	                } else {
	                    System.out.println("Invalid instruction format: " + line);
	                }
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return instructions;
	    }

	    public Instruction parseInstruction(String line) {
	        // Split the instruction by spaces
	        String[] parts = line.split("\\s+");
	        if (parts.length != 4) {
	            return null; // Invalid format
	        }

	        String operation = parts[0];
	        String destination = parts[1];
	        String source1 = parts[2];
	        String source2 = parts[3];

	        // Create and return a new Instruction object
	        return new Instruction(operation, destination, source1, source2);
	    }
	    
	    public static void main(String[] args) {
	    	mipsReader parser = new mipsReader();
	        // Specify the file path (update to your file's location)
	    	String filePath = "src/instruction.txt";

	        List<Instruction> instructions = parser.parseFile(filePath);

	        // Print the parsed instructions
	        System.out.println("Parsed Instructions:");
	        for (Instruction instruction : instructions) {
	            System.out.println(instruction);
	        }
	    }
}
