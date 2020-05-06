import javax.swing.JFrame;
import javax.swing.SpringLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

public class WriteGUI extends JFrame{
	
	private MainGUI mGUI;
	private JTextField textField;
	private int step = 0;
	private JComboBox <String> comboBox;
	private JTextField textField_1;
	
	public WriteGUI(MainGUI mGUI) {
		this.mGUI = mGUI;
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		
		setBounds(new Rectangle(750, 500, 320, 250));
		
		JLabel lblPageId = new JLabel("Write to address");
		lblPageId.setFont(new Font("Tahoma", Font.PLAIN, 16));
		springLayout.putConstraint(SpringLayout.NORTH, lblPageId, 34, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, lblPageId, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(lblPageId);
		
		textField = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, textField, 34, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, textField, 19, SpringLayout.EAST, lblPageId);
		springLayout.putConstraint(SpringLayout.EAST, textField, 151, SpringLayout.EAST, lblPageId);
		getContentPane().add(textField);
		textField.setColumns(10);
		
		JLabel lblFastForward = new JLabel("Step by step");
		springLayout.putConstraint(SpringLayout.WEST, lblFastForward, 10, SpringLayout.WEST, getContentPane());
		lblFastForward.setFont(new Font("Tahoma", Font.PLAIN, 16));
		getContentPane().add(lblFastForward);
		
		JCheckBox checkBox = new JCheckBox("");
		springLayout.putConstraint(SpringLayout.NORTH, lblFastForward, 0, SpringLayout.NORTH, checkBox);
		springLayout.putConstraint(SpringLayout.WEST, checkBox, 0, SpringLayout.WEST, textField);
		getContentPane().add(checkBox);
		
		JButton btnDone = new JButton("Done!");
		springLayout.putConstraint(SpringLayout.SOUTH, btnDone, -10, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, btnDone, -110, SpringLayout.EAST, getContentPane());
		btnDone.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				try {
					WriteGUI.this.getAddress();
				}catch(NumberFormatException nfex) {
					JOptionPane.showMessageDialog(null, "Please enter valid input!", "asdf", JOptionPane.ERROR_MESSAGE);
			        return;
				}
				
				mGUI.setAlgorithm(WriteGUI.this.getAlgorithmIndex());
				mGUI.setWriteData(WriteGUI.this.getWriteData());
				WriteGUI.this.dispose();
				
				if(checkBox.isSelected()) {
					mGUI.goStepByStep();
					mGUI.fulfillLoad(WriteGUI.this.getAddress(), true, -1);
				}
				else mGUI.fulfillLoad(WriteGUI.this.getAddress(), false, -1);
			}
		});
		getContentPane().add(btnDone);
		
		JLabel lblReplacementAlgorithm = new JLabel("Replacement Algorithm");
		springLayout.putConstraint(SpringLayout.SOUTH, checkBox, -6, SpringLayout.NORTH, lblReplacementAlgorithm);
		springLayout.putConstraint(SpringLayout.WEST, lblReplacementAlgorithm, 0, SpringLayout.WEST, lblPageId);
		springLayout.putConstraint(SpringLayout.SOUTH, lblReplacementAlgorithm, -19, SpringLayout.NORTH, btnDone);
		lblReplacementAlgorithm.setFont(new Font("Tahoma", Font.PLAIN, 16));
		getContentPane().add(lblReplacementAlgorithm);
		
		String[] listModel = {"FIFO", "CLK", "LRU"};
		comboBox = new JComboBox <String> (listModel);
		springLayout.putConstraint(SpringLayout.NORTH, comboBox, 2, SpringLayout.NORTH, lblReplacementAlgorithm);
		springLayout.putConstraint(SpringLayout.WEST, comboBox, 16, SpringLayout.EAST, lblReplacementAlgorithm);
		springLayout.putConstraint(SpringLayout.EAST, comboBox, 0, SpringLayout.EAST, textField);
		getContentPane().add(comboBox);
		
		JLabel lblWriteData = new JLabel("Write data");
		lblWriteData.setFont(new Font("Tahoma", Font.PLAIN, 16));
		springLayout.putConstraint(SpringLayout.NORTH, lblWriteData, 22, SpringLayout.SOUTH, lblPageId);
		springLayout.putConstraint(SpringLayout.WEST, lblWriteData, 0, SpringLayout.WEST, lblPageId);
		getContentPane().add(lblWriteData);
		
		textField_1 = new JTextField();
		springLayout.putConstraint(SpringLayout.WEST, textField_1, 0, SpringLayout.WEST, textField);
		springLayout.putConstraint(SpringLayout.SOUTH, textField_1, -16, SpringLayout.NORTH, checkBox);
		springLayout.putConstraint(SpringLayout.EAST, textField_1, 0, SpringLayout.EAST, textField);
		getContentPane().add(textField_1);
		textField_1.setColumns(10);
		
		this.setVisible(true);
		
	}
	
	public long getAddress() {
		return Long.decode(textField.getText());
	}
	
	public int getAlgorithmIndex() {
		return comboBox.getSelectedIndex();
	}
	
	public String getWriteData() {
		return this.textField_1.getText();
	}
}
