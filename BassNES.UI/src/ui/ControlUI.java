package ui;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import ui.input.ControllerInterface;
import ui.input.ControllerInterface.QuickKeyButtons;
import ui.input.ControllerInterface.ControllerButtons;
import ui.settings.UISettings;

import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTabbedPane;
import javax.swing.JCheckBox;

public class ControlUI extends JFrame {

	private static final long serialVersionUID = -7060080516930936378L;
	public JPanel contentPane;
	boolean awaitingkey;
	private ControllerInterface input;
	public ControlUI(ControllerInterface in) {
		setTitle("Control Settings");
		input = in;
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
		
		JButton abutton1 = new JButton("<"+ input.getControllerButtonIdName(0,ControllerButtons.A)+">");
		abutton1.setBounds(336, 111, 83, 23);
		panel.add(abutton1);
		
		JLabel lblBButton = new JLabel("B Button");
		lblBButton.setBounds(267, 91, 59, 14);
		panel.add(lblBButton);
		
		JButton bbutton1 = new JButton("<"+input.getControllerButtonIdName(0,ControllerButtons.B)+">");
		bbutton1.setBounds(243, 111, 83, 23);
		panel.add(bbutton1);
		
		JLabel lblUpdpad = new JLabel("Up");
		lblUpdpad.setBounds(77, 64, 42, 14);
		panel.add(lblUpdpad);
		
		JButton upbutton1 = new JButton("<"+input.getControllerButtonIdName(0,ControllerButtons.Up)+">");
		upbutton1.setBounds(47, 83, 83, 23);
		panel.add(upbutton1);
		
		JLabel lblDown = new JLabel("Down");
		lblDown.setBounds(78, 166, 42, 14);
		panel.add(lblDown);
		
		JButton downbutton1 = new JButton("<"+input.getControllerButtonIdName(0,ControllerButtons.Down)+">");
		downbutton1.setBounds(52, 180, 83, 23);
		panel.add(downbutton1);
		
		JLabel lblLeft = new JLabel("Left");
		lblLeft.setBounds(24, 115, 42, 14);
		panel.add(lblLeft);
		
		JButton leftbutton1 = new JButton("<"+input.getControllerButtonIdName(0,ControllerButtons.Left)+">");
		leftbutton1.setBounds(0, 132, 83, 23);
		panel.add(leftbutton1);
		
		JLabel lblRight = new JLabel("Right");
		lblRight.setBounds(130, 115, 42, 14);
		panel.add(lblRight);
		
		JButton rightbutton1 = new JButton("<"+input.getControllerButtonIdName(0,ControllerButtons.Right)+">");
		rightbutton1.setBounds(101, 132, 83, 23);
		panel.add(rightbutton1);
		
		JLabel lblStart = new JLabel("Start");
		lblStart.setBounds(336, 179, 42, 14);
		panel.add(lblStart);
		
		JButton startbutton1 = new JButton("<"+input.getControllerButtonIdName(0,ControllerButtons.Start)+">");
		startbutton1.setBounds(309, 199, 83, 23);
		panel.add(startbutton1);
		
		JLabel lblSelect = new JLabel("Select");
		lblSelect.setBounds(208, 180, 42, 14);
		panel.add(lblSelect);
		
		JButton selectbutton1 = new JButton("<"+input.getControllerButtonIdName(0,ControllerButtons.Select)+">");
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
		
		JButton abutton2 = new JButton("<"+input.getControllerButtonIdName(1,ControllerButtons.A)+">");
		abutton2.setBounds(336, 111, 83, 23);
		panel_1.add(abutton2);
		
		JLabel lblBButton_1 = new JLabel("B Button");
		lblBButton_1.setBounds(267, 91, 59, 14);
		panel_1.add(lblBButton_1);
		
		JButton bbutton2 = new JButton("<"+input.getControllerButtonIdName(1,ControllerButtons.B)+">");
		bbutton2.setBounds(243, 111, 83, 23);
		panel_1.add(bbutton2);
		
		JButton upbutton2 = new JButton("<"+input.getControllerButtonIdName(1,ControllerButtons.Up)+">");
		upbutton2.setBounds(47, 83, 83, 23);
		panel_1.add(upbutton2);
		upbutton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				input.setControllerButton(1,ControllerButtons.Up);
				upbutton2.setText("<"+input.getControllerButtonIdName(1,ControllerButtons.Up)+">");
			}
		});
		
		JLabel lblUp = new JLabel("Up");
		lblUp.setBounds(77, 64, 28, 14);
		panel_1.add(lblUp);
		
		JLabel label = new JLabel("Down");
		label.setBounds(78, 166, 35, 14);
		panel_1.add(label);
		
		JButton downbutton2 = new JButton("<"+input.getControllerButtonIdName(1,ControllerButtons.Down)+">");
		downbutton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				input.setControllerButton(1,ControllerButtons.Down);
				downbutton2.setText("<"+input.getControllerButtonIdName(1,ControllerButtons.Down)+">");
			}
		});
		downbutton2.setBounds(52, 180, 83, 23);
		panel_1.add(downbutton2);
		
		JLabel label_1 = new JLabel("Left");
		label_1.setBounds(24, 115, 31, 14);
		panel_1.add(label_1);
		
		JButton leftbutton2 = new JButton("<"+input.getControllerButtonIdName(1,ControllerButtons.Left)+">");
		leftbutton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				input.setControllerButton(1,ControllerButtons.Left);
				leftbutton2.setText("<"+input.getControllerButtonIdName(1,ControllerButtons.Left)+">");
				awaitingkey=true;
			}
		});
		leftbutton2.setBounds(0, 132, 83, 23);
		panel_1.add(leftbutton2);
		
		JLabel label_2 = new JLabel("Right");
		label_2.setBounds(130, 115, 34, 14);
		panel_1.add(label_2);
		
		JButton rightbutton2 = new JButton("<"+input.getControllerButtonIdName(1,ControllerButtons.Right)+">");
		rightbutton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				input.setControllerButton(1,ControllerButtons.Right);
				rightbutton2.setText("<"+input.getControllerButtonIdName(1,ControllerButtons.Right)+">");
			}
		});
		rightbutton2.setBounds(101, 132, 83, 23);
		panel_1.add(rightbutton2);
		
		JLabel label_3 = new JLabel("Start");
		label_3.setBounds(336, 179, 35, 14);
		panel_1.add(label_3);
		
		JButton startbutton2 = new JButton("<"+input.getControllerButtonIdName(1,ControllerButtons.Start)+">");
		startbutton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				input.setControllerButton(1,ControllerButtons.Start);
				startbutton2.setText("<"+input.getControllerButtonIdName(1,ControllerButtons.Start)+">");
			}
		});
		startbutton2.setBounds(309, 199, 83, 23);
		panel_1.add(startbutton2);
		
		JLabel label_4 = new JLabel("Select");
		label_4.setBounds(208, 180, 36, 14);
		panel_1.add(label_4);
		
		JButton selectbutton2 = new JButton("<"+input.getControllerButtonIdName(1,ControllerButtons.Select)+">");
		selectbutton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				input.setControllerButton(1,ControllerButtons.Select);
				selectbutton2.setText("<"+input.getControllerButtonIdName(1,ControllerButtons.Select)+">");
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
		
		JButton btnNewButton = new JButton("<"+input.getQuickKeyButtonIdName(QuickKeyButtons.SaveState)+">");
		btnNewButton.setBounds(188, 41, 83, 23);
		panel_2.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("<"+input.getQuickKeyButtonIdName(QuickKeyButtons.LoadState)+">");
		btnNewButton_1.setBounds(188, 66, 83, 23);
		panel_2.add(btnNewButton_1);
		
		JLabel lblNewLabel = new JLabel("Input Recording");
		lblNewLabel.setBounds(100, 95, 83, 14);
		panel_2.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Input Playback");
		lblNewLabel_1.setBounds(100, 120, 83, 14);
		panel_2.add(lblNewLabel_1);
		
		JButton btnNewButton_2 = new JButton("<"+input.getQuickKeyButtonIdName(QuickKeyButtons.InputRecord)+">");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				input.setQuickKeyButton(QuickKeyButtons.InputRecord);
				btnNewButton_2.setText("<"+input.getQuickKeyButtonIdName(QuickKeyButtons.InputRecord)+">");
			}
		});
		btnNewButton_2.setBounds(188, 91, 83, 23);
		panel_2.add(btnNewButton_2);
		
		JButton btnNewButton_3 = new JButton("<"+input.getQuickKeyButtonIdName(QuickKeyButtons.InputPlay)+">");
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				input.setQuickKeyButton(QuickKeyButtons.InputPlay);
				btnNewButton_3.setText("<"+input.getQuickKeyButtonIdName(QuickKeyButtons.InputPlay)+">");
			}
		});
		btnNewButton_3.setBounds(188, 116, 83, 23);
		panel_2.add(btnNewButton_3);
		
		JLabel lblToggleRecording = new JLabel("Toggle Recording");
		lblToggleRecording.setBounds(100, 145, 95, 14);
		panel_2.add(lblToggleRecording);
		
		JButton btnNewButton_4 = new JButton("<"+input.getQuickKeyButtonIdName(QuickKeyButtons.AudioRecord)+">");
		btnNewButton_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0){
				input.setQuickKeyButton(QuickKeyButtons.AudioRecord);
				btnNewButton_4.setText("<"+input.getQuickKeyButtonIdName(QuickKeyButtons.AudioRecord)+">");
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
				input.setQuickKeyButton(QuickKeyButtons.LoadState);
				btnNewButton_1.setText("<"+input.getQuickKeyButtonIdName(QuickKeyButtons.LoadState)+">");
			}
		});
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				input.setQuickKeyButton(QuickKeyButtons.SaveState);
				btnNewButton.setText("<"+input.getQuickKeyButtonIdName(QuickKeyButtons.SaveState)+">");
			}
		});
		bbutton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				input.setControllerButton(1,ControllerButtons.B);
				bbutton2.setText("<"+input.getControllerButtonIdName(1,ControllerButtons.B)+">");
			}
		});
		abutton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				input.setControllerButton(1,ControllerButtons.A);
				abutton2.setText("<"+input.getControllerButtonIdName(1,ControllerButtons.A)+">");
			}
		});
		selectbutton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				input.setControllerButton(0,ControllerButtons.Select);
				selectbutton1.setText("<"+input.getControllerButtonIdName(0,ControllerButtons.Select)+">");
			}
		});
		startbutton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				input.setControllerButton(0,ControllerButtons.Start);
				startbutton1.setText("<"+input.getControllerButtonIdName(0,ControllerButtons.Start)+">");
			}
		});
		rightbutton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				input.setControllerButton(0,ControllerButtons.Right);
				rightbutton1.setText("<"+input.getControllerButtonIdName(0,ControllerButtons.Right)+">");
			}
		});
		leftbutton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				input.setControllerButton(0,ControllerButtons.Left);
				leftbutton1.setText("<"+input.getControllerButtonIdName(0,ControllerButtons.Left)+">");
				awaitingkey=true;
			}
		});
		downbutton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				input.setControllerButton(0,ControllerButtons.Down);
				downbutton1.setText("<"+input.getControllerButtonIdName(0,ControllerButtons.Down)+">");
			}
		});
		upbutton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				input.setControllerButton(0,ControllerButtons.Up);
				upbutton1.setText("<"+input.getControllerButtonIdName(0,ControllerButtons.Up)+">");
			}
		});
		bbutton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				input.setControllerButton(0,ControllerButtons.B);
				bbutton1.setText("<"+input.getControllerButtonIdName(0,ControllerButtons.B)+">");
			}
		});
		abutton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				input.setControllerButton(0,ControllerButtons.A);
				abutton1.setText("<"+input.getControllerButtonIdName(0,ControllerButtons.A)+">");
			}
		});
		
		
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
	}

}
