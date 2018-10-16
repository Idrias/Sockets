package c_MessageStore2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import zz_helper.Connection;
import zz_helper.Server;


public class MessageServer extends Server {

	private int messageID = 0;
	private String folderName = "message";
	
	@Override
	public void start(int port) {
		// Start server
		super.start(port);
		
		while(true) {
			// Accept connection
			Connection c = acceptClient();
			if(c==null) continue;
			
			// Read message
			String message = c.readMessage();
			if(message==null) continue;
			
			// Split message in two parts
			String[] split = message.split(" ", 2);
			if(split.length < 2) {
				c.sendMessage("Error: Message not well formed!");
				continue;
			}
			
			String command = split[0];
			String rest = split[1];
			
			
			// Execute command
			switch(command) {
				case "SAVE": 
					saveMessage(c, rest);
					break;
				case "GET": 
					getMessage(c, rest);
					break;
				default:
					c.sendMessage("Error: Command not found.");
					break;
			}
			
			// Close connection
			c.close();
		}	
	}
	
	public void saveMessage(Connection c, String rest) {
		// Create folder
		File file = new File(folderName);
		file.mkdirs();

		// Write File
		try {
			FileWriter fileWriter = new FileWriter(folderName + "/" + messageID);
			fileWriter.write(rest);
			fileWriter.close();
		} catch(IOException e)  {
			c.sendMessage("ERROR: Could not write message to disk.");
			return;
		}
		
		// Send response
		c.sendMessage("KEY " + messageID);
		
		// Increment message ID (= file name)
		messageID++;
	}
	
	public void getMessage(Connection c, String rest) {
		// Create folder
		new File("message").mkdirs();
		
		try {
			// Get input stream
			FileReader fileReader = new FileReader(folderName + "/" + rest);
			BufferedReader br = new BufferedReader(fileReader);
			String contents = "";
			
			// Read line of file
			contents = br.readLine();
			br.close();
			
			// Send response
			c.sendMessage("OK " + contents);
		} catch(IOException e) {c.sendMessage("ERROR: Could not read from disk.");}
	}
	
} // End of class
