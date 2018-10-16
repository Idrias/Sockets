package b_MessageStore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	private final static int port = 7777;
	private static long messageID = 0;
	private static String folderName = "message";

	@SuppressWarnings("resource")
	public static void main(String[] args) {

		ServerSocket socket;
		
		try {
			
			// Open server
			socket = new ServerSocket(port);

			while (true) {
				
				try {
					
					// Wait until client connects
					Socket client = socket.accept();
					
					// Get Input / Output streams
					PrintWriter out = new PrintWriter(client.getOutputStream());
					BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
					
					// Read message from client
					String message = in.readLine();

					
					// Split message in two parts 
					String[] parts = message.split(" ", 2);
					if(parts.length < 2) {
						sendError(out);
						out.flush();
						continue;
					}
					
					String command = parts[0];
					String saveString = parts[1];

					
					// Execute commands
					if (command.equals("SAVE")) {
						// Create folder
						File file = new File(folderName);
						file.mkdirs();

						// Write File
						FileWriter fileWriter = new FileWriter(folderName + "/" + messageID);
						fileWriter.write(saveString);
						fileWriter.close();

						// Send response
						out.println("KEY " + messageID);
						
						// Increment message ID (= file name)
						messageID++;
					}

					else if (command.equals("GET")) {
						// Create folder
						new File(folderName).mkdirs();
						
						try {
							// Get input stream
							FileReader fileReader = new FileReader(folderName + "/" + saveString);
							BufferedReader br = new BufferedReader(fileReader);
							String contents = "";
							
							// Read line of file
							contents = br.readLine();
							
							// Send response
							out.println("OK " + contents);
						} catch(IOException e) {sendError(out); out.flush();}
					} 
					else sendError(out);
					
					// Flush output stream
					out.flush();
				} catch (IOException e) {
				}

			} // End of while

		} catch (IOException e1) {
			e1.printStackTrace();
		}

	} // End of method
	
	private static void sendError(PrintWriter pw) {
		pw.println("ERROR");
	}

} // End of class
