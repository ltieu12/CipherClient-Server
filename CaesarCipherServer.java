/**
 * Name: Lam Tieu
 * Banner ID: B00859543
 */
// import classes necessary for this program
import java.net.*;
import java.io.*;
import java.util.Date;
import java.lang.Math;

public class CaesarCipherServer{
	// create 2 constants of maximum and minimum values for the key
	final static int MIN = 1;
	final static int MAX = 25;
	public static void main(String[] args) throws IOException{
		// create a ServerSocket object to communicate with client
		ServerSocket serverSock = null;
		// assign a port number for ServerSocket object
		try{
			serverSock = new ServerSocket(50000);
		}
		// catch exception if there is any
		catch (IOException ie){
			System.out.println("Can't listen on 50000");
			System.exit(1);
		}
		// create a client socket (a 'mirror' of client in server process) and wait for the client to connect with the server
		Socket link = null;
		System.out.println("Listening for connection ...");
		// when the client sends a request, the server accepts the connection
		try {
			link = serverSock.accept();
		}
		catch (IOException ie){
			System.out.println("Accept failed");
			System.exit(1);
		}
		// indicate the user connection is successful and waiting for input
		System.out.println("Connection successful");
		System.out.println("Listening for input ...");

		// get references to streams associated with the socket connection using getOutputStream() and getInputStream()
		// here, we wrap OutputStream object with PrintWriter object, and InputStream with BufferedReader object to run in non-GUI application (Terminal, ..)
		PrintWriter output = new PrintWriter(link.getOutputStream(), true);
		BufferedReader input = new BufferedReader(new InputStreamReader(link.getInputStream()));
		// create 'conversation' between client and server
		// receiving and displaying input similarly to an I/O console
		String inputLine;
		// reference of generate a random integer within a range (inclusive): https://www.educative.io/answers/how-to-generate-random-numbers-in-java
		int key = 0;
		while ((inputLine = input.readLine())!=null){
			// if input equals to "Bye", stop the conversation
			if(inputLine.equals("Bye")){
				System.out.println("Closing connection");
				break;
			}
			// print the message from client
			System.out.println("Message from the client: " + inputLine);

			// check if key is updated to start decryption process
			if (key > 0 && key <= 25) {
				String decryptedMessage = "";
				char letter;
				for (int i = 0; i < inputLine.length(); i++) {
					// get each letter from the input
					letter = inputLine.charAt(i);
					// boolean variables to check whether the letter is uppercase or lowercase
					boolean inRangeUpperCase = letter >= 'A' && letter <= 'Z';
					boolean inRangeLowerCase = letter >= 'a' && letter <= 'z';

					// if it is a space, we keep it as is
					if (String.valueOf(letter).equals(" ")) {
						letter = letter;
					}
					// decrypt based on ASCII value
					// need to separate 2 scenarios uppercase and lowercase because uppercase letter values are smaller than lowercase

					// check the scenario where the letter is uppercase and needs to wrap around when decrypt
					else if (inRangeUpperCase && (letter - key) < 'A') {
						letter = (char) (letter - key + 26);
					}
					// check the scenario where the letter is lowercase and needs to wrap around when decrypt
					else if (inRangeLowerCase && (letter - key) < 'a') {
						letter = (char) (letter - key + 26);
					}
					// otherwise, just revert to the original letter
					else {
						letter = (char) (letter - key);
					}
					// add each letter back into a string
					decryptedMessage = decryptedMessage + letter;
				}
				// if decrypted message is "Bye", close the connection
				if (decryptedMessage.equals("Bye")) {
					System.out.println("Closing connection");
					break;
				}

				// print the decrypted message
				System.out.println("Decrypted: " + decryptedMessage);
			}
			// if key is not updated (has not been sent to client)
			else {
				// if client asks for the key, get the key randomly between 1 and 25 (inclusively) and send to the client
				if (inputLine.equals("Please send the key")) {
					// get a random integer within range. Reference: https://www.baeldung.com/java-generating-random-numbers-in-range
					key = (int) (Math.random() * (MAX - MIN + 1) + MIN);
					output.println(key);
				}
				// otherwise, just send the client request back to client
				else {
					output.println(inputLine);
				}
			}
		}
		// close all connections
		output.close();
		input.close();
		link.close();
		serverSock.close();
	}
}
