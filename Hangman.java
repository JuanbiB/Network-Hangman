/*
	Rock Paper Scissors

	This program contains the main method of the Rock Paper Scissors game
	application, which creates an instance of the RPS class.

	The RPS constructor does the following:

	1.  Creates an RPSFrame (the main frame for the game).
	2.  Creates an RPSServer, in case this player wants to listen for
         connections from other players.
     3.  Sets up action listeners for the "connect" and "listen" options
         on the connections menu.
	4.  Creates dialog boxes for getting the user's name and for requesting
         a connection.
 */

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

public class Hangman
{
	private HangmanFrame frame;
	private HangmanNameDialog nameDialog;
	private HangmanConnectDialog connectDialog;
	private HangmanServer server;
	public static final int NOT_CONNECTED=1, LISTENING=2, CONNECTED=3;

	private String serverAddress;
	private int portNumber;
	private Socket gameSocket; /* This is the socket that will be used for 	communicating game protocol messages to the opponent */
	private int connectState=NOT_CONNECTED;

	Hangman() throws IOException 
	{
		frame = new HangmanFrame();

		server = new HangmanServer(this, frame);

		/* Action listener for a connection request.  Just make the
  	   connect dialog box visible.
		 */

		frame.getConnectItem().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				updateState();
				if(connectState==CONNECTED)
					frame.writeMessage("Already connected");
				else {
					connectDialog.setMessageText("");
					connectDialog.setVisible(true);
				}
			}
		});
		/*  Action listener for a listen request.  Tell the RPSServer to
         listen for connections.
		 */ 

		frame.getListenItem().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				updateState();
				if(connectState==NOT_CONNECTED){
					connectState=LISTENING;
					server.listen();
				}
				else if(connectState==CONNECTED)
					frame.writeMessage("Already connected");
				else
					frame.writeMessage("Already listening for connections");
			}
		});
		
		// TODO: How should we handle disconnecting?
		frame.getDisconnectItem().addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.writeMessage("Disconnected...");
			}
		});

		nameDialog = new HangmanNameDialog(frame, "Hangman", true);

		connectDialog = new HangmanConnectDialog(frame, "Hangman", true);

		/*  Action listener for the connect dialog box.  It starts a new
 	    thread to make a connection request to a user-specified IP
	    address and port number.
		 */

		connectDialog.addConnectListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				serverAddress = connectDialog.getServerAddress();

				try {
					portNumber = Integer.parseInt(connectDialog.getPortNumber().trim());
				}
				catch (NumberFormatException nfe){
					portNumber = 1200;
					System.out.println("parseInt:"+nfe);
				}

				Thread connectThread = new Thread(){
					public void run(){
						connect();
					}
				};

				connectThread.start();
			}
		});

		frame.setVisible(true);
		nameDialog.setVisible(true);
	}

	/*
    Connect to a server to start a game of RPS.  This method
    is called by connectThread in response to a connection
    request by the interactive user.
	 */

	private void connect(){
		/*  Attempt connection.  */
		try {
			gameSocket = new Socket(serverAddress.trim(),portNumber);
		}
		catch(IOException ex){
			gameSocket = null;
			System.out.println("connect: "+ex);
			connectDialog.setMessageText("** Connection attempt failed **");
		}

		/*  If connection was successful, start up the game.  */

		if(gameSocket!=null){
			System.out.println("connected to "+gameSocket.getInetAddress());
			connectDialog.setVisible(false);
			connectState=CONNECTED;
			frame.writeMessage("Connected to "+gameSocket.getInetAddress());
			frame.setVisible(true);
			/* Last parameter set to false because I'm player two. I'll be guessing the word. */
			HangmanProtocol proto = new HangmanProtocol(gameSocket, frame, false);
		}
	}

	public void setSocket(Socket socket){
		gameSocket = socket;
	}

	private void updateState(){
		if(connectState==LISTENING && server.isConnected())
			connectState=CONNECTED;
	}

	public void setState(int newState){
		connectState = newState;
	}

	public static void main(String args[]) throws IOException
	{
		new Hangman();
	}


}
