package ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import java.awt.Insets;
import javax.swing.JPopupMenu;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.SwingConstants;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ControlUI extends JFrame {

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
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 153, 0, 0, 0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JLabel lblController = new JLabel("Controller 1");
		GridBagConstraints gbc_lblController = new GridBagConstraints();
		gbc_lblController.insets = new Insets(0, 0, 5, 5);
		gbc_lblController.gridx = 2;
		gbc_lblController.gridy = 0;
		contentPane.add(lblController, gbc_lblController);
		
		JLabel lblController_1 = new JLabel("Controller 2");
		GridBagConstraints gbc_lblController_1 = new GridBagConstraints();
		gbc_lblController_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblController_1.gridx = 5;
		gbc_lblController_1.gridy = 0;
		contentPane.add(lblController_1, gbc_lblController_1);
		
		JLabel lblAButton = new JLabel("A Button");
		GridBagConstraints gbc_lblAButton = new GridBagConstraints();
		gbc_lblAButton.insets = new Insets(0, 0, 5, 5);
		gbc_lblAButton.gridx = 1;
		gbc_lblAButton.gridy = 1;
		contentPane.add(lblAButton, gbc_lblAButton);
		
		JButton btnNewButton = new JButton("<"+KeyEvent.getKeyText(Integer.parseInt(prop.getProperty("c1a")))+">");
		btnNewButton.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				btnNewButton.setText("<"+KeyEvent.getKeyText(arg0.getKeyCode())+">");
				prop.setProperty("c1a",arg0.getKeyCode()+"");
			}
		});
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				awaitingkey=true;
			}
		});
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton.gridx = 2;
		gbc_btnNewButton.gridy = 1;
		contentPane.add(btnNewButton, gbc_btnNewButton);
		
		JLabel lblAButton_1 = new JLabel("A Button");
		GridBagConstraints gbc_lblAButton_1 = new GridBagConstraints();
		gbc_lblAButton_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblAButton_1.gridx = 4;
		gbc_lblAButton_1.gridy = 1;
		contentPane.add(lblAButton_1, gbc_lblAButton_1);
		
		JButton btnNewButton_8 = new JButton("<"+KeyEvent.getKeyText(Integer.parseInt(prop.getProperty("c2a")))+">");
		btnNewButton_8.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				btnNewButton_8.setText("<"+KeyEvent.getKeyText(e.getKeyCode())+">");
				prop.setProperty("c2a",e.getKeyCode()+"");
			}
		});
		GridBagConstraints gbc_btnNewButton_8 = new GridBagConstraints();
		gbc_btnNewButton_8.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton_8.gridx = 5;
		gbc_btnNewButton_8.gridy = 1;
		contentPane.add(btnNewButton_8, gbc_btnNewButton_8);
		
		JLabel lblBButton = new JLabel("B Button");
		GridBagConstraints gbc_lblBButton = new GridBagConstraints();
		gbc_lblBButton.insets = new Insets(0, 0, 5, 5);
		gbc_lblBButton.gridx = 1;
		gbc_lblBButton.gridy = 2;
		contentPane.add(lblBButton, gbc_lblBButton);
		
		JButton btnNewButton_1 = new JButton("<"+KeyEvent.getKeyText(Integer.parseInt(prop.getProperty("c1b")))+">");
		btnNewButton_1.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				btnNewButton_1.setText("<"+KeyEvent.getKeyText(arg0.getKeyCode())+">");
				prop.setProperty("c1b",arg0.getKeyCode()+"");
			}
		});
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				awaitingkey=true;
			}
		});
		GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
		gbc_btnNewButton_1.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton_1.gridx = 2;
		gbc_btnNewButton_1.gridy = 2;
		contentPane.add(btnNewButton_1, gbc_btnNewButton_1);
		
		JLabel lblBButton_1 = new JLabel("B Button");
		GridBagConstraints gbc_lblBButton_1 = new GridBagConstraints();
		gbc_lblBButton_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblBButton_1.gridx = 4;
		gbc_lblBButton_1.gridy = 2;
		contentPane.add(lblBButton_1, gbc_lblBButton_1);
		
		JButton btnNewButton_9 = new JButton("<"+KeyEvent.getKeyText(Integer.parseInt(prop.getProperty("c2b")))+">");
		btnNewButton_9.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				btnNewButton_9.setText("<"+KeyEvent.getKeyText(e.getKeyCode())+">");
				prop.setProperty("c2b",e.getKeyCode()+"");
			}
		});
		GridBagConstraints gbc_btnNewButton_9 = new GridBagConstraints();
		gbc_btnNewButton_9.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton_9.gridx = 5;
		gbc_btnNewButton_9.gridy = 2;
		contentPane.add(btnNewButton_9, gbc_btnNewButton_9);
		
		JLabel lblUpdpad = new JLabel("Up");
		GridBagConstraints gbc_lblUpdpad = new GridBagConstraints();
		gbc_lblUpdpad.insets = new Insets(0, 0, 5, 5);
		gbc_lblUpdpad.gridx = 1;
		gbc_lblUpdpad.gridy = 3;
		contentPane.add(lblUpdpad, gbc_lblUpdpad);
		
		JButton btnNewButton_2 = new JButton("<"+KeyEvent.getKeyText(Integer.parseInt(prop.getProperty("c1up")))+">");
		btnNewButton_2.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				btnNewButton_2.setText("<"+KeyEvent.getKeyText(e.getKeyCode())+">");
				prop.setProperty("c1up",e.getKeyCode()+"");
			}
		});
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				awaitingkey=true;
			}
		});
		GridBagConstraints gbc_btnNewButton_2 = new GridBagConstraints();
		gbc_btnNewButton_2.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton_2.gridx = 2;
		gbc_btnNewButton_2.gridy = 3;
		contentPane.add(btnNewButton_2, gbc_btnNewButton_2);
		
		JButton btnNewButton_10 = new JButton("<"+KeyEvent.getKeyText(Integer.parseInt(prop.getProperty("c2up")))+">");
		btnNewButton_10.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				btnNewButton_10.setText("<"+KeyEvent.getKeyText(e.getKeyCode())+">");
				prop.setProperty("c2up",e.getKeyCode()+"");
			}
		});
		
		JLabel lblUp = new JLabel("Up");
		GridBagConstraints gbc_lblUp = new GridBagConstraints();
		gbc_lblUp.insets = new Insets(0, 0, 5, 5);
		gbc_lblUp.gridx = 4;
		gbc_lblUp.gridy = 3;
		contentPane.add(lblUp, gbc_lblUp);
		GridBagConstraints gbc_btnNewButton_10 = new GridBagConstraints();
		gbc_btnNewButton_10.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton_10.gridx = 5;
		gbc_btnNewButton_10.gridy = 3;
		contentPane.add(btnNewButton_10, gbc_btnNewButton_10);
		
		JLabel lblDown = new JLabel("Down");
		GridBagConstraints gbc_lblDown = new GridBagConstraints();
		gbc_lblDown.insets = new Insets(0, 0, 5, 5);
		gbc_lblDown.gridx = 1;
		gbc_lblDown.gridy = 4;
		contentPane.add(lblDown, gbc_lblDown);
		
		JButton btnNewButton_3 = new JButton("<"+KeyEvent.getKeyText(Integer.parseInt(prop.getProperty("c1down")))+">");
		btnNewButton_3.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				btnNewButton_3.setText("<"+KeyEvent.getKeyText(e.getKeyCode())+">");
				prop.setProperty("c1down",e.getKeyCode()+"");
			}
		});
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				awaitingkey=true;
			}
		});
		GridBagConstraints gbc_btnNewButton_3 = new GridBagConstraints();
		gbc_btnNewButton_3.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton_3.gridx = 2;
		gbc_btnNewButton_3.gridy = 4;
		contentPane.add(btnNewButton_3, gbc_btnNewButton_3);
		
		JButton btnNewButton_4 = new JButton("<"+KeyEvent.getKeyText(Integer.parseInt(prop.getProperty("c1left")))+">");
		btnNewButton_4.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				btnNewButton_4.setText("<"+KeyEvent.getKeyText(e.getKeyCode())+">");
				prop.setProperty("c1left",e.getKeyCode()+"");
			}
		});
		btnNewButton_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				awaitingkey=true;
			}
		});
		
		JLabel lblDown_1 = new JLabel("Down");
		GridBagConstraints gbc_lblDown_1 = new GridBagConstraints();
		gbc_lblDown_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblDown_1.gridx = 4;
		gbc_lblDown_1.gridy = 4;
		contentPane.add(lblDown_1, gbc_lblDown_1);
		
		JButton btnNewButton_11 = new JButton("<"+KeyEvent.getKeyText(Integer.parseInt(prop.getProperty("c2down")))+">");
		btnNewButton_11.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				btnNewButton_11.setText("<"+KeyEvent.getKeyText(e.getKeyCode())+">");
				prop.setProperty("c2down",e.getKeyCode()+"");
			}
		});
		GridBagConstraints gbc_btnNewButton_11 = new GridBagConstraints();
		gbc_btnNewButton_11.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton_11.gridx = 5;
		gbc_btnNewButton_11.gridy = 4;
		contentPane.add(btnNewButton_11, gbc_btnNewButton_11);
		
		JLabel lblLeft = new JLabel("Left");
		GridBagConstraints gbc_lblLeft = new GridBagConstraints();
		gbc_lblLeft.insets = new Insets(0, 0, 5, 5);
		gbc_lblLeft.gridx = 1;
		gbc_lblLeft.gridy = 5;
		contentPane.add(lblLeft, gbc_lblLeft);
		GridBagConstraints gbc_btnNewButton_4 = new GridBagConstraints();
		gbc_btnNewButton_4.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton_4.gridx = 2;
		gbc_btnNewButton_4.gridy = 5;
		contentPane.add(btnNewButton_4, gbc_btnNewButton_4);
		
		JButton btnNewButton_12 = new JButton("<"+KeyEvent.getKeyText(Integer.parseInt(prop.getProperty("c2left")))+">");
		btnNewButton_12.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				btnNewButton_12.setText("<"+KeyEvent.getKeyText(e.getKeyCode())+">");
				prop.setProperty("c2left",e.getKeyCode()+"");
			}
		});
		
		JLabel lblLeft_1 = new JLabel("Left");
		GridBagConstraints gbc_lblLeft_1 = new GridBagConstraints();
		gbc_lblLeft_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblLeft_1.gridx = 4;
		gbc_lblLeft_1.gridy = 5;
		contentPane.add(lblLeft_1, gbc_lblLeft_1);
		GridBagConstraints gbc_btnNewButton_12 = new GridBagConstraints();
		gbc_btnNewButton_12.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton_12.gridx = 5;
		gbc_btnNewButton_12.gridy = 5;
		contentPane.add(btnNewButton_12, gbc_btnNewButton_12);
		
		JLabel lblRight = new JLabel("Right");
		GridBagConstraints gbc_lblRight = new GridBagConstraints();
		gbc_lblRight.insets = new Insets(0, 0, 5, 5);
		gbc_lblRight.gridx = 1;
		gbc_lblRight.gridy = 6;
		contentPane.add(lblRight, gbc_lblRight);
		
		JButton btnNewButton_5 = new JButton("<"+KeyEvent.getKeyText(Integer.parseInt(prop.getProperty("c1right")))+">");
		btnNewButton_5.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				btnNewButton_5.setText("<"+KeyEvent.getKeyText(e.getKeyCode())+">");
				prop.setProperty("c1right",e.getKeyCode()+"");
			}
		});
		btnNewButton_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				awaitingkey=true;
			}
		});
		GridBagConstraints gbc_btnNewButton_5 = new GridBagConstraints();
		gbc_btnNewButton_5.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton_5.gridx = 2;
		gbc_btnNewButton_5.gridy = 6;
		contentPane.add(btnNewButton_5, gbc_btnNewButton_5);
		
		JLabel lblRight_1 = new JLabel("Right");
		GridBagConstraints gbc_lblRight_1 = new GridBagConstraints();
		gbc_lblRight_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblRight_1.gridx = 4;
		gbc_lblRight_1.gridy = 6;
		contentPane.add(lblRight_1, gbc_lblRight_1);
		
		JButton btnNewButton_13 = new JButton("<"+KeyEvent.getKeyText(Integer.parseInt(prop.getProperty("c2right")))+">");
		btnNewButton_13.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				btnNewButton_13.setText("<"+KeyEvent.getKeyText(e.getKeyCode())+">");
				prop.setProperty("c2right",e.getKeyCode()+"");
			}
		});
		GridBagConstraints gbc_btnNewButton_13 = new GridBagConstraints();
		gbc_btnNewButton_13.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton_13.gridx = 5;
		gbc_btnNewButton_13.gridy = 6;
		contentPane.add(btnNewButton_13, gbc_btnNewButton_13);
		
		JLabel lblStart = new JLabel("Start");
		GridBagConstraints gbc_lblStart = new GridBagConstraints();
		gbc_lblStart.insets = new Insets(0, 0, 5, 5);
		gbc_lblStart.gridx = 1;
		gbc_lblStart.gridy = 7;
		contentPane.add(lblStart, gbc_lblStart);
		
		JButton btnNewButton_6 = new JButton("<"+KeyEvent.getKeyText(Integer.parseInt(prop.getProperty("c1start")))+">");
		btnNewButton_6.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				btnNewButton_6.setText("<"+KeyEvent.getKeyText(e.getKeyCode())+">");
				prop.setProperty("c1start",e.getKeyCode()+"");
			}
		});
		btnNewButton_6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				awaitingkey=true;
			}
		});
		GridBagConstraints gbc_btnNewButton_6 = new GridBagConstraints();
		gbc_btnNewButton_6.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton_6.gridx = 2;
		gbc_btnNewButton_6.gridy = 7;
		contentPane.add(btnNewButton_6, gbc_btnNewButton_6);
		
		JButton btnNewButton_14 = new JButton("<"+KeyEvent.getKeyText(Integer.parseInt(prop.getProperty("c2start")))+">");
		btnNewButton_14.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				btnNewButton_14.setText("<"+KeyEvent.getKeyText(e.getKeyCode())+">");
				prop.setProperty("c2start",e.getKeyCode()+"");
			}
		});
		
		JLabel lblStart_1 = new JLabel("Start");
		GridBagConstraints gbc_lblStart_1 = new GridBagConstraints();
		gbc_lblStart_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblStart_1.gridx = 4;
		gbc_lblStart_1.gridy = 7;
		contentPane.add(lblStart_1, gbc_lblStart_1);
		GridBagConstraints gbc_btnNewButton_14 = new GridBagConstraints();
		gbc_btnNewButton_14.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton_14.gridx = 5;
		gbc_btnNewButton_14.gridy = 7;
		contentPane.add(btnNewButton_14, gbc_btnNewButton_14);
		
		JLabel lblSelect = new JLabel("Select");
		GridBagConstraints gbc_lblSelect = new GridBagConstraints();
		gbc_lblSelect.insets = new Insets(0, 0, 5, 5);
		gbc_lblSelect.gridx = 1;
		gbc_lblSelect.gridy = 8;
		contentPane.add(lblSelect, gbc_lblSelect);
		
		JButton btnNewButton_7 = new JButton("<"+KeyEvent.getKeyText(Integer.parseInt(prop.getProperty("c1select")))+">");
		btnNewButton_7.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				btnNewButton_7.setText("<"+KeyEvent.getKeyText(e.getKeyCode())+">");
				prop.setProperty("c1select",e.getKeyCode()+"");
			}
		});
		btnNewButton_7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				awaitingkey=true;
			}
		});
		GridBagConstraints gbc_btnNewButton_7 = new GridBagConstraints();
		gbc_btnNewButton_7.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton_7.gridx = 2;
		gbc_btnNewButton_7.gridy = 8;
		contentPane.add(btnNewButton_7, gbc_btnNewButton_7);
		
		JButton btnNewButton_15 = new JButton("<"+KeyEvent.getKeyText(Integer.parseInt(prop.getProperty("c2select")))+">");
		btnNewButton_15.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				btnNewButton_15.setText("<"+KeyEvent.getKeyText(e.getKeyCode())+">");
				prop.setProperty("c2select",e.getKeyCode()+"");
			}
		});
		
		JLabel lblSelect_1 = new JLabel("Select");
		GridBagConstraints gbc_lblSelect_1 = new GridBagConstraints();
		gbc_lblSelect_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblSelect_1.gridx = 4;
		gbc_lblSelect_1.gridy = 8;
		contentPane.add(lblSelect_1, gbc_lblSelect_1);
		GridBagConstraints gbc_btnNewButton_15 = new GridBagConstraints();
		gbc_btnNewButton_15.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton_15.gridx = 5;
		gbc_btnNewButton_15.gridy = 8;
		contentPane.add(btnNewButton_15, gbc_btnNewButton_15);
		
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

	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}
