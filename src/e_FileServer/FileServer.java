package e_FileServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;

public class FileServer {

	private static final int PORT = 5999;
	private static final int PACKET_SIZE = (int) Math.pow(2, 16) - 29;
	private static final String PATH = "files";

	public static void main(String[] args) {

		// Ensure the data folder exists
		new File(PATH).mkdirs();
		
		// Open UDP Socket
		DatagramSocket ds = null;
		try {
			ds = new DatagramSocket(PORT);
			System.out.println("File Server started on port " + PORT + "!");
		} catch (SocketException e) {
			e.printStackTrace();
			System.exit(0);
		}

		// Enter main server loop
		while (true) {
			
			// Receive Packet 
			DatagramPacket dp = new DatagramPacket(new byte[PACKET_SIZE], PACKET_SIZE);
			try {
				ds.receive(dp);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// Resolve client address
			InetSocketAddress clientAddress = new InetSocketAddress(dp.getAddress(), dp.getPort());

			// Get message Parts (Command, File/Line, Optional: Line content)
			String message = new String(dp.getData(), 0, dp.getLength());
			String[] messageParts = message.split(" ");
			if (messageParts.length < 2) {
				sendMessage(ds, clientAddress, "ERROR: Bad input. (0)");
				continue;
			}
			String command = messageParts[0];

			// Determine file name and requested line
			String[] locationInfo = messageParts[1].split(",");
			if (locationInfo.length < 2) {
				sendMessage(ds, clientAddress, "ERROR: Bad input. (1)");
			}			
			String fileName = locationInfo[0];
			
			int line = 0;
			try {
				line = Integer.parseInt(locationInfo[1]);
			} catch (NumberFormatException e) {
				sendMessage(ds, clientAddress, "ERROR: Bad input. (2)");
				e.printStackTrace();
			}

			
			// Process command
			String returnLine;
			switch (command) {
				case "READ":
					// Read from disk
					returnLine = readLine(fileName, line);
					
					// Send answer
					sendMessage(ds, clientAddress, returnLine);
					break;

				case "WRITE":
					// Get line content from input message
					String contents = "";
					for(int i=2; i<messageParts.length; i++) contents += messageParts[i] + " ";
					
					// Write to disk
					returnLine = writeLine(fileName, contents, line);
					
					// Send answer
					sendMessage(ds, clientAddress, returnLine);
					break;

				default:
					// Send error message
					sendMessage(ds, clientAddress, "Sorry, '" + command + "' is not a valid command.");
					break;
			}
		}
	}

	
	private static String readLine(String name, int line) {

		// Reads line from file on disk
		
		if (line < 1)
			return "ERROR: This line number is not allowed!";
		
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(PATH + "/" + name));
			
			try {
				// Skip all lines before the one we need
				for (int i = 1; i < line; i++)
					br.readLine();

				// Read the relevant line
				String retLine = br.readLine();
				
				// Close reader & return result
				br.close();
				return (retLine);
			} catch (IOException e) {
				e.printStackTrace();
				return "ERROR: " + e.getMessage();
			}
			
		} catch (FileNotFoundException e) {
			return "ERROR: File " + name + " could not be found.";
		}
	}

	
	private static String writeLine(String name, String content, int line) {

		// Write line to file on disk and return the written line
		
		try {
			
			// Ensure file exists
			new File(PATH+"/"+name).createNewFile();

			// Read all lines that currently exist in file
			ArrayList<String> lines = new ArrayList<>();

			BufferedReader reader = new BufferedReader(new FileReader(PATH + "/" + name));
			String readLine = null;
			while ((readLine = reader.readLine()) != null) {
				lines.add(readLine);
			}
			reader.close();
			
			// Make sure the file will be long enough
			while(lines.size() < line) {
				lines.add("");
			}
			

			// Write new contents to file
			PrintWriter writer = new PrintWriter(PATH + "/" + name);
			for (int i = 1; i <= lines.size(); i++) {
				
				// Get the line we read earlier
				String storedLine = lines.get(i - 1);
				
				if (i == line)
					// Write the line the user gave us, not old one
					writer.println(content);
				else
					// Write back old content we read above
					writer.println(storedLine);
			}
			
			// Close writer & read again to ensure line replacement worked
			writer.close();
			return readLine(name, line);

		} catch (IOException e) {
			return "ERROR: " + e.getMessage();
		}
	}

	
	private static void sendMessage(DatagramSocket ds, InetSocketAddress receiver, String content) {

		// Send message via specified socket, to specified address, with specified content
		
		// Socket & Client address can not be null
		if (ds == null || receiver == null) {
			return;
		}
		
		// Content should not be empty as well
		if(content == null) content = "Sorry, this didn't work.";
		
		
		// Create Packet & fill with message
		DatagramPacket returnPacket = new DatagramPacket(new byte[PACKET_SIZE], PACKET_SIZE, receiver);
		returnPacket.setData(content.getBytes());

		// Try to send package
		try {
			ds.send(returnPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
