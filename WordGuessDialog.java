/*
	RPSConnectFrame -- provides a dialog box used by the Hangman 	application to request up a connection.
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class WordGuessDialog extends JDialog
{
	private JButton connectButton;
	private JButton cancelButton;
	private JTextField serverField;
	private JTextField portField;
	private JLabel serverLabel;
	private JLabel messageLabel;
	private JPanel serverPane = new JPanel();
	private JPanel buttonPane = new JPanel();
	
	/* The constructor sets up the GUI components. */
	WordGuessDialog(HangmanFrame owner, String title, boolean modal)
	{
		super(owner, title, modal);
		
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setSize(300,180);
		setLocationRelativeTo(owner);
		setTitle("Guess word");

		//	Create components

		serverLabel = new JLabel("Word to guess: ");

		messageLabel = new JLabel("                  ");
		messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		messageLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		serverField = new JTextField("                  ");
		serverField.setMaximumSize(new Dimension(100,20));
		
		connectButton = new JButton("Choose"); // create a JButton object
		connectButton.setMaximumSize(new Dimension(90,25));
		
		cancelButton = new JButton("Cancel");	
		cancelButton.setMaximumSize(new Dimension(90,25));

		//	Lay out components

		Container contentPane = getContentPane(); // Get a ref to the frame's content pane
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
		contentPane.add(Box.createGlue());
		contentPane.add(serverPane);
		contentPane.add(Box.createGlue());

		contentPane.add(buttonPane);
		contentPane.add(Box.createGlue());
		contentPane.add(messageLabel);
		contentPane.add(Box.createGlue());

		serverPane.setLayout(new BoxLayout(serverPane, BoxLayout.LINE_AXIS));
		serverPane.add(serverLabel);
		serverPane.add(serverField);  // add it to the JPanel

		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		buttonPane.add(connectButton);  // add it to the JPanel
		buttonPane.add(Box.createHorizontalStrut(40));
		buttonPane.add(cancelButton);

		//	Add action listeners

		cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				setVisible(false);
			}
		});

	}

	public void addGuessListener(ActionListener listener)
	{
		connectButton.addActionListener(listener);
	}

	public String getGuessWord(){
		return serverField.getText().trim().toLowerCase();
	}

	public String getPortNumber(){
		return portField.getText();
	}

	public void setMessageText(String text){
		messageLabel.setText(text);
	}

}

