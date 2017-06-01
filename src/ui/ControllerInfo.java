package ui;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

public class ControllerInfo {
	String controllername;
	Component.Identifier id;
	float val;
	public ControllerInfo(String name,Component.Identifier i,float v){
		val = v;
		id = i;
		controllername = name;
	}
	
	public boolean checkPressed(){
		Controller[] cont = ControllerEnvironment.getDefaultEnvironment().getControllers();
		for(Controller c: cont){
			if(c.getName().equals(controllername)){
				c.poll();
				return c.getComponent(id).getPollData() == val;
			}
		}
		return false;
	}
	public String toString(){
		return id.toString();
	}
	public String storeInfo(){
		return controllername+";"+id.getName()+";"+val;
	}
	public static ControllerInfo restoreInfo(String s,String d){
		if(s!=null){
			String[] info = s.split(";");
			float var = Float.parseFloat(info[2]);
			Controller[] cont = ControllerEnvironment.getDefaultEnvironment().getControllers();
			for(Controller c: cont){
				if(c.getName().equals(info[0]))
				for(Component comp:c.getComponents())
					if(comp.getIdentifier().getName().equals(info[1])){
						return new ControllerInfo(info[0],comp.getIdentifier(),var);
					}
			}
		}
		System.out.println("Not found :( Loading default...");
		return restoreInfo(d,"null");
	}
}
