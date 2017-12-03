/*
	RPSNameDialog

	provides a dialog box used by the Rock Paper Scissors application
        to get a player's name.
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class RPSNameDialog extends JDialog
{
	private JButton okButton;
	private JButton quitButton;
	private JTextField nameField;
	private JLabel welcomeLabel;
	private JLabel promptLabel;
	private JPanel middlePane=new JPanel();
	private JPanel bottomPane=new JPanel();
	private RPSFrame frame;

	RPSNameDialog(RPSFrame owner, String title, boolean modal)
	{
		super(owner, title, modal);

		frame=owner;

		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		setSize(300,180);
		Point p = owner.getLocation();
		setLocation((int)(p.getX()+50), (int)(p.getY()+50));  //100,100);

		setTitle("Rock Paper Scissors");

		Container contentPane = getContentPane(); // Get a ref to the frame's content pane

		//	Create GUI components and define their properties

		welcomeLabel = new JLabel("Welcome to the Rock Paper Scissors Game");
		welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		welcomeLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

		promptLabel = new JLabel("Please enter your name:");

		nameField = new JTextField(20);
		nameField.setMaximumSize(new Dimension(80,20));

		okButton = new JButton("OK");
		okButton.setMaximumSize(new Dimension(80,30));

		quitButton = new JButton("Quit");
		quitButton.setMaximumSize(new Dimension(80,30));

		/*  Make everything visible -- great for debugging!
welcomeLabel.setOpaque(true);
welcomeLabel.setBackground(Color.CYAN);
middlePane.setOpaque(true);
middlePane.setBackground(Color.MAGENTA);
bottomPane.setOpaque(true);
bottomPane.setBackground(Color.YELLOW);
promptLabel.setOpaque(true);
promptLabel.setBackground(Color.YELLOW);
okButton.setOpaque(true);
okButton.setBackground(Color.RED);
quitButton.setOpaque(true);
quitButton.setBackground(Color.BLUE);
		 */

		//	Lay out components

		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
		contentPane.add(Box.createRigidArea(new Dimension(20,20)));
		contentPane.add(welcomeLabel);
		contentPane.add(Box.createGlue());
		contentPane.add(middlePane);
		contentPane.add(Box.createGlue());
		contentPane.add(bottomPane);
		contentPane.add(Box.createRigidArea(new Dimension(20,20)));

		middlePane.setLayout(new BoxLayout(middlePane, BoxLayout.LINE_AXIS));
		middlePane.add(promptLabel);
		middlePane.add(Box.createHorizontalStrut(10));
		middlePane.add(nameField);

		bottomPane.setLayout(new BoxLayout(bottomPane, BoxLayout.LINE_AXIS));
		bottomPane.add(okButton);
		bottomPane.add(quitButton);

		//	Define action listeners

		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				frame.setName(nameField.getText().trim());
				setVisible(false);
			}
		});

		quitButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				System.exit(0);
			}
		});

		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent we){
				System.exit(0);
			}
			public void windowClosed(WindowEvent we){
				((RPSFrame)frame).setName(nameField.getName());
			}
		});
	}

	public String getName(){
		return nameField.getText();
	}

}

