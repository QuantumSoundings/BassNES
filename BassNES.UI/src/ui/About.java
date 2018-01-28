package ui;


import ui.settings.UISettings;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;

public class About extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3330730475146139713L;
	private JPanel contentPane;


	/**
	 * Create the frame.
	 */
	public About() {
		setTitle("About - BassNES");
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 290, 228);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("BassNES");
		lblNewLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
		lblNewLabel.setBounds(25, 11, 128, 43);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Version: "+ UISettings.version);
		lblNewLabel_1.setBounds(25, 51, 115, 14);
		contentPane.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("�2017 J. Howe (aka TheBassist95)");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel_2.setBounds(10, 65, 254, 23);
		contentPane.add(lblNewLabel_2);
	}

}
