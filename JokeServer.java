/*--------------------------------------------------------

1. Rohit Chundru / Date: 4/21/19

2. Java version used, if not the official version for the class: 1.8.0_181-b13

3. Precise command-line compilation examples / instructions:

>javac JokeServer.java
>javac JokeClient.java
>javac JokeClientAdmin.java


4. Precise examples / instructions to run this program:

In separate shell windows:

> java JokeServer
> java JokeServer secondary      <------ to run the joke server from secondary server
> java JokeClient
> java JokeClient localhost localhost       <------ to run joke client from primary and secondary server
> java JokeClientAdmin
> java JokeClientAdmin localhost localhost  <------ to run joke client admin from primary and secondary server

All acceptable commands are displayed on the various consoles.

This runs across machines, in which case you have to pass the IP address of
the server to the clients. For exmaple, if the server is running at
140.192.1.22 then you would type:

> java JokeClient 140.192.1.22
> java JokeClientAdmin 140.192.1.22

5. List of files needed for running the program.

 a. checklist.html
 b. JokeServer.java
 c. JokeClient.java
 d. JokeClientAdmin.java

5. Notes:


----------------------------------------------------------*/
import java.io.*;
import java.net.*;
import java.lang.Math;

 class AdminWorker extends Thread{ //used for admin connection
	  Socket sock;
	  AdminWorker (Socket s) {sock = s;}
	  public void run() {
			PrintStream out = null;
			BufferedReader in = null;
			try {
				in = new BufferedReader
						(new InputStreamReader(sock.getInputStream()));
				out = new PrintStream(sock.getOutputStream());
				try {
					String response;
					response = in.readLine(); //takes in the users response from the client
					if(response.equals("j")) {
						JokeServer.setJoke(true); //changes server to joke mode
						JokeServer.setproverb(false);
						if(JokeServer.port == 4545)
						System.out.println("Now in joke mode."); //output if in server 1
						else System.out.println("Now in joke mode. " + "<S2>"); //output if in server 2
					}
					else if(response .equals("p")) {
						JokeServer.setJoke(false);;
						JokeServer.setproverb(true);; //changes server to proverb mode
						if(JokeServer.port==4545)
						System.out.println("Now in proverb mode"); //output if in server 1
						else System.out.println("Now in proverb mode. " + "<S2>"); //output if in server 2
					}
					else { //user entered a unknown command
						out.println("unknown mode please enter j for joke mode or p for proverb mode or s to switch servers: ");
						System.out.println(response);
					}
				} catch(IOException x) {
					System.out.println("Server read error");
					x.printStackTrace();
				}
				sock.close();
			} catch(IOException ioe) {System.out.println(ioe);}
		}
  }

 class Worker extends Thread{ //for client connection
	Socket sock;
	Worker (Socket s) {sock = s;}
	public void run() {
		PrintStream out = null;
		BufferedReader in = null;
		try {
			in = new BufferedReader
					(new InputStreamReader(sock.getInputStream()));
			out = new PrintStream(sock.getOutputStream());
			try {
				String name;
				String userName;
				name = in.readLine();
				userName = in.readLine(); //reads in the users name from the client
				if(JokeServer.port == 4545) {
				out.println("Hey " + userName + " " + printResponse()); //prints response for server 1
				}else out.println("Hey " + userName + " " + printResponse() + " <S2>"); //prints response for server 2
			} catch(IOException x) {
				System.out.println("Server read error");
				x.printStackTrace();
			}
			sock.close();
		} catch(IOException ioe) {System.out.println(ioe);}
	}



	static String printResponse() {
		//stores the jokes in an array
		String[] jokes = new String[] {"Whats red and smells like blue paint? Red paint.",
				"How does NASA organize a party? They planet.",
				"What creature is smarter than a talking parrot? A spelling bee",
				"How does the ocean say hello? It waves"};

		//store the proverbs in an array
		String[] proverbs = new String[] {"Absence makes the heart grow fonder",
				"A monkey in silk is a monkey no less",
				"Early to bed and early to rise, makes a man healthy, wealthy and wise",
				"A picture paints a thousand words"};
		while(true) {
		if(JokeServer.getJokeStatus()) { //if the server is in joke mode
			int random = (int) (Math.random()*4); //gets a random number between 0 and 3
			if(!JokeClient.getJokesGiven(random)) { //makes sure the joke at that index hasnt been used
			System.out.println("Joke given " + random); //outputs on server which joke has been given
			JokeClient.setJokesGiven(random, true); //sets the status of that joke to true so that it wont be used again
			return jokes[random]; //returns the joke
			}

			//if all jokes in the array have been used it resets the status and starts over
			if(JokeClient.getJokesGiven(0) && JokeClient.getJokesGiven(1) &&
				JokeClient.getJokesGiven(2) && JokeClient.getJokesGiven(3)) {
			System.out.print("Joke Cycle Complete, Restarting...\n");
			for(int i = 0; i < 4; i++) {
				JokeClient.setJokesGiven(i, false);;
			}
		}
		}
		else { //if the server is in proverb mode, follows the same logic as joke
			int random = (int) (Math.random()*4);
			if(!JokeClient.getProverbsGiven(random)) {
			System.out.println("Proverb given " + random);
			JokeClient.setProverbsGiven(random, true);
			return proverbs[random];
			}
		}
		if(JokeClient.getProverbsGiven(0) && JokeClient.getProverbsGiven(1) &&
				JokeClient.getProverbsGiven(2) && JokeClient.getProverbsGiven(3)) {
			System.out.print("Proverb Cycle Complete, Restarting...\n");
			for(int i = 0; i < 4; i++) {
				JokeClient.setProverbsGiven(i, false);
			}
		}
		}
	}

	static String toText (byte ip[]) {
		StringBuffer result = new StringBuffer();
		for(int i = 0; i < ip.length; ++i) {
			if(i>0) {
				result.append(0xff & ip[i]);
			}
		}
		return result.toString();
	}
}

 public class JokeServer{
	 static boolean joke = true;
	 static boolean proverb = false;
	 //static String name = "";
	 static int port; //port number is stored as a global variable, will need this for multi server connections.
	 public static void main(String a[]) throws IOException{
		 int q_len = 6;
		 if (a.length < 1) port = 4545;
		 else if(a[0].equals("secondary")) port = 4546; //if the "secondary" argument is passed in that it connects to this port instead
		 else {
			 System.out.println("unknown argument, connecting to port 4545.");
			 port = 4545;
		 }
		 Socket sock;

		 // taken from joke-threads.html
		 AdminLooper AL = new AdminLooper();
		 Thread t = new Thread(AL);
		 t.start();

		 ServerSocket servsock = new ServerSocket(port, q_len);

		 System.out.println("Rohit Chundru's Joke server 1.8 starting up, listening at port " + port+ "\n");
		 while(true) {
			 sock = servsock.accept();
			 new Worker(sock).start();
		 }
	 }

	 //used to set the server in joke mode
	 public static void setJoke(boolean flag) {
		 joke = flag;
	 }

	 //returns if the server is in joke mode or not
	 public static boolean getJokeStatus() {
		 return joke;
	 }

	 //used to set the server in proverb mode
	 public static void setproverb(boolean flag) {
		 proverb = flag;
	 }

	 //returns if the server is in proverb mode or not
	 public static boolean getProverbStatus() {
		 return proverb;
	 }
 }

 //from joke-threads.html
 class AdminLooper implements Runnable {
	  public static boolean adminControlSwitch = true;

	  public void run(){
	    System.out.println("In the admin looper thread");
	    int port;
	    int q_len = 6;
	    if(JokeServer.port == 4545) { //checks if there is a secondary server open
	        port = 5050;
	    } else port = 5051;
	    Socket sock;

	    try{
	      ServerSocket servsock = new ServerSocket(port, q_len);
	      while (adminControlSwitch) {
		sock = servsock.accept();
		new AdminWorker (sock).start();
	      }
	    }catch (IOException ioe) {System.out.println(ioe);}
	  }
}
