package executionTable;

import java.util.ArrayList;
import java.util.List;

public class ExecutionTable {
    private List<ExecutionEntry> table; // List to hold all entries

    // Constructor
    public ExecutionTable() {
        this.table = new ArrayList<>();
    }
    public ExecutionEntry getTable(int i){
    	return table.get(i);
    }
    public boolean getReady(int i) {
    	return table.get(i).getReady();
    }
    public void setReady(int i,boolean b) {
    	table.get(i).setReady(b);
    }

    // Add a new entry to the table
    public void addEntry(ExecutionEntry entry) {
        table.add(entry);
    }
    // Add multiple entries (from a list)
    public void addEntries(List<ExecutionEntry> entries) {
        table.addAll(entries); // Add all entries from the list
    }
    
    // Set issue cycle for a specific entry
    public void setIssue(int index, int issueCycle) {
        table.get(index).setIssue(issueCycle);
    }

    // Set execution start cycle for a specific entry
    public void setExecutionStart(int index, int startCycle) {
        table.get(index).setExecutionStart(startCycle);
    }

    // Set execution end cycle for a specific entry
    public void setExecutionEnd(int index, int endCycle) {
        table.get(index).setExecutionEnd(endCycle);
    }

    // Set write-back cycle for a specific entry
    public void setWriteBack(int index, int writeBackCycle) {
        table.get(index).setWriteBack(writeBackCycle);
    }
    public ExecutionEntry get(int i) {
    	return table.get(i);
    }
    public int size() {
    	return table.size();
    }
    // Print the table in a tabular format
    public void printTable() {
        System.out.printf("%-15s %-10s %-20s %-10s\n", "Instruction", "Issue", "Execution (Start-End)", "WriteBack");
        System.out.println("-------------------------------------------------------------");
        for (ExecutionEntry entry : table) {
            String execution = "Start=" + (entry.getExecution().getStart() != null ? entry.getExecution().getStart() : "null")
                    + ", End=" + (entry.getExecution().getEnd() != null ? entry.getExecution().getEnd() : "null");
            System.out.printf("%-15s %-10s %-20s %-10s\n",
                    entry.getInstruction(),
                    entry.getIssue() != null ? entry.getIssue() : "null",
                    execution,
                    entry.getWriteBack() != null ? entry.getWriteBack() : "null");
        }
    }
	public Integer getExecutionStart(int i) {

		return table.get(i).getIssue();
	}
}
