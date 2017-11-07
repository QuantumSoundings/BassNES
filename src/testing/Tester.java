package testing;

import ui.SystemManager;

public class Tester {
	SystemManager sys;
	public Tester(SystemManager s){
		sys = s;
	}
	/*public void testRomSet(){
		//fc.setDirectory("C\\:\\Users\\Jordan\\Downloads\\NESrompack\\NESroms\\USA\\");
		sys.fc.setCurrentDirectory(new File(UISettings.lastLoadedDir));
		File folder = sys.fc.getCurrentDirectory();
		File[] files =folder.listFiles();
		for(File f:files){
			if(f.isDirectory()){
				countRomsInDir(f);
			}
		}
		java.lang.System.out.println(output);
		java.lang.System.out.println("Total Overall Roms: "+totalroms+ " Overall Unsupported roms: "+ totalun);
		java.lang.System.out.println("Roms with Unexpected Errors: "+othererrors);
		for(int i = 0;i<256;i++){
			if(totalmappernumber[i]>0)
				java.lang.System.out.println("Found Mapper "+i+": "+totalmappernumber[i]+" roms");
		}
		
	}
	String output ="";
	int othererrors = 0;
	int totalroms=0;
	int totalun = 0;
	int[] totalmappernumber = new int[256];
	public void countRomsInDir(File dir){
		File[] listofFiles = dir.listFiles();
		int total = 0,unsupported=0;
		int[] mappernumber = new int[256];
		for(File rom: listofFiles){
			total++;
			totalroms++;
			try {
				sys.nes = new NES(sys);
				sys.nes.loadRom(rom);
			} catch (UnSupportedMapperException e) {
				//e.printStackTrace();
				unsupported++;
				totalun++;
				mappernumber[e.mapperid]++;
				totalmappernumber[e.mapperid]++;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e){
				othererrors++;
				java.lang.System.err.println("Unexpected error has occured during rom loading please report this bug.");
				java.lang.System.err.println("Rom location: "+rom.getAbsolutePath());
				e.printStackTrace();
			}
		}
		output+="Dir: "+dir.getAbsolutePath()+" =======================================================================\n"+"\n";
		output+="Total roms: "+total+" Unsupported roms: "+unsupported+"\n";
		for(int i = 0;i<256;i++){
			if(mappernumber[i]>0)
				output+="Found Mapper "+i+": "+mappernumber[i]+" roms"+"\n";
		}
	}
	void runTests(){
		try {
			runTests2();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	int pass,fail,totalpass,total,regression;
	String testoutput;
	void runTests2() throws InterruptedException{
		Thread.sleep(500);
		long starttime = java.lang.System.nanoTime();
		NesSettings.frameLimit=false;
		testoutput="";
		boolean all = true;
		int speed = 2;
        if(false|all){
			testoutput = " Blargg PPU Tests \n\n";
			testrom(5000, new File(java.lang.System.getProperty("user.dir")+"/tests/blarggppu/sprite_ram.nes"),-991011135 );
			testrom(2000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/blarggppu/palette_ram.nes"),-991011135 );
			testrom(2000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/blarggppu/power_up_palette.nes"),-991011135);
			testrom(2000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/blarggppu/vbl_clear_time.nes"),-991011135 );
			testrom(2000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/blarggppu/vram_access.nes"),-991011135 );
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
        if(false|all){
			testoutput+= "\n Blargg CPU Tests \n\n";
			testrom(4000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/blarggcpu/01-basics.nes"),898324673);
			testrom(3000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/blarggcpu/02-implied.nes"),1454203073);
			testrom(3000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/blarggcpu/03-immediate.nes"),1521555009);
			testrom(3000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/blarggcpu/04-zero_page.nes"),-806086527);
			testrom(7000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/blarggcpu/05-zp_xy.nes"),-502248511 );
			testrom(3000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/blarggcpu/06-absolute.nes"),-1086829759);
			testrom(9000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/blarggcpu/07-abs_xy.nes"),-342091903);
			testrom(3000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/blarggcpu/08-ind_x.nes"),-1862521471);
			testrom(3000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/blarggcpu/09-ind_y.nes"),316435457);
			testrom(2000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/blarggcpu/10-branches.nes"),1739410241);
			testrom(5000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/blarggcpu/11-stack.nes"),1998261697 );
			testrom(3000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/blarggcpu/12-jmp_jsr.nes"),836421185 );
			testrom(2000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/blarggcpu/13-rts.nes"),1585305601 );
			testrom(2000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/blarggcpu/14-rti.nes"),1973438849 );
			testrom(2000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/blarggcpu/15-brk.nes"),975250241);
			testrom(2000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/blarggcpu/16-special.nes"),-1709185727);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
        if(false|all){
			testoutput+= "\n Blargg All CPU Instructions Test\n\n";
			testrom(30000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/blarggcpu/all_inst.nes"),-1295142399);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
        if(false|all){
			testoutput+= "\n Blargg APU Tests \n\n";
			testrom(2000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/blarggapu/01.len_ctr.nes"),-991011135);
			testrom(2000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/blarggapu/02.len_table.nes"),-991011135);
			testrom(2000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/blarggapu/03.irq_flag.nes"),-991011135);
			testrom(2000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/blarggapu/04.clock_jitter.nes"),-991011135);
			testrom(2000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/blarggapu/05.len_timing_mode0.nes"),-991011135);
			testrom(2000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/blarggapu/06.len_timing_mode1.nes"),-991011135);
			testrom(2000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/blarggapu/07.irq_flag_timing.nes"),-991011135);
			testrom(2000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/blarggapu/08.irq_timing.nes"),-991011135);
			testrom(2000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/blarggapu/09.reset_timing.nes"),-991011135);
			testrom(2000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/blarggapu/10.len_halt_timing.nes"),-991011135);
			testrom(2000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/blarggapu/11.len_reload_timing.nes"),-991011135);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
        if(true|all){
        	testoutput+= "\n Various DMC Tests \n\n";
			testrom(2000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/dmc/dma_2007_read.nes"),0);
			testrom(2000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/dmc/dma_2007_write.nes"),41608769);
			testrom(2000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/dmc/dma_4016_read.nes"),0);
			testrom(2000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/dmc/double_2007_read.nes"),0);
			testrom(2000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/dmc/read_write_2007.nes"),498004161);
			testrom(2000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/dmc/sprdma_and_dmc_dma.nes"),0);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
        }
        if(false|all){
			testoutput+= "\n PPU_VBL_NMI Tests \n\n";
			testrom(5000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/ppu_vbl_nmi/01-vbl_basics.nes"),1036527745);
			testrom(5000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/ppu_vbl_nmi/02-vbl_set_time.nes"),-236117247 );
			testrom(5000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/ppu_vbl_nmi/03-vbl_clear_time.nes"),-2105808895);
			testrom(3000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/ppu_vbl_nmi/04-nmi_control.nes"),-441830719);
			testrom(5000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/ppu_vbl_nmi/05-nmi_timing.nes"),1100088705);
			testrom(5000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/ppu_vbl_nmi/06-suppression.nes"),2047057985);
			testrom(5000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/ppu_vbl_nmi/07-nmi_on_timing.nes"),-488676223);
			testrom(5000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/ppu_vbl_nmi/08-nmi_off_timing.nes"),-343529791);
			testrom(5000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/ppu_vbl_nmi/09-even_odd_frames.nes"),2076043777 );
			testrom(5000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/ppu_vbl_nmi/10-even_odd_timing.nes"),-698856319);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
        if(false|all){
			testoutput+="\n Sprite Zero Hit Tests\n\n";
			testrom(3000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/sprite_hit/01-basics.nes"),898324673);
			testrom(3000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/sprite_hit/02-alignment.nes"),-581511743);
			testrom(3000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/sprite_hit/03-corners.nes"),1488564929);
			testrom(3000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/sprite_hit/04-flip.nes"),1908589121 );
			testrom(3000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/sprite_hit/05-left_clip.nes"),-1371701439);
			testrom(3000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/sprite_hit/06-right_edge.nes"),1713394433);
			testrom(3000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/sprite_hit/07-screen_bottom.nes"),-884640127 );
			testrom(3000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/sprite_hit/08-double_height.nes"),-1394642303);
			testrom(5000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/sprite_hit/09-timing.nes"),-1401514303 );
			testrom(5000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/sprite_hit/10-timing_order.nes"),1518391041);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
        if(false|all){
			testoutput+="\n Sprite Overflow Tests\n\n";
			testrom(3000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/sprite_overflow/01-basics.nes"),898324673);
			testrom(3000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/sprite_overflow/02-details.nes"),-1629941183);
			testrom(7000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/sprite_overflow/03-timing.nes"),-284309695);
			testrom(3000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/sprite_overflow/04-obscure.nes"),1795452865);
			testrom(3000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/sprite_overflow/05-emulator.nes"),-1455436927);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
        if(false|all){
			testoutput+="\n Instruction Misc Tests\n\n";
			testrom(3000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/instr_misc/01-abs_x_wrap.nes"),-1928374463 );
			testrom(3000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/instr_misc/02-branch_wrap.nes"),1758264129);
			testrom(3000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/instr_misc/03-dummy_reads.nes"),-868017919);
			testrom(7000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/instr_misc/04-dummy_reads_apu.nes"),-1866594495);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
        if(false|all){
			testoutput+="\n CPU Interrupts\n\n";
			testrom(3000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/cpu_interrupts/1-cli_latency.nes"),-688698111 );
			testrom(3000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/cpu_interrupts/2-nmi_and_brk.nes"),-1869123263);
			testrom(3000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/cpu_interrupts/3-nmi_and_irq.nes"),1866557697);
			testrom(5000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/cpu_interrupts/4-irq_and_dma.nes"),1806268353);
			testrom(7000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/cpu_interrupts/5-branch_delays_irq.nes"),806032449);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
        if(false|all){
			testoutput+="\n Instruction timing\n\n";
			testrom(23000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/1-instr_timing.nes"),73520321 );
			testrom(3000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/1-Branch_Basics.nes"),-947644927);
			testrom(3000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/2.Backward_Branch.nes"),787820545);
			testrom(3000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/3.Forward_Branch.nes"),1758476225);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
        if(false|all){
			testoutput+= "\n MMC3 Tests\n\n";
			testrom(3000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/mmc3_test/1-clocking.nes"),-1238792959);
			testrom(3000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/mmc3_test/2-details.nes"),-2075649855);
			testrom(3000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/mmc3_test/3-A12_clocking.nes"),-53768703);
			testrom(7000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/mmc3_test/4-scanline_timing.nes"),1752656001);
			testrom(3000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/mmc3_test/5-MMC3.nes"),-83482111);
			testrom(3000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/mmc3_test/6-MMC3_alt.nes"),0);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
        if(false|all){
			testoutput+= "\n OAM Tests\n\n";
			testrom(3000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/oam_read.nes"),-771118655);
			testrom(30000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/oam_stress.nes"),-461204351);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
        if(false|all){
			testoutput+= "\n PPU ReadBuffer Mega test\n\n";
			testrom(27000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/test_ppu_read_buffer.nes"),-1484609023 );
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
        if(false|all){
			testoutput+= "\n PPU OpenBus test\n\n";
			testrom(7000/speed, new File(SystemManager.getProperty("user.dir")+"/tests/ppu_open_bus.nes"),0);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
        if(false|all){
			testoutput+= "\n CPU Execution Space tests\n\n";
			testrom(3000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/execspace/test_cpu_exec_space_ppuio.nes"),-939378239);
			testrom(7000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/execspace/test_cpu_exec_space_apu.nes"),-627381823);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
        if(false|all){
			testoutput+= "\n CPU Dummy Write tests\n\n";
			testrom(8000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/dummywrites/cpu_dummy_writes_oam.nes"), 801265537);
			testrom(8000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/dummywrites/cpu_dummy_writes_ppumem.nes"),-2086578175);
			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
		}
        if(false|all){
        	testoutput+= "\n Holy Diver Batman tests\n\n";
			//testrom(10000/speed, new File(SystemManager.getProperty("user.dir")+"/tests/holydiver/M0_P32K_C8K_V.nes"),86500417);
			//testrom(10000/speed, new File(SystemManager.getProperty("user.dir")+"/tests/holydiver/M1_P128K_C128K_S8K.nes"),0);
			//testrom(10000/speed, new File(SystemManager.getProperty("user.dir")+"/tests/holydiver/M1_P128K_C128K_W8K.nes"),0);
			//testrom(10000/speed, new File(SystemManager.getProperty("user.dir")+"/tests/holydiver/M1_P128K_C128K.nes"),0);
			//testrom(10000/speed, new File(SystemManager.getProperty("user.dir")+"/tests/holydiver/M1_P128K_C32K_S8K.nes"),0);
			//testrom(10000/speed, new File(SystemManager.getProperty("user.dir")+"/tests/holydiver/M1_P128K_C32K_W8K.nes"),0);
			//testrom(10000/speed, new File(SystemManager.getProperty("user.dir")+"/tests/holydiver/M1_P128K_C32K.nes"),0);
			testrom(10000/speed, new File(java.lang.System.getProperty("user.dir")+"/tests/holydiver/M1_P128K.nes"),0);
			//testrom(10000/speed, new File(SystemManager.getProperty("user.dir")+"/tests/holydiver/M1_P512K_S32K.nes"),0);
			//testrom(10000/speed, new File(SystemManager.getProperty("user.dir")+"/tests/holydiver/M1_P512K_S8K.nes"),0);


			testoutput += "\n "+pass +"/"+(pass+fail)+" Passed\n";totalpass+=pass;total+=(pass+fail);pass=0;fail=0;
        }
        if(sys.nes!=null)
        	sys.nes.exit();
		testoutput+= "\n\n Overall results: "+totalpass+"/"+total+" Passed     " +(regression>0?regression+" Regressions":"");
		java.lang.System.out.println(testoutput);
		long stoptime = java.lang.System.nanoTime();
		java.lang.System.out.println("Completed Regression testing in: "+((stoptime-starttime)/1000000000)+"seconds.");
		NesSettings.frameLimit=true;
	}
	void testrom(int delay,File r,int goodhash) throws InterruptedException{
		BufferedImage bi = new BufferedImage(sys.display.getWidth(),sys.display.getHeight(),BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bi.createGraphics();
		sys.rom  = r;
		int hash=0;
		reset();startnes(delay);sys.display.paint(g);hash = getHash(bi);
		if(goodhash!=0&&hash!=goodhash){
            testoutput +="Test: "+((hash==goodhash)?"PASS":"FAIL") +" Name: "+ sys.rom.getName()+" Hash: "+hash+"                 ****REGRESSION WARNING****\n";
			regression++;
		}
		else
			testoutput +="Test: "+((hash==goodhash)?"PASS":"FAIL") +" Name: "+ sys.rom.getName()+" Hash: "+hash+"\n";
		if(hash==goodhash)pass++;else fail++;
	}
	void reset() throws InterruptedException{
		if(sys.nes!=null)
			sys.nes.exit();
		Thread.sleep(500);
	}
	int getHash(BufferedImage bufferedImage){
		int[] pixels;
		pixels = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();
		return Arrays.hashCode(pixels);
	}
	void startnes(int delay) throws InterruptedException {
		sys.createNES(sys.rom);
		sys.current = new Thread(sys.nes);
		sys.current.start();
		Thread.sleep(delay);
	}
	*/
}
