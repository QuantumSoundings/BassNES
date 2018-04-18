package core.video;

import java.io.IOException;
import java.io.InputStream;

public class StringRender {
    int[][] fixedfont = new int[100][0];
    public StringRender(){
        InputStream in = getClass().getResourceAsStream("/core/video/fixedfont");
        int[] expanded = new int[760*11];
        buildAllLetters(decompress(in,expanded));

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
    private void buildAllLetters(int[] expanded){
        int outnum = 0;
        for(int x = 0; x< 95;x++){
            buildLetter(expanded,x,outnum);
            outnum++;
        }
    }
    private void buildLetter(int[] expanded,int xoffset, int outnum){
        fixedfont[outnum] = new int[8*11];
        for(int y = 0; y<11;y++){
            for(int x = xoffset*8;x<xoffset*8+8;x++){
                fixedfont[outnum][y*8+(x%8)] = expanded[y*760 + x];
            }
        }
    }
    private void drawLetter(int[] buf, int index, int x, int y){
        for(int y1 = 0; y1 < 11; y1++){
            for(int x1 = 0; x1<8;x1++){
                if(fixedfont[index][y1*8+x1]==1) {
                    buf[(y1+y) * 256 + x1+x] = 0b11111111111111111111111111111111;
                }
                else
                    buf[(y1+y)*256+(x+x1)] =   0b11111111000000000000000000000000;
            }
        }
    }

    public void drawStringToBuffer(int[] buf, String s, int x, int y){
        for(char c:s.toCharArray()){
            int index = c-'!';
            if(index <0)
                index = 94;
            drawLetter(buf,index,x,y);
            x+=10;
            if(x+10>256)
                return;
        }
    }


}
