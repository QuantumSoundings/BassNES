package ui;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

public class ControllerInfo {
	Controller[] controllers;
	Controller control;
	Component.Identifier id;
	String idname;
	float val;
	public ControllerInfo(Controller c,Component.Identifier i,float v){
		val = v;
		if(i==null){
			idname = "N/A";
		}
		else
			idname = i.getName();
		id = i;
		control = c;
		controllers =ControllerEnvironment.getDefaultEnvironment().getControllers();
	}
	
	public boolean checkPressed(){
		if(control.getType()==Controller.Type.KEYBOARD){
			for(Controller c: controllers){
				if(c.getName().equals(control.getName())){
					c.poll();
					if(c.getComponent(id).getPollData()==val)
						return true;
				}
			}
		}
		else{
			control.poll();
			return control.getComponent(id).getPollData()==val;
		}
		return false;
	}
	public String toString(){
		return id.toString();
	}
	public String storeInfo(){
		return control.getName()+":"+control.getPortNumber()+";"+id.getName()+";"+val;
	}
	public static ControllerInfo restoreInfo(String s,String d){
		String[] info;
		//Check if we get null from our config file
		if(s==null)
			info = d.split(";");
		else
			info = s.split(";");
		String name = info[0].split(":")[0];
		String port = info[0].split(":")[1];
		float var = Float.parseFloat(info[2]);
		//First check if S will work
		Controller[] cont = ControllerEnvironment.getDefaultEnvironment().getControllers();
		for(Controller c: cont){
			if(c.getName().equals(name)&&c.getPortNumber()==Integer.parseInt(port)){
				for(Component comp: c.getComponents()){
					if(comp.getIdentifier().getName().equals(info[1])){
						return new ControllerInfo(c,comp.getIdentifier(),var);
					}
				}
			}
		}
		//Next check if default Keyboard will work
		info = d.split(";");
		for(Controller c: cont){
			System.out.println(c.getName()+" "+c.getType().toString());
			if(c.getType()==Controller.Type.KEYBOARD){
				System.out.println(c.getName());
				for(Component comp:c.getComponents())
					if(comp.getIdentifier().getName().equals(info[1])){
						return new ControllerInfo(c,comp.getIdentifier(),var);
					}
			}
		}
		//If that didn't work grab anything.
		for(Controller c:cont){
			for(Component comp:c.getComponents())
				return new ControllerInfo(c,comp.getIdentifier(),(float) 1.0);
		}
		//Return a null thing if nothing worked.
		return new ControllerInfo(null,null,(float) 1.0);
	}
}
