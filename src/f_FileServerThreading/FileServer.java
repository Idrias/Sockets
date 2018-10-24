package f_FileServerThreading;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

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
			System.out.println("File Server (threading enabled) started on port " + PORT + "!");
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
			switch (command) {
				case "READ":
					new FileReadClass(fileName, PATH, line, ds, clientAddress).start();
					break;

				case "WRITE":
					// Get line content from input message
					String contents = "";
					for(int i=2; i<messageParts.length; i++) contents += messageParts[i] + " ";
					
					// Write to disk
					new FileWriteClass(fileName, PATH, line, contents, ds, clientAddress).start();
					break;

				default:
					// Send error message
					sendMessage(ds, clientAddress, "Sorry, '" + command + "' is not a valid command.");
					break;
			}
		}
	}
	
	public static void sendMessage(DatagramSocket ds, InetSocketAddress receiver, String content) {

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
