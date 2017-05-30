package ui.debugger;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import ui.SystemUI;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JList;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Debugger extends JFrame {

	private static final long serialVersionUID = -4384773663935090830L;
	private JPanel contentPane;
	private JTextField pc;
	private JTextField a;
	private JTextField x;
	private JTextField y;
	private JTextField sp;
	private JCheckBox nflag;
	private JCheckBox vflag;
	private JCheckBox dflag;
	private JCheckBox iflag;
	private JCheckBox zflag;
	private JCheckBox cflag;
	private JCheckBox sprite0;
	private JCheckBox vb;
	private JCheckBox spriteover;
	private JCheckBox irqexternal;
	private JCheckBox irqframe;
	private JCheckBox irqdmc;
	private JCheckBox nmi;
	SystemUI sys;
	private JTextField inst;
	private JLabel lblInst;
	private JLabel lblPpuCycle;
	private JTextField ppucycle;
	private JLabel lblScanline;
	private JTextField ppuscanline;
	
	public JList<BreakPoint> breakpointui;
	public DefaultListModel<BreakPoint> breakpoints;
	private JButton btnAddBreak;
	/**
	 * Create the frame.
	 */
	public Debugger(SystemUI s) {
		sys = s;
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 611, 369);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		pc = new JTextField();
		pc.setEditable(false);
		pc.setBounds(45, 24, 32, 20);
		contentPane.add(pc);
		pc.setColumns(10);
		
		JLabel lblPc = new JLabel("PC");
		lblPc.setBounds(27, 26, 32, 14);
		contentPane.add(lblPc);
		
		a = new JTextField();
		a.setEditable(false);
		a.setBounds(45, 45, 32, 20);
		contentPane.add(a);
		a.setColumns(10);
		
		x = new JTextField();
		x.setEditable(false);
		x.setBounds(45, 64, 32, 20);
		contentPane.add(x);
		x.setColumns(10);
		
		y = new JTextField();
		y.setEditable(false);
		y.setBounds(45, 95, 32, 20);
		contentPane.add(y);
		y.setColumns(10);
		
		sp = new JTextField();
		sp.setEditable(false);
		sp.setBounds(45, 116, 32, 20);
		contentPane.add(sp);
		sp.setColumns(10);
		
		JLabel lblA = new JLabel("A");
		lblA.setBounds(27, 48, 32, 14);
		contentPane.add(lblA);
		
		JLabel lblX = new JLabel("X");
		lblX.setBounds(27, 73, 32, 14);
		contentPane.add(lblX);
		
		JLabel lblY = new JLabel("Y");
		lblY.setBounds(27, 98, 32, 14);
		contentPane.add(lblY);
		
		JLabel lblSp = new JLabel("SP");
		lblSp.setBounds(27, 119, 32, 14);
		contentPane.add(lblSp);
		
		 nflag = new JCheckBox("Negative");
		 nflag.setEnabled(false);
		nflag.setBounds(83, 23, 88, 23);
		contentPane.add(nflag);
		
		 vflag = new JCheckBox("Overflow");
		 vflag.setEnabled(false);
		vflag.setBounds(83, 44, 88, 23);
		contentPane.add(vflag);
		
		 dflag = new JCheckBox("Decimal");
		 dflag.setEnabled(false);
		dflag.setBounds(83, 63, 88, 23);
		contentPane.add(dflag);
		
		 iflag = new JCheckBox("Interrupt");
		 iflag.setEnabled(false);
		iflag.setBounds(173, 23, 82, 23);
		contentPane.add(iflag);
		
		 zflag = new JCheckBox("Zero");
		 zflag.setEnabled(false);
		zflag.setBounds(173, 44, 68, 23);
		contentPane.add(zflag);
		
		 cflag = new JCheckBox("Carry");
		 cflag.setEnabled(false);
		cflag.setBounds(173, 63, 68, 23);
		contentPane.add(cflag);
		
		JButton btnRunCycle = new JButton("Run Cycle");
		btnRunCycle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				sys.nes.runCPUCycle();
				updateInfo();
			}
		});
		btnRunCycle.setBounds(10, 172, 102, 23);
		contentPane.add(btnRunCycle);
		
		inst = new JTextField();
		inst.setEditable(false);
		inst.setBounds(55, 141, 45, 20);
		contentPane.add(inst);
		inst.setColumns(10);
		
		lblInst = new JLabel("INST");
		lblInst.setBounds(27, 144, 32, 14);
		contentPane.add(lblInst);
		
		lblPpuCycle = new JLabel("PPU Cycle");
		lblPpuCycle.setBounds(136, 119, 68, 14);
		contentPane.add(lblPpuCycle);
		
		ppucycle = new JTextField();
		ppucycle.setEditable(false);
		ppucycle.setBounds(194, 116, 53, 20);
		contentPane.add(ppucycle);
		ppucycle.setColumns(10);
		
		lblScanline = new JLabel("Scanline");
		lblScanline.setBounds(136, 144, 59, 14);
		contentPane.add(lblScanline);
		
		ppuscanline = new JTextField();
		ppuscanline.setEditable(false);
		ppuscanline.setBounds(194, 141, 53, 20);
		contentPane.add(ppuscanline);
		ppuscanline.setColumns(10);
		
		JButton btnNewButton = new JButton("Seek Break");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				sys.dobreakseek=true;
			}
		});
		btnNewButton.setBounds(10, 204, 102, 23);
		contentPane.add(btnNewButton);
		
		sprite0 = new JCheckBox("Sprite 0");
		sprite0.setEnabled(false);
		sprite0.setBounds(253, 115, 75, 23);
		contentPane.add(sprite0);
		
		vb = new JCheckBox("Vertical Blank");
		vb.setEnabled(false);
		vb.setBounds(252, 140, 97, 23);
		contentPane.add(vb);
		
		spriteover = new JCheckBox("Sprite Overflow");
		spriteover.setEnabled(false);
		spriteover.setBounds(253, 166, 113, 23);
		contentPane.add(spriteover);
		
		JLabel lblInterrupts = new JLabel("Interrupts");
		lblInterrupts.setBounds(281, 11, 68, 14);
		contentPane.add(lblInterrupts);
		
		irqexternal = new JCheckBox("IRQ: External");
		irqexternal.setEnabled(false);
		irqexternal.setBounds(257, 23, 97, 23);
		contentPane.add(irqexternal);
		
		irqframe = new JCheckBox("IRQ: Frame");
		irqframe.setEnabled(false);
		irqframe.setBounds(257, 44, 97, 23);
		contentPane.add(irqframe);
		
		irqdmc = new JCheckBox("IRQ: DMC");
		irqdmc.setEnabled(false);
		irqdmc.setBounds(257, 63, 97, 23);
		contentPane.add(irqdmc);
		
		nmi = new JCheckBox("NMI");
		nmi.setEnabled(false);
		nmi.setBounds(258, 84, 59, 20);
		contentPane.add(nmi);
		breakpoints = new DefaultListModel<BreakPoint>();
		breakpointui = new JList<BreakPoint>(breakpoints);
		breakpointui.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				if(arg0.isPopupTrigger())
					doPop(arg0);				
			}
			@Override
			public void mouseReleased(MouseEvent arg0) {
				if(arg0.isPopupTrigger())
					doPop(arg0);		
			}
			private void doPop(MouseEvent e){
				if(breakpointui.getSelectedValue()!=null){
					BreakPointPopup menu = new BreakPointPopup(breakpointui.getSelectedValue());
					menu.show(e.getComponent(),e.getX(),e.getY());
				}
			}
		});
		breakpointui.setBounds(172, 190, 295, 113);
		contentPane.add(breakpointui);
		
		btnAddBreak = new JButton("Add Break");
		btnAddBreak.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				AddBreakPoint window = new AddBreakPoint(breakpoints);
				window.setVisible(true);
			}
		});
		btnAddBreak.setBounds(378, 305, 89, 23);
		contentPane.add(btnAddBreak);
		
		this.setVisible(false);
		this.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent evt){
				sys.exitDebug();
			}
		});
	}
	public void updateInfo(){
		Object[] vars = sys.nes.getCPUDebugInfo();
		pc.setText(Integer.toHexString((int)vars[0]));
		a.setText(Integer.toHexString((int) vars[4]));
		x.setText(Integer.toHexString((int) vars[5]));
		y.setText(Integer.toHexString((int) vars[6]));
		sp.setText(Integer.toHexString((int)vars[3]));
		inst.setText((String)vars[1]+": "+(int)vars[2]);
		nflag.setSelected((boolean)vars[7]);
		vflag.setSelected((boolean)vars[8]);
		dflag.setSelected((boolean)vars[9]);
		iflag.setSelected((boolean)vars[10]);
		zflag.setSelected((boolean)vars[11]);
		cflag.setSelected((boolean)vars[12]);
		irqexternal.setSelected(((boolean[])vars[13])[0]);
		irqframe.setSelected(((boolean[])vars[13])[1]);
		irqdmc.setSelected(((boolean[])vars[13])[2]);
		
		Object[] ppuvars = sys.nes.getPPUDebugInfo();
		ppuscanline.setText((int) ppuvars[1]+"");
		ppucycle.setText((int) ppuvars[0]+"");
		vb.setSelected((boolean)ppuvars[2]);
		spriteover.setSelected((boolean)ppuvars[3]);
		sprite0.setSelected((boolean)ppuvars[4]);
		
	}
}
