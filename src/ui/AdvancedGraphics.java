package ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTabbedPane;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class AdvancedGraphics extends JFrame {

	private JPanel contentPane;

	public AdvancedGraphics() {
		setTitle("Graphics Options");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Advanced", null, panel, null);
		panel.setLayout(null);
		
		JCheckBox chckbxShowBackground = new JCheckBox("Show Background");
		chckbxShowBackground.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				UserSettings.RenderBackground=!UserSettings.RenderBackground;
			}
		});
		chckbxShowBackground.setSelected(true);
		chckbxShowBackground.setBounds(27, 29, 131, 23);
		panel.add(chckbxShowBackground);
		
		JCheckBox chckbxShowSprites = new JCheckBox("Show Sprites");
		chckbxShowSprites.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UserSettings.RenderSprites=!UserSettings.RenderSprites;
			}
		});
		chckbxShowSprites.setSelected(true);
		chckbxShowSprites.setBounds(27, 70, 110, 23);
		panel.add(chckbxShowSprites);
		
		JCheckBox chckbxEnableFrameLimit = new JCheckBox("Enable Frame Limit");
		chckbxEnableFrameLimit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UserSettings.frameLimit=!UserSettings.frameLimit;
			}
		});
		chckbxEnableFrameLimit.setSelected(true);
		chckbxEnableFrameLimit.setBounds(27, 115, 169, 23);
		panel.add(chckbxEnableFrameLimit);
		
		JCheckBox chckbxPoliteFrameTiming = new JCheckBox("Polite Frame Timing");
		chckbxPoliteFrameTiming.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UserSettings.politeFrameTiming=!UserSettings.politeFrameTiming;
			}
		});
		chckbxPoliteFrameTiming.setSelected(true);
		chckbxPoliteFrameTiming.setBounds(27, 167, 169, 23);
		panel.add(chckbxPoliteFrameTiming);
		
		String[] RenderMethods = new String[]{"Standard"};
		JComboBox comboBox = new JComboBox(RenderMethods);
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"Standard"}));
		comboBox.setBounds(260, 30, 101, 20);
		panel.add(comboBox);
		
		JLabel lblRenderMethod = new JLabel("Render Method");
		lblRenderMethod.setHorizontalAlignment(SwingConstants.CENTER);
		lblRenderMethod.setBounds(260, 5, 101, 14);
		panel.add(lblRenderMethod);
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
	}
}
