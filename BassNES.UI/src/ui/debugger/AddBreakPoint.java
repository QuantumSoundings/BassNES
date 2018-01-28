package ui.debugger;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import ui.debugger.BreakPoint.Variable;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class AddBreakPoint extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7030577224543112784L;
	private JPanel contentPane;
	private JTextField textField;

	/**
	 * Create the frame.
	 */
	public AddBreakPoint(DefaultListModel<BreakPoint> breakpoint) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JComboBox<Object> comboBox = new JComboBox<Object>();
		comboBox.setModel(new DefaultComboBoxModel<Object>(Variable.values()));
		comboBox.setBounds(10, 115, 141, 20);
		contentPane.add(comboBox);
		
		textField = new JTextField();
		textField.setBounds(226, 115, 141, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JButton btnAddBreakpoint = new JButton("Add BreakPoint");
		btnAddBreakpoint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Object val = null;
				String text = textField.getText().toLowerCase();
				if(text.equals("true"))
					val = true;
				else if(text.equals("false"))
					val = false;
				else{
					val = Integer.parseInt(text, 16);
				}
				breakpoint.addElement(new BreakPoint((Variable) comboBox.getSelectedItem(),val));
				setVisible(false);
				dispose();
			}
		});
		btnAddBreakpoint.setBounds(138, 191, 141, 23);
		contentPane.add(btnAddBreakpoint);
	}

}
