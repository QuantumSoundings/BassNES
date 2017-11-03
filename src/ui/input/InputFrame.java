package ui.input;

import java.util.Arrays;

public class InputFrame {
    int frameNumber;
    boolean[][] controllerInputs=new boolean[2][8];
    public InputFrame(boolean[][] inputs){
        System.arraycopy(inputs[0],0,controllerInputs[0],0,inputs[0].length);
        System.arraycopy(inputs[1],0,controllerInputs[1],0,inputs[1].length);
        //controllerInputs = inputs;
    }
    public String toString(){
        String s = "[ "+ Arrays.toString(controllerInputs[0])+" , "+Arrays.toString(controllerInputs[1])+" ]";
        return s;
    }
}
