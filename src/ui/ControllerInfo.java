package ui;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

public class ControllerInfo {
	Controller control;
	Component.Identifier id;
	float val;
	public ControllerInfo(Controller c,Component.Identifier i,float v){
		val = v;
		id = i;
		control = c;
	}
	
	public boolean checkPressed(){
		if(control.getType()==Controller.Type.KEYBOARD){
			Controller[] cont = ControllerEnvironment.getDefaultEnvironment().getControllers();
			for(Controller c: cont){
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
		if(s!=null){
			String[] info = s.split(";");
			String name = info[0].split(":")[0];
			String port = info[0].split(":")[1];
			float var = Float.parseFloat(info[2]);
			Controller[] cont = ControllerEnvironment.getDefaultEnvironment().getControllers();
			if(!d.equals("null")){
				for(Controller c: cont){
					if(c.getName().equals(name)&&c.getPortNumber()==Integer.parseInt(port)){
						for(Component comp: c.getComponents()){
							if(comp.getIdentifier().getName().equals(info[1])){
								return new ControllerInfo(c,comp.getIdentifier(),var);
							}
						}
					}
				}
			}
			else{
				for(Controller c: cont){
					if(c.getType()==Controller.Type.KEYBOARD){
						System.out.println(c.getName());
						for(Component comp:c.getComponents())
							if(comp.getIdentifier().getName().equals(info[1])){
								return new ControllerInfo(c,comp.getIdentifier(),var);
							}
					}
				}
			}
		}
		System.out.println("Not found :( Loading default...");
		return restoreInfo(d,"null");
	}
}
