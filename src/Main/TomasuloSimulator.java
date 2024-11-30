package Main;

import reader.*;

public class TomasuloSimulator {
    private mainCode2 components; // Holds the initialized components
    private int clockCycle;      // Tracks the current clock cycle

    // Constructor
    public TomasuloSimulator(mainCode2 components) {
        this.components = components;
        this.clockCycle = 1;
    }

    
    
    
    // Run the Tomasulo simulation loop
    public void run() {
    	int i =1;
        while (!isSimulationComplete()) {
            System.out.println("Clock Cycle: " + clockCycle);

            // Example: Update Execution Table
            if (clockCycle == 1) {
                components.table.setIssue(0, clockCycle);
                Instruction instruction = components.table.getTable(0).getInstruction();
                components.updateReservationStation(instruction.operation,  true, instruction.operation, instruction.source1, instruction.source2, null, null, instruction.destination, 2,0,clockCycle);
//                components.updateRegister(Integer.parseInt(instruction.destination.substring(1))-1,"M1");
               
            } else if (i< components.table.size()) {
//            	components.table.setIssue(i, clockCycle);
            	Instruction instruction = components.table.getTable(i).getInstruction();
                components.updateReservationStation(instruction.operation,  true, instruction.operation, instruction.source1, instruction.source2, instruction.source1, instruction.source2, instruction.destination, null,i,clockCycle);
                components.subtractCycle(clockCycle);
//                components.updateRegister(Integer.parseInt(instruction.destination.substring(1))-1,"A1");
                i++;
            }
            else {
            	components.subtractCycle(clockCycle);
            }

            

            // Increment clock cycle
            clockCycle++;

            // Print the state of all components
            components.printTables(); 
        }
    }

    // Check if the simulation is complete
    private boolean isSimulationComplete() {
        // Example condition: Stop after 10 clock cycles
        return clockCycle >=50;
    }
    public static void main(String[] args) {
    	mainCode2 program = new mainCode2(
                11, // Number of registers
                3,  // Number of load buffers
                3,  // Number of add reservation stations
                3,  // Number of multiply reservation stations
                "src/instruction.txt" // Instruction file path
        );
    	TomasuloSimulator t =new TomasuloSimulator(program);
    	t.run();
    }
}