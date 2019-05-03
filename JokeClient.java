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
import java.util.*; // used for scanner
public class JokeClient{
	static boolean[] jokeGiven = new boolean[] {false, false, false, false}; //stores the state of the jokes i.e. which jokes have been used and which have not
	static boolean[] proverbGiven = new boolean[] {false, false, false, false}; //stores the state of the proverbs i.e. which proverbs have been used and which have not
	public static void main (String args[]) {
		String serverName; // if a server is passed in than it stores that server name here
		String serverName2; // if a second server is passed om than it stores that second server in here
		boolean server1 = true; //server1 and server2 variables are used to keep track of which server the client is currently using
		boolean server2 = false;
		int port1 = 4545;
		int port2 = 4546; // uses this port if a secondary server is passed in
		if (args.length < 1) { //no arguments passed in
			serverName = "localhost";
			serverName2 = "localhost";
		}
		else if(args.length < 2) { // one ip/host passed in
			serverName = args[0];
			serverName2 = "localhost";
		}
		else if(args.length < 3) { // two IPs/hosts passed in
			System.out.println("Enter s to switch servers");
			serverName = args[0];
			serverName2 = args[1];
		}
		else { // user tried to pass in more than 2 IPs
			System.out.println("Can only connect two 2 servers. connecting to localhost");
			serverName = "localhost";
			serverName2 = "localhost";
		}

		System.out.println("Rohit Chundrus Joke Client, 1.8.\n");
		System.out.println("Using server: " + serverName + ", Port: 4545");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter your name: "); // asks for the users name
		System.out.flush ();
		String inputName = scanner.nextLine(); //stores that name here
		try {
			String response;
			do {
				System.out.print
				("Press enter for joke/proverb or enter s to switch servers ");
				System.out.flush ();
				response = in.readLine (); //waits for a user response
				if (response.indexOf("quit") < 0) {
					if(args.length < 1)
					getRemoteAddress(response, serverName, port1, inputName);
					else if(args.length < 2) {
						getRemoteAddress(response, serverName, port1, inputName);
					}
					else if(args.length<3) { //user wanted to switch server
						//getRemoteAddress(response, serverName, port1);
						String currentServer;
						int currentPort;
							if(response.equals("s")) {
								if(server1) { //if currently in server 1 it will switch to server 2
									System.out.println("Now entering " + serverName2);
						            getRemoteAddress(response, serverName2, port2, inputName);
						            currentServer = serverName2;
						            currentPort = port2;
						            server1 = false; //updates variables
						            server2 = true;
								}
								else if(server2) { //if currently on server 2 it will switch to server 1
									System.out.println("Now entering " + serverName);
									getRemoteAddress(response, serverName, port1, inputName);
									currentServer = serverName;
						            currentPort = port1;
									server1 = true; //updates variables
									server2 = false;
								}
								else {
									getRemoteAddress(response, serverName, port1, inputName);
									currentServer = serverName;
						            currentPort = port1;
								}
							} else { //used to stay in the switched server later onwards
								if(server1) {
								getRemoteAddress(response, serverName, port1, inputName);
								}
								else {
									getRemoteAddress(response, serverName2, port2, inputName);
								}
							}

					}
					else {
						getRemoteAddress(response, serverName, port1, inputName);
					}
				}
			} while (response.indexOf("quit") < 0);
			System.out.println ("Cancelled by user request.");
		} catch (IOException x) {x.printStackTrace ();}
	}

	 public static boolean getJokesGiven(int index) { //returns whether a joke has been given or not
		 return jokeGiven[index];
	 }
	 public static void setJokesGiven(int index, boolean flag) { //if a joke has been given sets that joke to true, otherwise false if it is resetting.
		 jokeGiven[index] = flag;
	 }
	 public static boolean getProverbsGiven(int index) { //returns whether a proverb has been used or not
		 return proverbGiven[index];
	 }
	 public static void setProverbsGiven(int index, boolean flag) { ////if a proverb has been given sets that proverb to true, otherwise false if it is resetting.
		 proverbGiven[index] = flag;
	 }

	static String toText (byte ip[]) {
		StringBuffer result = new StringBuffer ();
		for (int i = 0; i < ip.length; ++ i) {
			if (i > 0) result.append (".");
			result.append (0xff & ip[i]);
		}
		return result.toString ();
	}

	static void getRemoteAddress (String name, String serverName, int port, String userName){
		Socket sock;
		BufferedReader fromServer;
		PrintStream toServer;
		String textFromServer;

		try{
			//opens a connection to either the primary or secondary server, port number has to be passed in
			sock = new Socket(serverName, port);

			fromServer =
					new BufferedReader(new InputStreamReader(sock.getInputStream()));
			toServer = new PrintStream(sock.getOutputStream());

			toServer.println(name);
			toServer.println(userName); //sends the users name to the server
			toServer.flush();

			for (int i = 1; i <=3; i++){
				textFromServer = fromServer.readLine();
				if (textFromServer != null) System.out.println(textFromServer);
			}
			sock.close();
		} catch (IOException x) {
			System.out.println ("Socket error.");
			x.printStackTrace ();
		}
	}
}
