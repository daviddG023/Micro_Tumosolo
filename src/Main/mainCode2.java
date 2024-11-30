package Main;

import RegFile.RegFiles;
import LoadBuffer.LoadBuffers;
import reservation_tables.*;
import reader.*;
import executionTable.*;
import java.util.ArrayList;
import java.util.Comparator;

import java.util.List;


public class mainCode2 {
    ExecutionTable table;
    RegFiles regFile;
    LoadBuffers loadBuffers;
    ReservationStations addStations;
    ReservationStations mulStations;
	ArrayList<Points> writeBack; 
	ArrayList<ReservationStation> queue;
	boolean ready= true;
	
	// Base Point Class

	public class Points {
	    protected int x;
	    protected int y;
	    protected String op;

	    // Constructor
	    public Points(int x, int y,String op) {
	        this.x = x;
	        this.y = y;
	        this.op = op;
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
	    public String getOp() {
	        return op;
	    }

	    public void setOp(String op) {
	        this.op = op;
	    }

	    @Override
	    public String toString() {
	        return "Point{x=" + x + ", y=" + y + ", op=" + op+ "}";
	    }
	}


    // Constructor to initialize components
    public mainCode2(int regNum, int loadNum, int addNum, int mulNum, String instructionFilePath) {
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
    	
    	
    	
    	if(!queue.isEmpty()) {
    		System.out.println("this is the queue"+queue);
    		ReservationStation r = queue.get(0);
    		
    		
    		ReservationStations Stations = r.getOp().equals("ADD")?addStations:mulStations;
        	
        	Integer index = Stations.getFirstEmpty();//can be -1 if full
    		if(index==-1) {
//        		ReservationStation r1 =new ReservationStation("Queue");
//        		r1.setAll( r.getBusy(), r.getOp(), r.getVj(), r.getVk(), r.getQj(), r.getQk(),r.getA(),r.getTime(),r.getPos());
//        		queue.add(r1);
        		ReservationStation r2 =new ReservationStation("Queue");
        		r2.setAll( busy, op, vj, vk, qj, qk,a,Time,pos);
        		queue.add(r2);
        		return;
        	}
    		ready =false;
    		boolean qjFound = r.getQj() != null && checkRegisterExists(r.getQj()) && !r.getQj().equals(r.getA());
    	    boolean qkFound = r.getQk() != null && checkRegisterExists(r.getQk()) && !r.getQk().equals(r.getA());
    	    
    	    if(qjFound && qkFound) {     
        		ReservationStations rr = regFile.get(Integer.parseInt(r.getQj().substring(1))-1).getQi().substring(0,1).equals("M")?mulStations:addStations;
        		ReservationStations rr2 = regFile.get(Integer.parseInt(r.getQk().substring(1))-1).getQi().substring(0,1).equals("M")?mulStations:addStations;
        		int indexQj = Integer.parseInt(regFile.get(Integer.parseInt(r.getQj().substring(1))-1).getQi().substring(1));
        		int indexQk = Integer.parseInt(regFile.get(Integer.parseInt(r.getQk().substring(1))-1).getQi().substring(1));
        		rr.add(indexQj-1,new Point(index,"qj",r.getVj(),r.getOp()));
        		rr2.add(indexQk-1,new Point(index,"qk",r.getVk(),r.getOp()));
        		r.setAll( r.getBusy(), r.getOp(), null,null,regFile.get(Integer.parseInt(r.getQj().substring(1))-1).getQi(), regFile.get(Integer.parseInt(r.getQk().substring(1))-1).getQi(),r.getA(),r.getTime(),r.getPos());
    	    }
        	else if (qjFound || qkFound) {
        		if(qjFound) {
        			ReservationStations rr = regFile.get(Integer.parseInt(r.getQj().substring(1))-1).getQi().substring(0,1).equals("M")?mulStations:addStations;
        			int indexQj = Integer.parseInt(regFile.get(Integer.parseInt(r.getQj().substring(1))-1).getQi().substring(1));
        			rr.add(indexQj-1,new Point(index,"qj",r.getVj(),r.getOp()));
        			r.setAll( r.getBusy(), r.getOp(), null, r.getVk(),r.getQj(), null,r.getA(),r.getTime(),r.getPos());
            		
        		}
        		if(qkFound) {
        			ReservationStations rr2 = regFile.get(Integer.parseInt(r.getQk().substring(1))-1).getQi().substring(0,1).equals("M")?mulStations:addStations;
            		int indexQk = Integer.parseInt(regFile.get(Integer.parseInt(r.getQk().substring(1))-1).getQi().substring(1));            		
            		rr2.add(indexQk-1,new Point(index,"qk",r.getVk(),r.getOp()));        			
            		r.setAll( r.getBusy(), r.getOp(), r.getVj(), null, null, r.getQk(),r.getA(),r.getTime(),r.getPos());
        		}
        	}else {
        		r.setAll( r.getBusy(), r.getOp(), r.getVj(), r.getVk(), null, null,r.getA(),r.getTime(),r.getPos());
        	}
    	    table.setIssue(r.getPos(), clockCycle);
    	    System.out.println(queue.get(0));
    	    Stations = queue.get(0).getOp().equals("ADD")?addStations:mulStations;
    	    System.out.println("this isss"+queue.get(0).getOp()+index);
    	    Stations.setStation(index,r);
    	    String s = queue.get(0).getOp().equals("ADD")?"A":"M";
    	    updateRegister(Integer.parseInt(r.getA().substring(1))-1,s+(index+1));
    	    queue.remove(0);

    	    ReservationStation r2 =new ReservationStation("Queue");
    		r2.setAll( busy, op, vj, vk, qj, qk,a,Time,pos);
    		queue.add(r2);
    	}else {
    		ReservationStations Stations = type.equals("ADD")?addStations:mulStations;
        	
        	Integer index = Stations.getFirstEmpty();//can be -1 if full
    		if(index==-1) {
        		ReservationStation r =new ReservationStation("Queue");
        		r.setAll( busy, op, vj, vk, qj, qk,a,Time,pos);
        		queue.add(r);
        		return;
        	}
    		ready =false;
    		boolean qjFound = qj != null && checkRegisterExists(qj) && !qj.equals(a);
    	    boolean qkFound = qk != null && checkRegisterExists(qk) && !qk.equals(a);
    		if(qjFound && qkFound) {
            	Stations.get(index).setAll(busy, op, null, null,regFile.get(Integer.parseInt(qj.substring(1))-1).getQi(), regFile.get(Integer.parseInt(qk.substring(1))-1).getQi(),a, Time,pos);
            	ReservationStations rr = regFile.get(Integer.parseInt(qj.substring(1))-1).getQi().substring(0,1).equals("M")?mulStations:addStations;
            	ReservationStations rr2 = regFile.get(Integer.parseInt(qk.substring(1))-1).getQi().substring(0,1).equals("M")?mulStations:addStations;
            	int indexQj = Integer.parseInt(regFile.get(Integer.parseInt(qj.substring(1))-1).getQi().substring(1));
        		int indexQk = Integer.parseInt(regFile.get(Integer.parseInt(qk.substring(1))-1).getQi().substring(1));
        		rr.add(indexQj-1,new Point(index,"qj",vj,op));
        		rr2.add(indexQk-1,new Point(index,"qk",vk,op));
            	
            }
    		else if (qjFound || qkFound) {
                // Both qj and qk found in RegFiles
            	if(qjFound) {
            		Stations.get(index).setAll(busy, op, null, vk, regFile.get(Integer.parseInt(qj.substring(1))-1).getQi(), null, a, Time,pos);
            		ReservationStations rr = regFile.get(Integer.parseInt(qj.substring(1))-1).getQi().substring(0,1).equals("M")?mulStations:addStations;
            		int indexQj = Integer.parseInt(regFile.get(Integer.parseInt(qj.substring(1))-1).getQi().substring(1));
            		rr.add(indexQj-1,new Point(index,"qj",vj,op));            		
            	}
            	if(qkFound) {
            		Stations.get(index).setAll(busy, op, vj, null, null, regFile.get(Integer.parseInt(qk.substring(1))-1).getQi(), a, Time,pos);
            		ReservationStations rr2 = regFile.get(Integer.parseInt(qk.substring(1))-1).getQi().substring(0,1).equals("M")?mulStations:addStations;
            		int indexQk = Integer.parseInt(regFile.get(Integer.parseInt(qk.substring(1))-1).getQi().substring(1));
            		rr2.add(indexQk-1,new Point(index,"qk",vk,op));
            	}
            } else {
            	Stations.get(index).setAll(busy, op, vj, vk, qj, qk, a, Time,pos);
            }
            table.setIssue(pos, clockCycle);
            String s = type.equals("ADD")?"A":"M";
            
            updateRegister(Integer.parseInt(a.substring(1))-1,s+(index+1));
            regFile.printAllRegisters();
            
    	}
    	
    }
    
    public void updateReservationStation
    (int clockCycle,String type) { 
    	ReservationStations Stations = type.equals("ADD")?addStations:mulStations;
    	Integer index = Stations.getFirstEmpty();//can be -1 if full
    	System.out.println("index in UPD2 "+index);
    	
    	if(!queue.isEmpty()) {
    		ReservationStation r = queue.get(0);
    		boolean qjFound = r.getQj() != null && checkRegisterExists(r.getQj()) && !r.getQj().equals(r.getA());
    	    boolean qkFound = r.getQk() != null && checkRegisterExists(r.getQk()) && !r.getQk().equals(r.getA());

    	    if(qjFound && qkFound) {    
    	    	//this is the dependency error we are searching for qJ-1 in the stations rather than reg file then finding the index station
        		ReservationStations rr = regFile.get(Integer.parseInt(r.getQj().substring(1))-1).getQi().substring(0,1).equals("M")?mulStations:addStations;
        		ReservationStations rr2 = regFile.get(Integer.parseInt(r.getQk().substring(1))-1).getQi().substring(0,1).equals("M")?mulStations:addStations; 
        		//biko best
        		int indexQj = Integer.parseInt(regFile.get(Integer.parseInt(r.getQj().substring(1))-1).getQi().substring(1));
        		int indexQk = Integer.parseInt(regFile.get(Integer.parseInt(r.getQk().substring(1))-1).getQi().substring(1));
        		rr.add(indexQj-1,new Point(index,"qj",r.getVj(),r.getOp()));
        		rr2.add(indexQk-1,new Point(index,"qk",r.getVk(),r.getOp()));
        		r.setAll( r.getBusy(), null, null, r.getVk(),r.getQj(), r.getQk(),r.getA(),r.getTime(),r.getPos());
        		
    	    }
        	else if (qjFound || qkFound) {
        		if(qjFound) {
        			ReservationStations rr = regFile.get(Integer.parseInt(r.getQj().substring(1))-1).getQi().substring(0,1).equals("M")?mulStations:addStations;
        			int indexQj = Integer.parseInt(regFile.get(Integer.parseInt(r.getQj().substring(1))-1).getQi().substring(1));
        			rr.add(indexQj-1,new Point(index,"qj",r.getVj(),r.getOp()));
        			r.setAll( r.getBusy(), r.getOp(), null, r.getVk(),r.getQj(), null,r.getA(),r.getTime(),r.getPos());
        		}
        		if(qkFound) {
        			ReservationStations rr2 = regFile.get(Integer.parseInt(r.getQk().substring(1))-1).getQi().substring(0,1).equals("M")?mulStations:addStations;
        			int indexQk = Integer.parseInt(regFile.get(Integer.parseInt(r.getQk().substring(1))-1).getQi().substring(1));
        			rr2.add(indexQk-1,new Point(index,"qk",r.getVk(),r.getOp()));        			
        			r.setAll( r.getBusy(), r.getOp(), r.getVj(), null, null, r.getQk(),r.getA(),r.getTime(),r.getPos());
        		}
        	}else {
        		r.setAll( r.getBusy(), r.getOp(), r.getVj(), r.getVk(), null, null,r.getA(),r.getTime(),r.getPos());
        	}
    	    table.setIssue(r.getPos(), clockCycle);
    	    Stations = r.getOp().equals("ADD")?addStations:mulStations;
    	    Stations.setStation(index,r);
    	    String s = queue.get(0).getOp().equals("ADD")?"A":"M";
    	    updateRegister(Integer.parseInt(r.getA().substring(1))-1,s+(index+1));

    		
    	    queue.remove(0);
    	    ready =false;
    	}
    	
    }
    
    public void writeBack(int clockCycle) {
    	System.out.println("hala waala");
    	System.out.println(writeBack);
    	writeBack.sort(Comparator.comparingInt(Points::getX));
    	if(!writeBack.isEmpty()) {
    		String type = writeBack.get(0).op;
        	ReservationStations Stations = type.equals("ADD")?addStations:mulStations;
    		List<Point> l= Stations.get(writeBack.get(0).y).getList();
    		System.out.println("this is why "+writeBack);
    		System.out.println("this is why "+Stations.get(writeBack.get(0).y).getPos());
    		table.setWriteBack(writeBack.get(0).x, clockCycle);
        	for(Point p:l) {
        		ReservationStations stations = p.getOp().equals("ADD") ? addStations : mulStations;
        		System.out.println("this is the p "+p);
        		if(p.getY().equals("qj"))
        			stations.setStationJ(p.getX(), p.getZ());
        		if(p.getY().equals("qk"))
        			stations.setStationK(p.getX(), p.getZ());
        	
        		if(stations.get(p.getX()).getVj()!=null&&stations.get(p.getX()).getVk()!=null) {
        			stations.get(p.getX()).setTime(2);//adjust hasab el process
                }
        	}
        	String register = Stations.get(writeBack.get(0).y).getA(); // Assuming this is "R1"
        	int value = Integer.parseInt(register.substring(1)); // Removes the first character ("R") and parses the rest

        	regFile.resetRow(value-1);
        	System.out.println("this is the x"+writeBack.get(0).x);
        	System.out.println("this is the y"+writeBack.get(0).y);
//        	if(writeBack.get(0).y<3)
        	Stations.get(writeBack.get(0).y).reset();
        	writeBack.remove(0);
    	}
    }
    
    public void subtractCycle(int clockCycle) {
    	System.out.println("this is the whole queue"+queue.size());
    	System.out.println("this is the whole queue"+queue);
        // Loop over addStations
    	    for (int i = 0; i < addStations.size(); i++) {
    	    	if(addStations.get(i).getBusy()==false) {
    	    		if(!queue.isEmpty()&&queue.get(0).getOp().equals("ADD")&& ready==true) {
    	    			System.out.println("this is the queue "+queue);
    	    			updateReservationStation(clockCycle,"ADD");
    	    			ready=false;
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
    		            	writeBack.add(new Points(station.getPos(),i,station.getOp()));
    		            	station.setTime(-1);
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
    	    }
    	    // Loop over mulStations
    	    for (int i = 0; i < mulStations.size(); i++) {
    	    	if(mulStations.get(i).getBusy()==false) {
    	    		if(!queue.isEmpty()&&queue.get(0).getOp().equals("MUL")&& ready==true) {
    	    			updateReservationStation(clockCycle,"MUL");
    	    			ready=false;
    	    			//call when issuing all
    	    		}
    	    	}
    	    	if(mulStations.get(i).getBusy()==true){
    	    		ReservationStation stations = mulStations.get(i);
    		        Integer time = stations.getTime();
    	    		if (time != null) {  
    		        	if(table.get(stations.getPos()).getIssue()==null||time==2&&table.get(stations.getPos()).getIssue()==clockCycle)//change i adjust time
    		        		continue;
    		        	
    		        	if(time == 2) {//adjust time 
    		        		table.setExecutionStart(stations.getPos(), clockCycle);
    		        	}
    		        	if (time == 1) 
    		            	table.setExecutionEnd(stations.getPos(), clockCycle);
    		            // Subtract 1 from time
    		        	
    		            if (time == 0) {
    		            	writeBack.add(new Points(stations.getPos(),i,stations.getOp()));
    		            	stations.setTime(-1);
    		            }
    		            if(time!=0){
    		            	time = time - 1;
    		            	stations.setTime(time);
    		            }
    		        }else {
    		        	if(stations.getVj()!=null&&stations.getVk()!=null) {
    	        			stations.setTime(2);
    	        			System.out.println("this is thea "+clockCycle);
    	        			
    	                }
    		        	
    		        }
    	    	}
    	    }
    	    writeBack(clockCycle);
    	    ready=true;
    	

    }
    
    
    
    // Method to print all tables
    public void printTables() {
//        System.out.println("Execution Table:");
          table.printTable();

//        System.out.println("\nRegister File:");
        regFile.printAllRegisters();

//        System.out.println("\nLoad Buffers:");
//        loadBuffers.printAllBuffers();
//
        System.out.println("\nAdd Reservation Stations:");
        addStations.printAllStations();
//
        System.out.println("\nMultiply Reservation Stations:");
        mulStations.printAllStations();
    }




   
}
