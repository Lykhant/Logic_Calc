package es.etomas.logiccalc.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.synth.SynthLookAndFeel;

import es.etomas.logiccalc.logicparsers.PropLogic;


public class CalcFrame extends JFrame implements ActionListener{
	
	final static Color blurple = Color.decode("#7289da");
	final static Color gray1 = Color.decode("#424549");
	final static Color gray2 = Color.decode("#36393e");
	final static Color gray3 = Color.decode("#282b30");
	final static Color gray4 = Color.decode("#1e2124");
	
	private Map<String, PropLogic> exprs = new HashMap<>();
	private Map<String, Set<PropLogic>> exprSets = new HashMap<>();
	
	//Panel variables
	JPanel mainPanel;
	JPanel sidePanel;
	JPanel optionsPanel;
	
	JPanel varPanel;
	JPanel lowerSidePanel;
	JPanel northVarPanel;
	
	private void initLaF() {
		SynthLookAndFeel laf = new SynthLookAndFeel();
		
		try {
			InputStream fileStream = CalcFrame.class.getResourceAsStream("synthDarkTheme.xml");
			laf.load(fileStream, CalcFrame.class);
			UIManager.setLookAndFeel(laf);
		} catch (ParseException e) {
			System.err.println("Couldn't get specified look and feel ("
                    + laf
                     + "), for some reason.");
			System.err.println("Using the default look and feel.");
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	public JButton textButton(JPanel target, int index, String buttonText, String fieldText, ActionListener textHandler, String command) {
		JButton button = new JButton(buttonText);
		button.setPreferredSize(new Dimension(200,26));
		
		JTextField textField = new JTextField();
		textField.setPreferredSize(new Dimension(200,26));        
		textField.setHorizontalAlignment(JTextField.CENTER);
		textField.setVisible(false);
		
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
	
	public JButton delButton(String linkedKey, JLabel linkedLabel) {
		JButton button = new JButton("X");
		button.setFocusable(false);
		button.setPreferredSize(new Dimension(45,26));
		button.addActionListener(ae->{
			exprs.remove(linkedKey);
			button.setVisible(false);
			button.getParent().remove(linkedLabel);

			button.getParent().remove(button);
		});

		
		return button;
	}
	
	public CalcFrame() {
		
		initLaF();
	
		
		
		//Main Frame
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(500,500);
		this.setTitle("Logic Calculator");
		this.setLayout(new BorderLayout(10,10));
		this.getContentPane().setBackground(gray4);

		
		//Panels
		this.mainPanel = new JPanel();
		this.sidePanel = new JPanel();
		this.optionsPanel = new JPanel();
		
		this.varPanel = new JPanel();
		this.lowerSidePanel = new JPanel();
		JPanel outerVarPanel = new JPanel();
		JScrollPane scrollableVarPanel = new JScrollPane(
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		mainPanel.setPreferredSize(new Dimension(100,100));
		mainPanel.setBackground(gray3);
		
		sidePanel.setPreferredSize(new Dimension(230,100));
		
		sidePanel.setBackground(gray3);
		sidePanel.setLayout(new BorderLayout());

		varPanel.setBackground(gray3);
		varPanel.setLayout(new GridLayout(0, 2, 5, 5));
		
		lowerSidePanel.setBackground(gray2);
		lowerSidePanel.setPreferredSize(new Dimension(200,200));
		
		optionsPanel.setPreferredSize(new Dimension(100,170));
		optionsPanel.setBackground(gray3);
		
		scrollableVarPanel.setViewportView(outerVarPanel);
		scrollableVarPanel.setBorder(null);
		scrollableVarPanel.setBackground(gray3);
		scrollableVarPanel.setOpaque(true);
		
		JScrollBar vertScrollBar = scrollableVarPanel.getVerticalScrollBar();
		JScrollBar horizontalScrollBar = scrollableVarPanel.getHorizontalScrollBar();
		
		vertScrollBar.setUI(new DarkScrollBarUI());
		vertScrollBar.setUnitIncrement(10);
		horizontalScrollBar.setUI(new DarkScrollBarUI());
		horizontalScrollBar.setUnitIncrement(10);
		
		
		outerVarPanel.setBackground(gray3);
		outerVarPanel.setLayout(new FlowLayout());
		
		
		//Buttons
		JButton addExpressionButton = textButton(lowerSidePanel, 0, 
				"Add Expression", "Insert your expression", this, "addExpression");
		JButton addExprSetButton = textButton(lowerSidePanel, 2, 
				"Add Expression Set", "Insert your expression set", this, "addExpressionSet");
		JButton addClause = textButton(lowerSidePanel,4,"Add Clause", "Insert your clause", this,
				"addClause");
		JButton addClauseSet = textButton(lowerSidePanel,6,"Add Clause Set", "Insert your clause Set", this,
				"addClauseSet");
			
		addExpressionButton.setMnemonic(KeyEvent.VK_A);
		addExprSetButton.setMnemonic(KeyEvent.VK_S);

		outerVarPanel.add(varPanel);
		sidePanel.add(scrollableVarPanel);
		sidePanel.add(lowerSidePanel,BorderLayout.SOUTH);
		this.add(sidePanel,BorderLayout.WEST);
		this.add(optionsPanel,BorderLayout.SOUTH);
		this.add(mainPanel,BorderLayout.CENTER);
		
		this.pack();
		this.setVisible(true);
		this.setExtendedState(MAXIMIZED_BOTH);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		String input;
		String[] parsingSegments;
		String varName;
		JLabel label;
		switch(e.getActionCommand()) {
		case "addExpression":
			System.out.println("Adding expression: " + ((JTextField) e.getSource()).getText());
			
			input = ((JTextField) e.getSource()).getText();
			
			parsingSegments = input
					.replace(" ", "").split(":");
			
			PropLogic logicExpression;
			
			
			switch(parsingSegments.length) {
			case 1:
				//Assume logic expression
				varName = generateMapKey();
				logicExpression = PropLogic.parse(parsingSegments[0]);
				break;
			case 2:
				//Logic expression being named
				varName = parsingSegments[0];
				logicExpression = PropLogic.parse(parsingSegments[1]);
				break;
			default:
				throw new IllegalArgumentException("Invalid expression format");
			}
			
			
			System.out.println("Logic expression: " + logicExpression.toString());
			exprs.put(varName, logicExpression);
			label = new JLabel(varName + ": " + logicExpression.toString());
			label.setForeground(Color.white);
			

			varPanel.add(label);
			varPanel.add(delButton(varName, label));
			
			
			break;
		case "addExpressionSet":
			System.out.println("Adding expression set: " + ((JTextField) e.getSource()).getText());
			input = ((JTextField) e.getSource()).getText();
			parsingSegments = input
					.replace(" ", "").split(":");
			
			
			Set<PropLogic> expressions;
			
			switch(parsingSegments.length) {
			case 1:
				varName = generateMapKey();
				expressions = Arrays.stream(parsingSegments[0].split(","))
						.map(s->PropLogic.parse(s))
						.collect(Collectors.toSet());
				
				break;
			case 2:
				varName = parsingSegments[0];
				expressions = Arrays.stream(parsingSegments[1].split(","))
						.map(s->PropLogic.parse(s))
						.collect(Collectors.toSet());
				break;
			default:
				throw new IllegalArgumentException("Invalid format");
			}
			
			exprSets.put(varName, expressions);
			label = new JLabel(varName + ": " + expressions.toString());
			label.setForeground(Color.white);
			varPanel.add(label);
			varPanel.add(delButton(varName, label));
			break;
		case "addClause":
			System.out.println("Adding clause: " + ((JTextField) e.getSource()).getText());
			break;
		case "addClauseSet":
			System.out.println("Adding clause set: " + ((JTextField) e.getSource()).getText());
			break;
		default:
			break;
		}
	}
	
	public static String nextString(String string) {
		Boolean finished = false;
		String res = string;
		Integer index = string.length()-1;
		while(!finished && index <= 0) {
			String start = res.substring(0, index);
			char nextChar = (char) (((res.charAt(index) - 'A' + 1)% 26) + 'A');
			res = start + Character.toString(nextChar);
			finished = nextChar != 'A';
			index--;
		}
		return index<0&&!finished?"A" + res:res;
	}
	
	public String generateMapKey() {

		Set<String> keys = Stream.concat(exprSets.keySet().stream(),
				exprs.keySet().stream())
				.collect(Collectors.toSet());
		
		return Stream.iterate("A", s -> nextString(s))
				.filter(s->!keys.contains(s))
				.findFirst().get();
	}
	
	//Notes: Options on the bottom
}
