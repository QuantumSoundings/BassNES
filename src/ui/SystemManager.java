package ui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.util.EventListener;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import core.*;
import core.exceptions.UnSupportedMapperException;
import ui.debugger.BreakPoint;
import ui.debugger.DebugCallback;
import ui.debugger.Debugger;
import ui.input.*;
import ui.settings.ConfigurationManager;
import ui.settings.UISettings;


interface UpdateEventListener extends EventListener {
	public void doVideoFrame();
}
interface MainUICallback {
	enum UIWindows{Main,Debug,GraphicsSettings,About,KeyConfig,AudioSettings,Scope};
	void changeWindowVisibility(UIWindows window, boolean visible);
	void updateNesDisplay(int scaling);
	void updateNesImage(int x, int y);
	void startdebugging();
	void exit();
	InputMap getHotKeyInput();
	ActionMap getHotKeyAction();
}
interface AudioInfoCallback {
	Object[][] getAudioInfo();
	int getSampleRate();
	int getBufferSize();
}
interface AudioUpdateCallback {
	void updateSamplingRate(int rate);
}


public class SystemManager implements NESCallback, MainUICallback, ControllerCallback, HotKeyCallback, AudioInfoCallback, AudioUpdateCallback, DebugCallback {
	private NES nes;
	private final JFileChooser fc;
	private JFrame mainWindow,debugWindow,keyconfigWindow,audiomixerWindow,advancedGraphicsWindow,aboutWindow;

	private final ControllerInterface input;
	private final HotKeyInterface hotkeys;
	private final ConfigurationManager configurator;

	private Debugger debugInfo;
	private File rom,config;
	private File configuration;
	private NesDisplay display;
	private UpdateEventListener listener;
	private int[] pixels;
	private Thread current;
	private AudioInterface audio;
	static{
		try {
			java.lang.System.loadLibrary("jinput-linux64");
		}catch (UnsatisfiedLinkError e){
			
		}
		try {
			java.lang.System.loadLibrary("jinput-linux");
		}catch (UnsatisfiedLinkError e){
			
		}
	}

	public SystemManager(){
		//Setup system configuration
		configuration = new File("config.properties");
		config = new File("settings.xml");
		input = new ControllerManager(this);
		hotkeys = new HotKeyManager(this);
		configurator = new ConfigurationManager(config,input,hotkeys);
		loadConfiguration();

		//Setup the filechooser for rom selection
		fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter("Nes Files","nes","nsf","nsfe","NSFE","NSFe","DefaultNES","NSF"));

		audio = new AudioInterface(this);
		debugInfo = new Debugger(this);

		//Setup Main Gui Windows
		mainWindow = new MainUI(this);
		//debugWindow = new DebugUI();
		keyconfigWindow = new ControlUI(input);
		audiomixerWindow = new AudioSettingsUI(this);
		advancedGraphicsWindow = new AdvancedGraphics(this);
		aboutWindow = new About();

		//Configure Initial NesDisplay settings
		display = new NesDisplay();
		display.setSize(256, 240);
		display.updateScaling(2);

		//Setup Listener for threaded video frames
		listener = new UpdateEventListener(){
			public void doVideoFrame() {
				display.sendFrame(pixels);
			}
		};
		//Finish main window setup
       	setupMainWindow();
	}
	private void loadConfiguration(){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

	private void setupMainWindow(){
		mainWindow.setFocusable(true);
		mainWindow.requestFocusInWindow();
		mainWindow.add(display);
		mainWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		mainWindow.setResizable(false);
		mainWindow.getContentPane().setPreferredSize(new Dimension(256*2,240*2));
		mainWindow.pack();
		mainWindow.setVisible(true);
	}




	boolean debugMode = false;
	boolean docpucycle= false;
	public boolean dobreakseek=false;
	public void start(){
		while(true){
			if(debugMode){
				if(nes!=null)
					nes.pause();
				if(docpucycle){
					nes.runCPUCycle();
					debugInfo.updateInfo();
					docpucycle = false;
				}
				else if(dobreakseek){
					BreakPoint.setsystem(this);
					seekBreakpoint();
					dobreakseek = false;
				}
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else{
				if(UISettings.ShowFPS){
					if(nes!=null)
						mainWindow.setTitle("BassNES - FPS: "+String.format("%.2f", nes.getFPS()));
				}
				else
					mainWindow.setTitle("BassNES");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}


	private void saveState(int slot){
		nes.pause();
		try {
			nes.saveState("savestateY.txt".replaceAll("Y", slot+""));
			OSD.addOSDMessage("Saving state to slot "+slot, 120);
		} catch (IOException e) {
			e.printStackTrace();
		}
		nes.unpause();
	}
	private void restoreState(int slot){
		nes.pause();
		try {
			nes.restoreState("savestateY.txt".replaceAll("Y", slot+""));
			OSD.addOSDMessage("Loading savestate "+slot, 120);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		nes.unpause();
	}

	private void resetaudio(){
		while(audio.inaudio);
		audio.lock=true;
		//nes.setSampleRate(NesSettings.sampleRate);
		audio.restartSDL();
		audio.lock=false;	
	}
	private void toggleRecording(){
		if(audio.recording)
			audio.stopRecording();
		else
			audio.startRecording();
	}

	private void enterDebug(){
		debugMode = true;
		debugInfo.setVisible(true);
		nes.pause();
		BreakPoint.setsystem(this);
		debugInfo.updateInfo();
	}

	private void seekBreakpoint(){
		boolean loop = true;
		while(loop){
			BreakPoint.updateData();
			for(Object point: debugInfo.breakpoints.toArray())
				if(((BreakPoint) point).checkbreakpoint())
					loop = false;
			nes.runCPUCycle();
		}
		debugInfo.updateInfo();
	}

	@Override
	public void changeWindowVisibility(UIWindows window, boolean visible){
		switch(window){
			case About:     	   aboutWindow.setVisible(visible);break;
			case Debug:            debugWindow.setVisible(visible);break;
			case AudioSettings:    audiomixerWindow.setVisible(visible);break;
			case GraphicsSettings: advancedGraphicsWindow.setVisible(visible);break;
			case KeyConfig:		   keyconfigWindow.setVisible(visible);break;
			case Scope:			   audio.showscope();break;
			default:break;
		}
	}
	@Override
	public void updateNesDisplay(int scaling){
		display.updateScaling(scaling);
	}
	@Override
	public void updateNesImage(int x, int y){
		display.updateImage(x,y);
	}
	@Override
	public void startdebugging(){

	}
	@Override
	public void exit(){
		System.out.println("Exiting BassNes...");
		if(nes!=null)
			nes.exit();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		configurator.saveSettings(config);
		java.lang.System.exit(0);
	}
	@Override
	public InputMap getHotKeyInput(){return hotkeys.getInputMap();}
	@Override
	public ActionMap getHotKeyAction(){return hotkeys.getActionMap();}


	/*
	Controller Hotkeys
	 */
	@Override
	public void doQuickKey(ControllerInterface.QuickKeyButtons action){
		switch(action){
			case SaveState: saveState(20);break;
			case LoadState: restoreState(20);break;
			case AudioRecord: toggleRecording();break;
			default:break;
		}
	}

	/*
	AudioInterface / Oscilloscope
	 */
	@Override
	public Object[][] getAudioInfo(){
		return nes.getAudioChannelInfo();
	}
	@Override
    public int getSampleRate(){return NesSettings.sampleRate; }
    @Override
    public int getBufferSize(){return NesSettings.audioBufferSize;}

	/*
	AudioSettings UI
	 */
	public void updateSamplingRate(int rate){
		if(nes!=null)
			nes.setSampleRate(NesSettings.sampleRate);
		resetaudio();
	}
	/*
	Debugging
	 */

	@Override
	public Object[] getCPUDebuggingInformation() {
		return nes.getCPUDebugInfo();
	}

	@Override
	public Object[] getPPUDebuggingInformation() {
		return nes.getPPUDebugInfo();
	}
	@Override
	public void exitDebugging(){
		debugMode = false;
		nes.unpause();
	}
	@Override
	public void doCpuCycle(){
		nes.runCPUCycle();
	}
	/*
	OSCILLOSCOPE SPECIFIC
	 */
	/*public Object[][] AudioChannelInfoCallback(){
		return nes.getAudioChannelInfo();
	}
	public void showscope(){
		audio.showscope();
	}
	/*
	DefaultNES CALLBACK METHODS
	 */

	@Override
	public void videoCallback(int[] pixels) {
		this.pixels=pixels;
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run(){
				listener.doVideoFrame();
				input.updateInputs();
			}
		});
	}

	@Override
	public void unmixedAudioSampleCallback(int[] audiosample) {
		//UNIMPLEMENTED
	}

	@Override
	public void audioSampleCallback(int audiosample) {
		//UNIMPLEMENTED
	}

	@Override
	public void audioFrameCallback(int[] audioInts) {
		audio.setAudioFrame(audioInts);
	}

	@Override
	public boolean[][] pollController() {
		boolean[][] out = new boolean[2][8];
		input.updateInputs();
		if(mainWindow.hasFocus()||UISettings.controlwhilenotfocused){
			return input.getCurrentControllerOutputs();
		}
		return out;
	}


	/*
	HotKeys
	 */
	@Override
	public void doHotKey(HotKeyInterface.HotKeys action){
		if(action.ordinal()<10)
			saveState(action.ordinal());
		else if(action.ordinal()<20)
			restoreState(action.ordinal()-10);
		else
			switch (action){
				case StartCPU:
				case LoadRom: loadRom();break;
			}
	}
	private void loadRom(){
		fc.setCurrentDirectory(new File(UISettings.lastLoadedDir));
		int returnval = fc.showOpenDialog(mainWindow);
		if(returnval == JFileChooser.APPROVE_OPTION){
			rom = fc.getSelectedFile();
			UISettings.lastLoadedDir = fc.getCurrentDirectory().getAbsolutePath();
			if(UISettings.autoLoad){
				if(nes!=null)
					nes.exit();
				createAndStart(rom);
			}
		}
	}
	private void startCPU(){
		if(!rom.equals(null)){
			if(nes!=null)
				nes.exit();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			createAndStart(rom);
		}
	}
	private void createAndStart(File rom){
		nes = NESBuilder.buildNes(this);
		try {
			nes.loadRom(rom);
			current = new Thread(nes);
			current.start();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnSupportedMapperException e) {
			java.lang.System.err.println("Failed to load rom.");
			java.lang.System.err.println("Unsupported Mapper id: "+e.mapperid);
		} catch (Exception e){
			java.lang.System.err.println("Unexpected error has occured please report this bug.");
			e.printStackTrace();
		}
	}







}
