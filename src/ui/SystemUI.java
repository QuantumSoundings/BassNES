package ui;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.EventListener;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.NES;
import video.NesDisplay;
interface UpdateEventListener extends EventListener{
	public void doframe();
}
public class SystemUI {
	public NES nes;
	final JFileChooser fc = new JFileChooser();
	public JFrame mainWindow,debugWindow,keyconfigWindow,audiomixerWindow,advancedGraphicsWindow;
	File rom;
	NesDisplay display;
	JPanel panel;
	KeyChecker keys;
	public UpdateEventListener listener;
	int[] pixels;
	//JMenuBar menu;
	//JMenu system,cpu,audio,graphics,control,debug;
	Thread current;
	Properties prop;
	String testoutput;
	public boolean begin;
	public boolean showFPS=true;
	boolean autoload = true;
	
	public SystemUI(){
		try {
			UserSettings.loadSettings();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		rom = new File("zelda.nes");
		mainWindow = new MainUI(this);
		//debugframe = new DebugUI();
		keyconfigWindow = new ControlUI(prop,this);
		audiomixerWindow = new AudioMixerUI(this);
		advancedGraphicsWindow = new AdvancedGraphics();
		keys = new KeyChecker();
		mainWindow.addKeyListener(keys);
		mainWindow.setFocusable(true);
		mainWindow.requestFocusInWindow();
		display = new NesDisplay();
		panel = new JPanel();
		panel.setLayout(new FlowLayout());
		display.setSize(256, 240);
		//display.setFocusable(true);
		mainWindow.getContentPane().add(display);
		//display.requestFocusInWindow();
		mainWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		mainWindow.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent evt){
				if(nes!=null)
					nes.flag=false;
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				UserSettings.saveSettings();
				System.exit(0);
			}
		});
		mainWindow.pack();
		mainWindow.setBounds(100, 100, 256+10, 240+60);
		mainWindow.setVisible(true);
		listener = new UpdateEventListener(){
            public void doframe() {
               display.sendFrame(pixels);
           }
       };
		/*try {
			runTests();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		start();
	}
	public void start(){
		while(true){
			if(showFPS){
				if(nes!=null)
					mainWindow.setTitle("Nes Emulator     FPS: "+String.format("%.2f", nes.getFPS()));
			}
			else
				mainWindow.setTitle("Nes Emulator");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	int pass,fail,totalpass,total,regression;
	void runTests() throws InterruptedException{
		Thread.sleep(500);
		
		testoutput="";
		if(false){
			testoutput = " Blargg PPU Tests \n\n";
			testrom(5000, new File(System.getProperty("user.dir")+"/tests/blarggppu/sprite_ram.nes"),-897623842);
			testrom(2000, new File(System.getProperty("user.dir")+"/tests/blarggppu/palette_ram.nes"),-897623842);
			testrom(2000, new File(System.getProperty("user.dir")+"/tests/blarggppu/power_up_palette.nes"),0);
			testrom(2000, new File(System.getProperty("user.dir")+"/tests/blarggppu/vbl_clear_time.nes"),-897623842);
			testrom(2000, new File(System.getProperty("user.dir")+"/tests/blarggppu/vram_access.nes"),-897623842);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
		if(false){
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
		if(false){
			testoutput+= "\n Blargg All CPU Instructions Test\n\n";
			testrom(30000, new File(System.getProperty("user.dir")+"/tests/blarggcpu/all_inst.nes"),-1700840831);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
		if(false){
			testoutput+= "\n Blargg APU Tests \n\n";
			testrom(2000, new File(System.getProperty("user.dir")+"/tests/blarggapu/01.len_ctr.nes"),-897623842);
			testrom(2000, new File(System.getProperty("user.dir")+"/tests/blarggapu/02.len_table.nes"),-897623842);
			testrom(2000, new File(System.getProperty("user.dir")+"/tests/blarggapu/03.irq_flag.nes"),-897623842);
			testrom(2000, new File(System.getProperty("user.dir")+"/tests/blarggapu/04.clock_jitter.nes"),-897623842);
			testrom(2000, new File(System.getProperty("user.dir")+"/tests/blarggapu/05.len_timing_mode0.nes"),-897623842);
			testrom(2000, new File(System.getProperty("user.dir")+"/tests/blarggapu/06.len_timing_mode1.nes"),-897623842);
			testrom(2000, new File(System.getProperty("user.dir")+"/tests/blarggapu/07.irq_flag_timing.nes"),-897623842);
			testrom(2000, new File(System.getProperty("user.dir")+"/tests/blarggapu/08.irq_timing.nes"),-897623842);
			testrom(2000, new File(System.getProperty("user.dir")+"/tests/blarggapu/09.reset_timing.nes"),-897623842);
			testrom(2000, new File(System.getProperty("user.dir")+"/tests/blarggapu/10.len_halt_timing.nes"),-897623842);
			testrom(2000, new File(System.getProperty("user.dir")+"/tests/blarggapu/11.len_reload_timing.nes"),-897623842);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
		if(true){
			testoutput+= "\n PPU_VBL_NMI Tests \n\n";
			testrom(5000, new File(System.getProperty("user.dir")+"/tests/ppu_vbl_nmi/01-vbl_basics.nes"),-2003676729);
			testrom(5000, new File(System.getProperty("user.dir")+"/tests/ppu_vbl_nmi/02-vbl_set_time.nes"),2124708931);
			testrom(5000, new File(System.getProperty("user.dir")+"/tests/ppu_vbl_nmi/03-vbl_clear_time.nes"),-1540172293);
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/ppu_vbl_nmi/04-nmi_control.nes"),2029805090);
			testrom(5000, new File(System.getProperty("user.dir")+"/tests/ppu_vbl_nmi/05-nmi_timing.nes"),347938255);
			testrom(5000, new File(System.getProperty("user.dir")+"/tests/ppu_vbl_nmi/06-suppression.nes"),1343745688);
			testrom(5000, new File(System.getProperty("user.dir")+"/tests/ppu_vbl_nmi/07-nmi_on_timing.nes"),-1539996795);
			testrom(5000, new File(System.getProperty("user.dir")+"/tests/ppu_vbl_nmi/08-nmi_off_timing.nes"),834725570);
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
			testrom(5000, new File(System.getProperty("user.dir")+"/tests/sprite_hit/09-timing.nes"),571052092);
			testrom(5000, new File(System.getProperty("user.dir")+"/tests/sprite_hit/10-timing_order.nes"),-994804385);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
		if(true){
			testoutput+="\n Sprite Overflow Tests\n\n";
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/sprite_overflow/01-basics.nes"),-1888758012);
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/sprite_overflow/02-details.nes"),-2046399234);
			testrom(7000, new File(System.getProperty("user.dir")+"/tests/sprite_overflow/03-timing.nes"),1432501340);
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/sprite_overflow/04-obscure.nes"),-421192188);
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/sprite_overflow/05-emulator.nes"),1084950203);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
		if(false){
			testoutput+="\n Instruction Misc Tests\n\n";
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/instr_misc/01-abs_x_wrap.nes"),-2017680284);
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/instr_misc/02-branch_wrap.nes"),1897661446);
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/instr_misc/03-dummy_reads.nes"),-253529287);
			testrom(7000, new File(System.getProperty("user.dir")+"/tests/instr_misc/04-dummy_reads_apu.nes"),-1927266246);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
		if(true){
			testoutput+="\n CPU Interrupts\n\n";
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/cpu_interrupts/1-cli_latency.nes"),-435377923);
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/cpu_interrupts/2-nmi_and_brk.nes"),1753420384);
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/cpu_interrupts/3-nmi_and_irq.nes"),1109861111);
			testrom(5000, new File(System.getProperty("user.dir")+"/tests/cpu_interrupts/4-irq_and_dma.nes"),628280272);
			testrom(7000, new File(System.getProperty("user.dir")+"/tests/cpu_interrupts/5-branch_delays_irq.nes"),-1640116134);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
		if(false){
			testoutput+="\n Instruction timing\n\n";
			testrom(23000, new File(System.getProperty("user.dir")+"/tests/1-instr_timing.nes"),-277403970);
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/1-Branch_Basics.nes"),114070097);
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/2.Backward_Branch.nes"),-764278785);
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/3.Forward_Branch.nes"),-1744413954);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
		if(false){
			testoutput+= "\n MMC3 Tests\n\n";
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/mmc3_test/1-clocking.nes"),-19248225);
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/mmc3_test/2-details.nes"),962856060);
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/mmc3_test/3-A12_clocking.nes"),835606307);
			testrom(7000, new File(System.getProperty("user.dir")+"/tests/mmc3_test/4-scanline_timing.nes"),0);
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/mmc3_test/5-MMC3.nes"),482325539);
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/mmc3_test/6-MMC3_alt.nes"),0);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
		if(false){
			testoutput+= "\n OAM Tests\n\n";
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/oam_read.nes"),878913754);
			testrom(30000, new File(System.getProperty("user.dir")+"/tests/oam_stress.nes"),86089087);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
		if(false){
			testoutput+= "\n PPU ReadBuffer Mega test\n\n";
			testrom(27000, new File(System.getProperty("user.dir")+"/tests/test_ppu_read_buffer.nes"),-762073439);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
		if(false){
			testoutput+= "\n PPU OpenBus test\n\n";
			testrom(7000, new File(System.getProperty("user.dir")+"/tests/ppu_open_bus.nes"),0);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
		if(false){
			testoutput+= "\n CPU Execution Space tests\n\n";
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/execspace/test_cpu_exec_space_ppuio.nes"),1133191816);
			testrom(7000, new File(System.getProperty("user.dir")+"/tests/execspace/test_cpu_exec_space_apu.nes"),-1164932952);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
		if(false){
			testoutput+= "\n CPU Dummy Write tests\n\n";
			testrom(8000, new File(System.getProperty("user.dir")+"/tests/dummywrites/cpu_dummy_writes_oam.nes"),1310446203);
			testrom(8000, new File(System.getProperty("user.dir")+"/tests/dummywrites/cpu_dummy_writes_ppumem.nes"),-1007081423);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
		nes.flag=false;
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
		nes = new NES(rom,this);
		current = new Thread(nes);
		current.start();
		Thread.sleep(delay);
	}
	public void saveState(int slot){
		nes.pause=true;
		try {
			nes.saveState("savestateY.txt".replaceAll("Y", slot+""));
		} catch (IOException e) {
			e.printStackTrace();
		}
		nes.pause=false;
	}
	public void restoreState(int slot){
		nes.pause=true;
		try {
			nes.restoreState("savestateY.txt".replaceAll("Y", slot+""));
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}
	public boolean[][] pollController(){
		return keys.currentKeys();	
	}
	public void videoCallback(int[] p){
		pixels=p;
		dispatch();
		//display.sendFrame(pixels);
	}
	private void dispatch(){
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run(){
                listener.doframe();
            }
        
        });
    }
}
