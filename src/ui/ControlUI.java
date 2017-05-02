package ui;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.util.Properties;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ControlUI extends JFrame {

	private static final long serialVersionUID = -7060080516930936378L;
	public JPanel contentPane;
	boolean awaitingkey;
	Properties prop;
	SystemUI sys;

	public ControlUI(Properties p, SystemUI s) {
		sys = s;
		prop = p;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 313);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblController = new JLabel("Controller 1");
		lblController.setBounds(87, 1, 74, 14);
		contentPane.add(lblController);
		
		JLabel lblController_1 = new JLabel("Controller 2");
		lblController_1.setBounds(279, 1, 74, 14);
		contentPane.add(lblController_1);
		
		JLabel lblAButton = new JLabel("A Button");
		lblAButton.setBounds(45, 24, 59, 14);
		contentPane.add(lblAButton);
		
		JButton btnNewButton = new JButton("<"+KeyEvent.getKeyText(UserSettings.c1a)+">");
		btnNewButton.setBounds(114, 20, 83, 23);
		btnNewButton.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				btnNewButton.setText("<"+KeyEvent.getKeyText(arg0.getKeyCode())+">");
				//prop.setProperty("c1a",arg0.getKeyCode()+"");
				UserSettings.c1a = arg0.getKeyCode();
			}
		});
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				awaitingkey=true;
			}
		});
		contentPane.add(btnNewButton);
		
		JLabel lblAButton_1 = new JLabel("A Button");
		lblAButton_1.setBounds(232, 24, 59, 14);
		contentPane.add(lblAButton_1);
		
		JButton btnNewButton_8 = new JButton("<"+KeyEvent.getKeyText(UserSettings.c2a)+">");
		btnNewButton_8.setBounds(301, 20, 83, 23);
		btnNewButton_8.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				btnNewButton_8.setText("<"+KeyEvent.getKeyText(e.getKeyCode())+">");
				//prop.setProperty("c2a",e.getKeyCode()+"");
				UserSettings.c2a = e.getKeyCode();
			}
		});
		contentPane.add(btnNewButton_8);
		
		JLabel lblBButton = new JLabel("B Button");
		lblBButton.setBounds(45, 52, 59, 14);
		contentPane.add(lblBButton);
		
		JButton btnNewButton_1 = new JButton("<"+KeyEvent.getKeyText(UserSettings.c1b)+">");
		btnNewButton_1.setBounds(114, 48, 83, 23);
		btnNewButton_1.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				btnNewButton_1.setText("<"+KeyEvent.getKeyText(arg0.getKeyCode())+">");
				//prop.setProperty("c1b",arg0.getKeyCode()+"");
				UserSettings.c1b = arg0.getKeyCode();
			}
		});
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				awaitingkey=true;
			}
		});
		contentPane.add(btnNewButton_1);
		
		JLabel lblBButton_1 = new JLabel("B Button");
		lblBButton_1.setBounds(232, 52, 59, 14);
		contentPane.add(lblBButton_1);
		
		JButton btnNewButton_9 = new JButton("<"+KeyEvent.getKeyText(UserSettings.c2b)+">");
		btnNewButton_9.setBounds(301, 48, 83, 23);
		btnNewButton_9.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				btnNewButton_9.setText("<"+KeyEvent.getKeyText(e.getKeyCode())+">");
				//prop.setProperty("c2b",e.getKeyCode()+"");
				UserSettings.c2b = e.getKeyCode();
			}
		});
		contentPane.add(btnNewButton_9);
		
		JLabel lblUpdpad = new JLabel("Up");
		lblUpdpad.setBounds(45, 80, 42, 14);
		contentPane.add(lblUpdpad);
		
		JButton btnNewButton_2 = new JButton("<"+KeyEvent.getKeyText(UserSettings.c1up)+">");
		btnNewButton_2.setBounds(114, 76, 83, 23);
		btnNewButton_2.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				btnNewButton_2.setText("<"+KeyEvent.getKeyText(e.getKeyCode())+">");
				//prop.setProperty("c1up",e.getKeyCode()+"");
				UserSettings.c1up = e.getKeyCode();
			}
		});
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				awaitingkey=true;
			}
		});
		contentPane.add(btnNewButton_2);
		
		JButton btnNewButton_10 = new JButton("<"+KeyEvent.getKeyText(UserSettings.c2up)+">");
		btnNewButton_10.setBounds(301, 76, 83, 23);
		btnNewButton_10.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				btnNewButton_10.setText("<"+KeyEvent.getKeyText(e.getKeyCode())+">");
				//prop.setProperty("c2up",e.getKeyCode()+"");
				UserSettings.c2up = e.getKeyCode();
			}
		});
		
		JLabel lblUp = new JLabel("Up");
		lblUp.setBounds(246, 80, 28, 14);
		contentPane.add(lblUp);
		contentPane.add(btnNewButton_10);
		
		JLabel lblDown = new JLabel("Down");
		lblDown.setBounds(45, 108, 42, 14);
		contentPane.add(lblDown);
		
		JButton btnNewButton_3 = new JButton("<"+KeyEvent.getKeyText(UserSettings.c1down)+">");
		btnNewButton_3.setBounds(114, 104, 83, 23);
		btnNewButton_3.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				btnNewButton_3.setText("<"+KeyEvent.getKeyText(e.getKeyCode())+">");
				//prop.setProperty("c1down",e.getKeyCode()+"");
				UserSettings.c1down = e.getKeyCode();

			}
		});
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				awaitingkey=true;
			}
		});
		contentPane.add(btnNewButton_3);
		
		JButton btnNewButton_4 = new JButton("<"+KeyEvent.getKeyText(UserSettings.c1left)+">");
		btnNewButton_4.setBounds(114, 132, 83, 23);
		btnNewButton_4.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				btnNewButton_4.setText("<"+KeyEvent.getKeyText(e.getKeyCode())+">");
				//prop.setProperty("c1left",e.getKeyCode()+"");
				UserSettings.c1left = e.getKeyCode();

			}
		});
		btnNewButton_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				awaitingkey=true;
			}
		});
		
		JLabel lblDown_1 = new JLabel("Down");
		lblDown_1.setBounds(239, 108, 35, 14);
		contentPane.add(lblDown_1);
		
		JButton btnNewButton_11 = new JButton("<"+KeyEvent.getKeyText(UserSettings.c2down)+">");
		btnNewButton_11.setBounds(301, 104, 83, 23);
		btnNewButton_11.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				btnNewButton_11.setText("<"+KeyEvent.getKeyText(e.getKeyCode())+">");
				//prop.setProperty("c2down",e.getKeyCode()+"");
				UserSettings.c2up = e.getKeyCode();
			}
		});
		contentPane.add(btnNewButton_11);
		
		JLabel lblLeft = new JLabel("Left");
		lblLeft.setBounds(45, 136, 42, 14);
		contentPane.add(lblLeft);
		contentPane.add(btnNewButton_4);
		
		JButton btnNewButton_12 = new JButton("<"+KeyEvent.getKeyText(UserSettings.c2left)+">");
		btnNewButton_12.setBounds(301, 132, 83, 23);
		btnNewButton_12.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				btnNewButton_12.setText("<"+KeyEvent.getKeyText(e.getKeyCode())+">");
				//prop.setProperty("c2left",e.getKeyCode()+"");
				UserSettings.c2left = e.getKeyCode();
			}
		});
		
		JLabel lblLeft_1 = new JLabel("Left");
		lblLeft_1.setBounds(243, 136, 31, 14);
		contentPane.add(lblLeft_1);
		contentPane.add(btnNewButton_12);
		
		JLabel lblRight = new JLabel("Right");
		lblRight.setBounds(45, 164, 42, 14);
		contentPane.add(lblRight);
		
		JButton btnNewButton_5 = new JButton("<"+KeyEvent.getKeyText(UserSettings.c1right)+">");
		btnNewButton_5.setBounds(114, 160, 83, 23);
		btnNewButton_5.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				btnNewButton_5.setText("<"+KeyEvent.getKeyText(e.getKeyCode())+">");
				//prop.setProperty("c1right",e.getKeyCode()+"");
				UserSettings.c1right = e.getKeyCode();

			}
		});
		btnNewButton_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				awaitingkey=true;
			}
		});
		contentPane.add(btnNewButton_5);
		
		JLabel lblRight_1 = new JLabel("Right");
		lblRight_1.setBounds(240, 164, 34, 14);
		contentPane.add(lblRight_1);
		
		JButton btnNewButton_13 = new JButton("<"+KeyEvent.getKeyText(UserSettings.c2right)+">");
		btnNewButton_13.setBounds(301, 160, 83, 23);
		btnNewButton_13.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				btnNewButton_13.setText("<"+KeyEvent.getKeyText(e.getKeyCode())+">");
				//prop.setProperty("c2right",e.getKeyCode()+"");
				UserSettings.c2right = e.getKeyCode();

			}
		});
		contentPane.add(btnNewButton_13);
		
		JLabel lblStart = new JLabel("Start");
		lblStart.setBounds(45, 192, 42, 14);
		contentPane.add(lblStart);
		
		JButton btnNewButton_6 = new JButton("<"+KeyEvent.getKeyText(UserSettings.c1start)+">");
		btnNewButton_6.setBounds(114, 188, 83, 23);
		btnNewButton_6.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				btnNewButton_6.setText("<"+KeyEvent.getKeyText(e.getKeyCode())+">");
				//prop.setProperty("c1start",e.getKeyCode()+"");
				UserSettings.c1start = e.getKeyCode();

			}
		});
		btnNewButton_6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				awaitingkey=true;
			}
		});
		contentPane.add(btnNewButton_6);
		
		JButton btnNewButton_14 = new JButton("<"+KeyEvent.getKeyText(UserSettings.c2start)+">");
		btnNewButton_14.setBounds(301, 188, 83, 23);
		btnNewButton_14.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				btnNewButton_14.setText("<"+KeyEvent.getKeyText(e.getKeyCode())+">");
				//prop.setProperty("c2start",e.getKeyCode()+"");
				UserSettings.c2start = e.getKeyCode();

			}
		});
		
		JLabel lblStart_1 = new JLabel("Start");
		lblStart_1.setBounds(241, 192, 35, 14);
		contentPane.add(lblStart_1);
		contentPane.add(btnNewButton_14);
		
		JLabel lblSelect = new JLabel("Select");
		lblSelect.setBounds(45, 220, 42, 14);
		contentPane.add(lblSelect);
		
		JButton btnNewButton_7 = new JButton("<"+KeyEvent.getKeyText(UserSettings.c1select)+">");
		btnNewButton_7.setBounds(114, 216, 83, 23);
		btnNewButton_7.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				btnNewButton_7.setText("<"+KeyEvent.getKeyText(e.getKeyCode())+">");
				//prop.setProperty("c1select",e.getKeyCode()+"");
				UserSettings.c1select = e.getKeyCode();

			}
		});
		btnNewButton_7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				awaitingkey=true;
			}
		});
		contentPane.add(btnNewButton_7);
		
		JButton btnNewButton_15 = new JButton("<"+KeyEvent.getKeyText(UserSettings.c2select)+">");
		btnNewButton_15.setBounds(301, 216, 83, 23);
		btnNewButton_15.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				btnNewButton_15.setText("<"+KeyEvent.getKeyText(e.getKeyCode())+">");
				//prop.setProperty("c2select",e.getKeyCode()+"");
				UserSettings.c2select = e.getKeyCode();

			}
		});
		
		JLabel lblSelect_1 = new JLabel("Select");
		lblSelect_1.setBounds(238, 220, 36, 14);
		contentPane.add(lblSelect_1);
		contentPane.add(btnNewButton_15);
		
		/*JButton btnNewButton_16 = new JButton("Apply");
		btnNewButton_16.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		GridBagConstraints gbc_btnNewButton_16 = new GridBagConstraints();
		gbc_btnNewButton_16.insets = new Insets(0, 0, 0, 5);
		gbc_btnNewButton_16.gridx = 3;
		gbc_btnNewButton_16.gridy = 9;
		contentPane.add(btnNewButton_16, gbc_btnNewButton_16);*/
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	}

	
}
