package com;
import java.util.Hashtable;
import java.util.Scanner;

import mappers.Mapper;

public class CPU_6502 {
	Mapper map;
	boolean showInvalid=false;
	/*immediate 0
	zero 1
	zerox 2
	zeroy 3
	absolute 4
	absolutex 5
	absolutey 6
	(indirect,x) 7
	(indirect),y 8
	accumulator 9
	relative 10
	implied 11*/
	
	
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
	//other stuff
	private boolean brokenaddress = false;
	int instruction_cycle;
	public byte current_instruction;
    int current_inst_mode;
    int current_inst_type;
	private byte tempregister;
	int address;
	private int pointer;
	private boolean branchtaken;
	int lowpc;	
	public boolean writeDMA = false;
	public boolean doNMI = false;
	public int doIRQ = 0;
	boolean nmihijack;
	//private Memory memory;
	//private Hashtable<Byte,Integer> inst_type;//read/write/both
        private int[] inst_type = new int[256];
	//private Hashtable<Byte, Integer> inst_mode;//memory access
	private int[] inst_mode = new int[256];
        public Hashtable<Byte,String> inst_name;//instruction name
	public CPU_6502(Mapper mapper) {
		map = mapper;
		Scanner s;
		//inst_type= new Hashtable<Byte,Integer>();
		//inst_mode = new Hashtable<Byte,Integer>();
		inst_name = new Hashtable<Byte,String>();
		//try {
			//s = new Scanner(System.in);
			s = new Scanner((this.getClass().getResourceAsStream("cpu_instructions.txt")));
			while(s.hasNext()){
				byte x = Integer.valueOf(s.next(), 16).byteValue();
				int mode = s.nextInt();
				int type = s.nextInt();
				//inst_type.put(x,type);
                                inst_type[Byte.toUnsignedInt(x)]=type;
				//inst_mode.put(x, mode);
                                inst_mode[Byte.toUnsignedInt(x)]=mode;
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
            //current_inst_mode = inst_mode.get((byte)0x02);
            current_inst_mode = inst_mode[2];
            //current_inst_type = inst_type.get((byte)0x02);
			current_inst_type = inst_type[2];
                        return 0x02;
		}
		else if(nmi&&nmirecieved!=0){
			nmirecieved--;
            //current_inst_mode = inst_mode.get(map.cpuread(program_counter));
            current_inst_mode = inst_mode[map.cpureadu(program_counter)];
            //current_inst_type = inst_type.get(map.cpuread(program_counter));
            current_inst_type = inst_type[map.cpureadu(program_counter)];
			return map.cpuread(program_counter);
		}
		else if(doIRQ>0&&irqrecieved!=0){
			irqrecieved--;
			byte b = map.cpuread(program_counter);
            //current_inst_mode = inst_mode.get(map.cpuread(program_counter));
            current_inst_mode = inst_mode[map.cpureadu(program_counter)];
            //current_inst_type = inst_type.get(map.cpuread(program_counter));
            current_inst_type = inst_type[map.cpureadu(program_counter)];
			return map.cpuread(program_counter);
		}
		else if(doIRQ>0&&!IFlag&&irqlatency==0){//&&irqrecieved==0){
			//System.out.println("Executing IRQ ppu SL: "+map.ppu.scanline+" cycle: "+map.ppu.pcycle 
			//		+" Prev inst: "+inst_name.get(current_instruction));
			program_counter--;
			//doIRQ= false;
            //current_inst_mode = inst_mode.get((byte)0x12);
            current_inst_mode = inst_mode[0x12];
            //current_inst_type = inst_type.get((byte)0x12);
            current_inst_type = inst_type[0x12];
			return 0x12;
		}
		else{
			if(current_instruction == 0x12)
				dodebug = true;
			else
				dodebug = false;
			//System.out.println("waiting for irqlatency");
			//current_inst_mode = inst_mode.get(map.cpuread(program_counter));
            current_inst_mode = inst_mode[map.cpureadu(program_counter)];
            //current_inst_type = inst_type.get(map.cpuread(program_counter));
            current_inst_type = inst_type[map.cpureadu(program_counter)];
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
			//if(!inst_mode.containsKey(current_instruction)){
			//	debug(0);
			//	map.printMemory(0,0x20);
				//memory.printMemory(0x3a0, 0x20);
				//memory.printMemory(0, 0x100);
			//	System.out.println(Integer.toHexString(current_instruction)+" Scanline: "+map.ppu.scanline+" cycle: "+map.ppu.pcycle);
			//}
		switch(current_inst_mode){
		case 0:{
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
		case 1:
			switch(current_inst_type){
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
		case 2: case 3:
			switch(current_inst_type){
			case 0:
				switch(instruction_cycle){
				case 2:
					address = map.cpureadu(program_counter);
					program_counter++;
					instruction_cycle++;
					break;
				case 3:
					if(current_inst_mode==2)
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
					if(current_inst_mode==2)
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
		case 4:
			switch(current_inst_type){
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
					map.cpuwrite(address,tempregister);
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
		case 5: case 6: {
			switch(current_inst_type){
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
					if(current_inst_mode==5)
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
					if(current_inst_mode==5)
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
					if(current_inst_mode==5)
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
		case 7:{
			switch(current_inst_type){
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
		case 8:{
			switch(current_inst_type){
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
		case 9:{
			switch(instruction_cycle){
			case 2:{
				tempregister=accumulator;
				executeOp();
				accumulator = tempregister;
				instruction_cycle = 1;break;
			}
			}
		};break;
		case 10:{
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
		case 11: {
			executeOp();
		};break;
		
		}}
	}
	private void executeOp(){
		switch(Byte.toUnsignedInt(current_instruction)){
		case 0x0b: case 0x2b:{
		//case "AAC": {
			if(showInvalid)
			System.out.println("Invalid instruction AAC");
			accumulator= (byte)(accumulator &tempregister);
			if(accumulator==0) ZFlag=true;else ZFlag = false;
			if(accumulator<0) NFlag=true;else NFlag = false;
			CFlag = NFlag;
		};break;
		case 0x69: case 0x65: case 0x75: case 0x6d: case 0x7d: case 0x79:case 0x61: case 0x71:{
		//case "ADC": {
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
		case 0x29: case 0x25: case 0x35: case 0x2d: case 0x3d: case 0x39: case 0x21: case 0x31:{
		//case "AND":  {
			accumulator = (byte) (accumulator & tempregister);
			if(accumulator==0) ZFlag=true;else ZFlag = false;
			if(accumulator<0) NFlag=true;else NFlag = false;
		};break;
		case 0x6b:{
		//case "ARR": {
			if(showInvalid)
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
		case 0x0a: case 0x06: case 0x16: case 0x0e: case 0x1e:{
		//case "ASL": {
			int temp = Byte.toUnsignedInt(tempregister);
			if((tempregister&0x80)!=0) CFlag=true; else CFlag=false;
			tempregister = (byte) (temp<<1);
			if(tempregister==0) ZFlag = true;else ZFlag = false;
			if(tempregister<0) NFlag = true;else NFlag = false;
		};break;
		case 0x4b:{
		//case "ASR": {
			accumulator = (byte) (accumulator & tempregister);
			CFlag = (accumulator&1)!=0;
			accumulator= (byte)(Byte.toUnsignedInt(accumulator)>>1);
			if(accumulator==0) ZFlag=true;else ZFlag = false;
			if(accumulator<0) NFlag=true;else NFlag = false;
			
		};break;
		case 0xab:{
		//case "ATX": {
			if(showInvalid)System.out.println("Invalid instruction ATX");

			x_index_register = accumulator=tempregister;
			//accumulator = (byte) (accumulator & tempregister);
			if(accumulator==0) ZFlag=true;else ZFlag = false;
			if(accumulator<0) NFlag=true;else NFlag = false;
		};break;
		case 0xcb:{
		//case "AXS": {
			if(showInvalid)System.out.println("Invalid instruction AXS");
			int result = Byte.toUnsignedInt(x_index_register);
			result &= Byte.toUnsignedInt(accumulator);
			CFlag = result>=Byte.toUnsignedInt(tempregister);
			result-= tempregister;
			x_index_register = (byte) result;
			NFlag = x_index_register<0;
			ZFlag = x_index_register==0;
		};break;
		case 0x90:{
		//case "BCC": {
			if(!CFlag)
				branchtaken=true;
			else
				branchtaken=false;
		};break;
		case 0xb0:{
		//case "BCS": {
			if(CFlag)
				branchtaken=true;
			else
				branchtaken=false;
		};break;
		case 0xf0:{
		//case "BEQ": {
			if(ZFlag)
				branchtaken=true;
			else
				branchtaken=false;
		};break;
		case 0x24: case 0x2c:{
		//case "BIT": {
			if((accumulator&tempregister)==0)ZFlag = true; else ZFlag = false;
			if((tempregister&0x80)!=0)NFlag = true; else NFlag = false;
			if((tempregister&0x40)!=0)VFlag = true; else VFlag = false;
		};break;
		case 0x30:{
		//case "BMI":{
			if(NFlag)
				branchtaken=true;
			else
				branchtaken=false;
		};break;
		case 0xd0:{
		//case "BNE": {
			if(!ZFlag)
				branchtaken=true;
			else
				branchtaken=false;
		};break;
		case 0x10:{
		//case "BPL": {
			if(!NFlag)
				branchtaken=true;
			else
				branchtaken=false;
		};break;
		case 0x0:{
		//case "BRK": {
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
		case 0x50:{
		//case "BVC": {
			if(!VFlag)
				branchtaken=true;
			else
				branchtaken=false;
		};break;
		case 0x70:{
		//case "BVS": {
			if(VFlag)
				branchtaken=true;
			else
				branchtaken=false;
		};break;
		case 0x18:{
		//case "CLC": {
			CFlag = false;
			instruction_cycle = 1;
		};break;
		case 0xd8:{
		//case "CLD": {
			DFlag = false;
			instruction_cycle = 1;
		};break;
		case 0x58:{
		//case "CLI": {
			//if(map.control.checkDebug())
			//	System.out.println("Clearing IFlag scanline: "+map.ppu.scanline);
			IFlag = false;
			instruction_cycle=1;
			irqlatency = 2;
		};break;
		case 0xb8:{
		//case "CLV": {
			VFlag = false;
			instruction_cycle=1;
		};break;
		case 0xc9: case 0xc5: case 0xd5: case 0xcd: case 0xdd: case 0xd9: case 0xc1: case 0xd1:{
		//case "CMP": {
			if(Byte.toUnsignedInt(accumulator)>=Byte.toUnsignedInt(tempregister)) CFlag = true;else CFlag = false;
			if(accumulator == tempregister) ZFlag = true;else ZFlag = false;
			if(((Byte.toUnsignedInt(accumulator)-Byte.toUnsignedInt(tempregister))&0x80)!=0) NFlag = true;else NFlag = false;
		};break;
		case 0xe0: case 0xe4: case 0xec:{
		//case "CPX": {
			if(Byte.toUnsignedInt(x_index_register)>=Byte.toUnsignedInt(tempregister)) CFlag = true;else CFlag = false;
			if(x_index_register == tempregister) ZFlag = true;else ZFlag = false;
			if(((Byte.toUnsignedInt(x_index_register)-Byte.toUnsignedInt(tempregister))&0x80)!=0) NFlag = true;else NFlag = false;
		};break;
		case 0xc0: case 0xc4: case 0xcc:{
		//case "CPY": {
			if(Byte.toUnsignedInt(y_index_register)>=Byte.toUnsignedInt(tempregister)) CFlag = true;else CFlag = false;
			if(y_index_register == tempregister) ZFlag = true;else ZFlag = false;
			if(((Byte.toUnsignedInt(y_index_register)-Byte.toUnsignedInt(tempregister))&0x80)!=0) NFlag = true;else NFlag = false;
		};break;
		case 0xc3: case 0xc7: case 0xcf: case 0xd3: case 0xd7: case 0xdb: case 0xdf:{
		//case "DCP": {
			if(showInvalid)System.out.println("Invalid instruction DCP");
			tempregister--;
			if(Byte.toUnsignedInt(accumulator)>=Byte.toUnsignedInt(tempregister)) CFlag = true;else CFlag = false;
			if(accumulator == tempregister) ZFlag = true;else ZFlag = false;
			if(((Byte.toUnsignedInt(accumulator)-Byte.toUnsignedInt(tempregister))&0x80)!=0) NFlag = true;else NFlag = false;
		};break;
		case 0xc6: case 0xd6: case 0xce: case 0xde:{
		//case "DEC": {
			tempregister--;
			if(tempregister==0) ZFlag = true;else ZFlag = false;
			if(tempregister<0) NFlag = true;else NFlag = false;
		};break;
		case 0xca:{
		//case "DEX": {
			x_index_register--;
			if(x_index_register==0) ZFlag = true;else ZFlag = false;
			if(x_index_register<0) NFlag = true;else NFlag = false;
			//current_instruction = map.cpuread(program_counter);
			//program_counter++;
			instruction_cycle = 1;
		};break;
		case 0x88:{
		//case "DEY": {
			y_index_register--;
			if(y_index_register==0) ZFlag = true;else ZFlag = false;
			if(y_index_register<0) NFlag = true;else NFlag = false;
			//current_instruction = map.cpuread(program_counter);
			//program_counter++;
			instruction_cycle = 1;
		};break;
		case 0x49: case 0x45: case 0x55: case 0x4d: case 0x5d: case 0x59: case 0x41: case 0x51:{
		//case "EOR": {
			accumulator = (byte) (accumulator ^ tempregister);
			if(accumulator == 0) ZFlag = true; else ZFlag = false;
			if(accumulator<0)NFlag = true;else NFlag = false;
		};break;
		case 0xe6: case 0xf6: case 0xee: case 0xfe:{
		//case "INC": {
			tempregister++;
			if(tempregister==0) ZFlag = true;else ZFlag = false;
			if(tempregister<0) NFlag = true;else NFlag = false;
		};break;
		case 0xe8:{
		//case "INX": {
			x_index_register++;
			if(x_index_register==0) ZFlag = true;else ZFlag = false;
			if(x_index_register<0) NFlag = true;else NFlag = false;
			instruction_cycle = 1;
		};break;
		case 0xc8:{
		//case "INY": {
			y_index_register++;
			if(y_index_register==0) ZFlag = true;else ZFlag = false;
			if(y_index_register<0) NFlag = true;else NFlag = false;
			instruction_cycle = 1;
			
		};break;
		case 0x12:{
		//case "IRQ": {
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
		case 0xe3: case 0xe7: case 0xef: case 0xf3: case 0xf7: case 0xfb: case 0xff:{
		//case "ISB": {
			tempregister++;
			if(showInvalid)System.out.println("Invalid instruction ISB");
			int sum = Byte.toUnsignedInt(accumulator) - Byte.toUnsignedInt(tempregister) - (CFlag?0:1);
			CFlag = (sum>>8 ==0);
			VFlag = (((accumulator^tempregister)&0x80)!=0)&&(((accumulator^sum)&0x80)!=0);
			accumulator=(byte) (sum&0xff);
			NFlag = accumulator<0?true:false;
			ZFlag = accumulator==0?true:false;

		};break;
		case 0x6c:{
		//case "JMP": {
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
		case 0x20:{
		//case "JSR": {
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
		case 0xa3: case 0xa7: case 0xaf: case 0xb3: case 0xb7: case 0xbf:{
		//case "LAX": {
			if(showInvalid)System.out.println("Invalid instruction LAX");
			x_index_register = accumulator = tempregister;
			NFlag = accumulator<0;
			ZFlag = accumulator==0;
		};break;
		case 0xa9: case 0xa5: case 0xb5: case 0xad: case 0xbd: case 0xb9: case 0xa1: case 0xb1:{
		//case "LDA": {
			accumulator = tempregister;
			if(accumulator == 0) ZFlag = true;else ZFlag = false;
			if(accumulator <0)NFlag = true;else NFlag = false;
		};break;
		case 0xa2: case 0xa6: case 0xb6: case 0xae: case 0xbe:{
		//case "LDX": {
			x_index_register = tempregister;
			if(x_index_register == 0) ZFlag = true;else ZFlag = false;
			if(x_index_register<0)NFlag = true;else NFlag = false;
		};break;
		case 0xa0: case 0xa4: case 0xb4: case 0xac: case 0xbc: {
		//case "LDY": {
			y_index_register = tempregister;
			if(y_index_register == 0) ZFlag = true;else ZFlag = false;
			if(y_index_register<0)NFlag = true;else NFlag = false;
		};break;
		case 0x4a: case 0x46: case 0x56: case 0x4e: case 0x5e:{
		//case "LSR": {
			if((tempregister&1) >0)CFlag = true;else CFlag = false;
			tempregister=(byte) (Byte.toUnsignedInt(tempregister)>>>1);
			if(tempregister==0) ZFlag = true;else ZFlag = false;
			if(tempregister<0) NFlag = true;else NFlag = false;
		};break;
		case 0x2:{
		//case "NMI": {
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
		case 0xea: case 0x1a: case 0x3a: case 0x5a: case 0x7a: case 0xda: case 0xfa:{
		//case "NOP":case "NOP12":case "NOP13":case "NOP14":case "NOP15":case "NOP16":case "NOP17": {
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
		case 0x04: case 0x14: case 0x34: case 0x44: case 0x54: case 0x64: case 0x74: case 0x80:
		case 0x82: case 0x89: case 0xc2: case 0xd4: case 0xe2: case 0xf4:{
		//case "SKB": {
			if(showInvalid)System.out.println("Invalid instruction SKB");
		};break;
		case 0x0c: case 0x1c: case 0x3c: case 0x5c: case 0x7c: case 0xdc: case 0xfc:{
		//case "SKW": {
			if(showInvalid)System.out.println("Invalid instruction SKW");
		};break;
		case 0x09: case 0x05: case 0x15: case 0x0d: case 0x1d: case 0x19: case 0x01: case 0x11:{
		//case "ORA": {
			accumulator = (byte) (accumulator | tempregister);
			if(accumulator == 0) ZFlag = true;else ZFlag = false;
			if(accumulator<0)NFlag = true;else NFlag = false;
		};break;
		case 0x48:{
		//case "PHA": {
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
		case 0x8:{
		//case "PHP": {
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
		case 0x68:{
		//case "PLA": {
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
		case 0x28:{
		//case "PLP": {
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
					//IFlag = false;
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
		case 0x23: case 0x27: case 0x2f: case 0x33: case 0x37: case 0x3b: case 0x3f:{
		//case "RLA": {
			if(showInvalid)System.out.println("Invalid instruction RLA");
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
		case 0x2a: case 0x26: case 0x36: case 0x2e: case 0x3e:{
		//case "ROL": {
			int tcarry = tempregister<0?1:0;
			tempregister = (byte) (tempregister<<1);
			tempregister = (byte) (tempregister | (CFlag?1:0));
			CFlag = tcarry==1?true:false;
			if(tempregister ==0)ZFlag = true;else ZFlag = false;
			if(tempregister<0)NFlag = true; else NFlag = false;
		};break;
		case 0x6a: case 0x66: case 0x76: case 0x6e: case 0x7e:{
		//case "ROR": {
			int tcarry = tempregister&0x01;
			tempregister= (byte) (Byte.toUnsignedInt(tempregister)>>>1);
			tempregister = (byte) (tempregister | (CFlag?0x80:0));
			CFlag = tcarry!=0?true:false;
			if(tempregister==0)ZFlag = true; else ZFlag = false;
			if(tempregister<0)NFlag = true; else NFlag = false;
		};break;
		case 0x63: case 0x67: case 0x6f: case 0x73: case 0x77: case 0x7b: case 0x7f:{
		//case "RRA": {
			if(showInvalid)System.out.println("Invalid instruction RRA");
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
		case 0x40:{
		//case "RTI": {
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
		case 0x60:{
		//case "RTS":{
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
		case 0x83: case 0x87: case 0x8f: case 0x97:{
		//case "SAX": {
			if(showInvalid)System.out.println("Invalid instruction SAX");
			tempregister = (byte) (x_index_register&accumulator);
			map.cpuwrite(address, tempregister);
		};break;
		case 0xe9: case 0xe5: case 0xf5: case 0xed: case 0xfd: case 0xf9: case 0xe1: case 0xf1: case 0xeb:{
		//case "SBC": {//flags are broken
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
		case 0x38:{
		//case "SEC": {
			CFlag = true;
			instruction_cycle = 1;
		};break;
		case 0xf8:{
		//case "SED": {
			DFlag = true;
			instruction_cycle = 1;
		};break;
		case 0x78:{
		//case "SEI": {
			//IFlag = true;
			if(map.control.checkDebug())
				System.out.println("Setting IFlag to true scanline: "+map.ppu.scanline);
			irqsetdelay = 1;
			instruction_cycle = 1;
		};break;
		case 0x9e:{
		//case "SHX": {
			if(showInvalid)System.out.println("Invalid instruction SHX");
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
		case 0x9c:{
		//case "SHY": {
			if(showInvalid)System.out.println("Invalid instruction SHY");
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
		case 0x03: case 0x07: case 0xf: case 0x13: case 0x17: case 0x1b: case 0x1f:{
		//case "SLO": {
			if(showInvalid)System.out.println("Invalid instruction SLO");
			CFlag = (tempregister&0x80)!=0;
			tempregister<<=1;
			accumulator|=tempregister;
			NFlag = accumulator<0;
			ZFlag = accumulator==0;
		};break;
		case 0x43: case 0x47: case 0x4f: case 0x53: case 0x57: case 0x5b: case 0x5f:{
		//case "SRE": {
			if(showInvalid)System.out.println("Invalid instruction SRE");
			int result = Byte.toUnsignedInt(tempregister);
			CFlag = (result&1)!=0;
			result>>=1;
			accumulator ^=(byte)result;
			tempregister = (byte)result;
			//map.cpuwrite(address, (byte)result);
			NFlag = accumulator<0;
			ZFlag = accumulator==0;
		};break;
		case 0x85: case 0x95: case 0x8d: case 0x9d: case 0x99: case 0x81: case 0x91:{
		//case "STA": {
			//tempregister=accumulator;
			map.cpuwrite(address, accumulator);
		};break;
		case 0x86: case 0x96: case 0x8e:{
		//case "STX": {
			//tempregister=x_index_register;
			map.cpuwrite(address, x_index_register);
		};break;
		case 0x84: case 0x94: case 0x8c:{
		//case "STY": {
			//tempregister=y_index_register;
			map.cpuwrite(address, y_index_register);
		};break;
		case 0xaa:{
		//case "TAX": {
			switch(instruction_cycle){
			case 2: {
				x_index_register= accumulator;
				if(x_index_register ==0) ZFlag = true;else ZFlag = false;
				if(x_index_register <0) NFlag = true;else NFlag = false;
				instruction_cycle = 1;break;
			}
			}
		};break;
		case 0xa8:{
		//case "TAY": {
			switch(instruction_cycle){
			case 2: {
				y_index_register=accumulator;
				if(y_index_register ==0) ZFlag = true;else ZFlag = false;
				if(y_index_register <0) NFlag = true;else NFlag = false;
				instruction_cycle = 1;break;
			}
			}
		};break;
		case 0xba:{
		//case "TSX": {
			switch(instruction_cycle){
			case 2: {
				x_index_register = stack_pointer;
				if(x_index_register ==0) ZFlag = true;else ZFlag = false;
				if(x_index_register <0) NFlag = true;else NFlag = false;				
				instruction_cycle = 1;break;
			}
			}
		};break;
		case 0x8a:{
		//case "TXA": {
			switch(instruction_cycle){
			case 2: {
				accumulator = x_index_register;
				if(accumulator ==0) ZFlag = true;else ZFlag = false;
				if(accumulator <0) NFlag = true;else NFlag = false;
				instruction_cycle = 1;break;
			}
			}
		};break;
		case 0x9a:{
		//case "TXS": {
			switch(instruction_cycle){
			case 2: {
				stack_pointer = x_index_register;				
				instruction_cycle = 1;break;
			}
			}
		};break;
		case 0x98:{
		//case "TYA": {
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
