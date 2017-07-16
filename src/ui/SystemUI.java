package ui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.util.EventListener;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

import core.NES;
import core.NESCallback;
import core.NesSettings;
import core.exceptions.UnSupportedMapperException;
import net.java.games.input.Keyboard;
import ui.debugger.BreakPoint;
import ui.debugger.Debugger;
interface UpdateEventListener extends EventListener{
	public void doVideoFrame();
	public void doAudioFrame();
}
public class SystemUI implements NESCallback {
	public NES nes;
	final JFileChooser fc;
	public JFrame mainWindow,debugWindow,keyconfigWindow,audiomixerWindow,advancedGraphicsWindow,aboutWindow;
	public InputManager input;
	Debugger debugInfo;
	File rom,configuration;
	NesDisplay display;
	//private KeyChecker keys;
	public UpdateEventListener listener;
	private int[] pixels;
	private int[] audiobuffer;
	Thread current;
	Thread render;
	Properties prop;
	String testoutput;
	private AudioInterface audio;
	Keyboard key;
	
	public SystemUI(){
		configuration = new File("config.properties");
		input = new InputManager(this);
		try {
			NesSettings.loadSettings(configuration);
			UISettings.loadSettings(configuration);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter("Nes Files","nes","nsf","NES","NSF"));
		audio = new AudioInterface(this);
		debugInfo = new Debugger(this);
		rom = new File("gimmickj.nes");
		mainWindow = new MainUI(this);
		//debugWindow = new DebugUI();
		keyconfigWindow = new ControlUI(prop,this);
		audiomixerWindow = new AudioSettingsUI(this);
		advancedGraphicsWindow = new AdvancedGraphics(this);
		//keys = new KeyChecker();
		display = new NesDisplay();
		display.setSize(256, 240);
		display.updateScaling(2);		
		listener = new UpdateEventListener(){
            public void doVideoFrame() {
                display.sendFrame(pixels);
            }
            public void doAudioFrame(){
            	audio.setAudioFrame(audiobuffer);
            }
       };
       setupMainWindow();    
       //Tester test = new Tester(this);
       //test.runTests();
       //test.testRomSet();
		start();
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
	
	public void createNES(File rom){
		nes = new NES(this);
		nes.setCallback(this);
			try {
				nes.loadRom(rom);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (UnSupportedMapperException e) {
				e.printStackTrace();
			}
		resetaudio();
	}
	
	public void createAndStart(File rom){
		nes = new NES(this);
		try {
			nes.loadRom(rom);
			current = new Thread(nes);
			current.start();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnSupportedMapperException e) {
			System.err.println("Failed to load rom.");
			System.err.println("Unsupported Mapper id: "+e.mapperid);
		} catch (Exception e){
			System.err.println("Unexpected error has occured please report this bug.");
			e.printStackTrace();
		}
	}
	
	public void saveState(int slot){
		nes.pause();
		try {
			nes.saveState("savestateY.txt".replaceAll("Y", slot+""));
		} catch (IOException e) {
			e.printStackTrace();
		}
		nes.unpause();
	}
	public void restoreState(int slot){
		nes.pause();
		try {
			nes.restoreState("savestateY.txt".replaceAll("Y", slot+""));
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		nes.unpause();
	}
	public boolean[][] pollController(){
		boolean[][] out = new boolean[2][8];
		if(mainWindow.hasFocus()||UISettings.controlwhilenotfocused){
	        return InputManager.currentFrameInputs;
		}
		return out;	
	}
	public void videoCallback(int[] p){
		pixels=p;
		EventQueue.invokeLater(new Runnable() {
			@Override
	        public void run(){
				listener.doVideoFrame();
				input.updateInputs();
	        }
	    });
	}
	public void audioFrameCallback(int[] audioInts){
		audiobuffer = audioInts;
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run(){
				listener.doAudioFrame();
			}
		});
	}
	public void audioSampleCallback(int audiosample){
		audio.outputSample(audiosample);
	}
	public void unmixedAudioSampleCallback(int[] audiosample){
		
	}
	public void resetaudio(){
		while(audio.inaudio);
		audio.lock=true;
		//nes.setSampleRate(NesSettings.sampleRate);
		audio.restartSDL();
		audio.lock=false;	
	}
	public Object[][] AudioChannelInfoCallback(){
		return nes.getAudioChannelInfo();
	}
	public void showscope(){
		audio.showscope();
	}
	public void enterDebug(){
		debugMode = true;
		debugInfo.setVisible(true);
		nes.pause();
		BreakPoint.setsystem(this);
		debugInfo.updateInfo();
	}
	public void exitDebug(){
		debugMode = false;
		nes.unpause();
	}
	public void seekBreakpoint(){
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
}
