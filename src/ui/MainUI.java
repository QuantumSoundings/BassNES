package ui;


import javax.swing.JFrame;

import com.NES;


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

import java.awt.event.ActionListener;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Font;
@SuppressWarnings("serial")
public class MainUI extends JFrame {

	SystemUI sys;
	public MainUI(SystemUI s) {
		
		setTitle("Nes Emulator");
		sys = s;
        //noinspection MagicConstant
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 256, 240);
		
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
						sys.nes.flag=false;
					try {
						Thread.sleep(500);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					sys.nes = new NES(sys.rom,sys);
					sys.current = new Thread(sys.nes);
					sys.current.start();
				}
			}
		};
		Action loadRom = new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				int returnval = sys.fc.showOpenDialog(sys.mainWindow);
				if(returnval == JFileChooser.APPROVE_OPTION){
					sys.rom = sys.fc.getSelectedFile();
					if(UserSettings.autoLoad){
						if(sys.nes!=null)
							sys.nes.flag=false;
						sys.nes = new NES(sys.rom,sys);
						sys.current = new Thread(sys.nes);
						sys.current.start();
					}
				}
			}
		};
		Action autoLoad = new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				UserSettings.autoLoad = !UserSettings.autoLoad;
			}
		};
		Action showFPS = new AbstractAction(){
			public void actionPerformed(ActionEvent e) {
				UserSettings.ShowFPS = !UserSettings.ShowFPS;
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
		
		
		this.rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_1,
				java.awt.event.InputEvent.CTRL_DOWN_MASK),
			"saveState1");
		this.rootPane.getActionMap().put("saveState1", saveState1);
		this.rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_1, 
				java.awt.event.InputEvent.SHIFT_DOWN_MASK), "loadState1");
		this.rootPane.getActionMap().put("loadState1", loadState1);
		this.rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_2,
				java.awt.event.InputEvent.CTRL_DOWN_MASK),
			"saveState2");
		this.rootPane.getActionMap().put("saveState2", saveState2);
		this.rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_2, 
				java.awt.event.InputEvent.SHIFT_DOWN_MASK), "loadState2");
		this.rootPane.getActionMap().put("loadState2", loadState2);
		this.rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_3,
				java.awt.event.InputEvent.CTRL_DOWN_MASK),
			"saveState3");
		this.rootPane.getActionMap().put("saveState3", saveState3);
		this.rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_3, 
				java.awt.event.InputEvent.SHIFT_DOWN_MASK), "loadState3");
		this.rootPane.getActionMap().put("loadState3", loadState3);
		this.rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_4,
				java.awt.event.InputEvent.CTRL_DOWN_MASK),
			"saveState4");
		this.rootPane.getActionMap().put("saveState4", saveState4);
		this.rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_4, 
				java.awt.event.InputEvent.SHIFT_DOWN_MASK), "loadState4");
		this.rootPane.getActionMap().put("loadState4", loadState4);
		
		this.rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				java.awt.event.InputEvent.CTRL_DOWN_MASK),
			"startCPU");
		this.rootPane.getActionMap().put("startCPU", startCPU);
		
		
		
		
		
		
		
		
		
		
		
		
		JMenuItem mntmLoadRom = new JMenuItem("Load Rom");
		mntmLoadRom.addActionListener(loadRom);
		mnSystem.add(mntmLoadRom);
		
		JCheckBoxMenuItem chckbxmntmAutoload = new JCheckBoxMenuItem("AutoLoad");
		chckbxmntmAutoload.addActionListener(autoLoad);
		chckbxmntmAutoload.setSelected(UserSettings.autoLoad);
		mnSystem.add(chckbxmntmAutoload);
		
		JCheckBoxMenuItem chckbxmntmShowFps = new JCheckBoxMenuItem("Show FPS");
		chckbxmntmShowFps.addActionListener(showFPS);
		chckbxmntmShowFps.setSelected(UserSettings.ShowFPS);
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
					sys.nes.pause= !sys.nes.pause;
			}
		});
		mnCpu.add(mntmPauseCpu);
		
		JMenu mnNewMenu = new JMenu("Audio");
		mnNewMenu.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		menuBar.add(mnNewMenu);
		
		JCheckBoxMenuItem chckbxmntmEnableAudio = new JCheckBoxMenuItem("Enable Audio");
		UserSettings.AudioEnabled=true;
		chckbxmntmEnableAudio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					UserSettings.AudioEnabled=!UserSettings.AudioEnabled;
			}
		});
		chckbxmntmEnableAudio.setSelected(UserSettings.AudioEnabled);
		mnNewMenu.add(chckbxmntmEnableAudio);
		
		JMenuItem mntmAudioMixer = new JMenuItem("Audio Mixer");
		mntmAudioMixer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				sys.audiomixerWindow.setVisible(true);
			}
		});
		mnNewMenu.add(mntmAudioMixer);
		
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
		ButtonGroup group = new ButtonGroup();
		group.add(rdbtnmntmxScaling);
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
		group.add(rdbtnmntmxScaling_1);
		JRadioButtonMenuItem rdbtnmntmxScaling_2 = new JRadioButtonMenuItem("3x Scaling");
		rdbtnmntmxScaling_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sys.display.updateScaling(3);
				sys.mainWindow.getContentPane().setPreferredSize(new Dimension(256*3,240*3));
				sys.mainWindow.pack();
			}
		});
		mnScaling.add(rdbtnmntmxScaling_2);
		group.add(rdbtnmntmxScaling_2);
		JRadioButtonMenuItem rdbtnmntmxScaling_3 = new JRadioButtonMenuItem("4x Scaling");
		rdbtnmntmxScaling_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sys.display.updateScaling(4);
				sys.mainWindow.getContentPane().setPreferredSize(new Dimension(256*4,240*4));
				sys.mainWindow.pack();
			}
		});
		mnScaling.add(rdbtnmntmxScaling_3);
		group.add(rdbtnmntmxScaling_3);
		
		JMenuItem mntmMoreSettings = new JMenuItem("More Settings");
		mntmMoreSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sys.advancedGraphicsWindow.setVisible(true);
			}
		});
		
		mnGraphics.add(mntmMoreSettings);
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
				sys.debugWindow.setVisible(!sys.debugWindow.isVisible());
			}
		});
		mnDebug.add(chckbxmntmShowDebug);
		//contentPane = new JPanel();
		//contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		//contentPane.setLayout(new BorderLayout(0, 0));
		//setContentPane(contentPane);
		this.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent evt){
				if(sys.nes!=null)
					sys.nes.flag=false;
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				UserSettings.saveSettings();
				System.exit(0);
			}
		});
	}

}
