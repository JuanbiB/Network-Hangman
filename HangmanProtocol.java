/*
	Rock Paper Scissors Player Protocol
 */

import java.awt.event.*;
import java.net.*;
import java.util.Arrays;
import java.util.HashSet;
import java.io.*;

import javax.swing.*;

public class HangmanProtocol 
{
	private HangmanFrame gameFrame;
	private BufferedReader in;
	private PrintWriter out;
	private String name;
	private String otherName;
	private boolean alive=true;
	private Thread socketReader;
	private WordGuessDialog guessDialog;

	private String wordToGuess;
	private char[] guessedLetters;
	int numGuessed;
	HashSet<String> guessedAlready; 
	int guessTries;
	private static int GUESSTRIES = 6;
	boolean matchFinished;
	private int myScore = 0;
	private int opponentScore = 0;
	private static int sleepTime = 3000;

	/* Hangman */
	private boolean playerOne;

	HangmanProtocol(Socket gameSocket, HangmanFrame frame, boolean playerOne)
	{
		this.gameFrame = frame;
		this.playerOne = playerOne;
		name = frame.getName();
		matchFinished = false;

		addButtonListeners();

		try {
			in = new BufferedReader(new InputStreamReader(gameSocket.getInputStream()));
			out = new PrintWriter(gameSocket.getOutputStream(),true);
		}
		catch (IOException ex){
			System.out.println("socket streams: "+ex);
			System.exit(0);
		}

		out.println("NAME:"+name);
		SetUpPlayers();
		
		socketReader = new Thread(){
			String line;
			public void run(){
				while(alive){
					try {
						line=in.readLine();
						respondTo(line);
					}
					catch (IOException ioe){
						alive= false;
						doPartnerQuit();
					}
				}
			}
		};

		socketReader.start();

	}

	private void SetUpPlayers() {
		System.out.println("Setting up players.");
		matchFinished = false;
		if (playerOne) {
			gameFrame.HideGuessing();
			gameFrame.writeMessage("I'm player one. I'll be picking the word.\n");
			guessDialog = new WordGuessDialog(gameFrame, "Word Guess", true);
			guessDialog.addGuessListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					wordToGuess = guessDialog.getGuessWord();
					gameFrame.writeMessage("Player two will have to guess: " + wordToGuess);
					guessDialog.setVisible(false);
					out.println("READY:" + Integer.toString(wordToGuess.length()));
				}
			});
			guessDialog.setVisible(true);
			if (wordToGuess == null) {
				alive = false;
				out.print("QUIT:QUIT");
			}
			
			/* wordToGuess will be null if a player doens't pick a word and
			 * exits. */
			if (wordToGuess != null) {
				guessedLetters = new char[wordToGuess.length()];
				for (int i = 0 ; i < guessedLetters.length; i++) {
					guessedLetters[i] = '_';
				}
			}
			
			numGuessed = 0;
			guessTries = GUESSTRIES;
			guessedAlready = new HashSet<String>();

		}
		else {
			gameFrame.writeMessage("I'm player two, I'll be guessing the word.\n Waiting on other player to pick word...");
			guessedAlready = new HashSet<String>();
			gameFrame.ShowGuessing();
		}	
	}

	private void respondTo(String line){

		String[] words = line.split(":", 2);	
		if(words.length < 1) {
			System.out.println("Blank line received");
			return;
		}
		
		String msgID = words[0];
		String msg = words[1];
		
		if(msgID.equals("NAME")){
			if(words.length==1){
				System.out.println("Name missing in NAME message");
				otherName="unknown player";
			}
			else {
				otherName = msg;
			}
			gameFrame.setOtherName(otherName);
			gameFrame.writeMessage("Starting game with "+otherName+".\n"
					+ "Waiting for them to guess a letter...");
		}
		else if (msgID.equals("READY")){
			gameFrame.writeMessage("Other player picked word, you can start guessing!");
			gameFrame.writeMessage("The length of the word is: " + msg);
		}
		/* Guess message will only be received by player one. Check 
		 * guessCharacter method for details. */
		else if(msgID.equals("GUESS")){
			handleGuess(words);
		}
		else if (msgID.equals("FOUND")){
			//int guessesLeft = Integer.parseInt(msg);
			//drawStickFigure(guessesLeft);
			gameFrame.writeMessage(msg);
		}
		// Here we would draw a body part of the stick man.
		else if (msgID.equals("NOTFOUND")) {
			int guessesLeft = Integer.parseInt(msg);
			drawStickFigure(guessesLeft);
			gameFrame.writeMessage("That letter doesn't exist in the word. I have " + msg + " guesses left.");
		}
		else if (msgID.equals("WON") || msgID.equals("LOST")){
			gameFrame.writeMessage("\n" + msgID);
			
			if (msgID.equals("WON")) {
				myScore++;
			}
			else {
				opponentScore++;
			}
			
			gameFrame.writeMessage("My score: " + Integer.toString(myScore));
			gameFrame.writeMessage("Their score: " + Integer.toString(opponentScore));
			gameFrame.writeMessage("Starting new game as player one.\n"
					+ "<--------------------->\n");
			/* Sleeping thread so player has some time to read outcome before next 
			 * match. */
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			playerOne = !playerOne;
			SetUpPlayers();
		}
		else if(msgID.equals("QUIT")){
			doPartnerQuit();
		}
		else {
			System.out.println("Received unknown message: "+line);
		}
		
	}

	private void handleGuess(String[] words) {
		char guess = words[1].charAt(0);
		if (wordToGuess.contains(guess + "")) {
			for (int i = 0; i < wordToGuess.length(); i++) {
				if (wordToGuess.charAt(i) == guess) {
					guessedLetters[i] = wordToGuess.charAt(i);
					numGuessed++;
				}
			}
			/* Player one (word picker)  lose condition. Player two guesses the word.*/
			if (numGuessed == guessedLetters.length) {
				out.println("WON: You WON! The word was: " + wordToGuess + "\n");
				opponentScore++;
				
				gameFrame.writeMessage("My score: " + Integer.toString(myScore));
				gameFrame.writeMessage("Their score: " + Integer.toString(opponentScore));
				
				gameFrame.writeMessage("\nYou LOSE, they guessed your word(" + wordToGuess + ")\n Starting a new game as "
						+ "player two.\n"
						+ "<--------------------->\n");
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				matchFinished = true;
			}
			else {
				out.println("FOUND:You guessed character: " + guess + ". Progress: " + 
						Arrays.toString(guessedLetters));
			}
		}
		else {
			/* Player one (word picker) win condition. Player two misses too many words and stick man 
			 * is drawn and hung. */
			guessTries--;
			if (guessTries <= 0) {
				myScore++;
				gameFrame.writeMessage("You win! Starting a new game as player two...");
				gameFrame.writeMessage("My score: " + Integer.toString(myScore));
				gameFrame.writeMessage("Their score: " + Integer.toString(opponentScore));
				out.println("LOST:The stick figure has been hung! The word was: " + wordToGuess);
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				matchFinished = true;
			}
			else {
				gameFrame.writeMessage("Whoops, that letter doesn't exist! They have " + Integer.toString(guessTries) + 
						" guesses left.");
				out.println("NOTFOUND:" + Integer.toString(guessTries));
			}
		}
		if (!matchFinished){
			gameFrame.writeMessage(otherName + " guessed: " + words[1]);
			gameFrame.writeMessage("Their progress: " +  Arrays.toString(guessedLetters));
			drawStickFigure(guessTries);
			gameFrame.writeMessage("\nWaiting for them to guess a letter...");
		}
		else {
			/* Switch from word picker to guesser.*/
			playerOne = !playerOne;
			SetUpPlayers();
		}
	}

	// TODO: integrate Jframes to actually draw a stick figure.
	private void drawStickFigure(int guessesLeft){
		switch (guessesLeft){
		case(0):
			gameFrame.writeMessage("___________________\n"
								 + "|\t  |\n"
								 + "|\t O\n"
								 + "|\t-|-\n"
								 + "|\t  |\n"
								 + "|\t/   \\\n"
								 + "|_____________________________\n");
			break;
		case(1):
			gameFrame.writeMessage("___________________\n"
					 			 + "|\t  |\n"
					 			 + "|\t O\n"
					 			 + "|\t-|-\n"
					 			 + "|\t  |\n"
					 			 + "|\t/\n"
					 			 + "|_____________________________\n");
		break;
		case(2):
			gameFrame.writeMessage("___________________\n"
					 			 + "|\t  |\n"
					 			 + "|\t O\n"
					 			 + "|\t-|-\n"
					 			 + "|\t  |\n"
					 			 + "|\n"
					 			 + "|_____________________________\n");			
		break;
		case(3): 
			gameFrame.writeMessage("___________________\n"
					 			 + "|\t  |\n"
					 			 + "|\t O\n"
					 			 + "|\t-|\n"
					 			 + "|\t  |\n"
					 			 + "|\n"
					 			 + "|_____________________________\n");
			break;
		case(4): 
			gameFrame.writeMessage("___________________\n"
		 			 + "|\t  |\n"
		 			 + "|\t O\n"
		 			 + "|\t  |\n"
		 			 + "|\t  |\n"
		 			 + "|\n"
		 			 + "|_____________________________\n");		
		break;
		case(5): 
			gameFrame.writeMessage("___________________\n"
		 			 + "|\t  |\n"
		 			 + "|\t O\n"
		 			 + "|\t\n"
		 			 + "|\t\n"
		 			 + "|\n"
		 			 + "|_____________________________\n");			
		break;
		case(6): 
			gameFrame.writeMessage("___________________\n"
					 + "|\t|\n"
					 + "|\n"
					 + "|\n"
					 + "|\n"
					 + "|\n"
					 + "|_____________________________ \n");			
		break;
		}
	}
	
	private void doPartnerQuit(){
		out.close();
		gameFrame.writeMessage(otherName+" has left the game");
		JOptionPane.showMessageDialog(gameFrame,
				otherName+" has left the game.  Exiting", "Exit", JOptionPane.PLAIN_MESSAGE);
		System.exit(0);
	}
	
	private void addButtonListeners(){
		gameFrame.addGuessButtonListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				guessCharacter();
			}
		});

		gameFrame.addQuitButtonListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				out.println("QUIT:QUIT");
				alive=false;
				out.close();
				System.exit(0);
			}
		});

		gameFrame.addWindowClosingListener(new WindowAdapter(){
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				out.println("QUIT");
				alive=false;
				out.close();
				System.exit(0);
			}
		});
	}

	private void guessCharacter() {
		if (playerOne) {
			gameFrame.writeMessage("You're player one, you're not guessing!");
			return;
		}
		String guess = gameFrame.getGuess().trim().toLowerCase();
		if (guess.length() != 1) {
			gameFrame.writeMessage("Your guess has to be one character!");
			return;
		}
		else if (guessedAlready.contains(guess)){
			gameFrame.writeMessage("You've already guessed that letter...");
			return;
		}
		// TODO: error checking
		gameFrame.writeMessage("I guessed: " + guess);
		guessedAlready.add(guess);
		out.println("GUESS:" + guess);
	}


}
