package ui;


import javax.swing.*;

import core.NesSettings;
import ui.input.HotKeyInterface;
import ui.settings.UISettings;
import ui.settings.UISettings.VideoFilter;

import java.awt.event.ActionListener;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.awt.Font;

@SuppressWarnings("serial")
public class MainUI extends JFrame {

	MainUICallback sys;
	public MainUI(MainUICallback s) {
		
		setTitle("BassNES");
		sys = s;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 256, 240);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e2) {
			e2.printStackTrace();
		}
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		menuBar.setPreferredSize(new Dimension(240,25));
		
		JMenu mnSystem = new JMenu("System");
		mnSystem.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		menuBar.add(mnSystem);

		updateActionAndInputMaps();
		

		ActionMap actionMap = this.rootPane.getActionMap();
		
		JMenuItem mntmLoadRom = new JMenuItem("Load Rom");
		mntmLoadRom.addActionListener(actionMap.get(HotKeyInterface.HotKeys.LoadRom));
		mnSystem.add(mntmLoadRom);
		
		JCheckBoxMenuItem chckbxmntmAutoload = new JCheckBoxMenuItem("AutoLoad");
		chckbxmntmAutoload.addActionListener(actionMap.get(HotKeyInterface.HotKeys.AutoLoad));
		chckbxmntmAutoload.setSelected(UISettings.autoLoad);
		mnSystem.add(chckbxmntmAutoload);
		
		JCheckBoxMenuItem chckbxmntmShowFps = new JCheckBoxMenuItem("Show FPS");
		chckbxmntmShowFps.addActionListener(actionMap.get(HotKeyInterface.HotKeys.ShowFPS));
		chckbxmntmShowFps.setSelected(UISettings.ShowFPS);
		mnSystem.add(chckbxmntmShowFps);
		
		JMenu mnSaveState = new JMenu("Save State");
		mnSystem.add(mnSaveState);
		
		JMenuItem mntmState_4 = new JMenuItem("State 1");
		mnSaveState.add(mntmState_4);
		mntmState_4.addActionListener(actionMap.get(HotKeyInterface.HotKeys.SaveState1));
		
		JMenuItem mntmState_5 = new JMenuItem("State 2");
		mnSaveState.add(mntmState_5);
		mntmState_5.addActionListener(actionMap.get(HotKeyInterface.HotKeys.SaveState2));
		JMenuItem mntmState_6 = new JMenuItem("State 3");
		mnSaveState.add(mntmState_6);
		mntmState_6.addActionListener(actionMap.get(HotKeyInterface.HotKeys.SaveState3));
		JMenuItem mntmState_7 = new JMenuItem("State 4");
		mnSaveState.add(mntmState_7);
		mntmState_7.addActionListener(actionMap.get(HotKeyInterface.HotKeys.SaveState4));
		JMenu mnLoadState = new JMenu("Load State");
		mnSystem.add(mnLoadState);
		
		JMenuItem mntmState = new JMenuItem("State 1");
		mnLoadState.add(mntmState);
		mntmState.addActionListener(actionMap.get(HotKeyInterface.HotKeys.LoadState1));
		JMenuItem mntmState_1 = new JMenuItem("State 2");
		mnLoadState.add(mntmState_1);
		mntmState_1.addActionListener(actionMap.get(HotKeyInterface.HotKeys.LoadState2));
		JMenuItem mntmState_2 = new JMenuItem("State 3");
		mnLoadState.add(mntmState_2);
		mntmState_2.addActionListener(actionMap.get(HotKeyInterface.HotKeys.LoadState3));

		JMenuItem mntmState_3 = new JMenuItem("State 4");
		mnLoadState.add(mntmState_3);
		mntmState_3.addActionListener(actionMap.get(HotKeyInterface.HotKeys.LoadState4));

		JMenu mnCpu = new JMenu("CPU");
		mnCpu.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		menuBar.add(mnCpu);
		
		JMenuItem mntmStartCpu = new JMenuItem("Start/Reset CPU");
		mntmStartCpu.addActionListener(actionMap.get(HotKeyInterface.HotKeys.StartCPU));
		mnCpu.add(mntmStartCpu);
		
		JMenuItem mntmPauseCpu = new JMenuItem("Pause CPU");
		mntmPauseCpu.addActionListener(actionMap.get(HotKeyInterface.HotKeys.PauseCPU));
		mnCpu.add(mntmPauseCpu);
		
		JMenu mnNewMenu = new JMenu("Audio");
		mnNewMenu.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		menuBar.add(mnNewMenu);
		
		JCheckBoxMenuItem chckbxmntmEnableAudio = new JCheckBoxMenuItem("Enable Audio");
		UISettings.AudioEnabled=true;
		chckbxmntmEnableAudio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					UISettings.AudioEnabled=!UISettings.AudioEnabled;
			}
		});
		chckbxmntmEnableAudio.setSelected(UISettings.AudioEnabled);
		mnNewMenu.add(chckbxmntmEnableAudio);
		
		JMenuItem mntmAudioMixer = new JMenuItem("Audio Settings");
		mntmAudioMixer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				sys.changeWindowVisibility(SystemManager.UIWindows.AudioSettings,true);
			}
		});
		mnNewMenu.add(mntmAudioMixer);
		
		JMenuItem mntmShowOscillascope = new JMenuItem("Show Visualizer");
		mntmShowOscillascope.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				sys.changeWindowVisibility(SystemManager.UIWindows.Scope,true);
			}
		});
		mnNewMenu.add(mntmShowOscillascope);
		
		JMenu mnGraphics = new JMenu("Graphics");
		mnGraphics.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		menuBar.add(mnGraphics);
		
		JMenu mnScaling = new JMenu("Scaling");
		mnGraphics.add(mnScaling);
		
		JRadioButtonMenuItem rdbtnmntmxScaling = new JRadioButtonMenuItem("1x Scaling");
		rdbtnmntmxScaling.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sys.updateNesDisplay(1);
				getContentPane().setPreferredSize(new Dimension(256,240));
				pack();
			}
		});
		ButtonGroup videoSizeGroup = new ButtonGroup();
		videoSizeGroup.add(rdbtnmntmxScaling);
		mnScaling.add(rdbtnmntmxScaling);
		
		JRadioButtonMenuItem rdbtnmntmxScaling_1 = new JRadioButtonMenuItem("2x Scaling");
		rdbtnmntmxScaling_1.setSelected(true);
		rdbtnmntmxScaling_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sys.updateNesDisplay(2);
				getContentPane().setPreferredSize(new Dimension(256*2,240*2));
				pack();
			}
		});
		mnScaling.add(rdbtnmntmxScaling_1);
		videoSizeGroup.add(rdbtnmntmxScaling_1);
		JRadioButtonMenuItem rdbtnmntmxScaling_2 = new JRadioButtonMenuItem("3x Scaling");
		rdbtnmntmxScaling_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sys.updateNesDisplay(3);
				getContentPane().setPreferredSize(new Dimension(256*3,240*3));
				pack();
			}
		});
		mnScaling.add(rdbtnmntmxScaling_2);
		videoSizeGroup.add(rdbtnmntmxScaling_2);
		JRadioButtonMenuItem rdbtnmntmxScaling_3 = new JRadioButtonMenuItem("4x Scaling");
		rdbtnmntmxScaling_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sys.updateNesDisplay(4);
				getContentPane().setPreferredSize(new Dimension(256*4,240*4));
				pack();
			}
		});
		mnScaling.add(rdbtnmntmxScaling_3);
		videoSizeGroup.add(rdbtnmntmxScaling_3);

		JMenuItem mntmMoreSettings = new JMenuItem("More Settings");
		mntmMoreSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sys.changeWindowVisibility(SystemManager.UIWindows.GraphicsSettings,true);
			}
		});
		
		JMenu mnVideoFilter = new JMenu("Video Filter");
		mnGraphics.add(mnVideoFilter);
		ButtonGroup videoFilterGroup = new ButtonGroup();
		JCheckBoxMenuItem chckbxmntmNone = new JCheckBoxMenuItem("None");
		chckbxmntmNone.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				resetImage();
				UISettings.currentFilter = VideoFilter.None;
				
			}
		});
		chckbxmntmNone.setSelected(true);
		mnVideoFilter.add(chckbxmntmNone);
		videoFilterGroup.add(chckbxmntmNone);
		
		JCheckBoxMenuItem chckbxmntmNtsc = new JCheckBoxMenuItem("NTSC");
		chckbxmntmNtsc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resetImage();
				sys.updateNesImage(602,240);
				NesSettings.RenderMethod = 3;
				UISettings.currentFilter = VideoFilter.NTSC;
			}
		});
		mnVideoFilter.add(chckbxmntmNtsc);
		mnGraphics.add(mntmMoreSettings);
		videoFilterGroup.add(chckbxmntmNtsc);
		switch(UISettings.currentFilter){
		case NTSC:
			chckbxmntmNtsc.setSelected(true);break;
		default: 
			chckbxmntmNone.setSelected(true);break;
		}
		JCheckBoxMenuItem chckbxmntmScanlines = new JCheckBoxMenuItem("Scanlines");
		chckbxmntmScanlines.setSelected(UISettings.scanlinesEnabled);
		chckbxmntmScanlines.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UISettings.scanlinesEnabled = !UISettings.scanlinesEnabled;
			}
		});
		
		JSeparator separator = new JSeparator();
		mnVideoFilter.add(separator);
		mnVideoFilter.add(chckbxmntmScanlines);
		//videoFilterGroup.add(chckbxmntmScanlines);
		JMenu mnControl = new JMenu("Control");
		mnControl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		menuBar.add(mnControl);
		
		JMenuItem mntmConfigure = new JMenuItem("Configure");
		mntmConfigure.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sys.changeWindowVisibility(SystemManager.UIWindows.KeyConfig,true);
			}
		});
		mnControl.add(mntmConfigure);
		
		JMenu mnDebug = new JMenu("Debug");
		mnDebug.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		menuBar.add(mnDebug);
		
		JCheckBoxMenuItem chckbxmntmShowDebug = new JCheckBoxMenuItem("Show Debug");
		chckbxmntmShowDebug.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sys.changeWindowVisibility(SystemManager.UIWindows.Debug,true);
			}
		});
		mnDebug.add(chckbxmntmShowDebug);
		
		JMenuItem mntmDebugInfo = new JMenuItem("Debugger");
		mntmDebugInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sys.startdebugging();
			}
		});
		mnDebug.add(mntmDebugInfo);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmReportABug = new JMenuItem("Report a Bug");
		mntmReportABug.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
					try {
						openWebpage(new URL("https://github.com/QuantumSoundings/BassNES/issues/new"));
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				
			}
		});
		mnHelp.add(mntmReportABug);
		
		JSeparator separator_1 = new JSeparator();
		mnHelp.add(separator_1);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sys.changeWindowVisibility(SystemManager.UIWindows.About,true);
			}
		});
		mnHelp.add(mntmAbout);
		this.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent evt){
				System.out.println("Closing......");
				sys.exit();
			}
		});
	}
	private void resetImage(){
		sys.updateNesImage(256,240);
		NesSettings.RenderMethod = 2;
	}
	private void openWebpage(URI uri) {
	    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
	    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
	        try {
	            desktop.browse(uri);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	}

	private void openWebpage(URL url) {
	    try {
	        openWebpage(url.toURI());
	    } catch (URISyntaxException e) {
	        e.printStackTrace();
	    }
	}
	private void updateActionAndInputMaps(){
		InputMap inputMap = sys.getHotKeyInput();
		ActionMap actionMap = sys.getHotKeyAction();

		InputMap thismap = this.rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		this.rootPane.setActionMap(actionMap);

		for(KeyStroke key: inputMap.keys()){
			thismap.put(key,inputMap.get(key));
		}

	}

}
