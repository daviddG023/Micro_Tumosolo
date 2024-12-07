package Main;

import RegFile.RegFiles;
import StoreBuffer.StoreBuffer;
import StoreBuffer.StoreBuffers;
import LoadBuffer.LoadBuffer;
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
    StoreBuffers storeBuffers;
    ReservationStations addStations;
    ReservationStations mulStations;
	ArrayList<Points> writeBack; 
	ArrayList<Object> queue;
	boolean ready= true;
	
	// Base Point Class

	public class Points {
	    protected int x;	//pos of the table
	    protected int y;	//pos of i
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
    public mainCode2(int regNum, int loadNum, int storeNum,int addNum, int mulNum, String instructionFilePath) {
    	writeBack= new ArrayList<>();
    	queue= new ArrayList<>();

    	// Initialize Execution Table
        table = new ExecutionTable();

        // Initialize Register File
        regFile = new RegFiles(regNum);

        // Initialize Load Buffers
        loadBuffers = new LoadBuffers(loadNum);
        storeBuffers = new StoreBuffers(storeNum);

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
    		
    		if (queue.get(0) instanceof LoadBuffer) {
    			System.out.println("this is the queue"+queue);
    			LoadBuffer r =(LoadBuffer) queue.get(0);
    			LoadBuffers Stations = loadBuffers;
            	
            	Integer index = Stations.getFirstEmpty();//can be -1 if full
            	System.out.println("this is the index"+index);
    			if(index==-1) {
    				if(op.equals("LD")) {
        				LoadBuffer r2 =new LoadBuffer("Load");
        				r2.setAll(busy, a, Time, pos,op); //vK will be its address
                		queue.add(r2);
    				}else if(op.equals("ADD")||op.equals("MUL")) {
    					ReservationStation r2 =new ReservationStation(op);
    	        		r2.setAll( busy, op, vj, vk, qj, qk,a,Time,pos);
    	        		queue.add(r2);
    				}else if (op.equals("SD")) {
    	    		        StoreBuffer r2 = new StoreBuffer("Store");
    	    		        r2.setAll(busy, a, vj, qj, Time, pos, op); // `vj` is the value, `qj` is the dependency
    	    		        queue.add(r2);//add then then other store and normal     			
    				}
    				return;
            	}
    			ready =false;
    			updateRegister(Integer.parseInt(r.getA().substring(1))-1,"L"+(index+1));//R5
    			Stations.get(index).setAll(r.getBusy(), r.getA(), r.getTime(), r.getPos(),r.getOp()); //a will be its address
    			
    			queue.remove(0);
    			table.setIssue(r.getPos(), clockCycle);
    			if(op.equals("LD")) {
    				LoadBuffer r2 =new LoadBuffer("Load");
    				r2.setAll(busy, a, Time, pos,op); //vK will be its address
            		queue.add(r2);
				}else if(op.equals("ADD")||op.equals("MUL")) {
					ReservationStation r2 =new ReservationStation(op);
	        		r2.setAll( busy, op, vj, vk, qj, qk,a,Time,pos);
	        		queue.add(r2);
				}else if (op.equals("SD")) {
	    		        StoreBuffer r2 = new StoreBuffer("Store");
	    		        r2.setAll(busy, a, vj, qj, Time, pos, op); // `vj` is the value, `qj` is the dependency
	    		        queue.add(r2);//add then then other store and normal     			
				}
    			return;
    		}
    		if (queue.get(0) instanceof StoreBuffer) {
    		    System.out.println("this is the queue: " + queue);
    		    StoreBuffer r = (StoreBuffer) queue.get(0); // Cast the object from the queue
    		    StoreBuffers Stations = storeBuffers;

    		    Integer index = Stations.getFirstEmpty(); // Get the first available StoreBuffer index
    		    System.out.println("this is the index: " + index);

    		    if (index == -1) {
    		        // If no StoreBuffer is available, re-add the instruction to the queue
    		        if (op.equals("LD")) {
    		            LoadBuffer r2 = new LoadBuffer("Load");
    		            r2.setAll(busy, a, Time, pos, op); // vK will be its address
    		            queue.add(r2);
    		        } else if (op.equals("ADD") || op.equals("MUL")) {
    		            ReservationStation r2 = new ReservationStation(op);
    		            r2.setAll(busy, op, vj, vk, qj, qk, a, Time, pos);
    		            queue.add(r2);
    		        } else if (op.equals("SD")) {
    		            StoreBuffer r2 = new StoreBuffer("Store");
    		            r2.setAll(busy, a, vj, qj, Time, pos, op); // `vj` is the value, `qj` is the dependency
    		            queue.add(r2); // Add the instruction back to the queue
    		        }
    		        return;
    		    }

    		    ready = false; // Mark as not ready to avoid multiple issues

    		    // Check for dependencies
    		    boolean qjFound = r.getQ() != null 
    		                      && checkRegisterExists(r.getQ()) 
    		                      && !regFile.get(Integer.parseInt(r.getQ().substring(1)) - 1).getQi().equals("S" + (index + 1));
    		    System.out.println("Dependency (qjFound): " + qjFound);

    		    if (qjFound) {
    		        // Set dependency for StoreBuffer
    		        Stations.get(index).setAll(r.getBusy(), r.getA(), null, regFile.get(Integer.parseInt(r.getQ().substring(1)) - 1).getQi(), r.getTime(), r.getPos(), r.getOp());

    		        // Determine if the dependency comes from a LoadBuffer or ReservationStation
    		        ReservationStations rr = null;
    		        LoadBuffers loadBufferForQ = null;

    		        if (regFile.get(Integer.parseInt(r.getQ().substring(1)) - 1).getQi().substring(0, 1).equals("M")) {
    		            rr = mulStations;
    		        } else if (regFile.get(Integer.parseInt(r.getQ().substring(1)) - 1).getQi().substring(0, 1).equals("L")) {
    		            loadBufferForQ = loadBuffers;
    		        } else {
    		            rr = addStations; // Dependency on ADD
    		        }

    		        int indexQ = Integer.parseInt(regFile.get(Integer.parseInt(r.getQ().substring(1)) - 1).getQi().substring(1));
    		        if (loadBufferForQ != null) {
    		            loadBufferForQ.add(indexQ - 1, new Point(index, "qS", null, r.getOp()));
    		        } else if (rr != null) {
    		            rr.add(indexQ - 1, new Point(index, "qS", null, r.getOp()));
    		        }
    		    } else {
    		        // If no dependency is found, set the StoreBuffer with the provided values
    		        Stations.get(index).setAll(r.getBusy(), r.getA(), r.getV(), r.getQ(), r.getTime(), r.getPos(), r.getOp());
    		    }

    		    queue.remove(0); // Remove the processed StoreBuffer from the queue
    		    table.setIssue(r.getPos(), clockCycle); // Mark the issue time in the execution table

    		    // Add a new instruction to the queue based on its operation type
    		    if (op.equals("LD")) {
    		        LoadBuffer r2 = new LoadBuffer("Load");
    		        r2.setAll(busy, a, Time, pos, op); // vK will be its address
    		        queue.add(r2);
    		    } else if (op.equals("ADD") || op.equals("MUL")) {
    		        ReservationStation r2 = new ReservationStation(op);
    		        r2.setAll(busy, op, vj, vk, qj, qk, a, Time, pos);
    		        queue.add(r2);
    		    } else if (op.equals("SD")) {
    		        StoreBuffer r2 = new StoreBuffer("Store");
    		        r2.setAll(busy, a, vj, qj, Time, pos, op); // `vj` is the value, `qj` is the dependency
    		        queue.add(r2); // Add the instruction back to the queue
    		    }
    		    return;
    		}


    		ReservationStation r =(ReservationStation) queue.get(0);
    		
    		
    		ReservationStations Stations = r.getOp().equals("ADD")?addStations:mulStations;
        	
        	Integer index = Stations.getFirstEmpty();//can be -1 if full
    		if(index==-1) {
    			if(op.equals("LD")) {
    				LoadBuffer r2 =new LoadBuffer("Load");
    				r2.setAll(busy, a, Time, pos,op); //vK will be its address
            		queue.add(r2);
				}else if(op.equals("ADD")||op.equals("MUL")) {
					ReservationStation r2 =new ReservationStation(op);
	        		r2.setAll( busy, op, vj, vk, qj, qk,a,Time,pos);
	        		queue.add(r2);
				}else if (op.equals("SD")) {
	    		        StoreBuffer r2 = new StoreBuffer("Store");
	    		        r2.setAll(busy, a, vj, qj, Time, pos, op); // `vj` is the value, `qj` is the dependency
	    		        queue.add(r2);//add then then other store and normal     			
				} 
        		return;
        	}
    		ready =false;
    		// i want to remove last condition and add on that checks if the RegFile.qi!=to current station
    		String s = r.getOp().equals("ADD")?"A":r.getOp().equals("MUL")?"M":"L";
    		boolean qjFound = r.getQj() != null && checkRegisterExists(r.getQj()) && /*!r.getQj().equals(r.getA())*/!regFile.get(Integer.parseInt(r.getQj().substring(1))-1).getQi().equals(s+index+1);
    	    boolean qkFound = r.getQk() != null && checkRegisterExists(r.getQk()) && /*!r.getQk().equals(r.getA())*/!regFile.get(Integer.parseInt(r.getQj().substring(1))-1).getQi().equals(s+index+1);
    	    
    	    if(qjFound && qkFound) {     
    	    	ReservationStations rr = null, rr2 = null;
    	    	LoadBuffers loadBufferForQj = null, loadBufferForQk = null;

    	    	// Determine the types for qj and qk
    	    	String qiQj = regFile.get(Integer.parseInt(r.getQj().substring(1)) - 1).getQi().substring(0, 1);
    	    	String qiQk = regFile.get(Integer.parseInt(r.getQk().substring(1)) - 1).getQi().substring(0, 1);

    	    	rr = qiQj.equals("M") ? mulStations : qiQj.equals("A") ? addStations : null;
    	    	loadBufferForQj = qiQj.equals("L") ? loadBuffers : null;

    	    	rr2 = qiQk.equals("M") ? mulStations : qiQk.equals("A") ? addStations : null;
    	    	loadBufferForQk = qiQk.equals("L") ? loadBuffers : null;

    	    	// Parse indices
    	    	int indexQj = Integer.parseInt(regFile.get(Integer.parseInt(r.getQj().substring(1)) - 1).getQi().substring(1));
    	    	int indexQk = Integer.parseInt(regFile.get(Integer.parseInt(r.getQk().substring(1)) - 1).getQi().substring(1));

    	    	// Add dependencies
    	    	if (loadBufferForQj != null) loadBufferForQj.add(indexQj - 1, new Point(index, "qj", r.getVj(), r.getOp()));
    	    	else if (rr != null) rr.add(indexQj - 1, new Point(index, "qj", r.getVj(), r.getOp()));

    	    	if (loadBufferForQk != null) loadBufferForQk.add(indexQk - 1, new Point(index, "qk", r.getVk(), r.getOp()));
    	    	else if (rr2 != null) rr2.add(indexQk - 1, new Point(index, "qk", r.getVk(), r.getOp()));
        		r.setAll( r.getBusy(), r.getOp(), null,null,regFile.get(Integer.parseInt(r.getQj().substring(1))-1).getQi(), regFile.get(Integer.parseInt(r.getQk().substring(1))-1).getQi(),r.getA(),r.getTime(),r.getPos());
    	    }
        	else if (qjFound || qkFound) {
        		if(qjFound) {
        			String qiQj = regFile.get(Integer.parseInt(r.getQj().substring(1)) - 1).getQi().substring(0, 1);
        			int indexQj = Integer.parseInt(regFile.get(Integer.parseInt(r.getQj().substring(1)) - 1).getQi().substring(1));

        			// Determine if qj points to a LoadBuffer or ReservationStation
        			if (qiQj.equals("L")) {
        			    loadBuffers.add(indexQj - 1, new Point(index, "qj", r.getVj(), r.getOp()));
        			} else {
        			    ReservationStations rr = qiQj.equals("M") ? mulStations : addStations;
        			    rr.add(indexQj - 1, new Point(index, "qj", r.getVj(), r.getOp()));
        			}

        			r.setAll( r.getBusy(), r.getOp(), null, r.getVk(),r.getQj(), null,r.getA(),r.getTime(),r.getPos());
            		
        		}
        		if(qkFound) {
        			int indexQk = Integer.parseInt(regFile.get(Integer.parseInt(r.getQk().substring(1)) - 1).getQi().substring(1));

        			if (regFile.get(Integer.parseInt(r.getQk().substring(1)) - 1).getQi().startsWith("L")) {
        			    loadBuffers.add(indexQk - 1, new Point(index, "qk", r.getVk(), r.getOp()));
        			} else if (regFile.get(Integer.parseInt(r.getQk().substring(1)) - 1).getQi().startsWith("M")) {
        			    mulStations.add(indexQk - 1, new Point(index, "qk", r.getVk(), r.getOp()));
        			} else {
        			    addStations.add(indexQk - 1, new Point(index, "qk", r.getVk(), r.getOp()));
        			}
   			
            		r.setAll( r.getBusy(), r.getOp(), r.getVj(), null, null, r.getQk(),r.getA(),r.getTime(),r.getPos());
        		}
        	}else {
        		r.setAll( r.getBusy(), r.getOp(), r.getVj(), r.getVk(), null, null,r.getA(),r.getTime(),r.getPos());
        	}
    	    table.setIssue(r.getPos(), clockCycle);
    	    System.out.println(queue.get(0));
    	    Stations = r.getOp().equals("ADD")?addStations:mulStations;
    	    System.out.println("this isss"+r.getOp()+index);
    	    Stations.setStation(index,r);
    	    s = r.getOp().equals("ADD")?"A":"M";
    	    updateRegister(Integer.parseInt(r.getA().substring(1))-1,s+(index+1));
    	    queue.remove(0);

    	    if(op.equals("LD")) {
				LoadBuffer r2 =new LoadBuffer("Load");
				r2.setAll(busy, a, Time, pos,op); //vK will be its address
        		queue.add(r2);
			}else if(op.equals("ADD")||op.equals("MUL")) {
				ReservationStation r2 =new ReservationStation(op);
        		r2.setAll( busy, op, vj, vk, qj, qk,a,Time,pos);
        		queue.add(r2);
			}else if (op.equals("SD")) {
    		        StoreBuffer r2 = new StoreBuffer("Store");
    		        r2.setAll(busy, a, vj, qj, Time, pos, op); // `vj` is the value, `qj` is the dependency
    		        queue.add(r2);//add then then other store and normal     			
			}
    	}else {
    		if(op.equals("LD")) {
    			LoadBuffers Stations = loadBuffers;
            	
            	Integer index = Stations.getFirstEmpty();//can be -1 if full
    			if(index==-1) {
	    			LoadBuffer r2 =new LoadBuffer("Load");
	    			r2.setAll(busy, a, Time, pos,op); //a will be its address
	    			queue.add(r2);
    				return;
            	}
    			ready =false;
    			table.setIssue(pos, clockCycle);
    			updateRegister(Integer.parseInt(a.substring(1))-1,"L"+(index+1));//R5
    			Stations.get(index).setAll(busy, a, Time, pos,op); //a will be its address
    			return;
    		}
    		if (op.equals("SD")) {
    		    StoreBuffers Stations = storeBuffers;

    		    Integer index = Stations.getFirstEmpty(); // Get the first available StoreBuffer index
    		    if (index == -1) {
    		        // If no StoreBuffer is available, add the instruction to the queue
    		        StoreBuffer r2 = new StoreBuffer("Store");
    		        r2.setAll(busy, a, vj, qj, Time, pos, op); // `a` is its address
    		        queue.add(r2);
    		        return;
    		    }

    		    ready = false;
    		    table.setIssue(pos, clockCycle); // Set issue time in the execution table

    		    boolean qjFound = qj != null && checkRegisterExists(qj)&&!regFile.get(Integer.parseInt(qj.substring(1)) - 1).getQi().equals("S" + (index + 1));//changed this line
    		    System.out.println("this is the qj"+qjFound);

    		    if (qjFound) {
    		        Stations.get(index).setAll(busy, a, null, regFile.get(Integer.parseInt(qj.substring(1)) - 1).getQi(), Time, pos, op);

    		        // Determine if `qj` refers to a `ReservationStation` or `LoadBuffers`
    		        ReservationStations rr = null;
    		        LoadBuffers loadBufferForQj = null;

    		        if (regFile.get(Integer.parseInt(qj.substring(1)) - 1).getQi().substring(0, 1).equals("M"))rr = mulStations;
    		        else if (regFile.get(Integer.parseInt(qj.substring(1)) - 1).getQi().substring(0, 1).equals("L")) loadBufferForQj = loadBuffers; 
    		        else rr = addStations; // Dependency on ADD
    		        int indexQj = Integer.parseInt(regFile.get(Integer.parseInt(qj.substring(1)) - 1).getQi().substring(1));
    		        if (loadBufferForQj != null) loadBufferForQj.add(indexQj - 1, new Point(index, "qS", vj, op));
    		        else if (rr != null)rr.add(indexQj - 1, new Point(index, "qS", vj, op));
    		        
    		    } else {
    		        Stations.get(index).setAll(busy, a, vj, qj, Time, pos, op); // Set directly if no dependencies
    		    }
    		    return;
    		}


    		ReservationStations Stations = type.equals("ADD")?addStations:mulStations;
        	
        	Integer index = Stations.getFirstEmpty();//can be -1 if full
    		if(index==-1) {
        		ReservationStation r =new ReservationStation("Queue");
        		r.setAll( busy, op, vj, vk, qj, qk,a,Time,pos);
        		queue.add(r);
        		return;
        	}
    		ready =false;
    		String s = op.equals("ADD")?"A":op.equals("MUL")?"M":"L";
    		boolean qjFound = qj != null && checkRegisterExists(qj) && /*!qj.equals(a)*/!regFile.get(Integer.parseInt(qj.substring(1)) - 1).getQi().equals(s+index+1);
    	    boolean qkFound = qk != null && checkRegisterExists(qk) && /*!qk.equals(a)*/!regFile.get(Integer.parseInt(qk.substring(1)) - 1).getQi().equals(s+index+1);
    		if(qjFound && qkFound) {
            	Stations.get(index).setAll(busy, op, null, null,regFile.get(Integer.parseInt(qj.substring(1))-1).getQi(), regFile.get(Integer.parseInt(qk.substring(1))-1).getQi(),a, Time,pos);
            	ReservationStations rr = null;
            	ReservationStations rr2 = null;
            	LoadBuffers loadBufferForQj = null;
            	LoadBuffers loadBufferForQk = null;

            	if (regFile.get(Integer.parseInt(qj.substring(1)) - 1).getQi().substring(0, 1).equals("M")) rr = mulStations;
            	else if (regFile.get(Integer.parseInt(qj.substring(1)) - 1).getQi().substring(0, 1).equals("L")) loadBufferForQj = loadBuffers; // Use LoadBuffers for 'L' type
            	else rr = addStations;
            	if (regFile.get(Integer.parseInt(qk.substring(1)) - 1).getQi().substring(0, 1).equals("M")) rr2 = mulStations;
            	else if (regFile.get(Integer.parseInt(qk.substring(1)) - 1).getQi().substring(0, 1).equals("L")) loadBufferForQk = loadBuffers; // Use LoadBuffers for 'L' type
            	else rr2 = addStations;
            	int indexQj = Integer.parseInt(regFile.get(Integer.parseInt(qj.substring(1)) - 1).getQi().substring(1));
            	int indexQk = Integer.parseInt(regFile.get(Integer.parseInt(qk.substring(1)) - 1).getQi().substring(1));
            	if (loadBufferForQj != null) loadBufferForQj.add(indexQj - 1, new Point(index, "qj", vj, op)); // Custom logic for LoadBuffers
            	else if (rr != null) rr.add(indexQj - 1, new Point(index, "qj", vj, op));
            	if (loadBufferForQk != null) loadBufferForQk.add(indexQk - 1, new Point(index, "qk", vk, op)); // Custom logic for LoadBuffers
            	else if (rr2 != null) rr2.add(indexQk - 1, new Point(index, "qk", vk, op));
            	
            	
            }
    		else if (qjFound || qkFound) {
                // Both qj and qk found in RegFiles
            	if(qjFound) {
            		Stations.get(index).setAll(busy, op, null, vk, regFile.get(Integer.parseInt(qj.substring(1))-1).getQi(), null, a, Time,pos);
            		ReservationStations rr = null;
            		LoadBuffers loadBufferForQj = null;
            		if (regFile.get(Integer.parseInt(qj.substring(1)) - 1).getQi().substring(0, 1).equals("M")) {
            		    rr = mulStations;
            		} else if (regFile.get(Integer.parseInt(qj.substring(1)) - 1).getQi().substring(0, 1).equals("L")) {
            		    loadBufferForQj = loadBuffers; // Use LoadBuffers for 'L' type
            		} else {
            		    rr = addStations;
            		}
            		int indexQj = Integer.parseInt(regFile.get(Integer.parseInt(qj.substring(1)) - 1).getQi().substring(1));

            		if (loadBufferForQj != null) {
            		    loadBufferForQj.add(indexQj - 1, new Point(index, "qj", vj, op)); // Custom logic for LoadBuffers
            		} else if (rr != null) {
            		    rr.add(indexQj - 1, new Point(index, "qj", vj, op));
            		}          		
            	}
            	if(qkFound) {
            		Stations.get(index).setAll(busy, op, vj, null, null, regFile.get(Integer.parseInt(qk.substring(1))-1).getQi(), a, Time,pos);
            		ReservationStations rr2 = null;
            		LoadBuffers loadBufferForQk = null;

            		// Check if Qk refers to a `LoadBuffers` or a `ReservationStations`
            		if (regFile.get(Integer.parseInt(qk.substring(1)) - 1).getQi().substring(0, 1).equals("M")) {
            		    rr2 = mulStations;
            		} else if (regFile.get(Integer.parseInt(qk.substring(1)) - 1).getQi().substring(0, 1).equals("L")) {
            		    loadBufferForQk = loadBuffers; // Use LoadBuffers for 'L' type
            		} else {
            		    rr2 = addStations;
            		}

            		// Parse the index for Qk
            		int indexQk = Integer.parseInt(regFile.get(Integer.parseInt(qk.substring(1)) - 1).getQi().substring(1));

            		// Add Point to the appropriate station or buffer
            		if (loadBufferForQk != null) {
            		    loadBufferForQk.add(indexQk - 1, new Point(index, "qk", vk, op)); // Custom logic for LoadBuffers
            		} else if (rr2 != null) {
            		    rr2.add(indexQk - 1, new Point(index, "qk", vk, op));
            		}

            	}
            } else {
            	Stations.get(index).setAll(busy, op, vj, vk, qj, qk, a, Time,pos);//can change qj and qk with null
            }
            table.setIssue(pos, clockCycle);
            s = type.equals("ADD")?"A":"M";
            
            updateRegister(Integer.parseInt(a.substring(1))-1,s+(index+1));
            regFile.printAllRegisters();
            
    	}
    	
    }
    
    public void updateReservationStation
    (int clockCycle,String type) { 
    	
    	if(!queue.isEmpty()) {
    		if (queue.get(0) instanceof LoadBuffer) {
    			LoadBuffer r =(LoadBuffer) queue.get(0);
    			LoadBuffers Stations = loadBuffers;
            	
            	Integer index = Stations.getFirstEmpty();//can be -1 if full
    			if(index==-1) {
//	    			LoadBuffer r2 =new LoadBuffer("Load");
//	    			r2.setAll(r.getBusy(), r.getA(), r.getTime(), r.getPos(),r.getOp()); //vK will be its address
//	    			queue.add(r2);
    				return;
            	}
    			ready =false;
    			Stations.get(index).setAll(r.getBusy(), r.getA(), r.getTime(), r.getPos(), r.getOp());
    			updateRegister(Integer.parseInt(r.getA().substring(1))-1,"L"+(index+1));//R5 
    			queue.remove(0);
    			table.setIssue(r.getPos(), clockCycle);
    			return;
    		}
    		if (queue.get(0) instanceof StoreBuffer) {
    		    System.out.println("this is the queue: " + queue);
    		    StoreBuffer r = (StoreBuffer) queue.get(0); // Cast the object from the queue
    		    StoreBuffers Stations = storeBuffers;

    		    Integer index = Stations.getFirstEmpty(); // Get the first available StoreBuffer index
    		    System.out.println("this is the index: " + index);

    		    if (index == -1) {
    		        // If no StoreBuffer is available, re-add the instruction to the queue
//    		        StoreBuffer r2 = new StoreBuffer("Store");
//    		        r2.setAll(r.getBusy(), r.getA(), r.getV(), r.getQ(), r.getTime(), r.getPos(), r.getOp());
//    		        queue.add(r2);
    		        return;
    		    }

    		    ready = false; // Mark as not ready to avoid multiple issues
    		    boolean qjFound = r.getQ() != null 
                        && checkRegisterExists(r.getQ()) 
                        && !regFile.get(Integer.parseInt(r.getQ().substring(1)) - 1).getQi().equals("S" + (index + 1));
		        System.out.println("Dependency (qjFound): " + qjFound);
		        if (qjFound) {
		        	// Set dependency for StoreBuffer
			        Stations.get(index).setAll(r.getBusy(), r.getA(), null, regFile.get(Integer.parseInt(r.getQ().substring(1)) - 1).getQi(), r.getTime(), r.getPos(), r.getOp());
			
			        // Determine if the dependency comes from a LoadBuffer or ReservationStation
			        ReservationStations rr = null;
			        LoadBuffers loadBufferForQ = null;
			
			        if (regFile.get(Integer.parseInt(r.getQ().substring(1)) - 1).getQi().substring(0, 1).equals("M")) {
			        	rr = mulStations;
			        } else if (regFile.get(Integer.parseInt(r.getQ().substring(1)) - 1).getQi().substring(0, 1).equals("L")) {
			        	loadBufferForQ = loadBuffers;
			        } else {
			        	rr = addStations; // Dependency on ADD
			        }
			
			        int indexQ = Integer.parseInt(regFile.get(Integer.parseInt(r.getQ().substring(1)) - 1).getQi().substring(1));
			        if (loadBufferForQ != null) {
			        	loadBufferForQ.add(indexQ - 1, new Point(index, "qS", null, r.getOp()));
			        } else if (rr != null) {
			        	rr.add(indexQ - 1, new Point(index, "qS", null, r.getOp()));
			        }
		        } else {
		        	// If no dependency is found, set the StoreBuffer with the provided values
		        	Stations.get(index).setAll(r.getBusy(), r.getA(), r.getV(), r.getQ(), r.getTime(), r.getPos(), r.getOp());
		        }
			
		        queue.remove(0); // Remove the processed StoreBuffer from the queue
		        table.setIssue(r.getPos(), clockCycle); // Mark the issue time in the execution table
			
		        return;
    		}

    		
    		
    		ReservationStations Stations = type.equals("ADD")?addStations:mulStations;
    		Integer index = Stations.getFirstEmpty();//can be -1 if full
    		System.out.println("index in UPD2 "+index);
    		//if for buffers
    		ReservationStation r = (ReservationStation)queue.get(0);
    		String s = r.getOp().equals("ADD")?"A":"M";
    		boolean qjFound = r.getQj() != null && checkRegisterExists(r.getQj()) && !regFile.get(Integer.parseInt(r.getQj().substring(1)) - 1).getQi().equals(s + (index + 1));
    		boolean qkFound = r.getQk() != null && checkRegisterExists(r.getQk()) && !regFile.get(Integer.parseInt(r.getQk().substring(1)) - 1).getQi().equals(s + (index + 1));

    	    if(qjFound && qkFound) {    
    	    	if (qjFound && qkFound) {    
    	    	    // Determine the source of dependencies for Qj
    	    	    String qiQj = regFile.get(Integer.parseInt(r.getQj().substring(1)) - 1).getQi();
    	    	    String qiQjType = qiQj.substring(0, 1);
    	    	    ReservationStations rr = null;
    	    	    LoadBuffers loadBufferForQj = null;

    	    	    if (qiQjType.equals("L")) {
    	    	        loadBufferForQj = loadBuffers;
    	    	    } else if (qiQjType.equals("M")) {
    	    	        rr = mulStations;
    	    	    } else if (qiQjType.equals("A")) {
    	    	        rr = addStations;
    	    	    }
    	    	    int indexQj = Integer.parseInt(qiQj.substring(1));

    	    	    // Determine the source of dependencies for Qk
    	    	    String qiQk = regFile.get(Integer.parseInt(r.getQk().substring(1)) - 1).getQi();
    	    	    String qiQkType = qiQk.substring(0, 1);
    	    	    ReservationStations rr2 = null;
    	    	    LoadBuffers loadBufferForQk = null;

    	    	    if (qiQkType.equals("L")) {
    	    	        loadBufferForQk = loadBuffers;
    	    	    } else if (qiQkType.equals("M")) {
    	    	        rr2 = mulStations;
    	    	    } else if (qiQkType.equals("A")) {
    	    	        rr2 = addStations;
    	    	    }
    	    	    int indexQk = Integer.parseInt(qiQk.substring(1));

    	    	    // Add dependencies for Qj
    	    	    if (loadBufferForQj != null) {
    	    	        loadBufferForQj.add(indexQj - 1, new Point(index, "qj", r.getVj(), r.getOp()));
    	    	    } else if (rr != null) {
    	    	        rr.add(indexQj - 1, new Point(index, "qj", r.getVj(), r.getOp()));
    	    	    }

    	    	    // Add dependencies for Qk
    	    	    if (loadBufferForQk != null) {
    	    	        loadBufferForQk.add(indexQk - 1, new Point(index, "qk", r.getVk(), r.getOp()));
    	    	    } else if (rr2 != null) {
    	    	        rr2.add(indexQk - 1, new Point(index, "qk", r.getVk(), r.getOp()));
    	    	    }

    	    	    // Update the current reservation station with dependencies using `Qi` from the register file
    	    	    r.setAll(
    	    	        r.getBusy(),
    	    	        r.getOp(),
    	    	        null,
    	    	        null,
    	    	        regFile.get(Integer.parseInt(r.getQj().substring(1)) - 1).getQi(), // Use Qi from register file for Qj
    	    	        regFile.get(Integer.parseInt(r.getQk().substring(1)) - 1).getQi(), // Use Qi from register file for Qk
    	    	        r.getA(),
    	    	        r.getTime(),
    	    	        r.getPos()
    	    	    );
    	    	}

    	    }
    	    else if (qjFound || qkFound) {
    	        if (qjFound) {
    	            String qiQj = regFile.get(Integer.parseInt(r.getQj().substring(1)) - 1).getQi();
    	            String qiQjType = qiQj.substring(0, 1);
    	            ReservationStations rr = null;
    	            LoadBuffers loadBufferForQj = null;

    	            if (qiQjType.equals("L")) {
    	                loadBufferForQj = loadBuffers;
    	            } else if (qiQjType.equals("M")) {
    	                rr = mulStations;
    	            } else if (qiQjType.equals("A")) {
    	                rr = addStations;
    	            }
    	            int indexQj = Integer.parseInt(qiQj.substring(1));

    	            // Add dependency for Qj
    	            if (loadBufferForQj != null) {
    	                loadBufferForQj.add(indexQj - 1, new Point(index, "qj", r.getVj(), r.getOp()));
    	            } else if (rr != null) {
    	                rr.add(indexQj - 1, new Point(index, "qj", r.getVj(), r.getOp()));
    	            }

    	            r.setAll(
    	                r.getBusy(),
    	                r.getOp(),
    	                null,
    	                r.getVk(),
    	                regFile.get(Integer.parseInt(r.getQj().substring(1)) - 1).getQi(), // Use Qi from register file for Qj
    	                null,
    	                r.getA(),
    	                r.getTime(),
    	                r.getPos()
    	            );
    	        }
    	        if (qkFound) {
    	            String qiQk = regFile.get(Integer.parseInt(r.getQk().substring(1)) - 1).getQi();
    	            String qiQkType = qiQk.substring(0, 1);
    	            ReservationStations rr2 = null;
    	            LoadBuffers loadBufferForQk = null;

    	            if (qiQkType.equals("L")) {
    	                loadBufferForQk = loadBuffers;
    	            } else if (qiQkType.equals("M")) {
    	                rr2 = mulStations;
    	            } else if (qiQkType.equals("A")) {
    	                rr2 = addStations;
    	            }
    	            int indexQk = Integer.parseInt(qiQk.substring(1));

    	            // Add dependency for Qk
    	            if (loadBufferForQk != null) {
    	                loadBufferForQk.add(indexQk - 1, new Point(index, "qk", r.getVk(), r.getOp()));
    	            } else if (rr2 != null) {
    	                rr2.add(indexQk - 1, new Point(index, "qk", r.getVk(), r.getOp()));
    	            }

    	            r.setAll(
    	                r.getBusy(),
    	                r.getOp(),
    	                r.getVj(),
    	                null,
    	                null,
    	                regFile.get(Integer.parseInt(r.getQk().substring(1)) - 1).getQi(), // Use Qi from register file for Qk
    	                r.getA(),
    	                r.getTime(),
    	                r.getPos()
    	            );
    	        }
    	    }
    	    else {
        		r.setAll( r.getBusy(), r.getOp(), r.getVj(), r.getVk(), null, null,r.getA(),r.getTime(),r.getPos());
        	}
    	    table.setIssue(r.getPos(), clockCycle);
    	    Stations = r.getOp().equals("ADD")?addStations:mulStations;
    	    Stations.setStation(index,r);
    	    s = r.getOp().equals("ADD")?"A":"M";
    	    updateRegister(Integer.parseInt(r.getA().substring(1))-1,s+(index+1));

    		
    	    queue.remove(0);
    	    ready =false;
    	}
    	
    }
    
    public void writeBack1(int clockCycle) {
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
        		ReservationStations station = p.getOp().equals("ADD") ? addStations : mulStations;
        		System.out.println("this is the p "+p);
        		if(p.getY().equals("qj"))
        			station.setStationJ(p.getX(), p.getZ());
        		if(p.getY().equals("qk"))
        			station.setStationK(p.getX(), p.getZ());
        	
        		if(station.get(p.getX()).getVj()!=null&&station.get(p.getX()).getVk()!=null) {
        			station.get(p.getX()).setTime(2);//adjust hasab el process
                }//return here
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

    
    public void writeBack(int clockCycle) {
        writeBack.sort(Comparator.comparingInt(Points::getX));
        if (!writeBack.isEmpty()) {
            String type = writeBack.get(0).op;
            if (type.equals("SD")) {
                // Handle the case for Store Buffer
                StoreBuffer storeBuffer = storeBuffers.get(writeBack.get(0).y); // Access the specific Store buffer
                table.setWriteBack(writeBack.get(0).x, clockCycle); // Mark as written back in the table

                // Simulate memory write (you can implement the actual memory logic here)
                System.out.println("Memory write for address: " + storeBuffer.getA() + " with value: " + storeBuffer.getV());

                // Reset the store buffer
                storeBuffer.reset();
            } else if (type.equals("LD")) {
                // Handle the case for Load Station
                LoadBuffer loadStation = loadBuffers.get(writeBack.get(0).y); // Access the specific Load station
                List<Point> l = loadStation.getList();
                
                table.setWriteBack(writeBack.get(0).x, clockCycle);
                
                for (Point p : l) {
                    ReservationStations station = p.getOp().equals("ADD") ? addStations : mulStations;
                    System.out.println("this is the p " + p);
                    if (p.getY().equals("qj"))
                        station.setStationJ(p.getX(), p.getZ());
                    if (p.getY().equals("qk"))
                        station.setStationK(p.getX(), p.getZ());
                    if(p.getY().equals("qS")) {
                    	storeBuffers.setStationV(p.getX(),p.getZ());
                    	storeBuffers.get(p.getX()).setTime(2);
                    }
                    if (station.get(p.getX()).getVj() != null && station.get(p.getX()).getVk() != null) {
                        station.get(p.getX()).setTime(2); // Adjust as needed
                    }
                }
                String register = loadStation.getA(); // Assuming this is "R1"
                int value = Integer.parseInt(register.substring(1)); // Removes the first character ("R") and parses the rest
                int index = writeBack.get(0).y+1;
                if (regFile.get(value-1).getQi().equals("L"+index)) 
                	regFile.resetRow(value - 1);
                System.out.println("this is the x" + writeBack.get(0).x);
                System.out.println("this is the y" + writeBack.get(0).y);

                loadStation.reset(); // Reset the Load station
            } else {
                // Handle ADD and MUL stations
                ReservationStations stations = type.equals("ADD") ? addStations : mulStations;
                List<Point> l = stations.get(writeBack.get(0).y).getList();
                table.setWriteBack(writeBack.get(0).x, clockCycle);
                
                for (Point p : l) {
                    ReservationStations station = p.getOp().equals("ADD") ? addStations : mulStations;
                    System.out.println("this is the p " + p);
                    if (p.getY().equals("qj"))
                        station.setStationJ(p.getX(), p.getZ());
                    if (p.getY().equals("qk"))
                        station.setStationK(p.getX(), p.getZ());
                    if(p.getY().equals("qS")) {
                    	storeBuffers.setStationV(p.getX(),p.getZ());
                    	storeBuffers.get(p.getX()).setTime(2);
                    }

                    if ((p.getOp().equals("ADD")||p.getOp().equals("MUL"))&&station.get(p.getX()).getVj() != null && station.get(p.getX()).getVk() != null) {
                        station.get(p.getX()).setTime(2); // Adjust as needed
                    }
                }
                String register = stations.get(writeBack.get(0).y).getA(); // Assuming this is "R1"
                int value = Integer.parseInt(register.substring(1)); // Removes the first character ("R") and parses the rest
                String s = type.equals("ADD")?"A":"M";
                int index = writeBack.get(0).y+1;
                System.out.println("this is the register"+register+" "+regFile.get(value-1)+" "+s+index);
				if (regFile.get(value-1).getQi()!=null&&regFile.get(value-1).getQi().equals(s+index)) 
                	regFile.resetRow(value - 1);
                System.out.println("this is the x" + writeBack.get(0).x);
                System.out.println("this is the y" + writeBack.get(0).y);

                stations.get(writeBack.get(0).y).reset(); // Reset the ADD/MUL station
            }
            writeBack.remove(0);
        }
    }

    
    
    
    public void subtractCycle(int clockCycle) {
    	System.out.println("this is the whole queue"+queue.size());
    	System.out.println("this is the whole queue"+queue);
        // Loop over addStations
    	    for (int i = 0; i < addStations.size(); i++) {
    	    	if(addStations.get(i).getBusy()==false) {
    	    		if (!queue.isEmpty() && (queue.get(0) instanceof ReservationStation)) {
    	    		    ReservationStation station = (ReservationStation) queue.get(0); // Cast before accessing methods
    	    		    if (station.getOp().equals("ADD") && ready) {
    	    			System.out.println("this is the queue "+queue);
    	    			updateReservationStation(clockCycle,"ADD");
    	    			ready=false;
    	    			//call when issuing all
    	    		    }
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
    	    		if (!queue.isEmpty() && queue.get(0) instanceof ReservationStation) {
    	    		    ReservationStation station = (ReservationStation) queue.get(0); // Cast before accessing methods
    	    		    if (station.getOp().equals("MUL") && ready) {
	    	    			updateReservationStation(clockCycle,"MUL");
	    	    			ready=false;
	    	    			//call when issuing all
    	    		    }
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
    	 // Loop over loadBuffers
    	    for (int i = 0; i < loadBuffers.size(); i++) {
    	        // If the load buffer is not busy
    	        if (loadBuffers.get(i).getBusy() == false) {
    	            // If the queue is not empty and the operation is a load (e.g., "LOAD") and ready is true
    	        	if (!queue.isEmpty() && queue.get(0) instanceof LoadBuffer&&ready) {
    	        		updateReservationStation(clockCycle, "LD"); // Update the load buffer with the instruction
    	        		ready = false; // Mark ready as false to avoid issuing multiple instructions at once
    	        	}    
    	        }

    	        // If the load buffer is busy
    	        if (loadBuffers.get(i).getBusy() == true) {
    	            LoadBuffer buffer = loadBuffers.get(i); // Get the current load buffer
    	            Integer time = buffer.getTime(); // Get the time remaining for the load operation

    	            if (time != null) {
    	                // If the instruction has just started
    	                if (table.get(buffer.getPos()).getIssue() == null || (time == 5 && table.get(buffer.getPos()).getIssue() == clockCycle))
    	                    continue;

    	                // If the operation is starting
    	                if (time == 2) {
    	                    table.setExecutionStart(buffer.getPos(), clockCycle); // Set the execution start time
    	                }

    	                // If the operation is ending
    	                if (time == 1) {
    	                    table.setExecutionEnd(buffer.getPos(), clockCycle); // Set the execution end time
    	                }

    	                // If the operation is complete
    	                if (time == 0) {
    	                    writeBack.add(new Points(buffer.getPos(), i, "LD")); // Add to the write-back queue
    	                    buffer.setTime(-1); // Mark the buffer as done
    	                }

    	                // Decrement the time for the operation
    	                if (time != 0) {
    	                    time = time - 1;
    	                    buffer.setTime(time);
    	                }
    	            } else {
    	                // If both operands (or address) are available, set the time for the operation
    	                if (buffer.getA() != null) {
    	                    buffer.setTime(2); // Set the execution time for load instructions
    	                    System.out.println("Load operation starting at clock cycle: " + clockCycle);
    	                }
    	            }
    	        }
    	    }
    	 // Loop over storeBuffers
    	    for (int i = 0; i < storeBuffers.size(); i++) {
    	        // If the store buffer is not busy
    	        if (storeBuffers.get(i).getBusy() == false) {
    	            // If the queue is not empty and the operation is a store (e.g., "SD") and ready is true
    	            if (!queue.isEmpty() && queue.get(0) instanceof StoreBuffer && ready) {
    	                updateReservationStation(clockCycle, "SD"); // Update the store buffer with the instruction
    	                ready = false; // Mark ready as false to avoid issuing multiple instructions at once
    	            }
    	        }

    	        // If the store buffer is busy
    	        if (storeBuffers.get(i).getBusy() == true) {
    	            StoreBuffer buffer = storeBuffers.get(i); // Get the current store buffer
    	            Integer time = buffer.getTime(); // Get the time remaining for the store operation

    	            if (time != null) {
    	                // If the instruction has just started
    	                if (table.get(buffer.getPos()).getIssue() == null || (time == 2 && table.get(buffer.getPos()).getIssue() == clockCycle))
    	                    continue;

    	                // If the operation is starting
    	                if (time == 2) {
    	                	System.out.println("rgis is "+buffer.getPos());
    	                    table.setExecutionStart(buffer.getPos(), clockCycle); // Set the execution start time
    	                }

    	                // If the operation is ending
    	                if (time == 1) {
    	                    table.setExecutionEnd(buffer.getPos(), clockCycle); // Set the execution end time
    	                }

    	                // If the operation is complete
    	                if (time == 0) {
    	                    writeBack.add(new Points(buffer.getPos(), i, "SD")); // Add to the write-back queue
    	                    buffer.setTime(-1); // Mark the buffer as done
    	                }

    	                // Decrement the time for the operation
    	                if (time != 0) {
    	                    time = time - 1;
    	                    buffer.setTime(time);
    	                }
    	            } else {
    	                // If the address is available, set the time for the operation
    	                if (buffer.getV() != null) {
    	                    buffer.setTime(2); // Set the execution time for store instructions
    	                    System.out.println("Store operation starting at clock cycle: " + clockCycle);
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

        System.out.println("\nRegister File:");
        regFile.printAllRegisters();
//
//        System.out.println("\nLoad Buffers:");
//        loadBuffers.printAllBuffers();
////        
//        System.out.println("\nStore Buffers:");
//        storeBuffers.printAllBuffers();

        System.out.println("\nAdd Reservation Stations:");
        addStations.printAllStations();

        System.out.println("\nMultiply Reservation Stations:");
        mulStations.printAllStations();
        
    }




   
}
