package ui.input;

import ui.OSD;
import ui.SystemUI;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

public class ControllerManager implements ControllerInterface {
	private static ControllerInfo[][] controls = new ControllerInfo[2][8];
	//public static ControllerInfo[] c2controls = new ControllerInfo[8];
	private static ControllerInfo[] hotkeys = new ControllerInfo[8];
	private static boolean[][] currentFrameInputs = new boolean[2][8];
	private static boolean[] lasthotkey = new boolean[8];
	public static boolean playingback = false;
	public static boolean recording = false;
	private SystemUI sys;
	public ControllerManager(SystemUI s){
		sys = s;
	}
	@Override
	public void setControllerButton(int controllerNumber, ControllerInterface.ControllerButtons button) {
		controls[controllerNumber][button.ordinal()] = ControllerInfo.getButton();
	}

	@Override
	public void setQuickKeyButton(QuickKeyButtons button) {
		hotkeys[button.ordinal()] = ControllerInfo.getButton();
	}

	@Override
	public String getControllerButtonIdName(int controllerNumber, ControllerInterface.ControllerButtons button) {
		return controls[controllerNumber][button.ordinal()].getButtonName();
	}

	@Override
	public String getQuickKeyButtonIdName(QuickKeyButtons button) {
		return hotkeys[button.ordinal()].getButtonName();
	}

	@Override
	public void configureControllerInput(int controllerNumber, ControllerInterface.ControllerButtons button, String config) {
		System.out.println(config);
		controls[controllerNumber][button.ordinal()] = ControllerInfo.restoreInfo(config,ControllerInfo.defaultButtonConfig);
	}

	@Override
	public void configureQuickKeyInput(QuickKeyButtons button, String config) {
		hotkeys[button.ordinal()] = ControllerInfo.restoreInfo(config,ControllerInfo.defaultButtonConfig);
	}

	@Override
	public String getControllerInputConfig(int controllerNumber, ControllerButtons button) {
		return controls[controllerNumber][button.ordinal()].storeInfo();
	}

	@Override
	public String getQuickKeyInputConfig(QuickKeyButtons button) {
		return hotkeys[button.ordinal()].storeInfo();
	}

	@Override
	public boolean[][] getCurrentControllerOutputs() {
		return currentFrameInputs;
	}

	@Override
	public boolean[] getCurrentQuickKeyOutputs() {
		return lasthotkey;
	}

	@Override
	public void updateInputs(){
		if(lasthotkey[0]==false&&hotkeys[0].checkPressed())
			sys.saveState(20);
		lasthotkey[0] = hotkeys[0].checkPressed();
		
		if(lasthotkey[1]==false&&hotkeys[1].checkPressed())
			sys.restoreState(20);
		lasthotkey[1] = hotkeys[1].checkPressed();
		
		if(lasthotkey[4]==false&&hotkeys[4].checkPressed())
			sys.toggleRecording();
		lasthotkey[4] = hotkeys[4].checkPressed();
		
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
			currentFrameInputs[0][0] = controls[0][0].checkPressed();
			currentFrameInputs[0][1] = controls[0][1].checkPressed();
			currentFrameInputs[0][2] = controls[0][2].checkPressed();
			currentFrameInputs[0][3] = controls[0][3].checkPressed();
			currentFrameInputs[0][4] = controls[0][4].checkPressed();
			currentFrameInputs[0][5] = controls[0][5].checkPressed();
			currentFrameInputs[0][6] = controls[0][6].checkPressed();
			currentFrameInputs[0][7] = controls[0][7].checkPressed();
			
			currentFrameInputs[1][0] = controls[1][0].checkPressed();
			currentFrameInputs[1][1] = controls[1][1].checkPressed();
			currentFrameInputs[1][2] = controls[1][2].checkPressed();
			currentFrameInputs[1][3] = controls[1][3].checkPressed();
			currentFrameInputs[1][4] = controls[1][4].checkPressed();
			currentFrameInputs[1][5] = controls[1][5].checkPressed();
			currentFrameInputs[1][6] = controls[1][6].checkPressed();
			currentFrameInputs[1][7] = controls[1][7].checkPressed();
		}
		if(recording)
			recordFrame();
		
		if(lasthotkey[2]==false&&hotkeys[2].checkPressed()){
			if(recording)
				OSD.addOSDMessage("Stopping input recording...", 120);
			else
				OSD.addColoredOSDMessage("Starting input recording...", 120,Color.YELLOW);
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
			if(!playingback){
				OSD.addOSDMessage("Starting input playback...", 120);
			}
			playingback = !playingback;
			recording = false;
		}
		lasthotkey[3] = hotkeys[3].checkPressed();
		
	}
	private static int framenumber;
	private ArrayList<InputFrame> recorded = new ArrayList<InputFrame>();
	private void recordFrame(){
		recorded.add(new InputFrame(currentFrameInputs));
		String s = "[ "+ Arrays.toString(currentFrameInputs[0])+" , "+ Arrays.toString(currentFrameInputs[1])+" ]";
		String x = recorded.get(recorded.size()-1).toString();
		if(!s.equals(x))
			System.out.println("THEY ARNt the same!!!");
	}
	private static void saveRecordedFrames(){
		
	}

	private static boolean[][] getFormatedInputs(){
		return currentFrameInputs;
	}
}
