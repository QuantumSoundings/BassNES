package ui.debugger;

import ui.SystemManager;

public class BreakPoint {
	public enum Variable{ProgramCounter,A,X,Y,SP,Nflag,Vflag,Dflag,Iflag,Zflag,Cflag,Scanline,ppucycle,Verticalblank,Sprite0,Spriteoverflow,Irqexternal,Irqframe,Irqdmc,Nmi};
	private Variable var;
	private Object value;
	private boolean enabled;
	private static DebugCallback sys;
	private static Object[] lastcpudata;
	private static Object[] lastppudata;
	public BreakPoint(Variable var, Object value){
		this.var =var;
		this.value = value;
		enabled = true;
	}
	public static void setsystem(SystemManager s){
		sys = s;
	}
	public static void updateData(){
		lastcpudata = sys.getCPUDebuggingInformation();
		lastppudata = sys.getPPUDebuggingInformation();
	}
	public void toggleEnable(){
		enabled = !enabled;
	}
	public boolean checkbreakpoint(){
		if(enabled){
			switch(var){
			case ProgramCounter:
				return (int)lastcpudata[0] == (int)value;
			case A:
				return (int)lastcpudata[4] == (int)value;
			case X:
				return (int)lastcpudata[5] == (int)value;
			case Y:
				return (int)lastcpudata[6] == (int)value;
			case SP:
				return (int)lastcpudata[3] == (int)value;
			case Nflag:
				return (boolean)lastcpudata[7] == (boolean)value;
			case Vflag:
				return (boolean)lastcpudata[8] == (boolean)value;
			case Dflag:
				return (boolean)lastcpudata[9] == (boolean)value;
			case Iflag:
				return (boolean)lastcpudata[10] == (boolean)value;
			case Zflag:
				return (boolean)lastcpudata[11] == (boolean)value;
			case Cflag:
				return (boolean)lastcpudata[12] == (boolean)value;
			case Scanline:
				return (int)lastppudata[1] == (int)value;
			case ppucycle:
				return (int)lastppudata[0] == (int)value;
			case Verticalblank:
				return (boolean)lastppudata[2] == (boolean)value;
			case Sprite0:
				return (boolean)lastppudata[4] == (boolean)value;
			case Spriteoverflow:
				return (boolean)lastppudata[3] == (boolean)value;
			case Irqexternal:
				return ((boolean[])lastcpudata[13])[0] == (boolean)value;
			case Irqframe:
				return ((boolean[])lastcpudata[13])[1] == (boolean)value;
			case Irqdmc:
				return ((boolean[])lastcpudata[13])[2] == (boolean)value;
			case Nmi:
				return (boolean)lastcpudata[14] == (boolean)value;
			default: break;
			}
		}
		return false;
	}
	@Override
	public String toString(){
		String val = value+"";
		if(!val.equals("true")&&!val.equals(false))
			val = "$"+Integer.toHexString((int) value);
		return "Variable: "+var +"  Value: "+val +"  Enabled: " +enabled ;
	}
	
}
