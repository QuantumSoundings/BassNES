package ui.ui.input;

import ui.ControllerInfo;
import ui.SystemUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class InputManager {
	public static ControllerInfo[] c1controls = new ControllerInfo[8];
	public static ControllerInfo[] c2controls = new ControllerInfo[8];
	public static ControllerInfo[] hotkeys = new ControllerInfo[4];
	public static boolean[][] currentFrameInputs = new boolean[2][8];
	public static boolean[] lasthotkey = new boolean[4];
	public static boolean playingback = false;
	public static boolean recording = false;
	private SystemUI sys;
	public InputManager(SystemUI s){
		sys = s;
	}
	public void updateInputs(){
		if(lasthotkey[0]==false&&hotkeys[0].checkPressed())
			sys.saveState(20);
		lasthotkey[0] = hotkeys[0].checkPressed();
		
		if(lasthotkey[1]==false&&hotkeys[1].checkPressed())
			sys.restoreState(20);
		lasthotkey[1] = hotkeys[1].checkPressed();
		
		if(playingback){
			if(framenumber>=recorded.size()){
				playingback = false;
				framenumber = 0;
			}
			else{
				currentFrameInputs = recorded.get(framenumber).controllerInputs;
				//System.out.println(recorded.get(framenumber).toString());
				framenumber++;			
			}
		}
		else{
			currentFrameInputs[0][0] = c1controls[0].checkPressed();
			currentFrameInputs[0][1] = c1controls[1].checkPressed();
			currentFrameInputs[0][2] = c1controls[2].checkPressed();
			currentFrameInputs[0][3] = c1controls[3].checkPressed();
			currentFrameInputs[0][4] = c1controls[4].checkPressed();
			currentFrameInputs[0][5] = c1controls[5].checkPressed();
			currentFrameInputs[0][6] = c1controls[6].checkPressed();
			currentFrameInputs[0][7] = c1controls[7].checkPressed();
			
			currentFrameInputs[1][0] = c1controls[0].checkPressed();
			currentFrameInputs[1][1] = c1controls[1].checkPressed();
			currentFrameInputs[1][2] = c1controls[2].checkPressed();
			currentFrameInputs[1][3] = c1controls[3].checkPressed();
			currentFrameInputs[1][4] = c1controls[4].checkPressed();
			currentFrameInputs[1][5] = c1controls[5].checkPressed();
			currentFrameInputs[1][6] = c1controls[6].checkPressed();
			currentFrameInputs[1][7] = c1controls[7].checkPressed();
		}
		if(recording)
			recordFrame();
		
		if(lasthotkey[2]==false&&hotkeys[2].checkPressed()){
			if(recorded.size()>0&&!recording)
				recorded = new ArrayList<InputFrame>();
			recording=!recording;
			if(recording ==false){
				System.out.println(recorded.size());
				//for(String r: recorded)
				//	System.out.println(r);
			}
		}
		lasthotkey[2] = hotkeys[2].checkPressed();
		
		if(lasthotkey[3]==false&&hotkeys[3].checkPressed()){
			playingback = !playingback;
			recording = false;
		}
		lasthotkey[3] = hotkeys[3].checkPressed();
		
	}
	public static int framenumber;
	public ArrayList<InputFrame> recorded = new ArrayList<InputFrame>();
	public void recordFrame(){
		recorded.add(new InputFrame(currentFrameInputs));
		String s = "[ "+ Arrays.toString(currentFrameInputs[0])+" , "+ Arrays.toString(currentFrameInputs[1])+" ]";
		String x = recorded.get(recorded.size()-1).toString();
		if(!s.equals(x))
			System.out.println("THEY ARNt the same!!!");
	}

	public static void saveRecordedFrames(){
		
	}
}
