package ui;


import javax.swing.JFrame;
import javax.swing.JPanel;
import java.io.PrintStream;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class DebugUI extends JFrame {

	private static final long serialVersionUID = -7873279625262778152L;

	public DebugUI() {
		JPanel p1 = new JPanel();
		JTextArea text = new JTextArea(30,35);
		text.setLineWrap(true);
		text.setEditable(false);
		JScrollPane scroll = new JScrollPane(text);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		p1.add(scroll);
		
		getContentPane().add(p1);
		this.pack();
		PrintStream x = new PrintStream(System.out){
			@Override
			public void println(String x){
				text.append(x);
				text.append("\n");
			}
			@Override
			public void print(String x){
				text.append(x);
			}
		};
		System.setOut(x);
		System.setErr(x);
		
	}
}
