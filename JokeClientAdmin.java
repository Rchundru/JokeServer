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

when switching servers you will get an error saying "unknown mode please enter j for joke mode or
p for proverb mode or s to switch servers" you can ignore this, the server should have successfully switched
----------------------------------------------------------*/
import java.io.*;
import java.net.*;
public class JokeClientAdmin{
	public static void main (String args[]) {
		String serverName; // if a server is passed in than it stores that server name here
		String serverName2; // if a second server is passed om than it stores that second server in here
		boolean server1 = true; //server1 and server2 variables are used to keep track of which server the client is currently using
		boolean server2 = false;
		int port1 = 5050;
		int port2 = 5051; // uses this port if a secondary server is passed in
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
		System.out.println("Using server: " + serverName + ", Port: 5050");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		try {
			String response;
			do {
				System.out.print
				("enter j to enable joke mode or p to enable proverb mode or s to switch servers: ");
				System.out.flush ();
				response = in.readLine (); //waits for a user response
				if (response.indexOf("quit") < 0) {
					if(args.length < 1)
						getRemoteAddress(response, serverName, port1);
						else if(args.length < 2) {
							getRemoteAddress(response, serverName, port1);
						}
						else if(args.length<3) { //waits for a user response
							//getRemoteAddress(response, serverName, port1);
							String currentServer;
							int currentPort;
								if(response.equals("s")) {
									if(server1) { //if currently in server 1 it will switch to server 2
										System.out.println("Now entering " + serverName2);
							            getRemoteAddress(response, serverName2, port2);
							            currentServer = serverName2;
							            currentPort = port2;
							            server1 = false; //updates variables
							            server2 = true;
									}
									else if(server2) { //if currently on server 2 it will switch to server 1
										System.out.println("Now entering " + serverName);
										getRemoteAddress(response, serverName, port1);
										currentServer = serverName;
							            currentPort = port1;
										server1 = true;
										server2 = false; //updates variables
									}
									else {
										getRemoteAddress(response, serverName, port1);
										currentServer = serverName;
							            currentPort = port1;
									}
								} else { //used to stay in the switched server later onwards
									if(server1) {
									getRemoteAddress(response, serverName, port1);
									}
									else {
										getRemoteAddress(response, serverName2, port2);
									}
								}

						}
						else {
							getRemoteAddress(response, serverName, port1);
						}
					}
				}

			 while (response.indexOf("quit") < 0);
			System.out.println ("Cancelled by user request.");
		} catch (IOException x) {x.printStackTrace ();}
	}

	static String toText (byte ip[]) {
		StringBuffer result = new StringBuffer ();
		for (int i = 0; i < ip.length; ++ i) {
			if (i > 0) result.append (".");
			result.append (0xff & ip[i]);
		}
		return result.toString ();
	}

	static void getRemoteAddress (String name, String serverName, int port){
		Socket sock;
		BufferedReader fromServer;
		PrintStream toServer;
		String textFromServer;

		try{
			sock = new Socket(serverName, port);

			fromServer =
					new BufferedReader(new InputStreamReader(sock.getInputStream()));
			toServer = new PrintStream(sock.getOutputStream());
			toServer.println(name); toServer.flush();

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
