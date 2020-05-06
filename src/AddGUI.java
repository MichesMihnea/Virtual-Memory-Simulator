import javax.swing.JFrame;
import java.awt.Rectangle;
import javax.swing.SpringLayout;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.ListModel;

import java.awt.Font;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.JComboBox;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.event.ActionEvent;

public class AddGUI extends JFrame{
	private JTextField nameField;
	private JTextField sizeField;
	private MainGUI mGUI;
	JComboBox <String> list;
	public AddGUI(MainGUI mGUI) {
		this.mGUI = mGUI;
		setAlwaysOnTop(true);
		setBounds(new Rectangle(750, 500, 300, 160));
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		
		JLabel lblName = new JLabel("Name");
		lblName.setFont(new Font("Tahoma", Font.PLAIN, 16));
		springLayout.putConstraint(SpringLayout.NORTH, lblName, 10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, lblName, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(lblName);
		
		JLabel lblSize = new JLabel("Size");
		springLayout.putConstraint(SpringLayout.NORTH, lblSize, 6, SpringLayout.SOUTH, lblName);
		springLayout.putConstraint(SpringLayout.WEST, lblSize, 10, SpringLayout.WEST, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, lblSize, 41, SpringLayout.WEST, lblName);
		lblSize.setFont(new Font("Tahoma", Font.PLAIN, 16));
		getContentPane().add(lblSize);
		
		JLabel lblPolicy = new JLabel("Replacement Algorithm");
		lblPolicy.setFont(new Font("Tahoma", Font.PLAIN, 16));
		springLayout.putConstraint(SpringLayout.NORTH, lblPolicy, 6, SpringLayout.SOUTH, lblSize);
		springLayout.putConstraint(SpringLayout.WEST, lblPolicy, 0, SpringLayout.WEST, lblName);
		getContentPane().add(lblPolicy);
		
		nameField = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, nameField, 10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, nameField, 44, SpringLayout.EAST, lblName);
		springLayout.putConstraint(SpringLayout.EAST, nameField, 181, SpringLayout.EAST, lblName);
		getContentPane().add(nameField);
		nameField.setColumns(10);
		
		sizeField = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, sizeField, 8, SpringLayout.SOUTH, nameField);
		springLayout.putConstraint(SpringLayout.WEST, sizeField, 0, SpringLayout.WEST, nameField);
		springLayout.putConstraint(SpringLayout.EAST, sizeField, 0, SpringLayout.EAST, nameField);
		getContentPane().add(sizeField);
		sizeField.setColumns(10);
		
		String[] listModel = {"FIFO", "CLK", "LRU"};
		list = new JComboBox<String> (listModel);
		springLayout.putConstraint(SpringLayout.NORTH, list, 4, SpringLayout.NORTH, lblPolicy);
		springLayout.putConstraint(SpringLayout.WEST, list, 6, SpringLayout.EAST, lblPolicy);
		springLayout.putConstraint(SpringLayout.SOUTH, list, 0, SpringLayout.SOUTH, lblPolicy);
		springLayout.putConstraint(SpringLayout.EAST, list, 0, SpringLayout.EAST, nameField);
		getContentPane().add(list);
		
		JButton btnGo = new JButton("GO!");
		springLayout.putConstraint(SpringLayout.NORTH, btnGo, 6, SpringLayout.SOUTH, lblPolicy);
		btnGo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AddGUI.this.dispose();
				mGUI.fulfillRequest();
			}
		});
		springLayout.putConstraint(SpringLayout.EAST, btnGo, -107, SpringLayout.EAST, getContentPane());
		getContentPane().add(btnGo);
		this.setVisible(true);
	}
	
	public String getNameField() {
		return this.nameField.getText();
	}
	
	public long getSizeField() {
		return Long.parseLong(this.sizeField.getText());
	}
	
	public int getAlgorithmIndex() {
		return this.list.getSelectedIndex();
	}

}
