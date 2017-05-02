package ui;


import javax.swing.JFrame;

import com.NES;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JRadioButtonMenuItem;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class MainUI extends JFrame {

	SystemUI sys;
	public MainUI(SystemUI s) {
		setTitle("Nes Emulator");
		sys = s;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 256, 240);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnSystem = new JMenu("System");
		menuBar.add(mnSystem);
		
		JMenuItem mntmLoadRom = new JMenuItem("Load Rom");
		mntmLoadRom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int returnval = sys.fc.showOpenDialog(sys.mainWindow);
				if(returnval == JFileChooser.APPROVE_OPTION){
					sys.rom = sys.fc.getSelectedFile();
					if(sys.autoload){
						if(sys.nes!=null)
							sys.nes.flag=false;
						sys.nes = new NES(sys.display,sys.mainWindow,sys.rom);
						sys.current = new Thread(sys.nes);
						sys.current.start();
					}
				}
			}
		});
		mnSystem.add(mntmLoadRom);
		
		JCheckBoxMenuItem chckbxmntmAutoload = new JCheckBoxMenuItem("AutoLoad");
		chckbxmntmAutoload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sys.autoload = !sys.autoload;
			}
		});
		chckbxmntmAutoload.setSelected(true);
		mnSystem.add(chckbxmntmAutoload);
		
		JCheckBoxMenuItem chckbxmntmShowFps = new JCheckBoxMenuItem("Show FPS");
		chckbxmntmShowFps.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sys.showFPS = !sys.showFPS;
			}
		});
		chckbxmntmShowFps.setSelected(true);
		mnSystem.add(chckbxmntmShowFps);
		
		JMenuItem mntmSaveState = new JMenuItem("Save State");
		mntmSaveState.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					sys.saveState();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		mnSystem.add(mntmSaveState);
		
		JMenuItem mntmLoadState = new JMenuItem("Load State");
		mntmLoadState.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					sys.restoreState();
				} catch (ClassNotFoundException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		mnSystem.add(mntmLoadState);
		
		JMenu mnCpu = new JMenu("CPU");
		menuBar.add(mnCpu);
		
		JMenuItem mntmStartCpu = new JMenuItem("Start CPU");
		mntmStartCpu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!sys.rom.equals(null)){
					sys.begin = true;
					if(sys.nes!=null)
						sys.nes.flag=false;
					try {
						Thread.sleep(500);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					sys.nes = new NES(sys.display,sys.mainWindow,sys.rom);
					sys.current = new Thread(sys.nes);
					sys.current.start();
					//System.out.println(begin);
				}
				else
					sys.begin = false;	
			}
		});
		mnCpu.add(mntmStartCpu);
		
		JMenuItem mntmPauseCpu = new JMenuItem("Pause CPU");
		mntmPauseCpu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(sys.nes!=null)
					sys.nes.pause= !sys.nes.pause;
			}
		});
		mnCpu.add(mntmPauseCpu);
		
		JMenuItem mntmReset = new JMenuItem("Reset (WIP)");
		mnCpu.add(mntmReset);
		
		JMenuItem mntmHardReset = new JMenuItem("Reboot (WIP)");
		mnCpu.add(mntmHardReset);
		
		JMenu mnNewMenu = new JMenu("Audio");
		menuBar.add(mnNewMenu);
		
		JCheckBoxMenuItem chckbxmntmEnableAudio = new JCheckBoxMenuItem("Enable Audio");
		UserSettings.AudioEnabled=true;
		chckbxmntmEnableAudio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					UserSettings.AudioEnabled=!UserSettings.AudioEnabled;
			}
		});
		chckbxmntmEnableAudio.setSelected(true);
		mnNewMenu.add(chckbxmntmEnableAudio);
		
		JMenuItem mntmAudioMixer = new JMenuItem("Audio Mixer");
		mntmAudioMixer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				sys.audiomixerWindow.setVisible(true);
			}
		});
		mnNewMenu.add(mntmAudioMixer);
		
		JMenu mnGraphics = new JMenu("Graphics");
		menuBar.add(mnGraphics);
		
		JMenu mnScaling = new JMenu("Scaling");
		mnGraphics.add(mnScaling);
		
		JRadioButtonMenuItem rdbtnmntmxScaling = new JRadioButtonMenuItem("1x Scaling");
		rdbtnmntmxScaling.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sys.display.updateScaling(1);
				sys.mainWindow.setBounds(100, 100, 256+15, 240+60);
			}
		});
		ButtonGroup group = new ButtonGroup();
		group.add(rdbtnmntmxScaling);
		rdbtnmntmxScaling.setSelected(true);
		mnScaling.add(rdbtnmntmxScaling);
		
		JRadioButtonMenuItem rdbtnmntmxScaling_1 = new JRadioButtonMenuItem("2x Scaling");
		rdbtnmntmxScaling_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sys.display.updateScaling(2);
				sys.mainWindow.setBounds(100, 100, 256*2+15, 240*2+60);
			}
		});
		mnScaling.add(rdbtnmntmxScaling_1);
		group.add(rdbtnmntmxScaling_1);
		JRadioButtonMenuItem rdbtnmntmxScaling_2 = new JRadioButtonMenuItem("3x Scaling");
		rdbtnmntmxScaling_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sys.display.updateScaling(3);
				sys.mainWindow.setBounds(100, 100, 256*3+15, 240*3+60);
			}
		});
		mnScaling.add(rdbtnmntmxScaling_2);
		group.add(rdbtnmntmxScaling_2);

		JRadioButtonMenuItem rdbtnmntmxScaling_3 = new JRadioButtonMenuItem("4x Scaling");
		rdbtnmntmxScaling_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sys.display.updateScaling(4);
				sys.mainWindow.setBounds(100, 100, 256*4+15, 240*4+60);
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
		menuBar.add(mnControl);
		
		JMenuItem mntmConfigure = new JMenuItem("Configure");
		mntmConfigure.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sys.keyconfigWindow.setVisible(true);
			}
		});
		mnControl.add(mntmConfigure);
		
		JMenu mnDebug = new JMenu("Debug");
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
	}

}
