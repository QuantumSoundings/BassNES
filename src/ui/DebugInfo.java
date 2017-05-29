package ui;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class DebugInfo extends JFrame {

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
	SystemUI sys;
	private JTextField inst;
	private JLabel lblInst;
	private JLabel lblPpuCycle;
	private JTextField ppucycle;
	private JLabel lblScanline;
	private JTextField ppuscanline;
	/**
	 * Create the frame.
	 */
	public DebugInfo(SystemUI s) {
		sys = s;
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		pc = new JTextField();
		pc.setEditable(false);
		pc.setBounds(55, 23, 32, 20);
		contentPane.add(pc);
		pc.setColumns(10);
		
		JLabel lblPc = new JLabel("PC");
		lblPc.setBounds(27, 26, 32, 14);
		contentPane.add(lblPc);
		
		a = new JTextField();
		a.setEditable(false);
		a.setBounds(55, 45, 32, 20);
		contentPane.add(a);
		a.setColumns(10);
		
		x = new JTextField();
		x.setEditable(false);
		x.setBounds(55, 70, 32, 20);
		contentPane.add(x);
		x.setColumns(10);
		
		y = new JTextField();
		y.setEditable(false);
		y.setBounds(55, 95, 32, 20);
		contentPane.add(y);
		y.setColumns(10);
		
		sp = new JTextField();
		sp.setEditable(false);
		sp.setBounds(55, 116, 32, 20);
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
		nflag.setBounds(136, 23, 88, 23);
		contentPane.add(nflag);
		
		 vflag = new JCheckBox("Overflow");
		 vflag.setEnabled(false);
		vflag.setBounds(136, 48, 88, 23);
		contentPane.add(vflag);
		
		 dflag = new JCheckBox("Decimal");
		 dflag.setEnabled(false);
		dflag.setBounds(136, 74, 88, 23);
		contentPane.add(dflag);
		
		 iflag = new JCheckBox("Interrupt");
		 iflag.setEnabled(false);
		iflag.setBounds(226, 23, 82, 23);
		contentPane.add(iflag);
		
		 zflag = new JCheckBox("Zero");
		 zflag.setEnabled(false);
		zflag.setBounds(226, 48, 68, 23);
		contentPane.add(zflag);
		
		 cflag = new JCheckBox("Carry");
		 cflag.setEnabled(false);
		cflag.setBounds(226, 74, 68, 23);
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
		this.setVisible(false);
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
		
		Object[] ppuvars = sys.nes.getPPUDebugInfo();
		ppuscanline.setText((int) ppuvars[1]+"");
		ppucycle.setText((int) ppuvars[0]+"");
		
	}
}
