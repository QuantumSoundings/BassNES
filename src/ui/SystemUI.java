package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import com.NES;

import video.NesDisplay;

public class SystemUI {
	public final String version = "0.1.3";
	public NES nes;
	final JFileChooser fc = new JFileChooser();
	public JFrame frame,debugframe,keyconfig;
	File rom;
	NesDisplay display;
	JPanel panel;
	JButton b1;
	JButton b2;
	boolean awaitingkey;
	JMenuBar menu;
	JMenu system,cpu,audio,graphics,control,debug;
	Thread current;
	Properties prop;
	String testoutput;
	public boolean begin;
	boolean autoload = true;
	
	public SystemUI(){
		try {
			loadProperties();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		frame = new MainUI(this);
		//debugframe = new DebugUI();
		keyconfig = new ControlUI(prop,this);
		addapply();
		rom = new File("zelda.nes"); 
		display = new NesDisplay();
		panel = new JPanel();
		panel.setLayout(new FlowLayout());
		display.setSize(256, 240);
		display.setFocusable(true);
		frame.getContentPane().add(display);
		display.requestFocusInWindow();
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent evt){
				if(nes!=null)
					nes.flag=false;
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				try {
					FileOutputStream output = new FileOutputStream("config.properties");
					//URL resourceUrl = getClass().getResource("config.properties");
					//File file = new File(resourceUrl.toURI().toString());
					//OutputStream output = new FileOutputStream(file);
					prop.store(output, null);
				} catch (IOException e) {
					e.printStackTrace();
				} 
				System.exit(0);
			}
		});
		frame.pack();
		frame.setBounds(100, 100, 256+10, 240+60);
		frame.setVisible(true);
		//debugWindow();
		try {
			runTests();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void start(){
		
	}
	int pass,fail,totalpass,total,regression;
	void runTests() throws InterruptedException{
		Thread.sleep(500);
		
		//rom = new File(System.getProperty("user.dir")+"/blarggppu/vram_access.nes");
		//byte[] pixels;// = new int[bi.getHeight()*bi.getWidth()];
		//pixels = ((DataBufferByte)bi.getRaster().getDataBuffer()).getData();
		//Blargg PPU Tests
		testoutput="";
		if(true){
			testoutput = " Blargg PPU Tests \n\n";
			testrom(2000, new File(System.getProperty("user.dir")+"/tests/blarggppu/sprite_ram.nes"),-897623842);
			testrom(2000, new File(System.getProperty("user.dir")+"/tests/blarggppu/palette_ram.nes"),-897623842);
			testrom(2000, new File(System.getProperty("user.dir")+"/tests/blarggppu/power_up_palette.nes"),0);
			testrom(2000, new File(System.getProperty("user.dir")+"/tests/blarggppu/vbl_clear_time.nes"),-897623842);
			testrom(2000, new File(System.getProperty("user.dir")+"/tests/blarggppu/vram_access.nes"),-897623842);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
		//Blargg CPU Tests
		if(true){
			testoutput+= "\n Blargg CPU Tests \n\n";
			testrom(2000, new File(System.getProperty("user.dir")+"/tests/blarggcpu/01-basics.nes"),-1888758012);
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/blarggcpu/02-implied.nes"),-319326302);
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/blarggcpu/03-immediate.nes"),-256149224);
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/blarggcpu/04-zero_page.nes"),-1980793375);
			testrom(7000, new File(System.getProperty("user.dir")+"/tests/blarggcpu/05-zp_xy.nes"),-1850963870);
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/blarggcpu/06-absolute.nes"),-2062202114);
			testrom(9000, new File(System.getProperty("user.dir")+"/tests/blarggcpu/07-abs_xy.nes"),-1543345661);
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/blarggcpu/08-ind_x.nes"),-2082446367);
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/blarggcpu/09-ind_y.nes"),136771103);
			testrom(2000, new File(System.getProperty("user.dir")+"/tests/blarggcpu/10-branches.nes"),1993686150);
			testrom(5000, new File(System.getProperty("user.dir")+"/tests/blarggcpu/11-stack.nes"),1714125406);
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/blarggcpu/12-jmp_jsr.nes"),-1708679964);
			testrom(2000, new File(System.getProperty("user.dir")+"/tests/blarggcpu/13-rts.nes"),-1672382369);
			testrom(2000, new File(System.getProperty("user.dir")+"/tests/blarggcpu/14-rti.nes"),-1820265601);
			testrom(2000, new File(System.getProperty("user.dir")+"/tests/blarggcpu/15-brk.nes"),860016194);
			testrom(2000, new File(System.getProperty("user.dir")+"/tests/blarggcpu/16-special.nes"),-1902180960);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
		if(true){
			testoutput+= "\n Blargg All CPU Instructions Test\n\n";
			testrom(50000, new File(System.getProperty("user.dir")+"/tests/blarggcpu/all_inst.nes"),-1223976542);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
		if(true){
			testoutput+= "\n Blargg APU Tests \n\n";
			testrom(2000, new File(System.getProperty("user.dir")+"/tests/blarggapu/01.len_ctr.nes"),-897623842);
			testrom(2000, new File(System.getProperty("user.dir")+"/tests/blarggapu/02.len_table.nes"),-897623842);
			testrom(2000, new File(System.getProperty("user.dir")+"/tests/blarggapu/03.irq_flag.nes"),-897623842);
			testrom(2000, new File(System.getProperty("user.dir")+"/tests/blarggapu/04.clock_jitter.nes"),0);
			testrom(2000, new File(System.getProperty("user.dir")+"/tests/blarggapu/05.len_timing_mode0.nes"),0);
			testrom(2000, new File(System.getProperty("user.dir")+"/tests/blarggapu/06.len_timing_mode1.nes"),0);
			testrom(2000, new File(System.getProperty("user.dir")+"/tests/blarggapu/07.irq_flag_timing.nes"),0);
			testrom(2000, new File(System.getProperty("user.dir")+"/tests/blarggapu/08.irq_timing.nes"),0);
			testrom(2000, new File(System.getProperty("user.dir")+"/tests/blarggapu/09.reset_timing.nes"),0);
			testrom(2000, new File(System.getProperty("user.dir")+"/tests/blarggapu/10.len_halt_timing.nes"),0);
			testrom(2000, new File(System.getProperty("user.dir")+"/tests/blarggapu/11.len_reload_timing.nes"),0);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
		if(true){
			testoutput+= "\n PPU_VBL_NMI Tests \n\n";
			testrom(5000, new File(System.getProperty("user.dir")+"/tests/ppu_vbl_nmi/01-vbl_basics.nes"),-2003676729);
			testrom(5000, new File(System.getProperty("user.dir")+"/tests/ppu_vbl_nmi/02-vbl_set_time.nes"),2124708931);
			testrom(5000, new File(System.getProperty("user.dir")+"/tests/ppu_vbl_nmi/03-vbl_clear_time.nes"),-1540172293);
			testrom(5000, new File(System.getProperty("user.dir")+"/tests/ppu_vbl_nmi/04-nmi_control.nes"),2029805090);
			testrom(5000, new File(System.getProperty("user.dir")+"/tests/ppu_vbl_nmi/05-nmi_timing.nes"),0);
			testrom(5000, new File(System.getProperty("user.dir")+"/tests/ppu_vbl_nmi/06-suppression.nes"),1343745688);
			testrom(5000, new File(System.getProperty("user.dir")+"/tests/ppu_vbl_nmi/07-nmi_on_timing.nes"),0);
			testrom(5000, new File(System.getProperty("user.dir")+"/tests/ppu_vbl_nmi/08-nmi_off_timing.nes"),0);
			testrom(5000, new File(System.getProperty("user.dir")+"/tests/ppu_vbl_nmi/09-even_odd_frames.nes"),-276693205);
			testrom(5000, new File(System.getProperty("user.dir")+"/tests/ppu_vbl_nmi/10-even_odd_timing.nes"),613825573);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
		if(true){
			testoutput+="\n Sprite Zero Hit Tests\n\n";
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/sprite_hit/01-basics.nes"),-1888758012);
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/sprite_hit/02-alignment.nes"),-1156896354);
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/sprite_hit/03-corners.nes"),371304900);
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/sprite_hit/04-flip.nes"),-1102720830);
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/sprite_hit/05-left_clip.nes"),-1379369440);
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/sprite_hit/06-right_edge.nes"),1771125851);
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/sprite_hit/07-screen_bottom.nes"),-1828165181);
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/sprite_hit/08-double_height.nes"),626756671);
			testrom(5000, new File(System.getProperty("user.dir")+"/tests/sprite_hit/09-timing.nes"),0);
			testrom(5000, new File(System.getProperty("user.dir")+"/tests/sprite_hit/10-timing_order.nes"),0);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
		if(true){
			testoutput+="\n Sprite Overflow Tests\n\n";
			testrom(5000, new File(System.getProperty("user.dir")+"/tests/sprite_overflow/01-basics.nes"),-1888758012);
			testrom(5000, new File(System.getProperty("user.dir")+"/tests/sprite_overflow/02-details.nes"),-2046399234);
			testrom(5000, new File(System.getProperty("user.dir")+"/tests/sprite_overflow/03-timing.nes"),0);
			testrom(5000, new File(System.getProperty("user.dir")+"/tests/sprite_overflow/04-obscure.nes"),-421192188);
			testrom(5000, new File(System.getProperty("user.dir")+"/tests/sprite_overflow/05-emulator.nes"),1084950203);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
		if(true){
			testoutput+="\n Instruction Misc Tests\n\n";
			testrom(5000, new File(System.getProperty("user.dir")+"/tests/instr_misc/01-abs_x_wrap.nes"),-2017680284);
			testrom(5000, new File(System.getProperty("user.dir")+"/tests/instr_misc/02-branch_wrap.nes"),1897661446);
			testrom(5000, new File(System.getProperty("user.dir")+"/tests/instr_misc/03-dummy_reads.nes"),0);
			testrom(5000, new File(System.getProperty("user.dir")+"/tests/instr_misc/04-dummy_reads_apu.nes"),0);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
		if(true){
			testoutput+="\n CPU Interrupts\n\n";
			testrom(5000, new File(System.getProperty("user.dir")+"/tests/cpu_interrupts/1-cli_latency.nes"),0);
			testrom(5000, new File(System.getProperty("user.dir")+"/tests/cpu_interrupts/2-nmi_and_brk.nes"),0);
			testrom(5000, new File(System.getProperty("user.dir")+"/tests/cpu_interrupts/3-nmi_and_irq.nes"),0);
			testrom(5000, new File(System.getProperty("user.dir")+"/tests/cpu_interrupts/4-irq_and_dma.nes"),0);
			testrom(5000, new File(System.getProperty("user.dir")+"/tests/cpu_interrupts/5-branch_delays_irq.nes"),0);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
		if(true){
			testoutput+="\n Instruction timing\n\n";
			testrom(23000, new File(System.getProperty("user.dir")+"/tests/1-instr_timing.nes"),-277403970);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
		if(true){
			testoutput+= "\n MMC3 Tests\n\n";
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/mmc3_test/1-clocking.nes"),-19248225);
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/mmc3_test/2-details.nes"),962856060);
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/mmc3_test/3-A12_clocking.nes"),835606307);
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/mmc3_test/4-scanline_timing.nes"),0);
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/mmc3_test/5-MMC3.nes"),482325539);
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/mmc3_test/6-MMC3_alt.nes"),0);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
		if(true){
			testoutput+= "\n OAM Tests\n\n";
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/oam_read.nes"),878913754);
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/oam_stress.nes"),0);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
		if(true){
			testoutput+= "\n PPU ReadBuffer Mega test\n\n";
			testrom(30000, new File(System.getProperty("user.dir")+"/tests/test_ppu_read_buffer.nes"),-762073439);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
		if(true){
			testoutput+= "\n PPU OpenBus test\n\n";
			testrom(7000, new File(System.getProperty("user.dir")+"/tests/ppu_open_bus.nes"),0);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
		testoutput+= "\n\n Overall results: "+totalpass+"/"+total+" Passed     " +(regression>0?regression+" Regressions":"");
		System.out.println(testoutput);

		
		
	}
	void testrom(int delay,File r,int goodhash) throws InterruptedException{
		BufferedImage bi = new BufferedImage(display.getWidth(),display.getHeight(),BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bi.createGraphics();
		rom  = r;
		int hash=0;
		reset();startnes(delay);display.paint(g);hash = getHash(bi);
		if(goodhash!=0&&hash!=goodhash){
			testoutput +="Test: "+((hash==goodhash)?"PASS":"FAIL") +" Name: "+ rom.getName()+" Hash: "+hash+"                 ****REGRESSION WARNING****\n";
			regression++;
		}
		else
			testoutput +="Test: "+((hash==goodhash)?"PASS":"FAIL") +" Name: "+ rom.getName()+" Hash: "+hash+"\n";
		if(hash==goodhash)pass++;else fail++;
	}
	void reset() throws InterruptedException{
		if(nes!=null)
			nes.flag=false;
		Thread.sleep(500);
	}
	int getHash(BufferedImage bufferedImage){
		int[] pixels;
		pixels = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();
		return Arrays.hashCode(pixels);
	}
	void startnes(int delay) throws InterruptedException {
		nes = new NES(display,frame,rom,prop);
		current = new Thread(nes);
		current.start();
		Thread.sleep(delay);
	}
	
	private void addapply(){
		JButton btnNewButton_16 = new JButton("Apply");
		btnNewButton_16.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(nes!=null){
					nes.controller.updateKeys(prop);
					nes.controller2.updateKeys(prop);
				}
			}
		});
		GridBagConstraints gbc_btnNewButton_16 = new GridBagConstraints();
		gbc_btnNewButton_16.insets = new Insets(0, 0, 0, 5);
		gbc_btnNewButton_16.gridx = 3;
		gbc_btnNewButton_16.gridy = 9;
		keyconfig.getContentPane().add(btnNewButton_16, gbc_btnNewButton_16);
	}
	public void loadProperties() throws IOException{
		prop = new Properties();
		//InputStream input =this.getClass().getResourceAsStream("config.properties");
		//prop.load(input);
		File t = new File("config.properties");
		if(!t.exists()){
			prop.setProperty("version", version);
			prop.setProperty("c1up", KeyEvent.VK_UP+"");
			prop.setProperty("c1down", KeyEvent.VK_DOWN+"");
			prop.setProperty("c1left", KeyEvent.VK_LEFT+"");
			prop.setProperty("c1right", KeyEvent.VK_RIGHT+"");
			prop.setProperty("c1a", KeyEvent.VK_A+"");
			prop.setProperty("c1b", KeyEvent.VK_S+"");
			prop.setProperty("c1start", KeyEvent.VK_Q+"");
			prop.setProperty("c1select", KeyEvent.VK_W+"");
			prop.setProperty("c2up", KeyEvent.VK_UP+"");
			prop.setProperty("c2down", KeyEvent.VK_DOWN+"");
			prop.setProperty("c2left", KeyEvent.VK_LEFT+"");
			prop.setProperty("c2right", KeyEvent.VK_RIGHT+"");
			prop.setProperty("c2a", KeyEvent.VK_A+"");
			prop.setProperty("c2b", KeyEvent.VK_S+"");
			prop.setProperty("c2start", KeyEvent.VK_Q+"");
			prop.setProperty("c2select", KeyEvent.VK_W+"");
		}
		else{
			FileInputStream input = new FileInputStream("config.properties");
			prop.load(input);
			if(!prop.getProperty("version").equals(version)||!prop.containsKey("version")){
				t.delete();
				loadProperties();
			}
		}
	}
}
