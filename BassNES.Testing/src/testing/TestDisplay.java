package testing;

import core.NESCallback;

public class TestDisplay implements NESCallback{
	int[] pixels;
	public TestDisplay(){
		
	}
	@Override
	public void videoCallback(int[] pixels) {
		this.pixels = pixels;
	}

	@Override
	public void unmixedAudioSampleCallback(int[] audiosample) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void audioSampleCallback(int audiosample) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void audioFrameCallback(int[] audioInts) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean[][] pollController() {
		// TODO Auto-generated method stub
		return new boolean[][]{{false,false,false,false,false,false,false,false},{false,false,false,false,false,false,false,false}};
	}

}
