package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import core.NES;
import core.NesSettings;
import ui.filter.NesNtsc;

import javax.swing.JTabbedPane;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class AdvancedGraphics extends JFrame {

	private static final long serialVersionUID = -6383463064016482019L;
	SystemUI sys;
	@SuppressWarnings("unchecked")
	public AdvancedGraphics(SystemUI s) {
		sys = s;
		setTitle("Graphics Options");
		//noinspection MagicConstant
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		JPanel contentPane = new JPanel();
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
				NesSettings.RenderBackground=!NesSettings.RenderBackground;
			}
		});
		chckbxShowBackground.setSelected(NesSettings.RenderBackground);
		chckbxShowBackground.setBounds(27, 29, 131, 23);
		panel.add(chckbxShowBackground);
		
		JCheckBox chckbxShowSprites = new JCheckBox("Show Sprites");
		chckbxShowSprites.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NesSettings.RenderSprites=!NesSettings.RenderSprites;
			}
		});
		chckbxShowSprites.setSelected(NesSettings.RenderSprites);
		chckbxShowSprites.setBounds(27, 70, 110, 23);
		panel.add(chckbxShowSprites);
		
		JCheckBox chckbxEnableFrameLimit = new JCheckBox("Enable Frame Limit");
		chckbxEnableFrameLimit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NesSettings.frameLimit=!NesSettings.frameLimit;
			}
		});
		chckbxEnableFrameLimit.setSelected(NesSettings.frameLimit);
		chckbxEnableFrameLimit.setBounds(27, 115, 169, 23);
		panel.add(chckbxEnableFrameLimit);
		
		JCheckBox chckbxPoliteFrameTiming = new JCheckBox("Polite Frame Timing");
		chckbxPoliteFrameTiming.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NesSettings.politeFrameTiming=!NesSettings.politeFrameTiming;
			}
		});
		chckbxPoliteFrameTiming.setSelected(NesSettings.politeFrameTiming);
		chckbxPoliteFrameTiming.setBounds(27, 167, 169, 23);
		panel.add(chckbxPoliteFrameTiming);
		
		String[] RenderMethods = new String[]{"Standard"};
		@SuppressWarnings({ "rawtypes" })
		JComboBox comboBox = new JComboBox(RenderMethods);
		comboBox.setModel(new DefaultComboBoxModel<String>(RenderMethods));
		comboBox.setBounds(260, 30, 101, 20);
		panel.add(comboBox);
		
		JLabel lblRenderMethod = new JLabel("Render Method");
		lblRenderMethod.setHorizontalAlignment(SwingConstants.CENTER);
		lblRenderMethod.setBounds(260, 5, 101, 14);
		panel.add(lblRenderMethod);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Palette", null, panel_1, null);
		panel_1.setLayout(null);
		
		JPanel panel_2 = new JPanel(){
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g){
				int[] palette = NES.getInternalPaletteRGB(NesSettings.selectedPalette);
				g.setColor(Color.BLACK);
				for(int y = 0; y < 4; y++)
					for(int x=0;x<16;x++){
						g.setColor(new Color(palette[y*16+x]));
						g.fillRect(x*(this.getWidth()/16), y*(this.getHeight()/4), (x+1)*(this.getWidth()/16), (y+1)*(this.getHeight()/4));
					}
			}
		};
		panel_2.setBounds(21, 11, 259, 105);
		panel_1.add(panel_2);
		String[] pal = NesSettings.palettes;
		Arrays.sort(pal);
		@SuppressWarnings("rawtypes")
		JComboBox<String> comboBox_1 = new JComboBox(pal);
		comboBox_1.setModel(new DefaultComboBoxModel<String>(pal));
		comboBox_1.setSelectedIndex(Arrays.asList(pal).indexOf(NesSettings.selectedPalette));
		comboBox_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox<?> cb = (JComboBox<?>)e.getSource();
				String s = cb.getSelectedItem().toString();
				NES.setInternalPalette(s);
				NesNtsc.restartNTSC();
				panel_2.repaint();	
			}
		});
		panel_2.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseReleased(MouseEvent arg0) {
				int x = arg0.getX()/(panel_2.getWidth()/16);
				int y = arg0.getY()/(panel_2.getHeight()/4);
				int [] palette = NES.getInternalPaletteRGB(NesSettings.selectedPalette);
				int[] custom = NES.getInternalPaletteRGB("custom");
				Color newColor = JColorChooser.showDialog(null, "Choose a color", new Color(palette[y*16+x]));
				if(newColor!=null){
					custom=Arrays.copyOf(palette, palette.length);
					custom[y*16+x] = newColor.getRGB();
					NES.setCustomPalette(custom);
					NES.setInternalPalette("custom");
					comboBox_1.setSelectedIndex(1);
					panel_2.repaint();
					NesNtsc.restartNTSC();
				}
			}
		});
		comboBox_1.setBounds(290, 37, 129, 20);
		panel_1.add(comboBox_1);
		
		JPanel panel_3 = new JPanel();
		tabbedPane.addTab("NTSC", null, panel_3, null);
		panel_3.setLayout(null);
		
		JSlider slider = new JSlider();
		slider.setSnapToTicks(true);
		slider.setMinorTickSpacing(20);
		slider.setToolTipText("value");
		slider.setPaintTicks(true);
		slider.setMajorTickSpacing(100);
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				JSlider source = (JSlider) arg0.getSource();
				UISettings.ntsc_hue=source.getValue()/100.0;
				NesNtsc.restartNTSC();
			}
		});
		slider.setValue((int) (UISettings.ntsc_hue*100));
		slider.setMinimum(-100);
		slider.setBounds(10, 34, 74, 26);
		panel_3.add(slider);
		
		JLabel lblHue = new JLabel("Hue");
		lblHue.setHorizontalAlignment(SwingConstants.CENTER);
		lblHue.setBounds(25, 9, 46, 14);
		panel_3.add(lblHue);
		
		JSlider slider_1 = new JSlider();
		slider_1.setSnapToTicks(true);
		slider_1.setMinorTickSpacing(20);
		slider_1.setMajorTickSpacing(100);
		slider_1.setPaintTicks(true);
		slider_1.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				UISettings.ntsc_saturation=source.getValue()/100.0;
				NesNtsc.restartNTSC();
			}
		});
		slider_1.setValue((int) (UISettings.ntsc_saturation*100));
		slider_1.setMinimum(-100);
		slider_1.setBounds(110, 34, 74, 26);
		panel_3.add(slider_1);
		
		JSlider slider_2 = new JSlider();
		slider_2.setSnapToTicks(true);
		slider_2.setMajorTickSpacing(100);
		slider_2.setMinorTickSpacing(20);
		slider_2.setPaintTicks(true);
		slider_2.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				UISettings.ntsc_contrast=source.getValue()/100.0;
				NesNtsc.restartNTSC();
			}
		});
		slider_2.setValue((int) (UISettings.ntsc_contrast*100));
		slider_2.setMinimum(-100);
		slider_2.setBounds(209, 34, 74, 26);
		panel_3.add(slider_2);
		
		JSlider slider_3 = new JSlider();
		slider_3.setSnapToTicks(true);
		slider_3.setPaintTicks(true);
		slider_3.setMinorTickSpacing(20);
		slider_3.setMajorTickSpacing(100);
		slider_3.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				UISettings.ntsc_brightness=source.getValue()/100.0;
				NesNtsc.restartNTSC();
			}
		});
		slider_3.setValue((int) (UISettings.ntsc_brightness*100));
		slider_3.setMinimum(-100);
		slider_3.setBounds(309, 34, 74, 26);
		panel_3.add(slider_3);
		
		JLabel lblSaturation = new JLabel("Saturation");
		lblSaturation.setHorizontalAlignment(SwingConstants.CENTER);
		lblSaturation.setBounds(110, 9, 74, 14);
		panel_3.add(lblSaturation);
		
		JLabel lblContrast = new JLabel("Contrast");
		lblContrast.setHorizontalAlignment(SwingConstants.CENTER);
		lblContrast.setBounds(209, 9, 74, 14);
		panel_3.add(lblContrast);
		
		JLabel lblBrightness = new JLabel("Brightness");
		lblBrightness.setHorizontalAlignment(SwingConstants.CENTER);
		lblBrightness.setBounds(309, 9, 74, 14);
		panel_3.add(lblBrightness);
		
		JSlider slider_4 = new JSlider();
		slider_4.setSnapToTicks(true);
		slider_4.setPaintTicks(true);
		slider_4.setMajorTickSpacing(100);
		slider_4.setMinorTickSpacing(20);
		slider_4.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				UISettings.ntsc_sharpness=source.getValue()/100.0;
				NesNtsc.restartNTSC();
			}
		});
		slider_4.setValue((int) (UISettings.ntsc_sharpness*100));
		slider_4.setMinimum(-100);
		slider_4.setBounds(10, 96, 74, 26);
		panel_3.add(slider_4);
		
		JSlider slider_5 = new JSlider();
		slider_5.setSnapToTicks(true);
		slider_5.setPaintTicks(true);
		slider_5.setMinorTickSpacing(20);
		slider_5.setMajorTickSpacing(100);
		slider_5.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				UISettings.ntsc_gamma=source.getValue()/100.0;
				NesNtsc.restartNTSC();
			}
		});
		slider_5.setValue((int) (UISettings.ntsc_gamma*100));
		slider_5.setMinimum(-100);
		slider_5.setBounds(110, 96, 74, 26);
		panel_3.add(slider_5);
		
		JSlider slider_6 = new JSlider();
		slider_6.setSnapToTicks(true);
		slider_6.setPaintTicks(true);
		slider_6.setMinorTickSpacing(20);
		slider_6.setMajorTickSpacing(100);
		slider_6.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				UISettings.ntsc_artifacts=source.getValue()/100.0;
				NesNtsc.restartNTSC();
			}
		});
		slider_6.setValue((int) (UISettings.ntsc_artifacts*100));
		slider_6.setMinimum(-100);
		slider_6.setBounds(209, 96, 74, 26);
		panel_3.add(slider_6);
		
		JSlider slider_7 = new JSlider();
		slider_7.setSnapToTicks(true);
		slider_7.setPaintTicks(true);
		slider_7.setMinorTickSpacing(20);
		slider_7.setMajorTickSpacing(100);
		slider_7.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				UISettings.ntsc_bleed=source.getValue()/100.0;
				NesNtsc.restartNTSC();
			}
		});
		slider_7.setValue((int) (UISettings.ntsc_bleed*100));
		slider_7.setMinimum(-100);
		slider_7.setBounds(309, 96, 74, 26);
		panel_3.add(slider_7);
		
		JLabel lblSharpness = new JLabel("Sharpness");
		lblSharpness.setHorizontalAlignment(SwingConstants.CENTER);
		lblSharpness.setBounds(10, 71, 74, 14);
		panel_3.add(lblSharpness);
		
		JLabel lblGamma = new JLabel("Gamma");
		lblGamma.setHorizontalAlignment(SwingConstants.CENTER);
		lblGamma.setBounds(110, 71, 74, 14);
		panel_3.add(lblGamma);
		
		JLabel lblArtifacts = new JLabel("Artifacts");
		lblArtifacts.setHorizontalAlignment(SwingConstants.CENTER);
		lblArtifacts.setBounds(209, 71, 74, 14);
		panel_3.add(lblArtifacts);
		
		JLabel lblBleed = new JLabel("Bleed");
		lblBleed.setHorizontalAlignment(SwingConstants.CENTER);
		lblBleed.setBounds(309, 71, 74, 14);
		panel_3.add(lblBleed);
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
	}
}
