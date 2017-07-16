package ui;

public class InputManager {
	public static ControllerInfo[] c1controls = new ControllerInfo[8];
	public static ControllerInfo[] c2controls = new ControllerInfo[8];
	public static ControllerInfo[] hotkeys = new ControllerInfo[4];
	public static boolean[][] currentFrameInputs = new boolean[2][8];
	public static boolean[] lasthotkey = new boolean[4];
	
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
}
