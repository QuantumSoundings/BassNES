package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import video.NesColors;
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

public class AdvancedGraphics extends JFrame {

	private static final long serialVersionUID = -6383463064016482019L;

	@SuppressWarnings("unchecked")
	public AdvancedGraphics() {
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
				UserSettings.RenderBackground=!UserSettings.RenderBackground;
			}
		});
		chckbxShowBackground.setSelected(UserSettings.RenderBackground);
		chckbxShowBackground.setBounds(27, 29, 131, 23);
		panel.add(chckbxShowBackground);
		
		JCheckBox chckbxShowSprites = new JCheckBox("Show Sprites");
		chckbxShowSprites.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UserSettings.RenderSprites=!UserSettings.RenderSprites;
			}
		});
		chckbxShowSprites.setSelected(UserSettings.RenderSprites);
		chckbxShowSprites.setBounds(27, 70, 110, 23);
		panel.add(chckbxShowSprites);
		
		JCheckBox chckbxEnableFrameLimit = new JCheckBox("Enable Frame Limit");
		chckbxEnableFrameLimit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UserSettings.frameLimit=!UserSettings.frameLimit;
			}
		});
		chckbxEnableFrameLimit.setSelected(UserSettings.frameLimit);
		chckbxEnableFrameLimit.setBounds(27, 115, 169, 23);
		panel.add(chckbxEnableFrameLimit);
		
		JCheckBox chckbxPoliteFrameTiming = new JCheckBox("Polite Frame Timing");
		chckbxPoliteFrameTiming.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UserSettings.politeFrameTiming=!UserSettings.politeFrameTiming;
			}
		});
		chckbxPoliteFrameTiming.setSelected(UserSettings.politeFrameTiming);
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
				int[] palette = NesColors.getpalette(UserSettings.selectedPalette);
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
		String[] pal = NesColors.palettes;
		@SuppressWarnings("rawtypes")
		JComboBox<String> comboBox_1 = new JComboBox(pal);
		comboBox_1.setSelectedIndex(Arrays.asList(NesColors.palettes).indexOf(UserSettings.selectedPalette));
		comboBox_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox<?> cb = (JComboBox<?>)e.getSource();
				String s = cb.getSelectedItem().toString();
				NesColors.updatePalette(s);
				UserSettings.selectedPalette=s;
				panel_2.repaint();	
			}
		});
		panel_2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				int x = arg0.getX()/(panel_2.getWidth()/16);
				int y = arg0.getY()/(panel_2.getHeight()/4);
				int [] palette = NesColors.getpalette(UserSettings.selectedPalette);
				Color newColor = JColorChooser.showDialog(null, "Choose a color", new Color(palette[y*16+x]));
				if(newColor!=null){
					UserSettings.selectedPalette="custom";
					NesColors.custom=Arrays.copyOf(palette, palette.length);
					NesColors.custom[y*16+x] = newColor.getRGB();
					NesColors.updatePalette("Custom");
					comboBox_1.setSelectedIndex(1);
					panel_2.repaint();
				}
			}
		});
		comboBox_1.setBounds(290, 37, 129, 20);
		panel_1.add(comboBox_1);
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
	}
}
