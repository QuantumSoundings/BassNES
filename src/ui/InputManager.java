package ui;

import java.util.ArrayList;
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
				currentFrameInputs = decodeframe(recorded.get(framenumber));
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
				recorded = new ArrayList<String>();
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
	public ArrayList<String> recorded = new ArrayList<String>();
	public void recordFrame(){
		//System.out.println("recording frame! "+Arrays.toString(currentFrameInputs[0]));
		recorded.add(encodeframe());
	}
	public String encodeframe(){
		String s ="";
		for(boolean b:currentFrameInputs[0])
			s+=b+" ";
		for(boolean b:currentFrameInputs[1])
			s+=b+" ";
		return s;
	}
	
	public boolean[][] decodeframe(String s){
		Scanner scan = new Scanner(s);
		boolean[][] out = new boolean[2][8];
		for(int i = 0; i<8;i++){
			out[0][i] = scan.nextBoolean();
		}
		for(int i = 0; i<8;i++){
			out[1][i] = scan.nextBoolean();
		}
		scan.close();
		return out;	
	}
	public static void saveRecordedFrames(){
		
	}
}
