package ui;


import java.awt.*;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import javax.swing.event.ChangeListener;

import core.NesSettings;

import javax.swing.event.ChangeEvent;
import javax.swing.JTabbedPane;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.awt.Color;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLayeredPane;
import javax.swing.JSpinner;

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
		String[] samplingrates =new String[] {"8,000 Hz","11,025 Hz","22,050 Hz","44,100 Hz", "48,000 Hz", "96,000 Hz","192,000 Hz"};
		int[] sampleratesints = new int[]{8000,11025,22050,44100,48000,96000,192000};
		JComboBox<String> comboBox = new JComboBox<String>();
		comboBox.setModel(new DefaultComboBoxModel<String>(samplingrates));
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JComboBox<String> cb = (JComboBox<String>)arg0.getSource();
				NesSettings.sampleRate=sampleratesints[cb.getSelectedIndex()];
				if(sys.nes!=null)
					sys.nes.setSampleRate(NesSettings.sampleRate);
				sys.resetaudio();	
			}
		});
		comboBox.setBounds(10, 53, 84, 20);
		panel_1.add(comboBox);
		for(int i = 0;i<sampleratesints.length;i++){
			if(sampleratesints[i]==NesSettings.sampleRate)
				comboBox.setSelectedIndex(i);
		}

		
		JLabel lblSamplingRate = new JLabel("Sampling Rate");
		lblSamplingRate.setBounds(10, 34, 84, 14);
		panel_1.add(lblSamplingRate);
		
		JCheckBox chckbxLockFrameRate = new JCheckBox("Lock Frame Rate to Audio");
		chckbxLockFrameRate.setBounds(241, 30, 171, 23);
		chckbxLockFrameRate.setSelected(UISettings.lockVideoToAudio);
		panel_1.add(chckbxLockFrameRate);


		
		JLabel lbloverridesOtherFps = new JLabel("(overrides other FPS settings)");
		lbloverridesOtherFps.setBounds(241, 56, 144, 14);
		panel_1.add(lbloverridesOtherFps);

		JCheckBox highQualitySamplingBox = new JCheckBox("Enable High Quality Audio Sampling");
		highQualitySamplingBox.setBounds(241,90,171,23);
		highQualitySamplingBox.setSelected(NesSettings.highQualitySampling);
		panel_1.add(highQualitySamplingBox);
		
		JLabel lblAudioLatency = new JLabel("Audio Latency");
		lblAudioLatency.setBounds(10, 94, 77, 14);
		panel_1.add(lblAudioLatency);
		
		JSpinner spinner = new JSpinner();
		spinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSpinner spin = (JSpinner)e.getSource();
				int value =(int) spin.getValue();
				NesSettings.audioBufferSize = value;
				sys.resetaudio();
				if(sys.nes!=null)
					sys.nes.setSampleRate(NesSettings.sampleRate);
			}
		});
		spinner.setValue(NesSettings.audioBufferSize);
		spinner.setBounds(84, 91, 43, 20);
		panel_1.add(spinner);
		
		JLabel lblMs = new JLabel("ms");
		lblMs.setBounds(132, 94, 46, 14);
		panel_1.add(lblMs);
		

		
		highQualitySamplingBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				NesSettings.highQualitySampling=highQualitySamplingBox.isSelected();
			}
		});
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
				NesSettings.masterMixLevel=source.getValue();
			}
		});
		slider.setValue(NesSettings.masterMixLevel);
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
				NesSettings.pulse1MixLevel=source.getValue();
			}
		});
		slider_1.setValue(NesSettings.pulse1MixLevel);
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
				NesSettings.pulse2MixLevel=source.getValue();
			}
		});
		slider_2.setValue(NesSettings.pulse2MixLevel);
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
				NesSettings.triangleMixLevel=source.getValue();
			}
		});
		slider_3.setValue(NesSettings.triangleMixLevel);
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
				NesSettings.noiseMixLevel=source.getValue();
			}
		});
		slider_4.setValue(NesSettings.noiseMixLevel);
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
				NesSettings.dmcMixLevel=source.getValue();
			}
		});
		slider_5.setValue(NesSettings.dmcMixLevel);
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
				NesSettings.vrc6MixLevel=source.getValue();
			}
		});
		slider_6.setValue(NesSettings.vrc6MixLevel);
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
				NesSettings.namcoMixLevel=source.getValue();
			}
		});
		slider_7.setValue(NesSettings.namcoMixLevel);
		slider_7.setPaintTicks(true);
		slider_7.setPaintLabels(true);
		slider_7.setOrientation(SwingConstants.VERTICAL);
		slider_7.setMajorTickSpacing(20);
		slider_7.setBounds(81, 112, 50, 99);
		panel.add(slider_7);
		
		JLabel lblNamco = new JLabel("Namco");
		lblNamco.setHorizontalAlignment(SwingConstants.CENTER);
		lblNamco.setBounds(81, 208, 46, 14);
		panel.add(lblNamco);
		
		JSlider slider_8 = new JSlider();
		slider_8.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				JSlider source = (JSlider) arg0.getSource();
				NesSettings.mmc5MixLevel=source.getValue();
			}
		});
		slider_8.setValue(NesSettings.mmc5MixLevel);
		slider_8.setPaintTicks(true);
		slider_8.setPaintLabels(true);
		slider_8.setOrientation(SwingConstants.VERTICAL);
		slider_8.setMajorTickSpacing(20);
		slider_8.setBounds(150, 112, 50, 99);
		panel.add(slider_8);
		
		JLabel lblMmc = new JLabel("MMC5");
		lblMmc.setHorizontalAlignment(SwingConstants.CENTER);
		lblMmc.setBounds(154, 208, 46, 14);
		panel.add(lblMmc);
		
		JSlider slider_9 = new JSlider();
		slider_9.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				JSlider source = (JSlider) arg0.getSource();
				NesSettings.sunsoft5BMixLevel=source.getValue();
			}
		});
		slider_9.setValue(NesSettings.sunsoft5BMixLevel);
		slider_9.setPaintTicks(true);
		slider_9.setPaintLabels(true);
		slider_9.setOrientation(SwingConstants.VERTICAL);
		slider_9.setMajorTickSpacing(20);
		slider_9.setBounds(228, 109, 50, 99);
		panel.add(slider_9);
		
		JLabel lblSunsoftb = new JLabel("SunSoft5B");
		lblSunsoftb.setBounds(228, 208, 50, 14);
		panel.add(lblSunsoftb);
		
		JPanel panel_4 = new JPanel();
		tabbedPane.addTab("Panning", null, panel_4, null);
		panel_4.setLayout(null);
		
		JSlider slider_10 = new JSlider();
		slider_10.setMinimum(-100);
		slider_10.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				JSlider source = (JSlider) arg0.getSource();
				NesSettings.pulse1Panning=source.getValue();
			}
		});

		slider_10.setValue(NesSettings.pulse1Panning);
		slider_10.setMajorTickSpacing(20);
		slider_10.setPaintTicks(true);

		slider_10.setBounds(10, 27, 146, 26);
		panel_4.add(slider_10);
		
		JSlider slider_11 = new JSlider();
		slider_11.setMinimum(-100);
		slider_11.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				NesSettings.pulse2Panning=source.getValue();
			}
		});

		slider_11.setValue(NesSettings.pulse2Panning);
		slider_11.setPaintTicks(true);

		slider_11.setMajorTickSpacing(20);
		slider_11.setBounds(10, 70, 146, 26);
		panel_4.add(slider_11);
		
		JSlider slider_12 = new JSlider();
		slider_12.setMinimum(-100);
		slider_12.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				NesSettings.trianglePanning=source.getValue();
			}
		});

		slider_12.setValue(NesSettings.trianglePanning);
		slider_12.setPaintTicks(true);

		slider_12.setMajorTickSpacing(20);
		slider_12.setBounds(10, 110, 146, 26);
		panel_4.add(slider_12);
		
		JSlider slider_13 = new JSlider();
		slider_13.setMinimum(-100);
		slider_13.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				NesSettings.noisePanning=source.getValue();
			}
		});

		slider_13.setValue(NesSettings.noisePanning);
		slider_13.setPaintTicks(true);

		slider_13.setMajorTickSpacing(20);
		slider_13.setBounds(10, 150, 146, 26);
		panel_4.add(slider_13);
		
		JSlider slider_14 = new JSlider();
		slider_14.setSnapToTicks(true);
		slider_14.setMinimum(-100);
		slider_14.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				NesSettings.dmcPanning=source.getValue();
			}
		});

		slider_14.setValue(NesSettings.dmcPanning);
		slider_14.setPaintTicks(true);

		slider_14.setMajorTickSpacing(20);
		slider_14.setBounds(10, 190, 146, 26);
		panel_4.add(slider_14);
		
		JLabel lblPulse_4 = new JLabel("Pulse1");
		lblPulse_4.setHorizontalAlignment(SwingConstants.CENTER);
		lblPulse_4.setBounds(60, 11, 46, 14);
		panel_4.add(lblPulse_4);
		
		JLabel lblPulse_5 = new JLabel("Pulse 2");
		lblPulse_5.setHorizontalAlignment(SwingConstants.CENTER);
		lblPulse_5.setBounds(60, 53, 46, 14);
		panel_4.add(lblPulse_5);
		
		JLabel lblTriangle_2 = new JLabel("Triangle");
		lblTriangle_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblTriangle_2.setBounds(60, 95, 46, 14);
		panel_4.add(lblTriangle_2);
		
		JLabel lblNoise_1 = new JLabel("Noise");
		lblNoise_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNoise_1.setBounds(60, 135, 46, 14);
		panel_4.add(lblNoise_1);
		
		JLabel lblDmc_1 = new JLabel("DMC");
		lblDmc_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblDmc_1.setBounds(60, 175, 46, 14);
		panel_4.add(lblDmc_1);
		
		JSlider slider_15 = new JSlider();
		slider_15.setMinimum(-100);
		slider_15.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				NesSettings.vrc6Panning=source.getValue();
			}
		});

		slider_15.setValue(NesSettings.vrc6Panning);
		slider_15.setPaintTicks(true);

		slider_15.setMajorTickSpacing(20);
		slider_15.setBounds(243, 27, 146, 26);
		panel_4.add(slider_15);
		
		JSlider slider_16 = new JSlider();
		slider_16.setMinimum(-100);
		slider_16.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				JSlider source = (JSlider) arg0.getSource();
				NesSettings.namcoPanning=source.getValue();
			}
		});

		slider_16.setValue(NesSettings.namcoPanning);
		slider_16.setPaintTicks(true);

		slider_16.setMajorTickSpacing(20);
		slider_16.setBounds(243, 70, 146, 26);
		panel_4.add(slider_16);
		
		JLabel lblNewLabel_2 = new JLabel("VRC6");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2.setBounds(292, 11, 46, 14);
		panel_4.add(lblNewLabel_2);
		
		JLabel lblNewLabel_3 = new JLabel("Namco");
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_3.setBounds(292, 53, 46, 14);
		panel_4.add(lblNewLabel_3);
		
		JSlider slider_17 = new JSlider();
		slider_17.setMinimum(-100);
		slider_17.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				NesSettings.mmc5Panning=source.getValue();
			}
		});

		slider_17.setValue(NesSettings.mmc5Panning);
		slider_17.setPaintTicks(true);

		slider_17.setMajorTickSpacing(20);
		slider_17.setBounds(243, 110, 146, 26);
		panel_4.add(slider_17);
		
		JSlider slider_18 = new JSlider();
		slider_18.setMinimum(-100);
		slider_18.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				NesSettings.sunsoft5BPanning=source.getValue();
			}
		});

		slider_18.setValue(NesSettings.sunsoft5BPanning);
		slider_18.setPaintTicks(true);

		slider_18.setMajorTickSpacing(20);
		slider_18.setBounds(243, 150, 146, 26);
		panel_4.add(slider_18);
		
		JLabel lblNewLabel_4 = new JLabel("MMC5");
		lblNewLabel_4.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_4.setBounds(292, 95, 46, 14);
		panel_4.add(lblNewLabel_4);
		
		JLabel lblNewLabel_5 = new JLabel("SunSoft5B");
		lblNewLabel_5.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_5.setBounds(290, 135, 60, 14);
		panel_4.add(lblNewLabel_5);
		
		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("Visualizer", null, panel_2, null);
		panel_2.setLayout(null);
		
		JLabel lblPianoColors = new JLabel("Piano Colors");
		lblPianoColors.setBounds(31, 4, 67, 14);
		panel_2.add(lblPianoColors);
		
		JLabel lblPulse_2 = new JLabel("VRC6 Pulse 1");
		lblPulse_2.setBounds(10, 85, 67, 14);
		panel_2.add(lblPulse_2);
		
		JLabel lblPulse_3 = new JLabel("Pulse 2");
		lblPulse_3.setBounds(12, 38, 46, 14);
		panel_2.add(lblPulse_3);
		
		JLabel lblTriangle_1 = new JLabel("Triangle");
		lblTriangle_1.setBounds(10, 58, 46, 14);
		panel_2.add(lblTriangle_1);
		
		JLabel label_2 = new JLabel("Pulse 1");
		label_2.setBounds(11, 17, 46, 14);
		panel_2.add(label_2);
		
		JLabel lblVrcPulse = new JLabel("VRC6 Pulse 2");
		lblVrcPulse.setBounds(10, 109, 63, 14);
		panel_2.add(lblVrcPulse);
		
		JLabel lblVrcSaw = new JLabel("VRC6 Saw");
		lblVrcSaw.setBounds(13, 134, 49, 14);
		panel_2.add(lblVrcSaw);
		
		JPanel pulse1color = new JPanel();
		pulse1color.setBackground(UISettings.pianoColors[Arrays.asList(UISettings.channelNames).indexOf("Pulse 1")]);
		pulse1color.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				Color update = setColor("Pulse 1");
				pulse1color.setBackground(update);
				pulse1color.repaint();
			}
		});
		pulse1color.setBounds(95, 11, 20, 20);
		panel_2.add(pulse1color);
		
		JPanel pulse2color = new JPanel();
		pulse2color.setBackground(UISettings.pianoColors[Arrays.asList(UISettings.channelNames).indexOf("Pulse 2")]);

		pulse2color.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				Color update = setColor("Pulse 2");
				pulse2color.setBackground(update);
				pulse2color.repaint();
			}
		});
		pulse2color.setBounds(95, 36, 20, 20);
		panel_2.add(pulse2color);
		
		JPanel trianglecolor = new JPanel();
		trianglecolor.setBackground(UISettings.pianoColors[Arrays.asList(UISettings.channelNames).indexOf("Triangle")]);
		trianglecolor.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Color update = setColor("Triangle");
				trianglecolor.setBackground(update);
				trianglecolor.repaint();
			}
		});
		trianglecolor.setBounds(95, 61, 20, 20);
		panel_2.add(trianglecolor);
		
		JPanel vrc6pulse1color = new JPanel();
		vrc6pulse1color.setBackground(UISettings.pianoColors[Arrays.asList(UISettings.channelNames).indexOf("VRC6 Pulse 1")]);

		vrc6pulse1color.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				Color update = setColor("VRC6 Pulse 1");
				vrc6pulse1color.setBackground(update);
				vrc6pulse1color.repaint();
			}
		});
		vrc6pulse1color.setBounds(95, 86, 20, 20);
		panel_2.add(vrc6pulse1color);
		
		JPanel vrc6pulse2color = new JPanel();
		vrc6pulse2color.setBackground(UISettings.pianoColors[Arrays.asList(UISettings.channelNames).indexOf("VRC6 Pulse 2")]);

		vrc6pulse2color.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				Color update = setColor("VRC6 Pulse 2");
				vrc6pulse2color.setBackground(update);
				vrc6pulse2color.repaint();
			}
		});
		vrc6pulse2color.setBounds(95, 109, 20, 20);
		panel_2.add(vrc6pulse2color);
		
		JPanel vrc6sawcolor = new JPanel();
		vrc6sawcolor.setBackground(UISettings.pianoColors[Arrays.asList(UISettings.channelNames).indexOf("VRC6 Saw")]);

		vrc6sawcolor.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				Color update = setColor("VRC6 Saw");
				vrc6sawcolor.setBackground(update);
				vrc6sawcolor.repaint();
			}
		});
		vrc6sawcolor.setBounds(95, 134, 20, 20);
		panel_2.add(vrc6sawcolor);
		
		JPanel namcocolor = new JPanel();
		namcocolor.setBackground(UISettings.pianoColors[Arrays.asList(UISettings.channelNames).indexOf("N_Channel 0")]);
		namcocolor.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				Color update = setColor("Namco");
				namcocolor.setBackground(update);
				namcocolor.repaint();
			}
		});
		namcocolor.setBounds(95, 158, 20, 20);
		panel_2.add(namcocolor);
		
		JLabel lblNewLabel = new JLabel("Namco");
		lblNewLabel.setBounds(15, 158, 46, 14);
		panel_2.add(lblNewLabel);
		
		
		
		JCheckBox chckbxAllGreen = new JCheckBox("All Green");
		chckbxAllGreen.setSelected(UISettings.allGreen);
		chckbxAllGreen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UISettings.allGreen=!UISettings.allGreen;
			}
		});
		chckbxAllGreen.setBounds(157, 57, 77, 23);
		panel_2.add(chckbxAllGreen);
		
		JPanel mmc5pulse1color = new JPanel();
		mmc5pulse1color.setBackground(UISettings.pianoColors[Arrays.asList(UISettings.channelNames).indexOf("MMC5 Pulse 1")]);
		mmc5pulse1color.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				Color update = setColor("MMC5 Pulse 1");
				mmc5pulse1color.setBackground(update);
				mmc5pulse1color.repaint();
			}
		});
		mmc5pulse1color.setBackground((Color) null);
		mmc5pulse1color.setBounds(95, 184, 20, 20);
		panel_2.add(mmc5pulse1color);
		
		JPanel mmc5pulse2color = new JPanel();
		mmc5pulse2color.setBackground(UISettings.pianoColors[Arrays.asList(UISettings.channelNames).indexOf("MMC5 Pulse 2")]);
		mmc5pulse2color.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				Color update = setColor("MMC5 Pulse 2");
				mmc5pulse2color.setBackground(update);
				mmc5pulse2color.repaint();
			}
		});
		mmc5pulse2color.setBackground((Color) null);
		mmc5pulse2color.setBounds(96, 208, 20, 20);
		panel_2.add(mmc5pulse2color);
		
		JLabel lblMmcPulse = new JLabel("MMC5 Pulse 1");
		lblMmcPulse.setBounds(10, 183, 66, 14);
		panel_2.add(lblMmcPulse);
		
		JLabel lblMmcPulse_1 = new JLabel("MMC5 Pulse 2");
		lblMmcPulse_1.setBounds(10, 208, 66, 14);
		panel_2.add(lblMmcPulse_1);
		
		JCheckBox chckbxColorBlindMode = new JCheckBox("Color Blind Mode");
		chckbxColorBlindMode.setSelected(UISettings.colorBlindMode);
		chckbxColorBlindMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UISettings.colorBlindMode = !UISettings.colorBlindMode;
				if(UISettings.colorBlindMode){
					pulse1color.setBackground(UISettings.pianoColorsColorBlind[0]);
					pulse1color.repaint();
					pulse2color.setBackground(UISettings.pianoColorsColorBlind[1]);
					pulse2color.repaint();
					trianglecolor.setBackground(UISettings.pianoColorsColorBlind[2]);
					trianglecolor.repaint();
					vrc6pulse1color.setBackground(UISettings.pianoColorsColorBlind[3]);
					vrc6pulse1color.repaint();
					vrc6pulse2color.setBackground(UISettings.pianoColorsColorBlind[4]);
					vrc6pulse2color.repaint();
					vrc6sawcolor.setBackground(UISettings.pianoColorsColorBlind[5]);
					vrc6sawcolor.repaint();
					mmc5pulse1color.setBackground(UISettings.pianoColorsColorBlind[6]);
					mmc5pulse1color.repaint();
					mmc5pulse2color.setBackground(UISettings.pianoColorsColorBlind[7]);
					mmc5pulse2color.repaint();
					namcocolor.setBackground(UISettings.pianoColorsColorBlind[8]);
					namcocolor.repaint();
				}
				else{
					pulse1color.setBackground(UISettings.pianoColors[0]);
					pulse1color.repaint();
					pulse2color.setBackground(UISettings.pianoColors[1]);
					pulse2color.repaint();
					trianglecolor.setBackground(UISettings.pianoColors[2]);
					trianglecolor.repaint();
					vrc6pulse1color.setBackground(UISettings.pianoColors[3]);
					vrc6pulse1color.repaint();
					vrc6pulse2color.setBackground(UISettings.pianoColors[4]);
					vrc6pulse2color.repaint();
					vrc6sawcolor.setBackground(UISettings.pianoColors[5]);
					vrc6sawcolor.repaint();
					mmc5pulse1color.setBackground(UISettings.pianoColors[6]);
					mmc5pulse1color.repaint();
					mmc5pulse2color.setBackground(UISettings.pianoColors[7]);
					mmc5pulse2color.repaint();
					namcocolor.setBackground(UISettings.pianoColors[8]);
					namcocolor.repaint();
				}
			}
		});
		chckbxColorBlindMode.setBounds(157, 34, 122, 23);
		panel_2.add(chckbxColorBlindMode);
		
		JPanel panel_3 = new JPanel();
		tabbedPane.addTab("NSF Player", null, panel_3, null);
		panel_3.setLayout(null);
		String[] defaultTimes = new String[] {"0:30", "1:00", "1:30", "2:00", "2:30", "3:00", "3:30", "4:00", "4:30", "5:00"};
		int[] defaultTimesInt = {1800,3600,5400,7200,9000,10800,12600,14400,16200,18000};
		JComboBox<String> comboBox_1 = new JComboBox<String>();
		comboBox_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox<?> cb = (JComboBox<?>)e.getSource();
				String s = cb.getSelectedItem().toString();
				NesSettings.nsfPlayerSongLength = defaultTimesInt[Arrays.asList(defaultTimes).indexOf(s)];
			}
		});
		comboBox_1.setModel(new DefaultComboBoxModel<String>(defaultTimes));
		comboBox_1.setSelectedIndex(NesSettings.nsfPlayerSongLength/1800-1);
		comboBox_1.setBounds(117, 35, 59, 20);
		panel_3.add(comboBox_1);
		
		JLabel lblNewLabel_1 = new JLabel("Default Song Length");
		lblNewLabel_1.setBounds(10, 38, 108, 14);
		panel_3.add(lblNewLabel_1);
		if(UISettings.colorBlindMode){
			pulse1color.setBackground(UISettings.pianoColorsColorBlind[0]);
			pulse1color.repaint();
			pulse2color.setBackground(UISettings.pianoColorsColorBlind[1]);
			pulse2color.repaint();
			trianglecolor.setBackground(UISettings.pianoColorsColorBlind[2]);
			trianglecolor.repaint();
			vrc6pulse1color.setBackground(UISettings.pianoColorsColorBlind[3]);
			vrc6pulse1color.repaint();
			vrc6pulse2color.setBackground(UISettings.pianoColorsColorBlind[4]);
			vrc6pulse2color.repaint();
			vrc6sawcolor.setBackground(UISettings.pianoColorsColorBlind[5]);
			vrc6sawcolor.repaint();
			mmc5pulse1color.setBackground(UISettings.pianoColorsColorBlind[6]);
			mmc5pulse1color.repaint();
			mmc5pulse2color.setBackground(UISettings.pianoColorsColorBlind[7]);
			mmc5pulse2color.repaint();
			namcocolor.setBackground(UISettings.pianoColorsColorBlind[8]);
			namcocolor.repaint();
		}
		else{
			pulse1color.setBackground(UISettings.pianoColors[0]);
			pulse1color.repaint();
			pulse2color.setBackground(UISettings.pianoColors[1]);
			pulse2color.repaint();
			trianglecolor.setBackground(UISettings.pianoColors[2]);
			trianglecolor.repaint();
			vrc6pulse1color.setBackground(UISettings.pianoColors[3]);
			vrc6pulse1color.repaint();
			vrc6pulse2color.setBackground(UISettings.pianoColors[4]);
			vrc6pulse2color.repaint();
			vrc6sawcolor.setBackground(UISettings.pianoColors[5]);
			vrc6sawcolor.repaint();
			mmc5pulse1color.setBackground(UISettings.pianoColors[6]);
			mmc5pulse1color.repaint();
			mmc5pulse2color.setBackground(UISettings.pianoColors[7]);
			mmc5pulse2color.repaint();
			namcocolor.setBackground(UISettings.pianoColors[8]);
			namcocolor.repaint();
		}
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
	}
	private Color setColor(String channel){
		if(channel.equals("Namco"))
			return setNamco();
		int pianoindex = Arrays.asList(UISettings.channelNames).indexOf(channel);
		Color newColor = JColorChooser.showDialog(null, "Choose a color", UISettings.pianoColors[pianoindex]);
		if(newColor!=null){
			UISettings.pianoColors[pianoindex] = newColor;
			return newColor;
		}
		return UISettings.pianoColors[pianoindex];
	}
	private Color setNamco(){
		int pianoindex = Arrays.asList(UISettings.channelNames).indexOf("N_Channel 0");
		Color newColor = JColorChooser.showDialog(null, "Choose a color", UISettings.pianoColors[pianoindex]);
		if(newColor!=null){
			for(int i = pianoindex;i<pianoindex+8;i++)
				UISettings.pianoColors[i] = newColor;
			return newColor;
		}
		return UISettings.pianoColors[pianoindex];
	}
}
