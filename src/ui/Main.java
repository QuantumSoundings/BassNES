package ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.APU;
import com.NES;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JRadioButtonMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Main extends JFrame {

	private JPanel contentPane;
	SystemUI sys;
	public Main(SystemUI s) {
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
				int returnval = sys.fc.showOpenDialog(sys.frame);
				if(returnval == JFileChooser.APPROVE_OPTION){
					sys.rom = sys.fc.getSelectedFile();
					if(sys.autoload){
						if(sys.nes!=null)
							sys.nes.flag=false;
						sys.nes = new NES(sys.display,sys.frame,sys.rom,sys.prop);
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
		
		JMenu mnCpu = new JMenu("CPU");
		menuBar.add(mnCpu);
		
		JMenuItem mntmStartCpu = new JMenuItem("Start CPU");
		mntmStartCpu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!sys.rom.equals(null)){
					sys.begin = true;
					if(sys.nes!=null)
						sys.nes.flag=false;
					sys.nes = new NES(sys.display,sys.frame,sys.rom,sys.prop);
					sys.current = new Thread(sys.nes);
					sys.current.start();
					//System.out.println(begin);
				}
				else
					sys.begin = false;	
			}
		});
		mnCpu.add(mntmStartCpu);
		
		JMenu mnNewMenu = new JMenu("Audio");
		menuBar.add(mnNewMenu);
		
		JCheckBoxMenuItem chckbxmntmEnableAudio = new JCheckBoxMenuItem("Enable Audio");
		chckbxmntmEnableAudio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(sys.nes.doaudio){
					sys.nes.doaudio = false;
					sys.nes.apu.synth.stop();
				}
				else {
					sys.nes.doaudio=true;
					sys.nes.apu.synth.start();
				}
			}
		});
		chckbxmntmEnableAudio.setSelected(true);
		mnNewMenu.add(chckbxmntmEnableAudio);
		
		JMenu mnGraphics = new JMenu("Graphics");
		menuBar.add(mnGraphics);
		
		JMenu mnScaling = new JMenu("Scaling");
		mnGraphics.add(mnScaling);
		
		JRadioButtonMenuItem rdbtnmntmxScaling = new JRadioButtonMenuItem("1x Scaling");
		rdbtnmntmxScaling.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sys.display.updateScaling(1);
				sys.frame.setBounds(100, 100, 256+15, 240+60);
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
				sys.frame.setBounds(100, 100, 256*2+15, 240*2+60);
			}
		});
		mnScaling.add(rdbtnmntmxScaling_1);
		group.add(rdbtnmntmxScaling_1);
		JRadioButtonMenuItem rdbtnmntmxScaling_2 = new JRadioButtonMenuItem("3x Scaling");
		rdbtnmntmxScaling_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sys.display.updateScaling(3);
				sys.frame.setBounds(100, 100, 256*3+15, 240*3+60);
			}
		});
		mnScaling.add(rdbtnmntmxScaling_2);
		group.add(rdbtnmntmxScaling_2);

		JRadioButtonMenuItem rdbtnmntmxScaling_3 = new JRadioButtonMenuItem("4x Scaling");
		rdbtnmntmxScaling_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sys.display.updateScaling(4);
				sys.frame.setBounds(100, 100, 256*4+15, 240*4+60);
			}
		});
		mnScaling.add(rdbtnmntmxScaling_3);
		group.add(rdbtnmntmxScaling_3);
		JMenu mnControl = new JMenu("Control");
		menuBar.add(mnControl);
		
		JMenuItem mntmConfigure = new JMenuItem("Configure");
		mntmConfigure.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sys.keyconfig.setVisible(true);
			}
		});
		mnControl.add(mntmConfigure);
		
		JMenu mnDebug = new JMenu("Debug");
		menuBar.add(mnDebug);
		
		JCheckBoxMenuItem chckbxmntmShowDebug = new JCheckBoxMenuItem("Show Debug");
		chckbxmntmShowDebug.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sys.debugframe.setVisible(!sys.debugframe.isVisible());
			}
		});
		mnDebug.add(chckbxmntmShowDebug);
		//contentPane = new JPanel();
		//contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		//contentPane.setLayout(new BorderLayout(0, 0));
		//setContentPane(contentPane);
	}

}
