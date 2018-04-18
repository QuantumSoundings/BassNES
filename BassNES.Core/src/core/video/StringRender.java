package core.video;

import jdk.internal.util.xml.impl.Input;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class StringRender {
    public enum fontsize{BIG,SMALL};
    int[][] bigletters = new int[60][0];
    int[][] smallletters = new int[60][0];
    public StringRender(){
        InputStream is = getClass().getResourceAsStream("/core/video/bigletters");
        int[] expandedBig = new int[252*128];
        expandedBig = decompress(is,expandedBig);
        buildAllBigLetters(expandedBig);
        is = getClass().getResourceAsStream("/core/video/smallletters");
        int[] expandedsmall = new int[248*25];
        expandedsmall = decompress(is,expandedsmall);
        buildAllSmallLetters(expandedsmall);
        int[] temp = smallletters[25];
        smallletters[25] = smallletters[24];
        smallletters[24] = temp;

    }

    private int[] decompress(InputStream in, int[] expanded){
        byte[] onebyte = new byte[1];
        int status = 0;
        int pos = 0;
        try {
            while(status != -1) {
                status = in.read(onebyte);
                if(status>0){
                    byte b = onebyte[0];
                    expanded[pos+7] = (b&1);
                    expanded[pos+6] = (b>>1)&1;
                    expanded[pos+5] = (b>>2)&1;
                    expanded[pos+4] = (b>>3)&1;
                    expanded[pos+3] = (b>>4)&1;
                    expanded[pos+2] = (b>>5)&1;
                    expanded[pos+1] = (b>>6)&1;
                    expanded[pos]   = (b>>7)&1;
                    pos+=8;
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return expanded;
    }
    private void  buildAllBigLetters(int[] big){
        int outnum = 0;
        for(int y = 0; y < 4;y++){
            for(int x = 0; x < 14; x++){
                buildBigLetter(big,y,x,outnum);
                outnum++;
            }
        }
    }
    private void buildBigLetter(int[] big, int y1, int x1,int outnum){
        bigletters[outnum] = new int[20*18];
        for(int y = y1*32; y < y1*32+20;y++){
            for(int x = x1*18; x < x1*18+19; x++){
                bigletters[outnum][(y%32)*18+(x%18)] = big[y*252+x];
            }
        }
    }
    private void buildAllSmallLetters(int[] small){
        int outnum = 0;
        for(int y = 0; y<2;y++){
            for(int x = 0; x < 31;x++){
                if(outnum<60) {
                    buildSmallLetter(small, y, x, outnum);
                }
                outnum++;
            }
        }
    }
    private void buildSmallLetter(int[] small,int y1, int x1, int outnum){
        smallletters[outnum] = new int[8*9];
        for(int y = y1*13; y < y1*13+9;y++){
            for(int x = x1*8; x < x1*8+8; x++){
                smallletters[outnum][(y%13)*8+(x%8)] = small[y*248+x];
            }
        }
    }
    private void drawBigLetter(int[] buf, int index, int x, int y){
        //System.out.println("Drawing a big letter");
        for(int y1 = 0; y1 < 20; y1++){
            for(int x1 = 0; x1<18;x1++){
                if(bigletters[index][y1*18+x1]==1) {
                    buf[(y1+y) * 256 + x1+x] = 0b11111111111111111111111111111111;
                    //System.out.println("Printing a white pixel!");
                }
                else
                    buf[(y1+y)*256+(x+x1)] = 0b11111111000000000000000000000000;;
            }
        }
    }
    private void drawSmallLetter(int[] buf, int index, int x, int y){
        for(int y1 = 0; y1 < 9; y1++){
            for(int x1 = 0; x1<8;x1++){
                if(smallletters[index][y1*8+x1]==1) {
                    buf[(y1+y) * 256 + x1+x] = 0b11111111111111111111111111111111;
                    //System.out.println("Printing a white pixel!");
                }
                else
                    buf[(y1+y)*256+(x+x1)] =   0b11111111000000000000000000000000;
            }
        }
    }

    public void drawStringToBuffer(int[] buf, String s, int x, int y, fontsize size){
        s= s.toLowerCase();
        //System.out.println("Drawing string: "+s+" to buffer.");
        for(char c:s.toCharArray()){
            int index = -1;
            if(Character.isLetter(c))
                index = c-'a';
            else if(Character.isDigit(c))
                index = 26+c-'0';
            else
                switch (c){
                    case ':': index = 36;break;
                    case ';': index = 37;break;
                    case '\'': index = 38;break;
                    case '\"': index = 39;break;
                    case ',': index = 40;break;
                    case '.': index = 41;break;
                    case '/': index = 42;break;
                    case '(': index = 43;break;
                    case ')': index = 44;break;
                    case ' ': index = 50;break;
                    default: index = 50;break;
                }
            switch(size){
                case SMALL:
                    if(index!=-1)
                        drawSmallLetter(buf,index,x,y);
                    x += 10;
                    if(x + 20 > 256)
                        return;
                    break;
                case BIG:
                    if(index!=-1)
                        drawBigLetter(buf,index,x,y);
                    x += 18;
                    if(x+18>256)return;
                    break;
            }

        }
    }


}
