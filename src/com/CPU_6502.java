package com;
import java.util.Hashtable;
import java.util.Scanner;

import mappers.Mapper;

public class CPU_6502 {
	Mapper map;
	
	
	int program_counter;
	private byte stack_pointer;
	private byte accumulator;
	private byte x_index_register;
	private byte y_index_register;
	//flags
	private boolean NFlag;
	private boolean VFlag;
	private boolean BFlag;
	private boolean DFlag;
	public boolean IFlag;
	private boolean ZFlag;
	private boolean CFlag;
	int oldaddr;
	//other stuff
	private boolean brokenaddress = false;
	int instruction_cycle;
	public byte current_instruction;
	private byte tempregister;
	int address;
	private int pointer;
	private boolean branchtaken;
	int lowpc;
	byte old_inst;
	int old_cycle;
	
	public boolean writeDMA = false;
	public boolean doNMI = false;
	public boolean doIRQ = false;
	boolean nmihijack;
	//private Memory memory;
	private Hashtable<Byte,Integer> inst_type;//read/write/both
	private Hashtable<Byte,String> inst_mode;//memory access
	public Hashtable<Byte,String> inst_name;//instruction name
	public CPU_6502(Mapper mapper) {
		map = mapper;
		Scanner s;
		inst_type= new Hashtable<Byte,Integer>();
		inst_mode = new Hashtable<Byte,String>();
		inst_name = new Hashtable<Byte,String>();
		//try {
			//s = new Scanner(System.in);
			s = new Scanner((this.getClass().getResourceAsStream("cpu_instructions.txt")));
			while(s.hasNext()){
				byte x = Integer.valueOf(s.next(), 16).byteValue();
				String mode = s.next();
				int type = s.nextInt();
				inst_type.put(x,type);
				inst_mode.put(x, mode);
				inst_name.put(x, s.next());
			}
			s.close();
		//} catch (FileNotFoundException e) {
		//	e.printStackTrace();
		//}
		instruction_cycle = 1;
		stack_pointer = (byte)0xfd;
		setFlags((byte)0x34);
	}
	void debug(double i){
		System.out.println("PC:" + Integer.toHexString(program_counter) + " Current Instruction: " + inst_name.get(current_instruction)
				//+ " Instruction Hex: "+Integer.toHexString(Byte.toUnsignedInt(current_instruction))
				+ " Instruction Cycle: "+instruction_cycle
				+" total cycles: " +i
				+" SP:"+ Integer.toHexString(Byte.toUnsignedInt(stack_pointer))
				+" A:" + Integer.toHexString(Byte.toUnsignedInt(accumulator))
				+" X:" + Integer.toHexString(Byte.toUnsignedInt(x_index_register))
				+" Y:" + Integer.toHexString(Byte.toUnsignedInt(y_index_register))
				+" T:" + Integer.toHexString(Byte.toUnsignedInt(tempregister))
			    +" ADDR:" +Integer.toHexString(address)
				+"     N:" +(NFlag?1:0)
				+" V:" +(VFlag?1:0)
				+" D:" +(DFlag?1:0)
				+" I:" +(IFlag?1:0)
				+" Z:" +(ZFlag?1:0)
				+" C:" +(CFlag?1:0));
		//memory.printMemory(program_counter, 20);
	}
	int dmac=0;
	int dmain = 0;
	int dxx = 0;
	int cpuinc=0;
	void run_cycle(){
		if(writeDMA){
			//System.out.println("WRITE DMA IS TRUE!");
			if(dmac ==513){
				dmac =0;
				dmain=0;
				cpuinc=0;
				writeDMA=false;
				//map.printOAMPPU(0, 256);
			}
			else if(dmac==0){
				dmac++;
				dxx = map.cpureadu(0x4014);
				dxx<<=8;
				dmain = map.ppu.OAMADDR;
			}
			else if(dmac%2==1){
				dmac++;
				//where it reads
			}
			else{
				//System.out.println("read location: "+map.cpureadu(0x4014)*0x100+dmain);
				map.cpuwriteoam(dmain,map.cpuread(dxx+cpuinc));
				//map.cpuwrite(0x2004, map.cpuread(dxx*0x100+cpuinc));
				if(dmain==255)
					dmain=0;
				else
					dmain++;
				cpuinc++;
				dmac++;
			}
		}
		else{
			executeInstruction();
		}
	}
	void setPC(int val){
		program_counter =  val;
	}
	//Methods for setting flags.
	private byte buildFlags(){
		byte temp = 0;
		
		if(CFlag)temp = (byte) (temp|1);
		if(ZFlag)temp = (byte) (temp|(1<<1));
		if(IFlag)temp = (byte) (temp|(1<<2));
		if(DFlag)temp = (byte) (temp|(1<<3));
		//temp|=(1<<3);
		//temp|=(1<<4);
		if(BFlag)temp = (byte) (temp|(1<<4));
		temp = (byte) (temp|(1<<5));
		if(VFlag)temp = (byte) (temp|(1<<6));
		if(NFlag)temp = (byte) (temp|(1<<7));
		return temp;
	}
	private void setFlags(byte x){
		NFlag = x<0?true:false;
		VFlag = (x&(1<<6))>0?true:false;
		BFlag = (x&(1<<4))>0?true:false;
		DFlag = (x&(1<<3))>0?true:false;
		IFlag = (x&(1<<2))>0?true:false;
		ZFlag = (x&(1<<1))>0?true:false;
		CFlag = (x&1)>0?true:false;
		
	}
	boolean dodebug;
	int irqsetdelay=-1;
	boolean nmiInter;
	private void pollInterrupts(){
		//if(irqlatency>0){
		//	irqlatency--;
		//	//System.out.println("Im in here "+ IFlag);
		//}
		//if(irqsetdelay==0){
		//	IFlag = true;
		//	irqsetdelay =-1;
		//}
		//else
		//	irqsetdelay--;
		if(nmi){
			nmiInter = true;
			nmi = false;
		}
		//if()
	}
	private byte getNextInstruction(){
		//oldaddr = Byte.toUnsignedInt(current_instruction);
		if(irqlatency>0){
			irqlatency--;
			//System.out.println("Im in here "+ IFlag);
		}
		if(irqsetdelay==0){
			if(map.control.checkDebug())
				System.out.println("Setting IFlag=true instruction: "+inst_name.get(current_instruction)+" scanline: "+map.ppu.scanline);
			IFlag = true;
			irqsetdelay =-1;
		}
		else
			irqsetdelay--;
		if(nmi&&nmirecieved==0){
			//nmiInter = false;
			program_counter--;
			nmi=false;
			return 0x02;
		}
		else if(nmi&&nmirecieved!=0){
			nmirecieved--;
			return map.cpuread(program_counter);
		}
		else if(doIRQ&&irqrecieved!=0){
			irqrecieved--;
			byte b = map.cpuread(program_counter);
			return map.cpuread(program_counter);
		}
		else if(doIRQ&&!IFlag&&irqlatency==0){//&&irqrecieved==0){
			//System.out.println("Executing IRQ ppu SL: "+map.ppu.scanline+" cycle: "+map.ppu.pcycle 
			//		+" Prev inst: "+inst_name.get(current_instruction));
			program_counter--;
			//doIRQ= false;
			return 0x12;
		}
		else{
			if(current_instruction == 0x12)
				dodebug = true;
			else
				dodebug = false;
				//System.out.println("waiting for irqlatency");
			
			return map.cpuread(program_counter);
		}
	}
	boolean oldnmi = false;
	boolean oldirq = false;
	int irqlatency=0;
	boolean nmi=false;
	public int nmirecieved;
	public int irqrecieved;
	private void executeInstruction(){
		/*if(doNMI&&(old_inst!=current_instruction||instruction_cycle<old_cycle)){
				if(map.cpuread(program_counter-1)==current_instruction)
					program_counter--;
				if(map.cpuread(program_counter-2)==current_instruction)
					program_counter-=2;
				instruction_cycle = 2;
			current_instruction = 0x5a;
			doNMI = false;
		}*/
		if(doNMI&&!oldnmi){
			//if(instruction_cycle==2){//||instruction_cycle==2)
			//	System.out.println("In here");
			//	nmirecieved=0;
			//}
			//else if(inst_mode.get(current_instruction).equals("immediate")&&instruction_cycle<=2){
			//	System.out.println("no in this one. INstruction: "+inst_name.get(current_instruction));
			//	nmirecieved=0;
			//}
			//else{
				//System.out.println("Normal one INstruction: "+inst_name.get(current_instruction) +" mode "+inst_mode.get(current_instruction)+" cycle: "+instruction_cycle);
				nmirecieved=1;
			//}
			nmi=true;
		}
		oldnmi=doNMI;
		//if(doIRQ&&!oldirq){
		//	irqrecieved=1;
		//}
		//oldirq = doIRQ;
		if(instruction_cycle ==1){
			current_instruction = getNextInstruction();
			//System.out.println(memory[program_counter]);
			program_counter++;
			instruction_cycle++;
			//System.out.println("In the if: "+" "+current_instruction);
		}
		else{
			//System.out.println("In the else");
			if(!inst_mode.containsKey(current_instruction)){
				debug(0);
				map.printMemory(0,0x20);
				//memory.printMemory(0x3a0, 0x20);
				//memory.printMemory(0, 0x100);
				System.out.println(Integer.toHexString(current_instruction)+" Scanline: "+map.ppu.scanline+" cycle: "+map.ppu.pcycle);
			}
		switch(inst_mode.get(current_instruction)){
		case "immediate":{
			switch(instruction_cycle){
			case 2: {
				tempregister = map.cpuread(program_counter);
				//if(inst_name.get(current_instruction).equals("LDX")){
				//	System.out.println("In hererere");
				//	executeOp();
				//	instruction_cycle=1;
				//	program_counter++;
				//	break;
				//}
					
				program_counter++;
				instruction_cycle++;break;
			}
			case 3: {
				executeOp();
				current_instruction = getNextInstruction();
				program_counter++;
				instruction_cycle = 2;break;
			}
			}
		};break;
		case "zero":
			switch(inst_type.get(current_instruction)){
			case 0:
				switch(instruction_cycle){
				case 2:
					address = map.cpureadu(program_counter);
					program_counter++;
					instruction_cycle++;
					break;
				case 3: 
					tempregister = map.cpuread(address);
					instruction_cycle++;
					break;
				case 4:
					executeOp();
					address = 0;
					instruction_cycle=2;
					current_instruction = getNextInstruction();
					program_counter++;
					break;
				}
				break;
			case 1:
				switch(instruction_cycle){
				case 2:
					address = map.cpureadu(program_counter);
					program_counter++;
					instruction_cycle++;
					break;
				case 3:
					tempregister = map.cpuread(address);
					instruction_cycle++;
					break;
				case 4:
					map.cpuwrite(address, tempregister);
					executeOp();
					instruction_cycle++;
					break;
				case 5:
					map.cpuwrite(address, tempregister);
					address = 0;
					instruction_cycle=1;
					break;
				}
				break;
			case 2:
				switch(instruction_cycle){
				case 2:
					address = map.cpureadu(program_counter);
					program_counter++;
					instruction_cycle++;
					break;
				case 3:
					executeOp();
					address = 0;
					instruction_cycle =1;
					break;
				}
				break;
			}
			break;
		case "zerox": case "zeroy":
			switch(inst_type.get(current_instruction)){
			case 0:
				switch(instruction_cycle){
				case 2:
					address = map.cpureadu(program_counter);
					program_counter++;
					instruction_cycle++;
					break;
				case 3:
					if(inst_mode.get(current_instruction).equals("zerox"))
						address += Byte.toUnsignedInt(x_index_register);
					else
						address += Byte.toUnsignedInt(y_index_register);
					instruction_cycle++;
					break;		
				case 4:
					address&=0xff;
					tempregister= map.cpuread(address);
					instruction_cycle++;
					break;
				case 5:
					executeOp();
					instruction_cycle = 2;
					current_instruction = getNextInstruction();
					program_counter++;
					break;			
				}
				break;
			case 1:
				switch(instruction_cycle){
				case 2:
					address = map.cpureadu(program_counter);
					program_counter++;
					instruction_cycle++;
					break;
				case 3:
					address +=Byte.toUnsignedInt(x_index_register);
					instruction_cycle++;
					break;
				case 4:
					address&=0xff;
					tempregister = map.cpuread(address);
					instruction_cycle++;
					break;
				case 5:
					map.cpuwrite(address, tempregister);
					executeOp();
					instruction_cycle++;
					break;
				case 6:
					map.cpuwrite(address, tempregister);
					instruction_cycle = 1;
					address = 0;
					break;
				}
				break;
			case 2:
				switch(instruction_cycle){
				case 2:
					address = map.cpureadu(program_counter);
					program_counter++;
					instruction_cycle++;
					break;
				case 3:
					if(inst_mode.get(current_instruction).equals("zerox"))
						address += Byte.toUnsignedInt(x_index_register);
					else
						address += Byte.toUnsignedInt(y_index_register);
					instruction_cycle++;
					break;		
				case 4:
					address&=0xff;
					executeOp();
					instruction_cycle = 1;
					break;
				}
				break;
			}
			break;
		case "absolute":
			switch(inst_type.get(current_instruction)){
			case 0:
				switch(instruction_cycle){
				case 2:
					address = map.cpureadu(program_counter);
					program_counter++;
					instruction_cycle++;
					break;
				case 3:
					address = address|(map.cpureadu(program_counter)<<8);
					program_counter++;
					instruction_cycle++;
					break;
				case 4:
					tempregister = map.cpuread(address);
					instruction_cycle++;
					break;
				case 5:
					executeOp();
					address = 0;
					current_instruction = getNextInstruction();
					program_counter++;
					instruction_cycle=2;
					break;
				}
				break;
			case 1:
				switch(instruction_cycle){
				case 2:
					address = map.cpureadu(program_counter);
					program_counter+=1;
					instruction_cycle++;
					break;
				case 3:
					address = address|(map.cpureadu(program_counter)<<8);
					program_counter+=1;
					instruction_cycle++;
					break;
				case 4:
					tempregister = map.cpuread(address);
					instruction_cycle++;
					break;
				case 5:
					executeOp();
					instruction_cycle++;
					break;
				case 6:
					map.cpuwrite(address, tempregister);
					instruction_cycle = 1;
					address = 0;
					break;
				}
				break;
			case 2:
				switch(instruction_cycle){
				case 2:
					address = (map.cpureadu(program_counter));
					program_counter++;
					instruction_cycle++;
					break;
				case 3:
					address = address|(map.cpureadu(program_counter)<<8);
					program_counter++;
					instruction_cycle++;
					break;
				case 4:
					executeOp();
					address = 0;
					instruction_cycle=1;
					break;
				}
				break;
			case 4:
				switch(instruction_cycle){
				case 2:
					address = map.cpureadu(program_counter);
					//System.out.println(address);
					program_counter++;
					instruction_cycle++;
					break;
				case 3:
					address = address|(map.cpureadu(program_counter)<<8);
					program_counter= address;
					instruction_cycle=1;
					break;
				}
				break;
			}
			break;
		case "absolutex": case "absolutey": {
			switch(inst_type.get(current_instruction)){
			case 0: {
				switch(instruction_cycle){
				case 2: {
					address = 0;
					address = map.cpureadu(program_counter);
					program_counter++;
					instruction_cycle++;break;
				}
				case 3: {
					address |= (map.cpureadu(program_counter)<<8);
					lowpc = address&0xff00;
					if(inst_mode.get(current_instruction).equals("absolutex"))
						address += Byte.toUnsignedInt(x_index_register);
					else
						address += Byte.toUnsignedInt(y_index_register);
					program_counter++;
					instruction_cycle++;break;
				}
				case 4: {
					if(lowpc!=(address&0xff00)){
						brokenaddress = true;	
						address&=0xffff;
						tempregister=map.cpuread((address&0xff)|lowpc);
						instruction_cycle++;break;
					}
					else{
						address&=0xffff;
						tempregister = map.cpuread(address);
						instruction_cycle++;break;
					}
					
				}
				case 5: {
					if(brokenaddress){
						tempregister = map.cpuread(address);
						brokenaddress = false;
						instruction_cycle++;break;
					}
					else{
						executeOp();
						current_instruction= getNextInstruction();
						program_counter++;
						instruction_cycle= 2;break;
					}
				}
				case 6: {
					executeOp();
					current_instruction= getNextInstruction();
					program_counter++;
					instruction_cycle= 2;break;
				}
				}
			};break;
			case 1: {
				switch(instruction_cycle){
				case 2: {
					address= 0;
					address = address | map.cpureadu(program_counter);
					program_counter++;
					instruction_cycle++;break;
				}
				case 3: {
					address = address | (map.cpureadu(program_counter)<<8);
					if(inst_mode.get(current_instruction).equals("absolutex"))
						address+= Byte.toUnsignedInt(x_index_register);
					else
						address+= Byte.toUnsignedInt(y_index_register);
					program_counter++;
					instruction_cycle++;break;
				}
				case 4: { 
					address&=0xffff;
					tempregister=map.cpuread(program_counter);
					instruction_cycle++;break;
				}
				case 5: {
					tempregister = map.cpuread(address);
					instruction_cycle++;break;
				}
				case 6: {
					map.cpuwrite(address, tempregister);
					executeOp();
					instruction_cycle++;break;
				}
				case 7: {
					map.cpuwrite(address, tempregister);
					instruction_cycle = 1;break;
				}
				}
			};break;
			case 2: {
				switch(instruction_cycle){
				case 2: {
					address = map.cpureadu(program_counter);
					program_counter++;
					instruction_cycle++;break;
				}
				case 3: {
					address = address | (map.cpureadu(program_counter)<<8);
					lowpc=address&0xff00;
					if(inst_mode.get(current_instruction).equals("absolutex"))
						address += Byte.toUnsignedInt(x_index_register);
					else
						address += Byte.toUnsignedInt(y_index_register);
					program_counter++;
					instruction_cycle++;break;
				}
				case 4: {
					address&=0xffff;
					tempregister = map.cpuread((address&0xff)|lowpc);
					instruction_cycle++;break;
				}
				case 5: {
					executeOp();
					instruction_cycle = 1;break;
				}
				}
				
			}
			}
		};break;
		case "(indirect,x)":{
			switch(inst_type.get(current_instruction)){
			case 0: {
				switch(instruction_cycle){
				case 2: {
					pointer = map.cpureadu(program_counter);
					program_counter++;
					instruction_cycle++;break;
				}
				case 3: {
					pointer = pointer+Byte.toUnsignedInt(x_index_register);
					instruction_cycle++;break;
				}
				case 4: {
					pointer&=0xff;
					address = map.cpureadu(pointer);
					instruction_cycle++;break;
				}
				case 5: {
					pointer++;pointer&=0xff;
					address = address| (map.cpureadu(pointer)<<8);
					instruction_cycle++;break;
				}
				case 6: {
					tempregister = map.cpuread(address);
					instruction_cycle++;break;
				}
				case 7: {
					executeOp();
					current_instruction = getNextInstruction();
					program_counter++;
					instruction_cycle=2;
					address = 0;break;
				}
				}
			};break;
			case 1: {
				switch(instruction_cycle){
				case 2: {
					pointer = map.cpureadu(program_counter);
					program_counter++;
					instruction_cycle++;break;
				}
				case 3: {
					pointer = pointer+Byte.toUnsignedInt(x_index_register);
					instruction_cycle++;break;
				}
				case 4: {
					pointer&=0xff;
					address = map.cpureadu(pointer);
					instruction_cycle++;break;
				}
				case 5: {
					pointer++;pointer&=0xff;
					address = address| (map.cpureadu(pointer)<<8);
					instruction_cycle++;break;
				}
				case 6: {
					tempregister = map.cpuread(address);
					instruction_cycle++;break;
				}
				case 7: {
					map.cpuwrite(address, tempregister);
					executeOp();
					instruction_cycle++;break;
				}
				case 8: {
					map.cpuwrite(address, tempregister);
					instruction_cycle=1;
				}
				}
			};break;
			case 2: {
				switch(instruction_cycle){
				case 2: {
					pointer = map.cpureadu(program_counter);
					program_counter++;
					instruction_cycle++;break;
				}
				case 3: {
					pointer = pointer+Byte.toUnsignedInt(x_index_register);
					instruction_cycle++;break;
				}
				case 4: {
					pointer&=0xff;
					address = map.cpureadu(pointer);
					instruction_cycle++;break;
				}
				case 5: {
					pointer++;pointer&=0xff;
					address = address| (map.cpureadu(pointer)<<8);
					instruction_cycle++;break;
				}
				case 6: {
					tempregister=0;
					executeOp();
					instruction_cycle=1;
				}
				}
			}
			}
		};break;
		case "(indirect),y":{
			switch(inst_type.get(current_instruction)){
			case 0: {
				switch(instruction_cycle){
				case 2: {
					pointer = map.cpureadu(program_counter);
					program_counter++;
					instruction_cycle++;break;
				}
				case 3: {
					address = map.cpureadu(pointer);
					instruction_cycle++;break;
				}
				case 4: {
					if(pointer+1>0xff){
						address |= (map.cpureadu(0)<<8);
					}
					else
						address = address | (map.cpureadu(pointer+1)<<8);
					lowpc = address&0xff00;
					address += Byte.toUnsignedInt(y_index_register);
					if(lowpc!=(address&0xff00)){
						brokenaddress=true;
					}
					instruction_cycle++;break;
				}
				case 5: {
					if(brokenaddress){
						brokenaddress = true;
						//System.out.println("ADDRESS IS BROKEN");
						tempregister=map.cpuread((address&0xff)|lowpc);
						//address-=0x100;
						address&=0xffff;
						instruction_cycle++;break;
					}
					else{
						tempregister = map.cpuread(address);
						instruction_cycle++;break;
						
					}
				}
				case 6: {
					if(brokenaddress){//broken
						tempregister=map.cpuread(address);
						brokenaddress = false;
						instruction_cycle++;break;
					}
					else{
						executeOp();
						current_instruction = getNextInstruction();
						program_counter++;
						instruction_cycle=2;break;
					}
				}
				case 7: {
					executeOp();
					current_instruction = getNextInstruction();
					program_counter++;
					instruction_cycle=2;break;
				}
				}
			};break;
			case 1: {
				switch(instruction_cycle){
				case 2: {
					pointer = map.cpureadu(program_counter);
					program_counter++;
					instruction_cycle++;break;
				}
				case 3: {
					address = map.cpureadu(pointer);
					instruction_cycle++;break;
				}
				case 4: {
					if(pointer+1>0xff){
						address |= (map.cpureadu(0)<<8);
					}
					else
						address = address | (map.cpureadu(pointer+1)<<8);
					lowpc = address&0xff00;
					address += Byte.toUnsignedInt(y_index_register);
					instruction_cycle++;break;
				}
				case 5: {
					if(address>0xffff)
						address&=0xffff;
					tempregister= map.cpuread(address);
					instruction_cycle++;break;
				}
				case 6: {
					tempregister = map.cpuread(address);
					instruction_cycle++;break;
				}
				case 7: {
					map.cpuwrite(address, tempregister);
					executeOp();
					instruction_cycle++;break;
				}
				case 8: {
					map.cpuwrite(address, tempregister);
					instruction_cycle = 1; break;
				}
				}
			};break;
			case 2: {
				switch(instruction_cycle){
				case 2: {
					pointer = map.cpureadu(program_counter);
					program_counter++;
					instruction_cycle++;break;
				}
				case 3: {
					address = map.cpureadu(pointer);
					instruction_cycle++;break;
				}
				case 4: {
					if(pointer+1>0xff){
						address |= (map.cpureadu(0)<<8);
					}
					else
						address = address | (map.cpureadu(pointer+1)<<8);
					lowpc=address&0xff00;
					address += Byte.toUnsignedInt(y_index_register);
					instruction_cycle++;break;
				}
				case 5: {
					tempregister=map.cpuread((address&0xff)|lowpc);
					instruction_cycle++;break;
				}
				case 6: {
					executeOp();
					//map.cpuwrite(address, tempregister);
					instruction_cycle = 1;break;
				}
				}
			};break;
			}
		};break;
		case "accumulator":{
			switch(instruction_cycle){
			case 2:{
				tempregister=accumulator;
				executeOp();
				accumulator = tempregister;
				instruction_cycle = 1;break;
			}
			}
		};break;
		case "implied": {
			executeOp();
		};break;
		case "relative":{
			switch(instruction_cycle){
			case 2: {
				tempregister = map.cpuread(program_counter);
				program_counter++;
				instruction_cycle++;break;
			}
			case 3: {
				executeOp();
				if(branchtaken){
					//program_counter+=tempregister;
					int high = program_counter&0xff00;
					int chigh = (program_counter+tempregister)&0xff00;
					if(nmirecieved!=0&&chigh!=high)
						nmirecieved=0;
					instruction_cycle++;break;
				}
				else{
					//System.out.println("Going passed the branch"+map.cpuread(0x2002));
					current_instruction = getNextInstruction();
					program_counter++;
					instruction_cycle=2;break;
				}
			}
			case 4: {
				int high = program_counter&0xff00;
				int chigh = (program_counter+tempregister)&0xff00;
				
				if(chigh!=high){
					//System.out.println("uhoh in the broken part");
					
					program_counter+=tempregister;
					instruction_cycle++;break;
					
				}
				else{
					program_counter+=tempregister;
					program_counter&=0xffff;
					current_instruction = getNextInstruction();
					program_counter++;
					instruction_cycle = 2;break;
				}
				
			}
			case 5: {
				program_counter&=0xffff;
				current_instruction = getNextInstruction();
				program_counter++;
				instruction_cycle = 2; break;
			}
			}
		};break;
		
		}}
	}
	private void executeOp(){
		switch(inst_name.get(current_instruction)){
		case "AAC": {
			System.out.println("Invalid instruction AAC");
			accumulator= (byte)(accumulator &tempregister);
			if(accumulator==0) ZFlag=true;else ZFlag = false;
			if(accumulator<0) NFlag=true;else NFlag = false;
			CFlag = NFlag;
		};break;
		case "ADC": {
			//System.out.println("IM IN ADC!");
			int sum = Byte.toUnsignedInt(accumulator) + Byte.toUnsignedInt(tempregister) + (CFlag?1:0);
			CFlag = sum>0xff?true:false;
			VFlag = (~(accumulator^tempregister)&(accumulator^sum)&0x80)==0?false:true;
			accumulator=(byte) sum;
			NFlag = accumulator<0?true:false;
			ZFlag = accumulator==0?true:false;
			/*
			int sign1 = accumulator&0x80;
			int out = Byte.toUnsignedInt(accumulator)+Byte.toUnsignedInt(tempregister)+(CFlag?1:0);
			int tsign = tempregister&0x80;
			accumulator = (byte) (tempregister +accumulator+ (CFlag?1:0));
			int sign2 = accumulator&0x80;
			if(sign1==tsign&&sign1!=sign2)VFlag = true;else VFlag = false;
			if(accumulator<0)NFlag = true;else NFlag = false;
			if(out>0xff)CFlag = true;else CFlag = false;
			if(accumulator==0)ZFlag = true;else ZFlag = false;*/
		};break;
		case "AND": {
			accumulator = (byte) (accumulator & tempregister);
			if(accumulator==0) ZFlag=true;else ZFlag = false;
			if(accumulator<0) NFlag=true;else NFlag = false;
			break;
		}
		case "ARR": {
			System.out.println("Invalid instruction ARR");
			accumulator=(byte) (accumulator&tempregister);
			int result = Byte.toUnsignedInt(accumulator);
			result>>=1;
			if(CFlag) result|= 0x80;
			accumulator = (byte) result;
			NFlag = accumulator<0;
			ZFlag = result==0;
			CFlag = ((accumulator&(0b1000000))!=0);
			VFlag = CFlag ^ ((accumulator&0b100000)!=0);
			
		};break;
		case "ASL": {
			int temp = Byte.toUnsignedInt(tempregister);
			if((tempregister&0x80)!=0) CFlag=true; else CFlag=false;
			tempregister = (byte) (temp<<1);
			if(tempregister==0) ZFlag = true;else ZFlag = false;
			if(tempregister<0) NFlag = true;else NFlag = false;
			break;
		}
		case "ASR": {
			accumulator = (byte) (accumulator & tempregister);
			CFlag = (accumulator&1)!=0;
			accumulator= (byte)(Byte.toUnsignedInt(accumulator)>>1);
			if(accumulator==0) ZFlag=true;else ZFlag = false;
			if(accumulator<0) NFlag=true;else NFlag = false;
			
		};break;
		case "ATX": {
			System.out.println("Invalid instruction ATX");

			x_index_register = accumulator=tempregister;
			//accumulator = (byte) (accumulator & tempregister);
			if(accumulator==0) ZFlag=true;else ZFlag = false;
			if(accumulator<0) NFlag=true;else NFlag = false;
		};break;
		case "AXS": {
			System.out.println("Invalid instruction AXS");
			int result = Byte.toUnsignedInt(x_index_register);
			result &= Byte.toUnsignedInt(accumulator);
			CFlag = result>=Byte.toUnsignedInt(tempregister);
			result-= tempregister;
			x_index_register = (byte) result;
			NFlag = x_index_register<0;
			ZFlag = x_index_register==0;
		};break;
		case "BCC": {
			if(!CFlag)
				branchtaken=true;
			else
				branchtaken=false;
			break;
		}
		case "BCS": {
			if(CFlag)
				branchtaken=true;
			else
				branchtaken=false;
			break;
		}
		case "BEQ": {
			if(ZFlag)
				branchtaken=true;
			else
				branchtaken=false;
			break;
		}
		case "BIT": {
			if((accumulator&tempregister)==0)ZFlag = true; else ZFlag = false;
			if((tempregister&0x80)!=0)NFlag = true; else NFlag = false;
			if((tempregister&0x40)!=0)VFlag = true; else VFlag = false;
		};break;
		case "BMI":{
			if(NFlag)
				branchtaken=true;
			else
				branchtaken=false;
		};break;
		case "BNE": {
			if(!ZFlag)
				branchtaken=true;
			else
				branchtaken=false;
		};break;
		case "BPL": {
			if(!NFlag)
				branchtaken=true;
			else
				branchtaken=false;
		};break;
		case "BRK": {
			switch(instruction_cycle){
			case 2: {
				if(nmi&&doNMI)
					nmihijack=true;
				//System.out.println("IN the BRK instruction!");
				program_counter++;
				instruction_cycle++;break;
			}
			case 3: {
				if(!(doNMI&&nmi))
					nmihijack = false;
				map.cpuwrite(Byte.toUnsignedInt(stack_pointer)+0x100, (byte)(program_counter>>8));
				stack_pointer--;
				instruction_cycle++;break;
			}
			case 4: {
				if(!(doNMI&&nmi))
					nmihijack = false;
				map.cpuwrite(Byte.toUnsignedInt(stack_pointer)+0x100, (byte)(program_counter&0xff));
				stack_pointer--;
				instruction_cycle++;break;
			}
			case 5: {
				BFlag = true;
				map.cpuwrite(Byte.toUnsignedInt(stack_pointer)+0x100, buildFlags());
				BFlag = false;
				if(nmihijack){
					current_instruction = 0x02;
					nmihijack=false;
				}
				stack_pointer--;
				instruction_cycle++;break;
			}
			case 6: {
				program_counter = map.cpureadu(0xfffe);
				instruction_cycle++;break;
			}
			case 7: {
				program_counter |= map.cpureadu(0xffff)<<8;
				IFlag = true;
				instruction_cycle = 1;break;
			}
			}
		};break;
		case "BVC": {
			if(!VFlag)
				branchtaken=true;
			else
				branchtaken=false;
		};break;
		case "BVS": {
			if(VFlag)
				branchtaken=true;
			else
				branchtaken=false;
		};break;
		case "CLC": {
			CFlag = false;
			instruction_cycle = 1;
		};break;
		case "CLD": {
			DFlag = false;
			instruction_cycle = 1;
		};break;
		case "CLI": {
			//if(map.control.checkDebug())
			//	System.out.println("Clearing IFlag scanline: "+map.ppu.scanline);
			IFlag = false;
			instruction_cycle=1;
			irqlatency = 2;
		};break;
		case "CLV": {
			VFlag = false;
			instruction_cycle=1;
		};break;
		case "CMP": {
			if(Byte.toUnsignedInt(accumulator)>=Byte.toUnsignedInt(tempregister)) CFlag = true;else CFlag = false;
			if(accumulator == tempregister) ZFlag = true;else ZFlag = false;
			if(((Byte.toUnsignedInt(accumulator)-Byte.toUnsignedInt(tempregister))&0x80)!=0) NFlag = true;else NFlag = false;
		};break;
		case "CPX": {
			if(Byte.toUnsignedInt(x_index_register)>=Byte.toUnsignedInt(tempregister)) CFlag = true;else CFlag = false;
			if(x_index_register == tempregister) ZFlag = true;else ZFlag = false;
			if(((Byte.toUnsignedInt(x_index_register)-Byte.toUnsignedInt(tempregister))&0x80)!=0) NFlag = true;else NFlag = false;
		};break;
		case "CPY": {
			if(Byte.toUnsignedInt(y_index_register)>=Byte.toUnsignedInt(tempregister)) CFlag = true;else CFlag = false;
			if(y_index_register == tempregister) ZFlag = true;else ZFlag = false;
			if(((Byte.toUnsignedInt(y_index_register)-Byte.toUnsignedInt(tempregister))&0x80)!=0) NFlag = true;else NFlag = false;
		};break;
		case "DCP": {
			System.out.println("Invalid instruction DCP");
			tempregister--;
			if(Byte.toUnsignedInt(accumulator)>=Byte.toUnsignedInt(tempregister)) CFlag = true;else CFlag = false;
			if(accumulator == tempregister) ZFlag = true;else ZFlag = false;
			if(((Byte.toUnsignedInt(accumulator)-Byte.toUnsignedInt(tempregister))&0x80)!=0) NFlag = true;else NFlag = false;
		};break;
		case "DEC": {
			tempregister--;
			if(tempregister==0) ZFlag = true;else ZFlag = false;
			if(tempregister<0) NFlag = true;else NFlag = false;
		};break;
		case "DEX": {
			x_index_register--;
			if(x_index_register==0) ZFlag = true;else ZFlag = false;
			if(x_index_register<0) NFlag = true;else NFlag = false;
			//current_instruction = map.cpuread(program_counter);
			//program_counter++;
			instruction_cycle = 1;
		};break;
		case "DEY": {
			y_index_register--;
			if(y_index_register==0) ZFlag = true;else ZFlag = false;
			if(y_index_register<0) NFlag = true;else NFlag = false;
			//current_instruction = map.cpuread(program_counter);
			//program_counter++;
			instruction_cycle = 1;
		};break;
		case "EOR": {
			accumulator = (byte) (accumulator ^ tempregister);
			if(accumulator == 0) ZFlag = true; else ZFlag = false;
			if(accumulator<0)NFlag = true;else NFlag = false;
		};break;
		case "INC": {
			tempregister++;
			if(tempregister==0) ZFlag = true;else ZFlag = false;
			if(tempregister<0) NFlag = true;else NFlag = false;
		};break;
		case "INX": {
			x_index_register++;
			if(x_index_register==0) ZFlag = true;else ZFlag = false;
			if(x_index_register<0) NFlag = true;else NFlag = false;
			instruction_cycle = 1;
		};break;
		case "INY": {
			y_index_register++;
			if(y_index_register==0) ZFlag = true;else ZFlag = false;
			if(y_index_register<0) NFlag = true;else NFlag = false;
			instruction_cycle = 1;
			
		};break;
		case "IRQ": {
			//System.out.println("Doing the IRQ cycle: "+instruction_cycle);
			switch(instruction_cycle){
			case 2: {
				//program_counter++;
				instruction_cycle++;
			}
			case 3: {
				map.cpuwrite(Byte.toUnsignedInt(stack_pointer)+0x100, (byte)(program_counter>>8));
				stack_pointer--;
				instruction_cycle++;break;
			}
			case 4: {
				map.cpuwrite(Byte.toUnsignedInt(stack_pointer)+0x100, (byte)(program_counter&0xff));
				stack_pointer--;
				instruction_cycle++;break;
			}
			case 5: {
				//if(irqsetdelay==0)
				//	IFlag = true;
				//IFlag = false;
				map.cpuwrite(Byte.toUnsignedInt(stack_pointer)+0x100, buildFlags());
				stack_pointer--;
				instruction_cycle++;break;
			}
			case 6: {
				//System.out.println(Integer.toHexString(map.cpureadu(0xfffa)));
				program_counter = map.cpureadu(0xfffe);
				instruction_cycle++;break;
			}
			case 7: {
				//System.out.println(Integer.toHexString(map.cpureadu(0xfffb)));
				program_counter = (map.cpureadu(0xffff)<<8)|program_counter;
				//System.out.println(Integer.toHexString(program_counter));
				IFlag = true;
				instruction_cycle = 1;break;
			}
			}
		};break;
		case "ISB": {
			tempregister++;
			System.out.println("BROKEN");
			int sum = Byte.toUnsignedInt(accumulator) - Byte.toUnsignedInt(tempregister) - (CFlag?0:1);
			CFlag = (sum>>8 ==0);
			VFlag = (((accumulator^tempregister)&0x80)!=0)&&(((accumulator^sum)&0x80)!=0);
			accumulator=(byte) (sum&0xff);
			NFlag = accumulator<0?true:false;
			ZFlag = accumulator==0?true:false;

		};break;
		case "JMP": {
			switch(instruction_cycle){
			case 2: {
				address = map.cpureadu(program_counter);
				program_counter++;
				instruction_cycle++;break;
			}
			case 3: {
				address = address | (map.cpureadu(program_counter)<<8);
				program_counter++;
				instruction_cycle++;break;
			}
			case 4: {
				program_counter = map.cpureadu(address);
				instruction_cycle++;break;
			}
			case 5: {
				if(((address+1)&0xFF) ==0) address = address&0xFF00;else address++;
				program_counter = program_counter | (map.cpureadu(address)<<8);
				instruction_cycle =1; break;
			}
			}
		};break;
		case "JSR": {
			switch(instruction_cycle){
			case 2: {
				address = map.cpureadu(program_counter);
				program_counter++;
				instruction_cycle++;return;
			}
			case 3: {
				//address = address | (map.cpureadu(program_counter)<<8);
				//program_counter++;
				instruction_cycle++;break;
			}
			case 4: {
				map.cpuwrite(Byte.toUnsignedInt(stack_pointer)+0x0100, (byte)(program_counter>>>8));
				stack_pointer--;
				instruction_cycle++;break;
			}
			case 5: {
				map.cpuwrite(Byte.toUnsignedInt(stack_pointer)+0x0100, (byte) (program_counter & 0x00FF));
				stack_pointer--;
				instruction_cycle++;break;
			}
			case 6: {
				address = address | (map.cpureadu(program_counter)<<8);
				//System.out.println(Integer.toHexString(address));
				//memory.printMemory(0x100+Byte.toUnsignedInt(stack_pointer), 0x20);
				program_counter =  address;
				instruction_cycle = 1;break;
			}
			}
		}; break;
		case "LAX": {
			System.out.println("Invalid instruction LAX");
			x_index_register = accumulator = tempregister;
			NFlag = accumulator<0;
			ZFlag = accumulator==0;
		};break;
		case "LDA": {
			accumulator = tempregister;
			if(accumulator == 0) ZFlag = true;else ZFlag = false;
			if(accumulator <0)NFlag = true;else NFlag = false;
		};break;
		case "LDX": {
			x_index_register = tempregister;
			if(x_index_register == 0) ZFlag = true;else ZFlag = false;
			if(x_index_register<0)NFlag = true;else NFlag = false;
		};break;
		case "LDY": {
			y_index_register = tempregister;
			if(y_index_register == 0) ZFlag = true;else ZFlag = false;
			if(y_index_register<0)NFlag = true;else NFlag = false;
		};break;
		case "LSR": {
			if((tempregister&1) >0)CFlag = true;else CFlag = false;
			tempregister=(byte) (Byte.toUnsignedInt(tempregister)>>>1);
			if(tempregister==0) ZFlag = true;else ZFlag = false;
			if(tempregister<0) NFlag = true;else NFlag = false;
		};break;
		case "NMI": {
			switch(instruction_cycle){
			case 2: {
				//program_counter++;
				instruction_cycle++;
			}
			case 3: {
				map.cpuwrite(Byte.toUnsignedInt(stack_pointer)+0x100, (byte)(program_counter>>8));
				stack_pointer--;
				instruction_cycle++;break;
			}
			case 4: {
				map.cpuwrite(Byte.toUnsignedInt(stack_pointer)+0x100, (byte)(program_counter&0xff));
				stack_pointer--;
				instruction_cycle++;break;
			}
			case 5: {
				BFlag = false;
				map.cpuwrite(Byte.toUnsignedInt(stack_pointer)+0x100, buildFlags());
				stack_pointer--;
				instruction_cycle++;break;
			}
			case 6: {
				//System.out.println(Integer.toHexString(map.cpureadu(0xfffa)));
				program_counter = map.cpureadu(0xfffa);
				instruction_cycle++;break;
			}
			case 7: {
				//System.out.println(Integer.toHexString(map.cpureadu(0xfffb)));
				program_counter = (map.cpureadu(0xfffb)<<8)|program_counter;
				//System.out.println(Integer.toHexString(program_counter));
				IFlag = true;
				instruction_cycle = 1;break;
			}
			}
		};break;
		case "NOP":case "NOP12":case "NOP13":case "NOP14":case "NOP15":case "NOP16":case "NOP17": {
			switch(instruction_cycle){
			case 2: {
				instruction_cycle++;break;
			}
			case 3: {
				current_instruction = getNextInstruction();
				program_counter++;
				instruction_cycle=2;break;
			}
			}
		};break;
		case "SKB": {
			System.out.println("Invalid instruction SKB");
			if(instruction_cycle==inst_type.get(current_instruction)){
				program_counter++;
				current_instruction = getNextInstruction();
				program_counter++;
				instruction_cycle=2;
			}
			else{
				instruction_cycle++;
			}
		};break;
		case "SKW": {
			System.out.println("Invalid instruction SKW");
			if(instruction_cycle==inst_type.get(current_instruction)){
				program_counter+=2;
				current_instruction= getNextInstruction();
				program_counter++;
				instruction_cycle=2;
			}
			else
				instruction_cycle++;
		};break;
		case "ORA": {
			accumulator = (byte) (accumulator | tempregister);
			if(accumulator == 0) ZFlag = true;else ZFlag = false;
			if(accumulator<0)NFlag = true;else NFlag = false;
		};break;
		case "PHA": {
			switch(instruction_cycle){
			case 2: {
				instruction_cycle++;break;
			}
			case 3: {
				map.cpuwrite(Byte.toUnsignedInt(stack_pointer)+0x0100, accumulator);
				stack_pointer--;
				instruction_cycle = 1;break;
			}
			}
		};break;
		case "PHP": {
			switch(instruction_cycle){
			case 2: {
				instruction_cycle++;break;
			}
			case 3: {
				BFlag = true;
				byte x =buildFlags();
				//if(map.control.checkDebug()&&IFlag)
				//	System.out.println("PHP with IFlag=true  Scanline: "+map.ppu.scanline);
				
				map.cpuwrite(Byte.toUnsignedInt(stack_pointer)+0x0100,x);
				BFlag = false;
				stack_pointer--;
				//memory.printMemory(0x0100+Byte.toUnsignedInt(stack_pointer), 50);
				instruction_cycle = 1;break;
			}
			}
			
		};break;
		case "PLA": {
			switch(instruction_cycle){
			case 2: {
				instruction_cycle++;break;
			}
			case 3: {
				stack_pointer++;
				instruction_cycle++;break;
			}
			case 4: {
				accumulator = map.cpuread(Byte.toUnsignedInt(stack_pointer)+0x0100);
				//memory.printMemory(0x0100+Byte.toUnsignedInt(stack_pointer), 50);
				if(accumulator==0)ZFlag = true; else ZFlag = false;
				if(accumulator<0)NFlag = true; else NFlag = false;
				instruction_cycle = 1;break;
			}
			}
		};break;
		case "PLP": {
			switch(instruction_cycle){
			case 2: {
				instruction_cycle++;break;
			}
			case 3: {
				stack_pointer++;
				instruction_cycle++;break;
			}
			case 4: {
				boolean temp = IFlag;
				setFlags( map.cpuread(Byte.toUnsignedInt(stack_pointer)+0x0100));
				if(IFlag !=temp&&IFlag==true){
					IFlag = false;
					irqsetdelay = 1;
				}
				else if(IFlag!=temp&&IFlag ==false){
					//IFlag = true;
					irqlatency = 2;
				}
				instruction_cycle = 1;break;
			}
			}
		};break;
		case "RLA": {
			System.out.println("Invalid instruction RLA");
			int tcarry = tempregister<0?1:0;
			tempregister = (byte) (tempregister<<1);
			tempregister = (byte) (tempregister | (CFlag?1:0));
			CFlag = tcarry==1?true:false;
			if(tempregister ==0)ZFlag = true;else ZFlag = false;
			if(tempregister<0)NFlag = true; else NFlag = false;
			
			accumulator = (byte) (accumulator & tempregister);
			if(accumulator==0) ZFlag=true;else ZFlag = false;
			if(accumulator<0) NFlag=true;else NFlag = false;
			
		};break;
		case "ROL": {
			int tcarry = tempregister<0?1:0;
			tempregister = (byte) (tempregister<<1);
			tempregister = (byte) (tempregister | (CFlag?1:0));
			CFlag = tcarry==1?true:false;
			if(tempregister ==0)ZFlag = true;else ZFlag = false;
			if(tempregister<0)NFlag = true; else NFlag = false;
		};break;
		case "ROR": {
			int tcarry = tempregister&0x01;
			tempregister= (byte) (Byte.toUnsignedInt(tempregister)>>>1);
			tempregister = (byte) (tempregister | (CFlag?0x80:0));
			CFlag = tcarry!=0?true:false;
			if(tempregister==0)ZFlag = true; else ZFlag = false;
			if(tempregister<0)NFlag = true; else NFlag = false;
		};break;
		case "RRA": {
			System.out.println("Invalid instruction RRA");
			if(CFlag){
				CFlag = (tempregister&1)!=0;
				tempregister = (byte) ((Byte.toUnsignedInt(tempregister)>>1) | 0x80);
			}
			else{
				CFlag = (tempregister&1)!=0;
				tempregister = (byte) (Byte.toUnsignedInt(tempregister)>>1);
			}
			
			int sum = Byte.toUnsignedInt(accumulator) + Byte.toUnsignedInt(tempregister) + (CFlag?1:0);
			CFlag = sum>0xff?true:false;
			VFlag = (~(accumulator^tempregister)&(accumulator^sum)&0x80)==0?false:true;
			accumulator=(byte) sum;
			NFlag = accumulator<0?true:false;
			ZFlag = accumulator==0?true:false;
			
		};break;
		case "RTI": {
			switch(instruction_cycle){
			case 2:{
				//program_counter++;
				instruction_cycle++;break;
			}
			case 3: {
				stack_pointer++;
				instruction_cycle++;break;
			}
			case 4: {
				setFlags(map.cpuread(Byte.toUnsignedInt(stack_pointer)+0x0100));
				stack_pointer++;
				instruction_cycle++;break;
			}
			case 5: {
				//System.out.println("returning from interrupt");
				program_counter = map.cpureadu(Byte.toUnsignedInt(stack_pointer)+0x0100);
				stack_pointer++;
				instruction_cycle++;break;
			}
			case 6: {
				program_counter = program_counter| (map.cpureadu(Byte.toUnsignedInt(stack_pointer)+0x0100)<<8);
				instruction_cycle = 1;
				irqsetdelay=-1;
				break;
				
			}
			}
		};break;
		case "RTS":{
			switch(instruction_cycle){
			case 2: {
				instruction_cycle++;break;
			}
			case 3: {
				//memory.printMemory(0x100+Byte.toUnsignedInt(stack_pointer), 0x20);
				stack_pointer++;
				instruction_cycle++;break;
			}
			case 4: {
				program_counter = 0;
				program_counter = map.cpureadu(Byte.toUnsignedInt(stack_pointer)+0x0100);
				stack_pointer++;
				instruction_cycle++;break;
			}
			case 5: {
				program_counter = (program_counter | (map.cpureadu(Byte.toUnsignedInt(stack_pointer)+0x0100)<<8));
				instruction_cycle++;break;
			}
			case 6: {
				program_counter++;
				instruction_cycle=1;break;
			}
			}
		};break;
		case "SAX": {
			System.out.println("Invalid instruction SAX");
			tempregister = (byte) (x_index_register&accumulator);
			map.cpuwrite(address, tempregister);
		};break;
		case "SBC": {//flags are broken
			tempregister=(byte) ~tempregister;
			int sum = Byte.toUnsignedInt(accumulator) + Byte.toUnsignedInt(tempregister) + (CFlag?1:0);
			CFlag = sum>0xff?true:false;
			VFlag = (~(accumulator^tempregister)&(accumulator^sum)&0x80)==0?false:true;
			accumulator=(byte) sum;
			NFlag = accumulator<0?true:false;
			ZFlag = accumulator==0?true:false;
			
			/*int sign = accumulator&0x80;
			int temp = (int)(accumulator) - (int)(tempregister) - (1 - (CFlag?1:0));
			accumulator = (byte) (accumulator - tempregister - (1-(CFlag?1:0)));
			int sign2 = accumulator&0x80;
			if(accumulator <0)NFlag = true;else NFlag = false;
			if(accumulator==0)ZFlag = true;else ZFlag = false;
			if(temp<-128 ||temp>127)VFlag = true;else VFlag = false;
			if(sign!=sign2&&sign==0) CFlag = false; else CFlag = true;
			//if((sign!=0&&sign2==0)||(sign!=0&&sign2==0&&tempregister>0)) CFlag = false; else CFlag = true;
			*/
		};break;
		case "SEC": {
			CFlag = true;
			instruction_cycle = 1;
		};break;
		case "SED": {
			DFlag = true;
			instruction_cycle = 1;
		};break;
		case "SEI": {
			//IFlag = true;
			if(map.control.checkDebug())
				System.out.println("Setting IFlag to true scanline: "+map.ppu.scanline);
			irqsetdelay = 1;
			instruction_cycle = 1;
		};break;
		case "SHX": {
			System.out.println("Invalid instruction SHX");
			switch(instruction_cycle){
			case 2: 
				address = map.cpureadu(program_counter);
				program_counter++;
				instruction_cycle++;
				break;
			case 3:
				address|= map.cpureadu(program_counter)<<8;
				program_counter++;
				instruction_cycle++;
				break;
			case 4:
				map.cpureadu(address);
				instruction_cycle++;
				break;
			case 5:
				int t =(Byte.toUnsignedInt(x_index_register)&((address>>8)+1))&0xff;
				lowpc = address&0xff00;
				address+=Byte.toUnsignedInt(y_index_register);
				address = (address&0xff)|lowpc;
				map.cpuwrite(address&0xffff,(byte)t);
				instruction_cycle=1;
				break;	
			}
		};break;
		case "SHY": {
			System.out.println("Invalid instruction SHY");
			switch(instruction_cycle){
			case 2: 
				address = map.cpureadu(program_counter);
				program_counter++;
				instruction_cycle++;
				break;
			case 3:
				address|= map.cpureadu(program_counter)<<8;
				program_counter++;
				instruction_cycle++;
				break;
			case 4:
				map.cpureadu(address);
				instruction_cycle++;
				break;
			case 5:
				int t =(Byte.toUnsignedInt(y_index_register)&((address>>8)+1))&0xff;
				lowpc = address&0xff00;
				address+=Byte.toUnsignedInt(x_index_register);
				address = (address&0xff)|lowpc;
				map.cpuwrite(address&0xffff,(byte)t);
				instruction_cycle=1;
				break;		
			}
			
		};break;
		case "SLO": {
			System.out.println("Invalid instruction SLO");
			CFlag = (tempregister&0x80)!=0;
			tempregister<<=1;
			accumulator|=tempregister;
			NFlag = accumulator<0;
			ZFlag = accumulator==0;
		};break;
		case "SRE": {
			System.out.println("Invalid instruction SRE");
			int result = Byte.toUnsignedInt(tempregister);
			CFlag = (result&1)!=0;
			result>>=1;
			accumulator ^=(byte)result;
			tempregister = (byte)result;
			//map.cpuwrite(address, (byte)result);
			NFlag = accumulator<0;
			ZFlag = accumulator==0;
		};break;
		case "STA": {
			//tempregister=accumulator;
			map.cpuwrite(address, accumulator);
		};break;
		case "STX": {
			//tempregister=x_index_register;
			map.cpuwrite(address, x_index_register);
		};break;
		case "STY": {
			//tempregister=y_index_register;
			map.cpuwrite(address, y_index_register);
		};break;
		case "TAX": {
			switch(instruction_cycle){
			case 2: {
				x_index_register= accumulator;
				if(x_index_register ==0) ZFlag = true;else ZFlag = false;
				if(x_index_register <0) NFlag = true;else NFlag = false;
				instruction_cycle = 1;break;
			}
			}
		};break;
		case "TAY": {
			switch(instruction_cycle){
			case 2: {
				y_index_register=accumulator;
				if(y_index_register ==0) ZFlag = true;else ZFlag = false;
				if(y_index_register <0) NFlag = true;else NFlag = false;
				instruction_cycle = 1;break;
			}
			}
		};break;
		case "TSX": {
			switch(instruction_cycle){
			case 2: {
				x_index_register = stack_pointer;
				if(x_index_register ==0) ZFlag = true;else ZFlag = false;
				if(x_index_register <0) NFlag = true;else NFlag = false;				
				instruction_cycle = 1;break;
			}
			}
		};break;
		case "TXA": {
			switch(instruction_cycle){
			case 2: {
				accumulator = x_index_register;
				if(accumulator ==0) ZFlag = true;else ZFlag = false;
				if(accumulator <0) NFlag = true;else NFlag = false;
				instruction_cycle = 1;break;
			}
			}
		};break;
		case "TXS": {
			switch(instruction_cycle){
			case 2: {
				stack_pointer = x_index_register;				
				instruction_cycle = 1;break;
			}
			}
		};break;
		case "TYA": {
			switch(instruction_cycle){
			case 2: {
				accumulator = y_index_register;
				if(accumulator ==0)ZFlag = true; else ZFlag = false;
				if(accumulator <0)NFlag = true; else NFlag = false;
				instruction_cycle = 1;break;
			}
			}
		};break;
		default: {
			//System.out.println("INVALID INSTRUCTION:"+Integer.toHexString(Byte.toUnsignedInt(current_instruction))
			//+" "+inst_name.get(current_instruction)
			//+" program counter: "+Integer.toHexString(program_counter));
			//memory.printMemory(0, 0x5);
			//program_counter++;
			//instruction_cycle=1;
		};
		}
	}
	
}
