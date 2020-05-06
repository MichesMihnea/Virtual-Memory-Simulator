import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.SpringLayout;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class StartGUI extends JFrame {
	private JTextField PageField;
	private JTextField MemField;
	private JTextField TLBField;
	private MainGUI mGUI;
	public StartGUI(MainGUI mGUI) {
		
		this.mGUI = mGUI;
		setBounds(new Rectangle(600, 350, 480, 250));
		setTitle("VMS");
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		
		JLabel lblHelloAndWelcome = new JLabel("Hello and welcome to Virtual Memory Simulator!");
		springLayout.putConstraint(SpringLayout.NORTH, lblHelloAndWelcome, 10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, lblHelloAndWelcome, 104, SpringLayout.WEST, getContentPane());
		getContentPane().add(lblHelloAndWelcome);
		
		JLabel lblWeWillHave = new JLabel("We will have to collect some data before we begin the simulation.");
		springLayout.putConstraint(SpringLayout.NORTH, lblWeWillHave, 6, SpringLayout.SOUTH, lblHelloAndWelcome);
		springLayout.putConstraint(SpringLayout.EAST, lblWeWillHave, -59, SpringLayout.EAST, getContentPane());
		getContentPane().add(lblWeWillHave);
		
		JLabel lblPhysicalPageSize = new JLabel("Page Size (power of 2)");
		springLayout.putConstraint(SpringLayout.WEST, lblPhysicalPageSize, 47, SpringLayout.WEST, getContentPane());
		getContentPane().add(lblPhysicalPageSize);
		
		PageField = new JTextField();
		PageField.setText("128");
		springLayout.putConstraint(SpringLayout.NORTH, PageField, 12, SpringLayout.SOUTH, lblWeWillHave);
		springLayout.putConstraint(SpringLayout.NORTH, lblPhysicalPageSize, 3, SpringLayout.NORTH, PageField);
		springLayout.putConstraint(SpringLayout.EAST, PageField, 0, SpringLayout.EAST, lblHelloAndWelcome);
		getContentPane().add(PageField);
		PageField.setColumns(10);
		
		JLabel lblVirtualMemorySize = new JLabel("Memory Size (power of 2)");
		springLayout.putConstraint(SpringLayout.NORTH, lblVirtualMemorySize, 55, SpringLayout.SOUTH, lblPhysicalPageSize);
		springLayout.putConstraint(SpringLayout.EAST, lblVirtualMemorySize, 0, SpringLayout.EAST, lblPhysicalPageSize);
		getContentPane().add(lblVirtualMemorySize);
		
		MemField = new JTextField();
		MemField.setText("256");
		springLayout.putConstraint(SpringLayout.NORTH, MemField, -3, SpringLayout.NORTH, lblVirtualMemorySize);
		springLayout.putConstraint(SpringLayout.EAST, MemField, 0, SpringLayout.EAST, lblHelloAndWelcome);
		getContentPane().add(MemField);
		MemField.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("TLB Size");
		springLayout.putConstraint(SpringLayout.NORTH, lblNewLabel, 19, SpringLayout.SOUTH, lblPhysicalPageSize);
		springLayout.putConstraint(SpringLayout.EAST, lblNewLabel, 0, SpringLayout.EAST, lblPhysicalPageSize);
		getContentPane().add(lblNewLabel);
		
		TLBField = new JTextField();
		TLBField.setText("10");
		springLayout.putConstraint(SpringLayout.NORTH, TLBField, -2, SpringLayout.NORTH, lblNewLabel);
		springLayout.putConstraint(SpringLayout.EAST, TLBField, 0, SpringLayout.EAST, lblHelloAndWelcome);
		getContentPane().add(TLBField);
		TLBField.setColumns(10);
		
		JButton btnGo = new JButton("GO!");
		springLayout.putConstraint(SpringLayout.NORTH, btnGo, 27, SpringLayout.SOUTH, lblVirtualMemorySize);
		springLayout.putConstraint(SpringLayout.WEST, btnGo, 205, SpringLayout.WEST, getContentPane());
		btnGo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					long memSize = Long.parseLong(MemField.getText());
					long pageSize = Long.parseLong(PageField.getText());
					
					boolean f = false;
					
					for(int i = 0; i <= memSize; i ++) {
						if (Math.pow(2, i) == memSize) 
							f = true;
					}
					
					if (!f)
						throw new NumberFormatException();
					
					f = false;
					
					for(int i = 0; i <= memSize; i ++) {
						if (Math.pow(2, i) == pageSize) 
							f = true;
					}
					
					if (!f)
						throw new NumberFormatException();
					
					if (Long.parseLong(TLBField.getText()) <= 0)
						throw new NumberFormatException();
					
				}catch (NumberFormatException nfex) {
					JOptionPane.showMessageDialog(null, "Please enter valid input!", "asdf", JOptionPane.ERROR_MESSAGE);
			        return;
				}
					
				StartGUI.this.dispose();
				mGUI.setVisible(true);
				mGUI.setTLBSize(Long.parseLong(TLBField.getText()));
				mGUI.setMemSize(Long.parseLong(MemField.getText()));
				mGUI.setPageSize(Long.parseLong(PageField.getText()));
			}
		});
		getContentPane().add(btnGo);
		this.setVisible(true);
	}
}
