package executionTable;
import reader.*;

public class ExecutionEntry {
    private Instruction instruction; // Instruction (e.g., MUL, ADD)
    private Integer issue;           // Issue cycle (nullable)
    private Cycle execution;         // Execution start and end cycles (nullable)
    private Integer writeBack;       // Write-back cycle (nullable)
    private boolean ready;

    // Nested class for Execution Cycle (start and end)
    public static class Cycle {
        private Integer start;      // Start of execution (nullable)
        private Integer end;        // End of execution (nullable)

        public Cycle() {
            this.start = null; // Default to null
            this.end = null;   // Default to null
        }

        public Integer getStart() {
            return start;
        }

        public void setStart(Integer start) {
            if (this.start == null) {
                this.start = start; // Only set start if it hasn't been set yet
            } else {
                throw new IllegalStateException("Execution start is already set.");
            }
        }

        public Integer getEnd() {
            return end;
        }

        public void setEnd(Integer end) {
            if (this.start != null) { // Ensure start is set before setting end
                this.end = end;
            } else {
                throw new IllegalStateException("Cannot set execution end before start.");
            }
        }

        @Override
        public String toString() {
            return "Start=" + (start != null ? start : "null") + ", End=" + (end != null ? end : "null");
        }
    }

    // Constructor
    public ExecutionEntry(Instruction instruction) {
        this.instruction = instruction;
        this.issue = null; // Initially null
        this.execution = new Cycle(); // Execution cycle starts as null
        this.writeBack = null; // Initially null
        this.ready = true;
    }

    // Getters and setters
    public Instruction getInstruction() {
        return instruction;
    }

    public void setInstruction(Instruction instruction) {
        this.instruction = instruction;
    }

    public Integer getIssue() {
        return issue;
    }

    public void setIssue(Integer issue) {
        this.issue = issue;
    }

    public Cycle getExecution() {
        return execution;
    }

    public void setExecutionStart(Integer execStart) {
        execution.setStart(execStart); // Set execution start independently
    }

    public void setExecutionEnd(Integer execEnd) {
        execution.setEnd(execEnd); // Set execution end independently
    }

    public Integer getWriteBack() {
        return writeBack;
    }

    public void setWriteBack(Integer writeBack) {
        this.writeBack = writeBack;
    }
    
    public boolean getReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    @Override
    public String toString() {
        return "ExecutionEntry{" +
                "instruction='" + instruction + '\'' +
                ", issue=" + (issue != null ? issue : "null") +
                ", execution=" + execution +
                ", writeBack=" + (writeBack != null ? writeBack : "null") +
                '}';
    }

	public Object getStart() {
		return this.execution.start;
	}
}
