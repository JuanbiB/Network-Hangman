/*
	RPSFrame -- provides the GUI frame for the Rock Paper Scissors application
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.DefaultCaret;

public class HangmanFrame extends JFrame
{
	
	private JButton quitButton;
	private JTextArea messageArea;
	private JScrollPane messagePane;
	private String myChoice;
	private String myName;
	private String otherName;
	private JPanel buttonPanel;
	private JMenuBar menuBar;
	private JMenu connectMenu;
	private JMenuItem listenItem;
	private JMenuItem connectItem;
	private JMenuItem disconnectItem;
	//private boolean hasPlayed=false;
	
	/* Hangman fields*/
	private JTextField letterGuess;
	private JLabel letterGuessLabel;
	private JButton guessButton;

	HangmanFrame()
	{
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		//    setSize(400,200);
		setLocation(50,50);
		setTitle("Hangman");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Container contentPane = getContentPane(); // Get a reference to the frame's content pane
		
		/* Hangman fields */
		letterGuessLabel = new JLabel("Guess a character: ");
		letterGuess = new JTextField("      ");
		guessButton = new JButton("Guess");

		buttonPanel = new JPanel();  // create a JPanel object
		buttonPanel.add(letterGuessLabel);
		buttonPanel.add(letterGuess);
		buttonPanel.add(guessButton);
		
		contentPane.add(buttonPanel, BorderLayout.SOUTH); // add the panel to the content pane

		messageArea = new JTextArea(20,40);
		messagePane = new JScrollPane(messageArea);

		contentPane.add(messagePane, BorderLayout.CENTER);

		menuBar = new JMenuBar();
		connectMenu = new JMenu("Connections");
		menuBar.add(connectMenu);
		listenItem = new JMenuItem("Listen for connections", KeyEvent.VK_L);
		connectItem = new JMenuItem("Connect to ...", KeyEvent.VK_C);
		disconnectItem = new JMenuItem("Disconnect");
		connectMenu.add(listenItem);
		connectMenu.add(connectItem);
		connectMenu.add(disconnectItem);
		setJMenuBar(menuBar);

		quitButton = new JButton("Quit");
		buttonPanel.add(quitButton);
		pack();
	}

	public JMenuItem getConnectItem(){
		return connectItem;
	}

	public JMenuItem getListenItem(){
		return listenItem;
	}

	public JMenuItem getDisconnectItem(){
		return disconnectItem;
	}

	public String getPlay(){
		return myChoice;
	}
	
	public String getGuess(){
		String guess = letterGuess.getText().trim().toLowerCase();
		letterGuess.setText("   ");
		return guess;
	}
	
	public void HideGuessing(){
		letterGuess.setVisible(false);
		letterGuessLabel.setVisible(false);
		guessButton.setVisible(false);
	}
	
	public void ShowGuessing(){
		letterGuess.setVisible(true);
		letterGuessLabel.setVisible(true);
		guessButton.setVisible(true);
	}
	
	/* Hangman */
	public void addGuessButtonListener(ActionListener listener) {
		guessButton.addActionListener(listener);
	}

	public void addWindowClosingListener(WindowListener listener){
		this.addWindowListener(listener);
	}

	public void addQuitButtonListener(ActionListener listener)
	{
		quitButton.addActionListener(listener);
	}

	public String getName(){
		return myName;
	}

	public void setName(String name){
		myName = name;
		writeMessage("My name is "+name);
	}

	public void setOtherName(String name){
		otherName = name;
		writeMessage("My opponent's name is "+name);
	}

	public void writeMessage(String text){
		messageArea.append(text+"\n");
		messageArea.setCaretPosition(messageArea.getDocument().getLength());
	}

	public static void main(String[] args){
		HangmanFrame frame = new HangmanFrame();
		frame.setVisible(true);
	}




}

