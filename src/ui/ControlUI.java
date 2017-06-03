package ui;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;

import javax.swing.JButton;
import javax.swing.JLabel;
import java.util.Properties;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ControlUI extends JFrame {

	private static final long serialVersionUID = -7060080516930936378L;
	public JPanel contentPane;
	boolean awaitingkey;
	Properties prop;
	SystemUI sys;

	public ControlUI(Properties p, SystemUI s) {
		sys = s;
		prop = p;
        //noinspection MagicConstant
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 313);
		contentPane = new JPanel();  
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblController = new JLabel("Controller 1");
		lblController.setBounds(87, 1, 74, 14);
		contentPane.add(lblController);
		
		JLabel lblController_1 = new JLabel("Controller 2");
		lblController_1.setBounds(279, 1, 74, 14);
		contentPane.add(lblController_1);
		
		JLabel lblAButton = new JLabel("A Button");
		lblAButton.setBounds(45, 24, 59, 14);
		contentPane.add(lblAButton);
		
		JButton abutton1 = new JButton("<"+UISettings.c1controls[0].id.getName()+">");
		abutton1.setBounds(114, 20, 83, 23);
		abutton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UISettings.c1controls[0] =getbutton();
				abutton1.setText("<"+UISettings.c1controls[0].id.getName()+">");
			}
		});
		contentPane.add(abutton1);
		
		JLabel lblAButton_1 = new JLabel("A Button");
		lblAButton_1.setBounds(232, 24, 59, 14);
		contentPane.add(lblAButton_1);
		
		JButton abutton2 = new JButton("<"+UISettings.c2controls[0].id.getName()+">");
		abutton2.setBounds(301, 20, 83, 23);
		abutton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UISettings.c2controls[0] =getbutton();
				abutton2.setText("<"+UISettings.c2controls[0].id.getName()+">");
			}
		});
		
		contentPane.add(abutton2);
		
		JLabel lblBButton = new JLabel("B Button");
		lblBButton.setBounds(45, 52, 59, 14);
		contentPane.add(lblBButton);
		
		JButton bbutton1 = new JButton("<"+UISettings.c1controls[1].id.getName()+">");
		bbutton1.setBounds(114, 48, 83, 23);
		bbutton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UISettings.c1controls[1] =getbutton();
				bbutton1.setText("<"+UISettings.c1controls[1].id.getName()+">");
			}
		});
		contentPane.add(bbutton1);
		
		JLabel lblBButton_1 = new JLabel("B Button");
		lblBButton_1.setBounds(232, 52, 59, 14);
		contentPane.add(lblBButton_1);
		
		JButton bbutton2 = new JButton("<"+UISettings.c2controls[1].id.getName()+">");
		bbutton2.setBounds(301, 48, 83, 23);
		bbutton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UISettings.c2controls[1] =getbutton();
				bbutton2.setText("<"+UISettings.c2controls[1].id.getName()+">");
			}
		});
		contentPane.add(bbutton2);
		
		JLabel lblUpdpad = new JLabel("Up");
		lblUpdpad.setBounds(45, 80, 42, 14);
		contentPane.add(lblUpdpad);
		
		JButton upbutton1 = new JButton("<"+UISettings.c1controls[4].id.getName()+">");
		upbutton1.setBounds(114, 76, 83, 23);
		upbutton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UISettings.c1controls[4] =getbutton();
				upbutton1.setText("<"+UISettings.c1controls[4].id.getName()+">");
			}
		});
		contentPane.add(upbutton1);
		
		JButton upbutton2 = new JButton("<"+UISettings.c2controls[4].id.getName()+">");
		upbutton2.setBounds(301, 76, 83, 23);
		upbutton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UISettings.c2controls[4] =getbutton();
				upbutton2.setText("<"+UISettings.c2controls[4].id.getName()+">");
			}
		});
		
		JLabel lblUp = new JLabel("Up");
		lblUp.setBounds(246, 80, 28, 14);
		contentPane.add(lblUp);
		contentPane.add(upbutton2);
		
		JLabel lblDown = new JLabel("Down");
		lblDown.setBounds(45, 108, 42, 14);
		contentPane.add(lblDown);
		
		JButton downbutton1 = new JButton("<"+UISettings.c1controls[5].id.getName()+">");
		downbutton1.setBounds(114, 104, 83, 23);
		downbutton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UISettings.c1controls[5] =getbutton();
				downbutton1.setText("<"+UISettings.c1controls[5].id.getName()+">");
			}
		});
		contentPane.add(downbutton1);
		
		JButton leftbutton1 = new JButton("<"+UISettings.c1controls[6].id.getName()+">");
		leftbutton1.setBounds(114, 132, 83, 23);
		leftbutton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UISettings.c1controls[6] =getbutton();
				leftbutton1.setText("<"+UISettings.c1controls[6].id.getName()+">");
				awaitingkey=true;
			}
		});
		
		JLabel lblDown_1 = new JLabel("Down");
		lblDown_1.setBounds(239, 108, 35, 14);
		contentPane.add(lblDown_1);
		
		JButton downbutton2 = new JButton("<"+UISettings.c2controls[5].id.getName()+">");
		downbutton2.setBounds(301, 104, 83, 23);
		downbutton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UISettings.c2controls[5] =getbutton();
				downbutton2.setText("<"+UISettings.c2controls[5].id.getName()+">");
			}
		});
		contentPane.add(downbutton2);
		
		JLabel lblLeft = new JLabel("Left");
		lblLeft.setBounds(45, 136, 42, 14);
		contentPane.add(lblLeft);
		contentPane.add(leftbutton1);
		
		JButton leftbutton2 = new JButton("<"+UISettings.c2controls[6].id.getName()+">");
		leftbutton2.setBounds(301, 132, 83, 23);
		leftbutton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UISettings.c2controls[6] =getbutton();
				leftbutton2.setText("<"+UISettings.c2controls[6].id.getName()+">");
			}
		});
		
		JLabel lblLeft_1 = new JLabel("Left");
		lblLeft_1.setBounds(243, 136, 31, 14);
		contentPane.add(lblLeft_1);
		contentPane.add(leftbutton2);
		
		JLabel lblRight = new JLabel("Right");
		lblRight.setBounds(45, 164, 42, 14);
		contentPane.add(lblRight);
		
		JButton rightbutton1 = new JButton("<"+UISettings.c1controls[7].id.getName()+">");
		rightbutton1.setBounds(114, 160, 83, 23);
		rightbutton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UISettings.c1controls[7] =getbutton();
				rightbutton1.setText("<"+UISettings.c1controls[7].id.getName()+">");
			}
		});
		contentPane.add(rightbutton1);
		
		JLabel lblRight_1 = new JLabel("Right");
		lblRight_1.setBounds(240, 164, 34, 14);
		contentPane.add(lblRight_1);
		
		JButton rightbutton2 = new JButton("<"+UISettings.c2controls[7].id.getName()+">");
		rightbutton2.setBounds(301, 160, 83, 23);
		rightbutton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UISettings.c2controls[7] =getbutton();
				rightbutton2.setText("<"+UISettings.c2controls[7].id.getName()+">");
			}
		});
		contentPane.add(rightbutton2);
		
		JLabel lblStart = new JLabel("Start");
		lblStart.setBounds(45, 192, 42, 14);
		contentPane.add(lblStart);
		
		JButton startbutton1 = new JButton("<"+UISettings.c1controls[3].id.getName()+">");
		startbutton1.setBounds(114, 188, 83, 23);
		startbutton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UISettings.c1controls[3] =getbutton();
				startbutton1.setText("<"+UISettings.c1controls[3].id.getName()+">");
			}
		});
		contentPane.add(startbutton1);
		
		JButton startbutton2 = new JButton("<"+UISettings.c2controls[3].id.getName()+">");
		startbutton2.setBounds(301, 188, 83, 23);
		startbutton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UISettings.c2controls[3] =getbutton();
				startbutton2.setText("<"+UISettings.c2controls[3].id.getName()+">");
			}
		});
		
		JLabel lblStart_1 = new JLabel("Start");
		lblStart_1.setBounds(241, 192, 35, 14);
		contentPane.add(lblStart_1);
		contentPane.add(startbutton2);
		
		JLabel lblSelect = new JLabel("Select");
		lblSelect.setBounds(45, 220, 42, 14);
		contentPane.add(lblSelect);
		
		JButton selectbutton1 = new JButton("<"+UISettings.c1controls[2].id.getName()+">");
		selectbutton1.setBounds(114, 216, 83, 23);
		selectbutton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UISettings.c1controls[2] =getbutton();
				selectbutton1.setText("<"+UISettings.c1controls[2].id.getName()+">");
			}
		});
		contentPane.add(selectbutton1);
		
		JButton selectbutton2 = new JButton("<"+UISettings.c2controls[2].id.getName()+">");
		selectbutton2.setBounds(301, 216, 83, 23);
		selectbutton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UISettings.c1controls[2] =getbutton();
				selectbutton2.setText("<"+UISettings.c1controls[2].id.getName()+">");
			}
		});
		
		JLabel lblSelect_1 = new JLabel("Select");
		lblSelect_1.setBounds(238, 220, 36, 14);
		contentPane.add(lblSelect_1);
		contentPane.add(selectbutton2);
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
	}
	private ControllerInfo getbutton(){
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
	                	System.out.println(controllers[i].getName());
	                   return new ControllerInfo(controllers[i].getName(),comp.getIdentifier(),value);
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
