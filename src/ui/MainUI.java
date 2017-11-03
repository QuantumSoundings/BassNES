package ui;


import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import core.NesSettings;
import ui.settings.UISettings;
import ui.settings.UISettings.VideoFilter;

import java.awt.event.ActionListener;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.awt.Font;
import javax.swing.JSeparator;
@SuppressWarnings("serial")
public class MainUI extends JFrame {

	SystemUI sys;
	public MainUI(SystemUI s) {
		
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
		
		Action startCPU = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if(!sys.rom.equals(null)){
					if(sys.nes!=null)
						sys.nes.exit();
					try {
						Thread.sleep(500);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					sys.createAndStart(sys.rom);
				}
			}
		};
		Action loadRom = new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				sys.fc.setCurrentDirectory(new File(UISettings.lastLoadedDir));
				int returnval = sys.fc.showOpenDialog(sys.mainWindow);
				if(returnval == JFileChooser.APPROVE_OPTION){
					sys.rom = sys.fc.getSelectedFile();
					UISettings.lastLoadedDir = sys.fc.getCurrentDirectory().getAbsolutePath();
					if(UISettings.autoLoad){
						if(sys.nes!=null)
							sys.nes.exit();
						sys.createAndStart(sys.rom);
					}
				}
				
			}
		};
		Action autoLoad = new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				UISettings.autoLoad = !UISettings.autoLoad;
			}
		};
		Action showFPS = new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				UISettings.ShowFPS = !UISettings.ShowFPS;
			}
		};
		Action saveState1= new AbstractAction(){
			public void actionPerformed(ActionEvent arg0) {
				sys.saveState(1);
			}
		};
		Action saveState2= new AbstractAction(){
			public void actionPerformed(ActionEvent arg0) {
				sys.saveState(2);
			}
		};
		Action saveState3= new AbstractAction(){
			public void actionPerformed(ActionEvent arg0) {
				sys.saveState(3);
			}
		};
		Action saveState4= new AbstractAction(){
			public void actionPerformed(ActionEvent arg0) {
				sys.saveState(4);
			}
		};
		Action loadState1= new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				sys.restoreState(1);
			}
		};
		Action loadState2= new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				sys.restoreState(2);
			}
		};
		Action loadState3= new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				sys.restoreState(3);
			}
		};
		Action loadState4= new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				sys.restoreState(4);
			}
		};
		Action volumeUp = new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				NesSettings.masterMixLevel = Math.min(100, NesSettings.masterMixLevel+5);
				OSD.addOSDMessage("Master volume: "+NesSettings.masterMixLevel+"%", 120);
			}
		};
		Action volumeDown = new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				NesSettings.masterMixLevel = Math.max(0, NesSettings.masterMixLevel-5);
				OSD.addOSDMessage("Master volume: "+NesSettings.masterMixLevel+"%", 120);
			}
		};


		//this.rootPane.setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW,sys.hotkeys.getInputMap());
		//this.rootPane.setActionMap(sys.hotkeys.getActionMap());
		
		
		
		
		
		
		
		
		
		
		
		JMenuItem mntmLoadRom = new JMenuItem("Load Rom");
		mntmLoadRom.addActionListener(loadRom);
		mnSystem.add(mntmLoadRom);
		
		JCheckBoxMenuItem chckbxmntmAutoload = new JCheckBoxMenuItem("AutoLoad");
		chckbxmntmAutoload.addActionListener(autoLoad);
		chckbxmntmAutoload.setSelected(UISettings.autoLoad);
		mnSystem.add(chckbxmntmAutoload);
		
		JCheckBoxMenuItem chckbxmntmShowFps = new JCheckBoxMenuItem("Show FPS");
		chckbxmntmShowFps.addActionListener(showFPS);
		chckbxmntmShowFps.setSelected(UISettings.ShowFPS);
		mnSystem.add(chckbxmntmShowFps);
		
		JMenu mnSaveState = new JMenu("Save State");
		mnSystem.add(mnSaveState);
		
		JMenuItem mntmState_4 = new JMenuItem("State 1");
		mnSaveState.add(mntmState_4);
		mntmState_4.addActionListener(saveState1);
		
		JMenuItem mntmState_5 = new JMenuItem("State 2");
		mnSaveState.add(mntmState_5);
		mntmState_5.addActionListener(saveState2);
		JMenuItem mntmState_6 = new JMenuItem("State 3");
		mnSaveState.add(mntmState_6);
		mntmState_6.addActionListener(saveState3);
		JMenuItem mntmState_7 = new JMenuItem("State 4");
		mnSaveState.add(mntmState_7);
		mntmState_7.addActionListener(saveState4);
		JMenu mnLoadState = new JMenu("Load State");
		mnSystem.add(mnLoadState);
		
		JMenuItem mntmState = new JMenuItem("State 1");
		mnLoadState.add(mntmState);
		mntmState.addActionListener(loadState1);
		JMenuItem mntmState_1 = new JMenuItem("State 2");
		mnLoadState.add(mntmState_1);
		mntmState_1.addActionListener(loadState2);
		JMenuItem mntmState_2 = new JMenuItem("State 3");
		mnLoadState.add(mntmState_2);
		mntmState_2.addActionListener(loadState3);

		JMenuItem mntmState_3 = new JMenuItem("State 4");
		mnLoadState.add(mntmState_3);
		mntmState_3.addActionListener(loadState4);

		JMenu mnCpu = new JMenu("CPU");
		mnCpu.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		menuBar.add(mnCpu);
		
		JMenuItem mntmStartCpu = new JMenuItem("Start/Reset CPU");
		mntmStartCpu.addActionListener(startCPU);
		mnCpu.add(mntmStartCpu);
		
		JMenuItem mntmPauseCpu = new JMenuItem("Pause CPU");
		mntmPauseCpu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(sys.nes!=null)
					sys.nes.togglePause();
			}
		});
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
				sys.audiomixerWindow.setVisible(true);
			}
		});
		mnNewMenu.add(mntmAudioMixer);
		
		JMenuItem mntmShowOscillascope = new JMenuItem("Show Visualizer");
		mntmShowOscillascope.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				sys.showscope();
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
				sys.display.updateScaling(1);
				sys.mainWindow.getContentPane().setPreferredSize(new Dimension(256,240));
				sys.mainWindow.pack();
			}
		});
		ButtonGroup videoSizeGroup = new ButtonGroup();
		videoSizeGroup.add(rdbtnmntmxScaling);
		mnScaling.add(rdbtnmntmxScaling);
		
		JRadioButtonMenuItem rdbtnmntmxScaling_1 = new JRadioButtonMenuItem("2x Scaling");
		rdbtnmntmxScaling_1.setSelected(true);
		rdbtnmntmxScaling_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sys.display.updateScaling(2);
				sys.mainWindow.getContentPane().setPreferredSize(new Dimension(256*2,240*2));
				sys.mainWindow.pack();
			}
		});
		mnScaling.add(rdbtnmntmxScaling_1);
		videoSizeGroup.add(rdbtnmntmxScaling_1);
		JRadioButtonMenuItem rdbtnmntmxScaling_2 = new JRadioButtonMenuItem("3x Scaling");
		rdbtnmntmxScaling_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sys.display.updateScaling(3);
				sys.mainWindow.getContentPane().setPreferredSize(new Dimension(256*3,240*3));
				sys.mainWindow.pack();
			}
		});
		mnScaling.add(rdbtnmntmxScaling_2);
		videoSizeGroup.add(rdbtnmntmxScaling_2);
		JRadioButtonMenuItem rdbtnmntmxScaling_3 = new JRadioButtonMenuItem("4x Scaling");
		rdbtnmntmxScaling_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sys.display.updateScaling(4);
				sys.mainWindow.getContentPane().setPreferredSize(new Dimension(256*4,240*4));
				sys.mainWindow.pack();
			}
		});
		mnScaling.add(rdbtnmntmxScaling_3);
		videoSizeGroup.add(rdbtnmntmxScaling_3);

		JMenuItem mntmMoreSettings = new JMenuItem("More Settings");
		mntmMoreSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sys.advancedGraphicsWindow.setVisible(true);
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
				sys.display.updateImage(602, 240);
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
				sys.keyconfigWindow.setVisible(true);
			}
		});
		mnControl.add(mntmConfigure);
		
		JMenu mnDebug = new JMenu("Debug");
		mnDebug.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		menuBar.add(mnDebug);
		
		JCheckBoxMenuItem chckbxmntmShowDebug = new JCheckBoxMenuItem("Show Debug");
		chckbxmntmShowDebug.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(sys.debugWindow==null)
					sys.debugWindow= new DebugUI();
				sys.debugWindow.setVisible(!sys.debugWindow.isVisible());
			}
		});
		mnDebug.add(chckbxmntmShowDebug);
		
		JMenuItem mntmDebugInfo = new JMenuItem("Debugger");
		mntmDebugInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sys.enterDebug();
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
				if(sys.aboutWindow==null)
					sys.aboutWindow= new About();
				sys.aboutWindow.setVisible(!sys.aboutWindow.isVisible());
			}
		});
		mnHelp.add(mntmAbout);
		this.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent evt){
				if(sys.nes!=null)
					sys.nes.exit();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				NesSettings.saveSettings(sys.configuration);
				sys.configurator.saveSettings(sys.config);
				System.exit(0);
			}
		});
	}
	private void resetImage(){
		sys.display.updateImage(256, 240);
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

}
