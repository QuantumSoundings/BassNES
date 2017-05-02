package ui;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class AudioMixerUI extends JFrame {
	private static final long serialVersionUID = 8732673441553439113L;
	private JPanel contentPane;
	SystemUI sys;

	/**
	 * Launch the application.
	 */
	/**
	 * Create the frame.
	 */
	public AudioMixerUI(SystemUI s) {
		setTitle("Audio Mixer");
		sys =s;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JSlider slider = new JSlider();
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setMajorTickSpacing(20);
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				JSlider source = (JSlider) arg0.getSource();
				UserSettings.masterMixLevel=source.getValue();
			}
		});
		slider.setValue(100);
		slider.setOrientation(SwingConstants.VERTICAL);
		slider.setBounds(29, 11, 50, 99);
		contentPane.add(slider);
		
		JSlider slider_1 = new JSlider();
		slider_1.setPaintTicks(true);
		slider_1.setMajorTickSpacing(20);
		slider_1.setPaintLabels(true);
		slider_1.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				UserSettings.pulse1MixLevel=source.getValue();
			}
		});
		slider_1.setValue(100);
		slider_1.setOrientation(SwingConstants.VERTICAL);
		slider_1.setBounds(89, 11, 50, 99);
		contentPane.add(slider_1);
		
		JSlider slider_2 = new JSlider();
		slider_2.setMajorTickSpacing(20);
		slider_2.setPaintTicks(true);
		slider_2.setPaintLabels(true);
		slider_2.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				UserSettings.pulse2MixLevel=source.getValue();
			}
		});
		slider_2.setValue(100);
		slider_2.setOrientation(SwingConstants.VERTICAL);
		slider_2.setBounds(149, 11, 50, 99);
		contentPane.add(slider_2);
		
		JSlider slider_3 = new JSlider();
		slider_3.setMajorTickSpacing(20);
		slider_3.setPaintTicks(true);
		slider_3.setPaintLabels(true);
		slider_3.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				UserSettings.triangleMixLevel=source.getValue();
			}
		});
		slider_3.setValue(100);
		slider_3.setOrientation(SwingConstants.VERTICAL);
		slider_3.setBounds(209, 11, 50, 99);
		contentPane.add(slider_3);
		
		JSlider slider_4 = new JSlider();
		slider_4.setPaintTicks(true);
		slider_4.setPaintLabels(true);
		slider_4.setMajorTickSpacing(20);
		slider_4.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				UserSettings.noiseMixLevel=source.getValue();
			}
		});
		slider_4.setValue(100);
		slider_4.setOrientation(SwingConstants.VERTICAL);
		slider_4.setBounds(269, 11, 50, 99);
		contentPane.add(slider_4);
		
		JSlider slider_5 = new JSlider();
		slider_5.setMajorTickSpacing(20);
		slider_5.setPaintTicks(true);
		slider_5.setPaintLabels(true);
		slider_5.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				UserSettings.dmcMixLevel=source.getValue();
			}
		});
		slider_5.setValue(100);
		slider_5.setOrientation(SwingConstants.VERTICAL);
		slider_5.setBounds(329, 11, 50, 99);
		contentPane.add(slider_5);
		
		JLabel lblMaster = new JLabel("Master");
		lblMaster.setHorizontalAlignment(SwingConstants.CENTER);
		lblMaster.setBounds(29, 121, 46, 14);
		contentPane.add(lblMaster);
		
		JLabel lblPulse = new JLabel("Pulse 1");
		lblPulse.setHorizontalAlignment(SwingConstants.CENTER);
		lblPulse.setBounds(89, 121, 46, 14);
		contentPane.add(lblPulse);
		
		JLabel lblPulse_1 = new JLabel("Pulse 2");
		lblPulse_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblPulse_1.setBounds(149, 121, 46, 14);
		contentPane.add(lblPulse_1);
		
		JLabel lblTriangle = new JLabel("Triangle");
		lblTriangle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTriangle.setBounds(209, 121, 46, 14);
		contentPane.add(lblTriangle);
		
		JLabel lblNoise = new JLabel("Noise");
		lblNoise.setHorizontalAlignment(SwingConstants.CENTER);
		lblNoise.setBounds(269, 121, 46, 14);
		contentPane.add(lblNoise);
		
		JLabel lblDmc = new JLabel("DMC");
		lblDmc.setHorizontalAlignment(SwingConstants.CENTER);
		lblDmc.setBounds(329, 121, 46, 14);
		contentPane.add(lblDmc);
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
	}
}
