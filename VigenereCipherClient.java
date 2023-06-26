/**
 * Name: Lam Tieu
 * Banner ID: B00859543
 */
// import classes necessary for this program
import java.io.*;
import java.net.*;
public class VigenereCipherClient{
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
        String key = "";
        while ((usrInput = stdIn.readLine())!=null){
            // reset the key to extend correctly
            String extendedKey = key;
            // if keystring is updated (sent to client)
            if (!(key.equals(""))) {
                // if user input is longer than keystring, the key is repeated until match the length of input
                if (usrInput.length() > key.length()) {
                    int difference = usrInput.length() - key.length();
                    for (int i = 0; i < difference; i++) {
                        extendedKey = extendedKey + extendedKey.charAt(i);
                    }
                }

                String encryptedInput = "";
                char letter, keyLetter;
                for (int i = 0; i < usrInput.length(); i++) {
                    // get each letter from input and keystring to encrypt
                    letter = usrInput.charAt(i);
                    keyLetter = extendedKey.charAt(i);
                    // encrypt letter using ASCII value
                    char encryptedLetter = (char) (letter + keyLetter - 'A');
                    // if encrypted value is bigger than Z, wrap around to get letter within alphabet
                    if (encryptedLetter > 'Z') {
                        encryptedLetter = (char) (encryptedLetter - 26);
                    }
                    // put each encrypted letter into a string
                    encryptedInput = encryptedInput + encryptedLetter;
                }
                // send the client the encrypted message
                output.println(encryptedInput);
            }
            // if key has not been sent from the server
            else {
                // send the input to server
                output.println(usrInput);
                // if client asks for the key, get the key from the server
                if (usrInput.equals("Please send the key")) {
                    key = input.readLine();
                    System.out.println("Echo from Server: The key is " + key);
                }
                // otherwise, print the client input
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
