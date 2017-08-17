package core.mappers;

import java.util.Arrays;

public class Mapper_66 extends Mapper {
    public Mapper_66 (){
        super();
    }

    @Override
    public void cartridgeWrite(int index, byte b){
        if(index>=0x8000&&index<=0xffff){
            int prg = (b>>4)&0xf;
            int chr = (b&0xf);
            PRG_ROM[0] = PRGbanks[(prg*2)&(PRGbanks.length-1)];
            PRG_ROM[1] = PRGbanks[(prg*2+1)&(PRGbanks.length-1)];
            CHR_ROM[0] = CHRbanks[(chr*2)&(CHRbanks.length-1)];
            CHR_ROM[1] = CHRbanks[(chr*2+1)&(CHRbanks.length-1)];
        }
    }
    @Override
    public void setPRG(byte[] prg){
        PRGbanks = new byte[prg.length/0x4000][0x4000];
        for(int i=0;i*0x4000<prg.length;i++){
            PRGbanks[i]= Arrays.copyOfRange(prg, i*0x4000, (i*0x4000)+0x4000);
        }
        System.out.println("Bank size:" +PRGbanks.length);
        PRG_ROM[0]=PRGbanks[0];
        PRG_ROM[1]=PRGbanks[PRGbanks.length-1];
    }
    @Override
    public void setCHR(byte[] chr){
        CHRbanks = new byte[chr.length/0x1000][0x1000];
        if(chr.length>0){
            for(int i=0;i*0x1000<chr.length;i++){
                CHRbanks[i]= Arrays.copyOfRange(chr, i*0x1000, (i*0x1000)+0x1000);
            }
            CHR_ROM[0] = CHRbanks[0];
            CHR_ROM[1] = CHRbanks[1];
        }
        else CHR_ram = true;
    }
}
