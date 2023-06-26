/**
 * Name: Lam Tieu
 * Banner ID: B00859543
 */
// import classes necessary for this program
import java.io.*;
import java.net.*;
public class CaesarCipherClient{
	public static void main(String[] args) throws IOException{
		// initialize a Socket object, a PrintWriter and BufferedReader object
		Socket link = null;
		PrintWriter output = null;
		BufferedReader input = null;

		try{
			// create a link with local machine IP address as destination, and port number of the server
			// IP address starts with '127' is a loop-back address, that the packet does not leave the machine
			link = new Socket("127.0.0.1", 50000);
			// get output from getOutputStream() and wrap it in PrintWriter object
			output = new PrintWriter(link.getOutputStream(), true);
			// get input with getInputStream() and wrap it in BufferedReader object
			input = new BufferedReader(new InputStreamReader(link.getInputStream()));
		}
		// catch exception if there is any
		catch(UnknownHostException e)
		{
			System.out.println("Unknown Host");
			System.exit(1);
		}
		catch (IOException e){
			System.out.println("Cannot connect to host");
			System.exit(1);
		}
		// receive and send messages between client and server
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		String usrInput;
		int key = 0;
		// reading input from the client
		while ((usrInput = stdIn.readLine())!=null){
			// if key is sent from the server, start the encryption process
			if (key > 0 && key <= 25) {
				String encryptedInput = "";
				char letter;
				for (int i = 0; i < usrInput.length(); i++) {
					// get each letter from the input
					letter = usrInput.charAt(i);
					// boolean variables to check whether the letter is uppercase or lowercase
					boolean inRangeUpperCase = letter >= 'A' && letter <= 'Z';
					boolean inRangeLowerCase = letter >= 'a' && letter <= 'z';

					// if it is a space, we keep it as is
					if (String.valueOf(letter).equals(" ")) {
						letter = letter;
					}
					// encrypt based on ASCII value
					// need to separate 2 scenarios uppercase and lowercase because lowercase letter values are bigger than uppercase

					// check the scenario where the letter is uppercase and needs to wrap around when encrypt
					else if (inRangeUpperCase && (letter + key) > 'Z') {
						letter = (char) (letter + key - 26);
					}
					// check the scenario where the letter is lowercase and needs to wrap around when encrypt
					else if (inRangeLowerCase && (letter + key) > 'z') {
						letter = (char) (letter + key - 26);
					}
					// otherwise, encrypt to a new letter
					else {
						letter = (char) (letter + key);
					}
					// add each letter back into a string
					encryptedInput = encryptedInput + letter;
				}
				// send the encrypted message to the server
				output.println(encryptedInput);
			}
			// if key is not updated (has not been sent by server)
			else {
				// send the original request to the server
				output.println(usrInput);
				// if input is this command, get the key from the server
				if (usrInput.equals("Please send the key")) {
					key = Integer.parseInt(input.readLine());
					System.out.println("Echo from Server: The key is " + key);
				}
				// otherwise, print the message from the server
				else {
					System.out.println("Echo from Server: " + input.readLine());
				}
			}
		}
		// close all connections
		output.close();
		input.close();
		stdIn.close();
		link.close();
	}
}
