package ui;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import javax.swing.event.ChangeListener;

import javax.swing.event.ChangeEvent;
import javax.swing.JTabbedPane;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;

public class AudioSettingsUI extends JFrame {
	private static final long serialVersionUID = 8732673441553439113L;
	final SystemUI sys;

	/**
	 * Launch the application.
	 */
	/**
	 * Create the frame.
	 */
	public AudioSettingsUI(SystemUI s) {
		setTitle("Audio Settings");
		sys =s;
		//noinspection MagicConstant
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 444, 261);
		contentPane.add(tabbedPane);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Settings", null, panel_1, null);
		panel_1.setLayout(null);
		String[] samplingrates =new String[] {"44,100 Hz", "48,000 Hz", "96,000 Hz"};
		JComboBox<String> comboBox = new JComboBox<String>();
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JComboBox<?> cb = (JComboBox<?>)arg0.getSource();
				String s = cb.getSelectedItem().toString();
				int index =Arrays.asList(samplingrates).indexOf(s);
				switch(index){
				case 0: UserSettings.sampleRate = 44100;break;
				case 1: UserSettings.sampleRate = 48000;break;
				case 2: UserSettings.sampleRate = 96000;break;
				}
				sys.resetaudio();	
			}
		});
		comboBox.setModel(new DefaultComboBoxModel<String>(new String[] {"44,100 Hz", "48,000 Hz", "96,000 Hz"}));
		comboBox.setBounds(10, 53, 74, 20);
		panel_1.add(comboBox);
		
		JLabel lblSamplingRate = new JLabel("Sampling Rate");
		lblSamplingRate.setBounds(10, 34, 84, 14);
		panel_1.add(lblSamplingRate);
		
		JCheckBox chckbxLockFrameRate = new JCheckBox("Lock Frame Rate to Audio");
		chckbxLockFrameRate.setBounds(241, 30, 171, 23);
		chckbxLockFrameRate.setSelected(UserSettings.lockVideoToAudio);
		panel_1.add(chckbxLockFrameRate);
		
		JLabel lbloverridesOtherFps = new JLabel("(overrides other FPS settings)");
		lbloverridesOtherFps.setBounds(241, 56, 144, 14);
		panel_1.add(lbloverridesOtherFps);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Mixer", null, panel, null);
		panel.setLayout(null);
		
		JSlider slider = new JSlider();
		slider.setBounds(10, 0, 50, 99);
		panel.add(slider);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setMajorTickSpacing(20);
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				JSlider source = (JSlider) arg0.getSource();
				UserSettings.masterMixLevel=source.getValue();
			}
		});
		slider.setValue(UserSettings.masterMixLevel);
		slider.setOrientation(SwingConstants.VERTICAL);
		
		JLabel lblMaster = new JLabel("Master");
		lblMaster.setBounds(10, 98, 46, 14);
		panel.add(lblMaster);
		lblMaster.setHorizontalAlignment(SwingConstants.CENTER);
		
		JSlider slider_1 = new JSlider();
		slider_1.setBounds(81, 0, 50, 99);
		panel.add(slider_1);
		slider_1.setPaintTicks(true);
		slider_1.setMajorTickSpacing(20);
		slider_1.setPaintLabels(true);
		slider_1.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				UserSettings.pulse1MixLevel=source.getValue();
			}
		});
		slider_1.setValue(UserSettings.pulse1MixLevel);
		slider_1.setOrientation(SwingConstants.VERTICAL);
		
		JLabel lblPulse = new JLabel("Pulse 1");
		lblPulse.setBounds(86, 98, 46, 14);
		panel.add(lblPulse);
		lblPulse.setHorizontalAlignment(SwingConstants.CENTER);
		
		JSlider slider_2 = new JSlider();
		slider_2.setBounds(150, 0, 50, 99);
		panel.add(slider_2);
		slider_2.setMajorTickSpacing(20);
		slider_2.setPaintTicks(true);
		slider_2.setPaintLabels(true);
		slider_2.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				UserSettings.pulse2MixLevel=source.getValue();
			}
		});
		slider_2.setValue(UserSettings.pulse2MixLevel);
		slider_2.setOrientation(SwingConstants.VERTICAL);
		
		JLabel lblPulse_1 = new JLabel("Pulse 2");
		lblPulse_1.setBounds(158, 98, 46, 14);
		panel.add(lblPulse_1);
		lblPulse_1.setHorizontalAlignment(SwingConstants.CENTER);
		
		JSlider slider_3 = new JSlider();
		slider_3.setBounds(228, 0, 50, 99);
		panel.add(slider_3);
		slider_3.setMajorTickSpacing(20);
		slider_3.setPaintTicks(true);
		slider_3.setPaintLabels(true);
		slider_3.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				UserSettings.triangleMixLevel=source.getValue();
			}
		});
		slider_3.setValue(UserSettings.triangleMixLevel);
		slider_3.setOrientation(SwingConstants.VERTICAL);
		
		JLabel lblTriangle = new JLabel("Triangle");
		lblTriangle.setBounds(230, 98, 46, 14);
		panel.add(lblTriangle);
		lblTriangle.setHorizontalAlignment(SwingConstants.CENTER);
		
		JSlider slider_4 = new JSlider();
		slider_4.setBounds(298, 0, 50, 99);
		panel.add(slider_4);
		slider_4.setPaintTicks(true);
		slider_4.setPaintLabels(true);
		slider_4.setMajorTickSpacing(20);
		slider_4.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				UserSettings.noiseMixLevel=source.getValue();
			}
		});
		slider_4.setValue(UserSettings.noiseMixLevel);
		slider_4.setOrientation(SwingConstants.VERTICAL);
		
		JLabel lblNoise = new JLabel("Noise");
		lblNoise.setBounds(296, 98, 46, 14);
		panel.add(lblNoise);
		lblNoise.setHorizontalAlignment(SwingConstants.CENTER);
		
		JSlider slider_5 = new JSlider();
		slider_5.setBounds(359, 0, 50, 99);
		panel.add(slider_5);
		slider_5.setMajorTickSpacing(20);
		slider_5.setPaintTicks(true);
		slider_5.setPaintLabels(true);
		slider_5.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				UserSettings.dmcMixLevel=source.getValue();
			}
		});
		slider_5.setValue(UserSettings.dmcMixLevel);
		slider_5.setOrientation(SwingConstants.VERTICAL);
		
		JLabel lblDmc = new JLabel("DMC");
		lblDmc.setBounds(361, 99, 46, 14);
		panel.add(lblDmc);
		lblDmc.setHorizontalAlignment(SwingConstants.CENTER);
		
		JSlider slider_6 = new JSlider();
		slider_6.setBounds(10, 112, 50, 99);
		panel.add(slider_6);
		slider_6.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				JSlider source = (JSlider) arg0.getSource();
				UserSettings.vrc6MixLevel=source.getValue();
			}
		});
		slider_6.setValue(100);
		slider_6.setPaintTicks(true);
		slider_6.setPaintLabels(true);
		slider_6.setOrientation(SwingConstants.VERTICAL);
		slider_6.setMajorTickSpacing(20);
		
		JLabel lblVrc = new JLabel("VRC6");
		lblVrc.setBounds(10, 208, 46, 14);
		panel.add(lblVrc);
		lblVrc.setHorizontalAlignment(SwingConstants.CENTER);
		
		JSlider slider_7 = new JSlider();
		slider_7.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				JSlider source = (JSlider) arg0.getSource();
				UserSettings.namcoMixLevel=source.getValue();
			}
		});
		slider_7.setValue(100);
		slider_7.setPaintTicks(true);
		slider_7.setPaintLabels(true);
		slider_7.setOrientation(SwingConstants.VERTICAL);
		slider_7.setMajorTickSpacing(20);
		slider_7.setBounds(81, 109, 50, 99);
		panel.add(slider_7);
		
		JLabel lblNamco = new JLabel("Namco");
		lblNamco.setHorizontalAlignment(SwingConstants.CENTER);
		lblNamco.setBounds(81, 208, 46, 14);
		panel.add(lblNamco);
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
	}
}
