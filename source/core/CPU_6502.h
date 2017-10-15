//
// Created by Jordan on 9/29/2017.
//

#ifndef BASSNES_CPU_6502_H
#define BASSNES_CPU_6502_H
class Mapper;
class CPU_6502{
private:
	//flags
	bool NFlag = false;
	bool VFlag = false;
	bool BFlag = false;
	bool DFlag = false;
	bool IFlag = false;
	bool ZFlag = false;
	bool CFlag = false;

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
	void setNFlag(uint8_t val);
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

	/*typedef void (CPU_6502::*cpu_function)();
	cpu_function cpuaddressing[256] = {
		&CPU_6502::brk
		,&CPU_6502::indx_r
		,&CPU_6502::nmi_m
		,&CPU_6502::indx_rw
		,&CPU_6502::zero_r
		,&CPU_6502::zero_r
		,&CPU_6502::zero_rw
		,&CPU_6502::zero_rw
		,&CPU_6502::php
		,&CPU_6502::immediate
		,&CPU_6502::accumulator_m
		,&CPU_6502::immediate
		,&CPU_6502::abs_r//skw
		,&CPU_6502::abs_r
		,&CPU_6502::abs_rw
		,&CPU_6502::abs_rw
		,&CPU_6502::relative
		,&CPU_6502::indy_r//ora
		,&CPU_6502::irq//irq
		,&CPU_6502::indy_rw//slo
		,&CPU_6502::zerox_r//skb
		,&CPU_6502::zerox_r//ora
		,&CPU_6502::zerox_rw//asl
		,&CPU_6502::zerox_rw//slo
		,&CPU_6502::clc//clc
		,&CPU_6502::abs_y_r//ora
		,&CPU_6502::nop//nop12
		,&CPU_6502::abs_y_rw//slo
		,&CPU_6502::abs_x_r//skw
		,&CPU_6502::abs_x_r//ora
		,&CPU_6502::abs_x_rw//asl
		,&CPU_6502::abs_x_rw//slo
		,&CPU_6502::jsr//jsr
		,&CPU_6502::indx_r//and
		,&CPU_6502::nop///////////////////////////////////////////////////////////////
		,&CPU_6502::indx_rw//rla
		,&CPU_6502::zero_r//bit
		,&CPU_6502::zero_r//and
		,&CPU_6502::zero_rw//rol
		,&CPU_6502::zero_rw//rla
		,&CPU_6502::plp//plp
		,&CPU_6502::immediate//and
		,&CPU_6502::accumulator_m//rol
		,&CPU_6502::immediate//aac
		,&CPU_6502::abs_r//bit
		,&CPU_6502::abs_r//and
		,&CPU_6502::abs_rw//rol
		,&CPU_6502::abs_rw//rla
		,&CPU_6502::relative//bmi
		,&CPU_6502::indy_r//and
		,&CPU_6502::hlt//hlt
		,&CPU_6502::indy_rw//rla
		,&CPU_6502::zerox_r//skb
		,&CPU_6502::zerox_r//and
		,&CPU_6502::zerox_rw//rol
		,&CPU_6502::zerox_rw//rla
		,&CPU_6502::sec//sec
		,&CPU_6502::abs_y_r//and
		,&CPU_6502::nop//nop13
		,&CPU_6502::abs_y_rw//rla
		,&CPU_6502::abs_x_r//skw
		,&CPU_6502::abs_x_r//and
		,&CPU_6502::abs_x_rw//rol
		,&CPU_6502::abs_x_rw//rla
		,&CPU_6502::rti//rti
		,&CPU_6502::indx_r//eor
		,&CPU_6502::nop/////
		,&CPU_6502::indx_rw//sre
		,&CPU_6502::zero_r//skb
		,&CPU_6502::zero_r//eor
		,&CPU_6502::zero_rw//lsr
		,&CPU_6502::zero_rw//sre
		,&CPU_6502::pha//pha
		,&CPU_6502::immediate//eor
		,&CPU_6502::accumulator_m//lsr
		,&CPU_6502::immediate//asr
		,&CPU_6502::jmp_a//jmp_a
		,&CPU_6502::abs_r//eor
		,&CPU_6502::abs_rw//lsr
		,&CPU_6502::abs_rw//sre
		,&CPU_6502::relative//bvc
		,&CPU_6502::indy_r//eor
		,&CPU_6502::nop/////
		,&CPU_6502::indy_rw//sre
		,&CPU_6502::zerox_r//skb
		,&CPU_6502::zerox_r//eor
		,&CPU_6502::zerox_rw//lsr
		,&CPU_6502::zerox_rw//sre
		,&CPU_6502::cli//cli
		,&CPU_6502::abs_y_r//eor
		,&CPU_6502::nop//nop14
		,&CPU_6502::abs_y_rw//sre
		,&CPU_6502::abs_x_r//skw
		,&CPU_6502::abs_x_r//eor
		,&CPU_6502::abs_x_rw//lsr
		,&CPU_6502::abs_x_rw//sre
		,&CPU_6502::rts//rts
		,&CPU_6502::indx_r//adc
		,&CPU_6502::nop///////////////////////////////////////////////////////////////
		,&CPU_6502::indx_rw//rra
		,&CPU_6502::zero_r//skb
		,&CPU_6502::zero_r//adc
		,&CPU_6502::zero_rw//ror
		,&CPU_6502::zero_rw//rra
		,&CPU_6502::pla//pla
		,&CPU_6502::immediate//adc
		,&CPU_6502::accumulator_m//ror
		,&CPU_6502::immediate//arr
		,&CPU_6502::jmp//jmp
		,&CPU_6502::abs_r//adc
		,&CPU_6502::abs_rw//ror
		,&CPU_6502::abs_rw//rra
		,&CPU_6502::relative//bvs
		,&CPU_6502::indy_r//adc
		,&CPU_6502::nop/////
		,&CPU_6502::indy_rw//rra
		,&CPU_6502::zerox_r//skb
		,&CPU_6502::zerox_r//adc
		,&CPU_6502::zerox_rw//ror
		,&CPU_6502::zerox_rw//rra
		,&CPU_6502::sei//sei
		,&CPU_6502::abs_y_r//adc
		,&CPU_6502::nop//nop15
		,&CPU_6502::abs_y_rw//rra
		,&CPU_6502::abs_x_r//skw
		,&CPU_6502::abs_x_r//adc
		,&CPU_6502::abs_x_rw//ror
		,&CPU_6502::abs_x_rw//rra
		,&CPU_6502::immediate//skb
		,&CPU_6502::indx_w//sta
		,&CPU_6502::immediate//skb
		,&CPU_6502::indx_w//sax
		,&CPU_6502::zero_w//sty
		,&CPU_6502::zero_w//sta
		,&CPU_6502::zero_w//stx
		,&CPU_6502::zero_w//sax
		,&CPU_6502::dey//dey
		,&CPU_6502::immediate//skb
		,&CPU_6502::txa//txa
		,&CPU_6502::immediate//ane
		,&CPU_6502::abs_w//sty
		,&CPU_6502::abs_w//sta
		,&CPU_6502::abs_w//stx
		,&CPU_6502::abs_w//sax
		,&CPU_6502::relative//bcc
		,&CPU_6502::indy_w//sta
		,&CPU_6502::nop/////
		,&CPU_6502::indy_w//sha
		,&CPU_6502::zerox_w//sty
		,&CPU_6502::zerox_w//sta
		,&CPU_6502::zeroy_w//stx
		,&CPU_6502::zeroy_w//sax
		,&CPU_6502::tya//tya
		,&CPU_6502::abs_y_w//sta
		,&CPU_6502::txs//txs
		,&CPU_6502::abs_y_w//shs
		,&CPU_6502::shy//shy
		,&CPU_6502::abs_x_w//sta
		,&CPU_6502::shx//shx
		,&CPU_6502::abs_y_w//sha
		,&CPU_6502::immediate//ldy
		,&CPU_6502::indx_r//lda
		,&CPU_6502::immediate//ldx
		,&CPU_6502::indx_r//lax
		,&CPU_6502::zero_r//ldy
		,&CPU_6502::zero_r//lda
		,&CPU_6502::zero_r//ldx
		,&CPU_6502::zero_r//lax
		,&CPU_6502::tay//tay
		,&CPU_6502::immediate//lda
		,&CPU_6502::tax//tax
		,&CPU_6502::immediate//atx
		,&CPU_6502::abs_r//ldy
		,&CPU_6502::abs_r//lda
		,&CPU_6502::abs_r//ldx
		,&CPU_6502::abs_r//lax
		,&CPU_6502::relative//bcs
		,&CPU_6502::indy_r//lda
		,&CPU_6502::nop/////
		,&CPU_6502::indy_r//lax
		,&CPU_6502::zerox_r//ldy
		,&CPU_6502::zerox_r//lda
		,&CPU_6502::zeroy_r//ldx
		,&CPU_6502::zeroy_r//lax
		,&CPU_6502::clv//clv
		,&CPU_6502::abs_y_r//lda
		,&CPU_6502::tsx//tsx
		,&CPU_6502::abs_y_r//las
		,&CPU_6502::abs_x_r//ldy
		,&CPU_6502::abs_x_r//lda
		,&CPU_6502::abs_y_r//ldx
		,&CPU_6502::abs_y_r//lax
		,&CPU_6502::immediate//cpy
		,&CPU_6502::indx_r//cmp
		,&CPU_6502::immediate//skb
		,&CPU_6502::indx_rw//dcp
		,&CPU_6502::zero_r//cpy
		,&CPU_6502::zero_r//cmp
		,&CPU_6502::zero_rw//dec
		,&CPU_6502::zero_rw//dcp
		,&CPU_6502::iny//iny
		,&CPU_6502::immediate//cmp
		,&CPU_6502::dex//dex
		,&CPU_6502::immediate//axs
		,&CPU_6502::abs_r//cpy
		,&CPU_6502::abs_r//cmp
		,&CPU_6502::abs_rw//dec
		,&CPU_6502::abs_rw//dcp
		,&CPU_6502::relative//bne
		,&CPU_6502::indy_r//cmp
		,&CPU_6502::nop/////
		,&CPU_6502::indy_rw//dcp
		,&CPU_6502::zerox_r//skb
		,&CPU_6502::zerox_r//cmp
		,&CPU_6502::zerox_rw//dec
		,&CPU_6502::zerox_rw//dcp
		,&CPU_6502::cld//cld
		,&CPU_6502::abs_y_r//cmp
		,&CPU_6502::nop//nop16
		,&CPU_6502::abs_y_rw//dcp
		,&CPU_6502::abs_x_r//skw
		,&CPU_6502::abs_x_r//cmp
		,&CPU_6502::abs_x_rw//dec
		,&CPU_6502::abs_x_rw//dcp
		,&CPU_6502::immediate//cpx
		,&CPU_6502::indx_r//sbc
		,&CPU_6502::immediate//skb
		,&CPU_6502::indx_rw//isb
		,&CPU_6502::zero_r//cpx
		,&CPU_6502::zero_r//sbc
		,&CPU_6502::zero_rw//inc
		,&CPU_6502::zero_rw//isb
		,&CPU_6502::inx//inx
		,&CPU_6502::immediate//sbc
		,&CPU_6502::nop//nop
		,&CPU_6502::immediate//sbc
		,&CPU_6502::abs_r//cpx
		,&CPU_6502::abs_r//sbc
		,&CPU_6502::abs_rw//inc
		,&CPU_6502::abs_rw//isb
		,&CPU_6502::relative//beq
		,&CPU_6502::indy_r//sbc
		,&CPU_6502::nop///////////////////////////////////////////////////////////////
		,&CPU_6502::indy_rw//isb
		,&CPU_6502::zerox_r//skb
		,&CPU_6502::zerox_r//sbc
		,&CPU_6502::zerox_rw//inc
		,&CPU_6502::zerox_rw//isb
		,&CPU_6502::sed//sed
		,&CPU_6502::abs_y_r//sbc
		,&CPU_6502::nop//nop17
		,&CPU_6502::abs_y_rw//isb
		,&CPU_6502::abs_x_r//skw
		,&CPU_6502::abs_x_r//sbc
		,&CPU_6502::abs_x_rw//inc
		,&CPU_6502::abs_x_rw//isb

	};*/

public:
	
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
    enum IRQSource{External,FrameCounter,DMC};
    Mapper* map;
    int instruction_cycle = 0;
    uint8_t current_instruction=0;
    int program_counter=0;
    CPU_6502(Mapper* m);
    void run_cycle();
    void setNMI(bool donmi);
    void setIRQ(IRQSource irq);
    void removeIRQ(IRQSource irq);

    int dxx = 0;
};
#endif //BASSNES_CPU_6502_H
