/*
	Rock Paper Scissors Server

	The server listens for connection requests from other players.

 */

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

public class HangmanServer
{
	private Hangman controller;
	private HangmanFrame frame;

	String name;
	String serverAddress;
	String portNumber;
	Socket gameSocket;
	ServerSocket serverSocket=null;
	HangmanFrame gameFrame;
	Thread listenThread;
	boolean hasConnection;

	HangmanServer(Hangman controller, HangmanFrame frame) 
	{
		this.controller = controller;
		gameFrame = frame;
	}

	/* The checkConnection method is called by the listenThread to wait
   		for incoming connections.
	 */

	private void checkConnection(){
		hasConnection=false;
		try {
			gameSocket = serverSocket.accept();
			hasConnection=true;
		}
		catch (SocketTimeoutException ste){
			System.out.println("socket timeout:"+ste);
		}
		catch (IOException e) {
			gameSocket = null;
			System.out.println("accept: "+e);
		}

		/* When a connection is made, send the gameSocket to the controller
       and create an RPSProtocol object to actually play the game.
		 */

		if(hasConnection){
			if(gameSocket==null){
				System.out.println("accept failed");
			}
			else {
				System.out.println("accepted connection from "+gameSocket.getInetAddress());
				//  tell the controller we got it
				controller.setSocket(gameSocket);
				gameFrame.setVisible(true);
				gameFrame.writeMessage("Accepted connection from "+gameSocket.getInetAddress());
				/* last parameter is true because I'm player one. I'll be choosing the word. */
				HangmanProtocol proto = new HangmanProtocol(gameSocket, gameFrame, true);
			}
		}

	} // end of checkConnection

	public Socket getGameSocket(){
		return gameSocket;
	}

	public boolean isConnected(){
		return hasConnection;
	}

	/* The listen method is called by the action listener for the
   "listen for connections" menu option.
	 */

	public void listen(){

		//  Create a server socket to listen for connections
		if(serverSocket==null){
			try {
				serverSocket = new ServerSocket();
			}
			catch (IOException e){
				serverSocket = null;
				System.out.println("ServerSocket: "+e);
				System.exit(1);
			}

			try {
				serverSocket.bind(new InetSocketAddress(InetAddress.getLocalHost(),1200));
			}
			catch (IOException e){
				System.out.println("bind: "+e);
			}
		}
		gameFrame.writeMessage("Listening for a connection");
		try {
			gameFrame.writeMessage("I'm listening at: " + InetAddress.getLocalHost());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/* Create a thread to wait for a connection request. */

		listenThread = new Thread(){
			public void run(){
				checkConnection();
			}
		};

		listenThread.start();
	}

}
