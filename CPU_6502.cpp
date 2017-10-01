#include <cstdint>
#include "CPU_6502.h"
#include "Mapper.h"
#include <iostream>
//methods
CPU_6502::CPU_6502(Mapper* m){
    map = m;
    instruction_cycle=1;
    stack_pointer=0xfd;
    setFlags(0x34);
}
uint8_t CPU_6502::buildFlags(){
        uint8_t temp = 0;
        temp|= CFlag?1:0;
        temp|= ZFlag?2:0;
        temp|= IFlag?4:0;
        temp|= DFlag?8:0;
        temp|= BFlag?16:0;
        temp|= 32;
        temp|= VFlag?64:0;
        temp|= NFlag?128:0;
        return temp;
}
void CPU_6502::setFlags(uint8_t x){
        NFlag = x < 0;
        VFlag = (x & (1 << 6)) > 0;
        BFlag = (x & (1 << 4)) > 0;
        DFlag = (x & (1 << 3)) > 0;
        IFlag = (x & (1 << 2)) > 0;
        ZFlag = (x & (1 << 1)) > 0;
        CFlag = (x & 1) > 0;
}
void CPU_6502::dma(){
        if(dmac ==513){
            dmac =0;
            dmain=0;
            cpuinc=0;
            writeDMA=false;
        }
        else if(dmac==0){
            dmac++;
            dmadata = map->cpuread(dxx+cpuinc);
            dmain = map->ppu->OAMADDR;
        }
        else if(dmac%2==1){
            dmac++;
            dmadata = map->cpuread(dxx+cpuinc);
            //where it reads
        }
        else{
            map->cpuwriteoam(dmain,dmadata);
            if(dmain==255)
                dmain=0;
            else
                dmain++;
            cpuinc++;
            dmac++;
        }
}
    void CPU_6502::pollInterrupts(){
        if(doNMI&&!oldnmi){
            nmi=true;
        }
        oldnmi=doNMI;
        if(nmi){
            nmiInterrupt = true;
            nmi = false;
        }
        else if(doIRQ>0&&!IFlag){
            irqInterrupt=true;
        }

    }
    uint8_t CPU_6502::getNextInstruction(){
        if(nmiInterrupt){
            program_counter--;
            nmiInterrupt=false;
            return 0x02;
        }
        else if(irqInterrupt){
            irqInterrupt=false;
            program_counter--;
            return 0x12;
        }
        else{
            return map->cpuread(program_counter);
        }
    }
    void CPU_6502::executeInstruction(){
        if(instruction_cycle ==1){
            if(doOp){
                executeOp();
                doOp=false;
            }
            current_instruction = getNextInstruction();
            program_counter++;
            map->cpuread(program_counter);
        }
        switch(current_instruction){
            case 0x00:brk();break;
            case 0x01:indx_r();break;
            case 0x02:nmi_m();break;
            case 0x03:indx_rw();break;
            case 0x04:zero_r();break;
            case 0x05:zero_r();break;
            case 0x06:zero_rw();break;
            case 0x07:zero_rw();break;
            case 0x08:php();break;
            case 0x09:immediate();break;
            case 0x0a:accumulator_m();break;
            case 0x0b:immediate();break;
            case 0x0c:abs_r();break;//skw
            case 0x0d:abs_r();break;
            case 0x0e:abs_rw();break;
            case 0x0f:abs_rw();break;
            case 0x10:relative();break;
            case 0x11:indy_r();break;//ora
            case 0x12:irq();break;//irq
            case 0x13:indy_rw();break;//slo
            case 0x14:zerox_r();break;//skb
            case 0x15:zerox_r();break;//ora
            case 0x16:zerox_rw();break;//asl
            case 0x17:zerox_rw();break;//slo
            case 0x18:clc();break;//clc
            case 0x19:abs_y_r();break;//ora
            case 0x1a:nop();break;//nop12
            case 0x1b:abs_y_rw();break;//slo
            case 0x1c:abs_x_r();break;//skw
            case 0x1d:abs_x_r();break;//ora
            case 0x1e:abs_x_rw();break;//asl
            case 0x1f:abs_x_rw();break;//slo
            case 0x20:jsr();break;//jsr
            case 0x21:indx_r();break;//and
            case 0x22:break;///////////////////////////////////////////////////////////////
            case 0x23:indx_rw();break;//rla
            case 0x24:zero_r();break;//bit
            case 0x25:zero_r();break;//and
            case 0x26:zero_rw();break;//rol
            case 0x27:zero_rw();break;//rla
            case 0x28:plp();break;//plp
            case 0x29:immediate();break;//and
            case 0x2a:accumulator_m();break;//rol
            case 0x2b:immediate();break;//aac
            case 0x2c:abs_r();break;//bit
            case 0x2d:abs_r();break;//and
            case 0x2e:abs_rw();break;//rol
            case 0x2f:abs_rw();break;//rla
            case 0x30:relative();break;//bmi
            case 0x31:indy_r();break;//and
            case 0x32:hlt();break;//hlt
            case 0x33:indy_rw();break;//rla
            case 0x34:zerox_r();break;//skb
            case 0x35:zerox_r();break;//and
            case 0x36:zerox_rw();break;//rol
            case 0x37:zerox_rw();break;//rla
            case 0x38:sec();break;//sec
            case 0x39:abs_y_r();break;//and
            case 0x3a:nop();break;//nop13
            case 0x3b:abs_y_rw();break;//rla
            case 0x3c:abs_x_r();break;//skw
            case 0x3d:abs_x_r();break;//and
            case 0x3e:abs_x_rw();break;//rol
            case 0x3f:abs_x_rw();break;//rla
            case 0x40:rti();break;//rti
            case 0x41:indx_r();break;//eor
            case 0x42:break;///////////////////////////////////////////////////////////////
            case 0x43:indx_rw();break;//sre
            case 0x44:zero_r();break;//skb
            case 0x45:zero_r();break;//eor
            case 0x46:zero_rw();break;//lsr
            case 0x47:zero_rw();break;//sre
            case 0x48:pha();break;//pha
            case 0x49:immediate();break;//eor
            case 0x4a:accumulator_m();break;//lsr
            case 0x4b:immediate();break;//asr
            case 0x4c:jmp_a();break;//jmp_a
            case 0x4d:abs_r();break;//eor
            case 0x4e:abs_rw();break;//lsr
            case 0x4f:abs_rw();break;//sre
            case 0x50:relative();break;//bvc
            case 0x51:indy_r();break;//eor
            case 0x52:break;///////////////////////////////////////////////////////////////
            case 0x53:indy_rw();break;//sre
            case 0x54:zerox_r();break;//skb
            case 0x55:zerox_r();break;//eor
            case 0x56:zerox_rw();break;//lsr
            case 0x57:zerox_rw();break;//sre
            case 0x58:cli();break;//cli
            case 0x59:abs_y_r();break;//eor
            case 0x5a:nop();break;//nop14
            case 0x5b:abs_y_rw();break;//sre
            case 0x5c:abs_x_r();break;//skw
            case 0x5d:abs_x_r();break;//eor
            case 0x5e:abs_x_rw();break;//lsr
            case 0x5f:abs_x_rw();break;//sre
            case 0x60:rts();break;//rts
            case 0x61:indx_r();break;//adc
            case 0x62:break;///////////////////////////////////////////////////////////////
            case 0x63:indx_rw();break;//rra
            case 0x64:zero_r();break;//skb
            case 0x65:zero_r();break;//adc
            case 0x66:zero_rw();break;//ror
            case 0x67:zero_rw();break;//rra
            case 0x68:pla();break;//pla
            case 0x69:immediate();break;//adc
            case 0x6a:accumulator_m();break;//ror
            case 0x6b:immediate();break;//arr
            case 0x6c:jmp();break;//jmp
            case 0x6d:abs_r();break;//adc
            case 0x6e:abs_rw();break;//ror
            case 0x6f:abs_rw();break;//rra
            case 0x70:relative();break;//bvs
            case 0x71:indy_r();break;//adc
            case 0x72:break;///////////////////////////////////////////////////////////////
            case 0x73:indy_rw();break;//rra
            case 0x74:zerox_r();break;//skb
            case 0x75:zerox_r();break;//adc
            case 0x76:zerox_rw();break;//ror
            case 0x77:zerox_rw();break;//rra
            case 0x78:sei();break;//sei
            case 0x79:abs_y_r();break;//adc
            case 0x7a:nop();break;//nop15
            case 0x7b:abs_y_rw();break;//rra
            case 0x7c:abs_x_r();break;//skw
            case 0x7d:abs_x_r();break;//adc
            case 0x7e:abs_x_rw();break;//ror
            case 0x7f:abs_x_rw();break;//rra
            case 0x80:immediate();break;//skb
            case 0x81:indx_w();break;//sta
            case 0x82:immediate();break;//skb
            case 0x83:indx_w();break;//sax
            case 0x84:zero_w();break;//sty
            case 0x85:zero_w();break;//sta
            case 0x86:zero_w();break;//stx
            case 0x87:zero_w();break;//sax
            case 0x88:dey();break;//dey
            case 0x89:immediate();break;//skb
            case 0x8a:txa();break;//txa
            case 0x8b:immediate();break;//ane
            case 0x8c:abs_w();break;//sty
            case 0x8d:abs_w();break;//sta
            case 0x8e:abs_w();break;//stx
            case 0x8f:abs_w();break;//sax
            case 0x90:relative();break;//bcc
            case 0x91:indy_w();break;//sta
            case 0x92:break;///////////////////////////////////////////////////////////////
            case 0x93:indy_w();break;//sha
            case 0x94:zerox_w();break;//sty
            case 0x95:zerox_w();break;//sta
            case 0x96:zeroy_w();break;//stx
            case 0x97:zeroy_w();break;//sax
            case 0x98:tya();break;//tya
            case 0x99:abs_y_w();break;//sta
            case 0x9a:txs();break;//txs
            case 0x9b:abs_y_w();break;//shs
            case 0x9c:shy();break;//shy
            case 0x9d:abs_x_w();break;//sta
            case 0x9e:shx();break;//shx
            case 0x9f:abs_y_w();break;//sha
            case 0xa0:immediate();break;//ldy
            case 0xa1:indx_r();break;//lda
            case 0xa2:immediate();break;//ldx
            case 0xa3:indx_r();break;//lax
            case 0xa4:zero_r();break;//ldy
            case 0xa5:zero_r();break;//lda
            case 0xa6:zero_r();break;//ldx
            case 0xa7:zero_r();break;//lax
            case 0xa8:tay();break;//tay
            case 0xa9:immediate();break;//lda
            case 0xaa:tax();break;//tax
            case 0xab:immediate();break;//atx
            case 0xac:abs_r();break;//ldy
            case 0xad:abs_r();break;//lda
            case 0xae:abs_r();break;//ldx
            case 0xaf:abs_r();break;//lax
            case 0xb0:relative();break;//bcs
            case 0xb1:indy_r();break;//lda
            case 0xb2:break;///////////////////////////////////////////////////////////////
            case 0xb3:indy_r();break;//lax
            case 0xb4:zerox_r();break;//ldy
            case 0xb5:zerox_r();break;//lda
            case 0xb6:zeroy_r();break;//ldx
            case 0xb7:zeroy_r();break;//lax
            case 0xb8:clv();break;//clv
            case 0xb9:abs_y_r();break;//lda
            case 0xba:tsx();break;//tsx
            case 0xbb:abs_y_r();break;//las
            case 0xbc:abs_x_r();break;//ldy
            case 0xbd:abs_x_r();break;//lda
            case 0xbe:abs_y_r();break;//ldx
            case 0xbf:abs_y_r();break;//lax
            case 0xc0:immediate();break;//cpy
            case 0xc1:indx_r();break;//cmp
            case 0xc2:immediate();break;//skb
            case 0xc3:indx_rw();break;//dcp
            case 0xc4:zero_r();break;//cpy
            case 0xc5:zero_r();break;//cmp
            case 0xc6:zero_rw();break;//dec
            case 0xc7:zero_rw();break;//dcp
            case 0xc8:iny();break;//iny
            case 0xc9:immediate();break;//cmp
            case 0xca:dex();break;//dex
            case 0xcb:immediate();break;//axs
            case 0xcc:abs_r();break;//cpy
            case 0xcd:abs_r();break;//cmp
            case 0xce:abs_rw();break;//dec
            case 0xcf:abs_rw();break;//dcp
            case 0xd0:relative();break;//bne
            case 0xd1:indy_r();break;//cmp
            case 0xd2:break;///////////////////////////////////////////////////////////////
            case 0xd3:indy_rw();break;//dcp
            case 0xd4:zerox_r();break;//skb
            case 0xd5:zerox_r();break;//cmp
            case 0xd6:zerox_rw();break;//dec
            case 0xd7:zerox_rw();break;//dcp
            case 0xd8:cld();break;//cld
            case 0xd9:abs_y_r();break;//cmp
            case 0xda:nop();break;//nop16
            case 0xdb:abs_y_rw();break;//dcp
            case 0xdc:abs_x_r();break;//skw
            case 0xdd:abs_x_r();break;//cmp
            case 0xde:abs_x_rw();break;//dec
            case 0xdf:abs_x_rw();break;//dcp
            case 0xe0:immediate();break;//cpx
            case 0xe1:indx_r();break;//sbc
            case 0xe2:immediate();break;//skb
            case 0xe3:indx_rw();break;//isb
            case 0xe4:zero_r();break;//cpx
            case 0xe5:zero_r();break;//sbc
            case 0xe6:zero_rw();break;//inc
            case 0xe7:zero_rw();break;//isb
            case 0xe8:inx();break;//inx
            case 0xe9:immediate();break;//sbc
            case 0xea:nop();break;//nop
            case 0xeb:immediate();break;//sbc
            case 0xec:abs_r();break;//cpx
            case 0xed:abs_r();break;//sbc
            case 0xee:abs_rw();break;//inc
            case 0xef:abs_rw();break;//isb
            case 0xf0:relative();break;//beq
            case 0xf1:indy_r();break;//sbc
            case 0xf2:break;///////////////////////////////////////////////////////////////
            case 0xf3:indy_rw();break;//isb
            case 0xf4:zerox_r();break;//skb
            case 0xf5:zerox_r();break;//sbc
            case 0xf6:zerox_rw();break;//inc
            case 0xf7:zerox_rw();break;//isb
            case 0xf8:sed();break;//sed
            case 0xf9:abs_y_r();break;//sbc
            case 0xfa:nop();break;//nop17
            case 0xfb:abs_y_rw();break;//isb
            case 0xfc:abs_x_r();break;//skw
            case 0xfd:abs_x_r();break;//sbc
            case 0xfe:abs_x_rw();break;//inc
            case 0xff:abs_x_rw();break;//isb
        }
    }
    void CPU_6502::executeOp(){
        switch(current_instruction){
            case 0x00:brk();break;//brk
            case 0x01:ora();break;//ora
            case 0x02:nmi_m();break;//nmi
            case 0x03:slo();break;//slo
            case 0x04:skb();break;//skb
            case 0x05:ora();break;//ora
            case 0x06:asl();break;//asl
            case 0x07:slo();break;//slo
            case 0x08:php();break;//php
            case 0x09:ora();break;//ora
            case 0x0a:asl();break;//asl
            case 0x0b:aac();break;//aac
            case 0x0c:skw();break;//skw
            case 0x0d:ora();break;//ora
            case 0x0e:asl();break;//asl
            case 0x0f:slo();break;//slo
            case 0x10:bpl();break;//bpl
            case 0x11:ora();break;//ora
            case 0x12:irq();break;//irq
            case 0x13:slo();break;//slo
            case 0x14:skb();break;//skb
            case 0x15:ora();break;//ora
            case 0x16:asl();break;//asl
            case 0x17:slo();break;//slo
            case 0x18:clc();break;//clc
            case 0x19:ora();break;//ora
            case 0x1a:nop();break;//nop12
            case 0x1b:slo();break;//slo
            case 0x1c:skw();break;//skw
            case 0x1d:ora();break;//ora
            case 0x1e:asl();break;//asl
            case 0x1f:slo();break;//slo
            case 0x20:jsr();break;//jsr
            case 0x21:and_m();break;//and
            case 0x22:break;//////////////////////////////////////////////////////////
            case 0x23:rla();break;//rla
            case 0x24:bit();break;//bit
            case 0x25:and_m();break;//and
            case 0x26:rol();break;//rol
            case 0x27:rla();break;//rla
            case 0x28:plp();break;//plp
            case 0x29:and_m();break;//and
            case 0x2a:rol();break;//rol
            case 0x2b:aac();break;//aac
            case 0x2c:bit();break;//bit
            case 0x2d:and_m();break;//and
            case 0x2e:rol();break;//rol
            case 0x2f:rla();break;//rla
            case 0x30:bmi();break;//bmi
            case 0x31:and_m();break;//and
            case 0x32:hlt();break;//hlt
            case 0x33:rla();break;//rla
            case 0x34:skb();break;//skb
            case 0x35:and_m();break;//and
            case 0x36:rol();break;//rol
            case 0x37:rla();break;//rla
            case 0x38:sec();break;//sec
            case 0x39:and_m();break;//and
            case 0x3a:nop();break;//nop13
            case 0x3b:rla();break;//rla
            case 0x3c:skw();break;//skw
            case 0x3d:and_m();break;//and
            case 0x3e:rol();break;//rol
            case 0x3f:rla();break;//rla
            case 0x40:rti();break;//rti
            case 0x41:eor();break;//eor
            case 0x42:break;///////////////////////////////////////////////////////////////
            case 0x43:sre();break;//sre
            case 0x44:skb();break;//skb
            case 0x45:eor();break;//eor
            case 0x46:lsr();break;//lsr
            case 0x47:sre();break;//sre
            case 0x48:pha();break;//pha
            case 0x49:eor();break;//eor
            case 0x4a:lsr();break;//lsr
            case 0x4b:asr();break;//asr
            case 0x4c:jmp_a();break;//jmp_a
            case 0x4d:eor();break;//eor
            case 0x4e:lsr();break;//lsr
            case 0x4f:sre();break;//sre
            case 0x50:bvc();break;//bvc
            case 0x51:eor();break;//eor
            case 0x52:break;///////////////////////////////////////////////////////////////
            case 0x53:sre();break;//sre
            case 0x54:skb();break;//skb
            case 0x55:eor();break;//eor
            case 0x56:lsr();break;//lsr
            case 0x57:sre();break;//sre
            case 0x58:cli();break;//cli
            case 0x59:eor();break;//eor
            case 0x5a:nop();break;//nop14
            case 0x5b:sre();break;//sre
            case 0x5c:skw();break;//skw
            case 0x5d:eor();break;//eor
            case 0x5e:lsr();break;//lsr
            case 0x5f:sre();break;//sre
            case 0x60:rts();break;//rts
            case 0x61:adc();break;//adc
            case 0x62:break;///////////////////////////////////////////////////////////////
            case 0x63:rra();break;//rra
            case 0x64:skb();break;//skb
            case 0x65:adc();break;//adc
            case 0x66:ror();break;//ror
            case 0x67:rra();break;//rra
            case 0x68:pla();break;//pla
            case 0x69:adc();break;//adc
            case 0x6a:ror();break;//ror
            case 0x6b:arr();break;//arr
            case 0x6c:jmp();break;//jmp
            case 0x6d:adc();break;//adc
            case 0x6e:ror();break;//ror
            case 0x6f:rra();break;//rra
            case 0x70:bvs();break;//bvs
            case 0x71:adc();break;//adc
            case 0x72:break;///////////////////////////////////////////////////////////////
            case 0x73:rra();break;//rra
            case 0x74:skb();break;//skb
            case 0x75:adc();break;//adc
            case 0x76:ror();break;//ror
            case 0x77:rra();break;//rra
            case 0x78:sei();break;//sei
            case 0x79:adc();break;//adc
            case 0x7a:nop();break;//nop15
            case 0x7b:rra();break;//rra
            case 0x7c:skw();break;//skw
            case 0x7d:adc();break;//adc
            case 0x7e:ror();break;//ror
            case 0x7f:rra();break;//rra
            case 0x80:skb();break;//skb
            case 0x81:sta();break;//sta
            case 0x82:skb();break;//skb
            case 0x83:sax();break;//sax
            case 0x84:sty();break;//sty
            case 0x85:sta();break;//sta
            case 0x86:stx();break;//stx
            case 0x87:sax();break;//sax
            case 0x88:dey();break;//dey
            case 0x89:skb();break;//skb
            case 0x8a:txa();break;//txa
            case 0x8b:ane();break;//ane
            case 0x8c:sty();break;//sty
            case 0x8d:sta();break;//sta
            case 0x8e:stx();break;//stx
            case 0x8f:sax();break;//sax
            case 0x90:bcc();break;//bcc
            case 0x91:sta();break;//sta
            case 0x92:break;///////////////////////////////////////////////////////////////
            case 0x93:sha();break;//sha
            case 0x94:sty();break;//sty
            case 0x95:sta();break;//sta
            case 0x96:stx();break;//stx
            case 0x97:sax();break;//sax
            case 0x98:tya();break;//tya
            case 0x99:sta();break;//sta
            case 0x9a:txs();break;//txs
            case 0x9b:shs();break;//shs
            case 0x9c:shy();break;//shy
            case 0x9d:sta();break;//sta
            case 0x9e:shx();break;//shx
            case 0x9f:sha();break;//sha
            case 0xa0:ldy();break;//ldy
            case 0xa1:lda();break;//lda
            case 0xa2:ldx();break;//ldx
            case 0xa3:lax();break;//lax
            case 0xa4:ldy();break;//ldy
            case 0xa5:lda();break;//lda
            case 0xa6:ldx();break;//ldx
            case 0xa7:lax();break;//lax
            case 0xa8:tay();break;//tay
            case 0xa9:lda();break;//lda
            case 0xaa:tax();break;//tax
            case 0xab:atx();break;//atx
            case 0xac:ldy();break;//ldy
            case 0xad:lda();break;//lda
            case 0xae:ldx();break;//ldx
            case 0xaf:lax();break;//lax
            case 0xb0:bcs();break;//bcs
            case 0xb1:lda();break;//lda
            case 0xb2:break;///////////////////////////////////////////////////////////////
            case 0xb3:lax();break;//lax
            case 0xb4:ldy();break;//ldy
            case 0xb5:lda();break;//lda
            case 0xb6:ldx();break;//ldx
            case 0xb7:lax();break;//lax
            case 0xb8:clv();break;//clv
            case 0xb9:lda();break;//lda
            case 0xba:tsx();break;//tsx
            case 0xbb:las();break;//las
            case 0xbc:ldy();break;//ldy
            case 0xbd:lda();break;//lda
            case 0xbe:ldx();break;//ldx
            case 0xbf:lax();break;//lax
            case 0xc0:cpy();break;//cpy
            case 0xc1:cmp();break;//cmp
            case 0xc2:skb();break;//skb
            case 0xc3:dcp();break;//dcp
            case 0xc4:cpy();break;//cpy
            case 0xc5:cmp();break;//cmp
            case 0xc6:dec();break;//dec
            case 0xc7:dcp();break;//dcp
            case 0xc8:iny();break;//iny
            case 0xc9:cmp();break;//cmp
            case 0xca:dex();break;//dex
            case 0xcb:axs();break;//axs
            case 0xcc:cpy();break;//cpy
            case 0xcd:cmp();break;//cmp
            case 0xce:dec();break;//dec
            case 0xcf:dcp();break;//dcp
            case 0xd0:bne();break;//bne
            case 0xd1:cmp();break;//cmp
            case 0xd2:break;///////////////////////////////////////////////////////////////
            case 0xd3:dcp();break;//dcp
            case 0xd4:skb();break;//skb
            case 0xd5:cmp();break;//cmp
            case 0xd6:dec();break;//dec
            case 0xd7:dcp();break;//dcp
            case 0xd8:cld();break;//cld
            case 0xd9:cmp();break;//cmp
            case 0xda:nop();break;//nop16
            case 0xdb:dcp();break;//dcp
            case 0xdc:skw();break;//skw
            case 0xdd:cmp();break;//cmp
            case 0xde:dec();break;//dec
            case 0xdf:dcp();break;//dcp
            case 0xe0:cpx();break;//cpx
            case 0xe1:sbc();break;//sbc
            case 0xe2:skb();break;//skb
            case 0xe3:isb();break;//isb
            case 0xe4:cpx();break;//cpx
            case 0xe5:sbc();break;//sbc
            case 0xe6:inc();break;//inc
            case 0xe7:isb();break;//isb
            case 0xe8:inx();break;//inx
            case 0xe9:sbc();break;//sbc
            case 0xea:nop();break;//nop
            case 0xeb:sbc();break;//sbc
            case 0xec:cpx();break;//cpx
            case 0xed:sbc();break;//sbc
            case 0xee:inc();break;//inc
            case 0xef:isb();break;//isb
            case 0xf0:beq();break;//beq
            case 0xf1:sbc();break;//sbc
            case 0xf2:break;///////////////////////////////////////////////////////////////
            case 0xf3:isb();break;//isb
            case 0xf4:skb();break;//skb
            case 0xf5:sbc();break;//sbc
            case 0xf6:inc();break;//inc
            case 0xf7:isb();break;//isb
            case 0xf8:sed();break;//sed
            case 0xf9:sbc();break;//sbc
            case 0xfa:nop();break;//nop17
            case 0xfb:isb();break;//isb
            case 0xfc:skw();break;//skw
            case 0xfd:sbc();break;//sbc
            case 0xfe:inc();break;//inc
            case 0xff:isb();break;//isb
        }
    }
 void CPU_6502::immediate(){
        switch(instruction_cycle){
            case 1:
                pollInterrupts();instruction_cycle++;break;
            case 2:
                tempregister = map->cpuread(program_counter);
                //executeOp();
                program_counter++;doOp=true;
                instruction_cycle=1;break;
        }
    }
 void CPU_6502::zero_r(){
        switch(instruction_cycle){
            case 1: instruction_cycle++;break;
            case 2:
                address = map->cpureadu(program_counter);
                program_counter++;
                instruction_cycle++;
                pollInterrupts();
                break;
            case 3:
                tempregister = map->cpuread(address);
                instruction_cycle=1;doOp=true;
                break;
        }
    }
 void CPU_6502::zero_rw(){
        switch(instruction_cycle){
            case 1: instruction_cycle++;break;
            case 2:
                address = map->cpureadu(program_counter);
                program_counter++;
                instruction_cycle++;
                break;
            case 3:
                tempregister = map->cpuread(address);
                instruction_cycle++;
                break;
            case 4:
                map->cpuwrite(address, tempregister);
                executeOp();
                pollInterrupts();
                instruction_cycle++;
                break;
            case 5:
                map->cpuwrite(address, tempregister);
                address = 0;
                instruction_cycle=1;
                break;
        }
    }
 void CPU_6502::zero_w(){
        switch(instruction_cycle){
            case 1: instruction_cycle++;break;
            case 2:
                address = map->cpureadu(program_counter);
                program_counter++;
                pollInterrupts();
                instruction_cycle++;
                break;
            case 3:
                executeOp();
                address = 0;
                instruction_cycle =1;
                break;
        }
    }
 void CPU_6502::zerox_r(){
        switch(instruction_cycle){
            case 1: instruction_cycle++;break;
            case 2:
                address = map->cpureadu(program_counter);
                program_counter++;
                instruction_cycle++;
                break;
            case 3:
                address += x_index_register;
                instruction_cycle++;
                pollInterrupts();
                break;
            case 4:
                address&=0xff;
                tempregister= map->cpuread(address);
                instruction_cycle=1;doOp=true;
                break;
        }
    }

 void CPU_6502::zerox_rw(){
        switch(instruction_cycle){
            case 1: instruction_cycle++;break;
            case 2:
                address = map->cpureadu(program_counter);
                program_counter++;
                instruction_cycle++;
                break;
            case 3:
                address +=x_index_register;
                instruction_cycle++;
                break;
            case 4:
                address&=0xff;
                tempregister = map->cpuread(address);
                instruction_cycle++;
                break;
            case 5:
                map->cpuwrite(address, tempregister);
                executeOp();
                pollInterrupts();
                instruction_cycle++;
                break;
            case 6:
                map->cpuwrite(address, tempregister);
                instruction_cycle = 1;
                address = 0;
                break;
        }
    }

 void CPU_6502::zerox_w(){
        switch(instruction_cycle){
            case 1: instruction_cycle++;break;
            case 2:
                address = map->cpureadu(program_counter);
                program_counter++;
                instruction_cycle++;
                break;
            case 3:
                address += x_index_register;
                instruction_cycle++;
                pollInterrupts();
                break;
            case 4:
                address&=0xff;
                executeOp();
                instruction_cycle = 1;
                break;
        }
    }

 void CPU_6502::zeroy_r(){
        switch(instruction_cycle){
            case 1: instruction_cycle++;break;
            case 2:
                address = map->cpureadu(program_counter);
                program_counter++;
                instruction_cycle++;
                break;
            case 3:
                address += x_index_register;
                instruction_cycle++;
                pollInterrupts();
                break;
            case 4:
                address&=0xff;
                tempregister= map->cpuread(address);
                instruction_cycle=1;doOp=true;
                break;
        }
    }

 void CPU_6502::zeroy_w(){
        switch(instruction_cycle){
            case 1: instruction_cycle++;break;
            case 2:
                address = map->cpureadu(program_counter);
                program_counter++;
                instruction_cycle++;
                break;
            case 3:
                address += x_index_register;
                instruction_cycle++;
                pollInterrupts();
                break;
            case 4:
                address&=0xff;
                executeOp();
                instruction_cycle = 1;
                break;
        }
    }

 void CPU_6502::abs_r(){
        switch(instruction_cycle){
            case 1: instruction_cycle++;break;
            case 2:
                address = map->cpureadu(program_counter);
                program_counter++;
                instruction_cycle++;
                break;
            case 3:
                address = address|(map->cpureadu(program_counter)<<8);
                program_counter++;
                instruction_cycle++;
                pollInterrupts();
                break;
            case 4:
                tempregister = map->cpuread(address);
                instruction_cycle=1;doOp=true;
                break;
        }
    }

 void CPU_6502::abs_rw(){
        switch(instruction_cycle){
            case 1: instruction_cycle++;break;
            case 2:
                address = map->cpureadu(program_counter);
                program_counter++;
                instruction_cycle++;
                break;
            case 3:
                address = address|(map->cpureadu(program_counter)<<8);
                program_counter++;
                instruction_cycle++;
                break;
            case 4:
                tempregister = map->cpuread(address);
                instruction_cycle++;
                break;
            case 5:
                map->cpuwrite(address,tempregister);
                executeOp();
                pollInterrupts();
                instruction_cycle++;
                break;
            case 6:
                map->cpuwrite(address, tempregister);
                instruction_cycle = 1;
                address = 0;
                break;
        }
    }

 void CPU_6502::abs_w(){
        switch(instruction_cycle){
            case 1: instruction_cycle++;break;
            case 2:
                address = (map->cpureadu(program_counter));
                program_counter++;
                instruction_cycle++;
                break;
            case 3:
                address = address|(map->cpureadu(program_counter)<<8);
                program_counter++;
                instruction_cycle++;
                pollInterrupts();
                break;
            case 4:
                executeOp();
                address = 0;
                instruction_cycle=1;
                break;
        }
    }

 void CPU_6502::abs_x_r(){
        switch(instruction_cycle){
            case 1: instruction_cycle++;break;
            case 2:
                address = map->cpureadu(program_counter);
                program_counter++;
                instruction_cycle++;break;
            case 3:
                address |= (map->cpureadu(program_counter)<<8);
                lowpc = address&0xff00;
                address += x_index_register;
                program_counter++;
                if(lowpc==(address&0xff00)){
                    pollInterrupts();
                }
                instruction_cycle++;break;
            case 4:
                if(lowpc!=(address&0xff00)){
                    //brokenaddress = true;
                    address&=0xffff;
                    pollInterrupts();
                    tempregister=map->cpuread((address&0xff)|lowpc);
                    instruction_cycle++;break;
                }
                else{
                    address&=0xffff;
                    tempregister = map->cpuread(address);
                    instruction_cycle=1;doOp=true;
                    break;
                }
            case 5:
                tempregister = map->cpuread(address);
                instruction_cycle=1;doOp=true;break;

        }
    }
 void CPU_6502::abs_x_rw(){
        switch(instruction_cycle){
            case 1: instruction_cycle++;break;
            case 2:
                address= 0;
                address = address | map->cpureadu(program_counter);
                program_counter++;
                instruction_cycle++;break;
            case 3:
                address = address | (map->cpureadu(program_counter)<<8);
                lowpc = address&0xff00;
                address+= x_index_register;
                program_counter++;
                instruction_cycle++;break;
            case 4:
                address&=0xffff;
                tempregister=map->cpuread((address&0xff)|lowpc);
                instruction_cycle++;break;
            case 5:
                tempregister = map->cpuread(address);
                instruction_cycle++;break;
            case 6:
                map->cpuwrite(address, tempregister);
                executeOp();
                pollInterrupts();
                instruction_cycle++;break;
            case 7:
                map->cpuwrite(address, tempregister);
                instruction_cycle = 1;break;
        }
    }
 void CPU_6502::abs_x_w(){
        switch(instruction_cycle){
            case 1: instruction_cycle++;break;
            case 2:
                address = map->cpureadu(program_counter);
                program_counter++;
                instruction_cycle++;break;
            case 3:
                address = address | (map->cpureadu(program_counter)<<8);
                lowpc=address&0xff00;
                address += x_index_register;
                program_counter++;
                instruction_cycle++;break;
            case 4:
                address&=0xffff;
                tempregister = map->cpuread((address&0xff)|lowpc);
                pollInterrupts();
                instruction_cycle++;break;
            case 5:
                executeOp();
                instruction_cycle = 1;break;
        }
    }
 void CPU_6502::abs_y_r(){
        switch(instruction_cycle){
            case 1: instruction_cycle++;break;
            case 2:
                address = 0;
                address = map->cpureadu(program_counter);
                program_counter++;
                instruction_cycle++;break;
            case 3:
                address |= (map->cpureadu(program_counter)<<8);
                lowpc = address&0xff00;
                address += x_index_register;
                program_counter++;
                if(lowpc==(address&0xff00))
                    pollInterrupts();
                instruction_cycle++;break;
            case 4:
                if(lowpc!=(address&0xff00)){
                    brokenaddress = true;
                    address&=0xffff;
                    tempregister=map->cpuread((address&0xff)|lowpc);
                    pollInterrupts();
                    instruction_cycle++;break;
                }
                else{
                    address&=0xffff;
                    tempregister = map->cpuread(address);
                    instruction_cycle=1;doOp=true;break;
                }
            case 5:
                tempregister = map->cpuread(address);
                brokenaddress = false;
                instruction_cycle=1;doOp=true;break;
        }
    }
 void CPU_6502::abs_y_rw(){
        switch(instruction_cycle){
            case 1: instruction_cycle++;break;
            case 2:
                address= 0;
                address = address | map->cpureadu(program_counter);
                program_counter++;
                instruction_cycle++;break;
            case 3:
                address = address | (map->cpureadu(program_counter)<<8);
                lowpc = address&0xff00;
                address+= x_index_register;
                program_counter++;
                instruction_cycle++;break;
            case 4:
                address&=0xffff;
                tempregister=map->cpuread((address&0xff)|lowpc);
                instruction_cycle++;break;
            case 5:
                tempregister = map->cpuread(address);
                instruction_cycle++;break;
            case 6:
                map->cpuwrite(address, tempregister);
                executeOp();
                pollInterrupts();
                instruction_cycle++;break;
            case 7:
                map->cpuwrite(address, tempregister);
                instruction_cycle = 1;break;
        }
    }

 void CPU_6502::abs_y_w(){
        switch(instruction_cycle){
            case 1: instruction_cycle++;break;
            case 2:
                address = map->cpureadu(program_counter);
                program_counter++;
                instruction_cycle++;break;
            case 3:
                address = address | (map->cpureadu(program_counter)<<8);
                lowpc=address&0xff00;
                address += x_index_register;
                program_counter++;
                instruction_cycle++;break;
            case 4:
                address&=0xffff;
                tempregister = map->cpuread((address&0xff)|lowpc);
                pollInterrupts();
                instruction_cycle++;break;
            case 5:
                executeOp();
                instruction_cycle = 1;break;
        }
    }

 void CPU_6502::indx_r(){
        switch(instruction_cycle){
            case 1: instruction_cycle++;break;
            case 2:
                pointer = map->cpureadu(program_counter);
                program_counter++;
                instruction_cycle++;break;
            case 3:
                pointer = pointer+x_index_register;
                instruction_cycle++;break;
            case 4:
                pointer&=0xff;
                address = map->cpureadu(pointer);
                instruction_cycle++;break;
            case 5:
                pointer++;pointer&=0xff;
                address = address| (map->cpureadu(pointer)<<8);
                pollInterrupts();
                instruction_cycle++;break;
            case 6:
                tempregister = map->cpuread(address);
                instruction_cycle=1;doOp=true;break;
        }
    }
 void CPU_6502::indx_rw(){
        switch(instruction_cycle){
            case 1: instruction_cycle++;break;
            case 2:
                pointer = map->cpureadu(program_counter);
                program_counter++;
                instruction_cycle++;break;
            case 3:
                pointer = pointer+x_index_register;
                instruction_cycle++;break;
            case 4:
                pointer&=0xff;
                address = map->cpureadu(pointer);
                instruction_cycle++;break;
            case 5:
                pointer++;pointer&=0xff;
                address = address| (map->cpureadu(pointer)<<8);
                instruction_cycle++;break;
            case 6:
                tempregister = map->cpuread(address);
                instruction_cycle++;break;
            case 7:
                map->cpuwrite(address, tempregister);
                executeOp();
                pollInterrupts();
                instruction_cycle++;break;
            case 8:
                map->cpuwrite(address, tempregister);
                instruction_cycle=1;
        }
    }
 void CPU_6502::indx_w(){
        switch(instruction_cycle){
            case 1: instruction_cycle++;break;
            case 2:
                pointer = map->cpureadu(program_counter);
                program_counter++;
                instruction_cycle++;break;
            case 3:
                pointer = pointer+x_index_register;
                instruction_cycle++;break;
            case 4:
                pointer&=0xff;
                address = map->cpureadu(pointer);
                instruction_cycle++;break;
            case 5:
                pointer++;pointer&=0xff;
                address = address| (map->cpureadu(pointer)<<8);
                pollInterrupts();
                instruction_cycle++;break;
            case 6:
                tempregister=0;
                executeOp();
                instruction_cycle=1;
        }
    }
 void CPU_6502::indy_r(){
        switch(instruction_cycle){
            case 1: instruction_cycle++;break;
            case 2:
                pointer = map->cpureadu(program_counter);
                program_counter++;
                instruction_cycle++;break;
            case 3:
                address = map->cpureadu(pointer);
                instruction_cycle++;break;
            case 4:
                if(pointer+1>0xff)
                    address |= (map->cpureadu(0)<<8);
                else
                    address = address | (map->cpureadu(pointer+1)<<8);
                lowpc = address&0xff00;
                address += x_index_register;
                if(lowpc!=(address&0xff00))
                    brokenaddress=true;
                else
                    pollInterrupts();
                instruction_cycle++;break;
            case 5:
                if(brokenaddress){
                    brokenaddress=false;
                    tempregister=map->cpuread((address&0xff)|lowpc);
                    address&=0xffff;
                    pollInterrupts();
                    instruction_cycle++;break;
                }
                else{
                    tempregister = map->cpuread(address);
                    instruction_cycle=1;doOp=true;break;
                }
            case 6:
                tempregister=map->cpuread(address);
                instruction_cycle=1;doOp=true;break;
        }
    }
 void CPU_6502::indy_rw(){
        switch(instruction_cycle){
            case 1: instruction_cycle++;break;
            case 2: {
                pointer = map->cpureadu(program_counter);
                program_counter++;
                instruction_cycle++;break;
            }
            case 3: {
                address = map->cpureadu(pointer);
                instruction_cycle++;break;
            }
            case 4: {
                if(pointer+1>0xff)
                    address |= (map->cpureadu(0)<<8);
                else
                    address = address | (map->cpureadu(pointer+1)<<8);
                lowpc = address&0xff00;
                address += x_index_register;
                instruction_cycle++;break;
            }
            case 5: {
                if(address>0xffff)
                    address&=0xffff;
                tempregister= map->cpuread((address&0xff)|lowpc);
                instruction_cycle++;break;
            }
            case 6: {
                tempregister = map->cpuread(address);
                instruction_cycle++;break;
            }
            case 7: {
                map->cpuwrite(address, tempregister);
                executeOp();
                pollInterrupts();
                instruction_cycle++;break;
            }
            case 8: {
                map->cpuwrite(address, tempregister);
                instruction_cycle = 1; break;
            }
        }
    }

 void CPU_6502::indy_w(){
        switch(instruction_cycle){
            case 1: instruction_cycle++;break;
            case 2:
                pointer = map->cpureadu(program_counter);
                program_counter++;
                instruction_cycle++;break;
            case 3:
                address = map->cpureadu(pointer);
                instruction_cycle++;break;
            case 4:
                if(pointer+1>0xff)
                    address |= (map->cpureadu(0)<<8);
                else
                    address = address | (map->cpureadu(pointer+1)<<8);
                lowpc=address&0xff00;
                address += x_index_register;
                instruction_cycle++;break;
            case 5:
                tempregister=map->cpuread((address&0xff)|lowpc);
                instruction_cycle++;
                pollInterrupts();break;
            case 6:
                executeOp();
                instruction_cycle = 1;break;
        }
    }
 void CPU_6502::accumulator_m(){
        switch(instruction_cycle){
            case 1: pollInterrupts();instruction_cycle++;break;
            case 2:
                tempregister=accumulator;
                executeOp();
                accumulator = tempregister;
                instruction_cycle = 1;break;
        }
    }
 void CPU_6502::relative(){
        switch(instruction_cycle){
            case 1: pollInterrupts();instruction_cycle++;break;
            case 2:
                tempregister = map->cpuread(program_counter);
                program_counter++;
                //pollInterrupts();
                executeOp();
                if(branchtaken)
                    instruction_cycle++;
                else
                    instruction_cycle=1;
                break;
            case 3:
                /*executeOp();
                if(branchtaken){
                    instruction_cycle++;
                }
                else{
                    program_counter&=0xffff;
                    current_instruction=map->cpuread(program_counter);
                    program_counter++;
                    instruction_cycle=2;
                }
                break;*/
                if((program_counter&0xff00)==((program_counter+tempregister)&0xff00)){
                    program_counter+=tempregister;program_counter&=0xffff;
                    instruction_cycle=1;break;
                }
                else{
                    pollInterrupts();
                    program_counter+=tempregister;program_counter&=0xffff;
                    instruction_cycle++;break;
                }
            case 4:
                instruction_cycle=1;break;/*
			lowpc = program_counter&0xff00;
			program_counter+=tempregister;
			if((program_counter&0xff00)!=lowpc){
				program_counter&=0xffff;
				instruction_cycle=1;
			}
			else{
				program_counter&=0xffff;
				current_instruction=map->cpuread(program_counter);
				program_counter++;
				instruction_cycle=2;
			}
			break;*/
                //case 5:
                //	current_instruction = map->cpuread(program_counter);
                //	program_counter++;
                //	instruction_cycle=2;break;
        }
    }
 void CPU_6502::aac(){
        //if(showInvalid) System.out.println("Invalid instruction AAC");
        accumulator= (accumulator &tempregister);
        ZFlag = accumulator ==0; NFlag = accumulator<0;
        CFlag = NFlag;
    }
 void CPU_6502::adc(){
        int sum = accumulator + tempregister + (CFlag?1:0);
        CFlag = sum > 0xff;
        VFlag = (~(accumulator ^ tempregister) & (accumulator ^ sum) & 0x80) != 0;
        accumulator= sum;
        NFlag = accumulator<0;ZFlag = accumulator==0;
    }
 void CPU_6502::and_m(){
	 std::cout << " in and"<<std::endl;
        accumulator =  (accumulator & tempregister);
        ZFlag = accumulator==0; NFlag = accumulator<0;
		std::cout << tempregister << " " << accumulator << "Z: " << ZFlag << " N: " << NFlag << std::endl;
    }
 void CPU_6502::ane(){}
 void CPU_6502::arr(){
        //if(showInvalid) System.out.println("Invalid instruction ARR");
        accumulator= (accumulator&tempregister);
        int result = accumulator;
        result>>=1;
        if(CFlag) result|= 0x80;
        accumulator =  result;
        NFlag = accumulator<0;
        ZFlag = result==0;
        CFlag = ((accumulator&(0b1000000))!=0);
        VFlag = CFlag ^ ((accumulator&0b100000)!=0);
    }
 void CPU_6502::asl(){
        int temp = tempregister;
        CFlag = (tempregister & 0x80) != 0;
        tempregister =  (temp<<1);
        ZFlag = tempregister==0; NFlag = tempregister<0;
    }
 void CPU_6502::asr(){
        accumulator =  (accumulator & tempregister);
        CFlag = (accumulator&1)!=0;
        accumulator= (accumulator>>1);
        ZFlag = accumulator ==0; NFlag = accumulator<0;
    }
 void CPU_6502::atx(){
        //if(showInvalid)System.out.println("Invalid instruction ATX");
        x_index_register = accumulator=tempregister;
        ZFlag = accumulator ==0; NFlag = accumulator<0;
    }
 void CPU_6502::axs(){
        //if(showInvalid)System.out.println("Invalid instruction AXS");
        int result = x_index_register;
        result &= accumulator;
        CFlag = result>=tempregister;
        result-= tempregister;
        x_index_register =  result;
        NFlag = x_index_register<0; ZFlag = x_index_register==0;
    }
 void CPU_6502::bcc(){branchtaken=!CFlag;}
 void CPU_6502::bcs(){branchtaken=CFlag;}
 void CPU_6502::beq(){branchtaken=ZFlag;}
 void CPU_6502::bit(){
        ZFlag = (accumulator&tempregister)==0;
        NFlag = (tempregister&0x80)!=0; VFlag = (tempregister&0x40)!=0;
    }
 void CPU_6502::bmi(){branchtaken=NFlag;}
 void CPU_6502::bne(){branchtaken=!ZFlag;}
 void CPU_6502::bpl(){branchtaken=!NFlag;}
 void CPU_6502::brk(){
        switch(instruction_cycle){
            case 1: instruction_cycle++;break;
            case 2:
                program_counter++;
                instruction_cycle++;break;
            case 3:
                map->cpuwrite(stack_pointer+0x100, (program_counter>>8));
                stack_pointer--;
                instruction_cycle++;break;
            case 4:
                //if(!(doNMI&&nmi))
                //	nmihijack = false;
                map->cpuwrite(stack_pointer+0x100, (program_counter&0xff));
                stack_pointer--;
                instruction_cycle++;
                pollInterrupts();
                if(nmiInterrupt){
                    nmihijack=true;
                    nmiInterrupt=false;
                    irqInterrupt=false;
                }
                break;
            case 5:
                BFlag = true;
                map->cpuwrite(stack_pointer+0x100, buildFlags());
                BFlag = false;
                if(nmihijack){
                    current_instruction = 0x02;
                    nmihijack=false;
                }
                stack_pointer--;
                instruction_cycle++;break;
            case 6:
                program_counter = map->cpureadu(0xfffe);
                instruction_cycle++;break;
            case 7:
                program_counter |= map->cpureadu(0xffff)<<8;
                IFlag = true;
                instruction_cycle = 1;break;
        }
    }
 void CPU_6502::bvc(){branchtaken=!VFlag;}
 void CPU_6502::bvs(){branchtaken=VFlag;}
 void CPU_6502::clc(){
        switch(instruction_cycle){
            case 1: pollInterrupts();instruction_cycle++;break;
            case 2: CFlag = false;instruction_cycle = 1;break;
        }
    }
 void CPU_6502::cld(){
        switch(instruction_cycle){
            case 1: pollInterrupts();instruction_cycle++;break;
            case 2: DFlag = false;instruction_cycle = 1;break;
        }
    }
 void CPU_6502::cli(){
        switch(instruction_cycle){
            case 1: pollInterrupts();instruction_cycle++;break;
            case 2: IFlag = false;instruction_cycle = 1;break;
        }
    }
 void CPU_6502::clv(){
        switch(instruction_cycle){
            case 1: pollInterrupts();instruction_cycle++;break;
            case 2: VFlag = false;instruction_cycle = 1;break;
        }
    }
 void CPU_6502::cmp(){
        CFlag = accumulator>=tempregister;
        ZFlag = accumulator==tempregister;
        NFlag = ((accumulator-tempregister)&0x80)!=0;
        //if(accumulator>=tempregister) CFlag = true;else CFlag = false;
        //if(accumulator == tempregister) ZFlag = true;else ZFlag = false;
        //if(((accumulator-tempregister)&0x80)!=0) NFlag = true;else NFlag = false;
    }
 void CPU_6502::cpx(){
        CFlag = x_index_register>=tempregister;
        ZFlag = x_index_register==tempregister;
        NFlag = ((x_index_register-tempregister)&0x80)!=0;
        //if(x_index_register>=tempregister) CFlag = true;else CFlag = false;
        //if(x_index_register == tempregister) ZFlag = true;else ZFlag = false;
        //if(((x_index_register-tempregister)&0x80)!=0) NFlag = true;else NFlag = false;
    }
 void CPU_6502::cpy(){
        CFlag = y_index_register>=tempregister;
        ZFlag = y_index_register==tempregister;
        NFlag = ((y_index_register-tempregister)&0x80)!=0;
        //if(y_index_register>=tempregister) CFlag = true;else CFlag = false;
        //if(y_index_register == tempregister) ZFlag = true;else ZFlag = false;
        //if(((y_index_register-tempregister)&0x80)!=0) NFlag = true;else NFlag = false;
    }
 void CPU_6502::dcp(){
        //if(showInvalid)System.out.println("Invalid instruction DCP");
        tempregister--;
        CFlag = accumulator >= tempregister;
        ZFlag = accumulator == tempregister;
        NFlag = ((accumulator - tempregister) & 0x80) != 0;
    }
 void CPU_6502::dec(){
        tempregister--;
        ZFlag = tempregister==0;NFlag = tempregister<0;
    }
 void CPU_6502::dex(){
        switch(instruction_cycle){
            case 1: pollInterrupts();instruction_cycle++;break;
            case 2:
                x_index_register--;
                ZFlag = x_index_register==0;NFlag = x_index_register<0;
                instruction_cycle = 1;break;
        }
    }
 void CPU_6502::dey(){
        switch(instruction_cycle){
            case 1: pollInterrupts();instruction_cycle++;break;
            case 2:
                y_index_register--;
                ZFlag = y_index_register==0;NFlag = y_index_register<0;
                instruction_cycle = 1;
        }
    }
 void CPU_6502::eor(){
        accumulator =  (accumulator ^ tempregister);
        ZFlag = accumulator==0;NFlag = accumulator<0;
    }
 void CPU_6502::hlt(){}
 void CPU_6502::inc(){
        tempregister++;
        ZFlag = tempregister==0;NFlag = tempregister<0;
    }
 void CPU_6502::inx(){
        switch(instruction_cycle){
            case 1: pollInterrupts();instruction_cycle++;break;
            case 2:
                x_index_register++;
                ZFlag = x_index_register==0;NFlag = x_index_register<0;
                instruction_cycle = 1;break;
        }
    }
 void CPU_6502::iny(){
        switch(instruction_cycle){
            case 1: pollInterrupts();instruction_cycle++;break;
            case 2:
                y_index_register++;
                ZFlag = y_index_register==0;NFlag = y_index_register<0;
                instruction_cycle = 1;break;
        }
    }
 void CPU_6502::irq(){
        switch(instruction_cycle){
            case 1: instruction_cycle++;break;
            case 2: instruction_cycle++;break;
            case 3:
                map->cpuwrite(stack_pointer+0x100, (program_counter>>8));
                stack_pointer--;
                instruction_cycle++;break;
            case 4:
                map->cpuwrite(stack_pointer+0x100, (program_counter&0xff));
                stack_pointer--;
                instruction_cycle++;
                //pollInterrupts();
                if(doNMI&&!oldnmi){
                    nmihijack=true;
                    nmiInterrupt=false;
                    irqInterrupt=false;
                }
                break;
            case 5:
                BFlag=false;
                map->cpuwrite(stack_pointer+0x100, buildFlags());
                //pollInterrupts();
                if(nmihijack){
                    current_instruction=0x02;
                    nmihijack=false;
                    nmiInterrupt=false;
                }
                stack_pointer--;
                instruction_cycle++;break;
            case 6:
                program_counter = map->cpureadu(0xfffe);
                instruction_cycle++;
                IFlag = true;break;
            case 7:
                program_counter = (map->cpureadu(0xffff)<<8)|program_counter;
                instruction_cycle = 1;break;
        }
    }
 void CPU_6502::isb(){
        tempregister++;
        //if(showInvalid)System.out.println("Invalid instruction ISB");
        int sum = accumulator - tempregister - (CFlag?0:1);
        CFlag = (sum>>8 ==0);
        VFlag = (((accumulator^tempregister)&0x80)!=0)&&(((accumulator^sum)&0x80)!=0);
        accumulator= (sum&0xff);
        NFlag = accumulator<0;ZFlag = accumulator==0;
    }
 void CPU_6502::jmp(){
        switch(instruction_cycle){
            case 1: instruction_cycle++;break;
            case 2:
                address = map->cpureadu(program_counter);
                program_counter++;
                instruction_cycle++;break;
            case 3:
                address = address | (map->cpureadu(program_counter)<<8);
                program_counter++;
                instruction_cycle++;break;
            case 4:
                program_counter = map->cpureadu(address);
                instruction_cycle++;
                //pollInterrupts();
                break;
            case 5:
                if(((address+1)&0xFF) ==0) address = address&0xFF00;else address++;
                program_counter = program_counter | (map->cpureadu(address)<<8);
                instruction_cycle =1; break;
        }
    }
 void CPU_6502::jmp_a(){
        switch(instruction_cycle){
            case 1:instruction_cycle++;break;
            case 2:
                address = map->cpureadu(program_counter);
                program_counter++;
                instruction_cycle++;
                pollInterrupts();
                break;
            case 3:
                address = address|(map->cpureadu(program_counter)<<8);
                program_counter= address;
                //pollInterrupts();
                instruction_cycle=1;
                break;
                //case 4:
                //	instruction_cycle=1;break;
        }
    }
 void CPU_6502::jsr(){
        switch(instruction_cycle){
            case 1: instruction_cycle++;break;
            case 2:
                address = map->cpureadu(program_counter);
                program_counter++;
                instruction_cycle++;return;
            case 3:
                instruction_cycle++;break;
            case 4:
                map->cpuwrite(stack_pointer+0x0100, (program_counter>>8));
                stack_pointer--;
                instruction_cycle++;break;
            case 5:
                map->cpuwrite(stack_pointer+0x0100,  (program_counter & 0x00FF));
                stack_pointer--;
                instruction_cycle++;
                pollInterrupts();break;
            case 6:
                address = address | (map->cpureadu(program_counter)<<8);
                program_counter =  address;
                instruction_cycle = 1;break;
        }
    }
 void CPU_6502::las(){}
 void CPU_6502::lax(){
        //if(showInvalid)System.out.println("Invalid instruction LAX");
        x_index_register = accumulator = tempregister;
        NFlag = accumulator<0;ZFlag = accumulator==0;
    }
 void CPU_6502::lda(){
        accumulator = tempregister;
        ZFlag = accumulator==0;NFlag = accumulator<0;
    }
 void CPU_6502::ldx(){
        x_index_register = tempregister;
        ZFlag = x_index_register==0;NFlag = x_index_register<0;
    }
 void CPU_6502::ldy(){
        y_index_register = tempregister;
        ZFlag = y_index_register==0;NFlag = y_index_register<0;
    }
 void CPU_6502::lsr(){
        CFlag = (tempregister & 1) > 0;
        tempregister= (tempregister>>1);
        ZFlag = tempregister==0;NFlag = tempregister<0;
    }
 void CPU_6502::nmi_m(){
        switch(instruction_cycle){
            case 1: instruction_cycle++;break;
            case 2:
                instruction_cycle++;
            case 3:
                map->cpuwrite(stack_pointer+0x100, (program_counter>>8));
                stack_pointer--;
                instruction_cycle++;break;
            case 4:
                map->cpuwrite(stack_pointer+0x100, (program_counter&0xff));
                stack_pointer--;
                instruction_cycle++;break;
            case 5:
                BFlag = false;
                map->cpuwrite(stack_pointer+0x100, buildFlags());
                stack_pointer--;
                instruction_cycle++;break;
            case 6:
                program_counter = map->cpureadu(0xfffa);
                instruction_cycle++;
                IFlag = true;break;
            case 7:
                program_counter = (map->cpureadu(0xfffb)<<8)|program_counter;

                instruction_cycle = 1;break;
        }
    }
 void CPU_6502::nop(){
        switch(instruction_cycle){
            case 1: pollInterrupts();
                instruction_cycle++;break;
            case 2:instruction_cycle=1;break;
                //case 3:instruction_cycle=1;break;
        }
    }
 void CPU_6502::skb(){
        //if(showInvalid)System.out.println("Invalid instruction SKB");
    }
 void CPU_6502::skw(){
        //if(showInvalid)System.out.println("Invalid instruction SKW");
    }
 void CPU_6502::ora(){
        accumulator =  (accumulator | tempregister);
        ZFlag = accumulator==0;NFlag = accumulator<0;
    }
 void CPU_6502::pha(){
        switch(instruction_cycle){
            case 1: instruction_cycle++;break;
            case 2:
                instruction_cycle++;
                pollInterrupts();break;
            case 3:
                map->cpuwrite(stack_pointer+0x0100, accumulator);
                stack_pointer--;
                instruction_cycle = 1;break;
        }
    }
 void CPU_6502::php(){
        switch(instruction_cycle){
            case 1: instruction_cycle++;break;
            case 2:
                instruction_cycle++;
                pollInterrupts();break;
            case 3:
                BFlag = true;
                uint8_t x =buildFlags();
                map->cpuwrite(stack_pointer+0x0100,x);
                BFlag = false;
                stack_pointer--;
                instruction_cycle = 1;break;
        }

    }
 void CPU_6502::pla(){
        switch(instruction_cycle){
            case 1: instruction_cycle++;break;
            case 2:
                instruction_cycle++;break;
            case 3:
                stack_pointer++;
                instruction_cycle++;
                pollInterrupts();break;
            case 4:
                accumulator = map->cpuread(stack_pointer+0x0100);
                ZFlag = accumulator==0;NFlag = accumulator<0;
                instruction_cycle = 1;break;
        }
    }
 void CPU_6502::plp(){
        switch(instruction_cycle){
            case 1: instruction_cycle++;break;
            case 2:
                instruction_cycle++;break;
            case 3:
                stack_pointer++;
                instruction_cycle++;
                pollInterrupts();break;

            case 4:
                setFlags( map->cpuread(stack_pointer+0x0100));
                instruction_cycle = 1;break;
        }
    }
 void CPU_6502::rla(){
        //if(showInvalid)System.out.println("Invalid instruction RLA");
        int tcarry = tempregister<0?1:0;
        tempregister =  (tempregister<<1);
        tempregister =  (tempregister | (CFlag?1:0));
        CFlag = tcarry == 1;
        ZFlag = tempregister == 0;
        NFlag = tempregister < 0;
        accumulator =  (accumulator & tempregister);
        ZFlag = accumulator==0;NFlag = accumulator<0;
    }
 void CPU_6502::rol(){
        int tcarry = tempregister<0?1:0;
        tempregister =  (tempregister<<1);
        tempregister =  (tempregister | (CFlag?1:0));
        CFlag = tcarry == 1;
        ZFlag = tempregister==0;NFlag = tempregister<0;
    }
 void CPU_6502::ror(){
        int tcarry = tempregister&0x01;
        tempregister=  (tempregister>>1);
        tempregister =  (tempregister | (CFlag?0x80:0));
        CFlag = tcarry != 0;
        ZFlag = tempregister==0;NFlag = tempregister<0;
    }
 void CPU_6502::rra(){
        //if(showInvalid)System.out.println("Invalid instruction RRA");
        if(CFlag){
            CFlag = (tempregister&1)!=0;
            tempregister =  ((tempregister>>1) | 0x80);
        }
        else{
            CFlag = (tempregister&1)!=0;
            tempregister =  (tempregister>>1);
        }
        int sum = accumulator + tempregister + (CFlag?1:0);
        CFlag = sum > 0xff;
        VFlag = (~(accumulator ^ tempregister) & (accumulator ^ sum) & 0x80) != 0;
        accumulator= sum;
        NFlag = accumulator<0;ZFlag = accumulator==0;
    }
 void CPU_6502::rti(){
        switch(instruction_cycle){
            case 1: instruction_cycle++;break;
            case 2:
                instruction_cycle++;break;
            case 3:
                stack_pointer++;
                instruction_cycle++;break;
            case 4:
                setFlags(map->cpuread(stack_pointer+0x0100));
                stack_pointer++;
                instruction_cycle++;break;
            case 5:
                program_counter = map->cpureadu(stack_pointer+0x0100);
                stack_pointer++;
                instruction_cycle++;
                pollInterrupts();break;
            case 6:
                program_counter = program_counter| (map->cpureadu(stack_pointer+0x0100)<<8);
                instruction_cycle = 1;
                break;
        }
    }
 void CPU_6502::rts(){
        switch(instruction_cycle){
            case 1: instruction_cycle++;break;
            case 2:
                instruction_cycle++;break;
            case 3:
                stack_pointer++;
                instruction_cycle++;break;
            case 4:
                program_counter = 0;
                program_counter = map->cpureadu(stack_pointer+0x0100);
                stack_pointer++;
                instruction_cycle++;break;
            case 5:
                program_counter = (program_counter | (map->cpureadu(stack_pointer+0x0100)<<8));
                instruction_cycle++;
                pollInterrupts();break;
            case 6:
                program_counter++;
                instruction_cycle=1;break;
        }
    }
 void CPU_6502::sax(){
        //if(showInvalid)System.out.println("Invalid instruction SAX");
        tempregister =  (x_index_register&accumulator);
        map->cpuwrite(address, tempregister);
    }
 void CPU_6502::sbc(){
        tempregister= ~tempregister;
        int sum = accumulator + tempregister + (CFlag?1:0);
        CFlag = sum > 0xff;
        VFlag = (~(accumulator ^ tempregister) & (accumulator ^ sum) & 0x80) != 0;
        accumulator= sum;
        NFlag = accumulator<0;ZFlag = accumulator==0;
    }
 void CPU_6502::sec(){
        switch(instruction_cycle){
            case 1: pollInterrupts();instruction_cycle++;break;
            case 2: CFlag = true;instruction_cycle = 1;break;
        }
    }
 void CPU_6502::sed(){
        switch(instruction_cycle){
            case 1: pollInterrupts();instruction_cycle++;break;
            case 2: DFlag = true;instruction_cycle = 1;break;
        }
    }
 void CPU_6502::sei(){
        switch(instruction_cycle){
            case 1: pollInterrupts();instruction_cycle++;break;
            case 2: IFlag = true;instruction_cycle = 1;break;
        }
    }
 void CPU_6502::sha(){}
 void CPU_6502::shs(){}
 void CPU_6502::shx(){
        //if(showInvalid)System.out.println("Invalid instruction SHX");
        switch(instruction_cycle){
            case 1: instruction_cycle++;break;
            case 2:
                address = map->cpureadu(program_counter);
                program_counter++;
                instruction_cycle++;
                break;
            case 3:
                address|= map->cpureadu(program_counter)<<8;
                program_counter++;
                instruction_cycle++;
                break;
            case 4:
                map->cpureadu(address);
                instruction_cycle++;
                break;
            case 5:
                int t =(x_index_register&((address>>8)+1))&0xff;
                lowpc = address&0xff00;
                address+=y_index_register;
                address = (address&0xff)|lowpc;
                map->cpuread((address&0xff)|lowpc);
                map->cpuwrite(address&0xffff,t);
                instruction_cycle=1;
                break;
        }
    }
 void CPU_6502::shy(){
        //if(showInvalid)System.out.println("Invalid instruction SHY");
        switch(instruction_cycle){
            case 1: instruction_cycle++;break;
            case 2:
                address = map->cpureadu(program_counter);
                program_counter++;
                instruction_cycle++;
                break;
            case 3:
                address|= map->cpureadu(program_counter)<<8;
                program_counter++;
                instruction_cycle++;
                break;
            case 4:
                map->cpureadu(address);
                instruction_cycle++;
                break;
            case 5:
                int t =(y_index_register&((address>>8)+1))&0xff;
                lowpc = address&0xff00;
                address+=x_index_register;
                address = (address&0xff)|lowpc;
                map->cpuread((address&0xff)|lowpc);
                map->cpuwrite(address&0xffff,t);
                instruction_cycle=1;
                break;
        }

    }
 void CPU_6502::slo(){
        //if(showInvalid)System.out.println("Invalid instruction SLO");
        CFlag = (tempregister&0x80)!=0;
        tempregister<<=1;
        accumulator|=tempregister;
        NFlag = accumulator<0;ZFlag = accumulator==0;
    }
 void CPU_6502::sre(){
        int result = tempregister;
        CFlag = (result&1)!=0;
        result>>=1;
        accumulator ^=result;
        tempregister = result;
        NFlag = accumulator<0;ZFlag = accumulator==0;
    }
 void CPU_6502::sta(){map->cpuwrite(address, accumulator);}
 void CPU_6502::stx(){map->cpuwrite(address, x_index_register);}
 void CPU_6502::sty(){map->cpuwrite(address, y_index_register);}
 void CPU_6502::tax(){
        switch(instruction_cycle){
            case 1: pollInterrupts();instruction_cycle++;break;
            case 2:
                x_index_register= accumulator;
                ZFlag = x_index_register==0;NFlag = x_index_register<0;
                instruction_cycle = 1;break;
        }
    }
 void CPU_6502::tay(){
        switch(instruction_cycle){
            case 1: pollInterrupts();instruction_cycle++;break;
            case 2:
                y_index_register=accumulator;
                ZFlag = y_index_register==0;NFlag = y_index_register<0;
                instruction_cycle = 1;break;
        }
    }
 void CPU_6502::tsx(){
        switch(instruction_cycle){
            case 1: pollInterrupts();instruction_cycle++;break;
            case 2:
                x_index_register = stack_pointer;
                ZFlag = x_index_register==0;NFlag = x_index_register<0;
                instruction_cycle = 1;break;
        }
    }
 void CPU_6502::txa(){
        switch(instruction_cycle){
            case 1: pollInterrupts();instruction_cycle++;break;
            case 2:
                accumulator = x_index_register;
                ZFlag = accumulator==0;NFlag = accumulator<0;
                instruction_cycle = 1;break;
        }
    }
 void CPU_6502::txs(){
        switch(instruction_cycle){
            case 1: pollInterrupts(); instruction_cycle++;break;
            case 2:
                stack_pointer = x_index_register;
                instruction_cycle = 1;break;
        }
    }
 void CPU_6502::tya(){
        switch(instruction_cycle){
            case 1: pollInterrupts();instruction_cycle++;break;
            case 2:
                accumulator = y_index_register;
                ZFlag = accumulator==0;NFlag = accumulator<0;
                instruction_cycle = 1;break;
        }
    }
    //methods

    void CPU_6502::run_cycle(){
        if(stallcount>0){
            --stallcount;
            return;
        }
        if(writeDMA)
            dma();
        else
            executeInstruction();
    }
    void CPU_6502::setNMI(bool donmi){
        doNMI=donmi;
        if(donmi&&!oldnmi)
            nmi=true;
        oldnmi=donmi;
    }
    void CPU_6502::setIRQ(IRQSource irq){
        switch(irq){
            case External:
                if(!irqs[0]){
                    irqs[0] = true;
                    doIRQ++;
                }
                break;
            case FrameCounter:
                if(!irqs[1]){
                    irqs[1] = true;
                    doIRQ++;
                }
                break;
            case DMC:
                if(!irqs[2]){
                    irqs[2] = true;
                    doIRQ++;
                }
                break;
        }
    }
    void CPU_6502::removeIRQ(IRQSource irq){
        switch(irq){
            case External:
                if(irqs[0]){
                    irqs[0] = false;
                    doIRQ--;
                }
                break;
            case FrameCounter:
                if(irqs[1]){
                    irqs[1] = false;
                    doIRQ--;
                }
                break;
            case DMC:
                if(irqs[2]){
                    irqs[2] = false;
                    doIRQ--;
                }
                break;
        }
    }

