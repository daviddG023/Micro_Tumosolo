package Main;

import RegFile.RegFiles;
import LoadBuffer.LoadBuffers;
import reservation_tables.*;
import reader.*;
import executionTable.*;
import java.util.ArrayList;
import java.util.Comparator;

import java.util.List;


public class mainCode {
    ExecutionTable table;
    RegFiles regFile;
    LoadBuffers loadBuffers;
    ReservationStations addStations;
    ReservationStations mulStations;
	ArrayList<Points> writeBack; 
	ArrayList<ReservationStation> queue; 
    
	// Base Point Class

	public class Points {
	    protected int x;
	    protected int y;

	    // Constructor
	    public Points(int x, int y) {
	        this.x = x;
	        this.y = y;
	    }

	    // Getter and Setter for x
	    public int getX() {
	        return x;
	    }

	    public void setX(int x) {
	        this.x = x;
	    }

	    // Getter and Setter for y
	    public int getY() {
	        return y;
	    }

	    public void setY(int y) {
	        this.y = y;
	    }

	    @Override
	    public String toString() {
	        return "Point{x=" + x + ", y=" + y + "}";
	    }
	}


    // Constructor to initialize components
    public mainCode(int regNum, int loadNum, int addNum, int mulNum, String instructionFilePath) {
    	writeBack= new ArrayList<>();
    	queue= new ArrayList<ReservationStation>();
    	// Initialize Execution Table
        table = new ExecutionTable();

        // Initialize Register File
        regFile = new RegFiles(regNum);

        // Initialize Load Buffers
        loadBuffers = new LoadBuffers(loadNum);

        // Initialize Reservation Stations
        addStations = new ReservationStations(addNum, "A"); // Add Reservation Stations (A1, A2, A3)
        mulStations = new ReservationStations(mulNum, "M"); // Multiply Reservation Stations (M1, M2)

        // Initialize Instruction Table and Execution Entries
        try {
            mipsReader parser = new mipsReader();
            List<Instruction> instructionTable = parser.parseFile(instructionFilePath);

            // Convert Instructions into Execution Entries and add to Execution Table
            List<ExecutionEntry> Execution = new ArrayList<>();
            for (Instruction instr : instructionTable) {
                Execution.add(new ExecutionEntry(instr));
            }  
            table.addEntries(Execution);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to update an entry in the Execution Table
    public void updateExecutionEntry(int index, Integer issue, Integer execStart, Integer execEnd, Integer writeBack) {
    	if (index < table.size()) {
    	    ExecutionEntry entry = table.get(index);
            if (issue != null) entry.setIssue(issue);
            if (execStart != null) entry.setExecutionStart(execStart);
            if (execEnd != null) entry.setExecutionEnd(execEnd);
            if (writeBack != null) entry.setWriteBack(writeBack);
        } else {
            System.out.println("Invalid Execution Table index: " + index);
        }
    }

    // Method to update a specific register's Qi field
    public void updateRegister(int registerIndex, String qi) {
        regFile.getRegister(registerIndex).setQi(qi);
    }

    // Method to update a Load Buffer
    public void updateLoadBuffer(int bufferIndex, boolean busy, String address) {
        if (bufferIndex < loadBuffers.size()) {
            loadBuffers.get(bufferIndex).setBusy(busy);
            loadBuffers.get(bufferIndex).setAddress(address);
        } else {
            System.out.println("Invalid Load Buffer index: " + bufferIndex);
        }
    }


    private boolean checkRegisterExists(String register) {
        try {
            // Extract the numeric part of the register (e.g., "1" from "R1")
            int registerIndex = Integer.parseInt(register.substring(1)) - 1;

            // Validate the register index is within bounds of RegFiles
            return registerIndex >= 0 && registerIndex < regFile.size()&&regFile.get(registerIndex).getQi()!=null;
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            // Handle invalid register format (e.g., "R" with no number or non-numeric input)
            System.out.println("Invalid register format: " + register);
            return false;
        }
    }

    public void updateReservationStation
    (String type, boolean busy, String op, String vj, String vk, String qj, String qk,
    		String a, Integer Time,Integer pos,int clockCycle) { 
    	Integer index = addStations.getFirstEmpty();//can be -1 if full
    	if(index==-1) {
    		ReservationStation r =new ReservationStation("Queue");
    		r.setAll( busy, op, vj, vk, qj, qk,a,Time,pos);
    		queue.add(r);
    	}
    	else if(!queue.isEmpty()) {
    		ReservationStation r = queue.get(0);
    		boolean qjFound = r.getQj() != null && checkRegisterExists(r.getQj()) && !r.getQj().equals(r.getA());
    	    boolean qkFound = r.getQk() != null && checkRegisterExists(r.getQk()) && !r.getQk().equals(r.getA());
    	    
    	    if(qjFound && qkFound) {     
        		r.setAll( r.getBusy(), null, null, r.getVk(),r.getQj(), r.getQk(),r.getA(),r.getTime(),r.getPos());
        		addStations.add(Integer.parseInt(r.getQj().substring(1))-1,new Point(index,"qj",r.getVj(),r.getOp()));
        		addStations.add(Integer.parseInt(r.getQk().substring(1))-1,new Point(index,"qk",r.getVk(),r.getOp()));
            	return;
    	    }
        	else if (qjFound || qkFound) {
        		if(qjFound) {
        			r.setAll( r.getBusy(), r.getOp(), null, r.getVk(),r.getQj(), null,r.getA(),r.getTime(),r.getPos());
        			addStations.add(Integer.parseInt(r.getQj().substring(1))-1,new Point(index,"qj",r.getVj(),r.getOp()));
        		}
        		if(qkFound) {
        			r.setAll( r.getBusy(), r.getOp(), r.getVj(), null, null, r.getQk(),r.getA(),r.getTime(),r.getPos());
        			addStations.add(Integer.parseInt(r.getQk().substring(1))-1,new Point(index,"qk",r.getVk(),r.getOp()));        			
        		}
        	}else {
        		r.setAll( r.getBusy(), r.getOp(), r.getVj(), r.getVk(), null, null,r.getA(),r.getTime(),r.getPos());
        	}
    	    table.setIssue(r.getPos(), clockCycle);
    	    addStations.setStation(index,r);
    	    updateRegister(Integer.parseInt(r.getA().substring(1))-1,"A"+(index+1));

    	    ReservationStation r2 =new ReservationStation("Queue");
    		r2.setAll( busy, op, vj, vk, qj, qk,a,Time,pos);
    		queue.remove(0);
    		if(clockCycle<=table.size())
    			queue.add(r2);
    	}else {
    		boolean qjFound = qj != null && checkRegisterExists(qj) && !qj.equals(a);
    	    boolean qkFound = qk != null && checkRegisterExists(qk) && !qk.equals(a);
    		if(qjFound && qkFound) {
            	addStations.get(index).setAll(busy, op, null, null,regFile.get(Integer.parseInt(qj.substring(1))-1).getQi(), regFile.get(Integer.parseInt(qk.substring(1))-1).getQi(),a, Time,pos);
            	addStations.add(Integer.parseInt(qj.substring(1))-1,new Point(index,"qj",vj,type));
            	addStations.add(Integer.parseInt(qk.substring(1))-1,new Point(index,"qk",vk,type));
            	return;
            }
            if (qjFound || qkFound) {
                // Both qj and qk found in RegFiles
            	if(qjFound) {
            		addStations.get(index).setAll(busy, op, null, vk, regFile.get(Integer.parseInt(qj.substring(1))-1).getQi(), null, a, Time,pos);
            		addStations.add(Integer.parseInt(qj.substring(1))-1,new Point(index,"qj",vj,type));            		
            	}
            	if(qkFound) {
            		addStations.get(index).setAll(busy, op, vj, null, null, regFile.get(Integer.parseInt(qk.substring(1))-1).getQi(), a, Time,pos);
            		addStations.add(Integer.parseInt(qk.substring(1))-1,new Point(index,"qk",vk,type));
            	}
            } else {
            	addStations.get(index).setAll(busy, op, vj, vk, qj, qk, a, Time,pos);
            }
            table.setIssue(pos, clockCycle);
            updateRegister(Integer.parseInt(a.substring(1))-1,"A"+(index+1));
            
    	}
    	
    }
    
    public void updateReservationStation
    (int clockCycle) { 
    	Integer index = addStations.getFirstEmpty();//can be -1 if full
    	
    	if(!queue.isEmpty()) {
    		ReservationStation r = queue.get(0);
    		boolean qjFound = r.getQj() != null && checkRegisterExists(r.getQj()) && !r.getQj().equals(r.getA());
    	    boolean qkFound = r.getQk() != null && checkRegisterExists(r.getQk()) && !r.getQk().equals(r.getA());
    	    
    	    if(qjFound && qkFound) {     
        		r.setAll( r.getBusy(), null, null, r.getVk(),r.getQj(), r.getQk(),r.getA(),r.getTime(),r.getPos());
        		addStations.add(Integer.parseInt(r.getQj().substring(1))-1,new Point(index,"qj",r.getVj(),r.getOp()));
        		addStations.add(Integer.parseInt(r.getQk().substring(1))-1,new Point(index,"qk",r.getVk(),r.getOp()));
            	return;
    	    }
        	else if (qjFound || qkFound) {
        		if(qjFound) {
        			r.setAll( r.getBusy(), r.getOp(), null, r.getVk(),r.getQj(), null,r.getA(),r.getTime(),r.getPos());
        			addStations.add(Integer.parseInt(r.getQj().substring(1))-1,new Point(index,"qj",r.getVj(),r.getOp()));
        		}
        		if(qkFound) {
        			r.setAll( r.getBusy(), r.getOp(), r.getVj(), null, null, r.getQk(),r.getA(),r.getTime(),r.getPos());
        			addStations.add(Integer.parseInt(r.getQk().substring(1))-1,new Point(index,"qk",r.getVk(),r.getOp()));        			
        		}
        	}else {
        		r.setAll( r.getBusy(), r.getOp(), r.getVj(), r.getVk(), null, null,r.getA(),r.getTime(),r.getPos());
        	}
    	    table.setIssue(r.getPos(), clockCycle);
    	    addStations.setStation(index,r);
    	    updateRegister(Integer.parseInt(r.getA().substring(1))-1,"A"+(index+1));

    		
    		queue.remove(0);
    		
    	}
    	
    }
    
    public void writeBack(int clockCycle) {
    	writeBack.sort(Comparator.comparingInt(Points::getX));
    	if(!writeBack.isEmpty()) {
    		List<Point> l= addStations.get(writeBack.get(0).y).getList();
    		System.out.println("this is why "+writeBack);
    		System.out.println("this is why "+addStations.get(writeBack.get(0).y).getPos());
    		table.setWriteBack(writeBack.get(0).x, clockCycle);
        	for(Point p:l) {
        		ReservationStations stations = p.getOp().equals("ADD") ? addStations : mulStations;
        		System.out.println("this is the p "+p);
        		if(p.getY().equals("qj"))
        			stations.setStationJ(p.getX(), p.getZ());
        		if(p.getY().equals("qk"))
        			stations.setStationK(p.getX(), p.getZ());
        	
        		if(stations.get(p.getX()).getVj()!=null&&stations.get(p.getX()).getVk()!=null) {
        			stations.get(p.getX()).setTime(2);
//        			System.out.println("this is the stations "+stations.get(p.getX()).getVj()+stations.get(p.getX()).getVk());
//        			continue;
                }
        	}
        	String register = addStations.get(writeBack.get(0).y).getA(); // Assuming this is "R1"
        	int value = Integer.parseInt(register.substring(1)); // Removes the first character ("R") and parses the rest

        	regFile.resetRow(value-1);
        	System.out.println("this is the x"+writeBack.get(0).x);
        	System.out.println("this is the y"+writeBack.get(0).y);
//        	if(writeBack.get(0).y<3)
        	addStations.get(writeBack.get(0).y).reset();
        	writeBack.remove(0);
    	}
    }
    
    public void subtractCycle(int clockCycle) {
    	
    	
        // Loop over addStations
    	    for (int i = 0; i < addStations.size(); i++) {
    	    	if(addStations.get(i).getBusy()==false) {
    	    		if(!queue.isEmpty()) {
    	    			updateReservationStation(clockCycle);
    	    			//call when issuing all
    	    		}
    	    	}
    	    	if(addStations.get(i).getBusy()==true){
    	    		ReservationStation station = addStations.get(i);
    		        Integer time = station.getTime();
    	    		if (time != null) {  
    		        	if(table.get(station.getPos()).getIssue()==null||time==2&&table.get(station.getPos()).getIssue()==clockCycle)//change i adjust time
    		        		continue;
    		        	
    		        	if(time == 2) {//adjust time 
    		        		table.setExecutionStart(station.getPos(), clockCycle);
    		        	}
    		        	if (time == 1) 
    		            	table.setExecutionEnd(station.getPos(), clockCycle);
    		            // Subtract 1 from time
    		        	
    		            if (time == 0) {
    		            	writeBack.add(new Points(station.getPos(),i));
    		            	station.setTime(-1);
//    		            	System.out.println("this is the x"+writeBack.get(0).x);
//    		            	System.out.println("this is the y"+writeBack.get(0).y);
//    		            	station.reset();
    		            	

    		            	//regFile.resetRow(Integer.parseInt(addStations.getStation(i).getA().substring(1))-1);
    		            	
    		            	// Time has reached zero, perform necessary actions (empty for now)
    		            }
    		            if(time!=0){
    		            	time = time - 1;
    		            	station.setTime(time);
    		            }
    		        }else {
    		        	if(station.getVj()!=null&&station.getVk()!=null) {
    	        			station.setTime(2);
    	        			System.out.println("this is thea "+clockCycle);
    	        			
    	                }
    		        	
    		        }
    	    	}
    	    	//and clockCycle > 11 && queue not empty => add it in the reservation table 
    	    	//if busy
    	    	//check time if can start or not 
    	    	//if start->clockCycle
    	    	//then time--
    	    }
    	    writeBack(clockCycle);
    	

    }
    
    
    
    // Method to print all tables
    public void printTables() {
//        System.out.println("Execution Table:");
          table.printTable();

//        System.out.println("\nRegister File:");
//        regFile.printAllRegisters();

//        System.out.println("\nLoad Buffers:");
//        loadBuffers.printAllBuffers();
//
        System.out.println("\nAdd Reservation Stations:");
        addStations.printAllStations();
//
//        System.out.println("\nMultiply Reservation Stations:");
//        mulStations.printAllStations();
    }




   
}
