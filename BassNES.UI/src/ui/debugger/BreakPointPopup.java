package ui.debugger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class BreakPointPopup extends JPopupMenu {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9037671664854416423L;
	JMenuItem enable;
	public BreakPointPopup(BreakPoint p){
		enable = new JMenuItem("Toggle Enabled");
		enable.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				p.toggleEnable();
			}
			
		});
		add(enable);
	}
}
