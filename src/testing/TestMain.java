package testing;

import java.io.File;

import core.NES;
import ui.SystemUI;

public class TestMain {
	static TestDisplay sys;
	static SystemUI sys2;
	static File rom;
	static NES nes;
	static int pass;
	static int fail;
	static int totalpass;
	static int total;
	static int regression;
	static String testoutput;
	public static void main(String[] args) {
		boolean visualtest = false;
		
		
		if(visualtest){
			sys2 = new SystemUI();
			Tester test = new Tester(sys2);
			try {
				test.runTests2();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		else{
			sys = new TestDisplay();
			runTests2();
		}
		
	}
	static void runTests2(){
		testoutput="";
		boolean all = true;
		long starttime = System.nanoTime();
        if(false|all){
			testoutput = " Blargg PPU Tests \n\n";
			testrom(30, new File(System.getProperty("user.dir")+"/tests/blarggppu/sprite_ram.nes"),-404250338 );
			testrom(30, new File(System.getProperty("user.dir")+"/tests/blarggppu/palette_ram.nes"),-404250338 );
			testrom(30, new File(System.getProperty("user.dir")+"/tests/blarggppu/power_up_palette.nes"),-404250338);
			testrom(30, new File(System.getProperty("user.dir")+"/tests/blarggppu/vbl_clear_time.nes"),-404250338 );
			testrom(30, new File(System.getProperty("user.dir")+"/tests/blarggppu/vram_access.nes"),-404250338 );
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
        if(false|all){
			testoutput+= "\n Blargg CPU Tests \n\n";
			testrom(200, new File(System.getProperty("user.dir")+"/tests/blarggcpu/01-basics.nes"),-1847327932 );
			testrom(200, new File(System.getProperty("user.dir")+"/tests/blarggcpu/02-implied.nes"),1295495714);
			testrom(200, new File(System.getProperty("user.dir")+"/tests/blarggcpu/03-immediate.nes"),976738328);
			testrom(200, new File(System.getProperty("user.dir")+"/tests/blarggcpu/04-zero_page.nes"),932145569);
			testrom(300, new File(System.getProperty("user.dir")+"/tests/blarggcpu/05-zp_xy.nes"),939850466 );
			testrom(200, new File(System.getProperty("user.dir")+"/tests/blarggcpu/06-absolute.nes"),-891210050);
			testrom(500, new File(System.getProperty("user.dir")+"/tests/blarggcpu/07-abs_xy.nes"),-1961601469);
			testrom(200, new File(System.getProperty("user.dir")+"/tests/blarggcpu/08-ind_x.nes"),943828897);
			testrom(200, new File(System.getProperty("user.dir")+"/tests/blarggcpu/09-ind_y.nes"),64377247);
			testrom(200, new File(System.getProperty("user.dir")+"/tests/blarggcpu/10-branches.nes"),1388702022);
			testrom(200, new File(System.getProperty("user.dir")+"/tests/blarggcpu/11-stack.nes"),-2070109858 );
			testrom(200, new File(System.getProperty("user.dir")+"/tests/blarggcpu/12-jmp_jsr.nes"),2023643492 );
			testrom(200, new File(System.getProperty("user.dir")+"/tests/blarggcpu/13-rts.nes"),1992523615 );
			testrom(200, new File(System.getProperty("user.dir")+"/tests/blarggcpu/14-rti.nes"),-765307521 );
			testrom(200, new File(System.getProperty("user.dir")+"/tests/blarggcpu/15-brk.nes"),1888950082);
			testrom(200, new File(System.getProperty("user.dir")+"/tests/blarggcpu/16-special.nes"),1282163104);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
        if(false|all){
			testoutput+= "\n Blargg All CPU Instructions Test\n\n";
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/blarggcpu/all_inst.nes"),-329060991);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
        if(false|all){
			testoutput+= "\n Blargg APU Tests \n\n";
			testrom(100, new File(System.getProperty("user.dir")+"/tests/blarggapu/01.len_ctr.nes"),-404250338);
			testrom(100, new File(System.getProperty("user.dir")+"/tests/blarggapu/02.len_table.nes"),-404250338);
			testrom(100, new File(System.getProperty("user.dir")+"/tests/blarggapu/03.irq_flag.nes"),-404250338);
			testrom(100, new File(System.getProperty("user.dir")+"/tests/blarggapu/04.clock_jitter.nes"),-404250338);
			testrom(100, new File(System.getProperty("user.dir")+"/tests/blarggapu/05.len_timing_mode0.nes"),-404250338);
			testrom(100, new File(System.getProperty("user.dir")+"/tests/blarggapu/06.len_timing_mode1.nes"),-404250338);
			testrom(100, new File(System.getProperty("user.dir")+"/tests/blarggapu/07.irq_flag_timing.nes"),-404250338);
			testrom(100, new File(System.getProperty("user.dir")+"/tests/blarggapu/08.irq_timing.nes"),-404250338);
			testrom(100, new File(System.getProperty("user.dir")+"/tests/blarggapu/09.reset_timing.nes"),-404250338);
			testrom(100, new File(System.getProperty("user.dir")+"/tests/blarggapu/10.len_halt_timing.nes"),-404250338);
			testrom(100, new File(System.getProperty("user.dir")+"/tests/blarggapu/11.len_reload_timing.nes"),-404250338);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
        if(false|all){
        	testoutput+= "\n Various DMC Tests \n\n";
			testrom(200, new File(System.getProperty("user.dir")+"/tests/dmc/dma_2007_read.nes"),0);
			testrom(200, new File(System.getProperty("user.dir")+"/tests/dmc/dma_2007_write.nes"),465185332);
			testrom(200, new File(System.getProperty("user.dir")+"/tests/dmc/dma_4016_read.nes"),0);
			testrom(200, new File(System.getProperty("user.dir")+"/tests/dmc/double_2007_read.nes"),0);
			testrom(200, new File(System.getProperty("user.dir")+"/tests/dmc/read_write_2007.nes"),1533671364);
			testrom(200, new File(System.getProperty("user.dir")+"/tests/dmc/sprdma_and_dmc_dma.nes"),0);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
        }
        if(false|all){
			testoutput+= "\n PPU_VBL_NMI Tests \n\n";
			testrom(200, new File(System.getProperty("user.dir")+"/tests/ppu_vbl_nmi/01-vbl_basics.nes"),1555809031);
			testrom(200, new File(System.getProperty("user.dir")+"/tests/ppu_vbl_nmi/02-vbl_set_time.nes"),645087107 );
			testrom(200, new File(System.getProperty("user.dir")+"/tests/ppu_vbl_nmi/03-vbl_clear_time.nes"),-462471365);
			testrom(200, new File(System.getProperty("user.dir")+"/tests/ppu_vbl_nmi/04-nmi_control.nes"),-4523038);
			testrom(300, new File(System.getProperty("user.dir")+"/tests/ppu_vbl_nmi/05-nmi_timing.nes"),-82911921);
			testrom(300, new File(System.getProperty("user.dir")+"/tests/ppu_vbl_nmi/06-suppression.nes"),952345304);
			testrom(200, new File(System.getProperty("user.dir")+"/tests/ppu_vbl_nmi/07-nmi_on_timing.nes"),-838672187);
			testrom(300, new File(System.getProperty("user.dir")+"/tests/ppu_vbl_nmi/08-nmi_off_timing.nes"),-246060542);
			testrom(200, new File(System.getProperty("user.dir")+"/tests/ppu_vbl_nmi/09-even_odd_frames.nes"),-1970593685 );
			testrom(200, new File(System.getProperty("user.dir")+"/tests/ppu_vbl_nmi/10-even_odd_timing.nes"),1397079077);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
        if(true|all){
			testoutput+="\n Sprite Zero Hit Tests\n\n";
			testrom(200, new File(System.getProperty("user.dir")+"/tests/sprite_hit/01-basics.nes"),-1847327932);
			testrom(200, new File(System.getProperty("user.dir")+"/tests/sprite_hit/02-alignment.nes"),-1793318754);
			testrom(200, new File(System.getProperty("user.dir")+"/tests/sprite_hit/03-corners.nes"),-546976636);
			testrom(200, new File(System.getProperty("user.dir")+"/tests/sprite_hit/04-flip.nes"),623928834 );
			testrom(200, new File(System.getProperty("user.dir")+"/tests/sprite_hit/05-left_clip.nes"),136303456);
			testrom(200, new File(System.getProperty("user.dir")+"/tests/sprite_hit/06-right_edge.nes"),199167515);
			testrom(200, new File(System.getProperty("user.dir")+"/tests/sprite_hit/07-screen_bottom.nes"),-1118900669 );
			testrom(200, new File(System.getProperty("user.dir")+"/tests/sprite_hit/08-double_height.nes"),-1385276225);
			testrom(300, new File(System.getProperty("user.dir")+"/tests/sprite_hit/09-timing.nes"),702553020 );
			testrom(300, new File(System.getProperty("user.dir")+"/tests/sprite_hit/10-timing_order.nes"),953299743);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
        if(true|all){
			testoutput+="\n Sprite Overflow Tests\n\n";
			testrom(200, new File(System.getProperty("user.dir")+"/tests/sprite_overflow/01-basics.nes"),-1847327932);
			testrom(200, new File(System.getProperty("user.dir")+"/tests/sprite_overflow/02-details.nes"),-1771473218);
			testrom(400, new File(System.getProperty("user.dir")+"/tests/sprite_overflow/03-timing.nes"),-152874148);
			testrom(200, new File(System.getProperty("user.dir")+"/tests/sprite_overflow/04-obscure.nes"),-336555516);
			testrom(200, new File(System.getProperty("user.dir")+"/tests/sprite_overflow/05-emulator.nes"),1351578683);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
        if(true|all){
			testoutput+="\n Instruction Misc Tests\n\n";
			testrom(200, new File(System.getProperty("user.dir")+"/tests/instr_misc/01-abs_x_wrap.nes"),910572068 );
			testrom(200, new File(System.getProperty("user.dir")+"/tests/instr_misc/02-branch_wrap.nes"),1680242374);
			testrom(200, new File(System.getProperty("user.dir")+"/tests/instr_misc/03-dummy_reads.nes"),-726594183);
			testrom(400, new File(System.getProperty("user.dir")+"/tests/instr_misc/04-dummy_reads_apu.nes"),218104826);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
        if(true|all){
			testoutput+="\n CPU Interrupts\n\n";
			testrom(300, new File(System.getProperty("user.dir")+"/tests/cpu_interrupts/1-cli_latency.nes"),703149629 );
			testrom(300, new File(System.getProperty("user.dir")+"/tests/cpu_interrupts/2-nmi_and_brk.nes"),-274839776);
			testrom(300, new File(System.getProperty("user.dir")+"/tests/cpu_interrupts/3-nmi_and_irq.nes"),-1327566857);
			testrom(500, new File(System.getProperty("user.dir")+"/tests/cpu_interrupts/4-irq_and_dma.nes"),-1738127600);
			testrom(700, new File(System.getProperty("user.dir")+"/tests/cpu_interrupts/5-branch_delays_irq.nes"),-676019686);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
        if(true|all){
			testoutput+="\n Instruction timing\n\n";
			testrom(2300, new File(System.getProperty("user.dir")+"/tests/1-instr_timing.nes"),-552761986 );
			testrom(300, new File(System.getProperty("user.dir")+"/tests/1-Branch_Basics.nes"),-673283695);
			testrom(300, new File(System.getProperty("user.dir")+"/tests/2.Backward_Branch.nes"),1278455359);
			testrom(300, new File(System.getProperty("user.dir")+"/tests/3.Forward_Branch.nes"),-870059842);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
        if(true|all){
			testoutput+= "\n MMC3 Tests\n\n";
			testrom(300, new File(System.getProperty("user.dir")+"/tests/mmc3_test/1-clocking.nes"),-694302881);
			testrom(300, new File(System.getProperty("user.dir")+"/tests/mmc3_test/2-details.nes"),1200065212);
			testrom(300, new File(System.getProperty("user.dir")+"/tests/mmc3_test/3-A12_clocking.nes"),-680592989);
			testrom(700, new File(System.getProperty("user.dir")+"/tests/mmc3_test/4-scanline_timing.nes"),-1566816803);
			testrom(300, new File(System.getProperty("user.dir")+"/tests/mmc3_test/5-MMC3.nes"),2145922979);
			testrom(300, new File(System.getProperty("user.dir")+"/tests/mmc3_test/6-MMC3_alt.nes"),0);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
        if(true|all){
			testoutput+= "\n OAM Tests\n\n";
			testrom(300, new File(System.getProperty("user.dir")+"/tests/oam_read.nes"),1100990362);
			testrom(3000, new File(System.getProperty("user.dir")+"/tests/oam_stress.nes"),-1179422017);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
        if(true|all){
			testoutput+= "\n PPU ReadBuffer Mega test\n\n";
			testrom(2700, new File(System.getProperty("user.dir")+"/tests/test_ppu_read_buffer.nes"),2026498101 );
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
        if(false|all){
			testoutput+= "\n PPU OpenBus test\n\n";
			testrom(700, new File(System.getProperty("user.dir")+"/tests/ppu_open_bus.nes"),0);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
        if(true|all){
			testoutput+= "\n CPU Execution Space tests\n\n";
			testrom(300, new File(System.getProperty("user.dir")+"/tests/execspace/test_cpu_exec_space_ppuio.nes"),-1735327736);
			testrom(700, new File(System.getProperty("user.dir")+"/tests/execspace/test_cpu_exec_space_apu.nes"),-38474200);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
        if(true|all){
			testoutput+= "\n CPU Dummy Write tests\n\n";
			testrom(800, new File(System.getProperty("user.dir")+"/tests/dummywrites/cpu_dummy_writes_oam.nes"), 1571388347);
			testrom(800, new File(System.getProperty("user.dir")+"/tests/dummywrites/cpu_dummy_writes_ppumem.nes"),-1080981647);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
        if(false|all){
        	testoutput+= "\n Holy Diver Batman tests\n\n";
			//testrom(10000, new File(System.getProperty("user.dir")+"/tests/holydiver/M0_P32K_C8K_V.nes"),86500417);
			//testrom(10000, new File(System.getProperty("user.dir")+"/tests/holydiver/M1_P128K_C128K_S8K.nes"),0);
			//testrom(10000, new File(System.getProperty("user.dir")+"/tests/holydiver/M1_P128K_C128K_W8K.nes"),0);
			//testrom(10000, new File(System.getProperty("user.dir")+"/tests/holydiver/M1_P128K_C128K.nes"),0);
			//testrom(10000, new File(System.getProperty("user.dir")+"/tests/holydiver/M1_P128K_C32K_S8K.nes"),0);
			//testrom(10000, new File(System.getProperty("user.dir")+"/tests/holydiver/M1_P128K_C32K_W8K.nes"),0);
			//testrom(10000, new File(System.getProperty("user.dir")+"/tests/holydiver/M1_P128K_C32K.nes"),0);
			//testrom(10000, new File(System.getProperty("user.dir")+"/tests/holydiver/M1_P128K.nes"),0);
			//testrom(10000, new File(System.getProperty("user.dir")+"/tests/holydiver/M1_P512K_S32K.nes"),0);
			//testrom(10000, new File(System.getProperty("user.dir")+"/tests/holydiver/M1_P512K_S8K.nes"),0);


			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
        }
		testoutput+= "\n\n Overall results: "+totalpass+"/"+total+" Passed     " +(regression>0?regression+" Regressions":"");
		System.out.println(testoutput);
		long stoptime = System.nanoTime();
		System.out.println("Completed Regression testing in: "+((stoptime-starttime)/1000000000.0)+"seconds.");
	}
	static void testrom(int delay,File r,int goodhash){
		rom  = r;
		int hash=0;
		TestUtils.createNES(rom, sys);
		for(int i = 0; i<delay;i++){
			nes.runFrame();
			if(TestUtils.getPixelArrayHash(sys.pixels)==goodhash)
				break;
		}
		hash = TestUtils.getPixelArrayHash(sys.pixels);
		if(goodhash!=0&&hash!=goodhash){
            testoutput +="Test: "+((hash==goodhash)?"PASS":"FAIL") +" Name: "+ rom.getName()+" Hash: "+hash+"                 ****REGRESSION WARNING****\n";
			regression++;
		}
		else
			testoutput +="Test: "+((hash==goodhash)?"PASS":"FAIL") +" Name: "+ rom.getName()+" Hash: "+hash+ ((hash==goodhash)?"":"             ----Expected Failure---")+"\n";
		if(hash==goodhash)pass++;else fail++;
	}
	
}
