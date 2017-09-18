package ui;

import java.awt.Color;
import java.util.ArrayList;

public final class OSD {
	public static class OSDElement {
		public String message;
		public int timer;//in frames
		public Color color;
		public OSDElement(String m, int t){
			message = m;
			timer = t;
			color = Color.WHITE;
		}
		public OSDElement(String m, int t, Color c){
			message = m; timer = t;
			color = c;
		}
	}
	public static enum position{Top_left, Bottom_left,Top_right,Bottom_right};
	public static position selectedposition = position.Top_left;
	public static ArrayList<OSDElement> OSD_messages = new ArrayList<OSDElement>();
	
	public static void addOSDMessage(String message, int timer){
		OSD_messages.add(new OSDElement(message,timer));
	}
	public static void addColoredOSDMessage(String message, int timer, Color color){
		OSD_messages.add(new OSDElement(message,timer,color));
	}
}
