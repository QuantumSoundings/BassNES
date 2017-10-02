//
// Created by Jordan on 9/29/2017.
//

#ifndef BASSNES_CPU_6502_H
#define BASSNES_CPU_6502_H
class Mapper;
class CPU_6502{
private:
    bool irqs[3];
    
    //other stuff
    bool doOp = false;
    bool brokenaddress = false;
    
    uint16_t pointer = 0;
    bool branchtaken=false;
    uint8_t lowpc = 0;

    int dmac = 0;
    int dmain = 0;
    int cpuinc = 0;
    int dmadata = 0;

    //Methods
    uint8_t buildFlags();
    void setFlags(uint8_t x);
    void dma();
    void pollInterrupts();
    uint8_t getNextInstruction();
    void executeInstruction();
    void executeOp();
    void immediate();
    void zero_r();
    void zero_rw();
    void zero_w();
    void zerox_r();
    void zerox_rw();
    void zerox_w();
    void zeroy_r();
    void zeroy_rw();
    void zeroy_w();
    void abs_r();
    void abs_rw();
    void abs_w();
    void abs_x_r();
    void abs_x_rw();
    void abs_x_w();
    void abs_y_r();
    void abs_y_rw();
    void abs_y_w();
    void indx_r();
    void indx_rw();
    void indx_w();
    void indy_r();
    void indy_rw();
    void indy_w();
    void accumulator_m();
    void relative();
    void aac();
    void adc();
    void and_m();
    void ane();
    void arr();
    void asl();
    void asr();
    void atx();
    void axs();
    void bcc();
    void bcs();
    void beq();
    void bit();
    void bmi();
    void bne();
    void bpl();
    void brk();
    void bvc();
    void bvs();
    void clc();
    void cld();
    void cli();
    void clv();
    void cmp();
    void cpx();
    void cpy();
    void dcp();
    void dec();
    void dex();
    void dey();
    void eor();
    void hlt();
    void inc();
    void inx();
    void iny();
    void irq();
    void isb();
    void jmp();
    void jmp_a();
    void jsr();
    void las();
    void lax();
    void lda();
    void ldx();
    void ldy();
    void lsr();
    void nmi_m();
    void nop();
    void skb();
    void skw();
    void ora();
    void pha();
    void php();
    void pla();
    void plp();
    void rla();
    void rol();
    void ror();
    void rra();
    void rti();
    void rts();
    void sax();
    void sbc();
    void sec();
    void sed();
    void sei();
    void sha();
    void shs();
    void shx();
    void shy();
    void slo();
    void sre();
    void sta();
    void stx();
    void sty();
    void tax();
    void tay();
    void tsx();
    void txa();
    void txs();
    void tya();

public:
	//flags
	bool NFlag = false;
	bool VFlag = false;
	bool BFlag = false;
	bool DFlag = false;
	bool IFlag = false;
	bool ZFlag = false;
	bool CFlag = false;

	//registers
	uint8_t accumulator = 0;
	uint8_t x_index_register = 0;
	uint8_t y_index_register = 0;
	uint8_t stack_pointer = 0;
	uint8_t tempregister = 0;

	uint16_t address = 0;

    bool doNMI = false;
    bool nmiInterrupt = false;
    bool irqInterrupt = false;
    bool nmihijack = false;
    bool oldnmi = false;
    bool nmi = false;
    int stallcount = 0;
    bool writeDMA = false;
    int doIRQ = 0;
    static enum IRQSource{External,FrameCounter,DMC};
    Mapper* map;
    int instruction_cycle = 0;
    uint8_t current_instruction=0;
    int program_counter=0;
    CPU_6502(Mapper* m);
    void run_cycle();
    void setNMI(bool donmi);
    void setIRQ(IRQSource irq);
    void removeIRQ(IRQSource irq);
	void setNFlag(uint8_t val);

    int dxx = 0;
};
#endif //BASSNES_CPU_6502_H
