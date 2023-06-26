/**
 * Name: Lam Tieu
 * Banner ID: B00859543
 */
// import classes necessary for this program
import java.net.*;
import java.io.*;
import java.util.Date;
import java.lang.Math;

public class VigenereCipherServer{
    // create a fixed length for the key
    final static int KEY_LENGTH = 7;
    // create 2 constants of maximum and minimum values (ASCII values accordingly for each letter) for the key
    final static int LETTER_A = 65;
    final static int LETTER_Z = 90;

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
        String key = "";
        while ((inputLine = input.readLine())!=null){
            // if input equals to "Bye", stop the conversation
            if(inputLine.equals("Bye")){
                System.out.println("Closing connection");
                break;
            }
            // print the message from client
            System.out.println("Message from the client: " + inputLine);
            // reset the key to extend correctly
            String extendedKey = key;
            // check if key is updated to start decrypting process
            if (!(key.equals(""))) {
                // if user input is longer than keystring, the key is repeated until match the length of input
                if (inputLine.length() > key.length()) {
                    int difference = inputLine.length() - key.length();
                    for (int i = 0; i < difference; i++) {
                        extendedKey = extendedKey + extendedKey.charAt(i);
                    }
                }

                String decryptedMessage = "";
                char letter, keyLetter;
                for (int i = 0; i < inputLine.length(); i++) {
                    // get each letter from input and keystring to decrypt
                    letter = inputLine.charAt(i);
                    keyLetter = extendedKey.charAt(i);
                    // decrypt letter using ASCII value
                    char decryptedLetter = (char) (letter - keyLetter + 'A');
                    // if decrypted value is smaller than A, wrap around to get letter within alphabet
                    if (decryptedLetter < 'A') {
                        decryptedLetter = (char) (decryptedLetter + 26);
                    }
                    // put each decrypted letter into a string
                    decryptedMessage = decryptedMessage + decryptedLetter;
                }

                // if decrypted message is "Bye", close the connection
                if (decryptedMessage.equalsIgnoreCase("Bye")) {
                    System.out.println("Closing connection");
                    break;
                }
                // print the decrypted message
                System.out.println("Decrypted: " + decryptedMessage);
            }
            else {
                // if client asks for the key, send the key with defined length
                if (inputLine.equals("Please send the key")) {
                    for (int i = 0; i < KEY_LENGTH; i++) {
                        // reference of generate a random integer within a range (inclusive): https://www.educative.io/answers/how-to-generate-random-numbers-in-java
                        int letterValue = (int) (Math.random() * (LETTER_Z - LETTER_A + 1) + LETTER_A);
                        // convert the value into character and add into the keystring
                        char letterKey = (char) letterValue;
                        key = key + letterKey;
                    }
                    output.println(key);
                }
                // otherwise, send the input back to client
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
