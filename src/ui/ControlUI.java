package ui;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;
import ui.ui.input.InputManager;

import javax.swing.JButton;
import javax.swing.JLabel;
import java.util.Properties;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTabbedPane;
import javax.swing.JCheckBox;

public class ControlUI extends JFrame {

	private static final long serialVersionUID = -7060080516930936378L;
	public JPanel contentPane;
	boolean awaitingkey;
	Properties prop;
	SystemUI sys;

	public ControlUI(Properties p, SystemUI s) {
		setTitle("Control Settings");
		sys = s;
		prop = p;
        //noinspection MagicConstant
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 342);
		contentPane = new JPanel();  
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 434, 303);
		contentPane.add(tabbedPane);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Controller 1", null, panel, null);
		panel.setLayout(null);
		
		JLabel lblController = new JLabel("Controller 1");
		lblController.setBounds(171, 11, 74, 14);
		panel.add(lblController);
		
		JLabel lblAButton = new JLabel("A Button");
		lblAButton.setBounds(360, 91, 59, 14);
		panel.add(lblAButton);
		
		JButton abutton1 = new JButton("<"+ InputManager.c1controls[0].idname+">");
		abutton1.setBounds(336, 111, 83, 23);
		panel.add(abutton1);
		
		JLabel lblBButton = new JLabel("B Button");
		lblBButton.setBounds(267, 91, 59, 14);
		panel.add(lblBButton);
		
		JButton bbutton1 = new JButton("<"+InputManager.c1controls[1].idname+">");
		bbutton1.setBounds(243, 111, 83, 23);
		panel.add(bbutton1);
		
		JLabel lblUpdpad = new JLabel("Up");
		lblUpdpad.setBounds(77, 64, 42, 14);
		panel.add(lblUpdpad);
		
		JButton upbutton1 = new JButton("<"+InputManager.c1controls[4].idname+">");
		upbutton1.setBounds(47, 83, 83, 23);
		panel.add(upbutton1);
		
		JLabel lblDown = new JLabel("Down");
		lblDown.setBounds(78, 166, 42, 14);
		panel.add(lblDown);
		
		JButton downbutton1 = new JButton("<"+InputManager.c1controls[5].idname+">");
		downbutton1.setBounds(52, 180, 83, 23);
		panel.add(downbutton1);
		
		JLabel lblLeft = new JLabel("Left");
		lblLeft.setBounds(24, 115, 42, 14);
		panel.add(lblLeft);
		
		JButton leftbutton1 = new JButton("<"+InputManager.c1controls[6].idname+">");
		leftbutton1.setBounds(0, 132, 83, 23);
		panel.add(leftbutton1);
		
		JLabel lblRight = new JLabel("Right");
		lblRight.setBounds(130, 115, 42, 14);
		panel.add(lblRight);
		
		JButton rightbutton1 = new JButton("<"+InputManager.c1controls[7].idname+">");
		rightbutton1.setBounds(101, 132, 83, 23);
		panel.add(rightbutton1);
		
		JLabel lblStart = new JLabel("Start");
		lblStart.setBounds(336, 179, 42, 14);
		panel.add(lblStart);
		
		JButton startbutton1 = new JButton("<"+InputManager.c1controls[3].idname+">");
		startbutton1.setBounds(309, 199, 83, 23);
		panel.add(startbutton1);
		
		JLabel lblSelect = new JLabel("Select");
		lblSelect.setBounds(208, 180, 42, 14);
		panel.add(lblSelect);
		
		JButton selectbutton1 = new JButton("<"+InputManager.c1controls[2].idname+">");
		selectbutton1.setBounds(185, 199, 83, 23);
		panel.add(selectbutton1);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Controller 2", null, panel_1, null);
		panel_1.setLayout(null);
		
		JLabel lblController_1 = new JLabel("Controller 2");
		lblController_1.setBounds(171, 11, 74, 14);
		panel_1.add(lblController_1);
		
		JLabel lblAButton_1 = new JLabel("A Button");
		lblAButton_1.setBounds(360, 91, 59, 14);
		panel_1.add(lblAButton_1);
		
		JButton abutton2 = new JButton("<"+InputManager.c2controls[0].idname+">");
		abutton2.setBounds(336, 111, 83, 23);
		panel_1.add(abutton2);
		
		JLabel lblBButton_1 = new JLabel("B Button");
		lblBButton_1.setBounds(267, 91, 59, 14);
		panel_1.add(lblBButton_1);
		
		JButton bbutton2 = new JButton("<"+InputManager.c2controls[1].idname+">");
		bbutton2.setBounds(243, 111, 83, 23);
		panel_1.add(bbutton2);
		
		JButton upbutton2 = new JButton("<"+InputManager.c2controls[4].idname+">");
		upbutton2.setBounds(47, 83, 83, 23);
		panel_1.add(upbutton2);
		upbutton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InputManager.c2controls[4] =getbutton();
				upbutton2.setText("<"+InputManager.c2controls[4].idname+">");
			}
		});
		
		JLabel lblUp = new JLabel("Up");
		lblUp.setBounds(77, 64, 28, 14);
		panel_1.add(lblUp);
		
		JLabel label = new JLabel("Down");
		label.setBounds(78, 166, 35, 14);
		panel_1.add(label);
		
		JButton downbutton2 = new JButton("<"+InputManager.c2controls[5].idname+">");
		downbutton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InputManager.c2controls[5] =getbutton();
				downbutton2.setText("<"+InputManager.c2controls[5].idname+">");
			}
		});
		downbutton2.setBounds(52, 180, 83, 23);
		panel_1.add(downbutton2);
		
		JLabel label_1 = new JLabel("Left");
		label_1.setBounds(24, 115, 31, 14);
		panel_1.add(label_1);
		
		JButton leftbutton2 = new JButton("<"+InputManager.c2controls[6].idname+">");
		leftbutton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InputManager.c2controls[6] =getbutton();
				leftbutton2.setText("<"+InputManager.c2controls[6].idname+">");
				awaitingkey=true;
			}
		});
		leftbutton2.setBounds(0, 132, 83, 23);
		panel_1.add(leftbutton2);
		
		JLabel label_2 = new JLabel("Right");
		label_2.setBounds(130, 115, 34, 14);
		panel_1.add(label_2);
		
		JButton rightbutton2 = new JButton("<"+InputManager.c2controls[7].idname+">");
		rightbutton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InputManager.c2controls[7] =getbutton();
				rightbutton2.setText("<"+InputManager.c2controls[7].idname+">");
			}
		});
		rightbutton2.setBounds(101, 132, 83, 23);
		panel_1.add(rightbutton2);
		
		JLabel label_3 = new JLabel("Start");
		label_3.setBounds(336, 179, 35, 14);
		panel_1.add(label_3);
		
		JButton startbutton2 = new JButton("<"+InputManager.c2controls[3].idname+">");
		startbutton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InputManager.c2controls[3] =getbutton();
				startbutton2.setText("<"+InputManager.c2controls[3].idname+">");
			}
		});
		startbutton2.setBounds(309, 199, 83, 23);
		panel_1.add(startbutton2);
		
		JLabel label_4 = new JLabel("Select");
		label_4.setBounds(208, 180, 36, 14);
		panel_1.add(label_4);
		
		JButton selectbutton2 = new JButton("<"+InputManager.c2controls[2].idname+">");
		selectbutton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InputManager.c2controls[2] =getbutton();
				selectbutton2.setText("<"+InputManager.c2controls[2].idname+">");
			}
		});
		selectbutton2.setBounds(185, 199, 83, 23);
		panel_1.add(selectbutton2);
		
		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("Hot Keys", null, panel_2, null);
		panel_2.setLayout(null);
		
		JLabel lblHotKeys = new JLabel("Hot Keys");
		lblHotKeys.setBounds(172, 11, 46, 14);
		panel_2.add(lblHotKeys);
		
		JLabel lblQuickSave = new JLabel("Quick Save");
		lblQuickSave.setBounds(100, 44, 63, 14);
		panel_2.add(lblQuickSave);
		
		JLabel lblQuickLoad = new JLabel("Quick Load");
		lblQuickLoad.setBounds(100, 70, 63, 14);
		panel_2.add(lblQuickLoad);
		
		JButton btnNewButton = new JButton("<"+InputManager.hotkeys[0].idname+">");
		btnNewButton.setBounds(188, 41, 83, 23);
		panel_2.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("<"+InputManager.hotkeys[1].idname+">");
		btnNewButton_1.setBounds(188, 66, 83, 23);
		panel_2.add(btnNewButton_1);
		
		JLabel lblNewLabel = new JLabel("Input Recording");
		lblNewLabel.setBounds(100, 95, 83, 14);
		panel_2.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Input Playback");
		lblNewLabel_1.setBounds(100, 120, 83, 14);
		panel_2.add(lblNewLabel_1);
		
		JButton btnNewButton_2 = new JButton("<"+InputManager.hotkeys[2].idname+">");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				InputManager.hotkeys[2] = getbutton();
				btnNewButton_2.setText("<"+InputManager.hotkeys[2].idname+">");
			}
		});
		btnNewButton_2.setBounds(188, 91, 83, 23);
		panel_2.add(btnNewButton_2);
		
		JButton btnNewButton_3 = new JButton("<"+InputManager.hotkeys[3].idname+">");
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				InputManager.hotkeys[3] = getbutton();
				btnNewButton_3.setText("<"+InputManager.hotkeys[3].idname+">");
			}
		});
		btnNewButton_3.setBounds(188, 116, 83, 23);
		panel_2.add(btnNewButton_3);
		
		JLabel lblToggleRecording = new JLabel("Toggle Recording");
		lblToggleRecording.setBounds(100, 145, 95, 14);
		panel_2.add(lblToggleRecording);
		
		JButton btnNewButton_4 = new JButton("<"+InputManager.hotkeys[4].idname+">");
		btnNewButton_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0){
				InputManager.hotkeys[4] = getbutton();
				btnNewButton_4.setText("<"+InputManager.hotkeys[4].idname+">");
			}
		});
		btnNewButton_4.setBounds(188, 141, 83, 23);
		panel_2.add(btnNewButton_4);
		
		JPanel panel_3 = new JPanel();
		tabbedPane.addTab("Settings", null, panel_3, null);
		panel_3.setLayout(null);
		
		JLabel lblAllowInputsWhen = new JLabel("<html>Allow inputs while<br>window is not<br>focused.</html>");
		lblAllowInputsWhen.setBounds(36, 39, 98, 42);
		panel_3.add(lblAllowInputsWhen);
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("");
		chckbxNewCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UISettings.controlwhilenotfocused=!UISettings.controlwhilenotfocused;
			}
		});
		chckbxNewCheckBox.setBounds(140, 39, 31, 23);
		chckbxNewCheckBox.setSelected(UISettings.controlwhilenotfocused);
		panel_3.add(chckbxNewCheckBox);
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InputManager.hotkeys[1] = getbutton();
				btnNewButton.setText("<"+InputManager.hotkeys[1].idname+">");
			}
		});
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				InputManager.hotkeys[0] = getbutton();
				btnNewButton.setText("<"+InputManager.hotkeys[0].idname+">");
			}
		});
		bbutton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InputManager.c2controls[1] =getbutton();
				bbutton2.setText("<"+InputManager.c2controls[1].idname+">");
			}
		});
		abutton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InputManager.c2controls[0] =getbutton();
				abutton2.setText("<"+InputManager.c2controls[0].idname+">");
			}
		});
		selectbutton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InputManager.c1controls[2] =getbutton();
				selectbutton1.setText("<"+InputManager.c1controls[2].idname+">");
			}
		});
		startbutton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InputManager.c1controls[3] =getbutton();
				startbutton1.setText("<"+InputManager.c1controls[3].idname+">");
			}
		});
		rightbutton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InputManager.c1controls[7] =getbutton();
				rightbutton1.setText("<"+InputManager.c1controls[7].idname+">");
			}
		});
		leftbutton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InputManager.c1controls[6] =getbutton();
				leftbutton1.setText("<"+InputManager.c1controls[6].idname+">");
				awaitingkey=true;
			}
		});
		downbutton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InputManager.c1controls[5] =getbutton();
				downbutton1.setText("<"+InputManager.c1controls[5].idname+">");
			}
		});
		upbutton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InputManager.c1controls[4] =getbutton();
				upbutton1.setText("<"+InputManager.c1controls[4].idname+">");
			}
		});
		bbutton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InputManager.c1controls[1] =getbutton();
				bbutton1.setText("<"+InputManager.c1controls[1].idname+">");
			}
		});
		abutton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InputManager.c1controls[0] =getbutton();
				abutton1.setText("<"+InputManager.c1controls[0].idname+">");
			}
		});
		
		
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
	}
	private ControllerInfo getbutton(){
        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
        for(int i=0;i<controllers.length;i++) {
        	controllers[i].poll();
        	EventQueue queue = controllers[i].getEventQueue();
            Event event = new Event();
            queue.getNextEvent(event);
            while(queue.getNextEvent(event));
        }
		 while(true) {
	         if(controllers.length==0) {
	            System.out.println("Found no controllers.");
	            System.exit(0);
	         }
	         
	         for(int i=0;i<controllers.length;i++) {
	            controllers[i].poll();
	            EventQueue queue = controllers[i].getEventQueue();
	            Event event = new Event();
	            //queue.getNextEvent(event);
	            //controllers[i].poll();
	            while(queue.getNextEvent(event)&&!controllers[i].getType().equals(Controller.Type.MOUSE)) {
	                StringBuffer buffer = new StringBuffer(controllers[i].getName());
	                buffer.append(" at ");
	                buffer.append(event.getNanos()).append(", ");
	                Component comp = event.getComponent();
	                buffer.append(comp.getName()).append(" changed to ");
	                float value = event.getValue(); 
	                if(comp.isAnalog()) {
	                   buffer.append(value);
	                } else {
	                	System.out.println(controllers[i].getName()+":"+controllers[i].getPortNumber());
	                   return new ControllerInfo(controllers[i],comp.getIdentifier(),value);
	                }
	                System.out.println(buffer.toString());
	             }
	          }
	          
	          try {
	             Thread.sleep(20);
	          } catch (InterruptedException e) {
	             // TODO Auto-generated catch block
	             e.printStackTrace();
	          }
	          }
	}
}
