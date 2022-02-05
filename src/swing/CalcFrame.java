package swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class CalcFrame extends JFrame implements ActionListener{
	
	public JButton textButton(JPanel target, int index, String buttonText, String fieldText, ActionListener textHandler, String command) {
		JButton button = new JButton(buttonText);
		button.setBackground(Color.decode("#424549"));
		button.setForeground(Color.white);
		button.setBorderPainted(false);
		button.setFocusable(false);
		button.setPreferredSize(new Dimension(200,26));
		
		JTextField textField = new JTextField();
		textField.setBackground(Color.decode("#424549"));         
		textField.setForeground(Color.white);                     
		textField.setBorder(null);                        
		textField.setFont(new Font("Arial", Font.BOLD, 12));
		textField.setPreferredSize(new Dimension(200,26));        
		textField.setHorizontalAlignment(JTextField.CENTER);
		textField.setVisible(false);
		textField.setCaretColor(Color.white);
		
		button.addActionListener(ae-> {
			button.setVisible(false);
			textField.setText(fieldText);
			textField.setVisible(true);
			textField.requestFocus();
			textField.selectAll();
		});
		
		textField.addActionListener(ae -> {
			textField.setVisible(false);
			button.setVisible(true);
		});
		
		textField.addActionListener(textHandler);
		textField.setActionCommand(command);
		
		target.add(textField,index);
		target.add(button,index);

		return button;
	}
	
	
	public CalcFrame() {
		
		//Main Frame
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(500,500);
		this.setTitle("Logic Calculator");
		this.setLayout(new BorderLayout(10,10));
		this.getContentPane().setBackground(Color.decode("#1e2124"));
		this.setVisible(true);
		this.setExtendedState(MAXIMIZED_BOTH);
		
		//Panels
		JPanel mainPanel = new JPanel();
		JPanel sidePanel = new JPanel();
		JPanel optionsPanel = new JPanel();
		
		mainPanel.setPreferredSize(new Dimension(100,100));
		mainPanel.setBackground(Color.decode("#282b30"));
		
		sidePanel.setPreferredSize(new Dimension(230,100));
		sidePanel.setBackground(Color.decode("#282b30"));

		
		optionsPanel.setPreferredSize(new Dimension(100,170));
		optionsPanel.setBackground(Color.decode("#282b30"));
		
		//Buttons
		JButton addExpressionButton = textButton(sidePanel, 0, 
				"Add Expression", "Insert your expression", this, "addExpression");
		
		JButton addExprSetButton = textButton(sidePanel, 2, 
				"Add Expression Set", "Insert your expression set", this, "addExpressionSet");
		
		this.add(sidePanel,BorderLayout.WEST);
		this.add(optionsPanel,BorderLayout.SOUTH);
		this.add(mainPanel,BorderLayout.CENTER);
		
		this.setVisible(true);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		switch(e.getActionCommand()) {
		case "addExpression":
			System.out.println("Adding expression: " + ((JTextField) e.getSource()).getText());
			break;
		case "addExpressionSet":
			System.out.println("Adding expression set: " + ((JTextField) e.getSource()).getText());
		default:
			break;
		}
	}


	
	//Notes: Options on the bottom
}
