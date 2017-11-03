package ui.input;

import net.java.games.input.*;

class ControllerInfo {
	static String defaultButtonConfig = "Standard PS/2 Keyboard:0;A;1.0";
	private Controller[] controllers;
	private Controller control;
	private Component.Identifier id;
	private String idname;
	float val;
	public ControllerInfo(){
		idname = "N/A";
	}
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
	String getButtonName(){
		return idname;
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
		//System.out.println("Couldn't find requested Controller. Getting default.");
		String temp = "";
		if(info[0].contains("Keyboard"))
			temp = info[1];
		//Next check if default Keyboard will work
		info = d.split(";");
		info[1] = temp;
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
	static ControllerInfo getButton(){
		Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
		for(int i=0;i<controllers.length;i++) {
			controllers[i].poll();
			EventQueue queue = controllers[i].getEventQueue();
			Event event = new Event();
			queue.getNextEvent(event);
			while(queue.getNextEvent(event));
		}
		while(true) {
			if(controllers.length==0) {
				System.out.println("Found no controllers.");
				System.exit(0);
			}

			for(int i=0;i<controllers.length;i++) {
				controllers[i].poll();
				EventQueue queue = controllers[i].getEventQueue();
				Event event = new Event();
				//queue.getNextEvent(event);
				//controllers[i].poll();
				while(queue.getNextEvent(event)&&!controllers[i].getType().equals(Controller.Type.MOUSE)) {
					StringBuffer buffer = new StringBuffer(controllers[i].getName());
					buffer.append(" at ");
					buffer.append(event.getNanos()).append(", ");
					Component comp = event.getComponent();
					buffer.append(comp.getName()).append(" changed to ");
					float value = event.getValue();
					if(comp.isAnalog()) {
						buffer.append(value);
					} else {
						System.out.println(controllers[i].getName()+":"+controllers[i].getPortNumber());
						return new ControllerInfo(controllers[i],comp.getIdentifier(),value);
					}
					System.out.println(buffer.toString());
				}
			}

			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
